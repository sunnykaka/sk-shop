package productcenter.services;

import common.services.GeneralDao;
import common.utils.Money;
import common.utils.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productcenter.models.*;
import productcenter.util.Descartes;
import productcenter.util.PidVid;
import productcenter.util.SkuUtil;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Sku和库存Service
 * User: lidujun
 * Date: 2015-04-27
 */
@Service
@Transactional(readOnly = true)
public class SkuAndStorageService {

    @Autowired
    private GeneralDao generalDao;

    @Autowired
    private ProductService productService;

    @Autowired
    private PropertyAndValueService propertyAndValueService;

    /**
     * 获取数据库中所有sku记录(包含sku属性)
     *
     * @return
     */
    public List<StockKeepingUnit> queryAllStockKeepingUnits() {
        List<StockKeepingUnit> skus = generalDao.findAll(StockKeepingUnit.class);
        return skus;
    }

    /**
     * 按照sku主键获取sku(包含sku属性)
     *
     * @param id
     * @return
     */
    public StockKeepingUnit getStockKeepingUnitById(int id) {
        play.Logger.info("------SkuAndStorageService getStockKeepingUnitById begin exe-----------" + id);
        StockKeepingUnit sku = generalDao.get(StockKeepingUnit.class, id);
        return sku;
    }

