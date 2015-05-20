package dtos;

import productcenter.models.ProductPicture;
import productcenter.models.SkuStorage;
import productcenter.models.StockKeepingUnit;
import productcenter.services.ProductPictureService;
import productcenter.services.ProductService;
import productcenter.services.SkuAndStorageService;
import utils.Global;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by liubin on 15-5-15.
 */
public class SkuDetail {

    private StockKeepingUnit sku;

    private int stockQuantity;

    private List<String> imageList;

    private SkuDetail(StockKeepingUnit sku) {
        this.sku = sku;
    }

    public StockKeepingUnit getSku() {
        return sku;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public static class Builder {

        private SkuDetail skuDetail;

        private SkuAndStorageService skuAndStorageService = Global.ctx.getBean(SkuAndStorageService.class);
        private ProductPictureService productPictureService = Global.ctx.getBean(ProductPictureService.class);

        public static Builder newBuilder(StockKeepingUnit sku) {
            Builder builder = new Builder();
            builder.skuDetail = new SkuDetail(sku);
            return builder;
        }

        public Builder buildSku() {

            SkuStorage skuStorage = skuAndStorageService.getSkuStorage(skuDetail.sku.getId());
            if(skuStorage != null) {
                skuDetail.stockQuantity = skuStorage.getStockQuantity();
            }

            List<ProductPicture> productPictures = productPictureService.queryProductPicturesBySkuId(String.valueOf(skuDetail.sku.getId()));
            skuDetail.imageList = productPictures.stream().map(ProductPicture::getPictureUrl).collect(Collectors.toList());

            return this;
        }

        public SkuDetail build() {
            return skuDetail;
        }

    }

}
