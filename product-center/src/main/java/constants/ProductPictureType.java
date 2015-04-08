package constants;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.EnumSerializer;

/**
 * 商品图片类型
 * 默认为空（每个商品都有一个主图显示）
 *
 * Created by zhb on 15-4-2.
 */
@JsonSerialize(using = EnumSerializer.class)
public enum ProductPictureType{

    DEFAULT(""),

    MAIN("主图");

    public String value;

    ProductPictureType(String value) {this.value = value;}

    public String getName() {
        return this.toString();
    }

    public String getValue() {
        return value;
    }


}
