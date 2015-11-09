package cmscenter.services;

import cmscenter.constants.HomeFocusPageType;
import cmscenter.dtos.AppThemeDto;
import cmscenter.dtos.HomePageDto;
import cmscenter.dtos.ProductDto;
import cmscenter.models.AppTheme;
import cmscenter.models.HomeFocus;
import cmscenter.models.HomeNewProduct;
import common.services.GeneralDao;
import common.utils.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productcenter.models.Product;
import productcenter.models.ProductPicture;
import productcenter.models.StockKeepingUnit;
import productcenter.services.ProductPictureService;
import productcenter.services.ProductService;
import productcenter.services.SkuAndStorageService;
import usercenter.models.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by zhb on 2015/10/29.
 */
@Service
public class HomeFocusService {

    @Autowired
    GeneralDao generalDao;

    @Autowired
    AppThemeService appThemeService;

    @Autowired
    SkuAndStorageService skuService;

    @Autowired
    ProductService productService;

    @Autowired
    ProductPictureService productPictureService;

    @Autowired
    ThemeCollectService themeCollectService;

    public HomePageDto build(User user, String deviceId){

        HomePageDto homePage = new HomePageDto();

        //焦点图
        List<HomeFocus> focusList = findHomeFocusByPageType(HomeFocusPageType.focus);
        homePage.addFocusList(focusList);

        //最新专题
        AppTheme appTheme = appThemeService.getFristAppTheme();
        homePage.setAppTheme(AppThemeDto.build(appTheme,themeCollectService,user,deviceId));

        //精选图
        List<HomeFocus> selectionList = findHomeFocusByPageType(HomeFocusPageType.selection);
        homePage.addSelectionList(selectionList);

        //新品
        List<HomeNewProduct> newProductList = findHomeNewProductAll();
        for(HomeNewProduct homeNewProduct:newProductList){
            Product product = productService.getProductById(homeNewProduct.getProductId());
            ProductPicture productPicture = productPictureService.getMinorProductPictureByProductId(homeNewProduct.getProductId());
            StockKeepingUnit sku = skuService.querySkuByProductIdPriceSmall(homeNewProduct.getProductId());
            Money money = skuService.getSkuCurrentPrice(sku);

            ProductDto productDto = new ProductDto();
            productDto.setPrice(money.getAmountWithBigDecimal().toString());
            productDto.setProductId(homeNewProduct.getProductId());
            productDto.setProductName(product.getName());
            productDto.setProductUrl(productPicture.getPictureUrl());
            homePage.addProduct(productDto);
        }

        return homePage;
    }

    @Transactional(readOnly = true)
    public List<HomeFocus> findHomeFocusByPageType(HomeFocusPageType pageType){

        String jpql = "select h from HomeFocus h where 1=1 and h.pageType=:pageType order by h.priority desc";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("pageType", pageType);

        return generalDao.query(jpql, Optional.ofNullable(null), queryParams);

    }

    @Transactional(readOnly = true)
    public List<HomeNewProduct> findHomeNewProductAll(){

        String jpql = "select h from HomeNewProduct h where 1=1 order by h.priority desc";
        Map<String, Object> queryParams = new HashMap<>();

        return generalDao.query(jpql, Optional.ofNullable(null), queryParams);

    }

}
