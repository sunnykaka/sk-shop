package productcenter.services;

import common.services.GeneralDao;
import common.utils.page.Page;
import productcenter.constants.PropertyType;
import productcenter.models.ProductProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 产品（商品）关联属性 Service
 * User: lidujun
 * Date: 2015-04-01
 */
@Service
@Transactional
public class ProductPropertyService {
    @Autowired
    private GeneralDao generalDao;

    /**
     * 保存产品（商品）关联属性
     */
    public void save(ProductProperty property){
        play.Logger.info("--------ProductPropertyService save begin exe-----------" + property);
        generalDao.persist(property);
    }

    /**
     * 删除产品（商品）关联属性
     */
    public void realDelete(Integer propertyId){
        play.Logger.info("--------ProductPropertyService realDelete begin exe-----------" + propertyId);
        generalDao.removeById(ProductProperty.class, propertyId);
    }

    /**
     * 更新产品（商品）关联属性
     */
    public void update(ProductProperty property){
        play.Logger.info("--------ProductPropertyService update begin exe-----------" + property);
        generalDao.merge(property);
    }

    /**
     * 通过主键获取产品（商品）关联属性
     */
    @Transactional(readOnly = true)
    public Optional<ProductProperty> getProductPropertyById(Integer propertyId){
        play.Logger.info("--------ProductPropertyService getProductPropertyById begin exe-----------" + propertyId);
        return Optional.ofNullable(generalDao.get(ProductProperty.class, propertyId));
    }

    /**
     * 获取产品（商品）关联属性列表
     */
    @Transactional(readOnly = true)
    public List<ProductProperty> getProductPropertyList(Optional<Page<ProductProperty>> page, ProductProperty param){
        play.Logger.info("--------ProductPropertyService getProductPropertyList begin exe-----------" + page + "\n" + param);

        String jpql = "select o from ProductProperty o where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();

         if(param != null) {
             Integer productId = param.getProductId();
             if(productId != null && productId != 0) {
                 jpql += " and o.productId = :productId ";
                 queryParams.put("productId", productId);
             }

             PropertyType type = param.getType();
             if(type != null) {
                 jpql += " and o.type = :type ";
                 queryParams.put("type", type);
             }
         }

        jpql += " order by o.id ";
        return generalDao.query(jpql, page, queryParams);
    }

}
