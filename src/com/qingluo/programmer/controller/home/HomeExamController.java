package com.qingluo.programmer.controller.home;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
//前台用户考试控制器
import org.springframework.web.servlet.ModelAndView;


import com.qingluo.programmer.entity.admin.Exam;
import com.qingluo.programmer.entity.admin.ExamPaper;
import com.qingluo.programmer.entity.admin.ExamPaperAnswer;
import com.qingluo.programmer.entity.admin.Question;
import com.qingluo.programmer.entity.admin.Student;
import com.qingluo.programmer.service.admin.IExamPaperAnswerService;
import com.qingluo.programmer.service.admin.IExamPaperService;
import com.qingluo.programmer.service.admin.IExamService;
import com.qingluo.programmer.service.admin.IQuestionService;
import com.sun.org.apache.bcel.internal.generic.NEW;

import net.sf.json.JSONArray;
@Controller
@RequestMapping("/home/exam")
public class HomeExamController {
	
	@Autowired
	private IExamService examServiceImpl;
	@Autowired
	private IExamPaperService examPaperServiceImpl;
	@Autowired
	private IQuestionService questionServiceImpl;
	@Autowired
	private IExamPaperAnswerService examPaperAnswerServiceImpl;
	
	//考试前检查是否满足考试条件，并随机生成试题
	@PostMapping("/start_exam")
	@ResponseBody
	public Map<String, String> startExam(Long examId,HttpServletRequest request){
		Map<String, String> resCode=new HashMap<String, String>();
		Map<String, Object> queryMap=new HashMap<String, Object>();
		Exam exam=examServiceImpl.findById(examId);
		if(exam==null) {
			resCode.put("tyepe", "error");
			resCode.put("msg", "考试信息不存在");
			return resCode;
		}
		if(exam.getEndTime().getTime()<new Date().getTime()) {
			resCode.put("tyepe", "error");
			resCode.put("msg", "该考试已结束");
			return resCode;
		}
		Student currentStudent=(Student)request.getSession().getAttribute("student");
		if(exam.getSubjectId().longValue()!=currentStudent.getSubjectId().longValue()) {
			resCode.put("tyepe", "error");
			resCode.put("msg", "请选择相关的学科进行考试");
			return resCode;
		}
		queryMap.put("examId", exam.getId());
		queryMap.put("studentId",currentStudent.getId());
		ExamPaper examPaper=examPaperServiceImpl.findOnePaper(queryMap);
		if(examPaper!=null) {
			//表示试卷考过
			if(examPaper.getStatus()==1) {
				resCode.put("tyepe", "error");
				resCode.put("msg", "你已考过该考试");
				return resCode;
			}
			//试卷已经生成但是未提交，可以重新考试
			resCode.put("type", "success");
			resCode.put("msg", "考试条件审查通过，可以参加考试");
			return resCode;	
		}
		//符合考试条件，随机生成试卷
		//判断是否满足生成试卷的条件，即题库中的库存数量不满足
		//试卷中的某种题型的数量要基于实际，不能比已有的多。
		//此时去查询所填写的题型数量是否满足
		//获取单选题总数
		Map<String, Long> qMap = new HashMap<String, Long>();
		qMap.put("questionType", Long.valueOf(Question.QUESTION_TYPE_SINGLE));
		qMap.put("subjectId", exam.getSubjectId());
		int singleQuestionTotalNum = questionServiceImpl.getQuestionNumByType(qMap);
		if(exam.getSingleQuestionNum() > singleQuestionTotalNum){
			resCode.put("type", "error");
			resCode.put("msg", "单选题数量超过题库单选题总数，请修改!");
			return resCode;
		}
		//获取多选题总数
		qMap.put("questionType", Long.valueOf(Question.QUESTION_TYPE_MUILT));
		int muiltQuestionTotalNum = questionServiceImpl.getQuestionNumByType(qMap);
		if(exam.getMuiltQuestionNum() > muiltQuestionTotalNum){
			resCode.put("type", "error");
			resCode.put("msg", "多选题数量超过题库多选题总数，请修改!");
			return resCode;
		}
		//获取判断题总数
		qMap.put("questionType", Long.valueOf(Question.QUESTION_TYPE_CHARGE));
		int chargeQuestionTotalNum = questionServiceImpl.getQuestionNumByType(qMap);
		if(exam.getChargeQuestionNum() > chargeQuestionTotalNum){
			resCode.put("type", "error");
			resCode.put("msg", "判断题数量超过题库判断题总数，请修改!");
			return resCode;
		}
		//满足条件，生成试题
		ExamPaper newExamPaper=new ExamPaper();
		newExamPaper.setExamId(examId);
		newExamPaper.setStatus(0);
		newExamPaper.setStudentId(currentStudent.getId());
		newExamPaper.setTotalScore(exam.getTotalScore());
		newExamPaper.setUseTime(0);
		if(examPaperServiceImpl.add(newExamPaper)<=0) {
			resCode.put("type", "error");
			resCode.put("msg","生成试卷失败，请联系管理员");
			return resCode;
		}
		//试卷生成成功，需要填充上试题
		
		if(exam.getSingleQuestionNum()>0) {
			//获取单选题列表
			List<Question> singleQuestionList=questionServiceImpl.findQuestionByType(exam.getSubjectId(),Question.QUESTION_TYPE_SINGLE);
			//获取随机生成的单选题列表,生成答题试题并插入到数据库中
			List<Question> selectedSingleQuestions=getRandomList(singleQuestionList, exam.getSingleQuestionNum());
			insertQuestionAnswer(selectedSingleQuestions, examId, newExamPaper.getId(), currentStudent.getId());
		}
		if(exam.getMuiltQuestionNum()>0) {
			//获取多选题列表
			List<Question> muiltQuestionList=questionServiceImpl.findQuestionByType(exam.getSubjectId(),Question.QUESTION_TYPE_MUILT);
			//获取随机生成的多选题列表
			List<Question> selectedMuiltQuestions=getRandomList(muiltQuestionList, exam.getMuiltQuestionNum());
			insertQuestionAnswer(selectedMuiltQuestions, examId, newExamPaper.getId(), currentStudent.getId());
		}
		if(exam.getChargeQuestionNum()>0) {
			//获取判断题列表
			List<Question> chargeQuestionList=questionServiceImpl.findQuestionByType(exam.getSubjectId(),Question.QUESTION_TYPE_CHARGE);
			//获取随机生成的判断题列表
			List<Question> selectedChargeQuestions=getRandomList(chargeQuestionList, exam.getChargeQuestionNum());
			insertQuestionAnswer(selectedChargeQuestions, examId, newExamPaper.getId(), currentStudent.getId());
		}
		exam.setPaperNum(exam.getPaperNum()+1);
		examServiceImpl.updateExam(exam);
		resCode.put("type", "success");
		resCode.put("msg", "试卷生成成功!");
		return resCode;
	}
	
	
	
