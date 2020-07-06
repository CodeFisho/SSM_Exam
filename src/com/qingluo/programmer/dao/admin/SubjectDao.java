package com.qingluo.programmer.dao.admin;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.qingluo.programmer.entity.admin.Subject;

@Repository
public interface SubjectDao {

	public int add(Subject subject);
	
	public int edit(Subject subject);
	
	public int delete(Long id);
	
	public List<Subject> findList(Map<String, Object> queryMap);
	
	public int getTotal(Map<String, Object> queryMap);
	public Subject findById(Long id);
}
