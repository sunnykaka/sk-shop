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

        EmailUtils.sendEmail("zhenhaobin@163.com", "这是测试111", "这是测试信息111");

        //等待邮件发送完成
        Thread.sleep(30000L);


    }

}
