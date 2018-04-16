/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.entity.purchase;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkgem.jeesite.common.persistence.ActEntity;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContractItem;
import com.thinkgem.jeesite.modules.project.entity.execution.ProjectExecution;

import java.util.Date;

/**
 * 项目合同采购item Entity
 * @author jicdata
 * @version 2016-03-08
 */
public class ProjectPurchaseItem extends ActEntity<ProjectPurchaseItem> {

	private static final long serialVersionUID = 1L;
    private ProjectExecution execution;		// 合同执行申请表 父类
	private ProjectApplyExternal apply;		// 外部项目立项编号

    private ProjectContract contract;       // 合同申请单
    private ProjectContractItem contractItem;  // 合同项item

    private String brand;
    private String amount;
    private Date deliveryDate;
    private String warranty;
    private String supplier;
    private String supplierOrigin;

    private String contactPerson;
    private String contactPhone;
    private String paymentTerm;
    private String attachment;

    public ProjectPurchaseItem() {
        super();
    }

    public ProjectPurchaseItem(String id){
        super(id);
    }

    public ProjectPurchaseItem(ProjectExecution execution){
        this.execution = execution;
    }

    public ProjectApplyExternal getApply() {
        return apply;
    }

    public void setApply(ProjectApplyExternal apply) {
        this.apply = apply;
    }

    public ProjectExecution getExecution() {
        return execution;
    }

    public void setExecution(ProjectExecution execution) {
        this.execution = execution;
    }

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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @JsonFormat(pattern = "yyyy-MM-dd")
    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getSupplierOrigin() {
        return supplierOrigin;
    }

    public void setSupplierOrigin(String supplierOrigin) {
        this.supplierOrigin = supplierOrigin;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getPaymentTerm() {
        return paymentTerm;
    }

    public void setPaymentTerm(String paymentTerm) {
        this.paymentTerm = paymentTerm;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

}