/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.statistic.entity.finance;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

/**
 * 财务统计Entity
 * @author jicdata
 * @version 2016-03-17
 */
public class StatisticFinance extends DataEntity<StatisticFinance> {
	
	private static final long serialVersionUID = 1L;
	private String projectId;		// 项目id
	private String projectName;		// 项目名称
	private String projectCode;		// 项目code
	private String salary;		// 工资
	private String pension;		// 养老
	private String medical;		// 医疗
	private String unemployment;		// 失业
	private String occupationalInjury;		// 工伤
	private String birth;		// 生育
	private String rovidentFund;		// 公积金
	private String insuranceAndHousingFund;		// 五险一金
	private String labor;		// 人力
	
	public StatisticFinance() {
		super();
	}

	public StatisticFinance(String id){
		super(id);
	}

	@Length(min=0, max=64, message="项目id长度必须介于 0 和 64 之间")
	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	
	@Length(min=0, max=64, message="项目名称长度必须介于 0 和 64 之间")
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	@Length(min=0, max=64, message="项目code长度必须介于 0 和 64 之间")
	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	
	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}
	
	public String getPension() {
		return pension;
	}

	public void setPension(String pension) {
		this.pension = pension;
	}
	
	public String getMedical() {
		return medical;
	}

	public void setMedical(String medical) {
		this.medical = medical;
	}
	
	public String getUnemployment() {
		return unemployment;
	}

	public void setUnemployment(String unemployment) {
		this.unemployment = unemployment;
	}
	
	public String getOccupationalInjury() {
		return occupationalInjury;
	}

	public void setOccupationalInjury(String occupationalInjury) {
		this.occupationalInjury = occupationalInjury;
	}
	
	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}
	
	public String getRovidentFund() {
		return rovidentFund;
	}

	public void setRovidentFund(String rovidentFund) {
		this.rovidentFund = rovidentFund;
	}
	
	public String getInsuranceAndHousingFund() {
		return insuranceAndHousingFund;
	}

	public void setInsuranceAndHousingFund(String insuranceAndHousingFund) {
		this.insuranceAndHousingFund = insuranceAndHousingFund;
	}
	
	public String getLabor() {
		return labor;
	}

	public void setLabor(String labor) {
		this.labor = labor;
	}
	
}