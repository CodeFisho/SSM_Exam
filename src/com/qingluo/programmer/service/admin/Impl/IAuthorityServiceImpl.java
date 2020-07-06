package com.qingluo.programmer.service.admin.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qingluo.programmer.dao.admin.AuthorityDao;
import com.qingluo.programmer.entity.admin.Authority;
import com.qingluo.programmer.service.admin.IAuthorityService;

@Service
public class IAuthorityServiceImpl implements IAuthorityService{
	@Autowired
	private AuthorityDao authorityDao;

	@Override
	public int add(Authority authority) {
		// TODO Auto-generated method stub
		return authorityDao.add(authority);
	}

	@Override
	public int deleteByRoleId(Long roleId) {
		// TODO Auto-generated method stub
		return authorityDao.deleteByRoleId(roleId);
	}

	@Override
	public List<Authority> findListByRoleId(Long roleId) {
		// TODO Auto-generated method stub
		return authorityDao.findListByRoleId(roleId);
	}

}
