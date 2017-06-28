/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.crm.client.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.modules.crm.visit.entity.ClientVisit;
import org.hibernate.validator.constraints.Length;

/**
 * 拜访客户花费Entity
 * @author jicdata
 * @version 2016-02-21
 */
public class ClientCost extends DataEntity<ClientCost> {

	private static final long serialVersionUID = 1L;
	private String contactName;		// 联系人名称
	private String sex;		// 性别
	private String phone;		// 电话
	private String mobilePhone;		// 手机
	private String position; //职位

	private Client client ; // 客户
	private ClientContact contact; // 联系人
	private ClientVisit visit; // 拜访

	private String type; // 费用类别
	private String amount; // 金额


	public ClientCost() {
		super();
	}

	public ClientCost(String id){
		super(id);
	}

	public ClientCost(Client client){
		this.client = client;
	}

	public ClientCost(ClientVisit visit){
		this.visit = visit;
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
	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}


	public ClientContact getContact() {
		return contact;
	}

	public void setContact(ClientContact contact) {
		this.contact = contact;
	}

	public ClientVisit getVisit() {
		return visit;
	}

	public void setVisit(ClientVisit visit) {
		this.visit = visit;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
}