package com.thinkgem.jeesite.modules.statistic.entity.manhour;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkgem.jeesite.common.persistence.DataEntity;

import java.util.Date;

public class DeptManhour4pro  extends DataEntity<DeptManhour4pro> {

	private static final long serialVersionUID = 1L;
	private String projectId;
	private String officeId;
	private String applyId; //技术支援申请单
	private String officeName;
	private String techName; //技术人员名称
	private Date applyDate;
	private String manhour;
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getOfficeId() {
		return officeId;
	}
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}
	public String getApplyId() {
		return applyId;
	}
	public void setApplyId(String applyId) {
		this.applyId = applyId;
	}
	public String getOfficeName() {
		return officeName;
	}
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	
	public String getTechName() {
		return techName;
	}
	public void setTechName(String techName) {
		this.techName = techName;
	}
	public String getManhour() {
		return manhour;
	}
	public void setManhour(String manhour) {
		this.manhour = manhour;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}
	
	
	
}
