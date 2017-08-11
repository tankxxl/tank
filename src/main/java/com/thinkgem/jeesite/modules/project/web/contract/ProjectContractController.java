/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.web.contract;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.act.entity.Act;
import com.thinkgem.jeesite.modules.act.service.ActTaskService;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContractItem;
import com.thinkgem.jeesite.modules.project.service.contract.ProjectContractService;
import com.thinkgem.jeesite.modules.sys.utils.ExportUtils;
import com.thinkgem.jeesite.modules.sys.utils.POIUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 合同Controller
 * 
 * @author jicdata
 * @version 2016-03-09
 */
@Controller
@RequestMapping(value = "${adminPath}/project/contract/projectContract")
public class ProjectContractController extends BaseController {

	@Autowired
	private ProjectContractService contractService;
	@Autowired
	private ActTaskService actTaskService;

//	@Autowired
//	private ProjectExecutionService executionService;

	@ModelAttribute
	public ProjectContract get(@RequestParam(required = false) String id) {
		ProjectContract entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = contractService.get(id);
		}
		if (entity == null) {
			entity = new ProjectContract();
		}
		return entity;
	}

	@RequiresPermissions("project:contract:projectContract:view")
	@RequestMapping(value = { "list", "" })
	public String list(ProjectContract projectContract, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		projectContract.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		Page<ProjectContract> page = contractService.findPage(new Page<ProjectContract>(request, response),
				projectContract);
		model.addAttribute("page", page);
		return "modules/project/contract/projectContractList";
	}

	@RequiresPermissions("project:contract:projectContract:view")
	@RequestMapping(value = "form")
	public String form(ProjectContract projectContract, Model model) {

		String prefix = "modules/project/contract/";
		String view = "projectContractForm";

		model.addAttribute("projectContract", projectContract);

		// 待办、已办入口界面传的act是一样的，只是act中的status不一样。

		if (projectContract.getIsNewRecord()) {
			// 入口1：新建表单，直接返回空实体
			if (projectContract.hasAct()) {
				// 入口2：从已办任务界面来的请求，1、实体是新建的，2、act是activi框架填充的。
				// 此时实体应该由流程id来查询。
				view = "projectContractView";
				projectContract = contractService.findByProcInsId(projectContract);
				if (projectContract == null) {
					projectContract = new ProjectContract();
				}
				model.addAttribute("projectContract", projectContract);
			}
			return prefix + view;
		}

		// 入口3：在流程图中配置，从待办任务界面来的请求，entity和act都已加载。
		// 环节编号
		String taskDefKey = projectContract.getAct().getTaskDefKey();

		// 查看
		if(projectContract.getAct().isFinishTask()){
			view = "projectContractView";
		}
		// 修改环节
		else if ( UserTaskType.UT_OWNER.equals(taskDefKey) ){
			view = "projectContractForm";
		}
		// 下面是技术部门设置 项目经理
		else if ("usertask_software_development_leader".equals(taskDefKey)||"usertask_service_delivery_leader".equals(taskDefKey)) {
			view = "projectContractView";
		}
		// 某审批环节
		else if ("apply_end".equals(taskDefKey)){
			view = "projectContractView";  // replace ExecutionAudit
		} else {
			view = "projectContractView";
		}
		return prefix + view;
	}

	@RequiresPermissions("project:contract:projectContract:edit")
	@RequestMapping(value = "save")
	public String save(ProjectContract projectContract, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, projectContract)) {
			return form(projectContract, model);
		}

		String flag = projectContract.getAct().getFlag();
