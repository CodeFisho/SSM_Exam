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

//ϵͳ�������

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
	

	//��¼�����ҳ
	@RequestMapping(value="/index",method = RequestMethod.GET)
	public ModelAndView index(ModelAndView model,HttpServletRequest request) {
		List<Menu> userMenus=(List<Menu>)request.getSession().getAttribute("userMenus");
		model.setViewName("system/index");
		model.addObject("topMenuList", MenuUtils.getAllTopMenu(userMenus));
		model.addObject("secondMenuList", MenuUtils.getAllSecondMenu(userMenus));
		return model;
	}
	
	//��ӭҳ��
	@RequestMapping(value="/welcome",method = RequestMethod.GET)
	public ModelAndView welcome(ModelAndView model) {
		model.setViewName("system/welcome");
		return model;
	}
	
	
	//��¼ҳ��
	@RequestMapping(value="/login",method = RequestMethod.GET)
	public ModelAndView login(ModelAndView model) {
		model.setViewName("/system/login");
		return model;
	}
	//��¼����
	@RequestMapping(value="/login",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> loginAct(User user,String cpacha,HttpServletRequest request){
		Map<String, String> resCode=new HashMap<String, String>();
		if(user==null) {
			resCode.put("type","error");
			resCode.put("msg","����д�û���Ϣ");
			return resCode;
		}
		if(StringUtils.isEmpty(cpacha)) {
			resCode.put("type","error");
			resCode.put("msg","����д��֤��");
			return resCode;
		}
		
		if(StringUtils.isEmpty(user.getUsername())) {
			resCode.put("type","error");
			resCode.put("msg","����д�û���");
			return resCode;
		}
		if(StringUtils.isEmpty(user.getPassword())) {
			resCode.put("type","error");
			resCode.put("msg","����д����");
			return resCode;
		}
		Object loginCpacha=request.getSession().getAttribute("loginCpacha");
		
		if(loginCpacha==null) {
			resCode.put("type","error");
			resCode.put("msg","�Ự��ʱ����ˢ��ҳ��");
			return resCode;
		}
		if(!cpacha.toString().toUpperCase().equals(loginCpacha.toString().toUpperCase())) {
			resCode.put("type","error");
			resCode.put("msg","��֤�����");
			log.setContent("�û���:"+user.getUsername()+"���û��ڵ�¼ʱ����������֤��");
			logServiceImpl.add(log);
			return resCode;
		}
		User findByUserName = userServiceImpl.findByUserName(user.getUsername());
		if(findByUserName==null) {
			resCode.put("type","error");
			resCode.put("msg","�û��������ڣ�����������");
			return resCode;
		}
		if(!user.getPassword().equals(findByUserName.getPassword())) {
			resCode.put("type","error");
			resCode.put("msg","�����������������");
			return resCode;
		}
		//�����û��Ľ�ɫid�ҵ���Ӧ���û���ɫ
		Role role=roleServiceImpl.find(findByUserName.getRoleId());
		//���ݽ�ɫ��id�ҵ���Ӧ��Ȩ����Ϣ
		List<Authority> roleAuthotity=authorityServiceImpl.findListByRoleId(role.getId());
		String menuIds="";
		for(Authority authority:roleAuthotity) {
			menuIds+=authority.getMenuId()+",";
		}
		if(!StringUtils.isEmpty(menuIds)) {
			menuIds=menuIds.substring(0, menuIds.length()-1);
		}
		//���ݸ��û�������ɫ��Ȩ�����ȡӵ�еĲ˵�
		List<Menu> userMenus=menuServiceImpl.findListByIds(menuIds);
		//���û���һЩ�ؼ���Ϣ����session�У������ȡ
		request.getSession().setAttribute("admin", findByUserName);
		request.getSession().setAttribute("role", role);
		request.getSession().setAttribute("userMenus", userMenus);
		resCode.put("type", "success");
		resCode.put("msg", "��¼�ɹ�");
		log.setContent("�û���:"+user.getUsername()+"���û������̨����ϵͳ");
		logServiceImpl.add(log);
		return resCode;
	}
	
	
	
	
	
	//��֤��
	@RequestMapping(value="get_cpacha",method = RequestMethod.GET)
	public void generateCpacha(
			@RequestParam(value="vl",required = false,defaultValue = "4") Integer vcodeLen,
			@RequestParam(value="w",required = false,defaultValue = "100") Integer width,
			@RequestParam(value="h",required = false,defaultValue = "30") Integer height,
			@RequestParam(value="type",required = true,defaultValue = "loginCpacha") String cpacha,
			HttpServletRequest request,
			HttpServletResponse response) {
		CpachaUtil cpachaUtil=new CpachaUtil(vcodeLen,width,height);
		//������֤��
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
	
	//�˳�ע������
	@RequestMapping(value="/logout",method = RequestMethod.GET)
	public String logout(HttpServletRequest request) {
		HttpSession session=request.getSession();
		User user=(User)session.getAttribute("admin");
		log.setContent("�û���:"+user.getUsername()+"���û��˳�����ϵͳ");
		logServiceImpl.add(log);
		session.removeAttribute("admin");
		session.removeAttribute("role");
		session.removeAttribute("userMenus");		
		return "redirect:login";
	}
	
	
}
