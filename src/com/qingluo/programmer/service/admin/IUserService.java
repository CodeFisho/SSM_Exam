package com.qingluo.programmer.service.admin;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.qingluo.programmer.entity.admin.User;

@Service
public interface IUserService {
	public User findByUserName(String userName);
	public int add(User user);
	public int edit(User user);
	public int delete(String ids);
	public List<User> findList(Map<String, Object>querMap);
	public int getTotal(Map<String, Object>querMap);
	public int updataPasswordById(Map<String,Object> queryMap);
}
