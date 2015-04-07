package productcenter.product;

import models.ValuePic;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import play.test.WithApplication;
import services.ValuePicService;
import utils.Global;

import java.util.List;

/**
 * 标签
 * User: lidujun
 * Date: 2015-04-03
 */
public class ValuePicServiceTest extends WithApplication {

    private ValuePicService valuePicService = Global.ctx.getBean(ValuePicService.class);

    @Test
    @Rollback(false)
    public void testSave() {
        ValuePic valuePic = new ValuePic();
        valuePic.setProductId(1);
        valuePic.setPicUrl("www.baidu.com/1.jpg");

        System.out.println("testSave valuePic: " + valuePic.toString());

        valuePicService.save(valuePic);

        System.out.println("after save valuePic: " + valuePic.toString());
    }

    @Test
    @Rollback(false)
    public void testRealDelete() {
        ValuePic valuePic = valuePicService.getValuePicById(1);
        System.out.println("testRealDelete valuePic: " + valuePic.toString());
        valuePicService.realDelete(1);
    }

    @Test
    @Rollback(false)
    public void testUpdate() {
        ValuePic valuePic = valuePicService.getValuePicById(1);
        valuePic.setPicUrl("www.baidu.com/4.jpg");

        System.out.println("testUpdate valuePic: " + valuePic.toString());

        valuePicService.update(valuePic);

        System.out.println("after testUpdate ValuePic: " + valuePic.toString());
    }

    @Test
    @Rollback(false)
    public void testGetValuePicById() {
        ValuePic valuePic = valuePicService.getValuePicById(1);
        System.out.println("testGetValuePicById ValuePic: " + valuePic.toString());
    }

    @Test
    @Rollback(false)
    public void testGetValuePicList() {
        ValuePic param = new ValuePic();
        param.setProductId(1);
        List<ValuePic> valuePicList = valuePicService.getValuePicList(null, param);
        System.out.println("testGetValuePicList valuePicList: " + valuePicList.size() + "\n" + valuePicList);
    }

}
