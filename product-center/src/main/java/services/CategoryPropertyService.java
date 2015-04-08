package services;

import common.services.GeneralDao;
import models.CategoryProperty;
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
public class CategoryPropertyService {

    private static final Logger log = LoggerFactory.getLogger(CategoryPropertyService.class);

    @PersistenceContext
    EntityManager em;

    @Autowired
    GeneralDao generalDAO;

    public void save(CategoryProperty categoryProperty){
        generalDAO.persist(categoryProperty);
    }

    public CategoryProperty update(CategoryProperty categoryProperty){
        return generalDAO.merge(categoryProperty);
    }

    public void delete(int id){
        generalDAO.removeById(CategoryProperty.class,id);
    }

}
