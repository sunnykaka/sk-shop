package cmscenter.models;

import common.models.utils.EntityClass;

import javax.persistence.*;

/**
 * 首页新品
 *
 * Created by zhb on 2015/10/28.
 */
@Entity
@Table(name = "HomeNewProduct")
public class HomeNewProduct implements EntityClass<Integer> {

    private Integer id;

    private int productId;

    private int priority;

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

    @Column(name = "productId")
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    @Column(name = "priority")
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
