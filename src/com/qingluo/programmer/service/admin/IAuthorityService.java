package com.qingluo.programmer.service.admin;

import java.util.List;

import org.springframework.stereotype.Service;

import com.qingluo.programmer.entity.admin.Authority;

//È¨ÏÞservice
@Service
public interface IAuthorityService {
	public int add(Authority authority);
	public int deleteByRoleId(Long roleId);
	public List<Authority> findListByRoleId(Long roleId);

}
