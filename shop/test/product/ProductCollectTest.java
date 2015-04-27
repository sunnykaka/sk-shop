package product;

import common.utils.test.BaseTest;
import org.joda.time.DateTime;
import org.junit.Test;
import productcenter.models.ProductCollect;
import productcenter.services.ProductCollectService;
import utils.Global;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

/**
 * Created by zhb on 15-4-7.
 */
public class ProductCollectTest implements BaseTest {

    @Test
    public void testSaveProductCollec() {
        running(fakeApplication(), () -> {

            ProductCollectService productCollectService = Global.ctx.getBean(ProductCollectService.class);

            ProductCollect productCollect = new ProductCollect();
            productCollect.setProductId(10000);
            productCollect.setCollectTime(new DateTime());
            productCollect.setUserId(1);

            productCollectService.createProductCollect(productCollect);

        });
    }

}
