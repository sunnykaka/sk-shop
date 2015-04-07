package productcenter.product;

import models.Tag;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import play.test.WithApplication;
import services.TagService;
import utils.Global;

import java.util.List;

/**
 * 标签
 * User: lidujun
 * Date: 2015-04-02
 */
public class TagServiceTest extends WithApplication {

    private TagService tagService;

    @Before
    public void setUp() {
        super.startPlay();
        tagService = Global.ctx.getBean(TagService.class);
    }

    @Test
    @Rollback(false)
    public void testSave() {
        Tag tag = new Tag();
        tag.setName("测试标签01");

        System.out.println("testSave tag: " + tag.toString());

        tagService.save(tag);

        System.out.println("after save tag: " + tag.toString());
    }

    @Test
    @Rollback(false)
    public void testRealDelete() {
        Tag tag = tagService.getTagById(1);
        System.out.println("testRealDelete tag: " + tag.toString());
        tagService.realDelete(1);
    }

    @Test
    @Rollback(false)
    public void testUpdate() {
        Tag tag = tagService.getTagById(1);
        tag.setName("测试标签01_update");

        System.out.println("testUpdate tag: " + tag.toString());

        tagService.update(tag);

        System.out.println("after testUpdate tag: " + tag.toString());
    }

    @Test
    @Rollback(false)
    public void testGetTagById() {
        Tag tag = tagService.getTagById(1);
        System.out.println("testGetTagById tag: " + tag.toString());
    }

    @Test
    @Rollback(false)
    public void testGetTagList() {
        Tag param = new Tag();
        param.setName("测试");
        List<Tag> tagList = tagService.getTagList(null, param);
        System.out.println("testGetTagList tagList: " + tagList.size() + "\n" + tagList);
    }

}
