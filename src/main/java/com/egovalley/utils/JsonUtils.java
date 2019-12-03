package com.egovalley.utils;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    public final static ObjectMapper mapper = new ObjectMapper();

    static {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mapper.setDateFormat(dateFormat);
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        mapper.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, false);
        mapper.configure(SerializationConfig.Feature.WRITE_EMPTY_JSON_ARRAYS, false);
    }

    private static JavaType getCollectionType(Class<?> collectionClass, Class<?>... classes) {
        return mapper.getTypeFactory().constructParametricType(collectionClass, classes);
    }

    /**
     * Json字符串转对象
     */
    public static <T> T jsonToObject(String json, Class<T> clazz) throws Exception {
        if (json != null && !"".equals(json) && !"null".equals(json)) {
            return mapper.readValue(json, clazz);
        }
        return null;
    }

    /**
     * Json字符串转泛型集合
     */
    public static <T> List<T> jsonToObjectList(String json, Class<T> clazz) throws Exception {
        List<T> result = null;
        if (json != null && !"".equals(json) && !"null".equals(json)) {
            result = mapper.readValue(json, getCollectionType(ArrayList.class, clazz));
        }
        return result;
    }

    /**
     * Json字符串转Map
     */
    public static Map jsonToMap(String json) throws Exception {
        if (json != null && !"".equals(json) && !"null".equals(json)) {
            return jsonToObject(json, Map.class);
        }
        return null;
    }

    /**
     * 泛型集合转Json字符串
     */
    public static String objectListToJson(List list) throws Exception {
        if (list != null && !list.isEmpty()) {
            return mapper.writeValueAsString(list);
        }
        return "";
    }

    /**
     * 对象转Json字符串
     */
    public static String objectToJson(Object object) throws Exception {
        if (object != null) {
            return mapper.writeValueAsString(object);
        }
        return "";
    }

}
