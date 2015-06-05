package models;

import common.models.utils.EntityClass;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * Created by amos on 15-5-7.
 */
@Table(name = "cms_exhibition")
@Entity
public class CmsExhibition implements EntityClass<Integer> {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 专场的描述,介绍
     */
    private String introduction;

    /**
     * 状态,根据开始时间和结束时间来确定
     */
    private ExhibitionStatus status;

    /**
     * 开始时间
     */
    private DateTime beginTime;
    /**
     * 结束时间
     */
    private DateTime endTime;

    /**
     * 设计师ID,每个专场都是以1个设计师维度来进行的.
     */
    private Integer designerId;

    /**
     * 设计师名字,冗余字段,避免联表查询,首页只需要查询CMS就可以
     */
    private String designerName;

    private String designerLogo;

    /**
     * 专场的宣传图片,也就是在首页上展示的图片
     */
    private String pic;

    /**
     * 对所有的专场按从左到右,从上到下编个号.
     * 从1开始编号
     */
    private Integer positionIndex;
    /**
     * 给位置起个名,方便记忆,可以不起
     */
    private String positionName;

    /**
     * 首发中的商品数量
     */
    private Integer prodCount;

    /**
     * 关注的基础数目
     */
    private Integer baseLike;


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
    @Column(name = "introduction")
    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }


    /**
     * 专场的状态
     *
     * @return
     */
    @Transient
    public ExhibitionStatus getStatus() {
        if (this.beginTime.isAfterNow()) {
            return ExhibitionStatus.PREPARE;
        } else if (this.endTime.isBeforeNow()) {
            return ExhibitionStatus.OVER;
        }
        return ExhibitionStatus.SELLING;
    }

    public void setStatus(ExhibitionStatus status) {
        this.status = status;
    }

    @Column(name = "beginTime")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(DateTime beginTime) {
        this.beginTime = beginTime;
    }

    @Column(name = "endTime")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    @Basic
    @Column(name = "designerId")
    public Integer getDesignerId() {
        return designerId;
    }

    public void setDesignerId(Integer designerId) {
        this.designerId = designerId;
    }

    @Basic
    @Column(name="designerName")
    public String getDesignerName() {
        return designerName;
    }

    public void setDesignerName(String designerName) {
        this.designerName = designerName;
    }

    @Basic
    @Column(name="designerLogo")
    public String getDesignerLogo() {
        return designerLogo;
    }

    public void setDesignerLogo(String designerLogo) {
        this.designerLogo = designerLogo;
    }

    @Basic
    @Column(name = "pic")
    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    @Basic
    @Column(name = "positionIndex")
    public Integer getPositionIndex() {
        return positionIndex;
    }

    public void setPositionIndex(Integer positionIndex) {
        this.positionIndex = positionIndex;
    }

    @Basic
    @Column(name="positionName")
    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    @Basic
    @Column(name="prodCount")
    public Integer getProdCount() {
        return prodCount;
    }

    public void setProdCount(Integer prodCount) {
        this.prodCount = prodCount;
    }

    @Basic
    @Column(name="baseLike")
    public Integer getBaseLike() {
        return baseLike;
    }

    public void setBaseLike(Integer baseLike) {
        this.baseLike = baseLike;
    }
}
