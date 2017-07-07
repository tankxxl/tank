/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.web.tech;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.project.entity.tech.Assigning;
import com.thinkgem.jeesite.modules.project.entity.tech.Techapply;
import com.thinkgem.jeesite.modules.project.service.tech.AssigningService;
import com.thinkgem.jeesite.modules.project.service.tech.TechapplyService;
import com.thinkgem.jeesite.modules.project.service.tech.WorkorderService;
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
import java.util.Date;

/**
 * 资源申请Controller
 * @author jicdata:Arthur
 * @version 2016-03-11
 */
@Controller
@RequestMapping(value = "${adminPath}/project/tech/techapply")
public class TechapplyController extends BaseController {

	@Autowired
	private TechapplyService techapplyService;
	@Autowired
	private AssigningService assigningService;
	
	@Autowired
	private WorkorderService workorderService;
	
	@ModelAttribute
	public Techapply get(@RequestParam(required=false) String id) {
		Techapply entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = techapplyService.get(id);
		}
		if (entity == null){
			entity = new Techapply();
		}
		return entity;
	}
	
	@RequiresPermissions("project:tech:techapply:view")
	@RequestMapping(value = {"list", ""})
	public String list(Techapply techapply, HttpServletRequest request, HttpServletResponse response, Model model) {
		techapply.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "o1", "u"));
		/*
		 * 临时策略(Java中拼SQL，实现数据权限上的扩充)
		 * 修改数据权限SQL
		 */
		String authSql = techapply.getSqlMap().get("dsf");
		String additionSql = "OR o5.primary_person = '" + UserUtils.getUser().getId() + "'";
		if (!"".equals(authSql)) {
			authSql = authSql.substring(0, authSql.length() - 1) + " " + additionSql + ")";
		}
		techapply.getSqlMap().put("dsf", authSql);
		Page<Techapply> page = techapplyService.findPage(new Page<Techapply>(request, response), techapply); 
		model.addAttribute("page", page);
		return "modules/project/tech/techapplyList";
	}

	/**
	 * 流程界面路由
	 * @param techapply
	 * @param model
	 * @return
	 */
	@RequiresPermissions("project:tech:techapply:view")
	@RequestMapping(value = "form")
	public String form(Techapply techapply, Model model) {
		String view = "techapplyForm";
		if (StringUtils.isNotBlank(techapply.getId())) {
			// 环节编号
			String taskDefKey = techapply.getAct().getTaskDefKey();
			
			if(techapply.getAct().isFinishTask()){
//				techapply.getId();
//				Techapply techapply1 = techapplyService.get(techapply.getId());
//				Assigning assigning = assigningService.get(techapply1.get);
				
				
//				Workorder workorder = workorderService.get(manhour.getWorkorder().getId());
//		        Assigning assigning = assigningService.get(workorder.getAssigning().getId());
//		        Techapply techapply = techapplyService.get(assigning.getTechapply().getId());
//		        model.addAttribute("workorder", workorder);
//		        model.addAttribute("assigning", assigning);
//		        model.addAttribute("techapply", techapply);
				
//				assigningService.get
//				workorderService.get(entity)
				view = "techapplyView";
			}	
			// 修改环节
			else if ( UserTaskType.UT_OWNER.equals(taskDefKey) ) {
				view = "techapplyForm";
			}
			// 某审批环节
			else if ("apply_end".equals(taskDefKey)) {
				view = "techapplyAudit";
			} else {
				view = "techapplyAudit";
			}
		}

		model.addAttribute("techapply", techapply);
		return "modules/project/tech/" + view;
	}

	@RequiresPermissions("project:tech:techapply:edit")
	@RequestMapping(value = "save")
	public String save(Techapply techapply, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, techapply)){
			return form(techapply, model);
		}
		techapplyService.saveLaunch(techapply);
		addMessage(redirectAttributes, "保存资源申请成功");
		
		String usertask_owner = techapply.getAct().getTaskDefKey();
		if (UserTaskType.UT_OWNER.equals(usertask_owner)) { // 待办任务页面
			return "redirect:" + adminPath + "/act/task/todo/";
		} else { // 列表页面
			return "redirect:"+Global.getAdminPath()+"/project/tech/techapply/?repage";
		}
		
	}
	
	@RequiresPermissions("project:tech:techapply:edit")
	@RequestMapping(value = "delete")
	public String delete(Techapply techapply, RedirectAttributes redirectAttributes) {
		techapplyService.delete(techapply);
		addMessage(redirectAttributes, "删除资源申请成功");
		return "redirect:"+Global.getAdminPath()+"/project/tech/techapply/?repage";
	}
	
	
	public void saveAssigning(Assigning assigning, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		assigningService.save(assigning);
		addMessage(redirectAttributes, "保存派工信息成功");
	}
	
	@RequestMapping(value = "saveAudit")
	public String saveAudit(Techapply techapply,Assigning assigning, Model model) {
		if (StringUtils.isBlank(techapply.getAct().getFlag())
				|| StringUtils.isBlank(techapply.getAct().getComment())){
			addMessage(model, "请填写审核意见。");
			return form(techapply, model);
		}
		if(techapply.getAct().getFlagBoolean()){
			assigning.setId(null);
			assigning.setTechapply(techapply);
			assigning.setAssigningDate(new Date());
			assigning.setAssigningor(UserUtils.getUser());
			assigningService.save(assigning);
		}
		
		techapplyService.saveAudit(techapply);
		return "redirect:" + adminPath + "/act/task/todo/";
	}
}