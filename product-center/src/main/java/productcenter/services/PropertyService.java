package productcenter.services;

import common.services.GeneralDao;
import common.utils.page.Page;
import org.apache.commons.lang3.StringUtils;
import productcenter.models.CategoryProperty;
import productcenter.models.Property;
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


@Service
@Transactional
public class PropertyService {

    @PersistenceContext
    EntityManager em;

    @Autowired
    GeneralDao generalDAO;

    @Autowired
    private PropertyValueService propertyValueService;

    @Autowired
    private ValueService valueService;

    /**
     * 根据表单内容新建属性、值
     *
     * @param name
     * @param value
     */
    public void savePV(String name,String value){
        int pid = createProperty(new Property(name));

        if (StringUtils.isNotEmpty(value)) {
            String[] values = value.split(",|，"); //用逗号隔开的多值
            Set<String> valueSet = new HashSet<>(); //去重
            for (String v : values) {
                valueSet.add(v);
            }
            for (String valueName : valueSet) {
                int vid = valueService.createValue(new Value(valueName));
                propertyValueService.createValue(new PropertyValue(pid,vid));
            }
        }
    }

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

        List<PropertyValue> pvList = propertyValueService.findByPropertyId(property.getId());
        if(null != pvList){
            for(PropertyValue pv:pvList){
                generalDAO.removeById(PropertyValue.class,pv.getId());
            }
        }

        return generalDAO.removeById(Property.class, property.getId());
    }

    /**
     * 分页查询所有属性
     *
     * @param page
     * @param name
     * @return
     */
    public List<Property> findAllProperty(Optional<Page<Property>> page, String name){
        String jpql = "select p from Property p  where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();

        if(StringUtils.isNotEmpty(name)) {
            jpql += " and p.name = :name ";
            queryParams.put("name", name);
        }

        jpql += " order by p.priority desc";

        return generalDAO.query(jpql, page, queryParams);
    }

    @Deprecated
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

        String jpql = "select p from Property p where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and p.name = :name ";
        queryParams.put("name", name);

        List<Property> propertyList = generalDAO.query(jpql, Optional.ofNullable(null), queryParams);
        if (propertyList != null && propertyList.size() > 0) {
            return propertyList.get(0);
        }

        return null;

    }


}
