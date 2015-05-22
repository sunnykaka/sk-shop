package usercenter.models;

import common.models.utils.EntityClass;
import usercenter.constants.DesignerPictureType;

import javax.persistence.*;

/**
 * 设计师图片
 * @Author: lidujun
 * @Since: 2015-05-22
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

    private DesignerPictureType picType;

    private String size;

    public DesignerPicture(){}

    public DesignerPicture(String pictureUrl){
        this.pictureUrl = pictureUrl;
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

    @Column(name = "name")
    @Basic
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "pictureUrl")
    @Basic
    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    @Column(name = "designerId")
    @Basic
    public int getDesignerId() {
        return designerId;
    }

    public void setDesignerId(int designerId) {
        this.designerId = designerId;
    }

    @Column(name = "originalName")
    @Basic
    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    @Column(name = "pictureLocalUrl")
    @Basic
    public String getPictureLocalUrl() {
        return pictureLocalUrl;
    }

    public void setPictureLocalUrl(String pictureLocalUrl) {
        this.pictureLocalUrl = pictureLocalUrl;
    }

    @Column(name = "picType")
    @Enumerated(EnumType.STRING)
    public DesignerPictureType getPicType() {
        return picType;
    }

    public void setPicType(DesignerPictureType picType) {
        this.picType = picType;
    }

    @Column(name = "size")
    @Basic
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
