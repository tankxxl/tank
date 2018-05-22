/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.web.contract;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.beanvalidator.BeanValidators;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.persistence.RespEntity;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.utils.excel.ExportExcel;
import com.thinkgem.jeesite.common.utils.excel.ImportExcel;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.web.JxlsExcelView;
import com.thinkgem.jeesite.modules.act.entity.Act;
import com.thinkgem.jeesite.modules.act.service.ActTaskService;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.project.entity.bidding.ProjectBidding;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContractItem;
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoiceItem;
import com.thinkgem.jeesite.modules.project.service.contract.ProjectContractService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import com.thinkgem.jeesite.modules.sys.utils.ExportUtils;
import com.thinkgem.jeesite.modules.sys.utils.POIUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
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

	@ModelAttribute
	public ProjectContract get(@RequestParam(required = false) String id) {
		return contractService.get(id);
	}

	@RequiresPermissions("project:contract:projectContract:view")
	@RequestMapping(value = { "list", "" })
	public String list(ProjectContract projectContract,
					   HttpServletRequest request,
					   HttpServletResponse response,
					   Model model) {
		projectContract.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		Page<ProjectContract> page = contractService.findPage(new Page<>(request, response), projectContract);
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
				// view = projectContract.getView();
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
		// 某审批环节
		else if ("apply_end".equals(taskDefKey)){
			view = "projectContractView";  // replace ExecutionAudit
		} else {
			view = "projectContractView";
		}
		return prefix + view;
	}

    @RequiresPermissions("project:bidding:projectBidding:edit")
    @RequestMapping(value = "modify")
    public String modify(ProjectContract projectContract, Model model) {
        return "modules/project/contract/projectContractForm";
    }

	@RequiresPermissions("project:contract:projectContract:edit")
	@RequestMapping(value = "save")
	public String save(ProjectContract projectContract, Model model, RedirectAttributes redirectAttributes) {
        String flag = projectContract.getAct().getFlag();
        if (!"saveOnly".equals(flag)) {
            if (!beanValidator(model, projectContract)) {
                return form(projectContract, model);
            }
        }

		if ("saveOnly".equals(flag)) {
			System.out.println();
			contractService.save(projectContract);
		} else if ("saveFinishProcess".equals(flag)) {
			contractService.saveFinishProcess(projectContract);
		} else {
			contractService.saveLaunch(projectContract);
		}
		addMessage(redirectAttributes, "保存合同成功");
		String usertask = projectContract.getAct().getTaskDefKey();
		if (UserTaskType.UT_OWNER.equals(usertask)) {
			return "redirect:" + adminPath + "/act/task/todo/";
		} else {
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

	// /**
	//  * 验证合同编号是否有效，必须唯一
	//  * @param oldContractCode
	//  * @param contractCode
	//  * @return
	//  */
	// @ResponseBody
	// @RequestMapping(value = "checkContractCode")
	// public String checkContractCode(String oldContractCode, String contractCode) {
	// 	if (contractCode !=null && contractCode.equals(oldContractCode)) {
	// 		return "true";
	// 	} else if (contractCode !=null && contractService.getByCode(contractCode) == null) {
	// 		return "true";
	// 	}
	// 	return "false";
	// }

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
		// or return list;
    }

    /**
     * @param id 合同Item id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getItemAsJson")
    public ProjectContractItem getItemAsJson(@RequestParam(required=false) String id) {
		return contractService.getContractItem(id);
    }

	/**
	 * @param id 合同Item id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getAsJson")
	public ProjectContract getAsJson(@RequestParam(required=false) String id) {
		return contractService.get(id);
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

	/**
	 * 导入合同数据
	 * @param file
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("project:contract:projectContract:edit")
	@RequestMapping(value = "import", method=RequestMethod.POST)
	public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			System.out.println();
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<User> list = ei.getDataList(User.class);
			for (User user : list){
				try{
					// if ("true".equals(checkLoginName("", user.getLoginName()))){
					// 	user.setPassword(SystemService.entryptPassword("123456"));
					// 	BeanValidators.validateWithException(validator, user);
					// 	systemService.saveUser(user);
					// 	successNum++;
					// }else{
					// 	failureMsg.append("<br/>登录名 "+user.getLoginName()+" 已存在; ");
					// 	failureNum++;
					// }
				}catch(ConstraintViolationException ex){
					failureMsg.append("<br/>登录名 "+user.getLoginName()+" 导入失败：");
					List<String> messageList = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
					for (String message : messageList){
						failureMsg.append(message+"; ");
						failureNum++;
					}
				}catch (Exception ex) {
					failureMsg.append("<br/>登录名 "+user.getLoginName()+" 导入失败："+ex.getMessage());
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条用户，导入信息如下：");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条用户"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入用户失败！失败信息："+e.getMessage());
		}
		return "redirect:" + adminPath + "/project/contract/projectContract/list?repage";
	}

	/**
	 * 下载导入合同数据模板
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("project:contract:projectContract:edit")
	@RequestMapping(value = "import/template")
	public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "合同导入模板.xlsx";
			List<User> list = Lists.newArrayList();
			list.add(UserUtils.getUser());
			new ExportExcel("用户数据", User.class, 2)
					.setDataList(list)
					.write(response, fileName)
					.dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:" + adminPath + "/sys/user/list?repage";
	}

	/**
	 * 验证合同编号是否有效，不能重复
	 * @param oldContractCode
	 * @param contractCode
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("project:contract:projectContract:edit")
	@RequestMapping(value = "checkContractCode")
	public String checkContractCode(String oldContractCode, String contractCode) {
		if (contractCode !=null && contractCode.equals(oldContractCode)) {
			return "true";
		}
		// else if (contractCode !=null && systemService.getUserByLoginName(contractCode) == null) {
		// 	return "true";
		// }
		return "false";
	}

	@RequestMapping(value = "/exportList")
	public ModelAndView exportList(ProjectContract projectContract,
								   HttpServletRequest request, HttpServletResponse response, Map map) {

		projectContract.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));

		// 不用分页
		List<ProjectContract> list = contractService.findList(projectContract);
		Map<String, Object> model = new HashMap();

		map.put("list", list);


		String  exportFileName = "导出签约列表" + DateUtils.getDate("yyyyMMddHHmmss")+".xls";

		return new ModelAndView(
				new JxlsExcelView("ContractList.xls", exportFileName),
				model);
	}

	@RequestMapping(value = "stat")
	@ResponseBody
	public RespEntity stat(@RequestBody ProjectContract projectContract,
						   HttpServletRequest request, HttpServletResponse response, Model model) {

		String ctxPath = request.getContextPath();

		String pCode = ""; // pCode projectCode 项目编号
		if (projectContract != null && projectContract.getApply() != null) {
			pCode = projectContract.getApply().getProjectCode();
		}

		String pName = ""; // pName projectName 项目名称
		if (projectContract != null && projectContract.getApply() != null) {
			pName = projectContract.getApply().getProjectName();
		}

		String contractCode = ""; // contractCode 合同编号
		// if (invoiceItem != null && invoiceItem.getContract() != null) {
		// 	contractCode = invoiceItem.getContract().getContractCode();
		// }
		String queryBeginDate = "";
		String queryEndDate = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// if (invoiceItem != null && invoiceItem.getQueryBeginDate() != null) {
		// 	queryBeginDate = sdf.format(invoiceItem.getQueryBeginDate());
		// }
		// if (invoiceItem != null && invoiceItem.getQueryEndDate() != null) {
		// 	queryEndDate = sdf.format(invoiceItem.getQueryEndDate());
		// }

		String amount = "";
		// if (!StringUtils.isBlank(invoiceItem.getAmount())) {
		// 	amount = invoiceItem.getAmount();
		// }

		String customerInvoiceName = "";
		// if ( invoiceItem.getCustomerInvoice() != null && !StringUtils.isBlank(invoiceItem.getCustomerInvoice().getCustomerName())) {
		// 	customerInvoiceName = invoiceItem.getCustomerInvoice().getCustomerName();
		// }

		String taxRate = "";
		// if ( !StringUtils.isBlank(invoiceItem.getTaxRate())) {
		// 	taxRate = invoiceItem.getTaxRate();
		// }

		String officeId = "";
		// if (invoiceItem.getApply() != null &&
		// 		invoiceItem.getApply().getSaler() != null &&
		// 		invoiceItem.getApply().getSaler().getOffice() != null &&
		// 		!StringUtils.isBlank(invoiceItem.getApply().getSaler().getOffice().getId())) {
		// 	officeId = invoiceItem.getApply().getSaler().getOffice().getId();
		// }

		String invoiceDate = "";
		// if (invoiceItem.getInvoiceDate() != null) {
		// 	invoiceDate = sdf.format(invoiceItem.getInvoiceDate());
		// }

		String lineId = "";
		// if (invoiceItem.getApply() != null &&
		// 		invoiceItem.getApply().getLine() != null &&
		// 		!StringUtils.isBlank(invoiceItem.getApply().getLine().getId())) {
		// 	lineId = invoiceItem.getApply().getLine().getId();
		// }

		StringBuilder sbUrl = new StringBuilder();
		sbUrl.append(ctxPath);
		sbUrl.append("/ureport/preview?_u=file:contractList.ureport.xml");
		sbUrl.append("&pc=").append(pCode);
		sbUrl.append("&pn=").append(pName);
		sbUrl.append("&cc=").append(contractCode);
		sbUrl.append("&qb=").append(queryBeginDate);
		sbUrl.append("&qe=").append(queryEndDate);
		sbUrl.append("&am=").append(amount);
		sbUrl.append("&cin=").append(customerInvoiceName);
		sbUrl.append("&tr=").append(taxRate);
		sbUrl.append("&oi=").append(officeId);
		sbUrl.append("&ida=").append(invoiceDate);
		sbUrl.append("&lid=").append(lineId);

		//-2参数错误，-1操作失败，0操作成功，1成功刷新当前页，2成功并跳转到url，3成功并刷新iframe的父界面
		// 4 跳转新窗口
		RespEntity respEntity = new RespEntity(4, "查询成功！");
		respEntity.setUrl(sbUrl.toString());
		return respEntity;
	}


}