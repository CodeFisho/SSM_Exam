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


import com.qingluo.programmer.entity.admin.Subject;
import com.qingluo.programmer.page.admin.Page;
import com.qingluo.programmer.service.admin.ISubjectService;

//学科专业后台控制器

@Controller
@RequestMapping("/admin/subject")
public class SubjectController {

	@Autowired
	private ISubjectService subjectServiceImpl;

	// 学科列表显示页
	@GetMapping("/list")
	public ModelAndView list(ModelAndView model) {
		model.setViewName("subject/list");
		return model;
	}

	//获取菜单列表
	@RequestMapping(value="/list",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getMenuList(Page page,
			@RequestParam(name="name",required = false,defaultValue = "") String name){
		Map<String, Object> resCode=new HashMap<String, Object>();
		Map<String, Object> queryMap=new HashMap<String, Object>();
		queryMap.put("offset",page.getOffset());
		queryMap.put("pageSize",page.getRows());
		queryMap.put("name", name);
		resCode.put("rows",subjectServiceImpl.findList(queryMap));
		resCode.put("total",subjectServiceImpl.getTotal(queryMap));
		return resCode;
		}
	
	//添加学科信息
	@PostMapping("/add")
	@ResponseBody
	public Map<String, String> add(Subject subject){
		Map<String, String> resCode=new HashMap<String, String>();
		if(subject==null) {
			resCode.put("type", "error");
			resCode.put("msg", "请检查输入的数据");
			return resCode;
		}
		if(StringUtils.isEmpty(subject.getName())) {
			resCode.put("type", "error");
			resCode.put("msg", "请填写学科名称");
			return resCode;
		}
		if(subjectServiceImpl.add(subject)<=0) {
			resCode.put("type", "error");
			resCode.put("msg", "添加失败，请联系管理员");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg", "添加学科信息成功");
		return resCode;		
	}
	 
	//修改学科信息
	@PostMapping("/edit")
	@ResponseBody
	public Map<String, String> edit(Subject subject){
		Map<String, String> resCode=new HashMap<String, String>();
		if(subject==null) {
			resCode.put("type", "error");
			resCode.put("msg", "请检查输入的数据");
			return resCode;
		}
		if(subject.getId()==null) {
			resCode.put("type", "error");
			resCode.put("msg", "请选择需要修改的数据");
			return resCode;
		}
		if(StringUtils.isEmpty(subject.getName())) {
			resCode.put("type", "error");
			resCode.put("msg", "请填写学科名称");
			return resCode;
		}
		if(subjectServiceImpl.edit(subject)<=0) {
			resCode.put("type", "error");
			resCode.put("msg", "修改学科信息失败，请联系管理员");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg", "修改学科信息成功");
		return resCode;		
	}
	
	//删除学科信息
	@PostMapping("/delete")
	@ResponseBody
	public Map<String, String> delete(@RequestParam(name="id",required = true) Long id){
		Map<String, String> resCode=new HashMap<String, String>();
		if(id==null) {
			resCode.put("type", "error");
			resCode.put("msg", "请选择需要删除的数据");
			return resCode;
		}
		try {
			if(subjectServiceImpl.delete(id)<=0) {
				resCode.put("type", "error");
				resCode.put("msg", "删除学科信息失败，请联系管理员");
				return resCode;
			}
		}catch (Exception e) {
			resCode.put("type", "error");
			resCode.put("msg", "该学科下存在考生，无法删除!");
			return resCode;
		}
		
		resCode.put("type", "success");
		resCode.put("msg", "删除成功");
		return resCode;		
	}
	
	

}
