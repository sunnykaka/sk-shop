package productcenter.services;

import common.services.GeneralDao;
import common.utils.page.Page;
import productcenter.models.ProductTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 产品（商品）标签 Service
 * User: lidujun
 * Date: 2015-04-01
 */
@Service
@Transactional
public class ProductTagService {
    @Autowired
    private GeneralDao generalDao;

    /**
     * 保存产品（商品）标签
     */
    public void save(ProductTag tag){
        play.Logger.info("--------ProductTagService save begin exe-----------" + tag);
        generalDao.persist(tag);
    }

    /**
     * 删除产品（商品）标签
     */
    public void realDelete(Integer tagId){
        play.Logger.info("--------ProductTagService realDelete begin exe-----------" + tagId);
        generalDao.removeById(ProductTag.class, tagId);
    }

    /**
     * 更新产品（商品）标签
     */
    public void update(ProductTag tag){
        play.Logger.info("--------ProductTagService update begin exe-----------" + tag);
        generalDao.merge(tag);
    }

    /**
     * 通过主键获取产品（商品）标签
     */
    @Transactional(readOnly = true)
    public Optional<ProductTag> getTagById(Integer tagId){
        play.Logger.info("--------ProductTagService getTagById begin exe-----------" + tagId);
        return Optional.ofNullable(generalDao.get(ProductTag.class, tagId));
    }

    /**
     * 获取产品（商品）标签
     */
    @Transactional(readOnly = true)
    public List<ProductTag> getTagList(Optional<Page<ProductTag>> page, ProductTag param){
        play.Logger.info("--------ProductTagService getTagList begin exe-----------" + page + "\n" + param);

        String jpql = "select o from ProductTag o where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();

         if(param != null) {
             Integer tagId = param.getTagId();
             if(tagId != null && tagId != 0) {
                 jpql += " and o.tagId = :tagId ";
                 queryParams.put("tagId", tagId);
             }

             Integer productId = param.getProductId();
             if(productId != null && productId != 0) {
                 jpql += " and o.productId = :productId ";
                 queryParams.put("productId", productId);
             }

         }
        jpql += " order by o.id";
        return generalDao.query(jpql, page, queryParams);
    }

}
