package productcenter.product;

import productcenter.models.DesignerPicture;
import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import productcenter.services.DesignerPictureService;
import utils.Global;

import java.util.List;
import java.util.Optional;

/**
 * 设计师图片 Service测试
 * User: lidujun
 * Date: 2015-04-08
 */
public class DesignerPictureServiceTest extends WithApplication {

    private DesignerPictureService designerPictureService;

    @Before
    public void setUp() {
        super.startPlay();
        designerPictureService = Global.ctx.getBean(DesignerPictureService.class);
    }

    @Test
    public void testSave() {
        DesignerPicture designerPicture = new DesignerPicture();
        designerPicture.setDesignerId(1);
        designerPicture.setType("MAIN");
        designerPicture.setPicUrl("www.baidu.com/1.jpg");
        designerPicture.setType("test");

        System.out.println("test designerPictureOpt: " + designerPicture);

        designerPictureService.save(designerPicture);

        System.out.println("after save designerPictureOpt: " + designerPicture);
    }

    @Test
    public void testRealDelete() {
        Optional<DesignerPicture> designerPictureOpt = designerPictureService.getDesignerPictureById(1);
        if(designerPictureOpt.isPresent()) {
            DesignerPicture DesignerPicture = designerPictureOpt.get();
            System.out.println("testRealDelete designerPictureOpt: " + DesignerPicture);
            designerPictureService.realDelete(1);
        }
    }

    @Test
    public void testUpdate() {
        Optional<DesignerPicture> designerPictureOpt = designerPictureService.getDesignerPictureById(2);
        if(designerPictureOpt.isPresent()) {
            DesignerPicture designerPicture = designerPictureOpt.get();
            designerPicture.setDesignerId(1);
            designerPicture.setType("MAIN");
            designerPicture.setPicUrl("www.baidu.com/1.jpg_更新");

            System.out.println("test testUpdate designerPicture: " + designerPicture);

            designerPictureService.update(designerPicture);

            System.out.println("after update designerPicture: " + designerPicture);
        }
    }

    @Test
    public void getDesignerPictureById() {
        Optional<DesignerPicture> designerPictureOpt = designerPictureService.getDesignerPictureById(2);
        if(designerPictureOpt.isPresent()) {
            System.out.println("getDesignerPictureById designerPicture: " + designerPictureOpt.get());
        }
    }

    @Test
    public void testGetDesignerPictureList() {
        DesignerPicture param = new DesignerPicture();
        param.setDesignerId(1);
        List<DesignerPicture> designerPicturetList = designerPictureService.getDesignerPictureList(Optional.ofNullable(null), param);
        System.out.println("testGetDesignerPictureList designerPicturetList: " + designerPicturetList.size() + "\n" + designerPicturetList);
    }

}
