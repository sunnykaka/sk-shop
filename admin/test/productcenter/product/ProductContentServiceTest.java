package productcenter.product;

import models.ProductContent;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import play.test.WithApplication;
import services.ProductContentService;
import utils.Global;

/**
 * 产品（商品）内容（详情）Service测试
 * User: lidujun
 * Date: 2015-04-02
 */
public class ProductContentServiceTest extends WithApplication {

    private ProductContentService productContentService;

    @Before
    public void setUp() {
        super.startPlay();
        productContentService = Global.ctx.getBean(ProductContentService.class);
    }

    @Test
    @Rollback(false)
    public void testSave() {
        ProductContent content = new ProductContent();
        content.setProductId(1);
        content.setName("产品（商品）内容（详情）测试");
        content.setContent("测试测试测试测试");

        System.out.println("test ProductContent: " + content.toString());

        productContentService.save(content);

        System.out.println("after save ProductContent: " + content.toString());
    }

    @Test
    @Rollback(false)
    public void testRealDelete() {
        try {
            ProductContent content = productContentService.getProductContentById(1);
            System.out.println("testFalseDelete content: " + content);
            productContentService.realDelete(1);
        } catch (Exception e) {
            System.out.println("删除对象不存在或发生其它异常");
        }
    }

    @Test
    @Rollback(false)
    public void testUpdate() {
        ProductContent content = productContentService.getProductContentById(1);
        content.setId(2);
        content.setProductId(2);
        content.setName("产品（商品）内容（详情）测试-更新字段");
        content.setContent("测试测试测试测试");

        System.out.println("test content: " + content.toString());

        productContentService.save(content);

        System.out.println("after update content: " + content.toString());
    }

    @Test
    @Rollback(false)
    public void testGetProductContentById() {
        ProductContent content = productContentService.getProductContentById(2);
        System.out.println("testGetProductContentById content: " + content.toString());
    }

    @Test
    @Rollback(false)
    public void getProductContentByProductId() {
        ProductContent content = productContentService.getProductContentByProductId(2);
        System.out.println("getProductContentByProductId content: " + content.toString());
    }

}
