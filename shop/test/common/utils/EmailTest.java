package common.utils;

import org.junit.Ignore;
import org.junit.Test;
import play.test.WithApplication;


/**
 * Created by liubin on 15-4-2.
 */
public class EmailTest extends WithApplication{


    @Test
    @Ignore
    public void test() throws Exception {

        EmailUtils.sendEmail("youremail@qq.com", "这是测试", "<html><body><h1>这是测试信息</h1><body></html>");

    }

}
