package productcenter.models;

import common.models.utils.EntityClass;

import javax.persistence.*;

/**
 * 品牌对象
 * User: lidujun
 * Date: 2015-04-24
 */
@Table(name = "Brand")
@Entity
public class Brand implements EntityClass<Integer> {
    /**
     * 主键 id
     */
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 客户（设计师）id
     */
    private Integer customerId;

    /**
     * 品牌故事
     */
    private String story;

    /**
     * 品牌 logo 图
     */
    private String picture;

    /**
     * 品牌的文字描述
     */
    private String description;

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

    @Column(name = "customerId")
    @Basic
    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    @Column(name = "story")
    @Basic
    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    @Column(name = "picture")
    @Basic
    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Column(name = "description")
    @Basic
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
