package com.thinkgem.jeesite.test;

import com.thinkgem.jeesite.modules.project.service.approval.AssigneeService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivitiTest3 extends BaseTestCase {

//	@Resource
//	private RepositoryService repositoryService;

//	@Resource
//	private RuntimeService runtimeService;

//	@Resource
//	private TaskService taskService;

	String deploymentId = null;

	@Resource
	AssigneeService assigneeService;

	@Test
	public void testAssigneeService() {
		System.out.println("worked!");
		String loginName = assigneeService.findMarketBoss("");
		// System.out.println(loginName);
	}


	@Test
	public void testMain() {

		System.out.println("worked!");
		// 部署
//		deploymentId = repositoryService
//                .createDeployment()
//	            .addClasspathResource("act/test/TestSkipExp.bpmn")
//	            .deploy().getId();

       System.out.println(deploymentId);
       // start process
       Map<String, Object> vars = new HashMap<String, Object>();
       vars.put("apply", "tankxxl");
       vars.put("_ACTIVITI_SKIP_EXPRESSION_ENABLED", true);
//        vars.put("skip", 1);

//        ProcessInstance pi = runtimeService.startProcessInstanceByKey("TestSkipExp", vars);
//        System.out.println(pi.getProcessDefinitionKey());

       // query
//        Task task = taskService.createTaskQuery().taskAssignee("tankxxl").singleResult();
//        System.out.println(task.getName());  // task1

       Map<String, Object> vars1 = new HashMap<String, Object>();
       vars.put("apply", "tankxxl");
       vars.put("_ACTIVITI_SKIP_EXPRESSION_ENABLED", true);
       vars.put("skip", 1);
//        taskService.complete(task.getId(), vars1);



//        task = taskService.createTaskQuery().taskAssignee("tankxxl").singleResult();
//        System.out.println(task.getName());  // task2
//        taskService.complete(task.getId());

//        task = taskService.createTaskQuery().taskAssignee("tankxxl").singleResult();
//        System.out.println(task.getName());  // task3

//        assertEquals(true, pi.isEnded());
//
//		// 删除
//		repositoryService.deleteDeployment(deploymentId, true);
	}

	@Test
   public void claimAndCompleteHumanTask() throws Exception {
//	    List<Task> tasks = taskService.createTaskQuery()
//                .processDefinitionKey("TestSkipExp")
//                .taskDefinitionKey("HumanTaskExample")
//                .list();
//	    for (Task task : tasks) {
//	        taskService.claim(task.getId(), "DJ");
//	        Map<String, Object> variableMap = new HashMap<String, Object>();
//	        variableMap.put("HumanTaskCompletedBy", "DJ");
//	        taskService.complete(task.getId(), variableMap);
//        }
   }
	@Override
	protected void beforeTest() throws Exception {
		// TODO Auto-generated method stub

	}
	@Override
	protected void afterTest() throws Exception {
		// TODO Auto-generated method stub

	}


}
