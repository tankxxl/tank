/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.entity.invoice;

import com.thinkgem.jeesite.common.persistence.ActEntity;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContractItem;

/**
 * 开票item Entity
 * @author jicdata
 * @version 2016-03-08
 */
public class ProjectInvoiceItem extends ActEntity<ProjectInvoiceItem> {

	private static final long serialVersionUID = 1L;
    private ProjectInvoice invoice;		// 开票申请表 父类
    private ProjectApplyExternal apply;		// 外部项目立项编号

    private ProjectContract contract;       // 合同申请单
    private ProjectContractItem contractItem;  // 合同项item

    private String goodsName; //
    private String spec; //
    private String num; //
    private String price; //
    private String amount; //
    private String costAmount; //
    private String invoiceNo; //

    private String attachment;

    private String procInsId;		// 流程实例ID
    private String procStatus;		// 流程审批状态

    public ProjectInvoiceItem() {
        super();
    }

    public ProjectInvoiceItem(String id){
        super(id);
    }

    public ProjectInvoiceItem(ProjectInvoice invoice){
        this.invoice = invoice;
    }

    public ProjectInvoice getInvoice() {
        return invoice;
    }

    public void setInvoice(ProjectInvoice invoice) {
        this.invoice = invoice;
    }

    public ProjectApplyExternal getApply() {
        return apply;
    }

    public void setApply(ProjectApplyExternal apply) {
        this.apply = apply;
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

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCostAmount() {
        return costAmount;
    }

    public void setCostAmount(String costAmount) {
        this.costAmount = costAmount;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    @Override
    public String getProcInsId() {
        return procInsId;
    }

    @Override
    public void setProcInsId(String procInsId) {
        this.procInsId = procInsId;
    }

    public String getProcStatus() {
        return procStatus;
    }

    public void setProcStatus(String procStatus) {
        this.procStatus = procStatus;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }
}