package com.qingluo.programmer.controller.home;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

import com.qingluo.programmer.entity.admin.Exam;
import com.qingluo.programmer.entity.admin.ExamPaper;
import com.qingluo.programmer.entity.admin.ExamPaperAnswer;
import com.qingluo.programmer.entity.admin.Question;
import com.qingluo.programmer.entity.admin.Student;
import com.qingluo.programmer.service.admin.IExamPaperAnswerService;
import com.qingluo.programmer.service.admin.IExamPaperService;
import com.qingluo.programmer.service.admin.IExamService;
import com.qingluo.programmer.service.admin.IStudentService;
import com.qingluo.programmer.service.admin.ISubjectService;
import com.qingluo.programmer.util.DateFormatUtil;
import com.qingluo.programmer.util.QuestionUtils;

@Controller
@RequestMapping("/home/user")
public class HomeStudentController {

	@Autowired
	private IStudentService studentServiceImpl; 
	
	@Autowired
	private ISubjectService subjectServiceImpl;
	
	@Autowired
	private IExamService examServiceImpl;
	
	@Autowired
	private IExamPaperService examPaperServiceImpl;
	
	@Autowired
	private IExamPaperAnswerService examPaperAnswerServiceImpl;
	
	private final int  PAGESIZE=10;
	//ǰ����ҳ��
	@GetMapping("/index")
	public ModelAndView index(ModelAndView model) {		
		model.setViewName("/home/user/index");
		return model;
	}
	//ǰ�˻�ӭҳ��
	@GetMapping("/welcome")
	public ModelAndView welcome(ModelAndView model,HttpServletRequest request) {
		Student currentStudent=(Student)request.getSession().getAttribute("student");
		Map<String, Object> queryMap=new HashMap<String, Object>();
		queryMap.put("subjectId",currentStudent.getSubjectId());
		queryMap.put("startTime",DateFormatUtil.getDate("yyyy-MM-dd hh:mm:ss", new  Date()));
		queryMap.put("endTime",DateFormatUtil.getDate("yyyy-MM-dd hh:mm:ss", new Date()));
		queryMap.put("offset",0);
		queryMap.put("pageSize",10);
		queryMap.put("studentId", currentStudent.getId());
		model.addObject("examList",examServiceImpl.findListByUser(queryMap));
		
		model.addObject("subject", subjectServiceImpl.findById(currentStudent.getSubjectId()));
		model.addObject("historyList", examPaperServiceImpl.findHistory(queryMap));
		
		model.setViewName("/home/user/welcome");
		return model;
	}
	//��ȡ��ǰ��¼��������Ϣ
	@PostMapping("/get_current")
	@ResponseBody
	public Map<String, String> getCurrent(HttpServletRequest request){
		Map<String,String> resCode=new HashMap<String, String>();
		Student currentStudent=(Student)request.getSession().getAttribute("student");
		if(currentStudent==null) {
			resCode.put("type","error");
			resCode.put("msg","��¼��ϢʧЧ�������µ�¼");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("username", currentStudent.getName());
		return resCode;
	}
	//��ת���˻���Ϣҳ��
	@GetMapping("/profile")
	public ModelAndView profile(ModelAndView model,HttpServletRequest request) {
		Student currentStudent=(Student)request.getSession().getAttribute("student");
		model.addObject("student", currentStudent);
		model.addObject("subject",subjectServiceImpl.findById(currentStudent.getSubjectId()));
		model.setViewName("/home/user/profile");
		return model;
	}
	
	@GetMapping("/password")
	public ModelAndView turnToPassword(ModelAndView model,HttpServletRequest request) {
		Student currentStudent=(Student)request.getSession().getAttribute("student");
		model.addObject("student", currentStudent);
		
		model.setViewName("/home/user/password");
		return model;
	}
	
	//���¿����Ĳ�����Ϣ
	@PostMapping("/updateInfo")
	@ResponseBody
	public Map<String, String> updateInfo(Student student,HttpServletRequest request){
		Map<String, String> resCode=new HashMap<String, String>();
		Student onlineStudent=(Student)request.getSession().getAttribute("student");
		onlineStudent.setTel(student.getTel());
		onlineStudent.setTrueName(student.getTrueName());
		if(studentServiceImpl.edit(onlineStudent)<=0) {
			resCode.put("type", "error");
			resCode.put("msg", "�޸�ʧ��");
			return resCode;
		}
		request.getSession().setAttribute("student", onlineStudent);
		resCode.put("type", "success");
		resCode.put("msg", "�޸ĳɹ�");
		return resCode;
	}
	
	@PostMapping("/updatePassword")
	@ResponseBody
	public Map<String, String> updatePassword(
			@RequestParam(name="oldPassword") String oldPass,
			@RequestParam(name="newPassword") String newPass,
			@RequestParam(name="passAgain") String passAgain,
			HttpServletRequest request){
		Map<String, String> resCode=new HashMap<String, String>();
		Student onlineStudent=(Student)request.getSession().getAttribute("student");
		if(StringUtils.isEmpty(oldPass)) {
			resCode.put("type", "error");
			resCode.put("msg", "�����������");
			return resCode;
		}
		if(StringUtils.isEmpty(newPass)) {
			resCode.put("type", "error");
			resCode.put("msg", "�����벻��Ϊ��");
			return resCode;
		}
		if(StringUtils.isEmpty(passAgain)) {
			resCode.put("type", "error");
			resCode.put("msg", "ȷ�����벻��Ϊ��");
			return resCode;
		}
		//�ж�����ľ������Ƿ���ȷ
		if(!oldPass.equals(onlineStudent.getPassword())) {
			resCode.put("type", "error");
			resCode.put("msg", "�������������������");
			return resCode;
		}		
		//�ж�����������������Ƿ�һ��
		if(!newPass.equals(passAgain)) {
			resCode.put("type", "error");
			resCode.put("msg", "��������������벻һ�£���������д");
			return resCode;
		}//��֤������޸�����
		onlineStudent.setPassword(newPass);
		if(studentServiceImpl.edit(onlineStudent)<=0) {
			resCode.put("type", "error");
			resCode.put("msg", "�޸�ʧ��");
			return resCode;
		}
		request.getSession().setAttribute("student", onlineStudent);
		resCode.put("type", "success");
		resCode.put("msg", "�޸ĳɹ�");
		return resCode;
	}
	
	@GetMapping("/exam_list")
	public ModelAndView examList(ModelAndView model,
			@RequestParam(name="name",defaultValue = "") String name,
			@RequestParam(name="page",defaultValue = "1") Integer page,
			HttpServletRequest request) {
		Student currentStudent=(Student)request.getSession().getAttribute("student");
		Map<String, Object> queryMap=new HashMap<String, Object>();
		queryMap.put("subjectId", currentStudent.getSubjectId());
		queryMap.put("name",name);
		queryMap.put("offset",getOffset(page, PAGESIZE));
		queryMap.put("pageSize",PAGESIZE);
		model.addObject("examList",examServiceImpl.findListByUser(queryMap));
		model.addObject("name", name);
		model.addObject("subject", subjectServiceImpl.findById(currentStudent.getSubjectId()));
		if(page<1) {
			page=1;
		}
		model.addObject("page", page);
		model.addObject("nowTime", System.currentTimeMillis());
		model.setViewName("/home/user/exam_list");
		return model;
	}
	
	@PostMapping("/exam_list")
	public ModelAndView examListWithPost(ModelAndView model,
			@RequestParam(name="name",defaultValue = "") String name,
			@RequestParam(name="page",defaultValue = "1") Integer page,
			HttpServletRequest request) {
		Student currentStudent=(Student)request.getSession().getAttribute("student");
		Map<String, Object> queryMap=new HashMap<String, Object>();
		queryMap.put("subjectId", currentStudent.getSubjectId());
		queryMap.put("name",name);
		queryMap.put("offset",getOffset(page, PAGESIZE));
		queryMap.put("pageSize",PAGESIZE);
		model.addObject("examList",examServiceImpl.findListByUser(queryMap));
		model.addObject("name", name);
		model.addObject("subject", subjectServiceImpl.findById(currentStudent.getSubjectId()));
		if(page<1) {
			page=1;
		}
		model.addObject("page", page);
		model.setViewName("/home/user/exam_list");
		return model;
	}
	
	@GetMapping("/history_list")
	public ModelAndView history(ModelAndView model,
			@RequestParam(name="name",defaultValue = "") String name,
			@RequestParam(name="page",defaultValue = "1") Integer page,
			HttpServletRequest request) {
		Student currentStudent=(Student)request.getSession().getAttribute("student");
		Map<String, Object> queryMap=new HashMap<String, Object>();
		queryMap.put("studentId", currentStudent.getId());
		queryMap.put("name",name);
		queryMap.put("offset",getOffset(page, PAGESIZE));
		queryMap.put("pageSize",PAGESIZE);
		model.addObject("historyList",examPaperServiceImpl.findHistory(queryMap));
		model.addObject("name", name);
		model.addObject("subject", subjectServiceImpl.findById(currentStudent.getSubjectId()));
		if(page<1) {
			page=1;
		}
		model.addObject("page", page);
		model.setViewName("/home/user/history_list");
		return model;
	}
	@PostMapping("/history_list")
	public ModelAndView historyWithPost(ModelAndView model,
			@RequestParam(name="name",defaultValue = "") String name,
			@RequestParam(name="page",defaultValue = "1") Integer page,
			HttpServletRequest request) {
		Student currentStudent=(Student)request.getSession().getAttribute("student");
		Map<String, Object> queryMap=new HashMap<String, Object>();
		queryMap.put("studentId", currentStudent.getId());
		queryMap.put("name",name);
		queryMap.put("offset",getOffset(page, PAGESIZE));
		queryMap.put("pageSize",PAGESIZE);
		model.addObject("historyList",examPaperServiceImpl.findHistory(queryMap));
		model.addObject("name", name);
		model.addObject("subject", subjectServiceImpl.findById(currentStudent.getSubjectId()));
		if(page<1) {
			page=1;
		}
		model.addObject("page", page);
		model.setViewName("/home/user/history_list");
		return model;
	}
	//�ǳ�
	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		request.getSession().setAttribute("student", null);
		return "redirect:login";
	}
	
	
	//�ع��Ծ����
	@GetMapping("/review_exam")
	public ModelAndView index(ModelAndView model,
			@RequestParam(name="examPaperId") Long examPaperId,
			@RequestParam(name="examId") Long examId,
			HttpServletRequest request
			) {	
		Map<String, Object> queryMap=new HashMap<String, Object>();		
		Student currentStudent=(Student)request.getSession().getAttribute("student");
		Exam currentExam=examServiceImpl.findById(examId);
		//�жϿ����Ƿ����
		if(currentExam==null) {
			model.setViewName("/home/exam/error");
			model.addObject("msg", "��ǰ���Բ�����");
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
		if(examPaper.getStatus()==0) {
			model.setViewName("/home/exam/error");
			model.addObject("msg", "����δ�μӹ��ÿ���");
			return model;
		}		
		//��ȡ��Ŀ�����ļ���
		queryMap.put("examPaperId", examPaper.getId());
		List<ExamPaperAnswer> answerList=examPaperAnswerServiceImpl.findByStudent(queryMap);
		//ȡ�õ�ѡ���б�
		List<ExamPaperAnswer> singleQuestionList=QuestionUtils.getNeedQuestionList(answerList, Question.QUESTION_TYPE_SINGLE);
		//ȡ�ö�ѡ���б�
		List<ExamPaperAnswer> muiltQuestionList=QuestionUtils.getNeedQuestionList(answerList, Question.QUESTION_TYPE_MUILT);
		//ȡ���ж����б�
		List<ExamPaperAnswer> chargeQuestionList=QuestionUtils.getNeedQuestionList(answerList, Question.QUESTION_TYPE_CHARGE);
		
		//��model�д����������͵��б�������ǰ�˱���
		model.addObject("singleQuestionList", singleQuestionList);
		model.addObject("muiltQuestionList", muiltQuestionList);
		model.addObject("chargeQuestionList", chargeQuestionList);
		
		
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
		model.setViewName("/home/user/review_exam");
		return model;
	}
	
	
	//��ҳ��ѯ�α�λ��
	private int getOffset(int page,int pageSize) {
		if(page<1)page=1;
		return (page-1)*pageSize;
	}
}
