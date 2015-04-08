package productcenter.services;

import common.services.GeneralDao;
import productcenter.models.Value;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * value 操作
 * Created by zhb on 15-4-7.
 */
@Service
@Transactional
public class ValueService {

    @PersistenceContext
    EntityManager em;

    @Autowired
    GeneralDao generalDAO;

    /**
     * 保存value
     *
     * @param value
     */
    public int createValue(Value value){
       Value oldValue = findByName(value.getName());
        if (null != oldValue){
            return oldValue.getId();
        }else{
            generalDAO.persist(value);
            return value.getId();
        }
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
    @Transactional(readOnly = true)
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

    /**
     * 根据名字查找属性值
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public Value findByName(String name) {

        String jpql = "select o from value o where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and o.name = :name ";
        queryParams.put("name", name);

        List<Value> propertyList = generalDAO.query(jpql, null, queryParams);
        if (propertyList != null && propertyList.size() > 0) {
            return propertyList.get(0);
        }

        return null;

    }


}
