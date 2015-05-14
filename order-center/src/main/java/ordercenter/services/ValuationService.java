package ordercenter.services;

import common.services.GeneralDao;
import common.utils.page.Page;
import ordercenter.models.OrderItem;
import ordercenter.models.Valuation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    @Autowired
    private OrderService orderService;

    /**
     * 添加评论
     *
     * @param valuation
     * @return
     */
    public Valuation addValuation(Valuation valuation){

        generalDao.persist(valuation);

        OrderItem orderItem = orderService.getOrderItemById(valuation.getOrderItemId());
        orderItem.setAppraise(true);//同步订单项，已评论
        orderService.updateOrderItem(orderItem);

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
    public Valuation findByOrderItemId(int userId,int orderItemId){

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


    @Transactional(readOnly = true)
    public Optional<Page<Valuation>> findByProduct(Optional<Page<Valuation>> page, int productId, Integer point){

        String jpql = "select v from Valuation v where v.productId = :productId ";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("productId", productId);
        if(point != null) {
            jpql += " and v.point = :point";
            queryParams.put("point", point);
        }

        generalDao.query(jpql, page, queryParams);

        return page;

    }

}
