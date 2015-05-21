package dtos;

import models.CmsExhibition;
import productcenter.models.Product;
import productcenter.models.ProductPicture;

/**
 * Created by amos on 15-5-19.
 */
public class ProductInfo {

    private Product product;

    private ProductPicture mainPic;

    private CmsExhibition cmsExhibition;


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

    public CmsExhibition getCmsExhibition() {
        return cmsExhibition;
    }

    public void setCmsExhibition(CmsExhibition cmsExhibition) {
        this.cmsExhibition = cmsExhibition;
    }
}
