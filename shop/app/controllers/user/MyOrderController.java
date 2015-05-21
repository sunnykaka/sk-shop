package controllers.user;

import common.exceptions.AppBusinessException;
import common.utils.JsonResult;
import common.utils.page.Page;
import ordercenter.dtos.BackApplyForm;
import ordercenter.models.*;
import ordercenter.services.BackGoodsService;
import ordercenter.services.OrderService;
import ordercenter.services.ValuationService;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.services.SkuAndStorageService;
import usercenter.models.User;
import usercenter.utils.SessionUtils;
import utils.secure.SecuredAction;
import views.html.user.*;

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

    @Autowired
    private BackGoodsService backGoodsService;

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
                orderItem.setProperties(skuAndStorageService.getStockKeepingUnitById(orderItem.getSkuId()).getSkuProperties());
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
            orderItem.setProperties(skuAndStorageService.getStockKeepingUnitById(orderItem.getSkuId()).getSkuProperties());
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
            orderItem.setProperties(skuAndStorageService.getStockKeepingUnitById(orderItem.getSkuId()).getSkuProperties());
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

        Order order = orderService.getOrderById(orderId, user_id);
        Logistics logistics = orderService.getLogisticsByOrderId(order.getId());
        for (OrderItem orderItem : order.getOrderItemList()) {
            orderItem.setProperties(skuAndStorageService.getSKUPropertyValueMap(orderItem.getSkuId()));
        }
        List<OrderStateHistory> orderStateHistories = orderService.getOrderStateHistoryByOrderId(order.getId());
        return ok(myBackGoodsApply.render(order, logistics, orderStateHistories));
    }

    /**
     * 申请提交退货
     *
     * @return
     */
    @SecuredAction
    public Result backApplySubmit(){
        User user = SessionUtils.currentUser();
        user.setId(user_id);

        //TODO 判断是否订单是否达到退货要求

        Form<BackApplyForm> backApplyForm = Form.form(BackApplyForm.class).bindFromRequest();

        if (!backApplyForm.hasErrors()) {
            try {

                backGoodsService.submitBackGoods(backApplyForm.get(),user);

                return ok(new JsonResult(true, "提交成功").toNode());

            } catch (AppBusinessException e) {
                backApplyForm.reject("backReason", e.getMessage());
            }
        }

        return ok(new JsonResult(false, backApplyForm.errorsAsJson().toString()).toNode());
    }

    @SecuredAction
    public Result backCancel(int backGoodsId){

        try {
            backGoodsService.cancelBackApply(backGoodsId,user_id);
            return ok(new JsonResult(true, "取消成功").toNode());
        } catch (AppBusinessException e) {
            return ok(new JsonResult(false, e.getMessage()).toNode());
        }

    }

    /**
     * 售后服务首页
     *
     * @return
     */
    @SecuredAction
    public Result backIndex(int pageNo, int pageSize) {
        User user = SessionUtils.currentUser();

        Page<BackGoods> page = new Page<>(pageNo, pageSize);

        List<BackGoods> backGoodsList = backGoodsService.getMyBackGoods(Optional.of(page), user_id);
        for(BackGoods backGoods:backGoodsList){

            for(BackGoodsItem backGoodsItem:backGoods.getBackGoodsItemList()){
                OrderItem orderItem = orderService.getOrderItemById(backGoodsItem.getOrderItemId());
                orderItem.setProperties(skuAndStorageService.getSKUPropertyValueMap(orderItem.getSkuId()));
                backGoodsItem.setOrderItem(orderItem);
            }

        }
        page.setResult(backGoodsList);

        return ok(myBack.render(page));

    }

    /**
     * 售后详情
     *
     * @return
     */
    @SecuredAction
    public Result backContent(int backGoodsId) {
        User user = SessionUtils.currentUser();

        BackGoods backGoods = backGoodsService.getBackGoods(backGoodsId,user_id);
        for(BackGoodsItem backGoodsItem:backGoods.getBackGoodsItemList()){
            OrderItem orderItem = orderService.getOrderItemById(backGoodsItem.getOrderItemId());
            orderItem.setProperties(skuAndStorageService.getSKUPropertyValueMap(orderItem.getSkuId()));
            backGoodsItem.setOrderItem(orderItem);
        }
        List<BackGoodsLog> backGoodsLogs = backGoodsService.getBackGoodsLog(backGoodsId);

        return ok(myBackInfo.render(backGoods,backGoodsLogs));

    }

}
