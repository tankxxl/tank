/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.web.finish;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.project.entity.finish.ProjectFinishApproval;
import com.thinkgem.jeesite.modules.project.service.finish.ProjectFinishApprovalService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 结项Controller
 * @author jicdata
 * @version 2016-03-11
 */
@Controller
@RequestMapping(value = "${adminPath}/project/finish/projectFinishApproval")
public class ProjectFinishApprovalController extends BaseController {

	@Autowired
	private ProjectFinishApprovalService projectFinishApprovalService;
	
	@ModelAttribute
	public ProjectFinishApproval get(@RequestParam(required=false) String id) {
		ProjectFinishApproval entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = projectFinishApprovalService.get(id);
		}
		if (entity == null){
			entity = new ProjectFinishApproval();
		}
		return entity;
	}
	
	@RequiresPermissions("project:finish:projectFinishApproval:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProjectFinishApproval projectFinishApproval, HttpServletRequest request, HttpServletResponse response, Model model) {
		projectFinishApproval.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		Page<ProjectFinishApproval> page = projectFinishApprovalService.findPage(new Page<ProjectFinishApproval>(request, response), projectFinishApproval); 
		model.addAttribute("page", page);
		return "modules/project/finish/projectFinishApprovalList";
	}

	@RequiresPermissions("project:finish:projectFinishApproval:view")
	@RequestMapping(value = "form")
	public String form(ProjectFinishApproval projectFinishApproval, Model model) {
		String prefix = "modules/project/finish/";
		String view = "projectFinishApprovalForm";

		model.addAttribute("projectFinishApproval", projectFinishApproval);

		// 待办、已办入口界面传的act是一样的，只是act中的status不一样。

		if (projectFinishApproval.getIsNewRecord()) {
			// 入口1：新建表单，直接返回空实体
			if (projectFinishApproval.hasAct()) {
				// 入口2：从已办任务界面来的请求，1、实体是新建的，2、act是activi框架填充的。
				// 此时实体应该由流程id来查询。
				view = "ExecutionView";
				projectFinishApproval = projectFinishApprovalService.findByProcInsId(projectFinishApproval);
				if (projectFinishApproval == null) {
					projectFinishApproval = new ProjectFinishApproval();
				}
				model.addAttribute("projectFinishApproval", projectFinishApproval);
			}
			return prefix + view;
		}

		// 入口3：在流程图中配置，从待办任务界面来的请求，entity和act都已加载。
		// 环节编号
		String taskDefKey = projectFinishApproval.getAct().getTaskDefKey();

		// 查看
		if(projectFinishApproval.getAct().isFinishTask()){
			view = "projectFinishApprovalView";
		}
		// 修改环节
		else if ( UserTaskType.UT_OWNER.equals(taskDefKey) ){
			view = "projectFinishApprovalForm";
		}
		// 某审批环节
		else if ("apply_end".equals(taskDefKey)){
			view = "projectFinishApprovalView";  // replace ExecutionAudit
		} else {
			view = "projectFinishApprovalView";
		}
		return prefix + view;
	}

	// RedirectAttributes实现参数传递
	@RequiresPermissions("project:finish:projectFinishApproval:edit")
	@RequestMapping(value = "save")
	public String save(ProjectFinishApproval projectFinishApproval, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, projectFinishApproval)){
			return form(projectFinishApproval, model);
		}

		String flag = projectFinishApproval.getAct().getFlag();
//		flag在前台Form.jsp中传送过来，在些进行判断要进行的操作
		if ("saveOnly".equals(flag)) { // 只保存表单数据
			projectFinishApprovalService.save(projectFinishApproval);
		} else if ("saveFinishProcess".equals(flag)) { // 保存并结束流程
			projectFinishApprovalService.saveFinishProcess(projectFinishApproval);
		} else {
			projectFinishApprovalService.saveLaunch(projectFinishApproval);
		}
		addMessage(redirectAttributes, "保存结项审批成功");
		
		String usertask_owner = projectFinishApproval.getAct().getTaskDefKey();
		if (UserTaskType.UT_OWNER.equals(usertask_owner)) { // 待办任务页面
			return "redirect:" + adminPath + "/act/task/todo/";
		} else { // 列表页面
			return "redirect:"+Global.getAdminPath()+"/project/finish/projectFinishApproval/?repage";
		}
		
	}
	
	@RequiresPermissions("project:finish:projectFinishApproval:edit")
	@RequestMapping(value = "delete")
	public String delete(ProjectFinishApproval projectFinishApproval, RedirectAttributes redirectAttributes) {
		projectFinishApprovalService.delete(projectFinishApproval);
		addMessage(redirectAttributes, "删除结项审批成功");
		return "redirect:"+Global.getAdminPath()+"/project/finish/projectFinishApproval/?repage";
	}
	
	@RequestMapping(value = "saveAudit")
	public String saveAudit(ProjectFinishApproval projectFinishApproval, Model model) {
		if (StringUtils.isBlank(projectFinishApproval.getAct().getFlag())
				|| StringUtils.isBlank(projectFinishApproval.getAct().getComment())){
			addMessage(model, "请填写审核意见。");
			return form(projectFinishApproval, model);
		}
		
		projectFinishApprovalService.saveAudit(projectFinishApproval);
		return "redirect:" + adminPath + "/act/task/todo/";
	}
	
	

}