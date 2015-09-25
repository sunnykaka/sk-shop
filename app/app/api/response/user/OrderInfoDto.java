package api.response.user;

import common.models.utils.ViewEnum;
import common.utils.Money;
import ordercenter.constants.OrderState;
import ordercenter.models.Logistics;
import ordercenter.models.Order;
import ordercenter.models.OrderItem;
import ordercenter.models.Trade;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhb on 2015/9/6.
 */
public class OrderInfoDto {

    private Integer id;

    private long orderNo;

    private LogisticsDto logistics;

    /**
     * 支付银行
     */
    private String payBank;

    private String price;

    private String expressPrice;

    private String lastPrice;

    private List<OrderItemDto> orderItems = new ArrayList<>();

    private int orderStateInt = AppOrderState.Nil.getKey();

    //是否可评论
    private boolean valuation;

    public static OrderInfoDto build(Order order ,Logistics logistics,Trade trade){
        Money expressMoney = Money.valueOf(0);
        OrderInfoDto orderInfoDto = new OrderInfoDto();
        orderInfoDto.setId(order.getId());
        orderInfoDto.setOrderNo(order.getOrderNo());
        orderInfoDto.setExpressPrice(expressMoney.toString());
        orderInfoDto.setPrice(order.getTotalMoney().getAmountWithBigDecimal().toString());
        orderInfoDto.setLastPrice(order.getTotalMoney().add(expressMoney).getAmountWithBigDecimal().toString());
        orderInfoDto.setValuation(order.isValuation());
        if(null != trade){
            orderInfoDto.setPayBank(order.getPayBank().getValue());
        }
        if(order.getOrderState().getName().equals(OrderState.Create.getName())){
            orderInfoDto.setOrderStateInt(AppOrderState.Create.getKey());
        }else if(order.getOrderState().getName().equals(OrderState.Send.getName())){
            orderInfoDto.setOrderStateInt(AppOrderState.Send.getKey());
        }

        for(OrderItem orderItem : order.getOrderItemList()){
            orderInfoDto.addOrderItemDto(OrderItemDto.build(orderItem));
        }

        orderInfoDto.setLogistics(LogisticsDto.build(logistics));


        return orderInfoDto;

    }
    public void addOrderItemDto(OrderItemDto orderItemDto){
        orderItems.add(orderItemDto);
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

    public LogisticsDto getLogistics() {
        return logistics;
    }

    public void setLogistics(LogisticsDto logistics) {
        this.logistics = logistics;
    }

    public String getPayBank() {
        return payBank;
    }

    public void setPayBank(String payBank) {
        this.payBank = payBank;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getExpressPrice() {
        return expressPrice;
    }

    public void setExpressPrice(String expressPrice) {
        this.expressPrice = expressPrice;
    }

    public String getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(String lastPrice) {
        this.lastPrice = lastPrice;
    }

    public List<OrderItemDto> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDto> orderItems) {
        this.orderItems = orderItems;
    }

    public int getOrderStateInt() {
        return orderStateInt;
    }

    public void setOrderStateInt(int orderStateInt) {
        this.orderStateInt = orderStateInt;
    }

    public boolean isValuation() {
        return valuation;
    }

    public void setValuation(boolean valuation) {
        this.valuation = valuation;
    }

    public enum AppOrderState implements ViewEnum {
        Nil(0,"不能处理"),
        Create(1,"支付"),
        Send(2,"确认收货");

        public int key;

        public String name;

        AppOrderState(int key,String name){
            this.key = key;
            this.name = name;
        }

        @Override
        public String getName() {
            return this.toString();
        }

        @Override
        public String getValue() {
            return name;
        }

        public int getKey(){
            return key;
        }
    }

}