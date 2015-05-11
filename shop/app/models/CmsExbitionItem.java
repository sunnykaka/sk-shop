package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import common.models.utils.EntityClass;

import javax.persistence.*;

/**
 * 专场关联的商品ID
 * @auth amos
 * 15-4-27.
 */
@Entity
@Table(name="exhibition_item")
public class CmsExbitionItem implements EntityClass<Integer> {



    private Integer id;

    /**
     * 专场id
     */
    private Integer exhibitionId;

    @JsonIgnore
    private CmsExhibition cmsExhibition;

    /**
     * 商品id
     */
    private Integer prodId;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name="exhibitionId")
    public Integer getExhibitionId() {
        return exhibitionId;
    }

    public void setExhibitionId(Integer exhibitionId) {
        this.exhibitionId = exhibitionId;
    }

    @Basic
    @Column(name="prodId")
    public Integer getProdId() {
        return prodId;
    }

    public void setProdId(Integer prodId) {
        this.prodId = prodId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibitionId", insertable = false, updatable = false)
    public CmsExhibition getCmsExhibition() {
        return cmsExhibition;
    }

    public void setCmsExhibition(CmsExhibition cmsExhibition) {
        this.cmsExhibition = cmsExhibition;
    }
}
