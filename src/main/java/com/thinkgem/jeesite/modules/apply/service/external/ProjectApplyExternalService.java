/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.apply.service.external;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.common.utils.DateUtils;
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

import java.util.List;
import java.util.Map;

/**
 * 外部立项申请Service
 * @author jicdata
 * @version 2016-02-23
 */
@Service
@Transactional(readOnly = true)
public class ProjectApplyExternalService extends JicActService<ProjectApplyExternalDao, ProjectApplyExternal> {
	
	@Autowired
	MailService mailService;
	
	@Autowired
	private RoleDao roleDao;

	// @Override
	// public ProjectApplyExternal get(String id) {
	// 	return super.get(id);
	// }

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

	@Override
	public void setupVariable(ProjectApplyExternal projectApplyExternal, Map<String, Object> vars) {
		projectApplyExternal.preInsert4ProInteralApply();

		vars.put(ActUtils.VAR_PRJ_ID, projectApplyExternal.getId());
		vars.put(ActUtils.VAR_TITLE, projectApplyExternal.getProjectName());

		if ("1".equals(projectApplyExternal.getSelfDev()) ) {
			vars.put(ActUtils.VAR_SKIP_DEV, "0");
		} else {
			vars.put(ActUtils.VAR_SKIP_DEV, "1");
		}

		boolean isBossAudit = MyDictUtils.isBossAudit(projectApplyExternal.getEstimatedContractAmount(), projectApplyExternal.getEstimatedGrossProfitMargin());
		if (isBossAudit) { // 需要总经理审批
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
	// 		if ("1".equals(projectApplyExternal.getSelfDev()) ) {
	// 			varMap.put(ActUtils.VAR_SKIP_DEV, "0");
	// 		} else {
	// 			varMap.put(ActUtils.VAR_SKIP_DEV, "1");
	// 		}
    //
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
						dao.updatePorCode(String.valueOf(currentCode));
					}
				}
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
			mailService.sendMailByAsynchronousMode(email);
		}
		System.out.println("MailTo=" + sbMailTo.toString());
	}
	
	/**
	 * 维护自己的流程状态	
	 * @param id
	 * @param audit
	 */
	// @Transactional(readOnly = false)
	// public void auditTo(String id, String audit) {
	// 	ProjectApplyExternal projectApplyExternal = get(id);
	// 	if (projectApplyExternal == null) {
	// 		return ;
	// 	}
	// 	projectApplyExternal.setProcessStatus(audit);
	// 	dao.update(projectApplyExternal);
	// }
	
	/**
	 * 项目大状态
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

	public List<ProjectApplyExternal> findAllList4LargerMainStage(ProjectApplyExternal projectApplyExternal){
		return dao.findAllList4LargerMainStage(projectApplyExternal);
	}

	/**
	 * 流程结束时自动执行
	 * @param id
	 */
	@Transactional(readOnly = false)
	public void genProjectCode(String id) {

		ProjectApplyExternal external = get(id);
		if (external == null)
			return;

		String code = autoGenericCode(dao.getCurrentCode(), 2);

		external.setProjectCode(DateUtils.getDate("yyyyMMdd") + code);
		save(external);

		dao.updatePorCode(code);
	}

	/**
	 * 不够位数的在前面补0，保留num的长度位数字
	 *
	 * @param code
	 * @return 返回定长的、自增1的数字字符串
	 */
	private String autoGenericCode(String code, int num) {
		if (StringUtils.isBlank(code))
			code = "0";

		String result = "";
		// 保留num的位数
    	// 0 代表前面补充0
		// num 代表长度为4
		// d 代表参数为正数型
		result = String.format("%0" + num + "d", Integer.parseInt(code) + 1);
		return result;
	}
}