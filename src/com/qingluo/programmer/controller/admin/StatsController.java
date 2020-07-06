package com.qingluo.programmer.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
//成绩统计控制器
import org.springframework.web.servlet.ModelAndView;

import com.qingluo.programmer.service.admin.IExamPaperService;
import com.qingluo.programmer.service.admin.IExamService;

@Controller
@RequestMapping("/admin/stats")
public class StatsController {
	
	@Autowired
	private IExamService examServiceImpl;
	
	@Autowired
	private IExamPaperService examPaperServiceImpl;
	
	@GetMapping("/examStats")
	public ModelAndView stats(ModelAndView model) {
		Map<String, Object> queryMap=new HashMap<String, Object>();
		queryMap.put("offset", 0);
		queryMap.put("pageSize", 99999);
		model.addObject("examList", examServiceImpl.findList(queryMap));
		model.setViewName("stats/exam_stats");
		return model;
	}
	
	@PostMapping("/get_stats")
	@ResponseBody
	public Map<String, Object> getsStatsMa(Long examId){
		Map<String, Object> resCode=new HashMap<String, Object>();
		if(examId==null) {
			resCode.put("type", "error");
			resCode.put("msg", "请选择需要统计的考试信息");
			return resCode;
		}
		List<Map<String, Object>> examStats=examPaperServiceImpl.getExamStats(examId);
		resCode.put("type", "success");
		resCode.put("studentList", getListByMap(examStats, "sname"));
		resCode.put("studentScore", getListByMap(examStats, "score"));
		resCode.put("msg", "统计成功");
		return resCode;
	}
	
	private List<Object> getListByMap(List<Map<String, Object>> mapList,String key){
		List<Object> ret=new ArrayList<Object>();
		for(Map<String, Object>map:mapList) {
			ret.add(map.get(key));
		}
		return ret;
	}
}
