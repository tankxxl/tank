/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.web.execution;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.persistence.RespEntity;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目合同执行Controller
 * @author jicdata
 * @version 2016-03-08
 */
@Controller
@RequestMapping(value = "${adminPath}/project/execution")
public class ProjectExecutionController extends BaseController {

	static String prefix = "modules/project/execution/";
	static String LIST = "ExecutionList";
	static String VIEW = "ExecutionView";
	static String EDIT = "ExecutionForm";
	static String REDIRECT2VIEW = "/project/execution/?repage";

	@Autowired
	private ProjectExecutionService executionService;
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

	@Override
	protected String otherNode(String taskDefKey) {
		if (UserTaskType.UT_COMMERCE_SPECIALIST.equals(taskDefKey) ) {
			return "ExecutionView4Commerce";
		} else {
			return super.otherNode(taskDefKey);
		}
	}

	/**
	 *
	 * @ModelAttribute属性和@SessionAttribute属性主要用来在不同控制器和模型之间共享数据，
	 * 默认情况下，map的信息只是在request当前请求有效，如果想在一个controller中共享数据，可以使用这两个属性。
	 * 可以在前台页面访问到ModelAttribute标记的方法(方法返回值)
	 *
     * 如果把@ModelAttribute放在方法的注解上时，代表的是：该Controller的所有方法在调用前，先执行此@ModelAttribute方法
	 *
	 * 在页面请求form方法前，会首先执行@ModelAttribute标注的get方法，get方法中返回一个ProjectExecution对象，
	 * 将会被下面的form对象接收，form方法会把get方法返回的ProjectExecution对象和request请求中的参数合并，
	 * 并且request中的参数优先级更高，从而得到一个全新的ProjectExecution对象，一般用于局部修改业务。
	 *
	 * 1、@ModelAttribute注解void返回值的方法，如：
	 * @ModelAttribute
	 * public void populateModel(@RequestParam String abc, Model model) {
	 *		model.addAttribute("attributeName", abc);
	 * }
	 * 2、@ModelAttribute注解返回具体类的方法，如：
	 * @ModelAttribute
		public User populateModel() {
			User user=new User();
			user.setAccount("ray");
			return user;
		}
	 * 在请求此controller下的其他请求时，首先执行此get方法，返回ProjectExecution对象，model属性的名称没有指定，
	 * 它由返回类型隐含表示，如这个方法返回ProjectExecution类型，那么这个model属性的名称是projectExecution。
	 * 相当于：model.addAttribute("projectExecution", entity);
	 *
	 * 3、也可以指定属性名称
	 * @ModelAttribute(value="myUser")
		public User populateModel() {
			User user=new User();
			user.setAccount("ray");
			return user;
		}
	 *
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
                       HttpServletRequest request, HttpServletResponse response,
                       Model model) {
        projectExecution.getSqlMap().put("dsf",
                BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		Page<ProjectExecution> page = executionService.findPage(new Page<>(request, response),
                projectExecution);
		model.addAttribute("page", page);
		return prefix + LIST;
	}

	/**
	 * 查看表单
	 * 1、查看表单，view
	 * 2、审批表单，view + audit
	 * 3、新增、修改表单 form，申请人使用此界面
	 *
	 * 请求来源：审批单列表(有实体无act)，待办、已办任务列表(可能有实体，有act)，
	 * act对象只有从activi查出来后才有，而待办、已办来源于activi
     *
	 * @param projectExecution
	 * @param model
	 * @return
	 */
	@RequiresPermissions("project:execution:view")
	@RequestMapping(value = "form")
	public String form(ProjectExecution projectExecution, Model model) {
		return prefix + formToView(projectExecution);
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
		// model.addAttribute("projectExecution", projectExecution);
		return "modules/project/execution/ExecutionForm";
	}

	@RequestMapping(value = "view")
	public String view(ProjectExecution projectExecution, Model model) {
		// model.addAttribute("projectExecution", projectExecution);
//		return "modules/project/execution/ExecutionView2";
		// 主要是返回的界面跟form不一样，新建一个view的入口主要是为了返回不一样的界面。
		return "modules/project/execution/ExecutionViewDlg";
	}

	/**
	 * 启动流程、保存申请单、销毁流程、删除申请单。
	 *
     * 申请人使用，在form界面中使用。
	 *
	 * @param projectExecution
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("project:execution:edit")
	@RequestMapping(value = "save")
	public String save(ProjectExecution projectExecution, Model model,
					   RedirectAttributes redirectAttributes) {

		String flag = projectExecution.getAct().getFlag();
		if (!"saveOnly".equals(flag)) {
			if (!beanValidator(model, projectExecution)) {
				return form(projectExecution, model);
			}
		}

		saveBusi(executionService, projectExecution);
		addMessage(redirectAttributes, "保存成功!");
		
		String usertask_owner = projectExecution.getAct().getTaskDefKey();
        // 此节点只有在被驳回时才有，所以入口是在我的任务中
		if (UserTaskType.UT_OWNER.equals(usertask_owner)) { // 待办任务页面
			return "redirect:" + adminPath + "/act/task/todo/";
		} else { // 列表页面
			return "redirect:"+Global.getAdminPath()+"/project/execution/?repage";
		}
	}
	
	@RequiresPermissions("project:execution:edit")
	@RequestMapping(value = "delete")
	public String delete(ProjectExecution projectExecution, RedirectAttributes redirectAttributes) {
		executionService.delete(projectExecution);
		addMessage(redirectAttributes, "删除成功!");
		return "redirect:"+Global.getAdminPath()+"/project/execution/?repage";
	}

	// 审批人使用
	// audit页面使用，由activi引出act，再引出业务表单界面。
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

	@ResponseBody
	@RequestMapping(value = "/test")
	public Object test(ProjectExecution projectExecution, Model model) {
		//-2参数错误，-1操作失败，0操作成功，1成功刷新当前页，2成功并跳转到url，3成功并刷新iframe的父界面
		RespEntity response = new RespEntity(1, "成功修改！");

		// RespEntity response = new RespEntity(2, "/project/invoice");
		// response.setUrl("invoice");

		if ("2".equals(projectExecution.getProcStatus())) {
			projectExecution.setProcStatus("1");
		} else {
			projectExecution.setProcStatus("2");
		}

		executionService.save(projectExecution);

		// model.setData(list);
		return response;
	}

}