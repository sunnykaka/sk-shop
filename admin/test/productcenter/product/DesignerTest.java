package productcenter.product;

import productcenter.models.Designer;
import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import productcenter.services.DesignerService;
import utils.Global;

import java.util.List;
import java.util.Optional;


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
    public void testSave() {
        Designer designer = new Designer();
        designer.setName("设计师测试_ldj_01");
        designer.setNationId(1001);
        designer.setIntroduction("设计师简介");
        designer.setIsDelete(false);

        System.out.println("test designer: " + designer);

        designerService.save(designer);

        System.out.println("after save designer: " + designer);
    }

    @Test
    public void testFalseDelete() {
        Optional<Designer> designerOpt = designerService.getDesignerById(1);
        if(designerOpt.isPresent()) {
            Designer designer = designerOpt.get();
            System.out.println("testFalseDelete designer: " + designer);
            designerService.falseDelete(1);
        }
    }

    @Test
    public void testUpdate() {
        Optional<Designer> designerOpt = designerService.getDesignerById(2);
        if(designerOpt.isPresent()) {
            Designer designer = designerOpt.get();
            designer.setName("设计师测试_ldj_01");
            designer.setNationId(1001);
            designer.setIntroduction("设计师简介_update");
            designer.setIsDelete(false);

            System.out.println("testUpdate designer: " + designer);

            designerService.update(designer);

            System.out.println("after testUpdate designer: " + designer);
        }
    }

    @Test
    public void testGetDesignerById() {
        Optional<Designer> designerOpt = designerService.getDesignerById(2);
        if(designerOpt.isPresent()) {
            System.out.println("testGetDesignerById designer: " + designerOpt.get());
        }
    }

    @Test
    public void testGetDesignerList() {
        Designer param = new Designer();
        param.setName("测试");
        param.setNationId(1001);
        param.setIsDelete(false);
        List<Designer> designerList = designerService.getDesignerList(Optional.ofNullable(null), param);
        System.out.println("testGetDesignerList designerList: " + designerList.size() + "\n" + designerList);
    }

}
