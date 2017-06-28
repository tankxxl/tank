package com.thinkgem.jeesite.modules.apply.utils;

import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * 申请人所在部门的分管领导
 * 用在【业务部分管领导审批】节点
 * 得到申请人的部门-分管领导
 * @author rgz
 *
 */
public class BusiLeaderTaskListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		// 当前申请人
		String loginName = (String) delegateTask.getVariable(ActUtils.VAR_APPLY);
		User user = UserUtils.getByLoginName(loginName);
		// 把部门的辅负责人当成业务分管领导
		String busiLeader = null;
		try {
			busiLeader = user.getOffice().getDeputyPerson().getLoginName();
		} catch (Exception e) {
			busiLeader = "thinkgem";
		}
		delegateTask.setAssignee(busiLeader);
		
	}

}
