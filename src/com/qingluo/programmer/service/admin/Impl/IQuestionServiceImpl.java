package com.qingluo.programmer.service.admin.Impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qingluo.programmer.dao.admin.QuestionDao;
import com.qingluo.programmer.entity.admin.Question;
import com.qingluo.programmer.service.admin.IQuestionService;

@Service
public class IQuestionServiceImpl implements IQuestionService{
	
	@Autowired
	private QuestionDao questionDao;

	@Override
	public int add(Question question) {
		// TODO Auto-generated method stub
		return questionDao.add(question);
	}

	@Override
	public int edit(Question question) {
		// TODO Auto-generated method stub
		return questionDao.edit(question);
	}

	@Override
	public int delete(Long id) {
		// TODO Auto-generated method stub
		return questionDao.delete(id);
	}

	@Override
	public List<Question> findList(Map<String, Object> querMap) {
		// TODO Auto-generated method stub
		return questionDao.findList(querMap);
	}

	@Override
	public Integer getTotal(Map<String, Object> querMap) {
		// TODO Auto-generated method stub
		return questionDao.getTotal(querMap);
	}

	@Override
	public Question findByTitle(String title) {
		// TODO Auto-generated method stub
		return questionDao.findByTitle(title);
	}

	

	@Override
	public List<Question> findQuestionByType(Long subjectId,Integer questionType) {
		// TODO Auto-generated method stub
		return questionDao.findQuestionByType(subjectId,questionType);
	}

	@Override
	public Question findById(Long id) {
		// TODO Auto-generated method stub
		return questionDao.findById(id);
	}

	@Override
	public int getQuestionNumByType(Map<String, Long> queryMap) {
		// TODO Auto-generated method stub
		return questionDao.getQuestionNumByType(queryMap);
	}

}
