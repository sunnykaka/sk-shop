package productcenter.services;

import com.google.common.base.Preconditions;
import common.services.GeneralDao;
import common.utils.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    public List<CategoryProperty> findCategoryProperty(Integer categoryId, List<Integer> propertyIds) {
        Preconditions.checkNotNull(categoryId);
        Preconditions.checkArgument(propertyIds != null && !propertyIds.isEmpty());

        StringBuilder jpql = new StringBuilder("select cp from CategoryProperty cp where cp.categoryId = :categoryId and cp.propertyId in (");
        Map<String, Object> queryParams = new HashMap<>();

        for(int i=0; i<propertyIds.size(); i++) {
            String paramName = "propertyId" + i;
            jpql.append(String.format(":%s,", paramName));
            queryParams.put(paramName, propertyIds.get(i));
        }
        jpql.replace(jpql.length() - 1, jpql.length(), ")");

        return generalDao.query(jpql.toString(), Optional.empty(), queryParams);

    }

    /**
     * @param categoryId
     * @param propertyIds
     * @param valueIds
     * @return
     */
    public List<CategoryPropertyValue> findCategoryPropertyValue(Integer categoryId, List<Integer> propertyIds, List<Integer> valueIds) {
        Preconditions.checkNotNull(categoryId);
        Preconditions.checkArgument(propertyIds != null && !propertyIds.isEmpty());
        Preconditions.checkArgument(valueIds != null && !valueIds.isEmpty());

        StringBuilder jpql = new StringBuilder("select cpv from CategoryPropertyValue cpv where cpv.categoryId = :categoryId and cpv.propertyId in (");
        Map<String, Object> queryParams = new HashMap<>();

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





}
