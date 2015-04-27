package productcenter.services;

import common.services.GeneralDao;
import common.utils.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productcenter.models.Brand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BrandService {

    @Autowired
    GeneralDao generalDao;

    /**
     * 获取所有的品牌
     * @return
     */
    public List<Brand> queryAllBrandes() {
        return generalDao.findAll(Brand.class);
    }

    /**
     * 通过主键获取品牌信息
     */
    public Brand queryBrandById(Integer id){
        play.Logger.info("--------BrandService queryBrandById begin exe-----------" + id);
        return generalDao.get(Brand.class, id);
    }

    /**
     * 获取品牌信息列表
     */
    public List<Brand> queryBrandByCustomerId(int customerId){
        play.Logger.info("--------BrandService queryBrandByCustomerId begin exe-----------" + customerId);

        String jpql = "select o from Brand o where 1=1 and o.customerId=:customerId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("customerId", customerId);
        jpql += " order by o.name";
        return generalDao.query(jpql, Optional.<Page<Brand>>empty(), queryParams);
    }

    /**
     * 获取品牌信息列表
     */
    public List<Brand> queryBrandByPage(int customerId, Optional<Page<Brand>> page){
        play.Logger.info("--------DesignerService getDesignerList begin exe-----------" + page + "\n" + customerId);

        String jpql = "select o from Brand o where 1=1 and o.customerId=:customerId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("customerId", customerId);

        jpql += " order by o.name";
        return generalDao.query(jpql, page, queryParams);
    }
}
