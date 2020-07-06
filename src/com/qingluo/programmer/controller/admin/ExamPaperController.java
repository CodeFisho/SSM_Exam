package com.qingluo.programmer.controller.admin;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.qingluo.programmer.dao.admin.ExamPaperDao;
import com.qingluo.programmer.entity.admin.ExamPaper;
import com.qingluo.programmer.page.admin.Page;
import com.qingluo.programmer.service.admin.IExamPaperService;
import com.qingluo.programmer.service.admin.IExamService;
import com.qingluo.programmer.service.admin.IStudentService;

@Controller
@RequestMapping("/admin/exampaper")
public class ExamPaperController {

	@Autowired
	private IExamPaperService examPaperServiceImpl;
	
	@Autowired
	private IStudentService studentServceImpl;
	
	@Autowired
	private IExamService examServceImpl;
	
	
	//试卷管理页面
	@GetMapping("/list")
	public ModelAndView list(ModelAndView model) {
		Map<String, Object> queryMap=new HashMap<String, Object>();
		queryMap.put("offset",0);
		queryMap.put("pageSize", 99999);
		model.addObject("examList", examServceImpl.findList(queryMap));
		model.addObject("studentList", studentServceImpl.findList(queryMap));
		model.setViewName("exampaper/list");
		return model;
	}
	//试卷列表
	@PostMapping("/list")
	@ResponseBody
	public Map<String, Object> list(Page page,
			@RequestParam(name="examId",required = false) Long examId,
			@RequestParam(name="studentId",required = false) Long studentId,
			@RequestParam(name="status",required = false) Integer status			
			){
		Map<String, Object> resCode =new HashMap<String, Object>();
		Map<String, Object> queryMap=new HashMap<String, Object>();
		if(examId!=null) {
			queryMap.put("examId", examId);
		}
		if(studentId!=null) {
			queryMap.put("studentId", studentId);
		}
		if(status!=null) {
			queryMap.put("status", status);
		}
		queryMap.put("pageSize", page.getRows());
		queryMap.put("offset",page.getOffset());
		resCode.put("rows", examPaperServiceImpl.findList(queryMap));
		resCode.put("total", examPaperServiceImpl.getTotal(queryMap));
		return resCode;
		}
	
	//试卷信息添加
	@PostMapping("/add")
	@ResponseBody
	public Map<String, String> list(ExamPaper examPaper){
		Map<String, String> resCode=new HashMap<String, String>();
		if(examPaper==null) {
			resCode.put("type","error");
			resCode.put("msg", "请填写正确的信息");
			return resCode;
		}
		if(examPaper.getExamId()==null) {
			resCode.put("type","error");
			resCode.put("msg", "请选择所属考试");
			return resCode;
		}
		if(examPaper.getStudentId()==null) {
			resCode.put("type","error");
			resCode.put("msg", "请选择试卷所属学生");
			return resCode;
		}
		if(examPaperServiceImpl.add(examPaper)<=0) {
			resCode.put("type","error");
			resCode.put("msg", "添加试卷信息失败，请联系管理员");
			return resCode;
		}
		resCode.put("type","success");
		resCode.put("msg", "试卷添加成功");
		return resCode;
	}
	
	//试卷信息编辑
	@PostMapping("/edit")
	@ResponseBody
	public Map<String, String> edit(ExamPaper examPaper){
		Map<String, String> resCode=new HashMap<String, String>();
		if(examPaper==null) {
			resCode.put("type","error");
			resCode.put("msg", "请填写正确的信息");
			return resCode;
		}
		if(examPaper.getExamId()==null) {
			resCode.put("type","error");
			resCode.put("msg", "请选择所属考试");
			return resCode;
		}
		if(examPaper.getStudentId()==null) {
			resCode.put("type","error");
			resCode.put("msg", "请选择试卷所属学生");
			return resCode;
		}
		if(examPaperServiceImpl.edit(examPaper)<=0) {
			resCode.put("type","error");
			resCode.put("msg", "编辑试卷信息失败，请联系管理员");
			return resCode;
		}
		resCode.put("type","success");
		resCode.put("msg", "试卷编辑成功");
		return resCode;
	}
	
	//试卷信息删除
	@PostMapping("/delete")
	@ResponseBody
	public Map<String, String> delete(Long id){
		Map<String, String> resCode=new HashMap<String, String>();
		if(id==null) {
			resCode.put("type", "error");
			resCode.put("msg", "请选择需要删除的数据");
			return resCode;
		}
		try {
			if(examPaperServiceImpl.delete(id)<=0) {
				resCode.put("type", "error");
				resCode.put("msg", "删除失败，请联系管理员");
				return resCode;
			}
		} catch (Exception e) {
			resCode.put("type", "error");
			resCode.put("msg", "删除失败，该试卷下存在答题信息");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg", "删除成功");
		return resCode;
	}
}