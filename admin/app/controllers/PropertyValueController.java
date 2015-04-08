package controllers;

import constants.PropertyType;
import models.CategoryProperty;
import models.CategoryPropertyValue;
import models.Property;
import models.Value;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.*;

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
    public void categoryIndex(){

        play.Logger.info("属性首页");
        //TODO

    }

    /**
     * 新增属性、值
     *
     * @param cp
     * @param name
     * @param value
     */
    public Result createProperty(CategoryProperty cp,String name,String value){

        if (cp.getType() == PropertyType.SELL_PROPERTY) {
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

        if (cp.getType() == PropertyType.SELL_PROPERTY) {
            cp.setMultiValue(true);
        }

        CategoryProperty categoryProperty = categoryPropertyService.getCategoryPropertyById(cp.getId());




        return ok("true");

    }


}
