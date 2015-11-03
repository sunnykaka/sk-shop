package cmscenter.dtos;


import productcenter.dtos.ProductInSellList;

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
        productDto.setPrice(productInSellList.getPrice().toString());
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
