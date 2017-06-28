/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.crm.client.entity;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.common.utils.excel.annotation.ExcelField;
import com.thinkgem.jeesite.common.utils.excel.fieldtype.PrincipalType;
import com.thinkgem.jeesite.modules.crm.visit.entity.ClientVisit;
import com.thinkgem.jeesite.modules.sys.entity.User;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * 客户联系人信息Entity
 * @author jicdata
 * @version 2016-02-21
 */
public class Client extends DataEntity<Client> {
	
	private static final long serialVersionUID = 1L;
	private String customerName;		// 客户名称
	private String oldName; 		// 用于接收修改之前的客户名称
	
	private String phone;		// 电话
	private String fax;		// 传真
	private String industry;		// 行业
	private String province;		// 省份
	private String customerCategory;		// 客户类别
	private String address;		// 客户地点
	private String website;		// 网址
	private User principal;		// 负责人

	private List<ClientContact> customerContactList = Lists.newArrayList();		// 子表列表
	private List<ClientVisit> visitList = Lists.newArrayList();		// 子表列表
	private List<ClientAccount> accountList = Lists.newArrayList();		// 子表列表

	private String userId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public Client() {
		super();
	}

	public Client(String id){
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
	
	@Length(min=0, max=64, message="传真长度必须介于 0 和 64 之间")
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
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
	
	@Length(min=0, max=255, message="网址长度必须介于 0 和 255 之间")
	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}
	
	public List<ClientContact> getCustomerContactList() {
		return customerContactList;
	}

	public void setCustomerContactList(List<ClientContact> customerContactList) {
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
	
	
}