	//开始考试页面，从后端返回给前端数据并在前端显示
	@GetMapping("/examing")
	public ModelAndView index(ModelAndView model,
			@RequestParam(name="examId") Long examId,
			HttpServletRequest request
			) {	
		Map<String, Object> queryMap=new HashMap<String, Object>();		
		Student currentStudent=(Student)request.getSession().getAttribute("student");
		Exam currentExam=examServiceImpl.findById(examId);
		//判断试卷是否存在
		if(currentExam==null) {
			model.setViewName("/home/exam/error");
			model.addObject("msg", "当前考试不存在");
			return model;
		}
		//判断试卷是否在可考时间内
		if(currentExam.getEndTime().getTime()<new Date().getTime()) {
			model.setViewName("/home/exam/error");
			model.addObject("msg", "当前考试已过期");
			return model;
		}
		//判断当前学生的学科专业是否与代考科目一致 
		if(currentExam.getSubjectId().longValue()!=currentStudent.getSubjectId()) {
			model.setViewName("/home/exam/error");
			model.addObject("msg", "您的学科与考试科目不一致，请选择本专业的考试进行考试");
			return model;
		}
		queryMap.put("examId", currentExam.getId());
		queryMap.put("studentId", currentStudent.getId());
		//根据学生id和考试id获取到该学生的试卷
		ExamPaper examPaper=examPaperServiceImpl.findOnePaper(queryMap);
		if(examPaper==null) {
			model.setViewName("/home/exam/error");
			model.addObject("msg", "试卷生成错误，请联系管理员");
			return model;
		}
		if(examPaper.getStatus()==1) {
			model.setViewName("/home/exam/error");
			model.addObject("msg", "您已完成该考试");
			return model;
		}
		//如果开始考试的时间存在于session中，说明不是第一次进入此页面；如果为空，说明是第一次，将时间存入session
		Date now=new Date();
		Object startExamTimeObject=request.getSession().getAttribute("startExamTime");		
		if(startExamTimeObject==null) {
			request.getSession().setAttribute("startExamTime", now);
		}
		Date startExamTime=(Date)request.getSession().getAttribute("startExamTime");
		int passTiem=(int)(now.getTime()-startExamTime.getTime())/1000/60;
		//System.out.println(passTiem+"----"+currentExam.getAvaliableTime());
		if(passTiem>=currentExam.getAvaliableTime()) {
			
			//表示时间耗尽，按未答题处理
			examPaper.setScore(0);
			examPaper.setStartExamTime(startExamTime);
			examPaper.setStatus(1);
			examPaper.setUseTime(passTiem);
			examPaperServiceImpl.submitPaper(examPaper);
			model.setViewName("/home/exam/error");
			model.addObject("msg", "考试超时，按缺考处理");
			return model;
		}
		Integer hour=(currentExam.getAvaliableTime()-passTiem)/60;
		Integer minute=(currentExam.getAvaliableTime()-passTiem)%60;
		Integer second=(currentExam.getAvaliableTime()*60-(int)(now.getTime()-startExamTime.getTime())/1000)%60;
		//获取题目与答题的集合
		queryMap.put("examPaperId", examPaper.getId());
		List<ExamPaperAnswer> answerList=examPaperAnswerServiceImpl.findByStudent(queryMap);
		//取得单选题列表
		List<ExamPaperAnswer> singleQuestionList=getNeedQuestionList(answerList, Question.QUESTION_TYPE_SINGLE);
		//取得多选题列表
		List<ExamPaperAnswer> muiltQuestionList=getNeedQuestionList(answerList, Question.QUESTION_TYPE_MUILT);
		//取得判断题列表
		List<ExamPaperAnswer> chargeQuestionList=getNeedQuestionList(answerList, Question.QUESTION_TYPE_CHARGE);
		
		//在model中存入三种类型的列表，方便在前端遍历
		model.addObject("singleQuestionList", singleQuestionList);
		model.addObject("muiltQuestionList", muiltQuestionList);
		model.addObject("chargeQuestionList", chargeQuestionList);
		//在前端显示答题时间
		model.addObject("hour", hour);
		model.addObject("minute",minute);
		model.addObject("second",second);
		
		model.addObject("exam", currentExam);
		model.addObject("examPaper", examPaper);
		//将每种题目的类型和分值存入model，供前端调用
		model.addObject("singleScore", Question.QUESTION_TYPE_SINGLE_SCORE);
		model.addObject("muiltScore", Question.QUESTION_TYPE_MUILT_SOCRE);
		model.addObject("chargeScore", Question.QUESTION_TYPE_CHARGE_SCORE);
		model.addObject("singleQuestion", Question.QUESTION_TYPE_SINGLE);
		model.addObject("muiltQuestion", Question.QUESTION_TYPE_MUILT);
		model.addObject("chargeQuestion", Question.QUESTION_TYPE_CHARGE);
		model.addObject("nowTime", System.currentTimeMillis());
		model.setViewName("/home/exam/examing");
		return model;
	}
	//随机生成试题实现
		private List<Question> getRandomList(List<Question> questionList,int num){
			if(questionList.size()<=num) return questionList;
			Map<Integer, String> selectedMap=new HashMap<Integer, String>();
			List<Question> selectedList=new ArrayList<Question>();
			//从试题列表中随机获取题目，存入List中并返回
			while(selectedList.size()<num) {
				for(Question question:questionList) {
					int index=(int)(Math.random()*questionList.size());
					if(!selectedMap.containsKey(index)) {
						selectedMap.put(index, "");
						selectedList.add(questionList.get(index));
						if(selectedList.size()>=num) break;
					}
				}
			}
			return selectedList;
		}
		
