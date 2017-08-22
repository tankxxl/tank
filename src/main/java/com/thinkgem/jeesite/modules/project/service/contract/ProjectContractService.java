/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.contract;

import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
import com.thinkgem.jeesite.modules.project.dao.contract.ProjectContractDao;
import com.thinkgem.jeesite.modules.project.dao.contract.ProjectContractItemDao;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContractItem;
import com.thinkgem.jeesite.modules.project.utils.MyDictUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sound.midi.Soundbank;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 合同Service
 * @author jicdata
 * @version 2016-03-09
 */
@Service
@Transactional(readOnly = true)
public class ProjectContractService extends JicActService<ProjectContractDao, ProjectContract> {

	@Autowired
	private ProjectContractItemDao projectContractItemDao;
	
	// @Autowired
	// ActTaskService actTaskService;
	
//	@Autowired
//	private ProjectBiddingService projectBiddingService;

	 @Autowired
	 private ProjectApplyExternalService applyService;
	
	public ProjectContractItem getContractItem(String itemId){
		return projectContractItemDao.get(itemId);
	}

    public List<ProjectContractItem> findItemList(ProjectContractItem entity){
	    return projectContractItemDao.findList(entity);
//        return projectContractItemDao.get(itemId);
    }
	
	@Override
	public ProjectContract get(String id) {
		ProjectContract projectContract = super.get(id);
		// in case param id is not contract's id.
		if (projectContract == null)
		    return projectContract;

		projectContract.setProjectContractItemList(projectContractItemDao.findList(new ProjectContractItem(projectContract)));
		return projectContract;
	}
	
	public ProjectContract findContractByPrjId(String prjId) {
		return dao.findContractByPrjId(prjId);
	}

	/**
	 * 保存并结束流程
	 * @param projectContract
	 */
	// @Transactional(readOnly = false)
	// public void saveFinishProcess(ProjectContract projectContract) {
	// 	// 开启流程
	// 	String procInsId = saveLaunch(projectContract);
	// 	// 结束流程
	// 	endProcess(procInsId);
	// }


