package services;

import common.services.GeneralDao;
import models.CategoryProperty;
import models.CategoryPropertyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by zhb on 15-4-7.
 */
@Service
@Transactional
public class CategoryPropertyValueService {

    private static final Logger log = LoggerFactory.getLogger(CategoryPropertyValueService.class);

    @PersistenceContext
    EntityManager em;

    @Autowired
    GeneralDao generalDAO;

    public void save(CategoryPropertyValue categoryPropertyValue){
        generalDAO.persist(categoryPropertyValue);
    }

    public CategoryPropertyValue update(CategoryPropertyValue categoryPropertyValue){
        return generalDAO.merge(categoryPropertyValue);
    }

    public void delete(int id){
        generalDAO.removeById(CategoryPropertyValue.class,id);
    }
}
