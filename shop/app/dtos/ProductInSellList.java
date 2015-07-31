package dtos;

import common.utils.Money;
import models.CmsExhibition;
import models.ExhibitionStatus;
import productcenter.models.Product;
import productcenter.models.ProductPicture;
import productcenter.models.StockKeepingUnit;
import productcenter.services.ProductPictureService;
import productcenter.services.ProductService;
import productcenter.services.SkuAndStorageService;
import services.CmsService;
import utils.Global;

import java.util.List;
import java.util.Optional;

/**
 * Created by amoszhou on 15/7/20.
 */
public class ProductInSellList {

    /**
     * 商品基本信息
     */
    private Product product;

    /**
     * 商品副图
     */
    private ProductPicture minorPic;

    /**
     * 价格（该商品所有sku最低售价）
     */
    private Money price;

    /**
     * 状态，之所以没有用ExhibitionStatus，是担心以后系统改动时
     * 状态类型之类会完全发生变动。
     */
    private String status;


    public Product getProduct() {
        return product;
    }

    public ProductPicture getMinorPic() {
        return minorPic;
    }

    public Money getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    /**
     * 防止外部直接初始化
     */
    private ProductInSellList() {
    }

    public static class Builder {

        private Builder() {
        }

        /**
         * 商品id
         */
        private Integer productId;

        /**
         * 最终要输出的结果对象
         */
        private ProductInSellList productInSellList;


        private ProductPictureService productPictureService = Global.ctx.getBean(ProductPictureService.class);
        private ProductService productService = Global.ctx.getBean(ProductService.class);
        private SkuAndStorageService skuAndStorageService = Global.ctx.getBean(SkuAndStorageService.class);
        private CmsService cmsService = Global.ctx.getBean(CmsService.class);

        public static Builder getInstance() {
            Builder builder = new Builder();
            return builder;
        }


        public ProductInSellList buildProdct(Product product) {
            this.productInSellList = new ProductInSellList();
            this.productId = product.getId();
            this.productInSellList.product   = product;
            buildPicture().buildPrice().buildStatus();
            return productInSellList;
        }


        /**
         * 构造商品的副图，用于列表页面展示
         *
         * @return
         */
        protected Builder buildPicture() {
            this.productInSellList.minorPic = productPictureService.getMinorProductPictureByProductId(productId);
            return this;
        }


        /**
         * 拿到一款商品中最便宜的sku
         *
         * @return
         */
        protected Builder buildPrice() {
            List<StockKeepingUnit> skuList = skuAndStorageService.querySkuListByProductId(productId);
            Optional<StockKeepingUnit> stockKeepingUnit = skuList.stream().min((s1, s2) -> new Double(s1.getPrice().getAmount()).compareTo(new Double(s2.getPrice().getAmount())));
            StockKeepingUnit mostCheapSku = stockKeepingUnit.get();
            this.productInSellList.price = mostCheapSku.getPrice();
            return this;
        }

        protected Builder buildStatus() {
            Optional<CmsExhibition> exhibition = cmsService.findExhibitionWithProdId(this.productId);
            if (exhibition.isPresent()) {
                ExhibitionStatus status = exhibition.get().getStatus();
                this.productInSellList.status = status.toString();
            } else {
                this.productInSellList.status = ExhibitionStatus.OVER.toString();
            }
            return this;
        }


    }
}
