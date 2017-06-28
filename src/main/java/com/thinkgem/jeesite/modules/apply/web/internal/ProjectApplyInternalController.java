/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.apply.web.internal;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.apply.entity.internal.ProjectApplyInternal;
import com.thinkgem.jeesite.modules.apply.service.internal.ProjectApplyInternalService;
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
 * 内部立项申请Controller
 * @author jicdata
 * @version 2016-02-23
 */
@Controller
@RequestMapping(value = "${adminPath}/apply/internal/projectApplyInternal")
public class ProjectApplyInternalController extends BaseController {

	@Autowired
	private ProjectApplyInternalService projectApplyInternalService;
	
	@ModelAttribute
	public ProjectApplyInternal get(@RequestParam(required=false) String id) {
		ProjectApplyInternal entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = projectApplyInternalService.get(id);
		}
		if (entity == null){
			entity = new ProjectApplyInternal();
		}
		return entity;
	}
	
	@RequiresPermissions("apply:internal:projectApplyInternal:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProjectApplyInternal projectApplyInternal, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ProjectApplyInternal> page = projectApplyInternalService.findPage(new Page<ProjectApplyInternal>(request, response), projectApplyInternal); 
		model.addAttribute("page", page);
		return "modules/apply/internal/projectApplyInternalList";
	}

	@RequiresPermissions("apply:internal:projectApplyInternal:view")
	@RequestMapping(value = "form")
	public String form(ProjectApplyInternal projectApplyInternal, Model model) {
		model.addAttribute("projectApplyInternal", projectApplyInternal);
		return "modules/apply/internal/projectApplyInternalForm";
	}

	@RequiresPermissions("apply:internal:projectApplyInternal:edit")
	@RequestMapping(value = "save")
	public String save(ProjectApplyInternal projectApplyInternal, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, projectApplyInternal)){
			return form(projectApplyInternal, model);
		}
		projectApplyInternalService.save(projectApplyInternal);
		addMessage(redirectAttributes, "保存内部立项申请成功");
		return "redirect:"+Global.getAdminPath()+"/apply/internal/projectApplyInternal/?repage";
	}
	
	@RequiresPermissions("apply:internal:projectApplyInternal:edit")
	@RequestMapping(value = "delete")
	public String delete(ProjectApplyInternal projectApplyInternal, RedirectAttributes redirectAttributes) {
		projectApplyInternalService.delete(projectApplyInternal);
		addMessage(redirectAttributes, "删除内部立项申请成功");
		return "redirect:"+Global.getAdminPath()+"/apply/internal/projectApplyInternal/?repage";
	}

}