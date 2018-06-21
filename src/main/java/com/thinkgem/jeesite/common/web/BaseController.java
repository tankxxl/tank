/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.common.web;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.ActEntity;
import com.thinkgem.jeesite.common.service.JicActService;
import com.thinkgem.jeesite.modules.act.utils.UserTaskType;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.beanvalidator.BeanValidators;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.utils.DateUtils;

/**
 * 控制器支持类
 *
 *  servlet是单例的，而tomcat则是在多个线程中调用servlet的处理方法。
 *  因此如果servlet存在实例对象，那么就会引出线程安全的问题。
 * 	controller默认是单例的，是否会有线程安全问题。
 *
 * 在方法参数中写request是线程安全的，原因如下：
 * 1. 在controller中注入的request是jdk动态代理对象,ObjectFactoryDelegatingInvocationHandler的实例.
 * 	  当我们调用成员域request的方法的时候其实是调用了objectFactory的getObject()对象的相关方法.
 * 	  这里的objectFactory是RequestObjectFactory.
 * 2. RequestObjectFactory的getObject其实是从RequestContextHolder的threadlocal中去取值的.
 * 3. 请求刚进入springmvc的dispatcherServlet的时候会把request相关对象设置到RequestContextHolder的threadlocal中去.
 *
 * @author ThinkGem
 * @version 2013-3-23
 */
public abstract class BaseController {

	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 管理基础路径
	 */
	@Value("${adminPath}")
	protected String adminPath;
	
	/**
	 * 前端基础路径
	 */
	@Value("${frontPath}")
	protected String frontPath;
	
	/**
	 * 前端URL后缀
	 */
	@Value("${urlSuffix}")
	protected String urlSuffix;
	
	/**
	 * 验证Bean实例对象
	 */
	@Autowired
	protected Validator validator;

	/**
	 * 服务端参数有效性验证
	 * @param object 验证的实体对象
	 * @param groups 验证组
	 * @return 验证成功：返回true；严重失败：将错误信息添加到 message 中
	 */
	protected boolean beanValidator(Model model, Object object, Class<?>... groups) {
		try{
			BeanValidators.validateWithException(validator, object, groups);
		}catch(ConstraintViolationException ex){
			List<String> list = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
			list.add(0, "数据验证失败：");
			addMessage(model, list.toArray(new String[]{}));
			return false;
		}
		return true;
	}
	
	/**
	 * 服务端参数有效性验证
	 * @param object 验证的实体对象
	 * @param groups 验证组
	 * @return 验证成功：返回true；严重失败：将错误信息添加到 flash message 中
	 */
	protected boolean beanValidator(RedirectAttributes redirectAttributes, Object object, Class<?>... groups) {
		try{
			BeanValidators.validateWithException(validator, object, groups);
		}catch(ConstraintViolationException ex){
			List<String> list = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
			list.add(0, "数据验证失败：");
			addMessage(redirectAttributes, list.toArray(new String[]{}));
			return false;
		}
		return true;
	}
	
	/**
	 * 服务端参数有效性验证
	 * @param object 验证的实体对象
	 * @param groups 验证组，不传入此参数时，同@Valid注解验证
	 * @return 验证成功：继续执行；验证失败：抛出异常跳转400页面。
	 */
	protected void beanValidator(Object object, Class<?>... groups) {
		BeanValidators.validateWithException(validator, object, groups);
	}
	
	/**
	 * 添加Model消息
	 * @param
	 */
	protected void addMessage(Model model, String... messages) {
		StringBuilder sb = new StringBuilder();
		for (String message : messages){
			sb.append(message).append(messages.length>1?"<br/>":"");
		}
		model.addAttribute("message", sb.toString());
	}
	
	/**
	 * 添加Flash消息
	 *
	 */
	protected void addMessage(RedirectAttributes redirectAttributes, String... messages) {
		StringBuilder sb = new StringBuilder();
		for (String message : messages){
			sb.append(message).append(messages.length>1?"<br/>":"");
		}
		redirectAttributes.addFlashAttribute("message", sb.toString());
	}
	
	/**
	 * 客户端返回JSON字符串
	 * @param response
	 * @param object
	 * @return
	 */
	protected String renderString(HttpServletResponse response, Object object) {
		return renderString(response, JsonMapper.toJsonString(object), "application/json");
	}
	
