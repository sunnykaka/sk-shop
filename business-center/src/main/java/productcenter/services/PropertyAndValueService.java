package productcenter.services;

import common.services.GeneralDao;
import common.utils.StringUtils;
import common.utils.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productcenter.models.Property;
import productcenter.models.PropertyValueDetail;
import productcenter.models.Value;

import java.util.*;
import java.util.stream.IntStream;

/**
 * 属性和值 Service
 * User: lidujun
 * Date: 2015-04-27
 */
@Service
@Transactional(readOnly = true)
public class PropertyAndValueService {

    @Autowired
    GeneralDao generalDao;


    //////////////////////////////////属性////////////////////////////////////////////////

    /**
     * 获取数据库中所有属性，不包括已经删除了的
     *
     * @return
     */
    public List<Property> queryAllProperties() {
        play.Logger.info("------PropertyAndValueService queryAllProperties begin exe-----------");

        String jpql = "select o from Property o where 1=1 and o.isDelete=:isDelete";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("isDelete", false);
        return generalDao.query(jpql, Optional.<Page<Property>>empty(), queryParams);
    }

    /**
     * 通过属性id获取属性
     *
     * @param id
     * @return
     */
    public Property getPropertyById(int id) {
        play.Logger.info("------PropertyAndValueService getPropertyById begin exe-----------" + id);
        return generalDao.get(Property.class, id);
    }

    /**
     * @param ids
     * @return
     */
    public List<Property> getPropertyByIdList(List<Integer> ids) {
        if (ids.isEmpty()) return new ArrayList<>();

        StringBuilder jpql = new StringBuilder("select p from Property p where p.id in (");
        Map<String, Object> queryParams = new HashMap<>();

        for (int i = 0; i < ids.size(); i++) {
            String paramName = "id" + i;
            jpql.append(String.format(":%s,", paramName));
            queryParams.put(paramName, ids.get(i));
        }
        jpql.replace(jpql.length() - 1, jpql.length(), ")");

        return generalDao.query(jpql.toString(), Optional.<Page<Property>>empty(), queryParams);

    }

    /**
     * @param ids
     * @return
     */
    public List<Value> getValueByIdList(List<Integer> ids) {
        if (ids.isEmpty()) return new ArrayList<>();

        StringBuilder jpql = new StringBuilder("select v from Value v where v.id in (");
        Map<String, Object> queryParams = new HashMap<>();

        for (int i = 0; i < ids.size(); i++) {
            String paramName = "id" + i;
            jpql.append(String.format(":%s,", paramName));
            queryParams.put(paramName, ids.get(i));
        }
        jpql.replace(jpql.length() - 1, jpql.length(), ")");

        return generalDao.query(jpql.toString(), Optional.<Page<Value>>empty(), queryParams);

    }


    //////////////////////////////////值////////////////////////////////////////////////

    /**
     * 获取数据库中所有值，不包括已经删除了的
     *
     * @return
     */
    public List<Value> queryAllValues() {
        play.Logger.info("------PropertyAndValueService queryAllValues begin exe-----------");

        String jpql = "select o from Value o where 1=1 and o.isDelete=:isDelete";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("isDelete", false);
        return generalDao.query(jpql, Optional.<Page<Value>>empty(), queryParams);
    }

    /**
     * 通过值id获取值
     *
     * @param id
     * @return
     */
    public Value getValueById(int id) {
        play.Logger.info("------PropertyAndValueService getValueById begin exe-----------" + id);
        return generalDao.get(Value.class, id);
    }


    //////////////////////////////////属性及其值////////////////////////////////////////////////

    /**
     * 获取数据库中所有属性及其值，不包括已经删除了的
     *
     * @return
     */
    public List<PropertyValueDetail> queryAllPropertyValueDetails() {
        play.Logger.info("------PropertyAndValueService queryAllPropertyValueDetails begin exe-----------");
        String jpql = "select o from PropertyValueDetail o where 1=1 and o.isDelete=:isDelete";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("isDelete", false);
        return generalDao.query(jpql, Optional.<Page<PropertyValueDetail>>empty(), queryParams);
    }

    /**
     * 按照主键id获取属性及其值信息
     *
     * @param id
     * @return
     */
    public PropertyValueDetail getPropertyValueDetailById(int id) {
        play.Logger.info("------PropertyAndValueService getPropertyValueDetailById begin exe-----------" + id);
        return generalDao.get(PropertyValueDetail.class, id);
    }

    /**
     * @param propertyId
     * @param valueId
     * @return
     */
    public PropertyValueDetail getPropertyValueDetail(int propertyId, int valueId) {
        play.Logger.info("------PropertyAndValueService queryAllPropertyValueDetails begin exe-----------");
        String jpql = "select o from PropertyValueDetail o where 1=1 and o.propertyId=:propertyId and valueId=:valueId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("propertyId", propertyId);
        queryParams.put("valueId", valueId);

        List<PropertyValueDetail> list = generalDao.query(jpql, Optional.ofNullable(null), queryParams);
        PropertyValueDetail pv = null;
        if (list != null && list.size() > 0) {
            pv = list.get(0);
        }
        return pv;
    }

    /**
     * @param valueName
     * @return
     */
    public Value getValueByName(String valueName) {
        if(org.apache.commons.lang3.StringUtils.isBlank(valueName)) {
            return null;
        }
        String jpql = "select v from Value v where v.valueName = :valueName and isDelete = false ";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("valueName", valueName);

        List<Value> results = generalDao.query(jpql, Optional.empty(), queryParams);
        if(results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }

    }

    /**
     * @param propertyName
     * @return
     */
    public Property createPropertyIfNotExist(String propertyName) {

        Property property = getPropertyByName(propertyName);
        if (property == null) {
            property = new Property();
            property.setName(propertyName);
            generalDao.persist(property);
        }

        return property;
    }

    /**
     * @param valueName
     * @return
     */
    public Value createValueIfNotExist(String valueName) {

        Value value = getValueByName(valueName);
        if (value == null) {
            value = new Value();
            value.setValueName(valueName);
            generalDao.persist(value);
        }

        return value;
    }


    /**
     * 根据name得到property
     * @param name
     * @return
     */
    public Property getPropertyByName(String name) {
        if(org.apache.commons.lang3.StringUtils.isBlank(name)) {
            return null;
        }

        String jpql = "select p from Property p where p.name = :name and p.isDelete = false";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("name", name);

        List<Property> results = generalDao.query(jpql, Optional.empty(), queryParams);
        if(results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }

    }

}
