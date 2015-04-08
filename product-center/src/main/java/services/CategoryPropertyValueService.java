package services;

import common.services.GeneralDao;
import models.CategoryPropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by zhb on 15-4-7.
 */
@Service
@Transactional
public class CategoryPropertyValueService {

    @Autowired
    GeneralDao generalDAO;

    /**
     * 添加类目、属性、值关联
     *
     * @param categoryPropertyValue
     */
    public void save(CategoryPropertyValue categoryPropertyValue){
        generalDAO.persist(categoryPropertyValue);
    }

    /**
     *
     *
     * @param categoryPropertyValue
     * @return
     */
    public CategoryPropertyValue update(CategoryPropertyValue categoryPropertyValue){
        return generalDAO.merge(categoryPropertyValue);
    }

    public void delete(int id){
        generalDAO.removeById(CategoryPropertyValue.class,id);
    }
}
