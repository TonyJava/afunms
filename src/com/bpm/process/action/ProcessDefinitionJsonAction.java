package com.bpm.process.action;

import java.util.List;

import javax.annotation.Resource;

import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.process.service.ProcessService;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 获取流程定义列表
 * 
 * @author HXL
 * 
 */
@Controller
@Scope("prototype")
public class ProcessDefinitionJsonAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2462598158739580730L;
	private List<ProcessDefinition> pdList;
	@Resource
	private ProcessService processService;

	@Override
	public String execute() throws Exception {
		pdList = processService.findPdList();
		return SUCCESS;
	}

	public List<ProcessDefinition> getPdList() {
		return pdList;
	}

	public void setPdList(List<ProcessDefinition> pdList) {
		this.pdList = pdList;
	}

}
