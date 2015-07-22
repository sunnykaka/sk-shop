package common.utils;

import base.BaseTest;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Created by liubin on 15-4-2.
 */
public class SmsTest extends BaseTest {


    @Test
    @Ignore
    public void test() throws Exception {

        SmsUtils.sendSms("yourphone", "您好，您的验证码是123456");

    }

}
