package com.thinkgem.jeesite.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class SpringMailTest {
	public static void main(String[] args)

   {

       //得到spring的context对象ClassPathXmlApplicationContext
//        ApplicationContext ctx = new FileSystemXmlApplicationContext("/src/applicationContext.xml");
//		  ApplicationContext ctx = new ClassPathXmlApplicationContext("/src/main/resources/applicationContext.xml");
		ApplicationContext ctx = new FileSystemXmlApplicationContext("/src/main/resources/spring-context.xml");

       //Spring - mail提供者
       MailSender sender = (MailSender) ctx.getBean("mailSender");
       //创建一个简单mail消息对象
       SimpleMailMessage smm = new SimpleMailMessage();
       //收件人地址
       smm.setTo("rgz03407@163.com");
       //发件人地址
       smm.setFrom("rengangzai@cjitec.com");
       //邮件标题
       smm.setSubject("test");
       //邮件内容
       smm.setText("这是用Spring mail发送的邮件！！");
       //发送
//        sender.send(smm);
       //提示信息
       System.out.println("Send Success!!");
   }

}