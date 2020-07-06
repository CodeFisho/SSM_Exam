package com.qingluo.programmer.service.admin.Impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qingluo.programmer.dao.admin.SubjectDao;
import com.qingluo.programmer.entity.admin.Subject;
import com.qingluo.programmer.service.admin.ISubjectService;

@Service
public class ISubjectServiceImpl  implements ISubjectService{

	@Autowired
	private SubjectDao sunjectDao;
	@Override
	public int add(Subject subject) {
		// TODO Auto-generated method stub
		return sunjectDao.add(subject);
	}

	@Override
	public int edit(Subject subject) {
		// TODO Auto-generated method stub
		return sunjectDao.edit(subject);
	}

	@Override
	public int delete(Long id) {
		// TODO Auto-generated method stub
		return sunjectDao.delete(id);
	}

	@Override
	public List<Subject> findList(Map<String, Object> queryMap) {
		// TODO Auto-generated method stub
		return sunjectDao.findList(queryMap);
	}

	@Override
	public int getTotal(Map<String, Object> queryMap) {
		// TODO Auto-generated method stub
		return sunjectDao.getTotal(queryMap);
	}

	@Override
	public Subject findById(Long id) {
		// TODO Auto-generated method stub
		return sunjectDao.findById(id);
	}

}
