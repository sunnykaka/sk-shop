package cmscenter.services;

import cmscenter.models.VoucherActivity;
import common.services.GeneralDao;
import ordercenter.services.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by zhb on 2015/11/20.
 */
@Service
public class VoucherActivityService {

    @Autowired
    GeneralDao generalDAO;

    @Autowired
    private VoucherService voucherService;

    /**
     * 用户领取活动代金券
     *
     * @param voucherActivity
     */
    @Transactional
    public void createVoucherActivity(VoucherActivity voucherActivity){
        generalDAO.persist(voucherActivity);
        voucherService.requestForActivity(Optional.of(voucherActivity.getUniqueNo()),voucherActivity.getUserId(),1);
    }

    /**
     * 查找我的某张代金券
     *
     * @param uniqueNo
     * @param activityId
     * @param userId
     * @return
     */
    public VoucherActivity getMyVoucherActivity(String uniqueNo, int activityId, int userId){

        String jpql = "select v from VoucherActivity v where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and v.uniqueNo = :uniqueNo ";
        queryParams.put("uniqueNo", uniqueNo);

        jpql += " and v.activityId = :activityId ";
        queryParams.put("activityId", activityId);

        jpql += " and v.userId = :userId ";
        queryParams.put("userId", userId);

        List<VoucherActivity> valueList = generalDAO.query(jpql, Optional.empty(), queryParams);
        if (valueList != null && valueList.size() > 0) {
            return valueList.get(0);
        }

        return null;
    }
}
