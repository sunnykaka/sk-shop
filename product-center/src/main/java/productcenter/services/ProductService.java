package productcenter.services;

import common.services.GeneralDao;
import common.utils.Money;
import common.utils.page.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productcenter.constants.ProductTagType;
import productcenter.constants.SaleStatus;
import productcenter.constants.StoreStrategy;
import productcenter.models.Html;
import productcenter.models.Product;
import productcenter.models.ProductSpec;

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
public class ProductService {

    @Autowired
    GeneralDao generalDao;

    /**
     * 获取所有产品，不包括已经删除的产品
     *
     * @return
     */
    public List<Product> queryAllProducts() {
        play.Logger.info("--------ProductService queryAllProducts begin exe-----------");

        String jpql = "select o from Product o where 1=1 and o.isDelete=:isDelete";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("isDelete", false);
        jpql += " order by o.name";
        return generalDao.query(jpql, Optional.<Page<Product>>empty(), queryParams);
    }

    /**
     * 通过产品主键id获取产品
     *
     * @param id
     * @return
     */
    public Product getProductById(int id) {
        play.Logger.info("--------ProductService getProductById begin exe-----------" + id);
        return generalDao.get(Product.class, id);
    }


    /**
     * 通过产品code获取产品
     *
     * @param productCode
     * @return
     */
    public Product getProductByProductCode(String productCode) {
        play.Logger.info("--------ProductService getProductByCode begin exe-----------" + productCode);

        String jpql = "select o from Product o where 1=1 and productCode=:productCode";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("productCode", productCode);

        List<Product> list = generalDao.query(jpql, Optional.ofNullable(null), queryParams);
        Product product = null;
        if (list != null && list.size() > 0) {
            product = list.get(0);
        }
        return product;
    }

    /**
     * 按照条件获取产品（商品）分页列表，默认是未上架(返回的列表数据在page中也保存了一份，在实际使用中可以只使用page对象即可)，不包括已经删除的产品
     * 可以按照name和enName进行模糊查询
     * 可以传入categoryId、客户（设计师）customerId、品牌brandId、库存策略StoreStrategy、该商品是否上架online、产品标签ProductTagType
     */
    public List<Product> queryProductPageListWithPage(Optional<Page<Product>> page, Product param) {
        play.Logger.info("--------ProductService getOrderList begin exe-----------" + page + "\n" + param);

        String jpql = "select o from Product o where 1=1 and o.isDelete=:isDelete";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("isDelete", false);

        if (param != null) {
            //名称
            String name = param.getName();
            if (StringUtils.isNotEmpty(name)) {
                jpql += " and o.name like :name ";
                queryParams.put("name", "%" + name + "%");
            }

            //英文名称
            String enName = param.getEnName();
            if (StringUtils.isNotEmpty(enName)) {
                jpql += " and o.enName like :enName ";
                queryParams.put("enName", "%" + enName + "%");
            }

            //后台目录
            Integer categoryId = param.getCategoryId();
            if (categoryId != null && categoryId != 0) {
                jpql += " and o.categoryId = :categoryId ";
                queryParams.put("categoryId", categoryId);
            }

            //客户（设计师）id
            Integer customerId = param.getCustomerId();
            if (customerId != null && customerId != 0) {
                jpql += " and o.customerId = :customerId ";
                queryParams.put("customerId", customerId);
            }

            //品牌
            Integer brandId = param.getBrandId();
            if (brandId != null && brandId != 0) {
                jpql += " and o.brandId = :brandId ";
                queryParams.put("brandId", brandId);
            }

            //库存策略
            StoreStrategy storeStrategy = param.getStoreStrategy();
            if (storeStrategy != null) {
                jpql += " and o.storeStrategy = :storeStrategy ";
                queryParams.put("storeStrategy", storeStrategy);
            }

            //该商品是否上架
            Boolean online = param.isOnline();
            if (online != null) {
                jpql += " and o.online = :online ";
                queryParams.put("online", online);
            }

            //产品标签
            ProductTagType tagType = param.getTagType();
            if (tagType != null) {
                jpql += " and o.tagType = :tagType ";
                queryParams.put("tagType", tagType);
            }

        }
        jpql += " order by o.name";
        return generalDao.query(jpql, page, queryParams);
    }

    /**
     * 查询商品描述
     *
     * @param productId
     * @return
     */
    public List<Html> queryHtmlByProductId(Integer productId) {

        String jpql = "select h from Html h where h.productId=:productId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("productId", productId);

        List<Html> list = generalDao.query(jpql, Optional.empty(), queryParams);
        return list;
    }

    /**
     * 查询商品说明
     *
     * @param productId
     * @return
     */
    public List<ProductSpec> querySpecByProductId(Integer productId) {

        String jpql = "select ps from ProductSpec ps where ps.productId=:productId order by ps.priority desc ";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("productId", productId);

        return generalDao.query(jpql, Optional.empty(), queryParams);
    }

    /**
     * 查询某个设计师下面所有的商品
     *
     * @param designerId
     * @return
     */
    public List<Product> products4Designer(Integer designerId) {
        String sql = "select * from product where isDelete = 0 and online = 1 and customerId = ?1";
        return generalDao.getEm().createNativeQuery(sql, Product.class).setParameter(1, designerId).getResultList();

    }

    /**
     * 查询商品搭配商品列表
     *
     * @param productId
     * @return
     */
    public List<Product> queryMatchProductList(Integer productId) {
        String sql = "select a.* from product a,RecommendProduct b where a.id=b.recommendProductId and b.productId =? and b.recommendType ='MATCH' and a.isDelete =0 and a.online=1 order by b.priority desc";
        return generalDao.getEm().createNativeQuery(sql, Product.class).setParameter(1, productId).getResultList();
    }

    /**
     * check一个商品是正在首发中
     *
     * @param prodId
     * @return
     */
    @Transactional(readOnly = true)
    public boolean useFirstSellPrice(int prodId) {
        Product product = this.getProductById(prodId);
        return useFirstSellPrice(product);
    }


    /**
     * 是否采用首发价
     * @param product
     * @return
     */
    private boolean useFirstSellPrice(Product product) {
        if (product == null) {
            return false;
        }
        String saleStatus = product.getSaleStatus();
        return saleStatus.equals(SaleStatus.PLANSELL.toString()) || saleStatus.equals(SaleStatus.FIRSTSELL.toString()) || saleStatus.equals(SaleStatus.PRESELL.toString());
    }


}
