package com.thinkgem.jeesite.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 该配置代码的效果和如下代码等价~
 <mvc:resources mapping="swagger-ui.html" location="classpath:/META-INF/resources/" />
 <mvc:resources mapping="/webjars/**" location="classpath:/META-INF/resources/webjars/" />
 */
// @Configuration
// @EnableWebMvc
// public class WebAppConfig extends WebMvcConfigurerAdapter {
//
//     @Override
//     public void addResourceHandlers(ResourceHandlerRegistry registry) {
//         registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
//         registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
//     }
//
// }
