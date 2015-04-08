package productcenter.services;

import common.services.GeneralDao;
import common.utils.page.Page;
import productcenter.models.Sku;
import org.apache.commons.lang3.StringUtils;
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
    @Autowired
    private GeneralDao generalDao;

    /**
     * 保存单品SKU
     */
    public void save(Sku sku){
        play.Logger.info("--------SkuService save begin exe-----------" + sku);
        generalDao.persist(sku);
    }

    /**
     * 删除单品SKU
     */
    public void realDelete(Integer skuId){
        play.Logger.info("--------SkuService realDelete begin exe-----------" + skuId);
        generalDao.removeById(Sku.class, skuId);
    }

    /**
     * 更新单品SKU
     */
    public void update(Sku skuId){
        play.Logger.info("--------SkuService update begin exe-----------" + skuId);
        generalDao.merge(skuId);
    }

    /**
     * 通过主键获取单品SKU
     */
    @Transactional(readOnly = true)
    public Optional<Sku> getSkuById(Integer skuId){
        play.Logger.info("--------SkuService getSKuById begin exe-----------" + skuId);
        return Optional.ofNullable(generalDao.get(Sku.class, skuId));
    }

    /**
     * 通过编号获取单品SKU
     */
    @Transactional(readOnly = true)
    public Optional<Sku> getSkuBySkuCode(String skuCode){
        play.Logger.info("--------SkuService getSKuById begin exe-----------" + skuCode);

        String jpql = "select o from Sku o where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and o.skuCode = :skuCode ";
        queryParams.put("skuCode", skuCode);

        List<Sku> itemList = generalDao.query(jpql, Optional.ofNullable(null), queryParams);
        if(itemList != null && itemList.size() > 0) {
            return Optional.ofNullable(itemList.get(0));
        }
        return Optional.ofNullable(null);
    }

    /**
     * 获取单品SKU列表
     */
    @Transactional(readOnly = true)
    public List<Sku> getSkuList(Optional<Page<Sku>> page, Sku param){
        play.Logger.info("--------SkuService getSkuList begin exe-----------" + page + "\n" + param);

        String jpql = "select o from Sku o where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();

        if(param != null) {
            Integer productId = param.getProductId();
            if(productId != null && productId != 0) {
                jpql += " and o.productId = :productId ";
                queryParams.put("productId", productId);
            }
            Boolean online = param.getOnline();
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

        jpql += " order by o.id";
        return generalDao.query(jpql, page, queryParams);
    }

}
