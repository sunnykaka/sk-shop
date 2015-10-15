package dtos;

import productcenter.models.Product;
import productcenter.models.ProductPicture;

/**
 * Created by amos on 15-5-19.
 */
public class ProductInfo {

    private Product product;

    private ProductPicture mainPic;


    /** 是否收藏 */
    private boolean isFavorites;

    /** 收藏数量 */
    private int favoritesNum;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductPicture getMainPic() {
        return mainPic;
    }

    public void setMainPic(ProductPicture mainPic) {
        this.mainPic = mainPic;
    }


    public boolean isFavorites() {
        return isFavorites;
    }

    public void setFavorites(boolean isFavorites) {
        this.isFavorites = isFavorites;
    }

    public int getFavoritesNum() {
        return favoritesNum;
    }

    public void setFavoritesNum(int favoritesNum) {
        this.favoritesNum = favoritesNum;
    }
}
