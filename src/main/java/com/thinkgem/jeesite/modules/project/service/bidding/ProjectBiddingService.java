/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.bidding;

import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.service.ActTaskService;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
import com.thinkgem.jeesite.modules.mail.service.MailService;
import com.thinkgem.jeesite.modules.project.dao.bidding.ProjectBiddingDao;
import com.thinkgem.jeesite.modules.project.entity.bidding.ProjectBidding;
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
 * 项目投标Service
 * @author jicdata
 * @version 2016-03-08
 */
@Service
@Transactional(readOnly = true)
public class ProjectBiddingService extends CrudService<ProjectBiddingDao, ProjectBidding> {

	@Autowired
	ActTaskService actTaskService;
	
	@Autowired
	MailService mailService;
	
	@Autowired
	private ProjectApplyExternalService projectApplyExternalService;
	
	@Override
	public ProjectBidding get(String id) {
		return super.get(id);
	}
	
	@Override
	public List<ProjectBidding> findList(ProjectBidding projectBidding) {
		return super.findList(projectBidding);
	}
	
	@Override
	public Page<ProjectBidding> findPage(Page<ProjectBidding> page, ProjectBidding projectBidding) {
		return super.findPage(page, projectBidding);
	}

	/**
	 * 保存并结束流程
	 * @param projectBidding
	 */
	@Transactional(readOnly = false)
	public void saveFinishProcess(ProjectBidding projectBidding) {

		boolean isNew ;
		if (StringUtils.isBlank(projectBidding.getId())){
			isNew = true;
		} else {
			isNew = false;
		}

		String processStatus = DictUtils.getDictValue("审批结束", "AuditStatus", "0");
		projectBidding.setProcessStatus(processStatus);
		super.save(projectBidding);

		if (isNew) {
			String stageValue = DictUtils.getDictValue("投标完成", "jic_pro_main_stage", "0");
			projectApplyExternalService.stageTo(projectBidding.getApply().getId(), stageValue);
		} else {

		}
	}

	/**
	 * 只保存表单数据
	 * @param projectBidding
	 */
	@Transactional(readOnly = false)
	public void saveOnly(ProjectBidding projectBidding) {
		super.save(projectBidding);

	}

	/**
	 * 保存表单数据，并启动流程
	 * @param projectBidding
	 */
	@Override
	@Transactional(readOnly = false)
	public void save(ProjectBidding projectBidding) {
		
		// 申请发起
		if (StringUtils.isBlank(projectBidding.getId())){
			String processStatus = DictUtils.getDictValue("审批中", "AuditStatus", "0");
			projectBidding.setProcessStatus(processStatus);
			super.save(projectBidding);
			
			// 在立项审批表中也要保存一下是否有外包
			ProjectApplyExternal external = projectApplyExternalService.get(projectBidding.getApply().getId());
			external.setOutsourcing(projectBidding.getOutsourcing());
			projectApplyExternalService.saveOnly(external);
			
						
			// 启动流程
			String key = projectBidding.getClass().getSimpleName();
			// 设置流程变量
			Map<String, Object> varMap = new HashMap<String, Object>();
			varMap.put("apply", UserUtils.getUser().getLoginName());
			varMap.put("classType", key);
			varMap.put("objId", projectBidding.getId());
			varMap.put("prjId", projectBidding.getApply().getId());

			varMap.put(ActUtils.VAR_TITLE, projectBidding.getApply().getProjectName());
			
			actTaskService.startProcessEatFirstTask(
					ActUtils.PD_PROJECTBIDDING[0], 
					ActUtils.PD_PROJECTBIDDING[1], 
					projectBidding.getId(), 
					projectBidding.getContent(),
					varMap
			);
			
		} else {  // 1、重新编辑申请；2、销毁审请
			
			if (projectBidding.getAct().getFlagBoolean()) {
				projectBidding.preUpdate();
				dao.update(projectBidding);
				
				projectBidding.getAct().setComment(("yes".equals(projectBidding.getAct().getFlag())?"[重申] ":"[销毁] ")+projectBidding.getAct().getComment());
				
				// 完成流程任务
				Map<String, Object> vars = Maps.newHashMap();
				vars.put(ActUtils.VAR_PASS, projectBidding.getAct().getFlagNumber());
				vars.put(ActUtils.VAR_TITLE, projectBidding.getApply().getProjectName());
				actTaskService.complateByAct(projectBidding.getAct(), vars);	
			} else {
				dao.delete(projectBidding);
				actTaskService.endProcess(projectBidding.getAct());	
			}
		}
	}
	
