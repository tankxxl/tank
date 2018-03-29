/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.customer.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.customer.entity.Customer;
import com.thinkgem.jeesite.modules.customer.entity.CustomerInvoice;
import com.thinkgem.jeesite.modules.customer.service.CustomerInvoiceService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import org.apache.shiro.authz.annotation.Logical;
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
import java.util.List;
import java.util.Map;

/**
 * 开票客户信息Controller
 * @author jicdata
 * @version 2016-02-21
 */
@Controller
@RequestMapping(value = "${adminPath}/customer/invoice")
public class CustomerInvoiceController extends BaseController {

	@Autowired
	private CustomerInvoiceService customerInvoiceService;
	
	@ModelAttribute
	public CustomerInvoice get(@RequestParam(required=false) String id) {
		CustomerInvoice entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = customerInvoiceService.get(id);
		}
		if (entity == null){
			entity = new CustomerInvoice();
		}
		return entity;
	}
	
	@RequiresPermissions("customer:invoice:view")
	@RequestMapping(value = {"list", ""})
	public String list(CustomerInvoice customerInvoice, HttpServletRequest request, HttpServletResponse response, Model model) {

//		customer.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		BaseService.dataScopeFilter(customerInvoice, "dsf", "s5.id =id or u4.id is null ", "u4.id = id or u4.id is null");
		
		Page<CustomerInvoice> page = customerInvoiceService
				.findPage(
						new Page<CustomerInvoice>(request, response),
						customerInvoice);
		model.addAttribute("page", page);
		return "modules/customer/customerInvoiceList";
	}

	@RequiresPermissions("customer:invoice:view")
	@RequestMapping(value = "form")
	public String form(CustomerInvoice customerInvoice, Model model) {
		model.addAttribute("customerInvoice", customerInvoice);
		return "modules/customer/customerInvoiceForm";
	}

	@RequiresPermissions("customer:invoice:view")
	@RequestMapping(value = "view")
	public String view(CustomerInvoice customerInvoice, Model model) {
		customerInvoice.setFunc("view");
		model.addAttribute("customerInvoice", customerInvoice);
		return "modules/customer/customerInvoiceForm";
	}

	/**
	 * 提供界面+数据
	 * 客户负责人 看到的form界面，功能为添加联系人
	 * 权限需要  客户联系人维护 权限
	 * @return
	 */
	@RequiresPermissions("customer:contact:edit")
	@RequestMapping(value = "form4contact")
	public String form4contact(Customer customer, Model model) {
		model.addAttribute("customer", customer);
		return "modules/customer/customerForm4Saler";
	}
	
	/**
	 * 拥有 客户维护权限、客户联系人权限的都可以保存操作
	 */
	@RequiresPermissions(value={"customer:invoice:edit","customer:contact:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public String save(CustomerInvoice customerInvoice, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, customerInvoice)){
			return form(customerInvoice, model);
		}
		// if (!"true".equals(checkLoginName(customerInvoice.getOldName(), customerInvoice.getCustomerName()))){
		// 	addMessage(model, "保存开票客户'" + customerInvoice.getCustomerName() + "'失败，客户名称已存在");
		// 	return form(customerInvoice, model);
		// }
		customerInvoiceService.save(customerInvoice);
		addMessage(redirectAttributes, "保存开票客户成功");
		return "redirect:"+Global.getAdminPath()+"/customer/invoice/?repage";
	}
	
	/**
	 * 拥有 客户维护权限、客户联系人权限的都可以保存操作
	 */
	@RequiresPermissions(value={"customer:invoice:edit","customer:contact:edit"},logical=Logical.OR)
	@RequestMapping(value = "delete")
	public String delete(CustomerInvoice customerInvoice, RedirectAttributes redirectAttributes) {
		customerInvoiceService.delete(customerInvoice);
		addMessage(redirectAttributes, "删除开票客户成功");
		return "redirect:"+Global.getAdminPath()+"/customer/invoice/?repage";
	}

	
	/**
	 * 获取客户JSON数据。
	 * 给树控件提供数据，map格式
	 */
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData() {
		CustomerInvoice customerInvoice = new CustomerInvoice();
		/**
		 * 得到没有指定负责人部门的数据
		 */
//		customer.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		BaseService.dataScopeFilter(customerInvoice, "dsf", "s5.id =id or u4.id is null ", "u4.id = id or u4.id is null");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<CustomerInvoice> list = customerInvoiceService.findList(customerInvoice);
		for (int i=0; i<list.size(); i++){
			CustomerInvoice e = list.get(i);
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("name", e.getCustomerName());
				mapList.add(map);
		}
		return mapList;
	}

	/**
	 * 获取客户联系人JSON数据。
	 * 给树控件提供数据
	 */
	@ResponseBody
	@RequestMapping(value = "treeData2")
	public List<Map<String, Object>> treeData2(String customerInvoiceId) {
		List<Map<String, Object>> mapList = Lists.newArrayList();

		if (StringUtils.isEmpty(customerInvoiceId)) {
			return mapList;
		}
		CustomerInvoice customerInvoice = new CustomerInvoice(customerInvoiceId);
		// List<CustomerContact> list = customerInvoiceService.findContatList(customer);
		// for (int i=0; i<list.size(); i++){
		// 	CustomerContact e = list.get(i);
		// 		Map<String, Object> map = Maps.newHashMap();
		// 		map.put("id", e.getId());
		// 		map.put("name", e.getContactName());
		// 		mapList.add(map);
		// }
		return mapList;
	}

	/**
	 * 获得CustomerInvoice详情 json格式
	 * 供前端ajax单独请求数据，而不是页面
	 */
	@ResponseBody
	@RequestMapping(value = "getAsJson")
	public CustomerInvoice getAsJson(@RequestParam(required = false) String id, Model model) {
		model.addAttribute("customerInvoiceId", id);

		CustomerInvoice customerInvoice = get(id);
		// 字典数字转换成文字，也可以在前端转换
		customerInvoice.setCustomerCategory(
				DictUtils.getDictLabel(customerInvoice.getCustomerCategory(), "customer_category", ""));

		return customerInvoice;
	}

	// true: 不重复，前端放行，可以添加
	// false: 数据库已经有了
	// 修改的时候，才有oldName
	@ResponseBody
	@RequiresPermissions("customer:customer:edit")
	@RequestMapping(value = "checkName")
	public String checkName(String oldName, String customerName) {
		if (customerName !=null && customerName.equals(oldName)) {
			return "true";
		} else if (customerName !=null && customerInvoiceService.getByName(customerName) == null) {
			return "true";
		}
		return "false";
	}
}