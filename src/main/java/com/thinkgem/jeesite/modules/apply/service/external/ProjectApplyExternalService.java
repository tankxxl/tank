/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.apply.service.external;

import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.entity.Act;
import com.thinkgem.jeesite.modules.act.service.ActTaskService;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.apply.dao.external.ProjectApplyExternalDao;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.mail.entity.Email;
import com.thinkgem.jeesite.modules.mail.service.MailService;
import com.thinkgem.jeesite.modules.project.utils.MyDictUtils;
import com.thinkgem.jeesite.modules.sys.dao.RoleDao;
import com.thinkgem.jeesite.modules.sys.entity.Role;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 外部立项申请Service
 * @author jicdata
 * @version 2016-02-23
 */
@Service
@Transactional(readOnly = true)
public class ProjectApplyExternalService extends CrudService<ProjectApplyExternalDao, ProjectApplyExternal> {
	
	@Autowired
	MailService mailService;
	
	@Autowired
	ActTaskService actTaskService;
	
	@Autowired
	private RoleDao roleDao;
	
	
	
	@Override
	public ProjectApplyExternal get(String id) {
		return super.get(id);
	}

	/**
	 * 保存并结束流程
	 * @param projectApplyExternal
	 */
	@Transactional(readOnly = false)
	public void saveFinishProcess(ProjectApplyExternal projectApplyExternal) {
		
		projectApplyExternal.preInsert4ProInteralApply();//判断是否是插入还是修改，若是插入那么添加当前用户为销售
//		if (StringUtils.isBlank(projectApplyExternal.getAct().getFlag())) {
		// 修改小状态
		String processStatus = DictUtils.getDictValue("审批结束", "AuditStatus", "0");
		projectApplyExternal.setProcessStatus(processStatus);
//		}

		boolean isNew ;
		if (StringUtils.isBlank(projectApplyExternal.getId())){
			isNew = true;
		} else {
			isNew = false;
		}
		super.save(projectApplyExternal);

		if (isNew) {
			// 修改大状态
			String stageValue = DictUtils.getDictValue("立项完成", "jic_pro_main_stage", "0");
//			if (StringUtils.isBlank(projectApplyExternal.getAct().getFlag())) {
				stageTo(projectApplyExternal.getId(), stageValue);
//			}
		} else {

		}

	}

	/**
	 * 只保存表单数据
	 * @param projectApplyExternal
	 */
	@Transactional(readOnly = false)
	public void saveOnly(ProjectApplyExternal projectApplyExternal) {
		projectApplyExternal.preInsert4ProInteralApply();
		super.save(projectApplyExternal);

	}

	/**
	 * 审核新增或编辑
	 * @param projectApplyExternal
	 */
	@Override
	@Transactional(readOnly = false)
	public void save(ProjectApplyExternal projectApplyExternal) {
		// 申请发起
		if (StringUtils.isBlank(projectApplyExternal.getId())){
			String processStatus = DictUtils.getDictValue("审批中", "AuditStatus", "0");

			projectApplyExternal.setProcessStatus(processStatus);
			
			projectApplyExternal.preInsert4ProInteralApply();//判断是否是插入还是修改，若是插入那么添加当前用户为销售
			// TODO 申请人不生成项目编号
//			if(projectApplyExternal.getIsNewRecord()){
//				int currentCode = Integer.parseInt(projectApplyExternalDao.getCurrentCode());
//				currentCode++;
//				projectApplyExternalDao.updatePorCode(String.valueOf(currentCode));
//			}
			super.save(projectApplyExternal);
						
			// 启动流程
			String key = projectApplyExternal.getClass().getSimpleName();
			// 设置流程变量
			Map<String, Object> varMap = new HashMap<String, Object>();
			// varMap.put("apply", projectApplyExternal.getSaler().getLoginName());
			varMap.put("classType", key);
			varMap.put("objId", projectApplyExternal.getId());
			varMap.put(ActUtils.VAR_TITLE, projectApplyExternal.getProjectName());

			actTaskService.startProcessEatFirstTask(
						ActUtils.PD_PROJECTAPPLYEXTERNAL[0],
						ActUtils.PD_PROJECTAPPLYEXTERNAL[1],
						projectApplyExternal.getId(),
						projectApplyExternal.getProjectName(),
						varMap
			);

			
		} else {  // 重新编辑申请
			if (projectApplyExternal.getAct().getFlagBoolean()) {
				projectApplyExternal.preUpdate();
				dao.update(projectApplyExternal);
				
				projectApplyExternal.getAct().setComment(("yes".equals(projectApplyExternal.getAct().getFlag())?"[重申] ":"[销毁] ")+projectApplyExternal.getAct().getComment());
				
				// 完成流程任务
				Map<String, Object> vars = Maps.newHashMap();
				vars.put(ActUtils.VAR_PASS, projectApplyExternal.getAct().getFlagNumber());
				vars.put(ActUtils.VAR_TITLE, projectApplyExternal.getProjectName());
				actTaskService.complateByAct(projectApplyExternal.getAct(), vars);	
			} else {
				dao.delete(projectApplyExternal);
				actTaskService.endProcess(projectApplyExternal.getAct());
//				actTaskService.deleteTask(projectApplyExternal.getAct().getTaskId(), "申请人手动销毁");
			}
			
		}
	}
	
