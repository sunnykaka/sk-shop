package ordercenter.payment;

import java.util.HashMap;
import java.util.Map;

/**
 * 回调处理值的返回值
 * User: lidujun
 * Date: 2015-04-29
 */
public class CallBackResult {

    private boolean result;
    private Map<String, Object> data = new HashMap<String, Object>();

    /**
     * 不管成功与失败，不需要跳转页面，也就不需要successUrl与failureUrl。 比如与前端交互用Ajax
     */
    public CallBackResult() {
    }

    public boolean success() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Object getData(String key){
        if(data == null){
            return null;
        }
        return data.get(key);
    }


    public void addData(String key, Object value) {
        this.data.put(key, value);
    }

}
