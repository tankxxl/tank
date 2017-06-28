/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.entity.tech;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.modules.sys.entity.User;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 工单Entity
 * @author jicdata:Arthur
 * @version 2016-03-11
 */
public class Workorder extends DataEntity<Workorder> {
	
	private static final long serialVersionUID = 1L;
	private Assigning assigning;		// 派工编号
	private User user;		// 人员编号
	private String level;		// 等级
	private String descContent;		// 承担工作
	private String descLocation;		// 工作地点
	private Date descTimeBegin;		// 预计工作时间_起始
	private Date descTimeEnd;		// 预计工作时间_结束
	private String manHour;		// 预定工时
	private Date workorderDate;		// 工单创建时间
	private String confirmed;		// 工单是否确认
	private String completed;		// 工期是否结束
	private Date beginWorkorderDate;		// 开始 工单创建时间
	private Date endWorkorderDate;		// 结束 工单创建时间


	public Workorder() {
		super();
	}

	public Workorder(String id){
		super(id);
	}

	public Workorder(Assigning assigning){
		this.assigning = assigning;
	}


	@NotNull(message = "派工编号不能为空")
	public Assigning getAssigning() {
		return assigning;
	}

	public void setAssigning(Assigning assigning) {
		this.assigning = assigning;
	}

	@NotNull(message="人员编号不能为空")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@NotNull(message = "工程师等级不能为空")
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	@Length(min=0, max=255, message="承担工作长度必须介于 0 和 255 之间")
	public String getDescContent() {
		return descContent;
	}

	public void setDescContent(String descContent) {
		this.descContent = descContent;
	}

	@Length(min = 0, max = 255, message = "工作地点必须介于 0 和 255 之间")
	public String getDescLocation() {
		return descLocation;
	}

	public void setDescLocation(String descLocation) {
		this.descLocation = descLocation;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getDescTimeBegin() {
		return descTimeBegin;
	}

	public void setDescTimeBegin(Date descTimeBegin) {
		this.descTimeBegin = descTimeBegin;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getDescTimeEnd() {
		return descTimeEnd;
	}

	public void setDescTimeEnd(Date descTimeEnd) {
		this.descTimeEnd = descTimeEnd;
	}
	
	@Length(min=1, max=11, message="预定工时长度必须介于 1 和 11 之间")
	public String getManHour() {
		return manHour;
	}

	public void setManHour(String manHour) {
		this.manHour = manHour;
	}

	@NotNull(message = "工单创建时间不能为空")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getWorkorderDate() {
		return workorderDate;
	}

	public void setWorkorderDate(Date workorderDate) {
		this.workorderDate = workorderDate;
	}

	public String getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(String confirmed) {
		this.confirmed = confirmed;
	}

	public String getCompleted() {
		return completed;
	}

	public void setCompleted(String completed) {
		this.completed = completed;
	}

	public Date getBeginWorkorderDate() {
		return beginWorkorderDate;
	}

	public void setBeginWorkorderDate(Date beginWorkorderDate) {
		this.beginWorkorderDate = beginWorkorderDate;
	}

	public Date getEndWorkorderDate() {
		return endWorkorderDate;
	}

	public void setEndWorkorderDate(Date endWorkorderDate) {
		this.endWorkorderDate = endWorkorderDate;
	}
}