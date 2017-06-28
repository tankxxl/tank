package com.thinkgem.jeesite.modules.mail.entity;

import com.thinkgem.jeesite.common.utils.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

public class Email implements Serializable {
	// private UserGroups usersGroups;

	/** 收件人 **/
	private String addressee;

	/** 抄送给 **/
	private String cc;

	/** 邮件主题 **/
	private String subject;

	/** 邮件内容 **/
	private String content;

	/** 附件 **/
	private MultipartFile[] attachment = new MultipartFile[0];

	////////////////////////// 解析邮件地址//////////////////////////////
	public String[] getAddress() {
		if (StringUtils.isBlank(this.addressee)) {
			return null;
		}
		// if(!StringUtil.hasLength(this.addressee)) {
		// return null;
		//   }
		addressee = addressee.trim();
		addressee.replaceAll("\\；", ";");
		addressee.replaceAll("\\ ", ";");
		addressee.replaceAll("\\,", ";");
		addressee.replaceAll("\\，", ";");
		addressee.replaceAll("\\|", ";");
		

//		addressee.replace(" ", ";");
//		addressee.replace(",", ";");
//		addressee.replace("，", ";");
//		addressee.replace("|", ";");
		
		return addressee.split(";");
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public MultipartFile[] getAttachment() {
		return attachment;
	}

	public void setAttachment(MultipartFile[] attachment) {
		this.attachment = attachment;
	}

	public void setAddressee(String addressee) {
		this.addressee = addressee;
	}

}
