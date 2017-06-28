/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.finish;

import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.service.ActTaskService;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
import com.thinkgem.jeesite.modules.project.dao.finish.ProjectFinishApprovalDao;
import com.thinkgem.jeesite.modules.project.entity.finish.ProjectFinishApproval;
import com.thinkgem.jeesite.modules.project.utils.MyDictUtils;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 结项Service
 * @author jicdata
 * @version 2016-03-11
 */
@Service
@Transactional(readOnly = true)
public class ProjectFinishApprovalService extends CrudService<ProjectFinishApprovalDao, ProjectFinishApproval> {
	
	@Autowired
	ActTaskService actTaskService;

	@Autowired
	private ProjectApplyExternalService projectApplyExternalService;

	@Override
	public ProjectFinishApproval get(String id) {
		return super.get(id);
	}
	
	@Override
	public List<ProjectFinishApproval> findList(ProjectFinishApproval projectFinishApproval) {
		return super.findList(projectFinishApproval);
	}
	
	@Override
	public Page<ProjectFinishApproval> findPage(Page<ProjectFinishApproval> page, ProjectFinishApproval projectFinishApproval) {
		return super.findPage(page, projectFinishApproval);
	}

	@Transactional(readOnly = false)
	public void onlySave(ProjectFinishApproval projectFinishApproval) {

		boolean isNew ;
		if (StringUtils.isBlank(projectFinishApproval.getId())){
			isNew = true;
		} else {
			isNew = false;
		}

		String processStatus = DictUtils.getDictValue("审批结束", "AuditStatus", "0");
		projectFinishApproval.setProcessStatus(processStatus);
		super.save(projectFinishApproval);

		if (isNew) {
			String stageValue = DictUtils.getDictValue("结项完成", "jic_pro_main_stage", "0");
			projectApplyExternalService.stageTo(projectFinishApproval.getApply().getId(), stageValue);
		} else {

		}
	}
	
	@Override
	@Transactional(readOnly = false)
	public void save(ProjectFinishApproval projectFinishApproval) {
		// 申请发起
		if (StringUtils.isBlank(projectFinishApproval.getId())){
			String processStatus = DictUtils.getDictValue("审批中", "AuditStatus", "0");
			projectFinishApproval.setProcessStatus(processStatus);
			super.save(projectFinishApproval);
						
			// 启动流程
			String key = projectFinishApproval.getClass().getSimpleName();
			// 设置流程变量
			Map<String, Object> varMap = new HashMap<String, Object>();
			varMap.put("apply", UserUtils.getUser().getLoginName());
			varMap.put("classType", key);
			varMap.put("objId", projectFinishApproval.getId());
			varMap.put("prjId", projectFinishApproval.getApply().getId());

			varMap.put(ActUtils.VAR_TITLE, projectFinishApproval.getApply().getProjectName());
			
			actTaskService.startProcessEatFirstTask(
					ActUtils.PD_PROJECTFINISHAPPROVAL[0], 
					ActUtils.PD_PROJECTFINISHAPPROVAL[1], 
					projectFinishApproval.getId(), 
					projectFinishApproval.getApply().getProjectName(),
					varMap
			);
			
		} else {  // 重新编辑申请
			if (projectFinishApproval.getAct().getFlagBoolean()) {
				projectFinishApproval.preUpdate();
				dao.update(projectFinishApproval);
				
				projectFinishApproval.getAct().setComment(("yes".equals(projectFinishApproval.getAct().getFlag())?"[重申] ":"[销毁] ")+projectFinishApproval.getAct().getComment());
				
				// 完成流程任务
				Map<String, Object> vars = Maps.newHashMap();
				vars.put(ActUtils.VAR_PASS, projectFinishApproval.getAct().getFlagNumber());
				vars.put(ActUtils.VAR_TITLE, projectFinishApproval.getApply().getProjectName());
				actTaskService.complateByAct(projectFinishApproval.getAct(), vars);		
			} else {
				dao.delete(projectFinishApproval);
				actTaskService.endProcess(projectFinishApproval.getAct());	
			}
		}
	}
	
	@Override
	@Transactional(readOnly = false)
	public void delete(ProjectFinishApproval projectFinishApproval) {
		super.delete(projectFinishApproval);
	}
	
//	@Transactional(readOnly = false)
//	public void auditing(String id) {
//		ProjectFinishApproval projectFinishApproval = get(id);
//		projectFinishApproval.setProcessStatus(DictUtils.getDictValue("审批结束", "AuditStatus", "0"));
//		dao.update(projectFinishApproval);
//	}
	
	/**
	 * 维护自己的流程状态	
	 * @param id
	 * @param audit
	 */
	@Transactional(readOnly = false)
	public void auditTo(String id, String audit) {
		ProjectFinishApproval projectFinishApproval = get(id);
		if (projectFinishApproval == null) {
			return;
		}
		projectFinishApproval.setProcessStatus(audit);
		dao.update(projectFinishApproval);
	}
	
	@Transactional(readOnly = false)
	public void auditSave(ProjectFinishApproval projectFinishApproval) {
		// 设置意见
		projectFinishApproval.getAct().setComment((projectFinishApproval.getAct().getFlagBoolean() ? 
				"[同意] ":"[驳回] ") + projectFinishApproval.getAct().getComment());
		Map<String, Object> vars = Maps.newHashMap();		
		
		// 对不同环节的业务逻辑进行操作
		String taskDefKey = projectFinishApproval.getAct().getTaskDefKey();

		if (UserTaskType.UT_PROJECT_MANAGER.equals(taskDefKey)){
			if ( "03".equals(projectFinishApproval.getApply().getCategory()) ) {
				vars.put("type", "2");
			} else {
				vars.put("type", "1");
			}
			
			// 项目类型
			vars.put(ActUtils.VAR_PRJ_TYPE, projectFinishApproval.getApply().getCategory());
			
			
			// 项目金额大于2000W，需要总经理审批
			boolean isBossAudit = MyDictUtils.isBossAudit(projectFinishApproval.getApply().getEstimatedContractAmount(), projectFinishApproval.getApply().getEstimatedGrossProfitMargin());
			if (isBossAudit) {  // 需要总经理审批
				vars.put(ActUtils.VAR_BOSS_AUDIT, "1");
			} else {
				vars.put(ActUtils.VAR_BOSS_AUDIT, "0");
			}
		} else if ("".equals(taskDefKey)) {
			
		}
				
		// 提交流程任务
		vars.put(ActUtils.VAR_PASS, projectFinishApproval.getAct().getFlagNumber());
		vars.put(ActUtils.VAR_TITLE, projectFinishApproval.getRemarks());
		actTaskService.complateByAct(projectFinishApproval.getAct(), vars);	
	}
	
}