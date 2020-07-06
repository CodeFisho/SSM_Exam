package com.qingluo.programmer.entity.admin;
//学科专业实体类

import org.springframework.stereotype.Component;

@Component
public class Subject {
	private Long id;//学科id
	private String name;//学科名称
	private String remark;//学科备注
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
