package com.qingluo.programmer.util;

import java.util.ArrayList;
import java.util.List;

import com.qingluo.programmer.entity.admin.Menu;


public class MenuUtils {
	//��ȡ�����˵�
	public static List<Menu> getAllTopMenu(List<Menu> menuList){
		List<Menu> ret = new ArrayList<Menu>();
		for(Menu menu:menuList){
			if(menu.getParentId() == 0){
				ret.add(menu);
			}
		}
		return ret;
	}
	
	//��ȡ�����˵�
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
	
	//����ǰ�˴���Ķ����˵�id����ȡ�����˵�
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
