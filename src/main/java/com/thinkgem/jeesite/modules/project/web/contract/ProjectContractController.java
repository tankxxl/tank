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
import com.thinkgem.jeesite.common.web.JxlsExcelView;
import com.thinkgem.jeesite.modules.act.entity.Act;
import com.thinkgem.jeesite.modules.act.service.ActTaskService;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContractItem;
import com.thinkgem.jeesite.modules.project.entity.execution.ProjectExecution;
import com.thinkgem.jeesite.modules.project.service.contract.ProjectContractService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import com.thinkgem.jeesite.modules.sys.utils.ExportUtils;
import com.thinkgem.jeesite.modules.sys.utils.POIUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jxls.common.Context;
import org.jxls.transform.poi.PoiTransformer;
import org.jxls.util.JxlsHelper;
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
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * 合同Controller
 * 
 * @author jicdata
 * @version 2016-03-09
 */
@Controller
@RequestMapping(value = "${adminPath}/project/contract/projectContract")
public class ProjectContractController extends BaseController {

	static String prefix = "modules/project/contract/";
	static String LIST = "projectContractList";
	static String VIEW = "projectContractView";
	static String EDIT = "projectContractForm";
	static String REDIRECT2VIEW = "/project/contract/projectContract/?repage";

	@Autowired
	private ProjectContractService contractService;
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
	public ProjectContract get(@RequestParam(required = false) String id) {
		return contractService.get(id);
	}

	@RequiresPermissions("project:contract:projectContract:view")
	@RequestMapping(value = { "list", "" })
	public String list(ProjectContract projectContract,
					HttpServletRequest request, HttpServletResponse response,
					Model model) {
		projectContract.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		Page<ProjectContract> page = contractService.findPage(new Page<>(request, response),
				projectContract);
		model.addAttribute("page", page);
		return prefix + LIST;
	}

	@RequiresPermissions("project:contract:projectContract:view")
	@RequestMapping(value = "form")
	public String form(ProjectContract projectContract, Model model) {
		return prefix + formToView(projectContract);
	}

	@RequiresPermissions("project:contract:projectContract:edit")
	@RequestMapping(value = "save")
	public String save(ProjectContract projectContract, Model model, RedirectAttributes redirectAttributes) {

		String flag = projectContract.getAct().getFlag();
		// 如果是暂存，则不校验表单
		if (!"saveOnly".equals(flag)) {
			if (!beanValidator(model, projectContract)){
				return form(projectContract, model);
			}
		}
		saveBusi(contractService, projectContract);
		addMessage(redirectAttributes, "保存成功!");
		return saveToView(projectContract);
	}

	@RequiresPermissions("project:contract:projectContract:edit")
	@RequestMapping(value = "delete")
	public String delete(ProjectContract projectContract, RedirectAttributes redirectAttributes) {
		contractService.delete(projectContract);
		addMessage(redirectAttributes, "删除成功!");
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


	// 新jxls导出
	@RequiresPermissions("project:contract:projectContract:view")
	@RequestMapping(value = "export1")
	public ModelAndView export1(HttpServletRequest request, HttpServletResponse response, Map map) {

		ProjectContract projectContract = (ProjectContract) map.get("projectContract");
		if (StringUtils.isBlank(projectContract.getId())) {// 合同组为空
			return null;
		}


		Map<String, Object> model = new HashMap();

		List<ProjectContractItem> projectContractItemList = projectContract.getProjectContractItemList();
		List<String> contractCodeList = new ArrayList<String>();

		for (int i = 0; i < projectContractItemList.size(); i++) {
			projectContractItemList.get(i).setContract(projectContract);
			contractCodeList.add(projectContractItemList.get(i).getContractCode());
		}

		List<Act> actList =actTaskService.histoicFlowListPass(projectContract.getProcInsId(),null, null);


		String temp = DictUtils.getDictLabel(projectContract.getApply().getCategory(), "pro_category", "");
		model.put("pro_category", temp);

		model.put("contractList", projectContractItemList);
		model.put("sheetNames", contractCodeList);
		model.put("acts", actList);


		String  exportFileName = projectContract.getApply().getProjectName() + "_销售合同（签约）审批表";

		return new ModelAndView(new JxlsExcelView("ProjectContract2.xls",exportFileName), model);



		// //模板位置、下载的文件
		// String workBookFileRealPathName = request.getSession().getServletContext().getRealPath("/")+ "/WEB-INF/excel/project/ProjectContract2.xls";
		// String fileReturnName = projectContract.getApply().getProjectName() + "_销售合同（签约）审批表";
        //
		// OutputStream os = null;
		// FileInputStream workBookFis = null;
		// System.out.println();
		// try {
		// 	List<Act> actList = null;
		// 	actList = actTaskService.histoicFlowListPass(
		// 			projectContract.getProcInsId(), null, null);
        //
		// 	os = response.getOutputStream();
		// 	// 得到模板workbook
		// 	workBookFis = new FileInputStream(workBookFileRealPathName);
		// 	HSSFWorkbook wb = new HSSFWorkbook(workBookFis);
		// 	List<ProjectContractItem> projectContractItemList = projectContract.getProjectContractItemList();
		// 	List<String> contractCodeList = new ArrayList<String>();
        //
		// 	for (int i = 0; i < projectContractItemList.size(); i++) {
		// 		contractCodeList.add(projectContractItemList.get(i).getContractCode());
		// 	}
        //
		// 	// Context context = PoiTransformer.createInitialContext();
		// 	Context context = new Context();
		// 	context.putVar("contractList", projectContractItemList);
		// 	context.putVar("sheetNames", contractCodeList);
		// 	context.putVar("acts", actList);
		// 	context.putVar("pro_category", "任刚在");
		// 	JxlsHelper.getInstance()
		// 			.setUseFastFormulaProcessor(false)
		// 			.processTemplate(workBookFis, os, context);
        //
        //
		// 	// 移除第一页的modelSheet
		// 	// wb.removeSheetAt(0);
		// 	String codedFileName = java.net.URLEncoder.encode(fileReturnName, "UTF-8");
		// 	response.setHeader("content-disposition", "attachment;filename=" + codedFileName + ".xls");
		// 	// wb.write(os);
		// 	// os.close();
		// 	os.flush();
		// 	workBookFis.close();
        //
		// } catch (Exception e) {
		// 	System.out.println(e);
		// 	// addMessage(redirectAttributes, "导出用户失败！失败信息："+e.getMessage());
		// } finally {
        //
		// 	try {
		// 		if (os != null) {
		// 			os.flush();
		// 			os.close();
		// 		}
		// 	} catch (Exception e) {
		// 		System.out.println(e);
		// 	}
		// }
		// return;
	}

}