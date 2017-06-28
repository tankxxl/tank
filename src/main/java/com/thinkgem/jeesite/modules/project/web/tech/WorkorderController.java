/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.web.tech;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.project.entity.tech.Assigning;
import com.thinkgem.jeesite.modules.project.entity.tech.Techapply;
import com.thinkgem.jeesite.modules.project.entity.tech.Workorder;
import com.thinkgem.jeesite.modules.project.service.tech.AssigningService;
import com.thinkgem.jeesite.modules.project.service.tech.TechapplyService;
import com.thinkgem.jeesite.modules.project.service.tech.WorkorderService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 工单确认Controller
 * @author jicdata:Arthur
 * @version 2016-03-11
 */
@Controller
@RequestMapping(value = "${adminPath}/project/tech/workorder")
public class WorkorderController extends BaseController {

	@Autowired
	private WorkorderService workorderService;
	@Autowired
	private AssigningService assigningService;
	@Autowired
	private TechapplyService techapplyService;
	
	@ModelAttribute
	public Workorder get(@RequestParam(required=false) String id) {
		Workorder entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = workorderService.get(id);
		}
		if (entity == null){
			entity = new Workorder();
		}
		return entity;
	}
	
	@RequiresPermissions("project:tech:workorder:view")
	@RequestMapping(value = {"list", ""})
	public String list(Workorder workorder, HttpServletRequest request, HttpServletResponse response, Model model) {
		workorder.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s", "u3"));
		Page<Workorder> page = workorderService.findPage(new Page<Workorder>(request, response), workorder); 
		model.addAttribute("page", page);
		return "modules/project/tech/workorderList";
	}

	@RequiresPermissions("project:tech:workorder:view")
	@RequestMapping(value = "form")
	public String form(Workorder workorder, Model model) {
		model.addAttribute("workorder", workorder);
		Assigning assigning = assigningService.get(workorder.getAssigning().getId());
		Techapply techapply = techapplyService.get(assigning.getTechapply().getId());
		model.addAttribute("assigning", assigning);
		model.addAttribute("techapply", techapply);
		return "modules/project/tech/workorderForm";
	}

	@RequiresPermissions("project:tech:workorder:edit")
	@RequestMapping(value = "confirm")
	public String confirm(Workorder workorder, Model model) {
		workorder.setConfirmed(DictUtils.getDictValue("已确认", "workorder_confirmed", "未确认"));
		workorderService.save(workorder);
		return "redirect:"+Global.getAdminPath()+"/project/tech/workorder/?repage";
	}
}