package services;

import common.services.GeneralDao;
import common.utils.page.Page;
import models.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 标签 Service
 * User: lidujun
 * Date: 2015-04-01
 */
@Service
@Transactional
public class TagService {
    @Autowired
    private GeneralDao generalDao;

    /**
     * 保存标签
     */
    public void save(Tag tag){
        play.Logger.info("--------TagService save begin exe-----------" + tag);
        generalDao.persist(tag);
    }

    /**
     * 删除标签
     */
    public void realDelete(Integer tagId){
        play.Logger.info("--------TagService realDelete begin exe-----------" + tagId);
        generalDao.removeById(Tag.class, tagId);
    }

    /**
     * 更新标签
     */
    public void update(Tag tag){
        play.Logger.info("--------TagService update begin exe-----------" + tag);
        generalDao.merge(tag);
    }

    /**
     * 通过主键获取标签
     */
    @Transactional(readOnly = true)
    public Optional<Tag> getTagById(Integer tagId){
        play.Logger.info("--------TagService getTagById begin exe-----------" + tagId);
        return Optional.ofNullable(generalDao.get(Tag.class, tagId));
    }

    /**
     * 获取标签
     */
    @Transactional(readOnly = true)
    public List<Tag> getTagList(Optional<Page<Tag>> page, Tag param){
        play.Logger.info("--------TagService getTagList begin exe-----------" + page + "\n" + param);

        String jpql = "select o from Tag o where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        if(param != null) {
             String name = param.getName();
             if(!StringUtils.isEmpty(name)) {
                 jpql += " and o.name like :name ";
                 queryParams.put("name", "%" + name + "%");
             }
        }
        jpql += " order by o.name";
        return generalDao.query(jpql, page, queryParams);
    }

}
