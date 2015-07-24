package scheduler;

import common.utils.scheduler.SchedulerTask;
import ordercenter.services.OrderService;
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
    protected void doRun() {
        OrderService orderService = Global.ctx.getBean(OrderService.class);
        orderService.timerAutoCancelOrderProcess();
    }
}
