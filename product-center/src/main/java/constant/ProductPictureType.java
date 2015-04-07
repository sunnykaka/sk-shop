package constant;

import com.boobee.common.domain.util.ViewEnum;
import com.boobee.common.jackson.EnumSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 商品图片类型
 * 默认为空（每个商品都有一个主图显示）
 *
 * Created by zhb on 15-4-2.
 */
@JsonSerialize(using = EnumSerializer.class)
public enum ProductPictureType implements ViewEnum {

    DEFAULT(""),

    MAIN("主图");

    public String value;
    ProductPictureType(String value) {this.value = value;}

    @Override
    public String getName() {
        return this.toString();
    }

    @Override
    public String getValue() {
        return value;
    }


}
