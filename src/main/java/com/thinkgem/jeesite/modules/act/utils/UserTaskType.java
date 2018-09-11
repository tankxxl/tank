package com.thinkgem.jeesite.modules.act.utils;

/**
 * 流程节点类型
 * @author rgz
 *
 */
public class UserTaskType {
	
	/**
	 * 注：此处常量值：
	 * 1、要与流程图*.bpmn中的节点id一致。
	 * 
	 * 流程图*.bpmn中节点的Candidate Groups名称要与系统中设置的角色名称一致。
	 * 
	 * 目前任务节点id的名称与Candidate Groups名称与系统中角色名称是一致的。
	 */
	// 申请人节点
	public static final String UT_OWNER = "usertask_owner";
	// 项目专员节点
	public static final String UT_SPECIALIST = "usertask_specialist";
	// 项目专员节点
	public static final String UT_SPECIALIST_CONSULT = "usertask_specialist_consultation";
	
	// 项目经理节点--结项流程
	public static final String UT_PROJECT_MANAGER = "usertask_project_manager";
	
	// 业务部负责人节点--申请人直接领导
	public static final String UT_BUSINESS_LEADER = "usertask_business_leader";
	// 投标流程中，售前工程师节点
	public static final String UT_PRE_SALES_ENGINEER = "usertask_pre_sales_engineer";
	// 解决方案部节点
	public static final String UT_SOLUTION_LEADER = "usertask_solution_leader";
	// 服务支付部节点
	public static final String UT_SERVICE_DELIVERY_LEADER = "usertask_service_delivery_leader";
	// 软件开发部节点
	public static final String UT_SOFTWARE_DEVELOPMENT_LEADER = "usertask_software_development_leader";
	// 项目管理部节点
	public static final String UT_PROJECT_MANAGEMENT_LEADER = "usertask_project_management_leader";
	// 合同流程中，法务节点
	public static final String UT_LAWER = "usertask_lawer";
	// 投标、合同流程中，商务部节点
	public static final String UT_COMMERCE_LEADER = "usertask_commerce_leader";

	public static final String UT_COMMERCE_SPECIALIST = "usertask_commerce_specialist";
	// 技术分管领导--按业务分两人
	public static final String UT_TECHNOLOGY_BOSS = "usertask_technology_boss";
	// 业务分管领导--按部门分--定义在部门属性上
	public static final String UT_BUSINESS_BOSS = "usertask_business_boss";
	// 总经理节点
	public static final String UT_BOSS = "usertask_boss";

}
