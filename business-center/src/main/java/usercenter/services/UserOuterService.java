package usercenter.services;

import common.services.GeneralDao;
import common.utils.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usercenter.dtos.OpenUserInfo;
import usercenter.models.User;
import usercenter.models.UserOuter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by liubin on 15-5-25.
 */
@Service
public class UserOuterService {

    @Autowired
    private UserService userService;

    @Autowired
    private GeneralDao generalDao;

    /**
     * 返回数组的第一个元素是User，第二个是User是否是第一次登录
     * @param openUserInfo
     * @param registerIP
     * @return
     */
    @Transactional
    public Object[] handleOpenIdCallback(OpenUserInfo openUserInfo, String registerIP) {

        UserOuter userOuter = findUserOuterByOuterId(openUserInfo.getUnionId());
        if(userOuter == null) {

            return new Object[]{userService.registerByOpenId(openUserInfo, registerIP), true};

        } else {

            return new Object[]{userOuter.getUser(), false};
        }

    }

    @Transactional(readOnly = true)
    public UserOuter findUserOuterByOuterId(String unionId) {

        String jpql = "select uo from UserOuter uo where uo.outerId = :outerId";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("outerId", unionId);
        List<UserOuter> userOuterList = generalDao.query(jpql, Optional.empty(), paramMap);
        if(!userOuterList.isEmpty()) {
            return userOuterList.get(0);
        } else {
            return null;
        }

    }


}
