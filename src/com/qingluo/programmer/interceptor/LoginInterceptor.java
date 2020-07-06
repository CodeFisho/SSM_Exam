package com.qingluo.programmer.interceptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.qingluo.programmer.entity.admin.Menu;
import com.qingluo.programmer.entity.admin.User;
import com.qingluo.programmer.util.MenuUtils;

import jdk.nashorn.internal.ir.RuntimeNode.Request;
import net.sf.json.JSONObject;
import net.sf.jsqlparser.expression.LongValue;

//��̨��¼������
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
		User currentUser=(User)request.getSession().getAttribute("admin");
		if(currentUser==null) {
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
			response.sendRedirect(request.getServletContext().getContextPath()+"/system/login");
			return false;
			
		}
		String midString=request.getParameter("_mid");
		if(!StringUtils.isEmpty(midString)) {
			List<Menu> allThirdMenus=MenuUtils.getAllThirdMenu((List<Menu>)request.getSession().getAttribute("userMenus"), Long.valueOf(midString));
			request.setAttribute("thirdMenuList", allThirdMenus);
		}
		return true;
	}

}
