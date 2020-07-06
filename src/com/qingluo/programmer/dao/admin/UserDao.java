package com.qingluo.programmer.dao.admin;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.qingluo.programmer.entity.admin.User;

@Repository
public interface UserDao {
	public User findByUserName(String userName);
	public int add(User user);
	public int edit(User user);
	public int delete(String ids);
	public List<User> findList(Map<String, Object>querMap);
	public int getTotal(Map<String, Object>querMap);
	public int updataPasswordById(Map<String,Object> queryMap);
}
