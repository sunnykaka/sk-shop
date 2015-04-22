package productcenter.services;

import common.services.GeneralDao;
import productcenter.models.PropertyValue;
import productcenter.models.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

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

    @Autowired
    private PropertyValueService propertyValueService;

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

        List<PropertyValue> pvList = propertyValueService.findByPropertyId(propertyId);

        Set<Integer> vids = new HashSet<>();
        for(PropertyValue pv:pvList){
            vids.add(pv.getValueId());
        }

        if(vids.size() > 0){

            String jpql = "select v from Value v where 1=1 ";
            Map<String, Object> queryParams = new HashMap<>();
            jpql += " and v.id in :id ";
            queryParams.put("id", vids);
            return generalDAO.query(jpql, Optional.ofNullable(null), queryParams);

        }else{
            return new ArrayList<>();
        }

    }

    /**
     * 根据名字查找属性值
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public Value findByName(String name) {

        String jpql = "select v from Value v where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and v.name = :name ";
        queryParams.put("name", name);

        List<Value> valueList = generalDAO.query(jpql, Optional.ofNullable(null), queryParams);
        if (valueList != null && valueList.size() > 0) {
            return valueList.get(0);
        }

        return null;

    }


}