	@Override
	@Transactional(readOnly = false)
	public void delete(ProjectBidding projectBidding) {
		super.delete(projectBidding);
	}

//	@Transactional(readOnly = false)
//	public void auditing(String id) {
//		ProjectBidding projectBidding = get(id);
//		projectBidding.setProcessStatus(DictUtils.getDictValue("审批结束", "AuditStatus", "0"));
//		dao.update(projectBidding);
//	}

	public ProjectBidding findByProcInsId(String procInsId) {
		ProjectBidding bidding = null;
		if (StringUtils.isEmpty(procInsId)) {
			bidding = new ProjectBidding();
		} else {
			bidding = dao.findByProcInsId(procInsId);
		}
		if (bidding == null) {
			bidding = new ProjectBidding();
		}
		return bidding;
	}
	
	/**
	 * 维护自己的流程状态	
	 * @param id
	 * @param audit
	 */
	@Transactional(readOnly = false)
	public void auditTo(String id, String audit) {
		ProjectBidding projectBidding = get(id);
		if (projectBidding == null) {
			return;
		}
		projectBidding.setProcessStatus(audit);
		dao.update(projectBidding);
	}
	
	@Transactional(readOnly = false)
	public void auditSave(ProjectBidding projectBidding) {
		// 设置意见
		projectBidding.getAct().setComment((projectBidding.getAct().getFlagBoolean() ? 
				"[同意] ":"[驳回] ") + projectBidding.getAct().getComment());
		Map<String, Object> vars = Maps.newHashMap();		
		
		// 对不同环节的业务逻辑进行操作
		String taskDefKey = projectBidding.getAct().getTaskDefKey();

		if (UserTaskType.UT_PRE_SALES_ENGINEER.equals(taskDefKey)){
			
			if ("03".equals(projectBidding.getApply().getCategory()) ) {
				vars.put("type", "2");
			} else {
				vars.put("type", "1");
			}
			
			// 项目类型
			vars.put(ActUtils.VAR_PRJ_TYPE, projectBidding.getApply().getCategory());
//			projectBidding.getProfitMargin()
			// 使用各自审批表中的毛利率
			boolean isBossAudit = MyDictUtils.isBossAudit(projectBidding.getApply().getEstimatedContractAmount(), projectBidding.getProfitMargin());
			if (isBossAudit) { // 需要总经理审批
				vars.put(ActUtils.VAR_BOSS_AUDIT, "1");
			} else {
				vars.put(ActUtils.VAR_BOSS_AUDIT, "0");
			}
			// 有外包的项目需要人力审批
//			if ("1".equals(projectBidding.getOutsourcing())) {
//				vars.put(ActUtils.VAR_HR_AUDIT, "1");
//			} else {
//				vars.put(ActUtils.VAR_HR_AUDIT, "0");
//			}

			// 20160628下午,张雪口头提需求, 05类项目根据是否有外包选项,来决定流程是否走到人力
			// 其它所有类型项目,不管是否选择有外包,流程上必须过人力.
			// 涉及的流程包括:投标和合同
			if ("05".equals(projectBidding.getApply().getCategory()) ) {
				vars.put(ActUtils.VAR_HR_AUDIT, "1");
			} else {
				vars.put(ActUtils.VAR_HR_AUDIT, "0");
			}

			
		} else if ("".equals(taskDefKey)) {
			
		}
				
		// 提交流程任务
		vars.put(ActUtils.VAR_PASS, projectBidding.getAct().getFlagNumber());
		vars.put(ActUtils.VAR_TITLE, projectBidding.getApply().getProjectName());
		actTaskService.complateByAct(projectBidding.getAct(), vars);	
	}


	/**
	 * 流程中修改项目主状态、及各阶段小状态
	 *
	 */

	@Transactional(readOnly = false)
	public void auditStart(String id) {
		ProjectBidding projectBidding = get(id);
		if (projectBidding == null) {
			return;
		}

		String audit = DictUtils.getDictValue("审批中", "AuditStatus", "0");
		projectBidding.setProcessStatus(audit);
		dao.update(projectBidding);
	}

	@Transactional(readOnly = false)
	public void auditFinish(String id) {
		ProjectBidding projectBidding = get(id);
		if (projectBidding == null) {
			return;
		}

		String audit = DictUtils.getDictValue("审批结束", "AuditStatus", "0");
		projectBidding.setProcessStatus(audit);
		dao.update(projectBidding);
	}

}