package com.thinkgem.jeesite.modules.apply.utils;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * 得到项目专员节点的办理人。
 * 用在task create中
 * @author rgz
 *
 */
@Deprecated
public class SpecialistLeaderTaskListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		// 项目专员
		// String loginName = (String) delegateTask.getVariable(ActUtils.VAR_SPECIALIST);
		// User user = UserUtils.getByLoginName(loginName);
		// String officeLeader = user.getOffice().getPrimaryPerson().getLoginName();
		// 把部门领导作为任务的办理者
		// delegateTask.setAssignee(officeLeader);
	}

}
