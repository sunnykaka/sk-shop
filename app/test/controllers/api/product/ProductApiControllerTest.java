package controllers.api.product;

import base.BaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import productcenter.models.Product;
import productcenter.services.ProductTestDataService;
import usercenter.models.Designer;
import utils.Global;


/**
 * Created by liubin on 15-4-2.
 */
public class ProductApiControllerTest extends BaseTest{

    @Test
    public void testRequestProductSuccess() throws Exception {

//        initProduct();

        ProductTestDataService productTestDataService = Global.ctx.getBean(ProductTestDataService.class);
        Product product = productTestDataService.initProduct();
        System.out.println("init product id: " + product.getId());

    }



}
