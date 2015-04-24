package usercenter.services;

import common.exceptions.AppBusinessException;
import common.services.GeneralDao;
import common.utils.DateUtils;
import common.utils.PasswordHash;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import play.Logger;
import play.cache.Cache;
import usercenter.constants.AccountType;
import usercenter.dtos.LoginForm;
import usercenter.dtos.RegisterForm;
import usercenter.models.User;
import usercenter.models.UserData;

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

    public static String VERIFICATION_CODE_KEY_PREFIX = "register_phone:";
    public static String VERIFICATION_CODE_COUNT_KEY_PREFIX = "register_phone_count:";
    public static int PHONE_VERIFICATION_MAX_COUNT_IN_DAY = 5;
    public static int VERIFICATION_CODE_EXPIRE_TIME = 7200;
    public static int VERIFICATION_CODE_LENGTH = 6;


    /**
     * 根据手机生成短信验证码
     * @param phone
     * @return
     */
    public String generatePhoneVerificationCode(String phone) {

        //判断一天之内是不是发送过5次了
        Integer count = (Integer) Cache.get(VERIFICATION_CODE_COUNT_KEY_PREFIX);
        if (count != null && count >= PHONE_VERIFICATION_MAX_COUNT_IN_DAY) {
            return null;
        }

        //生成验证码
        String verificationCode = RandomStringUtils.randomNumeric(VERIFICATION_CODE_LENGTH);

        String existVerificationCode = (String) Cache.get(VERIFICATION_CODE_KEY_PREFIX + phone);
        if (existVerificationCode != null) {
            existVerificationCode += ":" + verificationCode;
        } else {
            existVerificationCode = verificationCode;
        }
        Cache.set(VERIFICATION_CODE_KEY_PREFIX + phone, existVerificationCode, VERIFICATION_CODE_EXPIRE_TIME);

        return verificationCode;

    }

    /**
     * 校验短信验证码是否有效
     * @param phone
     * @param verificationCode
     * @return
     */
    public boolean verifyPhoneVerificationCode(String phone, String verificationCode) {
        if(verificationCode.length() != VERIFICATION_CODE_LENGTH) {
            return false;
        }
        String verificationCodeStr = (String) Cache.get(VERIFICATION_CODE_KEY_PREFIX + phone);
        if(verificationCodeStr == null) {
            return false;
        }
        return verificationCodeStr.contains(verificationCode);
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



}
