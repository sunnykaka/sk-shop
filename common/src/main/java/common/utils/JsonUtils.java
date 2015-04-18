package common.utils;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 * User: amos.zhou
 * Date: 13-12-23
 * Time: 下午12:10
 * Json工具类
 */
public class JsonUtils {

    public static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    public static String object2Json(Object o) {
        StringWriter sw = new StringWriter();
        JsonGenerator gen = null;
        try {
            gen = new JsonFactory().createGenerator(sw);
            OBJECT_MAPPER.writeValue(gen, o);
        } catch (IOException e) {
            throw new RuntimeException("不能序列化对象为Json", e);
        } finally {
            if (null != gen) {
                try {
                    gen.close();
                } catch (IOException e) {
                    throw new RuntimeException("不能序列化对象为Json", e);
                }
            }
        }
        return sw.toString();
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Hibernate4Module().enable(Hibernate4Module.Feature.FORCE_LAZY_LOADING));
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        return objectMapper;
    }

    /**
     * 将 json 字段串转换为 对象.
     *
     * @param json  字符串
     * @param clazz 需要转换为的类
     * @return
     */
    public static <T> T json2Object(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("将 Json 转换为对象时异常,数据是:" + json, e);
        }
    }

    /**
     *   将 json 字段串转换为 List.
     * @param json
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> List<T> jsonToList(String json,Class<T> clazz) throws IOException {
        JavaType type = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);

        List<T> list = OBJECT_MAPPER.readValue(json, type);
        return list;
    }


    /**
     *  将 json 字段串转换为 数据.
     * @param json
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T>  T[] jsonToArray(String json,Class<T[]> clazz) throws IOException {
        return OBJECT_MAPPER.readValue(json, clazz);

    }
}
