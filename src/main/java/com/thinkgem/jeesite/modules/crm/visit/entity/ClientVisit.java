/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.crm.visit.entity;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.modules.crm.client.entity.Client;
import com.thinkgem.jeesite.modules.crm.client.entity.ClientContact;
import com.thinkgem.jeesite.modules.crm.client.entity.ClientCost;
import org.hibernate.validator.constraints.Length;

import java.util.Date;
import java.util.List;

/**
 * 客户拜访记录Entity
 * @author jicdata
 * @version 2016-02-21
 */
public class ClientVisit extends DataEntity<ClientVisit> {

	private static final long serialVersionUID = 1L;
	private String contactName;		// 联系人名称
	private String sex;		// 性别
	private String phone;		// 电话
	private String mobilePhone;		// 手机

	private String position; //职位

	private ClientContact contact; // 拜访联系人
	private Client client;		// 拜访客户 父类
	private String title ;
	private String content;
	private Date visitDate;

	private List<ClientCost> costList = Lists.newArrayList();		// 子表列表

	public ClientVisit() {
		super();
	}

	public ClientVisit(String id){
		super(id);
	}

	public ClientVisit(Client client){
		this.client = client;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(Date visitDate) {
		this.visitDate = visitDate;
	}

	public List<ClientCost> getCostList() {
		return costList;
	}

	public void setCostList(List<ClientCost> costList) {
		this.costList = costList;
	}
}