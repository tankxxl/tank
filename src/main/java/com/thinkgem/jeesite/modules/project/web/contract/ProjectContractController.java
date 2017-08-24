/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.web.contract;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.web.JxlsExcelView;
import com.thinkgem.jeesite.modules.act.entity.Act;
import com.thinkgem.jeesite.modules.act.service.ActTaskService;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import com.thinkgem.jeesite.modules.oa.entity.OaNotify;
import com.thinkgem.jeesite.modules.oa.entity.OaNotifyRecord;
import com.thinkgem.jeesite.modules.oa.service.OaNotifyService;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContract;
import com.thinkgem.jeesite.modules.project.entity.contract.ProjectContractItem;
import com.thinkgem.jeesite.modules.project.service.contract.ProjectContractService;
import com.thinkgem.jeesite.modules.sys.entity.Role;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
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

	@Autowired
	private ProjectContractService contractService;
	@Autowired
	private ActTaskService actTaskService;

	@Autowired
	private OaNotifyService notifyService;


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
	public String list(ProjectContract projectContract,
					   HttpServletRequest request,
					   HttpServletResponse response,
					   Model model) {
		// 此dataScopeFilter中的s5、u4从xml的sql语句可以看出来，是根据立项人来过滤数据的。
		// projectContract.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		// 现在我们要根据自己业务表(contract)中的createBy来过滤自己的数据。
		projectContract.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s6", "u6"));
		Page<ProjectContract> page = contractService.findPage(new Page<>(request, response),
				projectContract);
		model.addAttribute("page", page);
		return "modules/project/contract/projectContractList";
	}

	@RequiresPermissions("project:contract:projectContract:edit")
	@RequestMapping(value = "exportList")
	public String exportList(ProjectContract projectContract,
					   HttpServletRequest request,
					   HttpServletResponse response,
					   Model model) {
		// 此dataScopeFilter中的s5、u4从xml的sql语句可以看出来，是根据立项人来过滤数据的。
		// projectContract.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		// 现在我们要根据自己业务表(contract)中的createBy来过滤自己的数据。
		projectContract.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s6", "u6"));
		Page<ProjectContract> page = contractService.findPage(new Page<ProjectContract>(request, response, -1),
				projectContract);

		String fileName = "合同统计"+ DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
		// Page<User> page = systemService.findUser(new Page<User>(request, response, -1), user);
		// new ExportExcel("用户数据", User.class).setDataList(page.getList()).write(response, fileName).dispose();

		// return "modules/project/contract/projectContractList";
		return "redirect:" + adminPath + "/project/contract/projectContract/list?repage";
	}

	@RequestMapping(value = "/export1")
	public ModelAndView export1(ProjectContract projectContract,
								HttpServletRequest request,
								HttpServletResponse response,
								Model model) {
		// 查询数据
		projectContract.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s6", "u6"));
		Page<ProjectContract> page = contractService.findPage(new Page<>(request, response, -1),
				projectContract);

		String fileName = "合同统计"+ DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
		Map<String, Object> map = new HashMap();
		map.put("contracts", page.getList());
		return new ModelAndView(new JxlsExcelView("contract_list_template.xlsx",fileName), map);
	}

	@RequiresPermissions("project:contract:projectContract:view")
	@RequestMapping(value = "form")
	public String form(ProjectContract projectContract, Model model) {

		String prefix = "modules/project/contract/";
		String view = "projectContractForm";
		view = projectContract.getForm();

		model.addAttribute("projectContract", projectContract);

		// 待办、已办入口界面传的act是一样的，只是act中的status不一样。

		if (projectContract.getIsNewRecord()) {
			// 入口1：新建表单，直接返回空实体
			if (projectContract.hasAct()) {
				// 入口2：从已办任务界面来的请求，1、实体是新建的，2、act是activi框架填充的。
				// 此时实体应该由流程id来查询。
				view = "projectContractView";
				view = projectContract.getView();
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
			view = projectContract.getView();
		}
		// 修改环节
		else if ( UserTaskType.UT_OWNER.equals(taskDefKey) ){
			view = "projectContractForm";
			view = projectContract.getForm();
		}
		// 下面是技术部门设置 项目经理
		else if ("usertask_software_development_leader".equals(taskDefKey)||"usertask_service_delivery_leader".equals(taskDefKey)) {
			view = "projectContractView";
			view = projectContract.getView();
		}
		// 某审批环节
		else if ("apply_end".equals(taskDefKey)){
			view = "projectContractView";  // replace ExecutionAudit
			view = projectContract.getView();
		} else {
			view = "projectContractView";
			view = projectContract.getView();
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
			System.out.println("");
			contractService.save(projectContract);
		} else if ("saveFinishProcess".equals(flag)) { // 保存并结束流程
			contractService.saveFinishProcess(projectContract);
		} else {
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
		// if (StringUtils.isBlank(projectContract.getAct().getFlag())
		// 		|| StringUtils.isBlank(projectContract.getAct().getComment())) {
		if (StringUtils.isBlank(projectContract.getAct().getFlag()) ) {
			addMessage(model, "请填写审核意见。");
			return form(projectContract, model);
		}

		String usertask = projectContract.getAct().getTaskDefKey();
		if (UserTaskType.UT_SPECIALIST.equals(usertask)) {
			if (!"true".equals(checkContractCode(projectContract.getOldContractCode(), projectContract.getContractCode())) ) {
				addMessage(model, "保存合同'" + projectContract.getContractCode() + "'失败，合同编号已存在");
				return form(projectContract, model);
			}
		}


		if (StringUtils.isEmpty(projectContract.getAct().getComment())) {
			projectContract.getAct().setComment("同意");
		}
		contractService.saveAudit(projectContract);
		return "redirect:" + adminPath + "/act/task/todo/";
	}

	/**
	 * 验证合同编号是否有效，必须唯一
	 * @param oldContractCode
	 * @param contractCode
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "checkContractCode")
	public String checkContractCode(String oldContractCode, String contractCode) {
		if (contractCode !=null && contractCode.equals(oldContractCode)) {
			return "true";
		} else if (contractCode !=null && contractService.getByCode(contractCode) == null) {
			return "true";
		}
		return "false";
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
		// or return list;
    }

    /**
     * Json形式返回合同item信息
     * @param id 合同Item id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getItemAsJson")
    public ProjectContractItem getItemAsJson(@RequestParam(required=false) String id) {
		return contractService.getContractItem(id);
    }

	/**
	 * 获取我的通知数目(快到期的合同数量)
	 */
	@RequestMapping(value = "count")
	@ResponseBody
	public String count(ProjectContract contract, Model model) {
		// contract.setReadFlag("0");
		return String.valueOf(contractService.findPreEndCount(contract));
	}

	/**
	 * 快到期合同列表
	 */
	@RequestMapping(value = "preEndList")
	public String preEndList(ProjectContract contract,
							 HttpServletRequest request, HttpServletResponse response,
							 Model model) {
		// contract.setSelf(true);
		// Page对象由前端的request、response对象初始化
		Page<ProjectContract> page = contractService.findPreEndPage(new Page<>(request, response),
				contract);
		model.addAttribute("page", page);
		return "modules/project/contract/projectContractList";
	}

	// test 入口
	@RequestMapping(value = "addToNotify")
	public String addToNotify( HttpServletRequest request, HttpServletResponse response,
							 Model model) {

		OaNotify oaNotify = new OaNotify();
		oaNotify.setType("4");
		notifyService.deleteByType(oaNotify);

		ProjectContract contract = new ProjectContract();
		List<ProjectContract> contracts = contractService.findPreEndList(contract);
		notify(contracts);

		contracts = contractService.findNotify1List(contract);
		notify(contracts);

		contracts = contractService.findNotify2List(contract);
		notify(contracts);

		contracts = contractService.findNotify3List(contract);
		notify(contracts);

		// List<OaNotifyRecord> notifyRecords = new ArrayList<>();
		// notify.setOaNotifyRecordList(notifyRecords);

		// private String type;			// 类型 oa_notify_type 1, 2, 3,
		// private String title;		// 标题
		// private String content;		// 内容
		// private String status;		// 状态 oa_notify_status 0, 1

		// notify.setType("4");
		// notify.setTitle("title");
		// notify.setContent("content");
		// notify.setStatus("1");

		// OaNotifyRecord record = new OaNotifyRecord();
		// record.setId(IdGen.uuid());
		// record.setOaNotify(notify);
		// User user = UserUtils.getByLoginName("limin");
		// record.setUser(user);
		// record.setReadFlag("0");
		// notifyRecords.add(record);

		// notifyService.save(notify);

		// DictUtils
		// notify.setType();
		// contract.setSelf(true);
		// Page对象由前端的request、response对象初始化
		// Page<ProjectContract> page = contractService.findPreEndPage(new Page<>(request, response),
		// 		contract);
		// model.addAttribute("page", page);
		// return "modules/project/contract/projectContractList";
		return null;
	}

	public void notify(List<ProjectContract> contractList) {

		OaNotify notify ;
		List<OaNotifyRecord> oaNotifyRecordList;
		OaNotifyRecord record;
		String type;
//		List<User> userList = Collections.emptyList();
		List<User> userList = new ArrayList<>();

		User officeLeader, officeBoss;

		Role role = UserUtils.getRoleByEnname("usertask_specialist");
		if (role != null && role.getUserList() != null && !role.getUserList().isEmpty()) {
			userList.addAll(role.getUserList());
		}
		for (ProjectContract contract : contractList) {
			System.out.println();
			// 新建
			notify = new OaNotify();
			oaNotifyRecordList = Lists.newArrayList();
			notify.setOaNotifyRecordList(oaNotifyRecordList);

			// master
			type = DictUtils.getDictValue("合同预警", "oa_notify_type", "4");
			notify.setType(type);
			String title = StringUtils.isEmpty(contract.getContractCode()) ? contract.getClientName() : contract.getContractCode();
			notify.setTitle(title);
			notify.setContent(contract.getApply().getProjectName()
					+ "\n项目编号：" + contract.getApply().getProjectCode()
					+ "\n合同编号：" + contract.getContractCode()
					+ "\n客户名称：" + contract.getClientName()
					+ "\n合同到期日期：" + DateUtils.formatDateTime(contract.getEndDate()));
			notify.setStatus("1");
			notify.setCreateBy(UserUtils.get("1"));

			// slave 可以有多个接收人
//			record = new OaNotifyRecord();
//			record.setId(IdGen.uuid());
//			record.setOaNotify(notify);
//			record.setUser(contract1.getCreateBy());
//			record.setReadFlag("0");
//			oaNotifyRecordList.add(record);

			officeLeader = UserUtils.get(contract.getCreateBy().getId()).getOffice().getPrimaryPerson();
			officeBoss = UserUtils.get(contract.getCreateBy().getId()).getOffice().getDeputyPerson();
			userList.add(contract.getCreateBy());
			userList.add(officeLeader);
			userList.add(officeBoss);

			for (User user : userList) {
				System.out.println();
				record = new OaNotifyRecord();
				record.setId(IdGen.uuid());
				record.setOaNotify(notify);
				record.setUser(user);
				record.setReadFlag("0");
				oaNotifyRecordList.add(record);
			}
			// 保存
			notifyService.save(notify);
		} // end for list

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