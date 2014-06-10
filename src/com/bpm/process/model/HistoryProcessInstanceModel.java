package com.bpm.process.model;
/**
 * 历史流程实例MODEL，便于处理数据
 * @author HXL
 * 2012-12-13
 */
public class HistoryProcessInstanceModel {

	
	private String startUser;//发起人
	private String status;//处理状态
	private String startTime;//启动事件
	private String processInstanceId;//流程实例ID
	private String processDefinitionId;//流程定义ID
	public String getStartUser() {
		return startUser;
	}
	public void setStartUser(String startUser) {
		this.startUser = startUser;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}
	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}
	
	
	
}
