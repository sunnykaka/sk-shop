package productcenter.product;

import productcenter.models.ValuePic;
import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import productcenter.services.ValuePicService;
import utils.Global;

import java.util.List;
import java.util.Optional;

/**
 * 标签
 * User: lidujun
 * Date: 2015-04-03
 */
public class ValuePicServiceTest extends WithApplication {

    private ValuePicService valuePicService;

    @Before
    public void setUp() {
        super.startPlay();
        valuePicService = Global.ctx.getBean(ValuePicService.class);
    }

    @Test
    public void testSave() {
        ValuePic valuePic = new ValuePic();
        valuePic.setProductId(1);
        valuePic.setValueId(1111);
        valuePic.setSkuId(2222);
        valuePic.setPicUrl("www.baidu.com/1.jpg");

        System.out.println("testSave valuePic: " + valuePic);

        valuePicService.save(valuePic);

        System.out.println("after save valuePic: " + valuePic);
    }

    @Test
    public void testRealDelete() {
        Optional<ValuePic> valuePicOpt = valuePicService.getValuePicById(1);
        if(valuePicOpt.isPresent()) {
            ValuePic valuePic = valuePicOpt.get();
            System.out.println("testRealDelete valuePic: " + valuePic);
            valuePicService.realDelete(1);
        }
    }

    @Test
    public void testUpdate() {
        Optional<ValuePic> valuePicOpt = valuePicService.getValuePicById(2);
        if(valuePicOpt.isPresent()) {
            ValuePic valuePic = valuePicOpt.get();
            valuePic.setPicUrl("www.baidu.com/4.jpg");

            System.out.println("testUpdate valuePic: " + valuePic);

            valuePicService.update(valuePic);

            System.out.println("after testUpdate ValuePic: " + valuePic);
        }
    }

    @Test
    public void testGetValuePicById() {
        Optional<ValuePic> valuePicOpt = valuePicService.getValuePicById(2);
        if(valuePicOpt.isPresent()) {
            ValuePic valuePic = valuePicOpt.get();
            System.out.println("testGetValuePicById ValuePic: " + valuePic);
        }
    }

    @Test
    public void testGetValuePicList() {
        ValuePic param = new ValuePic();
        param.setProductId(1);
        List<ValuePic> valuePicList = valuePicService.getValuePicList(Optional.ofNullable(null), param);
        System.out.println("testGetValuePicList valuePicList: " + valuePicList.size() + "\n" + valuePicList);
    }

}
