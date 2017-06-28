/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.entity.tech;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 资源申请Entity
 * @author jicdata
 * @version 2016-03-11
 */
public class Need extends DataEntity<Need> {
	
	private static final long serialVersionUID = 1L;
	private Techapply techapply;		// 资源申请编号 父类
	private String level;		// 级别
	private String number;		// 人数
	private String descContent;		// 工作描述 - 工作内容
	private String descLocation;		// 工作描述 - 工作地点
	private Date descTimeBegin;		// 工作描述 - 预计时间(起始)
	private Date descTimeEnd;		// 工作描述 - 预计时间(结束)
	private String manHour;		// 工时
	
	public Need() {
		super();
	}

	public Need(String id){
		super(id);
	}

	public Need(Techapply techapply){
		this.techapply = techapply;
	}

	@Length(min=1, max=64, message="资源申请编号长度必须介于 1 和 64 之间")
	public Techapply getTechapply() {
		return techapply;
	}

	public void setTechapply(Techapply techapply) {
		this.techapply = techapply;
	}
	
	@NotNull(message = "级别不能为空")
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}
	
	@Length(min=1, max=11, message="人数长度必须介于 1 和 11 之间")
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
	@Length(min=0, max=255, message="工作内容长度必须介于 0 和 255 之间")
	public String getDescContent() {
		return descContent;
	}

	public void setDescContent(String descContent) {
		this.descContent = descContent;
	}
	
	@Length(min=0, max=255, message="工作地点长度必须介于 0 和 255 之间")
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
	
	@Length(min=0, max=11, message="工时长度必须介于 0 和 11 之间")
	public String getManHour() {
		return manHour;
	}

	public void setManHour(String manHour) {
		this.manHour = manHour;
	}
	
}