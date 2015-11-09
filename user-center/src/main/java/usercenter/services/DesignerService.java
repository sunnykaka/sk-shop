package usercenter.services;

import common.services.GeneralDao;
import common.utils.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import play.Logger;
import usercenter.constants.DesignerPictureType;
import usercenter.dtos.DesignerView;
import usercenter.models.Designer;
import usercenter.models.DesignerPicture;
import usercenter.models.DesignerSize;

import java.math.BigInteger;
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
     *
     * @param count
     * @return
     */
    @Transactional(readOnly = true)
    public List<DesignerView> lastCreateDesigner(int count) {
        String sql = "select t1.id,t1.name,t1.description,t2.StorePic,t2.ListMainPic,t2.ListLogoBigPic,t2.StoreLogoPic from customer as t1 left JOIN" +
                " ( select designerId,max(if(picType='StorePic',pictureUrl,NULL )) as StorePic ," +
                "max(if(picType='ListMainPic',pictureUrl,NULL )) as ListMainPic ," +
                "max(if(picType='ListLogoBigPic',pictureUrl,NULL )) as ListLogoBigPic,max(if(picType='StoreLogoPic',pictureUrl,NULL )) as StoreLogoPic  from  designer_picture group by designerId) AS t2 ON  t2.designerId = t1.id where t1.isDelete =0 and t1.isPublished = 1 order by id desc limit ?1";
        List list = generalDAO.getEm().createNativeQuery(sql).setParameter(1, count).getResultList();
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
            if (designer[6] != null) {
                dv.setStoreLogoPic(designer[6].toString());
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
    public List<DesignerView> designerById(Integer id) {
        String sql = "select t1.id,t1.name,t1.description,t2.StorePic,t2.ListMainPic,t2.ListLogoBigPic,t2.StoreLogoPic, t1.content from customer as t1 left JOIN" +
                " ( select designerId,max(if(picType='StorePic',pictureUrl,NULL )) as StorePic ," +
                "max(if(picType='ListMainPic',pictureUrl,NULL )) as ListMainPic ," +
                "max(if(picType='ListLogoBigPic',pictureUrl,NULL )) as ListLogoBigPic , max(if(picType='StoreLogoPic',pictureUrl,NULL )) as StoreLogoPic from  designer_picture group by designerId) AS t2 ON  t2.designerId = t1.id where t1.isDelete=0 and t1.isPublished = 1 ";
        sql += " and t1.id = " + id;
        sql += " order by t1.priority desc, id desc  ";
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
            if (designer[6] != null) {
                dv.setStoreLogoPic(designer[6].toString());
            }
            if (designer[7] != null) {
                dv.setContent(designer[7].toString());
            }
            result.add(dv);

        }
        return result;
    }

    public List<Designer> queryDesignerList(Page<Designer> page){
        String sql = "SELECT c.id,c.name from customer c JOIN product p ON c.id = p.customerId " +
                "where p.`online` = 1 and c.isDelete = 0 and c.isPublished = 1 " +
                "GROUP BY p.customerId ORDER BY c.priority desc";
        if (page != null) {
            sql += " limit " + page.getStart() + " , " + page.getLimit();
        }
        List list = generalDAO.getEm().createNativeQuery(sql).getResultList();

        List<Designer> result = new ArrayList<>();

        for (Object obj : list) {
            Object[] designer = (Object[]) obj;
            Designer dv = new Designer();
            dv.setId((Integer) designer[0]);
            dv.setName(designer[1].toString());
            result.add(dv);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<DesignerView> designerByPriority(Page page) {
        String sql = "select t1.id,t1.name,t1.description,t2.StorePic,t2.ListMainPic,t2.ListLogoBigPic,t2.WhiteBGPic from customer as t1 left JOIN" +
                " ( select designerId,max(if(picType='StorePic',pictureUrl,NULL )) as StorePic ," +
                "max(if(picType='ListMainPic',pictureUrl,NULL )) as ListMainPic ," +
                "max(if(picType='WhiteBGPic',pictureUrl,NULL )) as WhiteBGPic ," +
                "max(if(picType='ListLogoBigPic',pictureUrl,NULL )) as ListLogoBigPic from  designer_picture group by designerId) AS t2 ON  t2.designerId = t1.id where t1.isDelete=0 and t1.isPublished = 1 ";
        sql += " order by t1.priority desc, id desc  ";
        if (page != null) {
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
        Logger.info("------designerId-----" + designerId);
        return generalDAO.get(Designer.class, designerId);
    }

    /**
     * 通过图类型获取设计师图片
     *
     * @param picType
     * @return create by lidujun
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

    //================================= 设计师尺码表 =================================

    /**
     * 查询设计师尺码表
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public DesignerSize getDesignerSizeById(Integer id) {
        DesignerSize designerSize = null;
        if (null == id) {
            return designerSize;
        }

        String jpql = "select ds from DesignerSize ds where ds.deleted=false and ds.id =:id ";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("id", id);

        List<DesignerSize> list = generalDAO.query(jpql, Optional.ofNullable(null), queryParams);
        if (list != null && list.size() > 0) {
            designerSize = list.get(0);
        }
        return designerSize;
    }

    /**
     * 查询已经发布的，且未删除的设计师总数
     *
     * @return
     */
    public Integer allOnlineDesignerCount() {
        String sql = "select count(1) from customer where isDelete=0 and isPublished = 1 ";
        BigInteger count = (BigInteger) generalDAO.getEm().createNativeQuery(sql).getSingleResult();
        return count.intValue();
    }


    /**
     * 查询所有设计师
     * @return
     */
    public List<Designer> allOnlineDesigner() {
        String hql = " from Designer where isDelete=0 and isPublished = 1";
        return generalDAO.query(hql, Optional.empty(), new HashMap<>());
    }

}
