package productcenter.models;

import common.models.utils.EntityClass;
import productcenter.constants.PropertyType;
import productcenter.util.PidVidJsonUtil;

import javax.persistence.*;
import java.util.List;

/**
 * 商品属性
 * User: lidujun
 * Date: 2015-04-27
 */
@Table(name = "ProductProperty")
@Entity
public class ProductProperty implements EntityClass<Integer> {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 产品id
     */
    private int productId;

    /**
     * 属性类型
     */
    private PropertyType propertyType;

    /**
     * 商品的pidvid json串，表示商品的属性，按属性在类目上的优先级排序
     * '{"singlePidVidMap":{"1":4294977296,"4":17179879190,"5":21474846488},"pidvid":[4294977296,17179879190,21474846488],"multiPidVidMap":{}}'
     */
    private String json;

    /**
     * 读取pidvid的个数
     *
     * @return
     */
    @Transient
    public int getPidVidCount() {
        List<Long> longs = PidVidJsonUtil.restore(json).getPidvid();
        return longs.size();
    }

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

    @Column(name = "productId")
    @Basic
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    @Column(name = "propertyType")
    @Enumerated(EnumType.STRING)
    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    @Column(name = "json")
    @Basic
    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

}
