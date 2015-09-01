package productcenter.services;

import com.google.common.collect.Lists;
import common.services.GeneralDao;
import common.utils.DateUtils;
import common.utils.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import play.Logger;
import productcenter.constants.ProductTagType;
import productcenter.constants.PropertyType;
import productcenter.constants.StoreStrategy;
import productcenter.models.*;
import productcenter.util.PidVid;
import productcenter.util.PidVidJsonUtil;
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



    @Transactional
    public Product initProduct() {

        Product product = new Product();

        Designer designer = initDesigner();
        product.setCustomerId(designer.getId());
        Value value = propertyAndValueService.getValueByName(designer.getName());
        product.setBrandId(value.getId());

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

        generalDao.persist(product);

        initSku(value.getId(), product, category, categoryValueMap);

        return product;
    }

    private void initSku(Integer brandId, Product product, ProductCategory category, Map<String, List<String>> categoryValueMap) {

        Logger.debug(String.format("init sku, brandId[%d], product[id=%d], category[id=%d], categoryValueMap[%s]",
                brandId, product.getId(), category.getId(), categoryValueMap.toString()));

        List<CategoryProperty> categoryProperties = categoryPropertyService.findCategoryProperty(category.getId(), PropertyType.SELL_PROPERTY);

        Map<CategoryProperty, List<Integer>> selectedSkuValueMap = new LinkedHashMap<>();
        for (CategoryProperty categoryProperty : categoryProperties) {
            String propertyName = categoryProperty.getProperty().getName();
            List<String> categoryValueNames = categoryValueMap.get(propertyName);
            if(categoryValueNames != null && !categoryValueNames.isEmpty()) {

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

        skuAndStorageService.createSkuByDescartes(sell, product);

    }

    private PidVid[] parsePidVid(Integer brandId, Map<CategoryProperty, List<Integer>> categoryProperties) {
        PidVid key = new PidVid(PropertyType.KEY_PROPERTY);
        PidVid sell = new PidVid(PropertyType.SELL_PROPERTY);
        Property property = propertyAndValueService.getPropertyByName("品牌");

        for(Map.Entry<CategoryProperty, List<Integer>> entry : categoryProperties.entrySet()) {
            CategoryProperty categoryProperty = entry.getKey();
            List<Integer> valueIdList = entry.getValue();
            int pid = categoryProperty.getPropertyId();//遍历这个类目下的所有类目属性，通过属性ID去request中得到值ID
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
