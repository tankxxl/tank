package com.thinkgem.jeesite.common.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 在Spring MVC的Controller层经常会用到@RequestBody和@ResponseBody，
 * 通过这两个注解，可以在Controller中直接使用Java对象作为请求参数和返回内容，
 * 而完成这之间转换作用的便是HttpMessageConverter。
 *
 * 当前 Spring 中已经默认提供了相当多的转换器，分别有:
 * ByteArrayHttpMessageConverter            数据与字节数组的相互转换
 * StringHttpMessageConverter               数据与 String 类型的相互转换
 * FormHttpMessageConverter                 表单与 MultiValueMap的相互转换
 * SourceHttpMessageConverter               数据与 javax.xml.transform.Source 的相互转换
 * MarshallingHttpMessageConverter          使用 Spring 的 Marshaller/Unmarshaller 转换 XML 数据
 * MappingJackson2HttpMessageConverter      使用 Jackson 的 ObjectMapper 转换 Json 数据
 * MappingJackson2XmlHttpMessageConverter   使用 Jackson 的 XmlMapper 转换 XML 数据
 * BufferedImageHttpMessageConverter        数据与 java.awt.image.BufferedImage 的相互转换
 *
 *
 * Created by rgz on 28/06/2017.
 */
public class CustomJsonHttpMessageConverter implements HttpMessageConverter {

    // Jackson的Json映射器
    private ObjectMapper mapper = new ObjectMapper();

    // 该转换器的支持类型：application/json
    private List supportedMediaTypes = Arrays.asList(MediaType.APPLICATION_JSON);

    /**
     * 判断该转换器是否能将请求内容(输入内容)转换成 Java 对象
     * @param aClass 需要转换的java类型
     * @param mediaType 该请求的MediaType
     * @return
     */
    @Override
    public boolean canRead(Class aClass, MediaType mediaType) {
        if (mediaType == null) {
            return true;
        }
        for (MediaType supportedMediaType : getSupportedMediaTypes() ) {
            if (supportedMediaType.includes(mediaType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断该转换器是否可以将 Java 对象转换成返回内容(输出内容)
     * @param aClass 需要转换的Java类型
     * @param mediaType 该请求的MediaType
     * @return
     */
    @Override
    public boolean canWrite(Class aClass, MediaType mediaType) {
        if (mediaType == null || MediaType.ALL.equals(mediaType)) {
            return true;
        }
        for (MediaType supportedMediaType : getSupportedMediaTypes() ) {
            if (supportedMediaType.includes(mediaType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获得该转换器支持的 MediaType 类型
     * @return
     */
    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return supportedMediaTypes;
    }

    /**
     * 读取请求内容并转换成 Java 对象
     * 读取请求内容，将其中的Json转换成Java对象
     * @param aClass 需要转换的Java类型
     * @param httpInputMessage 代表着一次Http通讯中的请求和响应部分，请求对象
     * @return
     * @throws IOException
     * @throws HttpMessageNotReadableException
     */
    @Override
    public Object read(Class aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        return mapper.readValue(httpInputMessage.getBody(), aClass);
    }

    /**
     * 将 Java 对象转换成Json 写入返回内容
     * @param o 需要转换的对象
     * @param mediaType 返回类型
     * @param httpOutputMessage 代表着一次Http通讯中的请求和响应部分，响应对象
     * @throws IOException
     * @throws HttpMessageNotWritableException
     */
    @Override
    public void write(Object o, MediaType mediaType, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        mapper.writeValue(httpOutputMessage.getBody(), o);
    }
}
