/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.execution;

import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.act.utils.ProcessDefCache;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
import com.thinkgem.jeesite.modules.mail.service.MailService;
import com.thinkgem.jeesite.modules.project.dao.execution.ProjectExecutionDao;
import com.thinkgem.jeesite.modules.project.dao.execution.ProjectExecutionItemDao;
import com.thinkgem.jeesite.modules.project.entity.bidding.ProjectBidding;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
import com.thinkgem.jeesite.modules.project.entity.execution.ProjectExecution;
import com.thinkgem.jeesite.modules.project.entity.execution.ProjectExecutionItem;
import com.thinkgem.jeesite.modules.project.utils.MyDictUtils;
import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目合同执行Service
 * @author jicdata
 * @version 2016-03-08
 */
@Service
@Transactional(readOnly = true)
public class ProjectExecutionService extends JicActService<ProjectExecutionDao, ProjectExecution> {

    @Autowired
    private ProjectExecutionItemDao itemDao;

	// private boolean isNewRecord;

    @Autowired
    MailService mailService;

     @Autowired
     ProjectApplyExternalService applyService;
	
	@Override
	public ProjectExecution get(String id) {
//		return super.get(id);
        ProjectExecution execution = super.get(id);
        // in case param id is not execution's id.
        if (execution == null)
            return execution;

        execution.setExecutionItemList(itemDao.findList(new ProjectExecutionItem(execution)));
        return execution;
	}

	/**
	 * 保存并结束流程
	 // * @param projectExecution
	 */
	// @Transactional(readOnly = false)
	// public void saveFinishProcess(ProjectExecution projectExecution) {
     //    // 保存
     //    // save(projectExecution);
	//     // 开启流程
	//     String procInsId = saveLaunch(projectExecution);
	//     // 结束流程
     //    endProcess(procInsId);
	// }

    // 流程启动之前，设置map
    @Override
    public void setupVariable(ProjectExecution projectExecution, Map<String, Object> vars) {

        fillApply(projectExecution);

        myProcVariable(projectExecution, vars);
    }

    private void myProcVariable(ProjectExecution projectExecution, Map<String, Object> vars) {
        vars.put(ActUtils.VAR_PRJ_ID, projectExecution.getApply().getId());

        vars.put(ActUtils.VAR_PRJ_TYPE, projectExecution.getApply().getCategory());

        // vars.put(ActUtils.VAR_PROC_DEF_KEY, projectExecution.getDictRemarks());

        vars.put(ActUtils.VAR_TITLE, projectExecution.getApply().getProjectName());

        // 设置合同金额
        // vars.put(ActUtils.VAR_AMOUNT, projectExecution.getAmount());

        if ("03".equals(projectExecution.getApply().getCategory()) ) {
            System.out.println("");
            // 分支上使用，没在节点上使用
            vars.put(ActUtils.VAR_TYPE, "2");
        } else {
            vars.put(ActUtils.VAR_TYPE, "1");
        }

        // boolean isBossAudit = MyDictUtils.isBossAudit(projectExecution.getAmount(),
        //         projectExecution.getProfitMargin());
        // if (isBossAudit) { // 需要总经理审批
        //     // 节点上使用
        //     vars.put(ActUtils.VAR_SKIP_BOSS, "0");
        // } else {
        //     vars.put(ActUtils.VAR_SKIP_BOSS, "1");
        // }
    }

    /**
	 * 保存表单数据，并启动流程
     *
     * 申请人发起流程，申请人重新发起流程入口
     * 在form界面
     *
	 * @param projectExecution
	 */
	// @Transactional(readOnly = false)
	// public String saveLaunch(ProjectExecution projectExecution) {
    //
	//     if (projectExecution.getIsNewRecord()) {
	//         // 启动流程的时候，把业务数据放到流程变量里
     //        Map<String, Object> varMap = new HashMap<String, Object>();
     //        varMap.put(ActUtils.VAR_PRJ_ID, projectExecution.getApply().getId());
    //
     //        // varMap.put(ActUtils.VAR_PROC_NAME, ActUtils.PROC_NAME_execution);
     //        varMap.put(ActUtils.VAR_PRJ_TYPE, projectExecution.getApply().getCategory());
    //
     //        varMap.put(ActUtils.VAR_TITLE, projectExecution.getApply().getProjectName());
    //
     //        return launch(projectExecution, varMap);
     //    } else { // 把驳回到申请人(重新修改业务表单，重新发起流程、销毁流程)也当成一个特殊的审批节点
     //        // 只要不是启动流程，其它任意节点的跳转都当成节点审批
	//         saveAudit(projectExecution);
	//         return null;
     //    }
	// }

	/**
	 * 保存表单数据
	 * @param projectExecution
	 */
	@Override
	@Transactional(readOnly = false)
	public void save(ProjectExecution projectExecution) {
	    super.save(projectExecution);
	    saveItem(projectExecution);
	}

    public ProjectExecution findByExecutionContractNo(String executionContractNo) {
        return dao.findByExecutionContractNo(executionContractNo);
    }

