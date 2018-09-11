/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.test.web;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.test.entity.TestData;
import com.thinkgem.jeesite.test.service.TestDataService;
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

/**
 * 测试controller单例、多线程、实例变量相关
 * @author ThinkGem
 * @version 2015-04-06
 */
@Controller
@RequestMapping(value = "${adminPath}/controller")
public class ExampleController {

	// singletonInt的状态是共享的,因此Controller是单例的
	private int singletonInt = 1;

	@RequestMapping(value = "/test")
	@ResponseBody
	public String singleton(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String data = request.getParameter("data");
		if(data!=null&&data.length()>0){
			try{
				int paramInt= Integer.parseInt(data);
				singletonInt = singletonInt + paramInt;
			}
			catch(Exception ex){
				singletonInt+=10;
			}
		}else{
			singletonInt+=1000;
		}
		return String.valueOf(singletonInt);
	}


	@RequestMapping(value = "/sleepdata")
	@ResponseBody
	public String switcher(HttpServletRequest request
			, HttpServletResponse response)
			throws Exception {
		String sleep = request.getParameter("sleep");
		if (sleep.equals("on")) {
			Thread.currentThread().sleep(100000);
			return "sleep on";
		} else {
			return sleep;
		}
	}


}