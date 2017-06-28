package com.thinkgem.jeesite.common.service;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Spring读写分离
 * 我们还需要实现spring的抽象类AbstractRoutingDataSource，就是实现determineCurrentLookupKey方法
 *
 * 从DynamicDataSource 的定义看出，他返回的是DynamicDataSourceHolder.getDataSouce()值，我们需要在程序运行时调用DynamicDataSourceHolder.putDataSource()方法，对其赋值。下面是我们实现的核心部分，也就是AOP部分
 *
 *
 * Created by rgz on 09/05/2017.
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceHolder.getDataSource();
    }
}

