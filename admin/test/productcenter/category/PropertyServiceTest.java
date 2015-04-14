package productcenter.category;

import base.BaseTest;
import org.junit.Test;
import productcenter.models.CategoryProperty;
import productcenter.models.PropertyValue;
import productcenter.models.Value;
import productcenter.services.PropertyService;
import productcenter.services.PropertyValueService;
import productcenter.services.ValueService;
import utils.Global;

import java.util.List;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

/**
 * Created by zhb on 15-4-7.
 */
public class PropertyServiceTest implements BaseTest {

    @Test
    public void testSaveCategory() {
        running(fakeApplication(), () -> {

            PropertyService propertyService = Global.ctx.getBean(PropertyService.class);

            List<CategoryProperty> propertys = propertyService.findByPropertyWithGeneralDaoQuery(java.util.Optional.ofNullable(null), "dsfsdfsdf", null);

            System.out.println(propertys.size());


        });
    }

    @Test
    public void findByPropertyId() {
        running(fakeApplication(), () -> {

            PropertyValueService propertyValueService = Global.ctx.getBean(PropertyValueService.class);

            List<PropertyValue> values = propertyValueService.findByPropertyId(5);

            System.out.println(values.size());


        });
    }

    @Test
    public void findbyPropertyId() {
        running(fakeApplication(), () -> {

            ValueService valueService = Global.ctx.getBean(ValueService.class);

            List<Value> values = valueService.findbyPropertyId(5);

            System.out.println(values.size());


        });
    }

}
