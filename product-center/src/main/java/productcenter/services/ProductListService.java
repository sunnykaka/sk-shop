package productcenter.services;

import common.services.GeneralDao;
import common.utils.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productcenter.dtos.ProductQueryVO;
import productcenter.models.CategoryAssociation;
import productcenter.models.NavigateCategory;
import productcenter.models.Product;
import productcenter.models.ProductCategory;

import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Created by amoszhou on 15/7/13.
 */
@Service
@Transactional(readOnly = true)
public class ProductListService {

    @Autowired
    private GeneralDao generalDao;


    /**
     * 查询某个父前台类目下的所有子类目
     *
     * @param parentId 父类目id，如果查一级类目，则传入-1
     * @return
     */
    public List<NavigateCategory> navigatesBelow(Integer parentId) {
        String jpql = " select n from NavigateCategory n where n.parentId = :parentId and isDelete = 0  order by n.priority desc ";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("parentId", parentId);
        return generalDao.query(jpql, Optional.<Page<NavigateCategory>>empty(), queryParams);
    }


    /***
     * 根据前台类目查询关联的后台类目
     *
     * @param navegateId
     * @return
     */
    public List<CategoryAssociation> categoriesBelongNavigate(Integer navegateId) {
        String jpql = " select c from CategoryAssociation c where  c.navId = :navId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("navId", navegateId);
        return generalDao.query(jpql, Optional.<Page<CategoryAssociation>>empty(), queryParams);
    }

    /**
     * 根据后台类目id，查询下面所有类目即子类目，最多支持3级查询
     *
     * @param cateIds
     * @return
     */
    public List<ProductCategory> category4AllTree(List<Integer> cateIds) {
        String jpql = " select pc from ProductCategory pc where pc.deleted = 0 and pc.parentId in (:cateIds) or pc.id in (:cateIds)";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("cateIds", cateIds);
        List<ProductCategory> c1 = generalDao.query(jpql, Optional.<Page<ProductCategory>>empty(), queryParams);
        String jpql2 = " select t1 from ProductCategory t1, ProductCategory t2 where t1.parentId = t2.id and t2.parentId in (:cateIds) and t1.deleted = 0";
        List<ProductCategory> c2 = generalDao.query(jpql2, Optional.<Page<ProductCategory>>empty(), queryParams);
        c1.addAll(c2);
        return c1;
    }

    /**
     * 热销商品列表页
     *
     * @param page
     * @return
     */
    public List<Product> productList(Optional<Page<Product>> page, ProductQueryVO query) {
        StringBuilder jpql = new StringBuilder("select o from Product o where o.isDelete= 0 and o.online = 1 ");
        Integer navId = query.getNavigateId();
        Integer storageFilter = query.getSt();
        Map<String, Object> queryParams = new HashMap<>();
        if (storageFilter != null && storageFilter == 1) {
            jpql.append("  and id in ( select distinct sk.productId from StockKeepingUnit sk , SkuStorage ss where sk.id = ss.skuId and sk.skuState= 'NORMAL' and ss.stockQuantity >0 ) ");
        }

        //根据类目查询
        if (navId != null && navId.intValue() != -1) {
            List<CategoryAssociation> associations = categoriesBelongNavigate(navId);
            if (associations == null || associations.size() == 0) {
                return new ArrayList<>();
            }
            List<Integer> catelist = associations.stream().map(ca -> ca.getcId()).collect(toList());
            List<Integer> allCateIds = category4AllTree(catelist).stream().map(ca -> ca.getId()).collect(toList());
            jpql.append(" and o.categoryId in (:cateIds) ");
            queryParams.put("cateIds", allCateIds);
        }
        return generalDao.query(jpql.toString(), page, queryParams);
    }
}
