package controllers.category;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.constants.PropertyType;
import productcenter.models.CategoryProperty;
import productcenter.models.CategoryPropertyValue;
import productcenter.models.Property;
import productcenter.models.Value;
import productcenter.services.CategoryPropertyService;
import productcenter.services.CategoryPropertyValueService;
import productcenter.services.PropertyService;
import productcenter.services.ValueService;
import views.html.category.PVindex;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

        return ok(PVindex.render(""));
    }

    public void queryInfo(int categoryId){
        //TODO 04-08
        //categoryPropertyValueService.
    }


    /**
     * 新增属性、值
     *
     * @param cp
     * @param name
     * @param value
     */
    public Result createProperty(CategoryProperty cp,String name,String value){

        if (cp.getType() == PropertyType.SELL_PROPERTY.value) {
            cp.setMultiValue(true);
        }
        int pid = propertyService.createProperty(new Property(name));
        cp.setPropertyId(pid);

        categoryPropertyService.createCategoryProperty(cp);

        if (StringUtils.isNotEmpty(value)) {
            String[] values = value.split(",|，"); //用逗号隔开的多值
            Set<String> valueSet = new HashSet<>(); //去重
            for (String v : values) {
                valueSet.add(v);
            }
            List<Integer> newValue = new LinkedList<>();
            for (String valueName : valueSet) {
                int vid = valueService.createValue(new Value(valueName,cp.getPropertyId()));
                newValue.add(vid);
            }
            for (Integer vid : newValue) {
                categoryPropertyValueService.save(new CategoryPropertyValue(cp.getCategoryId(),pid,vid));
            }
        }

        return ok("true");

    }

    /**
     * 修改属性
     *
     * @param cp
     * @param name
     * @return
     */
    public Result updateProperty(CategoryProperty cp,String name,String jsonValueList){

        if (cp.getType() == PropertyType.SELL_PROPERTY.value) {
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
