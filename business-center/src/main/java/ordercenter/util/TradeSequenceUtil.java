package ordercenter.util;

import common.utils.DateUtils;
import usercenter.models.User;
import usercenter.utils.SessionUtils;

/**
 * 交易号码生成工具
 *
 * User: lidujun
 * Date: 2015-04-29
 */
public class TradeSequenceUtil {

    /**
     * 得到交易流水号
     *
     * @return
     */
    public static String getTradeNo() {
        User user = SessionUtils.currentUser();
        int userId = 1; //默认以1为种子用户
        if(user != null){
            userId = user.getId();
        }
       return userId + String.valueOf(System.currentTimeMillis());
    }

    /**
     * 生成退款批次号
     *
     * @return
     */
    public static String getRefundBatchNo() {
        return DateUtils.printDateTime(DateUtils.current(), DateUtils.SIMPLE_DATE_FORMAT_STR) + System.currentTimeMillis();
    }

    /**
     * 判断退货批次號是否是今天的~
     *
     * @param batchNo
     * @return 若批次号不是今天的, 则返回 true
     */
    public static boolean checkRefundBatchNoIsNotToday(String batchNo) {
        String date = batchNo.substring(0, DateUtils.SIMPLE_DATE_FORMAT_STR.length());
        return !DateUtils.printDateTime(DateUtils.current(), DateUtils.SIMPLE_DATE_FORMAT_STR) .equals(date);
    }

}
