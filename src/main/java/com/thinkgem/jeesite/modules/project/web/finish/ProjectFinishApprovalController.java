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
		String view = "projectFinishApprovalForm";
		
		// 查看审批申请单
		if (StringUtils.isNotBlank(projectFinishApproval.getId())){//.getAct().getProcInsId())){

			// 环节编号
			String taskDefKey = projectFinishApproval.getAct().getTaskDefKey();
			// 查看工单
			if(projectFinishApproval.getAct().isFinishTask()){
				
				view = "projectFinishApprovalView";
			}	
			// 修改环节
			else if ( UserTaskType.UT_OWNER.equals(taskDefKey) ) {
				view = "projectFinishApprovalForm";
			} else {
				view = "projectFinishApprovalAudit";
			}
		}
		model.addAttribute("projectFinishApproval", projectFinishApproval);
		return "modules/project/finish/" + view;
//		return "modules/project/finish/projectFinishApprovalForm";
	}

	@RequiresPermissions("project:finish:projectFinishApproval:edit")
	@RequestMapping(value = "save")
	public String save(ProjectFinishApproval projectFinishApproval, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, projectFinishApproval)){
			return form(projectFinishApproval, model);
		}

		String flag = projectFinishApproval.getAct().getFlag();
		if (StringUtils.isBlank(flag)) {
			projectFinishApprovalService.onlySave(projectFinishApproval);
		} else {
			projectFinishApprovalService.save(projectFinishApproval);
		}

//		projectFinishApprovalService.save(projectFinishApproval);
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
		
		projectFinishApprovalService.auditSave(projectFinishApproval);
		return "redirect:" + adminPath + "/act/task/todo/";
	}
	
	

}