package com.qingluo.programmer.service.admin.Impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qingluo.programmer.dao.admin.UserDao;
import com.qingluo.programmer.entity.admin.User;
import com.qingluo.programmer.service.admin.IUserService;

@Service
public class IUserServiceImpl implements IUserService{
	@Autowired
	private UserDao userDao;
	@Override
	public User findByUserName(String userName) {
		// TODO Auto-generated method stub
		return userDao.findByUserName(userName);
	}
	@Override
	public int add(User user) {
		// TODO Auto-generated method stub
		return userDao.add(user);
	}
	@Override
	public int edit(User user) {
		// TODO Auto-generated method stub
		return userDao.edit(user);
	}
	@Override
	public int delete(String ids) {
		// TODO Auto-generated method stub
		return userDao.delete(ids);
	}
	@Override
	public List<User> findList(Map<String, Object> querMap) {
		// TODO Auto-generated method stub
		return userDao.findList(querMap);
	}
	@Override
	public int getTotal(Map<String, Object> querMap) {
		// TODO Auto-generated method stub
		return userDao.getTotal(querMap);
	}
	@Override
	public int updataPasswordById(Map<String, Object> queryMap) {
		// TODO Auto-generated method stub
		return userDao.updataPasswordById(queryMap);
	}

}
