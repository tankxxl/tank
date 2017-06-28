package com.thinkgem.jeesite.modules.crm.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by rgz on 09/06/2017.
 */
public class JsonUtils {

    private static Logger logger = Logger.getLogger(JsonUtils.class);
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        //去掉默认的时间戳格式
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //设置为中国上海时区
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        //空值不序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 配置mapper忽略空属性
        // objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        // 为了使JSON视觉上的可读性，增加一行如下代码，注意，在生产中不需要这样，因为这样会增大Json的内容
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);



        //反序列化时，属性不存在的兼容处理
        objectMapper
                .getDeserializationConfig()
                .withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // or
        //当反序列化json时，未知属性会引起的反序列化被打断，这里我们禁用未知属性打断反序列化功能，
        //因为，例如json里有10个属性，而我们的bean中只定义了2个属性，其它8个属性将被忽略
        // objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);


        //序列化时，日期的统一格式
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //单引号处理
        objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonParseException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        } catch (JsonMappingException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    public static <T> String toJson(T entity) {
        try {
            return objectMapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T toCollection(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonParseException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        } catch (JsonMappingException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * json string convert to map
     */
    public static <T> Map<String, Object> json2map(String jsonStr)
            throws Exception {
        return objectMapper.readValue(jsonStr, Map.class);
    }

    /**
     * json string convert to map with javaBean
     */
    public static <T> Map<String, T> json2map(String jsonStr, Class<T> clazz)
            throws Exception {
        Map<String, Map<String, Object>> map = objectMapper.readValue(jsonStr,
                new TypeReference<Map<String, T>>() {
                });
        Map<String, T> result = new HashMap<String, T>();
        for (Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
            result.put(entry.getKey(), map2pojo(entry.getValue(), clazz));
        }
        return result;
    }

    /**
     * json array string convert to list with javaBean
     */
    public static <T> List<T> json2list(String jsonArrayStr, Class<T> clazz)
            throws Exception {
        List<Map<String, Object>> list = objectMapper.readValue(jsonArrayStr,
                new TypeReference<List<T>>() {
                });
        List<T> result = new ArrayList<T>();
        for (Map<String, Object> map : list) {
            result.add(map2pojo(map, clazz));
        }
        return result;
    }

    /**
     * map convert to javaBean
     */
    public static <T> T map2pojo(Map map, Class<T> clazz) {
        return objectMapper.convertValue(map, clazz);
    }
}
