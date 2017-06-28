package com.thinkgem.jeesite.common.service;

/**
 * Created by rgz on 09/05/2017.
 */
public class DynamicDataSourceHolder {
    public static final ThreadLocal<String> holder = new ThreadLocal<String>();

    public static void putDataSource(String name) {
        holder.set(name);
    }

    public static String getDataSource() {
        return holder.get();
    }
}
