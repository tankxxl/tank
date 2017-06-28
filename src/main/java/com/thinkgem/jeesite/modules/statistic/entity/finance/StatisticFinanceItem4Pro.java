package com.thinkgem.jeesite.modules.statistic.entity.finance;

import com.thinkgem.jeesite.common.persistence.DataEntity;

public class StatisticFinanceItem4Pro extends DataEntity<StatisticFinanceItem4Pro>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String techApplyId; // 项目code
	private String engnierName; // 项目code

	private String salary; // 工资
	private String pension; // 养老
	private String medical; // 医疗
	private String unemployment; // 失业
	private String occupationalInjury; // 工伤
	private String birth; // 生育
	private String rovidentFund; // 公积金
	private String insuranceAndHousingFund; // 五险一金
	private String labor; // 人力

	public StatisticFinanceItem4Pro() {
		super();
	}

	public String getTechApplyId() {
		return techApplyId;
	}

	public void setTechApplyId(String techApplyId) {
		this.techApplyId = techApplyId;
	}

	public String getEngnierName() {
		return engnierName;
	}

	public void setEngnierName(String engnierName) {
		this.engnierName = engnierName;
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
