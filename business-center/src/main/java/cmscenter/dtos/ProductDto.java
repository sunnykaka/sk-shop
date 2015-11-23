package cmscenter.dtos;


import common.utils.Money;
import productcenter.dtos.ProductInSellList;
import productcenter.models.Product;
import productcenter.models.ProductPicture;
import productcenter.models.StockKeepingUnit;
import productcenter.services.ProductPictureService;
import productcenter.services.ProductService;
import productcenter.services.SkuAndStorageService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhb on 2015/10/28.
 */
public class ProductDto {

    private Integer productId;

    private String productUrl;

    private String price;

    private String productName;

    public static ProductDto build(ProductInSellList productInSellList){
        ProductDto productDto = new ProductDto();
        productDto.setProductId(productInSellList.getProduct().getId());
        String price = productInSellList.getDiscount() != null ? productInSellList.getPrice().toString() : productInSellList.getMarketPrice().toString();
        productDto.setPrice(price);
        productDto.setProductName(productInSellList.getProduct().getName());
        productDto.setProductUrl(productInSellList.getMinorPic().getPictureUrl());
        return productDto;
    }

    public static List<ProductDto> build(List<ProductInSellList> productInSellLists){

        if(productInSellLists == null){
            return null;
        }

        List<ProductDto> productDtos = new ArrayList<>();
        for(ProductInSellList productInSellList:productInSellLists){
            productDtos.add(ProductDto.build(productInSellList));
        }

        return productDtos;

    }

    public static ProductDto getById(int productId, ProductService productService, ProductPictureService productPictureService, SkuAndStorageService skuService){

        Product product = productService.getProductById(productId);
        ProductPicture productPicture = productPictureService.getMinorProductPictureByProductId(productId);
        StockKeepingUnit sku = skuService.querySkuByProductIdPriceSmall(productId);
        Money money = skuService.getSkuCurrentPrice(sku);

        ProductDto productDto = new ProductDto();
        productDto.setPrice(money.getAmountWithBigDecimal().toString());
        productDto.setProductId(productId);
        productDto.setProductName(product.getName());
        productDto.setProductUrl(productPicture.getPictureUrl());

        return productDto;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
