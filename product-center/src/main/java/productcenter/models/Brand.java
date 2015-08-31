package productcenter.models;

import common.models.utils.EntityClass;

import javax.persistence.*;

/**
 * 品牌
 */
@Table(name = "brand")
@Entity
public class Brand implements EntityClass<Integer> {

    /**
     * 属性主键id
     */
    private Integer id;

    /**
     * 品牌名称
     */
    private String name;

    /**
     * 所属设计师ID
     */
    private Integer customerId;

    /**
     * 品牌故事
     */
    private String story;

    /**
     * 图片
     */
    private String picture;

    /**
     * 描述
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
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "customerId")
    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    @Column(name = "story")
    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    @Column(name = "picture")
    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
