package productcenter.services;

import common.services.GeneralDao;
import common.utils.page.Page;
import org.apache.commons.lang3.StringUtils;
import productcenter.models.CategoryProperty;
import productcenter.models.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;


@Service
@Transactional
public class PropertyService {

    @PersistenceContext
    EntityManager em;

    @Autowired
    GeneralDao generalDAO;

    /**
     * 添加类目属性
     *
     * @param property
     */
    public int createProperty(Property property) {
        Property oldProperty = findByName(property.getName());
        if (null != oldProperty){
            return oldProperty.getId();
        }else{
            generalDAO.persist(property);
            return property.getId();
        }
    }

    /**
     * 更新属性
     *
     * @param property
     */
    public Property updatePriority(Property property) {
        return generalDAO.merge(property);
    }

    /**
     * 根据ID查
     *
     * @param propertyId
     * @return
     */
    public Property getPriority(int propertyId) {
        return generalDAO.get(Property.class, propertyId);
    }

    /**
     * 删除某个属性
     *
     * @param property
     * @return
     */
    public boolean delete(Property property) {
        return generalDAO.removeById(Property.class, property.getId());
    }


    @Transactional(readOnly = true)
    public List<CategoryProperty> findByPropertyWithGeneralDaoQuery(Optional<Page<CategoryProperty>> page, String name,String type) {

        String jpql = "select cp from CategoryProperty cp join fetch cp.property p where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();

        if(StringUtils.isNotEmpty(type)) {
            jpql += " and cp.type = :type ";
            queryParams.put("type", type);
        }
        if(StringUtils.isNotEmpty(name)) {
            jpql += " and p.name = :name ";
            queryParams.put("name", name);
        }

        jpql += " order by p.priority ";

        return generalDAO.query(jpql, page, queryParams);

    }


    /**
     * 根据类目ID，查找类目属性
     *
     * @param categoryId
     * @return
     */
    @Transactional(readOnly = true)
    public List<Property> findPropertys(int categoryId) {

        //查找所有关联
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CategoryProperty> cq = cb.createQuery(CategoryProperty.class);
        Root<CategoryProperty> categoryProperty = cq.from(CategoryProperty.class);

        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(cb.equal(categoryProperty.get("category_id"), categoryId));

        cq.select(categoryProperty).where(predicateList.toArray(new Predicate[predicateList.size()]));

        TypedQuery<CategoryProperty> query = em.createQuery(cq);

        List<CategoryProperty> categoryProperties = query.getResultList();

        List<Property> propertyList = new ArrayList<>();
        //查出属性
        for (CategoryProperty categoryp : categoryProperties) {
            propertyList.add(getPriority(categoryp.getPropertyId()));
        }

        return propertyList;
    }

    @Transactional(readOnly = true)
    public Property findByName(String name) {

        String jpql = "select o from property o where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and o.name = :name ";
        queryParams.put("name", name);

        List<Property> propertyList = generalDAO.query(jpql, null, queryParams);
        if (propertyList != null && propertyList.size() > 0) {
            return propertyList.get(0);
        }

        return null;

    }


}
