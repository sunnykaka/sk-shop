package scheduler;

import ordercenter.services.OrderService;
import play.Logger;
import utils.Global;

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
        orderService.timerAutoCancelOrderProcess();
        Logger.info("----------------定时1小时未支付系统定时自动取消订单SysCancelOrderTask执行结束---------------------");
    }
}
