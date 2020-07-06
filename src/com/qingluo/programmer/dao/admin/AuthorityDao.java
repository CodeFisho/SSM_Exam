package com.qingluo.programmer.dao.admin;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.qingluo.programmer.entity.admin.Authority;
//È¨ÏÞdao
@Repository
public interface AuthorityDao {
	public int add(Authority authority);
	public int deleteByRoleId(Long roleId);
	public List<Authority> findListByRoleId(Long roleId);
}
