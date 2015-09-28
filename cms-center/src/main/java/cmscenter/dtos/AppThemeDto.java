package cmscenter.dtos;

import cmscenter.models.AppTheme;
import cmscenter.models.AppThemeContent;
import cmscenter.services.AppThemeService;
import cmscenter.services.ThemeCollectService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import productcenter.models.Product;
import productcenter.models.ProductPicture;
import productcenter.services.ProductPictureService;
import productcenter.services.ProductService;
import usercenter.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhb on 2015/9/15.
 */
public class AppThemeDto {

    private Integer id;

    private String startTime;

    private int themeNo;

    private int num;

    private String picUrl;

    private String name;

    private boolean isFavorites = false;

    private List<AppThemeContentDto> appThemeContentDtoList = new ArrayList<>();

    private List<ProductSimpleDto> productSimpleDtoList = new ArrayList<>();

    public static AppThemeDto build(AppTheme appTheme,AppThemeService appThemeService,
                                    ThemeCollectService themeCollectService,ProductService productService,
                                    ProductPictureService productPictureService,User user,String deviceId){

        AppThemeDto appThemeDto = new AppThemeDto();
        appThemeDto.setId(appTheme.getId());
        appThemeDto.setName(appTheme.getName());
        appThemeDto.setStartTime(appTheme.getStartTime().toString("yyyy-MM-dd"));
        appThemeDto.setPicUrl(appTheme.getPicUrl());
        appThemeDto.setThemeNo(appTheme.getThemeNo());
        appThemeDto.setIsFavorites(themeCollectService.isFavorites(appTheme.getId(),user,deviceId));
        appThemeDto.setNum(appTheme.getBaseNum() + themeCollectService.countThemeCollect(appTheme.getId()));

        if(StringUtils.isNotEmpty(appTheme.getProducts())){
            String[] productIds = appTheme.getProducts().split(",");
            for(String productId:productIds) {
                Product product = productService.getProductById(Integer.parseInt(productId));
                if(product != null){
                    ProductPicture productPicture = productPictureService.getMinorProductPictureByProductId(product.getId());
                    appThemeDto.addProductSimpleDto(new ProductSimpleDto(product.getId(), product.getName(),productPicture ==null?"":productPicture.getPictureUrl()));
                }
            }
        }

        List<AppThemeContent> appThemeContentList = appThemeService.getAppThemeContentByThemeId(appTheme.getId());
        for(AppThemeContent appThemeContent:appThemeContentList){
            appThemeDto.addAppThemeContentDto(AppThemeContentDto.build(appThemeContent));
        }

        return appThemeDto;

    }

    public static AppThemeDto build(AppTheme appTheme,AppThemeService appThemeService){
        AppThemeDto appThemeDto = new AppThemeDto();
        appThemeDto.setId(appTheme.getId());
        appThemeDto.setName(appTheme.getName());
        appThemeDto.setStartTime(appTheme.getStartTime().toString("yyyy-MM-dd"));
        appThemeDto.setPicUrl(appTheme.getPicUrl());
        appThemeDto.setThemeNo(appTheme.getThemeNo());
        List<AppThemeContent> appThemeContentList = appThemeService.getAppThemeContentByThemeId(appTheme.getId());
        for(AppThemeContent appThemeContent:appThemeContentList){
            appThemeDto.addAppThemeContentDto(AppThemeContentDto.build(appThemeContent));
        }

        return appThemeDto;
    }

    public void addProductSimpleDto(ProductSimpleDto productSimpleDto){
        productSimpleDtoList.add(productSimpleDto);
    }

    public void addAppThemeContentDto(AppThemeContentDto appThemeContentDto){
        appThemeContentDtoList.add(appThemeContentDto);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getThemeNo() {
        return themeNo;
    }

    public void setThemeNo(int themeNo) {
        this.themeNo = themeNo;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFavorites() {
        return isFavorites;
    }

    public void setIsFavorites(boolean isFavorites) {
        this.isFavorites = isFavorites;
    }

    public List<AppThemeContentDto> getAppThemeContentDtoList() {
        return appThemeContentDtoList;
    }

    public void setAppThemeContentDtoList(List<AppThemeContentDto> appThemeContentDtoList) {
        this.appThemeContentDtoList = appThemeContentDtoList;
    }

    public List<ProductSimpleDto> getProductSimpleDtoList() {
        return productSimpleDtoList;
    }

    public void setProductSimpleDtoList(List<ProductSimpleDto> productSimpleDtoList) {
        this.productSimpleDtoList = productSimpleDtoList;
    }
}
