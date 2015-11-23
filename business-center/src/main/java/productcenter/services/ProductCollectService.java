package productcenter.services;

import common.exceptions.AppBusinessException;
import common.services.GeneralDao;
import common.utils.SQLUtils;
import common.utils.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productcenter.models.ProductCollect;
import usercenter.models.User;

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

    @Autowired
    GeneralDao generalDAO;

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
            updateProductCollect(productCollect);
        }

    }

    /**
     * 删除我的收藏(软删)
     *
     * @param productId
     * @param userId
     */
    public void deleteMyProductCollect(int productId,int userId){

        ProductCollect productCollect = getByProductId(productId, userId);

        if(null == productCollect){
            throw new AppBusinessException("删除收藏失败");
        }

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

        List<ProductCollect> valueList = generalDAO.query(jpql, Optional.empty(), queryParams);
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

    /**
     * 是否收藏
     *
     * @param user
     * @param productId
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isFavorites(User user,int productId){
        if(null != user && null != getByProductId(productId,user.getId())){
            return true;
        }
        return false;
    }

    /**
     * 统计收藏的商品
     *
     * @param productId
     * @return
     */
    @Transactional(readOnly = true)
    public int countProductCollect(int productId){

        String jpql = "select pc from ProductCollect pc where 1=1 and pc.deleted=false ";
        Map<String, Object> queryParams = new HashMap<>();

        jpql += " and pc.productId = :productId ";
        queryParams.put("productId", productId);

        return generalDAO.query(jpql, Optional.<Page<ProductCollect>>empty(), queryParams).size();
    }

}
