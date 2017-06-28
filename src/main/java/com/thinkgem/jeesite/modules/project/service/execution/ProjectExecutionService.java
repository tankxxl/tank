/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.execution;

import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.annotation.Loggable;
import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
import com.thinkgem.jeesite.modules.mail.service.MailService;
import com.thinkgem.jeesite.modules.project.dao.execution.ProjectExecutionDao;
import com.thinkgem.jeesite.modules.project.dao.execution.ProjectExecutionItemDao;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
import com.thinkgem.jeesite.modules.project.entity.execution.ProjectExecution;
import com.thinkgem.jeesite.modules.project.entity.execution.ProjectExecutionItem;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
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

	private boolean isNewRecord;

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
	 * @param projectExecution
	 */
	@Transactional(readOnly = false)
	public void saveFinishProcess(ProjectExecution projectExecution) {
        // 保存
	    save(projectExecution);
	    // 开启流程
	    String procInsId = launchWorkflow(projectExecution);
	    // 结束流程
        endProcess(procInsId);
	}

	/**
	 * 保存表单数据，并启动流程
	 * @param projectExecution
	 */
	@Transactional(readOnly = false)
	public void saveLaunch(ProjectExecution projectExecution) {
	    save(projectExecution);
	    launchWorkflow(projectExecution);
	}

//    public ProjectExecution findByProcInsId(String procInsId) {
//        if (StringUtils.isEmpty(procInsId)) {
//            return null;
//        } else {
//            return dao.findByProcInsId(procInsId);
//        }
//    }


	/**
	 * 保存表单数据
	 * @param projectExecution
	 */
	@Override
	@Transactional(readOnly = false)
	public void save(ProjectExecution projectExecution) {
	    //
	    isNewRecord = projectExecution.getIsNewRecord();
	    super.save(projectExecution);
	    saveItem(projectExecution);
	}
	
	@Override
	@Transactional(readOnly = false)
	public void delete(ProjectExecution projectExecution) {
		super.delete(projectExecution);
//        itemDao.delete(new ProjectExecutionItem(projectExecution));
	}

    public ProjectExecution findByExecutionContractNo(String executionContractNo) {
        return dao.findByExecutionContractNo(executionContractNo);
    }

    /**
     * 审批人审批入口
     * @param projectExecution
     */
	@Transactional(readOnly = false)
	public void auditSave(ProjectExecution projectExecution) {
		// 设置意见
        projectExecution.getAct().setComment((projectExecution.getAct().getFlagBoolean() ?
				"[同意] ":"[驳回] ") + projectExecution.getAct().getComment());
		Map<String, Object> vars = Maps.newHashMap();
        vars.put(ActUtils.VAR_PASS, projectExecution.getAct().getFlagNumber());
		
		// 对不同环节的业务逻辑进行操作
		String taskDefKey = projectExecution.getAct().getTaskDefKey();

		if (UserTaskType.UT_BUSINESS_LEADER.equals(taskDefKey)){
			
			if ("03".equals(projectExecution.getApply().getCategory()) ) {
				vars.put("type", "2");
			} else {
				vars.put("type", "1");
			}
			// 项目类型
			vars.put(ActUtils.VAR_PRJ_TYPE, projectExecution.getApply().getCategory());
            // 都需要总经理审批
            vars.put(ActUtils.VAR_BOSS_AUDIT, "1");

		} else if ("".equals(taskDefKey)) {
			
		}
		// 提交流程任务
        saveAuditBase(projectExecution, vars);
	}


    private String launchWorkflow(ProjectExecution projectExecution) {
        // 设置流程变量
        Map<String, Object> varMap = new HashMap<String, Object>();
        varMap.put(ActUtils.VAR_PRJ_ID, projectExecution.getApply().getId());

        varMap.put(ActUtils.VAR_PROC_NAME, ActUtils.PROC_NAME_execution);
        varMap.put(ActUtils.VAR_PRJ_TYPE, projectExecution.getApply().getCategory());
        varMap.put("_ACTIVITI_SKIP_EXPRESSION_ENABLED", true);

        String title = projectExecution.getApply().getProjectName();

        return launchWorkflowBase(projectExecution, isNewRecord, title, varMap);
    }

    private void saveItem(ProjectExecution projectExecution) {
        for (ProjectExecutionItem item : projectExecution.getExecutionItemList()){

//            if (StringUtils.isBlank(item.getId())) {
//                continue;
//            }

            if (item.getId() == null){
                continue;
            }

            if (ProjectExecutionItem.DEL_FLAG_NORMAL.equals(item.getDelFlag())){
                if (StringUtils.isBlank(item.getId())){
                    item.setExecution(projectExecution);
                    item.preInsert();
                    itemDao.insert(item);
                }else{
                    item.preUpdate();
                    itemDao.update(item);
                }
            }else{
                itemDao.delete(item);
            }
        }
    }


    public void sendMail(DelegateExecution task, String groupName) {

        String procName = (String) task.getVariable(ActUtils.VAR_PROC_NAME);
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