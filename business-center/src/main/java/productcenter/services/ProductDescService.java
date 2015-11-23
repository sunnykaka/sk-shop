package productcenter.services;

import common.services.GeneralDao;
import common.utils.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productcenter.models.ProductDesc;

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
public class ProductDescService {

    @Autowired
    GeneralDao generalDao;

    /**
     * 获取所有商品html描述信息
     * @return
     */
    public List<ProductDesc> queryAllHtmlDesc() {
        play.Logger.info("------ProductDesService queryAllHtml begin exe-----------");

        String jpql = "select o from ProductDesc o where 1=1";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " order by o.name";
        return generalDao.query(jpql, Optional.<Page<ProductDesc>>empty(), queryParams);
    }

    public List<ProductDesc> queryHtmlDescByProductId(int productId) {
        play.Logger.info("------ProductDesService queryByProductId begin exe-----------" + productId);

        String jpql = "select o from ProductDesc o where 1=1 and o.productId=:productId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("productId", productId);
        jpql += " order by o.name";

        return generalDao.query(jpql, Optional.ofNullable(null), queryParams);
    }
}
