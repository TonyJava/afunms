package com.bpm.system.action;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.system.service.SystemService;

 
@Controller
@Scope("prototype")
public class UserDelAction extends BaseAction {

	private String checkbox[];
	private String result;
	@Resource
	private SystemService systemService;

	@Override
	public String execute() throws Exception {

		return result = systemService.deleteUsers(checkbox);
	}

	public String[] getCheckbox() {
		return checkbox;
	}

	public String getResult() {
		return result;
	}

	public void setCheckbox(String[] checkbox) {
		this.checkbox = checkbox;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
