/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.crm.client.web;

import com.thinkgem.jeesite.common.beanvalidator.BeanValidators;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.utils.excel.ExportExcel;
import com.thinkgem.jeesite.common.utils.excel.ImportExcel;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.crm.client.entity.Client;
import com.thinkgem.jeesite.modules.crm.visit.entity.ClientVisit;
import com.thinkgem.jeesite.modules.crm.client.service.ClientService;
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

/**
 * 客户联系人信息Controller
 * @author jicdata
 * @version 2016-02-21
 */
@Controller
@RequestMapping(value = "${adminPath}/crm/client")
public class ClientController extends BaseController {

	private static String urlPrefix = "modules/crm/client/";

	@Autowired
	private ClientService clientService;
	
	@ModelAttribute
	public Client get(@RequestParam(required=false) String id) {
		Client entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = clientService.get(id);
		}
		if (entity == null){
			entity = new Client();
		}
		return entity;
	}
	
	@RequiresPermissions("client:client:view")
	@RequestMapping(value = {"list", ""})
	public String list(Client client, HttpServletRequest request, HttpServletResponse response, Model model) {
//		client.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
		BaseService.dataScopeFilter(client, "dsf", "s5.id =id or u4.id is null ", "u4.id = id or u4.id is null");
		
		Page<Client> page = clientService.findPage(new Page<Client>(request, response), client);
		model.addAttribute("page", page);
		return urlPrefix + "ClientList";
	}

	@RequiresPermissions("client:client:view")
	@RequestMapping(value = "form")
	public String form(Client client, Model model) {
		model.addAttribute("customer", client);
		return urlPrefix + "ClientForm";
	}
	/**
	 * 客户负责人 看到的form界面，功能为添加联系人
	 * 权限需要  客户联系人维护 权限
	 * @param client
	 * @param model
	 * @return
	 */
	@RequiresPermissions("client:contact:edit")
	@RequestMapping(value = "form4contact")
	public String form4contact(Client client, Model model) {
		model.addAttribute("customer", client);
		return urlPrefix + "ClientContactForm";
	}

	/**
	 *
	 * @param client
	 * @param model
	 * @return
	 */
	@RequiresPermissions("client:contact:edit")
	@RequestMapping(value = "form4Visit")
	public String form4Visit(Client client, Model model) {
		model.addAttribute("client", client);

		return urlPrefix + "ClientVisitForm";
	}

	@RequiresPermissions("client:contact:edit")
	@RequestMapping(value = "save4Visit")
	public String save4Visit(ClientVisit visit, Model model) {

		return urlPrefix + "ClientVisitForm";
	}

	
	/**
	 * 拥有 客户维护权限、客户联系人权限的都可以保存操作
	 */
	@RequiresPermissions(value={"client:client:edit","client:contact:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public String save(Client client, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, client)){
			return form(client, model);
		}
		if (!"true".equals(checkLoginName(client.getOldName(), client.getCustomerName()))){
			addMessage(model, "保存客户'" + client.getCustomerName() + "'失败，客户名称已存在");
			return form(client, model);
		}
		clientService.save(client);
		addMessage(redirectAttributes, "保存客户成功");
		return "redirect:"+Global.getAdminPath()+"/crm/client/?repage";
	}
	
	/**
	 * 拥有 客户维护权限、客户联系人权限的都可以保存操作
	 */
	@RequiresPermissions(value={"client:client:edit","client:contact:edit"},logical=Logical.OR)
	@RequestMapping(value = "delete")
	public String delete(Client client, RedirectAttributes redirectAttributes) {
		clientService.delete(client);
		addMessage(redirectAttributes, "删除客户联系人成功");
		return "redirect:"+Global.getAdminPath()+"/crm/client/?repage";
	}

	/**
	 * 获取客户JSON数据。
	 * @param response
	 * @return
	 */
