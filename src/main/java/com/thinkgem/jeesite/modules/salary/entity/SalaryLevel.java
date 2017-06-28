/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.salary.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.common.utils.excel.annotation.ExcelField;
import org.hibernate.validator.constraints.Length;

/**
 * 薪资等级Entity
 * @author jicdata
 * @version 2016-02-19
 */
public class SalaryLevel extends DataEntity<SalaryLevel> {
	
	private static final long serialVersionUID = 1L;
	private String grade;		// 工程师职级
	private String profession;		// 工种
	private String payMonthly;		// 月薪资
	private String nssf;		// 社保基数
	private String pension;		// 养老
	private String pensionHourly;		// 养老小时成本
	private String unemployment;		// 失业
	private String unemploymentHourly;		// 失业小时成本
	private String occupationalInjury;		// 工伤
	private String occupationalInjuryHourly;		// 工伤小时成本
	private String birth;		// 生育
	private String birthHourly;		// 生育小时成本
	private String medical;		// 医疗
	private String medicalHourly;		// 医疗小时成本
	private String providentFund;		// 公积金
	private String providentFundHourly;		// 公积金小时成本
	private String insuranceAndHousingFundHourly;		// 五险一金小时成本合计
	private String salaryHourly;		// 工资小时成本
	private String laborHourly;		// 人工小时总成本
	
	public SalaryLevel() {
		super();
	}

	public SalaryLevel(String id){
		super(id);
	}

	@ExcelField(title="工程师职级", align=2, sort=2)
	@Length(min=1, max=50, message="工程师职级长度必须介于 1 和 50 之间")
	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}
	@ExcelField(title="工种", align=2, sort=1)
	@Length(min=1, max=50, message="工种长度必须介于 1 和 50 之间")
	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}
	
	@ExcelField(title="月薪资", align=2, sort=3)
	public String getPayMonthly() {
		return payMonthly;
	}

	public void setPayMonthly(String payMonthly) {
		this.payMonthly = payMonthly;
	}
	
	@ExcelField(title="社保基数", align=2, sort=4)
	public String getNssf() {
		return nssf;
	}

	
	public void setNssf(String nssf) {
		this.nssf = nssf;
	}
	
	public String getPension() {
		return pension;
	}

	public void setPension(String pension) {
		this.pension = pension;
	}
	
	public String getPensionHourly() {
		return pensionHourly;
	}

	public void setPensionHourly(String pensionHourly) {
		this.pensionHourly = pensionHourly;
	}
	
	public String getUnemployment() {
		return unemployment;
	}

	public void setUnemployment(String unemployment) {
		this.unemployment = unemployment;
	}
	
	public String getUnemploymentHourly() {
		return unemploymentHourly;
	}

	public void setUnemploymentHourly(String unemploymentHourly) {
		this.unemploymentHourly = unemploymentHourly;
	}
	
	public String getOccupationalInjury() {
		return occupationalInjury;
	}

	public void setOccupationalInjury(String occupationalInjury) {
		this.occupationalInjury = occupationalInjury;
	}
	
	public String getOccupationalInjuryHourly() {
		return occupationalInjuryHourly;
	}

	public void setOccupationalInjuryHourly(String occupationalInjuryHourly) {
		this.occupationalInjuryHourly = occupationalInjuryHourly;
	}
	
	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}
	
	public String getBirthHourly() {
		return birthHourly;
	}

	public void setBirthHourly(String birthHourly) {
		this.birthHourly = birthHourly;
	}
	
	public String getMedical() {
		return medical;
	}

	public void setMedical(String medical) {
		this.medical = medical;
	}
	
	public String getMedicalHourly() {
		return medicalHourly;
	}

	public void setMedicalHourly(String medicalHourly) {
		this.medicalHourly = medicalHourly;
	}
	
	public String getProvidentFund() {
		return providentFund;
	}

	public void setProvidentFund(String providentFund) {
		this.providentFund = providentFund;
	}
	
	public String getProvidentFundHourly() {
		return providentFundHourly;
	}

	public void setProvidentFundHourly(String providentFundHourly) {
		this.providentFundHourly = providentFundHourly;
	}
	
	public String getInsuranceAndHousingFundHourly() {
		return insuranceAndHousingFundHourly;
	}

	public void setInsuranceAndHousingFundHourly(String insuranceAndHousingFundHourly) {
		this.insuranceAndHousingFundHourly = insuranceAndHousingFundHourly;
	}
	
	public String getSalaryHourly() {
		return salaryHourly;
	}

	public void setSalaryHourly(String salaryHourly) {
		this.salaryHourly = salaryHourly;
	}
	
	public String getLaborHourly() {
		return laborHourly;
	}

	public void setLaborHourly(String laborHourly) {
		this.laborHourly = laborHourly;
	}
	
}