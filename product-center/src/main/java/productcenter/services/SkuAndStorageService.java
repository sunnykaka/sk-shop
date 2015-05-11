package productcenter.services;

import common.services.GeneralDao;
import common.utils.page.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productcenter.models.Product;
import productcenter.models.SkuProperty;
import productcenter.models.SkuStorage;
import productcenter.models.StockKeepingUnit;
import productcenter.util.PropertyValueUtil;

import java.util.*;

/**
 * Sku和库存Service
 * User: lidujun
 * Date: 2015-04-27
 */
@Service
@Transactional(readOnly = true)
public class SkuAndStorageService {

    @Autowired
    GeneralDao generalDao;

    @Autowired
    ProductService productService;

    /**
     * 将sku数据库中的字符串pidvid列表转换为Sku属性的List
     * @param stockKeepingUnit
     */
    private void loadSkuProperty(StockKeepingUnit stockKeepingUnit) {
        List<SkuProperty> skuProperties = new ArrayList<SkuProperty>();
        String skuPropertiesInDb = stockKeepingUnit.getSkuPropertiesInDb(); //把数据库中的字符串pidvid列表转换为Sku属性的List
        if (StringUtils.isNotEmpty(skuPropertiesInDb)) {
            String[] pidvid = skuPropertiesInDb.split(",");
            for (String s : pidvid) {
                PropertyValueUtil.PV pv = PropertyValueUtil.parseLongToPidVid(Long.valueOf(s));
                SkuProperty skuProperty = new SkuProperty();
                skuProperty.setSkuId(stockKeepingUnit.getId());
                skuProperty.setPropertyId(pv.pid);
                skuProperty.setValueId(pv.vid);
                skuProperties.add(skuProperty);
            }
        }
        stockKeepingUnit.setSkuProperties(skuProperties);
    }

    /**
     * 获取数据库中所有sku记录(包含sku属性)
     * @return
     */
    public List<StockKeepingUnit> queryAllStockKeepingUnits() {
        List<StockKeepingUnit> skus = generalDao.findAll(StockKeepingUnit.class);
        for (StockKeepingUnit sku : skus) {
            loadSkuProperty(sku);
        }
        return skus;
    }

    /**
     * 按照sku主键获取sku(包含sku属性)
     * @param id
     * @return
     */
    public StockKeepingUnit getStockKeepingUnitById(int id) {
        play.Logger.info("------SkuAndStorageService getStockKeepingUnitById begin exe-----------" + id);
        StockKeepingUnit sku = generalDao.get(StockKeepingUnit.class, id);
        if(sku != null) {
            loadSkuProperty(sku);
        }
        return sku;
    }

    /**
     * 通过sku条码获取sku记录(包含sku属性)
     * @param barCode
     * @return
     */
    public StockKeepingUnit querySKUByBarCode(String barCode) {
        play.Logger.info("------SkuAndStorageService querySKUByBarCode begin exe-----------" + barCode);

        String jpql = "select o from StockKeepingUnit o where 1=1 and o.barCode=:barCode";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("barCode", barCode);

        List<StockKeepingUnit> list = generalDao.query(jpql, Optional.ofNullable(null), queryParams);
        StockKeepingUnit stockKeepingUnit = null;
        if(list != null && list.size() > 0) {
            stockKeepingUnit = list.get(0);
        }
        if(stockKeepingUnit != null) {
            loadSkuProperty(stockKeepingUnit);
        }
        return stockKeepingUnit;
    }

    /**
     * 判断是否可以添加sku
     * @param skuId
     * @return
     */
    public boolean isSkuUsable(int skuId) {
        boolean flag = false;
        //sku所属商品是否下架
        StockKeepingUnit sku = this.getStockKeepingUnitById(skuId);
        if (sku != null && sku.canBuy()) {
            Product product = productService.getProductById(sku.getProductId());
            if (!product.getIsDelete() && product.isOnline()) {
                flag = true;
            }
        }
        //sku是否有库存
        if (flag) {
            SkuStorage skuStorage = this.getSkuStorage(skuId);
            if (skuStorage != null) {
                flag = skuStorage.getStockQuantity() > 0;
            } else {
                flag = false;
            }
        }
        return flag;
    }

    /**
     * 通过产品（商品）id获取sku列表(包含sku属性)
     * @param productId
     * @return
     */
    public List<StockKeepingUnit> querySkuListByProductId(int productId) {
        play.Logger.info("------SkuAndStorageService querySKUByProductId begin exe-----------" + productId);

        String jpql = "select o from StockKeepingUnit o where 1=1 and o.productId=:productId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("productId", productId);
        List<StockKeepingUnit> skus = generalDao.query(jpql, Optional.<Page<StockKeepingUnit>>empty(), queryParams);

        for (StockKeepingUnit sku : skus) {
            loadSkuProperty(sku);
        }
        return skus;
    }

    /**
     * 通过skuId获取sku对应库存
     * @param skuId
     * @return
     */
    public SkuStorage getSkuStorage(int skuId) {
        play.Logger.info("------SkuAndStorageService querySKUByBarCode begin exe-----------" + skuId);

        String jpql = "select o from SkuStorage o where 1=1 and o.skuId=:skuId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("skuId", skuId);

        List<SkuStorage> list = generalDao.query(jpql, Optional.ofNullable(null), queryParams);
        SkuStorage skuStorage = null;
        if(list != null && list.size() > 0) {
            skuStorage = list.get(0);
        }
        return skuStorage;
    }

}
