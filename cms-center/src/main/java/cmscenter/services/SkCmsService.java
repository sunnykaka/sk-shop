package cmscenter.services;

import cmscenter.models.CmsExhibitionFans;
import cmscenter.models.SkContent;
import cmscenter.models.SkExhibition;
import cmscenter.models.SkModule;
import common.exceptions.AppBusinessException;
import common.services.GeneralDao;
import common.utils.DateUtils;
import common.utils.RegExpUtils;
import common.utils.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usercenter.models.User;

import java.util.*;

/**
 * Created by amoszhou on 15/9/18.
 */
@Service
@Transactional
public class SkCmsService {

    @Autowired
    private GeneralDao generalDao;


    @Transactional
    public void userLikeExhibition(Integer exhibitionId, String phone, Optional<Integer> userId) {
        if (!RegExpUtils.isPhone(phone)) {
            throw new AppBusinessException("手机号格式不正确");
        }

        if (findCmsExhibitionFans(exhibitionId, phone).isPresent()) {
            throw new AppBusinessException("您已开启提醒");
        }
        CmsExhibitionFans cmsExhibitionFans = new CmsExhibitionFans();
        cmsExhibitionFans.setCreateTime(DateUtils.current());
        cmsExhibitionFans.setExhibitionId(exhibitionId);
        cmsExhibitionFans.setPhone(phone);
        if (userId.isPresent()) {
            cmsExhibitionFans.setUserId(userId.get());
        }
        generalDao.persist(cmsExhibitionFans);
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
     * 判断是否订阅
     *
     * @param user
     * @param productId
     * @return
     */
    public boolean isBooked(User user, int productId){
        if(user == null){
            return false;
        }
        Optional<CmsExhibitionFans> oCef = findCmsExhibitionFans(productId, user.getPhone());
        return oCef.isPresent();
    }

    /**
     * 我的订阅列表
     *
     * @param page
     * @param phone
     * @param processed
     * @return
     */
    @Transactional(readOnly = true)
    public List<CmsExhibitionFans> findCmsExhibitionFansByPhone(Optional<Page<CmsExhibitionFans>> page, String phone, boolean processed) {
        String hql = "select cef from CmsExhibitionFans cef where cef.processed = :processed and cef.phone = :phone order by createTime desc ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("processed", processed);
            put("phone", phone);
        }};
        return generalDao.query(hql, page, params);

    }


    /**
     * 组装好module 以及 module里面的内容，组装逻辑如下：
     * 1.所有module都要展示，各模块按优先级降序来排列，优先级越大越靠上面
     * 2.模块里面的内容根据module的itemCount来决定数量，排序逻辑也是按跟module一样
     *
     * @return
     */
    public List<SkModule> buildModuleAndContent() {

        /**
         * 1.查出所有module 并按照优先级降序排序
         */

        String query = " from SkModule order by priority desc ";
        List<SkModule> moduleList = generalDao.query(query, Optional.empty(), new HashMap<>());
//        moduleList.sort((m1, m2) -> {
//            if (m1.getPriority() > m2.getPriority()) {
//                return 1;
//            } else if (m1.getPriority() == m2.getPriority()) {
//                return 0;
//            }
//            return -1;
//        });

        /**
         * 每个模块里面的content按优先级倒序来排，并且只取指定个数，放到module中。
         */
        String jpql = " from SkContent where moduleId =:moduleId order by priority desc ";
        for (SkModule module : moduleList) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("moduleId", module.getId());
            List<SkContent> list = generalDao.query(jpql, Optional.of(new Page<>(1, module.getItemCount())), params);
            if (list != null && list.size() > 0) {
                module.setContents(list);
            }
        }

        /**
         * 删除没有内容的模块
         */
        Iterator<SkModule> it = moduleList.iterator();
        while (it.hasNext()) {
            if (it.next().getContents() == null) {
                it.remove();
            }
        }

        return moduleList;
    }


    /**
     * 活动列表
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<SkExhibition> allExhibition() {
        return generalDao.findAll(SkExhibition.class);
    }


    /**
     * 查询具体的某个活动
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Optional<SkExhibition> exhibitionById(Integer id) {
        String jpql = " from SkExhibition where id = :id";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        List<SkExhibition> list = generalDao.query(jpql, Optional.empty(), params);
        if (list != null && list.size() > 0) {
            return Optional.of(list.get(0));
        }
        return Optional.empty();
    }
}
