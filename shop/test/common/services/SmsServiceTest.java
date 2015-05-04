package common.services;

import org.junit.Test;
import play.test.WithApplication;
import play.twirl.api.Html;
import utils.Global;
import views.html.template.sms.userRegisterCode;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by liubin on 15-4-2.
 */
public class SmsServiceTest extends WithApplication{

    @Test
    public void test() {

        SmsService smsService = Global.ctx.getBean(SmsService.class);

        String phone = "18682000593";

        Html html = userRegisterCode.render(phone);

        assertThat(smsService.sendMessage(phone, html.body()), is(true));


    }


}
