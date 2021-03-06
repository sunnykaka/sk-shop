package api.response.product;

import org.joda.time.DateTime;
import productcenter.dtos.ProductDetailBase;
import productcenter.dtos.SkuCandidate;
import productcenter.dtos.SkuInfo;
import productcenter.models.Html;
import productcenter.models.ProductPicture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by liubin on 15-8-21.
 */
public class ProductDetailDto {

    private ProductDto product;

    private List<String> htmlList;

    private List<ProductSpecDto> specList;

    private DesignerSizeDto designerSize;

    //CMS相关
    private DateTime exhibitionEndTime;

    //是否有打折
    protected boolean inDiscount;

    //是否收藏
    private boolean isFavorites = false;

    //是否订阅
    private boolean isBooked = false;

    //收藏数量
    private int favoritesNum;

    //key为skuPropertiesInDb, value为一个sku的DTO
    //该字段会在页面输出json字符串, 让前端可以根据skuPropertiesInDb得到skuInfo对象
    protected Map<String, SkuInfo> skuMap = new HashMap<>();

    //该字段用于在页面输出sku的列表,以供用户选择
    protected List<SkuCandidate> skuCandidateList = new ArrayList<>();

    //默认SKU
    protected SkuInfo defaultSku;

    protected List<String> productPictureList = new ArrayList<>();

    public ProductDto getProduct() {
        return product;
    }

    public void setProduct(ProductDto product) {
        this.product = product;
    }

    public List<String> getHtmlList() {
        return htmlList;
    }

    public void setHtmlList(List<String> htmlList) {
        this.htmlList = htmlList;
    }

    public List<ProductSpecDto> getSpecList() {
        return specList;
    }

    public void setSpecList(List<ProductSpecDto> specList) {
        this.specList = specList;
    }

    public DesignerSizeDto getDesignerSize() {
        return designerSize;
    }

    public void setDesignerSize(DesignerSizeDto designerSize) {
        this.designerSize = designerSize;
    }

    public boolean isFavorites() {
        return isFavorites;
    }

    public void setFavorites(boolean isFavorites) {
        this.isFavorites = isFavorites;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setIsBooked(boolean isBooked) {
        this.isBooked = isBooked;
    }

    public int getFavoritesNum() {
        return favoritesNum;
    }

    public void setFavoritesNum(int favoritesNum) {
        this.favoritesNum = favoritesNum;
    }

    public DateTime getExhibitionEndTime() {
        return exhibitionEndTime;
    }

    public void setExhibitionEndTime(DateTime exhibitionEndTime) {
        this.exhibitionEndTime = exhibitionEndTime;
    }

    public Map<String, SkuInfo> getSkuMap() {
        return skuMap;
    }

    public void setSkuMap(Map<String, SkuInfo> skuMap) {
        this.skuMap = skuMap;
    }

    public List<SkuCandidate> getSkuCandidateList() {
        return skuCandidateList;
    }

    public void setSkuCandidateList(List<SkuCandidate> skuCandidateList) {
        this.skuCandidateList = skuCandidateList;
    }

    public SkuInfo getDefaultSku() {
        return defaultSku;
    }

    public void setDefaultSku(SkuInfo defaultSku) {
        this.defaultSku = defaultSku;
    }

    public List<String> getProductPictureList() {
        return productPictureList;
    }

    public void setProductPictureList(List<String> productPictureList) {
        this.productPictureList = productPictureList;
    }

    public boolean isInDiscount() {
        return inDiscount;
    }

    public void setInDiscount(boolean inDiscount) {
        this.inDiscount = inDiscount;
    }

    public static ProductDetailDto build(ProductDetailBase base) {
        ProductDetailDto productDetailDto = new ProductDetailDto();
        productDetailDto.setDefaultSku(base.getDefaultSku());
        productDetailDto.setDesignerSize(DesignerSizeDto.build(base.getDesignerSize()));
        productDetailDto.setExhibitionEndTime(base.getExhibitionEndTime());
        productDetailDto.setFavorites(base.isFavorites());
        productDetailDto.setIsBooked(base.isBooked());
        productDetailDto.setFavoritesNum(base.getFavoritesNum());
        productDetailDto.setHtmlList(base.getHtmlList().stream().map(Html::getContent).collect(Collectors.toList()));
        productDetailDto.setProduct(ProductDto.build(base.getProduct()));
        productDetailDto.setSkuCandidateList(base.getSkuCandidateList());
        productDetailDto.setSkuMap(base.getSkuMap());
        productDetailDto.setProductPictureList(base.getProductPictureList().stream().map(ProductPicture::getPictureUrl).collect(Collectors.toList()));
        productDetailDto.setInDiscount(base.isInDiscount());
        List<ProductSpecDto> specList = base.getSpecList().stream().map(ProductSpecDto::build).collect(Collectors.toList());
        productDetailDto.setSpecList(specList);

        return productDetailDto;
    }
}
