package com.qingluo.programmer.util;

import java.util.ArrayList;
import java.util.List;

import com.qingluo.programmer.entity.admin.ExamPaperAnswer;

public class QuestionUtils {
	public static List<ExamPaperAnswer> getNeedQuestionList(List<ExamPaperAnswer> examPaperAnswers,int questionType){
		List<ExamPaperAnswer> needQuestionList=new ArrayList<ExamPaperAnswer>();
		for(ExamPaperAnswer answer:examPaperAnswers) {
			if(answer.getQuestion().getQuestionType()==questionType) {
				needQuestionList.add(answer);
			}
		}
		return needQuestionList;
	}

}
