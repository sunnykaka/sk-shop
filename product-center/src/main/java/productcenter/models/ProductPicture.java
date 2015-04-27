package productcenter.models;

import common.models.utils.EntityClass;

import javax.persistence.*;

/**
 * 产品图片
 * 对应大图，因为一个商品可能有多张大图，其他小图通过大图的路径加上像素来定位
 * 商品图片上传的时候会生成各种缩略图
 * <p/>
 * 一个商品存在一个主图，主图唯一
 * 一个sku存在一组图片
 * User: lidujun
 * Date: 2015-04-24
 */
@Table(name = "ProductPicture")
@Entity
public class ProductPicture implements EntityClass<Integer> {

    /**
     * 图片主键id
     */
    private Integer id;

    /**
     * 产品d
     */
    private Integer productId;

    /**
     * 单品sku id
     */
    private String skuId;

    /**
     * 原始名称
     */
    private String originalName;

    /**
     * 名称
     */
    private String name;

    /**
     * 是否主图：0 代表不是主图, 1 代表是, 默认是 0
     */
    private Boolean mainPic;

    /**
     *是否副图：0 代表不是主图, 1 代表是, 默认是 0
     */
    private Boolean minorPic;

    /**
     * 图片url
     */
    private String pictureUrl;

    /**
     * 图片本地url
     */
    private String pictureLocalUrl;

    /**
     * 数量
     */
    private int number;

    @Override
    public String toString() {
        return "ProductPicture{" +
                "id=" + id +
                ", productId=" + productId +
                ", skuId='" + skuId + '\'' +
                ", originalName='" + originalName + '\'' +
                ", name='" + name + '\'' +
                ", mainPic=" + mainPic +
                ", minorPic=" + minorPic +
                ", pictureUrl='" + pictureUrl + '\'' +
                ", pictureLocalUrl='" + pictureLocalUrl + '\'' +
                ", number=" + number +
                '}';
    }

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "productId")
    @Basic
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    @Column(name = "skuId")
    @Basic
    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    @Column(name = "originalName")
    @Basic
    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    @Column(name = "name")
    @Basic
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "mainPic")
    @Basic
    public Boolean isMainPic() {
        return mainPic;
    }

    public void setMainPic(Boolean mainPic) {
        this.mainPic = mainPic;
    }

    @Column(name = "minorPic")
    @Basic
    public Boolean isMinorPic() {
        return minorPic;
    }

    public void setMinorPic(Boolean minorPic) {
        this.minorPic = minorPic;
    }

    @Column(name = "pictureUrl")
    @Basic
    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    @Column(name = "pictureLocalUrl")
    @Basic
    public String getPictureLocalUrl() {
        return pictureLocalUrl;
    }

    public void setPictureLocalUrl(String pictureLocalUrl) {
        this.pictureLocalUrl = pictureLocalUrl;
    }

    @Column(name = "number")
    @Basic
    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
