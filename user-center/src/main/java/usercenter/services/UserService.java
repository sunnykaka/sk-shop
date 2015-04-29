package usercenter.services;

import com.google.common.base.Preconditions;
import common.exceptions.AppBusinessException;
import common.services.GeneralDao;
import common.utils.DateUtils;
import common.utils.PasswordHash;
import common.utils.RegExpUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import play.Logger;
import usercenter.constants.AccountType;
import usercenter.domain.PhoneVerification;
import usercenter.dtos.LoginForm;
import usercenter.dtos.RegisterForm;
import usercenter.models.User;
import usercenter.models.UserData;

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
     * 根据手机生成短信验证码
     * @param phone
     * @return
     */
    public String generatePhoneVerificationCode(String phone) {
        return new PhoneVerification(phone).generatePhoneVerificationCode();
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
        if(!new PhoneVerification(registerForm.getPhone()).verifyCode(registerForm.getVerificationCode())) {
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
    public User login(LoginForm loginForm) {

        User user = authenticate(loginForm.getPassport(), loginForm.getPassword());
        if(user == null) {
            throw new AppBusinessException("用户名或密码错误");
        }
        return doLogin(user);

    }

    @Transactional
    public User loginByCookie(Integer userId) {

        User user = generalDao.get(User.class, userId);
        if(user == null) {
            Logger.error(String.format("用cookie登录时,根据userId[%d]没有找到user", userId));
            return null;
        }
        return doLogin(user);

    }

    private User doLogin(User user) {
        if(!user.isActive() || user.isDeleted() || user.isHasForbidden()) {
            throw new AppBusinessException("抱歉,该用户已被禁止登录");
        }
        user.setLoginCount(user.getLoginCount() + 1);
        user.setLoginTime(DateUtils.current());
        return generalDao.merge(user);
    }


    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        Preconditions.checkNotNull(username, "findByUsername中username不能为null");

        CriteriaBuilder cb = generalDao.getEm().getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> user = cq.from(User.class);

        cq.select(user).where(cb.equal(user.get("userName"), username));
        TypedQuery<User> query = generalDao.getEm().createQuery(cq);
        return query.getSingleResult();

    }

    @Transactional(readOnly = true)
    public User findByPhone(String phone) {
        Preconditions.checkNotNull(phone, "findByPhone中phone不能为null");

        CriteriaBuilder cb = generalDao.getEm().getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> user = cq.from(User.class);

        cq.select(user).where(cb.equal(user.get("phone"), phone));
        TypedQuery<User> query = generalDao.getEm().createQuery(cq);
        return query.getSingleResult();

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
