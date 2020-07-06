package com.qingluo.programmer.controller.admin;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.qingluo.programmer.entity.admin.Authority;
import com.qingluo.programmer.entity.admin.Log;
import com.qingluo.programmer.entity.admin.Menu;
import com.qingluo.programmer.entity.admin.Role;
import com.qingluo.programmer.entity.admin.User;
import com.qingluo.programmer.page.admin.Page;
import com.qingluo.programmer.service.admin.IAuthorityService;
import com.qingluo.programmer.service.admin.ILogService;
import com.qingluo.programmer.service.admin.IMenuService;
import com.qingluo.programmer.service.admin.IRoleService;
import com.qingluo.programmer.service.admin.IUserService;
import com.qingluo.programmer.util.CpachaUtil;
import com.qingluo.programmer.util.MenuUtils;

//系统类控制器

@Controller
@RequestMapping("/system")
public class SystemController {
	
	@Autowired
	private IUserService userServiceImpl;
	
	@Autowired
	private IRoleService roleServiceImpl;
	
	@Autowired
	private IAuthorityService authorityServiceImpl;
	
	@Autowired
	private IMenuService menuServiceImpl;
	
	@Autowired
	private ILogService logServiceImpl;
	
	@Autowired
	private Log log;
	

	//登录后的主页
	@RequestMapping(value="/index",method = RequestMethod.GET)
	public ModelAndView index(ModelAndView model,HttpServletRequest request) {
		List<Menu> userMenus=(List<Menu>)request.getSession().getAttribute("userMenus");
		model.setViewName("system/index");
		model.addObject("topMenuList", MenuUtils.getAllTopMenu(userMenus));
		model.addObject("secondMenuList", MenuUtils.getAllSecondMenu(userMenus));
		return model;
	}
	
	//欢迎页面
	@RequestMapping(value="/welcome",method = RequestMethod.GET)
	public ModelAndView welcome(ModelAndView model) {
		model.setViewName("system/welcome");
		return model;
	}
	
	
	//登录页面
	@RequestMapping(value="/login",method = RequestMethod.GET)
	public ModelAndView login(ModelAndView model) {
		model.setViewName("/system/login");
		return model;
	}
	//登录过程
	@RequestMapping(value="/login",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> loginAct(User user,String cpacha,HttpServletRequest request){
		Map<String, String> resCode=new HashMap<String, String>();
		if(user==null) {
			resCode.put("type","error");
			resCode.put("msg","请填写用户信息");
			return resCode;
		}
		if(StringUtils.isEmpty(cpacha)) {
			resCode.put("type","error");
			resCode.put("msg","请填写验证码");
			return resCode;
		}
		
		if(StringUtils.isEmpty(user.getUsername())) {
			resCode.put("type","error");
			resCode.put("msg","请填写用户名");
			return resCode;
		}
		if(StringUtils.isEmpty(user.getPassword())) {
			resCode.put("type","error");
			resCode.put("msg","请填写密码");
			return resCode;
		}
		Object loginCpacha=request.getSession().getAttribute("loginCpacha");
		
		if(loginCpacha==null) {
			resCode.put("type","error");
			resCode.put("msg","会话超时，请刷新页面");
			return resCode;
		}
		if(!cpacha.toString().toUpperCase().equals(loginCpacha.toString().toUpperCase())) {
			resCode.put("type","error");
			resCode.put("msg","验证码错误");
			log.setContent("用户名:"+user.getUsername()+"的用户在登录时输入错误的验证码");
			logServiceImpl.add(log);
			return resCode;
		}
		User findByUserName = userServiceImpl.findByUserName(user.getUsername());
		if(findByUserName==null) {
			resCode.put("type","error");
			resCode.put("msg","用户名不存在，请重新输入");
			return resCode;
		}
		if(!user.getPassword().equals(findByUserName.getPassword())) {
			resCode.put("type","error");
			resCode.put("msg","密码错误，请重新输入");
			return resCode;
		}
		//根据用户的角色id找到对应的用户角色
		Role role=roleServiceImpl.find(findByUserName.getRoleId());
		//根据角色的id找到对应的权限信息
		List<Authority> roleAuthotity=authorityServiceImpl.findListByRoleId(role.getId());
		String menuIds="";
		for(Authority authority:roleAuthotity) {
			menuIds+=authority.getMenuId()+",";
		}
		if(!StringUtils.isEmpty(menuIds)) {
			menuIds=menuIds.substring(0, menuIds.length()-1);
		}
		//根据该用户所属角色的权限里获取拥有的菜单
		List<Menu> userMenus=menuServiceImpl.findListByIds(menuIds);
		//将用户的一些关键信息放入session中，方便获取
		request.getSession().setAttribute("admin", findByUserName);
		request.getSession().setAttribute("role", role);
		request.getSession().setAttribute("userMenus", userMenus);
		resCode.put("type", "success");
		resCode.put("msg", "登录成功");
		log.setContent("用户名:"+user.getUsername()+"的用户登入后台管理系统");
		logServiceImpl.add(log);
		return resCode;
	}
	
	
	
	
	
	//验证码
	@RequestMapping(value="get_cpacha",method = RequestMethod.GET)
	public void generateCpacha(
			@RequestParam(value="vl",required = false,defaultValue = "4") Integer vcodeLen,
			@RequestParam(value="w",required = false,defaultValue = "100") Integer width,
			@RequestParam(value="h",required = false,defaultValue = "30") Integer height,
			@RequestParam(value="type",required = true,defaultValue = "loginCpacha") String cpacha,
			HttpServletRequest request,
			HttpServletResponse response) {
		CpachaUtil cpachaUtil=new CpachaUtil(vcodeLen,width,height);
		//生成验证码
		String generatorVCode=cpachaUtil.generatorVCode();
		request.getSession().setAttribute(cpacha, generatorVCode);
		BufferedImage generatorRotataVCodeImage=cpachaUtil.generatorRotateVCodeImage(generatorVCode, true);
		try {
			ImageIO.write(generatorRotataVCodeImage, "gif", response.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//退出注销功能
	@RequestMapping(value="/logout",method = RequestMethod.GET)
	public String logout(HttpServletRequest request) {
		HttpSession session=request.getSession();
		User user=(User)session.getAttribute("admin");
		log.setContent("用户名:"+user.getUsername()+"的用户退出管理系统");
		logServiceImpl.add(log);
		session.removeAttribute("admin");
		session.removeAttribute("role");
		session.removeAttribute("userMenus");		
		return "redirect:login";
	}
	
	
}
