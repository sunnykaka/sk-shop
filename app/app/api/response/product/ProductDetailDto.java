package api.response.product;

import org.joda.time.DateTime;
import productcenter.dtos.ProductDetailBase;
import productcenter.dtos.SkuCandidate;
import productcenter.dtos.SkuInfo;
import productcenter.models.Html;
import productcenter.models.ProductSpec;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by liubin on 15-8-21.
 */
public class ProductDetailDto {

    private ProductDto product;

    private List<String> htmlList;

    private Map<String, String> specMap;

    private DesignerSizeDto designerSize;

    //CMS相关
    private boolean isInExhibition;

    //是否收藏
    private boolean isFavorites = false;

    //收藏数量
    private int favoritesNum;

    private DateTime exhibitionEndTime;

    //key为skuPropertiesInDb, value为一个sku的DTO
    //该字段会在页面输出json字符串, 让前端可以根据skuPropertiesInDb得到skuInfo对象
    protected Map<String, SkuInfo> skuMap = new HashMap<>();

    //该字段用于在页面输出sku的列表,以供用户选择
    protected List<SkuCandidate> skuCandidateList = new ArrayList<>();

    //默认SKU
    protected SkuInfo defaultSku;


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

    public Map<String, String> getSpecMap() {
        return specMap;
    }

    public void setSpecMap(Map<String, String> specMap) {
        this.specMap = specMap;
    }

    public DesignerSizeDto getDesignerSize() {
        return designerSize;
    }

    public void setDesignerSize(DesignerSizeDto designerSize) {
        this.designerSize = designerSize;
    }

    public boolean isInExhibition() {
        return isInExhibition;
    }

    public void setInExhibition(boolean isInExhibition) {
        this.isInExhibition = isInExhibition;
    }

    public boolean isFavorites() {
        return isFavorites;
    }

    public void setFavorites(boolean isFavorites) {
        this.isFavorites = isFavorites;
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

    public static ProductDetailDto build(ProductDetailBase base) {
        ProductDetailDto productDetailDto = new ProductDetailDto();
        productDetailDto.setDefaultSku(base.getDefaultSku());
        productDetailDto.setDesignerSize(DesignerSizeDto.build(base.getDesignerSize()));
        productDetailDto.setExhibitionEndTime(base.getExhibitionEndTime());
        productDetailDto.setFavorites(base.isFavorites());
        productDetailDto.setFavoritesNum(base.getFavoritesNum());
        productDetailDto.setHtmlList(base.getHtmlList().stream().map(Html::getContent).collect(Collectors.toList()));
        productDetailDto.setInExhibition(base.isInExhibition());
        productDetailDto.setProduct(ProductDto.build(base.getProduct()));
        productDetailDto.setSkuCandidateList(base.getSkuCandidateList());
        productDetailDto.setSkuMap(base.getSkuMap());
        productDetailDto.setSpecMap(base.getSpecList().
                stream().
                collect(Collectors.toMap(
                        ProductSpec::getName,
                        ProductSpec::getValue
                ))
        );

        return productDetailDto;
    }
}
