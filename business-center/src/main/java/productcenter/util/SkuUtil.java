package productcenter.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by liubin on 15-8-31.
 */
public class SkuUtil {


    /** 类目位数 */
    public static final int SZIE_CATEGORY_ID = 3;
    /** 商品位数 */
    public static final int SIZE_PRODUCT_ID = 4;
    /** SKU位数 */
    public static final int SIZE_SKU_ID = 6;


    /**
     * 生成 sku编码
     *
     * @param categoryId
     * @param productId
     * @return
     */
    public static String initSkuCode(int categoryId, int productId, int skuId) {
        StringBuffer codeStr = new StringBuffer();

        return codeStr.append(initNewCode(String.valueOf(categoryId), SZIE_CATEGORY_ID))
                .append(initNewCode(String.valueOf(productId), SIZE_PRODUCT_ID))
                .append(initNewCode(String.valueOf(skuId), SIZE_SKU_ID)).toString();

    }

    /**
     * 生成相应位数的ID值
     *
     * @param code
     * @param size
     * @return
     */
    private static String initNewCode(String code, int size) {

        StringBuffer codeStr = new StringBuffer();

        if (StringUtils.isEmpty(code)) {
            for (int i = 0; i < size; i++) {
                codeStr.append("0");
            }

            return codeStr.toString();
        }


        if (code.length() == size) {
            return code;
        }

        if (code.length() < size) {
            for (int i = 0; i < (size - code.length()); i++) {
                codeStr.append("0");
            }
            codeStr.append(code);
            return codeStr.toString();
        }

        return code;
    }

}
