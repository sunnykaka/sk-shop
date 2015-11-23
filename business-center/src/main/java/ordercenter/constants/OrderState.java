package ordercenter.constants;

import common.models.utils.ViewEnum;

/**
 * 订单状态
 * User: lidujun
 * Date: 2015-04-30
 */
public enum OrderState implements ViewEnum {
    /** 已创建 */
    Create("未支付","您提交了订单，请在一小时内支付"),

    /**待审核:【客户】付款完成, 等待【客服】确认 */
    Pay("支付成功","支付成功"),

    ////////////////////已审核对应3个值，原因：保持与其他系统中订单状态值一致////////////////////
    /**已审核:【客服】确认完成, 等待【商家】打单 */
    Confirm("已审核","您的订单已审核通过"),

    /**已审核:【商家】已打单, 等待【商家】验货 */
    Print("已审核","已打单"),

    /**已审核:【商家】已验货, 等待【商家】发货 */
    Verify("已审核","已验货"),
   ////////////////////已审核对应3个值，原因：保持与其他系统中订单状态值一致////////////////////

    /**已发货:【商家】已发货, 等待【客户】收货 */
    Send("已发货","您的订单已经发货"),
    /**
     * 已签收:客户已收货,请等待订单完成
     */
    Receiving("已签收","您的订单已经签收"),

    /**已完成：交易成功 */
    Success("已完成","您的订单已完成"),

    /**已退货:【客户】已退货 */
    Back("已退款", "已退款，预计7个工作日内到账（以各银行具体情况为准）"),

    /**已取消:订单已取消(交易取消)*/
    Cancel("已取消","您的订单已经取消"),

    /**已关闭:订单已关闭(交易取消)*/
    Close("已关闭","您的订单已关闭");

    public String value;

    public String logMsg;

    OrderState(String value, String logMsg) {
        this.value = value;
        this.logMsg = logMsg;
    }

    @Override
    public String getName() {
        return this.toString();
    }

    @Override
    public String getValue() {
        return value;
    }

    public String getLogMsg() {
        return logMsg;
    }

    public void setLogMsg(String logMsg) {
        this.logMsg = logMsg;
    }

    /**
     * 是否是等待付款
     */
    public boolean waitPay(TradePayType payType) {
        return this == Create && payType == TradePayType.OnLine;
    }

    /**
     * 未发送的订单都能取消
     * @return
     */
    public boolean canCancel() {
        return Create == this || Pay == this || Confirm == this || Print == this || Verify == this;
    }
}
