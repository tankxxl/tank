/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.biddingArchive;

import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
import com.thinkgem.jeesite.modules.project.dao.biddingArchive.BiddingArchiveDao;
import com.thinkgem.jeesite.modules.project.entity.bidding.ProjectBidding;
import com.thinkgem.jeesite.modules.project.entity.biddingArchive.BiddingArchive;
import com.thinkgem.jeesite.modules.project.service.bidding.ProjectBiddingService;
import com.thinkgem.jeesite.modules.project.utils.MyDictUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 项目投标备案Service
 * @author jicdata
 * @version 2016-03-08
 */
@Service
@Transactional(readOnly = true)
public class BiddingArchiveService extends JicActService<BiddingArchiveDao, BiddingArchive> {
	
	@Autowired
	private ProjectApplyExternalService projectApplyExternalService;

	@Autowired
	private ProjectBiddingService biddingService;
	
	// @Override
	// public ProjectBidding get(String id) {
	// 	return super.get(id);
	// }
	
	// @Override
	// public List<ProjectBidding> findList(ProjectBidding projectBidding) {
	// 	return super.findList(projectBidding);
	// }
	
	// @Override
	// public Page<ProjectBidding> findPage(Page<ProjectBidding> page, ProjectBidding projectBidding) {
	// 	return super.findPage(page, projectBidding);
	// }

	/**
	 * 保存并结束流程
	 * @param archive
	 */
	// @Transactional(readOnly = false)
	// public void saveFinishProcess(BiddingArchive archive) {
	// 	String procInsId = saveLaunch(archive);
	// 	// 结束流程
	// 	endProcess(procInsId);
	// }

	@Override
	@Transactional(readOnly = false)
	public void setupVariable(BiddingArchive archive, Map<String, Object> varMap) {

		// 在此保存投标信息表
		ProjectBidding bidding = biddingService.get(archive.getBidding().getId());
		bidding.setArchiveFlag("1");
		biddingService.save(bidding);

		varMap.put(ActUtils.VAR_PRJ_ID, archive.getApply().getId());

		varMap.put(ActUtils.VAR_TITLE, archive.getApply().getProjectName());
		if ("03".equals(archive.getApply().getCategory()) ) {
			// 分支上使用，没在节点上使用
			varMap.put(ActUtils.VAR_TYPE, "2");
		} else {
			varMap.put(ActUtils.VAR_TYPE, "1");
		}

		boolean isBossAudit = MyDictUtils.isBossAudit(archive.getAmount(),
				archive.getProfitMargin());
		if (isBossAudit) { // 需要总经理审批
			// 节点上使用
			varMap.put(ActUtils.VAR_SKIP_BOSS, "0");
		} else {
			varMap.put(ActUtils.VAR_SKIP_BOSS, "1");
		}

		varMap.put(ActUtils.VAR_END, archive.isBossAudit());
	}

	/**
	 * 保存表单数据，并启动流程
	 *
	 * 申请人发起流程，申请人重新发起流程入口
	 * 在form界面
	 *
	 * @param archive
	 */
	// @Transactional(readOnly = false)
	// public String saveLaunch(BiddingArchive archive) {
    //
     //    // 在立项审批表中也要保存一下是否有外包
     //    ProjectApplyExternal external = projectApplyExternalService.get(archive.getApply().getId());
     //    external.setOutsourcing(archive.getOutsourcing());
     //    projectApplyExternalService.save(external);
    //
	// 	if (archive.getIsNewRecord()) {
	// 		// 启动流程的时候，把业务数据放到流程变量里
	// 		Map<String, Object> varMap = new HashMap<String, Object>();
	// 		varMap.put(ActUtils.VAR_PRJ_ID, archive.getApply().getId());
    //
	// 		varMap.put(ActUtils.VAR_PRJ_TYPE, archive.getApply().getCategory());
    //
	// 		varMap.put(ActUtils.VAR_TITLE, archive.getApply().getProjectName());
	// 		if ("03".equals(archive.getApply().getCategory()) ) {
	// 			// 分支上使用，没在节点上使用
	// 			varMap.put(ActUtils.VAR_TYPE, "2");
	// 		} else {
	// 			varMap.put(ActUtils.VAR_TYPE, "1");
	// 		}
    //
	// 		boolean isBossAudit = MyDictUtils.isBossAudit(archive.getAmount(),
	// 				archive.getProfitMargin());
	// 		if (isBossAudit) { // 需要总经理审批
	// 			// 节点上使用
	// 			varMap.put(ActUtils.VAR_SKIP_BOSS, "0");
	// 		} else {
	// 			varMap.put(ActUtils.VAR_SKIP_BOSS, "1");
	// 		}
    //
	// 		varMap.put(ActUtils.VAR_END, archive.isBossAudit());
    //
	// 		// 20160628下午,张雪口头提需求, 05类项目根据是否有外包选项,来决定流程是否走到人力
	// 		// 其它所有类型项目,不管是否选择有外包,流程上必须过人力.
	// 		// 涉及的流程包括:投标和合同
	// 		if ("05".equals(archive.getApply().getCategory()) ) {
	// 			// 因为流程图上分支判断在前，故节点上的skip_hr暂时不用。
	// 			varMap.put("hr", "0");
	// 			varMap.put(ActUtils.VAR_SKIP_HR, "1");
	// 		} else {
	// 			varMap.put("hr", "1");
	// 			varMap.put(ActUtils.VAR_SKIP_HR, "0");
	// 		}
    //
	// 		return launch(archive, varMap);
	// 	} else { // 把驳回到申请人(重新修改业务表单，重新发起流程、销毁流程)也当成一个特殊的审批节点
	// 		// 只要不是启动流程，其它任意节点的跳转都当成节点审批
	// 		saveAudit(archive);
	// 		return null;
	// 	}
	// }

}