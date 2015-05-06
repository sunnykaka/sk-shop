package ordercenter.payment.constants;

/**
 * 调用回调处理类的方式
 * User: lidujun
 * Date: 2015-04-28
 */
public enum ResponseType {
    /**
     * 正常返回
     */
    RETURN("正常返回"), //return
    /**
     * Notify回来
     */
    NOTIFY("Notify回来"); //notify

    private String value;

    ResponseType(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
}
