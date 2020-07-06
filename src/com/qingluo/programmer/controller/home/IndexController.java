package com.qingluo.programmer.controller.home;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.qingluo.programmer.entity.admin.Student;
import com.qingluo.programmer.service.admin.IStudentService;
import com.qingluo.programmer.service.admin.ISubjectService;

//前台主页控制器
@RequestMapping("/home")
@Controller
public class IndexController {
	@Autowired
	private ISubjectService subjectServiceImpl;
	
	@Autowired
	private IStudentService studentServiceImpl;
	
	//前台登录页面
	@GetMapping("/login")
	public ModelAndView login(ModelAndView model) {
		model.setViewName("/home/login");
		return model;
	}
	
	//跳转注册页面
	@GetMapping("/register")
	public ModelAndView register(ModelAndView model) {
		Map<String, Object> queryMap=new HashMap<String, Object>();
		queryMap.put("offset",0);
		queryMap.put("pageSize", 99999);
		model.addObject("subjectList", subjectServiceImpl.findList(queryMap));
		model.setViewName("/home/register");
		return model;
	}
	
	//考生注册提交
	@PostMapping("/register")
	@ResponseBody
	public Map<String, String> register(Student student){
		Map<String, String> resCode=new HashMap<String, String>();
		if(student==null) {
			resCode.put("type", "error");
			resCode.put("msg","请填写正确的考生信息");
			return resCode;
		}
		if(StringUtils.isEmpty(student.getName())) {
			resCode.put("type", "error");
			resCode.put("msg","请填写登录名");
			return resCode;
		}
		if(StringUtils.isEmpty(student.getPassword())) {
			resCode.put("type", "error");
			resCode.put("msg","请填写密码");
			return resCode;
		}
		if(student.getSubjectId()==null) {
			resCode.put("type", "error");
			resCode.put("msg","请填写专业");
			return resCode;
		}
		Student existedStudent=studentServiceImpl.findNameByNameAndId(student.getName());
		if(existedStudent!=null) {
			resCode.put("type", "error");
			resCode.put("msg","该用户名已存在，请重新输入");
			return resCode;
		}
		try {
			if(studentServiceImpl.add(student)<=0) {
				resCode.put("type", "error");
				resCode.put("msg","注册失败，请联系管理员");
				return resCode;
			}
		} catch (Exception e) {
			resCode.put("type", "error");
			resCode.put("msg","数据库异常");
			return resCode;
		}
		
		resCode.put("type", "success");
		resCode.put("msg", "注册成功");
		return resCode;
	}
	
	@PostMapping("/login")
	@ResponseBody
	public Map<String, String> login(
			@RequestParam(name="name",required = true) String name,
			@RequestParam(name="password",required = true) String password,
			HttpServletRequest request){
		Map<String, String> resCode=new HashMap<String, String>();
		if(StringUtils.isEmpty(name)) {
			resCode.put("type", "error");
			resCode.put("msg","请输入用户名");
			return resCode;
		}
		if(StringUtils.isEmpty(password)) {
			resCode.put("type", "error");
			resCode.put("msg","请输入密码");
			return resCode;
		}
		Student currentStudent=studentServiceImpl.findAllInfo(name);
		if(currentStudent==null) {
			resCode.put("type", "error");
			resCode.put("msg","用户名错误，请重新输入");
			return resCode;
		}
		if(!password.equals(currentStudent.getPassword())) {
			resCode.put("type", "error");
			resCode.put("msg","密码错误，请重新输入");
			return resCode;
		}
		request.getSession().setAttribute("student", currentStudent);
		resCode.put("type", "success");
		resCode.put("msg","登录成功");
		return resCode;
	}
}
