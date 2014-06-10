/**
 * <p>Description: nms_symantec记录</p>
 * <p>Company: 北京东华合创数码科技股份有限公司</p>
 * @author 王福民
 * @project 阿福网管
 * @date 2005-3-14
 */

package com.afunms.security.model;

public class Symantec {
	private String beginDate;
	private String machine;
	private String machineIp;
	private String virus;
	private String virusFile;
	private String dealWay;

	public String getBeginDate() {
		return beginDate;
	}

	public String getDealWay() {
		return dealWay;
	}

	public String getMachine() {
		return machine;
	}

	public String getMachineIp() {
		return machineIp;
	}

	public String getVirus() {
		return virus;
	}

	public String getVirusFile() {
		return virusFile;
	}

	public void setBegintime(String beginDate) {
		this.beginDate = beginDate;
	}

	public void setDealWay(String newDealWay) {
		dealWay = newDealWay;
	}

	public void setMachine(String newMachine) {
		machine = newMachine;
	}

	public void setMachineIp(String newMachineIp) {
		machineIp = newMachineIp;
	}

	public void setVirus(String newVirus) {
		virus = newVirus;
	}

	public void setVirusFile(String newVirusFile) {
		virusFile = newVirusFile;
	}
}
