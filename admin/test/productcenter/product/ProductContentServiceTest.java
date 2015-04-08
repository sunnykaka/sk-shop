package productcenter.product;

import models.ProductContent;
import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import services.ProductContentService;
import utils.Global;

import java.util.Optional;

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
    public void testSave() {
        ProductContent content = new ProductContent();
        content.setProductId(1);
        content.setName("产品（商品）内容（详情）测试");
        content.setContent("测试测试测试测试");

        System.out.println("test ProductContent: " + content);

        productContentService.save(content);

        System.out.println("after save ProductContent: " + content);
    }

    @Test
    public void testRealDelete() {
        Optional<ProductContent> contentOpt = productContentService.getProductContentById(1);
        if(contentOpt.isPresent()) {
            ProductContent content = contentOpt.get();
            System.out.println("testFalseDelete content: " + content);
            productContentService.realDelete(1);
        }
    }

    @Test
    public void testUpdate() {
        Optional<ProductContent> contentOpt = productContentService.getProductContentById(2);
        if(contentOpt.isPresent()) {
            ProductContent content = contentOpt.get();
            content.setId(2);
            content.setProductId(2);
            content.setName("产品（商品）内容（详情）测试-更新字段");
            content.setContent("测试测试测试测试");

            System.out.println("test content: " + content);

            productContentService.update(content);

            System.out.println("after update content: " + content);
        }
    }

    @Test
    public void testGetProductContentById() {
        Optional<ProductContent> contentOpt = productContentService.getProductContentById(2);
        if(contentOpt.isPresent()) {
            ProductContent content = contentOpt.get();
            System.out.println("testGetProductContentById content: " + content);
        }
    }

    @Test
    public void getProductContentByProductId() {
        Optional<ProductContent> contentOpt = productContentService.getProductContentByProductId(2);
        if(contentOpt.isPresent()) {
            ProductContent content = contentOpt.get();
            System.out.println("getProductContentByProductId content: " + content);
        }
    }

}