	/**
	 * 审核审批保存
	 * @param projectApplyExternal
	 */
	@Transactional(readOnly = false)
	public void auditSave(ProjectApplyExternal projectApplyExternal) {
		Act act = projectApplyExternal.getAct();
		// 设置意见
		act.setComment((act.getFlagBoolean() ? "[同意] " : "[驳回] ") + act.getComment() );
		Map<String, Object> vars = Maps.newHashMap();		
		
		// 对不同环节的业务逻辑进行操作
		String taskDefKey = act.getTaskDefKey();

		// 审核环节--项目专员--需要生成并保存项目编号
		if (UserTaskType.UT_SPECIALIST.equals(taskDefKey)){
			
			// 审批通过才保存项目信息、项目编号
			if (act.getFlagBoolean()) {
				String proCodeStr = projectApplyExternal.getProjectCode();//包含类别、年份、归属的信息+标识位
                if (StringUtils.isNotEmpty(proCodeStr) && proCodeStr.length() > 3) {
                	String suffix = proCodeStr.substring(proCodeStr.length()-3);
                	if (StringUtils.isNumeric(suffix)) {
						int currentCode = Integer.parseInt(suffix);
						currentCode++;
						dao.updatePorCode(String.valueOf(currentCode));
					}
                }

				projectApplyExternal.preUpdate();
				super.save(projectApplyExternal);
//				键值	标签	类型	描述	排序	操作
//				03	应用开发	pro_category	应用开发	10	修改 删除 添加键值
//				05	系统集成	pro_category	系统集成	20	修改 删除 添加键值
//				08	IDC	pro_category	IDC	30	修改 删除 添加键值
//				10	基础IT	pro_category	基础IT	40	修改 删除 添加键值
//				07	其它
				if ("03".equals(projectApplyExternal.getCategory())) {
					vars.put("type", "2");
				} else {
					vars.put("type", "1");
				}
				// 项目类型
				vars.put(ActUtils.VAR_PRJ_TYPE, projectApplyExternal.getCategory());
				
				boolean isBossAudit = MyDictUtils.isBossAudit(projectApplyExternal.getEstimatedContractAmount(), projectApplyExternal.getEstimatedGrossProfitMargin());
				
				if (isBossAudit) { // 需要总经理审批
					vars.put(ActUtils.VAR_BOSS_AUDIT, "1");
				} else {
					vars.put(ActUtils.VAR_BOSS_AUDIT, "0");
				}
				
				// TODO
//				Email email = new Email();
//				email.setAddressee("zhangxueguang@cjitec.com");
//				email.setSubject("项目审批结束");
//				email.setContent("项目编号：" + projectApplyExternal.getProjectCode() + "已立项，请尽快录入ERP。");
//				mailService.sendMailByAsynchronousMode(email);
			}
			
		} else if ("".equals(taskDefKey)) {
			
		}
//		else if ("audit2".equals(taskDefKey)){
//			testAudit.setHrText(testAudit.getAct().getComment());
//			dao.updateHrText(testAudit);
//		}
//		else if ("audit4".equals(taskDefKey)){
//			testAudit.setMainLeadText(testAudit.getAct().getComment());
//			dao.updateMainLeadText(testAudit);
//		}
//		else if ("apply_end".equals(taskDefKey)){
//			
//		}
//		
//		// 未知环节，直接返回
//		else{
//			return;
//		}
				
		// 提交流程任务
		vars.put(ActUtils.VAR_PASS, act.getFlagNumber());
		vars.put(ActUtils.VAR_TITLE, projectApplyExternal.getProjectName());
		actTaskService.complateByAct(act, vars);	
	}
	
