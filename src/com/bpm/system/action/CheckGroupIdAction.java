package com.bpm.system.action;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.system.service.SystemService;

@Controller
@Scope("prototype")
public class CheckGroupIdAction extends BaseAction {

	private String result; 
	private String groupId; 
	@Resource
	private SystemService systemService;

	@Override
	public String execute() throws Exception {

		result = null == systemService.getGroupById(groupId) ? "success" : "error";
		return SUCCESS;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

}
