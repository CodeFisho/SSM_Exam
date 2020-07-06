package com.qingluo.programmer.controller.admin;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.qingluo.programmer.entity.admin.Question;
import com.qingluo.programmer.page.admin.Page;
import com.qingluo.programmer.service.admin.IQuestionService;
import com.qingluo.programmer.service.admin.ISubjectService;

//试题控制器
@Controller
@RequestMapping("/admin/question")
public class QuestionController {

	@Autowired 
	private IQuestionService questionServiceImpl;
	
	@Autowired
	private ISubjectService subjectServiceImpl;
	
	//试题列表页面
	@GetMapping("/list")
	public ModelAndView list(ModelAndView model) {
		Map<String, Object> queryMap=new HashMap<String, Object>();
		queryMap.put("pageSize", 99999);
		queryMap.put("offset",0 );
		model.addObject("subjectList", subjectServiceImpl.findList(queryMap));
		model.setViewName("question/list");
		return model;
	}
	
	  //加载试题信息列表
	  @PostMapping("/list")
	  @ResponseBody 
	  public Map<String, Object> list(Page page,
			  @RequestParam(name="title",required = false) String title,
			  @RequestParam(name="questionType",required = false) Integer questionType,
			  @RequestParam(name="subjectId",required = false) Long subjectId
			  ){
		  Map<String,Object> resCode=new HashMap<String, Object>();
		  Map<String, Object> queryMap=new HashMap<String, Object>();
		  queryMap.put("offset", page.getOffset());
		  queryMap.put("pageSize", page.getRows());
		  if(!StringUtils.isEmpty(title)) {
			  queryMap.put("title", title);
		  }
		  if(questionType!=null) {
			  queryMap.put("questionType",questionType);
		  }
		  if(subjectId!=null) {
			  queryMap.put("subjectId", subjectId);
		  }
		  resCode.put("rows", questionServiceImpl.findList(queryMap));
		  resCode.put("total", questionServiceImpl.getTotal(queryMap));
		  return resCode;  
	  }
	 
	@PostMapping("/add")
	@ResponseBody
	public Map<String, String> add(Question question){
		 Map<String,String> resCode=new HashMap<String, String>();
		 if(question==null) {
			 resCode.put("type", "error");
			 resCode.put("msg", "请检查输入的数据");
			 return resCode;
		 }
		 if(StringUtils.isEmpty(question.getTitle())) {
			 resCode.put("type", "error");
			 resCode.put("msg", "请填写试题题目");
			 return resCode;
		 }
		 if(StringUtils.isEmpty(question.getAnswer())) {
			 resCode.put("type", "error");
			 resCode.put("msg", "请填写正确答案");
			 return resCode;
		 }
		 if(StringUtils.isEmpty(question.getAttrA())) {
			 resCode.put("type", "error");
			 resCode.put("msg", "请填写试题选项A！");
			 return resCode;
		 }
		 if(StringUtils.isEmpty(question.getAttrB())) {
			 resCode.put("type", "error");
			 resCode.put("msg", "请填写试题选项B");
			 return resCode;
		 }
		 question.setScoreByType();
		 if(questionServiceImpl.add(question)<=0){
			 resCode.put("type", "error");
			 resCode.put("msg", "试题添加失败，请联系管理员");
			 return resCode;
		 }
		 resCode.put("type", "success");
		 resCode.put("msg", "试题添加成功");
		 return resCode;
	}
	
	@PostMapping("/edit")
	@ResponseBody
	public Map<String, String> edit(Question question){
		Map<String,String> resCode=new HashMap<String, String>();
		 if(question==null) {
			 resCode.put("type", "error");
			 resCode.put("msg", "请检查输入的数据");
			 return resCode;
		 }
		 if(StringUtils.isEmpty(question.getTitle())) {
			 resCode.put("type", "error");
			 resCode.put("msg", "请填写试题题目");
			 return resCode;
		 }
		 if(StringUtils.isEmpty(question.getAnswer())) {
			 resCode.put("type", "error");
			 resCode.put("msg", "请填写正确答案");
			 return resCode;
		 }
		 if(StringUtils.isEmpty(question.getAttrA())) {
			 resCode.put("type", "error");
			 resCode.put("msg", "请填写试题选项A！");
			 return resCode;
		 }
		 if(StringUtils.isEmpty(question.getAttrB())) {
			 resCode.put("type", "error");
			 resCode.put("msg", "请填写试题选项B");
			 return resCode;
		 }
		 question.setScoreByType();
		 if(questionServiceImpl.edit(question)<=0){
			 resCode.put("type", "error");
			 resCode.put("msg", "试题修改失败，请联系管理员");
			 return resCode;
		 }
		 resCode.put("type", "success");
		 resCode.put("msg", "试题修改成功");
		 return resCode;
	}
	
