package com.thinkgem.jeesite.modules.project.utils;

import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
import com.thinkgem.jeesite.modules.project.service.bidding.ProjectBiddingService;
import com.thinkgem.jeesite.modules.project.service.biddingArchive.BiddingArchiveService;
import com.thinkgem.jeesite.modules.project.service.contract.ProjectContractService;
import com.thinkgem.jeesite.modules.project.service.execution.ProjectExecutionService;
import com.thinkgem.jeesite.modules.project.service.finish.ProjectFinishApprovalService;
import com.thinkgem.jeesite.modules.project.service.invoice.ProjectInvoiceService;
import com.thinkgem.jeesite.modules.project.service.purchase.ProjectPurchaseService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;


/**
 * 在流程结束时，自动修改表单状态
 *
 * @author rgz
 *
 */
public class AuditEndListener implements ExecutionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateExecution execution) throws Exception {

//	    processDefinitionId = ProjectApplyExternal:1:b1b91e833adc49ee9a2a3689cf62a44d
//      businessKey = project_apply_external:11e6bec955d34b3a8f05b1aaf72b7c94
//      processInstanceId = 0373e076ce6e479fbe188cb412a9aba9

		String id = (String) execution.getVariable(ActUtils.VAR_OBJ_ID);
		String prjId = (String) execution.getVariable(ActUtils.VAR_PRJ_ID);

        ExecutionEntity executionEntity = (ExecutionEntity) execution;
        String defKey = executionEntity.getProcessDefinitionKey();


        JicActService jicActService = null;
        String stageValue = null;

        if (ActUtils.PD_PROJECTAPPLYEXTERNAL[0].equalsIgnoreCase(defKey)) {
            // 得到业务Service
            jicActService = SpringContextHolder.getBean(ProjectApplyExternalService.class);

            //
            prjId = id;
            stageValue = DictUtils.getDictValue("立项完成", "jic_pro_main_stage", "0");
        } else if (ActUtils.PD_PROJECTBIDDING[0].equalsIgnoreCase(defKey)) {
            // 得到业务Service
            jicActService = SpringContextHolder.getBean(ProjectBiddingService.class);

            stageValue = DictUtils.getDictValue("投标完成", "jic_pro_main_stage", "0");
        } else if (ActUtils.PD_BIDDINGARCHIVE[0].equalsIgnoreCase(defKey)) { // 投标备案
            // 得到业务Service
            jicActService = SpringContextHolder.getBean(BiddingArchiveService.class);

            stageValue = DictUtils.getDictValue("投标备案完成", "jic_pro_main_stage", "0");
        } else if (ActUtils.PD_PROJECTCONTRACT[0].equalsIgnoreCase(defKey)) { // 合同
            // 得到业务Service
            jicActService = SpringContextHolder.getBean(ProjectContractService.class);

            stageValue = DictUtils.getDictValue("合同完成", "jic_pro_main_stage", "0");
        } else if (ActUtils.PD_execution[0].equalsIgnoreCase(defKey)) {  // 合同执行
            // 得到业务Service
            jicActService = SpringContextHolder.getBean(ProjectExecutionService.class);

            ProjectExecutionService executionService = SpringContextHolder.getBean(ProjectExecutionService.class);
            stageValue = DictUtils.getDictValue("执行完成", "jic_pro_main_stage", "0");

            // TODO 邮件通知项目管理专员
            executionService.sendMail(execution, "usertask_specialist");

        } else if (ActUtils.PD_purchase[0].equalsIgnoreCase(defKey)) {
            // 得到业务Service
            jicActService = SpringContextHolder.getBean(ProjectPurchaseService.class);

            stageValue = DictUtils.getDictValue("采购完成", "jic_pro_main_stage", "0");
        } else if (ActUtils.PD_invoice[0].equalsIgnoreCase(defKey)) {
            // 得到业务Service
            jicActService = SpringContextHolder.getBean(ProjectInvoiceService.class);

            stageValue = DictUtils.getDictValue("开票完成", "jic_pro_main_stage", "0");
        } else if (ActUtils.PD_PROJECTFINISHAPPROVAL[0].equalsIgnoreCase(defKey)) {
            // 得到业务Service
            jicActService = SpringContextHolder.getBean(ProjectFinishApprovalService.class);

            stageValue = DictUtils.getDictValue("结项完成", "jic_pro_main_stage", "0");
        } else if (ActUtils.PD_TECHAPPLY[0].equalsIgnoreCase(defKey)) {

        }

		if (jicActService != null) {
            String audit = DictUtils.getDictValue("审批结束", "AuditStatus", "0");
            jicActService.auditTo(id, audit);
        }

		if (StringUtils.isEmpty(prjId) || StringUtils.isEmpty(stageValue))
		    return;

		ProjectApplyExternalService applyExternalService = SpringContextHolder.getBean(ProjectApplyExternalService.class);

		applyExternalService.stageTo(prjId, stageValue);

	}

}
