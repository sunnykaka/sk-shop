package ordercenter.services;

/**
 * 库存扣减策略接口实现类-支付成功则扣减库存策略
 * User: lidujun
 * Date: 2015-05-11
 */
public class PayStoreStrategyService implements IStoreStrategyService{
    @Override
    public void operateStorageWhenCreateOrder(TradeSuccessService service, int skuId, int number, boolean isCashOnDelivery) {
        if (isCashOnDelivery) {
            service.minusSkuStock(skuId, number);
        }
    }

    @Override
    public void operateStorageWhenCancelOrder(TradeSuccessService service, int skuId, int number, boolean isCashOnDelivery) {
        if (isCashOnDelivery) {
            service.addSkuStock(skuId, number);
        }
    }

    @Override
    public void operateStorageWhenPayOrder(TradeSuccessService service, int skuId, int number) {
        service.minusSkuStock(skuId, number);
    }
}
