package common.utils;

import base.BaseTest;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Created by liubin on 15-4-2.
 */
public class EmailTest extends BaseTest {


    @Test
    @Ignore
    public void test() throws Exception {

        EmailUtils.sendEmail("youremail@vip.qq.com", "这是测试", "这是测试信息");

        //等待邮件发送完成
        Thread.sleep(30000L);


    }

}