	//提交题目答案	
	@PostMapping("/submit_answer")
	@ResponseBody
	private Map<String, String> submitAnswer(ExamPaperAnswer examPaperAnswer,HttpServletRequest request){		
		Map<String, String> resCode=new HashMap<String, String>();
		Map<String, Object> queryMap=new HashMap<String, Object>();
		if(examPaperAnswer==null) {
			resCode.put("type", "error");
			resCode.put("msg", "请正确操作");
			return resCode;
		}
		Exam currentExam=examServiceImpl.findById(examPaperAnswer.getExamId());
		if(currentExam==null) {
			resCode.put("type", "error");
			resCode.put("msg", "考试信息不存在");
			return resCode;
		}
		Student currentStudent=(Student)request.getSession().getAttribute("student");
		if(currentStudent==null) {
			resCode.put("type", "error");
			resCode.put("msg", "请正确操作");
			return resCode;
		}
		queryMap.put("examId", currentExam.getId());
		queryMap.put("studentId",currentStudent.getId());
		ExamPaper examPaper=examPaperServiceImpl.findOnePaper(queryMap);
		if(examPaper==null) {
			resCode.put("type", "error");
			resCode.put("msg", "试卷不存在");
			return resCode;	
		}
		if(examPaper.getId().longValue()!=examPaperAnswer.getExamPaperId().longValue()) {
			resCode.put("type", "error");
			resCode.put("msg", "试卷错误");
			return resCode;	
		}
		
		//无误后，将提交的答案保存到数据库中
		Question currentQuestion=questionServiceImpl.findById(examPaperAnswer.getQuestionId());
		if(currentQuestion==null) {
			resCode.put("type", "error");
			resCode.put("msg", "试题不存在");
			return resCode;	
		}
		examPaperAnswer.setStudentId(currentStudent.getId());
		if(currentQuestion.getAnswer().equals(examPaperAnswer.getAnswer())) {
			examPaperAnswer.setIsCorrect(1);
		}
		if(examPaperAnswerServiceImpl.submitAnswer(examPaperAnswer)<=0) {
			resCode.put("type", "error");
			resCode.put("msg", "答题出错，请联系管理员");
			return resCode;	
		}
		resCode.put("type", "success");
		return resCode;
	}	
	
