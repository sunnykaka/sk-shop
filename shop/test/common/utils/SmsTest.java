package common.utils;

import org.junit.Ignore;
import org.junit.Test;
import play.test.WithApplication;


/**
 * Created by liubin on 15-4-2.
 */
public class SmsTest extends WithApplication{


    @Test
    @Ignore
    public void test() throws Exception {

        SmsUtils.sendSms("yourphone", "圣光普照众生");

    }

}
