package productcenter.services;

import common.services.GeneralDao;
import common.utils.JsonUtils;
import common.utils.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productcenter.dtos.SkuDiscount;
import productcenter.models.LimitTimeDiscount;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Created by amoszhou on 15/11/9.
 */
@Service
@Transactional(readOnly = true)
public class DiscountService {


    @Autowired
    private GeneralDao generalDao;


    /**
     * 查询某个商品的折扣信息
     *
     * @param productId
     * @return
     */
    public LimitTimeDiscount findDiscount4Product(Integer productId) {
        String jpql = " from LimitTimeDiscount where productId = :id and isDelete = 0 ";
        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        List<LimitTimeDiscount> list = generalDao.getEm().createQuery(jpql, LimitTimeDiscount.class).setParameter("id", productId).getResultList();
        if (list != null && list.size() > 0)
            return list.get(0);
        return null;
//        generalDao.query(jpql, Optional.empty(),map);
    }


    /**
     * 获取商品的所有的sku价格
     *
     * @param productId
     * @return
     */
    public Optional<List<SkuDiscount>> skuDiscounts(Integer productId) {
        LimitTimeDiscount discount = findDiscount4Product(productId);
        if (discount != null) {
            String priceJson = discount.getSkuDetailsJson();
            try {
                List<SkuDiscount> skuList = JsonUtils.json2List(priceJson, SkuDiscount.class);
                skuList.forEach(sku -> sku.setSkuMoney(Money.valueOfCent(Long.parseLong(sku.getSkuPrice()))));
                return Optional.of(skuList);
            } catch (IOException e) {
                // e.printStackTrace(); do nothing
            }
        }
        return Optional.empty();
    }

}
