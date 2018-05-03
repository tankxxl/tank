package com.thinkgem.jeesite.modules.project.utils;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 可以自动完成审请人的节点
 *
 */
public class AutoCompleteTaskListener extends DefaultTaskListener {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onCreate(DelegateTask delegateTask) throws Exception {
        String username = Authentication.getAuthenticatedUserId();
        String assignee = delegateTask.getAssignee();
        if (username != null && username.equals(assignee)) {
            ((TaskEntity) delegateTask).complete(null, false);
        }
    }
}
