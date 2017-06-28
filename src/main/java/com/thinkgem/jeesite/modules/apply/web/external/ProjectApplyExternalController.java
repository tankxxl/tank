/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.apply.web.external;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.IdGen;
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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 外部立项申请Controller
 * @author jicdata
 * @version 2016-02-23
 */
@Controller
@RequestMapping(value = "${adminPath}/apply/external/projectApplyExternal")
public class ProjectApplyExternalController extends BaseController {

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
            // rgz 提前分配数据，因为前端要使用。
            entity.setDocPath("A" + IdGen.uuid().substring(5, 9));
		}
		return entity;
	}
	
	@RequiresPermissions("apply:external:projectApplyExternal:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProjectApplyExternal projectApplyExternal, HttpServletRequest request, HttpServletResponse response, Model model) {
		projectApplyExternal.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		Page<ProjectApplyExternal> page = applyService.findPage(new Page<ProjectApplyExternal>(request, response), projectApplyExternal);
		model.addAttribute("page", page);
		return "modules/apply/external/projectApplyExternalList";
	}

	@RequestMapping(value = "updateDocPath")
    public String updateAttach(ProjectApplyExternal projectApplyExternal,
                               HttpServletRequest request,
                               HttpServletResponse response,
                               Model model) {

        List<ProjectApplyExternal> list = applyService.findList(projectApplyExternal);

        for (int i = 0; i < list.size(); i++) {
            ProjectApplyExternal entity = list.get(i);
            String path = entity.getDocumentAttachmentPath();
            if (StringUtils.isEmpty(path))
                continue;
            String path1 = path.replace("/pms", "");
            entity.setDocumentAttachmentPath(path1);
            applyService.save(entity);
        }
        return "";
    }

	@RequiresPermissions("apply:external:projectApplyExternal:view")
	@RequestMapping(value = "form")
	public String form(ProjectApplyExternal projectApplyExternal, Model model) { 
		String view = "projectApplyExternalForm";

		// 查看审批申请单
		if (StringUtils.isNotBlank(projectApplyExternal.getId())) {//.getAct().getProcInsId())){

			// 环节编号
			String taskDefKey = projectApplyExternal.getAct().getTaskDefKey();
			// 查看工单
			if(projectApplyExternal.getAct().isFinishTask()){
				
				view = "projectApplyExternalView";
			}	
			// 修改环节
			else if ( UserTaskType.UT_OWNER.equals(taskDefKey) ) {
				view = "projectApplyExternalForm";
			}
			// 项目管理专员审核环节-要可以生成项目编号，选择项目类型，然后点击按钮生成项目编号。
			else if (UserTaskType.UT_SPECIALIST.equals(taskDefKey)){
				view = "projectApplyExternalForm4specialist";
			}
			// 审核环节2
			else if ("usertask_software_leader".equals(taskDefKey)){
				view = "projectApplyExternalAudit";
			} else {
				view = "projectApplyExternalAudit";
			}
		} else {  // 从已办任务过来，如果流程已结束，流程实例就没有了，业务id就没有了，只有act对象，这时就根据act.procInsId来查询业务表，加载业务对象用来查看
			if (projectApplyExternal.hasAct()) {
				view = "projectApplyExternalView";
				Act oldAct = projectApplyExternal.getAct();
				projectApplyExternal = applyService.findByProcInsId(projectApplyExternal.getAct().getProcInsId());
				projectApplyExternal.setAct(oldAct);
			}
		}
		model.addAttribute("projectApplyExternal", projectApplyExternal);
		return "modules/apply/bjkj/" + view;
	}

	@RequiresPermissions("apply:external:projectApplyExternal:modify")
	@RequestMapping(value = "modify")
	public String modify(ProjectApplyExternal projectApplyExternal, Model model) {
		model.addAttribute("projectApplyExternal", projectApplyExternal);
		return "modules/apply/external/projectApplyExternalForm";
	}

	/**
	 * 申请单保存/修改，申请人操作
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
		if ("saveOnly".equals(flag)) { // 只保存表单数据
			applyService.saveOnly(projectApplyExternal);
		} else if ("saveFinishProcess".equals(flag)) { // 保存并结束流程
			applyService.saveFinishProcess(projectApplyExternal);
		} else if ("yes".equals(flag)) {
			applyService.save(projectApplyExternal);
		} else if ("no".equals(flag)) {
			applyService.save(projectApplyExternal);
		}

		addMessage(redirectAttributes, "保存成功！");
		
		String usertask_owner = projectApplyExternal.getAct().getTaskDefKey();
		if (UserTaskType.UT_OWNER.equals(usertask_owner)) { // 待办任务页面
			return "redirect:" + adminPath + "/act/task/todo/";
		} else { // 列表页面
			return "redirect:"+Global.getAdminPath()+"/apply/external/projectApplyExternal/?repage";
		}
	}
	
	@RequiresPermissions("apply:external:projectApplyExternal:edit")
	@RequestMapping(value = "delete")
	public String delete(ProjectApplyExternal projectApplyExternal, RedirectAttributes redirectAttributes) {
		applyService.delete(projectApplyExternal);
		addMessage(redirectAttributes, "删除成功！");
		return "redirect:"+Global.getAdminPath()+"/apply/external/projectApplyExternal/?repage";
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
		applyService.auditSave(projectApplyExternal);
		return "redirect:" + adminPath + "/act/task/todo/";
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
		Map<String,String> returnMap = new HashMap<String, String>();

		//若项目类型为空，返回map中加error键值对
		if(category == null){
			returnMap.put("error","项目类型不能为空，请先选择项目类型");
			return returnMap;
		}

		StringBuffer projectCode =new StringBuffer();
		//添加项目类型值
		projectCode.append(category);
		//若项目类型不为空，则在map中添加data键值对
		SimpleDateFormat df = new SimpleDateFormat("yyyy");
		String dateString = df.format(new Date());
		projectCode.append(dateString);
		//添加项目归属对应值
		projectCode.append(ownership);
		//添加后3位累加值
		int maxIdentityLength =3;//项目编码标识位长度
		String currentCode = applyService.getCurrentCode();
		for(int i=currentCode.length();i<maxIdentityLength;i++){
			projectCode.append("0");
		}
		projectCode.append(currentCode);

		returnMap.put("data",projectCode.toString() );
		
		return returnMap;
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
		List<Act> actList =actTaskService.histoicFlowListPass(apply.getProcessInstanceId(),null, null);
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
	 * @param response
	 * @param proMainStage 项目阶段
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(HttpServletResponse response,String proMainStage) {
		ProjectApplyExternal applyExternal = new ProjectApplyExternal();
		/**
		 * 设置待查询的项目阶段（如 10，11，20,21等）
		 */
