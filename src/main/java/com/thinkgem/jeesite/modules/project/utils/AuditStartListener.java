package com.thinkgem.jeesite.modules.project.utils;

import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
import com.thinkgem.jeesite.modules.project.service.bidding.ProjectBiddingService;
import com.thinkgem.jeesite.modules.project.service.contract.ProjectContractService;
import com.thinkgem.jeesite.modules.project.service.execution.ProjectExecutionService;
import com.thinkgem.jeesite.modules.project.service.finish.ProjectFinishApprovalService;
import com.thinkgem.jeesite.modules.project.service.invoice.ProjectInvoiceService;
import com.thinkgem.jeesite.modules.project.service.purchase.ProjectPurchaseService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import org.activiti.engine.EngineServices;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;


/**
 * 全局监听器，监听流程实例启动、结束
 *
 * 不用每个流程都定义一个监听器了，统一定义一个监听器类来监听所有的流程，判断流程定义KEY即可分辩各个流程。
 * 在流程图上定义start、end事件的监听器，而不是在usertask节点上设置。
 *
 * 在流程开始时，自动修改表单状态
 * 1、主表修改为：xxx审批中
 * 2、子表修改为：审批中
 * @author rgz
 *
 */
public class AuditStartListener implements ExecutionListener{

    String EVENTNAME_START = "start";
    String EVENTNAME_END = "end";
    String EVENTNAME_TAKE = "take";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateExecution execution) throws Exception {

	    // test
        String eventName = execution.getEventName();
        if ("start".equals(eventName)) {
            System.out.println("start---流程启动了");
        } else if ("end".equals(eventName)) {
            System.out.println("end---流程结束了");
        }
        // execution id
        execution.getId();
        // 流程实例id
        execution.getProcessInstanceId();
        // 这个比较有用 主要就是start、end、take
        execution.getEventName();
        // 业务id
        execution.getProcessBusinessKey();
        // 流程定义id
        execution.getProcessDefinitionId();
        // 获取父id，并发的时候有用
        execution.getParentId();
        // 获取当前的ActivityId
        execution.getCurrentActivityId();
        // 获取当前的Activity name
        execution.getCurrentActivityName();
        // 这个非常有用吧，当拿到EngineServices 对象所有的xxxService都可以拿到。
        EngineServices engineServices = execution.getEngineServices();


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
            // 立项时相等
            prjId = id;
            stageValue = DictUtils.getDictValue("立项审批中", "jic_pro_main_stage", "0");
        } else if (ActUtils.PD_PROJECTBIDDING[0].equalsIgnoreCase(defKey)) {
            // 得到业务Service
            jicActService = SpringContextHolder.getBean(ProjectBiddingService.class);

            stageValue = DictUtils.getDictValue("投标审批中", "jic_pro_main_stage", "0");

        } else if (ActUtils.PD_PROJECTCONTRACT[0].equalsIgnoreCase(defKey)) {
            // 得到业务Service
            jicActService = SpringContextHolder.getBean(ProjectContractService.class);

            stageValue = DictUtils.getDictValue("合同审批中", "jic_pro_main_stage", "0");
        } else if (ActUtils.PD_execution[0].equalsIgnoreCase(defKey)) {
            // 得到业务Service
            jicActService = SpringContextHolder.getBean(ProjectExecutionService.class);

            stageValue = DictUtils.getDictValue("执行审批中", "jic_pro_main_stage", "0");
        } else if (ActUtils.PD_purchase[0].equalsIgnoreCase(defKey)) {
            // 得到业务Service
            jicActService = SpringContextHolder.getBean(ProjectPurchaseService.class);

            stageValue = DictUtils.getDictValue("采购审批中", "jic_pro_main_stage", "0");
        } else if (ActUtils.PD_invoice[0].equalsIgnoreCase(defKey)) {
            // 得到业务Service
            jicActService = SpringContextHolder.getBean(ProjectInvoiceService.class);

            stageValue = DictUtils.getDictValue("开票审批中", "jic_pro_main_stage", "0");
        } else if (ActUtils.PD_PROJECTFINISHAPPROVAL[0].equalsIgnoreCase(defKey)) {
            // 得到业务Service
            jicActService = SpringContextHolder.getBean(ProjectFinishApprovalService.class);

            stageValue = DictUtils.getDictValue("结项审批中", "jic_pro_main_stage", "0");
        } else if (ActUtils.PD_TECHAPPLY[0].equalsIgnoreCase(defKey)) {

        }

        if (jicActService != null) {
            String audit = DictUtils.getDictValue("审批中", "AuditStatus", "0");
            jicActService.auditTo(id, audit);
        }

        if (StringUtils.isEmpty(prjId) || StringUtils.isEmpty(stageValue))
            return;

        ProjectApplyExternalService applyExternalService = SpringContextHolder.getBean(ProjectApplyExternalService.class);

        applyExternalService.stageTo(prjId, stageValue);
	}

}
