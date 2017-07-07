package com.thinkgem.jeesite.modules.project.utils;

import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
import com.thinkgem.jeesite.modules.project.service.contract.ProjectContractService;
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
public class ProjectContractAuditListener implements ExecutionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2890171250343157142L;

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		String id = (String) execution.getVariable("objId");
		String prjId = (String) execution.getVariable("prjId");
		
		ProjectContractService contractService = SpringContextHolder.getBean(ProjectContractService.class);
		String audit = DictUtils.getDictValue("审批结束", "AuditStatus", "0");
		contractService.auditTo(id, audit);
		
		ProjectApplyExternalService applyExternalService = SpringContextHolder.getBean(ProjectApplyExternalService.class);
		String stageValue = DictUtils.getDictValue("合同完成", "jic_pro_main_stage", "0");
		applyExternalService.stageTo(prjId, stageValue);
		
	}

}
