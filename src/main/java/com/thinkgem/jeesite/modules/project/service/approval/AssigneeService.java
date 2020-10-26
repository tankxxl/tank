/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.approval;

import com.thinkgem.jeesite.common.annotation.Loggable;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.sys.entity.Office;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.OfficeService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.activiti.engine.runtime.Execution;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 根据loginName得到相应的节点审批人
 * 当使用表达式设置candidateUsers、Groups时，这些方法查找出来的返回值必须是String或List<String>
 * @author jicdata
 * @version 2016-02-22
 */
@Service
@Transactional(readOnly = true)
public class AssigneeService extends BaseService {

    @Loggable
    Logger logger;

    @Autowired
	OfficeService officeService;

    public String findTechRole(ActivityExecution execution) {
    	String nodeId = execution.getActivity().getId();
		ExecutionEntity executionEntity = (ExecutionEntity) execution;
		execution.getVariable(ActUtils.VAR_APPLY);
		String prjType = (String) execution.getVariable(ActUtils.VAR_PRJ_TYPE);
		String officeCode = execution.getVariable(ActUtils.VAR_OFFICE_CODE).toString();
		String procDefName = ((ExecutionEntity) execution).getProcessDefinitionKey();
		return getTechRole(officeCode, prjType, procDefName);
	}
	// 根据事业部、项目类型->技术负责人审批角色
	private String getTechRole(String officeCode, String prjType, String procDefName) {
    	String roleName = "system";
    	// 先判断事业部
    	if ("100000012".equals(officeCode)) { // 软件事业部
    		// 再判断项目类型
    		if ("03".equals(prjType)) { // 软件
				roleName = "ut_tech_software_03";
			} else {
    			roleName = "ut_tech_software_10";
			}
		} else if ("100000021".equals(officeCode)) { // 系统集成事业部
			if ("03".equals(prjType)) {
				roleName = "ut_tech_integration_03";
			} else {
				roleName = "ut_tech_integration_10";
			}
		} else if ("100000023".equals(officeCode)) { // 创新事业部
			if ("03".equals(prjType)) {
				roleName = "ut_tech_innovation_03";
			} else {
				roleName = "ut_tech_innovation_10";
			}
		} else if ("100000024".equals(officeCode)) { // 大客户事业部
			if ("03".equals(prjType)) {
				roleName = "ut_tech_vip_03";
			} else {
				roleName = "ut_tech_vip_10";
			}
		} else if ("100000025".equals(officeCode)) { // 四川分公司
			if ("03".equals(prjType)) {
				roleName = "ut_tech_sichuan_03";
			} else {
				roleName = "ut_tech_sichuan_10";
			}
		}
    	return roleName;
	}

	public boolean skipApplyByAmount(ActivityExecution execution) {
		String amountStr = execution.getVariable(ActUtils.VAR_AMOUNT).toString();
		double amount = Double.parseDouble(amountStr);
		int amountUnit = 1;
		double amountStd = 0;  // 金额标准，单位为W
		String prjType = (String) execution.getVariable(ActUtils.VAR_PRJ_TYPE);
		// 由于立项流程金额用的单位是W，投标流程金额用的单位是元。所以在此要判断是那个流程
		String procDefName = ((ExecutionEntity) execution).getProcessDefinitionKey();
		// 确定单位是：万或元
		if ("ProjectApplyExternal".equals(procDefName)) {
			amountUnit = 1;
			amountStd = Double.parseDouble(DictUtils.getDictValue("stage_apply", "jic_amount", "0"));
		} else if ("ProjectBidding".equals(procDefName)) {
			amountUnit = 10000; // 与前端界面单位要一致
			amountStd = Double.parseDouble(DictUtils.getDictValue("stage_bidding", "jic_amount", "0"));
		} else if ("ProjectContract".equalsIgnoreCase(procDefName)) {
			amountUnit = 10000;
			amountStd = Double.parseDouble(DictUtils.getDictValue("stage_contract", "jic_amount", "0"));
		} else {
			amountStd = 0;
		}

		// 确定金额免批上限，单位是W
		// if ("03".equals(prjType)) {
		// 	amountStd = 200;
		// } else {
		// 	amountStd = 500;
		// }

		boolean skipped = false;
		if (amount < amountStd * amountUnit) {
			skipped = true;
		} else {
			skipped = false;
		}
		return skipped;
	}
	
	// 设置流程节点中的-业务部/事业部负责人审批节点-定位到人，查找员工的直接领导loginName
	// 找申请人的部门负责人
	// 查找组织架构
	public String findLeader(String loginName) {
		User user = UserUtils.getByLoginName(loginName);
		String leader = null;
		try {
			leader = user.getOffice().getPrimaryPerson().getLoginName();
		} catch (Exception e) {
			leader = "thinkgem";
		}
        if (StringUtils.isEmpty(leader)) {
		    logger.info(loginName + "-此用户所在部门没有对应的主负责人。");
		    leader = "thinkgem";
        }
		// 把部门领导作为任务的办理者
		return leader;
	}
	
