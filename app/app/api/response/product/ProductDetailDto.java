package api.response.product;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

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




}