	/**
	 * 获取当前数据库中项目编号
	 * @return
	 */
	public String getCurrentCode(){
		
		return dao.getCurrentCode();
	}

	public String getProcDefIdByProcInsId(String procInsId) {
		
		return actTaskService.getProcDefIdByProcInsId(procInsId);
	}

	public String getExecutionIdByProcInsId(String procInsId) {
		return actTaskService.getExecutionIdByProcInsId(procInsId);
	}

	public void sendMail(DelegateTask task, String assignee, String userId, String groupId) {
		StringBuilder sbMailTo = new StringBuilder();
		User user;
		// 加入assignee
		if (StringUtils.isNotBlank(assignee)) {
			user = UserUtils.getByLoginName(assignee);
			if (user != null && StringUtils.isNotBlank(user.getEmail()))
				sbMailTo.append(user.getEmail() + ";");	
		}
		
		String [] userArray = userId.split(",");
		// 加入user
		for (int i = 0; i < userArray.length; i++) {
			if (StringUtils.isBlank(userArray[i]))
				continue;
			
			user = UserUtils.getByLoginName(userArray[i]);
			if (user != null && StringUtils.isNotBlank(user.getEmail()))
			sbMailTo.append(user.getEmail() + ";");
		}
		
		String[] groupArray = groupId.split(",");
		Role role = new Role();
		// 加入group
		for (int i = 0; i < groupArray.length; i++) {
			if (StringUtils.isBlank(groupArray[i]))
				continue;
			//
			role.setEnname(groupArray[i]);
			role = roleDao.getRoleUserByEnname(role);
			if (role == null)
				continue;
			
			List<User> users = role.getUserList();
			if (users == null)
				continue;
			
			for (int j = 0; j < users.size(); j++) {
				user = users.get(j);
				if (user == null)
					continue;
				if ("thinkgem".equals(user.getLoginName())) 
					continue;
				if (StringUtils.isBlank(user.getEmail()))
					continue;
				sbMailTo.append(user.getEmail() + ";");
			} // end for UserList
		} // end for groups
		
		String mailTo = sbMailTo.toString();
		if (StringUtils.isBlank(mailTo))
			return;
		
		mailTo = mailTo.substring(0, mailTo.length() - 1);
		// Email
		Email email = new Email();
		email.setAddressee(mailTo);

//		((TaskEntity) task).execution.processDefinition.name
		String subject = ((TaskEntity) task).getExecution().getProcessDefinition().getName();
		email.setSubject(subject); // xxx审批流程
//		email.setSubject((String) task.getVariable(ActUtils.VAR_PROC_NAME));
		email.setContent((String) task.getVariable(ActUtils.VAR_TITLE)); // 项目名称
		if (Global.isSendEmail()) {
			mailService.sendMailByAsynchronousMode(email);
		}
		System.out.println("MailTo=" + sbMailTo.toString());
	}
	
	/**
	 * 维护自己的流程状态	
	 * @param id
	 * @param audit
	 */
	@Transactional(readOnly = false)
	public void auditTo(String id, String audit) {
		ProjectApplyExternal projectApplyExternal = get(id);
		if (projectApplyExternal == null) {
			return ;
		}
		projectApplyExternal.setProcessStatus(audit);
		dao.update(projectApplyExternal);
	}


	public ProjectApplyExternal findByProcInsId(String procInsId) {
		ProjectApplyExternal external = null;
		if (StringUtils.isEmpty(procInsId)) {
			external = new ProjectApplyExternal();
		} else {
			external = dao.findByProcInsId(procInsId);
		}
		if (external == null) {
			external = new ProjectApplyExternal();
		}
		return external;
	}
	
	/**
	 * 业务状态、项目的业务状态、立项审批中、立项完成、招标审批中、招标完成、合同、结项等。
	 * 在各个流程引擎开始和结束时修改项目主表中的字段。
	 * @param id
	 * @param stageValue
	 */
	@Transactional(readOnly = false)
	public void stageTo(String id, String stageValue) {
		ProjectApplyExternal projectApplyExternal = get(id);
		if (projectApplyExternal == null) {
			return;
		}
		projectApplyExternal.setProMainStage(stageValue);
		dao.update(projectApplyExternal);
	}
	
	public List<ProjectApplyExternal> findList4LargerMainStage(ProjectApplyExternal projectApplyExternal){
		return dao.findList4LargerMainStage(projectApplyExternal);
	}
	
}