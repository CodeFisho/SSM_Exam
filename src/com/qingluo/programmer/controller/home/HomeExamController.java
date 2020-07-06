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
//ǰ̨�û����Կ�����
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
	
	//����ǰ����Ƿ����㿼���������������������
	@PostMapping("/start_exam")
	@ResponseBody
	public Map<String, String> startExam(Long examId,HttpServletRequest request){
		Map<String, String> resCode=new HashMap<String, String>();
		Map<String, Object> queryMap=new HashMap<String, Object>();
		Exam exam=examServiceImpl.findById(examId);
		if(exam==null) {
			resCode.put("tyepe", "error");
			resCode.put("msg", "������Ϣ������");
			return resCode;
		}
		if(exam.getEndTime().getTime()<new Date().getTime()) {
			resCode.put("tyepe", "error");
			resCode.put("msg", "�ÿ����ѽ���");
			return resCode;
		}
		Student currentStudent=(Student)request.getSession().getAttribute("student");
		if(exam.getSubjectId().longValue()!=currentStudent.getSubjectId().longValue()) {
			resCode.put("tyepe", "error");
			resCode.put("msg", "��ѡ����ص�ѧ�ƽ��п���");
			return resCode;
		}
		queryMap.put("examId", exam.getId());
		queryMap.put("studentId",currentStudent.getId());
		ExamPaper examPaper=examPaperServiceImpl.findOnePaper(queryMap);
		if(examPaper!=null) {
			//��ʾ�Ծ���
			if(examPaper.getStatus()==1) {
				resCode.put("tyepe", "error");
				resCode.put("msg", "���ѿ����ÿ���");
				return resCode;
			}
			//�Ծ��Ѿ����ɵ���δ�ύ���������¿���
			resCode.put("type", "success");
			resCode.put("msg", "�����������ͨ�������Բμӿ���");
			return resCode;	
		}
		//���Ͽ�����������������Ծ�
		//�ж��Ƿ����������Ծ��������������еĿ������������
		//�Ծ��е�ĳ�����͵�����Ҫ����ʵ�ʣ����ܱ����еĶࡣ
		//��ʱȥ��ѯ����д�����������Ƿ�����
		//��ȡ��ѡ������
		Map<String, Long> qMap = new HashMap<String, Long>();
		qMap.put("questionType", Long.valueOf(Question.QUESTION_TYPE_SINGLE));
		qMap.put("subjectId", exam.getSubjectId());
		int singleQuestionTotalNum = questionServiceImpl.getQuestionNumByType(qMap);
		if(exam.getSingleQuestionNum() > singleQuestionTotalNum){
			resCode.put("type", "error");
			resCode.put("msg", "��ѡ������������ⵥѡ�����������޸�!");
			return resCode;
		}
		//��ȡ��ѡ������
		qMap.put("questionType", Long.valueOf(Question.QUESTION_TYPE_MUILT));
		int muiltQuestionTotalNum = questionServiceImpl.getQuestionNumByType(qMap);
		if(exam.getMuiltQuestionNum() > muiltQuestionTotalNum){
			resCode.put("type", "error");
			resCode.put("msg", "��ѡ��������������ѡ�����������޸�!");
			return resCode;
		}
		//��ȡ�ж�������
		qMap.put("questionType", Long.valueOf(Question.QUESTION_TYPE_CHARGE));
		int chargeQuestionTotalNum = questionServiceImpl.getQuestionNumByType(qMap);
		if(exam.getChargeQuestionNum() > chargeQuestionTotalNum){
			resCode.put("type", "error");
			resCode.put("msg", "�ж���������������ж������������޸�!");
			return resCode;
		}
		//������������������
		ExamPaper newExamPaper=new ExamPaper();
		newExamPaper.setExamId(examId);
		newExamPaper.setStatus(0);
		newExamPaper.setStudentId(currentStudent.getId());
		newExamPaper.setTotalScore(exam.getTotalScore());
		newExamPaper.setUseTime(0);
		if(examPaperServiceImpl.add(newExamPaper)<=0) {
			resCode.put("type", "error");
			resCode.put("msg","�����Ծ�ʧ�ܣ�����ϵ����Ա");
			return resCode;
		}
		//�Ծ����ɳɹ�����Ҫ���������
		
		if(exam.getSingleQuestionNum()>0) {
			//��ȡ��ѡ���б�
			List<Question> singleQuestionList=questionServiceImpl.findQuestionByType(exam.getSubjectId(),Question.QUESTION_TYPE_SINGLE);
			//��ȡ������ɵĵ�ѡ���б�,���ɴ������Ⲣ���뵽���ݿ���
			List<Question> selectedSingleQuestions=getRandomList(singleQuestionList, exam.getSingleQuestionNum());
			insertQuestionAnswer(selectedSingleQuestions, examId, newExamPaper.getId(), currentStudent.getId());
		}
		if(exam.getMuiltQuestionNum()>0) {
			//��ȡ��ѡ���б�
			List<Question> muiltQuestionList=questionServiceImpl.findQuestionByType(exam.getSubjectId(),Question.QUESTION_TYPE_MUILT);
			//��ȡ������ɵĶ�ѡ���б�
			List<Question> selectedMuiltQuestions=getRandomList(muiltQuestionList, exam.getMuiltQuestionNum());
			insertQuestionAnswer(selectedMuiltQuestions, examId, newExamPaper.getId(), currentStudent.getId());
		}
		if(exam.getChargeQuestionNum()>0) {
			//��ȡ�ж����б�
			List<Question> chargeQuestionList=questionServiceImpl.findQuestionByType(exam.getSubjectId(),Question.QUESTION_TYPE_CHARGE);
			//��ȡ������ɵ��ж����б�
			List<Question> selectedChargeQuestions=getRandomList(chargeQuestionList, exam.getChargeQuestionNum());
			insertQuestionAnswer(selectedChargeQuestions, examId, newExamPaper.getId(), currentStudent.getId());
		}
		exam.setPaperNum(exam.getPaperNum()+1);
		examServiceImpl.updateExam(exam);
		resCode.put("type", "success");
		resCode.put("msg", "�Ծ����ɳɹ�!");
		return resCode;
	}
	
	
	
	//��ʼ����ҳ�棬�Ӻ�˷��ظ�ǰ�����ݲ���ǰ����ʾ
	@GetMapping("/examing")
	public ModelAndView index(ModelAndView model,
			@RequestParam(name="examId") Long examId,
			HttpServletRequest request
			) {	
		Map<String, Object> queryMap=new HashMap<String, Object>();		
		Student currentStudent=(Student)request.getSession().getAttribute("student");
		Exam currentExam=examServiceImpl.findById(examId);
		//�ж��Ծ��Ƿ����
		if(currentExam==null) {
			model.setViewName("/home/exam/error");
			model.addObject("msg", "��ǰ���Բ�����");
			return model;
		}
		//�ж��Ծ��Ƿ��ڿɿ�ʱ����
		if(currentExam.getEndTime().getTime()<new Date().getTime()) {
			model.setViewName("/home/exam/error");
			model.addObject("msg", "��ǰ�����ѹ���");
			return model;
		}
		//�жϵ�ǰѧ����ѧ��רҵ�Ƿ��������Ŀһ�� 
		if(currentExam.getSubjectId().longValue()!=currentStudent.getSubjectId()) {
			model.setViewName("/home/exam/error");
			model.addObject("msg", "����ѧ���뿼�Կ�Ŀ��һ�£���ѡ��רҵ�Ŀ��Խ��п���");
			return model;
		}
		queryMap.put("examId", currentExam.getId());
		queryMap.put("studentId", currentStudent.getId());
		//����ѧ��id�Ϳ���id��ȡ����ѧ�����Ծ�
		ExamPaper examPaper=examPaperServiceImpl.findOnePaper(queryMap);
		if(examPaper==null) {
			model.setViewName("/home/exam/error");
			model.addObject("msg", "�Ծ����ɴ�������ϵ����Ա");
			return model;
		}
		if(examPaper.getStatus()==1) {
			model.setViewName("/home/exam/error");
			model.addObject("msg", "������ɸÿ���");
			return model;
		}
		//�����ʼ���Ե�ʱ�������session�У�˵�����ǵ�һ�ν����ҳ�棻���Ϊ�գ�˵���ǵ�һ�Σ���ʱ�����session
		Date now=new Date();
		Object startExamTimeObject=request.getSession().getAttribute("startExamTime");		
		if(startExamTimeObject==null) {
			request.getSession().setAttribute("startExamTime", now);
		}
		Date startExamTime=(Date)request.getSession().getAttribute("startExamTime");
		int passTiem=(int)(now.getTime()-startExamTime.getTime())/1000/60;
		//System.out.println(passTiem+"----"+currentExam.getAvaliableTime());
		if(passTiem>=currentExam.getAvaliableTime()) {
			
			//��ʾʱ��ľ�����δ���⴦��
			examPaper.setScore(0);
			examPaper.setStartExamTime(startExamTime);
			examPaper.setStatus(1);
			examPaper.setUseTime(passTiem);
			examPaperServiceImpl.submitPaper(examPaper);
			model.setViewName("/home/exam/error");
			model.addObject("msg", "���Գ�ʱ����ȱ������");
			return model;
		}
		Integer hour=(currentExam.getAvaliableTime()-passTiem)/60;
		Integer minute=(currentExam.getAvaliableTime()-passTiem)%60;
		Integer second=(currentExam.getAvaliableTime()*60-(int)(now.getTime()-startExamTime.getTime())/1000)%60;
		//��ȡ��Ŀ�����ļ���
		queryMap.put("examPaperId", examPaper.getId());
		List<ExamPaperAnswer> answerList=examPaperAnswerServiceImpl.findByStudent(queryMap);
		//ȡ�õ�ѡ���б�
		List<ExamPaperAnswer> singleQuestionList=getNeedQuestionList(answerList, Question.QUESTION_TYPE_SINGLE);
		//ȡ�ö�ѡ���б�
		List<ExamPaperAnswer> muiltQuestionList=getNeedQuestionList(answerList, Question.QUESTION_TYPE_MUILT);
		//ȡ���ж����б�
		List<ExamPaperAnswer> chargeQuestionList=getNeedQuestionList(answerList, Question.QUESTION_TYPE_CHARGE);
		
		//��model�д����������͵��б�������ǰ�˱���
		model.addObject("singleQuestionList", singleQuestionList);
		model.addObject("muiltQuestionList", muiltQuestionList);
		model.addObject("chargeQuestionList", chargeQuestionList);
		//��ǰ����ʾ����ʱ��
		model.addObject("hour", hour);
		model.addObject("minute",minute);
		model.addObject("second",second);
		
		model.addObject("exam", currentExam);
		model.addObject("examPaper", examPaper);
		//��ÿ����Ŀ�����ͺͷ�ֵ����model����ǰ�˵���
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
	//�����������ʵ��
		private List<Question> getRandomList(List<Question> questionList,int num){
			if(questionList.size()<=num) return questionList;
			Map<Integer, String> selectedMap=new HashMap<Integer, String>();
			List<Question> selectedList=new ArrayList<Question>();
			//�������б��������ȡ��Ŀ������List�в�����
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
		
	//�ύ��Ŀ��	
	@PostMapping("/submit_answer")
	@ResponseBody
	private Map<String, String> submitAnswer(ExamPaperAnswer examPaperAnswer,HttpServletRequest request){		
		Map<String, String> resCode=new HashMap<String, String>();
		Map<String, Object> queryMap=new HashMap<String, Object>();
		if(examPaperAnswer==null) {
			resCode.put("type", "error");
			resCode.put("msg", "����ȷ����");
			return resCode;
		}
		Exam currentExam=examServiceImpl.findById(examPaperAnswer.getExamId());
		if(currentExam==null) {
			resCode.put("type", "error");
			resCode.put("msg", "������Ϣ������");
			return resCode;
		}
		Student currentStudent=(Student)request.getSession().getAttribute("student");
		if(currentStudent==null) {
			resCode.put("type", "error");
			resCode.put("msg", "����ȷ����");
			return resCode;
		}
		queryMap.put("examId", currentExam.getId());
		queryMap.put("studentId",currentStudent.getId());
		ExamPaper examPaper=examPaperServiceImpl.findOnePaper(queryMap);
		if(examPaper==null) {
			resCode.put("type", "error");
			resCode.put("msg", "�Ծ�����");
			return resCode;	
		}
		if(examPaper.getId().longValue()!=examPaperAnswer.getExamPaperId().longValue()) {
			resCode.put("type", "error");
			resCode.put("msg", "�Ծ����");
			return resCode;	
		}
		
		//����󣬽��ύ�Ĵ𰸱��浽���ݿ���
		Question currentQuestion=questionServiceImpl.findById(examPaperAnswer.getQuestionId());
		if(currentQuestion==null) {
			resCode.put("type", "error");
			resCode.put("msg", "���ⲻ����");
			return resCode;	
		}
		examPaperAnswer.setStudentId(currentStudent.getId());
		if(currentQuestion.getAnswer().equals(examPaperAnswer.getAnswer())) {
			examPaperAnswer.setIsCorrect(1);
		}
		if(examPaperAnswerServiceImpl.submitAnswer(examPaperAnswer)<=0) {
			resCode.put("type", "error");
			resCode.put("msg", "�����������ϵ����Ա");
			return resCode;	
		}
		resCode.put("type", "success");
		return resCode;
	}	
	
	//�������
	@PostMapping("/submit_exampaper")
	@ResponseBody
	private Map<String, String> submitExamPaper(@RequestParam(name="examId")Long examId, 
			@RequestParam(name="examPaperId")Long examPaperId,
			HttpServletRequest request){
		Map<String, String> resCode=new HashMap<String, String>();
		Exam currentExam=examServiceImpl.findById(examId);
		if(currentExam==null) {
			resCode.put("type", "error");
			resCode.put("msg", "���Բ�����");
			return resCode;
		}
		if(currentExam.getEndTime().getTime()<new Date().getTime()) {
			resCode.put("type", "error");
			resCode.put("msg", "�����ѽ���");
			return resCode;
		}
		Student currentStudent=(Student)request.getSession().getAttribute("student");
		if(currentExam.getSubjectId().longValue()!=currentStudent.getSubjectId().longValue()) {
			resCode.put("type", "error");
			resCode.put("msg", "ѧ�Ʋ�ͬ���޷�������ز���");
			return resCode;
		}
		Map<String, Object>queryMap=new HashMap<String, Object>();
		queryMap.put("examId", examId);
		queryMap.put("studentId", currentStudent.getId());
		ExamPaper currentExamPaper=examPaperServiceImpl.findOnePaper(queryMap);
		if(currentExamPaper==null) {
			resCode.put("type", "error");
			resCode.put("msg", "�Ծ�����");
			return resCode;
		}
		if(currentExamPaper.getId().longValue()!=examPaperId.longValue()) {
			resCode.put("type", "error");
			resCode.put("msg", "�Ծ����");
			return resCode;
		}
		if(currentExamPaper.getStatus()==1) {
			resCode.put("type", "error");
			resCode.put("msg", "�����ظ��ύ");
			return resCode;
		}
		//���㿼�Ե÷�
		//��ȡ��ǰ�Ծ�Ĵ�����Ϣ�б�
		queryMap.put("examPaperId",currentExamPaper.getId());
		int score=0;//�Ծ��ܵ÷�
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
			resCode.put("msg", "�Ծ��ύʧ�ܣ�����ϵ����Ա");
			return resCode;
		}
		currentExam.setExamedNum(currentExam.getExamedNum()+1);
		if(currentExamPaper.getScore()>=currentExam.getPassScore()) {
			currentExam.setPassNum(currentExam.getPassNum()+1);
		}
		request.getSession().setAttribute("startExamTime", null);
		//�Ծ��ύ����¿��Ե������Ϣ
		examServiceImpl.updateExam(currentExam);
		resCode.put("type","success");
		resCode.put("msg", "�ύ�ɹ�");
		return resCode;
	}
	
	
	//�����ɵ�������뵽���ݿ���
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
	//���ݴ�����������ͻ�ȡ��ѡ�⡢��ѡ���������
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