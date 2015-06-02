package usercenter.services;

import com.google.common.base.Preconditions;
import common.exceptions.AppBusinessException;
import common.exceptions.AppException;
import common.services.GeneralDao;
import common.utils.DateUtils;
import common.utils.PasswordHash;
import common.utils.RegExpUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import play.Logger;
import play.twirl.api.Content;
import usercenter.constants.AccountType;
import usercenter.domain.SmsSender;
import usercenter.dtos.*;
import usercenter.models.User;
import usercenter.models.UserData;
import usercenter.models.UserOuter;
import usercenter.utils.SessionUtils;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by liubin on 15-4-24.
 */
@Service
public class UserService {

    @Autowired
    GeneralDao generalDao;

    /**
     * 获取账号
     *
     * @param id
     * @return
     */
    public User getById(int id){
        return generalDao.get(User.class,id);
    }

    /**
     * 判断用户名是否存在
     * @param username
     * @param userId 当前用户ID,如果是修改用户的话,需要传,否则可以传empty
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isUsernameExist(String username, Optional<Integer> userId) {

        String hql = "select u.id from User u where u.userName = :username";
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        if(userId.isPresent()) {
            hql += " and u.userId != :userId ";
            params.put("userId", userId);
        }
        List<Integer> results = generalDao.query(hql, Optional.empty(), params);
        return !results.isEmpty();
    }

    /**
     * 判断手机号码是否存在
     * @param phone
     * @param userId 当前用户ID,如果是修改用户的话,需要传,否则可以传empty
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isPhoneExist(String phone, Optional<Integer> userId) {
        String hql = "select u.id from User u where u.phone = :phone";
        Map<String, Object> params = new HashMap<>();
        params.put("phone", phone);
        if(userId.isPresent()) {
            hql += " and u.userId != :userId ";
            params.put("userId", userId);
        }
        List<Integer> results = generalDao.query(hql, Optional.empty(), params);
        return !results.isEmpty();
    }


    @Transactional
    public User register(RegisterForm registerForm, String registerIP) {
        if(isUsernameExist(registerForm.getUsername(), Optional.empty())) {
            throw new AppBusinessException("用户名已存在");
        }
        if(isPhoneExist(registerForm.getPhone(), Optional.empty())) {
            throw new AppBusinessException("手机已存在");
        }
        if(!new SmsSender(registerForm.getPhone(), SmsSender.Usage.REGISTER).verifyCode(registerForm.getVerificationCode())) {
            throw new AppBusinessException("校验码验证失败");
        }

        DateTime now = DateUtils.current();
        User user = new User();
        user.setAccountType(AccountType.KRQ);
        user.setActive(true);
        user.setLoginTime(now);
        user.setLoginCount(0);
        try {
            user.setPassword(PasswordHash.createHash(registerForm.getPassword()));
        } catch (GeneralSecurityException e) {
            Logger.error("创建哈希密码的时候发生错误", e);
            throw new AppBusinessException("用户注册失败");
        }
        user.setPhone(registerForm.getPhone());
        user.setRegisterDate(DateUtils.printDateTime(now));
        user.setRegisterIP(registerIP);
        user.setUserName(registerForm.getUsername());

        generalDao.persist(user);

        UserData userData = new UserData();
        userData.setUserId(user.getId());
        userData.setCreateDate(now);
        userData.setUpdateDate(now);
        generalDao.persist(userData);

        return user;
    }

    @Transactional
    public User registerByOpenId(OpenUserInfo openUserInfo, String registerIP) {

        DateTime now = DateUtils.current();
        User user = new User();
        user.setAccountType(openUserInfo.getAccountType());
        user.setActive(true);
        user.setLoginTime(now);
        user.setLoginCount(0);
        user.setRegisterDate(DateUtils.printDateTime(now));
        user.setRegisterIP(registerIP);
        String username = null;
        if(RegExpUtils.isUsername(openUserInfo.getNickName())) {
            //昵称满足用户名规则,直接使用
            username = openUserInfo.getNickName();
        }
        while(username == null || isUsernameExist(username, Optional.<Integer>empty())) {
            //生成随机6位数字作为用户名
            username = RandomStringUtils.randomNumeric(6);
        }
        user.setUserName(username);
        user.setPassword("");
        generalDao.persist(user);

        UserData userData = new UserData();
        userData.setUserId(user.getId());
        userData.setCreateDate(now);
        userData.setUpdateDate(now);
        userData.setSex(openUserInfo.getGender().userSex);
        userData.setName(openUserInfo.getNickName());
        generalDao.persist(userData);

        UserOuter userOuter = new UserOuter();
        userOuter.setAccountType(openUserInfo.getAccountType());
        userOuter.setCreateDate(now);
        userOuter.setLocked(false);
        userOuter.setOuterId(openUserInfo.getUnionId());
        userOuter.setUserId(user.getId());
        generalDao.persist(userOuter);

        return user;
    }

    @Transactional
    public User login(LoginForm loginForm) {
        SessionUtils.clearRememberMe();

        User user = authenticate(loginForm.getPassport(), loginForm.getPassword());
        if(user == null) {
            throw new AppBusinessException("用户名或密码错误");
        }

        doLogin(user);

        try {
            SessionUtils.setCurrentUser(user, "true".equalsIgnoreCase(loginForm.getRememberMe()));
            return user;
        } catch (AppException e) {
            Logger.error("", e);
            throw new AppBusinessException("服务器发生错误, 登录失败");
        }

    }

    @Transactional
    public User loginByCookie(Integer userId) {

        User user = generalDao.get(User.class, userId);
        if(user == null) {
            Logger.error(String.format("用cookie登录时,根据userId[%d]没有找到user", userId));
            return null;
        }

        doLogin(user);

        try {
            SessionUtils.setCurrentUser(user, false);
            return user;
        } catch (AppException e) {
            Logger.error("", e);
        }

        return null;

    }

    @Transactional
    public User loginByRegister(User user, boolean rememberMe) {

        doLogin(user);

        try {
            SessionUtils.setCurrentUser(user, rememberMe);
            return user;
        } catch (AppException e) {
            Logger.error("", e);
        }

        return null;

    }


    /**
     * 直接修改密码
     * @param user
     * @param psw
     * @return
     */
    @Transactional
    public User updatePassword(User user,PswForm psw){

        if(!psw.getNewPassword().equals(psw.getRePassword())) {
            throw new AppBusinessException("两次输入密码不一致");
        }

        try {

            user.setPassword(PasswordHash.createHash(psw.getNewPassword()));
            generalDao.merge(user);

        } catch (GeneralSecurityException e) {
            Logger.error("创建哈希密码的时候发生错误", e);
            throw new AppBusinessException("用户修改密码失败");
        }
        return user;
    }

