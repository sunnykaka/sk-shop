package productcenter.models;

import common.models.utils.EntityClass;

import javax.persistence.*;

/**
 * Created by amoszhou on 15/7/13.
 */

@Entity
@Table(name = "categoryassociation")
public class CategoryAssociation implements EntityClass<Integer> {

    private Integer  id;

    private Integer navId;

    private Integer cId;

    @Basic
    @Column(name = "navId")
    public Integer getNavId() {
        return navId;
    }

    public void setNavId(Integer navId) {
        this.navId = navId;
    }

    @Basic
    @Column(name = "cid")
    public Integer getcId() {
        return cId;
    }

    public void setcId(Integer cId) {
        this.cId = cId;
    }


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
}
