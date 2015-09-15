package common.utils.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import common.models.utils.ViewEnum;
import common.utils.jackson.custom.CustomDeserializationContext;
import org.apache.commons.lang3.StringUtils;
import play.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class EnumDeserializer extends JsonDeserializer<ViewEnum> {

    @Override
    public ViewEnum deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

        if(!(ctxt instanceof CustomDeserializationContext)) {
            Logger.error("DeserializationContext的实现类CustomDeserializationContext, 无法反序列化枚举类型");
            return null;
        }

        JsonNode jsonNode = jp.getCodec().readTree(jp);
        JsonNode nameNode = jsonNode.get("name");
        if(nameNode == null || StringUtils.isBlank(nameNode.asText())) {
            Logger.warn("枚举类型在反序列化的时候发现json字符串里没有name属性, json: " + jsonNode.toString());
            return null;
        }

        String name = nameNode.asText();
        String fieldName = ctxt.getParser().getParsingContext().getCurrentName();
        /**
         * 因为使用了CustomObjectMapper,所以可以得到属性的类型
         */
        List<JavaType> beanTypeList = ((CustomDeserializationContext) ctxt).getBeanTypeList();

        try {
            for(int i=beanTypeList.size()-1; i>=0; i--) {
                JavaType beanType = beanTypeList.get(i);
                try {
                    Field field = beanType.getRawClass().getDeclaredField(fieldName);
                    Class<?> enumType = field.getType();
                    Method method = enumType.getDeclaredMethod("valueOf", String.class);
                    return (ViewEnum)method.invoke(null, name);

                } catch (NoSuchFieldException | NoSuchMethodException | IllegalArgumentException ignore) {
                }
            }

        } catch (Exception e) {
            Logger.error("反序列化枚举失败", e);
        }

        Logger.error(String.format("反序列化枚举的时候,类列表: %s 里没有找到%s属性",
                beanTypeList.stream().map(x -> x.getRawClass().getName()).collect(Collectors.toList()).toString(),
                fieldName));

        return null;

    }

}