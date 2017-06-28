/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.entity.tech;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 知会Entity
 * @author jicdata
 * @version 2016-03-11
 */
public class Notification extends DataEntity<Notification> {
	
	private static final long serialVersionUID = 1L;
	private Assigning assigning;		// 派工编号
	private String notification;		// 知会内容
	private String reply;		// 知会评论
	private Date notificationDate;		// 知会时间
	private Date replyDate;		// 评论时间
	private Date beginNotificationDate;		// 开始 知会时间
	private Date endNotificationDate;		// 结束 知会时间


	public Notification() {
		super();
	}

	public Notification(String id){
		super(id);
	}

	public Notification(Assigning assigning) {
		this.assigning = assigning;
	}

	@NotNull(message = "派工编号不能为空")
	public Assigning getAssigning() {
		return assigning;
	}

	public void setAssigning(Assigning assigning) {
		this.assigning = assigning;
	}

	@Length(min=0, max=255, message="知会内容长度必须介于 0 和 255 之间")
	public String getNotification() {
		return notification;
	}

	public void setNotification(String notification) {
		this.notification = notification;
	}
	
	@Length(min=0, max=255, message="知会评论长度必须介于 0 和 255 之间")
	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public Date getNotificationDate() {
		return notificationDate;
	}

	public void setNotificationDate(Date notificationDate) {
		this.notificationDate = notificationDate;
	}

	public Date getReplyDate() {
		return replyDate;
	}

	public void setReplyDate(Date replyDate) {
		this.replyDate = replyDate;
	}

	public Date getBeginNotificationDate() {
		return beginNotificationDate;
	}

	public void setBeginNotificationDate(Date beginNotificationDate) {
		this.beginNotificationDate = beginNotificationDate;
	}

	public Date getEndNotificationDate() {
		return endNotificationDate;
	}

	public void setEndNotificationDate(Date endNotificationDate) {
		this.endNotificationDate = endNotificationDate;
	}
}