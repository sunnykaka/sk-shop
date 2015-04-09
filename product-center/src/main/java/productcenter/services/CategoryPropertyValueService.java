package productcenter.services;

import common.services.GeneralDao;
import productcenter.models.CategoryPropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.print.attribute.SetOfIntegerSyntax;
import java.util.*;

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

    /**
     * 查出类目属性值
     *
     * @param categoryId 类目Id
     * @param propertyId 属性Id
     * @return
     */
    public List<CategoryPropertyValue> findByCidAndPid(int categoryId,int propertyId){

        String jpql = "select o from category_value o where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and o.category_id = :categoryId ";
        queryParams.put("categoryId", categoryId);

        jpql += " and o.property_id = :propertyId ";
        queryParams.put("propertyId", propertyId);

        return generalDAO.query(jpql, null, queryParams);

    }

    /**
     * 查出类目所有属性
     *
     * @param categoryId
     * @return
     */
    public List<CategoryPropertyValue> findByCid(int categoryId){

        String jpql = "select o from category_value o where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and o.category_id = :categoryId ";
        queryParams.put("categoryId", categoryId);

        List<CategoryPropertyValue> cPVList = generalDAO.query(jpql, null, queryParams);

        Set<Integer> propertyIds = new HashSet<>();
        if(null != cPVList && cPVList.size() > 0){
            for(CategoryPropertyValue categoryPropertyValue:cPVList){
                propertyIds.add(categoryPropertyValue.getPropertyId());
            }
        }

        return generalDAO.query(jpql, null, queryParams);

    }
}
