package common.utils;

import com.google.common.base.Preconditions;
import common.services.SmsHistoryService;
import common.utils.play.BaseGlobal;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.Play;

import com.esms.*;
import com.esms.common.entity.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * Created by liubin on 15-5-7.
 */
public class SmsUtils {

    public static boolean sendSms(String[] phones, String content) {
        Preconditions.checkArgument(phones != null && phones.length > 0, "短信未发送, 手机号码不能为空");
        for(String phone : phones) {
            Preconditions.checkArgument(RegExpUtils.isPhone(phone), String.format("短信未发送, 手机号码[%s]不正确", Arrays.toString(phones)));
        }
        Preconditions.checkArgument(StringUtils.isNotBlank(content), "短信未发送, 内容不能为空");

        boolean mock = Play.application().configuration().getBoolean("sms.mock");
        if(mock) {
            play.Logger.info(String.format("mock环境不发送真实短信 收信手机:[%s], 短信内容:[%s]", Arrays.toString(phones), content));

        } else {
            if(play.Logger.isDebugEnabled()) {
                play.Logger.debug(String.format("发送短信, 收信手机:[%s], 短信内容:[%s]", Arrays.toString(phones), content));
            }

            try {
                GsmsResponse resp = doSendSms(phones, content);
                BaseGlobal.ctx.getBean(SmsHistoryService.class).createSmsHistory(phones, content, resp);

            } catch (Exception e) {
                Logger.error("短信发送失败, 手机号:" + Arrays.toString(phones), e);
                return false;
            }

        }

        return true;
    }

    public static boolean sendSms(String phone, String content) {
        return sendSms(new String[]{phone}, content);
    }

    private static GsmsResponse doSendSms(String[] phones, String content) throws Exception{

        String user = Play.application().configuration().getString("sms.user");
        String password = Play.application().configuration().getString("sms.password");
        Account ac = new Account(user, password);
        PostMsg pm = new PostMsg();
        String cmHost = Play.application().configuration().getString("sms.cm.host");
        int cmPort = Play.application().configuration().getInt("sms.cm.port");
        String wsHost = Play.application().configuration().getString("sms.ws.host");
        int wsPort = Play.application().configuration().getInt("sms.ws.port");

        pm.getCmHost().setHost(cmHost, cmPort);//设置网关的IP和port，用于发送信息
        pm.getWsHost().setHost(wsHost, wsPort);//设置网关的 IP和port，用于获取账号信息、上行、状态报告等等

        List<MessageData> messageDataList = new ArrayList<>();

        for(String phone : phones) {
            messageDataList.add(new MessageData(phone, content));
        }

        MTPack pack = new MTPack();
        pack.setBatchID(UUID.randomUUID());
        pack.setBatchName("短信测试批次");
        pack.setMsgType(MTPack.MsgType.SMS);
        pack.setBizType(0);
        pack.setDistinctFlag(false);
        pack.setSendType(MTPack.SendType.MASS);
        pack.setMsgs(messageDataList);

        GsmsResponse resp = pm.post(ac, pack);

        if(Logger.isDebugEnabled()) {
            Logger.debug("短信发送结果: " + resp);
        }

        return resp;

    }


}
