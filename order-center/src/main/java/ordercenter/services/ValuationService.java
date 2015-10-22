package ordercenter.services;

import common.services.GeneralDao;
import common.utils.page.Page;
import ordercenter.models.Order;
import ordercenter.models.OrderItem;
import ordercenter.models.Valuation;
import org.apache.commons.lang3.ArrayUtils;
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
        generalDao.persist(orderItem);

        //同步订单 ------开始
        Order order = orderService.getOrderById(orderItem.getOrderId(),valuation.getUserId());
        int count = 0;
        for(OrderItem oi:order.getOrderItemList()){
            if(oi.isAppraise()){
                count = count + 1;
            }
        }
        if(count == order.getOrderItemList().size()){
            order.setValuation(true);
            orderService.updateOrder(order);
        }
        //----------结束
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

    /**
     * 返回评价按照好评/中评/差评分组的总数
     * @return
     */
    @Transactional(readOnly = true)
    public int[] countValuationGroupByPoint(int productId){

        String jpql = "select v.point, count(v.id) from Valuation v where v.productId = :productId group by v.point order by v.point";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("productId", productId);
        List<Object[]> results = generalDao.query(jpql, Optional.empty(), queryParams);

        Map<Integer, Integer> map = new HashMap<Integer, Integer>() {{
            put(0, 0);
            put(1, 0);
            put(2, 0);
        }};

        results.forEach(array -> map.put((Integer)array[0], ((Long)array[1]).intValue()));

        return ArrayUtils.toPrimitive(map.values().toArray(new Integer[map.values().size()]));

    }


}
