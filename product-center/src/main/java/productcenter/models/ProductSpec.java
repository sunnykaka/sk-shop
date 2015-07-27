package productcenter.models;


import common.models.utils.EntityClass;

import javax.persistence.*;

/**
 * 产品规格说明
 */
@Table(name = "product_spec")
@Entity
public class ProductSpec implements EntityClass<Integer> {

    private Integer id;

    private String name;

    private String value;

    private Integer productId;

    @Override
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
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

    @Column(name = "value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Column(name = "productId")
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }
}