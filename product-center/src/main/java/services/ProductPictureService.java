package services;

import common.services.GeneralDao;
import common.utils.page.Page;
import models.ProductPicture;
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
 * 产品（商品）图片Service
 * User: lidujun
 * Date: 2015-04-02
 */
@Service
@Transactional
public class ProductPictureService {
    private static final Logger log = LoggerFactory.getLogger(ProductPictureService.class);

    @Autowired
    GeneralDao generalDao;

    /**
     * 保存产品（商品）图片
     */
    public void save(ProductPicture picture){
        log.info("--------ProductPictureService save begin exe-----------" + picture);
        generalDao.persist(picture);
    }

    /**
     * 删除产品（商品）图片
     */
    public void realDelete(Integer pictureId){
        log.info("--------ProductPictureService realDelete begin exe-----------" + pictureId);
        generalDao.removeById(ProductPicture.class, pictureId);
    }

    /**
     * 更新产品（商品）图片
     */
    public void update(ProductPicture picture){
        log.info("--------ProductPictureService update begin exe-----------" + picture);
        generalDao.merge(picture);
    }

    /**
     * 通过主键获取产品（商品）图片
     */
    @Transactional(readOnly = true)
    public ProductPicture getProductPictureById(Integer pictureId){
        log.info("--------ProductPictureService getProductPictureById begin exe-----------" + pictureId);
        return generalDao.get(ProductPicture.class, pictureId);
    }

    /**
     * 获取产品（商品）图片列表
     */
    @Transactional(readOnly = true)
    public List<ProductPicture> getProductPictureList(Optional<Page<ProductPicture>> page, ProductPicture param){
        log.info("--------ProductPictureService getProductPictureList begin exe-----------" + page + "\n" + param);

        String jpql = "select o from ProductPicture o where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();

        if(param != null) {
            Integer productId = param.getProductId();
            if(productId != null && productId != 0) {
                jpql += " and o.productId = :productId ";
                queryParams.put("productId", productId);
            }

            Integer skuId = param.getSkuId();
            if(skuId != null && skuId != 0) {
                jpql += " and o.skuId = :skuId ";
                queryParams.put("skuId", skuId);
            }

            String originalName = param.getOriginalName();
            if(!StringUtils.isEmpty(originalName)) {
                jpql += " and o.originalName like :originalName ";
                queryParams.put("originalName", originalName);
            }

            String name = param.getName();
            if(!StringUtils.isEmpty(name)) {
                jpql += " and o.name like :name ";
                queryParams.put("name", name);
            }

            String picUrl = param.getPicUrl();
            if(!StringUtils.isEmpty(picUrl)) {
                jpql += " and o.picUrl = :picUrl ";
                queryParams.put("picUrl", picUrl);
            }

            String type = param.getType();
            if(!StringUtils.isEmpty(type)) {
                jpql += " and o.type = :type ";
                queryParams.put("type", type);
            }
        }
        jpql += " group by o.id ";
        return generalDao.query(jpql, page, queryParams);
    }

}
