/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.web.execution;

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
import com.thinkgem.jeesite.modules.project.service.execution.ProjectExecutionService;
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
 * 项目合同执行Controller
 *
 * @author jicdata
 * @version 2016-03-08
 */
@Controller
@RequestMapping(value = "${adminPath}/project/execution")
public class ProjectExecutionController extends BaseController {

	@Autowired
	private ProjectExecutionService executionService;
	@Autowired
	private ActTaskService actTaskService;

    /**
     * @param id
     * @return
     */
	@ModelAttribute
	public ProjectExecution get(@RequestParam(required=false) String id) {
		return executionService.get(id);
	}

	@RequiresPermissions("project:execution:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProjectExecution projectExecution,
                       HttpServletRequest request,
                       HttpServletResponse response,
                       Model model) {
        projectExecution.getSqlMap().put("dsf",
                BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		Page<ProjectExecution> page = executionService.findPage(new Page<>(request, response),
                projectExecution);
		model.addAttribute("page", page);
		return "modules/project/execution/ExecutionList";
	}

	/**
	 * 查看表单
     *
	 * @param projectExecution
	 * @param model
	 * @return
	 */
	@RequiresPermissions("project:execution:view")
	@RequestMapping(value = "form")
	public String form(ProjectExecution projectExecution, Model model) {
	    String prefix = "modules/project/execution/";
		String view = "ExecutionForm";

        model.addAttribute("projectExecution", projectExecution);

        // 待办、已办入口界面传的act是一样的，只是act中的status不一样。

		if (projectExecution.getIsNewRecord()) {
			// 入口1：新建表单，直接返回空实体
			if (projectExecution.hasAct()) {
				// 入口2：从已办任务界面来的请求，1、实体是新建的，2、act是activi框架填充的。
				// 此时实体应该由流程id来查询。
				view = "ExecutionView";
				projectExecution = executionService.findByProcInsId(projectExecution);
				if (projectExecution == null) {
					projectExecution = new ProjectExecution();
				}
			}
		    return prefix + view;
        }

        // 入口3：在流程图中配置，从待办任务界面来的请求，entity和act都已加载。
        // 环节编号
        String taskDefKey = projectExecution.getAct().getTaskDefKey();

        // 查看
        if(projectExecution.getAct().isFinishTask()){
            view = "ExecutionView";
        }
        // 修改环节
        else if ( UserTaskType.UT_OWNER.equals(taskDefKey) ){
            view = "ExecutionForm";
        }
        // 商务部专员-要填写供应商的联系人信息
        else if (UserTaskType.UT_COMMERCE_SPECIALIST.equals(taskDefKey)) {
			view = "ExecutionView4Commerce";
		}
        // 某审批环节
        else if ("apply_end".equals(taskDefKey)){
            view = "ExecutionView";  // replace ExecutionAudit
        } else {
            view = "ExecutionView";
        }
        return prefix + view;
	}

    /**
     * 可能与form请求合并
     * @param projectExecution
     * @param model
     * @return
     */
	@RequiresPermissions("project:execution:admin")
	@RequestMapping(value = "modify")
	public String modify(ProjectExecution projectExecution, Model model) {
		return "modules/project/execution/ExecutionForm";
	}

	@RequestMapping(value = "view")
	public String view(ProjectExecution projectExecution, Model model) {
		return "modules/project/execution/ExecutionViewDlg";
	}

	/**
	 *
	 * @param projectExecution
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("project:execution:edit")
	@RequestMapping(value = "save")
	public String save(ProjectExecution projectExecution, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, projectExecution)){
			return form(projectExecution, model);
		}
		String flag = projectExecution.getAct().getFlag();

		if ("saveOnly".equals(flag)) {
			executionService.save(projectExecution);
		} else if ("saveFinishProcess".equals(flag)) {
            executionService.saveFinishProcess(projectExecution);
		} else {
            executionService.saveLaunch(projectExecution);
		}

		addMessage(redirectAttributes, "保存项目合同执行申请单成功");
		
		String usertask_owner = projectExecution.getAct().getTaskDefKey();
		if (UserTaskType.UT_OWNER.equals(usertask_owner)) {
			return "redirect:" + adminPath + "/act/task/todo/";
		} else {
			return "redirect:"+Global.getAdminPath()+"/project/execution/?repage";
		}
	}
	
	@RequiresPermissions("project:execution:edit")
	@RequestMapping(value = "delete")
	public String delete(ProjectExecution projectExecution, RedirectAttributes redirectAttributes) {
		executionService.delete(projectExecution);
		addMessage(redirectAttributes, "删除项目合同执行成功");
		return "redirect:"+Global.getAdminPath()+"/project/execution/?repage";
	}

	@RequestMapping(value = "saveAudit")
	public String saveAudit(ProjectExecution projectExecution, Model model) {
		if (StringUtils.isBlank(projectExecution.getAct().getFlag())
				|| StringUtils.isBlank(projectExecution.getAct().getComment())){
			addMessage(model, "请填写审核意见。");
			return form(projectExecution, model);
		}
		executionService.saveAudit(projectExecution);
		return "redirect:" + adminPath + "/act/task/todo/";
	}

	
	/**
	 * 使用的导出
	 */
	@RequiresPermissions("project:execution:view")
	@RequestMapping(value = "export")
	public void export(HttpServletRequest request, HttpServletResponse response,Map map) {
        ProjectExecution execution=(ProjectExecution) map.get("execution");
		List<Act> actList =actTaskService.histoicFlowListPass(execution.getProcInsId(),null, null);
		String  fileReturnName=execution.getApply().getProjectName()+"_合同执行审批表";
		String workBookFileRealPathName =request.getSession().getServletContext().getRealPath("/")+"WEB-INF/excel/project/ProjectBidding.xls";
		ExportUtils.export(response, execution, actList, workBookFileRealPathName, fileReturnName,"yyyy-MM-dd");
	}


    @RequestMapping(value = "/export1")
    public ModelAndView export1(HttpServletRequest request, HttpServletResponse response, Map map) {
        Map<String, Object> model = new HashMap();

        ProjectExecution execution = (ProjectExecution) map.get("projectExecution");
        List<Act> actList =actTaskService.histoicFlowListPass(execution.getProcInsId(),null, null);

        execution.setActs(actList);
        String temp = DictUtils.getDictLabel(execution.getApply().getCustomer().getCustomerCategory(), "customer_category", "");
        model.put("customerCategory", temp);
        temp = DictUtils.getDictLabel(execution.getApply().getCustomer().getIndustry(), "customer_industry", "");
        model.put("customerIndustry", temp);
        temp = DictUtils.getDictLabel(execution.getApply().getCategory(), "pro_category", "");
        model.put("projectCategory", temp);

        model.put("execution", execution);

        String  exportFileName = execution.getApply().getProjectName()+"_合同执行审批表";

        return new ModelAndView(new JxlsExcelView("ProjectExecution.xls",exportFileName), model);

    }

}