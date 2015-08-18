package services;

import base.BaseTest;
import common.constants.MessageJobSource;
import common.models.MessageJob;
import common.services.MessageJobService;
import common.utils.DateUtils;
import common.utils.JsonResult;
import controllers.user.routes;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import usercenter.cache.UserCache;
import usercenter.domain.SmsSender;
import usercenter.utils.SessionUtils;
import utils.Global;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.test.Helpers.*;


/**
 * Created by liubin on 15-4-2.
 */
public class MessageJobServiceTest extends BaseTest {


    @Test
    public void testCreateAndExecuteMessageJobSuccess() throws Exception {
        MessageJobService messageJobService = Global.ctx.getBean(MessageJobService.class);

        MessageJob messageJob1 = messageJobService.sendEmail("sunnykaka@vip.qq.com", "你好", "你好啊", MessageJobSource.CHANGE_EMAIL, null);
        MessageJob messageJob2 = messageJobService.sendSmsMessage("18682000593", "您的短信验证码是123456，两小时内有效", MessageJobSource.REGISTER_VERIFICATION_CODE, null);

        Thread.sleep(1000L);

        messageJobService.executeUnprocessedMessageJobs();

        doInTransactionWithGeneralDao(generalDao -> {

            MessageJob mj1 = generalDao.get(MessageJob.class, messageJob1.getId());
            assertThat(mj1.isProcessed(), is(true));
            assertThat(mj1.getProcessInfo(), nullValue());
            assertThat(mj1.getUpdateTime(), notNullValue());
            assertThat(mj1.getUpdateTime().isAfter(mj1.getCreateTime()), is(true));

            MessageJob mj2 = generalDao.get(MessageJob.class, messageJob2.getId());
            assertThat(mj2.isProcessed(), is(true));
            assertThat(mj2.getProcessInfo(), nullValue());
            assertThat(mj2.getUpdateTime(), notNullValue());
            assertThat(mj2.getUpdateTime().isAfter(mj2.getCreateTime()), is(true));

            return null;
        });

    }

    @Test
    public void testCreateErrorMessageJobToExecute() throws Exception {
        MessageJobService messageJobService = Global.ctx.getBean(MessageJobService.class);

        MessageJob messageJob1 = messageJobService.sendEmail("sunnykaka@vip.qq.com", "", "你好啊", MessageJobSource.CHANGE_EMAIL, null);
        MessageJob messageJob2 = messageJobService.sendSmsMessage("186", "您的短信验证码是123456，两小时内有效", MessageJobSource.REGISTER_VERIFICATION_CODE, null);

        messageJobService.executeUnprocessedMessageJobs();

        doInTransactionWithGeneralDao(generalDao -> {

            MessageJob mj1 = generalDao.get(MessageJob.class, messageJob1.getId());
            assertThat(mj1.isProcessed(), is(false));
            assertThat(mj1.getProcessInfo(), notNullValue());

            MessageJob mj2 = generalDao.get(MessageJob.class, messageJob2.getId());
            assertThat(mj2.isProcessed(), is(false));
            assertThat(mj2.getProcessInfo(), notNullValue());

            return null;
        });

    }

    @Test
    public void testSendMessageInTargetTime() throws Exception {
        MessageJobService messageJobService = Global.ctx.getBean(MessageJobService.class);

        MessageJob messageJob1 = messageJobService.sendEmail("sunnykaka@vip.qq.com", "你好", "你好啊", MessageJobSource.CHANGE_EMAIL, DateUtils.current().plusSeconds(3));

        messageJobService.executeUnprocessedMessageJobs();

        doInTransactionWithGeneralDao(generalDao -> {

            MessageJob mj1 = generalDao.get(MessageJob.class, messageJob1.getId());
            assertThat(mj1.isProcessed(), is(false));
            assertThat(mj1.getProcessInfo(), nullValue());

            return null;
        });

        Thread.sleep(4000L);
        messageJobService.executeUnprocessedMessageJobs();

        doInTransactionWithGeneralDao(generalDao -> {

            MessageJob mj1 = generalDao.get(MessageJob.class, messageJob1.getId());
            assertThat(mj1.isProcessed(), is(true));
            assertThat(mj1.getProcessInfo(), nullValue());
            assertThat(mj1.getUpdateTime(), notNullValue());
            assertThat(mj1.getUpdateTime().isAfter(mj1.getCreateTime()), is(true));

            return null;
        });

    }

}
