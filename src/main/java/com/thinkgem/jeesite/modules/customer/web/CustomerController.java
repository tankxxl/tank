/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.customer.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.beanvalidator.BeanValidators;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.utils.excel.ExportExcel;
import com.thinkgem.jeesite.common.utils.excel.ImportExcel;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.customer.entity.Customer;
import com.thinkgem.jeesite.modules.customer.entity.CustomerContact;
import com.thinkgem.jeesite.modules.customer.service.CustomerService;
import com.thinkgem.jeesite.modules.sys.service.DictService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;

/**
 * 客户联系人信息Controller
 * @author jicdata
 * @version 2016-02-21
 */
@Controller
@RequestMapping(value = "${adminPath}/customer/customer")
public class CustomerController extends BaseController {

	@Autowired
	private CustomerService customerService;
	@Autowired
	private DictService dictService;
	
	@ModelAttribute
	public Customer get(@RequestParam(required=false) String id) {
		Customer entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = customerService.get(id);
		}
		if (entity == null){
			entity = new Customer();
		}
		return entity;
	}
	
	@RequiresPermissions("customer:customer:view")
	@RequestMapping(value = {"list", ""})
	public String list(Customer customer, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		/**
		 * 
		 */
//		customer.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		BaseService.dataScopeFilter(customer, "dsf", "s5.id =id or u4.id is null ", "u4.id = id or u4.id is null");
		
		Page<Customer> page = customerService.findPage(new Page<Customer>(request, response), customer); 
		model.addAttribute("page", page);
		return "modules/customer/customerList";
	}

	@RequiresPermissions("customer:customer:view")
	@RequestMapping(value = "form")
	public String form(Customer customer, Model model) {
		model.addAttribute("customer", customer);
		return "modules/customer/customerForm";
	}
	/**
	 * 客户负责人 看到的form界面，功能为添加联系人
	 * 权限需要  客户联系人维护 权限
	 * @param customer
	 * @param model
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
	@RequiresPermissions(value={"customer:customer:edit","customer:contact:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public String save(Customer customer, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, customer)){
			return form(customer, model);
		}
		if (!"true".equals(checkLoginName(customer.getOldName(), customer.getCustomerName()))){
			addMessage(model, "保存客户'" + customer.getCustomerName() + "'失败，客户名称已存在");
			return form(customer, model);
		}
		customerService.save(customer);
		addMessage(redirectAttributes, "保存客户成功");
		return "redirect:"+Global.getAdminPath()+"/customer/customer/?repage";
	}
	
	/**
	 * 拥有 客户维护权限、客户联系人权限的都可以保存操作
	 */
	@RequiresPermissions(value={"customer:customer:edit","customer:contact:edit"},logical=Logical.OR)
	@RequestMapping(value = "delete")
	public String delete(Customer customer, RedirectAttributes redirectAttributes) {
		customerService.delete(customer);
		addMessage(redirectAttributes, "删除客户联系人成功");
		return "redirect:"+Global.getAdminPath()+"/customer/customer/?repage";
	}

	
	/**
	 * 获取客户JSON数据。
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(HttpServletResponse response) {
		Customer customer = new Customer();
		/**
		 * 得到没有指定负责人部门的数据
		 */
//		customer.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		BaseService.dataScopeFilter(customer, "dsf", "s5.id =id or u4.id is null ", "u4.id = id or u4.id is null");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Customer> list = customerService.findList(customer);
		for (int i=0; i<list.size(); i++){
			Customer e = list.get(i);
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("name", e.getCustomerName());
				mapList.add(map);
		}
		return mapList;
	}
	
	
	/**
	 * 获取客户联系人JSON数据。
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "treeData2")
	public List<Map<String, Object>> treeData2(String customerId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		
		Customer customer = new Customer(customerId);
		List<CustomerContact> list = customerService.findContatList(customer);
		for (int i=0; i<list.size(); i++){
			CustomerContact e = list.get(i);
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("name", e.getContactName());
				mapList.add(map);
		}
		return mapList;
	}
	
	
	/**
	 * 为了projectExternalForm的ajax请求。
	 * @param response
	 * @return 返回客户的 类型 与 行业
	 */
	@ResponseBody
	@RequestMapping(value = "customer4projectApplyExternal")
	public  Customer getCustomer4ProjectApplyExternal(Customer customer) {
		Customer customer2 =new Customer();
		
		customer2.setCustomerCategory(DictUtils.getDictLabel(customer.getCustomerCategory(), "customer_category", ""));
		customer2.setIndustry(DictUtils.getDictLabel(customer.getIndustry(), "customer_industry", ""));
		
		return customer2;
	}
	
	
	
	/**
	 * 
	 * @param customerConcatId传过来的 客户联系人id
	 * @return 返回 联系人的 电话 与 职位
	 */
	@ResponseBody
	@RequestMapping(value = "customerConcat4ProjectApplyExternal")
	public CustomerContact getCustomerConcat4ProjectApplyExternal(String customerConcatId) {
		CustomerContact tempCustomerConcat = customerService.getCustomerConcat(customerConcatId);
		CustomerContact returnCustomerConcat = new CustomerContact();
		returnCustomerConcat.setPosition(tempCustomerConcat.getPosition());
		returnCustomerConcat.setPhone(tempCustomerConcat.getPhone());
		
		return returnCustomerConcat;
		
	}
	
//	
	/**
	 * 客户批量上传
	 * @param file
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("customer:contact:edit")
	@RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Customer> customers = ei.getDataList(Customer.class);
//			customerService.saveList(customers);
			for (Customer customer : customers) {
				try {
					if ("true".equals(checkLoginName("", customer.getCustomerName()))) {
						BeanValidators.validateWithException(validator, customer);
						customerService.save(customer);
						successNum++;
					} else {
						failureMsg.append("<br/>客户名称 "+customer.getCustomerName() +" 已存在; ");
						failureNum++;
					}
				} catch (ConstraintViolationException ex) {
					failureMsg.append("<br/>客户名称 "+customer.getCustomerName()+" 导入失败：");
					List<String> messageList = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
					for (String message : messageList){
						failureMsg.append(message+"; ");
						failureNum++;
					}
				}catch (Exception ex) {
					failureMsg.append("<br/>客户名称 "+customer.getCustomerName()+" 导入失败："+ex.getMessage());
				}
			} // end for customers
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条客户，导入信息如下：");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条客户"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入客户失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/customer/customer/?repage";
    } 
	
	
	/**
	 * 下载导入模板
	 * @param salaryLevel
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("customer:contact:edit")
	@RequestMapping(value = "template")
	public String template(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		
		try {
            String fileName = "客户导入模板"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
    		new ExportExcel("客户数据", Customer.class).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出客户导入模板失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/customer/customer/?repage";
	}
	
	
	public String checkLoginName(String oldCustomerName, String customerName) {
		if (customerName !=null && customerName.equals(oldCustomerName)) {
			return "true";
		} else if (customerName !=null && customerService.getByName(customerName) == null) {
			return "true";
		}
		return "false";
	}
}