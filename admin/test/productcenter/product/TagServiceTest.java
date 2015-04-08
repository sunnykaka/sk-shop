package productcenter.product;

import models.Tag;
import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import services.TagService;
import utils.Global;

import java.util.List;
import java.util.Optional;

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
    public void testSave() {
        Tag tag = new Tag();
        tag.setName("测试标签01");

        System.out.println("testSave tag: " + tag);

        tagService.save(tag);

        System.out.println("after save tag: " + tag);
    }

    @Test
    public void testRealDelete() {
        Optional<Tag> tagOpt = tagService.getTagById(1);
        if(tagOpt.isPresent()) {
            Tag tag = tagOpt.get();
            System.out.println("testRealDelete tag: " + tag);
            tagService.realDelete(1);
        }
    }

    @Test
    public void testUpdate() {
        Optional<Tag> tagOpt = tagService.getTagById(2);
        if(tagOpt.isPresent()) {
            Tag tag = tagOpt.get();
            tag.setName("测试标签01_update");

            System.out.println("testUpdate tag: " + tag);

            tagService.update(tag);

            System.out.println("after testUpdate tag: " + tag);
        }
    }

    @Test
    public void testGetTagById() {
        Optional<Tag> tagOpt = tagService.getTagById(2);
        if(tagOpt.isPresent()) {
            Tag tag = tagOpt.get();
            System.out.println("testGetTagById tag: " + tag);
        }
    }

    @Test
    public void testGetTagList() {
        Tag param = new Tag();
        param.setName("测试");
        List<Tag> tagList = tagService.getTagList(Optional.ofNullable(null), param);
        System.out.println("testGetTagList tagList: " + tagList.size() + "\n" + tagList);
    }

}
