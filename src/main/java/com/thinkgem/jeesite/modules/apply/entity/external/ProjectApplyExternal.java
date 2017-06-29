/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.apply.entity.external;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkgem.jeesite.common.persistence.ActEntity;
import com.thinkgem.jeesite.modules.customer.entity.Customer;
import com.thinkgem.jeesite.modules.customer.entity.CustomerContact;
import com.thinkgem.jeesite.modules.sys.entity.Office;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Digits;
import java.util.Date;
import java.util.List;

/**
 * 外部立项申请Entity
 * 
 * @author jicdata
 * @version 2016-02-23
 */
public class ProjectApplyExternal extends ActEntity<ProjectApplyExternal> {

	private static final long serialVersionUID = 1L;
	private String projectCode; // 项目编码
	private String projectName; // 项目名称
	private User saler; // 销售人员
	private Office saleOffice;// 销售部门
	private Customer customer; // 客户
	private CustomerContact customerContact; // 客户联系人

	private Double estimatedContractAmount; // 预计合同金额 万元
	private Double estimatedGrossProfitMargin; // 预计毛利率
	private Date estimatedTimeOfSigning; // 预计签约时间
	private String category; // 项目类别
	private String description; // 项目描述
	private String estimatedGrossProfitMarginDescription; // 项目毛利率说明
	private String riskAnalysis; // 项目风险分析
	private String documentAttachmentPath; // 文档附件
	private String processInstanceId; // 流程实例ID
	private String processStatus; // 流程审批状态
	private String ownership; // 项目归属：华科01，数据02

	private String proMainStage;// 项目大阶段：立项，投标，合同等
	private String proStage;// 项目小阶段：未审批、审批中、审批通过
	
	private List<String> queryStage; // 查询条件字符串，可以保存多个状态，在其它审批阶段过滤项目时使用，如：11,12,13，sql中使用 in (11,12,13)

	private String applyFlag; 
	private String biddingFlag;
	private String contractFlag;
	private String finishFlag;
	
	private String outsourcing; // 此项目是否有外包

    private String docPath; // 项目路径标识，参与附件路径，uuid

	private String selfDev; // bjkj-是否涉及自研
	private Date beginDate; // bjkj-预计开始时间
	private Date endDate; // bjkj-预计截止时间
	private User projectManager; // bjkj-项目经理
	private String projectMembers; // bjkj-项目组成员
	private String pattern; // bjkj-项目业务模式/产品形式
	private String target; // bjkj-项目目标/阶段性目标
	private String analysis; // bjkj-项目盈利分析
	private String resource; // bjkj-项目需要资源
	private String membersName; // 合成字段，用于前台展示，数据来源于数据库



    public String getDocPath() {
        return docPath;
    }

    public void setDocPath(String docPath) {
        this.docPath = docPath;
    }


	public ProjectApplyExternal() {
		super();
	}

	public ProjectApplyExternal(String id) {
		super(id);
	}

	/**
	 * 插入之前执行方法，需要手动调用(设置销售为第一次填报的用户) 在ProjectInternalService.java中save方法调用了
	 */
	public void preInsert4ProInteralApply() {

		if (this.getIsNewRecord()) {
			// 将当前录入者当成立项申请人
			User user2 = UserUtils.getUser();
			this.saler = user2;
		}
	};

	@Length(min = 1, max = 64, message = "项目编码长度必须介于 1 和 64 之间")
	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	@Length(min = 1, max = 200, message = "项目名称长度必须介于 1 和 64 之间")
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public User getSaler() {
		return saler;
	}

	public void setSaler(User saler) {
		this.saler = saler;
	}

	// @NotNull(message = "客户不能为空")
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	// @NotNull(message = "客户联系人不能为空")
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

	@Digits(integer = 2, fraction = 2, message = "毛利率必须小于100，最多有两位小数。")
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

	@Length(min = 0, max = 5, message = "项目类别长度必须介于 0 和 5 之间")
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Length(min = 0, max = 255, message = "项目描述长度必须介于 0 和 255 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Length(min = 0, max = 255, message = "项目毛利率说明长度必须介于 0 和 255 之间")
	public String getEstimatedGrossProfitMarginDescription() {
		return estimatedGrossProfitMarginDescription;
	}

	public void setEstimatedGrossProfitMarginDescription(String estimatedGrossProfitMarginDescription) {
		this.estimatedGrossProfitMarginDescription = estimatedGrossProfitMarginDescription;
	}

	@Length(min = 0, max = 255, message = "项目风险分析长度必须介于 0 和 255 之间")
	public String getRiskAnalysis() {
		return riskAnalysis;
	}

	public void setRiskAnalysis(String riskAnalysis) {
		this.riskAnalysis = riskAnalysis;
	}

	@Length(min = 0, max = 20000, message = "文档附件长度必须介于 0 和 20000 之间")
	public String getDocumentAttachmentPath() {
		return documentAttachmentPath;
	}

	public void setDocumentAttachmentPath(String documentAttachmentPath) {
		this.documentAttachmentPath = documentAttachmentPath;
	}

	@Length(min = 0, max = 100, message = "流程实例ID长度必须介于 0 和 100 之间")
	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	@Length(min = 0, max = 100, message = "流程审批状态长度必须介于 0 和 100 之间")
	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	public Office getSaleOffice() {
		return saleOffice;
	}

	public void setSaleOffice(Office saleOffice) {
		this.saleOffice = saleOffice;
	}

	public String getOwnership() {
		return ownership;
	}

	public void setOwnership(String ownership) {
		this.ownership = ownership;
	}

	public String getProMainStage() {
		return proMainStage;
	}

	public void setProMainStage(String proMainStage) {
		this.proMainStage = proMainStage;
	}

	public String getProStage() {
		return proStage;
	}

	public void setProStage(String proStage) {
		this.proStage = proStage;
	}
	
	public void setMainStageAuditing() {
		
	}
	
	public void setMainStageFinished() {
		
	}
	
	public List<String> getQueryStage() {
		return queryStage;
	}

	public void setQueryStage(List<String> queryStage) {
		this.queryStage = queryStage;
	}
	
	public String getApplyFlag() {
		return applyFlag;
	}

	public void setApplyFlag(String applyFlag) {
		this.applyFlag = applyFlag;
	}

	public String getBiddingFlag() {
		return biddingFlag;
	}

	public void setBiddingFlag(String biddingFlag) {
		this.biddingFlag = biddingFlag;
	}

	public String getContractFlag() {
		return contractFlag;
	}

	public void setContractFlag(String contractFlag) {
		this.contractFlag = contractFlag;
	}

	public String getFinishFlag() {
		return finishFlag;
	}

	public void setFinishFlag(String finishFlag) {
		this.finishFlag = finishFlag;
	}
	
	public String getOutsourcing() {
		return outsourcing;
	}

	public void setOutsourcing(String outsourcing) {
		this.outsourcing = outsourcing;
	}



	public String getSelfDev() {
		return selfDev;
	}

	public void setSelfDev(String selfDev) {
		this.selfDev = selfDev;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public User getProjectManager() {
		return projectManager;
	}

	public void setProjectManager(User projectManager) {
		this.projectManager = projectManager;
	}

	public String getProjectMembers() {
		return projectMembers;
	}

	public void setProjectMembers(String projectMembers) {
		this.projectMembers = projectMembers;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getAnalysis() {
		return analysis;
	}

	public void setAnalysis(String analysis) {
		this.analysis = analysis;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getMembersName() {
		return membersName;
	}

	public void setMembersName(String membersName) {
		this.membersName = membersName;
	}
	
}