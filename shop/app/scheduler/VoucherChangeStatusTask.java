package scheduler;

import common.utils.scheduler.SchedulerTask;
import ordercenter.services.VoucherService;
import utils.Global;

/**
 * Created by liubin on 15-6-4.
 */
public class VoucherChangeStatusTask extends SchedulerTask {

    private static VoucherChangeStatusTask INSTANCE = new VoucherChangeStatusTask();

    private VoucherChangeStatusTask() {}

    public static VoucherChangeStatusTask getInstance() {
        return INSTANCE;
    }

    @Override
    protected void doRun() {

        VoucherService voucherService = Global.ctx.getBean(VoucherService.class);

        voucherService.changeStatus();

    }


}
