/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.entity.invoice;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.persistence.ActEntity;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContractItem;
import com.thinkgem.jeesite.modules.sys.entity.User;

import java.util.Date;
import java.util.List;

/**
 * 项目开票Entity
 * @author jicdata
 * @version 2016-03-08
 *
 */
public class ProjectInvoice extends ActEntity<ProjectInvoice> {

	private static final long serialVersionUID = 1L;
	private ProjectApplyExternal apply;		// 外部项目立项编号
    private ProjectContract contract; // 合同申请单
    private ProjectContractItem contractItem; // 合同项
    private User saler; // 销售人员
    private List<ProjectInvoiceItem> invoiceItemList = Lists.newArrayList();		// 子表列表

    private List<ProjectInvoiceReturn> returnList = Lists.newArrayList();		// 子表列表

    private String contractType; // 合同类别
    private String invoiceType; // 开票类型
    private String customerName; //
    private String taxNo; //
    private String bankName; //
    private String bankNo; //
    private String address; //
    private String phone; //
    private String invoiceAmount; // 本次开票金额
    private Date returnDate;	// 预计回款日期
    private Date invoiceDate; // 开票日期

    private String attachment; // 文档附件

    /**
     * 功能，由于json传输时act对象不能使用，所以新建一个func用来接收前端传来的功能(如：save、update、delete等)
     *
     */
    private String func;


	public ProjectInvoice() {
		super();
	}

	public ProjectInvoice(String id){
		super(id);
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

    public User getSaler() {
        return saler;
    }

    public void setSaler(User saler) {
        this.saler = saler;
    }

    public List<ProjectInvoiceItem> getInvoiceItemList() {
        return invoiceItemList;
    }

    public void setInvoiceItemList(List<ProjectInvoiceItem> invoiceItemList) {
        this.invoiceItemList = invoiceItemList;
    }

    public List<ProjectInvoiceReturn> getReturnList() {
        return returnList;
    }

    public void setReturnList(List<ProjectInvoiceReturn> returnList) {
        this.returnList = returnList;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getTaxNo() {
        return taxNo;
    }

    public void setTaxNo(String taxNo) {
        this.taxNo = taxNo;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    @JsonFormat(pattern = "yyyy-MM-dd")
    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd")
    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }


    public boolean hasAct() {
	    if (super.hasAct()) {
	        if (StringUtils.isNotEmpty(this.getAct().getTaskId())) {
	            return true;
            }
        }
        return false;
    }
}