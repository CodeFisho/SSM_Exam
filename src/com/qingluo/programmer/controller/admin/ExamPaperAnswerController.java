package com.qingluo.programmer.controller.admin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.qingluo.programmer.page.admin.Page;
import com.qingluo.programmer.service.admin.IExamPaperAnswerService;
import com.qingluo.programmer.service.admin.IExamService;
import com.qingluo.programmer.service.admin.IQuestionService;
import com.qingluo.programmer.service.admin.IStudentService;

//答题信息控制器
@Controller
@RequestMapping("/admin/examPaperAnswer")
public class ExamPaperAnswerController {

	@Autowired
	private IExamPaperAnswerService examPaperAnswerServiceImpl;
	
	@Autowired
	private IStudentService studentServceImpl;
	
	@Autowired
	private IQuestionService questionServceImpl;
	
	@Autowired
	private IExamService examServiceImpl;
	
	
	//试卷答题管理页面
	@GetMapping("/list")
	public ModelAndView list(ModelAndView model) {
		Map<String, Object> queryMap=new HashMap<String, Object>();
		queryMap.put("offset",0);
		queryMap.put("pageSize", 99999);
		model.addObject("examList", examServiceImpl.findList(queryMap));
		model.addObject("studentList", studentServceImpl.findList(queryMap));
		model.addObject("questionList",questionServceImpl.findList(queryMap));
		model.setViewName("examPaperAnswer/list");
		return model;
	}
	//试卷答题列表
	@PostMapping("/list")
	@ResponseBody
	public Map<String, Object> list(Page page,
			@RequestParam(name="examId",required = false) Long examId,
			@RequestParam(name="studentId",required = false) Long studentId,
			@RequestParam(name="questionId",required = false) Long questionId			
			){
		Map<String, Object> resCode =new HashMap<String, Object>();
		Map<String, Object> queryMap=new HashMap<String, Object>();
		if(examId!=null) {
			queryMap.put("examId", examId);
		}
		if(studentId!=null) {
			queryMap.put("studentId", studentId);
		}
		if(questionId!=null) {
			queryMap.put("questionId", questionId);
		}
		queryMap.put("pageSize", page.getRows());
		queryMap.put("offset",page.getOffset());
		resCode.put("rows", examPaperAnswerServiceImpl.findList(queryMap));
		resCode.put("total", examPaperAnswerServiceImpl.getTotal(queryMap));
		return resCode;
		}
}