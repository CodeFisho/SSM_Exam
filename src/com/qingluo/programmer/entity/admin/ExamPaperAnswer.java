package com.qingluo.programmer.entity.admin;

import java.util.List;

import org.springframework.stereotype.Component;
//�Ծ������Ϣʵ��

@Component
public class ExamPaperAnswer {

	private Long id;
	private Long studentId;//����ѧ��ID
	private Long examId;//��������
	private Long examPaperId;//�����Ծ�ID
	private long questionId;
	private String answer;//�ύ��
	private int isCorrect;//�Ƿ���ȷ
	private Question question;//���⼯��
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getStudentId() {
		return studentId;
	}
	public void setStudentId(Long studentId) {
		this.studentId = studentId;
	}
	public Long getExamPaperId() {
		return examPaperId;
	}
	public void setExamPaperId(Long examPaperId) {
		this.examPaperId = examPaperId;
	}
	public long getQuestionId() {
		return questionId;
	}
	public void setQuestionId(long questionId) {
		this.questionId = questionId;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public int getIsCorrect() {
		return isCorrect;
	}
	public void setIsCorrect(int isCorrect) {
		this.isCorrect = isCorrect;
	}
	public Long getExamId() {
		return examId;
	}
	public void setExamId(Long examId) {
		this.examId = examId;
	}
	public Question getQuestion() {
		return question;
	}
	public void setQuestion(Question question) {
		this.question = question;
	}
	

	
}
