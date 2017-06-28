/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.entity.tech;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.modules.sys.entity.User;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 派工Entity
 * @author jicdata:Arthur
 * @version 2016-03-11
 */
public class Assigning extends DataEntity<Assigning> {

	private static final long serialVersionUID = 1L;
	private Techapply techapply;		// 申请编号
	private User assigningor;		// 派工人编号
	private Date assigningDate;		// 派工时间
	private String completed;		// 工期是否结束
	private Date beginAssigningDate;		// 开始 派工时间
	private Date endAssigningDate;		// 结束 派工时间
	private List<Workorder> workorderList = Lists.newArrayList();		// 工单列表


	public Assigning() {
		super();
	}

	public Assigning(String id){
		super(id);
	}

	@NotNull(message="申请编号不能为空")
	public Techapply getTechapply() {
		return techapply;
	}

	public void setTechapply(Techapply techapply) {
		this.techapply = techapply;
	}

	@NotNull(message="派工人编号不能为空")
	public User getAssigningor() {
		return assigningor;
	}

	public void setAssigningor(User assigningor) {
		this.assigningor = assigningor;
	}

	@NotNull(message = "派工时间不能为空")
	public Date getAssigningDate() {
		return assigningDate;
	}

	public void setAssigningDate(Date assigningDate) {
		this.assigningDate = assigningDate;
	}

	public String getCompleted() {
		return completed;
	}

	public void setCompleted(String completed) {
		this.completed = completed;
	}

	public Date getBeginAssigningDate() {
		return beginAssigningDate;
	}

	public void setBeginAssigningDate(Date beginAssigningDate) {
		this.beginAssigningDate = beginAssigningDate;
	}

	public Date getEndAssigningDate() {
		return endAssigningDate;
	}

	public void setEndAssigningDate(Date endAssigningDate) {
		this.endAssigningDate = endAssigningDate;
	}

	public List<Workorder> getWorkorderList() {
		return workorderList;
	}

	public void setWorkorderList(List<Workorder> workorderList) {
		this.workorderList = workorderList;
	}
}