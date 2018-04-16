/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.apply.web.external;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.web.JxlsExcelView;
import com.thinkgem.jeesite.modules.act.entity.Act;
import com.thinkgem.jeesite.modules.act.service.ActTaskService;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import com.thinkgem.jeesite.modules.sys.utils.ExportUtils3;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jxls.common.Context;
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
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 外部立项申请Controller
 * @author jicdata
 * @version 2016-02-23
 */
@Controller
@RequestMapping(value = "${adminPath}/apply/external/projectApplyExternal")
public class ProjectApplyExternalController extends BaseController {

	static String prefix = "modules/apply/external/"; // jsp url
	static String vList = "projectApplyExternalList";
	static String vForm = "projectApplyExternalForm";
	static String vView = "projectApplyExternalView";

	static String vRedirect = "redirect:" + Global.getAdminPath() +
			"/apply/external/projectApplyExternal/?repage"; // RequestMapping
	static String vTodo = "redirect:" + Global.getAdminPath() + "/act/task/todo/"; // RequestMapping

	@Autowired
	private ProjectApplyExternalService applyService;
	@Autowired
	private ActTaskService actTaskService;
	
	@ModelAttribute
	public ProjectApplyExternal get(@RequestParam(required=false) String id) {
		ProjectApplyExternal entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = applyService.get(id);
		}
		if (entity == null){
			entity = new ProjectApplyExternal();
		}
		return entity;
	}

	/**
	 * Json形式返回项目信息
	 * @param id 项目表id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getAsJson")
	public ProjectApplyExternal getAsJson(@RequestParam(required=false) String id, Model model) {
		model.addAttribute("id", id);
		ProjectApplyExternal apply = get(id);
		// 后台进行字典数据转换，也可以前端转换
		apply.setCategory(DictUtils.getDictLabel(apply.getCategory(), "pro_category", ""));
		return apply;
	}

	@RequiresPermissions("apply:external:projectApplyExternal:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProjectApplyExternal projectApplyExternal,
					   HttpServletRequest request, HttpServletResponse response, Model model) {
		projectApplyExternal
				.getSqlMap()
				.put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		// 根据request、response中的分页参数生成Page对象
		Page<ProjectApplyExternal> page = applyService.findPage(new Page<>(request, response), projectApplyExternal);
		model.addAttribute("page", page);
		return prefix + vList;
	}

	// 入口有：
	// 1. 待办，有act
	// 2. 已办，有act
	// 3. 新增，无act
	// 4. 查看，无act，有id
	@RequiresPermissions("apply:external:projectApplyExternal:view")
	@RequestMapping(value = "form")
	public String form(ProjectApplyExternal projectApplyExternal, Model model) {

		String view = "projectApplyExternalForm";

		model.addAttribute("projectApplyExternal", projectApplyExternal);

		// 待办、已办入口界面传的act是一样的，只是act中的status不一样。
		if (projectApplyExternal.getIsNewRecord()) {
			// 入口1：新建表单，直接返回空实体
			if (projectApplyExternal.hasAct()) {
				// 入口2：从已办任务界面来的请求，1、实体是新建的，2、act是activi框架填充的。
				// 此时实体应该由流程id来查询。
				view = "projectApplyExternalView";
				projectApplyExternal = applyService.findByProcInsId(projectApplyExternal);
				if (projectApplyExternal == null) {
					projectApplyExternal = new ProjectApplyExternal();
				}
				model.addAttribute("projectApplyExternal", projectApplyExternal);
			}
			return prefix + view;
		}

		// 入口3：在流程图中配置，从待办任务界面来的请求，entity和act都已加载。
		// 环节编号
		String taskDefKey = projectApplyExternal.getAct().getTaskDefKey();

		// 查看
		if(projectApplyExternal.getAct().isFinishTask()){
			view = "projectApplyExternalView";
		}
		// 修改环节
		else if ( UserTaskType.UT_OWNER.equals(taskDefKey) ){
			view = "projectApplyExternalForm";
		}
		// 某审批环节
		else if ("apply_end".equals(taskDefKey)){
			view = "projectApplyExternalView";  // replace ExecutionAudit
		} else {
			view = "projectApplyExternalView";
		}
		return prefix + view;
	}

	@RequiresPermissions("apply:external:projectApplyExternal:modify")
	@RequestMapping(value = "modify")
	public String modify(ProjectApplyExternal projectApplyExternal) {
		return prefix + vForm;
	}

	/**
	 * 申请单保存/修改，申请人操作，只有申请人使用此方法
	 * @param projectApplyExternal
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("apply:external:projectApplyExternal:edit")
	@RequestMapping(value = "save")
	public String save(@Valid ProjectApplyExternal projectApplyExternal, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, projectApplyExternal)){
			return form(projectApplyExternal, model);
		}
		String flag = projectApplyExternal.getAct().getFlag();

//		flag在前台Form.jsp中传送过来，在些进行判断要进行的操作
		if ("saveOnly".equals(flag) ) { // 只保存表单数据
			applyService.save(projectApplyExternal);
		} else if ("saveFinishProcess".equals(flag)) { // 保存并结束流程
			applyService.saveFinishProcess(projectApplyExternal);
		} else if ("end".equalsIgnoreCase(flag)) {

		} else {
			applyService.saveLaunch(projectApplyExternal);
		}

		addMessage(redirectAttributes, "保存成功！");
		
		String usertask_owner = projectApplyExternal.getAct().getTaskDefKey();
		if (UserTaskType.UT_OWNER.equals(usertask_owner)) { // 待办任务页面
			// return "redirect:" + adminPath + "/act/task/todo/";
			return vTodo;
		} else { // 列表页面
			return vRedirect;
			// return "redirect:"+Global.getAdminPath()+"/apply/external/projectApplyExternal/?repage";
		}
	}
	
	@RequiresPermissions("apply:external:projectApplyExternal:edit")
	@RequestMapping(value = "delete")
	public String delete(ProjectApplyExternal projectApplyExternal, RedirectAttributes redirectAttributes) {
		applyService.delete(projectApplyExternal);
		addMessage(redirectAttributes, "删除成功！");
		// return "redirect:"+Global.getAdminPath()+"/apply/external/projectApplyExternal/?repage";
		return vRedirect;
	}
	
	/**
	 * 工单执行（完成任务），审批人操作
	 * @param projectApplyExternal
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "saveAudit")
	public String saveAudit(ProjectApplyExternal projectApplyExternal, Model model) {
		if (StringUtils.isBlank(projectApplyExternal.getAct().getFlag())
				|| StringUtils.isBlank(projectApplyExternal.getAct().getComment())){
			addMessage(model, "请填写审核意见。");
			return form(projectApplyExternal, model);
		}
        applyService.saveAudit(projectApplyExternal);
		// return "redirect:" + adminPath + "/act/task/todo/";
		return vTodo;
	}
	
	/**
	 * 生成项目编号
	 * 逻辑：项目类型值（如IT对应07）+当前年份（如 2016）+项目归属（如：建投对应02，华科对应01）+3位的自增值（如 001）
	 * @param category
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "projectCodeGenerate")
	public Map<String,String> projectCodeGenerate(String category,String ownership) {
		return applyService.genPrjCode(category, ownership);
	}
	
	/**
	 * 使用的导出
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @param map
	 * @return
	 */
	@RequiresPermissions("apply:external:projectApplyExternal:view")
	@RequestMapping(value = "export")
	public void export2(HttpServletRequest request, HttpServletResponse response,
                        RedirectAttributes redirectAttributes,
                        Map map) {
		ProjectApplyExternal apply = (ProjectApplyExternal) map.get("projectApplyExternal");
		List<Act> actList =actTaskService.histoicFlowListPass(apply.getProcInsId(),null, null);
		apply.setActs(actList);
		String  fileReturnName = apply.getProjectName()+"_立项审批表";
//		String templateFile =request.getSession().getServletContext().getRealPath("/")+"WEB-INF/excel/project/ProjectApplyExternal.xls";
//		String templateFile = request
//                .getSession()
//                .getServletContext()
//                .getRealPath("/") + "WEB-INF/excel/project/ProjectApplyExternal2.xls";

        String templateFile = request
                .getSession()
                .getServletContext()
                .getRealPath("/") + "/WEB-INF/excel/project/ProjectApplyExternal5.xls";

        Context context = new Context();
        context.putVar("apply", apply);

        String temp = DictUtils.getDictLabel(apply.getCustomer().getCustomerCategory(), "customer_category", "");
        context.putVar("customerCategory", temp);
        temp = DictUtils.getDictLabel(apply.getCustomer().getIndustry(), "customer_industry", "");
        context.putVar("customerIndustry", temp);
        temp = DictUtils.getDictLabel(apply.getCategory(), "pro_category", "");
        context.putVar("projectCategory", temp);

//		ExportUtils3.export(response,
//				apply,
//				templateFile,
//				fileReturnName);

        ExportUtils3.exportByContext(response,
				context,
				templateFile,
				fileReturnName);
	}


	/**
	 * 获取外部申请列表（已通过的审批的立项）
	 * 单独使用一个实体属性来传递多个项目阶段给sql
	 *
	 * eg: treeData?proMainStage=11,21,30,31
	 *
	 * @param proMainStage 项目阶段
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(String proMainStage, Boolean strict) {
		if (strict == null) {
			strict = false;
		}
		return applyService.findList4tree(proMainStage, strict);
	}
	/**
	 * 获取 mainstage为更大的值的项目 如（proMainStage 为11 则查询 20，21，30，31等）
	 * @param proMainStage 项目阶段
	 * @return
	 */
	// @ResponseBody
	// @RequestMapping(value = "treeData4LargerMainStage")
	// public List<Map<String, Object>> treeData4LargerMainStage(
	// 		@RequestParam(required=false) String proMainStage,
	// 		@RequestParam(required=false) Boolean isAll) {
	// 	ProjectApplyExternal applyExternal = new ProjectApplyExternal();
    //
	// 	proMainStage = StringUtils.substringBefore(proMainStage, "?");
	// 	applyExternal.setProMainStage(proMainStage);
    //
	// 	List<ProjectApplyExternal> list = null;
	// 	if (isAll != null && isAll) {
	// 		list = applyService.findAllList(applyExternal);
	// 	} else {
	// 		applyExternal.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
	// 		list = applyService.findList4LargerMainStage(applyExternal);
	// 	}
	// 	return toMapList(list);
	// }

	/**
	 * 使用getAsJson代替
	 * 获取立项 的项目编码、销售名称、客户行业
	 * 用于 投标时候获取立项基本信息
	 * @param projectApplyExternal
	 * @return
	 */
	// @Deprecated
	// @ResponseBody
	// @RequestMapping(value = "proApply4bidding")
	// public Map<String,String> proApply4bidding(ProjectApplyExternal projectApplyExternal) {
	// 	 Map<String,String> resultMap = new HashMap<String, String>();
	//
	// 	 resultMap.put("projectCode",projectApplyExternal.getProjectCode() );
	// 	 resultMap.put("customerName", projectApplyExternal.getCustomer().getCustomerName());
	// 	 resultMap.put("customerIndustry", DictUtils.getDictLabel(projectApplyExternal.getCustomer().getIndustry(), "customer_industry", ""));
	// 	 resultMap.put("projectSaler", projectApplyExternal.getSaler().getName());
	// 	 resultMap.put("applyProfitMargin", projectApplyExternal.getEstimatedGrossProfitMargin().toString());
	// 	 return resultMap;
	//
	// }
	
	/**
	 * 使用getAsJson代替
	 * 获取 客户名称、客户联系人名称、客户电话、项目编号
	 * 用于 合同签约时候获取立项基本信息
	 * @param projectApplyExternal
	 * @return
	 */
	// @Deprecated
	// @ResponseBody
	// @RequestMapping(value = "proApply4contract")
	// public Map<String,String> proApply4contract(ProjectApplyExternal projectApplyExternal) {
	// 	 Map<String,String> resultMap = new HashMap<String, String>();
	//
	// 	 resultMap.put("projectCode",projectApplyExternal.getProjectCode() );
	// 	 resultMap.put("customerName",projectApplyExternal.getCustomer().getCustomerName());
	// 	 resultMap.put("customerContactName", projectApplyExternal.getCustomerContact().getContactName());
	// 	 resultMap.put("customerPhone", projectApplyExternal.getCustomer().getPhone());
	// 	 resultMap.put("projectCategory", DictUtils.getDictLabel(projectApplyExternal.getCategory(), "pro_category", ""));
	// 	 return resultMap;
	//
	// }
	
	/**
	 * 使用getAsJson代替
	 * 获取 客户名称、客户联系人名称、客户电话、项目编号
	 * 用于 结项时候获取立项基本信息
	 * @param projectApplyExternal
	 * @return
	 */
	// @Deprecated
	// @ResponseBody
	// @RequestMapping(value = "proApply4finish")
	// public Map<String,String> proApply4finish(ProjectApplyExternal projectApplyExternal) {
	// 	Map<String,String> resultMap = new HashMap<String, String>();
	// 	resultMap.put("projectCode",projectApplyExternal.getProjectCode() );
	// 	resultMap.put("salerName",projectApplyExternal.getSaler().getName() );
	// 	resultMap.put("salerOfficeName",projectApplyExternal.getSaler().getOffice().getName());
	// 	resultMap.put("customerName",projectApplyExternal.getCustomer().getCustomerName());
	// 	return resultMap;
	// }

	@RequestMapping(value = "/test")
	public ModelAndView export() {
        Map<String, Object> model = new HashMap();
        model.put("report_year", 2015);
        model.put("report_month", 8);
        //queryUser()为数据获取的方法
//        List<User> userList = queryUser();
//        model.put("users", userList);
        return new ModelAndView(new JxlsExcelView("template.xls","output"), model);

    }




}