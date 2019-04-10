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

	 @Autowired
	 private ProjectApplyExternalService applyService;
	
	public ProjectContractItem getContractItem(String itemId){
		return projectContractItemDao.get(itemId);
	}

    public List<ProjectContractItem> findItemList(ProjectContractItem entity){
	    return projectContractItemDao.findList(entity);
    }
	
	@Override
	public ProjectContract get(String id) {
		ProjectContract projectContract = super.get(id);
		// in case param id is not contract's id.
		if (projectContract == null)
		    return new ProjectContract();

		projectContract.setProjectContractItemList(projectContractItemDao.findList(new ProjectContractItem(projectContract)));
		return projectContract;
	}
	
	public ProjectContract findContractByPrjId(String prjId) {
		return dao.findContractByPrjId(prjId);
	}

	@Override
	public void setupVariable(ProjectContract projectContract, Map<String, Object> vars) {

		fillApply(projectContract);

		vars.put(ActUtils.VAR_PRJ_ID, projectContract.getApply().getId());

		vars.put(ActUtils.VAR_PRJ_TYPE, projectContract.getApply().getCategory());

		vars.put(ActUtils.VAR_TITLE, projectContract.getApply().getProjectName());
		// 20190409事业部
		vars.put(ActUtils.VAR_OFFICE_CODE, projectContract.getApply().getSaler().getOffice().getCode());

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