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

//ǰ̨��ҳ������
@RequestMapping("/home")
@Controller
public class IndexController {
	@Autowired
	private ISubjectService subjectServiceImpl;
	
	@Autowired
	private IStudentService studentServiceImpl;
	
	//ǰ̨��¼ҳ��
	@GetMapping("/login")
	public ModelAndView login(ModelAndView model) {
		model.setViewName("/home/login");
		return model;
	}
	
	//��תע��ҳ��
	@GetMapping("/register")
	public ModelAndView register(ModelAndView model) {
		Map<String, Object> queryMap=new HashMap<String, Object>();
		queryMap.put("offset",0);
		queryMap.put("pageSize", 99999);
		model.addObject("subjectList", subjectServiceImpl.findList(queryMap));
		model.setViewName("/home/register");
		return model;
	}
	
	//����ע���ύ
	@PostMapping("/register")
	@ResponseBody
	public Map<String, String> register(Student student){
		Map<String, String> resCode=new HashMap<String, String>();
		if(student==null) {
			resCode.put("type", "error");
			resCode.put("msg","����д��ȷ�Ŀ�����Ϣ");
			return resCode;
		}
		if(StringUtils.isEmpty(student.getName())) {
			resCode.put("type", "error");
			resCode.put("msg","����д��¼��");
			return resCode;
		}
		if(StringUtils.isEmpty(student.getPassword())) {
			resCode.put("type", "error");
			resCode.put("msg","����д����");
			return resCode;
		}
		if(student.getSubjectId()==null) {
			resCode.put("type", "error");
			resCode.put("msg","����дרҵ");
			return resCode;
		}
		Student existedStudent=studentServiceImpl.findNameByNameAndId(student.getName());
		if(existedStudent!=null) {
			resCode.put("type", "error");
			resCode.put("msg","���û����Ѵ��ڣ�����������");
			return resCode;
		}
		try {
			if(studentServiceImpl.add(student)<=0) {
				resCode.put("type", "error");
				resCode.put("msg","ע��ʧ�ܣ�����ϵ����Ա");
				return resCode;
			}
		} catch (Exception e) {
			resCode.put("type", "error");
			resCode.put("msg","���ݿ��쳣");
			return resCode;
		}
		
		resCode.put("type", "success");
		resCode.put("msg", "ע��ɹ�");
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
			resCode.put("msg","�������û���");
			return resCode;
		}
		if(StringUtils.isEmpty(password)) {
			resCode.put("type", "error");
			resCode.put("msg","����������");
			return resCode;
		}
		Student currentStudent=studentServiceImpl.findAllInfo(name);
		if(currentStudent==null) {
			resCode.put("type", "error");
			resCode.put("msg","�û�����������������");
			return resCode;
		}
		if(!password.equals(currentStudent.getPassword())) {
			resCode.put("type", "error");
			resCode.put("msg","�����������������");
			return resCode;
		}
		request.getSession().setAttribute("student", currentStudent);
		resCode.put("type", "success");
		resCode.put("msg","��¼�ɹ�");
		return resCode;
	}
}
