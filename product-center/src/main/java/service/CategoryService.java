package service;

import common.services.GeneralDao;
import domain.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * <p/>
 * Created by zhb on 15-4-2.
 */
@Service
@Transactional
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

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
    public void save(Category category) {

        if (null != category && (category.getParentId() == 0 || category.getParentId() == null)) {
            category.setParentId(category.PARENT_DEFAULT);
        }

        generalDao.persist(category);
    }

    /**
     * 更新后台类目
     *
     * @param category
     * @return
     */
    public Category update(Category category){

        return generalDao.merge(category);
    }

    /**
     * 删除后台类目
     *
     * @param category
     * @return
     */
    public boolean delete(Category category) {

        return generalDao.removeById(Category.class,category.getId());

    }


    /**
     * 根据父ID，查找下级类目
     *
     * @param parentId
     * @return
     */
    public List<Category> getCategorybyId(int parentId) {

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
    public List<Category> findFatherCategorys() {

        Search search = new Search(Category.class);
        search.addFilterEqual("parentId", Category.PARENT_DEFAULT);
        return generalDAO.search(search);

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
//            List<Category> categoryChildrenList = getCategorybyId(categoryFather.getParentId());//二级类目
//
//            List<CategoryTree> treeFatherList = new ArrayList<>();
//            for (Category categoryChildren : categoryChildrenList) {
//                List<Category> categoryList = getCategorybyId(categoryChildren.getParentId());//三级类目
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
