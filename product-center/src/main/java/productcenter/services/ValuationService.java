package productcenter.services;

import common.services.GeneralDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productcenter.models.Valuation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 商品评论
 *
 * Created by zhb on 15-5-7.
 */
@Service
@Transactional
public class ValuationService {

    @Autowired
    private GeneralDao generalDao;

    /**
     * 添加评论
     *
     * @param valuation
     * @return
     */
    public Valuation addValuation(Valuation valuation){

        generalDao.persist(valuation);

        //TODO 同步订单项

        return valuation;

    }

    /**
     * 查询订单项评论
     *
     * @param userId
     * @param orderItemId
     * @return
     */
    @Transactional(readOnly = true)
    public Valuation findByOrderItemId(int userId,long orderItemId){

        String jpql = "select v from Valuation v where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and v.orderItemId = :orderItemId ";
        queryParams.put("orderItemId", orderItemId);

        jpql += " and v.userId = :userId ";
        queryParams.put("userId", userId);

        List<Valuation> valueList = generalDao.query(jpql, Optional.empty(), queryParams);
        if (valueList != null && valueList.size() > 0) {
            return valueList.get(0);
        }

        return null;

    }
}