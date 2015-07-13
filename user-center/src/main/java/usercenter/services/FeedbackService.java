package usercenter.services;

import common.services.GeneralDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usercenter.models.Bulletin;
import usercenter.models.Feedback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 反馈意见service
 * User: lidujun
 * Date: 2015-07-13
 */
@Service
public class FeedbackService {

    @Autowired
    private GeneralDao generalDAO;

    /**
     * 创建反馈意见
     *
     * @param feedback
     */
    @Transactional
    public void createFeedback(Feedback feedback){
        generalDAO.persist(feedback);
    }

    /**
     * 通过图类型获取设计师图片
     *
     * @return
     * create by lidujun
     */
    @Transactional(readOnly = true)
    public Bulletin getBulletin() {
        Bulletin bulletin = null;

        String jpql = "select dp from Bulletin b where b.effective=:effective";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("effective", true);

        List<Bulletin> list = generalDAO.query(jpql, Optional.ofNullable(null), queryParams);
        if (list != null && list.size() > 0) {
            bulletin = list.get(0);
        }
        return bulletin;
    }
}
