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

//ѧ��רҵ��̨������

@Controller
@RequestMapping("/admin/subject")
public class SubjectController {

	@Autowired
	private ISubjectService subjectServiceImpl;

	// ѧ���б���ʾҳ
	@GetMapping("/list")
	public ModelAndView list(ModelAndView model) {
		model.setViewName("subject/list");
		return model;
	}

	//��ȡ�˵��б�
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
	
	//���ѧ����Ϣ
	@PostMapping("/add")
	@ResponseBody
	public Map<String, String> add(Subject subject){
		Map<String, String> resCode=new HashMap<String, String>();
		if(subject==null) {
			resCode.put("type", "error");
			resCode.put("msg", "�������������");
			return resCode;
		}
		if(StringUtils.isEmpty(subject.getName())) {
			resCode.put("type", "error");
			resCode.put("msg", "����дѧ������");
			return resCode;
		}
		if(subjectServiceImpl.add(subject)<=0) {
			resCode.put("type", "error");
			resCode.put("msg", "���ʧ�ܣ�����ϵ����Ա");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg", "���ѧ����Ϣ�ɹ�");
		return resCode;		
	}
	 
	//�޸�ѧ����Ϣ
	@PostMapping("/edit")
	@ResponseBody
	public Map<String, String> edit(Subject subject){
		Map<String, String> resCode=new HashMap<String, String>();
		if(subject==null) {
			resCode.put("type", "error");
			resCode.put("msg", "�������������");
			return resCode;
		}
		if(subject.getId()==null) {
			resCode.put("type", "error");
			resCode.put("msg", "��ѡ����Ҫ�޸ĵ�����");
			return resCode;
		}
		if(StringUtils.isEmpty(subject.getName())) {
			resCode.put("type", "error");
			resCode.put("msg", "����дѧ������");
			return resCode;
		}
		if(subjectServiceImpl.edit(subject)<=0) {
			resCode.put("type", "error");
			resCode.put("msg", "�޸�ѧ����Ϣʧ�ܣ�����ϵ����Ա");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg", "�޸�ѧ����Ϣ�ɹ�");
		return resCode;		
	}
	
	//ɾ��ѧ����Ϣ
	@PostMapping("/delete")
	@ResponseBody
	public Map<String, String> delete(@RequestParam(name="id",required = true) Long id){
		Map<String, String> resCode=new HashMap<String, String>();
		if(id==null) {
			resCode.put("type", "error");
			resCode.put("msg", "��ѡ����Ҫɾ��������");
			return resCode;
		}
		try {
			if(subjectServiceImpl.delete(id)<=0) {
				resCode.put("type", "error");
				resCode.put("msg", "ɾ��ѧ����Ϣʧ�ܣ�����ϵ����Ա");
				return resCode;
			}
		}catch (Exception e) {
			resCode.put("type", "error");
			resCode.put("msg", "��ѧ���´��ڿ������޷�ɾ��!");
			return resCode;
		}
		
		resCode.put("type", "success");
		resCode.put("msg", "ɾ���ɹ�");
		return resCode;		
	}
	
	

}
