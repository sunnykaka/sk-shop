package ordercenter.services;

import common.services.GeneralDao;
import common.utils.page.Page;
import ordercenter.models.Order;
import ordercenter.models.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import play.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 购物车服务
 * User: lidujun
 * Date: 2015-04-29
 */
@Service
@Transactional
public class OrderService {

    @Autowired
    GeneralDao generalDao;

    private static final ReentrantLock LOCK = new ReentrantLock();

    /**
     * 创建购订单
     * @param order
     */
    public void createOrder(Order order) {
        Logger.info("--------OrderService createCart begin exe-----------" + order);
        generalDao.persist(order);
    }

    /**
     * 更新订单
     * @param order
     */
    public void updateOrder(Order order) {
        Logger.info("--------OrderService updateCart begin exe-----------" + order);
        generalDao.merge(order);
    }

    /**
     * 通过订单id删除订单
     * @param orderId
     */
    public void deleteOrder(int orderId) {
        Logger.info("--------OrderService deleteCart real delete  begin exe-----------" + orderId);
        generalDao.removeById(Order.class, orderId);
    }

    /**
     * 通过订单id获取订单
     * @param orderId
     * @return
     */
    @Transactional(readOnly = true)
    public Order getOrder(int orderId) {
        Logger.info("--------OrderService getCart begin exe-----------" + orderId);
        return generalDao.get(Order.class, orderId);
    }

    @Transactional(readOnly = true)
    private String getSelectAllOrderSql() {
        return "select v from Order v where 1=1 ";
    }

    /**
     * 通过订单号获取订单
     * @param orderNo
     * @return
     */
    @Transactional(readOnly = true)
    public Order getOrderByOrderNo(int orderNo) {
        String jpql = getSelectAllOrderSql();
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and v.orderNo = :orderNo ";
        queryParams.put("orderNo", orderNo);

        List<Order> orderList = generalDao.query(jpql, Optional.ofNullable(null), queryParams);
        Order order = null;
        if(orderList != null || orderList.size() > 0) {
            order= orderList.get(0);
        }
        return  order;
    }

    /**
     * 通过用户id获取订单
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<Order> getOrderByUserId(int userId) {
        Logger.info("--------OrderService getOrderByUserId begin exe-----------" + userId);
        String jpql = getSelectAllOrderSql();
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and v.userId = :userId ";
        queryParams.put("userId", userId);

        return generalDao.query(jpql, Optional.ofNullable(null), queryParams);
    }

    //////////////////////////////订单项/////////////////////////////////////////////
    /**
     * 创建订单项
     * @param orderItem
     */
    public void createOrderItem(OrderItem orderItem) {
        Logger.info("--------OrderService createOrderItem begin exe-----------" + orderItem);
        generalDao.persist(orderItem);
    }

    /**
     * 更新订单项
     * @param orderItem
     */
    public void updateOrderItem(OrderItem orderItem) {
        Logger.info("--------OrderService updateOrderItem begin exe-----------" + orderItem);
        generalDao.merge(orderItem);
    }

    /**
     * 通过订单主键id获取订单项
     * @param orderId
     * @return
     */
    @Transactional(readOnly = true)
    public List<OrderItem> queryOrderItemsByOrderId(int orderId) {
        Logger.info("--------OrderService queryOrderItemsByOrderId begin exe-----------" + orderId);

        String jpql = "select o from OrderItem o where 1=1 and o.orderId=:orderId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("orderId", orderId);
        return generalDao.query(jpql, Optional.<Page<OrderItem>>empty(), queryParams);
    }

    /**
     * 通过订单项id删除订单项
     * @param id
     */
    public void deleteOrderItemById(int id) {
        play.Logger.info("--------OrderService deleteOrderItemById real delete  begin exe-----------" + id);
        generalDao.removeById(OrderItem.class, id);
    }

    /**
     * 通过orderId来删除订单下的所有订单项
     * @param orderId
     */
    public void deleteOrderItemByOrderId(int orderId) {
        play.Logger.info("--------OrderService deleteOrderById real delete  begin exe-----------" + orderId);

        String jpql = "delete from OrderItem v where orderId=:orderId ";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("orderId", orderId);
        generalDao.update(jpql,queryParams);
    }

}
