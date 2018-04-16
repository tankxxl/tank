package com.thinkgem.jeesite.modules.apply.utils;

import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * 根据项目类型把此节点分配给不同的角色去审批。
 * 
 * @author rgz
 *
 */
@Deprecated
public class ProjectTypeToRoleTaskListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		// 项目类型
		String prjType = (String) delegateTask.getVariable(ActUtils.VAR_PRJ_TYPE);
//		SystemService systemService = SpringContextHolder.getBean(SystemService.class);
		if ("03".equals(prjType)) {
			delegateTask.addCandidateGroup("usertask_tech_boss");
		} else if ("05".equals(prjType)) {
			delegateTask.addCandidateGroup("usertask_busi_boss");
		} else {
			delegateTask.addCandidateGroup("usertask_busi_boss");
		}
	}

}
