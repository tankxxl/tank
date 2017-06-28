/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.entity.contract;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 合同Entity
 * @author jicdata
 * @version 2016-03-09
 */
public class ProjectContractItem extends DataEntity<ProjectContractItem> {
	
	private static final long serialVersionUID = 1L;
	private ProjectContract contract;		// 合同表 父类
	private String contractCode;		// 合同编号
	private String contractAmount;		// 签约金额
    private String grossMargin; // 签约毛利
	private Double grossProfitMargin; // 预计毛利率
	private String termsOfPayment;		// 收款条款
	private String termsOfWarranty;		// 保修条款
	private Date contractStartTime;		// 合同起始时间
	private Date contractEndTime;		// 合同起始时间
	private String trainingOrOutsourcing;		// 培训、外包
	private String other;		// 其他
	private String procInsId;		// 流程实例ID
	private String processStatus;		// 流程审批状态
	
	public ProjectContractItem() {
		super();
		this.contract = new ProjectContract();
	}

	public ProjectContractItem(String id){
		super(id);
	}

	public ProjectContractItem(ProjectContract contract){
		this.contract = contract;
	}

	
	
	public ProjectContract getContract() {
		return contract;
	}

	public void setContract(ProjectContract contract) {
		this.contract = contract;
	}

	@Length(min=0, max=64, message="合同编号长度必须介于 1 和 64 之间")
	public String getContractCode() {
		return contractCode;
	}

	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}
	
	public String getContractAmount() {
		return contractAmount;
	}

	public void setContractAmount(String contractAmount) {
		this.contractAmount = contractAmount;
	}
	
	@Length(min=1, max=255, message="收款条款长度必须介于 1 和 255 之间")
	public String getTermsOfPayment() {
		return termsOfPayment;
	}

	public void setTermsOfPayment(String termsOfPayment) {
		this.termsOfPayment = termsOfPayment;
	}
	
	@Length(min=1, max=255, message="保修条款长度必须介于 1 和 255 之间")
	public String getTermsOfWarranty() {
		return termsOfWarranty;
	}

	public void setTermsOfWarranty(String termsOfWarranty) {
		this.termsOfWarranty = termsOfWarranty;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	@NotNull(message="合同起始时间不能为空")
	public Date getContractStartTime() {
		return contractStartTime;
	}

	public void setContractStartTime(Date contractStartTime) {
		this.contractStartTime = contractStartTime;
	}

	
	
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	@NotNull(message="合同结束时间不能为空")
	public Date getContractEndTime() {
		return contractEndTime;
	}

	public void setContractEndTime(Date contractEndTime) {
		this.contractEndTime = contractEndTime;
	}

	
	@Length(min=1, max=255, message="培训、外包长度必须介于 1 和 255 之间")
	public String getTrainingOrOutsourcing() {
		return trainingOrOutsourcing;
	}

	public void setTrainingOrOutsourcing(String trainingOrOutsourcing) {
		this.trainingOrOutsourcing = trainingOrOutsourcing;
	}
	
	@Length(min=1, max=255, message="其他长度必须介于 1 和 255 之间")
	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}
	
	@Length(min=0, max=100, message="流程实例ID长度必须介于 0 和 100 之间")
	public String getProcInsId() {
		return procInsId;
	}

	public void setProcInsId(String procInsId) {
		this.procInsId = procInsId;
	}
	
	@Length(min=0, max=100, message="流程审批状态长度必须介于 0 和 100 之间")
	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	@Digits(integer = 2, fraction = 2, message = "毛利率必须小于100，且最多有2位小数。")
	public Double getGrossProfitMargin() {
		return grossProfitMargin;
	}

    public String getGrossMargin() {
        return grossMargin;
    }

    public void setGrossMargin(String grossMargin) {
        this.grossMargin = grossMargin;
    }

    public void setGrossProfitMargin(Double grossProfitMargin) {
		this.grossProfitMargin = grossProfitMargin;
	}
	
}