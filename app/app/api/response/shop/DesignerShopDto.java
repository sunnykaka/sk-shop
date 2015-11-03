package api.response.shop;

import cmscenter.dtos.ProductDto;
import productcenter.dtos.ProductInSellList;
import productcenter.constants.SaleStatus;
import productcenter.models.Product;
import productcenter.services.ProductService;
import usercenter.dtos.DesignerView;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by zhb on 2015/10/28.
 */
public class DesignerShopDto {

    private int designerId;

    private String picUrl;

    private String description;

    private String name;

    private List<DesignerProductDto> productList = new ArrayList<>();

    public static DesignerShopDto build(DesignerView designerView, ProductService productService) {
        DesignerShopDto designerShopDto = new DesignerShopDto();

        designerShopDto.setDesignerId(designerView.getId());
        designerShopDto.setPicUrl(designerView.getStorePic());
        designerShopDto.setDescription(designerView.getDescription());
        designerShopDto.setName(designerView.getName());

        List<Product> products = productService.products4Designer(designerView.getId());
        ProductInSellList.Builder builder = ProductInSellList.Builder.getInstance();
        List<ProductInSellList> productList = products.stream().map(prod -> builder.buildProdct(prod)).collect(toList());


        /**
         * 首发
         */
        List<ProductInSellList> sellProds = productList.stream().filter(prod ->
                        prod.getProduct().getSaleStatus().equals(SaleStatus.FIRSTSELL.toString())
        ).collect(toList());
        if(sellProds.size() > 0){
            designerShopDto.addProductList(new DesignerProductDto("首发",ProductDto.build(sellProds)));
        }

        /**
         * 预售
         */
        List<ProductInSellList> preProds = productList.stream().filter(prod ->
                        prod.getProduct().getSaleStatus().equals(SaleStatus.PRESELL.toString())
        ).collect(toList());
        if(preProds.size() > 0){
            designerShopDto.addProductList(new DesignerProductDto("预售", ProductDto.build(preProds)));
        }

        /**
         * 热卖
         */
        List<ProductInSellList> normalProds = productList.stream().filter(prod ->
                        prod.getProduct().getSaleStatus().equals(SaleStatus.HOTSELL.toString())
        ).collect(toList());
        if(normalProds.size() > 0){
            designerShopDto.addProductList(new DesignerProductDto("热卖", ProductDto.build(normalProds)));
        }

        /**
         * 即将开售
         */
        List<ProductInSellList> planProds = productList.stream().filter(prod ->
                        prod.getProduct().getSaleStatus().equals(SaleStatus.PLANSELL.toString())
        ).collect(toList());
        if(planProds.size() > 0){
            designerShopDto.addProductList(new DesignerProductDto("即将开售", ProductDto.build(planProds)));
        }

        return designerShopDto;
    }

    public void addProductList(DesignerProductDto designerProductDto){
        productList.add(designerProductDto);
    }

    public int getDesignerId() {
        return designerId;
    }

    public void setDesignerId(int designerId) {
        this.designerId = designerId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DesignerProductDto> getProductList() {
        return productList;
    }

    public void setProductList(List<DesignerProductDto> productList) {
        this.productList = productList;
    }
}
