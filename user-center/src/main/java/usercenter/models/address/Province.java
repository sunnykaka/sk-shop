package usercenter.models.address;

import common.models.utils.EntityClass;

import javax.persistence.*;

/**
 * 省市信息
 *
 * @author Athens(刘杰)
 */
@Entity
@Table(name = "province")
public class Province implements EntityClass<Integer> {

    private Integer id;

    private String name;

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
}
