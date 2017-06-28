package com.thinkgem.jeesite.common.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;

/**
 * 回到最开始所提的需求，既然要对返回的Json内容进行加密，
 * 肯定是对MappingJackson2HttpMessageConverter进行改造，
 * 并且只需要重写write方法。
 *
 * 从MappingJackson2HttpMessageConverter的父类AbstractHttpMessageConverter中的write方法可以看出，
 * 该方法通过writeInternal方法向返回结果的输出流中写入数据，所以只需要重写该方法即可:
 *
 * Created by rgz on 28/06/2017.
 */
public class EncyptJsonHttpMessageConverter extends MappingJackson2HttpMessageConverter {

    /**
     * 重写 writeInternal 方法，在返回内容前首先进行加密
     * @param object
     * @param outputMessage
     * @throws IOException
     * @throws HttpMessageNotWritableException
     */
    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        // 使用Jackson的ObjectMapper将Java对象转换成 Json String
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(object);
        System.out.println(json);
        // 加密
        String result = json + "加密了！";
        System.out.println(result);

        // 输出
        outputMessage.getBody().write(result.getBytes());
    }
}
