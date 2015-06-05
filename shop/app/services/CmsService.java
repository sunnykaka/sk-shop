package services;

import common.exceptions.AppBusinessException;
import common.services.GeneralDao;
import common.utils.DateUtils;
import common.utils.RegExpUtils;
import models.*;
import org.joda.time.DateTime;
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
     * 根据位置，数量，还有状态查询
     * @param positionName
     * @param size
     * @param status
     * @return
     */
    @Transactional(readOnly = true)
    public List<CmsExhibition> queryExhibitionByPosition(String positionName, int size, ExhibitionStatus status) {
        StringBuilder sql = new StringBuilder(" select * from cms_exhibition where positionName = ?1 ");
        if (status.equals(ExhibitionStatus.SELLING)) {
            sql.append(" and (beginTime <= Now() and endTime > Now())");
        }
        if (status.equals(ExhibitionStatus.PREPARE)) {
            sql.append(" and beginTime > Now() ");
        }
        if (status.equals(ExhibitionStatus.OVER)) {
            sql.append(" and end > Now() ");
        }

        sql.append(" order by positionIndex desc, beginTime asc limit ?2");

        return generalDao.getEm().createNativeQuery(sql.toString(), CmsExhibition.class).setParameter(1, positionName).setParameter(2, size).getResultList();


    }


    /**
     * 查询设计师所有的专场
     *
     * @param dId
     * @return
     */
    @Transactional(readOnly = true)
    public List<CmsExhibition> findExhibitionByDesigner(Integer dId) {
        String sql = "select * from cms_exhibition where designerId = ?1";
        return generalDao.getEm().createNativeQuery(sql, CmsExhibition.class).setParameter(1, dId).getResultList();
    }


    /**
     * 查找某个位置，最新的专场，不管下架与否
     *
     * @param position
     * @return
     */
    @Transactional(readOnly = true)
    public CmsExhibition findExhibitionByPosition(Integer position) {
        String sql = " select * from cms_exhibition where positionIndex  = ?1 order by endTime desc limit 1";
        CmsExhibition exhibition = (CmsExhibition) generalDao.getEm().createNativeQuery(sql, CmsExhibition.class).setParameter(1, position).getSingleResult();
        return exhibition;
    }

    /**
     * 根据ID 查询首发专场
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
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
        String sql = "select * from cms_exhibition where (beginTime  <= Now() AND endTime > Now())  and  id in ( select exhibitionId from exhibition_item where prodId = ?1)";
        List<CmsExhibition> list = generalDao.getEm().createNativeQuery(sql, CmsExhibition.class).setParameter(1, prodId).getResultList();
        if (list != null && list.size() > 0) {
            return Optional.of(list.get(0));
        }
        return Optional.empty();
    }

    @Transactional
    public void userLikeExhibition(Integer exhibitionId, String phone, Optional<Integer> userId) {
        if(!RegExpUtils.isPhone(phone)) {
            throw new AppBusinessException("手机号格式不正确");
        }
        if(findCmsExhibitionFans(exhibitionId, phone).isPresent()) {
            throw new AppBusinessException("您已开启提醒");
        }
        CmsExhibitionFans cmsExhibitionFans = new CmsExhibitionFans();
        cmsExhibitionFans.setCreateTime(DateUtils.current());
        cmsExhibitionFans.setExhibitionId(exhibitionId);
        cmsExhibitionFans.setPhone(phone);
        if(userId.isPresent()) {
            cmsExhibitionFans.setUserId(userId.get());
        }
        generalDao.persist(cmsExhibitionFans);

        String sql = "update cms_exhibition set baseLike = baseLike + 1 where id = ?1 ";
        generalDao.getEm().createNativeQuery(sql).setParameter(1, exhibitionId).executeUpdate();

    }

    @Transactional(readOnly = true)
    public Optional<CmsExhibitionFans> findCmsExhibitionFans(Integer exhibitionId, String phone) {
        String hql = "select cef from CmsExhibitionFans cef where cef.exhibitionId = :exhibitionId and cef.phone = :phone ";
        Map<String, Object> params = new HashMap<String, Object>() {{
           put("exhibitionId", exhibitionId);
           put("phone", phone);
        }};
        List<CmsExhibitionFans> query = generalDao.query(hql, Optional.empty(), params);
        return query.isEmpty() ? Optional.empty() : Optional.of(query.get(0));

    }

    /**
     * 根据开始时间范围查找专场
     * @param startTimeStart
     * @param startTimeEnd
     * @return
     */
    @Transactional(readOnly = true)
    public List<CmsExhibition> findCmsExhibitionByStartTime(DateTime startTimeStart, DateTime startTimeEnd) {
        String hql = "select ce from CmsExhibition ce where ce.startTime between :startTimeStart and :startTimeEnd";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("startTimeStart", startTimeStart);
            put("startTimeEnd", startTimeEnd);
        }};
        return generalDao.query(hql, Optional.empty(), params);
    }

    @Transactional(readOnly = true)
    public List<CmsExhibitionFans> findUnprocessedExhibitionFans(Integer exhibitionId) {
        String hql = "select cef from CmsExhibitionFans cef where cef.exhibitionId = :exhibitionId and cef.processed = false ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("exhibitionId", exhibitionId);
        }};
        return generalDao.query(hql, Optional.empty(), params);
    }




    @Transactional
    public void remindExhibitionStart() {
        DateTime startTimeStart = DateUtils.current();
        DateTime startTimeEnd = startTimeStart.plusSeconds(3600);

        List<CmsExhibition> exhibitionList = findCmsExhibitionByStartTime(startTimeStart, startTimeEnd);
        exhibitionList.forEach(exhibition -> {
            List<CmsExhibitionFans> exhibitionFansList = findUnprocessedExhibitionFans(exhibition.getId());


        });


    }
}
