/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.apply.service.external;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.common.utils.Collections3;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.apply.dao.external.ProjectApplyExternalDao;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.mail.entity.Email;
import com.thinkgem.jeesite.modules.mail.service.MailService;
import com.thinkgem.jeesite.modules.project.utils.MyDictUtils;
import com.thinkgem.jeesite.modules.sys.dao.RoleDao;
import com.thinkgem.jeesite.modules.sys.entity.Role;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.IdentityLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 立项申请Service
 * @author jicdata
 * @version 2016-02-23
 */
@Service
@Transactional(readOnly = true)
public class ProjectApplyExternalService
		extends JicActService<ProjectApplyExternalDao, ProjectApplyExternal> {
	
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

	// @Override
	// @Transactional(readOnly = false)
	// public void delete(ProjectApplyExternal entity) {
	// 	// 流程审批状态,字典数据AuditStatus，0初始录入，1审批中，2审批结束
	// 	String status = entity.getProcStatus();
	// 	if ("1".equals(status)) {
	// 		endProcess(entity.getProcInsId());
	// 	}
	// 	super.delete(entity);
	// }

	@Override
	@Transactional(readOnly = false)
	public void save(ProjectApplyExternal entity) {
		if (entity.getIsNewRecord()){
			entity.preInsert4ProInteralApply();
		}
		super.save(entity);
	}

	// 回调函数，设置流程变量
	@Override
	public void setupVariable(ProjectApplyExternal projectApplyExternal, Map<String, Object> vars) {
		vars.put(ActUtils.VAR_PRJ_ID, projectApplyExternal.getId());
		vars.put(ActUtils.VAR_TITLE, projectApplyExternal.getProjectName());
		vars.put(ActUtils.VAR_PRJ_TYPE, projectApplyExternal.getCategory());
		vars.put(ActUtils.VAR_OFFICE_CODE, projectApplyExternal.getSaler().getOffice().getCode());
		// 事业部修改20190404
		vars.put(ActUtils.VAR_AMOUNT, projectApplyExternal.getEstimatedContractAmount());
	}

	// 回调函数
	@Override
	public void processAudit(ProjectApplyExternal projectApplyExternal, Map<String, Object> vars) {
		// 对不同环节的业务逻辑进行操作
		String taskDefKey = projectApplyExternal.getAct().getTaskDefKey();
		if (taskDefKey.indexOf(UserTaskType.UT_SPECIALIST) != -1) {
			// 审批通过才保存项目信息、项目编号
			if (projectApplyExternal.getAct().getFlagBoolean()) {
				String proCodeStr = projectApplyExternal.getProjectCode();//包含类别、年份、归属的信息+标识位
				if (StringUtils.isNotEmpty(proCodeStr) && proCodeStr.length() > 3) {
					String suffix = proCodeStr.substring(proCodeStr.length()-3);
					if (StringUtils.isNumeric(suffix)) {
						int currentCode = Integer.parseInt(suffix);
						currentCode++;
						// 项目专员审批后，保存项目编号
						dao.updatePorCode(String.valueOf(currentCode));
					}
				}
				// 项目专员审批后，保存立项申请单
				save(projectApplyExternal);
			}
		}
	}

	/**
	 * 业务状态、项目的业务状态、立项审批中、立项完成、招标审批中、招标完成、合同、结项等。
	 * 在各个流程引擎开始和结束时修改项目主表中的字段。
	 * 项目的大阶段
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
		if(StringUtils.isBlank(category)) {
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
		//添加后3位累加值
		String currentCode = dao.getCurrentCode();
		// currentCode = "";
		// 如果项目号后缀超过1000，则重新从001开始生成
		if (StringUtils.isNotEmpty(currentCode) && currentCode.length() >= 4) {
			currentCode = "001";
		}
		if (StringUtils.isEmpty(currentCode)) {
			currentCode = "001";
		}

		// for(int i=currentCode.length();i<maxIdentityLength;i++){
		// 	projectCode.append("0");
		// }
		currentCode = StringUtils.leftPad(StringUtils.isBlank(currentCode) ? "0" : currentCode, 3, '0');
		// or String.format("%03d", Integer.parseInt(currentCode) + 1);
		projectCode.append(currentCode);
		returnMap.put("data",projectCode.toString() );

		return returnMap;
	}

	//
	public List<Map<String, Object>> findList4tree(String proMainStage, boolean strict) {

		ProjectApplyExternal applyExternal = new ProjectApplyExternal();

		proMainStage = StringUtils.substringBefore(proMainStage, "?");
		if (strict) {
			applyExternal.setQueryStage(Arrays.asList(proMainStage.split(",")));
		} else {
			applyExternal.setProMainStage(proMainStage);
		}

		applyExternal.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "saler"));
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

	// 发送邮件，流程监听器中调用
	public void sendMail(DelegateTask task, String assignee, String userId, String groupId) {
		if (!Global.isSendEmail()) {
			return;
		}
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
		} // end for groups

		String mailTo = sbMailTo.toString();
		if (StringUtils.isBlank(mailTo))
			return;

		mailTo = mailTo.substring(0, mailTo.length() - 1);
		// Email
		Email email = new Email();
		email.setAddressee(mailTo);

//		((TaskEntity) task).execution.processDefinition.name
		String subject = ((TaskEntity) task).getExecution().getProcessDefinition().getName();
		email.setSubject(subject); // xxx审批流程
		email.setContent((String) task.getVariable(ActUtils.VAR_TITLE)); // 项目名称
		if (Global.isSendEmail()) {
			mailService.sendMailByAsyncMode(email);
		}
	}

    // 发送邮件，流程监听器中调用 -- used now
    public void sendMail(DelegateTask delegateTask ) {
        // 1. 审批人
        String assignee = delegateTask.getAssignee();

        // 2. 候选人、候选组
        Set<IdentityLink> candidates = delegateTask.getCandidates();
        Iterator<IdentityLink> iterator = candidates.iterator();

        List<String> groupEnameList = new ArrayList<>();
        List<String> userLoginNameList = new ArrayList<>();
        while(iterator.hasNext()) {
            IdentityLink identityLink= iterator.next();
            groupEnameList.add(identityLink.getGroupId());
            userLoginNameList.add(identityLink.getUserId());
        }
        userLoginNameList.add(assignee);

        // 保存邮箱地址
        List<String> mailTo = new ArrayList<>();
        User user;

        // 加入user email
        for (int i = 0; i < userLoginNameList.size(); i++) {
            if ("thinkgem".equals(userLoginNameList.get(i))) {
                continue;
            }
            user = UserUtils.getByLoginName( userLoginNameList.get(i) );
            if (user != null && StringUtils.isNotBlank(user.getEmail())) {
                mailTo.add(user.getEmail());
            }
        }

        Role role = new Role();
        // 加入group中的人员email
        for (int i = 0; i < groupEnameList.size(); i++) {
            if (StringUtils.isBlank(groupEnameList.get(i) ))
                continue;
            //
            role.setEnname(groupEnameList.get(i) );
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
                mailTo.add(user.getEmail() );
            } // end for UserList
        } // end for groups
        // 邮箱地址去重
        mailTo = Collections3.removeDuplicate(mailTo);
        String mailToString = StringUtils.join(mailTo.toArray(), ";");
        if (StringUtils.isBlank(mailToString)) {
            return;
        }

        // Email
        Email email = new Email();
        email.setAddressee(mailToString);

        String subject = ((TaskEntity) delegateTask).getExecution().getProcessDefinition().getName();
        email.setSubject(subject); // xxx审批流程
        email.setContent((String) delegateTask.getVariable(ActUtils.VAR_TITLE)); // 项目名称

        mailService.sendMailByAsyncMode(email);
    }
	
}