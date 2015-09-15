package controllers.api.user;

import api.response.user.OrderDto;
import api.response.user.OrderInfoDto;
import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;
import common.utils.FormUtils;
import common.utils.JsonResult;
import common.utils.JsonUtils;
import common.utils.ParamUtils;
import common.utils.page.Page;
import controllers.BaseController;
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
import productcenter.models.StockKeepingUnit;
import productcenter.services.SkuAndStorageService;
import usercenter.dtos.DesignerView;
import usercenter.models.Designer;
import usercenter.models.User;
import usercenter.services.DesignerService;
import usercenter.utils.SessionUtils;
import utils.secure.SecuredAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 我的订单管理
 * <p>
 * Created by zhb on 15-5-7.
 */
@org.springframework.stereotype.Controller
public class MyOrderApiController extends BaseController {

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
    public Result list(int queryType, int pageNo, int pageSize) {
        User user = this.currentUser();

        Page<Order> page = new Page<>(pageNo, pageSize);
        Page<OrderDto> pageDto = new Page<>(pageNo, pageSize);

        List<Order> orderList = orderService.getOrderByUserId(Optional.of(page), user.getId(), queryType);
        List<OrderDto> orderDtos = new ArrayList<>();
        try {
            for (Order order : orderList) {
                orderDtos.add(OrderDto.build(order));
            }
        }catch (Exception e){
            play.Logger.info("--------MyOrderApiController list error-----------\n userId = " + user.getId());
            throw new AppBusinessException(ErrorCode.InternalError);
        }

        pageDto.setResult(orderDtos);

        return ok(JsonUtils.object2Node(pageDto));

    }

    /**
     * 订单详情页面
     *
     * @return
     */
    @SecuredAction
    public Result orderInfo(int orderId) {
        User user = this.currentUser();

        Order order = orderService.getOrderById(orderId, user.getId());

        if(null == order){
            throw new AppBusinessException(ErrorCode.Conflict,"查询不到此订单信息");
        }

        Logistics logistics = orderService.getLogisticsByOrderId(order.getId());
        for (OrderItem orderItem : order.getOrderItemList()) {
            StockKeepingUnit stockKeepingUnit = skuAndStorageService.getStockKeepingUnitById(orderItem.getSkuId());
            if(stockKeepingUnit != null) {
                orderItem.setProperties(stockKeepingUnit.getSkuProperties());
            }
            Designer designer = designerService.getDesignerById(orderItem.getCustomerId());
            orderItem.setCustomerName(designer==null?"":designer.getName());
        }
        //List<OrderStateHistory> orderStateHistories = orderService.getOrderStateHistoryByOrderId(order.getId());
        Trade trade = tradeService.getTradeOrdeByOrderId(order.getId());

        OrderInfoDto orderInfoDto = OrderInfoDto.build(order,logistics,trade);

        return ok(JsonUtils.object2Node(orderInfoDto));

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
        User user = this.currentUser();

        orderService.receivingOrder(orderId, user.getId());

        return noContent();

    }
//
//    /**
//     * 评论首页
//     *
//     * @param orderId
//     * @return
//     */
//    @SecuredAction
//    public Result orderAppraise(int orderId) {
//        User user = SessionUtils.currentUser();
//
//        Order order = orderService.getOrderById(orderId, user.getId());
//
//        if(null == order){
//            List<DesignerView> designerViews = new ArrayList<>();
//            try {
//                designerViews = designerService.lastCreateDesigner(4);
//            } catch (Exception e) {
//                Logger.error("", e);
//            }
//            return Results.notFound(error_404.render("查询不到你的订单", designerViews));
//        }
//
//        for (OrderItem orderItem : order.getOrderItemList()) {
//            StockKeepingUnit stockKeepingUnit = skuAndStorageService.getStockKeepingUnitById(orderItem.getSkuId());
//            if(stockKeepingUnit != null) {
//                orderItem.setProperties(stockKeepingUnit.getSkuProperties());
//            }
//
//            if (orderItem.isAppraise()) {
//                orderItem.setValuation(valuationService.findByOrderItemId(user.getId(), orderItem.getId()));
//            }
//        }
//
//        return ok(myOrderAppraise.render(order));
//
//    }

}
