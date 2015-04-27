package productcenter.services;

import common.services.GeneralDao;
import common.utils.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productcenter.constants.PropertyType;
import productcenter.models.ProductProperty;

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
public class ProductPropertyService {

    @Autowired
    GeneralDao generalDao;

    /**
     * 获取所有产品所有属性
     * @return
     */
    public List<ProductProperty> queryAllProductProperty() {
        return generalDao.findAll(ProductProperty.class);
    }

    /**
     * 通过主键获取属性
     * @param id
     * @return
     */
    public ProductProperty getProductPropertyById(int id) {
        play.Logger.info("------ProductPropertyService ProductProperty begin exe-----------" + id);
        return generalDao.get(ProductProperty.class, id);
    }

    /**
     * 通过产品id获取产品对应的所有属性
     * @param productId
     * @return
     */
    public List<ProductProperty> queryByProductId(int productId) {
        play.Logger.info("------ProductPropertyService queryByProductId begin exe-----------" + productId);

        String jpql = "select o from ProductProperty o where 1=1 and o.productId=:productId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("productId", productId);
        jpql += " order by o.name";
        return generalDao.query(jpql, Optional.<Page<ProductProperty>>empty(), queryParams);
    }

    /**
     * 通过产品id和属性类型获取对应产品的所有对应属性
     * @param productId
     * @param propertyType
     * @return
     */
    public ProductProperty queryProductPropertyByPropertyType(int productId, PropertyType propertyType) {
        play.Logger.info("------ProductPropertyService ProductProperty begin exe-----------" + productId);

        String jpql = "select o from ProductProperty o where 1=1 and o.productId=:productId and o.propertyType=:propertyType";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("productId", productId);
        queryParams.put("propertyType", propertyType);

        List<ProductProperty> list = generalDao.query(jpql, Optional.ofNullable(null), queryParams);
        ProductProperty productProperty = null;
        if(list != null && list.size() > 0) {
            productProperty = list.get(0);
        }
        return productProperty;
    }

}
