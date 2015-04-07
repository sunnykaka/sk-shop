package services;

import common.services.GeneralDao;
import common.utils.page.Page;
import models.Designer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 设计师 Service
 * User: lidujun
 * Date: 2015-04-07
 */
@Service
@Transactional
public class DesignerService {
    private static final Logger log = LoggerFactory.getLogger(DesignerService.class);

    @Autowired
    GeneralDao generalDao;

    /**
     * 保存设计师信息
     */
    public void save(Designer designer){
        log.info("--------DesignerService save begin exe-----------" + designer);
        generalDao.persist(designer);
    }

    /**
     * 假删除设计师信息
     */
    public void falseDelete(Integer designerId){
        log.info("--------DesignerService falseDelete begin exe-----------" + designerId);
        Designer designer = generalDao.get(Designer.class, designerId);
        designer.setIsDelete(false);
        this.update(designer);
    }

    /**
     * 更新设计师信息
     */
    public void update(Designer designer){
        log.info("--------DesignerService update begin exe-----------" + designer);
        generalDao.merge(designer);
    }

    /**
     * 通过主键获取设计师信息
     */
    @Transactional(readOnly = true)
    public Designer getDesignerById(Integer designerId){
        log.info("--------ProductContentService getDesignerById begin exe-----------" + designerId);
        return generalDao.get(Designer.class, designerId);
    }

    /**
     * 获取设计师信息列表
     */
    @Transactional(readOnly = true)
    public List<Designer> getDesignerList(Optional<Page<Designer>> page, Designer param){
        log.info("--------DesignerService getDesignerList begin exe-----------" + page + "\n" + param);

        String jpql = "select o from Order o join o.orderItemList o where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();

         if(param != null) {
             String name = param.getName();
             if(!StringUtils.isEmpty(name)) {
                 jpql += " and o.name like :name ";
                 queryParams.put("name", name);
             }

             Integer nationId = param.getNationId();
             if(nationId != null && nationId != 0) {
                 jpql += " and o.nationId = :nationId ";
                 queryParams.put("nationId", nationId);
             }

             Boolean isDelete = param.getIsDelete();
             if(isDelete != null) {
                 jpql += " and o.isDelete = :isDelete ";
                 queryParams.put("isDelete", isDelete);
             }
         }
        jpql += " group by o.name";
        return generalDao.query(jpql, page, queryParams);
    }

}
