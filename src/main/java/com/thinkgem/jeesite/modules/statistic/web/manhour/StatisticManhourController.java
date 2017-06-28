/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.statistic.web.manhour;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.statistic.entity.manhour.DeptManhour4pro;
import com.thinkgem.jeesite.modules.statistic.entity.manhour.StatisticManhour;
import com.thinkgem.jeesite.modules.statistic.service.manhour.StatisticManhourService;
import com.thinkgem.jeesite.modules.sys.entity.Role;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.ExportUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工时统计Controller
 * @author jicdata
 * @version 2016-03-15
 */
@Controller
@RequestMapping(value = "${adminPath}/statistic/manhour/statisticManhour")
public class StatisticManhourController extends BaseController {

	@Autowired
	private StatisticManhourService statisticManhourService;
	
	
     
	/**
	 * 一级界面
	 * 业务数据的过滤解释：
	 * 销售、销售部门领导、公司领导  依靠 设置bean的sqlmap.（jeesite提供的数据范围机制）实现的
	 * 
	 * 具体技术部门领导 查看数据  依靠 特定角色"statistic_manhour_technical"技术部门领导角色
	 * 	在StatisticManhourDao.xml中<if test="tectechnicalDepartmentId = null">${sqlMap.dsf}</if>①策略解除 数据范围的拦截，②然后在存储过程中只查询跟project_techapply的office_id为当前用户的部门 。
	 * 
	 * 前端：判断 statisticManhour对象中statistic_manhour_technical属性值。根据值显示对应列（①技术部门领导 只查看到 项目在自己部门产生的工时与费用。②销售、销售领导、公司领导用户 看到项目在各个技术部门产生的工时、费用和总的工时、费用）
	 */
	@RequiresPermissions("statistic:manhour:statisticManhour:view")
	@RequestMapping(value = {"list", ""})
	public String list(StatisticManhour statisticManhour, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		User currentUser = UserUtils.getUser();//当前用户
		
		/**
		 * 下面代码： 判断是不是“具体技术部门负责人”，
		 * 若是： 设置 参数statisticManhour的tectechnicalDepartmentId属性为当前用户的部门。
		 */
		List<Role> roleList = currentUser.getRoleList();
		for (Role role : roleList) {
			if("statistic_manhour_technical".equals(role.getEnname())){
				statisticManhour.setTectechnicalDepartmentId(currentUser.getOffice().getId());
				break;
			}
		}
		
		
		statisticManhour.getSqlMap().put("dsf", BaseService.dataScopeFilter(currentUser, "s5", "u4"));
		Page<StatisticManhour> findPage2 = statisticManhourService.findPage2(new Page<StatisticManhour>(request, response), statisticManhour);
		model.addAttribute("page", findPage2);
		return "modules/statistic/manhour/statisticManhourList";
	}

	/**
	 * 二级界面
	 * @param deptManhour4pro  主要参数  项目id、技术部门id
	 */
	@RequiresPermissions("statistic:manhour:statisticManhour:view")
	@RequestMapping(value = {"findDepartmentManhour4pro"})
	public String findDepartmentManhour4pro(DeptManhour4pro deptManhour4pro, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		
		Page<DeptManhour4pro> findPage = statisticManhourService.findPage4DeptManhour4pro(new Page<DeptManhour4pro>(request, response), deptManhour4pro);
		model.addAttribute("page", findPage);
		
		Map map = new HashMap<String, String>();
		model.addAttribute("deptManhour4pro", deptManhour4pro);
		
		return "modules/statistic/manhour/statisticDeptManhour4pro";
	}
	
	
	@RequiresPermissions("statistic:manhour:statisticManhour:view")
	@RequestMapping(value = "export")
	public void export(StatisticManhour statisticManhour,HttpServletRequest request, HttpServletResponse response,Map map) {
		String  fileReturnName = "工时统计表";
		String workBookFileRealPathName =request.getSession().getServletContext().getRealPath("/")+"WEB-INF/excel/project/StatisticManhour.xls";
		
		ArrayList<StatisticManhour> cycleObjectList =new  ArrayList<StatisticManhour>();
		
		/**
		 * 下面代码： 判断是不是“具体技术部门负责人”，
		 * 若是： 设置 参数statisticManhour的tectechnicalDepartmentId属性为当前用户的部门。
		 */
		User currentUser = UserUtils.getUser();
		List<Role> roleList = currentUser.getRoleList();
		for (Role role : roleList) {
			if("statistic_manhour_technical".equals(role.getEnname())){
				statisticManhour.setTectechnicalDepartmentId(currentUser.getOffice().getId());
				break;
			}
		}
		statisticManhour.getSqlMap().put("dsf", BaseService.dataScopeFilter(currentUser, "s5", "u4"));
		
		
		
		cycleObjectList.addAll(statisticManhourService.findList(statisticManhour));
		ExportUtils.export4Statistic(response, new Object(), cycleObjectList, workBookFileRealPathName, fileReturnName, "yyyy-MM-dd");
	}
	
	
	@RequiresPermissions("statistic:manhour:statisticManhour:view")
	@RequestMapping(value = "export4SecendPage")
	public void export4SecendPage(DeptManhour4pro deptManhour4pro, HttpServletRequest request, HttpServletResponse response) {
		String  fileReturnName = "工时详情表表";
		String workBookFileRealPathName =request.getSession().getServletContext().getRealPath("/")+"WEB-INF/excel/project/StatisticManhour4SecondPage.xls";
		//获取二级界面list列表
		ArrayList<DeptManhour4pro> cycleObjectList =new  ArrayList<DeptManhour4pro>();
		cycleObjectList.addAll(statisticManhourService.findList4DeptManhour4pro(deptManhour4pro));
		
		ExportUtils.export4Statistic(response, new Object(), cycleObjectList, workBookFileRealPathName, fileReturnName, "yyyy-MM-dd");
	}
	
}