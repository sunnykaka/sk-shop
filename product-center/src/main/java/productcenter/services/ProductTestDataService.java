package productcenter.services;

import com.google.common.collect.Lists;
import common.services.GeneralDao;
import common.utils.DateUtils;
import common.utils.Money;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import play.Logger;
import productcenter.constants.*;
import productcenter.models.*;
import productcenter.util.PidVid;
import usercenter.models.Designer;
import usercenter.models.DesignerSize;
import usercenter.services.NationService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by liubin on 15-8-27.
 */
@Service
public class ProductTestDataService {

    @Autowired
    NationService nationService;

    @Autowired
    GeneralDao generalDao;

    @Autowired
    PropertyAndValueService propertyAndValueService;

    @Autowired
    CategoryPropertyService categoryPropertyService;

    @Autowired
    BrandService brandService;

    @Autowired
    ProductService productService;

    @Autowired
    ProductPropertyService productPropertyService;

    @Autowired
    SkuAndStorageService skuAndStorageService;


    /**
     *
     * 初始化一个上架的产品对象并持久化到数据库,会生成2个sku并且库存都为1,其他产品信息都是随机的(名称,描述等).
     * 该产品在初始化之前会自动初始化随机的类目,设计师,设计师尺码.
     * 该方法不会依赖其他数据.除了国籍信息.
     *
     * @return
     */
    @Transactional
    public Product initProduct() {

        Product product = new Product();

        Designer designer = initDesigner();
        product.setCustomerId(designer.getId());
        Value value = propertyAndValueService.getValueByName(designer.getName());
        product.setBrandId(value.getId());

        //产品初始化2个sku,分别为红色大号和蓝色大号
        Map<String, List<String>> categoryValueMap = new LinkedHashMap<>();
        categoryValueMap.put("test颜色", Lists.newArrayList("test红色", "test蓝色"));
        categoryValueMap.put("test尺寸", Lists.newArrayList("test大号"));
        ProductCategory category = initCategory(categoryValueMap);

        product.setCategoryId(category.getId());
        product.setDescription("产品推荐");
        DesignerSize designerSize = initDesignerSize(designer);
        product.setDesignerSizeId(designerSize.getId());
        product.setEnName(RandomStringUtils.randomAlphabetic(6) + "enName");
        product.setModelInfo("模特信息");
        product.setName(RandomStringUtils.randomAlphabetic(6) + "名称");
        product.setOnline(true);
        product.setOnlineTime(DateUtils.current());
        product.setOnLineTimeLong(0L);
        product.setOfflineTime(DateUtils.current());
        product.setProductCode(RandomStringUtils.randomAlphanumeric(8));
        product.setRecommendDesc("推荐描述");
        product.setStoreStrategy(StoreStrategy.PayStrategy);
        product.setTagType(ProductTagType.DEFAULT);
        product.setIsDelete(false);
        product.setSaleStatus(SaleStatus.HOTSELL.name());

        generalDao.persist(product);

        initSku(value.getId(), product, category, categoryValueMap);
        initProductPicture(product);

        return product;
    }

    private void initProductPicture(Product product) {
        ProductPicture productPicture = new ProductPicture();
        productPicture.setProductId(product.getId());
        productPicture.setOriginalName("347_main_3.jpg");
        productPicture.setName("347_main_3.jpg");
        productPicture.setMainPic(true);
        productPicture.setMinorPic(false);
        productPicture.setPictureUrl("http://imgp.fashiongeeker.com/47/347_main_3.jpg");
        productPicture.setPictureLocalUrl("http://imgp.fashiongeeker.com/47/347_main_3.jpg");
        generalDao.persist(productPicture);

        productPicture = new ProductPicture();
        productPicture.setProductId(product.getId());
        productPicture.setOriginalName("566_main_1.jpg");
        productPicture.setName("566_main_1.jpg");
        productPicture.setMainPic(false);
        productPicture.setMinorPic(true);
        productPicture.setPictureUrl("http://imgp.fashiongeeker.com/66/566_main_1.jpg");
        productPicture.setPictureLocalUrl("http://imgp.fashiongeeker.com/66/566_main_1.jpg");
        generalDao.persist(productPicture);
    }

