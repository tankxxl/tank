/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.contract;

import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.service.ActTaskService;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
import com.thinkgem.jeesite.modules.project.dao.contract.ProjectContractDao;
import com.thinkgem.jeesite.modules.project.dao.contract.ProjectContractItemDao;
import com.thinkgem.jeesite.modules.project.entity.bidding.ProjectBidding;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContractItem;
import com.thinkgem.jeesite.modules.project.utils.MyDictUtils;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class ProjectContractService extends CrudService<ProjectContractDao, ProjectContract> {

	@Autowired
	private ProjectContractItemDao projectContractItemDao;
	
	@Autowired
	ActTaskService actTaskService;
	
//	@Autowired
//	private ProjectBiddingService projectBiddingService;

	@Autowired
	private ProjectApplyExternalService projectApplyExternalService;
	
	
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
	
	@Override
	public List<ProjectContract> findList(ProjectContract projectContract) {
		return super.findList(projectContract);
	}
	
	@Override
	public Page<ProjectContract> findPage(Page<ProjectContract> page, ProjectContract projectContract) {
		return super.findPage(page, projectContract);
	}

	@Transactional(readOnly = false)
	public void onlySave(ProjectContract projectContract) {

		boolean isNew ;
		if (StringUtils.isBlank(projectContract.getId())){
			isNew = true;
		} else {
			isNew = false;
		}

		String processStatus = DictUtils.getDictValue("审批结束", "AuditStatus", "0");
		projectContract.setProcessStatus(processStatus);
		super.save(projectContract);

		saveItem(projectContract);

		if (isNew) {
			String stageValue = DictUtils.getDictValue("合同完成", "jic_pro_main_stage", "0");
			projectApplyExternalService.stageTo(projectContract.getApply().getId(), stageValue);
		} else {

		}
	}
	
	@Override
	@Transactional(readOnly = false)
	public void save(ProjectContract projectContract) {
		
		// 申请发起
		if (StringUtils.isBlank(projectContract.getId())){
//			String processStatus = DictUtils.getDictValue("审批中", "AuditStatus", "0");
//			projectContract.setProcessStatus(processStatus);
			super.save(projectContract);

            saveItem(projectContract);
						
			// 启动流程
			String key = projectContract.getClass().getSimpleName();
			// 设置流程变量
			Map<String, Object> varMap = new HashMap<String, Object>();
			varMap.put("apply", UserUtils.getUser().getLoginName());
			varMap.put("classType", key);
			varMap.put("objId", projectContract.getId());
			varMap.put("prjId", projectContract.getApply().getId());
			

			varMap.put(ActUtils.VAR_TITLE, projectContract.getApply().getProjectName());
			
			actTaskService.startProcessEatFirstTask(
					ActUtils.PD_PROJECTCONTRACT[0], 
					ActUtils.PD_PROJECTCONTRACT[1], 
					projectContract.getId(), 
					projectContract.getApply().getProjectName(),
					varMap
			);
			
		} else {  // 重新编辑申请
			if (projectContract.getAct().getFlagBoolean()) {
				projectContract.preUpdate();
				dao.update(projectContract);

                saveItem(projectContract);
				
				projectContract.getAct().setComment(("yes".equals(projectContract.getAct().getFlag())?"[重申] ":"[销毁] ")+projectContract.getAct().getComment());
				
				// 完成流程任务
				Map<String, Object> vars = Maps.newHashMap();
				vars.put(ActUtils.VAR_PASS, projectContract.getAct().getFlagNumber());
				vars.put(ActUtils.VAR_TITLE, projectContract.getApply().getProjectName());
				actTaskService.complateByAct(projectContract.getAct(), vars);	
			} else {
				dao.delete(projectContract);
				actTaskService.endProcess(projectContract.getAct());	
			}	
		}
	}
	
	@Override
	@Transactional(readOnly = false)
	public void delete(ProjectContract projectContract) {
		super.delete(projectContract);
		projectContractItemDao.delete(new ProjectContractItem(projectContract));
	}

	public ProjectContract findByProcInsId(String procInsId) {
		ProjectContract contract = null;
		if (StringUtils.isEmpty(procInsId)) {
			contract = new ProjectContract();
		} else {
			contract = dao.findByProcInsId(procInsId);
		}
		if (contract == null) {
			contract = new ProjectContract();
		}
		return contract;
	}
	/**
	 * 审核审批保存
	 * @param projectContract
	 */
	@Transactional(readOnly = false)
	public void auditSave(ProjectContract projectContract) {
		// 设置意见
		projectContract.getAct().setComment((projectContract.getAct().getFlagBoolean() ? 
						"[同意] ":"[驳回] ") + projectContract.getAct().getComment());
		Map<String, Object> vars = Maps.newHashMap();		
		
		// 对不同环节的业务逻辑进行操作
		String taskDefKey = projectContract.getAct().getTaskDefKey();
		
		// 审核环节--下面是技术部门设置 --需要保存项目经理
		if ("usertask_software_development_leader".equals(taskDefKey)||"usertask_service_delivery_leader".equals(taskDefKey)){
			if(StringUtils.isNotBlank(projectContract.getProjectManager().getId())){
				super.save(projectContract);
			}
		}
		
		if (UserTaskType.UT_COMMERCE_LEADER.equals(taskDefKey)){
			// 保存合同号
            saveItem(projectContract);
//			super.save(projectContract); 
			if ( "03".equals(projectContract.getApply().getCategory()) ) {
				vars.put("type", "2");
			} else {
				vars.put("type", "1");
			}
			
			// 项目类型
			vars.put(ActUtils.VAR_PRJ_TYPE, projectContract.getApply().getCategory());
			
			// 项目金额大于2000W，需要总经理审批
//			boolean isBossAudit = MyDictUtils.isBossAudit(projectContract.getApply().getEstimatedContractAmount(), projectContract.getApply().getEstimatedGrossProfitMargin());

			boolean isBossAudit = shouldBossAudit(projectContract);
			if (isBossAudit) {
				// 需要总经理审批
				vars.put(ActUtils.VAR_BOSS_AUDIT, "1");
			} else {
				vars.put(ActUtils.VAR_BOSS_AUDIT, "0");
			}
			
			// 有外包的项目需要人力审批---项目的外包属性放在投标审批表中，也可以放到立项审批表中一个。
//			if ("1".equals(projectContract.getApply().getOutsourcing())) {
//				vars.put(ActUtils.VAR_HR_AUDIT, "1");
//			} else {
//				vars.put(ActUtils.VAR_HR_AUDIT, "0");
//			}

			// 20160628下午,张雪口头提需求, 05类项目根据是否有外包选项,来决定流程是否走到人力
			// 其它所有类型项目,不管是否选择有外包,流程上必须过人力.
			// 涉及的流程包括:投标和合同
			if ("05".equals(projectContract.getApply().getCategory()) ) {
				vars.put(ActUtils.VAR_HR_AUDIT, "1");
			} else {
				vars.put(ActUtils.VAR_HR_AUDIT, "0");
			}
			
		} else if ("".equals(taskDefKey)) {
			
		}
				
		// 提交流程任务
		vars.put(ActUtils.VAR_PASS, projectContract.getAct().getFlagNumber() );
		vars.put(ActUtils.VAR_TITLE, projectContract.getApply().getProjectName() );
		actTaskService.complateByAct(projectContract.getAct(), vars);	
	}
	/**
	 * 维护自己的流程状态	
	 * @param id
	 * @param audit
	 */
	@Transactional(readOnly = false)
	public void auditTo(String id, String audit) {
		ProjectContract projectContract = get(id);
		if (projectContract == null) {
			return;
		}
		projectContract.setProcessStatus(audit);
		dao.update(projectContract);
	}


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