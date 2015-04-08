package productcenter.product;

import models.ProductTag;
import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import services.ProductTagService;
import utils.Global;

import java.util.List;
import java.util.Optional;

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
    public void testSave() {
        ProductTag tag = new ProductTag();
        tag.setTagId(1);
        tag.setProductId(1);

        System.out.println("testSave tag: " + tag);

        productTagService.save(tag);

        System.out.println("after save tag: " + tag);
    }

    @Test
    public void testRealDelete() {
        Optional<ProductTag> tagOpt =  productTagService.getTagById(1);
        if(tagOpt.isPresent()) {
            ProductTag tag = tagOpt.get();
            System.out.println("testRealDelete tag: " + tag);
            productTagService.realDelete(1);
        }
    }

    @Test
    public void testUpdate() {
        Optional<ProductTag> tagOpt =  productTagService.getTagById(2);
        if(tagOpt.isPresent()) {
            ProductTag tag = tagOpt.get();
            tag.setTagId(1);
            tag.setProductId(2);

            System.out.println("testUpdate tag: " + tag);

            productTagService.update(tag);

            System.out.println("after testUpdate tag: " + tag);
        }
    }

    @Test
    public void testGetTagById() {
        Optional<ProductTag> tagOpt =  productTagService.getTagById(2);
        if(tagOpt.isPresent()) {
            ProductTag tag = tagOpt.get();
            System.out.println("testGetTagById tag: " + tag);
        }
    }

    @Test
    public void testGetTagList() {
        ProductTag param = new ProductTag();
        param.setProductId(1);
        List<ProductTag> tagList = productTagService.getTagList(Optional.ofNullable(null), param);
        System.out.println("testGetTagList tagList: " + tagList.size() + "\n" + tagList);
    }

}