    /**
     * 初始化sku信息
     * @param brandId
     * @param product
     * @param category
     * @param categoryValueMap
     */
    private void initSku(Integer brandId, Product product, ProductCategory category, Map<String, List<String>> categoryValueMap) {

        Logger.debug(String.format("init sku, brandId[%d], product[id=%d], category[id=%d], categoryValueMap[%s]",
                brandId, product.getId(), category.getId(), categoryValueMap.toString()));

        //首先查询该类目下都有哪些销售属性
        List<CategoryProperty> categoryProperties = categoryPropertyService.findCategoryProperty(category.getId(), PropertyType.SELL_PROPERTY);

        Map<CategoryProperty, List<Integer>> selectedSkuValueMap = new LinkedHashMap<>();
        for (CategoryProperty categoryProperty : categoryProperties) {
            String propertyName = categoryProperty.getProperty().getName();
            List<String> categoryValueNames = categoryValueMap.get(propertyName);
            if(categoryValueNames != null && !categoryValueNames.isEmpty()) {
                //选择的sku属性id
                List<Integer> valueIdList =
                        categoryValueNames.
                                stream().
                                map(valueName -> {
                                    Value value = propertyAndValueService.getValueByName(valueName);
                                    if (value == null) {
                                        Logger.warn(String.format("根据name[%s]查询value对象返回null", valueName));
                                        return null;
                                    }
                                    return value.getId();
                                }).
                                filter(x -> x != null).
                                collect(Collectors.toList());

                if(!categoryProperty.isMultiValue() && valueIdList.size() > 1) {
                    String errorInfo = String.format("CategoryProperty[id=%d]不是多值属性, 但是valueIdList数量大于1, valueIdList[%s]",
                            categoryProperty.getId(), valueIdList.toString());
                    Logger.error(errorInfo);
                    throw new IllegalStateException(errorInfo);
                }

                selectedSkuValueMap.compute(categoryProperty, (k, v) -> {
                    if (v == null) {
                        return valueIdList;
                    } else {
                        v.addAll(valueIdList);
                        return v;
                    }
                });
            }

        }

        PidVid[] keyAndSellPidVid = parsePidVid(brandId, selectedSkuValueMap);
        PidVid key = keyAndSellPidVid[0];
        PidVid sell = keyAndSellPidVid[1];

        productPropertyService.createProductProperty(product.getId(), key);
        productPropertyService.createProductProperty(product.getId(), sell);

        List<StockKeepingUnit> skuList = skuAndStorageService.createSkuByDescartes(sell, product);
        createSkuStorage(skuList);

    }

    /**
     *
     * @param skuList
     */
    private void createSkuStorage(List<StockKeepingUnit> skuList) {

        for(StockKeepingUnit sku : skuList) {
            //首发价的区间为(0.5, +∞), 售卖价的区间为(1, +∞)
            double price = 0.5d + new java.util.Random().nextDouble();
            double marketPrice = price + 0.5d;
            sku.setPrice(Money.valueOf(price));
            sku.setMarketPrice(Money.valueOf(marketPrice));
            sku.setSkuState(SKUState.NORMAL);
            generalDao.persist(sku);

            SkuStorage skuStorage = new SkuStorage();
            skuStorage.setSkuId(sku.getId());
            skuStorage.setStockQuantity(10);
            skuStorage.setTradeMaxNumber(10);
            generalDao.persist(skuStorage);
        }


    }

    /**
     * 分析选中的类目属性值,得到销售属性和关键属性对象
     * @param brandId 品牌属性值ID
     * @param categoryProperties key-类目属性名称,value-选中的类目属性值ID集合
     * @return
     */
    private PidVid[] parsePidVid(Integer brandId, Map<CategoryProperty, List<Integer>> categoryProperties) {
        PidVid key = new PidVid(PropertyType.KEY_PROPERTY);
        PidVid sell = new PidVid(PropertyType.SELL_PROPERTY);
        Property property = propertyAndValueService.getPropertyByName("品牌");

        for(Map.Entry<CategoryProperty, List<Integer>> entry : categoryProperties.entrySet()) {
            CategoryProperty categoryProperty = entry.getKey();
            List<Integer> valueIdList = entry.getValue();
            int pid = categoryProperty.getPropertyId();
            if (pid == property.getId()) { //品牌属性不需要处理，因为品牌这个属性单独处理了
                continue;
            }

            if (valueIdList != null) {
                for (Integer valueId : valueIdList) {
                    if (categoryProperty.getPropertyType() == PropertyType.KEY_PROPERTY) {
                        key.add(pid, valueId, categoryProperty.isMultiValue());
                    }
                    if (categoryProperty.getPropertyType() == PropertyType.SELL_PROPERTY) {
                        sell.add(pid, valueId, categoryProperty.isMultiValue());
                    }
                }
            }
        }

        key.add(property.getId(), brandId, false);

        return new PidVid[]{key, sell};
    }


