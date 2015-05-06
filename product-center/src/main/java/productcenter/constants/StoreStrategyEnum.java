package productcenter.constants;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import common.utils.jackson.EnumDeserializer;
import common.utils.jackson.EnumSerializer;
import productcenter.services.SkuAndStorageService;

/**
 * User: lidujun
 * Date: 2015-04-28
 */
@JsonSerialize(using = EnumSerializer.class)
@JsonDeserialize(using = EnumDeserializer.class)
public interface StoreStrategyEnum {
    /**
     * 创建订单时的库存操作(如果是货到付款, 创建订单时减库存, 不管哪种策略)
     *
     * @param skuService
     * @param skuId sku编号
     * @param number 购买数量
     * @param isCashOnDelivery 是否是货到付款
     */
    void operateStorageWhenCreateOrder(SkuAndStorageService skuService, int skuId, int number, boolean isCashOnDelivery);

    /**
     * 取消订单时的库存操作(如果是货到付款, 取消时回加库存, 不管哪种策略)
     *
     * @param skuService
     * @param skuId sku编号
     * @param number 购买数量
     * @param isCashOnDelivery 是否是货到付款
     */
    void operateStorageWhenCancelOrder(SkuAndStorageService skuService, int skuId, int number, boolean isCashOnDelivery);

    /**
     * 订单支付成功后的库存操作
     *
     * @param skuService
     * @param skuId sku编号
     * @param number 购买数量
     */
    void operateStorageWhenPayOrder(SkuAndStorageService skuService, int skuId, int number);
}