	// 设置流程节点中的-业务部分管领导审批节点-定位到人，当前申请人所在部门的业务分管领导loginName
    // 业务分管领导按部门进行指定
    // 根据登录用户的部门信息得到业务分管领导
    // 找申请人的部门分管领导
	// 查找组织构架 - 机构副负责人字段
	@Transactional(readOnly = true)
	public String findBusiBoss(String loginName) {

		User user = UserUtils.getByLoginName(loginName);
		// 把部门的辅负责人当成业务分管领导
		String busiLeader = null;
		try {
			busiLeader = user.getOffice().getDeputyPerson().getLoginName();
			if (StringUtils.isEmpty(busiLeader)) {
				logger.info(loginName + "-此用户所在部门没有设置对应的副负责人。");
				busiLeader = "thinkgem";
			}
		} catch (Exception e) {
			busiLeader = "thinkgem";
		}
		return busiLeader;
	}
	
	// 设置流程节点中的-技术部分管领导审批节点-定位到角色，技术分管领导角色根据项目类型分两种，一种是软件类项目，一种是集成类项目。
    // 技术分管领导按项目类型指定
    // 以前有两位技术副总，现在只有胡总一个了，所以流程中直接使用组名就可以了。
    // 此方法暂时在流程中没用
	public String findTechRoleByType(String prjType) {
		String role = null;
		// 在字典中，03代表软件项目
		if ("03".equals(prjType)) {
			role = "usertask_tech_boss";
		} else if ("05".equals(prjType)) {
			role = "usertask_busi_boss";
		} else {
			role = "usertask_busi_boss";
		}
		return role;
	}
	
	// 设置流程节点中的-解决方案审批节点-定位到角色，解决方案角色根据项目类型分两种，一种是软件类项目，一种是集成类项目。
    public String findSolutionRoleByType(String prjType) {
        String role = null;
        // 在字典中，03代表软件项目
        if ("03".equals(prjType)) {
            role = "usertask_solution_leader_software";
        } else if ("05".equals(prjType)) {
            role = "usertask_solution_leader_integration";
        } else {
            role = "usertask_solution_leader_integration";
        }
        return role;
    }

    // 设置流程节点中的-技术部负责人审批节点-定位到角色，技术中负责人角色根据项目类型分两种，一种是软件类项目，一种是集成类项目。
    // 技术部负责人按项目类型指定
    public String findTechLeaderRoleByType(String prjType) {
        String role = null;
        // 在字典中，03代表软件项目
        if ("03".equals(prjType)) {
            role = "usertask_software_development_leader";
        } else if ("05".equals(prjType)) {
            role = "usertask_service_delivery_leader";
        } else {
            role = "usertask_service_delivery_leader";
        }
        return role;
    }

    // bj used only. 找市场营销中心部门的分管
	public String findMarketBoss(String apply) {
		return findDeptBoss("市场营销中心");
	}

	// sd used only.
	// 根据申请人得以部门的技术工程师角色
	// 返回角色
	public String findEngineerRole(String apply) {

		User user = UserUtils.getByLoginName(apply);
		String engineerRole = null;
		try {
			// 部门联系地址字段存放部门技术工程师角色
			engineerRole = user.getOffice().getAddress();
		} catch (Exception e) {
			// 如果部门没有技术工程师角色，则返回系统角色
			engineerRole = "system";
		}
		if (StringUtils.isEmpty(engineerRole)) {
			logger.info(apply + "-此用户所在部门没有对应的联系地址信息。");
			engineerRole = "system";
		}
		// 把部门地址作为任务的办理者角色
		return engineerRole;
	}


	// jx used only
	// 可以通过部门属性查找到分管领导(某个人)
	// 也可以通过在系统中定义一个分管角色
	// 在流程图上指定审批人时，要相应修改 - 流程图中使用
	public String findCommerceBoss(String apply) {
		return findDeptBoss("商务部");
	}

	// jx 查找【商务部】负责人 - 流程图中使用
	public String findCommerceLeader(String apply) {
		return findDeptLeader("商务部");
	}

	public String findDeptBoss(String deptName) {
		Office office = new Office();
		office.setName(deptName);

		String boss = null;
		try {
			office = officeService.getByName(office);
			boss = office.getDeputyPerson().getLoginName();
		} catch (Exception e) {
			boss = "thinkgem";
		}

		if (StringUtils.isEmpty(boss)) {
			boss = "thinkgem";
		}
		return boss;
	}

	public String findDeptLeader(String deptName) {
		Office office = new Office();
		office.setName(deptName);

		String boss = null;
		try {
			office = officeService.getByName(office);
			office.getPrimaryPerson().getLoginName();
			boss = office.getDeputyPerson().getLoginName();
		} catch (Exception e) {
			boss = "thinkgem";
		}
		if (StringUtils.isEmpty(boss)) {
			boss = "thinkgem";
		}
		return boss;
	}

	public Office findDeptByName(String deptName) {
		Office office = new Office();
		office.setName(deptName);
		try {
			office = officeService.getByName(office);
		} catch (Exception e) {
		}
		return office;
	}



}