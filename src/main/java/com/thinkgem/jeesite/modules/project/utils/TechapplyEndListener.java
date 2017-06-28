package com.thinkgem.jeesite.modules.project.utils;

import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.modules.project.service.tech.TechapplyService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

/**
 * 在流程结束时，自动修改表单状态
 * @author rgz
 *
 */
public class TechapplyEndListener implements ExecutionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6183468030922515197L;

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		String id = (String) execution.getVariable("objId");
//		String prjId = (String) execution.getVariable("prjId");
		
		TechapplyService techapplyService = SpringContextHolder.getBean(TechapplyService.class);
		String audit = DictUtils.getDictValue("审批结束", "AuditStatus", "0");
		techapplyService.auditTo(id, audit);
		
//		ProjectFinishApprovalService projectFinishApprovalService = SpringContextHolder.getBean(ProjectFinishApprovalService.class);
//		String audit = DictUtils.getDictValue("审批结束", "AuditStatus", "0");
//		projectFinishApprovalService.auditTo(id, audit);
//		
//		ProjectApplyExternalService applyExternalService = SpringContextHolder.getBean(ProjectApplyExternalService.class);
//		String stageValue = DictUtils.getDictValue("结项", "jic_pro_main_stage", "defaultLabel");
//		applyExternalService.stageTo(prjId, stageValue);
	}

}
