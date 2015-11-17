package productcenter.dtos;

import play.data.validation.Constraints;

/**
 * 用户评论
 *
 * Created by zhb on 15-5-7.
 */
public class ValuationForm {

    private int orderItemId;

    private int point;

    private int productId;

    private Integer replyUserId;

    @Constraints.Required(message = "内容不能为空")
    @Constraints.MinLength(value = 10, message = "输入不能少于10字符")
    @Constraints.MaxLength(value = 300, message = "输入超过最大300字符限制")
    private String content;

    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getReplyUserId() {
        return replyUserId;
    }

    public void setReplyUserId(Integer replyUserId) {
        this.replyUserId = replyUserId;
    }
}