    /**
     * 初始化设计师尺码表,名称随机,表格内容固定
     * @param designer
     * @return
     */
    @Transactional
    public DesignerSize initDesignerSize(Designer designer) {

        DesignerSize designerSize = new DesignerSize();
        designerSize.setDesignerId(designer.getId());
        designerSize.setName(RandomStringUtils.randomAlphabetic(6) + "名称");
        designerSize.setPrompt("prompt提示");
        String content = "<table style=\"width:100%;\" cellpadding=\"2\" cellspacing=\"0\" border=\"1\" bordercolor=\"#000000\">\n" +
                "\t<tbody>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>\n" +
                "\t\t\t\t原码<br />\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td>\n" +
                "\t\t\t\t裙长<br />\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td>\n" +
                "\t\t\t\t腰围<br />\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td>\n" +
                "\t\t\t\t臀围<br />\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>\n" +
                "\t\t\t\t34\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td>\n" +
                "\t\t\t\t35\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td>\n" +
                "\t\t\t\t36\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td>\n" +
                "\t\t\t\t37\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>\n" +
                "\t\t\t\t38\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td>\n" +
                "\t\t\t\t369\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td>\n" +
                "\t\t\t\t40\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td>\n" +
                "\t\t\t\t41\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>\n" +
                "\t\t\t\t42\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td>\n" +
                "\t\t\t\t43\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td>\n" +
                "\t\t\t\t44\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td>\n" +
                "\t\t\t\t45\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t</tbody>\n" +
                "</table>\n" +
                "<br />";
        designerSize.setContent(content);

        generalDao.persist(designerSize);

        return designerSize;
    }

    /**
     * 初始化类目,类目下的属性都是销售属性,除了品牌
     * @param categoryValueMap key-类目属性名称, value-该类目属性可选的值
     * @return
     */
    @Transactional
    public ProductCategory initCategory(Map<String, List<String>> categoryValueMap) {
        ProductCategory pc = new ProductCategory();
        pc.setName(RandomStringUtils.randomAlphabetic(6) + "类目");
        pc.setParentId(-1);
        generalDao.persist(pc);

        Map<String, List<String>> newMap = new LinkedHashMap<>();
        newMap.put("品牌", null);
        newMap.putAll(categoryValueMap);

        createCategoryPropertyAndValue(pc.getId(), newMap);

        return pc;
    }

    private void createCategoryPropertyAndValue(Integer categoryId, Map<String, List<String>> categoryValueMap) {

        int i = 0;
        for(Map.Entry<String, List<String>> entry : categoryValueMap.entrySet()) {
            String propertyName = entry.getKey();
            List<String> propertyValueList = entry.getValue();

            CategoryProperty categoryProperty = new CategoryProperty();
            categoryProperty.setCategoryId(categoryId);
            Property property = propertyAndValueService.createPropertyIfNotExist(propertyName);
            categoryProperty.setPropertyId(property.getId());
            categoryProperty.setProperty(property);
            if("品牌".equals(propertyName)) {
                categoryProperty.setPropertyType(PropertyType.KEY_PROPERTY);
                categoryProperty.setMultiValue(false);
            } else {
                categoryProperty.setPropertyType(PropertyType.SELL_PROPERTY);
                categoryProperty.setMultiValue(true);
            }
            categoryProperty.setPriority(categoryValueMap.size() - ++i);
            categoryPropertyService.createCategoryProperty(categoryProperty);

            if(propertyValueList != null) {
                int j = 0;
                for(String valueName : propertyValueList) {
                    Value value = propertyAndValueService.createValueIfNotExist(valueName);

                    CategoryPropertyValue cpv = new CategoryPropertyValue();
                    cpv.setCategoryId(categoryId);
                    cpv.setPropertyId(property.getId());
                    cpv.setValueId(value.getId());
                    cpv.setDeleted(false);
                    cpv.setPriority(propertyValueList.size() - ++j);

                    generalDao.persist(cpv);
                }
            }

        }



    }

    /**
     * 初始化设计师,会自动初始化品牌
     * @return
     */
    @Transactional
    public Designer initDesigner() {

        Designer designer = new Designer();
        designer.setDescription("推荐理由");
        designer.setIsDelete(false);
        designer.setIsPublished(true);
        designer.setName(RandomStringUtils.randomAlphabetic(6) + "设计师");
        designer.setNationId(nationService.findByNameZh("中国").get(0).getId());
        designer.setPriority(0);

        brandService.createDesignerAndBrand(designer);

        return designer;
    }



}
