package productcenter.services;

import common.services.GeneralDao;
import productcenter.models.CategoryPropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhb on 15-4-7.
 */
@Service
@Transactional
public class CategoryPropertyValueService {

    @Autowired
    GeneralDao generalDAO;

    /**
     * 添加类目、属性、值关联
     *
     * @param categoryPropertyValue
     */
    public void save(CategoryPropertyValue categoryPropertyValue){
        generalDAO.persist(categoryPropertyValue);
    }

    /**
     * 修改类目、属性、值关联
     *
     * @param categoryPropertyValue
     * @return
     */
    public CategoryPropertyValue update(CategoryPropertyValue categoryPropertyValue){
        return generalDAO.merge(categoryPropertyValue);
    }

    /**
     * 根据Id删除
     *
     * @param id
     */
    public void delete(int id){
        generalDAO.removeById(CategoryPropertyValue.class,id);
    }

    public List<CategoryPropertyValue> findByCidAndPid(int categoryId,int propertyId){

        String jpql = "select o from category_value o where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and o.category_id = :categoryId ";
        queryParams.put("categoryId", categoryId);

        jpql += " and o.property_id = :propertyId ";
        queryParams.put("propertyId", propertyId);

        return generalDAO.query(jpql, null, queryParams);

    }
}
