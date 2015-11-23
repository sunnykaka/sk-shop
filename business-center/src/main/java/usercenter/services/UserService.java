package usercenter.services;

import com.google.common.base.Preconditions;
import common.exceptions.AppBusinessException;
import common.exceptions.AppException;
import common.exceptions.ErrorCode;
import common.services.GeneralDao;
import common.utils.DateUtils;
import common.utils.PasswordHash;
import common.utils.RegExpUtils;
import common.utils.play.BaseGlobal;
import ordercenter.services.VoucherService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import play.Logger;
import play.cache.CacheApi;
import usercenter.constants.AccountType;
import usercenter.constants.MarketChannel;
import usercenter.domain.SmsSender;
import usercenter.dtos.*;
import usercenter.models.LoginRecord;
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

import static usercenter.utils.UserTokenUtils.*;

/**
 * Created by liubin on 15-4-24.
 */
@Service
public class UserService {

    @Autowired
    GeneralDao generalDao;

    @Autowired
    private VoucherService voucherService;

    private static CacheApi cacheApi() {
        return BaseGlobal.injector.instanceOf(CacheApi.class);
    }


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
            throw new AppBusinessException(ErrorCode.UsernameExist, "用户名已存在");
        }
        if(isPhoneExist(registerForm.getPhone(), Optional.empty())) {
            throw new AppBusinessException(ErrorCode.PhoneExist, "手机已存在");
        }
        if(!new SmsSender(registerForm.getPhone(), registerIP, SmsSender.Usage.REGISTER).verifyCode(registerForm.getVerificationCode())) {
            throw new AppBusinessException(ErrorCode.InvalidArgument, "校验码验证失败");
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

        Logger.debug(String.format("用户%s注册成功", user.getUserName()));

        try {
            //发放注册代金券
            voucherService.requestForRegister(user.getId(), 1);
        } catch (Exception e) {
            Logger.error("用户注册的时候请求代金券失败", e);
        }

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
        String nickName = openUserInfo.getNickName();
        if(RegExpUtils.isUsername(nickName)) {
            //昵称满足用户名规则,直接使用
            username = nickName;
        } else {
            if(StringUtils.isNoneBlank(nickName)) {
                nickName = nickName.replaceAll("[^(0-9a-zA-Z\\u4e00-\\u9fa5)]", "");
            }
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
        userData.setName(nickName);
        generalDao.persist(userData);

        UserOuter userOuter = new UserOuter();
        userOuter.setAccountType(openUserInfo.getAccountType());
        userOuter.setCreateDate(now);
        userOuter.setLocked(false);
        userOuter.setOuterId(openUserInfo.getUnionId());
        userOuter.setUserId(user.getId());
        generalDao.persist(userOuter);

        Logger.debug(String.format("用户%s注册成功", user.getUserName()));

        try {
            //发放注册代金券
            voucherService.requestForRegister(user.getId(), 1);
        } catch (Exception e) {
            Logger.error("用户注册的时候请求代金券失败", e);
        }

        return user;
    }

    /**
     * Web登录
     * @param loginForm
     * @return
     */
    @Transactional
    public User login(LoginForm loginForm) {
        SessionUtils.clearRememberMe();

        User user = authenticate(loginForm.getPassport(), loginForm.getPassword());
        if(user == null) {
            throw new AppBusinessException(ErrorCode.InvalidArgument, "用户名或密码错误");
        }

        doLogin(user, loginForm.retrieveLoginInfo());

        try {
            SessionUtils.setCurrentUser(user, "true".equalsIgnoreCase(loginForm.getRememberMe()));
            return user;
        } catch (AppException e) {
            Logger.error("", e);
            throw new AppBusinessException("服务器发生错误, 登录失败");
        }

    }

    /**
     * Web用Cookie登录
     * @param userId
     * @return
     */
    @Transactional
    public User loginByCookie(Integer userId) {

        User user = generalDao.get(User.class, userId);
        if(user == null) {
            Logger.error(String.format("用cookie登录时,根据userId[%d]没有找到user", userId));
            return null;
        }

        doLogin(user, new LoginForm.LoginInfo(null, MarketChannel.WEB.getValue(), null));

        try {
            SessionUtils.setCurrentUser(user, false);
            return user;
        } catch (AppException e) {
            Logger.error("", e);
        }

        return null;

    }

    /**
     * Web在注册成功之后自动登录
     * @param userId
     * @param rememberMe
     * @return
     */
    @Transactional
    public User loginByRegister(Integer userId, boolean rememberMe) {

        User user = getById(userId);

        doLogin(user, new LoginForm.LoginInfo(null, MarketChannel.WEB.getValue(), null));

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
            throw new AppBusinessException(ErrorCode.InvalidArgument, "两次输入密码不一致");
        }

        try {
            User userNew = getById(user.getId());
            userNew.setPassword(PasswordHash.createHash(psw.getNewPassword()));
            generalDao.merge(userNew);

        } catch (GeneralSecurityException e) {
            Logger.error("创建哈希密码的时候发生错误", e);
            throw new AppBusinessException("用户修改密码失败");
        }
        return user;
    }

    /**
     * app直接修改密码
     * @param user
     * @param psw
     * @return
     */
    @Transactional
    public User updatePassword(User user,RecoverPswForm psw){

        try {
            User userNew = getById(user.getId());
            userNew.setPassword(PasswordHash.createHash(psw.getPassword()));
            generalDao.merge(userNew);

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
            throw new AppBusinessException(ErrorCode.InvalidArgument, "两次输入密码不一致");
        }

        try {
            User userNew = getById(user.getId());
            if(!PasswordHash.validatePassword(psw.getPassword(),userNew.getPassword())){
                throw new AppBusinessException(ErrorCode.InvalidArgument, "旧密码输入错误");
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
    public User updatePhone(User user, PhoneCodeForm phoneCode, String ip){

        if(!new SmsSender(phoneCode.getPhone(), ip, SmsSender.Usage.BIND).verifyCode(phoneCode.getVerificationCode())) {
            throw new AppBusinessException(ErrorCode.InvalidArgument, "校验码验证失败");
        }

        if (phoneCode.getPhone().equals(user.getPhone())) {
            throw new AppBusinessException(ErrorCode.InvalidArgument, "请输入与旧手机号码不一样的号码");
        }

        User userPhone = findByPhone(phoneCode.getPhone());
        if (null != userPhone) {
            throw new AppBusinessException(ErrorCode.InvalidArgument, "输入的号码已被注册");
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

    /**
     * 记录用户登录
     * @param user
     * @param loginInfo
     */
    @Transactional
    public void doLogin(User user, LoginForm.LoginInfo loginInfo) {
        if(!user.isActive() || user.isDeleted() || user.isHasForbidden()) {
            throw new AppBusinessException(ErrorCode.Forbidden, "抱歉,该用户已被禁止登录");
        }
        user.setLoginCount(user.getLoginCount() + 1);
        user.setLoginTime(DateUtils.current());
        generalDao.persist(user);

        createLoginRecord(user, loginInfo);
    }

    /**
     * 保存登录记录
     * @param user
     * @param loginInfo
     */
    private void createLoginRecord(User user, LoginForm.LoginInfo loginInfo) {
        LoginRecord loginRecord = new LoginRecord();
        loginRecord.setCreateTime(DateUtils.current());
        loginRecord.setDeviceId(loginInfo.getDeviceId());
        loginRecord.setDeviceInfo(loginInfo.getDeviceInfo());
        loginRecord.setUserId(user.getId());
        MarketChannel channel = null;
        if(loginInfo.getChannel() != null) {
            try {
                channel = MarketChannel.valueOf(loginInfo.getChannel());
            } catch (IllegalArgumentException ignore) {}
        }
        if(channel == null) {
            throw new AppBusinessException(ErrorCode.InvalidArgument, "未知的渠道类型: " + loginInfo.getChannel());
        }
        loginRecord.setChannel(channel);

        generalDao.persist(loginRecord);
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

    /**
     * 通过用户名/手机查找用户并且判断密码是否正确，如果成功返回用户，否则返回null
     * @param passport
     * @param password
     * @return
     */
    @Transactional(readOnly = true)
    public User authenticate(String passport, String password) {

        User user = null;
        if(RegExpUtils.isPhone(passport)) {
            user = findByPhone(passport);
        }
        if(user == null) {
            user = findByUsername(passport);
        }

        try {
            if(user != null) {
                if(StringUtils.isEmpty(user.getPassword())){
                    return null;
                }
                if(PasswordHash.validatePassword(password, user.getPassword())) {
                    return user;
                }
            }
        } catch (GeneralSecurityException e) {
            Logger.error("创建哈希密码的时候发生错误", e);
        }

        return null;
    }

    /**
     * 根据AccessToken拿到用户ID
     * @param accessToken
     * @return
     */
    public Optional<Integer> retrieveUserIdByAccessToken(String accessToken) {
        if(StringUtils.isNoneBlank(accessToken)) {
            String[] array = retrieveTokenValueFromCache(cacheApi().get(getAccessTokenKey(accessToken)));
            if(array.length == 3) {
                return Optional.ofNullable(Integer.parseInt(array[1]));
            }
        }
        return Optional.empty();
    }


}
