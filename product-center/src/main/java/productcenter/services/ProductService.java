package productcenter.services;

import common.services.GeneralDao;
import common.utils.page.Page;
import productcenter.models.Product;
import org.apache.commons.lang3.StringUtils;
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
    @Autowired
    private GeneralDao generalDao;

    /**
     * 保存产品（商品）
     */
    public void save(Product product){
        play.Logger.info("--------ProductService save begin exe-----------" + product);
        generalDao.persist(product);
    }

    /**
     * 假删除产品（商品）
     */
    public void falseDelete(Integer productId){
        play.Logger.info("--------ProductService falseDelete begin exe-----------" + productId);
        Product product = generalDao.get(Product.class, productId);
        product.setIsDelete(false);
        this.update(product);
    }

    /**
     * 更新产品（商品）
     */
    public void update(Product product){
        play.Logger.info("--------ProductService update begin exe-----------" + product);
        generalDao.merge(product);
    }

    /**
     * 通过主键获取产品（商品）
     */
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(int productId){
        play.Logger.info("--------ProductContentService getProductById begin exe-----------" + productId);
        return Optional.ofNullable(generalDao.get(Product.class, productId));
    }

    /**
     * 获取产品（商品）列表
     */
    @Transactional(readOnly = true)
    public List<Product> getProductList(Optional<Page<Product>> page, Product param){
        play.Logger.info("--------ProductService getOrderList begin exe-----------" + page + "\n" + param);

        String jpql = "select o from Product o where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();

         if(param != null) {
             String name = param.getName();
             if(!StringUtils.isEmpty(name)) {
                 jpql += " and o.name like :name ";
                 queryParams.put("name", "%" + name + "%");
             }

             Integer desigerId = param.getDesigerId();
             if(desigerId != null && desigerId != 0) {
                 jpql += " and o.desigerId = :desigerId ";
                 queryParams.put("desigerId", desigerId);
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
                 queryParams.put("address", "%" +address + "%");
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
        jpql += " order by o.name";
        return generalDao.query(jpql, page, queryParams);
    }

}
