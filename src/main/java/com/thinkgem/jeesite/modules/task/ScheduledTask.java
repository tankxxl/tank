package com.thinkgem.jeesite.modules.task;

import com.thinkgem.jeesite.common.annotation.Loggable;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.slf4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 定时任务不能是懒加载
 *
 * spring通过接口TaskExecutor和TaskScheduler这两个接口的方式为异步定时任务提供了一种抽象。
 * 这就意味着spring容许你使用其他的定时任务框架，当然spring自身也提供了一种定时任务的实现：spring task。
 * spring task支持线程池，可以高效处理许多不同的定时任务。
 * 同时，spring还支持使用Java自带的Timer定时器和Quartz定时框架。
 *
 *
 * TaskExecutor是spring task的第一个抽象，它很自然让人联想到jdk中concurrent包下的Executor，
 * 实际上TaskExecutor就是为区别于Executor才引入的，而引入TaskExecutor的目的就是为定时任务的执行提供线程池的支持.
 *
 *
 * 在spring 4.x中已经不支持7个参数的cronin表达式了，要求必须是6个参数。具体格式如下：
 * {秒} {分} {时} {日期（具体哪天）} {月} {星期}
 *
 *
 * Seconds Minutes Hours DayofMonth Month DayofWeek Year或
 * Seconds Minutes Hours DayofMonth Month DayofWeek
 *
 * *：表示匹配该域的任意值，假如在Minutes域使用*, 即表示每分钟都会触发事件。
 *
 1.  Seconds
 2.  Minutes
 3.  Hours
 4.  Day-of-Month
 5.  Month
 6.  Day-of-Week
 7.  Year (可选字段)

 “*” 代表整个时间段。
 “?”字符：表示不确定的值
 “,”字符：指定数个值
 “-”字符：指定一个值的范围
 “/”字符：指定一个值的增加幅度。n/m表示从n开始，每次增加m

 */
//@Service
// @Service
// @EnableScheduling
@Component
@Lazy(false)
public class ScheduledTask {
	
	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//    private final Logger logger = Logger.getLogger(ServiceTask3.class.getName());

    @Loggable
    Logger logger;

    // @Scheduled(fixedDelay = 100)
    // @Scheduled(cron="0/10 * *  * * ? ")   //每10秒执行一次
    public void pushQuestionna() {

        System.out.println("定时任务1，自动执行:" + format.format(new Date()));
        logger.info("定时任务1，自动执行:" + format.format(new Date()));
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
    
    // @Scheduled(cron="0/5 * *  * * ? ")   //每5秒执行一次
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
