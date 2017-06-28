package com.thinkgem.jeesite.modules.supplier.httpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 从 http://www.122park.com/ 导入免费停车场 信息
 *
 * Created by rgz on 04/05/2017.
 */
public class ImportParsFrom122Park {
    public Logger logger = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        ImportParsFrom122Park tr = new ImportParsFrom122Park();
        String urls[] = {"http://www.122park.com/",
                "http://www.122park.com/sh",
                "http://www.122park.com/gz",
                "http://www.122park.com/sz"};
        String city[] = {"北京", "上海", "广州", "深圳"};
        int i = 0;
        // Location
    }
}