    /**
     * 原密码修改密码
     *
     * @param user
     * @param psw
     * @return
     */
    @Transactional
    public User updatePassword(User user,ChangePswForm psw){

        if(!psw.getNewPassword().equals(psw.getRePassword())) {
            throw new AppBusinessException("两次输入密码不一致");
        }

        try {

            User userNew = getById(user.getId());
            if(PasswordHash.validatePassword(psw.getPassword(),userNew.getPassword())){
                throw new AppBusinessException("旧密码输入错误");
            }

            userNew.setPassword(PasswordHash.createHash(psw.getNewPassword()));
            generalDao.merge(userNew);

        } catch (GeneralSecurityException e) {
            Logger.error("创建哈希密码的时候发生错误", e);
            throw new AppBusinessException("用户修改密码失败");
        }
        return user;
    }

    /**
     * 修改用户手机号码
     *
     * @param user
     * @param phoneCode
     * @return
     */
    @Transactional
    public User updatePhone(User user,PhoneCodeForm phoneCode){

        if(!new SmsSender(phoneCode.getPhone(), SmsSender.Usage.BIND).verifyCode(phoneCode.getVerificationCode())) {
            throw new AppBusinessException("校验码验证失败");
        }

        if (phoneCode.getPhone().equals(user.getPhone())) {
            throw new AppBusinessException("请输入与旧手机号码不一样的号码");
        }

        User userPhone = findByPhone(phoneCode.getPhone());
        if (null != userPhone) {
            throw new AppBusinessException("输入的号码已被注册");
        }

        User oldUser = getById(user.getId());

        oldUser.setPhone(phoneCode.getPhone());
        return generalDao.merge(oldUser);
    }

    /**
     * 修改邮箱地址
     *
     * @param user
     * @param email
     * @return
     */
    @Transactional
    public User updateEmail(User user,String email){

        User newUser = getById(user.getId());
        newUser.setEmail(email);

        return generalDao.merge(newUser);
    }

    private void doLogin(User user) {
        if(!user.isActive() || user.isDeleted() || user.isHasForbidden()) {
            throw new AppBusinessException("抱歉,该用户已被禁止登录");
        }
        user.setLoginCount(user.getLoginCount() + 1);
        user.setLoginTime(DateUtils.current());
        generalDao.persist(user);
    }

    @Transactional
    public void logout(User user) {
        if(user == null) return;

        SessionUtils.logout(user);

    }



    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        Preconditions.checkNotNull(username, "findByUsername中username不能为null");

        CriteriaBuilder cb = generalDao.getEm().getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> user = cq.from(User.class);

        cq.select(user).where(cb.equal(user.get("userName"), username));
        TypedQuery<User> query = generalDao.getEm().createQuery(cq);
        List<User> results = query.getResultList();
        if(results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }

    }

    @Transactional(readOnly = true)
    public User findByPhone(String phone) {
        Preconditions.checkNotNull(phone, "findByPhone中phone不能为null");

        CriteriaBuilder cb = generalDao.getEm().getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> user = cq.from(User.class);

        cq.select(user).where(cb.equal(user.get("phone"), phone));
        TypedQuery<User> query = generalDao.getEm().createQuery(cq);
        List<User> results = query.getResultList();
        if(results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }

    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        String jpql = "select u from User u where 1=1 and u.deleted=false ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and u.email = :email ";
        queryParams.put("email", email);

        List<User> valueList = generalDao.query(jpql, Optional.empty(), queryParams);
        if (valueList != null && valueList.size() > 0) {
            return valueList.get(0);
        }

        return null;

    }

    private User authenticate(String passport, String password) {

        User user = null;
        if(RegExpUtils.isPhone(passport)) {
            user = findByPhone(passport);
        }
        if(user == null) {
            user = findByUsername(passport);
        }

        try {
            if(user != null) {
                if(PasswordHash.validatePassword(password, user.getPassword())) {
                    return user;
                }
            }
        } catch (GeneralSecurityException e) {
            Logger.error("创建哈希密码的时候发生错误", e);
        }

        return null;
    }

}
