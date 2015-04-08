package services;

import common.services.GeneralDao;
import models.Value;
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

/**
 * value 操作
 * Created by zhb on 15-4-7.
 */
@Service
@Transactional
public class ValueService {

    private static final Logger log = LoggerFactory.getLogger(ValueService.class);

    @PersistenceContext
    EntityManager em;

    @Autowired
    GeneralDao generalDAO;

    /**
     * 保存value
     *
     * @param value
     */
    public void save(Value value,int propertyId){
        value.setPropertyId(propertyId);
        generalDAO.persist(value);
    }

    /**
     * 更新value
     * @param value
     * @return
     */
    public Value update(Value value){

       return generalDAO.merge(value);
    }

    /**
     * 删除value
     * @param valueId
     */
    public void delete(int valueId){
        generalDAO.removeById(Value.class,valueId);
    }

    /**
     * 根据属性ID查对应值
     *
     * @param propertyId
     * @return
     */
    public List<Value> findbyPropertyId(int propertyId){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Value> cq = cb.createQuery(Value.class);
        Root<Value> rootValue = cq.from(Value.class);

        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(cb.equal(rootValue.get("property_id"), propertyId));

        cq.select(rootValue).where(predicateList.toArray(new Predicate[predicateList.size()]));

        TypedQuery<Value> query = em.createQuery(cq);

        return query.getResultList();
    }


}
