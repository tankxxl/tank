package com.thinkgem.jeesite.modules.project.utils;

import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.project.entity.tech.Techapply;
import com.thinkgem.jeesite.modules.project.service.tech.TechapplyService;
import com.thinkgem.jeesite.modules.sys.entity.Office;
import com.thinkgem.jeesite.modules.sys.entity.User;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 设置任务执行人
 * @author rgz
 *
 */
public class TechapplyListener implements TaskListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8976105123390960802L;
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void notify(DelegateTask delegateTask) {
		logger.debug("=============================================");
		String id = (String) delegateTask.getVariable("objId");
		
		TechapplyService service = SpringContextHolder.getBean(TechapplyService.class);
		Techapply techapply = service.get(id);
		if (techapply == null) {
			logger.error("找不到此资源申请单。");
			return;
		} 
		Office office = techapply.getOffice();
		if (office == null) {
			logger.error("此资源申请单没有指定人力来源部门。");
			return;
		}
		User user = office.getPrimaryPerson();
		if (user == null) {
			logger.error("资源申请单--部门没有指定主负责人。");
			return;
		}
		String loginName = user.getLoginName();
		if (StringUtils.isBlank(loginName)) {
			logger.error("资源申请单--部门主负责人没有加载登录名字段。");
			return;
		}
		delegateTask.setAssignee(loginName);
	}

}
