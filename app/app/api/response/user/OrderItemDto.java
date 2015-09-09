package api.response.user;

import ordercenter.models.OrderItem;
import productcenter.models.SkuProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/7.
 */
public class OrderItemDto {

    private String picUrl;

    /**
     * 设计师名称
     */
    private String designerName;

    /**
     * 产品名称，展示时使用
     */
    private String productName;

    private String totalPrice;

    /**
     * 购买数量
     */
    private int number;

    private List<SkuProperty> properties = new ArrayList<>();

    public static OrderItemDto build(OrderItem orderItem){
        if(null == orderItem){return null;}

        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setDesignerName(orderItem.getCustomerName());
        orderItemDto.setNumber(orderItem.getNumber());
        orderItemDto.setPicUrl(orderItem.getMainPicture());
        orderItemDto.setProductName(orderItem.getProductName());
        orderItemDto.setTotalPrice(orderItem.getTotalPrice().getAmountWithBigDecimal().toString());
        orderItemDto.setProperties(orderItem.getProperties());

        return orderItemDto;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getDesignerName() {
        return designerName;
    }

    public void setDesignerName(String designerName) {
        this.designerName = designerName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<SkuProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<SkuProperty> properties) {
        this.properties = properties;
    }
}
