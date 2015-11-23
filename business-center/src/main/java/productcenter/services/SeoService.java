package productcenter.services;

import common.services.GeneralDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productcenter.constants.SeoType;
import productcenter.models.Product;
import productcenter.models.Seo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * SEO 数据
 * <p>
 * Created by zhb on 15-6-3.
 */
@Service
@Transactional(readOnly = true)
public class SeoService {

    @Autowired
    private GeneralDao generalDao;

    /**
     * 获取商品的SEO
     *
     * @param product
     * @return
     */
    public Seo getProductSeo(Product product) {
        Seo seo = getByObjectId(String.valueOf(product.getId()), SeoType.PRODUCT);
        if (null != seo) {
            return seo;
        }

        Seo newSeo = new Seo();
        newSeo.setTitle(product.getName() + "-尚客");
        newSeo.setKeywords(product.getCustomer().getName() + "全球首发，独立设计师，限量发售，限时首发");
        newSeo.setDescription("尚客是全球首家独立设计师首发平台，致力于打造最权威的一站式风尚购物平台，为中国消费者引荐全球顶尖的独立设计师，甄选100%设计师授权的服装、鞋包及配饰。");

        return newSeo;
    }


    /**
     * 获取对像的SEO
     *
     * @param objectId
     * @param type
     * @return
     */
    public Seo getByObjectId(String objectId, SeoType type) {

        String jpql = "select pc from Seo pc where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and pc.seoObjectId = :seoObjectId ";
        queryParams.put("seoObjectId", objectId);

        jpql += " and pc.seoType = :seoType ";
        queryParams.put("seoType", type);

        List<Seo> valueList = generalDao.query(jpql, Optional.empty(), queryParams);
        if (valueList != null && valueList.size() > 0) {
            return valueList.get(0);
        }

        return null;
    }

}
