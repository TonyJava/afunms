package com.bpm.system.action;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.system.service.SystemService;
import com.opensymphony.xwork2.ActionSupport;

@Controller
@Scope("prototype")
public class FormDeleteAction extends ActionSupport {
	@Resource
	private SystemService systemService;
	private String[] checkbox;
	private String result;

	public String[] getCheckbox() {
		return checkbox;
	}

	@Override
	public String execute() throws Exception {
		result = systemService.deleteForm(checkbox);
		return result;
	}

	public void setCheckbox(String[] checkbox) {
		this.checkbox = checkbox;
	}

}
