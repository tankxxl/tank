/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.tech;

import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.entity.Act;
import com.thinkgem.jeesite.modules.act.service.ActTaskService;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.project.dao.tech.NeedDao;
import com.thinkgem.jeesite.modules.project.dao.tech.TechapplyDao;
import com.thinkgem.jeesite.modules.project.entity.tech.Need;
import com.thinkgem.jeesite.modules.project.entity.tech.Techapply;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资源申请Service
 * @author jicdata:Arthur
 * @version 2016-03-11
 */
@Service
@Transactional(readOnly = true)
public class TechapplyService extends CrudService<TechapplyDao, Techapply> {
	
	@Autowired
	ActTaskService actTaskService;

	@Autowired
	private NeedDao needDao;

	public Techapply get(String id) {
		Techapply techapply = super.get(id);
		techapply.setNeedList(needDao.findList(new Need(techapply)));
		return techapply;
	}
	
	public List<Techapply> findList(Techapply techapply) {
		return super.findList(techapply);
	}
	
	public Page<Techapply> findPage(Page<Techapply> page, Techapply techapply) {
		return super.findPage(page, techapply);
	}
	
	@Transactional(readOnly = false)
	public void save(Techapply techapply) {
		Act act = techapply.getAct();
		
		// 申请发起
		if (StringUtils.isBlank(techapply.getId())){
			String processStatus = DictUtils.getDictValue("审批中", "AuditStatus", "0");
			techapply.setProcessStatus(processStatus);
			super.save(techapply);
			for (Need need : techapply.getNeedList()){
				if (need.getId() == null){
					continue;
				}
				if (Need.DEL_FLAG_NORMAL.equals(need.getDelFlag())){
					if (StringUtils.isBlank(need.getId())){
						need.setTechapply(techapply);
						need.preInsert();
						needDao.insert(need);
					}else{
						need.preUpdate();
						needDao.update(need);
					}
				}else{
					needDao.delete(need);
				}
			}
						
			// 启动流程
			String key = techapply.getClass().getSimpleName();
			// 设置流程变量
			Map<String, Object> varMap = new HashMap<String, Object>();
			varMap.put("apply", UserUtils.getUser().getLoginName());
			varMap.put("classType", key);
			varMap.put("objId", techapply.getId());
			varMap.put("prjId", techapply.getProject().getId());
			
			actTaskService.startProcessEatFirstTask(
					ActUtils.PD_TECHAPPLY[0], 
					ActUtils.PD_TECHAPPLY[1], 
					techapply.getId(), 
					techapply.getProject().getProjectName(),
					varMap
			);
			
		} else {  // 重新编辑申请
			techapply.preUpdate();
			dao.update(techapply);
			
			act.setComment(("yes".equals(act.getFlag())?"[重申] ":"[销毁] ")+act.getComment());
			
			// 完成流程任务
			Map<String, Object> vars = Maps.newHashMap();
			vars.put(ActUtils.VAR_PASS, act.getFlagNumber());
			vars.put(ActUtils.VAR_TITLE, techapply.getProject().getProjectName());
			actTaskService.complateByAct(act, vars);	
		}
		
	}
	
	@Transactional(readOnly = false)
	public void delete(Techapply techapply) {
		super.delete(techapply);
		needDao.delete(new Need(techapply));
	}
	
	
	@Transactional(readOnly = false)
	public void auditSave(Techapply techapply) {
		Act act = techapply.getAct();
		// 设置意见
		act.setComment((act.getFlagBoolean() ? "[同意] ":"[驳回] ") + act.getComment());
		Map<String, Object> vars = Maps.newHashMap();		
		
		// 对不同环节的业务逻辑进行操作
		String taskDefKey = techapply.getAct().getTaskDefKey();

		if (UserTaskType.UT_SPECIALIST.equals(taskDefKey)){
			if ("03".equals(techapply.getProject().getCategory())) {
				vars.put("type", "2");
			} else {
				vars.put("type", "1");
			}
		} else if ("".equals(taskDefKey)) {
			
		}
				
		// 提交流程任务
		vars.put(ActUtils.VAR_PASS, act.getFlagNumber());
		vars.put(ActUtils.VAR_TITLE, techapply.getRemarks());
		actTaskService.complateByAct(act, vars);	
	}
	
	/**
	 * 维护自己的流程状态	
	 * @param id
	 * @param audit
	 */
	@Transactional(readOnly = false)
	public void auditTo(String id, String audit) {
		Techapply techapply = get(id);
		techapply.setProcessStatus(audit);
		dao.update(techapply);
	}
}