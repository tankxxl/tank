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
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoiceItem;
import com.thinkgem.jeesite.modules.project.utils.MyDictUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.context.SaveContextOnUpdateOrErrorResponseWrapper;
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
public class ProjectFinishApprovalService
		extends JicActService<ProjectFinishApprovalDao, ProjectFinishApproval> {

	 @Autowired
	 private ProjectApplyExternalService applyService;

	@Override
	public ProjectFinishApproval get(String id) {
		ProjectFinishApproval finishApproval = super.get(id);
		if (finishApproval == null)
			return new ProjectFinishApproval();
		return finishApproval;
	}

	// 流程启动之前，设置map
	@Override
	public void setupVariable(ProjectFinishApproval projectFinishApproval, Map<String, Object> vars) {

		fillApply(projectFinishApproval);

		vars.put(ActUtils.VAR_PRJ_ID, projectFinishApproval.getApply().getId());
		vars.put(ActUtils.VAR_PRJ_TYPE, projectFinishApproval.getApply().getCategory());
		vars.put(ActUtils.VAR_TITLE, projectFinishApproval.getApply().getProjectName());

		// todo
		vars.put(ActUtils.VAR_PASS, "1");

		if ("03".equals(projectFinishApproval.getApply().getCategory()) ) {
			vars.put(ActUtils.VAR_TYPE, "2");
		} else {
			vars.put(ActUtils.VAR_TYPE, "1");
		}
	}

	// 审批过程中
	@Override
	public void processAudit(ProjectFinishApproval projectFinishApproval, Map<String, Object> vars) {
		super.processAudit(projectFinishApproval, vars);
	}

	private void fillApply(ProjectFinishApproval projectFinishApproval) {
		ProjectApplyExternal apply = applyService.get(projectFinishApproval.getApply().getId());
		projectFinishApproval.setApply(apply);
	}
}