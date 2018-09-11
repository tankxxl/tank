package com.thinkgem.jeesite.modules.mail.service;

import com.thinkgem.jeesite.common.annotation.Loggable;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.ActEntity;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.mail.entity.Email;
import com.thinkgem.jeesite.modules.sys.dao.RoleDao;
import com.thinkgem.jeesite.modules.sys.entity.Role;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import freemarker.template.Configuration;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class MailService extends BaseService {
	
	@Resource
	JavaMailSender mailSender;
	@Resource
	TaskExecutor taskExecutor;

    @Autowired
    Configuration freemarkerConfiguration;

    @Autowired
    private RoleDao roleDao;

	@Loggable
    Logger logger;
	
	public void sendMail(Email email) {
		if (email.getAddress() == null || email.getAddress().length == 0 ) {
			logger.debug("没有收件人");
			return;
		}
		if (email.getAddress().length > 1) {
			sendMailByAsyncMode(email);
			logger.debug("收件人过多，正在采用异步方式发送...<br/>");
		} else {
			sendMailBySyncMode(email);
			logger.debug("正在同步方式发送邮件...<br/>");
		}
	}

	/**
	 * 异步发送
	 * @param email
	 */
	public void sendMailByAsyncMode(final Email email) {
        if (!Global.isSendEmail()) {
            return;
        }
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					sendMailBySyncMode(email);
				} catch (Exception e) {
					logger.error("邮件发送失败", e);
				}
			}
		});
	}
	
	/**
	 * 同步发送, 发送html格式的Email，邮件正文不使用模板
	 * @param email
	 */
	public void sendMailBySyncMode(final Email email) {
		try {
			MimeMessage mime = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mime, true, "utf-8");
			helper.setFrom(Global.getFromEmail()); // 发件人必须跟配置文件里一样
			helper.setTo(email.getAddress()); // 收件人
            // helper.setBcc("xx@163.com"); // 暗送
			if (StringUtils.isNotBlank(email.getCc())) {
				String cc[] = email.getCc().split(";");
				helper.setCc(cc); // 抄送
			}
            // helper.setReplyTo("xx@163.com"); // 回复到
			helper.setSubject(email.getSubject() + "--" + email.getContent()); // 邮件主题
			helper.setText(email.getContent()
                    + "<br/>请您登录项目管理系统，在【待办任务】中进行办理。"
                    + "<br/>网址：<br/>" +
                    "<a href='http://pm.jicdata.com/pms'>http://pm.jicdata.com/pms</a>" , true); // true表示设定html格式
            // helper.addInline("log", new ClassPathResource("logo.gif"));
			// 处理附件
			for (MultipartFile file : email.getAttachment()) {
				if (file == null || file.isEmpty()) {
					continue;
				}
				// 使用MimeUtility.encodeWord()来解决附件名称的中文问题			
				helper.addAttachment(
				        MimeUtility.encodeWord(file.getOriginalFilename()),
						new ByteArrayResource(file.getBytes()));
			}
			mailSender.send(mime);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    //
    public void sendEmail(String subject, ProjectApplyExternal apply, ActEntity actEntity, List<String> groupNames, List<String> loginNames) {

        MimeMessagePreparator preparator = getMessagePreparator(subject, apply, actEntity, groupNames, loginNames);

        try {
            mailSender.send(preparator);
            System.out.println("Message has been sent.......");
        } catch (MailException e) {
            System.err.println(e.getMessage());
        }
    }


    private MimeMessagePreparator getMessagePreparator(final String subject,
                                                       final ProjectApplyExternal apply, final ActEntity entity,
                                                       final List<String> groupNames,
                                                       final List<String> loginNames) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

                helper.setSubject(subject);
                helper.setFrom(Global.getFromEmail());
                helper.setTo(getEmailByList(groupNames, loginNames));

                Map<String, Object> model = new HashMap<String, Object>();
                model.put("entity", entity);
                model.put("apply", apply);

                String text = getFreeMarkerTemplateContent(model); // Use Freemarker or Velocity
                System.out.println("Template content : " + text);

                // use the true flag to indicate you need a multipart message
                helper.setText(text, true);

                // 最后添加文件
                helper.addInline("identifier1234", new ClassPathResource("linux-icon.png"));

                // Additionally, let's add a resource as an attachment as well.
                helper.addAttachment("cutie.png", new ClassPathResource("linux-icon.png"));
            }
        };

        return preparator;
    }

    private String getFreeMarkerTemplateContent(Map<String, Object> model) {
        StringBuffer content = new StringBuffer();

        try {
            content.append(FreeMarkerTemplateUtils.processTemplateIntoString(
                    freemarkerConfiguration.getTemplate("fm_mailTemplate.txt"),
                    model
            ));
            return content.toString();
        } catch (Exception e) {
            System.out.println("Exception occured while processing fmtemplate: " + e.getMessage());
        }
        return "";
    }


    //

    /**
     * 根据角色名称得到发件人地址
     * 用例1：用于审批流程中按角色发送邮件
     *
     * @param groupNames 角色名称以逗号分隔
     * @return
     */
    private String getEmailByGroupName(List<String> groupNames) {
        return StringUtils.join(getEmailListByGroupName(groupNames), ";");
    }

    private String getEmailByLoginName(List<String> loginNames) {
        return StringUtils.join(getEmailListByLoginName(loginNames), ";");
    }

    private String getEmailByList(List<String> groupNames, List<String> loginNames) {
        List<String> list = new ArrayList<String>();
        if (groupNames != null && groupNames.size() != 0) {
            list.addAll(getEmailListByGroupName(groupNames));
        }
        if (loginNames != null && loginNames.size() != 0) {
            list.addAll(getEmailListByLoginName(loginNames));
        }
        return StringUtils.join(list, ";");
    }

    private List<String> getEmailListByGroupName(List<String> groupNames) {
        List<String> emails = new ArrayList<String>();
        Role role = new Role();

        for (String groupName: groupNames) {
            if (StringUtils.isEmpty(groupName))
                continue;
            role.setEnname(groupName);
            role = roleDao.getRoleUserByEnname(role);
            if (role == null)
                continue;

            List<User> users = role.getUserList();

            for (User user: users) {
                if (user == null ||
                        "thinkgem".equals(user.getLoginName()) ||
                        StringUtils.isEmpty(user.getEmail()))
                    continue;
                emails.add(user.getEmail());
            } // end users
        } // end groups
        return emails;
    }

    private List<String> getEmailListByLoginName(List<String> loginNames) {
        List<String> emails = new ArrayList<String>();
        User user;

        for (String loginName: loginNames) {
            if (StringUtils.isEmpty(loginName))
                continue;
            user = UserUtils.getByLoginName(loginName);
            if (user != null && StringUtils.isNotBlank(user.getEmail())) {
                emails.add(user.getEmail());
            }
        }
        return emails;
    }


    /**
    public void sendEmail(Object object) {

        ProductOrder order = (ProductOrder) object;

        MimeMessagePreparator preparator = getMessagePreparator(order);

        try {
            mailSender.send(preparator);
            System.out.println("Message Send...Hurrey");
        } catch (MailException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private MimeMessagePreparator getMessagePreparator(final ProductOrder order) {

        MimeMessagePreparator preparator = new MimeMessagePreparator() {

            public void prepare(MimeMessage mimeMessage) throws Exception {
                mimeMessage.setFrom("customerserivces@yourshop.com");
                mimeMessage.setRecipient(Message.RecipientType.TO,
                        new InternetAddress(order.getCustomerInfo().getEmail()));
                mimeMessage.setText("Dear " + order.getCustomerInfo().getName()
                        + ", thank you for placing order. Your order id is " + order.getOrderId() + ".");
                mimeMessage.setSubject("Your order on Demoapp");
            }
        };
        return preparator;
    }


     // add Attachment
     private MimeMessagePreparator getContentWtihAttachementMessagePreparator(final ProductOrder order) {

     MimeMessagePreparator preparator = new MimeMessagePreparator() {

     public void prepare(MimeMessage mimeMessage) throws Exception {
     MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

     helper.setSubject("Your order on Demoapp with attachement");
     helper.setFrom("customerserivces@yourshop.com");
     helper.setTo(order.getCustomerInfo().getEmail());
     String content = "Dear " + order.getCustomerInfo().getName()
     + ", thank you for placing order. Your order id is " + order.getOrderId() + ".";

     helper.setText(content);

     // Add a resource as an attachment
     helper.addAttachment("cutie.png", new ClassPathResource("linux-icon.png"));

     }
     };
     return preparator;
     }


     // Preparing MimeMessage with inline content
     private MimeMessagePreparator getContentAsInlineResourceMessagePreparator(final ProductOrder order) {

     MimeMessagePreparator preparator = new MimeMessagePreparator() {

     public void prepare(MimeMessage mimeMessage) throws Exception {
     MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

     helper.setSubject("Your order on Demoapp with Inline resource");
     helper.setFrom("customerserivces@yourshop.com");
     helper.setTo(order.getCustomerInfo().getEmail());

     String content = "Dear " + order.getCustomerInfo().getName()
     + ", thank you for placing order. Your order id is " + order.getOrderId() + ".";

     // Add an inline resource.
     // use the true flag to indicate you need a multipart message
     helper.setText("<html><body><p>" + content + "</p><img src='cid:company-logo'></body></html>", true);
     helper.addInline("company-logo", new ClassPathResource("linux-icon.png"));
     }
     };
     return preparator;
     }

     */


    /**  for test
    public static void main(String[] args) {
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        OrderService orderService = (OrderService) context.getBean("orderService");
        orderService.sendOrderConfirmation(getDummyOrder());
        ((AbstractApplicationContext) context).close();
    }

    public static ProductOrder getDummyOrder(){
        ProductOrder order = new ProductOrder();
        order.setOrderId("1111");
        order.setProductName("Thinkpad T510");
        order.setStatus("confirmed");

        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setName("Websystique Admin");
        customerInfo.setAddress("WallStreet");
        customerInfo.setEmail("websystique@gmail.com");
        order.setCustomerInfo(customerInfo);
        return order;
    }
     */


    /**
     * 利用Spring最简单地使用异步方法 @Async
     * @throws InterruptedException
     */
    @Async
    public void doBusiness() throws InterruptedException {
        logger.info("start to do business.");
        TimeUnit.SECONDS.sleep(10);
        logger.info("end to do business.");
    }

}
