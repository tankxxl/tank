package com.thinkgem.jeesite.common.service;

import com.thinkgem.jeesite.common.annotation.DataSource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * Spring读写分离
 *
 * 从DynamicDataSource 的定义看出，他返回的是DynamicDataSourceHolder.getDataSouce()值，我们需要在程序运行时调用DynamicDataSourceHolder.putDataSource()方法，对其赋值。下面是我们实现的核心部分，也就是AOP部分
 * Created by rgz on 09/05/2017.
 */
public class DataSourceAspect {

    public void before(JoinPoint point) {
        Object target = point.getTarget();
        String method = point.getSignature().getName();

        Class<?>[] classz = target.getClass().getInterfaces();

        Class<?>[] parameterTypes = ((MethodSignature)point.getSignature()).getMethod().getParameterTypes();

        try {
            Method m = classz[0].getMethod(method, parameterTypes);
            if (m != null && m.isAnnotationPresent(DataSource.class)) {
                DataSource data = m.getAnnotation(DataSource.class);
                DynamicDataSourceHolder.putDataSource(data.value());
                System.out.println(data.value());
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