//		flag在前台Form.jsp中传送过来，在些进行判断要进行的操作
		if ("saveOnly".equals(flag)) { // 只保存表单数据
			contractService.save(projectContract);
		} else if ("saveFinishProcess".equals(flag)) { // 保存并结束流程
			contractService.saveFinishProcess(projectContract);
		} else {
			System.out.println();
			contractService.saveLaunch(projectContract);
		}

		addMessage(redirectAttributes, "保存合同成功");
		
		String usertask = projectContract.getAct().getTaskDefKey();
		if (UserTaskType.UT_OWNER.equals(usertask)) { // 待办任务页面
			return "redirect:" + adminPath + "/act/task/todo/";
		} else { // 列表页面
			return "redirect:" + Global.getAdminPath() + "/project/contract/projectContract/?repage";
		}
		
	}

	@RequiresPermissions("project:contract:projectContract:edit")
	@RequestMapping(value = "delete")
	public String delete(ProjectContract projectContract, RedirectAttributes redirectAttributes) {
		contractService.delete(projectContract);
		addMessage(redirectAttributes, "删除合同成功");
		return "redirect:" + Global.getAdminPath() + "/project/contract/projectContract/?repage";
	}

	@RequestMapping(value = "saveAudit")
	public String saveAudit(ProjectContract projectContract, Model model) {
		if (StringUtils.isBlank(projectContract.getAct().getFlag())
				|| StringUtils.isBlank(projectContract.getAct().getComment())) {
			addMessage(model, "请填写审核意见。");
			return form(projectContract, model);
		}
		contractService.saveAudit(projectContract);
		return "redirect:" + adminPath + "/act/task/todo/";
	}


    /**
     * 根据项目id获取 合同项(item)列表
     * @param prjId 项目id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "treeDataContractItemList")
    public List<Map<String, Object>> treeDataContractItemList(@RequestParam(required=false) String prjId) {
        ProjectContractItem item = new ProjectContractItem();
        item.getContract().getApply().setId(prjId);
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<ProjectContractItem> list = contractService.findItemList(item);
        for (int i=0; i<list.size(); i++){
            ProjectContractItem e = list.get(i);
            Map<String, Object> map = Maps.newHashMap();
            map.put("id", e.getId());
            map.put("name", e.getContractCode());
            mapList.add(map);
        }

//		ProjectExecution execution = new ProjectExecution();
//		ProjectApplyExternal applyExternal = new ProjectApplyExternal();
//		applyExternal.setId(prjId);
//		execution.setApply(applyExternal);
//        List<ProjectExecution> executionList = executionService.findList(execution);
//		for (int i=0; i<executionList.size(); i++){
//			ProjectExecution e = executionList.get(i);
//			Map<String, Object> map = Maps.newHashMap();
//			map.put("id", e.getExecutionContractNo());
//			map.put("name", e.getExecutionContractNo());
//			mapList.add(map);
//		}

        return mapList;
    }

    /**
     * Json形式返回合同item信息
     * @param id 合同Item id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getItemAsJson")
    public ProjectContractItem getItemAsJson(@RequestParam(required=false) String id) {
    	// 当item.id在数据库中不存在时，就说明这个id为project_execution表中execution_contract_no的值
//		ProjectContractItem contractItem = contractService.getContractItem(id);
//		if (contractItem == null) {
//			contractItem = new ProjectContractItem();
//			ProjectExecution execution = executionService.findByExecutionContractNo(id);
//			if (execution == null)
//				return contractItem;
//			contractItem.setContractCode(execution.getExecutionContractNo());
//			contractItem.setContractAmount(execution.getAmount());
//		}
//		return contractItem;
		return contractService.getContractItem(id);
    }


	@RequiresPermissions("project:contract:projectContract:view")
	@RequestMapping(value = "export")
	public void export(HttpServletRequest request, HttpServletResponse response, Map map) {

		ProjectContract projectContract = (ProjectContract) map.get("projectContract");
		if (StringUtils.isBlank(projectContract.getId())) {// 合同组为空
			return;
		}
		//模板位置、下载的文件
		String workBookFileRealPathName = request.getSession().getServletContext().getRealPath("/")+ "/WEB-INF/excel/project/ProjectContract.xls";
		String fileReturnName = projectContract.getApply().getProjectName() + "_销售合同（签约）审批表";
		
		OutputStream os = null;
		FileInputStream workBookFis = null;
		try {
			/**
			 * 下面设置 客户类型、客户行业、项目类型，读取字典表
			 */
			os = response.getOutputStream();
			// 得到模板workbook
			workBookFis = new FileInputStream(workBookFileRealPathName);
			HSSFWorkbook wb = new HSSFWorkbook(workBookFis);
			HSSFSheet modelSheet = wb.getSheetAt(0);
			HSSFSheet tempSheet = null;
			List<ProjectContractItem> projectContractItemList = projectContract.getProjectContractItemList();

			ProjectContractItem projectContractItem = null;
			List<Act> actList = null;
			for (Iterator iterator = projectContractItemList.iterator(); iterator.hasNext();) {
				// 数据来源
				projectContractItem = (ProjectContractItem) iterator.next();
				projectContractItem.setContract(projectContract);
//				actList = actTaskService.histoicFlowListPass(
//						projectContract.getApply().getProcessInstanceId(), null, null);

				actList = actTaskService.histoicFlowListPass(
						projectContract.getProcInsId(), null, null);
				// 创建个模板
				tempSheet = wb.createSheet(projectContractItem.getContractCode());
				POIUtils.copySheet(wb, modelSheet, tempSheet, true);

				// 插入
				ExportUtils.insertBeanValueToExcel(tempSheet, 2, tempSheet.getLastRowNum(), projectContractItem,
						actList,"yyyy-MM-dd");

			}

			// 移除第一页的modelSheet
			wb.removeSheetAt(0);
			String codedFileName = java.net.URLEncoder.encode(fileReturnName, "UTF-8");
			response.setHeader("content-disposition", "attachment;filename=" + codedFileName + ".xls");
			wb.write(os);

		} catch (Exception e) {
			System.out.println(e);
			// addMessage(redirectAttributes, "导出用户失败！失败信息："+e.getMessage());
		} finally {

			try {
				if (os != null) {
					os.flush();
					os.close();
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		return;

	}

}