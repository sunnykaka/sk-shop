package product;


import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import productcenter.models.ProductProperty;
import productcenter.services.ProductPropertyService;
import productcenter.constants.PropertyType;
import utils.Global;

import java.util.List;

/**
 * 产品（商品）图片 Service测试
 * User: lidujun
 * Date: 2015-04-02
 */
public class ProductPropertyServiceTest extends WithApplication {

    private ProductPropertyService productPropertyService;

    @Before
    public void setUp() {
        super.startPlay();
        productPropertyService = Global.ctx.getBean(ProductPropertyService.class);
    }

    @Test
    public void queryAllProductPictures() {
        List<ProductProperty> list = productPropertyService.queryAllProductProperty();
        System.out.println("-----------------------------: " + list.size() + "\n" + list);


    }

    @Test
    public void getProductPropertyById() {
        ProductProperty productProperty = productPropertyService.getProductPropertyById(5609);
        System.out.println("-----------------------------: " + productProperty);
    }

    @Test
    public void queryByProductId() {
        List<ProductProperty> list = productPropertyService.queryByProductId(2161);
        System.out.println("-----------------------------: " + list.size() + "\n" + list);
    }

    @Test
    public void queryProductPropertyByPropertyType() {
        ProductProperty productProperty = productPropertyService.queryProductPropertyByPropertyType(2161, PropertyType.SELL_PROPERTY);
        System.out.println("-----------------------------: " + productProperty);
    }
}
