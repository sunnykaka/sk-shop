package productcenter.services;

import common.services.GeneralDao;
import common.utils.SQLUtils;
import common.utils.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productcenter.models.ProductCollect;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by zhb on 15-4-24.
 */
@Service
@Transactional
public class ProductCollectService {



    @PersistenceContext
    private EntityManager em;

    @Autowired
    private GeneralDao generalDAO;

    /**
     * 新建
     *
     */
    public void createProductCollect(ProductCollect productCollect){
        generalDAO.persist(productCollect);
    }

    /**
     * 更新
     *
     * @param productCollect
     * @return
     */
    public ProductCollect updateProductCollect(ProductCollect productCollect){
        return generalDAO.merge(productCollect);
    }

    /**
     * 根据Id软删除
     *
     * @param id
     */
    public void deleteProductCollect(int id){

        ProductCollect productCollect = generalDAO.get(ProductCollect.class,id);
        if(null != productCollect){
            productCollect.setDeleted(SQLUtils.SQL_DELETE_TRUE);
        }

        updateProductCollect(productCollect);

    }

    /**
     * 删除我的收藏(软删)
     *
     * @param productId
     * @param userId
     */
    public void deleteMyProductCollect(int productId,int userId){

        ProductCollect productCollect = getByProductId(productId, userId);
        productCollect.setDeleted(SQLUtils.SQL_DELETE_TRUE);
        updateProductCollect(productCollect);

    }

    /**
     * 根据Id查找
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public ProductCollect getById(int id){
        return generalDAO.get(ProductCollect.class,id);
    }

    /**
     * 查询我的收藏记录
     *
     * @param productId
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public ProductCollect getByProductId(int productId,int userId){

        String jpql = "select pc from ProductCollect pc where 1=1 and pc.deleted=false ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and pc.productId = :productId ";
        queryParams.put("productId", productId);

        jpql += " and pc.userId = :userId ";
        queryParams.put("userId", userId);

        List<ProductCollect> valueList = generalDAO.query(jpql, Optional.ofNullable(null), queryParams);
        if (valueList != null && valueList.size() > 0) {
            return valueList.get(0);
        }

        return null;
    }


    /**
     * 根据userId分页查询
     *
     * @param page
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<ProductCollect> getProductCollectList(Optional<Page<ProductCollect>> page, int userId){
        play.Logger.info("--------ProductCollectService getProductCollectList begin exe-----------" + page + "\n" + userId);

        String jpql = "select pc from ProductCollect pc where 1=1 and pc.deleted=false ";
        Map<String, Object> queryParams = new HashMap<>();

        jpql += " and pc.userId = :userId ";
        queryParams.put("userId", userId);

        return generalDAO.query(jpql, page, queryParams);
    }

}
