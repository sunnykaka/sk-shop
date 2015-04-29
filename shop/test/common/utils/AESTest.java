package common.utils;

import common.utils.test.EncryptUtil;
import org.junit.Test;
import play.Play;
import play.test.WithApplication;

import java.util.Base64;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.test.Helpers.*;


/**
 * Created by liubin on 15-4-2.
 */
public class AESTest extends WithApplication{


    @Test
    public void test() throws Exception {

//        System.out.println(Base64.getEncoder().encodeToString(AES.initPasswordKey()));

        String secret = Play.application().configuration().getString("shop.secret");
        byte[] password = Base64.getDecoder().decode(secret);

        String content = "hello, my friend";
        byte[] contentAfter = AES.decrypt(AES.encrypt(content.getBytes("UTF-8"), password), password);
        String contentByUtil = EncryptUtil.decrypt(EncryptUtil.encrypt(content));

        assertThat(new String(contentAfter, "UTF-8"), is(content));
        assertThat(contentByUtil, is(content));



        content = "你好我的朋友";
        contentAfter = AES.decrypt(AES.encrypt(content.getBytes("UTF-8"), password), password);
        contentByUtil = EncryptUtil.decrypt(EncryptUtil.encrypt(content));

        assertThat(new String(contentAfter, "UTF-8"), is(content));
        assertThat(contentByUtil, is(content));

    }

}
