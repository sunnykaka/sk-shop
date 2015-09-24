package cmscenter.services;

import cmscenter.models.SkContent;
import cmscenter.models.SkExhibition;
import cmscenter.models.SkModule;
import common.services.GeneralDao;
import common.utils.page.Page;
import common.utils.page.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by amoszhou on 15/9/18.
 */
@Service
@Transactional
public class SkCmsService {

    @Autowired
    private GeneralDao generalDao;


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
