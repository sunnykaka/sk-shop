package productcenter.services;

import common.services.GeneralDao;
import common.utils.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productcenter.models.Property;
import productcenter.models.PropertyValueDetail;
import productcenter.models.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Sku和库存Service
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
     * @param id
     * @return
     */
    public Property getPropertyById(int id) {
        play.Logger.info("------PropertyAndValueService getPropertyById begin exe-----------" + id);
        return generalDao.get(Property.class, id);
    }


    //////////////////////////////////值////////////////////////////////////////////////
    /**
     * 获取数据库中所有值，不包括已经删除了的
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
     * @param id
     * @return
     */
    public PropertyValueDetail getPropertyValueDetailById(int id) {
        play.Logger.info("------PropertyAndValueService getPropertyValueDetailById begin exe-----------" + id);
        return generalDao.get(PropertyValueDetail.class, id);
    }

    /**
     *
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
        if(list != null && list.size() > 0) {
            pv = list.get(0);
        }
        return pv;
    }

}
