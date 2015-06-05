package scheduler;

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
    void doRun() {

        CmsService cmsService = Global.ctx.getBean(CmsService.class);

        cmsService.remindExhibitionStart();


    }


}
