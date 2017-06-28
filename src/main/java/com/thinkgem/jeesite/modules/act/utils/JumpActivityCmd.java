package com.thinkgem.jeesite.modules.act.utils;


import org.activiti.engine.ManagementService;


import org.activiti.engine.ProcessEngine;


import org.activiti.engine.ProcessEngines;


import org.activiti.engine.impl.interceptor.Command;


import org.activiti.engine.impl.interceptor.CommandContext;


import org.activiti.engine.impl.persistence.entity.ExecutionEntity;


import org.activiti.engine.impl.pvm.process.ActivityImpl;


import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;


import org.junit.Test;

/**
 * 流程中任意跳跃
 *
 * Created by rgz on 04/05/2017.
 */
public class JumpActivityCmd implements Command<Object> {
    private String activityId;
    private String processInstanceId;
    private String jumpOrigin;
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

    public JumpActivityCmd() {}

    public JumpActivityCmd(String processInstanceId, String activityId) {
        this(processInstanceId, activityId, "jump");
    }

    /**
     *
     * @param processInstanceId 流程实例ID
     * @param activityId 跳转目标环节key
     * @param jumpOrigin 自动完成环节的意见
     */
    public JumpActivityCmd(String processInstanceId, String activityId, String jumpOrigin) {
        this.processInstanceId = processInstanceId;
        this.activityId = activityId;
        this.jumpOrigin = jumpOrigin;
    }

    @Override
    public Object execute(CommandContext commandContext) {
        ExecutionEntity executionEntity = commandContext.getExecutionEntityManager()
                .findExecutionById(processInstanceId);
        executionEntity.destroyScope(jumpOrigin);
        ProcessDefinitionImpl processDefinition = executionEntity.getProcessDefinition();
        ActivityImpl activity = processDefinition.findActivity(activityId);
        executionEntity.executeActivity(activity);
        return executionEntity;
    }


    @Test
    public void testName() {
        ManagementService managementService = processEngine.getManagementService();

        // 第一个参数 流程实例id
        // 第二个参数 跳转目标环节key
        // 第三个参数 自动完成环节的意见
        Command<Object> cmdCommand = new JumpActivityCmd("1201", "usertask2", "强制跳转");
        managementService.executeCommand(cmdCommand);
    }
}
