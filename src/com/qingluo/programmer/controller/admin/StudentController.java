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

//����������
@Controller
@RequestMapping("admin/student")
public class StudentController {
	@Autowired
	IStudentService studentServiceImpl;
	
	@Autowired
	private ISubjectService subjectServiceImpl;
	
	//�����б�ҳ
	@GetMapping("/list")
	public ModelAndView list(ModelAndView model) {
		Map<String, Object> queryMap=new HashMap<String, Object>();
		queryMap.put("offset", 0);
		queryMap.put("pageSize",99999);
		model.addObject("subjectList", subjectServiceImpl.findList(queryMap));
		model.setViewName("student/list");
		return model;
	}
	
	//���ؿ�����Ϣ�б�
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
			resCode.put("msg", "�������������Ϣ");
			return resCode;
		}
		if(student.getSubjectId()==null) {
			resCode.put("type", "error");
			resCode.put("msg", "����д����ѧ��רҵ��Ϣ");
			return resCode;
		}
		if(StringUtils.isEmpty(student.getName())) {
			resCode.put("type", "error");
			resCode.put("msg", "�������û���");
			return resCode;
		}
		if(StringUtils.isEmpty(student.getPassword())) {
			resCode.put("type", "error");
			resCode.put("msg", "����д��¼����");
			return resCode;
		}
		if(hasExist(student.getName(), 0L)) {
			resCode.put("type", "error");
			resCode.put("msg", "���û����Ѵ��ڣ�����������");
			return resCode;
		}
		
		if(studentServiceImpl.add(student)<=0) {
			resCode.put("type", "error");
			resCode.put("msg", "������Ϣ���ʧ�ܣ�����ϵ����Ա");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg", "������Ϣ��ӳɹ�");
		return resCode;
	}
	
	@PostMapping("/edit")
	@ResponseBody
	public Map<String, String> edit(Student student){
		Map<String, String> resCode=new HashMap<String, String>();
		if(student==null) {
			resCode.put("type", "error");
			resCode.put("msg", "�������������");
			return resCode;
		}
		if(student.getId()==null) {
			resCode.put("type", "error");
			resCode.put("msg", "��ѡ����Ҫ�޸ĵ�����");
			return resCode;
		}
		if(StringUtils.isEmpty(student.getName())) {
			resCode.put("type", "error");
			resCode.put("msg", "�������û���");
			return resCode;
		}
		if(StringUtils.isEmpty(student.getPassword())) {
			resCode.put("type", "error");
			resCode.put("msg", "����д��¼����");
			return resCode;
		}
		if(hasExist(student.getName(), student.getId())) {
			resCode.put("type", "error");
			resCode.put("msg", "���û����Ѵ��ڣ�����������!");
			return resCode;
		}
		if(studentServiceImpl.edit(student)<=0) {
			resCode.put("type", "error");
			resCode.put("msg", "�޸�ʧ�ܣ�����ϵ����Ա");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg", "�޸Ŀ�����Ϣ�ɹ�");
		return resCode;
	}
	
	
	@PostMapping("/delete")
	@ResponseBody
	public Map<String, String> delete(@RequestParam(name="id",required=true) Long id){
		Map<String, String> resCode=new HashMap<String, String>();
		if(id==null) {
			resCode.put("type", "error");
			resCode.put("msg", "��ѡ����Ҫɾ���Ŀ�����Ϣ");
			return resCode;
		}try {
			if(studentServiceImpl.delete(id)<=0) {
				resCode.put("type", "error");
				resCode.put("msg", "ɾ��ʧ�ܣ�����ϵ����Ա");
				return resCode;
			}
		} catch (Exception e) {
			resCode.put("type", "error");
			resCode.put("msg", "�ÿ����´��ڿ�����Ϣ������ɾ��!");
			return resCode;
		}
		
		resCode.put("type", "success");
		resCode.put("msg", "������Ϣɾ���ɹ�");
		return resCode;
	}
	
	//�ж��û����Ƿ���ڣ���������ڣ�����false,�����򷵻�true
	public boolean hasExist(String name,Long id) {
		Student student=studentServiceImpl.findNameByNameAndId(name);
		if(student==null) return false;;//�����û���û�в�ѯ��˵�����û���������
		if(student.getId()==id) return false;//��������û����õ����û�id�뵱ǰ�����һ�£���Ϊ����
		return true;
	}
}
