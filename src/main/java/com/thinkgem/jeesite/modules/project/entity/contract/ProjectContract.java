/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.entity.contract;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.persistence.ActEntity;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.sys.entity.Dict;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;

import java.util.Date;
import java.util.List;


/**
 * 合同Entity
 * @author jicdata
 * @version 2016-03-09
 */
public class ProjectContract extends ActEntity<ProjectContract> {

	private static final long serialVersionUID = 1L;
	private ProjectApplyExternal apply;//项目立项bean
	private String contractCode; // 合同编号
	private String clientName; // 合同对方名称
	private String amount; // 合同总金额
	private String amountDetail; // 合同金额明细
	private String contentSummary; // 合同内容摘要
	private Date beginDate; // bjkj-合同有效期
	private Date endDate; // bjkj-合同有效期
	private String resignFlag; // 是否为续签合同
	private String resignInfo; // 若续签合同的相关条款与原合同不一致，请进行说明

	private String paymentType; // 付款方式

	private String contractType; // 合同类型

	// 采购合同表单
	private String purchaseCode; // 采购申请编号
	private String supplierName; // 供应商名称
	private String profitMargin; // 合同毛利率


	// 销售合同表单
	private String deliveryDate;
	private String deliveryAddress;



	// 管理合同表单
	private String oaNo; // 事权审批OA号

	private String originCode; // 事权审批OA号

	private String sealType; // 印章类型
	private User projectManager;//项目经理
	private String stage;		// 项目步骤
	// private String procInsId;		// 流程实例ID
	// private String processStatus;		// 流程审批状态
	private String attachment; // 文档附件

	// @Valid
	// @NotEmpty(message = "至少要填写一个合同项。${validatedValue}")
	private List<ProjectContractItem> projectContractItemList = Lists.newArrayList();		// 子表列表

	public ProjectContract() {
		super();
		this.apply = new ProjectApplyExternal();
	}

	public ProjectContract(String id){
		super(id);
	}

	@JsonIgnore
	public String getView() {
		if (StringUtils.isEmpty(contractType))
			return "";
		if (contractType.equals("1")
				|| contractType.equals("2")
				|| contractType.equals("5") ) {
			return getDictRemarks("1") + "View";
		}
		return getDictRemarks() + "View";
	}

	@JsonIgnore
	public String getForm() {
		if (StringUtils.isEmpty(contractType))
			return "";
		if (contractType.equals("1")
				|| contractType.equals("2")
				|| contractType.equals("5") ) {
			return getDictRemarks("1") + "Form";
		}
		return getDictRemarks() + "Form";
	}

	// Remarks保存的是自己的前缀，或者是自己的工作流key
	@JsonIgnore
	public String getDictRemarks(String aType) {
		String aContractType = aType;
		if (StringUtils.isEmpty(aContractType)) {
			aContractType = this.contractType;
		}
		if (StringUtils.isEmpty(aContractType)) {
			return "";
		}
		String view = "";
		List<Dict> typeList = DictUtils.getDictList("jic_contract_type");
		for (Dict dict : typeList) {
			if ( aContractType.equals(dict.getValue()) ) {
				view = dict.getRemarks();
				break;
			}
		}
		if (StringUtils.isEmpty(view)) {
			return "";
		}
		return view;
	}

	@JsonIgnore
	public String getDictRemarks() {
		return getDictRemarks(contractType);
	}

	public ProjectApplyExternal getApply() {
		return apply;
	}

	public void setApply(ProjectApplyExternal apply) {
		this.apply = apply;
	}

	public User getProjectManager() {
		return projectManager;
	}

	public void setProjectManager(User projectManager) {
		this.projectManager = projectManager;
	}

	// @Length(min=1, max=64, message="项目步骤长度必须介于 1 和 64 之间")
	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public String getContractCode() {
		return contractCode;
	}

	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getAmountDetail() {
		return amountDetail;
	}

	public void setAmountDetail(String amountDetail) {
		this.amountDetail = amountDetail;
	}

	public String getContentSummary() {
		return contentSummary;
	}

	public void setContentSummary(String contentSummary) {
		this.contentSummary = contentSummary;
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

	public String getResignFlag() {
		return resignFlag;
	}

	public void setResignFlag(String resignFlag) {
		this.resignFlag = resignFlag;
	}

	public String getResignInfo() {
		return resignInfo;
	}

	public void setResignInfo(String resignInfo) {
		this.resignInfo = resignInfo;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getPurchaseCode() {
		return purchaseCode;
	}

	public void setPurchaseCode(String purchaseCode) {
		this.purchaseCode = purchaseCode;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getProfitMargin() {
		return profitMargin;
	}

	public void setProfitMargin(String profitMargin) {
		this.profitMargin = profitMargin;
	}

	public String getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public String getOaNo() {
		return oaNo;
	}

	public void setOaNo(String oaNo) {
		this.oaNo = oaNo;
	}

	public String getSealType() {
		return sealType;
	}

	public void setSealType(String sealType) {
		this.sealType = sealType;
	}

	public List<ProjectContractItem> getProjectContractItemList() {
		return projectContractItemList;
	}

	public void setProjectContractItemList(List<ProjectContractItem> projectContractItemList) {
		this.projectContractItemList = projectContractItemList;
	}

	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}


	public String getOriginCode() {
		return originCode;
	}

	public void setOriginCode(String originCode) {
		this.originCode = originCode;
	}
}