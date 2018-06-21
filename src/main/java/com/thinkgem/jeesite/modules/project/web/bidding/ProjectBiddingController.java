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

	static String prefix = "modules/project/bidding/";
	static String LIST = "projectBiddingList";
	static String VIEW = "projectBiddingView";
	static String EDIT = "projectBiddingForm";
	static String REDIRECT2VIEW = "/project/bidding/projectBidding/?repage";

	@Autowired
	private ProjectBiddingService projectBiddingService;
	@Autowired
	private ActTaskService actTaskService;

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


	/**
     * 如果把@ModelAttribute放在方法的注解上时，
	 * 代表的是：该Controller的所有方法在调用前，先执行此@ModelAttribute方法
     * @param id
     * @return
     */
	@ModelAttribute
	public ProjectBidding get(@RequestParam(required=false) String id) {
		return projectBiddingService.get(id);
	}
	
	@RequiresPermissions("project:bidding:projectBidding:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProjectBidding projectBidding,
					   HttpServletRequest request, HttpServletResponse response, Model model) {
		projectBidding.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		Page<ProjectBidding> page = projectBiddingService.findPage(new Page<>(request, response), projectBidding);
		model.addAttribute("page", page);
		return prefix + LIST;
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
		return prefix + formToView(projectBidding);
	}

	@RequiresPermissions("project:bidding:projectBidding:modify")
	@RequestMapping(value = "modify")
	public String modify(ProjectBidding projectBidding, Model model) {
		// model.addAttribute("projectBidding", projectBidding);
		return prefix + EDIT;
	}

	/**
	 * 启动流程、保存申请单、销毁流程、删除申请单。
	 * @param projectBidding
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("project:bidding:projectBidding:edit")
	@RequestMapping(value = "save")
	public String save(ProjectBidding projectBidding, Model model,
					   RedirectAttributes redirectAttributes) {
		String flag = projectBidding.getAct().getFlag();
		// 如果是暂存，则不校验表单
		if (!"saveOnly".equals(flag)) {
			if (!beanValidator(model, projectBidding)){
				return form(projectBidding, model);
			}
		}
		saveBusi(projectBiddingService, projectBidding);
		addMessage(redirectAttributes, "保存成功!");

		return saveToView(projectBidding);
	}
	
	@RequiresPermissions("project:bidding:projectBidding:edit")
	@RequestMapping(value = "delete")
	public String delete(ProjectBidding projectBidding, RedirectAttributes redirectAttributes) {
		projectBiddingService.delete(projectBidding);
		addMessage(redirectAttributes, "删除成功");
		return "redirect:"+Global.getAdminPath() + REDIRECT2VIEW;
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