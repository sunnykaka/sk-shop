package controllers.category;

import models.CategoryTree;
import org.springframework.beans.factory.annotation.Autowired;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.models.Category;
import productcenter.services.CategoryService;
import views.html.category.categoryPVlist;
import views.html.category.categorylist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhb on 15-4-8.
 */
@org.springframework.stereotype.Controller
public class CategoryController extends Controller {

    @Autowired
    private CategoryService categoryService;

    /**
     * 类目首页
     *
     * @return
     */
    public void categoryIndex(){

        play.Logger.info("后台类目首页内容");
        //TODO

    }

    /**
     * 添加一级类目
     *
     * @return
     */
    public Result addFirst(){
        final Form<Category> categoryForm = Form.form(Category.class).bindFromRequest();
        final Category category = categoryForm.get();
        categoryService.saveOrUpdate(category);
        return redirect(routes.CategoryController.findFatherCategorys());
    }

    /**
     * 类目属性值管理
     *
     * @param categoryId
     * @return
     */
    public Result PVList(int categoryId){

        return ok(categoryPVlist.render(""));
    }

    /**
     * 根类目
     *
     * @return
     */
    public Result findFatherCategorys() {
        Map<String,Object> map = new HashMap<>();

        List<Category> categoryList = categoryService.findFatherCategorys();
        for(Category categoryFather:categoryList){

            CategoryTree treeFather = new CategoryTree();
            treeFather.setId(categoryFather.getId());
            treeFather.setName(categoryFather.getName());
            treeFather.setType(CategoryTree.TYPE_ITEM);

            List<Category> categoryChildrens = categoryService.getCategorybyParentId(categoryFather.getId());
            if(null != categoryChildrens && categoryChildrens.size() > 0){
                treeFather.setType(CategoryTree.TYPE_FOLDER);

                Map<String,Map<String,CategoryTree>> mapFather = new HashMap<>();
                Map<String,CategoryTree> mapTreeChildren = new HashMap<>();
                for(Category categoryChildren:categoryChildrens){
                    CategoryTree treeChildren = new CategoryTree();
                    treeChildren.setId(categoryChildren.getId());
                    treeChildren.setName(categoryChildren.getName());
                    treeChildren.setType(CategoryTree.TYPE_ITEM);

                    //查孙一级
                    List<Category> categoryGrandsons = categoryService.getCategorybyParentId(categoryChildren.getId());
                    if(null != categoryGrandsons && categoryGrandsons.size() > 0){
                        treeChildren.setType(CategoryTree.TYPE_FOLDER);
                    }

                    mapTreeChildren.put(categoryChildren.getName(),treeChildren);

                }
                mapFather.put("children",mapTreeChildren);
                treeFather.setAdditionalParameters(mapFather);
            }
            map.put(categoryFather.getName(),treeFather);
        }

        return ok(categorylist.render("{\"for-sale\" : {\"name\": \"for-sale\", \"type\": \"folder\",\"id\":\"1\",\"pid\":\"-1\"},\"i则试22\" : {\"name\": \"i则试22\", \"type\": \"folder\",\"id\":\"2\",\"pid\":\"-1\"}}"));
    }



}
