package com.thinkgem.jeesite.modules.sys.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinkgem.jeesite.common.service.BaseService;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by rgz on 09/06/2017.
 */
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.NamedThreadLocal;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


/**
 * <b>function:</b> spring mvc 请求拦截器
 * @author hoojo
 * @createDate 2016-11-24 下午3:19:27
 * @file MVCRequestInterceptor.java
 * @package com.xxx.eduyun.app.mvc.interceptor
 * @project eduyun-app-web
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class MVCRequestInterceptor extends BaseService implements HandlerInterceptor {

    private static final ObjectMapper mapper = new ObjectMapper();
    private NamedThreadLocal<Long>  startTimeThreadLocal = new NamedThreadLocal<Long>("StopWatch-startTimed");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        info("##############################【一个MVC完整请求开始】##############################");

        info("*******************MVC业务处理开始**********************");
        try {
            long timed = System.currentTimeMillis();
            startTimeThreadLocal.set(timed);

            String requestURL = request.getRequestURI();
            info("当前请求的URL：【{}】", requestURL);
            // info("执行目标方法: {}", handler);

            Map<String, ?> params = request.getParameterMap();
            if (!params.isEmpty()) {
                info("当前请求参数打印：");
                print(request.getParameterMap(), "参数");
            }
        } catch (Exception e) {
            error("MVC业务处理-拦截器异常：", e);
        }
        info("*******************MVC业务处理结束**********************");

        return true;
    }

    private void info(String s) {

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        info("*******************一个MVC 视图渲染开始**********************");

        try {
            // info("执行业务逻辑代码耗时：【{}】", TimeUtil.formatTime(new Date().getTime() - startTimeThreadLocal.get()));
            String requestURL = request.getRequestURI();
            info("当前请求的URL：【{}】", requestURL);

            if (modelAndView != null) {
                info("即将返回到MVC视图：{}", modelAndView.getViewName());

                if (modelAndView.getView() != null) {
                    info("返回到MVC视图内容类型ContentType：{}", modelAndView.getView().getContentType());
                }

                if (!modelAndView.getModel().isEmpty()) {

                    info("返回到MVC视图{}数据打印如下：", modelAndView.getViewName());
                    print(modelAndView.getModel(), "返回数据");
                }
            }
        } catch (Exception e) {
            error("MVC 视图渲染-拦截器异常：", e);
        }

        info("*******************一个MVC 视图渲染结束**********************");
    }

    private void error(String s, Exception e) {

    }

    private void info(String s, String requestURL) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        try {
            String requestURL = request.getRequestURI();
            info("MVC返回请求完成URL：【{}】", requestURL);
            // info("MVC返回请求完成耗时：【{}】", TimeUtil.formatTime(new Date().getTime() - startTimeThreadLocal.get()));
            if (ex != null) {
                info("MVC返回请求发生异常：", ex.getMessage());
                error("异常信息如下：", ex);
            }
        } catch (Exception e) {
            error("MVC完成返回-拦截器异常：", e);
        }

        info("##############################【一个MVC完整请求完成】##############################");
    }

    private void print(Map<String, ?> map, String prefix) {
        if (map != null) {
            Set<String> keys = map.keySet();
            Iterator<String> iter = keys.iterator();
            while (iter.hasNext()) {

                String name = iter.next();
                if (name.contains("org.springframework.validation.BindingResult")) {
                    continue;
                }

                String value = "";
                try {
                    value = mapper.writeValueAsString(map.get(name));
                } catch (Exception e) {
                    error("转换参数【{}】发生异常：", name, e);
                }
                info("{} \"{}\"： {}", prefix, name, value);
            }
        }
    }

    private void error(String s, String name, Exception e) {

    }

    private void info(String s, String prefix, String name, String value) {

    }
}
