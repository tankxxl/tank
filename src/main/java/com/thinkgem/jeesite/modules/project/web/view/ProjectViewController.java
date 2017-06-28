///**
// * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
// */
//package com.thinkgem.jeesite.modules.project.web.view;
//
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import com.thinkgem.jeesite.common.config.Global;
//import com.thinkgem.jeesite.common.persistence.Page;
//import com.thinkgem.jeesite.common.service.BaseService;
//import com.thinkgem.jeesite.common.utils.StringUtils;
//import com.thinkgem.jeesite.common.web.BaseController;
//import com.thinkgem.jeesite.modules.act.entity.Act;
//import com.thinkgem.jeesite.modules.act.service.ActTaskService;
//import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
//import com.thinkgem.jeesite.modules.apply.service.external.ProjectApplyExternalService;
//import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
//import com.thinkgem.jeesite.modules.sys.utils.ExportUtils2;
//import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
//import org.apache.shiro.authz.annotation.RequiresPermissions;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * 结项Controller
// * @author jicdata
// * @version 2016-03-11
// */
//@Controller
//@RequestMapping(value = "${adminPath}/project/view/projectView")
//public class ProjectViewController extends BaseController {
//
//	@Autowired
//	private ProjectApplyExternalService projectApplyExternalService;
//	@Autowired
//	private ActTaskService actTaskService;
//
//	@ModelAttribute
//	public ProjectApplyExternal get(@RequestParam(required=false) String id) {
//		ProjectApplyExternal entity = null;
//		if (StringUtils.isNotBlank(id)){
//			entity = projectApplyExternalService.get(id);
//		}
//		if (entity == null){
//			entity = new ProjectApplyExternal();
//		}
//		return entity;
//	}
//
//	@RequiresPermissions("apply:external:projectApplyExternal:view")
//	@RequestMapping(value = {"list", ""})
//	public String list(ProjectApplyExternal projectApplyExternal, HttpServletRequest request, HttpServletResponse response, Model model) {
//		projectApplyExternal.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
//		Page<ProjectApplyExternal> page = projectApplyExternalService.findPage(new Page<ProjectApplyExternal>(request, response), projectApplyExternal);
//		model.addAttribute("page", page);
//		return "modules/project/view/projectViewList";
//	}
//
//	@RequiresPermissions("apply:external:projectApplyExternal:view")
//	@RequestMapping(value = "form")
//	public String form(ProjectApplyExternal projectApplyExternal, Model model) {
//		String view = "projectExternalView";
//
//		model.addAttribute("projectApplyExternal", projectApplyExternal);
//		return "modules/project/view/" + view;
//	}
//
//	/**
//	 * 申请单保存/修改，申请人操作
//	 * @param projectApplyExternal
//	 * @param model
//	 * @param redirectAttributes
//	 * @return
//	 */
//	@RequiresPermissions("apply:external:projectApplyExternal:edit")
//	@RequestMapping(value = "save")
//	public String save(ProjectApplyExternal projectApplyExternal, Model model, RedirectAttributes redirectAttributes) {
//		if (!beanValidator(model, projectApplyExternal)){
//			return form(projectApplyExternal, model);
//		}
//		projectApplyExternalService.save(projectApplyExternal);
//		addMessage(redirectAttributes, "保存外部立项申请成功");
//		return "redirect:"+Global.getAdminPath()+"/apply/external/projectApplyExternal/?repage";
//	}
//
//	/**
//	 * 工单执行（完成任务），审批人操作
//	 * @param projectApplyExternal
//	 * @param model
//	 * @return
//	 */
//	@RequestMapping(value = "saveAudit")
//	public String saveAudit(ProjectApplyExternal projectApplyExternal, Model model) {
//		if (StringUtils.isBlank(projectApplyExternal.getAct().getFlag())
//				|| StringUtils.isBlank(projectApplyExternal.getAct().getComment())){
//			addMessage(model, "请填写审核意见。");
//			return form(projectApplyExternal, model);
//		}
//		projectApplyExternalService.auditSave(projectApplyExternal);
//		return "redirect:" + adminPath + "/act/task/todo/";
//	}
//
//	/**
//	 * 使用的导出
//	 * @param request
//	 * @param response
//	 * @param redirectAttributes
//	 * @param map
//	 * @param model
//	 * @return
//	 */
//	@RequiresPermissions("apply:external:projectApplyExternal:view")
//	@RequestMapping(value = "export")
//	public void export2(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes,Map map) {
//		ProjectApplyExternal projectApplyExternal =(ProjectApplyExternal) map.get("projectApplyExternal");
//		List<Act> actList =actTaskService.histoicFlowListPass(projectApplyExternal.getProcessInstanceId(),null, null);
//		projectApplyExternal.setActs(actList);
//		String  fileReturnName=projectApplyExternal.getProjectName()+"_立项审批表";
////		String workBookFileRealPathName =request.getSession().getServletContext().getRealPath("/")+"WEB-INF/excel/project/ProjectApplyExternal.xls";
//		String workBookFileRealPathName =request.getSession().getServletContext().getRealPath("/")+"WEB-INF/excel/project/ProjectApplyExternal2.xls";
//		ExportUtils2.export(response,
//				projectApplyExternal,
//				workBookFileRealPathName,
//				fileReturnName,
//				"yyyy-MM-dd" );
//	}
//
//	/**
//	 * 获取外部申请列表（已通过的审批的立项）
//	 * @param response
//	 * @param proMainStage 项目阶段
//	 * @return
//	 */
//	@ResponseBody
//	@RequestMapping(value = "treeData")
//	public List<Map<String, Object>> treeData(HttpServletResponse response,String proMainStage) {
//		ProjectApplyExternal applyExternal = new ProjectApplyExternal();
//		/**
//		 * 设置待查询的项目阶段（如 10，11，20,21等）
//		 */
////		applyExternal.setProMainStage(proMainStage);
//
////		List<String> items = Arrays.asList(proMainStage.split(","));
//		applyExternal.setQueryStage(Arrays.asList(proMainStage.split(",")));
//
//		applyExternal.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
//		List<Map<String, Object>> mapList = Lists.newArrayList();
//		List<ProjectApplyExternal> list = projectApplyExternalService.findList(applyExternal);
//		for (int i=0; i<list.size(); i++){
//			ProjectApplyExternal e = list.get(i);
//				Map<String, Object> map = Maps.newHashMap();
//				map.put("id", e.getId());
//				map.put("name", e.getProjectName());
//				mapList.add(map);
//		}
//		return mapList;
//	}
//	/**
//	 * 获取 mainstage为更大的值的项目 如（proMainStage 为11 则查询 20，21，30，31等）
//	 * @param response
//	 * @param proMainStage 项目阶段
//	 * @return
//	 */
//	@ResponseBody
//	@RequestMapping(value = "treeData4LargerMainStage")
//	public List<Map<String, Object>> treeData4LargerMainStage(HttpServletResponse response,String proMainStage) {
//		ProjectApplyExternal applyExternal = new ProjectApplyExternal();
//
//		applyExternal.setProMainStage(proMainStage);
//
//		applyExternal.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
//		List<Map<String, Object>> mapList = Lists.newArrayList();
//		List<ProjectApplyExternal> list = projectApplyExternalService.findList4LargerMainStage(applyExternal);
//		for (int i=0; i<list.size(); i++){
//			ProjectApplyExternal e = list.get(i);
//			Map<String, Object> map = Maps.newHashMap();
//			map.put("id", e.getId());
//			map.put("name", e.getProjectName());
//			mapList.add(map);
//		}
//		return mapList;
//	}
//
//
//	/**
//	 * 获取外部申请列表（已通过的审批的立项）
//	 * @param response
//	 * @param proMainStage 项目阶段
//	 * @return
//	 */
//	@ResponseBody
//	@RequestMapping(value = "treeAllData")
//	public List<Map<String, Object>> treeAllData(HttpServletResponse response,String proMainStage) {
//		ProjectApplyExternal applyExternal = new ProjectApplyExternal();
//		/**
//		 * 设置待查询的项目阶段（如 01，02，03等）
//		 */
//		applyExternal.setProMainStage(proMainStage);
//
//		List<Map<String, Object>> mapList = Lists.newArrayList();
//		List<ProjectApplyExternal> list = projectApplyExternalService.findList(applyExternal);
//		for (int i=0; i<list.size(); i++){
//			ProjectApplyExternal e = list.get(i);
//			Map<String, Object> map = Maps.newHashMap();
//			map.put("id", e.getId());
//			map.put("name", e.getProjectName());
//			mapList.add(map);
//		}
//		return mapList;
//	}
//
//
//	/**
//	 * 获取立项 的项目编码、销售名称、客户行业
//	 * 用于 投标时候获取立项基本信息
//	 * @param response
//	 * @return
//	 */
//	@ResponseBody
//	@RequestMapping(value = "proApply4bidding")
//	public Map<String,String> proApply4bidding(ProjectApplyExternal projectApplyExternal) {
//		 Map<String,String> resultMap = new HashMap<String, String>();
//
//		 resultMap.put("projectCode",projectApplyExternal.getProjectCode() );
//		 resultMap.put("customerName", projectApplyExternal.getCustomer().getCustomerName());
//		 resultMap.put("customerIndustry", DictUtils.getDictLabel(projectApplyExternal.getCustomer().getIndustry(), "customer_industry", ""));
//		 resultMap.put("projectSaler", projectApplyExternal.getSaler().getName());
//		 resultMap.put("applyProfitMargin", projectApplyExternal.getEstimatedGrossProfitMargin().toString());
//		 return resultMap;
//
//	}
//
//	/**
//	 * 获取 客户名称、客户联系人名称、客户电话、项目编号
//	 * 用于 投标时候获取立项基本信息
//	 * @param response
//	 * @return
//	 */
//	@ResponseBody
//	@RequestMapping(value = "proApply4contract")
//	public Map<String,String> proApply4contract(ProjectApplyExternal projectApplyExternal) {
//		 Map<String,String> resultMap = new HashMap<String, String>();
//
//		 resultMap.put("projectCode",projectApplyExternal.getProjectCode() );
//		 resultMap.put("customerName",projectApplyExternal.getCustomer().getCustomerName());
//		 resultMap.put("customerContactName", projectApplyExternal.getCustomerContact().getContactName());
//		 resultMap.put("customerPhone", projectApplyExternal.getCustomer().getPhone());
//		 resultMap.put("projectCategory", DictUtils.getDictLabel(projectApplyExternal.getCategory(), "pro_category", ""));
//		 return resultMap;
//
//	}
//
//	/**
//	 * 获取 客户名称、客户联系人名称、客户电话、项目编号
//	 * 用于 投标时候获取立项基本信息
//	 * @param response
//	 * @return
//	 */
//	@ResponseBody
//	@RequestMapping(value = "proApply4finish")
//	public Map<String,String> proApply4finish(ProjectApplyExternal projectApplyExternal) {
//		Map<String,String> resultMap = new HashMap<String, String>();
//
//		resultMap.put("projectCode",projectApplyExternal.getProjectCode() );
//		resultMap.put("salerName",projectApplyExternal.getSaler().getName() );
//		resultMap.put("salerOfficeName",projectApplyExternal.getSaler().getOffice().getName());
//		resultMap.put("customerName",projectApplyExternal.getCustomer().getCustomerName());
//		return resultMap;
//
//	}
//
//	/**
//	 * Json形式返回项目信息
//	 * @author Arthur
//	 * @param id
//	 * @return
//	 */
//	@ResponseBody
//	@RequestMapping(value = "getAsJson")
//	public ProjectApplyExternal getAsJson(@RequestParam(required=false) String id) {
//		return get(id);
//	}
//
//}