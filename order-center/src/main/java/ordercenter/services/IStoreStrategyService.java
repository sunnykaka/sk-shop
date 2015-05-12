package ordercenter.services;

/**
 * 库存扣减策略接口
 * User: lidujun
 * Date: 2015-05-11
 */
public interface IStoreStrategyService {
    /**
     * 创建订单时的库存操作(如果是货到付款, 创建订单时减库存, 不管哪种策略)
     *
     * @param service
     * @param skuId sku编号
     * @param number 购买数量
     * @param isCashOnDelivery 是否是货到付款
     */
    void operateStorageWhenCreateOrder(TradeSuccessService service, int skuId, int number, boolean isCashOnDelivery);

    /**
     * 取消订单时的库存操作(如果是货到付款, 取消时回加库存, 不管哪种策略)
     *
     * @param service
     * @param skuId sku编号
     * @param number 购买数量
     * @param isCashOnDelivery 是否是货到付款
     */
    void operateStorageWhenCancelOrder(TradeSuccessService service, int skuId, int number, boolean isCashOnDelivery);

    /**
     * 订单支付成功后的库存操作
     *
     * @param service
     * @param skuId sku编号
     * @param number 购买数量
     */
    void operateStorageWhenPayOrder(TradeSuccessService service, int skuId, int number);
}
