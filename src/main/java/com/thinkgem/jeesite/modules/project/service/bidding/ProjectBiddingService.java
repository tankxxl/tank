/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.bidding;

import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
import com.thinkgem.jeesite.modules.project.dao.bidding.ProjectBiddingDao;
import com.thinkgem.jeesite.modules.project.entity.bidding.ProjectBidding;
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

	// @Autowired
	// ActTaskService actTaskService;
	
	// @Autowired
	// MailService mailService;
	
	@Autowired
	private ProjectApplyExternalService projectApplyExternalService;
	
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
	 * @param projectBidding
	 */
	// @Transactional(readOnly = false)
	// public void saveFinishProcess(ProjectBidding projectBidding) {
	// 	String procInsId = saveLaunch(projectBidding);
	// 	// 结束流程
	// 	endProcess(procInsId);
	// }

	@Override
	public void setupVariable(ProjectBidding projectBidding, Map<String, Object> vars) {

		// 在立项审批表中也要保存一下是否有外包
		ProjectApplyExternal external = projectApplyExternalService.get(projectBidding.getApply().getId());
		external.setOutsourcing(projectBidding.getOutsourcing());
		projectApplyExternalService.save(external);
		// 设置流程变量
		vars.put(ActUtils.VAR_PRJ_ID, projectBidding.getApply().getId());

		vars.put(ActUtils.VAR_PRJ_TYPE, projectBidding.getApply().getCategory());

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

		// 20160628下午,张雪口头提需求, 05类项目根据是否有外包选项,来决定流程是否走到人力
		// 其它所有类型项目,不管是否选择有外包,流程上必须过人力.
		// 涉及的流程包括:投标和合同
		if ("05".equals(projectBidding.getApply().getCategory()) ) {
			// 因为流程图上分支判断在前，故节点上的skip_hr暂时不用。
			System.out.println();
			vars.put("hr", "0");
			vars.put(ActUtils.VAR_SKIP_HR, "1");
		} else {
			vars.put("hr", "1");
			vars.put(ActUtils.VAR_SKIP_HR, "0");
		}
	}

	/**
	 * 保存表单数据，并启动流程
	 *
	 * 申请人发起流程，申请人重新发起流程入口
	 * 在form界面
	 *
	 * @param projectBidding
	 */
// 	@Transactional(readOnly = false)
// 	public String saveLaunch(ProjectBidding projectBidding) {
//
//         // 在立项审批表中也要保存一下是否有外包
//         ProjectApplyExternal external = projectApplyExternalService.get(projectBidding.getApply().getId());
//         external.setOutsourcing(projectBidding.getOutsourcing());
//         projectApplyExternalService.save(external);
//
// 		if (projectBidding.getIsNewRecord()) {
// 			// 启动流程的时候，把业务数据放到流程变量里
// 			Map<String, Object> varMap = new HashMap<String, Object>();
// 			varMap.put(ActUtils.VAR_PRJ_ID, projectBidding.getApply().getId());
//
// 			varMap.put(ActUtils.VAR_PRJ_TYPE, projectBidding.getApply().getCategory());
//
// 			varMap.put(ActUtils.VAR_TITLE, projectBidding.getApply().getProjectName());
// 			if ("03".equals(projectBidding.getApply().getCategory()) ) {
// 				// 分支上使用，没在节点上使用
// 				varMap.put(ActUtils.VAR_TYPE, "2");
// 			} else {
// 				varMap.put(ActUtils.VAR_TYPE, "1");
// 			}
//
// 			boolean isBossAudit = MyDictUtils.isBossAudit(projectBidding.getAmount(),
// 					projectBidding.getProfitMargin());
// 			if (isBossAudit) { // 需要总经理审批
// 				// 节点上使用
// 				varMap.put(ActUtils.VAR_SKIP_BOSS, "0");
// 			} else {
// 				varMap.put(ActUtils.VAR_SKIP_BOSS, "1");
// 			}
//
// 			// 有外包的项目需要人力审批
// //			if ("1".equals(projectBidding.getOutsourcing())) {
// //				vars.put(ActUtils.VAR_HR_AUDIT, "1");
// //			} else {
// //				vars.put(ActUtils.VAR_HR_AUDIT, "0");
// //			}
//
// 			// 20160628下午,张雪口头提需求, 05类项目根据是否有外包选项,来决定流程是否走到人力
// 			// 其它所有类型项目,不管是否选择有外包,流程上必须过人力.
// 			// 涉及的流程包括:投标和合同
// 			if ("05".equals(projectBidding.getApply().getCategory()) ) {
// 				// 因为流程图上分支判断在前，故节点上的skip_hr暂时不用。
// 				varMap.put("hr", "0");
// 				varMap.put(ActUtils.VAR_SKIP_HR, "1");
// 			} else {
// 				varMap.put("hr", "1");
// 				varMap.put(ActUtils.VAR_SKIP_HR, "0");
// 			}
//
// 			return launch(projectBidding, varMap);
// 		} else { // 把驳回到申请人(重新修改业务表单，重新发起流程、销毁流程)也当成一个特殊的审批节点
// 			// 只要不是启动流程，其它任意节点的跳转都当成节点审批
// 			saveAudit(projectBidding);
// 			return null;
// 		}
// 	}
	
	/**
	 * 维护自己的流程状态	
	 // * @param id
	 // * @param audit
	 */
	// @Transactional(readOnly = false)
	// public void auditTo(String id, String audit) {
	// 	ProjectBidding projectBidding = get(id);
	// 	if (projectBidding == null) {
	// 		return;
	// 	}
	// 	projectBidding.setProcessStatus(audit);
	// 	dao.update(projectBidding);
	// }

	/**
	 * 流程中修改项目主状态、及各阶段小状态
	 *
	 */

	// @Transactional(readOnly = false)
	// public void auditStart(String id) {
	// 	ProjectBidding projectBidding = get(id);
	// 	if (projectBidding == null) {
	// 		return;
	// 	}
    //
	// 	String audit = DictUtils.getDictValue("审批中", "AuditStatus", "0");
	// 	projectBidding.setProcessStatus(audit);
	// 	dao.update(projectBidding);
	// }

	// @Transactional(readOnly = false)
	// public void auditFinish(String id) {
	// 	ProjectBidding projectBidding = get(id);
	// 	if (projectBidding == null) {
	// 		return;
	// 	}
    //
	// 	String audit = DictUtils.getDictValue("审批结束", "AuditStatus", "0");
	// 	projectBidding.setProcessStatus(audit);
	// 	dao.update(projectBidding);
	// }

}