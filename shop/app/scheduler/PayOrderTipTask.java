package scheduler;

import common.utils.scheduler.SchedulerTask;
import ordercenter.services.OrderService;
import utils.Global;

/**
 * Created by lidujun on 2015-07-01.
 */
public class PayOrderTipTask extends SchedulerTask {

    private static PayOrderTipTask INSTANCE = new PayOrderTipTask();

    private PayOrderTipTask() {}

    public static PayOrderTipTask getInstance() {
        return INSTANCE;
    }

    @Override
    protected void doRun() {
        OrderService orderService = Global.ctx.getBean(OrderService.class);
        orderService.timerAutoTipPayOrderProcess();
    }
}
