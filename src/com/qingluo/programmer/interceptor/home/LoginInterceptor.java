package com.qingluo.programmer.interceptor.home;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.qingluo.programmer.entity.admin.Student;

import net.sf.json.JSONObject;

//前台登录拦截器
public class LoginInterceptor implements HandlerInterceptor{

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		// TODO Auto-generated method stub
		String requestUrlString=request.getRequestURI();
		Student currentStudent=(Student)request.getSession().getAttribute("student");
		if(currentStudent==null) {
			System.out.println("链接:"+requestUrlString+"进入拦截器");
			String header=request.getHeader("X-Requested-With");
			if("XMLHttpRequest".equals(header)) {
				//表示该请求为ajax请求
				Map<String, String> resCode=new HashMap<String, String>();
				resCode.put("type","error");
				resCode.put("msg","登录会话超时或还未登录，请重新登录!");
				response.getWriter().write(JSONObject.fromObject(resCode).toString());
				return false;
			}
			response.sendRedirect(request.getServletContext().getContextPath()+"/home/login");
			return false;
			
		}
		return true;
	}

}
