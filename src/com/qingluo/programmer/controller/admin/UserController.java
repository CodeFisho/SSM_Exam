package com.qingluo.programmer.controller.admin;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.qingluo.programmer.entity.admin.Role;
import com.qingluo.programmer.entity.admin.User;
import com.qingluo.programmer.page.admin.Page;
import com.qingluo.programmer.service.admin.IRoleService;
import com.qingluo.programmer.service.admin.IUserService;

import com.sun.org.apache.bcel.internal.classfile.Field;

//�û����������
@Controller
@RequestMapping("/admin/user")
public class UserController {
	@Autowired
	private IUserService userServiceImpl;
	
	@Autowired
	private IRoleService roleServiceImpl;
	
	//�û��б�ҳ��
	@RequestMapping(value="/list",method = RequestMethod.GET)
	public ModelAndView list(ModelAndView model) {
		Map<String, Object> queryMap=new HashMap<String, Object>();
		model.setViewName("user/list");
		model.addObject("roleList",roleServiceImpl.findList(queryMap));
		return model;
	} 
	
	//��ȡ�û��б�
	@RequestMapping(value="/list",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getList(Page page,
			@RequestParam(name="username",required = false,defaultValue = "")String username,
			@RequestParam(name="roleId",required = false)Long roleId,
			@RequestParam(name="sex",required = false)Integer sex
			){
		Map<String, Object> ret = new HashMap<String, Object>();
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("username",username);
		queryMap.put("roleId",roleId);
		queryMap.put("sex",sex);
		queryMap.put("offset", page.getOffset());
		queryMap.put("pageSize", page.getRows());
		ret.put("rows",userServiceImpl.findList(queryMap));
		ret.put("total",userServiceImpl.getTotal(queryMap));
		return ret;
	}
	
	//�û����
	@RequestMapping(value="/add",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> add(User user){
		Map<String, String> ret = new HashMap<String, String>();
		if(user == null){
			ret.put("type", "error");
			ret.put("msg", "����д��ȷ���û���Ϣ��");
			return ret;
		}
		if(StringUtils.isEmpty(user.getUsername())){
			ret.put("type", "error");
			ret.put("msg", "����д�û����ƣ�");
			return ret;
		}
		if(StringUtils.isEmpty(user.getPassword())){
			ret.put("type", "error");
			ret.put("msg", "����д���룡");
			return ret;
		}
		if(user.getRoleId()==null) {
			ret.put("type", "error");
			ret.put("msg", "��ѡ���û���������ɫ��");
			return ret;
		}
		if(isExist(user.getUsername(), 0l)) {
			ret.put("type", "error");
			ret.put("msg", "���û����Ѿ����ڣ����������룡");
			return ret;
		}
		if(userServiceImpl.add(user)<=0) {
			ret.put("type", "error");
			ret.put("msg", "�û����ʧ�ܣ�����ϵ����Ա!");
			return ret;
		}
		ret.put("type", "success");
		ret.put("msg", "��ɫ��ӳɹ���");
		return ret;
	}
	
	
	//�޸��û���Ϣ
	@RequestMapping(value="/edit",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> edit(User user){
		Map<String, String> ret = new HashMap<String, String>();
		
		if(user == null){
			ret.put("type", "error");
			ret.put("msg", "����д��ȷ���û���Ϣ��");
			return ret;
		}
		if(StringUtils.isEmpty(user.getUsername())){
			ret.put("type", "error");
			ret.put("msg", "����д�û����ƣ�");
			return ret;
		}
		if(user.getRoleId()==null) {
			ret.put("type", "error");
			ret.put("msg", "��ѡ���û���������ɫ��");
			return ret;
		}
		if(isExist(user.getUsername(), user.getId())) {
			ret.put("type", "error");
			ret.put("msg", "���û����Ѿ����ڣ����������룡");
			return ret;
		}
		if(userServiceImpl.edit(user)<=0) {
			ret.put("type", "error");
			ret.put("msg", "�޸����ʧ�ܣ�����ϵ����Ա!");
			return ret;
		}
		ret.put("type", "success");
		ret.put("msg", "�û���Ϣ�޸ĳɹ���");
		return ret;
	}
	
	//����ɾ���û�
	@RequestMapping(value="/delete",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String>delete(@RequestParam(name="ids",required = true) String ids){
		Map<String, String> resCode=new HashMap<String, String>();
		if(StringUtils.isEmpty(ids)) {
			resCode.put("type", "error");
			resCode.put("msg","��ѡ����Ҫɾ��������!");
			return resCode;
		}
		if(ids.contains(",")) {
			ids=ids.substring(0, ids.length()-1);
		}
		if(userServiceImpl.delete(ids)<=0) {
			resCode.put("type", "error");
			resCode.put("msg","ɾ��ʧ�ܣ�����ϵ����Ա");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg","ɾ���ɹ�!");
		return resCode;
		
	}
	
	@RequestMapping(value="/upload_photo",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, String> uploadPhoto(MultipartFile photo,HttpServletRequest request){
		Map<String, String> resCode=new HashMap<String, String>();
		if(photo==null) {
			resCode.put("type", "error");
			resCode.put("msg","��ѡ����Ҫ�ϴ���ͼƬ");
			return resCode;
		}
		if(photo.getSize()>1024*10240) {
			resCode.put("type", "error");
			resCode.put("msg","�ļ�����10M��������ѡ��");
			return resCode;
		}
		//��ȡ�ļ���׺��
		int start=photo.getOriginalFilename().lastIndexOf(".")+1;
		int last=photo.getOriginalFilename().length();
		
		String lastName=photo.getOriginalFilename().substring(start, last);
		if(!"jpg,jpeg,git,png".toUpperCase().contains(lastName.toUpperCase())) {
			resCode.put("type", "error");
			resCode.put("msg","��ѡ���ʺϵ�ͼƬ");
			return resCode;
		}
		String savePath=request.getServletContext().getRealPath("/")+"/resources/upload/";
		File savaPathFile=new File(savePath);
		if(!savaPathFile.exists()) {
			savaPathFile.mkdir();
		}
		String fileName=new Date().getTime()+"."+lastName;
		try {
			photo.transferTo(new File(savePath+fileName));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			resCode.put("type", "error");
			resCode.put("msg","�ļ��ϴ�ʧ��");
			e.printStackTrace();
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg","�ļ��ϴ��ɹ�");
		resCode.put("filePath", request.getServletContext().getContextPath()+"/resources/upload/"+fileName);
		return resCode;
	}
	
	//�޸�����
	
	@RequestMapping(value="/update_password",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, String> updatePassword(@RequestParam(name="id",required = true) Long id,
			@RequestParam(name="username",required = true) String username,
			@RequestParam(name="oldPassword",required = true) String oldPassword,
			@RequestParam(name="newPassword",required = true)String newPassword
			){
		Map<String, String> resCode=new HashMap<String, String>();
		Map<String, Object> updateMap = new HashMap<String, Object>();
		if(id==null) {
			resCode.put("type", "error");
			resCode.put("msg","��ѡ����Ҫ�޸�����Ķ���");
			return resCode;
		}
		if(StringUtils.isEmpty(oldPassword)) {
			resCode.put("type", "error");
			resCode.put("msg","�������޸�ǰ������");
			return resCode;
		}
		if(StringUtils.isEmpty(newPassword)) {
			resCode.put("type", "error");
			resCode.put("msg","�������µ�����");
			return resCode;
		}
		User user=userServiceImpl.findByUserName(username);
		if(!user.getPassword().equals(oldPassword)) {
			resCode.put("type", "error");
			resCode.put("msg","�����ԭʼ��������,����������!");
			return resCode;
		}
		updateMap.put("id", id);
		updateMap.put("username", username);
		updateMap.put("password", newPassword);
		if(userServiceImpl.updataPasswordById(updateMap)<=0) {
			resCode.put("type", "error");
			resCode.put("msg","�޸�����ʧ�ܣ�����ϵ����Ա!");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg","�����޸ĳɹ�!");
		return resCode;
		
	}
	
	//�ж��û��Ƿ����
	private boolean isExist(String username,Long id) {
		User user=userServiceImpl.findByUserName(username);
		if(user==null) {
			return false;
		}
		if(user.getId().longValue()==id.longValue()) {
			return false;
		}
		return true;
	}
}