	/**
	 * 客户端返回字符串
	 * @param response
	 * @param string
	 * @return
	 */
	protected String renderString(HttpServletResponse response, String string, String type) {
		try {
			response.reset();
	        response.setContentType(type);
	        response.setCharacterEncoding("utf-8");
			response.getWriter().print(string);
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 参数绑定异常
	 */
	@ExceptionHandler({BindException.class, ConstraintViolationException.class, ValidationException.class})
    public String bindException() {  
        return "error/400";
    }
	
	/**
	 * 授权登录异常
	 */
	@ExceptionHandler({AuthenticationException.class})
    public String authenticationException() {  
        return "error/403";
    }
	
	/**
	 * 初始化数据绑定
	 * 1. 将所有传递进来的String进行HTML编码，防止XSS攻击
	 * 2. 将字段中Date类型转换为String类型
	 */
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		// String类型转换，将所有传递进来的String进行HTML编码，防止XSS攻击
		binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				setValue(text == null ? null : StringEscapeUtils.escapeHtml4(text.trim()));
			}
			@Override
			public String getAsText() {
				Object value = getValue();
				return value != null ? value.toString() : "";
			}
		});
		// Date 类型转换
		binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				setValue(DateUtils.parseDate(text));
			}
//			@Override
//			public String getAsText() {
//				Object value = getValue();
//				return value != null ? DateUtils.formatDateTime((Date)value) : "";
//			}
		});
	}

//	protected HttpServletRequest request;
//	protected HttpServletResponse response;
//	protected HttpSession session;
//
//	@ModelAttribute
//    public void setReqAndRes(HttpServletRequest request, HttpServletResponse response) {
//	    this.request = request;
//	    this.response = response;
//	    this.session = request.getSession();
//    }

	// 抽取子类的表单保存逻辑
	protected String saveBusi(JicActService jicActService, ActEntity actEntity) {

		String flag = actEntity.getAct().getFlag();

		// flag在前台Form.jsp中传送过来，在些进行判断要进行的操作
		if ("saveOnly".equals(flag) ) { // 只保存表单数据
			jicActService.save(actEntity);
		} else if ("saveFinishProcess".equals(flag)) { // 保存并结束流程
			jicActService.saveFinishProcess(actEntity);
		} else if ("no".equalsIgnoreCase(flag)) { // 销毁申请单
			jicActService.delete(actEntity);
		} else { // 保存表单并启动流程
			jicActService.saveLaunch(actEntity);
		}
		return null;
	}

	// 抽取子类的显示界面判断逻辑
	// 表单展示的路由
	protected String formToView(ActEntity actEntity) {

		// 入口1：新建表单，直接返回空实体
		if (actEntity.getIsNewRecord()) {
			return getEdit();
		}

		String view = null;
		// 入口2：在流程图中配置，从待办、已办任务界面来的请求，entity和act都已加载
		// 环节编号
		String taskDefKey = actEntity.getAct().getTaskDefKey();
		// 先看是不是流程结束
		if(actEntity.getAct().isFinishTask()){
			view = getView();
		}
		// 修改环节，再看是不是发起人
		else if ( UserTaskType.UT_OWNER.equals(taskDefKey) ){
			view = getEdit();
		}
		// 某审批环节
		else {
			// 需要特殊界面的节点，在子类中实现此方法去判断
			view = otherNode(taskDefKey);
		}
		return view;
	}

	// 子类可以重载此函数，用来实现在其它审批节点时显示特别的界面
	protected String otherNode(String taskDefKey) {
		return getView();
	}

	// 保存之后的路由
	protected String saveToView(ActEntity actEntity) {
		String usertask_owner = actEntity.getAct().getTaskDefKey();
		if (UserTaskType.UT_OWNER.equals(usertask_owner)) { // 待办任务页面
			return "redirect:" + adminPath + "/act/task/todo/";
		} else { // 列表页面
			return "redirect:"+Global.getAdminPath() + getRedirectView(); // "/project/finish/projectFinishApproval/?repage";
		}
	}

	protected String getRedirectView() {
		return "";
	}

	protected String getView() {
		return "";
	}

	protected String getEdit() {
		return "";
	}
}
