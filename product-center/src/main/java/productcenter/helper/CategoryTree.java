package productcenter.helper;

import java.util.List;

/**
 * 前端需求
 * 后台类目返回数据类型
 * <p/>
 * Created by zhb on 15-4-3.
 */
public class CategoryTree {

    /** 有子类目 */
    public static final boolean EXPANDED_TRUE = true;

    /** 没有子类目 */
    public static final boolean EXPANDED_FALSE = false;

    private Integer id;

    private String text;

    /** 是否可展开 */
    private boolean expanded;

    private List<CategoryTree> children;

    public CategoryTree() {
    }

    public CategoryTree(int id, String text, boolean expanded, List<CategoryTree> children) {
        this.id = id;
        this.text = text;
        this.expanded = expanded;
        this.children = children;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public List<CategoryTree> getChildren() {
        return children;
    }

    public void setChildren(List<CategoryTree> children) {
        this.children = children;
    }
}
