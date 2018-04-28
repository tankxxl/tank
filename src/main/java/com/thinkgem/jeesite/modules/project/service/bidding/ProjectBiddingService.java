/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.bidding;

import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.project.dao.bidding.ProjectBiddingDao;
import com.thinkgem.jeesite.modules.project.entity.bidding.ProjectBidding;
import com.thinkgem.jeesite.modules.project.utils.MyDictUtils;
import org.activiti.engine.ProcessEngine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 项目投标Service
 * @author jicdata
 * @version 2016-03-08
 */
@Service
@Transactional(readOnly = true)
public class ProjectBiddingService extends JicActService<ProjectBiddingDao, ProjectBidding> {

	@Override
	public ProjectBidding get(String id) {
		ProjectBidding bidding = super.get(id);
		return bidding == null ? new ProjectBidding() : bidding;
	}

	@Override
	public void setupVariable(ProjectBidding projectBidding, Map<String, Object> vars) {
		vars.put(ActUtils.VAR_PRJ_ID, projectBidding.getApply().getId());

		vars.put(ActUtils.VAR_PRJ_TYPE, projectBidding.getApply().getCategory());

		vars.put(ActUtils.VAR_TITLE, projectBidding.getApply().getProjectName());

		String pm = projectBidding.getProjectManager().getLoginName();
		pm = StringUtils.isBlank(pm) ? "thinkgem" : pm;
		vars.put(ActUtils.VAR_PM, pm);

		if ("03".equals(projectBidding.getApply().getCategory()) ) {
			vars.put(ActUtils.VAR_TYPE, "2");
		} else {
			vars.put(ActUtils.VAR_TYPE, "1");
		}

		boolean isBossAudit = MyDictUtils.isBossAudit(projectBidding.getAmount(),
				projectBidding.getProfitMargin());
		if (isBossAudit) {
			vars.put(ActUtils.VAR_SKIP_BOSS, "0");
		} else {
			vars.put(ActUtils.VAR_SKIP_BOSS, "1");
		}
	}

}