package ordercenter.payment.models;

/**
 * 虚拟订单交易结果
 * User: lidujun
 * Date: 2015-04-29
 */
public class VirtualOrderResult {

    private int id;

    private int virtualOrderId;

    private long tradePrice;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVirtualOrderId() {
        return virtualOrderId;
    }

    public void setVirtualOrderId(int virtualOrderId) {
        this.virtualOrderId = virtualOrderId;
    }

    public long getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(long tradePrice) {
        this.tradePrice = tradePrice;
    }
}
