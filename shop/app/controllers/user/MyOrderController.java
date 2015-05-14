package controllers.user;

import common.utils.JsonResult;
import common.utils.page.Page;
import ordercenter.models.Logistics;
import ordercenter.models.Order;
import ordercenter.models.OrderItem;
import ordercenter.models.OrderStateHistory;
import ordercenter.services.OrderService;
import ordercenter.services.ValuationService;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import play.mvc.Result;

import productcenter.services.SkuAndStorageService;
import usercenter.models.User;
import usercenter.utils.SessionUtils;
import utils.secure.SecuredAction;
import views.html.user.myOrder;
import views.html.user.myOrderAppraise;
import views.html.user.myOrderInfo;

import java.util.List;
import java.util.Optional;

/**
 * 我的订单管理
 * <p>
 * Created by zhb on 15-5-7.
 */
@org.springframework.stereotype.Controller
public class MyOrderController extends Controller {

    public static final int user_id = 14303;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SkuAndStorageService skuAndStorageService;

    @Autowired
    private ValuationService valuationService;

    /**
     * 单订管理首页
     *
     * @param queryType 0所有订单、1待收货、2待评价
     * @return
     */
    public Result index(int queryType, int pageNo, int pageSize) {
        //User user = SessionUtils.currentUser();

        Page<Order> page = new Page<>(pageNo, pageSize);

        List<Order> orderList = orderService.getOrderByUserId(Optional.of(page), user_id, queryType);
        for (Order order : orderList) {
            for (OrderItem orderItem : order.getOrderItemList()) {
                orderItem.setProperties(skuAndStorageService.getSKUPropertyValueMap(orderItem.getSkuId()));
            }
            Logistics logistics = orderService.getLogisticsByOrderId(order.getId());
            if (null != logistics) {
                order.setAddressName(logistics.getName());
            } else {
                order.setAddressName(order.getUserName());
            }

        }

        page.setResult(orderList);

        return ok(myOrder.render(page, queryType));

    }

    /**
     * 订单详情页面
     *
     * @param orderId
     * @return
     */
    @SecuredAction
    public Result orderContent(int orderId) {
        User user = SessionUtils.currentUser();

        Order order = orderService.getOrderById(orderId, user_id);
        Logistics logistics = orderService.getLogisticsByOrderId(order.getId());
        for (OrderItem orderItem : order.getOrderItemList()) {
            orderItem.setProperties(skuAndStorageService.getSKUPropertyValueMap(orderItem.getSkuId()));
        }
        List<OrderStateHistory> orderStateHistories = orderService.getOrderStateHistoryByOrderId(order.getId());


        return ok(myOrderInfo.render(order, logistics, orderStateHistories));

    }

    /**
     * 取消订单
     *
     * @param orderId
     * @param type
     *          CancelOrderType
     * @return
     */
    @SecuredAction
    public Result orderCancel(int orderId,int type) {

        orderService.cancelOrder(orderId, user_id, type);

        return ok(new JsonResult(true, "取消成功").toNode());

    }

    /**
     * 确认收货
     *
     * @param orderId
     *
     * @return
     */
    @SecuredAction
    public Result orderReceiving(int orderId) {

        orderService.receivingOrder(orderId, user_id);

        return ok(new JsonResult(true, "取消成功").toNode());

    }

    /**
     * 评论首页
     *
     * @param orderId
     * @return
     */
    @SecuredAction
    public Result orderAppraise(int orderId) {
        User user = SessionUtils.currentUser();

        Order order = orderService.getOrderById(orderId, user_id);
        for (OrderItem orderItem : order.getOrderItemList()) {
            orderItem.setProperties(skuAndStorageService.getSKUPropertyValueMap(orderItem.getSkuId()));
            if (orderItem.isAppraise()) {
                orderItem.setValuation(valuationService.findByOrderItemId(user_id, orderItem.getId()));
            }
        }

        return ok(myOrderAppraise.render(order));

    }

    /**
     * 申请退货页面
     *
     * @param orderId
     * @return
     */
    @SecuredAction
    public Result backApply(int orderId) {
        User user = SessionUtils.currentUser();

        return ok();

    }

    /**
     * 售后服务首页
     *
     * @return
     */
    @SecuredAction
    public Result backIndex() {
        User user = SessionUtils.currentUser();

        return ok();

    }

    /**
     * 售后详情
     *
     * @return
     */
    @SecuredAction
    public Result backContent() {
        User user = SessionUtils.currentUser();

        return ok();

    }

}
