/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.entity.invoice;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkgem.jeesite.common.persistence.ActEntity;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContractItem;

import java.util.Date;

/**
 * 开票item回款 Entity
 * @author jicdata
 * @version 2016-03-08
 */
public class ProjectInvoiceReturn extends ActEntity<ProjectInvoiceReturn> {

	private static final long serialVersionUID = 1L;
    private ProjectInvoice invoice;		// 开票申请表 父类
    private ProjectApplyExternal apply;		// 外部项目立项编号

    private ProjectContract contract;       // 合同申请单
    private ProjectContractItem contractItem;  // 合同项item

    // private String goodsName; //
    // private String spec; //
    // private String num; //
    // private String price; //
    private String amount; // 回款金额
    // private String costAmount; //

    private String attachment;

    private Date returnDate;	// 回款日期

//    private String procInsId;		// 流程实例ID
//    private String procStatus;		// 流程审批状态

    public ProjectInvoiceReturn() {
        super();
    }

    public ProjectInvoiceReturn(String id){
        super(id);
    }

    public ProjectInvoiceReturn(ProjectInvoice invoice){
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

    // public String getGoodsName() {
    //     return goodsName;
    // }
    //
    // public void setGoodsName(String goodsName) {
    //     this.goodsName = goodsName;
    // }
    //
    // public String getSpec() {
    //     return spec;
    // }
    //
    // public void setSpec(String spec) {
    //     this.spec = spec;
    // }
    //
    // public String getNum() {
    //     return num;
    // }
    //
    // public void setNum(String num) {
    //     this.num = num;
    // }
    //
    // public String getPrice() {
    //     return price;
    // }
    //
    // public void setPrice(String price) {
    //     this.price = price;
    // }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    // public String getCostAmount() {
    //     return costAmount;
    // }
    //
    // public void setCostAmount(String costAmount) {
    //     this.costAmount = costAmount;
    // }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    @JsonFormat(pattern = "yyyy-MM-dd")
    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

//    @Override
//    public String getProcInsId() {
//        return procInsId;
//    }
//
//    @Override
//    public void setProcInsId(String procInsId) {
//        this.procInsId = procInsId;
//    }

//    public String getProcStatus() {
//        return procStatus;
//    }
//
//    public void setProcStatus(String procStatus) {
//        this.procStatus = procStatus;
//    }

}