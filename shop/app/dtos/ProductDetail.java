package dtos;

import common.exceptions.AppBusinessException;
import models.CmsExhibition;
import ordercenter.services.ValuationService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import productcenter.constants.SKUState;
import productcenter.models.*;
import productcenter.services.*;
import services.CmsService;
import usercenter.models.DesignerSize;
import usercenter.services.DesignerService;
import usercenter.utils.SessionUtils;
import utils.Global;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by liubin on 15-5-13.
 */
public class ProductDetail {

    //商品描述部分
    private Product product;

    private List<Html> htmlList;

    private ProductPicture productPicture;

    private DesignerSize designerSize;

    //评论总数量
    private int goodValuationCount;

    private int normalValuationCount;

    private int badValuationCount;

    private int allValuationCount;

    //CMS相关
    private boolean isInExhibition;

    //是否收藏
    private boolean isFavorites = false;

    //收藏数量
    private int favoritesNum;

    private DateTime exhibitionEndTime;

    //SKU
    //SkuDetail为StockKeepingUnit的包装类, 多出了库存字段
    private List<SkuDetail> skuDetailList = new ArrayList<>();

    //key为skuPropertiesInDb, value为一个sku的DTO
    //该字段会在页面输出json字符串, 让前端可以根据skuPropertiesInDb得到skuInfo对象
    private Map<String, SkuInfo> skuMap = new HashMap<>();

    //该字段用于在页面输出sku的列表,以供用户选择
    private List<SkuCandidate> skuCandidateList = new ArrayList<>();

    //默认SKU
    private SkuInfo defaultSku;

    private Seo seo;

    private List<Product> matchProductList;

