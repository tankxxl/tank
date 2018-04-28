/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.finish;

import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.project.dao.finish.ProjectFinishApprovalDao;
import com.thinkgem.jeesite.modules.project.entity.finish.ProjectFinishApproval;
import com.thinkgem.jeesite.modules.project.utils.MyDictUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 结项Service
 * @author jicdata
 * @version 2016-03-11
 */
@Service
@Transactional(readOnly = true)
public class ProjectFinishApprovalService extends JicActService<ProjectFinishApprovalDao, ProjectFinishApproval> {

	@Override
	public void setupVariable(ProjectFinishApproval finish, Map<String, Object> vars) {
		vars.put(ActUtils.VAR_PRJ_ID, finish.getApply().getId());

		vars.put(ActUtils.VAR_PRJ_TYPE, finish.getApply().getCategory());

		vars.put(ActUtils.VAR_TITLE, finish.getApply().getProjectName());
		if ("03".equals(finish.getApply().getCategory()) ) {
			vars.put(ActUtils.VAR_TYPE, "2");
		} else {
			vars.put(ActUtils.VAR_TYPE, "1");
		}

		boolean isBossAudit = MyDictUtils.isBossAudit(finish.getApply().getEstimatedContractAmount(),
				finish.getApply().getEstimatedGrossProfitMargin());
		if (isBossAudit) {
			vars.put(ActUtils.VAR_SKIP_BOSS, "0");
		} else {
			vars.put(ActUtils.VAR_SKIP_BOSS, "1");
		}
	}
}