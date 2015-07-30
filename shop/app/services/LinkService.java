package services;

import common.services.GeneralDao;
import models.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 友情链接
 * User: lidujun
 * Date: 2015-07-14
 */
@Service
public class LinkService {

    @Autowired
    private GeneralDao generalDao;

    /**
     * 获取有效的友情连接列表
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<Link> getLinkList(){
        play.Logger.info("--------LinkService getLinkList begin exe-----------");

        String jpql = "select o from Link o where o.effective=:effective order by o.name";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("effective", true);
        return generalDao.query(jpql, Optional.ofNullable(null), queryParams);
    }

}
