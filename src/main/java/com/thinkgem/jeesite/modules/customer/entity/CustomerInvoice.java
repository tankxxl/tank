/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.customer.entity;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.common.utils.excel.annotation.ExcelField;
import com.thinkgem.jeesite.common.utils.excel.fieldtype.PrincipalType;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.entity.User;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * 开票客户信息Entity
 * @author jicdata
 * @version 2016-02-21
 */
public class CustomerInvoice extends DataEntity<CustomerInvoice> {

	private static final long serialVersionUID = 1L;
	private String customerName;		// 客户名称
	private String oldName; 		// 用于接收修改之前的客户名称

	private String phone;		// 电话
	private String mailPerson;		// 邮寄联系人
	private String mailAddress;		// 邮寄地址

	private String industry;		// 行业
	private String province;		// 省份
	private String customerCategory;		// 客户类别
	private String address;		// 客户地点

	private User principal;		// 负责人

	private String taxNo;
	private String bankName;
	private String bankNo;

	private Area area;		// 归属区域

	/**
	 * 1、view：编辑、查看，使用layer.open dialog查看
	 */
	private String func; // 前后端传递参数使用，不持久化

	private List<CustomerContact> customerContactList = Lists.newArrayList();		// 子表列表

	public CustomerInvoice() {
		super();
	}

	public CustomerInvoice(String id){
		super(id);
	}

	@ExcelField(title="客户名称", type=0, align=2, sort=1)
	@Length(min=1, max=64, message="客户名称长度必须介于 1 和 64 之间")
	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	@Length(min=0, max=20, message="电话长度必须介于 0 和 20 之间")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Length(min=1, max=255, message="行业长度必须介于 1 和 255 之间")
	@ExcelField(title="行业", type=0, align=2, sort=3, dictType="customer_industry")
	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}
	
	@Length(min=0, max=255, message="省份长度必须介于 0 和 255 之间")
	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}
	
	@Length(min=0, max=64, message="客户类型长度必须介于 0 和 64 之间")
	@ExcelField(title="客户类型", type=0, align=2, sort=2, dictType="customer_category")
	public String getCustomerCategory() {
		return customerCategory;
	}

	public void setCustomerCategory(String customerCategory) {
		this.customerCategory = customerCategory;
	}

	@Length(min=0, max=255, message="客户地点长度必须介于 0 和 255 之间")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public List<CustomerContact> getCustomerContactList() {
		return customerContactList;
	}

	public void setCustomerContactList(List<CustomerContact> customerContactList) {
		this.customerContactList = customerContactList;
	}
	@ExcelField(title="客户负责人", type=0, align=2, sort=4, fieldType=PrincipalType.class)
	public User getPrincipal() {
		return principal;
	}

	public void setPrincipal(User principal) {
		this.principal = principal;
	}
	
	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
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

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public String getMailPerson() {
		return mailPerson;
	}

	public void setMailPerson(String mailPerson) {
		this.mailPerson = mailPerson;
	}

	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	public String getFunc() {
		return func;
	}

	public void setFunc(String func) {
		this.func = func;
	}
}