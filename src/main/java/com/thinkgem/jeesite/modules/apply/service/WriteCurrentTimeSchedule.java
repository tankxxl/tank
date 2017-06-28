package com.thinkgem.jeesite.modules.apply.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rgz on 13/10/2016.
 */
@Component
public class WriteCurrentTimeSchedule {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss sss");

    // 2 second.
    @Scheduled(fixedDelay = 2 * 1000)
    public void writeCurrentTime() {
        Date now = new Date();

        String nowString = df.format(now);
        System.out.println("Now is: " + nowString);

        logger.info("Now is: " + nowString);
    }
}
