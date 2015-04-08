package productcenter.services;

import common.services.GeneralDao;
import common.utils.page.Page;
import productcenter.models.ValuePic;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 产品（商品）属性个性化图片 Service
 * User: lidujun
 * Date: 2015-04-03
 */
@Service
@Transactional
public class ValuePicService {
    @Autowired
    private GeneralDao generalDao;

    /**
     * 保存产品（商品）属性个性化图片
     */
    public void save(ValuePic valuePic){
        play.Logger.info("--------ValuePicService save begin exe-----------" + valuePic);
        generalDao.persist(valuePic);
    }

    /**
     * 删除产品（商品）属性个性化图片
     */
    public void realDelete(Integer valuePicId){
        play.Logger.info("--------ValuePicService realDelete begin exe-----------" + valuePicId);
        generalDao.removeById(ValuePic.class, valuePicId);
    }

    /**
     * 更新产品（商品）属性个性化图片
     */
    public void update(ValuePic valuePic){
        play.Logger.info("--------ValuePicService update begin exe-----------" + valuePic);
        generalDao.merge(valuePic);
    }

    /**
     * 通过主键获取产品（商品）属性个性化图片
     */
    @Transactional(readOnly = true)
    public Optional<ValuePic> getValuePicById(Integer valuePicId){
        play.Logger.info("--------ValuePicService getValuePicById begin exe-----------" + valuePicId);
        return Optional.ofNullable(generalDao.get(ValuePic.class, valuePicId));
    }

    /**
     * 获取产品（商品）属性个性化图片列表
     */
    @Transactional(readOnly = true)
    public List<ValuePic> getValuePicList(Optional<Page<ValuePic>> page, ValuePic param){
        play.Logger.info("--------ValuePicService getValuePicList begin exe-----------" + page + "\n" + param);

        String jpql = "select o from ValuePic o where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();

        if(param != null) {
            Integer productId = param.getProductId();
            if(productId != null && productId != 0) {
                jpql += " and o.productId = :productId ";
                queryParams.put("productId", productId);
            }

            Integer valueId = param.getValueId();
            if(valueId != null && valueId != 0) {
                jpql += " and o.valueId = :valueId ";
                queryParams.put("valueId", valueId);
            }

            Integer skuId = param.getSkuId();
            if(skuId != null && skuId != 0) {
                jpql += " and o.skuId = :skuId ";
                queryParams.put("skuId", skuId);
            }

            String picUrl = param.getPicUrl();
            if(!StringUtils.isEmpty(picUrl)) {
                jpql += " and o.picUrl = :picUrl ";
                queryParams.put("picUrl", picUrl);
            }
        }
        jpql += " order by o.id";
        return generalDao.query(jpql, page, queryParams);
    }

}
