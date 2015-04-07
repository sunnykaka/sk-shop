package services;

import common.services.GeneralDao;
import common.utils.page.Page;
import models.Sku;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 单品SKU Service
 * User: lidujun
 * Date: 2015-04-03
 */
@Service
@Transactional
public class SkuService {
    private static final Logger log = LoggerFactory.getLogger(SkuService.class);

    @Autowired
    private GeneralDao generalDao;

    /**
     * 保存单品SKU
     */
    public void save(Sku sku){
        log.info("--------SkuService save begin exe-----------" + sku);
        generalDao.persist(sku);
    }

    /**
     * 删除单品SKU
     */
    public void realDelete(Integer skuId){
        log.info("--------SkuService realDelete begin exe-----------" + skuId);
        generalDao.removeById(Sku.class, skuId);
    }

    /**
     * 更新单品SKU
     */
    public void update(Sku skuId){
        log.info("--------SkuService update begin exe-----------" + skuId);
        generalDao.merge(skuId);
    }

    /**
     * 通过主键获取单品SKU
     */
    @Transactional(readOnly = true)
    public Sku getSkuById(Integer skuId){
        log.info("--------SkuService getSKuById begin exe-----------" + skuId);
        return generalDao.get(Sku.class, skuId);
    }

    /**
     * 通过编号获取单品SKU
     */
    @Transactional(readOnly = true)
    public Sku getSkuBySkuCode(String skuCode){
        log.info("--------SkuService getSKuById begin exe-----------" + skuCode);

        String jpql = "select o from SKU o where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and o.skuCode = :skuCode ";
        queryParams.put("skuCode", skuCode);

        List<Sku> itemList = generalDao.query(jpql, null, queryParams);
        if(itemList != null && itemList.size() > 0) {
            return itemList.get(0);
        }
        return null;
    }

    /**
     * 获取单品SKU列表
     */
    @Transactional(readOnly = true)
    public List<Sku> getSkuList(Optional<Page<Sku>> page, Sku param){
        log.info("--------SkuService getSkuList begin exe-----------" + page + "\n" + param);

        String jpql = "select o from Product o where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();

        if(param != null) {
            Integer productId = param.getProductId();
            if(productId != null && productId != 0) {
                jpql += " and o.productId = :productId ";
                queryParams.put("productId", productId);
            }
            Boolean online = param.isOnline();
            if(online != null) {
                jpql += " and o.online = :online ";
                queryParams.put("online", online);
            }

            String barCode = param.getBarCode();
            if(!StringUtils.isEmpty(barCode)) {
                jpql += " and o.barCode = :barCode ";
                queryParams.put("barCode", barCode);
            }

            String skuCode = param.getSkuCode();
            if(!StringUtils.isEmpty(skuCode)) {
                jpql += " and o.skuCode = :skuCode ";
                queryParams.put("skuCode", skuCode);
            }

        }

        jpql += " group by o.id";
        return generalDao.query(jpql, page, queryParams);
    }

}
