package ordercenter.models;

import common.models.utils.ViewEnum;

/**
 * 退货单状态.
 *
 * @author zhb
 */
public enum BackGoodsState implements ViewEnum {

    /**
     * 创建退货单
     */
    Create("待审核","您的售后单已提交，请等待客服审核"),

    /**
     * 审核通过
     */
    Verify("审核通过","您的售后单已审核通过"),

    /**
     * 确认收货(若未发货则无此一步)
     */
    Receive("等待退款","商家已收到您退回的商品，将尽快安排退款"),

    /**
     * 处理完成(已退款)
     */
    Success("已退款","已退款，预计7个工作日内到账（以各银行具体情况为准）"),

    /**
     * 取消
     */
    Cancel("已取消","您的售后单已取消");

    public String value;

    public String logMsg;

    BackGoodsState(String value, String logMsg) {
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
     * 检查退货单是否能取消(只针对用户).
     *
     * @return 若不能取消则返回 true
     */
    public boolean checkCanNotCancelForUser() {
        return this != Create;
    }

    /**
     * 检查退货单是否能取消(针对客服的操作).
     *
     * @return 若不能取消则返回 true
     */
    public boolean checkCanNotCancelForCustomerService() {
        return this == Success || this == Cancel;
    }

    /**
     * 退货单类型, 已发货 或 未发货(系统及客服线下的判断)
     */
    public enum BackGoodsType {

        /**
         * 已发货的退单
         */
        YetSend {
            @Override
            public boolean checkCanNotRefunds(BackGoodsState backGoodsState) {
                return backGoodsState != Receive;
            }
        },

        /**
         * 未发货的退单
         */
        NoSend {
            @Override
            public boolean checkCanNotRefunds(BackGoodsState backGoodsState) {
                return backGoodsState != Verify;
            }
        };

        /**
         * 检查退货单是否能退款.
         *
         * @param backGoodsState 退货单状态
         * @return 若不能退款则返回 true.
         */
        public abstract boolean checkCanNotRefunds(BackGoodsState backGoodsState);
    }

}
