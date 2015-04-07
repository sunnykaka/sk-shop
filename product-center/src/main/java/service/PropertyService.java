package service;

import com.boobee.boss.domain.CategoryProperty;
import com.boobee.boss.domain.Property;
import com.boobee.common.genericdao.dao.hibernate.GeneralDAO;
import com.boobee.common.genericdao.search.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * Created by zhb on 15-4-3.
 */
@Service
@Transactional
public class PropertyService {

    private static final Logger log = LoggerFactory.getLogger(PropertyService.class);

    @Autowired
    private GeneralDAO generalDAO;

    /**
     * 添加修改类目属性
     *
     * @param property
     */
    public void save(Property property,CategoryProperty categoryProgerty) {
        generalDAO.saveOrUpdate(property);
        categoryProgerty.setPropertyId(property.getId());
        generalDAO.saveOrUpdate(categoryProgerty);
    }

    /**
     * 更新属性
     *
     * @param property
     */
    public void updatePriority(Property property){
        generalDAO.saveOrUpdate(property);
    }

    /**
     * 根据ID查
     *
     * @param propertyId
     * @return
     */
    public Property getPriority(int propertyId){
       return generalDAO.get(Property.class,propertyId);
    }

    /**
     * 删除某个属性
     *
     * @param property
     * @return
     */
    public boolean delete(Property property){
        return generalDAO.removeById(Property.class,property.getId());
    }

    /**
     * 根据类目ID，查找类目属性
     *
     * @param categoryId
     * @return
     */
    public List<Property> findPropertys(int categoryId){

        //查找所有关联
        Search search = new Search(CategoryProperty.class);
        search.addFilterEqual("category_id",categoryId);
        List<CategoryProperty> categoryProperties = generalDAO.search(search);

        List<Property> propertyList = new ArrayList<>();
        //查出属性
        for(CategoryProperty categoryProperty:categoryProperties){
            propertyList.add(getPriority(categoryProperty.getPropertyId()));
        }

        return propertyList;
    }


}