// 	@ResponseBody
// 	@RequestMapping(value = "treeData")
// 	public List<Map<String, Object>> treeData(HttpServletResponse response) {
// 		Client client = new Client();
// 		/**
// 		 * 得到没有指定负责人部门的数据
// 		 */
// //		client.getSqlMap().put("dsf", BaseService.dataScopeFilter(UserUtils.getUser(), "s5", "u4"));
// 		BaseService.dataScopeFilter(client, "dsf", "s5.id =id or u4.id is null ", "u4.id = id or u4.id is null");
// 		List<Map<String, Object>> mapList = Lists.newArrayList();
// 		List<Client> list = clientService.findList(client);
// 		for (int i=0; i<list.size(); i++){
// 			Client e = list.get(i);
// 				Map<String, Object> map = Maps.newHashMap();
// 				map.put("id", e.getId());
// 				map.put("name", e.getCustomerName());
// 				mapList.add(map);
// 		}
// 		return mapList;
// 	}
	
	
	/**
	 * 获取客户联系人JSON数据。
	 * @param response
	 * @return
	 */
	// @ResponseBody
	// @RequestMapping(value = "treeData2")
	// public List<Map<String, Object>> treeData2(String customerId, HttpServletResponse response) {
	// 	List<Map<String, Object>> mapList = Lists.newArrayList();
	//
	// 	Client client = new Client(customerId);
	// 	List<ClientContact> list = clientService.findContatList(client);
	// 	for (int i=0; i<list.size(); i++){
	// 		ClientContact e = list.get(i);
	// 			Map<String, Object> map = Maps.newHashMap();
	// 			map.put("id", e.getId());
	// 			map.put("name", e.getContactName());
	// 			mapList.add(map);
	// 	}
	// 	return mapList;
	// }
	
	
	/**
	 * 为了projectExternalForm的ajax请求。
	 * @param client
	 * @return 返回客户的 类型 与 行业
	 */
	// @ResponseBody
	// @RequestMapping(value = "customer4projectApplyExternal")
	// public Client getCustomer4ProjectApplyExternal(Client client) {
	// 	Client client2 =new Client();
	//
	// 	client2.setCustomerCategory(DictUtils.getDictLabel(client.getCustomerCategory(), "customer_category", ""));
	// 	client2.setIndustry(DictUtils.getDictLabel(client.getIndustry(), "customer_industry", ""));
	//
	// 	return client2;
	// }

	/**
	 * 
	 * @param customerConcatId 客户联系人id
	 * @return 返回 联系人的 电话 与 职位
	 */
	// @ResponseBody
	// @RequestMapping(value = "customerConcat4ProjectApplyExternal")
	// public ClientContact getCustomerConcat4ProjectApplyExternal(String customerConcatId) {
	// 	ClientContact tempCustomerConcat = clientService.getCustomerConcat(customerConcatId);
	// 	ClientContact returnCustomerConcat = new ClientContact();
	// 	returnCustomerConcat.setPosition(tempCustomerConcat.getPosition());
	// 	returnCustomerConcat.setPhone(tempCustomerConcat.getPhone());
	//
	// 	return returnCustomerConcat;
	// }
	
//	
	/**
	 * 客户批量上传
	 * @param file
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("client:contact:edit")
	@RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Client> clients = ei.getDataList(Client.class);
//			clientService.saveList(clients);
			for (Client client : clients) {
				try {
					if ("true".equals(checkLoginName("", client.getCustomerName()))) {
						BeanValidators.validateWithException(validator, client);
						clientService.save(client);
						successNum++;
					} else {
						failureMsg.append("<br/>客户名称 "+ client.getCustomerName() +" 已存在; ");
						failureNum++;
					}
				} catch (ConstraintViolationException ex) {
					failureMsg.append("<br/>客户名称 "+ client.getCustomerName()+" 导入失败：");
					List<String> messageList = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
					for (String message : messageList){
						failureMsg.append(message+"; ");
						failureNum++;
					}
				}catch (Exception ex) {
					failureMsg.append("<br/>客户名称 "+ client.getCustomerName()+" 导入失败："+ex.getMessage());
				}
			} // end for clients
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条客户，导入信息如下：");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条客户"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入客户失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/client/client/?repage";
    } 
	
	
	/**
	 * 下载导入模板
	 * @param
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("client:contact:edit")
	@RequestMapping(value = "template")
	public String template(HttpServletRequest request,
						   HttpServletResponse response,
						   RedirectAttributes redirectAttributes) {
		
		try {
            String fileName = "客户导入模板"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
    		new ExportExcel("客户数据", Client.class).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出客户导入模板失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/client/client/?repage";
	}
	
	
	public String checkLoginName(String oldCustomerName, String customerName) {
		if (customerName !=null && customerName.equals(oldCustomerName)) {
			return "true";
		} else if (customerName !=null && clientService.getByName(customerName) == null) {
			return "true";
		}
		return "false";
	}
}