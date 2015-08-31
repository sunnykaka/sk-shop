package productcenter.services;

import com.google.common.collect.Lists;
import common.services.GeneralDao;
import common.utils.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        product.setProductCode(RandomStringUtils.randomAlphanumeric(8));
        product.setRecommendDesc("推荐描述");
        product.setStoreStrategy(StoreStrategy.NormalStrategy);
        product.setTagType(ProductTagType.DEFAULT);

        initSku(product, category, categoryValueMap);

        return product;
    }

    private void initSku(Product product, ProductCategory category, Map<String, List<String>> categoryValueMap) {

        List<CategoryProperty> categoryProperties = categoryPropertyService.findCategoryProperty(category.getId(), PropertyType.SELL_PROPERTY);
        Property brandProperty = propertyAndValueService.getPropertyByName("品牌");

        Map<ProductCategory, List<Integer>> selectedSkuValueMap = new HashMap<>();
        for (CategoryProperty categoryProperty : categoryProperties) {
            String propertyName = categoryProperty.getProperty().getName();
            List<String> categoryValueNames = categoryValueMap.get(propertyName);
            if(categoryValueNames != null && !categoryValueNames.isEmpty()) {
                List<Value> valueList = categoryValueNames.stream().map(propertyAndValueService::getValueByName).collect(Collectors.toList());
//                selectedSkuValueMap.compute(categoryProperty, (k, v) -> {
//                    if (v == null) {
//                        Set<SkuProperty> skuPropertySet = new LinkedHashSet<>();
//                        skuPropertySet.add(skuProperty);
//                        return skuPropertySet;
//                    } else {
//                        v.add(skuProperty);
//                        return v;
//                    }
//                });
            }

        }

    }

    private Object[] parsePidVid(Integer brandId, List<CategoryProperty> categoryProperties) {
        PidVid key = new PidVid();
        PidVid sell = new PidVid();
        Property property = propertyAndValueService.getPropertyByName("品牌");
        for (CategoryProperty categoryProperty : categoryProperties) {
            int pid = categoryProperty.getPropertyId();//遍历这个类目下的所有类目属性，通过属性ID去request中得到值ID
            if (pid != property.getId()) { //品牌属性不需要处理，因为品牌这个属性单独处理了
                if (categoryProperty.isMultiValue()) {
                    String[] values = request.getParameterValues("" + pid);
                    for (String value : values) {
                        if (categoryProperty.getPropertyType() == PropertyType.KEY_PROPERTY) {
                            key.add(pid, Integer.parseInt(value), true);
                        }
                        if (categoryProperty.getPropertyType() == PropertyType.SELL_PROPERTY) {
                            sell.add(pid, Integer.parseInt(value), true);
                        }
                    }
                } else {
                    String vid = request.getParameter("" + pid);
                    if (categoryProperty.getPropertyType() == PropertyType.KEY_PROPERTY) {
                        key.add(pid, Integer.parseInt(vid), false);
                    }
                    if (categoryProperty.getPropertyType() == PropertyType.SELL_PROPERTY) {
                        sell.add(pid, Integer.parseInt(vid), false);
                    }
                }
            }
        }
        key.add(property.getId(), brandId, false);
        return new ProductPidVid(sell, key);
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
            Property property = new Property();
            categoryProperty.setCategoryId(categoryId);
            property.setName(propertyName);
            int propertyId = propertyAndValueService.createPropertyIfNotExist(property);
            categoryProperty.setPropertyId(propertyId);
            if("品牌".equals(propertyName)) {
                categoryProperty.setPropertyType(PropertyType.KEY_PROPERTY);
            } else {
                categoryProperty.setPropertyType(PropertyType.SELL_PROPERTY);
            }
            categoryProperty.setPriority(categoryValueMap.size() - ++i);
            categoryProperty.setMultiValue(propertyValueList != null && propertyValueList.size() > 1);
            categoryPropertyService.createCategoryProperty(categoryProperty);

            if(propertyValueList != null) {
                int j = 0;
                for(String valueName : propertyValueList) {
                    Value value = new Value();
                    value.setValueName(valueName);
                    int valueId = propertyAndValueService.createValueIfNotExist(value);

                    CategoryPropertyValue cpv = new CategoryPropertyValue();
                    cpv.setCategoryId(categoryId);
                    cpv.setPropertyId(propertyId);
                    cpv.setValueId(valueId);
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
