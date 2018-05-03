package com.thinkgem.jeesite.modules.project.utils;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultExecutionListener implements ExecutionListener {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception {
        String eventName = delegateExecution.getEventName();
        if ("start".equals(eventName)) {
            try {
                this.onStart(delegateExecution);
            } catch(Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        if ("end".equals(eventName)) {
            try {
                this.onEnd(delegateExecution);
            } catch(Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    public void onStart(DelegateExecution delegateExecution) throws Exception {
    }

    public void onEnd(DelegateExecution delegateExecution) throws Exception {
    }
}
