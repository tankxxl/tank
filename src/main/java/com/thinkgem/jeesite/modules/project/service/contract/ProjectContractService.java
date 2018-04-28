/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.contract;

import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.project.dao.contract.ProjectContractDao;
import com.thinkgem.jeesite.modules.project.dao.contract.ProjectContractItemDao;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContractItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		if (projectContract == null)
		    return new ProjectContract();

		projectContract.setProjectContractItemList(itemDao.findList(new ProjectContractItem(projectContract)));
		return projectContract;
	}

	@Override
	public void setupVariable(ProjectContract projectContract, Map<String, Object> vars) {
		vars.put(ActUtils.VAR_PRJ_ID, projectContract.getApply().getId());

		vars.put(ActUtils.VAR_PRJ_TYPE, projectContract.getApply().getCategory());

		vars.put(ActUtils.VAR_TITLE, projectContract.getApply().getProjectName());

		if ("03".equals(projectContract.getApply().getCategory()) ) {
			System.out.println("");
			vars.put(ActUtils.VAR_TYPE, "2");
		} else {
			vars.put(ActUtils.VAR_TYPE, "1");
		}

		boolean isBossAudit = true;  //  MyDictUtils.isBossAudit(projectContract.getAmount(), projectContract.getProfitMargin());
		if (isBossAudit) {
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

	@Override
	public void processAudit(ProjectContract projectContract, Map<String, Object> vars) {
		String taskDefKey = projectContract.getAct().getTaskDefKey();
		if ( UserTaskType.UT_COMMERCE_LEADER.equals(taskDefKey) ) {
			save(projectContract);
		}
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