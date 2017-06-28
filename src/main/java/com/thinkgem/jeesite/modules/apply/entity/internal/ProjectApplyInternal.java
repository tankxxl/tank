/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.apply.entity.internal;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.hibernate.validator.constraints.Length;

/**
 * 内部立项申请Entity
 * @author jicdata
 * @version 2016-02-23
 */
public class ProjectApplyInternal extends DataEntity<ProjectApplyInternal> {
	
	private static final long serialVersionUID = 1L;
	private String projectCode;		// 项目编号
	private String projectName;		// 项目名称
	private User organiger;		// 立项发起人
	private String description;		// 项目描述
	private String descriptionProjectSchedule;		// 项目进度描述
	private String input;		// 项目投入
	private String output;		// 项目产出
	private String riskAnalysis;		// 项目风险分析
	private String documentAttachmentPath;		// 文档附件
	private String processInstanceId;		// 流程实例ID
	private String processStatus;		// 流程审批状态
	
	public ProjectApplyInternal() {
		super();
	}

	public ProjectApplyInternal(String id){
		super(id);
	}

	/**
	* 插入之前执行方法，需要手动调用(设置立项申请人为第一次填报的用户)
	* 在ProjectApplyInternalService.java中save方法调用了
	*/
	public void preInsert4ProInteralApply() {

		if (this.getIsNewRecord()){
		//将当前录入者当成立项申请人
		User user2 = UserUtils.getUser();
		this.organiger = user2;
		}
	};
	
	@Length(min=0, max=64, message="项目编号长度必须介于 0 和 64 之间")
	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	
	@Length(min=0, max=64, message="项目名称长度必须介于 0 和 64 之间")
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public User getOrganiger() {
		return organiger;
	}

	public void setOrganiger(User organiger) {
		this.organiger = organiger;
	}
	
	@Length(min=0, max=255, message="项目描述长度必须介于 0 和 255 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Length(min=0, max=255, message="项目进度描述长度必须介于 0 和 255 之间")
	public String getDescriptionProjectSchedule() {
		return descriptionProjectSchedule;
	}

	public void setDescriptionProjectSchedule(String descriptionProjectSchedule) {
		this.descriptionProjectSchedule = descriptionProjectSchedule;
	}
	
	@Length(min=0, max=255, message="项目投入长度必须介于 0 和 255 之间")
	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}
	
	@Length(min=0, max=255, message="项目产出长度必须介于 0 和 255 之间")
	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}
	
	@Length(min=0, max=255, message="项目风险分析长度必须介于 0 和 255 之间")
	public String getRiskAnalysis() {
		return riskAnalysis;
	}

	public void setRiskAnalysis(String riskAnalysis) {
		this.riskAnalysis = riskAnalysis;
	}
	
	@Length(min=0, max=2000, message="文档附件长度必须介于 0 和 2000 之间")
	public String getDocumentAttachmentPath() {
		return documentAttachmentPath;
	}

	public void setDocumentAttachmentPath(String documentAttachmentPath) {
		this.documentAttachmentPath = documentAttachmentPath;
	}
	
	@Length(min=0, max=100, message="流程实例ID长度必须介于 0 和 100 之间")
	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
	@Length(min=0, max=100, message="流程审批状态长度必须介于 0 和 100 之间")
	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}
	
}