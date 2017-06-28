package com.thinkgem.jeesite.modules.project.utils;

import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
import com.thinkgem.jeesite.modules.project.service.contract.ProjectContractService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 设置任务执行人
 * @author rgz
 *
 */
public class PmListener implements TaskListener {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void notify(DelegateTask delegateTask) {
//		String id = (String) delegateTask.getVariable("objId");
		String prjId = (String) delegateTask.getVariable(ActUtils.VAR_PRJ_ID);
		ProjectContractService service = SpringContextHolder.getBean(ProjectContractService.class);
		ProjectContract contract = service.findContractByPrjId(prjId);
		if (contract == null) {
			logger.error("找不到此合同审批单。");
			delegateTask.setAssignee("thinkgem");
			return;
		} 
		User pmUser = contract.getProjectManager();
		if (pmUser == null) {
			logger.error("此合同审批单没有指定项目经理。");
			delegateTask.setAssignee("thinkgem");
			return;
		}
		String assignee = pmUser.getLoginName();
		if (StringUtils.isBlank(assignee)) {
			logger.error("合同审批单的项目经理人员没有加载登录名字段。");
			delegateTask.setAssignee("thinkgem");
			return;
		}
		delegateTask.setAssignee(assignee);
	}

}
