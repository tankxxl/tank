package com.thinkgem.jeesite.modules.project.utils;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 流程实例全局监听器 - 监听流程实例的启动和结束
 *
 * 共有三个事件：
 * start：
 * take：监控连线的时候使用
 * end：
 *
 *
 * 1.activiti监听方式分为三大类，节点监听、全局监听、连线监听。
 *
 * 2.activiti监听主要实现的类是两个 节点监听、全局监听实现org.activiti.engine.delegate.ExecutionListener
 * 节点的监听实现org.activiti.engine.delegate.TaskListener接口即可。
 *
 * 3.监听器其实就是一个观察者模式
 *
 *
 */
public class DefaultExecutionListener implements ExecutionListener {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception {
        String eventName = delegateExecution.getEventName();

        // execution id
        delegateExecution.getId();
        // 流程实例id
        delegateExecution.getProcessInstanceId();
        // 业务id
        delegateExecution.getProcessBusinessKey();
        // 流程定义id
        delegateExecution.getProcessDefinitionId();
        // 获取父id，并发时候有用
        delegateExecution.getParentId();
        // 获取当前的ActivityId、Name
        delegateExecution.getCurrentActivityId();
        delegateExecution.getCurrentActivityName();
        // 获取TenantId 当有多个TenantId有用
        delegateExecution.getTenantId();
        // * 这个非常有用，当拿到EngineServices对象，所有的xxxService都可以拿到了
        delegateExecution.getEngineServices();

        try {
            if (EVENTNAME_START.equals(eventName)) {
                this.onStart(delegateExecution);
            } else if (EVENTNAME_TAKE.equals(eventName)) {
                this.onTake(delegateExecution);
            } else if (EVENTNAME_END.equals(eventName)) {
                this.onEnd(delegateExecution);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void onStart(DelegateExecution delegateExecution) throws Exception {
    }

    public void onTake(DelegateExecution delegateExecution) throws Exception {
    }

    public void onEnd(DelegateExecution delegateExecution) throws Exception {
    }
}
