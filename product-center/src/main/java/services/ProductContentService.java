package services;

import common.services.GeneralDao;
import models.ProductContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 产品（商品）内容（详情）Service
 * User: lidujun
 * Date: 2015-04-02
 */
@Service
@Transactional
public class ProductContentService {
    @Autowired
    GeneralDao generalDao;

    /**
     * 保存产品（商品）内容（详情）
     */
    public void save(ProductContent content){
        play.Logger.info("--------ProductContentService save begin exe-----------" + content);
        generalDao.persist(content);
    }

    /**
     * 删除产品（商品）内容（详情）
     */
    public void realDelete(Integer contentId){
        play.Logger.info("--------ProductContentService realDelete begin exe-----------" + contentId);
        generalDao.removeById(ProductContent.class, contentId);
    }

    /**
     * 更新产品（商品）内容（详情）
     */
    public void update(ProductContent content){
        play.Logger.info("--------ProductContentService update begin exe-----------" + content);
        generalDao.merge(content);
    }

    /**
     * 通过主键获取产品（商品）内容（详情）
     */
    @Transactional(readOnly = true)
    public Optional<ProductContent> getProductContentById(Integer contentId){
        play.Logger.info("--------ProductContentService getProductContentById begin exe-----------" + contentId);
        return Optional.ofNullable(generalDao.get(ProductContent.class, contentId));
    }

    /**
     * 通过产品（商品）主键获取产品（商品）内容（详情）
     */
    @Transactional(readOnly = true)
    public Optional<ProductContent> getProductContentByProductId(Integer productId){
        play.Logger.info("--------ProductContentService getProductContentByProductId begin exe-----------" + productId);

        String jpql = "select o from ProductContent o where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and o.productId = :productId ";
        queryParams.put("productId", productId);

        List<ProductContent> itemList = generalDao.query(jpql, Optional.ofNullable(null), queryParams);
        if(itemList != null && itemList.size() > 0) {
            return Optional.ofNullable(itemList.get(0));
        }
        return Optional.ofNullable(null);
    }

}
