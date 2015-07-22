package common.services;

import com.esms.common.entity.GsmsResponse;
import common.models.SmsHistory;
import common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by liubin on 15-6-5.
 */
@Service
public class SmsHistoryService {

    @Autowired
    private GeneralDao generalDao;

    @Transactional
    public void createSmsHistory(String[] phones, String content, GsmsResponse resp) {

        try {

            SmsHistory history = new SmsHistory();
            if(phones != null) {
                history.setPhones(String.join(",", phones));
                history.setCount(phones.length);
            } else {
                history.setPhones("");
                history.setCount(0);
            }
            history.setCreateTime(DateUtils.current());
            history.setResult(String.valueOf(resp.getResult()));
            history.setResp(resp.toString());
            history.setContent(content);

            generalDao.persist(history);

        } catch (Exception e) {
            play.Logger.error(e.getMessage(), e);
        }

    }
}
