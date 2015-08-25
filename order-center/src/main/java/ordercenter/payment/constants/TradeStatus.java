package ordercenter.payment.constants;

/**
 * 交易状态
 * Created by amoszhou on 15/8/18.
 */
public enum TradeStatus {
    /**
     * 新建，初始化状态，未传到第三方支付平台时的状态
     */
    INIT,

    /**
     * 已传到第三方平台，但用户还未支付
     */
    WAIT_BUYER_PAY,

    /**
     * 交易成功
     */
    TRADE_SUCCESS,

    /**
     * 交易成功并结束
     */
    TRADE_FINISHED,

    /**
     * 交易关闭
     */
    TRADE_CLOSED,
    /**
     * 交易失败
     */
    FAILURE
}
