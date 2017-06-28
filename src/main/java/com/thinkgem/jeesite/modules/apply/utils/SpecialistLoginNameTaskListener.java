package com.thinkgem.jeesite.modules.apply.utils;

import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * 在项目专员审批流程节点完成后，得到项目专员节点的办理人。
 * 用在task complete中
 * @author rgz
 *
 */
public class SpecialistLoginNameTaskListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		String specialist = delegateTask.getAssignee();
		delegateTask.getVariables().put(ActUtils.VAR_SPECIALIST, specialist);
	}

}
