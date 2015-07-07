package controllers.user;

import common.exceptions.AppBusinessException;
import common.utils.FormUtils;
import common.utils.JsonResult;
import common.utils.page.Page;
import common.utils.play.BaseGlobal;
import ordercenter.dtos.BackApplyForm;
import ordercenter.models.*;
import ordercenter.services.BackGoodsService;
import ordercenter.services.OrderService;
import ordercenter.services.TradeService;
import ordercenter.services.ValuationService;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import productcenter.services.SkuAndStorageService;
import usercenter.dtos.DesignerView;
import usercenter.models.User;
import usercenter.services.DesignerService;
import usercenter.utils.SessionUtils;
import utils.Global;
import utils.secure.SecuredAction;
import views.html.error_404;
import views.html.user.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 我的订单管理
 * <p>
 * Created by zhb on 15-5-7.
 */
@org.springframework.stereotype.Controller
public class MyOrderController extends Controller {

    @Autowired
    private OrderService orderService;

    @Autowired
    private SkuAndStorageService skuAndStorageService;

    @Autowired
    private ValuationService valuationService;

    @Autowired
    private BackGoodsService backGoodsService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private DesignerService designerService;

    /**
     * 单订管理首页
     *
     * @param queryType 0所有订单、1待收货、2待评价
     * @return
     */
    @SecuredAction
    public Result index(int queryType, int pageNo, int pageSize) {
        User user = SessionUtils.currentUser();

        Page<Order> page = new Page<>(pageNo, pageSize);

        List<Order> orderList = orderService.getOrderByUserId(Optional.of(page), user.getId(), queryType);
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
            order.setBackGoodsl(orderService.isBackGoods(order));

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

        Order order = orderService.getOrderById(orderId, user.getId());

        if(null == order){
            List<DesignerView> designerViews = new ArrayList<>();
            try {
                designerViews = designerService.lastCreateDesigner(4);
            } catch (Exception e) {
                Logger.error("", e);
            }
            return Results.notFound(error_404.render("查询不到你的订单", designerViews));
        }

        Logistics logistics = orderService.getLogisticsByOrderId(order.getId());
        for (OrderItem orderItem : order.getOrderItemList()) {
            orderItem.setProperties(skuAndStorageService.getStockKeepingUnitById(orderItem.getSkuId()).getSkuProperties());
        }
        List<OrderStateHistory> orderStateHistories = orderService.getOrderStateHistoryByOrderId(order.getId());
        Trade trade = tradeService.getTradeOrdeByOrderId(order.getId());

        return ok(myOrderInfo.render(order, logistics, orderStateHistories, trade));

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

        User user = SessionUtils.currentUser();
        orderService.cancelOrder(orderId, user.getId(), type);

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
        User user = SessionUtils.currentUser();

        orderService.receivingOrder(orderId, user.getId());

        return redirect(routes.MyOrderController.orderAppraise(orderId));//(new JsonResult(true, "确认收货成功").toNode());

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

        Order order = orderService.getOrderById(orderId, user.getId());

        if(null == order){
            List<DesignerView> designerViews = new ArrayList<>();
            try {
                designerViews = designerService.lastCreateDesigner(4);
            } catch (Exception e) {
                Logger.error("", e);
            }
            return Results.notFound(error_404.render("查询不到你的订单", designerViews));
        }

        for (OrderItem orderItem : order.getOrderItemList()) {
            orderItem.setProperties(skuAndStorageService.getStockKeepingUnitById(orderItem.getSkuId()).getSkuProperties());
            if (orderItem.isAppraise()) {
                orderItem.setValuation(valuationService.findByOrderItemId(user.getId(), orderItem.getId()));
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

        Order order = orderService.getOrderById(orderId, user.getId());

        if(null == order){
            List<DesignerView> designerViews = new ArrayList<>();
            try {
                designerViews = designerService.lastCreateDesigner(4);
            } catch (Exception e) {
                Logger.error("", e);
            }
            return Results.notFound(error_404.render("查询不到你的订单", designerViews));
        }

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

        Form<BackApplyForm> backApplyForm = Form.form(BackApplyForm.class).bindFromRequest();

        if (!backApplyForm.hasErrors()) {
            try {

                backGoodsService.submitBackGoods(backApplyForm.get(),user);

                return ok(new JsonResult(true, "提交成功").toNode());

            } catch (AppBusinessException e) {
                backApplyForm.reject("backReason", e.getMessage());
            }
        }

        return ok(new JsonResult(false, FormUtils.showErrorInfo(backApplyForm.errors())).toNode());
    }

    /**
     * 取消退货订单
     *
     * @param backGoodsId
     * @return
     */
    @SecuredAction
    public Result backCancel(int backGoodsId){
        User user = SessionUtils.currentUser();

        try {
            backGoodsService.cancelBackApply(backGoodsId,user.getId());
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

        List<BackGoods> backGoodsList = backGoodsService.getMyBackGoods(Optional.of(page), user.getId());
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

        BackGoods backGoods = backGoodsService.getBackGoods(backGoodsId,user.getId());

        if(null == backGoods){
            List<DesignerView> designerViews = new ArrayList<>();
            try {
                designerViews = designerService.lastCreateDesigner(4);
            } catch (Exception e) {
                Logger.error("", e);
            }
            return Results.notFound(error_404.render("查询不到你的售后订单", designerViews));
        }

        for(BackGoodsItem backGoodsItem:backGoods.getBackGoodsItemList()){
            OrderItem orderItem = orderService.getOrderItemById(backGoodsItem.getOrderItemId());
            orderItem.setProperties(skuAndStorageService.getSKUPropertyValueMap(orderItem.getSkuId()));
            backGoodsItem.setOrderItem(orderItem);
        }
        List<BackGoodsLog> backGoodsLogs = backGoodsService.getBackGoodsLog(backGoodsId);

        return ok(myBackInfo.render(backGoods,backGoodsLogs));

    }

}
