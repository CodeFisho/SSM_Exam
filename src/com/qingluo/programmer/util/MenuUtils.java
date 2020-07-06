package com.qingluo.programmer.util;

import java.util.ArrayList;
import java.util.List;

import com.qingluo.programmer.entity.admin.Menu;


public class MenuUtils {
	//获取顶级菜单
	public static List<Menu> getAllTopMenu(List<Menu> menuList){
		List<Menu> ret = new ArrayList<Menu>();
		for(Menu menu:menuList){
			if(menu.getParentId() == 0){
				ret.add(menu);
			}
		}
		return ret;
	}
	
	//获取二级菜单
	public static List<Menu> getAllSecondMenu(List<Menu> menuList){
		List<Menu> ret = new ArrayList<Menu>();
		List<Menu> topMenuList=getAllTopMenu(menuList);
		for(Menu menu:menuList) {
			for(Menu topMenu:topMenuList) {
				if(menu.getParentId()==topMenu.getId()) {
					ret.add(menu);
					break;
				}
			}
		}
		return ret;
	}
	
	//根据前端传入的二级菜单id，获取三级菜单
	public static List<Menu> getAllThirdMenu(List<Menu> menuList,Long mid){
		List<Menu> ret = new ArrayList<Menu>();
		for(Menu menu:menuList) {
			if(menu.getParentId()==mid) {
				ret.add(menu);
			}
		}
		return ret;
	}
}
