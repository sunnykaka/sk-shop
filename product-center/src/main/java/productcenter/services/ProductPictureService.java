package productcenter.services;

import common.services.GeneralDao;
import common.utils.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productcenter.models.ProductPicture;

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
public class ProductPictureService {

    @Autowired
    GeneralDao generalDao;

    /**
     * 获取所有的产品图片
     * @return
     */
    public List<ProductPicture> queryAllProductPictures() {
        return generalDao.findAll(ProductPicture.class);
    }

    /**
     * 通过图片主键获取产品图片
     * @param id
     * @return
     */
    public ProductPicture getProductPictureById(int id) {
        play.Logger.info("------ProductPictureService getProductPictureById begin exe-----------" + id);
        return generalDao.get(ProductPicture.class, id);
    }

    /**
     * 按照产品id获取产品图片
     * @param productId
     * @return
     */
    public List<ProductPicture> queryProductPicturesByProductId(int productId) {
        play.Logger.info("------ProductPictureService queryProductPicturesByProductId begin exe-----------" + productId);

        String jpql = "select o from ProductPicture o where 1=1 and o.productId=:productId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("productId", productId);
        jpql += " order by o.name";
        return generalDao.query(jpql, Optional.<Page<ProductPicture>>empty(), queryParams);
    }

    /**
     * 通过skuId加载产品图片
     * @param skuId
     * @return
     */
    public List<ProductPicture> queryProductPicturesBySkuId(String skuId) {
        play.Logger.info("------ProductPictureService queryProductPicturesBySkuId begin exe-----------" + skuId);

        String jpql = "select o from ProductPicture o where 1=1 and o.skuId=:skuId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("skuId", skuId);
        jpql += " order by o.name";
        return generalDao.query(jpql, Optional.<Page<ProductPicture>>empty(), queryParams);
    }

    public ProductPicture getMainProductPictureByProductId(int productId) {
        play.Logger.info("------ProductPictureService getMainProductPictureByProductId begin exe-----------" + productId);

        String jpql = "select o from ProductPicture o where 1=1 and o.mainPic=1 and o.productId=:productId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("productId", productId);

        List<ProductPicture> list = generalDao.query(jpql, Optional.ofNullable(null), queryParams);
        if(list != null && list.size() > 0) {
            return list.get(0);
        }else{
            return new ProductPicture(ProductPicture.PRODUCT_DEFAULT_PIC);
        }
    }


    public ProductPicture getMainProductPictureByProductIdSKuId(int productId, int skuId) {
        play.Logger.info("------ProductPictureService getMainProductPictureByProductIdSKuId begin exe-----------" + productId + " : " + skuId);

        String jpql = "select o from ProductPicture o where 1=1 and o.mainPic=1 and o.productId=:productId and o.skuId=:skuId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("productId", productId);
        queryParams.put("skuId", skuId + "");

        List<ProductPicture> list = generalDao.query(jpql, Optional.ofNullable(null), queryParams);
        if(list != null && list.size() > 0) {
            return list.get(0);
        }else{
            return new ProductPicture(ProductPicture.PRODUCT_DEFAULT_PIC);
        }
    }

    public ProductPicture getMinorProductPictureByProductId(int productId) {
        play.Logger.info("------ProductPictureService getMinorProductPictureByProductId begin exe-----------" + productId);

        String jpql = "select o from ProductPicture o where 1=1 and o.minorPic=1 and o.productId=:productId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("productId", productId);

        List<ProductPicture> list = generalDao.query(jpql, Optional.ofNullable(null), queryParams);
        ProductPicture productPicture = null;
        if(list != null && list.size() > 0) {
            productPicture = list.get(0);
        }
        return productPicture;
    }
}
