package services;

import common.services.GeneralDao;
import models.CategoryProperty;
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

    @PersistenceContext
    EntityManager em;

    @Autowired
    GeneralDao generalDAO;

    /**
     * 新建CategoryProperty关联
     *
     * @param categoryProperty
     */
    public void createCategoryProperty(CategoryProperty categoryProperty){
        generalDAO.persist(categoryProperty);
    }

    /**
     * 更新CategoryProperty关联
     *
     * @param categoryProperty
     * @return
     */
    public CategoryProperty updateCategoryProperty(CategoryProperty categoryProperty){
        return generalDAO.merge(categoryProperty);
    }

    /**
     * 根据Id删除
     *
     * @param id
     */
    public void delete(int id){
        generalDAO.removeById(CategoryProperty.class,id);
    }

    /**
     * 根据Id查找
     *
     * @param id
     * @return
     */
    public CategoryProperty getCategoryPropertyById(int id){
        return generalDAO.get(CategoryProperty.class,id);
    }

}