	@PostMapping("/delete")
	@ResponseBody
	public Map<String, String> delete(Long id){
		Map<String,String> resCode=new HashMap<String, String>();
		if(id==null) {
			resCode.put("type", "error");
			resCode.put("msg","请选择需要删除的数据");
			return resCode;
		}
		try {
			if(questionServiceImpl.delete(id)<=0) {
				resCode.put("type", "error");
				resCode.put("msg","删除失败，请联系管理员");
				return resCode;
			}
		}catch (Exception e) {
			resCode.put("type", "error");
			resCode.put("msg","无法删除该试题");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg","删除试题信息成功");
		return resCode;
	}
	
	//上传excel并且处理
	@PostMapping("/uploap_file")
	@ResponseBody
	private Map<String, String> uploadExcelFile(MultipartFile excelFile,Long subjectId){
		Map<String, String> resCode=new HashMap<String, String>();
		if(excelFile==null) {
			resCode.put("type", "error");
			resCode.put("msg","请选择需要上传的文件");
			return resCode;
		}
		if(subjectId==null) {
			resCode.put("type", "error");
			resCode.put("msg","请选择试题所属科目");
			return resCode;
		}
		//检查文件大小
		if(excelFile.getSize()>1024*1024*5) {
			resCode.put("type", "error");
			resCode.put("msg","文件过大");
			return resCode;
		}
		int start=excelFile.getOriginalFilename().lastIndexOf(".")+1;
		int end=excelFile.getOriginalFilename().length();
		//获取后缀名
		String suffix=excelFile.getOriginalFilename().substring(start, end);
		if(!"xls,xlsx".contains(suffix)) {
			resCode.put("type", "error");
			resCode.put("msg","请选择excel文件");
			return resCode;
		}
		String msg="";
		try {
			msg+=readExcel(excelFile.getInputStream(),subjectId);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if("".equals(msg)){
			msg="全部导入成功";
		}
		resCode.put("type", "success");
		resCode.put("msg",msg);
		return resCode;
	}
	
	//读取合适格式的excel文件并将内容插入到数据库中
	private String readExcel(InputStream fileInputStream,Long subjectId) {
		StringBuilder sBuilder=new StringBuilder();
		try {
			HSSFWorkbook questionWorkbook=new HSSFWorkbook(fileInputStream);
			HSSFSheet sheetAt=questionWorkbook.getSheetAt(0);
			if(sheetAt.getLastRowNum()<=0) {
				sBuilder.append("该文件为空!");
			}
			for(int rowIndex=1;rowIndex<=sheetAt.getLastRowNum();rowIndex++) {
				Question question=new Question();
				HSSFRow row =sheetAt.getRow(rowIndex);
				if(row.getCell(0)==null) {
					sBuilder.append("第"+rowIndex+"行,试题类型为空，跳过<br/>");
					continue;
				}
				Double questionType=row.getCell(0).getNumericCellValue();
				question.setQuestionType(questionType.intValue());
				
				if(row.getCell(1)==null) {
					sBuilder.append("第"+rowIndex+"行,题目为空，跳过<br/>");
					continue;
				}
				question.setTitle(row.getCell(1).getStringCellValue());
				
				if(row.getCell(2)==null) {
					sBuilder.append("第"+rowIndex+"行,分值为空，跳过<br/>");
					continue;
				}
				Double score=row.getCell(2).getNumericCellValue();
				question.setScore(score.intValue());
						
				if(row.getCell(3)==null) {
					sBuilder.append("第"+rowIndex+"行,选项A为空，跳过<br/>");
					continue;
				}
				question.setAttrA(row.getCell(3).getStringCellValue());
				
				if(row.getCell(4)==null) {
					sBuilder.append("第"+rowIndex+"行,选项B为空，跳过<br/>");
					continue;
				}
				question.setAttrB(row.getCell(4).getStringCellValue());
				
				//选项C、D可以为空；为空说明是判断题
				question.setAttrC(row.getCell(5)==null?"":row.getCell(5).getStringCellValue());
				question.setAttrD(row.getCell(6)==null?"":row.getCell(6).getStringCellValue());
				if(row.getCell(7)==null) {
					sBuilder.append("第"+rowIndex+"行,正确答案位空，跳过<br/>");
					continue;
				}
				question.setAnswer(row.getCell(7).getStringCellValue());
				question.setSubjectId(subjectId);
				if(questionServiceImpl.add(question)<=0) {
					sBuilder.append("第"+rowIndex+"行,插入失败<br/>");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sBuilder.toString();
	}
}
