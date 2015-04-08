package services;

import common.services.GeneralDao;
import common.utils.page.Page;
import models.Product;
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
 * 产品（商品）Service
 * User: lidujun
 * Date: 2015-04-01
 */
@Service
@Transactional
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private GeneralDao generalDao;

    /**
     * 保存产品（商品）
     */
    public void save(Product product){
        log.info("--------ProductService save begin exe-----------" + product);
        generalDao.persist(product);
    }

    /**
     * 假删除产品（商品）
     */
    public void falseDelete(Integer productId){
        log.info("--------ProductService falseDelete begin exe-----------" + productId);
        Product product = generalDao.get(Product.class, productId);
        product.setIsDelete(false);
        this.update(product);
    }

    /**
     * 更新产品（商品）
     */
    public void update(Product product){
        log.info("--------ProductService update begin exe-----------" + product);
        generalDao.merge(product);
    }

    /**
     * 通过主键获取产品（商品）
     */
    @Transactional(readOnly = true)
    public Product getProductById(Integer productId){
        log.info("--------ProductContentService getProductById begin exe-----------" + productId);
        return generalDao.get(Product.class, productId);
    }

    /**
     * 获取产品（商品）列表
     */
    @Transactional(readOnly = true)
    public List<Product> getProductList(Optional<Page<Product>> page, Product param){
        log.info("--------ProductService getOrderList begin exe-----------" + page + "\n" + param);

        String jpql = "select o from Product o where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();

         if(param != null) {
             String name = param.getName();
             if(!StringUtils.isEmpty(name)) {
                 jpql += " and o.name like :name ";
                 queryParams.put("name", name);
             }

             Integer brandId = param.getBrandId();
             if(brandId != null && brandId != 0) {
                 jpql += " and o.brandId = :brandId ";
                 queryParams.put("brandId", brandId);
             }

             String supplierSpuCode = param.getSupplierSpuCode();
             if(!StringUtils.isEmpty(supplierSpuCode)) {
                 jpql += " and o.supplierSpuCode = :supplierSpuCode ";
                 queryParams.put("supplierSpuCode", supplierSpuCode);
             }

             String spuCode = param.getSpuCode();
             if(!StringUtils.isEmpty(spuCode)) {
                 jpql += " and o.spuCode = :spuCode ";
                 queryParams.put("spuCode", spuCode);
             }

             Integer categoryId = param.getCategoryId();
             if(categoryId != null && categoryId != 0) {
                 jpql += " and o.categoryId = :categoryId ";
                 queryParams.put("categoryId", categoryId);
             }

             String address = param.getAddress();
             if(!StringUtils.isEmpty(address)) {
                 jpql += " and o.address like :address ";
                 queryParams.put("address", address);
             }

             Boolean online = param.getOnline();
             if(online != null) {
                 jpql += " and o.online = :online ";
                 queryParams.put("online", online);
             }

             Boolean isDelete = param.getIsDelete();
             if(isDelete != null) {
                 jpql += " and o.isDelete = :isDelete ";
                 queryParams.put("isDelete", isDelete);
             }
         }
        jpql += " group by o.name";
        return generalDao.query(jpql, page, queryParams);
    }

}
