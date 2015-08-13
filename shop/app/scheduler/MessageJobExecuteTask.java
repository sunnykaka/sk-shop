package scheduler;

import common.utils.scheduler.SchedulerTask;
import services.MessageJobService;
import utils.Global;

/**
 * Created by liubin on 15-6-4.
 */
public class MessageJobExecuteTask extends SchedulerTask {

    private static MessageJobExecuteTask INSTANCE = new MessageJobExecuteTask();

    private MessageJobExecuteTask() {}

    public static MessageJobExecuteTask getInstance() {
        return INSTANCE;
    }

    @Override
    protected void doRun() {

        MessageJobService messageJobService = Global.ctx.getBean(MessageJobService.class);

        messageJobService.executeUnprocessedMessageJobs();

    }


}
