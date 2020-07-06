package com.qingluo.programmer.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.qingluo.programmer.entity.admin.Student;
import com.qingluo.programmer.page.admin.Page;
import com.qingluo.programmer.service.admin.IStudentService;
import com.qingluo.programmer.service.admin.ISubjectService;

//考生控制器
@Controller
@RequestMapping("admin/student")
public class StudentController {
	@Autowired
	IStudentService studentServiceImpl;
	
	@Autowired
	private ISubjectService subjectServiceImpl;
	
	//考生列表页
	@GetMapping("/list")
	public ModelAndView list(ModelAndView model) {
		Map<String, Object> queryMap=new HashMap<String, Object>();
		queryMap.put("offset", 0);
		queryMap.put("pageSize",99999);
		model.addObject("subjectList", subjectServiceImpl.findList(queryMap));
		model.setViewName("student/list");
		return model;
	}
	
	//加载考生信息列表
	@RequestMapping(value="/list",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getMenuList(Page page,
			@RequestParam(name="name",required = false,defaultValue = "") String name,
			@RequestParam(name="subjetcId",required = false) Long subjectId
			){
		Map<String, Object> resCode=new HashMap<String, Object>();
		Map<String, Object> queryMap=new HashMap<String, Object>();
		queryMap.put("offset",page.getOffset());
		queryMap.put("pageSize",page.getRows());
		queryMap.put("name", name);
		if(subjectId!=null) {
			queryMap.put("subjectId", subjectId);
		}
		queryMap.put("subjectId", subjectId);
		resCode.put("rows",studentServiceImpl.findList(queryMap));
		resCode.put("total",studentServiceImpl.getTotal(queryMap));
		return resCode;
		}
	
	
	@PostMapping("/add")
	@ResponseBody
	public Map<String, String> add(Student student){
		Map<String, String> resCode=new HashMap<String, String>();
		if(student==null) {
			resCode.put("type", "error");
			resCode.put("msg", "请检查你输入的信息");
			return resCode;
		}
		if(student.getSubjectId()==null) {
			resCode.put("type", "error");
			resCode.put("msg", "请填写所属学科专业信息");
			return resCode;
		}
		if(StringUtils.isEmpty(student.getName())) {
			resCode.put("type", "error");
			resCode.put("msg", "请输入用户名");
			return resCode;
		}
		if(StringUtils.isEmpty(student.getPassword())) {
			resCode.put("type", "error");
			resCode.put("msg", "请填写登录密码");
			return resCode;
		}
		if(hasExist(student.getName(), 0L)) {
			resCode.put("type", "error");
			resCode.put("msg", "该用户名已存在，请重新输入");
			return resCode;
		}
		
		if(studentServiceImpl.add(student)<=0) {
			resCode.put("type", "error");
			resCode.put("msg", "考生信息添加失败，请联系管理员");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg", "考生信息添加成功");
		return resCode;
	}
	
	@PostMapping("/edit")
	@ResponseBody
	public Map<String, String> edit(Student student){
		Map<String, String> resCode=new HashMap<String, String>();
		if(student==null) {
			resCode.put("type", "error");
			resCode.put("msg", "请检查输入的数据");
			return resCode;
		}
		if(student.getId()==null) {
			resCode.put("type", "error");
			resCode.put("msg", "请选择需要修改的数据");
			return resCode;
		}
		if(StringUtils.isEmpty(student.getName())) {
			resCode.put("type", "error");
			resCode.put("msg", "请输入用户名");
			return resCode;
		}
		if(StringUtils.isEmpty(student.getPassword())) {
			resCode.put("type", "error");
			resCode.put("msg", "请填写登录密码");
			return resCode;
		}
		if(hasExist(student.getName(), student.getId())) {
			resCode.put("type", "error");
			resCode.put("msg", "该用户名已存在，请重新输入!");
			return resCode;
		}
		if(studentServiceImpl.edit(student)<=0) {
			resCode.put("type", "error");
			resCode.put("msg", "修改失败，请联系管理员");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg", "修改考生信息成功");
		return resCode;
	}
	
	
	@PostMapping("/delete")
	@ResponseBody
	public Map<String, String> delete(@RequestParam(name="id",required=true) Long id){
		Map<String, String> resCode=new HashMap<String, String>();
		if(id==null) {
			resCode.put("type", "error");
			resCode.put("msg", "请选择需要删除的考生信息");
			return resCode;
		}try {
			if(studentServiceImpl.delete(id)<=0) {
				resCode.put("type", "error");
				resCode.put("msg", "删除失败，请联系管理员");
				return resCode;
			}
		} catch (Exception e) {
			resCode.put("type", "error");
			resCode.put("msg", "该考生下存在考试信息，不能删除!");
			return resCode;
		}
		
		resCode.put("type", "success");
		resCode.put("msg", "考生信息删除成功");
		return resCode;
	}
	
	//判断用户名是否存在，如果不存在，返回false,存在则返回true
	public boolean hasExist(String name,Long id) {
		Student student=studentServiceImpl.findNameByNameAndId(name);
		if(student==null) return false;;//根据用户名没有查询到说明该用户名不存在
		if(student.getId()==id) return false;//如果根据用户名得到的用户id与当前传入的一致，则为本人
		return true;
	}
}
