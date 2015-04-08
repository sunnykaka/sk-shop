package services;

import common.services.GeneralDao;
import common.utils.page.Page;
import models.DesignerPicture;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * 设计师图片Service
 * User: lidujun
 * Date: 2015-04-08
 */
@Service
@Transactional
public class DesignerPictureService {
    @Autowired
    GeneralDao generalDao;

    /**
     * 保存设计师图片
     */
    public void save(DesignerPicture picture){
        play.Logger.info("--------DesignerPictureService save begin exe-----------" + picture);
        generalDao.persist(picture);
    }

    /**
     * 删除设计师图片
     */
    public void realDelete(Integer pictureId){
        play.Logger.info("--------DesignerPictureService realDelete begin exe-----------" + pictureId);
        generalDao.removeById(DesignerPicture.class, pictureId);
    }

    /**
     * 更新设计师图片
     */
    public void update(DesignerPicture picture){
        play.Logger.info("--------DesignerPictureService update begin exe-----------" + picture);
        generalDao.merge(picture);
    }

    /**
     * 通过主键获取设计师图片
     */
    @Transactional(readOnly = true)
    public Optional<DesignerPicture> getDesignerPictureById(Integer pictureId){
        play.Logger.info("--------DesignerPictureService getDesignerPictureById begin exe-----------" + pictureId);
        return Optional.ofNullable(generalDao.get(DesignerPicture.class, pictureId));
    }

    /**
     * 获取设计师图片列表
     */
    @Transactional(readOnly = true)
    public List<DesignerPicture> getDesignerPictureList(Optional<Page<DesignerPicture>> page, DesignerPicture param){
        play.Logger.info("--------DesignerPictureService getDesignerPictureList begin exe-----------" + page + "\n" + param);

        String jpql = "select o from DesignerPicture o where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();

        if(param != null) {
            Integer designerId = param.getDesignerId();
            if(designerId != null && designerId != 0) {
                jpql += " and o.designerId = :designerId ";
                queryParams.put("designerId", designerId);
            }

            String type = param.getType();
            if(!StringUtils.isEmpty(type)) {
                jpql += " and o.type = :type ";
                queryParams.put("type", type);
            }

            String picUrl = param.getPicUrl();
            if(!StringUtils.isEmpty(picUrl)) {
                jpql += " and o.picUrl = :picUrl ";
                queryParams.put("picUrl", picUrl);
            }
        }
        jpql += " order by o.id ";
        return generalDao.query(jpql, page, queryParams);
    }

}
