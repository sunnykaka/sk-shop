package cmscenter.models;

import common.models.utils.EntityClass;

import javax.persistence.*;
import java.util.List;

/**
 * Created by amoszhou on 15/9/17.
 */
@Entity
@Table(name = "cms_module")
public class SkModule implements EntityClass<Integer> {

    private Integer id;

    private String name;

    private String description;

    private Integer priority;

    private Integer itemCount;


    private List<SkContent> contents;


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


    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "priority")
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Basic
    @Column(name = "itemCount")
    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    @Transient
    public List<SkContent> getContents() {
        return contents;
    }

    public void setContents(List<SkContent> contents) {
        this.contents = contents;
    }
}
