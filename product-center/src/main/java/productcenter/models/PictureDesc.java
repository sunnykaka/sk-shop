package productcenter.models;

import java.util.List;

/**
 * 对商品进行图片描述
 * User: lidujun
 * Date: 2015-04-24
 */
public class PictureDesc {
    public static final String defaultPictureUrl="";

    private int productId;

    private List<ProductPicture> pictures;

    /**
     * 得到主图，如果商品没有图片，使用一个默认的
     *
     * @return
     */
    public ProductPicture getMainPicture() {
        ProductPicture main = null;
        for (ProductPicture picture : pictures) {
            if (picture.isMainPic()) {
                main = picture;
                break;
            }
        }
        if (main == null) {
            main = new ProductPicture();
            main.setName("not found");
            main.setMainPic(true);
            main.setPictureUrl(defaultPictureUrl);
        }
        return main;
    }

    public List<ProductPicture> getPictures() {
        return pictures;
    }

    public void setPictures(List<ProductPicture> pictures) {
        this.pictures = pictures;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
