package productcenter.product;

import models.Designer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import play.test.WithApplication;
import services.DesignerService;
import utils.Global;

import java.util.List;


/**
 * 设计师Service测试
 * User: lidujun
 * Date: 2015-04-07
 */
public class DesignerTest extends WithApplication {

    private DesignerService designerService;

    @Before
    public void setUp() {
        super.startPlay();
        designerService = Global.ctx.getBean(DesignerService.class);
    }

    @Test
    @Rollback(false)
    public void testSave() {
        Designer designer = new Designer();
        designer.setName("设计师测试_ldj_01");
        designer.setNationId(1001);
        designer.setIntroduction("设计师简介");
        designer.setIsDelete(false);

        System.out.println("test designer: " + designer.toString());

        designerService.save(designer);

        System.out.println("after save designer: " + designer.toString());
    }

    @Test
    @Rollback(false)
    public void testFalseDelete() {
        Designer designer = designerService.getDesignerById(1);
        System.out.println("testFalseDelete designer: " + designer.toString());
        designerService.falseDelete(1);
        designer = designerService.getDesignerById(1);
        System.out.println("testFalseDelete designer: " + designer.toString());
    }

    @Test
    @Rollback(false)
    public void testUpdate() {
        Designer designer = designerService.getDesignerById(1);
        designer.setName("设计师测试_ldj_01");
        designer.setNationId(1001);
        designer.setIntroduction("设计师简介_update");
        designer.setIsDelete(false);

        System.out.println("testUpdate designer: " + designer.toString());

        designerService.update(designer);

        System.out.println("after testUpdate designer: " + designer.toString());
    }

    @Test
    @Rollback(false)
    public void testGetDesignerById() {
        Designer designer = designerService.getDesignerById(1);
        System.out.println("testGetDesignerById designer: " + designer.toString());
    }

    @Test
    @Rollback(false)
    public void testGetDesignerList() {
        Designer param = new Designer();
        param.setName("测试");
        List<Designer> designerList = designerService.getDesignerList(null, param);
        System.out.println("testGetDesignerList designerList: " + designerList.size() + "\n" + designerList);
    }

}
