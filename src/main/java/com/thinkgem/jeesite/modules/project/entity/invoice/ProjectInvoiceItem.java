/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.entity.invoice;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.customer.entity.CustomerInvoice;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContractItem;
import com.thinkgem.jeesite.modules.sys.entity.User;

import java.util.Date;

/**
 * 开票item Entity
 * @author jicdata
 * @version 2016-03-08
 */
public class ProjectInvoiceItem extends DataEntity<ProjectInvoiceItem> {

	private static final long serialVersionUID = 1L;
    private ProjectInvoice invoice;		// 开票申请表 父类
    private ProjectApplyExternal apply;		// 外部项目立项编号

    private ProjectContract contract;       // 合同申请单 合同号直接在申请单中，一对一
    private ProjectContractItem contractItem;  // 合同项item bjkj不用

    private User saler;

    private CustomerInvoice customerInvoice; // 开票客户

    private String invoiceType; // 开票类型

    private String goodsName; // 商品名称
    private String spec; // 规格型号
    private String num; // 数量(套)
    private String price; // 单价
    private String amount; // 金额
    private String costAmount; // 本次结转成本金额

    private String attachment;

    private String procInsId;		// 流程实例ID
    private String procStatus;		// 流程审批状态


    // bj invoice
    private String clientName; // 客户名称
    private String content; // 开票内容
    private String settlement; // 结算周期
    private String unit; // 单位(元、套、个)
    private String profit; // 利润点
    private String invoiceNo; // 发票号
    private int ver; // 版本号，数据库真实存在
    private String pId; // 跟ver字段一起使用，实现版本，最新的发票，此值为空
    private int verNum; // 共有多少版本，计算字段或者组合字段，在sql中运算
    private String reason; // 重开票原因

    private Date returnDate;	// 预计回款日期
    private String returnAmount; // 回款金额

    private String invalid; // 作废标志0有效1作废



    public ProjectInvoiceItem() {
        super();
        this.invalid = DEL_FLAG_NORMAL;
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

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
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


    public String getProcStatus() {
        return procStatus;
    }

    public void setProcStatus(String procStatus) {
        this.procStatus = procStatus;
    }


    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSettlement() {
        return settlement;
    }

    public void setSettlement(String settlement) {
        this.settlement = settlement;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public int getVer() {
        return ver;
    }

    public void setVer(int ver) {
        this.ver = ver;
    }

    public int getVerNum() {
        return verNum;
    }

    public void setVerNum(int verNum) {
        this.verNum = verNum;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    // 版本号自增1
    public void incVer() {
        setVer(this.getVer() + 1);
    }

    public String getInvalid() {
        return invalid;
    }

    public void setInvalid(String invalid) {
        this.invalid = invalid;
    }

    public User getSaler() {
        return saler;
    }

    public void setSaler(User saler) {
        this.saler = saler;
    }

    public String getReturnAmount() {
        return returnAmount;
    }

    public void setReturnAmount(String returnAmount) {
        this.returnAmount = returnAmount;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public CustomerInvoice getCustomerInvoice() {
        return customerInvoice;
    }

    public void setCustomerInvoice(CustomerInvoice customerInvoice) {
        this.customerInvoice = customerInvoice;
    }
}