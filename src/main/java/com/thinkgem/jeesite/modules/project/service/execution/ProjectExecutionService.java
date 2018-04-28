/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.execution;

import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.act.utils.ProcessDefCache;
import com.thinkgem.jeesite.modules.mail.service.MailService;
import com.thinkgem.jeesite.modules.project.dao.execution.ProjectExecutionDao;
import com.thinkgem.jeesite.modules.project.dao.execution.ProjectExecutionItemDao;
import com.thinkgem.jeesite.modules.project.entity.execution.ProjectExecution;
import com.thinkgem.jeesite.modules.project.entity.execution.ProjectExecutionItem;
import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
	
	@Override
	public ProjectExecution get(String id) {
        ProjectExecution execution = super.get(id);
        if (execution == null)
            return new ProjectExecution();

        execution.setExecutionItemList(itemDao.findList(new ProjectExecutionItem(execution)));
        return execution;
	}

    @Override
    public void setupVariable(ProjectExecution projectExecution, Map<String, Object> varMap) {
        varMap.put(ActUtils.VAR_PRJ_ID, projectExecution.getApply().getId());

        varMap.put(ActUtils.VAR_PRJ_TYPE, projectExecution.getApply().getCategory());

        varMap.put(ActUtils.VAR_TITLE, projectExecution.getApply().getProjectName());
    }

	@Override
	@Transactional(readOnly = false)
	public void save(ProjectExecution projectExecution) {
	    super.save(projectExecution);
	    saveItem(projectExecution);
	}

    public ProjectExecution findByExecutionContractNo(String executionContractNo) {
        return dao.findByExecutionContractNo(executionContractNo);
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
        String procName = ProcessDefCache.get(task.getProcessDefinitionId()).getName();
        String title = (String) task.getVariable(ActUtils.VAR_TITLE);
        String id = (String) task.getVariable(ActUtils.VAR_OBJ_ID);
        String prjId = (String) task.getVariable(ActUtils.VAR_PRJ_ID);
        List<String> groupNames = new ArrayList<>();

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