	@Override
	public void setupVariable(ProjectContract projectContract, Map<String, Object> vars) {

		fillApply(projectContract);

		vars.put(ActUtils.VAR_PRJ_ID, projectContract.getApply().getId());

		vars.put(ActUtils.VAR_PRJ_TYPE, projectContract.getApply().getCategory());

		vars.put(ActUtils.VAR_TITLE, projectContract.getApply().getProjectName());

		if ("03".equals(projectContract.getApply().getCategory()) ) {
			vars.put(ActUtils.VAR_TYPE, "2");
		} else {
			vars.put(ActUtils.VAR_TYPE, "1");
		}

		boolean isBossAudit = shouldBossAudit(projectContract);
		if (isBossAudit) {
			// 需要总经理审批
			vars.put(ActUtils.VAR_SKIP_BOSS, "0");
		} else {
			vars.put(ActUtils.VAR_SKIP_BOSS, "1");
		}
		// 20160628下午,张雪口头提需求, 05类项目根据是否有外包选项,来决定流程是否走到人力
		// 其它所有类型项目,不管是否选择有外包,流程上必须过人力.
		// 涉及的流程包括:投标和合同
		if ("05".equals(projectContract.getApply().getCategory()) ) {
			// 因为流程图上分支判断在前，故节点上的skip_hr暂时不用。
			vars.put("hr", "0");
			System.out.println();
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
	 * @param projectContract
	 */
// 	@Transactional(readOnly = false)
// 	public String saveLaunch(ProjectContract projectContract) {
//
// 		if (projectContract.getIsNewRecord()) {
// 			// 启动流程的时候，把业务数据放到流程变量里
// 			Map<String, Object> varMap = new HashMap<String, Object>();
// 			varMap.put(ActUtils.VAR_PRJ_ID, projectContract.getApply().getId());
//
// 			varMap.put(ActUtils.VAR_PRJ_TYPE, projectContract.getApply().getCategory());
//
// 			varMap.put(ActUtils.VAR_TITLE, projectContract.getApply().getProjectName());
//
// 			if ("03".equals(projectContract.getApply().getCategory()) ) {
// 				varMap.put(ActUtils.VAR_TYPE, "2");
// 			} else {
// 				varMap.put(ActUtils.VAR_TYPE, "1");
// 			}
//
// 			boolean isBossAudit = shouldBossAudit(projectContract);
// 			if (isBossAudit) {
// 				// 需要总经理审批
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
// 			if ("05".equals(projectContract.getApply().getCategory()) ) {
// 				// 因为流程图上分支判断在前，故节点上的skip_hr暂时不用。
// 				varMap.put("hr", "0");
// 				varMap.put(ActUtils.VAR_SKIP_HR, "1");
// 			} else {
// 				varMap.put("hr", "1");
// 				varMap.put(ActUtils.VAR_SKIP_HR, "0");
// 			}
//
// 			return launch(projectContract, varMap);
// 		} else { // 把驳回到申请人(重新修改业务表单，重新发起流程、销毁流程)也当成一个特殊的审批节点
// 			// 只要不是启动流程，其它任意节点的跳转都当成节点审批
// 			saveAudit(projectContract);
// 			return null;
// 		}
// 	}
	
	@Override
	@Transactional(readOnly = false)
	public void save(ProjectContract projectContract) {
		super.save(projectContract);
		saveItem(projectContract);
	}
	
	@Override
	@Transactional(readOnly = false)
	public void delete(ProjectContract projectContract) {
		super.delete(projectContract);
		projectContractItemDao.delete(new ProjectContractItem(projectContract));
	}

	// 审批过程中
	@Override
	public void processAudit(ProjectContract projectContract, Map<String, Object> vars) {
		setupVariable(projectContract, vars);
		// 对不同环节的业务逻辑进行操作
		String taskDefKey = projectContract.getAct().getTaskDefKey();

		if (UserTaskType.UT_SERVICE_DELIVERY_LEADER.equals(taskDefKey) ||
				UserTaskType.UT_SOFTWARE_DEVELOPMENT_LEADER.equals(taskDefKey)){
			if(StringUtils.isNotBlank(projectContract.getProjectManager().getId())){
				// 保存选择的项目经理
				save(projectContract);
			}
		} else if (UserTaskType.UT_COMMERCE_LEADER.equalsIgnoreCase(taskDefKey)) {
			// 保存合同编号
			save(projectContract);
		} else if (UserTaskType.UT_OWNER.equalsIgnoreCase(taskDefKey)) {
			// 驳回到发起人节点后，他可以修改所有的字段，所以重新设置一下流程变量
			setupVariable(projectContract, vars);
		}




	}

	public void fillApply(ProjectContract contract) {
		ProjectApplyExternal apply = applyService.get(contract.getApply().getId());
		contract.setApply(apply);
	}

	/**
	 * 维护自己的流程状态	
	 // * @param id
	 // * @param audit
	 */
	// @Transactional(readOnly = false)
	// public void auditTo(String id, String audit) {
	// 	ProjectContract projectContract = get(id);
	// 	if (projectContract == null) {
	// 		return;
	// 	}
	// 	projectContract.setProcessStatus(audit);
	// 	dao.update(projectContract);
	// }


	private boolean shouldBossAudit(ProjectContract projectContract) {
		boolean shouldAudit = false;
		for (ProjectContractItem projectContractItem : projectContract.getProjectContractItemList()){

			shouldAudit = MyDictUtils.isBossAudit(projectContractItem.getContractAmount(),
					projectContractItem.getGrossProfitMargin());

			if (shouldAudit) {
				return shouldAudit;
			}
		}

		return false;
	}


	private void saveItem(ProjectContract projectContract) {
        for (ProjectContractItem projectContractItem : projectContract.getProjectContractItemList()){

//            if (StringUtils.isBlank(projectContractItem.getId())) {
//                continue;
//            }

            if (projectContractItem.getId() == null){
                continue;
            }

            if (ProjectContractItem.DEL_FLAG_NORMAL.equals(projectContractItem.getDelFlag())){
                if (StringUtils.isBlank(projectContractItem.getId())){
                    projectContractItem.setContract(projectContract);
                    projectContractItem.preInsert();
                    projectContractItemDao.insert(projectContractItem);
                }else{
                    projectContractItem.preUpdate();
                    projectContractItemDao.update(projectContractItem);
                }
            }else{
                projectContractItemDao.delete(projectContractItem);
            }
        }
    }

}