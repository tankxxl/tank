/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.entity.tech;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.persistence.ActEntity;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.sys.entity.Office;
import com.thinkgem.jeesite.modules.sys.entity.User;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 资源申请Entity
 * @author jicdata:Arthur
 * @version 2016-03-11
 */
public class Techapply extends ActEntity<Techapply> {
	
	private static final long serialVersionUID = 1L;
	
	public static final String ASSIGN_FLAG_4_IS_ASSIGNED = "1";//已经派工
	/*
	 * 总结：
	 * (1)自动生成的create_by等字段由预处理自动生成，不可用作业务数据
	 * (2)关联信息使用对象表示方便前端展示及后端写入
	 */
	private User applicant;		// 申请人编号
	private ProjectApplyExternal project;		// 项目编号
	private String projectProgress;		// 项目进展
	private String projectProgressExtra;		// 项目进展，其他信息
	private Office office;		// 人力来源；技术部门
	private String applyReason;		// 申请原因
	private Date applyDate;		// 申请时间
	private Date beginApplyDate;		// 开始 申请时间
	private Date endApplyDate;		// 结束 申请时间
	private String processStatus;		// 流程审批状态
	private String assignFlag;	//派工标记。0表示没派工，1表示已经派工
	private List<Need> needList = Lists.newArrayList();		// 人员需求列表

	public Techapply() {
		super();
	}

	public Techapply(String id){
		super(id);
	}

	@NotNull(message="申请人不能为空")
	public User getApplicant() {
		return applicant;
	}

	public void setApplicant(User applicant) {
		this.applicant = applicant;
	}

	@NotNull(message="项目不能为空")
	public ProjectApplyExternal getProject() {
		return project;
	}

	public void setProject(ProjectApplyExternal project) {
		this.project = project;
	}

	@Length(min=0, max=64, message="项目进展长度必须介于 0 和 64 之间")
	public String getProjectProgress() {
		return projectProgress;
	}

	public void setProjectProgress(String projectProgress) {
		this.projectProgress = projectProgress;
	}
	
	@Length(min=1, max=128, message="项目进展(其他信息)长度必须介于 1 和 128 之间")
	public String getProjectProgressExtra() {
		return projectProgressExtra;
	}

	public void setProjectProgressExtra(String projectProgressExtra) {
		this.projectProgressExtra = projectProgressExtra;
	}
	
	@NotNull(message="人力来源(技术部门)不能为空")
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}
	
	@Length(min=1, max=64, message="申请原因长度必须介于 1 和 64 之间")
	public String getApplyReason() {
		return applyReason;
	}

	public void setApplyReason(String applyReason) {
		this.applyReason = applyReason;
	}

	@NotNull(message = "申请时间不能为空")
	public Date getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	public Date getBeginApplyDate() {
		return beginApplyDate;
	}

	public void setBeginApplyDate(Date beginApplyDate) {
		this.beginApplyDate = beginApplyDate;
	}

	public Date getEndApplyDate() {
		return endApplyDate;
	}

	public void setEndApplyDate(Date endApplyDate) {
		this.endApplyDate = endApplyDate;
	}
	
	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}
	
	public String getAssignFlag() {
		return assignFlag;
	}

	public void setAssignFlag(String assignFlag) {
		this.assignFlag = assignFlag;
	}

	public List<Need> getNeedList() {
		return needList;
	}

	public void setNeedList(List<Need> needList) {
		this.needList = needList;
	}
}