    private ProductDetail(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public List<Html> getHtmlList() {
        return htmlList;
    }

    public ProductPicture getProductPicture() {
        return productPicture;
    }

    public DesignerSize getDesignerSize() {
        return designerSize;
    }

    public void setDesignerSize(DesignerSize designerSize) {
        this.designerSize = designerSize;
    }

    public int getGoodValuationCount() {
        return goodValuationCount;
    }

    public int getNormalValuationCount() {
        return normalValuationCount;
    }

    public int getBadValuationCount() {
        return badValuationCount;
    }

    public int getAllValuationCount() {
        return allValuationCount;
    }

    public boolean isInExhibition() {
        return isInExhibition;
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

    public List<SkuDetail> getSkuDetailList() {
        return skuDetailList;
    }

    public Map<String, SkuInfo> getSkuMap() {
        return skuMap;
    }

    public List<SkuCandidate> getSkuCandidateList() {
        return skuCandidateList;
    }

    public SkuInfo getDefaultSku() {
        return defaultSku;
    }

    public Seo getSeo() {
        return seo;
    }

    public void setSeo(Seo seo) {
        this.seo = seo;
    }

    public List<Product> getMatchProductList() {
        return matchProductList;
    }

    public void setMatchProductList(List<Product> matchProductList) {
        this.matchProductList = matchProductList;
    }

    public static class Builder {

        public static final String NOT_EXIST_KEY = "";

        private ProductDetail productDetail;
        private Integer defaultSkuId;

        private ProductPictureService productPictureService = Global.ctx.getBean(ProductPictureService.class);
        private ProductService productService = Global.ctx.getBean(ProductService.class);
        private ValuationService valuationService = Global.ctx.getBean(ValuationService.class);
        private CmsService cmsService = Global.ctx.getBean(CmsService.class);
        private SkuAndStorageService skuAndStorageService = Global.ctx.getBean(SkuAndStorageService.class);
        private CategoryPropertyService categoryPropertyService = Global.ctx.getBean(CategoryPropertyService.class);
        private ProductCollectService productCollectService = Global.ctx.getBean(ProductCollectService.class);
        private DesignerService designerService = Global.ctx.getBean(DesignerService.class);


        public static Builder newBuilder(Product product, Integer defaultSkuId) {
            Builder builder = new Builder();
            builder.defaultSkuId = defaultSkuId;
            builder.productDetail = new ProductDetail(product);
            return builder;
        }

        /**
         * 读取产品描述与图片
         * @return
         */
        public Builder buildProductDetail() {

            //读取主图
            List<ProductPicture> productPictures = productPictureService.queryProductPicturesByProductId(productDetail.product.getId());
            if(!productPictures.isEmpty()) {
                List<ProductPicture> collect = productPictures.stream().filter(ProductPicture::isMainPic).collect(Collectors.toList());
                if(!collect.isEmpty()) {
                    productDetail.productPicture = collect.get(0);
                }
            }

            //读取商品描述
            List<Html> htmlList = productService.queryHtmlByProductId(productDetail.product.getId());
            productDetail.htmlList = htmlList;

            //读尺码表
            DesignerSize ds = designerService.getDesignerSizeById(productDetail.product.getDesignerSizeId());
            productDetail.setDesignerSize(ds);

            return this;
        }

        /**
         * 读取评价数量
         * @return
         */
        public Builder buildValuation() {
            int[] counts = valuationService.countValuationGroupByPoint(productDetail.product.getId());
            productDetail.goodValuationCount = counts[0];
            productDetail.normalValuationCount = counts[1];
            productDetail.badValuationCount = counts[2];
            productDetail.allValuationCount = IntStream.of(counts).sum();

            return this;
        }

        /**
         * 读取CMS相关
         * @return
         */
        public Builder buildCms() {
            Optional<CmsExhibition> exhibition = cmsService.findExhibitionWithProdId(productDetail.product.getId());
            if(exhibition.isPresent()) {
                productDetail.isInExhibition = true;
                productDetail.exhibitionEndTime = exhibition.get().getEndTime();

            } else {
                productDetail.isInExhibition = false;
            }

            return this;
        }

        /**
         * 读取sku相关
         * @return
         */
        public Builder buildSku() {

            //构造skuDetailList
            List<SkuDetail> skuDetailList =
                    skuAndStorageService.querySkuListByProductId(productDetail.product.getId()).
                    stream().
                    filter(sku -> sku.getSkuState() == SKUState.NORMAL || sku.getId().equals(defaultSkuId)).
                    map(sku -> SkuDetail.Builder.newBuilder(sku).buildSku().build()).
                    collect(Collectors.toList());

            if(skuDetailList.isEmpty()) {
                play.Logger.warn(String.format("商品[id=%d]无有效的sku", productDetail.product.getId()));
                throw new AppBusinessException("您请求的商品没有找到, 请看看其他的吧");
            }

            productDetail.skuDetailList = skuDetailList;

            //构造skuMap
            Map<String, SkuInfo> skuMap =
                    skuDetailList.stream().
                            map(skuDetail -> new SkuInfo(
                                    skuDetail.getSku().getId(),
                                    skuDetail.getSku().getSkuCode(),
                                    skuDetail.getSku().getBarCode(),
                                    skuDetail.getSku().getPrice(),
                                    skuDetail.getSku().getMarketPrice(),
                                    skuDetail.getSku().getSkuPropertiesInDbWithOrder(),
                                    skuDetail.getStockQuantity(),
                                    skuDetail.getTradeMaxNumber(),
                                    skuDetail.getImageList())).
                            collect(Collectors.toMap(
                                    skuInfo -> StringUtils.isBlank(skuInfo.getSkuPropertiesInDb()) ? NOT_EXIST_KEY : skuInfo.getSkuPropertiesInDb(),
                                    skuInfo -> skuInfo));

            productDetail.skuMap = skuMap;

            //构造skuCandidateList
            List<SkuCandidate> skuCandidateList = new ArrayList<>();
            //key为propertyId, value为propertyId对应的所有value
            Map<Integer, Set<SkuProperty>> skuPropertyMap = new LinkedHashMap<>();
            //所有valueId的set
            Set<Integer> valueIdSet = new HashSet<>();

            //填充skuPropertyMap
            skuDetailList.stream().flatMap(skuDetail ->
                skuDetail.getSku().getSkuProperties().stream()
            ).forEach(skuProperty -> {
                valueIdSet.add(skuProperty.getValueId());
                skuPropertyMap.compute(skuProperty.getPropertyId(), (k, v) -> {
                    if (v == null) {
                        Set<SkuProperty> skuPropertySet = new LinkedHashSet<>();
                        skuPropertySet.add(skuProperty);
                        return skuPropertySet;
                    } else {
                        v.add(skuProperty);
                        return v;
                    }
                });
            });

            //读取property和propertyValue的排序字段
            Integer categoryId = productDetail.product.getCategoryId();
            List<CategoryProperty> categoryPropertyList = categoryPropertyService.
                    findCategoryProperty(categoryId, new ArrayList<>(skuPropertyMap.keySet()));
            List<CategoryPropertyValue> categoryPropertyValueList = categoryPropertyService.
                    findCategoryPropertyValue(categoryId, new ArrayList<>(skuPropertyMap.keySet()), new ArrayList<>(valueIdSet));

            //property的排序
            Map<Integer, Integer> propertyPriorityMap = categoryPropertyList.stream().
                    collect(Collectors.toMap(CategoryProperty::getPropertyId, CategoryProperty::getPriority));
            //propertyValue的排序
            Map<String, Integer> propertyValuePriorityMap = categoryPropertyValueList.stream().
                    collect(Collectors.toMap(cpv -> String.format("%d,%d", cpv.getPropertyId(), cpv.getValueId()), CategoryPropertyValue::getPriority));

            //填充skuCandidateList
            skuPropertyMap.forEach((k, v) -> {
                SkuProperty randomSkuProperty = v.stream().findFirst().get();
                SkuCandidate.SkuProp skuProp = new SkuCandidate.SkuProp(k, randomSkuProperty.getPropertyName(), propertyPriorityMap.getOrDefault(k, 0));
                Set<SkuCandidate.SkuValue> skuValueSet =
                        v.stream().
                        map(skuProperty -> new SkuCandidate.SkuValue(
                                skuProperty.getValueId(),
                                skuProperty.getPropertyValue(),
                                skuProperty.getPidvid(),
                                propertyValuePriorityMap.getOrDefault(String.format("%d,%d", k, skuProperty.getValueId()), 0))).
                        collect(Collectors.toSet());

                skuCandidateList.add(new SkuCandidate(skuProp, new ArrayList<>(skuValueSet)));
            });

            //为skuCandidateList排序, 按priority排序
            skuCandidateList.sort((o1, o2) -> new Integer(o1.getSkuProp().getPriority()).compareTo(o2.getSkuProp().getPriority()));
            skuCandidateList.forEach(skuCandidate ->
                    skuCandidate.getSkuValueList().sort((o1, o2) -> new Integer(o1.getPriority()).compareTo(o2.getPriority()))
            );

            productDetail.skuCandidateList = skuCandidateList;

            return this;
        }

        /**
         * 读取默认sku信息
         * @return
         */
        public Builder buildDefaultSku() {

            if(!productDetail.skuMap.isEmpty()) {
                boolean findDefaultSku = false;
                if(defaultSkuId != null) {
                    //前端请求的时候带了skuId
                    Optional<SkuInfo> first = productDetail.skuMap.values().stream().
                            filter(skuInfo -> skuInfo.getSkuId().equals(defaultSkuId)).findFirst();
                    if(first.isPresent()) {
                        findDefaultSku = true;
                        productDetail.defaultSku = first.get();
                    }
                }
                if(!findDefaultSku) {
                    //查找价格最低的sku
                    //查找库存数量大于0, 并且价格最低的sku
                    Optional<SkuInfo> min = productDetail.skuMap.values().stream().
                            filter(skuInfo -> skuInfo.getStockQuantity() > 0).
                            min(minSkuPriceComparator());
                    //如果min不存在，代表库存数量都为0，直接找价格最低的sku
                    if(!min.isPresent()) {
                        min = productDetail.skuMap.values().stream().
                                min(minSkuPriceComparator());
                    }

                    productDetail.defaultSku = min.get();
                }
            }

            return this;
        }

        private Comparator<SkuInfo> minSkuPriceComparator() {
            return (o1, o2) -> {
                if (productDetail.isInExhibition) {
                    return o1.getPrice().compareTo(o2.getPrice());
                } else {
                    return o1.getMarketPrice().compareTo(o2.getMarketPrice());
                }
            };
        }

        public Builder buildFavorites(){

            productDetail.setFavoritesNum(productCollectService.countProductCollect(productDetail.product.getId()));
            productDetail.setFavorites(productCollectService.isFavorites(SessionUtils.currentUser(),productDetail.product.getId()));

            return this;
        }

        /**
         * 搭配产品（商品）
         * @return
         */
        public Builder buildMatchProductList() {
            List<Product> matchProjectList = productService.queryMatchProductList(productDetail.product.getId());
            productDetail.setMatchProductList(matchProjectList);
            return this;
        }

        public ProductDetail build() {
            return productDetail;
        }
    }


}
