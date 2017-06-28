/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.customer.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

/**
 * 客户联系人信息Entity
 * @author jicdata
 * @version 2016-02-21
 */
public class CustomerContact extends DataEntity<CustomerContact> {
	
	private static final long serialVersionUID = 1L;
	private String contactName;		// 联系人名称
	private String sex;		// 性别
	private String phone;		// 电话
	private String mobilePhone;		// 手机
	private Customer customer;		// 客户编码 父类
	private String position; //职位
	
	public CustomerContact() {
		super();
	}

	public CustomerContact(String id){
		super(id);
	}

	public CustomerContact(Customer customer){
		this.customer = customer;
	}

	@Length(min=0, max=64, message="职位名称长度必须介于 0 和 64 之间")
	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	@Length(min=0, max=64, message="联系人名称长度必须介于 0 和 64 之间")
	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	
	@Length(min=0, max=1, message="性别长度必须介于 0 和 1 之间")
	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}
	
	@Length(min=0, max=255, message="电话长度必须介于 0 和 255 之间")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Length(min=0, max=255, message="手机长度必须介于 0 和 255 之间")
	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	
	@Length(min=1, max=255, message="客户编码长度必须介于 1 和 255 之间")
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
}