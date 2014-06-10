package com.bpm.system.action;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.afunms.system.model.Codedetail;
import com.bpm.system.service.SystemService;

@Controller
@Scope("prototype")
public class CodeDetailQueryAction extends BaseAction {

	private List<Codedetail> list;
	private String typeId;
	@Resource
	private SystemService systemService;

	@Override
	public String execute() throws Exception {
		list = systemService.loadCodedetail(typeId);
		return SUCCESS;
	}

	public List<Codedetail> getList() {
		return list;
	}

	public void setList(List<Codedetail> list) {
		this.list = list;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public SystemService getSystemService() {
		return systemService;
	}

	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}

}
