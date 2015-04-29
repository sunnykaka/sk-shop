package usercenter.services;

import common.services.GeneralDao;
import common.utils.SQLUtils;
import common.utils.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usercenter.models.DesignerCollect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by zhb on 15-4-27.
 */
@Service
@Transactional
public class DesignerCollectService {

    @Autowired
    GeneralDao generalDAO;

    /**
     * 新建
     *
     */
    public void createDesignerCollect(DesignerCollect DesignerCollect){
        generalDAO.persist(DesignerCollect);
    }

    /**
     * 更新
     *
     * @param DesignerCollect
     * @return
     */
    public DesignerCollect updateDesignerCollect(DesignerCollect DesignerCollect){
        return generalDAO.merge(DesignerCollect);
    }

    /**
     * 根据Id软删除
     *
     * @param id
     */
    public void deleteDesignerCollect(int id){

        DesignerCollect DesignerCollect = generalDAO.get(DesignerCollect.class,id);
        if(null != DesignerCollect){
            DesignerCollect.setDeleted(SQLUtils.SQL_DELETE_TRUE);
        }

        updateDesignerCollect(DesignerCollect);

    }

    /**
     * 删除我的收藏(软删)
     *
     * @param designerId
     * @param userId
     */
    public void deleteMyDesignerCollect(int designerId,int userId){

        DesignerCollect DesignerCollect = getByDesignerId(designerId, userId);
        DesignerCollect.setDeleted(SQLUtils.SQL_DELETE_TRUE);
        updateDesignerCollect(DesignerCollect);

    }

    /**
     * 根据Id查找
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public DesignerCollect getById(int id){
        return generalDAO.get(DesignerCollect.class,id);
    }

    /**
     * 查询我的收藏记录
     *
     * @param designerId
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public DesignerCollect getByDesignerId(int designerId,int userId){

        String jpql = "select pc from DesignerCollect pc where 1=1 and pc.deleted=false ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and pc.designerId = :designerId ";
        queryParams.put("designerId", designerId);

        jpql += " and pc.userId = :userId ";
        queryParams.put("userId", userId);

        List<DesignerCollect> valueList = generalDAO.query(jpql, Optional.empty(), queryParams);
        if (valueList != null && valueList.size() > 0) {
            return valueList.get(0);
        }

        return null;
    }


    /**
     * 根据userId分页查询
     *
     * @param page
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<DesignerCollect> getDesignerCollectList(Optional<Page<DesignerCollect>> page, int userId){
        play.Logger.info("--------DesignerCollectService getDesignerCollectList begin exe-----------" + page + "\n" + userId);

        String jpql = "select dc from DesignerCollect dc left join fetch dc.designer d where 1=1 and dc.deleted=false ";
        Map<String, Object> queryParams = new HashMap<>();

        jpql += " and dc.userId = :userId ";
        queryParams.put("userId", userId);

        return generalDAO.query(jpql, page, queryParams);
    }

}
