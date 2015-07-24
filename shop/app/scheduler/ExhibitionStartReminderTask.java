package scheduler;

import common.utils.scheduler.SchedulerTask;
import services.CmsService;
import utils.Global;

/**
 * Created by liubin on 15-6-4.
 */
public class ExhibitionStartReminderTask extends SchedulerTask {

    private static ExhibitionStartReminderTask INSTANCE = new ExhibitionStartReminderTask();

    private ExhibitionStartReminderTask() {}

    public static ExhibitionStartReminderTask getInstance() {
        return INSTANCE;
    }

    @Override
    protected void doRun() {

        CmsService cmsService = Global.ctx.getBean(CmsService.class);

        cmsService.remindExhibitionStart();


    }


}
