/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.web.invoice;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.persistence.RespEntity;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.act.entity.Act;
import com.thinkgem.jeesite.modules.act.service.ActTaskService;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoice;
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoiceItem;
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoiceReturn;
import com.thinkgem.jeesite.modules.project.service.invoice.ProjectInvoiceService;
import com.thinkgem.jeesite.modules.sys.utils.ExportUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 项目开票Controller
 * @author jicdata
 * @version 2016-03-08
 */
@Controller
@RequestMapping(value = "${adminPath}/project/invoice")
public class ProjectInvoiceController extends BaseController {

	@Autowired
	private ProjectInvoiceService invoiceService;
	@Autowired
	private ActTaskService actTaskService;

	String prefix = "modules/project/invoice/";
	String vEdit = "InvoiceFormLayer";
	String vViewAudit = "InvoiceView";

    /**
     * 如果把@ModelAttribute放在方法的注解上时，代表的是：该Controller的所有方法在调用前，先执行此@ModelAttribute方法
	 * 绑定键值对到Model中
	 *
     * @param id
     * @return
     */
	@ModelAttribute
	public ProjectInvoice get(@RequestParam(required=false) String id) {
        ProjectInvoice entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = invoiceService.get(id);
		}
		if (entity == null){
			entity = new ProjectInvoice();
		}
		return entity;
	}
	@ModelAttribute
	public ProjectInvoiceItem getItem(@RequestParam(required = false) String id) {
		ProjectInvoiceItem item = null;
		if (StringUtils.isNotBlank(id)){
			item = invoiceService.getItem(id);
		}
		if (item == null){
			item = new ProjectInvoiceItem();
		}
		return item;
	}

	@RequiresPermissions("project:invoice:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProjectInvoice projectInvoice, HttpServletRequest request, HttpServletResponse response, Model model) {
		projectInvoice.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		Page<ProjectInvoice> page = invoiceService.findPage(new Page<ProjectInvoice>(request, response), projectInvoice);
		model.addAttribute("page", page);
		return "modules/project/invoice/InvoiceList";
	}

	/**
	 * 查看审批表单，来源：新建、审批单列表(查看详情)、待办任务列表(act.taskId查看详情)、已办任务列表(act.status查看详情)
	 * @param projectInvoice
	 * @param model
	 * @return
	 */
	@RequiresPermissions("project:invoice:view")
	@RequestMapping(value = "form")
	public String form(ProjectInvoice projectInvoice, Model model) {
		String prefix = "modules/project/invoice/";
		String view = "InvoiceForm";

		// @todo test
		view = "InvoiceFormLayer";
		// form为编辑页面、view为查看、审批页面

		model.addAttribute("projectInvoice", projectInvoice);

		// 待办、已办入口界面传的act是一样的，只是act中的status不一样。

		if (projectInvoice.getIsNewRecord()) {
			// 入口1：新建表单，直接返回空实体
			if (projectInvoice.hasAct()) {
				// 入口2：从已办任务界面来的请求，1、实体是新建的，2、act是activi框架填充的。
				// 此时实体应该由流程id来查询。
				view = vViewAudit; // "projectBiddingView";
				projectInvoice = invoiceService.findByProcInsId(projectInvoice);
				if (projectInvoice == null) {
					projectInvoice = new ProjectInvoice();
				}
				model.addAttribute("projectInvoice", projectInvoice);
			}
			return prefix + view;
		}

		// 入口3：在流程图中配置，从待办任务界面来的请求，entity和act都已加载。
		// 环节编号
		String taskDefKey = projectInvoice.getAct().getTaskDefKey();

		// 查看
		if(projectInvoice.getAct().isFinishTask()){
			view = vViewAudit; // "projectBiddingView";
		}
		// 修改环节
		else if ( UserTaskType.UT_OWNER.equals(taskDefKey) ){
			view = vEdit; // "projectBiddingForm";
		}
		// 某审批环节
		else if ("apply_end".equals(taskDefKey)){
			view = vViewAudit; // "projectBiddingView";  // replace ExecutionAudit
		} else {
			view = vViewAudit; // "projectBiddingView";
		}
		return prefix + view;
	}

	@RequestMapping(value = "addItemView")
	public String addItemView(ProjectInvoiceItem projectInvoiceItem, Model model) {
		String prefix = "modules/project/invoice/";
		String view = "InvoiceItemAdd";

		model.addAttribute("projectInvoiceItem", projectInvoiceItem);
		return prefix + view;
	}

	@RequestMapping(value = "resignView")
	public String resignView(ProjectInvoiceItem projectInvoiceItem, Model model) {
		String prefix = "modules/project/invoice/";
		String view = "InvoiceItemAdd";

		model.addAttribute("projectInvoiceItem", projectInvoiceItem);
		return prefix + view;
	}

	// no used
	@RequestMapping(value = "update")
	public String update(ProjectInvoice projectInvoice, Model model) {
		String prefix = "modules/project/invoice/";
		String view = "InvoiceAdd";

		model.addAttribute("projectInvoice", projectInvoice);
		return prefix + view;
	}

	@RequiresPermissions("project:invoice:admin")
	@RequestMapping(value = "modify")
	public String modify(ProjectInvoice projectInvoice, Model model) {
		model.addAttribute("projectInvoice", projectInvoice);
        List<ProjectInvoice> invoiceList = invoiceService.findListByContractId(projectInvoice);
        model.addAttribute("projectInvoiceList", invoiceList);
		List<ProjectInvoiceReturn> returnList = invoiceService.findReturnByContractId(projectInvoice);
		model.addAttribute("returnList", returnList);
		return prefix + vEdit;
	}

	// no used
	@RequestMapping(value = "returnForm")
	public String returnForm(ProjectInvoice projectInvoice, Model model) {
		model.addAttribute("projectInvoice", projectInvoice);
		List<ProjectInvoice> invoiceList = invoiceService.findListByContractId(projectInvoice);
		model.addAttribute("projectInvoiceList", invoiceList);
		List<ProjectInvoiceReturn> returnList = invoiceService.findReturnByContractId(projectInvoice);
		model.addAttribute("returnList", returnList);

		return "modules/project/invoice/InvoiceViewReturnForm";
	}

	/**
	 * 验证合同号是否重复
	 * @param code
	 * @return
	 */
	@ResponseBody
	// @RequiresPermissions(value={"pur:wzmcgl:add","pur:wzmcgl:edit"},logical= Logical.OR)
	@RequestMapping(value = "hasCode")
	public String hasCode(String oldCode, @RequestParam("contract.contractCode") String code) {
		if (code!=null && code.equals(oldCode)) {
			return "true";
		} else if (code!=null && invoiceService.getItemByContractCode(code) == null) {
			return "true";
		}
		return "false";
	}


	/**
	 * 启动流程、保存申请单、销毁流程、删除申请单。
	 * @param projectInvoice
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("project:invoice:edit")
	@RequestMapping(value = "save")
	// @ResponseBody
	public String save(@RequestBody ProjectInvoice projectInvoice, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, projectInvoice)){
			return form(projectInvoice, model);
		}
		// form提交时，使用act.flag字段
		String flag = projectInvoice.getAct().getFlag();
		// ajax json传输时使用func字段
		flag = projectInvoice.getFunc();

        // flag在前台Form.jsp中传送过来，在些进行判断要进行的操作
		if ("saveOnly".equals(flag)) { // 只保存表单数据
			invoiceService.save(projectInvoice);
		} else if ("saveFinishProcess".equals(flag)) { // 保存并结束流程
			invoiceService.saveFinishProcess(projectInvoice);
		} else {
			invoiceService.saveLaunch(projectInvoice);
		}

		addMessage(redirectAttributes, "保存成功");

		String usertask_owner = projectInvoice.getAct().getTaskDefKey();
		if (UserTaskType.UT_OWNER.equals(usertask_owner)) { // 待办任务页面
			// return "redirect:" + adminPath + "/act/task/todo/";
			return adminPath + "/act/task/todo/";
		} else { // 列表页面
			// return "redirect:"+Global.getAdminPath() + "/project/invoice/?repage";
			return Global.getAdminPath() + "/project/invoice/?repage";
		}
	}

	@RequiresPermissions("project:invoice:edit")
	@RequestMapping(value = "saveAjax")
	@ResponseBody
	public RespEntity saveAjax(@RequestBody ProjectInvoice projectInvoice,
							   Model model, RedirectAttributes redirectAttributes,
							   HttpServletRequest request, HttpServletResponse response) {
		// if (!beanValidator(model, projectInvoice)){
		// 	return form(projectInvoice, model);
		// }
		String path = request.getContextPath();
		// form提交时，使用act.flag字段
		String flag = projectInvoice.getAct().getFlag();
		// ajax json传输时使用func字段
		flag = projectInvoice.getFunc();
		projectInvoice.getAct().setFlag(flag);

		// flag在前台Form.jsp中传送过来，在些进行判断要进行的操作
		if ("saveOnly".equals(flag)) { // 只保存表单数据
			invoiceService.save(projectInvoice);
		} else if ("saveFinishProcess".equals(flag)) { // 保存并结束流程
			invoiceService.saveFinishProcess(projectInvoice);
		} else {
			invoiceService.saveLaunch(projectInvoice);
		}

		addMessage(redirectAttributes, "保存成功");

		String usertask_owner = projectInvoice.getAct().getTaskDefKey();
		String url = "";
		if (UserTaskType.UT_OWNER.equals(usertask_owner)) { // 待办任务页面
			// return "redirect:" + adminPath + "/act/task/todo/";
			url =path + "/" + adminPath + "/act/task/todo/";
		} else { // 列表页面
			// return "redirect:"+Global.getAdminPath() + "/project/invoice/?repage";
			url = path + "/" + Global.getAdminPath() + "/project/invoice/?repage";
		}

		//-2参数错误，-1操作失败，0操作成功，1成功刷新当前页，2成功并跳转到url，3成功并刷新iframe的父界面
		RespEntity respEntity = new RespEntity(2, "成功修改！");
		respEntity.setUrl(url);
		return respEntity;
	}

	@RequestMapping(value = "test1")
	@ResponseBody
	public String test1(@RequestBody ProjectInvoice projectInvoice) {
		String flag = projectInvoice.getDelFlag();
		System.out.println(flag);
		return "test";
	}

	@RequestMapping(value = "test2")
	@ResponseBody
	public String test2(@RequestBody String jsonStr) {
		String flag = jsonStr;
		ProjectInvoice invoice = JsonMapper.getInstance().fromJson(jsonStr, ProjectInvoice.class);
		System.out.println(flag);
		System.out.println(invoice.getId());
		return "test";
	}

	@RequestMapping(value = "test3")
	@ResponseBody
	public String test3() {
		// String flag = jsonStr;
		// ProjectInvoice invoice = JsonMapper.getInstance().fromJson(jsonStr, ProjectInvoice.class);
		// System.out.println(flag);
		System.out.println("test3");
		// System.out.println(invoice.getId());
		return "test3";
	}

	@RequestMapping(value = "test4")
	@ResponseBody
	public String test4() {
		// String flag = jsonStr;
		// ProjectInvoice invoice = JsonMapper.getInstance().fromJson(jsonStr, ProjectInvoice.class);
		// System.out.println(flag);
		System.out.println("test4");
		// System.out.println(invoice.getId());
		return "test4";
	}

	// 批量删除子表
	@RequestMapping(value = "deleteItemByIds")
	@ResponseBody
	public RespEntity deleteItemByIds(@RequestBody String[] ids, HttpServletRequest request, HttpServletResponse response) {
		System.out.println("ids=" + ids);
		invoiceService.deleteItemByIds(ids);

		//-2参数错误，-1操作失败，0操作成功，1成功刷新当前页，2成功并跳转到url，3成功并刷新iframe的父界面
		RespEntity respEntity = new RespEntity(0, "删除成功！");
		// respEntity.setUrl(url);
		return respEntity;
	}



	@RequiresPermissions("project:invoice:edit")
	@RequestMapping(value = "delete")
	public String delete(ProjectInvoice projectInvoice, RedirectAttributes redirectAttributes) {
        invoiceService.delete(projectInvoice);
		addMessage(redirectAttributes, "删除成功");
		return "redirect:"+Global.getAdminPath()+ "/project/invoice/?repage";
	}

    // 审批人使用
	@RequestMapping(value = "saveAudit")
	public String saveAudit(ProjectInvoice projectInvoice, Model model) {
		if (StringUtils.isBlank(projectInvoice.getAct().getFlag())
				|| StringUtils.isBlank(projectInvoice.getAct().getComment())){
			addMessage(model, "请填写审核意见。");
			return form(projectInvoice, model);
		}
		if ("save".equalsIgnoreCase(projectInvoice.getAct().getFlag())) {
			invoiceService.save(projectInvoice);
		} else {
			invoiceService.saveAudit(projectInvoice);
		}

		return "redirect:" + adminPath + "/act/task/todo/";
	}
	
	
	/**
	 * 使用的导出
	 */
	@RequiresPermissions("project:invoice:view")
	@RequestMapping(value = "export")
	public void export(HttpServletRequest request, HttpServletResponse response,Map map) {
        ProjectInvoice projectInvoice=(ProjectInvoice) map.get("projectInvoice");

		List<Act> actList =actTaskService.histoicFlowListPass(projectInvoice.getProcInsId(),null, null);
		String  fileReturnName=projectInvoice.getRemarks() + "_开票审批表";
		String workBookFileRealPathName =request.getSession().getServletContext().getRealPath("/")+"WEB-INF/excel/project/ProjectBidding.xls";
		ExportUtils.export(response, projectInvoice, actList, workBookFileRealPathName, fileReturnName,"yyyy-MM-dd");
	}

