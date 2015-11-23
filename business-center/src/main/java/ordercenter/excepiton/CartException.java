package ordercenter.excepiton;

import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;

/**
 * Created by liubin on 15-10-15.
 */
public class CartException extends AppBusinessException {

    private Integer maxCanBuyNum;

    public CartException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public CartException(ErrorCode errorCode) {
        super(errorCode);
    }

    public Integer getMaxCanBuyNum() {
        return maxCanBuyNum;
    }

    public void setMaxCanBuyNum(Integer maxCanBuyNum) {
        this.maxCanBuyNum = maxCanBuyNum;
    }
}
