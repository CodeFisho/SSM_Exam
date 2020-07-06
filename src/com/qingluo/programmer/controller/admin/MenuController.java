package com.qingluo.programmer.controller.admin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.qingluo.programmer.entity.admin.Menu;
import com.qingluo.programmer.page.admin.Page;
import com.qingluo.programmer.service.admin.IMenuService;



//�˵����������
@Controller
@RequestMapping("/admin/menu")
public class MenuController {
	
	@Autowired
	private IMenuService menuServiceImpl;
	
	//�˵������б�ҳ
	@RequestMapping(value="/list",method = RequestMethod.GET)
	public ModelAndView list(ModelAndView model) {
		model.addObject("topList",menuServiceImpl.findTopList());
		model.setViewName("menu/list");
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
		List<Menu> menuList=menuServiceImpl.findList(queryMap);
		resCode.put("rows",menuList);
		resCode.put("total",menuServiceImpl.getTotal(queryMap));
		return resCode;
	}
	
	//��ȡָ��Ŀ¼�µ�icon����
	@RequestMapping(value="/get_icons",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getIconList(HttpServletRequest request){
		Map<String, Object> resCode=new HashMap<String, Object>();
		String realPath=request.getServletContext().getRealPath("/");
		File file=new File(realPath+"\\resources\\admin\\easyui\\css\\icons");
		List<String> icons=new ArrayList<String>();
		if(!file.exists()) {
			resCode.put("type","error");
			resCode.put("msg", "�ļ�Ŀ¼������");
			return resCode;
		}
		File[] listFiles=file.listFiles();
		for(File f:listFiles) {
			if(f!=null && f.getName().contains("png")) {
				icons.add("icon-" + f.getName().substring(0, f.getName().indexOf(".")).replace("_", "-"));
			}
		}
		resCode.put("type","success");
		resCode.put("content",icons);
		return resCode;
	}
	
	
	//�˵����
	@RequestMapping(value="/add",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> add(Menu menu){
		Map<String, String> resCode=new HashMap<String, String>();
		if(menu==null) {
			resCode.put("type","error");
			resCode.put("msg","����д��ȷ�Ĳ˵���Ϣ");
			return resCode;
		}
		if(StringUtils.isEmpty(menu.getName())) {
			resCode.put("type","error");
			resCode.put("msg","����д�˵�����");
			return resCode;
		}
		if(StringUtils.isEmpty(menu.getIcon())) {
			resCode.put("type","error");
			resCode.put("msg","��ѡ��˵�ͼ��");
			return resCode;
		}
		if(menu.getParentId()==null) {
			menu.setParentId(0l);
		}
		if(menuServiceImpl.add(menu)<=0) {
			resCode.put("type","error");
			resCode.put("msg","���ʧ�ܣ�����ϵ����Ա");
			return resCode;
		}
		resCode.put("type","success");
		resCode.put("msg","�˵���ӳɹ�!");
		return resCode;
		
		
	}
	

	//�˵��޸�
	@RequestMapping(value="/edit",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> edit(Menu menu){
		Map<String, String> resCode=new HashMap<String, String>();
		if(menu==null) {
			resCode.put("type","error");
			resCode.put("msg","��ѡ����ȷ�Ĳ˵���Ϣ");
			return resCode;
		}
		if(StringUtils.isEmpty(menu.getName())) {
			resCode.put("type","error");
			resCode.put("msg","����д�˵�����");
			return resCode;
		}
		if(StringUtils.isEmpty(menu.getIcon())) {
			resCode.put("type","error");
			resCode.put("msg","��ѡ��˵�ͼ��");
			return resCode;
		}
		if(menu.getParentId()==null) {
			menu.setParentId(0l);
		}
		if(menuServiceImpl.edit(menu)<=0) {
			resCode.put("type","error");
			resCode.put("msg","�޸�ʧ�ܣ�����ϵ����Ա");
			return resCode;
		}
		resCode.put("type","success");
		resCode.put("msg","�˵��޸ĳɹ�!");
		return resCode;
	}
	//ɾ���˵���Ϣ
	@RequestMapping(value="/delete",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> delete(@RequestParam(name="id",required = true) Long id){
		Map<String, String> resCode=new HashMap<String, String>();
		if(id==null) {
			resCode.put("type", "error");
			resCode.put("msg","��ѡ����Ҫɾ���Ĳ˵���Ϣ");
			return resCode;
		}
		List<Menu> findChilerenList=menuServiceImpl.findChildrenList(id);
		if(findChilerenList!=null && findChilerenList.size()>0) {
			//����ò˵��´����Ӳ˵���������ֱ��ɾ�����˵�
			resCode.put("type", "error");
			resCode.put("msg","�ò˵��´����Ӳ˵�������ɾ��");
			return resCode;
		}
		if(menuServiceImpl.delete(id)<=0) {
			resCode.put("type", "error");
			resCode.put("msg","ɾ��ʧ������ϵ����Ա");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg","ɾ���ɹ�");
		return resCode;
	}

}
