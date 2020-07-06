package com.qingluo.programmer.interceptor.home;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.qingluo.programmer.entity.admin.Student;

import net.sf.json.JSONObject;

//ǰ̨��¼������
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
			System.out.println("����:"+requestUrlString+"����������");
			String header=request.getHeader("X-Requested-With");
			if("XMLHttpRequest".equals(header)) {
				//��ʾ������Ϊajax����
				Map<String, String> resCode=new HashMap<String, String>();
				resCode.put("type","error");
				resCode.put("msg","��¼�Ự��ʱ��δ��¼�������µ�¼!");
				response.getWriter().write(JSONObject.fromObject(resCode).toString());
				return false;
			}
			response.sendRedirect(request.getServletContext().getContextPath()+"/home/login");
			return false;
			
		}
		return true;
	}

}
