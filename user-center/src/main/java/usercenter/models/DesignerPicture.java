package usercenter.models;

import common.models.utils.EntityClass;

import javax.persistence.*;

/**
 * 设计师图片
 * @Author: lidujun
 * @Since: 2015-04-21
 */
@Entity
@Table(name = "designer_picture")
public class DesignerPicture implements EntityClass<Integer> {

    private Integer id;

    private int designerId;

    private String name;

    private String originalName;

    private String pictureUrl;

    private String pictureLocalUrl;

    private boolean mainPic; //是否主图

    private boolean minorPic;//是否副图

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

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "pictureUrl")
    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    @Column(name = "designerId")
    public int getDesignerId() {
        return designerId;
    }

    public void setDesignerId(int designerId) {
        this.designerId = designerId;
    }

    @Column(name = "mainPic")
    public boolean isMainPic() {
        return mainPic;
    }

    public void setMainPic(boolean mainPic) {
        this.mainPic = mainPic;
    }

    @Column(name = "originalName")
    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    @Column(name = "pictureLocalUrl")
    public String getPictureLocalUrl() {
        return pictureLocalUrl;
    }

    public void setPictureLocalUrl(String pictureLocalUrl) {
        this.pictureLocalUrl = pictureLocalUrl;
    }

    @Column(name = "minorPic")
    public boolean isMinorPic() {
        return minorPic;
    }

    public void setMinorPic(boolean minorPic) {
        this.minorPic = minorPic;
    }

}
