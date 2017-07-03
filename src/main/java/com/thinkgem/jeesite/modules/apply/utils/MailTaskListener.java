package com.thinkgem.jeesite.modules.apply.utils;

import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.service.ActTaskService;
import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.IdentityLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Set;

/**
 * 节点监听器
 * @author rgz
 *
 */
public class MailTaskListener implements TaskListener {

	String EVENTNAME_CREATE1 = "create";
	String EVENTNAME_ASSIGNMENT1 = "assignment";
	String EVENTNAME_COMPLETE1 = "complete";
	String EVENTNAME_DELETE1 = "delete";

	// @Autowired
	// ActTaskService actTaskService;

	ActTaskService actTaskService = SpringContextHolder.getBean(ActTaskService.class);
	// @Loggable
	// Logger logger;

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void notify(DelegateTask delegateTask) {

		// test
		// 节点开始时，assignment事件比create先执行
		// 节点结束时，complete事件比delete事件先执行
		String eventName = delegateTask.getEventName();
		logger.info("notify=========" + eventName);
		if ("create".endsWith(eventName)) {
			System.out.println("create=========");
			logger.info("create=========");
		}else if ("assignment".endsWith(eventName)) {
			logger.info("assignment========");
			System.out.println("assignment========");
		}else if ("complete".endsWith(eventName)) {
			logger.info("complete===========");
			System.out.println("complete===========");
		}else if ("delete".endsWith(eventName)) {
			logger.info("delete=============");
			System.out.println("delete=============");
		}

		String taskKey = delegateTask.getTaskDefinitionKey();
		String taskId = delegateTask.getId();

		// todo 直接通过某个节点
		// if ("usertask_business_leader".equalsIgnoreCase(taskKey)) {
		// 	actTaskService.complete(taskId , null, null, null);
		// 	return;
		// }

		Set<IdentityLink> candidates = delegateTask.getCandidates();
		Iterator<IdentityLink> iterator = candidates.iterator();

		StringBuilder sbGroupId = new StringBuilder();
		StringBuilder sbUserId = new StringBuilder();
		while(iterator.hasNext()) {
			IdentityLink identityLink= iterator.next();
			String groupId = identityLink.getGroupId();
			if (StringUtils.isNotBlank(groupId)) {
				sbGroupId.append(groupId + ",");
			}
			String userId = identityLink.getUserId();
			if (StringUtils.isNotBlank(userId)) {
				sbUserId.append(userId + ",");
			}
		}
		String assignee = delegateTask.getAssignee();
//		String userTaskDefKey = delegateTask.getTaskDefinitionKey();
//		String userTaskName = delegateTask.getName();
		
		ProjectApplyExternalService applyExternalService = SpringContextHolder.getBean(ProjectApplyExternalService.class);
		applyExternalService.sendMail(delegateTask, assignee, sbUserId.toString(), sbGroupId.toString());
	}

}
