package com.thinkgem.jeesite.test;

import com.thinkgem.jeesite.modules.test.activiti.LogCallsToScriptTask;
import com.thinkgem.jeesite.modules.test.activiti.PeopleService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.zip.ZipInputStream;


public class ActivitiTest extends BaseTestCase {

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private ProcessEngine processEngine;

	@Autowired
	private PeopleService peopleService;

//	@Resource
//	private TaskService taskService;

//	@Autowired
//	OrdersCustomMapper ordersCustomMapper;


	@Test
	public void testDeploy() throws IOException {

		// InputStream is = readXmlFile();
		// Assert.assertNotNull(is);
		//发布流程
		// org.activiti.engine.repository.Deployment deployment = repositoryService.createDeployment().addInputStream("bpmn20.xml", is).name("holidayRequest").deploy();

		Deployment deployment = repositoryService.createDeployment()
				.addClasspathResource("MyMultiInstanceProcess.bpmn")
				.name("流程定义")//添加部署名称
				.deploy();
		System.out.println("发布的ID：" + deployment.getId());


		// 查询流程定义
		ProcessDefinition processDefinition = repositoryService
				.createProcessDefinitionQuery()
				.deploymentId(deployment.getId())
				.singleResult();

		// CN = 1 3, CA = 2 4 , US = 3, 5, CN4 = 4 6
		String location = "CN4";

		// 打印两次，called和returning
		// int numPeopleAtLocation = peopleService.getPeopleByLocation(location).size();

		HashMap<String, Object> vars = new HashMap<String, Object>();
		vars.put("location", location);

		// 启动流程
		processEngine.getRuntimeService().startProcessInstanceByKey("myMultiInstanceProcess", vars);

		int numPeopleLoggedInScript = LogCallsToScriptTask.peopleLogged.size();
		System.out.println("numPeopleLoggedInScript=" + numPeopleLoggedInScript);
		// assertEquals("Scripts should have logged "
		// 				+ numPeopleAtLocation
		// 				+"times.",
		// 		numPeopleAtLocation,
		// 		numPeopleLoggedInScript);

		System.out.println(PeopleService.callCount.get());
		// assertEquals("PeopleService called more times than expected for location",
		// 		1, PeopleService.callCount.get());

		// 删除流程定义
		processEngine.getRepositoryService()
				.deleteDeployment(deployment.getId(), true);

		System.out.println("删除成功");
	}

	public InputStream readXmlFile() throws IOException {
		String filePath = "holidayRequest.bpmn";
		return Class.class.getClass().getResource("/" + filePath).openStream();
	}


	/*
     *部署流程定义 （从classpath）
     */
	@Test
	public void deploymentProcessDefinition_classpath(){
		Deployment deployment = processEngine.getRepositoryService()//与流程定义和部署对象相关的Service
				.createDeployment()//创建一个部署对象
				.name("流程定义")//添加部署名称
				.addClasspathResource("diagrams/HelloWorld.bpmn")//从classpath的资源中加载，一次只能加载一个文件
				.addClasspathResource("diagrams/HelloWorld.png")
				.deploy();//完成部署
		System.out.println("部署ID："+deployment.getId());
		System.out.println("部署名称:"+deployment.getName());

	}

	/*
     *部署流程定义 （从zip）
     */
	@Test
	public void deploymentProcessDefinition_zip(){
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("diagrams/HelloWorld.zip");
		ZipInputStream zipInputStream = new ZipInputStream(in);
		Deployment deployment = processEngine.getRepositoryService()//与流程定义和部署对象相关的Service
				.createDeployment()//创建一个部署对象
				.name("流程定义")//添加部署名称
				.addZipInputStream(zipInputStream)//完成zip文件的部署
				.deploy();//完成部署
		System.out.println("部署ID："+deployment.getId());
		System.out.println("部署名称:"+deployment.getName());
	}

