/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.common.utils.excel.fieldtype;

import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * rgz
 * 字段类型转换
 * @author ThinkGem
 * @version 2013-03-10
 */
public class PrincipalType {

	/**
	 * 获取对象值（导入）
	 */
	public static Object getValue(String val) {
		if (StringUtils.isBlank(val))
			return null;
		User user = UserUtils.getByLoginName(val);
		if (user == null)
			return null;
		return user;
	}

	/**
	 * 设置对象值（导出）
	 */
	public static String setValue(Object val) {
		if (val != null && ((User)val).getLoginName() != null){
			return ((User)val).getLoginName();
		}
		return "";
	}
}