//	ajax 请求
	/**
	 * Json形式返回开票申请信息
	 *
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getAsJson")
	public ProjectInvoice getAsJson(@RequestParam(required=false) String id, Model model) {
		model.addAttribute("prjId", id);

		ProjectInvoice projectInvoice = get(id);
		// 转换字典数据
		// apply.setCategory(DictUtils.getDictLabel(apply.getCategory(), "pro_category", ""));

		return projectInvoice;
	}

	// json形式返回开票item信息
	@ResponseBody
	@RequestMapping(value = "getItemAsJson")
	public ProjectInvoiceItem getItemAsJson(@RequestParam(required = false) String id, Model model) {
		model.addAttribute("itemId", id);
		ProjectInvoiceItem item = getItem(id);
		return item;
	}

	/**
	 * ajax，只返回数据
	 * @param projectInvoice
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "table")
	@ResponseBody
	public Page<ProjectInvoice> table(ProjectInvoice projectInvoice, HttpServletRequest request, HttpServletResponse response, Model model) {
		projectInvoice.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		Page<ProjectInvoice> page = invoiceService.findPage(new Page<ProjectInvoice>(request, response), projectInvoice);
		model.addAttribute("page", page);
		// return "modules/apply/external/DemoList";
		return page;
	}

	@RequestMapping(value = "tableItem")
	@ResponseBody
	public Page<ProjectInvoice> tableItem(ProjectInvoice projectInvoice, HttpServletRequest request, HttpServletResponse response, Model model) {
		projectInvoice.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		Page<ProjectInvoice> page = invoiceService.findPage(new Page<ProjectInvoice>(request, response), projectInvoice);
		model.addAttribute("page", page);
		// return "modules/apply/external/DemoList";
		return page;
	}
}