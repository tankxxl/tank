package com.thinkgem.jeesite.modules.apply.utils;

import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.modules.act.utils.ActUtils;
import com.thinkgem.jeesite.modules.sys.entity.Office;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.OfficeService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * 根据项目类型把此节点分配给不同的部门领导去审批。
 * 
 * @author rgz
 *
 */
@Deprecated
public class ProjectTypeToOfficeTaskListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		// 当前项目类型
		String prjType = (String) delegateTask.getVariable(ActUtils.VAR_PRJ_TYPE);
		OfficeService officeService = SpringContextHolder.getBean(OfficeService.class);
		Office office = new Office();
		
//		键值	标签	类型	描述	排序	操作
//		03	应用开发	pro_category	应用开发	10	修改 删除 添加键值
//		05	系统集成	pro_category	系统集成	20	修改 删除 添加键值
//		08	IDC	pro_category	IDC	30	修改 删除 添加键值
//		10	基础IT	pro_category	基础IT	40	修改 删除 添加键值
//		07	其它
		if ("03".equals(prjType)) {
			office.setName("软件开发部");
		} else if ("05".equals(prjType)) {
			office.setName("服务交付部");
		} else {
			office.setName("服务交付部");
		}
		
		office = officeService.getByName(office);
		User user = UserUtils.get(office.getPrimaryPerson().getId());
		// 把部门领导作为任务的办理者
		delegateTask.setAssignee(user.getLoginName());
		
	}

}
