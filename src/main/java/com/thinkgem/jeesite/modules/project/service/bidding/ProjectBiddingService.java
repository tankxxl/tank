/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.bidding;

import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
import com.thinkgem.jeesite.modules.project.dao.bidding.ProjectBiddingDao;
import com.thinkgem.jeesite.modules.project.entity.bidding.ProjectBidding;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
import com.thinkgem.jeesite.modules.project.utils.MyDictUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sound.midi.Soundbank;
import java.util.HashMap;
import java.util.Map;

/**
 * 项目投标Service
 * @author jicdata
 * @version 2016-03-08
 */
@Service
@Transactional(readOnly = true)
public class ProjectBiddingService extends JicActService<ProjectBiddingDao, ProjectBidding> {

	@Autowired
	private ProjectApplyExternalService applyService;
	
	@Override
	public ProjectBidding get(String id) {
		ProjectBidding bidding = super.get(id);
		if (bidding == null) {
			bidding = new ProjectBidding();
		}
		return bidding;
	}

	@Override
	public void setupVariable(ProjectBidding projectBidding, Map<String, Object> vars) {

		// 在立项审批表中也要保存一下是否有外包
		ProjectApplyExternal external = applyService.get(projectBidding.getApply().getId());
		projectBidding.setApply(external);
		external.setOutsourcing(projectBidding.getOutsourcing());
		applyService.save(external);
		// 设置流程变量
		myProcVariable(projectBidding, vars);
	}

	private void myProcVariable(ProjectBidding projectBidding, Map<String, Object> vars) {
		vars.put(ActUtils.VAR_PRJ_ID, projectBidding.getApply().getId());

		vars.put(ActUtils.VAR_PRJ_TYPE, projectBidding.getApply().getCategory());

		// 临时用一下，以后会删除
		vars.put(ActUtils.VAR_PASS, "1");

		vars.put(ActUtils.VAR_TITLE, projectBidding.getApply().getProjectName());
		if ("03".equals(projectBidding.getApply().getCategory()) ) {
			// 分支上使用，没在节点上使用
			vars.put(ActUtils.VAR_TYPE, "2");
		} else {
			vars.put(ActUtils.VAR_TYPE, "1");
		}

		boolean isBossAudit = MyDictUtils.isBossAudit(projectBidding.getAmount(),
				projectBidding.getProfitMargin());
		if (isBossAudit) { // 需要总经理审批
			// 节点上使用
			vars.put(ActUtils.VAR_SKIP_BOSS, "0");
		} else {
			vars.put(ActUtils.VAR_SKIP_BOSS, "1");
		}

		if ("1".equals(projectBidding.getApply().getOutsourcing()) ) {
			// 因为流程图上分支判断在前，故节点上的skip_hr暂时不用。
			vars.put("hr", "1");
			vars.put(ActUtils.VAR_SKIP_HR, "0");
		} else {
			vars.put("hr", "0");
			vars.put(ActUtils.VAR_SKIP_HR, "1");
		}
		// 20190409事业部
		vars.put(ActUtils.VAR_AMOUNT, Double.valueOf(projectBidding.getAmount()));
		vars.put(ActUtils.VAR_OFFICE_CODE, projectBidding.getApply().getSaler().getOffice().getCode());
	}

	// 审批过程中
	@Override
	public void processAudit(ProjectBidding projectBidding, Map<String, Object> vars) {
		// 对不同环节的业务逻辑进行操作
		String taskDefKey = projectBidding.getAct().getTaskDefKey();
		if (UserTaskType.UT_BUSINESS_LEADER.equals(taskDefKey)) {
		} else if (UserTaskType.UT_OWNER.equals(taskDefKey)) {
			// 驳回到发起人节点后，他可以修改所有的字段，所以重新设置一下流程变量
			fillApply(projectBidding);
			myProcVariable(projectBidding, vars);
		} else if (UserTaskType.UT_PRE_SALES_ENGINEER.equalsIgnoreCase(taskDefKey)) {
			// todo 临时用一下，现在在此节点有几个遗留的表单需要审批
			fillApply(projectBidding);
			myProcVariable(projectBidding, vars);
		}

	}

	public void fillApply(ProjectBidding projectBidding) {
		ProjectApplyExternal apply = applyService.get(projectBidding.getApply().getId());
		projectBidding.setApply(apply);
	}

}