/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.finish;

import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.project.dao.finish.ProjectFinishApprovalDao;
import com.thinkgem.jeesite.modules.project.entity.finish.ProjectFinishApproval;
import com.thinkgem.jeesite.modules.project.utils.MyDictUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 结项Service
 * @author jicdata
 * @version 2016-03-11
 */
@Service
@Transactional(readOnly = true)
public class ProjectFinishApprovalService extends JicActService<ProjectFinishApprovalDao, ProjectFinishApproval> {
	
	// @Autowired
	// ActTaskService actTaskService;

	// @Autowired
	// private ProjectApplyExternalService projectApplyExternalService;

	/**
	 * 保存并结束流程
	 // * @param projectFinishApproval
	 */
	// @Transactional(readOnly = false)
	// public void saveFinishProcess(ProjectFinishApproval projectFinishApproval) {
	// 	// 开启流程
	// 	String procInsId = saveLaunch(projectFinishApproval);
	// 	// 结束流程
	// 	endProcess(procInsId);
	// }


	@Override
	public void setupVariable(ProjectFinishApproval finish, Map<String, Object> vars) {
		vars.put(ActUtils.VAR_PRJ_ID, finish.getApply().getId());

		vars.put(ActUtils.VAR_PRJ_TYPE, finish.getApply().getCategory());

		vars.put(ActUtils.VAR_TITLE, finish.getApply().getProjectName());
		if ("03".equals(finish.getApply().getCategory()) ) {
			// 分支上使用，没在节点上使用
			vars.put(ActUtils.VAR_TYPE, "2");
		} else {
			vars.put(ActUtils.VAR_TYPE, "1");
		}

		boolean isBossAudit = MyDictUtils.isBossAudit(finish.getApply().getEstimatedContractAmount(),
				finish.getApply().getEstimatedGrossProfitMargin());
		if (isBossAudit) { // 需要总经理审批
			// 节点上使用
			vars.put(ActUtils.VAR_SKIP_BOSS, "0");
		} else {
			vars.put(ActUtils.VAR_SKIP_BOSS, "1");
		}
	}

	/**
	 * 保存表单数据，并启动流程
	 *
	 * 申请人发起流程，申请人重新发起流程入口
	 * 在form界面
	 *
	 * @param projectFinishApproval
	 */
	// @Transactional(readOnly = false)
	// public String saveLaunch(ProjectFinishApproval projectFinishApproval) {
    //
	// 	if (projectFinishApproval.getIsNewRecord()) {
	// 		// 启动流程的时候，把业务数据放到流程变量里
	// 		Map<String, Object> varMap = new HashMap<String, Object>();
	// 		varMap.put(ActUtils.VAR_PRJ_ID, projectFinishApproval.getApply().getId());
    //
	// 		varMap.put(ActUtils.VAR_PRJ_TYPE, projectFinishApproval.getApply().getCategory());
    //
	// 		varMap.put(ActUtils.VAR_TITLE, projectFinishApproval.getApply().getProjectName());
    //
	// 		if ("03".equals(projectFinishApproval.getApply().getCategory()) ) {
	// 			varMap.put(ActUtils.VAR_TYPE, "2");
	// 		} else {
	// 			varMap.put(ActUtils.VAR_TYPE, "1");
	// 		}
    //
	// 		boolean isBossAudit = MyDictUtils.isBossAudit(projectFinishApproval.getApply().getEstimatedContractAmount(),
	// 				projectFinishApproval.getApply().getEstimatedGrossProfitMargin());
	// 		if (isBossAudit) { // 需要总经理审批
	// 			varMap.put(ActUtils.VAR_SKIP_BOSS, "0");
	// 		} else {
	// 			varMap.put(ActUtils.VAR_SKIP_BOSS, "1");
	// 		}
    //
	// 		return launch(projectFinishApproval, varMap);
	// 	} else { // 把驳回到申请人(重新修改业务表单，重新发起流程、销毁流程)也当成一个特殊的审批节点
	// 		// 只要不是启动流程，其它任意节点的跳转都当成节点审批
	// 		saveAudit(projectFinishApproval);
	// 		return null;
	// 	}
	// }

//	@Transactional(readOnly = false)
//	public void auditing(String id) {
//		ProjectFinishApproval projectFinishApproval = get(id);
//		projectFinishApproval.setProcessStatus(DictUtils.getDictValue("审批结束", "AuditStatus", "0"));
//		dao.update(projectFinishApproval);
//	}

	/**
	 * 维护自己的流程状态
	 // * @param id
	 // * @param audit
	 */
	// @Transactional(readOnly = false)
	// public void auditTo(String id, String audit) {
	// 	ProjectFinishApproval projectFinishApproval = get(id);
	// 	if (projectFinishApproval == null) {
	// 		return;
	// 	}
	// 	projectFinishApproval.setProcessStatus(audit);
	// 	dao.update(projectFinishApproval);
	// }

	// @Transactional(readOnly = false)
	// public void auditSave(ProjectFinishApproval projectFinishApproval) {
	// 	// 设置意见
	// 	projectFinishApproval.getAct().setComment((projectFinishApproval.getAct().getFlagBoolean() ?
	// 			"[同意] ":"[驳回] ") + projectFinishApproval.getAct().getComment());
	// 	Map<String, Object> vars = Maps.newHashMap();
	//
	// 	// 对不同环节的业务逻辑进行操作
	// 	String taskDefKey = projectFinishApproval.getAct().getTaskDefKey();
    //
	// 	if (UserTaskType.UT_PROJECT_MANAGER.equals(taskDefKey)){
	// 		if ( "03".equals(projectFinishApproval.getApply().getCategory()) ) {
	// 			vars.put("type", "2");
	// 		} else {
	// 			vars.put("type", "1");
	// 		}
	//
	// 		// 项目类型
	// 		vars.put(ActUtils.VAR_PRJ_TYPE, projectFinishApproval.getApply().getCategory());
	//
	//
	// 		// 项目金额大于2000W，需要总经理审批
	// 		boolean isBossAudit = MyDictUtils.isBossAudit(projectFinishApproval.getApply().getEstimatedContractAmount(), projectFinishApproval.getApply().getEstimatedGrossProfitMargin());
	// 		if (isBossAudit) {  // 需要总经理审批
	// 			vars.put(ActUtils.VAR_BOSS_AUDIT, "1");
	// 		} else {
	// 			vars.put(ActUtils.VAR_BOSS_AUDIT, "0");
	// 		}
	// 	} else if ("".equals(taskDefKey)) {
	//
	// 	}
	//
	// 	// 提交流程任务
	// 	vars.put(ActUtils.VAR_PASS, projectFinishApproval.getAct().getFlagNumber());
	// 	vars.put(ActUtils.VAR_TITLE, projectFinishApproval.getRemarks());
	// 	actTaskService.complateByAct(projectFinishApproval.getAct(), vars);
	// }
	
}