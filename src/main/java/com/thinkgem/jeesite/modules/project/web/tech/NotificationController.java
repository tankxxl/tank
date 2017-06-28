/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.web.tech;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.project.entity.tech.Assigning;
import com.thinkgem.jeesite.modules.project.entity.tech.Notification;
import com.thinkgem.jeesite.modules.project.entity.tech.Techapply;
import com.thinkgem.jeesite.modules.project.entity.tech.Workorder;
import com.thinkgem.jeesite.modules.project.service.tech.AssigningService;
import com.thinkgem.jeesite.modules.project.service.tech.NotificationService;
import com.thinkgem.jeesite.modules.project.service.tech.TechapplyService;
import com.thinkgem.jeesite.modules.project.service.tech.WorkorderService;
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
import java.util.List;

/**
 * 知会Controller
 * @author jicdata:Arthur
 * @version 2016-03-11
 */
@Controller
@RequestMapping(value = "${adminPath}/project/tech/notification")
public class NotificationController extends BaseController {

	@Autowired
	private NotificationService notificationService;
	@Autowired
	private AssigningService assigningService;
	@Autowired
	private TechapplyService techapplyService;
	@Autowired
	private WorkorderService workorderService;
	
	@ModelAttribute
	public Notification get(@RequestParam(required=false) String id) {
		Notification entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = notificationService.get(id);
		}
		if (entity == null){
			entity = new Notification();
		}
		return entity;
	}
	
	@RequiresPermissions("project:tech:notification:view")
	@RequestMapping(value = {"list", ""})
	public String list(Notification notification, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Notification> page = notificationService.findPage(new Page<Notification>(request, response), notification); 
		model.addAttribute("page", page);
		return "modules/project/tech/notificationList";
	}

	@RequiresPermissions("project:tech:notification:view")
	@RequestMapping(value = "form")
	public String form(Notification notification, Model model, HttpServletRequest request) {
		model.addAttribute("notification", notification);
		Assigning assigning = null;
		String assigningId = request.getParameter("assigningId");
		if (null == assigningId || "".equals(assigningId)) {
			assigning = assigningService.get(notification.getAssigning().getId());
		} else {
			assigning = assigningService.get(assigningId);
		}
		model.addAttribute("assigning", assigning);
		Workorder workorder = new Workorder(assigning);
		List<Workorder> workorderList = workorderService.findList(workorder);
		model.addAttribute("workorderList", workorderList);
		Techapply techapply = techapplyService.get(assigning.getTechapply().getId());
		model.addAttribute("techapply", techapply);
		return "modules/project/tech/notificationForm";
	}

	/**
	 * 保存知会；同时设定工期结束
	 * @param notification
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions(value = "project:tech:notification:edit")
	@RequestMapping(value = "save")
	public String save(Notification notification, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		if (!beanValidator(model, notification)){
			return form(notification, model, request);
		}
		if (null == notification.getNotificationDate()) {
			notification.setNotificationDate(new Date());
		}
		notificationService.saveNotification(notification);
		addMessage(redirectAttributes, "保存知会成功");
		return "redirect:"+Global.getAdminPath()+"/project/tech/notification/?repage";
	}

	/**
	 * 修改知会
	 * @param notification
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions(value = "project:tech:notification:edit")
	@RequestMapping(value = "update")
	public String update(Notification notification, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		if (!beanValidator(model, notification)){
			return form(notification, model, request);
		}
		if (null == notification.getNotificationDate()) {
			notification.setNotificationDate(new Date());
		}
		notificationService.save(notification);
		addMessage(redirectAttributes, "知会修改成功");
		return "redirect:"+Global.getAdminPath()+"/project/tech/notification/?repage";
	}

	/**
	 * 派工知会
	 * @param notification
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions(value = "project:tech:notification:reply")
	@RequestMapping(value = "reply")
	public String reply(Notification notification, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		if (!beanValidator(model, notification)){
			return form(notification, model, request);
		}
		if (null == notification.getReplyDate()) {
			notification.setReplyDate(new Date());
		}
		notificationService.save(notification);
		addMessage(redirectAttributes, "知会评论成功");
		return "redirect:"+Global.getAdminPath()+"/project/tech/notification/?repage";
	}


}