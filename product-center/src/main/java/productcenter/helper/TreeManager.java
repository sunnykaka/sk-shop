package productcenter.helper;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhb on 15-4-7.
 */
@Deprecated
public abstract class TreeManager<T> {

    private T parent;

    private List<Integer> descendantIds = new LinkedList<Integer>();//所有子孙后代的ID

    public T getParent() {
        return parent;
    }

    public void setParent(T parent) {
        this.parent = parent;
    }

    public List<Integer> getDescendantIds() {
        return descendantIds;
    }

    public void setDescendantIds(List<Integer> descendantIds) {
        this.descendantIds = descendantIds;
    }

    /**
     * 加入一个子孙后代ID
     *
     * @param id
     */
    public void addDescendantCategoryId(int id) {
        descendantIds.add(id);
    }

    /**
     * 加入一串子孙后代ID
     * @param ids
     */
    public void addDescendantCategoryId(List<Integer> ids) {
        descendantIds.addAll(ids);
    }
}
