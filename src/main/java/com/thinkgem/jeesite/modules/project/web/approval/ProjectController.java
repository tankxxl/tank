/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.web.approval;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.project.entity.approval.Project;
import com.thinkgem.jeesite.modules.project.service.approval.ProjectService;
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
 * 立项Controller
 * @author jicdata
 * @version 2016-02-22
 */
@Controller
@RequestMapping(value = "${adminPath}/project/approval/project")
public class ProjectController extends BaseController {

	@Autowired
	private ProjectService projectService;
	
	@ModelAttribute
	public Project get(@RequestParam(required=false) String id) {
		Project entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = projectService.get(id);
		}
		if (entity == null){
			entity = new Project();
		}
		return entity;
	}
	
	@RequiresPermissions("project:approval:project:view")
	@RequestMapping(value = {"list", ""})
	public String list(Project project, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Project> page = projectService.findPage(new Page<Project>(request, response), project); 
		model.addAttribute("page", page);
		return "modules/project/approval/projectList";
	}

	@RequiresPermissions("project:approval:project:view")
	@RequestMapping(value = "form")
	public String form(Project project, Model model) {
		model.addAttribute("project", project);
		return "modules/project/approval/projectForm";
	}

	@RequiresPermissions("project:approval:project:edit")
	@RequestMapping(value = "save")
	public String save(Project project, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, project)){
			return form(project, model);
		}
		projectService.save(project);
		addMessage(redirectAttributes, "保存立项成功");
		return "redirect:"+Global.getAdminPath()+"/project/approval/project/?repage";
	}
	
	@RequiresPermissions("project:approval:project:edit")
	@RequestMapping(value = "delete")
	public String delete(Project project, RedirectAttributes redirectAttributes) {
		projectService.delete(project);
		addMessage(redirectAttributes, "删除立项成功");
		return "redirect:"+Global.getAdminPath()+"/project/approval/project/?repage";
	}

}