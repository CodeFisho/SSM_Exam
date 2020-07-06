package com.qingluo.programmer.controller.admin;

import java.util.Date;
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

import com.qingluo.programmer.entity.admin.Exam;
import com.qingluo.programmer.entity.admin.Question;
import com.qingluo.programmer.page.admin.Page;
import com.qingluo.programmer.service.admin.IExamService;
import com.qingluo.programmer.service.admin.IQuestionService;
import com.qingluo.programmer.service.admin.ISubjectService;

import sun.java2d.d3d.D3DSurfaceData;

@Controller
@RequestMapping("/admin/exam")
public class ExamController {
	@Autowired
	private IExamService examServiceImpl;
	
	@Autowired
	private IQuestionService questionServiceImpl;
	
	@Autowired
	private ISubjectService subjectServiceImpl;
	
	//考试页面
	@GetMapping("/list")
	public ModelAndView list(ModelAndView model) {
		Map<String, Object> queryMap=new HashMap<String, Object>();
		queryMap.put("pageSize", 99999);
		queryMap.put("offset",0 );
		model.addObject("subjectList", subjectServiceImpl.findList(queryMap));
		model.addObject("singleScore", Question.QUESTION_TYPE_SINGLE_SCORE);
		model.addObject("muiltScore", Question.QUESTION_TYPE_MUILT_SOCRE);
		model.addObject("chargeScore", Question.QUESTION_TYPE_CHARGE_SCORE);
		model.setViewName("exam/list");;
		return model;
	}

