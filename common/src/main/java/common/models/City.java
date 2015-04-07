package common.models;


import common.models.utils.EntityClass;

import javax.persistence.*;

/**
 * User: liubin
 * Date: 14-3-28
 */
@Table(name = "t_city")
@Entity
public class City implements EntityClass<String> {

    private String id;

    private String name;

    private String provinceId;

    private Province province;


    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    @Column(name = "province_id")
    @Basic
    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="province_id", insertable = false, updatable = false)
    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

}
