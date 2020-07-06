package com.qingluo.programmer.controller.admin;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.qingluo.programmer.entity.admin.Log;
import com.qingluo.programmer.page.admin.Page;
import com.qingluo.programmer.service.admin.ILogService;

@Controller
@RequestMapping("/admin/log")
public class LogController {

	@Autowired
	private ILogService logServiceImpl;
	
	
	//��ת����־�б�ҳ��
	@RequestMapping(value="/list",method = RequestMethod.GET) 
	public ModelAndView list(ModelAndView model) {		
		model.setViewName("log/list");
		return model;
	}
	
	
	//�����û��б���Ϣ
	@RequestMapping(value="/list",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> list(Page page,
			@RequestParam(name="content",required = false,defaultValue = "") String content
			){
		Map<String, Object> resCode=new HashMap<String, Object>();
		Map<String, Object> queryMap=new HashMap<String, Object>();
		queryMap.put("content", content);
		queryMap.put("offset", page.getOffset());
		queryMap.put("pageSize",page.getRows());
		resCode.put("rows", logServiceImpl.findList(queryMap));
		resCode.put("total",logServiceImpl.getTotal(queryMap));
		return resCode;
	}
	
	
	//��־��Ӳ���
	@RequestMapping(value="/add",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> addLog(Log log){
		Map<String, String> resCode=new HashMap<String, String>();
		if(log==null) {
			resCode.put("type", "error");
			resCode.put("msg","�������������");
			return resCode;
		}
		if(StringUtils.isEmpty(log.getContent())) {
			resCode.put("type", "error");
			resCode.put("msg","����д��־����");
			return resCode;
		}
		if(logServiceImpl.add(log)<=0) {
			resCode.put("type", "error");
			resCode.put("msg","�����־��Ϣʧ�ܣ�����ϵ����Ա");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg","��־��ӳɹ�");
		return resCode;
	}
	
	@RequestMapping(value="/delete",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> delete(@RequestParam(name="ids",required = false,defaultValue = "") String ids){
		Map<String, String> resCode=new HashMap<String, String>();
		if(StringUtils.isEmpty(ids)) {
			resCode.put("type", "error");
			resCode.put("msg","��ѡ����Ҫɾ��������");
			return resCode;
		}
		if(ids.contains(",")) {
			ids=ids.substring(0, ids.length()-1);
		}
		if(logServiceImpl.delete(ids)<=0) {
			resCode.put("type", "error");
			resCode.put("msg","ɾ����־��Ϣʧ�ܣ�����ϵ����Ա");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg","ɾ����־��Ϣ�ɹ���");
		return resCode;
	}
}
