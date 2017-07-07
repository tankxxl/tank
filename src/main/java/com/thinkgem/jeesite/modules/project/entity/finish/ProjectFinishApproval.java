/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.entity.finish;

import com.thinkgem.jeesite.common.persistence.ActEntity;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import org.hibernate.validator.constraints.Length;

/**
 * 结项Entity
 * @author jicdata
 * @version 2016-03-11
 */
public class ProjectFinishApproval extends ActEntity<ProjectFinishApproval> {
	
	private static final long serialVersionUID = 1L;
	private ProjectApplyExternal apply;		// 外部项目立项编号
	private String category;		// 结项种类
	private String riskAssessment;		// 风险评估
	private String projectAccounting;		// 项目核算
	private String projectAccountingFile;		// 项目核算附件
	// private String procInsId;		// 流程实例ID
	// private String processStatus;		// 流程审批状态
	
	public ProjectFinishApproval() {
		super();
	}

	public ProjectFinishApproval(String id){
		super(id);
	}


	
	public ProjectApplyExternal getApply() {
		return apply;
	}

	public void setApply(ProjectApplyExternal apply) {
		this.apply = apply;
	}

	@Length(min=1, max=5, message="结项种类长度必须介于 1 和 5 之间")
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	@Length(min=1, max=255, message="风险评估长度必须介于 1 和 255 之间")
	public String getRiskAssessment() {
		return riskAssessment;
	}

	public void setRiskAssessment(String riskAssessment) {
		this.riskAssessment = riskAssessment;
	}
	
	//@Length(min=1, max=255, message="项目核算长度必须介于 1 和 255 之间")
	public String getProjectAccounting() {
		return projectAccounting;
	}

	public void setProjectAccounting(String projectAccounting) {
		this.projectAccounting = projectAccounting;
	}

	public String getProjectAccountingFile() {
		return projectAccountingFile;
	}

	public void setProjectAccountingFile(String projectAccountingFile) {
		this.projectAccountingFile = projectAccountingFile;
	}
	
	
	
}