package com.thinkgem.jeesite.modules.mail.web;

import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.mail.entity.Email;
import com.thinkgem.jeesite.modules.mail.service.MailService;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

//@Controller
public class MailController {
	
	@Resource
	MailService mailService;
	
//	@Resource
//	UserService userService;
	@RequestMapping(value ="/sendEmail", method=RequestMethod.GET)
	public String sendEmail(@RequestParam(value="email",required=false) String singleEmailAddress , HttpServletRequest request) {
		Email email = new Email();
	
		if(StringUtils.isNotBlank(singleEmailAddress)){
			email.setAddressee(singleEmailAddress);
		}
		request.setAttribute("email", email);
		return "system/sendMail";
	}
	
	
	
	@RequestMapping(value ="/sendEmail", method=RequestMethod.POST)
	public String send(@ModelAttribute Email email,  //Spring MVC将form表单的数据封装到这个对象中
			BindingResult result,
			ModelMap model,
			HttpServletRequest request){
		try {
//			new EmailValidator().validate(email, result);
//			if(result.hasErrors()){
//				throw new RuntimeException("数据填写不正确");
//			}
//			if(email.getEmailGroup()!=null){
//	    List<User> users = userService.getUserByUserGroups(email.getEmailGroup(), "userName,email", null, null);
//	    StringBuffer sb = new StringBuffer(StringUtil.hasLengthBytrim(email.getAddressee()) ? (email.getAddressee().trim() + ";") : "");
//	    for(User user : users){
//	     sb.append(user.getEmail()).append(";");
//	    }
//	    email.setAddressee(sb.toString());
//	   }
		if(email.getAddress()==null || email.getAddress().length==0){
			request.setAttribute("message", "没有收件人!");
			return "message";
		}
			
		mailService.sendMail(email);  //大于5个收件人时，分流器会自动选择异步方式发送
//		request.setAttribute("message", mailService.getMessage().append("本次共发送了 ").append(email.getAddress().length).append(" 封邮件.").toString());
		} catch (Exception e) {
			request.setAttribute("message", "Has errors! The info by "+e.getMessage()+"<br/>Can log in to view more detailed information on abnormalities.");
//	   log.error(this.getClass().getName()+"中发生异常---------------:\n", e);
		}
//	  	return BACK + "message";
		return "";
		}
	}
