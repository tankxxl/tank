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

    @Autowired
    MailService mailService;

     @Autowired
     ProjectApplyExternalService applyService;
	
	@Override
	public ProjectExecution get(String id) {
        ProjectExecution execution = super.get(id);
        // in case param id is not execution's id.
        if (execution == null)
            return new ProjectExecution();

        execution.setExecutionItemList(itemDao.findList(new ProjectExecutionItem(execution)));
        return execution;
	}

    // 流程启动之前，设置map
    @Override
    public void setupVariable(ProjectExecution projectExecution, Map<String, Object> vars) {

        fillApply(projectExecution);

        myProcVariable(projectExecution, vars);
    }

    private void myProcVariable(ProjectExecution projectExecution, Map<String, Object> vars) {
        vars.put(ActUtils.VAR_PRJ_ID, projectExecution.getApply().getId());

        vars.put(ActUtils.VAR_PRJ_TYPE, projectExecution.getApply().getCategory());

        vars.put(ActUtils.VAR_TITLE, projectExecution.getApply().getProjectName());

        if ("03".equals(projectExecution.getApply().getCategory()) ) {
            System.out.println("");
            // 分支上使用，没在节点上使用
            vars.put(ActUtils.VAR_TYPE, "2");
        } else {
            vars.put(ActUtils.VAR_TYPE, "1");
        }
    }

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

    @Override
	public void processAudit(ProjectExecution projectExecution, Map<String, Object> vars) {
        // 对不同环节的业务逻辑进行操作
        String taskDefKey = projectExecution.getAct().getTaskDefKey();
        if (UserTaskType.UT_BUSINESS_LEADER.equals(taskDefKey)) {
        } else if (UserTaskType.UT_OWNER.equals(taskDefKey)) {
            // 驳回到发起人节点后，他可以修改所有的字段，所以重新设置一下流程变量
            fillApply(projectExecution);
            myProcVariable(projectExecution, vars);

        } else if ("".equalsIgnoreCase(taskDefKey)) {
        }
    }

    public void fillApply(ProjectExecution projectExecution) {
        ProjectApplyExternal apply = applyService.get(projectExecution.getApply().getId());
        projectExecution.setApply(apply);
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