package productcenter.dtos;

/**
 * Created by amoszhou on 15/7/17.
 */
public class ProductQueryVO {

    /**
     * 前台导航类目的id
     */
    private Integer navigateId;

    /**
     * 是否有货，1的视为要过滤有货，非1视为不过滤是否有货。
     */
    private Integer st;


    public Integer getNavigateId() {
        return navigateId;
    }

    public void setNavigateId(Integer navigateId) {
        this.navigateId = navigateId;
    }

    public Integer getSt() {
        return st;
    }

    public void setSt(Integer st) {
        this.st = st;
    }
}
