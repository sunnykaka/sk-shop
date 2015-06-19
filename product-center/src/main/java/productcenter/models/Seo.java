package productcenter.models;

import common.models.utils.EntityClass;
import common.models.utils.OperableData;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import productcenter.constants.SeoType;

import javax.persistence.*;

/**
 * SEO推广
 * User: Alec
 * Date: 13-10-9
 * Time: 下午2:12
 */
@Table(name = "seo")
@Entity
public class Seo implements EntityClass<Integer>, OperableData {
    private Integer id;
    /**
     * 推广所属ID
     * PS：categoryId,productId,pageId
     */
    private String seoObjectId;
    /**
     * 推广所属类型
     * PS：类目，商品详情，自定义页面
     */
    private SeoType seoType;

    private String title;

    private String description;

    private String keywords;

    private DateTime createTime;

    private DateTime updateTime;

    public Seo(){}

    public Seo(String title,String description,String keywords){
        this.title = title;
        this.description = description;
        this.keywords = keywords;
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

    @Column(name = "seoObjectId")
    @Basic
    public String getSeoObjectId() {
        return seoObjectId;
    }

    public void setSeoObjectId(String seoObjectId) {
        this.seoObjectId = seoObjectId;
    }

    @Column(name = "seoType")
    @Enumerated(EnumType.STRING)
    public SeoType getSeoType() {
        return seoType;
    }

    public void setSeoType(SeoType seoType) {
        this.seoType = seoType;
    }

    @Column(name = "title")
    @Basic
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "description")
    @Basic
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "keywords")
    @Basic
    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    @Column(name = "createDate")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Override
    public DateTime getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public void setUpdateTime(DateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Column(name = "updateDate")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Override
    public DateTime getUpdateTime() {
        return updateTime;
    }
}
