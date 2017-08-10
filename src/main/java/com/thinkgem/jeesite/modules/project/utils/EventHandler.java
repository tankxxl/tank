package com.thinkgem.jeesite.modules.project.utils;

import org.activiti.engine.delegate.event.ActivitiEvent;

public interface EventHandler {
    /**
     * 事件处理器
     * @param event
     */
    public void handle(ActivitiEvent event);
}