    // /**
    //  * 审批人审批入口
    //  * @param projectExecution
    //  */
	// @Transactional(readOnly = false)
	// public void auditSave(ProjectExecution projectExecution) {
        // // 设置意见
        // projectExecution.getAct().setComment((projectExecution.getAct().getFlagBoolean() ?
			// 	"[同意] ":"[驳回] ") + projectExecution.getAct().getComment());
        // Map<String, Object> vars = Maps.newHashMap();
        // vars.put(ActUtils.VAR_PASS, projectExecution.getAct().getFlagNumber());
        //
        // // 对不同环节的业务逻辑进行操作
        // String taskDefKey = projectExecution.getAct().getTaskDefKey();
        //
        // if (UserTaskType.UT_BUSINESS_LEADER.equals(taskDefKey)){
			//
			// if ("03".equals(projectExecution.getApply().getCategory()) ) {
			// 	vars.put("type", "2");
			// } else {
			// 	vars.put("type", "1");
			// }
			// // 项目类型
			// vars.put(ActUtils.VAR_PRJ_TYPE, projectExecution.getApply().getCategory());
        //     // 都需要总经理审批
        //     vars.put(ActUtils.VAR_BOSS_AUDIT, "1");
        //
        // } else if ("".equals(taskDefKey)) {
			//
        // }
        // // 提交流程任务
        // saveAuditBase(projectExecution, vars);
	// }

    @Override
	public void processAudit(ProjectExecution projectExecution, Map<String, Object> vars) {
        // 对不同环节的业务逻辑进行操作
        String taskDefKey = projectExecution.getAct().getTaskDefKey();
        if (UserTaskType.UT_BUSINESS_LEADER.equals(taskDefKey)) {
//            if ("03".equals(projectExecution.getApply().getCategory()) ) {
//                vars.put(ActUtils.VAR_TYPE, "2");
//            } else {
//                vars.put(ActUtils.VAR_TYPE, "1");
//            }
            // 项目类型
            // vars.put(ActUtils.VAR_PRJ_TYPE, projectExecution.getApply().getCategory());
            // 都需要总经理审批
        } else if (UserTaskType.UT_OWNER.equals(taskDefKey)) {
            // 驳回到发起人节点后，他可以修改所有的字段，所以重新设置一下流程变量
            fillApply(projectExecution);
            myProcVariable(projectExecution, vars);

            // 又到自己的手里，重新提交
            // save(projectExecution);
            // projectExecution.getAct().setComment((projectExecution.getAct().getFlagBoolean() ?
            //         	"[同意] ":"[驳回] ") + projectExecution.getAct().getComment());
        } else if ("".equalsIgnoreCase(taskDefKey)) {
        }
    }

    public void fillApply(ProjectExecution projectExecution) {
        ProjectApplyExternal apply = applyService.get(projectExecution.getApply().getId());
        projectExecution.setApply(apply);
    }

    // private String launchWorkflow(ProjectExecution projectExecution) {
    //     // 设置流程变量
    //     Map<String, Object> varMap = new HashMap<String, Object>();
    //     varMap.put(ActUtils.VAR_PRJ_ID, projectExecution.getApply().getId());
    //
    //     varMap.put(ActUtils.VAR_PROC_NAME, ActUtils.PROC_NAME_execution);
    //     varMap.put(ActUtils.VAR_PRJ_TYPE, projectExecution.getApply().getCategory());
    //     varMap.put("_ACTIVITI_SKIP_EXPRESSION_ENABLED", true);
    //
    //     String title = projectExecution.getApply().getProjectName();
    //
    //     return launchWorkflowBase(projectExecution, isNewRecord, title, varMap);
    // }

    private void saveItem(ProjectExecution projectExecution) {
        for (ProjectExecutionItem item : projectExecution.getExecutionItemList()){
//            if (StringUtils.isBlank(item.getId())) {
//                continue;
//            }
            if (item.getId() == null){
                continue;
            }
            if (ProjectExecutionItem.DEL_FLAG_NORMAL.equals(item.getDelFlag())){
                if (StringUtils.isBlank(item.getId()) ) {
                    item.setExecution(projectExecution);
                    item.preInsert();
                    itemDao.insert(item);
                } else {
                    item.preUpdate();
                    itemDao.update(item);
                }
            } else {
                itemDao.delete(item);
            }
        }
    }


    public void sendMail(DelegateExecution task, String groupName) {

        // String procName = (String) task.getVariable(ActUtils.VAR_PROC_NAME);
        String procName = ProcessDefCache.get(task.getProcessDefinitionId()).getName();
        String title = (String) task.getVariable(ActUtils.VAR_TITLE);
        String id = (String) task.getVariable(ActUtils.VAR_OBJ_ID);
        String prjId = (String) task.getVariable(ActUtils.VAR_PRJ_ID);
        List<String> groupNames = new ArrayList<String>();
        groupNames.add(groupName);

        ProjectExecution execution = null;

        if (StringUtils.isNotEmpty(id)) {
            execution = get(id);
        }
        if (execution == null) {
            logger.info("执行对象为空，不能发邮件！");
            return;
        }

        String subject = procName + "-" + title + "-" + execution.getApply().getProjectCode();

        mailService.sendEmail(subject, execution.getApply(), execution, groupNames, null);

    }

}