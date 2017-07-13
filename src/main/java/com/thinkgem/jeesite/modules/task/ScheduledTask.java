package com.thinkgem.jeesite.modules.task;

import com.thinkgem.jeesite.common.annotation.Loggable;
import com.thinkgem.jeesite.modules.apply.dao.external.ProjectApplyExternalDao;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 定时任务不能是懒加载
 */
//@Service
// @Service
// @EnableScheduling
@Component
@Lazy(false)
public class ScheduledTask {
	
	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
    ProjectApplyExternalDao applyExternalDao;

//    private final Logger logger = Logger.getLogger(ServiceTask3.class.getName());

    @Loggable
    Logger logger;

    // @Scheduled(fixedDelay = 100)
    @Scheduled(cron="0/10 * *  * * ? ")   //每10秒执行一次
    public void pushQuestionna() {

        System.out.println("定时任务1，自动执行:" + format.format(new Date()));
        logger.info("定时任务1，自动执行:" + format.format(new Date()));

        // applyExternalDao.updatePorCode("1");
    }


    // @Scheduled(cron = "0 10-30 9 * * ?")
    public void pushQuestionnaire() {  
  
        System.out.println("定时任务1，自动执行:" + format.format(new Date()));
        logger.info("定时任务1，自动执行:" + format.format(new Date()));
    }  
  
    // @Scheduled(cron = "0 10-30 9 * * ?")
    public void pushQuestionnaire2() {  
  
        System.out.println("定时任务2，自动执行:" + format.format(new Date()));
        logger.info("定时任务2，自动执行:" + format.format(new Date()));

    }  
    
    @Scheduled(cron="0/5 * *  * * ? ")   //每5秒执行一次
    public void pushQuestionnaire3() {  
    	  
        System.out.println("定时任务3，自动执行:" + format.format(new Date()));
        logger.info("定时任务3，自动执行:" + format.format(new Date()));
    }

    // @Scheduled(cron = "0 * * * * ?")
    public void run() {
//        logger.info("MyFirstSpringJob trigger...");
        logger.info("trigger...");

        /* 模拟此Job需耗时5秒 */
//        try {
//            TimeUnit.SECONDS.sleep(5);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    // @Scheduled(cron = "3 * * * * ?")
    public void run2() {
        logger.info("MySecondSpringJob trigger...");
    }
}
