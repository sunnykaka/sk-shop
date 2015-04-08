package productcenter.product;

import productcenter.constants.PropertyType;
import productcenter.models.ProductProperty;
import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import productcenter.services.ProductPropertyService;
import utils.Global;

import java.util.List;
import java.util.Optional;

/**
 * 标签
 * User: lidujun
 * Date: 2015-04-03
 */
public class ProductPropertyServiceTest extends WithApplication {

    private ProductPropertyService productPropertyService;

    @Before
    public void setUp() {
        super.startPlay();
        productPropertyService = Global.ctx.getBean(ProductPropertyService.class);
    }

    @Test
    public void testSave() {
        ProductProperty property = new ProductProperty();
        property.setProductId(1);
        property.setType(PropertyType.SELL_PROPERTY);
        //商品的pidvid json串，表示商品的属性，按属性在类目上的优先级排序
        // '{\"singlePidVidMap\":{\"1\":4294977296,\"4\":17179879190,\"5\":21474846488},\"pidvid\":[4294977296,17179879190,21474846488],\"multiPidVidMap\":{}}'
        property.setJson("{\\\"singlePidVidMap\\\":{\\\"1\\\":4294977296,\\\"4\\\":17179879190,\\\"5\\\":21474846488},\\\"pidvid\\\":[4294977296,17179879190,21474846488],\\\"multiPidVidMap\\\":{}}");

        System.out.println("testSave property: " + property);

        productPropertyService.save(property);

        System.out.println("after save property: " + property);
    }

    @Test
    public void testRealDelete() {
        Optional<ProductProperty> propertyOpt = productPropertyService.getProductPropertyById(1);
        if(propertyOpt.isPresent()) {
            ProductProperty property = propertyOpt.get();
            System.out.println("testRealDelete property: " + property);
            productPropertyService.realDelete(1);
        }
    }

    @Test
    public void testUpdate() {
        Optional<ProductProperty> propertyOpt = productPropertyService.getProductPropertyById(2);
        if(propertyOpt.isPresent()) {
            ProductProperty property = propertyOpt.get();
            property.setProductId(2);
            property.setType(PropertyType.SELL_PROPERTY);
            //商品的pidvid json串，表示商品的属性，按属性在类目上的优先级排序
            // '{\"singlePidVidMap\":{\"1\":4294977296,\"4\":17179879190,\"5\":21474846488},\"pidvid\":[4294977296,17179879190,21474846488],\"multiPidVidMap\":{}}'
            property.setJson("{\\\"singlePidVidMap\\\":{\\\"1\\\":4294977296,\\\"4\\\":17179879190,\\\"5\\\":21474846488},\\\"pidvid\\\":[4294977296,17179879190,21474846488],\\\"multiPidVidMap\\\":{}}");

            System.out.println("testUpdate property: " + property);

            productPropertyService.update(property);

            System.out.println("after testUpdate property: " + property);
        }
    }

    @Test
    public void testGetValuePicById() {
        Optional<ProductProperty> propertyOpt = productPropertyService.getProductPropertyById(2);
        if(propertyOpt.isPresent()) {
            System.out.println("testGetValuePicById property: " + propertyOpt.get());
        }
    }

    @Test
    public void testGetValuePicList() {
        ProductProperty param = new ProductProperty();
        param.setProductId(1);
        List<ProductProperty> propertyList = productPropertyService.getProductPropertyList(Optional.ofNullable(null), param);
        System.out.println("testGetValuePicList propertyList: " + propertyList.size() + "\n" + propertyList);
    }

}
