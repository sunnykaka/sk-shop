package productcenter.models;

import javax.persistence.*;

/**
 * 商品的超文本描述
 * User: Asion
 * Date: 11-9-6
 * Time: 上午10:58
 */
@Table(name = "Html")
@Entity
public class Html {

    private Integer productId;

    private String name;

    private String content;

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Id
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    @Column(name = "content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
