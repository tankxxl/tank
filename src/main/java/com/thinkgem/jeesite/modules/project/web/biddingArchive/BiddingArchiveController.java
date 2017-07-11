/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.web.biddingArchive;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.act.entity.Act;
import com.thinkgem.jeesite.modules.act.service.ActTaskService;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.project.entity.bidding.ProjectBidding;
import com.thinkgem.jeesite.modules.project.entity.biddingArchive.BiddingArchive;
import com.thinkgem.jeesite.modules.project.service.bidding.ProjectBiddingService;
import com.thinkgem.jeesite.modules.project.service.biddingArchive.BiddingArchiveService;
import com.thinkgem.jeesite.modules.sys.utils.ExportUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.commons.beanutils.BeanUtils;
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
 * 项目投标备案Controller
 * @author jicdata
 * @version 2016-03-08
 */
@Controller
@RequestMapping(value = "${adminPath}/bidding/archive")
public class BiddingArchiveController extends BaseController {

	@Autowired
	private BiddingArchiveService archiveService;
	@Autowired
	private ActTaskService actTaskService;

	@Autowired
	private ProjectBiddingService biddingService;

	private static String prefix = "modules/project/biddingArchive/";

    /**
     * 如果把@ModelAttribute放在方法的注解上时，代表的是：该Controller的所有方法在调用前，先执行此@ModelAttribute方法
     * @param id
     * @return
     */
	@ModelAttribute
	public BiddingArchive get(@RequestParam(required=false) String id) {
		BiddingArchive entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = archiveService.get(id);
		}
		if (entity == null){
			entity = new BiddingArchive();
		}
		return entity;
	}
	
	@RequiresPermissions("project:bidding:projectBidding:view")
	@RequestMapping(value = {"list", ""})
	public String list(BiddingArchive archive,
					   HttpServletRequest request, HttpServletResponse response, Model model) {
		archive.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		Page<BiddingArchive> page = archiveService.findPage(new Page<BiddingArchive>(request, response), archive);
		model.addAttribute("page", page);
		return prefix + "BiddingArchiveList";
	}

	/**
	 * 查看审批表单，来源：审批单列表，待办任务列表
	 * @param archive
	 * @param model
	 * @return
	 */
	@RequiresPermissions("project:bidding:projectBidding:view")
	@RequestMapping(value = "form")
	public String form(BiddingArchive archive, Model model) {
		String view = "BiddingArchiveForm";

		model.addAttribute("biddingArchive", archive);

		// 待办、已办入口界面传的act是一样的，只是act中的status不一样。

		if (archive.getIsNewRecord()) {
			// 入口1：新建表单，直接返回空实体
			if (archive.hasAct()) {
				// 入口2：从已办任务界面来的请求，1、实体是新建的，2、act是activi框架填充的。
				// 此时实体应该由流程id来查询。
				view = "BiddingArchiveView";
				archive = archiveService.findByProcInsId(archive);
				if (archive == null) {
					archive = new BiddingArchive();
				}
			}
			return prefix + view;
		}

		// 入口3：在流程图中配置，从待办任务界面来的请求，entity和act都已加载。
		// 环节编号
		String taskDefKey = archive.getAct().getTaskDefKey();

		// 查看
		if(archive.getAct().isFinishTask()){
			view = "BiddingArchiveView";
		}
		// 修改环节
		else if ( UserTaskType.UT_OWNER.equals(taskDefKey) ){
			view = "BiddingArchiveForm";
		}
		// 某审批环节
		else if ("apply_end".equals(taskDefKey)){
			view = "BiddingArchiveView";  // replace ExecutionAudit
		} else {
			view = "BiddingArchiveView";
		}
		return prefix + view;
	}

	// 新增投标备案审批入口，投标备案审批是在投标处添加的
	// 所以此处把Bidding实体查询出来，再新建一个Archive实体，把Archive跟这个Bidding实体关联起来就ok了。
	@RequestMapping(value = "bidding2archive")
	public String bidding2archive(@RequestParam(required = false) String biddingId, Model model) {

		ProjectBidding bidding = null;
		if (StringUtils.isNotBlank(biddingId)) {
			bidding = biddingService.get(biddingId);
		}
		if (bidding == null) {
			bidding = new ProjectBidding();
		}
		BiddingArchive archive = new BiddingArchive();

		try {
			// 拷贝属性
			BeanUtils.copyProperties(archive, bidding);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 新
		archive.setId(null);
		archive.setProcInsId(null);
		archive.setProcStatus(null);

		model.addAttribute("biddingArchive", archive);

		return prefix + "BiddingArchiveForm";
	}

	@RequiresPermissions("project:bidding:projectBidding:modify")
	@RequestMapping(value = "modify")
	public String modify(BiddingArchive archive, Model model) {
		model.addAttribute("biddingArchive", archive);
		return prefix + "BiddingArchiveForm";
	}

	/**
	 * 启动流程、保存申请单、销毁流程、删除申请单。
	 * @param archive
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("project:bidding:projectBidding:edit")
	@RequestMapping(value = "save")
	public String save(BiddingArchive archive, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, archive)){
			return form(archive, model);
		}
		String flag = archive.getAct().getFlag();

//		flag在前台Form.jsp中传送过来，在些进行判断要进行的操作
		if ("saveOnly".equals(flag)) { // 只保存表单数据
			archiveService.save(archive);
		} else if ("saveFinishProcess".equals(flag)) { // 保存并结束流程
			archiveService.saveFinishProcess(archive);
		} else {
			archiveService.saveLaunch(archive);
		}

		addMessage(redirectAttributes, "保存投标备案成功！");
		
		String usertask_owner = archive.getAct().getTaskDefKey();
		if (UserTaskType.UT_OWNER.equals(usertask_owner)) { // 待办任务页面
			return "redirect:" + adminPath + "/act/task/todo/";
		} else { // 列表页面
			return "redirect:"+Global.getAdminPath()+"/bidding/archive/?repage";
		}
	}
	
	@RequiresPermissions("project:bidding:projectBidding:edit")
	@RequestMapping(value = "delete")
	public String delete(BiddingArchive archive, RedirectAttributes redirectAttributes) {
		archiveService.delete(archive);
		addMessage(redirectAttributes, "删除成功!");
		return "redirect:"+Global.getAdminPath()+"/bidding/archive/?repage";
	}
	
	@RequestMapping(value = "saveAudit")
	public String saveAudit(BiddingArchive archive, Model model) {
		if (StringUtils.isBlank(archive.getAct().getFlag())
				|| StringUtils.isBlank(archive.getAct().getComment())){
			addMessage(model, "请填写审核意见。");
			return form(archive, model);
		}
		archiveService.saveAudit(archive);
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