	/*
     * 删除流程定义
     */
	@Test
	public void deleteProcessDefinition(){
		//使用部署ID，完成删除
		String deploymentId = "1";
        /*
         * 不带级联的删除
         * 只能删除没有启动的流程，如果流程启动，就会抛出异常
         */
//        processEngine.getRepositoryService()//与流程定义和部署对象相关的Service
//                        .deleteDeployment(deploymentId);
        /*
         * 能级联的删除
         * 能删除启动的流程，会删除和当前规则相关的所有信息，正在执行的信息，也包括历史信息
         */
		processEngine.getRepositoryService()//与流程定义和部署对象相关的Service
				.deleteDeployment(deploymentId, true);

		System.out.println("删除成功");
	}

	@Test
	public void testMybatis() {
//		List<OrdersCustom> ordersCustoms = ordersCustomMapper.findOrdersUser();
//		Iterator<OrdersCustom> iterator = ordersCustoms.iterator();
//		while (iterator.hasNext()) {
//			OrdersCustom ordersCustom = iterator.next();
//			System.out.println("username= " + ordersCustom.getUsername());
//		}
	}

	@Test
	public void testMyBatisResultMap() {
//		List<Orders> orders = ordersCustomMapper.findOrdersUserResultMap();
//		Iterator<Orders> iterator = orders.iterator();
//		while (iterator.hasNext()) {
//			Orders order = iterator.next();
//			System.out.println("username= " + order.getUser().getUsername());
//		}
	}

	@Test
	public void testMyBatis1() {
//		List<Orders> orders = ordersCustomMapper.findOrdersUser1();
//		Iterator<Orders> iterator = orders.iterator();
//		while (iterator.hasNext()) {
//			Orders order = iterator.next();
//			System.out.println("username= " + order.getUser().getUsername());
//		}
	}

	@Test
	public void testMyBatisOrderDetail() {
//		List<Orders> orders = ordersCustomMapper.findOrdersAndOrderDetailResultMap();
//		Iterator<Orders> iterator = orders.iterator();
//		while (iterator.hasNext()) {
//			Orders order = iterator.next();
//			System.out.println("createTime=" + order.getCreateTime() + ",username=" + order.getUser().getUsername());
//			List<OrderDetail> orderDetails = order.getOrderdetails();
//
//			Iterator<OrderDetail> iterator2 = orderDetails.iterator();
//			while(iterator2.hasNext()) {
//				OrderDetail detail = iterator2.next();
//				System.out.println("ItemsId= " + detail.getItemsId());
//			}
//
//		}
	}

	@Test
	public void testMyBatisUserAndItemsResultMap() {
//		List<User> users = ordersCustomMapper.findUserAndItemsResultMap();
//		System.out.println(users);
//		Iterator<Orders> iterator = orders.iterator();
//		while (iterator.hasNext()) {
//			Orders order = iterator.next();
//			System.out.println("createTime=" + order.getCreateTime() + ",username=" + order.getUser().getUsername());
//			List<OrderDetail> orderDetails = order.getOrderdetails();
//
//			Iterator<OrderDetail> iterator2 = orderDetails.iterator();
//			while(iterator2.hasNext()) {
//				OrderDetail detail = iterator2.next();
//				System.out.println("ItemsId= " + detail.getItemsId());
//			}
//
//		}
	}

	@Test
	public void testFindOrdersUserLazyLoading() {
//		List<Orders> orders = ordersCustomMapper.findOrdersUserLazyLoading();
//		Iterator<Orders> iterator = orders.iterator();
//		while (iterator.hasNext()) {
//			Orders order = iterator.next();
//			System.out.println("createTime=" + order.getCreateTime() + ",username=" + order.getUser().getUsername());
//		}
	}


	// or

	@Rule
	ActivitiRule activitiRule = new ActivitiRule();

	@Test
	@org.activiti.engine.test.Deployment(resources = {"org/myGroup/my-process.bpmn20.xml"})
	public void testAnnotationDeploy() {
		ProcessInstance processInstance = activitiRule
				.getRuntimeService()
				.startProcessInstanceByKey("my-process");
		System.out.println(processInstance);

		Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
		System.out.println(task.getName());
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
