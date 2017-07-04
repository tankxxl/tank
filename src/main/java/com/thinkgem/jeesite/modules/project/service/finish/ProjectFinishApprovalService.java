/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.finish;

import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.modules.act.service.ActTaskService;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
import com.thinkgem.jeesite.modules.project.dao.finish.ProjectFinishApprovalDao;
import com.thinkgem.jeesite.modules.project.entity.finish.ProjectFinishApproval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 结项Service
 * @author jicdata
 * @version 2016-03-11
 */
@Service
@Transactional(readOnly = true)
public class ProjectFinishApprovalService extends JicActService<ProjectFinishApprovalDao, ProjectFinishApproval> {
	
	@Autowired
	ActTaskService actTaskService;

	private boolean isNewRecord;

	@Autowired
	private ProjectApplyExternalService projectApplyExternalService;

	@Override
	public ProjectFinishApproval get(String id) {
		return super.get(id);
	}

	/**
	 * 保存并结束流程
	 * @param projectFinishApproval
	 */
	@Transactional(readOnly = false)
	public void saveFinishProcess(ProjectFinishApproval projectFinishApproval) {
		// 保存
		save(projectFinishApproval);
		// 开启流程
		String procInsId = launchWorkflow(projectFinishApproval);
		// 结束流程
		endProcess(procInsId);
	}

	/**
	 * 保存表单数据，并启动流程
	 * @param projectFinishApproval
	 */
	@Transactional(readOnly = false)
	public void saveLaunch(ProjectFinishApproval projectFinishApproval) {
		save(projectFinishApproval);
		launchWorkflow(projectFinishApproval);
	}
	
	@Override
	@Transactional(readOnly = false)
	public void save(ProjectFinishApproval projectFinishApproval) {
		isNewRecord = projectFinishApproval.getIsNewRecord();
		super.save(projectFinishApproval);
	}
	
	@Override
	@Transactional(readOnly = false)
	public void delete(ProjectFinishApproval projectFinishApproval) {
		super.delete(projectFinishApproval);
	}

	/**
	 * 审批人审批入口
	 * @param projectFinishApproval
	 */
	@Transactional(readOnly = false)
	public void auditSave(ProjectFinishApproval projectFinishApproval) {
		// 设置意见
		projectFinishApproval.getAct().setComment((projectFinishApproval.getAct().getFlagBoolean() ?
				"[同意] ":"[驳回] ") + projectFinishApproval.getAct().getComment());
		Map<String, Object> vars = Maps.newHashMap();
		vars.put(ActUtils.VAR_PASS, projectFinishApproval.getAct().getFlagNumber());

		// 对不同环节的业务逻辑进行操作
		String taskDefKey = projectFinishApproval.getAct().getTaskDefKey();

		if (UserTaskType.UT_BUSINESS_LEADER.equals(taskDefKey)){

			if ("03".equals(projectFinishApproval.getApply().getCategory()) ) {
				vars.put("type", "2");
			} else {
				vars.put("type", "1");
			}
			// 项目类型
			vars.put(ActUtils.VAR_PRJ_TYPE, projectFinishApproval.getApply().getCategory());
			// 都需要总经理审批
			vars.put(ActUtils.VAR_BOSS_AUDIT, "1");

		} else if ("".equals(taskDefKey)) {

		}
		// 提交流程任务
		saveAuditBase(projectFinishApproval, vars);
	}

	private String launchWorkflow(ProjectFinishApproval projectFinishApproval) {
		// 设置流程变量
		Map<String, Object> varMap = new HashMap<String, Object>();
		varMap.put(ActUtils.VAR_PRJ_ID, projectFinishApproval.getApply().getId());

		varMap.put(ActUtils.VAR_PROC_NAME, ActUtils.PROC_NAME_FINISH);
		varMap.put(ActUtils.VAR_PRJ_TYPE, projectFinishApproval.getApply().getCategory());
		varMap.put("_ACTIVITI_SKIP_EXPRESSION_ENABLED", true);

		String title = projectFinishApproval.getApply().getProjectName();

		return launchWorkflowBase(projectFinishApproval, isNewRecord, title, varMap);
	}
	
}