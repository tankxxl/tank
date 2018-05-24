/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.apply.service.external;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.common.utils.StringUtils;
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
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
public class ProjectApplyExternalService
		extends JicActService<ProjectApplyExternalDao, ProjectApplyExternal> {
	
	@Autowired
	MailService mailService;
	
	@Autowired
	private RoleDao roleDao;

	@Override
	public ProjectApplyExternal get(String id) {
		ProjectApplyExternal apply = super.get(id);
		if (apply == null) {
			apply = new ProjectApplyExternal();
		}
		return apply;
	}

	@Override
	@Transactional(readOnly = false)
	public void delete(ProjectApplyExternal entity) {
		// 流程审批状态,字典数据AuditStatus，0初始录入，1审批中，2审批结束
		String status = entity.getProcStatus();
		if ("1".equals(status)) {
			endProcess(entity.getProcInsId());
		}
		super.delete(entity);
	}

	@Override
	@Transactional(readOnly = false)
	public void save(ProjectApplyExternal entity) {
		if (entity.getIsNewRecord()){
			entity.preInsert4ProInteralApply();
		}
		super.save(entity);
	}

	@Override
	public void setupVariable(ProjectApplyExternal projectApplyExternal, Map<String, Object> vars) {
		projectApplyExternal.preInsert4ProInteralApply();

		vars.put(ActUtils.VAR_PRJ_ID, projectApplyExternal.getId());
		vars.put(ActUtils.VAR_TITLE, projectApplyExternal.getProjectName());
		vars.put(ActUtils.VAR_PRJ_TYPE, projectApplyExternal.getCategory());
		vars.put(ActUtils.VAR_OFFICE_CODE, projectApplyExternal.getSaler().getOffice().getCode());
		// vars.put(ActUtils.VAR_TITLE, projectApplyExternal.getProjectName());

		if ("03".equals(projectApplyExternal.getCategory()) ) {
			vars.put(ActUtils.VAR_TYPE, "2");
		} else {
			vars.put(ActUtils.VAR_TYPE, "1");
		}

		boolean isBossAudit = MyDictUtils.isBossAudit(projectApplyExternal.getEstimatedContractAmount(), projectApplyExternal.getEstimatedGrossProfitMargin());

		if (isBossAudit) { // 需要总经理审批
			vars.put(ActUtils.VAR_SKIP_BOSS, "0");
		} else {
			vars.put(ActUtils.VAR_SKIP_BOSS, "1");
		}

		// if ("1".equals(projectApplyExternal.getSelfDev()) ) {
		// 	vars.put(ActUtils.VAR_SKIP_DEV, "0");
		// } else {
		// 	vars.put(ActUtils.VAR_SKIP_DEV, "1");
		// }
	}

	/**
	 * 保存并结束流程
	 * @param projectApplyExternal
	 */
	// @Transactional(readOnly = false)
	// public void saveFinishProcess(ProjectApplyExternal projectApplyExternal) {
	// 	// 开启流程
	// 	String procInsId = saveLaunch(projectApplyExternal);
	// 	// 结束流程
	// 	endProcess(procInsId);
	// }

	/**
	 * 保存表单数据，并启动流程
	 *
	 * 申请人发起流程，申请人重新发起流程入口
	 * 在form界面
	 *
	 * @param projectApplyExternal
	 */
	// @Transactional(readOnly = false)
	// public String saveLaunch(ProjectApplyExternal projectApplyExternal) {
	// 	if (projectApplyExternal.getIsNewRecord()) {
	// 		projectApplyExternal.preInsert4ProInteralApply();//判断是否是插入还是修改，若是插入那么添加当前用户为销售
	// 		// 启动流程的时候，把业务数据放到流程变量里
	// 		Map<String, Object> varMap = new HashMap<String, Object>();
	// 		varMap.put(ActUtils.VAR_PRJ_ID, projectApplyExternal.getId());
    //
	// 		varMap.put(ActUtils.VAR_PRJ_TYPE, projectApplyExternal.getCategory());
    //
	// 		varMap.put(ActUtils.VAR_TITLE, projectApplyExternal.getProjectName());
    //
	// 		if ("03".equals(projectApplyExternal.getCategory()) ) {
	// 			varMap.put(ActUtils.VAR_TYPE, "2");
	// 		} else {
	// 			varMap.put(ActUtils.VAR_TYPE, "1");
	// 		}
    //
	// 		boolean isBossAudit = MyDictUtils.isBossAudit(projectApplyExternal.getEstimatedContractAmount(), projectApplyExternal.getEstimatedGrossProfitMargin());
	// 		if (isBossAudit) { // 需要总经理审批
	// 			varMap.put(ActUtils.VAR_SKIP_BOSS, "0");
	// 		} else {
	// 			varMap.put(ActUtils.VAR_SKIP_BOSS, "1");
	// 		}
    //
	// 		return launch(projectApplyExternal, varMap);
	// 	} else { // 把驳回到申请人(重新修改业务表单，重新发起流程、销毁流程)也当成一个特殊的审批节点
	// 		// 只要不是启动流程，其它任意节点的跳转都当成节点审批
	// 		saveAudit(projectApplyExternal);
	// 		return null;
	// 	}
	// }

	@Override
	public void processAudit(ProjectApplyExternal projectApplyExternal, Map<String, Object> vars) {
		// 对不同环节的业务逻辑进行操作
		String taskDefKey = projectApplyExternal.getAct().getTaskDefKey();
		if (UserTaskType.UT_SPECIALIST.equals(taskDefKey)) {
			// 审批通过才保存项目信息、项目编号
			if (projectApplyExternal.getAct().getFlagBoolean()) {
				String proCodeStr = projectApplyExternal.getProjectCode();//包含类别、年份、归属的信息+标识位
				if (StringUtils.isNotEmpty(proCodeStr) && proCodeStr.length() > 3) {
					String suffix = proCodeStr.substring(proCodeStr.length()-3);
					if (StringUtils.isNumeric(suffix)) {
						int currentCode = Integer.parseInt(suffix);
						currentCode++;
						// 项目专员审批后，保存项目编号
						dao.updatePorCode(String.valueOf(currentCode));
					}
				}
				// 项目专员审批后，保存立项申请单
				save(projectApplyExternal);
			}
		} else if (UserTaskType.UT_OWNER.equals(taskDefKey)) {
			// 又到自己的手里，重新提交
			// save(projectExecution);
			// projectExecution.getAct().setComment((projectExecution.getAct().getFlagBoolean() ?
			//         	"[同意] ":"[驳回] ") + projectExecution.getAct().getComment());
		}
	}


	/**
	 * 获取当前数据库中项目编号
	 * @return
	 */
	public String getCurrentCode(){
		return dao.getCurrentCode();
	}


	public void sendMail(DelegateTask task, String assignee, String userId, String groupId) {
		if (!Global.isSendEmail()) {
			return;
		}
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
		email.setContent((String) task.getVariable(ActUtils.VAR_TITLE)); // 项目名称
		if (Global.isSendEmail()) {
			mailService.sendMailByAsyncMode(email);
		}
		System.out.println("MailTo=" + sbMailTo.toString());
	}
	
	/**
	 * 业务状态、项目的业务状态、立项审批中、立项完成、招标审批中、招标完成、合同、结项等。
	 * 在各个流程引擎开始和结束时修改项目主表中的字段。
	 * 项目的大阶段
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

	@Deprecated
	public List<ProjectApplyExternal> findList4LargerMainStage(ProjectApplyExternal projectApplyExternal){
		return dao.findList4LargerMainStage(projectApplyExternal);
	}

	@Deprecated
	public List<ProjectApplyExternal> findAllList4LargerMainStage(ProjectApplyExternal projectApplyExternal){
		return dao.findAllList4LargerMainStage(projectApplyExternal);
	}

	//
	public List<Map<String, Object>> findList4tree(String proMainStage, boolean strict) {

		ProjectApplyExternal applyExternal = new ProjectApplyExternal();

		proMainStage = StringUtils.substringBefore(proMainStage, "?");
		if (strict) {
			applyExternal.setQueryStage(Arrays.asList(proMainStage.split(",")));
		} else {
			applyExternal.setProMainStage(proMainStage);
		}

		applyExternal.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		List<ProjectApplyExternal> list = findList(applyExternal);
		return toMapList(list);
	}

	private List<Map<String, Object>> toMapList(List<ProjectApplyExternal> list) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		if (list == null || list.isEmpty())
			return mapList;

		for (int i=0; i<list.size(); i++) {
			ProjectApplyExternal e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("name", e.getProjectName());
			mapList.add(map);
		}
		return mapList;
	}
	
}