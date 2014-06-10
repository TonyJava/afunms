package com.bpm.system.action;

import java.util.List;

import javax.annotation.Resource;

import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.system.service.SystemService;

@Controller
@Scope("prototype")
public class UserQueryAction extends BaseAction {

	private List<UserEntity> list;
	private List<GroupEntity> group;
	private String result;
	@Resource
	private SystemService systemService;

	@Override
	public String execute() throws Exception {
		list = systemService.queryUser();
		group = systemService.queryAllGroup();
		return SUCCESS;
	}

	public List<UserEntity> getList() {
		return list;
	}

	public void setList(List<UserEntity> list) {
		this.list = list;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public List<GroupEntity> getGroup() {
		return group;
	}

	public void setGroup(List<GroupEntity> group) {
		this.group = group;
	}

}
