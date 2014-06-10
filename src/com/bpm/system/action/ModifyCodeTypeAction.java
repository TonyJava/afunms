package com.bpm.system.action;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.system.service.SystemService;

@Controller
@Scope("prototype")
public class ModifyCodeTypeAction extends BaseAction {

	private String keytext;
	private String modelid;
	@Resource
	private SystemService systemService;
	private String result;

	@Override
	public String execute() throws Exception {
		result = systemService.modifyCodeType(keytext, modelid);
		return SUCCESS;
	}

	public String getKeytext() {
		return keytext;
	}

	public void setKeytext(String keytext) {
		this.keytext = keytext;
	}

	public String getModelid() {
		return modelid;
	}

	public void setModelid(String modelid) {
		this.modelid = modelid;
	}

	public SystemService getSystemService() {
		return systemService;
	}

	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
