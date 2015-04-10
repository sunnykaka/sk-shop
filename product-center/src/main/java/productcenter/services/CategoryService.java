package productcenter.services;

import common.services.GeneralDao;
import productcenter.models.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * 类目操作
 *
 * Created by zhb on 15-4-2.
 */
@Service
public class CategoryService {

    @PersistenceContext
    EntityManager em;

    @Autowired
    GeneralDao generalDao;

    /**
     * 增加后台类目
     *
     * @param category
     * @return
     */
    @Transactional
    public Category saveOrUpdate(Category category) {
        if(null == category){
            return null;
        }

        if(null != category.getId() && category.getId() > 0){
            System.out.println("1111");
            return generalDao.merge(category);//更新
        }else{
            if (category.getParentId() == null || category.getParentId() == 0) {
                category.setParentId(category.PARENT_DEFAULT);
            }
            generalDao.persist(category);//添加
            return category;
        }

    }

    /**
     * 删除后台类目
     *
     * @param category
     * @return
     */
    @Transactional
    public boolean delete(Category category) {

        return generalDao.removeById(Category.class,category.getId());

    }

    /**
     * 根据Id查找
     *
     * @param id
     * @return
     */
    public Category getCategorybyId(int id){
        return generalDao.get(Category.class,id);
    }


    /**
     * 根据父ID，查找下级类目
     *
     * @param parentId
     * @return
     */
    @Transactional(readOnly = true)
    public List<Category> getCategorybyParentId(int parentId) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Category> cq = cb.createQuery(Category.class);
        Root<Category> category = cq.from(Category.class);

        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(cb.equal(category.get("parentId"), parentId));

        cq.select(category).where(predicateList.toArray(new Predicate[predicateList.size()]));

        TypedQuery<Category> query = em.createQuery(cq);

        return query.getResultList();

    }

    /**
     * 查询所有后台一级类目
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<Category> findFatherCategorys() {

        return getCategorybyParentId(Category.PARENT_DEFAULT);

    }

    /**
     * 返回后台类目结构
     *
     * @return
     */
//    public List<CategoryTree> getAllCategory(List<CategoryTree> treeList,int categoryId,int len) {
//        if(len <= 0){
//            return treeList;
//        }
//
//        Search search = new Search(Category.class);
//        search.addFilterEqual("parentId", categoryId);
//        List<Category> categoryFatherList = generalDAO.search(search);//一级类目
//        len--;
//        for (Category categoryFather : categoryFatherList) {
//            List<Category> categoryChildrenList = getCategorybyParentId(categoryFather.getParentId());//二级类目
//
//            List<CategoryTree> treeFatherList = new ArrayList<>();
//            for (Category categoryChildren : categoryChildrenList) {
//                List<Category> categoryList = getCategorybyParentId(categoryChildren.getParentId());//三级类目
//
//                List<CategoryTree> treeChildrenList = new ArrayList<>();
//                for (Category category : categoryList) {
//                    treeChildrenList.add(new CategoryTree(category.getId(), category.getName(), false, null));
//                }
//
//                treeFatherList.add(new CategoryTree(categoryChildren.getId(), categoryChildren.getName(), true, treeChildrenList));
//            }
//
//            treeList.add(new CategoryTree(categoryFather.getId(), categoryFather.getName(), true, getAllCategory(treeList,categoryFather.getParentId(),len)));
//
//        }
//
//        return treeList;
//    }




}
