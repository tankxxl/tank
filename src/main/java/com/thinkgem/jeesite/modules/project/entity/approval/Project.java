/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.entity.approval;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.modules.customer.entity.Customer;
import com.thinkgem.jeesite.modules.customer.entity.CustomerContact;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

/**
 * 立项Entity
 * @author jicdata
 * @version 2016-02-22
 */
public class Project extends DataEntity<Project> {
	
	private static final long serialVersionUID = 1L;
	private String projectCode;		// 项目编码
	private String projectName;		// 项目名称
	private User user;		// 销售人员编号
	private Customer customer;		// 客户编号
	private CustomerContact customerContact;		// 客户联系人编号
	private Double estimatedContractAmount;		// 预计合同金额
	private Double estimatedGrossProfitMargin;		// 预计毛利率
	private Date estimatedTimeOfSigning;		// 预计签约时间
	private String category;		// 项目类别
	private String description;		// 项目描述
	private String estimatedGrossProfitMarginDescription;		// 项目毛利率说明
	private String riskAnalysis;		// 项目风险分析
	private String documentAttachmentPath;		// 文档附件
	
	public Project() {
		super();
	}

	public Project(String id){
		super(id);
	}

	/**
	 * 插入之前执行方法，需要手动调用(设置销售为第一次填报的用户)
	 */
	public void preInsert4Pro() {
		
		if (this.getIsNewRecord()){
			//将当前录入者当成销售
			User user2 = UserUtils.getUser();
			this.user = user2;
		}
	};
	
	
	@Length(min=0, max=64, message="项目编码长度必须介于 0 和 64 之间")
	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	
	@Length(min=0, max=64, message="项目名称长度必须介于 0 和 64 之间")
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	public CustomerContact getCustomerContact() {
		return customerContact;
	}

	public void setCustomerContact(CustomerContact customerContact) {
		this.customerContact = customerContact;
	}
	
	public Double getEstimatedContractAmount() {
		return estimatedContractAmount;
	}

	public void setEstimatedContractAmount(Double estimatedContractAmount) {
		this.estimatedContractAmount = estimatedContractAmount;
	}
	
	public Double getEstimatedGrossProfitMargin() {
		return estimatedGrossProfitMargin;
	}

	public void setEstimatedGrossProfitMargin(Double estimatedGrossProfitMargin) {
		this.estimatedGrossProfitMargin = estimatedGrossProfitMargin;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getEstimatedTimeOfSigning() {
		return estimatedTimeOfSigning;
	}

	public void setEstimatedTimeOfSigning(Date estimatedTimeOfSigning) {
		this.estimatedTimeOfSigning = estimatedTimeOfSigning;
	}
	
	@Length(min=0, max=5, message="项目类别长度必须介于 0 和 5 之间")
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	@Length(min=0, max=255, message="项目描述长度必须介于 0 和 255 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Length(min=0, max=255, message="项目毛利率说明长度必须介于 0 和 255 之间")
	public String getEstimatedGrossProfitMarginDescription() {
		return estimatedGrossProfitMarginDescription;
	}

	public void setEstimatedGrossProfitMarginDescription(String estimatedGrossProfitMarginDescription) {
		this.estimatedGrossProfitMarginDescription = estimatedGrossProfitMarginDescription;
	}
	
	@Length(min=0, max=255, message="项目风险分析长度必须介于 0 和 255 之间")
	public String getRiskAnalysis() {
		return riskAnalysis;
	}

	public void setRiskAnalysis(String riskAnalysis) {
		this.riskAnalysis = riskAnalysis;
	}
	
	@Length(min=0, max=20000, message="文档附件长度必须介于 0 和 20000 之间")
	public String getDocumentAttachmentPath() {
		return documentAttachmentPath;
	}

	public void setDocumentAttachmentPath(String documentAttachmentPath) {
		this.documentAttachmentPath = documentAttachmentPath;
	}
	
}