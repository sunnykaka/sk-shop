package productcenter.models;

import common.models.utils.EntityClass;
import common.models.utils.TableTimeData;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * 商品收藏
 *
 * Created by zhb on 15-4-22.
 */
@Table(name = "productcollect")
@Entity
public class ProductCollect implements EntityClass<Integer>,TableTimeData {

    private Integer id;

    private int userId;

    private int productId;

    private DateTime collectTime;

    /** 软删 */
    private boolean deleted;

    private DateTime createDate;

    private DateTime updateDate;

    private Integer skuId;

    private Integer quantity;

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

    @Column(name = "userId")
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Column(name = "productId")
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    @Column(name = "collectTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(DateTime collectTime) {
        this.collectTime = collectTime;
    }

    @Column(name = "isDelete")
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Column(name = "skuId")
    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    @Column(name = "quantity")
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Column(name = "createDate")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Override
    public DateTime getCreateDate() {
        return createDate;
    }

    @Override
    public void setCreateDate(DateTime createDate) {
        this.createDate = createDate;
    }

    @Column(name = "updateDate")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Override
    public DateTime getUpdateDate() {
        return updateDate;
    }

    @Override
    public void setUpdateDate(DateTime updateDate) {
        this.updateDate = updateDate;
    }

}
