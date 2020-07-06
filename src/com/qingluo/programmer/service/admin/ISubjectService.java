package com.qingluo.programmer.service.admin;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.qingluo.programmer.entity.admin.Subject;

//学科专业servcie层
@Service
public interface ISubjectService {

	public int add(Subject subject);
	
	public int edit(Subject subject);
	
	public int delete(Long id);
	
	public List<Subject> findList(Map<String, Object> queryMap);
	
	public int getTotal(Map<String, Object> queryMap);
	
	public Subject findById(Long id);
}
