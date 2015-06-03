package usercenter.services;

import common.services.GeneralDao;
import common.utils.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usercenter.constants.DesignerPictureType;
import usercenter.dtos.DesignerView;
import usercenter.models.Designer;
import usercenter.models.DesignerPicture;

import java.util.*;

/**
 * Created by zhb on 15-4-27.
 */
@Service
@Transactional
public class DesignerService {

    @Autowired
    GeneralDao generalDAO;


    /**
     * 查询最新入住的设计师
     * @param count
     * @return
     */
    @Transactional(readOnly = true)
    public List<DesignerView> lastCreateDesigner(int count){
        String sql = "select t1.id,t1.name,t1.description,t2.StorePic,t2.ListMainPic,t2.ListLogoBigPic from customer as t1 left JOIN" +
                " ( select designerId,max(if(picType='StorePic',pictureUrl,NULL )) as StorePic ," +
                "max(if(picType='ListMainPic',pictureUrl,NULL )) as ListMainPic ," +
                "max(if(picType='ListLogoBigPic',pictureUrl,NULL )) as ListLogoBigPic from  designer_picture group by designerId) AS t2 ON  t2.designerId = t1.id where t1.isDelete =0 and t1.isPublished = 1 order by id desc limit ?1";
        List list = generalDAO.getEm().createNativeQuery(sql).setParameter(1,count).getResultList();
        List<DesignerView> result = new ArrayList<>();
        for (Object obj : list) {
            Object[] designer = (Object[]) obj;
            DesignerView dv = new DesignerView();
            dv.setId((Integer) designer[0]);
            dv.setName(designer[1].toString());

            if (designer[2] != null) {
                dv.setDescription(designer[2].toString());
            }
            /**
             * 防止有些设计师，没有设置主图
             */
            if (designer[3] != null) {
                dv.setStorePic(designer[3].toString());
            }
            if (designer[4] != null) {
                dv.setMainPic(designer[4].toString());
            }
            if (designer[5] != null) {
                dv.setBrandPic(designer[5].toString());
            }

            result.add(dv);

        }
        return result;
    }


    /**
     * 设计师列表，如果ID为空，一次性查询上所有的设计师
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<DesignerView> designerById(Integer id,Page page) {
        String sql = "select t1.id,t1.name,t1.description,t2.StorePic,t2.ListMainPic,t2.ListLogoBigPic from customer as t1 left JOIN" +
                " ( select designerId,max(if(picType='StorePic',pictureUrl,NULL )) as StorePic ," +
                "max(if(picType='ListMainPic',pictureUrl,NULL )) as ListMainPic ," +
                "max(if(picType='ListLogoBigPic',pictureUrl,NULL )) as ListLogoBigPic from  designer_picture group by designerId) AS t2 ON  t2.designerId = t1.id where t1.isDelete=0 and t1.isPublished = 1 ";
        if(id != null){
            sql += " and t1.id = " +id;
        }
        if(page != null){
            sql += " limit " + page.getStart() + " , " + page.getLimit();
        }
        List list = generalDAO.getEm().createNativeQuery(sql).getResultList();
        List<DesignerView> result = new ArrayList<>();
        for (Object obj : list) {
            Object[] designer = (Object[]) obj;
            DesignerView dv = new DesignerView();
            dv.setId((Integer) designer[0]);
            dv.setName(designer[1].toString());

            if (designer[2] != null) {
                dv.setDescription(designer[2].toString());
            }
            /**
             * 防止有些设计师，没有设置主图
             */
            if (designer[3] != null) {
                dv.setStorePic(designer[3].toString());
            }
            if (designer[4] != null) {
                dv.setMainPic(designer[4].toString());
            }
            if (designer[5] != null) {
                dv.setBrandPic(designer[5].toString());
            }

            result.add(dv);

        }
        return result;
    }

    /**
     * 根据ID查询
     *
     * @param designerId
     * @return
     */
    @Transactional(readOnly = true)
    public Designer getDesignerById(int designerId) {
        return generalDAO.get(Designer.class, designerId);
    }

    /**
     * 通过图类型获取设计师图片
     *
     * @param picType
     * @return
     * create by lidujun
     */
    @Transactional(readOnly = true)
    public DesignerPicture getDesignerPicByType(int designerId, DesignerPictureType picType) {
        DesignerPicture designerPicture = null;

        String jpql = "select dp from DesignerPicture dp where dp.picType=:picType and dp.designerId=:designerId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("picType", picType);
        queryParams.put("designerId", designerId);

        List<DesignerPicture> list = generalDAO.query(jpql, Optional.ofNullable(null), queryParams);
        if (list != null && list.size() > 0) {
            designerPicture = list.get(0);
        }
        return designerPicture;
    }
}
