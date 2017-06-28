package com.thinkgem.jeesite.modules.project.utils2;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.ResultSetDynaClass;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommonBeanUtils {
	
	@Test
	public void testUser() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Address address1 = new Address("中国1", "北京1", "100011", "方圆大厦1");
		Address address2 = new Address("中国2", "北京2", "100011", "方圆大厦2");
		Address address3 = new Address("中国3", "北京3", "100012", "方圆大厦3");
		Address[] addresses = {address1, address2, address3};
		
		Map phone = new HashMap();
		phone.put("home", "010-12345678");
		phone.put("office", "010-63975800");
		
		Profile profile = new Profile(phone, addresses, new Date(), "rengangzai@cjitec.com");
		
		Long userId = 123L;
		String username = "rengangzai";
		String password = "pwd";
		User user = new User(userId, username, password, profile);
		
		// BeanUtils
//		1．获取属性，支持”级联获取”：
//		BeanUtils.getProperty(ClassName, PropertyName);//不管属性是什么类型，都返回字符串类型
//		PropertyUtils.getProperty(Object bean, String name)//返回字段原始类型
		System.out.println(BeanUtils.getProperty(user, "username"));
		System.out.println(PropertyUtils.getProperty(user, "username"));
		System.out.println(BeanUtils.getProperty(user, "profile.email"));
		System.out.println(BeanUtils.getProperty(user, "profile.birthDate"));
		System.out.println(BeanUtils.getProperty(user, "profile.phone(home)"));
		System.out.println(BeanUtils.getProperty(user, "profile.phone(office)"));
		System.out.println(BeanUtils.getProperty(user, "profile.address[0].city"));
		
		
//		2．动态生成Bean：
		ResultSetDynaClass xx ;
		LazyDynaBean address = new LazyDynaBean();
		address.set("country", "中国");
		address.set("city", "北京");
		address.set("postCode", "100120");
		address.set("addr", "天安门北大街888号");
//		…
		System.out.println(BeanUtils.getProperty(user, "username"));
		System.out.println(BeanUtils.getProperty(user, "profile.birthDate"));

		
		
	}

}
