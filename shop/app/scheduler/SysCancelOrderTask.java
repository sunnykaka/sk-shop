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
        OrderService orderService = Global.ctx.getBean(OrderService.class);
        orderService.timerAutoCancelOrderProcess();
        //父类会负责打印日志并计时
    }
}
