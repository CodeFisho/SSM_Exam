package com.qingluo.programmer.service.admin.Impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qingluo.programmer.dao.admin.StudentDao;
import com.qingluo.programmer.entity.admin.Student;
import com.qingluo.programmer.entity.admin.Subject;
import com.qingluo.programmer.service.admin.IStudentService;
import com.qingluo.programmer.service.admin.ISubjectService;

@Service
public class IStudentServiceImpl implements IStudentService{

	@Autowired
	private StudentDao studentDao;

	@Override
	public int add(Student student) {
		// TODO Auto-generated method stub
		return studentDao.add(student);
	}

	@Override
	public int edit(Student student) {
		// TODO Auto-generated method stub
		return studentDao.edit(student);
	}

	@Override
	public int delete(Long id) {
		// TODO Auto-generated method stub
		return studentDao.delete(id);
	}

	@Override
	public List<Student> findList(Map<String, Object> queryMap) {
		// TODO Auto-generated method stub
		return studentDao.findList(queryMap);
	}

	@Override
	public Integer getTotal(Map<String, Object> queryMap) {
		// TODO Auto-generated method stub
		return studentDao.getTotal(queryMap);
	}

	@Override
	public Student findNameByNameAndId(String name) {
		// TODO Auto-generated method stub
		return studentDao.findNameByNameAndId(name);
	}

	@Override
	public Student findNameAndPassword(String name) {
		// TODO Auto-generated method stub
		return studentDao.findNameAndPassword(name);
	}

	@Override
	public Student findAllInfo(String name) {
		// TODO Auto-generated method stub
		return studentDao.findAllInfo(name);
	}
	
	

}
