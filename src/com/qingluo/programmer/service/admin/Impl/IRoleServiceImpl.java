package com.qingluo.programmer.service.admin.Impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qingluo.programmer.dao.admin.RoleDao;
import com.qingluo.programmer.entity.admin.Role;
import com.qingluo.programmer.service.admin.IRoleService;

@Service
public class IRoleServiceImpl implements IRoleService{

	@Autowired
	private RoleDao roleDao;
	@Override
	public int add(Role role) {
		// TODO Auto-generated method stub
		return roleDao.add(role);
	}

	@Override
	public int edit(Role role) {
		// TODO Auto-generated method stub
		return roleDao.edit(role);
	}

	@Override
	public int delete(Long id) {
		// TODO Auto-generated method stub
		return roleDao.delete(id);
	}

	@Override
	public List<Role> findList(Map<String, Object> queryMap) {
		// TODO Auto-generated method stub
		return roleDao.findList(queryMap);
	}

	@Override
	public int getTotal(Map<String, Object> queryMap) {
		// TODO Auto-generated method stub
		return roleDao.getTotal(queryMap);
	}

	@Override
	public Role find(Long id) {
		// TODO Auto-generated method stub
		return roleDao.find(id);
	}

}
