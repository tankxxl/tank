/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.salary.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.utils.excel.ExportExcel;
import com.thinkgem.jeesite.common.utils.excel.ImportExcel;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.salary.entity.SalaryLevel;
import com.thinkgem.jeesite.modules.salary.service.SalaryLevelService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 薪资等级Controller
 * @author jicdata
 * @version 2016-02-19
 */
@Controller
@RequestMapping(value = "${adminPath}/salary/salaryLevel")
public class SalaryLevelController extends BaseController {

	@Autowired
	private SalaryLevelService salaryLevelService;
	
	@ModelAttribute
	public SalaryLevel get(@RequestParam(required=false) String id) {
		SalaryLevel entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = salaryLevelService.get(id);
		}
		if (entity == null){
			entity = new SalaryLevel();
		}
		return entity;
	}
	
	@RequiresPermissions("salary:salaryLevel:view")
	@RequestMapping(value = {"list", ""})
	public String list(SalaryLevel salaryLevel, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SalaryLevel> page = salaryLevelService.findPage(new Page<SalaryLevel>(request, response), salaryLevel); 
		model.addAttribute("page", page);
		return "modules/salary/salaryLevelList";
	}
	
	
	/**
	 * 导出薪资
	 * @param salaryLevel
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("salary:salaryLevel:view")
	@RequestMapping(value = "export")
	public String export(SalaryLevel salaryLevel, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		
		try {
            String fileName = "工资数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<SalaryLevel> page  =  salaryLevelService.findPage(new Page<SalaryLevel>(request, response,-1), salaryLevel); 
    		new ExportExcel("工资数据", SalaryLevel.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出用户失败！失败信息："+e.getMessage());
		}
		return "redirect:" + adminPath + "/salary/salaryLevel/?repage";
	}

	
	/**
	 * 导入薪资
	 * @param file
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("salary:salaryLevel:edit")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/salary/salaryLevel/?repage";
		}
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<SalaryLevel> list = ei.getDataList(SalaryLevel.class);
			System.out.println(list);
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条用户，导入信息如下：");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条用户"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入用户失败！失败信息："+e.getMessage());
		}
		return "redirect:" + adminPath + "/salary/salaryLevel/?repage";
    }
	
	
	@RequiresPermissions("salary:salaryLevel:view")
	@RequestMapping(value = "form")
	public String form(SalaryLevel salaryLevel, Model model) {
		model.addAttribute("salaryLevel", salaryLevel);
		return "modules/salary/salaryLevelForm";
	}

	@RequiresPermissions("salary:salaryLevel:edit")
	@RequestMapping(value = "save")
	public String save(SalaryLevel salaryLevel, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, salaryLevel)){
			return form(salaryLevel, model);
		}
		salaryLevelService.save(salaryLevel);
		addMessage(redirectAttributes, "保存薪资等级成功");
		return "redirect:"+Global.getAdminPath()+"/salary/salaryLevel/?repage";
	}
	
	@RequiresPermissions("salary:salaryLevel:edit")
	@RequestMapping(value = "delete")
	public String delete(SalaryLevel salaryLevel, RedirectAttributes redirectAttributes) {
		salaryLevelService.delete(salaryLevel);
		addMessage(redirectAttributes, "删除薪资等级成功");
		return "redirect:"+Global.getAdminPath()+"/salary/salaryLevel/?repage";
	}

	@ResponseBody
	@RequestMapping("treeDate4UserSalaryRelation")
	public List<Map<String, Object>> treeDate4UserSalaryRelation(SalaryLevel salaryLevel){
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<SalaryLevel> salarys =salaryLevelService.findList(salaryLevel);
		SalaryLevel tempSalary =null;
		String splitStr ="-";
		for (int i = 0; i < salarys.size(); i++) {
			Map<String, Object> map = Maps.newHashMap();
			tempSalary =salarys.get(i);
			map.put("id", tempSalary.getId());
			
			map.put("name", new StringBuilder().append(DictUtils.getDictLabel(tempSalary.getProfession(), "profession", ""))
							.append(splitStr)
							.append(DictUtils.getDictLabel(tempSalary.getGrade(), "profession_grade", ""))
							.append(splitStr)
							.append(tempSalary.getPayMonthly()));
			mapList.add(map);
		}
		return  mapList;
	}
}