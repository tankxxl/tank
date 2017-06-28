package com.thinkgem.jeesite.test;

import com.thinkgem.jeesite.modules.demo.dao.OrdersCustomMapper;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.IOException;



public class ActivitiTest extends BaseTestCase {

//	@Resource
//	private RepositoryService repositoryService;

//	@Resource
//	private RuntimeService runtimeService;

//	@Resource
//	private TaskService taskService;

//	@Autowired
//	OrdersCustomMapper ordersCustomMapper;

	@Test
	public void testStringBuilder() {
		String temp = "";
		String[] array = temp.split(",");
		for (int i = 0; i < array.length; i++) {
			System.out.println("array[" + i + "]=" + array[i]);
		}
		System.out.println("=====");

		String temp1 = "";
		String[] array1 = temp1.split(",");
		for (int i = 0; i < array1.length; i++) {
			System.out.println("array1[" + i + "]=" + array1[i]);
		}
		System.out.println("=====");

		temp = "1,";
		System.out.println(temp.substring(0, temp.length() - 1));
	}

	@Test
	public void testDeploy() throws IOException {

//		Assert.assertNotNull(taskService);
//		System.out.println("activiti");

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





	@Override
	protected void beforeTest() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void afterTest() throws Exception {
		// TODO Auto-generated method stub

	}



}
