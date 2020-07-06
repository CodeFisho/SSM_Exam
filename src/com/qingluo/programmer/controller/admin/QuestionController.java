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

//���������
@Controller
@RequestMapping("/admin/question")
public class QuestionController {

	@Autowired 
	private IQuestionService questionServiceImpl;
	
	@Autowired
	private ISubjectService subjectServiceImpl;
	
	//�����б�ҳ��
	@GetMapping("/list")
	public ModelAndView list(ModelAndView model) {
		Map<String, Object> queryMap=new HashMap<String, Object>();
		queryMap.put("pageSize", 99999);
		queryMap.put("offset",0 );
		model.addObject("subjectList", subjectServiceImpl.findList(queryMap));
		model.setViewName("question/list");
		return model;
	}
	
	  //����������Ϣ�б�
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
			 resCode.put("msg", "�������������");
			 return resCode;
		 }
		 if(StringUtils.isEmpty(question.getTitle())) {
			 resCode.put("type", "error");
			 resCode.put("msg", "����д������Ŀ");
			 return resCode;
		 }
		 if(StringUtils.isEmpty(question.getAnswer())) {
			 resCode.put("type", "error");
			 resCode.put("msg", "����д��ȷ��");
			 return resCode;
		 }
		 if(StringUtils.isEmpty(question.getAttrA())) {
			 resCode.put("type", "error");
			 resCode.put("msg", "����д����ѡ��A��");
			 return resCode;
		 }
		 if(StringUtils.isEmpty(question.getAttrB())) {
			 resCode.put("type", "error");
			 resCode.put("msg", "����д����ѡ��B");
			 return resCode;
		 }
		 question.setScoreByType();
		 if(questionServiceImpl.add(question)<=0){
			 resCode.put("type", "error");
			 resCode.put("msg", "�������ʧ�ܣ�����ϵ����Ա");
			 return resCode;
		 }
		 resCode.put("type", "success");
		 resCode.put("msg", "������ӳɹ�");
		 return resCode;
	}
	
	@PostMapping("/edit")
	@ResponseBody
	public Map<String, String> edit(Question question){
		Map<String,String> resCode=new HashMap<String, String>();
		 if(question==null) {
			 resCode.put("type", "error");
			 resCode.put("msg", "�������������");
			 return resCode;
		 }
		 if(StringUtils.isEmpty(question.getTitle())) {
			 resCode.put("type", "error");
			 resCode.put("msg", "����д������Ŀ");
			 return resCode;
		 }
		 if(StringUtils.isEmpty(question.getAnswer())) {
			 resCode.put("type", "error");
			 resCode.put("msg", "����д��ȷ��");
			 return resCode;
		 }
		 if(StringUtils.isEmpty(question.getAttrA())) {
			 resCode.put("type", "error");
			 resCode.put("msg", "����д����ѡ��A��");
			 return resCode;
		 }
		 if(StringUtils.isEmpty(question.getAttrB())) {
			 resCode.put("type", "error");
			 resCode.put("msg", "����д����ѡ��B");
			 return resCode;
		 }
		 question.setScoreByType();
		 if(questionServiceImpl.edit(question)<=0){
			 resCode.put("type", "error");
			 resCode.put("msg", "�����޸�ʧ�ܣ�����ϵ����Ա");
			 return resCode;
		 }
		 resCode.put("type", "success");
		 resCode.put("msg", "�����޸ĳɹ�");
		 return resCode;
	}
	
	@PostMapping("/delete")
	@ResponseBody
	public Map<String, String> delete(Long id){
		Map<String,String> resCode=new HashMap<String, String>();
		if(id==null) {
			resCode.put("type", "error");
			resCode.put("msg","��ѡ����Ҫɾ��������");
			return resCode;
		}
		try {
			if(questionServiceImpl.delete(id)<=0) {
				resCode.put("type", "error");
				resCode.put("msg","ɾ��ʧ�ܣ�����ϵ����Ա");
				return resCode;
			}
		}catch (Exception e) {
			resCode.put("type", "error");
			resCode.put("msg","�޷�ɾ��������");
			return resCode;
		}
		resCode.put("type", "success");
		resCode.put("msg","ɾ��������Ϣ�ɹ�");
		return resCode;
	}
	
	//�ϴ�excel���Ҵ���
	@PostMapping("/uploap_file")
	@ResponseBody
	private Map<String, String> uploadExcelFile(MultipartFile excelFile,Long subjectId){
		Map<String, String> resCode=new HashMap<String, String>();
		if(excelFile==null) {
			resCode.put("type", "error");
			resCode.put("msg","��ѡ����Ҫ�ϴ����ļ�");
			return resCode;
		}
		if(subjectId==null) {
			resCode.put("type", "error");
			resCode.put("msg","��ѡ������������Ŀ");
			return resCode;
		}
		//����ļ���С
		if(excelFile.getSize()>1024*1024*5) {
			resCode.put("type", "error");
			resCode.put("msg","�ļ�����");
			return resCode;
		}
		int start=excelFile.getOriginalFilename().lastIndexOf(".")+1;
		int end=excelFile.getOriginalFilename().length();
		//��ȡ��׺��
		String suffix=excelFile.getOriginalFilename().substring(start, end);
		if(!"xls,xlsx".contains(suffix)) {
			resCode.put("type", "error");
			resCode.put("msg","��ѡ��excel�ļ�");
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
			msg="ȫ������ɹ�";
		}
		resCode.put("type", "success");
		resCode.put("msg",msg);
		return resCode;
	}
	
	//��ȡ���ʸ�ʽ��excel�ļ��������ݲ��뵽���ݿ���
	private String readExcel(InputStream fileInputStream,Long subjectId) {
		StringBuilder sBuilder=new StringBuilder();
		try {
			HSSFWorkbook questionWorkbook=new HSSFWorkbook(fileInputStream);
			HSSFSheet sheetAt=questionWorkbook.getSheetAt(0);
			if(sheetAt.getLastRowNum()<=0) {
				sBuilder.append("���ļ�Ϊ��!");
			}
			for(int rowIndex=1;rowIndex<=sheetAt.getLastRowNum();rowIndex++) {
				Question question=new Question();
				HSSFRow row =sheetAt.getRow(rowIndex);
				if(row.getCell(0)==null) {
					sBuilder.append("��"+rowIndex+"��,��������Ϊ�գ�����<br/>");
					continue;
				}
				Double questionType=row.getCell(0).getNumericCellValue();
				question.setQuestionType(questionType.intValue());
				
				if(row.getCell(1)==null) {
					sBuilder.append("��"+rowIndex+"��,��ĿΪ�գ�����<br/>");
					continue;
				}
				question.setTitle(row.getCell(1).getStringCellValue());
				
				if(row.getCell(2)==null) {
					sBuilder.append("��"+rowIndex+"��,��ֵΪ�գ�����<br/>");
					continue;
				}
				Double score=row.getCell(2).getNumericCellValue();
				question.setScore(score.intValue());
						
				if(row.getCell(3)==null) {
					sBuilder.append("��"+rowIndex+"��,ѡ��AΪ�գ�����<br/>");
					continue;
				}
				question.setAttrA(row.getCell(3).getStringCellValue());
				
				if(row.getCell(4)==null) {
					sBuilder.append("��"+rowIndex+"��,ѡ��BΪ�գ�����<br/>");
					continue;
				}
				question.setAttrB(row.getCell(4).getStringCellValue());
				
				//ѡ��C��D����Ϊ�գ�Ϊ��˵�����ж���
				question.setAttrC(row.getCell(5)==null?"":row.getCell(5).getStringCellValue());
				question.setAttrD(row.getCell(6)==null?"":row.getCell(6).getStringCellValue());
				if(row.getCell(7)==null) {
					sBuilder.append("��"+rowIndex+"��,��ȷ��λ�գ�����<br/>");
					continue;
				}
				question.setAnswer(row.getCell(7).getStringCellValue());
				question.setSubjectId(subjectId);
				if(questionServiceImpl.add(question)<=0) {
					sBuilder.append("��"+rowIndex+"��,����ʧ��<br/>");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sBuilder.toString();
	}
}
