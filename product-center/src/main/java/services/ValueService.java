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



}
