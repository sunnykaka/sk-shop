package ordercenter.payment;

import ordercenter.models.Trade;
import play.mvc.Http.Request;

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
    Trade buildFromRequest(Request request);

    /**
     * 构建交易参数
     * @param request
     * @return
     */
    Map<String, String> buildParam(Request request);
}
