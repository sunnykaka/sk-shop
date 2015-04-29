package usercenter.services;

import common.services.GeneralDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usercenter.models.UserData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by zhb on 15-4-29.
 */
@Service
public class UserDataService {

    @Autowired
    private GeneralDao generalDao;

    /**
     * 更新资料
     *
     * @param userData
     * @return
     */
    @Transactional
    public UserData updateUserDate(UserData userData){

        return generalDao.merge(userData);
    }

    public UserData findById(int id){
        return generalDao.get(UserData.class,id);
    }

    /**
     * 根据Id
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public UserData findByUserId(int userId){

        String jpql = "select ud from UserData ud left join fetch ud.user u where 1=1 and u.id = :userId";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and ud.userId = :userId ";
        queryParams.put("userId", userId);

        List<UserData> valueList = generalDao.query(jpql, Optional.empty(), queryParams);
        if (valueList != null && valueList.size() > 0) {
            return valueList.get(0);
        }

        return null;

    }

}
