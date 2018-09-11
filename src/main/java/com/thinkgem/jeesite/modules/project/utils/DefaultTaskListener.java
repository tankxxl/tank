package com.thinkgem.jeesite.modules.project.utils;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 当流程运转到usertask2我们看一下程序的输出：
 * assignment========
 * create=========
 * 因为usertask2节点配置了处理人所以触发assignment事件监听，当任务运转到usertask2的时候触发了create事件
 * 这里我们也可以得出一个结论：assignment事件比create先执行
 * 使用代码结束任务，代码如下：
 * String taskId="127515";
 * demo.getTaskService().complete(taskId);
 * 当我们结束usertask2我们看一下程序的输出：
 * complete===========
 * delete=============
 * 任务完成的时候，触发complete事件，因为任务完成之后，要从ACT_RU_TASK中删除这条记录，所以触发delete事件
 */
public class DefaultTaskListener implements TaskListener {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void notify(DelegateTask delegateTask) {
        String eventName = delegateTask.getEventName();
        logger.info("================" + eventName + "================");

        // 数据库中的taskId主键
        delegateTask.getId();
        // 任务名称
        delegateTask.getName();
        delegateTask.getDescription();

        delegateTask.getProcessInstanceId();

        delegateTask.getExecutionId();

        // Add the given user as a candidate user to this task.
        // delegateTask.addCandidateUser("userId");

        // 添加候选人
        // delegateTask.addCandidateUsers(List);

        // 添加候选组
        // delegateTask.addCandidateGroup("groupId");

        try {
            if ("assignment".equals(eventName)) {
                this.onAssignment(delegateTask);
            } else if ("create".equals(eventName)) {
                this.onCreate(delegateTask);
            } else if ("complete".equals(eventName)) {
                this.onComplete(delegateTask);
            } else if (EVENTNAME_DELETE.equals(eventName)) {
                this.onDelete(delegateTask);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }


    }

    public void onCreate(DelegateTask delegateTask) throws Exception { }
    public void onAssignment(DelegateTask delegateTask) throws Exception { }
    public void onComplete(DelegateTask delegateTask) throws Exception { }
    public void onDelete(DelegateTask delegateTask) throws Exception { }
}
