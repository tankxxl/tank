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
import com.thinkgem.jeesite.modules.project.entity.execution.ProjectExecution;
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

	static String prefix = "modules/project/finish/";
	static String LIST = "projectFinishApprovalList";

	static String VIEW = "projectFinishApprovalView";
	static String EDIT = "projectFinishApprovalForm";
	static String REDIRECT2VIEW = "/project/finish/projectFinishApproval/?repage";

	@Autowired
	private ProjectFinishApprovalService projectFinishApprovalService;

	// 相关界面
	// 保存结束-->跳转到controller的默认列表路径
	@Override
	protected String getRedirectView() {
		return REDIRECT2VIEW;
	}

	// 查看表单页面-->查看、审批共用，页面中使用taskId、taskDefKey等来控制界面，提交到saveAudit
	@Override
	protected String getView() {
		return VIEW;
	}

	// 编辑表单页面-->新建、修改、驳回修改共用，提交到save
	@Override
	protected String getEdit() {
		return EDIT;
	}

	@ModelAttribute
	public ProjectFinishApproval get(@RequestParam(required=false) String id) {
		return projectFinishApprovalService.get(id);
	}
	
	@RequiresPermissions("project:finish:projectFinishApproval:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProjectFinishApproval projectFinishApproval,
					   HttpServletRequest request, HttpServletResponse response, Model model) {
		projectFinishApproval.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		Page<ProjectFinishApproval> page = projectFinishApprovalService.findPage(new Page<>(request, response), projectFinishApproval);
		model.addAttribute("page", page);
		return prefix + LIST;
	}

	@RequiresPermissions("project:finish:projectFinishApproval:view")
	@RequestMapping(value = "form")
	public String form(ProjectFinishApproval projectFinishApproval, Model model) {
		return prefix + formToView(projectFinishApproval);
	}

	@RequiresPermissions("project:finish:projectFinishApproval:edit")
	@RequestMapping(value = "save")
	public String save(ProjectFinishApproval projectFinishApproval, Model model,
					   RedirectAttributes redirectAttributes) {

		String flag = projectFinishApproval.getAct().getFlag();
		if (!"saveOnly".equals(flag)) {
			if (!beanValidator(model, projectFinishApproval)) {
				return form(projectFinishApproval, model);
			}
		}

		saveBusi(projectFinishApprovalService, projectFinishApproval);
		addMessage(redirectAttributes, "保存成功!");
		return saveToView(projectFinishApproval);
	}
	
	@RequiresPermissions("project:finish:projectFinishApproval:edit")
	@RequestMapping(value = "delete")
	public String delete(ProjectFinishApproval projectFinishApproval, RedirectAttributes redirectAttributes) {
		projectFinishApprovalService.delete(projectFinishApproval);
		addMessage(redirectAttributes, "删除成功!");
		return "redirect:"+Global.getAdminPath()+ REDIRECT2VIEW;
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