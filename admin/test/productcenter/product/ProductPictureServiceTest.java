package productcenter.product;

import models.ProductPicture;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import play.test.WithApplication;
import services.ProductPictureService;
import utils.Global;

import java.util.List;

/**
 * 产品（商品）图片 Service测试
 * User: lidujun
 * Date: 2015-04-02
 */
public class ProductPictureServiceTest extends WithApplication {

    private ProductPictureService productPictureService = Global.ctx.getBean(ProductPictureService.class);

    @Test
    @Rollback(false)
    public void testSave() {
        ProductPicture productPicture = new ProductPicture();
        productPicture.setProductId(1);
        productPicture.setSkuId(1);
        productPicture.setOriginalName("pic_ldj_test_01");
        productPicture.setName("pic_ldj_test_02");
        productPicture.setPicUrl("www.baidu.com/1.jpg");
        productPicture.setType("test");

        System.out.println("test productPicture: " + productPicture.toString());

        productPictureService.save(productPicture);

        System.out.println("after save productPicture: " + productPicture.toString());
    }

    @Test
    @Rollback(false)
    public void testRealDelete() {
        ProductPicture productPicture = productPictureService.getProductPictureById(1);
        System.out.println("testRealDelete productPicture: " + productPicture.toString());
        productPictureService.realDelete(1);
    }

    @Test
    @Rollback(false)
    public void testUpdate() {
        ProductPicture productPicture = productPictureService.getProductPictureById(1);
        productPicture.setProductId(2);
        productPicture.setSkuId(1);
        productPicture.setOriginalName("pic_ldj_test_01");
        productPicture.setName("pic_ldj_test_02_更新");
        productPicture.setPicUrl("www.baidu.com/1.jpg");
        productPicture.setType("test");

        System.out.println("test testUpdate productPicture: " + productPicture.toString());

        productPictureService.update(productPicture);

        System.out.println("after update productPicture: " + productPicture.toString());
    }

    @Test
    @Rollback(false)
    public void getProductPictureById() {
        ProductPicture productPicture = productPictureService.getProductPictureById(1);
        System.out.println("getProductPictureById productPicture: " + productPicture.toString());
    }

    @Test
    @Rollback(false)
    public void testGetProductPictureList() {
        ProductPicture param = new ProductPicture();
        //param.setId(1);
        param.setProductId(1);
        List<ProductPicture> productPicturetList = productPictureService.getProductPictureList(null, param);
        System.out.println("testGetProductPictureList productPicturetList: " + productPicturetList.size() + "\n" + productPicturetList);
    }

}
