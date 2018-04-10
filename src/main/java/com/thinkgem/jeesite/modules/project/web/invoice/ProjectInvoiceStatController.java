/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.project.web.invoice;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.persistence.RespEntity;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoice;
import com.thinkgem.jeesite.modules.project.entity.invoice.ProjectInvoiceItem;
import com.thinkgem.jeesite.modules.project.service.invoice.ProjectInvoiceService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;

/**
 * 项目开票Controller
 *
 *
 * 提交表单类：使用json提交，返回json的Entity
 * 查询类：使用form提交，返回json的page对象
 *
 * @author jicdata
 * @version 2016-03-08
 */
@Controller
@RequestMapping(value = "${adminPath}/project/invoiceStat")
public class ProjectInvoiceStatController extends BaseController {

	@Autowired
	private ProjectInvoiceService invoiceService;

	String prefix = "modules/project/invoice/";

	String vStatList = "InvoiceStatList";

    /**
     * 如果把@ModelAttribute放在方法的注解上时，
	 * 代表的是：该Controller的所有方法在调用前，先执行此@ModelAttribute方法
	 * 绑定键值对到Model中
	 *
     * @param id
     * @return
     */
	@ModelAttribute
	public ProjectInvoice get(@RequestParam(required=false) String id) {
        ProjectInvoice entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = invoiceService.get(id);
		}
		if (entity == null) {
			entity = new ProjectInvoice();
		}
		return entity;
	}

	/**
	 * 根据item id获取发票item，并绑定一个对象到Model上
	 * @param id
	 * @return
	 */
	@ModelAttribute
	public ProjectInvoiceItem getItem(@RequestParam(required = false) String id) {
		ProjectInvoiceItem item = null;
		if (StringUtils.isNotBlank(id)){
			item = invoiceService.getItem(id);
		}
		if (item == null) {
			item = new ProjectInvoiceItem();
		}
		return item;
	}

	@RequiresPermissions("project:invoice:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProjectInvoice projectInvoice, HttpServletRequest request, HttpServletResponse response, Model model) {
		projectInvoice.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s6", "u6"));
		Page<ProjectInvoice> page = invoiceService.findPage(new Page<ProjectInvoice>(request, response), projectInvoice);
		model.addAttribute("page", page);
		return prefix + vStatList;
	}

	@RequestMapping(value = "stat")
	@ResponseBody
	public RespEntity stat(@RequestBody ProjectInvoiceItem invoiceItem,
						   HttpServletRequest request, HttpServletResponse response, Model model) {

		String ctxPath = request.getContextPath();

		String pCode = ""; // pCode projectCode 项目编号
		if (invoiceItem != null && invoiceItem.getApply() != null) {
			pCode = invoiceItem.getApply().getProjectCode();
		}

		String pName = ""; // pName projectName 项目名称
		if (invoiceItem != null && invoiceItem.getApply() != null) {
			pName = invoiceItem.getApply().getProjectName();
		}

		String contractCode = ""; // contractCode 合同编号
		if (invoiceItem != null && invoiceItem.getContract() != null) {
			contractCode = invoiceItem.getContract().getContractCode();
		}
		String queryBeginDate = "";
		String queryEndDate = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (invoiceItem != null && invoiceItem.getQueryBeginDate() != null) {
			queryBeginDate = sdf.format(invoiceItem.getQueryBeginDate());
		}
		if (invoiceItem != null && invoiceItem.getQueryEndDate() != null) {
			queryEndDate = sdf.format(invoiceItem.getQueryEndDate());
		}

		StringBuilder sbUrl = new StringBuilder();
		sbUrl.append(ctxPath);
		sbUrl.append("/ureport/preview?_u=file:invoiceItem.ureport.xml&pc=");
		sbUrl.append(pCode);
		sbUrl.append("&pn=").append(pName);
		sbUrl.append("&cc=").append(contractCode);
		sbUrl.append("&qb=").append(queryBeginDate);
		sbUrl.append("&qe=").append(queryEndDate);

		//-2参数错误，-1操作失败，0操作成功，1成功刷新当前页，2成功并跳转到url，3成功并刷新iframe的父界面
		// 4 跳转新窗口
		RespEntity respEntity = new RespEntity(4, "查询成功！");
		respEntity.setUrl(sbUrl.toString());
		return respEntity;
	}

}