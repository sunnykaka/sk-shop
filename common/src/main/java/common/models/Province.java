package common.models;


import common.models.utils.EntityClass;

import javax.persistence.*;

/**
 * User: liubin
 * Date: 14-3-28
 */
@Table(name = "t_province")
@Entity
public class Province implements EntityClass<String> {

    private String id;

    private String name;


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
}
