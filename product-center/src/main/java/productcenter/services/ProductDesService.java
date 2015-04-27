package productcenter.services;

import common.services.GeneralDao;
import common.utils.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productcenter.models.Html;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 产品Service
 * User: lidujun
 * Date: 2015-04-23
 */
@Service
@Transactional(readOnly = true)
public class ProductDesService {

    @Autowired
    GeneralDao generalDao;

    /**
     * 获取所有商品html描述信息
     * @return
     */
    public List<Html> queryAllHtmlDes() {
        play.Logger.info("------ProductDesService queryAllHtml begin exe-----------");

        String jpql = "select o from Html o where 1=1";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " order by o.name";
        return generalDao.query(jpql, Optional.<Page<Html>>empty(), queryParams);
    }

    public Html getHtmlDesByProductId(int productId) {
        play.Logger.info("------ProductDesService getByProductId begin exe-----------" + productId);

        String jpql = "select o from Html o where 1=1 and o.productId=:productId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("productId", productId);
        jpql += " order by o.name";

        List<Html> list = generalDao.query(jpql, Optional.ofNullable(null), queryParams);
        Html html = null;
        if(list != null && list.size() > 0) {
            html = list.get(0);
        }
        return html;
    }

    public List<Html> queryHtmlDesByProductId(int productId) {
        play.Logger.info("------ProductDesService queryByProductId begin exe-----------" + productId);

        String jpql = "select o from Html o where 1=1 and o.productId=:productId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("productId", productId);
        jpql += " order by o.name";

        return generalDao.query(jpql, Optional.ofNullable(null), queryParams);
    }
}
