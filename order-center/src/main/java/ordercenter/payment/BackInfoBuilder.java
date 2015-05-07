package ordercenter.payment;

import ordercenter.payment.models.TradeInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 支付返回Builder
 * User: lidujun
 * Date: 2015-04-29
 */
public interface BackInfoBuilder {

    /**
     * 从请求获得交易信息
     * @param request
     * @return
     */
    TradeInfo buildFromRequest(HttpServletRequest request);

    /**
     * 构建交易参数
     * @param request
     * @return
     */
    Map<String, String> buildParam(HttpServletRequest request);
}
