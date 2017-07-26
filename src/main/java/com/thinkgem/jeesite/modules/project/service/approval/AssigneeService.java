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
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

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

//	public List<String> findDeptLeaders(String employee);
	
	// 设置流程节点中的-业务部负责人审批节点-定位到人，查找员工的直接领导loginName
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
	@Transactional(readOnly = true)
	public String findBusiBoss(String loginName) {

		User user = UserUtils.getByLoginName(loginName);
		// 把部门的辅负责人当成业务分管领导
		String busiLeader = null;
		try {
			busiLeader = user.getOffice().getDeputyPerson().getLoginName();
			if (StringUtils.isEmpty(busiLeader)) {
				busiLeader = "thinkgem";
			}
		} catch (Exception e) {
			busiLeader = "thinkgem";
		}
		return busiLeader;
	}
	
	// 设置流程节点中的-技术部分管领导审批节点-定位到角色，技术分管领导角色根据项目类型分两种，一种是软件类项目，一种是集成类项目。
    // 技术分管领导按项目类型指定
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

    // bj used only.
	public String findMarketBoss(String apply) {

		Office office = new Office();
		office.setName(apply);
		office.setName("市场营销中心");


		String boss = null;
		try {
			office = officeService.getByName(office);
			boss = office.getDeputyPerson().getLoginName();
			if (StringUtils.isEmpty(boss)) {
				boss = "thinkgem";
			}
		} catch (Exception e) {
			boss = "thinkgem";
		}
		return boss;
	}

	// bj used only.
	// 返回职能部门(办公室 财务部 运营管理部)所对应的三个审批角色
	public List<String> findFuncDeptRole(String apply) {

		String[] role = {
				"usertask_finance_leader",
				"usertask_operation_management_leader",
				"usertask_operation_management",
				"usertask_office_leader",
				"usertask_lawer"};

		List<String> list = Arrays.asList(role);

		return list;
	}

	public boolean isComplete(ActivityExecution execution) {
		String nodeId = execution.getActivity().getId();
		ExecutionEntity executionEntity = (ExecutionEntity) execution;

		execution.getVariable(ActUtils.VAR_OBJ_ID);
		executionEntity.getProcessDefinitionId();

		// execution.setVariable(ActUtils.VAR_OBJ_ID, "");

		String roleValue = executionEntity.getVariableInstance("role").getTextValue();

		if ("usertask_operation_management_leader".equalsIgnoreCase(roleValue)) {
			return true;
		}
		return false;
	}

	// sd used only.
	public String findEngineerRole(String apply) {

		User user = UserUtils.getByLoginName(apply);
		String engineerRole = null;
		try {
			engineerRole = user.getOffice().getAddress();
		} catch (Exception e) {
			engineerRole = "system";
		}
		if (StringUtils.isEmpty(engineerRole)) {
			logger.info(apply + "-此用户所在部门没有对应的联系地址信息。");
			engineerRole = "system";
		}
		// 把部门地址作为任务的办理者角色
		return engineerRole;
	}




}