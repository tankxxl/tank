/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.crm.common;

import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.CacheUtils;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.modules.sys.dao.*;
import com.thinkgem.jeesite.modules.sys.entity.*;
import com.thinkgem.jeesite.modules.sys.security.SystemAuthorizingRealm.Principal;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import java.util.List;

/**
 * 用户工具类
 * @author ThinkGem
 * @version 2013-12-05
 */
public class UserUtils2 extends UserUtils {

	/**
	 * 获取当前用户有权限访问的部门
	 * @return
	 */
	public static List<Office> getOfficeList(User user) {
		if (user == null)
			return null;
		List<Office> officeList = null;
		if (user.isAdmin()){
			officeList = officeDao.findAllList(new Office());
		} else {
			Office office = new Office();
			office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));
			officeList = officeDao.findList(office);
		}
		return officeList;
	}

	public static List<Office> getOfficeList(String loginName) {
		// User user = get(userId);
		User user = getByLoginName(loginName);

		return getOfficeList(user);
	}
	
}
