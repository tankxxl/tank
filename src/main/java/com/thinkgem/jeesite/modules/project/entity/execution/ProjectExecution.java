/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.entity.execution;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.persistence.ActEntity;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContractItem;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;

import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

/**
 * 项目合同执行Entity
 * @author jicdata
 * @version 2016-03-08
 */
public class ProjectExecution extends ActEntity<ProjectExecution> {

	private static final long serialVersionUID = 1L;
	private ProjectApplyExternal apply;		// 外部项目立项编号

    private ProjectContract contract;       //
    private ProjectContractItem contractItem;// 关联的合同项

//    private String procInsId;		// 流程实例ID
//    private String procStatus;		// 流程审批状态

    // 执行合同号-当没有合同编号时，在此流程指定一个合同号，用于后面业务流程管理(如开票必须对应一个合同号)
    private String executionContractNo;

    private String amount;  // 执行金额
    private String grossMargin;  // 毛利率
    private String paymentTerm; // 付款条件
    private String executionBasis; // 合同执行依据

    private String attachment; // 文档附件
    private String deliveryAddress; // 交货地址
    private String deliveryPerson; // 交货联系人
    private String deliveryPhone; // 交货联系人电话

    private List<ProjectExecutionItem> executionItemList = Lists.newArrayList();		// 子表列表


    public ProjectExecution() {
        super();
    }

    public ProjectExecution(String id){
        super(id);
    }


    public List<ProjectExecutionItem> getExecutionItemList() {
        return executionItemList;
    }

    public void setExecutionItemList(List<ProjectExecutionItem> executionItemList) {
        this.executionItemList = executionItemList;
    }

    public String getExecutionContractNo() {
        return executionContractNo;
    }

    public void setExecutionContractNo(String executionContractNo) {
        this.executionContractNo = executionContractNo;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getGrossMargin() {
        return grossMargin;
    }

    public void setGrossMargin(String grossMargin) {
        this.grossMargin = grossMargin;
    }

    public String getPaymentTerm() {
        return paymentTerm;
    }

    public void setPaymentTerm(String paymentTerm) {
        this.paymentTerm = paymentTerm;
    }

    public String getExecutionBasis() {
        return executionBasis;
    }

    public void setExecutionBasis(String executionBasis) {
        this.executionBasis = executionBasis;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryPerson() {
        return deliveryPerson;
    }

    public void setDeliveryPerson(String deliveryPerson) {
        this.deliveryPerson = deliveryPerson;
    }

    public String getDeliveryPhone() {
        return deliveryPhone;
    }

    public void setDeliveryPhone(String deliveryPhone) {
        this.deliveryPhone = deliveryPhone;
    }

    public ProjectContractItem getContractItem() {
        return contractItem;
    }

    public void setContractItem(ProjectContractItem contractItem) {
        this.contractItem = contractItem;
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
//
//	@Length(min=0, max=100, message="流程实例ID长度必须介于 0 和 100 之间")
//	public String getProcInsId() {
//		return procInsId;
//	}
//
//	public void setProcInsId(String procInsId) {
//		this.procInsId = procInsId;
//	}
	
//	@Length(min=0, max=100, message="流程审批状态长度必须介于 0 和 100 之间")
//	public String getProcStatus() {
//		return procStatus;
//	}
//
//	public void setProcStatus(String procStatus) {
//		this.procStatus = procStatus;
//	}
	

//	@XmlTransient
//	public List<String> getCategoryList() {
//		if (category == null){
//			return Lists.newArrayList();
//		}else{
//			return Lists.newArrayList(StringUtils.split(category, ","));
//		}
//	}

	
//	public void setCategoryList(List<String> categoryList) {
//		if (categoryList == null){
//			this.category = "";
//		}else{
//			this.category = ","+StringUtils.join(categoryList, ",") + ",";
//		}
//	}

    // jsp前端checkbox使用，前端checkbox使用数组，后端使用字符串，使用逗号分隔
    // 所以前端使用executionBasisList字段，保存数据库时使用executionBasis字段
	@XmlTransient
	public List<String> getExecutionBasisList() {
		if (executionBasis == null){
			return Lists.newArrayList();
		}else{
			return Lists.newArrayList(StringUtils.split(executionBasis, ","));
		}
	}
	
	public void setExecutionBasisList(List<String> executionBasisList) {
		if (executionBasisList == null){
			this.executionBasis = "";
		}else{
			this.executionBasis = ","+StringUtils.join(executionBasisList, ",") + ",";
		}
	}
	
//	@XmlTransient
//	public String getCategory4Export(){
//		if (category == null){
//			return "";
//		}else{
//			return DictUtils.getDictLabels(category, "tender_category", "");
//		}
//	}

    // 导出excel表格时使用
	@XmlTransient
	public String getExecutionBasis4Export(){
		if (executionBasis == null){
			return "";
		}else{
			return DictUtils.getDictLabels(executionBasis, "jic_execution_basis", "");
		}
	}
}