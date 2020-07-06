package com.qingluo.programmer.service.admin;
// ‘Ã‚service

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import com.qingluo.programmer.entity.admin.Question;



@Service
public interface IQuestionService {

	public int add(Question student);
	public int edit(Question student);
	public int delete(Long id);
	public List<Question> findList(Map<String, Object>querMap);
	public Integer getTotal(Map<String, Object>querMap);
	public Question findByTitle(String title);
	public int getQuestionNumByType(Map<String, Long>queryMap);
	public List<Question> findQuestionByType(Long subjectId, Integer questionId);
	public Question findById(Long id);
}
