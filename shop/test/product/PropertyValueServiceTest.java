package product;

import base.BaseTest;
import org.junit.Before;
import org.junit.Test;
import productcenter.models.Property;
import productcenter.models.PropertyValueDetail;
import productcenter.models.Value;
import productcenter.services.PropertyAndValueService;
import utils.Global;

import java.util.List;

/**
 * Sku和库存Service测试
 * User: lidujun
 * Date: 2015-04-02
 */
public class PropertyValueServiceTest extends BaseTest {

    private PropertyAndValueService propertyAndValueService;

    @Before
    public void setUp() {
        propertyAndValueService = Global.ctx.getBean(PropertyAndValueService.class);
    }

    //////////////////////////////////属性////////////////////////////////////////////////
    @Test
    public void queryAllProperties() {
        List<Property> list = propertyAndValueService.queryAllProperties();
        System.out.println("-----------------------------: " + list.size() + "\n" + list);
    }

    @Test
    public void getPropertyById() {
        Property property = propertyAndValueService.getPropertyById(71);
        System.out.println("-----------------------------: " + property);
    }

    //////////////////////////////////值////////////////////////////////////////////////
    @Test
    public void queryAllValues() {
        List<Value> list = propertyAndValueService.queryAllValues();
        System.out.println("-----------------------------: " + list.size() + "\n" + list);
    }

    @Test
    public void getValueById() {
        Value value = propertyAndValueService.getValueById(11585);
        System.out.println("-----------------------------: " + value);
    }

    //////////////////////////////////属性及其值////////////////////////////////////////////////
    @Test
    public void queryAllPropertyValueDetails() {
        List<PropertyValueDetail> list = propertyAndValueService.queryAllPropertyValueDetails();
        System.out.println("-----------------------------: " + list.size() + "\n" + list);
    }

    @Test
    public void getPropertyValueDetailById() {
        PropertyValueDetail pv = propertyAndValueService.getPropertyValueDetailById(3);
        System.out.println("-----------------------------: " + pv);
    }

    @Test
    public void getPropertyValueDetail() {
        PropertyValueDetail pv = propertyAndValueService.getPropertyValueDetail(73,11589);
        System.out.println("-----------------------------: " + pv);
    }
}
