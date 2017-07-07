package com.thinkgem.jeesite.modules.apply.utils;

import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

/**
 * 在流程结束时，自动修改表单状态
 * @deprecated 不用每个流程都定义一个监听器了，统一定义一个监听器类来监听所有的流程，判断流程定义KEY即可分辩各个流程。
 * 使用AuditEndListener监听器
 *
 * @author rgz
 *
 */
@Deprecated
public class ProjectApplyAuditListener implements ExecutionListener{

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		String id = (String) execution.getVariable("objId");

//        ExecutionEntity executionEntity = (ExecutionEntity) execution;
//        executionEntity.getProcessDefinitionKey();
////	processDefinitionId=	ProjectApplyExternal:1:b1b91e833adc49ee9a2a3689cf62a44d
////        businessKey = project_apply_external:11e6bec955d34b3a8f05b1aaf72b7c94
////        processInstanceId = 0373e076ce6e479fbe188cb412a9aba9
//
//
//        ActUtils.PD_PROJECTAPPLYEXTERNAL[0];

		ProjectApplyExternalService projectApplyExternalService = SpringContextHolder.getBean(ProjectApplyExternalService.class);
		
		String audit = DictUtils.getDictValue("审批结束", "AuditStatus", "0");
		projectApplyExternalService.auditTo(id, audit);
		
		String stageValue = DictUtils.getDictValue("立项完成", "jic_pro_main_stage", "0");
		projectApplyExternalService.stageTo(id, stageValue);
		
		
	}

}
