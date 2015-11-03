package api.response.shop;

import cmscenter.dtos.ProductDto;

import java.util.List;

/**
 * Created by zhb on 2015/10/28.
 */
public class DesignerProductDto {

    private String name;

    private List<ProductDto> products;

    public DesignerProductDto(){}

    public DesignerProductDto(String name,List<ProductDto> products){
        this.name = name;
        this.products = products;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProductDto> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDto> products) {
        this.products = products;
    }
}
