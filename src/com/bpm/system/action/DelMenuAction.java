package com.bpm.system.action;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.system.service.SystemService;

@Controller
@Scope("prototype")
public class DelMenuAction extends BaseAction {

	private String result;
	private String checkbox[];
	@Resource
	private SystemService systemService;

	@Override
	public String execute() throws Exception {
		result = systemService.deleteOneLevelMenuByIds(checkbox);
		return result;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String[] getCheckbox() {
		return checkbox;
	}

	public void setCheckbox(String[] checkbox) {
		this.checkbox = checkbox;
	}

}
