package common.services;

import org.springframework.stereotype.Service;

/**
 * Created by liubin on 15-5-4.
 */
@Service
public class SmsService {

    public boolean sendMessage(String phone, String message) {

        //TODO 调用短信接口
        if(play.Logger.isDebugEnabled()) {
            play.Logger.debug(String.format("发送短信, 收信手机:[%s], 短信内容:[%s]", phone, message));
        }

        return true;
    }

}
