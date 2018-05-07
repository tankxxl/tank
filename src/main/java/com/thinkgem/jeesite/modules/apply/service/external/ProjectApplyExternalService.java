/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.apply.service.external;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.apply.dao.external.ProjectApplyExternalDao;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.mail.entity.Email;
import com.thinkgem.jeesite.modules.mail.service.MailService;
import com.thinkgem.jeesite.modules.project.entity.bidding.ProjectBidding;
import com.thinkgem.jeesite.modules.project.utils.MyDictUtils;
import com.thinkgem.jeesite.modules.sys.dao.RoleDao;
import com.thinkgem.jeesite.modules.sys.entity.Role;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 立项申请Service
 *
 * @author jicdata
 * @version 2016-02-23
 */
@Service
@Transactional(readOnly = true)
public class ProjectApplyExternalService extends
		JicActService<ProjectApplyExternalDao, ProjectApplyExternal> {
	
	@Autowired
	MailService mailService;

	@Autowired
	private RoleDao roleDao;

    @Override
    public ProjectApplyExternal get(String id) {
        ProjectApplyExternal apply = super.get(id);
        if (apply == null) {
            apply = new ProjectApplyExternal();
        }
        return apply;
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(ProjectApplyExternal entity) {
        // 流程审批状态,字典数据AuditStatus，0初始录入，1审批中，2审批结束
		String status = entity.getProcStatus();
		if ("1".equals(status)) {
			endProcess(entity.getProcInsId());
		}
        super.delete(entity);

    }

    @Override
	public void setupVariable(ProjectApplyExternal projectApplyExternal, Map<String, Object> vars) {
		
		projectApplyExternal.preInsert4ProInteralApply();

		vars.put(ActUtils.VAR_PRJ_ID, projectApplyExternal.getId());
		vars.put(ActUtils.VAR_TITLE, projectApplyExternal.getProjectName());

		vars.put(ActUtils.VAR_OFFICE_CODE, projectApplyExternal.getSaler().getOffice().getCode());

		boolean isBossAudit = MyDictUtils.isBossAudit(projectApplyExternal.getEstimatedContractAmount(), projectApplyExternal.getEstimatedGrossProfitMargin());
		if (isBossAudit) {
			vars.put(ActUtils.VAR_SKIP_BOSS, "0");
		} else {
			vars.put(ActUtils.VAR_SKIP_BOSS, "1");
		}
	}

	@Override
	public void processAudit(ProjectApplyExternal projectApplyExternal, Map<String, Object> vars) {
		String taskDefKey = projectApplyExternal.getAct().getTaskDefKey();
		if (UserTaskType.UT_SPECIALIST.equals(taskDefKey)) {
			if (projectApplyExternal.getAct().getFlagBoolean()) {
				String proCodeStr = projectApplyExternal.getProjectCode();
				if (StringUtils.isNotEmpty(proCodeStr) && proCodeStr.length() > 3) {
					String suffix = proCodeStr.substring(proCodeStr.length()-3);
					if (StringUtils.isNumeric(suffix)) {
						int currentCode = Integer.parseInt(suffix);
						currentCode++;
						dao.updatePorCode(String.valueOf(currentCode));
					}
				}
				save(projectApplyExternal);
			}
		}
	}

    @Override
    @Transactional(readOnly = false)
    public void save(ProjectApplyExternal entity) {
        if (entity.getIsNewRecord()){
            entity.preInsert4ProInteralApply();
        }
        super.save(entity);
    }

    // 流程过程中，发送邮件
	public void sendMail(DelegateTask task, String assignee, String userId, String groupId) {
		StringBuilder sbMailTo = new StringBuilder();
		User user;
		// 加入assignee
		if (StringUtils.isNotBlank(assignee)) {
			user = UserUtils.getByLoginName(assignee);
			if (user != null && StringUtils.isNotBlank(user.getEmail()))
				sbMailTo.append(user.getEmail() + ";");	
		}
		
		String [] userArray = userId.split(",");
		// 加入user
		for (int i = 0; i < userArray.length; i++) {
			if (StringUtils.isBlank(userArray[i]))
				continue;
			
			user = UserUtils.getByLoginName(userArray[i]);
			if (user != null && StringUtils.isNotBlank(user.getEmail()))
			sbMailTo.append(user.getEmail() + ";");
		}
		
		String[] groupArray = groupId.split(",");
		Role role = new Role();
		// 加入group
		for (int i = 0; i < groupArray.length; i++) {
			if (StringUtils.isBlank(groupArray[i]))
				continue;
			//
			role.setEnname(groupArray[i]);
			role = roleDao.getRoleUserByEnname(role);
			if (role == null)
				continue;
			
			List<User> users = role.getUserList();
			if (users == null)
				continue;

			for (int j = 0; j < users.size(); j++) {
				user = users.get(j);
				if (user == null)
					continue;
				if ("thinkgem".equals(user.getLoginName()))
					continue;
				if (StringUtils.isBlank(user.getEmail()))
					continue;
				sbMailTo.append(user.getEmail() + ";");
			} // end for UserList
		//	replaced by java8
		// 	users.stream()
		// 			.filter(userItem -> userItem != null)
		// 			.filter(userItem -> !"thinkgem".equals(userItem.getLoginName()))
		// 			.filter(userItem -> StringUtils.isNotEmpty(userItem.getEmail()))
		// 			.forEach(userItem -> sbMailTo.append(userItem.getEmail() + ";"));
		} // end for groups
		
		String mailTo = sbMailTo.toString();
		if (StringUtils.isBlank(mailTo))
			return;
		
		mailTo = mailTo.substring(0, mailTo.length() - 1);
		// Email
		Email email = new Email();
		email.setAddressee(mailTo);

		String subject = ((TaskEntity) task).getExecution().getProcessDefinition().getName();
		email.setSubject(subject); // xxx审批流程
		email.setContent((String) task.getVariable(ActUtils.VAR_TITLE)); // 项目名称
		if (Global.isSendEmail()) {
			mailService.sendMailByAsynchronousMode(email);
		}
		System.out.println("MailTo=" + sbMailTo.toString());
	}

	/**
	 * 修改项目大状态
	 *
	 * @param id
	 * @param stageValue
	 */
	@Transactional(readOnly = false)
	public void stageTo(String id, String stageValue) {
		ProjectApplyExternal projectApplyExternal = get(id);
		if (projectApplyExternal == null) {
			return;
		}
		projectApplyExternal.setProMainStage(stageValue);
		dao.update(projectApplyExternal);
	}

	// 根据参数生成项目编号
	public Map<String,String> genPrjCode(String category,String ownership) {
		Map<String,String> returnMap = new HashMap<>();

		//若项目类型为空，返回map中加error键值对
		if(category == null){
			returnMap.put("error","项目类型不能为空，请先选择项目类型");
			return returnMap;
		}

		StringBuffer projectCode =new StringBuffer();
		//添加项目类型值
		projectCode.append(category);
		//若项目类型不为空，则在map中添加data键值对
		SimpleDateFormat df = new SimpleDateFormat("yyyy");
		String dateString = df.format(new Date());
		projectCode.append(dateString);
		//添加项目归属对应值
		projectCode.append(ownership);

		String currentCode = dao.getCurrentCode();
		currentCode = StringUtils.leftPad(StringUtils.isBlank(currentCode) ? "0" : currentCode, 3, '0');
		// or String.format("%03d", Integer.parseInt(currentCode) + 1);
		projectCode.append(currentCode);

		returnMap.put("data",projectCode.toString() );

		return returnMap;
	}

	public List<Map<String, Object>> findList4tree(String proMainStage, boolean strict) {

		ProjectApplyExternal applyExternal = new ProjectApplyExternal();

		proMainStage = StringUtils.substringBefore(proMainStage, "?");
		if (strict) {
			applyExternal.setQueryStage(Arrays.asList(proMainStage.split(",")));
		} else {
			applyExternal.setProMainStage(proMainStage);
		}

		applyExternal.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		List<ProjectApplyExternal> list = findList(applyExternal);
		return toMapList(list);
	}

	private List<Map<String, Object>> toMapList(List<ProjectApplyExternal> list) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		if (list == null || list.isEmpty())
			return mapList;

		for (int i=0; i<list.size(); i++) {
			ProjectApplyExternal e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("name", e.getProjectName());
			mapList.add(map);
		}
		return mapList;
	}
}