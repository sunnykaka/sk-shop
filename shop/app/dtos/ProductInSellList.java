package dtos;

import common.utils.Money;
import productcenter.models.Product;
import productcenter.models.ProductPicture;
import productcenter.models.StockKeepingUnit;
import productcenter.services.ProductPictureService;
import productcenter.services.ProductService;
import productcenter.services.SkuAndStorageService;
import usercenter.models.Designer;
import usercenter.services.DesignerService;
import utils.Global;

import java.util.List;
import java.util.Optional;

/**
 * Created by amoszhou on 15/7/20.
 */
public class ProductInSellList implements Comparable<ProductInSellList> {

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

    private Long storage;

    private String brandName;

    public String getBrandName() {
        return brandName;
    }

    public Long getStorage() {
        return storage;
    }

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

    @Override
    public int compareTo(ProductInSellList o) {
        if (this.getStorage() > 0 && o.getStorage() <= 0) {
            return -1;
        }
        if (this.getStorage() <= 0 && o.getStorage() > 0) {
            return 1;
        }
        return 0;
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

        private volatile List<Designer> list;


        private ProductPictureService productPictureService = Global.ctx.getBean(ProductPictureService.class);
        private ProductService productService = Global.ctx.getBean(ProductService.class);
        private SkuAndStorageService skuAndStorageService = Global.ctx.getBean(SkuAndStorageService.class);
        private DesignerService designerService = Global.ctx.getBean(DesignerService.class);

        public static Builder getInstance() {
            Builder builder = new Builder();
            return builder;
        }

        public void initDesignerList() {
            if (list == null)
                list = designerService.allOnlineDesigner();
        }


        public ProductInSellList buildProdct(Product product) {
            initDesignerList();
            this.productInSellList = new ProductInSellList();
            this.productId = product.getId();
            this.productInSellList.product = product;
            buildPicture().buildPriceAndStatus(product);
            this.productInSellList.brandName = list.stream().filter(designer -> designer.getId() == product.getCustomerId()).findFirst().get().getName();
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
        protected Builder buildPriceAndStatus(Product product) {

            /**
             * 读取商品的状态
             */
//            Optional<CmsExhibition> exhibition = cmsService.findExhibitionWithProdId(this.productId);
//            ExhibitionStatus status = ExhibitionStatus.OVER; //默认是正常售卖
//            if (exhibition.isPresent()) {
//                status = exhibition.get().getStatus();
//            }
            this.productInSellList.status = product.getSaleStatus();


            /**
             * 获取默认的sku
             */
            List<StockKeepingUnit> skuLit = skuAndStorageService.querySkuListByProductId(this.productId);

            Optional<StockKeepingUnit> defaultSku = skuLit.stream().filter(sku -> sku.isDefaultSku()).findFirst();

            StockKeepingUnit mostCheapSku = null;

            /**
             * 如果有default sku就读取default sku的价格，否则取最便宜的的SKU
             */
            if (!defaultSku.isPresent()) {
                mostCheapSku = skuAndStorageService.querySkuByProductIdPriceSmall(this.productId);
            } else {
                mostCheapSku = defaultSku.get();
            }


            /**
             * 首发就读首发价，其余状态都读正常售卖价
             */
            if (productService.useFirstSellPrice(this.productId)) {
                this.productInSellList.price = mostCheapSku.getPrice();
            } else {
                this.productInSellList.price = mostCheapSku.getMarketPrice();
            }

            Long storage = skuAndStorageService.getProductStorage(this.productId);
            this.productInSellList.storage = storage;

            return this;
        }
    }
}
