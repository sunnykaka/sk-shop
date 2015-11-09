package cmscenter.dtos;

import cmscenter.models.HomeFocus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhb on 2015/10/29.
 */
public class HomePageDto {

    private List<HomeFocusDto> focusList = new ArrayList<>();

    private AppThemeDto appTheme;

    private List<HomeFocusDto> selectionList= new ArrayList<>();

    private List<ProductDto> productList= new ArrayList<>();

    public void addFocusList(List<HomeFocus> homeFocuses){
        for(HomeFocus homeFocus:homeFocuses){
            focusList.add(HomeFocusDto.build(homeFocus));
        }
    }

    public void addSelectionList(List<HomeFocus> homeFocuses){
        for(HomeFocus homeFocus:homeFocuses){
            selectionList.add(HomeFocusDto.build(homeFocus));
        }
    }

    public void addProduct(ProductDto productDto){
        productList.add(productDto);
    }

    public List<HomeFocusDto> getFocusList() {
        return focusList;
    }

    public void setFocusList(List<HomeFocusDto> focusList) {
        this.focusList = focusList;
    }

    public AppThemeDto getAppTheme() {
        return appTheme;
    }

    public void setAppTheme(AppThemeDto appTheme) {
        this.appTheme = appTheme;
    }

    public List<HomeFocusDto> getSelectionList() {
        return selectionList;
    }

    public void setSelectionList(List<HomeFocusDto> selectionList) {
        this.selectionList = selectionList;
    }

    public List<ProductDto> getProductList() {
        return productList;
    }

    public void setProductList(List<ProductDto> productList) {
        this.productList = productList;
    }
}
