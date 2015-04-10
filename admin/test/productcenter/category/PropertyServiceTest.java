package productcenter.category;

import base.BaseTest;
import org.junit.Test;
import productcenter.models.CategoryProperty;
import productcenter.services.PropertyService;
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

}
