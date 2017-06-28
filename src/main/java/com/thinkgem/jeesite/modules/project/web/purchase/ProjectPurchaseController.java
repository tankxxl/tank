/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.web.purchase;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.web.JxlsExcelView;
import com.thinkgem.jeesite.modules.act.entity.Act;
import com.thinkgem.jeesite.modules.act.service.ActTaskService;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.project.entity.execution.ProjectExecution;
import com.thinkgem.jeesite.modules.project.entity.purchase.ProjectPurchase;
import com.thinkgem.jeesite.modules.project.service.execution.ProjectExecutionService;
import com.thinkgem.jeesite.modules.project.service.purchase.ProjectPurchaseService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import com.thinkgem.jeesite.modules.sys.utils.ExportUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目采购Controller
 * @author jicdata
 * @version 2016-03-08
 */
@Controller
@RequestMapping(value = "${adminPath}/project/purchase")
public class ProjectPurchaseController extends BaseController {

	@Autowired
	private ProjectPurchaseService purchaseService;

	@Autowired
	private ProjectExecutionService executionService;

	@Autowired
	private ActTaskService actTaskService;

    /**
     * 如果把@ModelAttribute放在方法的注解上时，代表的是：该Controller的所有方法在调用前，先执行此@ModelAttribute方法
     * @param id
     * @return
     */
	@ModelAttribute
	public ProjectPurchase get(@RequestParam(required=false) String id) {
        ProjectPurchase entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = purchaseService.get(id);
		}
		if (entity == null){
			entity = new ProjectPurchase();
		}
		return entity;
	}

	@RequiresPermissions("project:purchase:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProjectPurchase projectPurchase, HttpServletRequest request,
                       HttpServletResponse response, Model model) {

        projectPurchase.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		Page<ProjectPurchase> page = purchaseService.findPage(new Page<ProjectPurchase>(request, response),
                projectPurchase);
		model.addAttribute("page", page);
		return "modules/project/purchase/PurchaseList";
	}

	/**
	 * 查看审批表单，来源：审批单列表，待办任务列表
	 * @param projectPurchase
	 * @param model
	 * @return
	 */
	@RequiresPermissions("project:purchase:view")
	@RequestMapping(value = "form")
	public String form(ProjectPurchase projectPurchase, Model model) {
		String view = "PurchaseForm";
        String prefix = "modules/project/purchase/";

        model.addAttribute("projectPurchase", projectPurchase);

        if (projectPurchase.getIsNewRecord()) {
			// 从已办任务入口过来，流程实例已结束，只有act对象
			if (projectPurchase.hasAct()) {
				view = "PurchaseView";
				projectPurchase = purchaseService.findByProcInsId(projectPurchase);
				if (projectPurchase == null) {
					projectPurchase = new ProjectPurchase();
				}
			}
            return prefix + view;
        }

        // 环节编号
        String taskDefKey = projectPurchase.getAct().getTaskDefKey();

        // 查看
        if(projectPurchase.getAct().isFinishTask()){
            view = "PurchaseView";
        }
        // 修改环节
        else if ( UserTaskType.UT_OWNER.equals(taskDefKey) ){
            view = "PurchaseForm";
        }
        // 某审批环节
        else if ("apply_end".equals(taskDefKey)){
            view = "PurchaseView";  // PurchaseAudit
        } else {
            view = "PurchaseView";
        }
        return prefix + view;
	}

	@RequestMapping(value = "exec2Purchase")
	public String exec2Purchase(@RequestParam(required=false) String execId, Model model) {
		String view = "PurchaseForm";
		String prefix = "modules/project/purchase/";

		ProjectExecution execution = null;
		if (StringUtils.isNotBlank(execId)){
			execution = executionService.get(execId);
		}
		if (execution == null){
			execution = new ProjectExecution();
		}
		ProjectPurchase projectPurchase = new ProjectPurchase();
		projectPurchase.setExecution(execution);
		projectPurchase.setApply(execution.getApply());
		projectPurchase.setContract(execution.getContract());
		projectPurchase.setContractItem(execution.getContractItem());
		projectPurchase.setContractInfo(execution.getExecutionBasis());

		model.addAttribute("projectPurchase", projectPurchase);

		return prefix + view;
	}

	@RequiresPermissions("project:purchase:admin")
	@RequestMapping(value = "modify")
	public String modify(ProjectPurchase projectPurchase, Model model) {
		model.addAttribute("projectPurchase", projectPurchase);
		return "modules/project/purchase/PurchaseForm";
	}

	/**
	 * 启动流程、保存申请单、销毁流程、删除申请单。
	 * @param projectPurchase
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("project:purchase:edit")
	@RequestMapping(value = "save")
	public String save(ProjectPurchase projectPurchase, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, projectPurchase)){
			return form(projectPurchase, model);
		}
		String flag = projectPurchase.getAct().getFlag();

        // flag在前台Form.jsp中传送过来，在些进行判断要进行的操作
        if ("saveOnly".equals(flag)) { // 只保存表单数据
            purchaseService.save(projectPurchase);
        } else if ("saveFinishProcess".equals(flag)) { // 保存并结束流程
            purchaseService.saveFinishProcess(projectPurchase);
        } else {
            purchaseService.saveLaunch(projectPurchase);
        }

		addMessage(redirectAttributes, "保存项目采购成功");
		
		String usertask_owner = projectPurchase.getAct().getTaskDefKey();
		if (UserTaskType.UT_OWNER.equals(usertask_owner)) { // 待办任务页面
			return "redirect:" + adminPath + "/act/task/todo/";
		} else { // 列表页面
			return "redirect:"+Global.getAdminPath()+"/project/purchase/?repage";
		}
	}
	
	@RequiresPermissions("project:execution:edit")
	@RequestMapping(value = "delete")
	public String delete(ProjectPurchase projectPurchase, RedirectAttributes redirectAttributes) {
		purchaseService.delete(projectPurchase);
		addMessage(redirectAttributes, "删除项目采购成功");
		return "redirect:"+Global.getAdminPath()+"/project/purchase/?repage";
	}
	
	@RequestMapping(value = "saveAudit")
	public String saveAudit(ProjectPurchase projectPurchase, Model model) {
		if (StringUtils.isBlank(projectPurchase.getAct().getFlag())
				|| StringUtils.isBlank(projectPurchase.getAct().getComment())){
			addMessage(model, "请填写审核意见。");
			return form(projectPurchase, model);
		}
		purchaseService.auditSave(projectPurchase);
		return "redirect:" + adminPath + "/act/task/todo/";
	}
	
	
	/**
	 * 使用的导出
	 */
	@RequiresPermissions("project:execution:view")
	@RequestMapping(value = "export")
	public void export(HttpServletRequest request, HttpServletResponse response,Map map) {
        ProjectPurchase projectPurchase=(ProjectPurchase) map.get("projectPurchase");
		List<Act> actList =actTaskService.histoicFlowListPass(projectPurchase.getProcInsId(),null, null);
		String  fileReturnName=projectPurchase.getApply().getProjectName()+"_采购审批表";
		String workBookFileRealPathName =request.getSession().getServletContext().getRealPath("/")+"WEB-INF/excel/project/ProjectPurchase.xls";
		ExportUtils.export(response, projectPurchase, actList, workBookFileRealPathName, fileReturnName,"yyyy-MM-dd");
	}

    @RequestMapping(value = "/export1")
    public ModelAndView export(Map map) {
        Map<String, Object> model = new HashMap();

        ProjectPurchase purchase = (ProjectPurchase) map.get("projectPurchase");
        List<Act> actList =actTaskService.histoicFlowListPass(purchase.getProcInsId(),null, null);

        purchase.setActs(actList);
        String temp = DictUtils.getDictLabel(purchase.getApply().getCustomer().getCustomerCategory(), "customer_category", "");
        model.put("customerCategory", temp);
        temp = DictUtils.getDictLabel(purchase.getApply().getCustomer().getIndustry(), "customer_industry", "");
        model.put("customerIndustry", temp);
        temp = DictUtils.getDictLabel(purchase.getApply().getCategory(), "pro_category", "");
        model.put("projectCategory", temp);

        String  exportFileName = purchase.getApply().getProjectName()+"_采购审批表";

        return new ModelAndView(new JxlsExcelView("ProjectPurchase.xls",exportFileName), model);

    }
}