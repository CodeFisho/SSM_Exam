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
	
	//����ҳ��
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

	//�����б�
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
	//��ӿ�����Ϣ
	@PostMapping("/add")
	@ResponseBody
	public Map<String, String> add(Exam exam){
		Map<String, String> resCode=new HashMap<String, String>();
		if(exam==null) {
			resCode.put("type", "error");
			resCode.put("msg","���������������");
			return resCode;
		}
		if(StringUtils.isEmpty(exam.getName())) {
			resCode.put("type", "error");
			resCode.put("msg","�����뿼������");
			return resCode;
		}
		if(exam.getSubjectId()==null) {
			resCode.put("type", "error");
			resCode.put("msg","��ѡ����������Ŀ");
			return resCode;
		}
		if(exam.getStartTime()==null) {
			resCode.put("type", "error");
			resCode.put("msg","����д���Կ�ʼʱ��");
			return resCode;
		}
		if(exam.getEndTime()==null) {
			resCode.put("type", "error");
			resCode.put("msg","����д���Խ���ʱ��");
			return resCode;
		}
		if(exam.getPassScore()==0) {
			resCode.put("type", "error");
			resCode.put("msg","����д���Լ������");
			return resCode;
		}
		if(exam.getSingleQuestionNum()==0 && exam.getMuiltQuestionNum()==0 && exam.getChargeQuestionNum()==0) {
			resCode.put("type", "error");
			resCode.put("msg","��ѡ�⡢��ѡ�⡢�ж���������һ�����͵���Ŀ��");
			return resCode;
		}
		//��ʱȥ��ѯ����д�����������Ƿ�����
		//��ȡ��ѡ������
		Map<String, Long> queryMap = new HashMap<String, Long>();
		queryMap.put("questionType", Long.valueOf(Question.QUESTION_TYPE_SINGLE));
		queryMap.put("subjectId", exam.getSubjectId());
		int singleQuestionTotalNum = questionServiceImpl.getQuestionNumByType(queryMap);
		if(exam.getSingleQuestionNum() > singleQuestionTotalNum){
			resCode.put("type", "error");
			resCode.put("msg", "��ѡ������������ⵥѡ�����������޸�!");
			return resCode;
		}
		//��ȡ��ѡ������
		queryMap.put("questionType", Long.valueOf(Question.QUESTION_TYPE_MUILT));
		int muiltQuestionTotalNum = questionServiceImpl.getQuestionNumByType(queryMap);
		if(exam.getMuiltQuestionNum() > muiltQuestionTotalNum){
			resCode.put("type", "error");
			resCode.put("msg", "��ѡ��������������ѡ�����������޸�!");
			return resCode;
		}
		//��ȡ�ж�������
		queryMap.put("questionType", Long.valueOf(Question.QUESTION_TYPE_CHARGE));
		int chargeQuestionTotalNum = questionServiceImpl.getQuestionNumByType(queryMap);
		if(exam.getChargeQuestionNum() > chargeQuestionTotalNum){
			resCode.put("type", "error");
			resCode.put("msg", "�ж���������������ж������������޸�!");
			return resCode;
		}
		//������Ŀ����
		exam.setQuestionNum(exam.getSingleQuestionNum()+exam.getChargeQuestionNum()+exam.getMuiltQuestionNum());
		//�����ܷ�
		exam.setTotalScore(exam.getSingleQuestionNum()*Question.QUESTION_TYPE_SINGLE_SCORE
				+exam.getMuiltQuestionNum()*Question.QUESTION_TYPE_MUILT_SOCRE
				+exam.getChargeQuestionNum()*Question.QUESTION_TYPE_CHARGE_SCORE
			);
		if(examServiceImpl.add(exam)<=0) {
			resCode.put("type", "error");
			resCode.put("msg","���ʧ�ܣ�����ϵ����Ա!");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg","��ӳɹ�");
		return resCode;
	}
	//�༭������Ϣ
	@PostMapping("/edit")
	@ResponseBody
	public Map<String,String> edit(Exam exam){
		Map<String, String> resCode=new HashMap<String, String>();
		if(exam==null) {
			resCode.put("type", "error");
			resCode.put("msg","��ѡ����Ҫ�޸ĵĿ�����Ϣ");
			return resCode;
		}
		if(StringUtils.isEmpty(exam.getName())) {
			resCode.put("type", "error");
			resCode.put("msg","�����뿼������");
			return resCode;
		}
		if(exam.getSubjectId()==null) {
			resCode.put("type", "error");
			resCode.put("msg","��ѡ����������Ŀ");
			return resCode;
		}
		if(exam.getStartTime()==null) {
			resCode.put("type", "error");
			resCode.put("msg","����д���Կ�ʼʱ��");
			return resCode;
		}
		if(exam.getEndTime()==null) {
			resCode.put("type", "error");
			resCode.put("msg","����д���Խ���ʱ��");
			return resCode;
		}
		if(exam.getPassScore()==0) {
			resCode.put("type", "error");
			resCode.put("msg","����д���Լ������");
			return resCode;
		}
		if(exam.getSingleQuestionNum()==0 && exam.getMuiltQuestionNum()==0 && exam.getChargeQuestionNum()==0) {
			resCode.put("type", "error");
			resCode.put("msg","��ѡ�⡢��ѡ�⡢�ж���������һ�����͵���Ŀ��");
			return resCode;
		}
		//��ʱȥ��ѯ����д�����������Ƿ�����
		//��ȡ��ѡ������
		Map<String, Long> queryMap = new HashMap<String, Long>();
		queryMap.put("questionType", Long.valueOf(Question.QUESTION_TYPE_SINGLE));
		queryMap.put("subjectId", exam.getSubjectId());
		int singleQuestionTotalNum = questionServiceImpl.getQuestionNumByType(queryMap);
		if(exam.getSingleQuestionNum() > singleQuestionTotalNum){
			resCode.put("type", "error");
			resCode.put("msg", "��ѡ������������ⵥѡ�����������޸�!");
			return resCode;
		}
		//��ȡ��ѡ������
		queryMap.put("questionType", Long.valueOf(Question.QUESTION_TYPE_MUILT));
		int muiltQuestionTotalNum = questionServiceImpl.getQuestionNumByType(queryMap);
		if(exam.getMuiltQuestionNum() > muiltQuestionTotalNum){
			resCode.put("type", "error");
			resCode.put("msg", "��ѡ��������������ѡ�����������޸�!");
			return resCode;
		}
		//��ȡ�ж�������
		queryMap.put("questionType", Long.valueOf(Question.QUESTION_TYPE_CHARGE));
		int chargeQuestionTotalNum = questionServiceImpl.getQuestionNumByType(queryMap);
		if(exam.getChargeQuestionNum() > chargeQuestionTotalNum){
			resCode.put("type", "error");
			resCode.put("msg", "�ж���������������ж������������޸�!");
			return resCode;
		}
		/*
		 * Map<String, Long> queryMap=new HashMap<String, Long>();
		 * queryMap.put("subjectId", exam.getSubjectId()); queryMap.put("questionType",
		 * Long.valueOf(Question.QUESTION_TYPE_SINGLE)); //�Ծ��е�ĳ�����͵�����Ҫ����ʵ�ʣ����ܱ����еĶࡣ
		 * //��ȡ��ѡ������� int
		 * totalSingle=questionServiceImpl.getQuestionNumByType(queryMap); //��ȡ��ѡ�������
		 * queryMap.put("questionType", Long.valueOf(Question.QUESTION_TYPE_MUILT)); int
		 * totalMuilt=questionServiceImpl.getQuestionNumByType(queryMap); //��ȡ�ж��������
		 * queryMap.put("questionId", Long.valueOf(Question.QUESTION_TYPE_CHARGE)); int
		 * totalCharge=questionServiceImpl.getQuestionNumByType(queryMap);
		 * if(exam.getSingleQuestionNum()>totalSingle) { resCode.put("type", "error");
		 * resCode.put("msg","��ѡ��������"+exam.getSingleQuestionNum()+"��������е�����:"+
		 * totalSingle+"��������С�ڻ���ڴ˵�����!"); return resCode; }
		 * if(exam.getMuiltQuestionNum()>totalMuilt) { resCode.put("type", "error");
		 * resCode.put("msg","��ѡ��������"+exam.getMuiltQuestionNum()+"��������е�����:"+totalMuilt+
		 * "��������С�ڻ���ڴ˵�����!"); return resCode; }
		 * if(exam.getChargeQuestionNum()>totalCharge) { resCode.put("type", "error");
		 * resCode.put("msg","�ж���������"+exam.getChargeQuestionNum()+"��������е�����:"+
		 * totalCharge+"��������С�ڻ���ڴ˵�����!"); return resCode; }
		 */
		//������Ŀ����
		exam.setQuestionNum(exam.getSingleQuestionNum()+exam.getChargeQuestionNum()+exam.getMuiltQuestionNum());
		//�����ܷ�
		exam.setTotalScore(exam.getSingleQuestionNum()*Question.QUESTION_TYPE_SINGLE_SCORE
				+exam.getMuiltQuestionNum()*Question.QUESTION_TYPE_MUILT_SOCRE
				+exam.getChargeQuestionNum()*Question.QUESTION_TYPE_CHARGE_SCORE
			);
		if(examServiceImpl.edit(exam)<=0) {
			resCode.put("type", "error");
			resCode.put("msg","�༭ʧ�ܣ�����ϵ����Ա!");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg","�༭�ɹ�");
		return resCode;
	}
	//ɾ��������Ϣ
	@PostMapping ("/delete")
	@ResponseBody
	public Map<String, String> delete(@RequestParam(name="id",required = true)Long id){
		Map<String, String> resCode=new HashMap<String, String>();
		if(id==null) {
			resCode.put("type", "error");
			resCode.put("msg","��ѡ����Ҫɾ��������");
			return resCode;
		}
		try {
			if(examServiceImpl.delete(id)<=0) {
				resCode.put("type", "error");
				resCode.put("msg","ɾ��ʧ�ܣ�����ϵ����Ա");
				return resCode;
			}
		}catch (Exception e) {
			resCode.put("type", "error");
			resCode.put("msg","ɾ��ʧ�ܣ��ÿ����´����Ծ���Ϣ���Լ�¼");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg","ɾ���ɹ�");
		return resCode;
	}
}
