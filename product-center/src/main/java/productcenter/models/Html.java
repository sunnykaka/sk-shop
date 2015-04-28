package productcenter.models;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 商品的超文本描述
 * User: lidujun
 * Date: 2015-04-27
 */
@Table(name = "Html")
@Entity
public class Html implements Serializable {

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

    @Override
    public String toString() {
        return "Html{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "productId")
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
