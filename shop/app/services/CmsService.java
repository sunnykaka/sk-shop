package services;

import common.services.GeneralDao;
import models.CmsContent;
import models.CmsExbitionItem;
import models.CmsExhibition;
import models.ExhibitionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by amos on 15-5-7.
 */
@Service
@Transactional
public class CmsService {

    @Autowired
    private GeneralDao generalDao;


    /**
     * 首发专场的读取
     *
     * @return
     */
    @Transactional(readOnly = true)
    public Map<ExhibitionStatus, List<CmsExhibition>> queryAllExhibition() {
        List<CmsExhibition> list = generalDao.findAll(CmsExhibition.class);
        Map<ExhibitionStatus, List<CmsExhibition>> result = new HashMap<>();
        List<CmsExhibition> listPrepare = new LinkedList<>();
        List<CmsExhibition> listSelling = new LinkedList<>();
        result.put(ExhibitionStatus.SELLING, listSelling);
        result.put(ExhibitionStatus.PREPARE, listPrepare);
        for (CmsExhibition exhibition : list) {
            if (exhibition.getStatus().equals(ExhibitionStatus.SELLING)) {
                listSelling.add(exhibition);
            }

            if (exhibition.getStatus().equals(ExhibitionStatus.PREPARE)) {
                listPrepare.add(exhibition);
            }
        }
        return result;
    }


    /**
     * 查询首发专场的产品ID
     * @param exhibitionId
     * @return
     */
    @Transactional(readOnly = true)
    public List<CmsExbitionItem> queryItemListByExbId(Integer exhibitionId) {
        String sql = " select * from exhibition_item where exhibitionId = ?1";
        List<CmsExbitionItem> items = generalDao.getEm().createNativeQuery(sql, CmsExbitionItem.class).setParameter(1, exhibitionId).getResultList();
        return items;
    }


    /**
     * 查询所有的内容列表
     * @return
     */
    @Transactional(readOnly = true)
    public List<CmsContent> allContents() {
        return generalDao.findAll(CmsContent.class);
    }


    /**
     * check一个商品是正在首发中
     * @param prodId
     * @return
     */
    @Transactional(readOnly = true)
    public boolean onFirstPublish(int prodId) {
        String sql = "select * from cms_exhibition where (beginTime  <= Now() AND endTime > Now())  and  id in ( select exhibitionId from exhibition_item where prodId = ?1)";
        List<CmsExhibition> list = generalDao.getEm().createNativeQuery(sql, CmsExhibition.class).setParameter(1, prodId).getResultList();
        return list != null && list.size() > 0;
    }


}
