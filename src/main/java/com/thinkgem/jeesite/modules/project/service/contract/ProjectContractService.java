/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.contract;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.project.dao.contract.ProjectContractDao;
import com.thinkgem.jeesite.modules.project.dao.contract.ProjectContractItemDao;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContractItem;
import com.thinkgem.jeesite.modules.project.utils.MyDictUtils;
import com.thinkgem.jeesite.modules.sys.entity.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
	private ProjectContractItemDao itemDao;
	
	public ProjectContractItem getContractItem(String itemId){
		return itemDao.get(itemId);
	}

    public List<ProjectContractItem> findItemList(ProjectContractItem entity){
	    return itemDao.findList(entity);
    }
	
	@Override
	public ProjectContract get(String id) {
		ProjectContract projectContract = super.get(id);
		// in case param id is not contract's id.
		if (projectContract == null)
		    return null;

		projectContract.setProjectContractItemList(itemDao.findList(new ProjectContractItem(projectContract)));
		return projectContract;
	}

	public Page<ProjectContract> findPage(Page<ProjectContract> page, ProjectContract projectContract) {

		// 设置默认时间范围，默认当前月
		if (projectContract.getQueryBeginDate() == null){
			projectContract.setQueryBeginDate(DateUtils.setDays(DateUtils.parseDate(DateUtils.getDate()), 1));
		}
		if (projectContract.getQueryEndDate() == null){
			projectContract.setQueryEndDate(DateUtils.addMonths(projectContract.getQueryBeginDate(), 1));
		}

		return super.findPage(page, projectContract);

	}

	// 流程启动之前，设置map
	@Override
	public void setupVariable(ProjectContract projectContract, Map<String, Object> vars) {
		vars.put(ActUtils.VAR_PRJ_ID, projectContract.getApply().getId());

		vars.put(ActUtils.VAR_PRJ_TYPE, projectContract.getApply().getCategory());

		vars.put(ActUtils.VAR_PROC_DEF_KEY, projectContract.getDictRemarks());

		vars.put(ActUtils.VAR_TITLE, projectContract.getApply().getProjectName());

		if (StringUtils.isEmpty(projectContract.getApply().getProjectName())) {
			vars.put(ActUtils.VAR_TITLE, projectContract.getClientName());
		}

		// 设置合同金额
		vars.put(ActUtils.VAR_AMOUNT, projectContract.getAmount());

		if ("03".equals(projectContract.getApply().getCategory()) ) {
			System.out.println("");
			// 分支上使用，没在节点上使用
			vars.put(ActUtils.VAR_TYPE, "2");
		} else {
			vars.put(ActUtils.VAR_TYPE, "1");
		}

		boolean isBossAudit = MyDictUtils.isBossAudit(projectContract.getAmount(),
				projectContract.getProfitMargin());
		if (isBossAudit) { // 需要总经理审批
			// 节点上使用
			vars.put(ActUtils.VAR_SKIP_BOSS, "0");
		} else {
			vars.put(ActUtils.VAR_SKIP_BOSS, "1");
		}
	}
	
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
		itemDao.delete(new ProjectContractItem(projectContract));
	}

	public List<ProjectContract> findPreEndList(ProjectContract projectContract) {
		return dao.findPreEndList(projectContract);
	}

	// page在Service中组装
	public Page<ProjectContract> findPreEndPage(Page<ProjectContract> page, ProjectContract entity) {
		entity.setPage(page);
		page.setList(dao.findPreEndList(entity));
		return page;
	}


	public Long findPreEndCount(ProjectContract projectContract) {
		return dao.findPreEndCount(projectContract);
	}

	// 审批过程中
	@Override
	public void processAudit(ProjectContract projectContract, Map<String, Object> vars) {
		// 对不同环节的业务逻辑进行操作
		String taskDefKey = projectContract.getAct().getTaskDefKey();

		if ( UserTaskType.UT_SPECIALIST.equals(taskDefKey) ) {
			if(StringUtils.isNotBlank(projectContract.getContractCode())){
				// 保存合同编号
				save(projectContract);
			}
		} else if ( UserTaskType.UT_BUSINESS_LEADER.equals(taskDefKey) ) {
			if ("4".equals(projectContract.getContractType())) {
				// 保存服务合同的毛利率
				save(projectContract);
			}
		}
		// else if (UserTaskType.UT_COMMERCE_LEADER.equalsIgnoreCase(taskDefKey)) {
		// 	// 保存合同编号
		// 	save(projectContract);
		// }
	}

	// 审批结束时
	@Override
	public void processAuditEnd(ProjectContract contract) {
		// contract.setAuditEndDate(null);
		contract.setAuditEndDate(new Date()) ;
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
                    itemDao.insert(projectContractItem);
                }else{
                    projectContractItem.preUpdate();
                    itemDao.update(projectContractItem);
                }
            }else{
                itemDao.delete(projectContractItem);
            }
        }
    }

}