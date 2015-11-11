package productcenter.models;

import common.models.utils.EntityClass;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * Created by amoszhou on 15/11/9.
 */
@Entity
@Table(name = "limitedtimediscount")
public class LimitTimeDiscount implements EntityClass<Integer> {

    private Integer id;

    private Integer productId;

    private Long discount;

    private DiscountType discountType;

    private DateTime beginDate;

    private DateTime endDate;

    private String skuDetailsJson;

    private Integer isDelete;


    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "productId")
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    @Basic
    @Column(name = "discount")
    public Long getDiscount() {
        return discount;
    }

    public void setDiscount(Long discount) {
        this.discount = discount;
    }

    @Column(name = "discountType")
    @Enumerated(EnumType.STRING)
    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    @Column(name = "beginDate")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(DateTime beginDate) {
        this.beginDate = beginDate;
    }

    @Column(name = "endDate")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    @Basic
    @Column(name = "skuDetailsJson")
    public String getSkuDetailsJson() {
        return skuDetailsJson;
    }

    public void setSkuDetailsJson(String skuDetailsJson) {
        this.skuDetailsJson = skuDetailsJson;
    }


    @Basic
    @Column(name="isDelete")
    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }
}
