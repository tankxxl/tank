/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.entity.purchase;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.persistence.ActEntity;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContractItem;
import com.thinkgem.jeesite.modules.project.entity.execution.ProjectExecution;
import com.thinkgem.jeesite.modules.project.entity.execution.ProjectExecutionItem;

import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

/**
 * 项目合同采购Entity
 * @author jicdata
 * @version 2016-03-08
 */
public class ProjectPurchase extends ActEntity<ProjectPurchase> {

	private static final long serialVersionUID = 1L;
	private ProjectApplyExternal apply;		// 外部项目立项编号
    private ProjectContract contract;
    private ProjectContractItem contractItem;
    private ProjectExecution execution;
    private ProjectExecutionItem executionItem;

    private String purchaseCode; // 采购合同号
    private String amount; // 采购金额
    private String supplier; // 供应商名称
    private String supplierPerson; // 供应商联系人
    private String supplierPhone; // 供应商联系人电话
    private String supplierOrigin; // 供应商来源

    private String contractType; // 合同类型-枚举-设备、人力
    private String contractInfo;		// 销售合同状况，于合同执行依据相同
    private String amountInfo;		// 采购金额说明
    private String paymentInfo;		// 付款条件说明
    private String inventoryInfo;		// 产品配置清单说明
    private String warrantyInfo;		// 保修条款说明
    private String deliveryInfo;		// 交货日期说明


    private String attachment; // 文档附件
//    private String procInsId;		// 流程实例ID
//    private String procStatus;		// 流程审批状态

	public ProjectPurchase() {
		super();
	}

	public ProjectPurchase(String id){
		super(id);
	}
	
	public ProjectApplyExternal getApply() {
		return apply;
	}

	public void setApply(ProjectApplyExternal apply) {
		this.apply = apply;
	}


//	@XmlTransient
//	public List<String> getPrintingPasteList() {
//		if (printingPaste == null){
//			return Lists.newArrayList();
//		}else{
//			return Lists.newArrayList(StringUtils.split(printingPaste, ","));
//		}
//	}
//
//	public void setPrintingPasteList(List<String> printingPasteList) {
//		if (printingPasteList == null){
//			this.printingPaste = "";
//		}else{
//			this.printingPaste = ","+StringUtils.join(printingPasteList, ",") + ",";
//		}
//	}
	
//	@XmlTransient
//	public String getCategory4Export(){
//		if (category == null){
//			return "";
//		}else{
//			return DictUtils.getDictLabels(category, "tender_category", "");
//		}
//	}
//
//	@XmlTransient
//	public String getPrintingPaste4Export(){
//		if (printingPaste == null){
//			return "";
//		}else{
//			return DictUtils.getDictLabels(printingPaste, "tender_printing_paste", "");
//		}
//	}


    public ProjectContract getContract() {
        return contract;
    }

    public void setContract(ProjectContract contract) {
        this.contract = contract;
    }

    public ProjectContractItem getContractItem() {
        return contractItem;
    }

    public void setContractItem(ProjectContractItem contractItem) {
        this.contractItem = contractItem;
    }

    public ProjectExecution getExecution() {
        return execution;
    }

    public void setExecution(ProjectExecution execution) {
        this.execution = execution;
    }

    public ProjectExecutionItem getExecutionItem() {
        return executionItem;
    }

    public void setExecutionItem(ProjectExecutionItem executionItem) {
        this.executionItem = executionItem;
    }

    public String getPurchaseCode() {
        return purchaseCode;
    }

    public void setPurchaseCode(String purchaseCode) {
        this.purchaseCode = purchaseCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getSupplierPerson() {
        return supplierPerson;
    }

    public void setSupplierPerson(String supplierPerson) {
        this.supplierPerson = supplierPerson;
    }

    public String getSupplierPhone() {
        return supplierPhone;
    }

    public void setSupplierPhone(String supplierPhone) {
        this.supplierPhone = supplierPhone;
    }

    public String getSupplierOrigin() {
        return supplierOrigin;
    }

    public void setSupplierOrigin(String supplierOrigin) {
        this.supplierOrigin = supplierOrigin;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getContractInfo() {
        return contractInfo;
    }

    public void setContractInfo(String contractInfo) {
        this.contractInfo = contractInfo;
    }

    public String getAmountInfo() {
        return amountInfo;
    }

    public void setAmountInfo(String amountInfo) {
        this.amountInfo = amountInfo;
    }

    public String getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(String paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public String getInventoryInfo() {
        return inventoryInfo;
    }

    public void setInventoryInfo(String inventoryInfo) {
        this.inventoryInfo = inventoryInfo;
    }

    public String getWarrantyInfo() {
        return warrantyInfo;
    }

    public void setWarrantyInfo(String warrantyInfo) {
        this.warrantyInfo = warrantyInfo;
    }

    public String getDeliveryInfo() {
        return deliveryInfo;
    }

    public void setDeliveryInfo(String deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }



    // jsp前端checkbox使用，前端checkbox使用数组，后端使用字符串，使用逗号分隔
    // 所以前端使用executionBasisList字段，保存数据库时使用contractInfo字段
    @XmlTransient
    public List<String> getExecutionBasisList() {
        if (contractInfo == null){
            return Lists.newArrayList();
        }else{
            return Lists.newArrayList(StringUtils.split(contractInfo, ","));
        }
    }

    public void setExecutionBasisList(List<String> executionBasisList) {
        if (executionBasisList == null){
            this.contractInfo = "";
        }else{
            this.contractInfo = ","+StringUtils.join(executionBasisList, ",") + ",";
        }
    }

}