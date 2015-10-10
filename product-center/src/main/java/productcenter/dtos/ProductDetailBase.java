package productcenter.dtos;

import common.exceptions.AppBusinessException;
import common.services.GeneralDao;
import common.utils.play.BaseGlobal;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import productcenter.constants.SKUState;
import productcenter.constants.SaleStatus;
import productcenter.models.*;
import productcenter.services.*;
import usercenter.models.DesignerSize;
import usercenter.models.User;
import usercenter.services.DesignerService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by liubin on 15-5-13.
 */
public class ProductDetailBase {

    //商品描述部分
    protected Product product;

    protected List<Html> htmlList;

    protected List<ProductSpec> specList = new ArrayList<>();

    //protected ProductPicture productPicture;
    protected List<ProductPicture> productPictureList = new ArrayList<>();

    protected DesignerSize designerSize;

    //是否首发或者预售
    protected boolean isInExhibition;

    //是否收藏
    protected boolean isFavorites = false;

    //收藏数量
    protected int favoritesNum;

    protected DateTime exhibitionEndTime;

    //SKU
    //SkuDetail为StockKeepingUnit的包装类, 多出了库存字段
    protected List<SkuDetail> skuDetailList = new ArrayList<>();

    //key为skuPropertiesInDb, value为一个sku的DTO
    //该字段会在页面输出json字符串, 让前端可以根据skuPropertiesInDb得到skuInfo对象
    protected Map<String, SkuInfo> skuMap = new HashMap<>();

    //该字段用于在页面输出sku的列表,以供用户选择
    protected List<SkuCandidate> skuCandidateList = new ArrayList<>();

    //默认SKU
    protected SkuInfo defaultSku;

    protected List<Product> matchProductList;

