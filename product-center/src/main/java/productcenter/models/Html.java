package productcenter.models;

import common.models.utils.EntityClass;

import javax.persistence.*;

/**
 * 商品的超文本描述
 * User: lidujun
 * Date: 2015-04-27
 */
public class Html {
    private Integer id;

    /**
     * 产品id
     */
    private int productId;

    /**
     * 名称
     */
    private String name;

    /**
     * 内容
     */
    private String content;

    @Column(name = "productId")
    @Basic
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    @Column(name = "name")
    @Basic
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "content")
    @Basic
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
