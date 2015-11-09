package api.response.shop;

import productcenter.models.Product;
import productcenter.models.ProductPicture;
import productcenter.services.ProductPictureService;
import productcenter.services.ProductService;
import usercenter.constants.DesignerPictureType;
import usercenter.models.Designer;
import usercenter.models.DesignerPicture;
import usercenter.services.DesignerService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhb on 2015/11/5.
 */
public class DesignerListDto {

    private int designerId;

    private String name;

    private String pic;

    private List<String> productPicList = new ArrayList<>();

    public static List<DesignerListDto> buildList(List<Designer> designerList,DesignerService designerService, ProductService productService, ProductPictureService productPictureService){

        List<DesignerListDto> list = new ArrayList<>();
        for(Designer designer:designerList){
            DesignerListDto designerListDto = new DesignerListDto();
            designerListDto.setDesignerId(designer.getId());
            designerListDto.setName(designer.getName());
            DesignerPicture designerPicture = designerService.getDesignerPicByType(designer.getId(), DesignerPictureType.WhiteBGPic);
            if(designerPicture != null){
                designerListDto.setPic(designerPicture.getPictureUrl());
            }
            List<Product> productList = new ArrayList<>();
            productList.addAll(productService.queryThreeProduct(designer.getId()));
            for(Product product:productList){
                ProductPicture pp = productPictureService.getMinorProductPictureByProductId(product.getId());
                if(pp != null){
                    designerListDto.productPicList.add(pp.getPictureUrl());
                }
            }
            list.add(designerListDto);

        }

        return list;
    }

    public int getDesignerId() {
        return designerId;
    }

    public void setDesignerId(int designerId) {
        this.designerId = designerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public List<String> getProductPicList() {
        return productPicList;
    }

    public void setProductPicList(List<String> productPicList) {
        this.productPicList = productPicList;
    }
}
