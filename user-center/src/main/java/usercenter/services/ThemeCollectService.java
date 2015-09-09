package usercenter.services;

import common.exceptions.AppBusinessException;
import common.services.GeneralDao;
import common.utils.SQLUtils;
import common.utils.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usercenter.models.DesignerCollect;
import usercenter.models.ThemeCollect;
import usercenter.models.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by zhb on 15-4-27.
 */
@Service
@Transactional
public class ThemeCollectService {

    @Autowired
    GeneralDao generalDAO;

    /**
     * 新建
     *
     */
    public void createThemeCollect(ThemeCollect themeCollect){
        generalDAO.persist(themeCollect);
    }

    /**
     * 更新
     *
     * @param themeCollect
     * @return
     */
    public ThemeCollect updateThemeCollect(ThemeCollect themeCollect){
        return generalDAO.merge(themeCollect);
    }

    /**
     * 删除我的收藏(软删)
     *
     * @param themeId
     * @param userId
     */
    public void deleteThemeCollect(int themeId,int userId){

        ThemeCollect themeCollect = getByThemeId(themeId, userId);
        if(null == themeCollect){
            throw new AppBusinessException("删除收藏失败");
        }
        themeCollect.setDeleted(SQLUtils.SQL_DELETE_TRUE);
        updateThemeCollect(themeCollect);

    }

    /**
     * 查询我的收藏记录
     *
     * @param themeId
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public ThemeCollect getByThemeId(int themeId,int userId){

        String jpql = "select pc from ThemeCollect pc where 1=1 and pc.deleted=false ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and pc.themeId = :themeId ";
        queryParams.put("themeId", themeId);

        jpql += " and pc.userId = :userId ";
        queryParams.put("userId", userId);

        List<ThemeCollect> valueList = generalDAO.query(jpql, Optional.empty(), queryParams);
        if (valueList != null && valueList.size() > 0) {
            return valueList.get(0);
        }

        return null;
    }

    /**
     * 是否收藏
     *
     * @param user
     * @param designerId
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isFavorites(User user,int designerId){
        if(null != user && null != getByThemeId(designerId, user.getId())) {
            return true;
        }
        return false;
    }


    /**
     * 根据userId分页查询
     *
     * @param page
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<ThemeCollect> getThemeCollectList(Optional<Page<ThemeCollect>> page, int userId){
        play.Logger.info("--------ThemeCollectService getThemeCollectList begin exe-----------" + page + "\n" + userId);

        String jpql = "select dc from ThemeCollect dc where 1=1 and dc.deleted=false ";
        Map<String, Object> queryParams = new HashMap<>();

        jpql += " and dc.userId = :userId ";
        queryParams.put("userId", userId);

        return generalDAO.query(jpql, page, queryParams);
    }

    /**
     * 统计收藏的专题
     *
     * @param themeId
     * @return
     */
    @Transactional(readOnly = true)
    public int countThemeCollect(int themeId){

        String jpql = "select dc from ThemeCollect dc where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();

        jpql += " and dc.themeId = :themeId ";
        queryParams.put("themeId", themeId);

        return generalDAO.query(jpql, Optional.<Page<DesignerCollect>>empty(), queryParams).size();
    }

}
