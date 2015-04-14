package productcenter.services;

import common.services.GeneralDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productcenter.models.PropertyValue;
import productcenter.models.Value;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * Property value 关联操作
 * Created by zhb on 15-4-7.
 */
@Service
@Transactional
public class PropertyValueService {

    @PersistenceContext
    EntityManager em;

    @Autowired
    GeneralDao generalDAO;

    /**
     *  新建 PropertyValue
     *
     * @param pv
     */
    public void createValue(PropertyValue pv){
        generalDAO.persist(pv);
    }

    /**
     * 更新 PropertyValue
     * @param pv
     * @return
     */
    public PropertyValue update(PropertyValue pv){

       return generalDAO.merge(pv);
    }

    /**
     * 根据propertyId查询
     *
     * @param propertyId
     */
    public List<PropertyValue> findByPropertyId(int propertyId){

        String jpql = "select pv from PropertyValue pv where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and pv.propertyId = :propertyId ";
        queryParams.put("propertyId", propertyId);

        return generalDAO.query(jpql, Optional.ofNullable(null), queryParams);
    }

    /**
     * 根据valueId查询
     *
     * @param valueId
     */
    public List<PropertyValue> findByValueId(int valueId){

        String jpql = "select pv from PropertyValue pv where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and v.valueId = :valueId ";
        queryParams.put("valueId", valueId);

        return generalDAO.query(jpql, Optional.ofNullable(null), queryParams);
    }


}
