/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.salary.web.relation;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.salary.entity.SalaryLevel;
import com.thinkgem.jeesite.modules.salary.entity.relation.UserSalaryRelation;
import com.thinkgem.jeesite.modules.salary.service.relation.UserSalaryRelationService;
import com.thinkgem.jeesite.modules.sys.entity.User;
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
import java.util.HashMap;

/**
 * 人员薪资管理Controller
 * @author jicdata
 * @version 2016-03-12
 */
@Controller
@RequestMapping(value = "${adminPath}/salary/relation/userSalaryRelation")
public class UserSalaryRelationController extends BaseController {

	@Autowired
	private UserSalaryRelationService userSalaryRelationService;
	
	@ModelAttribute
	public UserSalaryRelation get(@RequestParam(required=false) String id) {
		UserSalaryRelation entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userSalaryRelationService.get(id);
		}
		if (entity == null){
			entity = new UserSalaryRelation();
		}
		return entity;
	}
	
	@RequiresPermissions("salary:relation:userSalaryRelation:view")
	@RequestMapping(value = {"index"})
	public String index(UserSalaryRelation userSalaryRelation, HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/salary/relation/userSalaryIndex";
	}
	@RequiresPermissions("salary:relation:userSalaryRelation:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserSalaryRelation userSalaryRelation, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<UserSalaryRelation> page = userSalaryRelationService.findPage(new Page<UserSalaryRelation>(request, response), userSalaryRelation); 
		model.addAttribute("page", page);
		return "modules/salary/relation/userSalaryRelationList";
	}

	@RequiresPermissions("salary:relation:userSalaryRelation:view")
	@RequestMapping(value = "form")
	public String form(UserSalaryRelation userSalaryRelation, Model model) {
		model.addAttribute("userSalaryRelation", userSalaryRelation);
		return "modules/salary/relation/userSalaryRelationForm";
	}

	/**
	 * 
	 * @param userIds
	 * @param salaryIds
	 * @param salaryFlags 用于判断 是插入还是更新。。若hasSalary=ture 表示，用户已经设过薪资了，现在只是更新
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("salary:relation:userSalaryRelation:edit")
	@RequestMapping(value = "save")
	public String save(@RequestParam(value = "userIds[]") String[] userIds,@RequestParam(value = "salaryIds[]") String[] salaryIds,@RequestParam(value = "salaryFlags[]") boolean[] salaryFlags) {
		UserSalaryRelation tempRelation = null;
		for(int i =0;i<userIds.length;i++){
			tempRelation =  new UserSalaryRelation();
			tempRelation.setUser(new User(userIds[i]));
			tempRelation.setSalary(new SalaryLevel(salaryIds[i]));
			try{
				userSalaryRelationService.save(tempRelation,salaryFlags[i]);
			}catch(Exception e){
				//有可能出现 表单重复提交
			}
				
		}
		return "保存人员薪资成功";
	}
	
	@RequiresPermissions("salary:relation:userSalaryRelation:edit")
	@RequestMapping(value = "delete")
	public String delete(UserSalaryRelation userSalaryRelation, RedirectAttributes redirectAttributes) {
		userSalaryRelationService.delete(userSalaryRelation);
		addMessage(redirectAttributes, "删除人员薪资成功");
		return "redirect:"+Global.getAdminPath()+"/salary/relation/userSalaryRelation/?repage";
	}

	/**
	 * 根据用户ID获取用户薪资级别信息
	 * @author Arthur
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getAsJsonByUserId")
	public UserSalaryRelation getAsJsonByUserId(@RequestParam(required=false) String userId) {
		UserSalaryRelation entity = null;
		if (StringUtils.isNotBlank(userId)){
			HashMap<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("userId", userId);
			entity = userSalaryRelationService.getByParam(paramMap);
		}
		if (entity == null){
			entity = new UserSalaryRelation();
		}
		return entity;
	}

}