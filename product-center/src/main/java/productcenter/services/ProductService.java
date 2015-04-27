package productcenter.services;

import common.services.GeneralDao;
import common.utils.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productcenter.models.Product;

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
     * 获取所有没有被删除的产品
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
     * @param id
     * @return
     */
    public Product getProductById(int id) {
        play.Logger.info("--------ProductService getProductById begin exe-----------" + id);
        return generalDao.get(Product.class, id);
    }

    /**
     * 通过后台类目id获取其下关联的所有产品，不包括已经删除的产品
     * @param categoryId
     * @return
     */
    public List<Product> queryProductsByCategoryId(int categoryId) {
        play.Logger.info("--------ProductService queryProductsByCategoryId begin exe-----------");

        String jpql = "select o from Product o where 1=1 and o.isDelete=:isDelete and categoryId=:categoryId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("isDelete", false);
        queryParams.put("categoryId", categoryId);
        jpql += " order by o.name";
        return generalDao.query(jpql, Optional.<Page<Product>>empty(), queryParams);
    }

    /**
     * 按照条件获取产品（商品）分页列表，默认是未上架
     */
    public List<Product> getProductList(Optional<Page<Product>> page, Product param){
        play.Logger.info("--------ProductService getOrderList begin exe-----------" + page + "\n" + param);

        String jpql = "select o from Product o where 1=1 and o.isDelete=:isDelete";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("isDelete", false);
        if(param != null) {
            Integer categoryId = param.getCategoryId();
            if(categoryId != null && categoryId != 0) {
                jpql += " and o.categoryId = :categoryId ";
                queryParams.put("categoryId", categoryId);
            }

            Integer customerId = param.getCustomerId();
            if(customerId != null && customerId != 0) {
                jpql += " and o.customerId = :customerId ";
                queryParams.put("customerId", customerId);
            }

            Integer brandId = param.getBrandId();
            if(brandId != null && brandId != 0) {
                jpql += " and o.brandId = :brandId ";
                queryParams.put("brandId", brandId);
            }

            //默认是未上架
            jpql += " and o.online = :online ";
            queryParams.put("online", param.isOnline());
        }
        jpql += " order by o.name";
        return generalDao.query(jpql, page, queryParams);
    }
}
