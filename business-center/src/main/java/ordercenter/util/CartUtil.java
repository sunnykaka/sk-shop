package ordercenter.util;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by liubin on 15-11-17.
 */
public class CartUtil {

    public static List<Integer> getCartItemIdList(String selCartItems) {
        List<Integer> selCartItemIdList = new ArrayList<>();
        if(selCartItems != null) {
            selCartItemIdList = Lists.newArrayList(selCartItems.split("_")).stream().
                    map(Integer::parseInt).collect(Collectors.toList());
        }
        return selCartItemIdList;
    }

}