    /**
     * 通过sku条码获取sku记录(包含sku属性)
     *
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
        if (list != null && list.size() > 0) {
            stockKeepingUnit = list.get(0);
        }
        return stockKeepingUnit;
    }

    /**
     * 判断是否可以添加sku
     *
     * @param skuId
     * @return
     */
    public boolean isSkuUsable(int skuId) {
        boolean flag = false;
        //sku所属商品是否下架
        StockKeepingUnit sku = this.getStockKeepingUnitById(skuId);
        if (sku != null && sku.canBuy()) {
            Product product = productService.getProductById(sku.getProductId());
            if ((!product.getIsDelete()) && product.isOnline()) {
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
     *
     * @param productId
     * @return
     */
    public List<StockKeepingUnit> querySkuListByProductId(int productId) {
        play.Logger.info("------SkuAndStorageService querySKUByProductId begin exe-----------" + productId);

        String jpql = "select o from StockKeepingUnit o where 1=1 and o.productId=:productId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("productId", productId);
        List<StockKeepingUnit> skus = generalDao.query(jpql, Optional.<Page<StockKeepingUnit>>empty(), queryParams);

        return skus;
    }

    /**
     * 获取售卖价最低的SKU信息
     *
     * @param productId
     * @return
     */
    public StockKeepingUnit querySkuByProductIdPriceSmall(int productId) {
        play.Logger.info("------SkuAndStorageService querySKUByProductId begin exe-----------" + productId);

        String jpql = "select o from StockKeepingUnit o where 1=1 and o.productId=:productId and o.skuState='NORMAL' order by o.marketPrice asc";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("productId", productId);
        List<StockKeepingUnit> skus = generalDao.query(jpql, Optional.<Page<StockKeepingUnit>>empty(), queryParams);

        StockKeepingUnit stockKeepingUnit = null;
        if (skus != null && skus.size() > 0) {
            stockKeepingUnit = skus.get(0);
        }
        return stockKeepingUnit;
    }

    /**
     * 产生SKU属性及对应值
     *
     * @param skuId
     */
    public List<SkuProperty> getSKUPropertyValueMap(int skuId) {
        StockKeepingUnit SKU = this.getStockKeepingUnitById(skuId);

        if (null == SKU) {
            return new ArrayList<>();//防止SKU为null引起的异常
        }

        for (SkuProperty p : SKU.getSkuProperties()) {
            Property property = propertyAndValueService.getPropertyById(p.getPropertyId());
            p.setPropertyName(property.getName());
            Value value = propertyAndValueService.getValueById(p.getValueId());
            p.setPropertyValue(value.getValueName());
        }
        return SKU.getSkuProperties();
    }

    ///////////////////////////////////库存操作//////////////////////////////////////////

    /**
     * 通过skuId获取sku对应库存
     *
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
        if (list != null && list.size() > 0) {
            skuStorage = list.get(0);
        }
        return skuStorage;
    }


    /**
     * 查询商品有效SKU的总库存
     *
     * @param prodId
     * @return
     */
    public Long getProductStorage(int prodId) {
        String jpql = "select sum(ss.stockQuantity) from StockKeepingUnit sk , SkuStorage ss where sk.id = ss.skuId and sk.skuState= 'NORMAL' and sk.productId =:productId ";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("productId", prodId);
        List<Long> list = generalDao.query(jpql, Optional.ofNullable(null), queryParams);
        return list.get(0);
    }

    /**
     * 减去sku库存数
     *
     * @param skuId
     * @param number
     * @return
     */
    @Transactional
    public boolean minusSkuStock(int skuId, int number) {
        play.Logger.info("------SkuAndStorageService minusSkuStock begin exe-----------" + skuId + ":" + number);
        EntityManager em = generalDao.getEm();
        Query query = em.createNativeQuery("update SkuStorage set stockQuantity = stockQuantity - ? where skuId=?").setParameter(1, number).setParameter(2, skuId);
        int count = query.executeUpdate();
        return count == 1;
    }

    /**
     * 增加sku库存数
     *
     * @param skuId
     * @param number
     * @return
     */
    @Transactional
    public boolean addSkuStock(int skuId, int number) {
        play.Logger.info("------SkuAndStorageService addSkuStock begin exe-----------" + skuId + ":" + number);
        EntityManager em = generalDao.getEm();
        Query query = em.createNativeQuery("update SkuStorage set stockQuantity = stockQuantity + ? where skuId=?").setParameter(1, number).setParameter(2, skuId);
        int count = query.executeUpdate();
        return count == 1;
    }

    /**
     * 创建SKU用多值pid的笛卡尔积
     * 如果销售属性为空或者是单值，则不用笛卡尔积，sku属性列为空
     * 创建过程中先会删除一次
     *
     * @param sell
     * @param product
     */
    @Transactional
    public List<StockKeepingUnit> createSkuByDescartes(PidVid sell, Product product) {
        deleteSkuByProductId(product.getId());//删除SKU及SKU相应库存
        List<StockKeepingUnit> skuList = new ArrayList<>();
        //销售属性为空，或者没有多值，直接创建SKU，这时属性为空
        if (sell == null || sell.getMultiPidVidMap().isEmpty()) {
            StockKeepingUnit stockKeepingUnit = new StockKeepingUnit();
            stockKeepingUnit.setProductId(product.getId());
            skuList.add(createStockKeepingUnit(product, null));
        } else {
            List<String> skuPropertiesInDbList = parseSkuList(sell);
            skuList.addAll(skuPropertiesInDbList.
                    stream().
                    map(skuPropertiesInDb -> createStockKeepingUnit(product, skuPropertiesInDb)).
                    collect(Collectors.toList()));
        }

        return skuList;
    }

    /**
     * 分析销售属性,得到skuPropertiesInDbList集合
     *
     * @param sell
     * @return
     */
    private List<String> parseSkuList(PidVid sell) {
        Map<Integer, List<Long>> multiPidVidMap = sell.getMultiPidVidMap();
        Descartes descartes = new Descartes();
        //计算笛卡尔积
        multiPidVidMap.values().forEach(descartes::compute);
        List<List<Long>> result = descartes.getResult();
        //新的sku列表
        List<String> skuPropertiesInDbList = new LinkedList<>();
        for (List<Long> pidvidList : result) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pidvidList.size(); i++) {
                Long pidvid = pidvidList.get(i);
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(pidvid);
            }
            skuPropertiesInDbList.add(sb.toString());
        }
        return skuPropertiesInDbList;
    }


    private StockKeepingUnit createStockKeepingUnit(Product product, String skuPropertiesInDb) {
        StockKeepingUnit stockKeepingUnit = new StockKeepingUnit();
        stockKeepingUnit.setProductId(product.getId());
        stockKeepingUnit.setBarCode("");
        stockKeepingUnit.setSkuPropertiesInDb(skuPropertiesInDb);
        generalDao.persist(stockKeepingUnit);
        stockKeepingUnit.setSkuCode(SkuUtil.initSkuCode(product.getCategoryId(), product.getId(), stockKeepingUnit.getId()));
        generalDao.persist(stockKeepingUnit);

        return stockKeepingUnit;
    }

    @Transactional
    public void deleteSkuByProductId(int productId) {
        List<StockKeepingUnit> skuList = querySkuListByProductId(productId);
        for (StockKeepingUnit sku : skuList) {
            SkuStorage skuStorage = getSkuStorage(sku.getId());
            if (skuStorage != null) {
                generalDao.remove(skuStorage);
            }

            generalDao.remove(sku);
        }
    }

    private List<SkuStorage> querySkuStorageByProductId(int productId) {
        String jpql = " select ss from SkuStorage ss join ss.sku sku where sku.productId = :productId";
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        return generalDao.query(jpql, Optional.empty(), params);
    }


    /**
     * 判断当前SKU当前的价格，会根据销售状态，是否首发，预售
     * @param sku
     * @return
     */
    public Money getSkuCurrentPrice(StockKeepingUnit sku) {
        if(null == sku){
            return Money.valueOf(0);
        }

        Integer prodId = sku.getProductId();
        if (productService.useFirstSellPrice(prodId)) {
            return sku.getPrice();
        }
        return sku.getMarketPrice();

    }

}
