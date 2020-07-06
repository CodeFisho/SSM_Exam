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

//用户管理控制器
@Controller
@RequestMapping("/admin/user")
public class UserController {
	@Autowired
	private IUserService userServiceImpl;
	
	@Autowired
	private IRoleService roleServiceImpl;
	
	//用户列表页面
	@RequestMapping(value="/list",method = RequestMethod.GET)
	public ModelAndView list(ModelAndView model) {
		Map<String, Object> queryMap=new HashMap<String, Object>();
		model.setViewName("user/list");
		model.addObject("roleList",roleServiceImpl.findList(queryMap));
		return model;
	} 
	
	//获取用户列表
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
	
	//用户添加
	@RequestMapping(value="/add",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> add(User user){
		Map<String, String> ret = new HashMap<String, String>();
		if(user == null){
			ret.put("type", "error");
			ret.put("msg", "请填写正确的用户信息！");
			return ret;
		}
		if(StringUtils.isEmpty(user.getUsername())){
			ret.put("type", "error");
			ret.put("msg", "请填写用户名称！");
			return ret;
		}
		if(StringUtils.isEmpty(user.getPassword())){
			ret.put("type", "error");
			ret.put("msg", "请填写密码！");
			return ret;
		}
		if(user.getRoleId()==null) {
			ret.put("type", "error");
			ret.put("msg", "请选择用户的所属角色！");
			return ret;
		}
		if(isExist(user.getUsername(), 0l)) {
			ret.put("type", "error");
			ret.put("msg", "该用户名已经存在，请重新输入！");
			return ret;
		}
		if(userServiceImpl.add(user)<=0) {
			ret.put("type", "error");
			ret.put("msg", "用户添加失败，请联系管理员!");
			return ret;
		}
		ret.put("type", "success");
		ret.put("msg", "角色添加成功！");
		return ret;
	}
	
	
	//修改用户信息
	@RequestMapping(value="/edit",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> edit(User user){
		Map<String, String> ret = new HashMap<String, String>();
		
		if(user == null){
			ret.put("type", "error");
			ret.put("msg", "请填写正确的用户信息！");
			return ret;
		}
		if(StringUtils.isEmpty(user.getUsername())){
			ret.put("type", "error");
			ret.put("msg", "请填写用户名称！");
			return ret;
		}
		if(user.getRoleId()==null) {
			ret.put("type", "error");
			ret.put("msg", "请选择用户的所属角色！");
			return ret;
		}
		if(isExist(user.getUsername(), user.getId())) {
			ret.put("type", "error");
			ret.put("msg", "该用户名已经存在，请重新输入！");
			return ret;
		}
		if(userServiceImpl.edit(user)<=0) {
			ret.put("type", "error");
			ret.put("msg", "修改添加失败，请联系管理员!");
			return ret;
		}
		ret.put("type", "success");
		ret.put("msg", "用户信息修改成功！");
		return ret;
	}
	
	//批量删除用户
	@RequestMapping(value="/delete",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String>delete(@RequestParam(name="ids",required = true) String ids){
		Map<String, String> resCode=new HashMap<String, String>();
		if(StringUtils.isEmpty(ids)) {
			resCode.put("type", "error");
			resCode.put("msg","请选择需要删除的数据!");
			return resCode;
		}
		if(ids.contains(",")) {
			ids=ids.substring(0, ids.length()-1);
		}
		if(userServiceImpl.delete(ids)<=0) {
			resCode.put("type", "error");
			resCode.put("msg","删除失败，请联系管理员");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg","删除成功!");
		return resCode;
		
	}
	
	@RequestMapping(value="/upload_photo",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, String> uploadPhoto(MultipartFile photo,HttpServletRequest request){
		Map<String, String> resCode=new HashMap<String, String>();
		if(photo==null) {
			resCode.put("type", "error");
			resCode.put("msg","请选择需要上传的图片");
			return resCode;
		}
		if(photo.getSize()>1024*10240) {
			resCode.put("type", "error");
			resCode.put("msg","文件大于10M，请重新选择");
			return resCode;
		}
		//获取文件后缀名
		int start=photo.getOriginalFilename().lastIndexOf(".")+1;
		int last=photo.getOriginalFilename().length();
		
		String lastName=photo.getOriginalFilename().substring(start, last);
		if(!"jpg,jpeg,git,png".toUpperCase().contains(lastName.toUpperCase())) {
			resCode.put("type", "error");
			resCode.put("msg","请选择适合的图片");
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
			resCode.put("msg","文件上传失败");
			e.printStackTrace();
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg","文件上传成功");
		resCode.put("filePath", request.getServletContext().getContextPath()+"/resources/upload/"+fileName);
		return resCode;
	}
	
	//修改密码
	
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
			resCode.put("msg","请选择需要修改密码的对象");
			return resCode;
		}
		if(StringUtils.isEmpty(oldPassword)) {
			resCode.put("type", "error");
			resCode.put("msg","请输入修改前的密码");
			return resCode;
		}
		if(StringUtils.isEmpty(newPassword)) {
			resCode.put("type", "error");
			resCode.put("msg","请输入新的密码");
			return resCode;
		}
		User user=userServiceImpl.findByUserName(username);
		if(!user.getPassword().equals(oldPassword)) {
			resCode.put("type", "error");
			resCode.put("msg","输入的原始密码有误,请重新输入!");
			return resCode;
		}
		updateMap.put("id", id);
		updateMap.put("username", username);
		updateMap.put("password", newPassword);
		if(userServiceImpl.updataPasswordById(updateMap)<=0) {
			resCode.put("type", "error");
			resCode.put("msg","修改密码失败，请联系管理员!");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg","密码修改成功!");
		return resCode;
		
	}
	
	//判断用户是否存在
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
