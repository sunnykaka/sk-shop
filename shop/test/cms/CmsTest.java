package cms;

import org.junit.Assert;
import org.junit.Test;
import play.test.WithApplication;
import services.CmsService;
import utils.Global;

import java.sql.SQLException;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

/**
 * Created by amos on 15-5-11.
 */
public class CmsTest extends WithApplication {


    @Test
    public void testProdOnPublish() {
            CmsService cmsService = Global.ctx.getBean(CmsService.class);
            try {
                Boolean result = cmsService.onFirstPublish(2184);
                Assert.assertTrue(result);

                Assert.assertFalse(cmsService.onFirstPublish(2186));

            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
}
