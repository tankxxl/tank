/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.finish;

import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
import com.thinkgem.jeesite.modules.project.dao.finish.ProjectFinishApprovalDao;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
import com.thinkgem.jeesite.modules.project.entity.finish.ProjectFinishApproval;
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoice;
import com.thinkgem.jeesite.modules.project.utils.MyDictUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 结项Service
 * @author jicdata
 * @version 2016-03-11
 */
@Service
@Transactional(readOnly = true)
public class ProjectFinishApprovalService extends JicActService<ProjectFinishApprovalDao, ProjectFinishApproval> {

	 @Autowired
	 private ProjectApplyExternalService applyService;

	// 流程启动之前，设置map
	@Override
	public void setupVariable(ProjectFinishApproval projectFinishApproval, Map<String, Object> vars) {

		fillApply(projectFinishApproval);

		vars.put(ActUtils.VAR_PRJ_ID, projectFinishApproval.getApply().getId());
		vars.put(ActUtils.VAR_PRJ_TYPE, projectFinishApproval.getApply().getCategory());
		vars.put(ActUtils.VAR_TITLE, projectFinishApproval.getApply().getProjectName());

		// 根据项目类型及最低毛利率判断
		boolean isBossAudit = MyDictUtils.isJxBossAudit(projectFinishApproval.getApply().getCategory(),
				projectFinishApproval.getApply().getEstimatedGrossProfitMargin());

		if (isBossAudit) { // 需要总经理审批
			vars.put(ActUtils.VAR_SKIP_BOSS, "0");
		} else {
			vars.put(ActUtils.VAR_SKIP_BOSS, "1");
		}
	}

	// 审批过程中
	@Override
	public void processAudit(ProjectFinishApproval projectFinishApproval, Map<String, Object> vars) {
		// 对不同环节的业务逻辑进行操作
		String taskDefKey = projectFinishApproval.getAct().getTaskDefKey();
		if (UserTaskType.UT_BUSINESS_LEADER.equals(taskDefKey)) {
			System.out.println("");
		} else if (UserTaskType.UT_OWNER.equals(taskDefKey)) {
			// 驳回到发起人节点后，他可以修改所有的字段，所以重新设置一下流程变量
			setupVariable(projectFinishApproval, vars);
		} else if ("".equalsIgnoreCase(taskDefKey)) {
		}

	}

	public void fillApply(ProjectFinishApproval projectFinishApproval) {
		ProjectApplyExternal apply = applyService.get(projectFinishApproval.getApply().getId());
		projectFinishApproval.setApply(apply);
	}
}