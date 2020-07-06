package com.qingluo.programmer.service.admin.Impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qingluo.programmer.dao.admin.LogDao;
import com.qingluo.programmer.entity.admin.Log;
import com.qingluo.programmer.service.admin.ILogService;
import com.sun.xml.internal.messaging.saaj.util.LogDomainConstants;

@Service
public class ILogServiceImpl implements ILogService{

	
	@Autowired
	private LogDao logDao;
	@Override
	public int add(Log log) {
		// TODO Auto-generated method stub
		return logDao.add(log);
	}

	@Override
	public List<Log> findList(Map<String, Object> queryMap) {
		// TODO Auto-generated method stub
		return logDao.findList(queryMap);
	}

	@Override
	public int getTotal(Map<String, Object> queryMap) {
		// TODO Auto-generated method stub
		return logDao.getTotal(queryMap);
	}

	@Override
	public int delete(String ids) {
		// TODO Auto-generated method stub
		return logDao.delete(ids);
	}

}
