package models;

import java.util.Map;

/**
 * 页面需求封装数据
 *
 * Created by zhb on 15-4-9.
 */
public class CategoryTree {

    /** 可展开 */
    public static final String TYPE_FOLDER = "folder";

    /** 不可展开 */
    public static final String TYPE_ITEM= "item";

    private Integer id;

    private String name;

    private String type;

    private Map<String,Map<String,CategoryTree>> additionalParameters;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Map<String, CategoryTree>> getAdditionalParameters() {
        return additionalParameters;
    }

    public void setAdditionalParameters(Map<String, Map<String, CategoryTree>> additionalParameters) {
        this.additionalParameters = additionalParameters;
    }

    @Override
    public String toString() {
        return "CategoryTree{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", additionalParameters='" + additionalParameters + '\'' +
                '}';
    }
}
