package common.models;


import common.models.utils.EntityClass;

import javax.persistence.*;

/**
 * User: liubin
 * Date: 14-3-28
 */
@Table(name = "t_area")
@Entity
public class Area implements EntityClass<String> {

    private String id;

    private String name;

    private String cityId;

    private City city;

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


    @Column(name = "city_id")
    @Basic
    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="city_id", insertable = false, updatable = false)
    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

}