	//交卷操作
	@PostMapping("/submit_exampaper")
	@ResponseBody
	private Map<String, String> submitExamPaper(@RequestParam(name="examId")Long examId, 
			@RequestParam(name="examPaperId")Long examPaperId,
			HttpServletRequest request){
		Map<String, String> resCode=new HashMap<String, String>();
		Exam currentExam=examServiceImpl.findById(examId);
		if(currentExam==null) {
			resCode.put("type", "error");
			resCode.put("msg", "考试不存在");
			return resCode;
		}
		if(currentExam.getEndTime().getTime()<new Date().getTime()) {
			resCode.put("type", "error");
			resCode.put("msg", "考试已结束");
			return resCode;
		}
		Student currentStudent=(Student)request.getSession().getAttribute("student");
		if(currentExam.getSubjectId().longValue()!=currentStudent.getSubjectId().longValue()) {
			resCode.put("type", "error");
			resCode.put("msg", "学科不同，无法进行相关操作");
			return resCode;
		}
		Map<String, Object>queryMap=new HashMap<String, Object>();
		queryMap.put("examId", examId);
		queryMap.put("studentId", currentStudent.getId());
		ExamPaper currentExamPaper=examPaperServiceImpl.findOnePaper(queryMap);
		if(currentExamPaper==null) {
			resCode.put("type", "error");
			resCode.put("msg", "试卷不存在");
			return resCode;
		}
		if(currentExamPaper.getId().longValue()!=examPaperId.longValue()) {
			resCode.put("type", "error");
			resCode.put("msg", "试卷错误");
			return resCode;
		}
		if(currentExamPaper.getStatus()==1) {
			resCode.put("type", "error");
			resCode.put("msg", "请勿重复提交");
			return resCode;
		}
		//计算考试得分
		//获取当前试卷的答题信息列表
		queryMap.put("examPaperId",currentExamPaper.getId());
		int score=0;//试卷总得分
		List<ExamPaperAnswer> answerList=examPaperAnswerServiceImpl.findByStudent(queryMap);
		for(ExamPaperAnswer answer:answerList) {
			if(answer.getIsCorrect()==1) {
				score+=answer.getQuestion().getScore();
			}
		}
		currentExamPaper.setEndExamTime(new Date());
		currentExamPaper.setScore(score);
		currentExamPaper.setStartExamTime((Date)request.getSession().getAttribute("startExamTime"));
		currentExamPaper.setStatus(1);
		currentExamPaper.setUseTime((int)(currentExamPaper.getEndExamTime().getTime()-currentExamPaper.getStartExamTime().getTime())/1000/60);
		if(examPaperServiceImpl.submitPaper(currentExamPaper)<=0) {
			resCode.put("type", "error");
			resCode.put("msg", "试卷提交失败，请联系管理员");
			return resCode;
		}
		currentExam.setExamedNum(currentExam.getExamedNum()+1);
		if(currentExamPaper.getScore()>=currentExam.getPassScore()) {
			currentExam.setPassNum(currentExam.getPassNum()+1);
		}
		request.getSession().setAttribute("startExamTime", null);
		//试卷提交后更新考试的相关信息
		examServiceImpl.updateExam(currentExam);
		resCode.put("type","success");
		resCode.put("msg", "提交成功");
		return resCode;
	}
	
	
	//将生成的试题插入到数据库中
	private void insertQuestionAnswer(List<Question> questionList,Long examId,Long examPaperId,Long studentId){
		for(Question singleQuestion:questionList) {
			ExamPaperAnswer examPaperAnswer=new ExamPaperAnswer();
			examPaperAnswer.setExamPaperId(examPaperId);
			examPaperAnswer.setExamId(examId);
			examPaperAnswer.setQuestionId(singleQuestion.getId());
			examPaperAnswer.setStudentId(studentId);
			examPaperAnswer.setIsCorrect(0);
			examPaperAnswerServiceImpl.add(examPaperAnswer);
		}
	}
	//根据传入的试题类型获取单选题、多选题或者判题
	private List<ExamPaperAnswer> getNeedQuestionList(List<ExamPaperAnswer> examPaperAnswers,int questionType){
		List<ExamPaperAnswer> needQuestionList=new ArrayList<ExamPaperAnswer>();
		for(ExamPaperAnswer answer:examPaperAnswers) {
			if(answer.getQuestion().getQuestionType()==questionType) {
				needQuestionList.add(answer);
			}
		}
		return needQuestionList;
	}
}