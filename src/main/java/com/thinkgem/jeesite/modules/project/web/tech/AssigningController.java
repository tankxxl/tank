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
import com.thinkgem.jeesite.modules.project.service.tech.AssigningService;
import com.thinkgem.jeesite.modules.project.service.tech.TechapplyService;
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
 * 派工Controller
 * @author jicdata:Arthur
 * @version 2016-03-11
 */
@Controller
@RequestMapping(value = "${adminPath}/project/tech/assigning")
public class AssigningController extends BaseController {

	@Autowired
	private TechapplyService techapplyService;
	@Autowired
	private AssigningService assigningService;
	
	@ModelAttribute
	public Assigning get(@RequestParam(required=false) String id) {
		Assigning entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = assigningService.get(id);
		}
		if (entity == null){
			entity = new Assigning();
		}
		return entity;
	}
	
	@RequiresPermissions("project:tech:assigning:view")
	@RequestMapping(value = {"list", ""})
	public String list(Assigning assigning, HttpServletRequest request, HttpServletResponse response, Model model) {
		assigning.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s", "u"));
		Page<Assigning> page = assigningService.findPage(new Page<Assigning>(request, response), assigning);
		model.addAttribute("page", page);
		return "modules/project/tech/assigningList";
	}

	/**
	 * 根据techapplyId区分派工发起(由资源申请页面按引起)/修改
	 * @param assigning
	 * @param model
	 * @param request
	 * @return
	 */
	@RequiresPermissions("project:tech:assigning:view")
	@RequestMapping(value = "form")
	public String form(Assigning assigning, Model model, HttpServletRequest request) {
		model.addAttribute("assigning", assigning);
		Techapply techapply = null;
		String techapplyId = request.getParameter("techapplyId");
		if (null == techapplyId || "".equals(techapplyId)) {
			techapply = techapplyService.get(assigning.getTechapply().getId());
		} else {
			techapply = techapplyService.get(techapplyId);
		}
		model.addAttribute("techapply", techapply);
		return "modules/project/tech/assigningForm";
	}

	@RequiresPermissions("project:tech:assigning:edit")
	@RequestMapping(value = "save")
	public String save(Assigning assigning, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		if (!beanValidator(model, assigning)){
			return form(assigning, model, request);
		}
		assigningService.save(assigning);
		addMessage(redirectAttributes, "保存派工信息成功");
		return "redirect:"+Global.getAdminPath()+"/project/tech/assigning/?repage";
	}
}