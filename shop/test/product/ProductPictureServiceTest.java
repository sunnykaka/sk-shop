package product;


import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import productcenter.models.ProductPicture;
import productcenter.services.ProductPictureService;
import utils.Global;

import java.util.List;

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
    public void queryAllProductPictures() {
        List<ProductPicture> list = productPictureService.queryAllProductPictures();
        System.out.println("-----------------------------: " + list.size() + "\n" + list);
    }

    @Test
    public void getProductPictureById() {
        ProductPicture productPicture = productPictureService.getProductPictureById(12527);
        System.out.println("-----------------------------: " + productPicture);
    }

    @Test
    public void queryProductPicturesByProductId() {
        List<ProductPicture> list = productPictureService.queryProductPicturesByProductId(2159);
        System.out.println("-----------------------------: " + list.size() + "\n" + list);
    }

    @Test
    public void queryProductPicturesBySkuId() {
        List<ProductPicture> list = productPictureService.queryProductPicturesBySkuId("10");
        System.out.println("-----------------------------: " + list.size() + "\n" + list);
    }

    @Test
    public void getMainProductPictureByProductId() {
        ProductPicture productPicture = productPictureService.getMainProductPictureByProductId(2159);
        System.out.println("-----------------------------: " + productPicture);
    }

    @Test
    public void getMinorProductPictureByProductId() {
        ProductPicture productPicture = productPictureService.getMinorProductPictureByProductId(2159);
        System.out.println("-----------------------------: " + productPicture);
    }

}
