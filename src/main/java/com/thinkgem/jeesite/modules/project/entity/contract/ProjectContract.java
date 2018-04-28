/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.entity.contract;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.persistence.ActEntity;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.sys.entity.User;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.List;


/**
 * 合同Entity
 * @author jicdata
 * @version 2016-03-09
 */
public class ProjectContract extends ActEntity<ProjectContract> {
	
	private static final long serialVersionUID = 1L;
	private ProjectApplyExternal apply;
	private User projectManager;//项目经理
	private String stage;		// 项目步骤

	private String attachment; // 文档附件

	@Valid
	@NotEmpty(message = "至少要填写一个合同项。${validatedValue}")
	private List<ProjectContractItem> projectContractItemList = Lists.newArrayList();		// 子表列表
	
	public ProjectContract() {
		super();
		this.apply = new ProjectApplyExternal();
	}

	public ProjectContract(String id){
		super(id);
	}

	public ProjectApplyExternal getApply() {
		return apply;
	}

	public void setApply(ProjectApplyExternal apply) {
		this.apply = apply;
	}

	public User getProjectManager() {
		return projectManager;
	}

	public void setProjectManager(User projectManager) {
		this.projectManager = projectManager;
	}

	@Length(min=1, max=64, message="项目步骤长度必须介于 1 和 64 之间")
	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	
	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}
	
	public List<ProjectContractItem> getProjectContractItemList() {
		return projectContractItemList;
	}

	public void setProjectContractItemList(List<ProjectContractItem> projectContractItemList) {
		this.projectContractItemList = projectContractItemList;
	}
}