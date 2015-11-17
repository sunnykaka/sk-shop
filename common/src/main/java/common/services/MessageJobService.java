package common.services;

import common.constants.MessageJobSource;
import common.constants.MessageJobType;
import common.models.MessageJob;
import common.utils.DateUtils;
import common.utils.EmailUtils;
import common.utils.SmsUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import play.Logger;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by liubin on 15-8-13.
 */
@Service
public class MessageJobService {

    @Autowired
    private GeneralDao generalDao;

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);

    private final Logger.ALogger SCHEDULER_LOGGER = Logger.of("schedulerTask");

    @Transactional
    public void executeUnprocessedMessageJobs() {

        String jpql = "select mj from MessageJob mj where mj.processed = :processed " +
                "and (mj.targetTime is null or mj.targetTime <= :targetTime) ";
        Map<String, Object> params = new HashMap<>();
        params.put("processed", false);
        params.put("targetTime", DateUtils.current());
        List<MessageJob> messageJobs = generalDao.query(jpql, Optional.empty(), params);
        if(messageJobs.isEmpty()) return;

        SCHEDULER_LOGGER.debug("message job tasks准备执行, 任务数量: " + messageJobs.size());
        List<Object[]> futureList = new ArrayList<>();
        messageJobs.forEach(messageJob -> {
            Future<Boolean> f = EXECUTOR.submit(() -> executeJob(messageJob));
            futureList.add(new Object[]{messageJob, f});
        });

        futureList.forEach(array -> {
            MessageJob messageJob = (MessageJob) array[0];
            Future<Boolean> f = (Future<Boolean>) array[1];
            boolean result = false;
            String processInfo = null;
            try {
                result = f.get(5, TimeUnit.SECONDS);
            } catch (CancellationException | InterruptedException | TimeoutException e) {
                processInfo = e.getMessage();
                SCHEDULER_LOGGER.error(String.format("MessageJob[id=%d]处理失败: " + e.getMessage(), messageJob.getId()));
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause != null) {
                    processInfo = cause.getMessage();
                    SCHEDULER_LOGGER.error(String.format("MessageJob[id=%d]处理失败: " + cause.getMessage(), messageJob.getId()));
                }
            }

            if (result || processInfo != null) {
                messageJob.setProcessed(result);
                messageJob.setProcessInfo(processInfo);
                generalDao.persist(messageJob);
            }

        });

    }

    private boolean executeJob(MessageJob messageJob) {
        String contact = messageJob.getContact();
        if(StringUtils.isBlank(contact)) {
            throw new IllegalArgumentException("contact为空");
        }
        String[] contacts = contact.split(",");
        if(contacts.length == 0) {
            throw new IllegalArgumentException("contact为空");
        }
        if(StringUtils.isBlank(messageJob.getContent())) {
            throw new IllegalArgumentException("消息内容为空");
        }

        boolean result = false;

        switch (messageJob.getType()) {
            case SMS:
                result = SmsUtils.sendSms(contacts, messageJob.getContent());
                break;
            case EMAIL:
                if(StringUtils.isBlank(messageJob.getTitle())) {
                    throw new IllegalArgumentException("邮件标题为空");
                }
                for(String c : contacts) {
                    if(EmailUtils.sendEmail(c, messageJob.getTitle(), messageJob.getContent())) {
                        result = true;
                    }
                }
                break;
            default:
                break;
        }

        return result;
    }

    /**
     * 创建发送短信任务
     *
     * @param contact 用户联系手机号码,多个手机号码用逗号","分隔
     * @param content 消息内容
     * @param source 来源
     * @param targetTime 定时发送, 不需要就传null
     */
    @Transactional
    public MessageJob sendSmsMessage(String contact, String content, MessageJobSource source, DateTime targetTime) {
        MessageJob messageJob = new MessageJob();
        messageJob.setContact(contact);
        messageJob.setContent(content);
        messageJob.setProcessed(false);
        messageJob.setTargetTime(targetTime);
        messageJob.setType(MessageJobType.SMS);
        messageJob.setSource(source);
        generalDao.persist(messageJob);
        return messageJob;
    }


    /**
     * 创建发送邮件任务
     *
     * @param contact 用户邮箱,多个用逗号","分隔
     * @param title 邮件标题
     * @param content 邮件内容
     * @param source 来源
     * @param targetTime 定时发送, 不需要就传null
     */
    @Transactional
    public MessageJob sendEmail(String contact, String title, String content, MessageJobSource source, DateTime targetTime) {
        MessageJob messageJob = new MessageJob();
        messageJob.setContact(contact);
        messageJob.setTitle(title);
        messageJob.setContent(content);
        messageJob.setProcessed(false);
        messageJob.setTargetTime(targetTime);
        messageJob.setType(MessageJobType.EMAIL);
        messageJob.setSource(source);
        generalDao.persist(messageJob);
        return messageJob;
    }

}
