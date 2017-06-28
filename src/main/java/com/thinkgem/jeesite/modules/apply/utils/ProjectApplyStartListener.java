package com.thinkgem.jeesite.modules.apply.utils;

import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

/**
 * 在流程开始时，自动修改表单状态
 * @author rgz
 *
 */
public class ProjectApplyStartListener implements ExecutionListener{

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		String id = (String) execution.getVariable("objId");
		
		ProjectApplyExternalService projectApplyExternalService = SpringContextHolder.getBean(ProjectApplyExternalService.class);
		
		String audit = DictUtils.getDictValue("审批中", "AuditStatus", "0");
		projectApplyExternalService.auditTo(id, audit);
		
		String stageValue = DictUtils.getDictValue("立项审批中", "jic_pro_main_stage", "0");
		projectApplyExternalService.stageTo(id, stageValue);
		
		
	}

}
