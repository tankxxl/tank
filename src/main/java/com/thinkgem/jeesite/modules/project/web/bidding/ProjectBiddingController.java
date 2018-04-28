/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.web.bidding;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.act.entity.Act;
import com.thinkgem.jeesite.modules.act.service.ActTaskService;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.project.entity.bidding.ProjectBidding;
import com.thinkgem.jeesite.modules.project.service.bidding.ProjectBiddingService;
import com.thinkgem.jeesite.modules.sys.utils.ExportUtils;
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
import java.util.List;
import java.util.Map;

/**
 * 项目投标Controller
 * @author jicdata
 * @version 2016-03-08
 */
@Controller
@RequestMapping(value = "${adminPath}/project/bidding/projectBidding")
public class ProjectBiddingController extends BaseController {

	@Autowired
	private ProjectBiddingService projectBiddingService;
	@Autowired
	private ActTaskService actTaskService;

	@ModelAttribute
	public ProjectBidding get(@RequestParam(required=false) String id) {
		return  projectBiddingService.get(id);
	}
	
	@RequiresPermissions("project:bidding:projectBidding:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProjectBidding projectBidding, HttpServletRequest request, HttpServletResponse response, Model model) {
		projectBidding.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		Page<ProjectBidding> page = projectBiddingService.findPage(new Page<>(request, response), projectBidding);
		model.addAttribute("page", page);
		return "modules/project/bidding/projectBiddingList";
	}

	/**
	 * 查看审批表单，来源：审批单列表，待办任务列表
	 * @param projectBidding
	 * @param model
	 * @return
	 */
	@RequiresPermissions("project:bidding:projectBidding:view")
	@RequestMapping(value = "form")
	public String form(ProjectBidding projectBidding, Model model) {
		String prefix = "modules/project/bidding/";
		String view = "projectBiddingForm";

		model.addAttribute("projectBidding", projectBidding);

		// 待办、已办入口界面传的act是一样的，只是act中的status不一样。

		if (projectBidding.getIsNewRecord()) {
			// 入口1：新建表单，直接返回空实体
			if (projectBidding.hasAct()) {
				// 入口2：从已办任务界面来的请求，1、实体是新建的，2、act是activi框架填充的。
				// 此时实体应该由流程id来查询。
				view = "projectBiddingView";
				projectBidding = projectBiddingService.findByProcInsId(projectBidding);
				if (projectBidding == null) {
					projectBidding = new ProjectBidding();
				}
				model.addAttribute("projectBidding", projectBidding);
			}
			return prefix + view;
		}

		// 入口3：在流程图中配置，从待办任务界面来的请求，entity和act都已加载。
		// 环节编号
		String taskDefKey = projectBidding.getAct().getTaskDefKey();

		// 查看
		if(projectBidding.getAct().isFinishTask()){
			view = "projectBiddingView";
		}
		// 修改环节
		else if ( UserTaskType.UT_OWNER.equals(taskDefKey) ){
			view = "projectBiddingForm";
		}
		// 某审批环节
		else if ("apply_end".equals(taskDefKey)){
			view = "projectBiddingView";  // replace ExecutionAudit
		} else {
			view = "projectBiddingView";
		}
		return prefix + view;
	}

	@RequiresPermissions("project:bidding:projectBidding:edit")
	@RequestMapping(value = "modify")
	public String modify(ProjectBidding projectBidding, Model model) {
		return "modules/project/bidding/projectBiddingForm";
	}

	/**
	 * @param projectBidding
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("project:bidding:projectBidding:edit")
	@RequestMapping(value = "save")
	public String save(ProjectBidding projectBidding, Model model, RedirectAttributes redirectAttributes) {
		String flag = projectBidding.getAct().getFlag();
		if (!"saveOnly".equals(flag)) {
			if (!beanValidator(model, projectBidding)){
				return form(projectBidding, model);
			}
		}

		if ("saveOnly".equals(flag)) { // 只保存表单数据
			projectBiddingService.save(projectBidding);
		} else if ("saveFinishProcess".equals(flag)) { // 保存并结束流程
			projectBiddingService.saveFinishProcess(projectBidding);
		} else {
			projectBiddingService.saveLaunch(projectBidding);
		}

		addMessage(redirectAttributes, "保存项目投标成功");
		
		String usertask_owner = projectBidding.getAct().getTaskDefKey();
		if (UserTaskType.UT_OWNER.equals(usertask_owner)) {
			return "redirect:" + adminPath + "/act/task/todo/";
		} else {
			return "redirect:"+Global.getAdminPath()+"/project/bidding/projectBidding/?repage";
		}
	}
	
	@RequiresPermissions("project:bidding:projectBidding:edit")
	@RequestMapping(value = "delete")
	public String delete(ProjectBidding projectBidding, RedirectAttributes redirectAttributes) {
		projectBiddingService.delete(projectBidding);
		addMessage(redirectAttributes, "删除项目投标成功");
		return "redirect:"+Global.getAdminPath()+"/project/bidding/projectBidding/?repage";
	}
	
	@RequestMapping(value = "saveAudit")
	public String saveAudit(ProjectBidding projectBidding, Model model) {
		if (StringUtils.isBlank(projectBidding.getAct().getFlag())
				|| StringUtils.isBlank(projectBidding.getAct().getComment())){
			addMessage(model, "请填写审核意见。");
			return form(projectBidding, model);
		}
		projectBiddingService.saveAudit(projectBidding);
		return "redirect:" + adminPath + "/act/task/todo/";
	}

	/**
	 * 使用的导出
	 * @param request
	 * @param response
	 * @param map
	 * @return
	 */
	@RequiresPermissions("project:bidding:projectBidding:view")
	@RequestMapping(value = "export")
	public void export(HttpServletRequest request, HttpServletResponse response,Map map) {
		ProjectBidding projectBidding=(ProjectBidding) map.get("projectBidding");
		List<Act> actList =actTaskService.histoicFlowListPass(projectBidding.getProcInsId(),null, null);
		String  fileReturnName=projectBidding.getApply().getProjectName()+"_投标审批表";
		String workBookFileRealPathName =request.getSession().getServletContext().getRealPath("/")+"WEB-INF/excel/project/ProjectBidding.xls";
		ExportUtils.export(response, projectBidding, actList, workBookFileRealPathName, fileReturnName,"yyyy-MM-dd");
	}
}