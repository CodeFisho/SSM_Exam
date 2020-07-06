package com.qingluo.programmer.entity.admin;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

//角色实体类

@Component

public class Role {
	private Long id;//角色id
	
	private String name;//角色名称
	
	private String remark;//角色备注

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