//		applyExternal.setProMainStage(proMainStage);

		proMainStage = StringUtils.substringBefore(proMainStage, "?");
		applyExternal.setQueryStage(Arrays.asList(proMainStage.split(",")));

		applyExternal.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		List<ProjectApplyExternal> list = applyService.findList(applyExternal);

		return toMapList(list);
	}
	/**
	 * 获取 mainstage为更大的值的项目 如（proMainStage 为11 则查询 20，21，30，31等）
	 * @param response
	 * @param proMainStage 项目阶段
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "treeData4LargerMainStage")
	public List<Map<String, Object>> treeData4LargerMainStage(HttpServletResponse response,String proMainStage) {
		ProjectApplyExternal applyExternal = new ProjectApplyExternal();

		proMainStage = StringUtils.substringBefore(proMainStage, "?");
		applyExternal.setProMainStage(proMainStage);
		
		applyExternal.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		List<ProjectApplyExternal> list = applyService.findList4LargerMainStage(applyExternal);
		return toMapList(list);
	}
	
	
	/**
	 * 获取外部申请列表（已通过的审批的立项）
	 * @param response
	 * @param proMainStage 项目阶段
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "treeAllData")
	public List<Map<String, Object>> treeAllData(HttpServletResponse response,String proMainStage) {
		ProjectApplyExternal applyExternal = new ProjectApplyExternal();
		/**
		 * 设置待查询的项目阶段（如 01，02，03等）
		 */
		applyExternal.setProMainStage(proMainStage);
		applyExternal.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		List<ProjectApplyExternal> list = applyService.findList(applyExternal);
        return toMapList(list);
	}
	
	
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
	@Deprecated
	@ResponseBody
	@RequestMapping(value = "proApply4contract")
	public Map<String,String> proApply4contract(ProjectApplyExternal projectApplyExternal) {
		 Map<String,String> resultMap = new HashMap<String, String>();
		 
		 resultMap.put("projectCode",projectApplyExternal.getProjectCode() );
		 resultMap.put("customerName",projectApplyExternal.getCustomer().getCustomerName());
		 resultMap.put("customerContactName", projectApplyExternal.getCustomerContact().getContactName());
		 resultMap.put("customerPhone", projectApplyExternal.getCustomer().getPhone());
		 resultMap.put("projectCategory", DictUtils.getDictLabel(projectApplyExternal.getCategory(), "pro_category", ""));
		 return resultMap;		 
		
	}
	
	/**
	 * 使用getAsJson代替
	 * 获取 客户名称、客户联系人名称、客户电话、项目编号
	 * 用于 结项时候获取立项基本信息
	 * @param projectApplyExternal
	 * @return
	 */
	@Deprecated
	@ResponseBody
	@RequestMapping(value = "proApply4finish")
	public Map<String,String> proApply4finish(ProjectApplyExternal projectApplyExternal) {
		Map<String,String> resultMap = new HashMap<String, String>();
		
		resultMap.put("projectCode",projectApplyExternal.getProjectCode() );
		resultMap.put("salerName",projectApplyExternal.getSaler().getName() );
		resultMap.put("salerOfficeName",projectApplyExternal.getSaler().getOffice().getName());
		resultMap.put("customerName",projectApplyExternal.getCustomer().getCustomerName());
		return resultMap;		 
		
	}

	/**
	 * Json形式返回项目信息
	 * @author Arthur
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getAsJson")
	public ProjectApplyExternal getAsJson(@RequestParam(required=false) String id, Model model) {
        model.addAttribute("prjId", id);

		return get(id);
	}


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


    private List<Map<String, Object>> toMapList(List<ProjectApplyExternal> list) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        for (int i=0; i<list.size(); i++) {
            ProjectApplyExternal e = list.get(i);
            Map<String, Object> map = Maps.newHashMap();
            map.put("id", e.getId());
            map.put("name", e.getProjectName());
            mapList.add(map);
        }
        return mapList;
    }

}