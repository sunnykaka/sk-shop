package scheduler;

import common.utils.DateUtils;
import ordercenter.constants.CancelOrderType;
import ordercenter.constants.OrderState;
import ordercenter.models.Order;
import ordercenter.models.OrderItem;
import ordercenter.models.OrderStateHistory;
import ordercenter.services.OrderService;
import play.Logger;
import productcenter.services.SkuAndStorageService;
import utils.Global;

import java.util.List;

/**
 * Created by lidujun on 2015-07-01.
 */
public class SysCancelOrderTask extends SchedulerTask {

    private static SysCancelOrderTask INSTANCE = new SysCancelOrderTask();

    private SysCancelOrderTask() {}

    public static SysCancelOrderTask getInstance() {
        return INSTANCE;
    }

    @Override
    void doRun() {
        Logger.info("----------------定时1小时未支付系统定时自动取消订单SysCancelOrderTask开始执行---------------------");
        OrderService orderService = Global.ctx.getBean(OrderService.class);
        List<Order> orderList = orderService.getAllOrderListByOrderState(OrderState.Create);
        if(orderList != null && orderList.size() > 0) {
            SkuAndStorageService skuAndStorageService = Global.ctx.getBean(SkuAndStorageService.class);
            for(Order order : orderList) {
                if(!order.getOrderState().equals(OrderState.Create)) {
                    continue;
                }

                if(DateUtils.current().getMillis() - order.getCreateTime().getMillis() < 3600000) {
                    continue;
                }

                int count = orderService.updateOrderStateByStrictState(order.getId(), OrderState.Cancel,order.getOrderState());
                if(count == 1) {
                    List<OrderItem> orderItemList = orderService.queryOrderItemsByOrderId(order.getId());
                    for(OrderItem orderItem : orderItemList) {
                        orderService.updateOrderItemStateByStrictState(orderItem.getId(), OrderState.Cancel, orderItem.getOrderState());
                        //增加库存
                        skuAndStorageService.addSkuStock(orderItem.getSkuId(), orderItem.getNumber());
                    }
                    orderService.createOrderStateHistory(new OrderStateHistory(order, OrderState.Cancel.getLogMsg(), CancelOrderType.Sys.getName()));
                }
            }
        }
        Logger.info("----------------定时1小时未支付系统定时自动取消订单SysCancelOrderTask执行结束---------------------");
    }
}
