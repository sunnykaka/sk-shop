package productcenter.category;

import base.BaseTest;
import base.PrepareOrderData;
import models.Category;
import org.junit.Test;
import services.CategoryService;
import utils.Global;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

/**
 * Created by zhb on 15-4-7.
 */
public class CategoryServiceTest implements BaseTest {

    @Test
    public void testSaveCategory() {
        running(fakeApplication(), () -> {

            CategoryService categoryService = Global.ctx.getBean(CategoryService.class);

            Category category = new Category();
            category.setName("测试项目1");
            category.setParentId(Category.PARENT_DEFAULT);
            category.setOperatorId(1);

            categoryService.save(category);

        });
    }

}
