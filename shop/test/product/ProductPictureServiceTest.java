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
        System.out.println("-----------------------------: " + list);
    }


}
