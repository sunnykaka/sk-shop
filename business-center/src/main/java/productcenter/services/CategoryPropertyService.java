package productcenter.services;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import common.exceptions.AppBusinessException;
import common.services.GeneralDao;
import common.utils.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productcenter.constants.PropertyType;
import productcenter.models.CategoryProperty;
import productcenter.models.CategoryPropertyValue;
import productcenter.models.Property;

import java.util.*;

/**
 * Created by liubin on 15-5-19.
 */
@Service
public class CategoryPropertyService {

    @Autowired
    private GeneralDao generalDao;

    /**
     * @param categoryId
     * @param propertyIds
     * @return
     */
    @Transactional(readOnly = true)
    public List<CategoryProperty> findCategoryProperty(Integer categoryId, List<Integer> propertyIds) {
        Preconditions.checkNotNull(categoryId);
        if(propertyIds == null || propertyIds.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder jpql = new StringBuilder("select cp from CategoryProperty cp where cp.deleted = false and " +
                " cp.categoryId = :categoryId and cp.propertyId in (");
        Map<String, Object> queryParams = new HashMap<>();

        queryParams.put("categoryId", categoryId);
        for(int i=0; i<propertyIds.size(); i++) {
            String paramName = "propertyId" + i;
            jpql.append(String.format(":%s,", paramName));
            queryParams.put(paramName, propertyIds.get(i));
        }
        jpql.replace(jpql.length() - 1, jpql.length(), ")");

        return generalDao.query(jpql.toString(), Optional.empty(), queryParams);

    }

    @Transactional(readOnly = true)
    public List<CategoryProperty> findCategoryProperty(Integer categoryId, PropertyType propertyType) {
        Preconditions.checkNotNull(categoryId);
        Preconditions.checkNotNull(propertyType);

        StringBuilder jpql = new StringBuilder("select cp from CategoryProperty cp where cp.deleted = false and " +
                " cp.categoryId = :categoryId and cp.propertyType = :propertyType ");
        Map<String, Object> queryParams = new HashMap<>();

        queryParams.put("categoryId", categoryId);
        queryParams.put("propertyType", propertyType);

        return generalDao.query(jpql.toString(), Optional.empty(), queryParams);
    }


    /**
     * @param categoryId
     * @param propertyIds
     * @param valueIds
     * @return
     */
    @Transactional(readOnly = true)
    public List<CategoryPropertyValue> findCategoryPropertyValue(Integer categoryId, List<Integer> propertyIds, List<Integer> valueIds) {
        Preconditions.checkNotNull(categoryId);
        if(propertyIds == null || propertyIds.isEmpty() || valueIds == null || valueIds.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder jpql = new StringBuilder("select cpv from CategoryPropertyValue cpv where cpv.deleted = false and" +
                " cpv.categoryId = :categoryId and cpv.propertyId in (");
        Map<String, Object> queryParams = new HashMap<>();

        queryParams.put("categoryId", categoryId);
        for(int i=0; i<propertyIds.size(); i++) {
            String paramName = "propertyId" + i;
            jpql.append(String.format(":%s,", paramName));
            queryParams.put(paramName, propertyIds.get(i));
        }
        jpql.replace(jpql.length() - 1, jpql.length(), ")");

        jpql.append(" and cpv.valueId in (");
        for(int i=0; i<valueIds.size(); i++) {
            String paramName = "valueId" + i;
            jpql.append(String.format(":%s,", paramName));
            queryParams.put(paramName, valueIds.get(i));
        }
        jpql.replace(jpql.length() - 1, jpql.length(), ")");

        return generalDao.query(jpql.toString(), Optional.empty(), queryParams);

    }


    /**
     * 创建categoryProperty, 创建之前会校验categoryId和propertyId的联合唯一约束在数据库是否存在
     * @param categoryProperty
     */
    @Transactional
    public void createCategoryProperty(CategoryProperty categoryProperty) {
        List<CategoryProperty> categoryPropertyList = findCategoryProperty(
                categoryProperty.getCategoryId(), Lists.newArrayList(categoryProperty.getPropertyId()));
        if(!categoryPropertyList.isEmpty()) {
            throw new AppBusinessException(String.format(
                    "不能创建CategoryProperty, 该CategoryProperty对应的categoryId[%d], propertyId[%d]在数据已存在",
                    categoryProperty.getCategoryId(), categoryProperty.getPropertyId()));
        }

        generalDao.persist(categoryProperty);
    }
}
