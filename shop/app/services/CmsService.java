package services;

import common.services.GeneralDao;
import models.CmsContent;
import models.CmsExbitionItem;
import models.CmsExhibition;
import models.ExhibitionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.*;

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
        String sql = "select * from cms_exhibition order by positionIndex asc";
        List<CmsExhibition> list = generalDao.getEm().createNativeQuery(sql, CmsExhibition.class).getResultList();
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
     * 查询设计师所有的专场
     *
     * @param dId
     * @return
     */
    public List<CmsExhibition> findExhibitionByDesigner(Integer dId) {
        String sql = "select * from cms_exhibition where designerId = ?1";
        return generalDao.getEm().createNativeQuery(sql, CmsExhibition.class).setParameter(1, dId).getResultList();
    }


    /**
     * 查找某个位置，最新的专场，不管下架与否
     * @param position
     * @return
     */
    public CmsExhibition findExhibitionByPosition(Integer position){
        String sql = " select * from cms_exhibition where positionIndex  = ?1 order by endTime desc limit 1";
        CmsExhibition exhibition = (CmsExhibition) generalDao.getEm().createNativeQuery(sql,CmsExhibition.class).setParameter(1,position).getSingleResult();
        return exhibition;
    }

    /**
     * 根据ID 查询首发专场
     *
     * @param id
     * @return
     */
    public CmsExhibition findExhibitionByIds(Integer id) {
        String sql = "select * from cms_exhibition where id = ?1";
        return (CmsExhibition) generalDao.getEm().createNativeQuery(sql, CmsExhibition.class).setParameter(1, id).getSingleResult();
    }

    /**
     * 查询首发专场的产品ID
     *
     * @param exhibitionIds
     * @return
     */
    @Transactional(readOnly = true)
    public List<CmsExbitionItem> queryItemListByExbId(List<Integer> exhibitionIds) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < exhibitionIds.size(); i++) {
            stringBuilder.append("?").append(i + 1).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        String jpql = " select ci from CmsExbitionItem ci where ci.exhibitionId in (" + stringBuilder + ")";
        Query query = generalDao.getEm().createQuery(jpql);
        for (int i = 0; i < exhibitionIds.size(); i++) {
            query.setParameter(i + 1, exhibitionIds.get(i));
        }
        return query.getResultList();
    }


    /**
     * 查询所有的内容列表
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<CmsContent> allContents() {
        String sql = "select * from cms_content order by priority desc,id desc";
        return generalDao.getEm().createNativeQuery(sql, CmsContent.class).getResultList();
    }


    /**
     * check一个商品是正在首发中
     *
     * @param prodId
     * @return
     */
    @Transactional(readOnly = true)
    public boolean onFirstPublish(int prodId) {
        return findExhibitionWithProdId(prodId).isPresent();
    }

    @Transactional(readOnly = true)
    public Optional<CmsExhibition> findExhibitionWithProdId(int prodId) {
        String sql = "select * from cms_exhibition where (beginTime  <= Now() AND endTime > Now())  and  id = ( select exhibitionId from exhibition_item where prodId = ?1)";
        List<CmsExhibition> list = generalDao.getEm().createNativeQuery(sql, CmsExhibition.class).setParameter(1, prodId).getResultList();
        if (list != null && list.size() > 0) {
            return Optional.of(list.get(0));
        }
        return Optional.empty();
    }

}
