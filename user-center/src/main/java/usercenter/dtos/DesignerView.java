package usercenter.dtos;

/**
 * Created by amos on 15-5-15.
 */
public class DesignerView {

    private Integer id;

    private String name;

    private String nation;

    private String description;

    private String content;

    /**
     * 设计师列表页主图
     */
    private String mainPic;

    /**
     * 设计师列表页品牌图
     */
    private String brandPic;

    /**
     * 店铺页主图
     */
    private String storePic;

    /**
     * 店铺页的LOGO大图
     */
    private String storeLogoPic;

    /**
     * 是否收藏
     */
    private boolean isFavorites;

    public String getStorePic() {
        return storePic;
    }

    public void setStorePic(String storePic) {
        this.storePic = storePic;
    }

    public String getMainPic() {
        return mainPic;
    }

    public void setMainPic(String mainPic) {
        this.mainPic = mainPic;
    }

    public String getBrandPic() {
        return brandPic;
    }

    public void setBrandPic(String brandPic) {
        this.brandPic = brandPic;
    }

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

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFavorites() {
        return isFavorites;
    }

    public void setFavorites(boolean isFavorites) {
        this.isFavorites = isFavorites;
    }

    public String getStoreLogoPic() {
        return storeLogoPic;
    }

    public void setStoreLogoPic(String storeLogoPic) {
        this.storeLogoPic = storeLogoPic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
