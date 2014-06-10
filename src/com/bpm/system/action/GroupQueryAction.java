package com.bpm.system.action;

import java.util.List;

import javax.annotation.Resource;

import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.system.service.SystemService;

@Controller
@Scope("prototype")
public class GroupQueryAction extends BaseAction {

	private List<GroupEntity> list;
	private String result;
	@Resource
	private SystemService systemService;

	@Override
	public String execute() throws Exception {
		list = systemService.queryAllGroup();
		return SUCCESS;
	}

	public List<GroupEntity> getList() {
		return list;
	}

	public String getResult() {
		return result;
	}

	public void setList(List<GroupEntity> list) {
		this.list = list;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
