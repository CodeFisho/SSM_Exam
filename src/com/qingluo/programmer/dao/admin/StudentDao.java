package com.qingluo.programmer.dao.admin;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.qingluo.programmer.entity.admin.Student;

@Repository
public interface StudentDao {
	public int add(Student student);
	public int edit(Student student);
	public int delete(Long id);
	public List<Student> findList(Map<String, Object>querMap);
	public Integer getTotal(Map<String, Object>querMap);
	public Student findNameByNameAndId(String name);
	public Student findNameAndPassword(String name);
	public Student findAllInfo(String name);
}
