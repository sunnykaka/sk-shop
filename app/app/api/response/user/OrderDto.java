package api.response.user;

import ordercenter.models.Order;
import org.joda.time.DateTime;

/**
 * Created by Administrator on 2015/9/6.
 */
public class OrderDto {

    private Integer id;

    private long orderNo;

    private String orderStateStr;

    private DateTime createTime;

    private String price;

    private String picUrl;

    public static OrderDto build(Order order){

        if(null == order){
            return null;
        }

        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setOrderNo(order.getOrderNo());
        orderDto.setOrderStateStr(order.getOrderState().getValue());
        orderDto.setCreateTime(order.getCreateTime());
        orderDto.setPrice(order.getTotalMoney().getAmountWithBigDecimal().toString());
        if(order.getOrderItemList().size() > 0){
            orderDto.setPicUrl(order.getOrderItemList().get(0).getMainPicture());
        }

        return orderDto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(long orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderStateStr() {
        return orderStateStr;
    }

    public void setOrderStateStr(String orderStateStr) {
        this.orderStateStr = orderStateStr;
    }

    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
