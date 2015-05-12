package ordercenter.services;

import productcenter.constants.StoreStrategy;

/**
 * 库存扣减策略工厂类
 * User: lidujun
 * Date: 2015-05-11
 */
public class StoreStrategyServiceFactory {

    public static IStoreStrategyService getStoreStrategyServiceImpl(StoreStrategy storeStrategy) {
        //现在因为只有一种库存扣减策略，此为以后有多种策略预留，默认也返回
        if(storeStrategy == StoreStrategy.PayStrategy) {
            return  new PayStoreStrategyService();
        } else {
            return new NormalStoreStrategyService();
        }
    }
}
