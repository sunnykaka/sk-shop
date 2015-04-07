package services;

import common.services.GeneralDao;
import models.Category;
import models.CategoryProperty;
import models.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class PropertyService {

    private static final Logger log = LoggerFactory.getLogger(PropertyService.class);

    @PersistenceContext
    EntityManager em;

    @Autowired
    GeneralDao generalDAO;

    /**
     * 添加修改类目属性
     *
     * @param property
     * @param categoryProgerty
     */
    public void save(Property property, CategoryProperty categoryProgerty) {
        generalDAO.persist(property);
        categoryProgerty.setPropertyId(property.getId());
        generalDAO.persist(categoryProgerty);
    }

    /**
     * 更新属性
     *
     * @param property
     */
    public Property updatePriority(Property property){
       return generalDAO.merge(property);
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
    @Transactional(readOnly = true)
    public List<Property> findPropertys(int categoryId){

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
        for(CategoryProperty categoryp:categoryProperties){
            propertyList.add(getPriority(categoryp.getPropertyId()));
        }

        return propertyList;
    }


}
