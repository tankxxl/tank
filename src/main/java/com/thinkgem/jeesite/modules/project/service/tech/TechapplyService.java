/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.service.tech;

import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.project.dao.tech.NeedDao;
import com.thinkgem.jeesite.modules.project.dao.tech.TechapplyDao;
import com.thinkgem.jeesite.modules.project.entity.tech.Need;
import com.thinkgem.jeesite.modules.project.entity.tech.Techapply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 资源申请Service
 * @author jicdata:Arthur
 * @version 2016-03-11
 */
@Service
@Transactional(readOnly = true)
public class TechapplyService extends JicActService<TechapplyDao, Techapply> {

	@Autowired
	private NeedDao needDao;

	public Techapply get(String id) {
		Techapply techapply = super.get(id);
		techapply.setNeedList(needDao.findList(new Need(techapply)));
		return techapply;
	}

	/**
	 * 保存并结束流程
	 * @param techapply
	 */
	@Transactional(readOnly = false)
	public void saveFinishProcess(Techapply techapply) {
		// 开启流程
		String procInsId = saveLaunch(techapply);
		// 结束流程
		endProcess(procInsId);
	}

	/**
	 * 保存表单数据，并启动流程
	 *
	 * 申请人发起流程，申请人重新发起流程入口
	 * 在form界面
	 *
	 * @param techapply
	 */
	@Transactional(readOnly = false)
	public String saveLaunch(Techapply techapply) {

		if (techapply.getIsNewRecord()) {
			// 启动流程的时候，把业务数据放到流程变量里
			Map<String, Object> varMap = new HashMap<String, Object>();
			varMap.put(ActUtils.VAR_PRJ_ID, techapply.getProject().getId());

			varMap.put(ActUtils.VAR_PRJ_TYPE, techapply.getProject().getCategory());

			varMap.put(ActUtils.VAR_TITLE, techapply.getProject().getProjectName());

			return launch(techapply, varMap);
		} else { // 把驳回到申请人(重新修改业务表单，重新发起流程、销毁流程)也当成一个特殊的审批节点
			// 只要不是启动流程，其它任意节点的跳转都当成节点审批
			saveAudit(techapply);
			return null;
		}
	}
	
	@Transactional(readOnly = false)
	public void save(Techapply techapply) {
		super.save(techapply);
		saveItem(techapply);
	}

	@Transactional(readOnly = false)
	public void delete(Techapply techapply) {
		super.delete(techapply);
		needDao.delete(new Need(techapply));
	}

	private void saveItem(Techapply techapply) {
		for (Need need : techapply.getNeedList()) {
			if (need.getId() == null){
				continue;
			}
			if (Need.DEL_FLAG_NORMAL.equals(need.getDelFlag())) {
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
	}

	
	// @Transactional(readOnly = false)
	// public void auditSave(Techapply techapply) {
	// 	Act act = techapply.getAct();
	// 	// 设置意见
	// 	act.setComment((act.getFlagBoolean() ? "[同意] ":"[驳回] ") + act.getComment());
	// 	Map<String, Object> vars = Maps.newHashMap();
	//
	// 	// 对不同环节的业务逻辑进行操作
	// 	String taskDefKey = techapply.getAct().getTaskDefKey();
    //
	// 	if (UserTaskType.UT_SPECIALIST.equals(taskDefKey)){
	// 		if ("03".equals(techapply.getProject().getCategory())) {
	// 			vars.put("type", "2");
	// 		} else {
	// 			vars.put("type", "1");
	// 		}
	// 	} else if ("".equals(taskDefKey)) {
	//
	// 	}
	//
	// 	// 提交流程任务
	// 	vars.put(ActUtils.VAR_PASS, act.getFlagNumber());
	// 	vars.put(ActUtils.VAR_TITLE, techapply.getRemarks());
	// 	actTaskService.complateByAct(act, vars);
	// }
	
	/**
	 * 维护自己的流程状态	
	 // * @param id
	 // * @param audit
	 */
	// @Transactional(readOnly = false)
	// public void auditTo(String id, String audit) {
	// 	Techapply techapply = get(id);
	// 	techapply.setProcessStatus(audit);
	// 	dao.update(techapply);
	// }
}