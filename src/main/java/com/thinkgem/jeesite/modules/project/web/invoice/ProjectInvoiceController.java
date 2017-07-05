/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.web.invoice;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.act.entity.Act;
import com.thinkgem.jeesite.modules.act.service.ActTaskService;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoice;
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoiceReturn;
import com.thinkgem.jeesite.modules.project.service.invoice.ProjectInvoiceService;
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
import java.util.Iterator;
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

    /**
     * 如果把@ModelAttribute放在方法的注解上时，代表的是：该Controller的所有方法在调用前，先执行此@ModelAttribute方法
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
	
	@RequiresPermissions("project:invoice:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProjectInvoice projectInvoice, HttpServletRequest request, HttpServletResponse response, Model model) {
		projectInvoice.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		Page<ProjectInvoice> page = invoiceService.findPage(new Page<ProjectInvoice>(request, response), projectInvoice);
		model.addAttribute("page", page);
		return "modules/project/invoice/InvoiceList";
	}

	/**
	 * 查看审批表单，来源：审批单列表，待办任务列表
	 * @param projectInvoice
	 * @param model
	 * @return
	 */
	@RequiresPermissions("project:invoice:view")
	@RequestMapping(value = "form")
	public String form(ProjectInvoice projectInvoice, Model model) {
        String prefix = "modules/project/invoice/";
        String view = "InvoiceForm";

        model.addAttribute("projectInvoice", projectInvoice);
		// 已开票信息，从开票申请单里找
		List<ProjectInvoice> invoiceList;

		// 已回款信息，从回款表中找
		List<ProjectInvoiceReturn> returnList;


        if (projectInvoice.getIsNewRecord()) {

			// 从已办任务入口过来，流程实例已结束，只有act对象
			if (projectInvoice.hasAct()) {
				// view = "InvoiceView";
				view = "InvoiceViewReturnForm";
				projectInvoice = invoiceService.findByProcInsId(projectInvoice);
				if (projectInvoice == null) {
					projectInvoice = new ProjectInvoice();
				}
				invoiceList = invoiceService.findListByContractId(projectInvoice);
				model.addAttribute("projectInvoiceList", invoiceList);
				returnList = invoiceService.findReturnByContractId(projectInvoice);
				model.addAttribute("returnList", returnList);
			}

            return prefix + view;
        }

		invoiceList = invoiceService.findListByContractId(projectInvoice);
		model.addAttribute("projectInvoiceList", invoiceList);

		Iterator<ProjectInvoice> it = invoiceList.iterator();
		ProjectInvoice invoice = null;
		while (it.hasNext()) {
			invoice = it.next();
			if (invoice.getInvoiceDate() == null) {
				it.remove();
			}
		}

		returnList = invoiceService.findReturnByContractId(projectInvoice);
		model.addAttribute("returnList", returnList);

        // 环节编号
        String taskDefKey = projectInvoice.getAct().getTaskDefKey();

        // 查看
        if(projectInvoice.getAct().isFinishTask()){
            // view = "InvoiceView";
			view = "InvoiceViewReturnForm";
        }
        // 修改环节
        else if ( UserTaskType.UT_OWNER.equals(taskDefKey) ){
            view = "InvoiceForm";
        }
        // usertask_invoice -财务部专员8-开票-填写开票日期
		else if ("usertask_invoice".equalsIgnoreCase(taskDefKey)) {
			view = "InvoiceViewReturnForm";
		}
        // usertask_return -财务部专员10-录入回款信息(回款金额、回款日期)
		else if ("usertask_return".equalsIgnoreCase(taskDefKey)) {
			view = "InvoiceViewReturnForm";
		}
		// usertask_inout -商务部专员2-提供商务信息，录入是否需要出入库
		else if ("usertask_inout".equalsIgnoreCase(taskDefKey)) {
			view = "InvoiceViewReturnForm";
		}

		// test usertask1
		// else if ("usertask1".equalsIgnoreCase(taskDefKey) || "usertask9".equalsIgnoreCase(taskDefKey)) {
		// 	view = "InvoiceViewReturnForm";
		// }
        // 某审批环节
        else if ("apply_end".equals(taskDefKey)){
            // view = "InvoiceView";  // InvoiceAudit
			view = "InvoiceViewReturnForm";
        } else {
            // view = "InvoiceView";
			view = "InvoiceViewReturnForm";
        }
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
		return "modules/project/invoice/InvoiceForm";
	}

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
	 * 启动流程、保存申请单、销毁流程、删除申请单。
	 * @param projectInvoice
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("project:invoice:edit")
	@RequestMapping(value = "save")
	public String save(ProjectInvoice projectInvoice, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, projectInvoice)){
			return form(projectInvoice, model);
		}
		String flag = projectInvoice.getAct().getFlag();

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
			return "redirect:" + adminPath + "/act/task/todo/";
		} else { // 列表页面
			return "redirect:"+Global.getAdminPath() + "/project/invoice/?repage";
		}
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
		String  fileReturnName=projectInvoice.getApply().getProjectName()+"_合同执行审批表";
		String workBookFileRealPathName =request.getSession().getServletContext().getRealPath("/")+"WEB-INF/excel/project/ProjectBidding.xls";
		ExportUtils.export(response, projectInvoice, actList, workBookFileRealPathName, fileReturnName,"yyyy-MM-dd");
	}
}