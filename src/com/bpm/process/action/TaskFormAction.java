package com.bpm.process.action;

/**
 *  Description:
 * 根据taskId，查看流程的form表单
 * @author ywx
 *
 */
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.form.FormProperty;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.process.service.ProcessService;
import com.opensymphony.xwork2.ActionSupport;

@Controller
@Scope("prototype")
@SuppressWarnings("unchecked")
public class TaskFormAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2340817004314597758L;
	@Resource
	private ProcessService processService;
	private String taskId;
	private String taskName;
	private String taskExtId;
	private String pdId;
	private List<FormProperty> fpList;
	private Map formMap;
	private List<ActivityImpl> backAIList;

	@Override
	public String execute() throws Exception {
		System.out.println(taskName);
		fpList = processService.findFpList(taskId);
		for (FormProperty fp : fpList) {
			System.out.println(fp.getName());
		}
		formMap = processService.findFormMap(taskExtId);
		backAIList = processService.findBackAIList(taskId, pdId, taskExtId);
		return SUCCESS;
	}

	public List<ActivityImpl> getBackAIList() {
		return backAIList;
	}

	public Map getFormMap() {
		return formMap;
	}

	public List<FormProperty> getFpList() {
		return fpList;
	}

	public String getPdId() {
		return pdId;
	}

	public String getTaskExtId() {
		return taskExtId;
	}

	public String getTaskId() {
		return taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setBackAIList(List<ActivityImpl> backAIList) {
		this.backAIList = backAIList;
	}

	public void setFormMap(Map formMap) {
		this.formMap = formMap;
	}

	public void setFpList(List<FormProperty> fpList) {
		this.fpList = fpList;
	}

	public void setPdId(String pdId) {
		this.pdId = pdId;
	}

	public void setTaskExtId(String taskExtId) {
		this.taskExtId = taskExtId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

}
