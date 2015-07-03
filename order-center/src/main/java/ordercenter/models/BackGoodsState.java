package ordercenter.models;

import java.util.HashMap;
import java.util.Map;

/**
 * 退货单状态.
 *
 * @author Athens(刘杰)
 */
public enum BackGoodsState {

    /**
     * 创建退货单
     */
    Create,

    /**
     * 审核通过
     */
    Verify,

    /**
     * 确认收货(若未发货则无此一步)
     */
    Receive,

    /**
     * 处理完成(已退款)
     */
    Success,

    /**
     * 取消
     */
    Cancel;

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
     * 用户状态
     */
    private static Map<BackGoodsState, String> userMapping = new HashMap<BackGoodsState, String>();

    /**
     * 客服状态
     */
    private static Map<BackGoodsState, String> serviceMapping = new HashMap<BackGoodsState, String>();

    /**
     * 财务状态
     */
    private static Map<BackGoodsState, String> financeMapping = new HashMap<BackGoodsState, String>();

    static {
        // 用户对应的状态
        userMapping.put(Create, "您的售后单已提交，请等待客服审核");
        userMapping.put(Verify, "您的售后单已审核通过");
        userMapping.put(Receive, "商家已收到您退回的商品，将尽快安排退款");
        userMapping.put(Success, "已退款，预计7个工作日内到账（以各银行具体情况为准）。");
        userMapping.put(Cancel, "您的售后单已取消");

        // 财务对应的状态
        financeMapping.put(Create, "");
        financeMapping.put(Verify, "审核通过");
        financeMapping.put(Receive, "等待退款");
        financeMapping.put(Success, "已退款");
        financeMapping.put(Cancel, "");

        // 客服对应的状态
        serviceMapping.put(Create, "您的售后单已提交，请等待客服审核");
        serviceMapping.put(Verify, "您的售后单已审核通过");
        serviceMapping.put(Receive, "商家已收到您退回的商品，将尽快安排退款");
        serviceMapping.put(Success, "已退款，预计7个工作日内到账（以各银行具体情况为准）。");
        serviceMapping.put(Cancel, "您的售后单已取消");
    }


    /**
     * 用户可见的状态说明
     */
    public String userDesc(BackGoodsType backGoodsType) {
        String userDesc = userMapping.get(this);
        if (backGoodsType == BackGoodsType.YetSend) {
            if (this == Receive) {
                userDesc += ",等待退款";
            } else if (this == Verify) {
                userDesc += ",等待客户发回商品";
            }
        } else {
            if (this == Verify) {
                userDesc += ",等待退款";
            }
        }
        return userDesc;
    }

    /**
     * 客服可见的状态说明
     */
    public String serviceDesc() {
        return serviceMapping.get(this);
    }

    /**
     * 财务可见的状态说明
     */
    public String financeDesc() {
        return financeMapping.get(this);
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

    /*
        财务查询 等待退款的 退款单:
        from BackGoods where (..type = 'NoSend' and ..state = 'Verify') or (..type = 'YetSend' and ..state = 'Receive')
    */

}
