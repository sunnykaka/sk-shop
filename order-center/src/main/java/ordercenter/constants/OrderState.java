package ordercenter.constants;

import common.models.utils.ViewEnum;

/**
 * 订单状态
 * User: lidujun
 * Date: 2015-04-30
 */
public enum OrderState implements ViewEnum {
    /** 已创建 */
    Create("未支付"),

    /**待审核:【客户】付款完成, 等待【客服】确认 */
    Pay("待审核"),

    ////////////////////已审核对应3个值，原因：保持与其他系统中订单状态值一致////////////////////
    /**已审核:【客服】确认完成, 等待【商家】打单 */
    Confirm("已审核"),

    /**已审核:【商家】已打单, 等待【商家】验货 */
    Print("已审核"),

    /**已审核:【商家】已验货, 等待【商家】发货 */
    Verify("已审核"),
   ////////////////////已审核对应3个值，原因：保持与其他系统中订单状态值一致////////////////////

    /**已发货:【商家】已发货, 等待【客户】收货 */
    Send("已发货"),
    /**
     * 已签收:客户已收货,请等待订单完成
     */
    Receiving("已签收"),

    /**已完成：交易成功 */
    Success("已完成"),

    /**已退货:【客户】已退货 */
    Back("已退货"),

    /**已取消:订单已取消(交易取消)*/
    Cancel("已取消"),

    /**已关闭:订单已关闭(交易取消)*/
    Close("已关闭");

    public String value;

    OrderState(String value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return this.toString();
    }

    @Override
    public String getValue() {
        return value;
    }

    /**
     * 是否是等待付款
     */
    public boolean waitPay(TradePayType payType) {
        return this == Create && payType == TradePayType.OnLine;
    }
}
