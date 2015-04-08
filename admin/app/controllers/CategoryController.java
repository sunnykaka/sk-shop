package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.services.CategoryService;

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
     * 根类目
     *
     * @return
     */
    public Result findFatherCategorys() {
        return ok(Json.toJson(categoryService.findFatherCategorys()));
    }



}
