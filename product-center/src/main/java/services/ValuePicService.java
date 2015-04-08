package services;

import common.services.GeneralDao;
import common.utils.page.Page;
import models.ValuePic;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(ValuePicService.class);

    @Autowired
    private GeneralDao generalDao;

    /**
     * 保存产品（商品）属性个性化图片
     */
    public void save(ValuePic valuePic){
        log.info("--------ValuePicService save begin exe-----------" + valuePic);
        generalDao.persist(valuePic);
    }

    /**
     * 删除产品（商品）属性个性化图片
     */
    public void realDelete(Integer valuePicId){
        log.info("--------ValuePicService realDelete begin exe-----------" + valuePicId);
        generalDao.removeById(ValuePic.class, valuePicId);
    }

    /**
     * 更新产品（商品）属性个性化图片
     */
    public void update(ValuePic valuePic){
        log.info("--------ValuePicService update begin exe-----------" + valuePic);
        generalDao.merge(valuePic);
    }

    /**
     * 通过主键获取产品（商品）属性个性化图片
     */
    @Transactional(readOnly = true)
    public ValuePic getValuePicById(Integer valuePicId){
        log.info("--------ValuePicService getValuePicById begin exe-----------" + valuePicId);
        return generalDao.get(ValuePic.class, valuePicId);
    }

    /**
     * 获取产品（商品）属性个性化图片列表
     */
    @Transactional(readOnly = true)
    public List<ValuePic> getValuePicList(Optional<Page<ValuePic>> page, ValuePic param){
        log.info("--------ValuePicService getValuePicList begin exe-----------" + page + "\n" + param);

        String jpql = "select o from Product o where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();

        if(param != null) {
            Integer productId = param.getProductId();
            if(productId != null && productId != 0) {
                jpql += " and o.productId like :productId ";
                queryParams.put("productId", productId);
            }

            String picUrl = param.getPicUrl();
            if(!StringUtils.isEmpty(picUrl)) {
                jpql += " and o.picUrl like :picUrl ";
                queryParams.put("picUrl", picUrl);
            }
        }
        jpql += " group by o.id";
        return generalDao.query(jpql, page, queryParams);
    }

}
