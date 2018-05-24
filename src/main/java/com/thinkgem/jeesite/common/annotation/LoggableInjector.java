package com.thinkgem.jeesite.common.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * The LoggableInjector implements Spring's BeanPostProcessor interface,
 * which give you the chance to do something just before/after creation of beans.
 * What the LoggableInjector class actually do is after creation of a bean object,
 * find the field annotated by @Loggable, then set the field value to a real logger.
 *
 * One thing need to notice is that the real logger is created after the bean object has been initialized,
 * which means you can NOT use logger in the constructor of the bean. Because at that time,
 * the logger field has not be defined, you'll get NullPointerException Error.
 *
 * Created by rgz on 14/12/2016.
 */
@Service
public class LoggableInjector implements BeanPostProcessor {

    // 在初始化Bean之前
    @Override
    public Object postProcessBeforeInitialization(final Object bean, String beanName)
            throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
//                make the field accessible if defined private
                ReflectionUtils.makeAccessible(field);
                if (field.getAnnotation(Loggable.class) != null) {
                    Logger logger = LoggerFactory.getLogger(bean.getClass());
                    field.set(bean, logger);
                }
            }
        });
        return bean;
    }

    // 在初始化Bean之后
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }
}
