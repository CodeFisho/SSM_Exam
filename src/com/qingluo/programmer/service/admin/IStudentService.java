package com.qingluo.programmer.service.admin;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.qingluo.programmer.entity.admin.Student;

@Service
public interface IStudentService {
	public int add(Student student);
	public int edit(Student student);
	public int delete(Long id);
	public List<Student> findList(Map<String, Object>querMap);
	public Integer getTotal(Map<String, Object>querMap);
	public Student findNameByNameAndId(String name);
	public Student findNameAndPassword(String name);
	public Student findAllInfo(String name);
}
