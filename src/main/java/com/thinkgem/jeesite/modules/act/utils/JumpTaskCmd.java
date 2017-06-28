package com.thinkgem.jeesite.modules.act.utils;

import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.task.Comment;

/**
 * rgz
 * activiti5之自由流转
 * 
 //调用方法：
String taskId=request.getParameter("taskId");
Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
String executionId=task.getExecutionId();
String taskDefinitionKey = request.getParameter("taskDefinitionKey");
String activityId = taskDefinitionKey;
TaskServiceImpl taskServiceImpl = (TaskServiceImpl) taskService;
taskServiceImpl.getCommandExecutor().execute(new JumpTaskCmd(executionId, activityId));
感谢临远大神提供的demo
demo github地址：https://github.com/xuhuisheng/activiti-demo/
 * 
 * @author rgz
 *
 */
public class JumpTaskCmd implements Command<Comment> {
	
	protected String executionId;
	protected String activityId;
	
	public JumpTaskCmd(String executionId, String activityId) {
		this.executionId = executionId;
		this.activityId = activityId;
	}

	@Override
	public Comment execute(CommandContext commandContext) {
		for (TaskEntity taskEntity : Context.getCommandContext().getTaskEntityManager().findTasksByExecutionId(executionId)) {
            Context.getCommandContext().getTaskEntityManager().deleteTask(taskEntity, "jump", false);
        }
        ExecutionEntity executionEntity = Context.getCommandContext().getExecutionEntityManager().findExecutionById(executionId);
        ProcessDefinitionImpl processDefinition = executionEntity.getProcessDefinition();
        ActivityImpl activity = processDefinition.findActivity(activityId);
        executionEntity.executeActivity(activity);
        return null;
    }

}
