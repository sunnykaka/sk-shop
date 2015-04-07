package productcenter.product;

import constants.PropertyType;
import models.ProductProperty;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import play.test.WithApplication;
import services.ProductPropertyService;
import utils.Global;

import java.util.List;

/**
 * 标签
 * User: lidujun
 * Date: 2015-04-03
 */
public class ProductPropertyServiceTest extends WithApplication {

    private ProductPropertyService productPropertyService = Global.ctx.getBean(ProductPropertyService.class);

    @Test
    @Rollback(false)
    public void testSave() {
        ProductProperty property = new ProductProperty();
        property.setProductId(1);
        property.setType(PropertyType.SELL_PROPERTY);
        //商品的pidvid json串，表示商品的属性，按属性在类目上的优先级排序
        // '{\"singlePidVidMap\":{\"1\":4294977296,\"4\":17179879190,\"5\":21474846488},\"pidvid\":[4294977296,17179879190,21474846488],\"multiPidVidMap\":{}}'
        property.setJson("{\\\"singlePidVidMap\\\":{\\\"1\\\":4294977296,\\\"4\\\":17179879190,\\\"5\\\":21474846488},\\\"pidvid\\\":[4294977296,17179879190,21474846488],\\\"multiPidVidMap\\\":{}}");

        System.out.println("testSave property: " + property.toString());

        productPropertyService.save(property);

        System.out.println("after save property: " + property.toString());
    }

    @Test
    @Rollback(false)
    public void testRealDelete() {
        ProductProperty property = productPropertyService.getProductPropertyById(1);
        System.out.println("testRealDelete property: " + property.toString());
        productPropertyService.realDelete(1);
    }

    @Test
    @Rollback(false)
    public void testUpdate() {
        ProductProperty property = productPropertyService.getProductPropertyById(1);
        property.setProductId(2);
        property.setType(PropertyType.SELL_PROPERTY);
        //商品的pidvid json串，表示商品的属性，按属性在类目上的优先级排序
        // '{\"singlePidVidMap\":{\"1\":4294977296,\"4\":17179879190,\"5\":21474846488},\"pidvid\":[4294977296,17179879190,21474846488],\"multiPidVidMap\":{}}'
        property.setJson("{\\\"singlePidVidMap\\\":{\\\"1\\\":4294977296,\\\"4\\\":17179879190,\\\"5\\\":21474846488},\\\"pidvid\\\":[4294977296,17179879190,21474846488],\\\"multiPidVidMap\\\":{}}");


        System.out.println("testUpdate property: " + property.toString());

        productPropertyService.update(property);

        System.out.println("after testUpdate property: " + property.toString());
    }

    @Test
    @Rollback(false)
    public void testGetValuePicById() {
        ProductProperty property = productPropertyService.getProductPropertyById(1);
        System.out.println("testGetValuePicById property: " + property.toString());
    }

    @Test
    @Rollback(false)
    public void testGetValuePicList() {
        ProductProperty param = new ProductProperty();
        param.setProductId(1);
        List<ProductProperty> propertyList = productPropertyService.getProductPropertyList(null, param);
        System.out.println("testGetValuePicList propertyList: " + propertyList.size() + "\n" + propertyList);
    }

}
