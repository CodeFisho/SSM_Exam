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
	
	
	//�Ծ����ҳ��
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
	//�Ծ��б�
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
	
	//�Ծ���Ϣ���
	@PostMapping("/add")
	@ResponseBody
	public Map<String, String> list(ExamPaper examPaper){
		Map<String, String> resCode=new HashMap<String, String>();
		if(examPaper==null) {
			resCode.put("type","error");
			resCode.put("msg", "����д��ȷ����Ϣ");
			return resCode;
		}
		if(examPaper.getExamId()==null) {
			resCode.put("type","error");
			resCode.put("msg", "��ѡ����������");
			return resCode;
		}
		if(examPaper.getStudentId()==null) {
			resCode.put("type","error");
			resCode.put("msg", "��ѡ���Ծ�����ѧ��");
			return resCode;
		}
		if(examPaperServiceImpl.add(examPaper)<=0) {
			resCode.put("type","error");
			resCode.put("msg", "����Ծ���Ϣʧ�ܣ�����ϵ����Ա");
			return resCode;
		}
		resCode.put("type","success");
		resCode.put("msg", "�Ծ���ӳɹ�");
		return resCode;
	}
	
	//�Ծ���Ϣ�༭
	@PostMapping("/edit")
	@ResponseBody
	public Map<String, String> edit(ExamPaper examPaper){
		Map<String, String> resCode=new HashMap<String, String>();
		if(examPaper==null) {
			resCode.put("type","error");
			resCode.put("msg", "����д��ȷ����Ϣ");
			return resCode;
		}
		if(examPaper.getExamId()==null) {
			resCode.put("type","error");
			resCode.put("msg", "��ѡ����������");
			return resCode;
		}
		if(examPaper.getStudentId()==null) {
			resCode.put("type","error");
			resCode.put("msg", "��ѡ���Ծ�����ѧ��");
			return resCode;
		}
		if(examPaperServiceImpl.edit(examPaper)<=0) {
			resCode.put("type","error");
			resCode.put("msg", "�༭�Ծ���Ϣʧ�ܣ�����ϵ����Ա");
			return resCode;
		}
		resCode.put("type","success");
		resCode.put("msg", "�Ծ�༭�ɹ�");
		return resCode;
	}
	
	//�Ծ���Ϣɾ��
	@PostMapping("/delete")
	@ResponseBody
	public Map<String, String> delete(Long id){
		Map<String, String> resCode=new HashMap<String, String>();
		if(id==null) {
			resCode.put("type", "error");
			resCode.put("msg", "��ѡ����Ҫɾ��������");
			return resCode;
		}
		try {
			if(examPaperServiceImpl.delete(id)<=0) {
				resCode.put("type", "error");
				resCode.put("msg", "ɾ��ʧ�ܣ�����ϵ����Ա");
				return resCode;
			}
		} catch (Exception e) {
			resCode.put("type", "error");
			resCode.put("msg", "ɾ��ʧ�ܣ����Ծ��´��ڴ�����Ϣ");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg", "ɾ���ɹ�");
		return resCode;
	}
}