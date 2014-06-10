package com.bpm.system.action;


import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.system.service.SystemService;
@Controller
@Scope("prototype")
public class CheckUserIdAction extends BaseAction {

	private String result; 
	private String userId; 
	@Resource
	private SystemService systemService;
	
	@Override
	public String execute() throws Exception {
		result = null==systemService.getUserById(userId)?"success":"error";
		return SUCCESS;
	}

	
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
