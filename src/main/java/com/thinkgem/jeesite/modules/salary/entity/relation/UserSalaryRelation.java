/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.salary.entity.relation;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.modules.salary.entity.SalaryLevel;
import com.thinkgem.jeesite.modules.sys.entity.User;
import org.hibernate.validator.constraints.Length;

/**
 * 人员薪资管理Entity
 * @author jicdata
 * @version 2016-03-12
 */
public class UserSalaryRelation extends DataEntity<UserSalaryRelation> {
	
	private static final long serialVersionUID = 1L;
	private User user;		// 用户编号
	private SalaryLevel salary;		// 工资编号
	private String procInsId;		// 流程实例ID
	private String processStatus;		// 流程审批状态
	
	public UserSalaryRelation() {
		super();
	}

	public UserSalaryRelation(String id){
		super(id);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
	
	public SalaryLevel getSalary() {
		return salary;
	}

	public void setSalary(SalaryLevel salary) {
		this.salary = salary;
	}

	@Length(min=0, max=100, message="流程实例ID长度必须介于 0 和 100 之间")
	public String getProcInsId() {
		return procInsId;
	}

	public void setProcInsId(String procInsId) {
		this.procInsId = procInsId;
	}
	
	@Length(min=0, max=100, message="流程审批状态长度必须介于 0 和 100 之间")
	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}
	
}