package controllers.category;

import models.PropertyValueForm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.constants.PropertyType;
import productcenter.models.*;
import productcenter.services.CategoryPropertyService;
import productcenter.services.CategoryPropertyValueService;
import productcenter.services.PropertyService;
import productcenter.services.ValueService;
import views.html.category.PVindex;

import java.util.*;

/**
 * 属性控制层
 *
 * Created by zhb on 15-4-8.
 */
@org.springframework.stereotype.Controller
public class PropertyValueController extends Controller {

    @Autowired
    private CategoryPropertyService categoryPropertyService;

    @Autowired
    private CategoryPropertyValueService categoryPropertyValueService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private ValueService valueService;

    /**
     * 属性首页
     *
     * @return
     */
    public Result propertyIndex(){

        play.Logger.info("属性首页");
        //TODO
        List<Property> propertyList = propertyService.findAllProperty(Optional.ofNullable(null), null);
        for(Property p:propertyList){
            List<Value> values = valueService.findbyPropertyId(p.getId());
            p.setValues(values);
        }

        return ok(PVindex.render(propertyList));
    }

    /**
     * 新增属性、值
     *
     */
    public Result createProperty(){

        final Form<PropertyValueForm> PVForm = Form.form(PropertyValueForm.class).bindFromRequest();
        PropertyValueForm pv = PVForm.get();

        //TODO 较验参数未处理

        propertyService.savePV(pv.getName(), pv.getValue());

        return redirect(routes.PropertyValueController.propertyIndex());

    }

    /**
     * 删除属性、值
     *
     * @return
     */
    public Result deleteProperty(){

        final Form<Property> PVForm = Form.form(Property.class).bindFromRequest();
        final Property property = PVForm.get();

        propertyService.delete(property);

        return redirect(routes.PropertyValueController.propertyIndex());
    }

    /**
     * 修改属性
     *
     * @param cp
     * @param name
     * @return
     */
    public Result updateProperty(CategoryProperty cp,String name,String jsonValueList){

        if (cp.getType() == PropertyType.SELL_PROPERTY.getName()) {
            cp.setMultiValue(true);
        }

        CategoryProperty categoryProperty = categoryPropertyService.getCategoryPropertyById(cp.getId());

        //新属性ID
        int pid = propertyService.createProperty(new Property(name));
        //原来属性值重新关联到新属性上
        List<CategoryPropertyValue> categoryPropertyValues = categoryPropertyValueService.findByCidAndPid(categoryProperty.getCategoryId(), categoryProperty.getPropertyId());
        for (CategoryPropertyValue categoryPropertyValue : categoryPropertyValues) {
            categoryPropertyValue.setPropertyId(pid);
            categoryPropertyValueService.update(categoryPropertyValue);
        }
        categoryProperty.setPropertyId(pid);
        categoryProperty.setMultiValue(cp.isMultiValue());
        categoryProperty.setType(cp.getType());
        categoryPropertyService.updateCategoryProperty(categoryProperty);

        return ok("true");

    }


}