	//考试列表
	@PostMapping("/list")
	@ResponseBody
	public Map<String, Object> list(Page page,
			@RequestParam(name="name",defaultValue = "") String name,
			@RequestParam(name="subjectId",required = false) Long subjectId,
			@RequestParam(name="startTime",required = false) String startTime,
			@RequestParam(name="endTime",required = false) String endTime
			){
		Map<String, Object> resCode =new HashMap<String, Object>();
		Map<String, Object> queryMap=new HashMap<String, Object>();
		if(!StringUtils.isEmpty(name)) {
			queryMap.put("name", name);
		}
		if(subjectId!=null) {
			queryMap.put("subjectId", subjectId);
		}
		if(!StringUtils.isEmpty(startTime)) {
			queryMap.put("startTime", startTime);
		}
		if(!StringUtils.isEmpty(endTime)) {
			queryMap.put("endTime", endTime);
		}
		queryMap.put("pageSize", page.getRows());
		queryMap.put("offset",page.getOffset());
		resCode.put("rows", examServiceImpl.findList(queryMap));
		resCode.put("total", examServiceImpl.getTotal(queryMap));
		return resCode;
	}
	//添加考试信息
	@PostMapping("/add")
	@ResponseBody
	public Map<String, String> add(Exam exam){
		Map<String, String> resCode=new HashMap<String, String>();
		if(exam==null) {
			resCode.put("type", "error");
			resCode.put("msg","请检查你输入的数据");
			return resCode;
		}
		if(StringUtils.isEmpty(exam.getName())) {
			resCode.put("type", "error");
			resCode.put("msg","请输入考试名称");
			return resCode;
		}
		if(exam.getSubjectId()==null) {
			resCode.put("type", "error");
			resCode.put("msg","请选择考试所属科目");
			return resCode;
		}
		if(exam.getStartTime()==null) {
			resCode.put("type", "error");
			resCode.put("msg","请填写考试开始时间");
			return resCode;
		}
		if(exam.getEndTime()==null) {
			resCode.put("type", "error");
			resCode.put("msg","请填写考试结束时间");
			return resCode;
		}
		if(exam.getPassScore()==0) {
			resCode.put("type", "error");
			resCode.put("msg","请填写考试及格分数");
			return resCode;
		}
		if(exam.getSingleQuestionNum()==0 && exam.getMuiltQuestionNum()==0 && exam.getChargeQuestionNum()==0) {
			resCode.put("type", "error");
			resCode.put("msg","单选题、多选题、判断题至少有一种类型的题目！");
			return resCode;
		}
		//此时去查询所填写的题型数量是否满足
		//获取单选题总数
		Map<String, Long> queryMap = new HashMap<String, Long>();
		queryMap.put("questionType", Long.valueOf(Question.QUESTION_TYPE_SINGLE));
		queryMap.put("subjectId", exam.getSubjectId());
		int singleQuestionTotalNum = questionServiceImpl.getQuestionNumByType(queryMap);
		if(exam.getSingleQuestionNum() > singleQuestionTotalNum){
			resCode.put("type", "error");
			resCode.put("msg", "单选题数量超过题库单选题总数，请修改!");
			return resCode;
		}
		//获取多选题总数
		queryMap.put("questionType", Long.valueOf(Question.QUESTION_TYPE_MUILT));
		int muiltQuestionTotalNum = questionServiceImpl.getQuestionNumByType(queryMap);
		if(exam.getMuiltQuestionNum() > muiltQuestionTotalNum){
			resCode.put("type", "error");
			resCode.put("msg", "多选题数量超过题库多选题总数，请修改!");
			return resCode;
		}
		//获取判断题总数
		queryMap.put("questionType", Long.valueOf(Question.QUESTION_TYPE_CHARGE));
		int chargeQuestionTotalNum = questionServiceImpl.getQuestionNumByType(queryMap);
		if(exam.getChargeQuestionNum() > chargeQuestionTotalNum){
			resCode.put("type", "error");
			resCode.put("msg", "判断题数量超过题库判断题总数，请修改!");
			return resCode;
		}
		//计算题目数量
		exam.setQuestionNum(exam.getSingleQuestionNum()+exam.getChargeQuestionNum()+exam.getMuiltQuestionNum());
		//计算总分
		exam.setTotalScore(exam.getSingleQuestionNum()*Question.QUESTION_TYPE_SINGLE_SCORE
				+exam.getMuiltQuestionNum()*Question.QUESTION_TYPE_MUILT_SOCRE
				+exam.getChargeQuestionNum()*Question.QUESTION_TYPE_CHARGE_SCORE
			);
		if(examServiceImpl.add(exam)<=0) {
			resCode.put("type", "error");
			resCode.put("msg","添加失败，请联系管理员!");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg","添加成功");
		return resCode;
	}
	//编辑考试信息
	@PostMapping("/edit")
	@ResponseBody
	public Map<String,String> edit(Exam exam){
		Map<String, String> resCode=new HashMap<String, String>();
		if(exam==null) {
			resCode.put("type", "error");
			resCode.put("msg","请选择需要修改的考试信息");
			return resCode;
		}
		if(StringUtils.isEmpty(exam.getName())) {
			resCode.put("type", "error");
			resCode.put("msg","请输入考试名称");
			return resCode;
		}
		if(exam.getSubjectId()==null) {
			resCode.put("type", "error");
			resCode.put("msg","请选择考试所属科目");
			return resCode;
		}
		if(exam.getStartTime()==null) {
			resCode.put("type", "error");
			resCode.put("msg","请填写考试开始时间");
			return resCode;
		}
		if(exam.getEndTime()==null) {
			resCode.put("type", "error");
			resCode.put("msg","请填写考试结束时间");
			return resCode;
		}
		if(exam.getPassScore()==0) {
			resCode.put("type", "error");
			resCode.put("msg","请填写考试及格分数");
			return resCode;
		}
		if(exam.getSingleQuestionNum()==0 && exam.getMuiltQuestionNum()==0 && exam.getChargeQuestionNum()==0) {
			resCode.put("type", "error");
			resCode.put("msg","单选题、多选题、判断题至少有一种类型的题目！");
			return resCode;
		}
		//此时去查询所填写的题型数量是否满足
		//获取单选题总数
		Map<String, Long> queryMap = new HashMap<String, Long>();
		queryMap.put("questionType", Long.valueOf(Question.QUESTION_TYPE_SINGLE));
		queryMap.put("subjectId", exam.getSubjectId());
		int singleQuestionTotalNum = questionServiceImpl.getQuestionNumByType(queryMap);
		if(exam.getSingleQuestionNum() > singleQuestionTotalNum){
			resCode.put("type", "error");
			resCode.put("msg", "单选题数量超过题库单选题总数，请修改!");
			return resCode;
		}
		//获取多选题总数
		queryMap.put("questionType", Long.valueOf(Question.QUESTION_TYPE_MUILT));
		int muiltQuestionTotalNum = questionServiceImpl.getQuestionNumByType(queryMap);
		if(exam.getMuiltQuestionNum() > muiltQuestionTotalNum){
			resCode.put("type", "error");
			resCode.put("msg", "多选题数量超过题库多选题总数，请修改!");
			return resCode;
		}
		//获取判断题总数
		queryMap.put("questionType", Long.valueOf(Question.QUESTION_TYPE_CHARGE));
		int chargeQuestionTotalNum = questionServiceImpl.getQuestionNumByType(queryMap);
		if(exam.getChargeQuestionNum() > chargeQuestionTotalNum){
			resCode.put("type", "error");
			resCode.put("msg", "判断题数量超过题库判断题总数，请修改!");
			return resCode;
		}
		/*
		 * Map<String, Long> queryMap=new HashMap<String, Long>();
		 * queryMap.put("subjectId", exam.getSubjectId()); queryMap.put("questionType",
		 * Long.valueOf(Question.QUESTION_TYPE_SINGLE)); //试卷中的某种题型的数量要基于实际，不能比已有的多。
		 * //获取单选题的数量 int
		 * totalSingle=questionServiceImpl.getQuestionNumByType(queryMap); //获取多选题的数量
		 * queryMap.put("questionType", Long.valueOf(Question.QUESTION_TYPE_MUILT)); int
		 * totalMuilt=questionServiceImpl.getQuestionNumByType(queryMap); //获取判断题的数量
		 * queryMap.put("questionId", Long.valueOf(Question.QUESTION_TYPE_CHARGE)); int
		 * totalCharge=questionServiceImpl.getQuestionNumByType(queryMap);
		 * if(exam.getSingleQuestionNum()>totalSingle) { resCode.put("type", "error");
		 * resCode.put("msg","单选题数量："+exam.getSingleQuestionNum()+"超过题库中的数量:"+
		 * totalSingle+"，请输入小于或等于此的数量!"); return resCode; }
		 * if(exam.getMuiltQuestionNum()>totalMuilt) { resCode.put("type", "error");
		 * resCode.put("msg","多选题数量："+exam.getMuiltQuestionNum()+"超过题库中的数量:"+totalMuilt+
		 * "，请输入小于或等于此的数量!"); return resCode; }
		 * if(exam.getChargeQuestionNum()>totalCharge) { resCode.put("type", "error");
		 * resCode.put("msg","判断题数量："+exam.getChargeQuestionNum()+"超过题库中的数量:"+
		 * totalCharge+"，请输入小于或等于此的数量!"); return resCode; }
		 */
		//计算题目数量
		exam.setQuestionNum(exam.getSingleQuestionNum()+exam.getChargeQuestionNum()+exam.getMuiltQuestionNum());
		//计算总分
		exam.setTotalScore(exam.getSingleQuestionNum()*Question.QUESTION_TYPE_SINGLE_SCORE
				+exam.getMuiltQuestionNum()*Question.QUESTION_TYPE_MUILT_SOCRE
				+exam.getChargeQuestionNum()*Question.QUESTION_TYPE_CHARGE_SCORE
			);
		if(examServiceImpl.edit(exam)<=0) {
			resCode.put("type", "error");
			resCode.put("msg","编辑失败，请联系管理员!");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg","编辑成功");
		return resCode;
	}
	//删除考试信息
	@PostMapping ("/delete")
	@ResponseBody
	public Map<String, String> delete(@RequestParam(name="id",required = true)Long id){
		Map<String, String> resCode=new HashMap<String, String>();
		if(id==null) {
			resCode.put("type", "error");
			resCode.put("msg","请选择需要删除的数据");
			return resCode;
		}
		try {
			if(examServiceImpl.delete(id)<=0) {
				resCode.put("type", "error");
				resCode.put("msg","删除失败，请联系管理员");
				return resCode;
			}
		}catch (Exception e) {
			resCode.put("type", "error");
			resCode.put("msg","删除失败，该考试下存在试卷信息或考试记录");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg","删除成功");
		return resCode;
	}
}
