package productcenter.product;

import models.ProductPicture;
import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import services.ProductPictureService;
import utils.Global;

import java.util.List;
import java.util.Optional;

/**
 * 产品（商品）图片 Service测试
 * User: lidujun
 * Date: 2015-04-02
 */
public class ProductPictureServiceTest extends WithApplication {

    private ProductPictureService productPictureService;

    @Before
    public void setUp() {
        super.startPlay();
        productPictureService = Global.ctx.getBean(ProductPictureService.class);
    }

    @Test
    public void testSave() {
        ProductPicture productPicture = new ProductPicture();
        productPicture.setProductId(1);
        productPicture.setSkuId(1);
        productPicture.setOriginalName("pic_ldj_test_01");
        productPicture.setName("pic_ldj_test_02");
        productPicture.setPicUrl("www.baidu.com/1.jpg");
        productPicture.setType("test");

        System.out.println("test productPicture: " + productPicture);

        productPictureService.save(productPicture);

        System.out.println("after save productPicture: " + productPicture);
    }

    @Test
    public void testRealDelete() {
        Optional<ProductPicture> productPictureOpt = productPictureService.getProductPictureById(1);
        if(productPictureOpt.isPresent()) {
            ProductPicture productPicture = productPictureOpt.get();
            System.out.println("testRealDelete productPicture: " + productPicture);
            productPictureService.realDelete(1);
        }
    }

    @Test
    public void testUpdate() {
        Optional<ProductPicture> productPictureOpt = productPictureService.getProductPictureById(2);
        if(productPictureOpt.isPresent()) {
            ProductPicture productPicture = productPictureOpt.get();
            productPicture.setProductId(2);
            productPicture.setSkuId(1);
            productPicture.setOriginalName("pic_ldj_test_01");
            productPicture.setName("pic_ldj_test_02_更新");
            productPicture.setPicUrl("www.baidu.com/1.jpg");
            productPicture.setType("test");

            System.out.println("test testUpdate productPicture: " + productPicture);

            productPictureService.update(productPicture);

            System.out.println("after update productPicture: " + productPicture);
        }
    }

    @Test
    public void getProductPictureById() {
        Optional<ProductPicture> productPictureOpt = productPictureService.getProductPictureById(2);
        if(productPictureOpt.isPresent()) {
            System.out.println("getProductPictureById productPicture: " + productPictureOpt.get());
        }
    }

    @Test
    public void testGetProductPictureList() {
        ProductPicture param = new ProductPicture();
        param.setProductId(1);
        List<ProductPicture> productPicturetList = productPictureService.getProductPictureList(Optional.ofNullable(null), param);
        System.out.println("testGetProductPictureList productPicturetList: " + productPicturetList.size() + "\n" + productPicturetList);
    }

}