    protected ProductDetailBase(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public List<Html> getHtmlList() {
        return htmlList;
    }

    public List<ProductSpec> getSpecList() {
        return specList;
    }

    public DesignerSize getDesignerSize() {
        return designerSize;
    }

    public void setDesignerSize(DesignerSize designerSize) {
        this.designerSize = designerSize;
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

    public List<ProductPicture> getProductPictureList() {
        return productPictureList;
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

    public List<Product> getMatchProductList() {
        return matchProductList;
    }

    public void setMatchProductList(List<Product> matchProductList) {
        this.matchProductList = matchProductList;
    }

    public static class Builder {

        public static final String NOT_EXIST_KEY = "";

        private ProductDetailBase productDetailBase;
        private Integer defaultSkuId;
        private User user;  //可能为null

        private ProductPictureService productPictureService = BaseGlobal.ctx.getBean(ProductPictureService.class);
        private ProductService productService = BaseGlobal.ctx.getBean(ProductService.class);
        private SkuAndStorageService skuAndStorageService = BaseGlobal.ctx.getBean(SkuAndStorageService.class);
        private CategoryPropertyService categoryPropertyService = BaseGlobal.ctx.getBean(CategoryPropertyService.class);
        private ProductCollectService productCollectService = BaseGlobal.ctx.getBean(ProductCollectService.class);
        private DesignerService designerService = BaseGlobal.ctx.getBean(DesignerService.class);


        public static Builder newBuilder(Product product, Integer defaultSkuId, User user) {
            Builder builder = new Builder();
            builder.defaultSkuId = defaultSkuId;
            builder.productDetailBase = new ProductDetailBase(product);
            builder.user = user;
            return builder;
        }

        /**
         * 读取产品描述与图片
         *
         * @return
         */
        public Builder buildProductDetail() {

            //读取主图
            List<ProductPicture> productPictures = productPictureService.queryProductPicturesByProductId(productDetailBase.product.getId());
            //productDetail.productPictureList.addAll(productPictures);
            //需求变动。改为多主图轮播
            if (!productPictures.isEmpty()) {
                List<ProductPicture> collect = productPictures.stream().filter(ProductPicture::isMainPic).collect(Collectors.toList());
                if (!collect.isEmpty()) {
                    //productDetail.productPicture = collect.get(0);
                    productDetailBase.productPictureList.addAll(collect);
                }
            }

            //读取商品描述
            productDetailBase.htmlList = productService.queryHtmlByProductId(productDetailBase.product.getId());

            //读尺码表
            DesignerSize ds = designerService.getDesignerSizeById(productDetailBase.product.getDesignerSizeId());
            productDetailBase.setDesignerSize(ds);

            //读取商品说明
            productDetailBase.specList = productService.querySpecByProductId(productDetailBase.product.getId());

            return this;
        }

        /**
         * 读取CMS相关
         *
         * @return
         */
        public Builder buildCms() {

            //FIXME cms重做前的临时办法
            String sql = "select endTime from cms_exhibition where (beginTime  <= Now() AND endTime > Now())  and  id in ( select exhibitionId from exhibition_item where prodId = ?1)";
            List<java.sql.Timestamp> list = BaseGlobal.ctx.getBean(GeneralDao.class).getEm().createNativeQuery(sql).setParameter(1, productDetailBase.product.getId()).getResultList();

            Optional<DateTime> exhibitionEndTime = (list != null && list.size() > 0) ? Optional.of(new DateTime(list.get(0).getTime())) : Optional.empty();

            if (exhibitionEndTime.isPresent()) {
                productDetailBase.isInExhibition = productDetailBase.product.getSaleStatus().equals(SaleStatus.FIRSTSELL.toString()) ||
                        productDetailBase.product.getSaleStatus().equals(SaleStatus.PLANSELL.toString());
                productDetailBase.exhibitionEndTime = exhibitionEndTime.get();
            }

            return this;
        }

        /**
         * 读取sku相关
         *
         * @return
         */
        public Builder buildSku() {

            //构造skuDetailList
            List<SkuDetail> skuDetailList =
                    skuAndStorageService.querySkuListByProductId(productDetailBase.product.getId()).
                            stream().
                            filter(sku -> sku.getSkuState() == SKUState.NORMAL || sku.getId().equals(defaultSkuId)).
                            map(sku -> SkuDetail.Builder.newBuilder(sku).buildSku().build()).
                            collect(Collectors.toList());

            if (skuDetailList.isEmpty()) {
                play.Logger.warn(String.format("商品[id=%d]无有效的sku", productDetailBase.product.getId()));
                throw new AppBusinessException("您请求的商品没有找到, 请看看其他的吧");
            }

            productDetailBase.skuDetailList = skuDetailList;

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
                                    skuDetail.getImageList(),
                                    skuDetail.getSku().isDefaultSku())).
                            collect(Collectors.toMap(
                                    skuInfo -> StringUtils.isBlank(skuInfo.getSkuPropertiesInDb()) ? NOT_EXIST_KEY : skuInfo.getSkuPropertiesInDb(),
                                    skuInfo -> skuInfo));

            productDetailBase.skuMap = skuMap;

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
            Integer categoryId = productDetailBase.product.getCategoryId();
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

            productDetailBase.skuCandidateList = skuCandidateList;

            return this;
        }

        /**
         * 读取默认sku信息
         *
         * @return
         */
        public Builder buildDefaultSku() {

            if (!productDetailBase.skuMap.isEmpty()) {
                Optional<SkuInfo> defaultSku = Optional.empty();
                if (defaultSkuId != null) {
                    //前端请求的时候带了skuId
                    defaultSku = productDetailBase.skuMap.values().stream().
                            filter(skuInfo -> skuInfo.getSkuId().equals(defaultSkuId)).findFirst();
                }
                if (!defaultSku.isPresent()) {
                    //查找defaultSku为true的sku
                    defaultSku = productDetailBase.skuMap.values().stream().
                            filter(SkuInfo::isDefaultSku).findFirst();

                }
                if (!defaultSku.isPresent()) {
                    //查找价格最低的sku
                    //查找库存数量大于0, 并且价格最低的sku
                    Optional<SkuInfo> min = productDetailBase.skuMap.values().stream().
                            filter(skuInfo -> skuInfo.getStockQuantity() > 0).
                            min(minSkuPriceComparator());
                    //如果min不存在，代表库存数量都为0，直接找价格最低的sku
                    if (!min.isPresent()) {
                        min = productDetailBase.skuMap.values().stream().
                                min(minSkuPriceComparator());
                    }

                    defaultSku = min;
                }

                productDetailBase.defaultSku = defaultSku.orElse(null);

            }

            return this;
        }

        private Comparator<SkuInfo> minSkuPriceComparator() {
            return (o1, o2) -> {
                if (productDetailBase.isInExhibition) {
                    return o1.getPrice().compareTo(o2.getPrice());
                } else {
                    return o1.getMarketPrice().compareTo(o2.getMarketPrice());
                }
            };
        }

        public Builder buildFavorites() {

            productDetailBase.setFavoritesNum(productCollectService.countProductCollect(productDetailBase.product.getId()));
            productDetailBase.setFavorites(productCollectService.isFavorites(user, productDetailBase.product.getId()));

            return this;
        }

        /**
         * 搭配产品（商品）
         *
         * @return
         */
        public Builder buildMatchProductList() {
            List<Product> matchProjectList = productService.queryMatchProductList(productDetailBase.product.getId());
            for (Product product : matchProjectList) { //先这样，以后再优化
                //读取副图
                List<ProductPicture> productPictures = productPictureService.queryProductPicturesByProductId(product.getId());
                if (!productPictures.isEmpty()) {
                    List<ProductPicture> collect = productPictures.stream().filter(ProductPicture::isMinorPic).collect(Collectors.toList());
                    if (!collect.isEmpty()) {
                        product.setMainPic(collect.get(0).getPictureUrl());
                    }
                }
            }
            productDetailBase.setMatchProductList(matchProjectList);
            return this;
        }

        public ProductDetailBase build() {
            return productDetailBase;
        }
    }


}
