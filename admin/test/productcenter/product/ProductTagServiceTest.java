package productcenter.product;

import models.ProductTag;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import play.test.WithApplication;
import services.ProductTagService;
import utils.Global;

import java.util.List;

/**
 * 产品（商品）标签
 * User: lidujun
 * Date: 2015-04-02
 */
public class ProductTagServiceTest extends WithApplication {

    private ProductTagService productTagService;

    @Before
    public void setUp() {
        super.startPlay();
        productTagService = Global.ctx.getBean(ProductTagService.class);
    }

    @Test
    @Rollback(false)
    public void testSave() {
        ProductTag tag = new ProductTag();
        tag.setTagId(1);
        tag.setProductId(1);

        System.out.println("testSave tag: " + tag.toString());

        productTagService.save(tag);

        System.out.println("after save tag: " + tag.toString());
    }

    @Test
    @Rollback(false)
    public void testRealDelete() {
        ProductTag tag = productTagService.getTagById(1);
        System.out.println("testRealDelete tag: " + tag.toString());
        productTagService.realDelete(1);
    }

    @Test
    @Rollback(false)
    public void testUpdate() {
        ProductTag tag = productTagService.getTagById(1);
        tag.setTagId(1);
        tag.setProductId(2);

        System.out.println("testUpdate tag: " + tag.toString());

        productTagService.update(tag);

        System.out.println("after testUpdate tag: " + tag.toString());
    }

    @Test
    @Rollback(false)
    public void testGetTagById() {
        ProductTag tag = productTagService.getTagById(1);
        System.out.println("testGetTagById tag: " + tag.toString());
    }

    @Test
    @Rollback(false)
    public void testGetTagList() {
        ProductTag param = new ProductTag();
        param.setProductId(1);
        List<ProductTag> tagList = productTagService.getTagList(null, param);
        System.out.println("testGetTagList tagList: " + tagList.size() + "\n" + tagList);
    }

}
