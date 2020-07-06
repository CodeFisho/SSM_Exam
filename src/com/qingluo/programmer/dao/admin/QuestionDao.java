package com.qingluo.programmer.dao.admin;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.qingluo.programmer.entity.admin.Question;


@Repository
public interface QuestionDao {

	public int add(Question question);
	public int edit(Question question);
	public int delete(Long id);
	public List<Question> findList(Map<String, Object>querMap);
	public Integer getTotal(Map<String, Object>querMap);
	public Question findByTitle(String title);
	public int getQuestionNumByType(Map<String, Long>queryMap);
	public List<Question> findQuestionByType(@Param(value = "subjectId")Long subjectId,@Param(value="questionType")Integer questionType);
	public Question findById(Long id);
}
