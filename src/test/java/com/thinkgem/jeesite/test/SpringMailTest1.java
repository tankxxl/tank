package com.thinkgem.jeesite.test;

import com.thinkgem.jeesite.common.annotation.Loggable;
import com.thinkgem.jeesite.modules.mail.entity.Email;
import com.thinkgem.jeesite.modules.mail.service.MailService;
import com.thinkgem.jeesite.modules.task.ScheduledTask;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.Test;

/**
* Created by rgz on 09/12/2016.
*/

public class SpringMailTest1 extends BaseTestCase {

//    @Loggable
//    Logger logger;

//    @Autowired
//    ScheduledTask task;

   @Autowired
   MailService mailService;

   @Test
   public void testSendMail() throws InterruptedException {
        Email email = new Email();
        email.setAddressee("rengangzai@cjitec.com");
        email.setSubject("subject汉字");
        email.setContent("content汉字");
       mailService.sendMail(email);
   }

   @Test
   public void testLoggable() {
//        task.pushQuestionna();
   }

   @Override
   protected void beforeTest() throws Exception {

   }

   @Override
   protected void afterTest() throws Exception {

   }
}
