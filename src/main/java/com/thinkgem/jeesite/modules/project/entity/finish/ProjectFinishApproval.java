/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.entity.finish;

import com.thinkgem.jeesite.common.persistence.ActEntity;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

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

	private String estimatedProfitMargin; // 预算利润率
	private String settlementProfitMargin; // 结算利润率
	private String profitMarginFloat;
	private String profitMarginInfo; // 利润浮动
	private String feeInfo;
	private String runningPlan;
	private String summary;
	private String achievement;
	private Date finishDate;
	private String finishPrice;
	
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

	public String getEstimatedProfitMargin() {
		return estimatedProfitMargin;
	}

	public void setEstimatedProfitMargin(String estimatedProfitMargin) {
		this.estimatedProfitMargin = estimatedProfitMargin;
	}

	public String getSettlementProfitMargin() {
		return settlementProfitMargin;
	}

	public void setSettlementProfitMargin(String settlementProfitMargin) {
		this.settlementProfitMargin = settlementProfitMargin;
	}

	public String getProfitMarginFloat() {
		return profitMarginFloat;
	}

	public void setProfitMarginFloat(String profitMarginFloat) {
		this.profitMarginFloat = profitMarginFloat;
	}

	public String getProfitMarginInfo() {
		return profitMarginInfo;
	}

	public void setProfitMarginInfo(String profitMarginInfo) {
		this.profitMarginInfo = profitMarginInfo;
	}

	public String getFeeInfo() {
		return feeInfo;
	}

	public void setFeeInfo(String feeInfo) {
		this.feeInfo = feeInfo;
	}

	public String getRunningPlan() {
		return runningPlan;
	}

	public void setRunningPlan(String runningPlan) {
		this.runningPlan = runningPlan;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getAchievement() {
		return achievement;
	}

	public void setAchievement(String achievement) {
		this.achievement = achievement;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public String getFinishPrice() {
		return finishPrice;
	}

	public void setFinishPrice(String finishPrice) {
		this.finishPrice = finishPrice;
	}
}