package com.thinkgem.jeesite.modules.apply.utils;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * 申请人直接领导
 * 在流程中，自动分配流程发起者的经理任务
 * 把此类配置到直接经理审批的userTask中。
 * @author rgz
 *
 */
@Deprecated
public class UserLeaderTaskListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {

		// String id = (String) delegateTask.getVariable(ActUtils.VAR_OBJ_ID);
		// String prjId = (String) delegateTask.getVariable(ActUtils.VAR_PRJ_ID);
        //
		// if (StringUtils.isEmpty(id)) {
		// 	return;
		// }
		// ProjectContractService contractService = SpringContextHolder.getBean(ProjectContractService.class);
        //
		// ProjectContract contract = contractService.get(id);
		// if (contract == null) {
		// 	return;
		// }
        //
		// String loginName = contract.getProjectManager().getLoginName();
		// if (StringUtils.isEmpty(loginName)) {
		// 	return;
		// }
		// delegateTask.setAssignee(loginName);





		// 当前申请人
		// String loginName = (String) delegateTask.getVariable(ActUtils.VAR_APPLY);
		// User user = UserUtils.getByLoginName(loginName);
		// String officeLeader = null;
		// try {
		// 	officeLeader = user.getOffice().getPrimaryPerson().getLoginName();
		// } catch (Exception e) {
		// 	officeLeader = "thinkgem";
		// }
		
		// 把部门领导作为任务的办理者
		// delegateTask.setAssignee(officeLeader);
		
	}

}
