package com.thinkgem.jeesite.modules.sys.listener;

import com.thinkgem.jeesite.modules.sys.service.SystemService;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

// 监听器是web层的组件，它是tomcat实例化的，不是Spring实例化的，不能放到Spring中
public class WebContextListener extends org.springframework.web.context.ContextLoaderListener {
	
	@Override
	public WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
		if (!SystemService.printKeyLoadMessage()){
			return null;
		}
		return super.initWebApplicationContext(servletContext);
	}
}
