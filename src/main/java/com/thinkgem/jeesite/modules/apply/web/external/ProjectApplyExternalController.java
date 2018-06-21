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

	static String prefix = "modules/apply/external/";
    static String LIST = "projectApplyExternalList";
	static String VIEW = "projectApplyExternalView";
	static String EDIT = "projectApplyExternalForm";

    static String REDIRECT2VIEW = "/apply/external/projectApplyExternal/?repage";

	@Autowired
	private ProjectApplyExternalService applyService;
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


    @ModelAttribute
	public ProjectApplyExternal get(@RequestParam(required=false) String id) {
		return applyService.get(id);
	}
	
	@RequiresPermissions("apply:external:projectApplyExternal:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProjectApplyExternal projectApplyExternal,
					   HttpServletRequest request, HttpServletResponse response, Model model) {
		projectApplyExternal.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "saler"));
		Page<ProjectApplyExternal> page = applyService.findPage(new Page<>(request, response), projectApplyExternal);
		model.addAttribute("page", page);
		return prefix + LIST;
		// return "modules/apply/external/ApplyList"; // bootstrap-table
	}

	/**
	 * ajax，只返回申请单列表数据，用于bootstrap-table组件
	 * @param projectApplyExternal
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "table")
	@ResponseBody
	public Page<ProjectApplyExternal> table(ProjectApplyExternal projectApplyExternal,
							HttpServletRequest request, HttpServletResponse response, Model model) {
		projectApplyExternal.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "saler"));
		Page<ProjectApplyExternal> page = applyService.findPage(new Page<>(request, response), projectApplyExternal);
		model.addAttribute("page", page);
		return page;
	}

	@RequiresPermissions("apply:external:projectApplyExternal:view")
	@RequestMapping(value = "form")
	public String form(ProjectApplyExternal projectApplyExternal, Model model) {
	    return prefix + formToView(projectApplyExternal);
	}

	@RequiresPermissions("apply:external:projectApplyExternal:modify")
	@RequestMapping(value = "modify")
	public String modify(ProjectApplyExternal projectApplyExternal, Model model) {
		return prefix + EDIT;
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
	public String save(@Valid ProjectApplyExternal projectApplyExternal,
					   Model model, RedirectAttributes redirectAttributes) {
		String flag = projectApplyExternal.getAct().getFlag();
		// 如果是暂存，则不校验表单
		if (!"saveOnly".equals(flag)) {
			if (!beanValidator(model, projectApplyExternal)){
				return form(projectApplyExternal, model);
			}
		}

		saveBusi(applyService, projectApplyExternal);
		addMessage(redirectAttributes, "保存成功！");

		return saveToView(projectApplyExternal);
	}
	
	@RequiresPermissions("apply:external:projectApplyExternal:edit")
	@RequestMapping(value = "delete")
	public String delete(ProjectApplyExternal projectApplyExternal, RedirectAttributes redirectAttributes) {
		applyService.delete(projectApplyExternal);
		addMessage(redirectAttributes, "删除成功！");
		return "redirect:"+Global.getAdminPath() + REDIRECT2VIEW;
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
		return applyService.findList4tree(proMainStage, strict == null ? false : strict);
	}

	/**
	 * Json形式返回项目信息
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getAsJson")
	public ProjectApplyExternal getAsJson(@RequestParam(required=false) String id, Model model) {
        model.addAttribute("prjId", id);
		ProjectApplyExternal apply = get(id);
		// 转换字典数据
		apply.setCategory(DictUtils.getDictLabel(apply.getCategory(), "pro_category", ""));
		return apply;
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

}