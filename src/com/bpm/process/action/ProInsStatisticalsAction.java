package com.bpm.process.action;

import java.util.HashMap;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.process.service.ProcessService;
import com.bpm.system.utils.ProcessEnum;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 流程统计
 * 
 * @author ywx 2013-5-30
 */

@Controller
@Scope("prototype")
public class ProInsStatisticalsAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -629653156544219211L;
	@Resource
	private ProcessService processService;
	private HashMap<ProcessEnum, String> map;
	private String pieXml;
	private String barXml;

	@Override
	public String execute() throws Exception {
		map = processService.queryProIns();
		pieXml = processService.pieXmlProIns(map);
		barXml = processService.barXmllProIns(map);
		return SUCCESS;
	}

	public String getBarXml() {
		return barXml;
	}

	public HashMap<ProcessEnum, String> getMap() {
		return map;
	}

	public String getPieXml() {
		return pieXml;
	}

	public void setBarXml(String barXml) {
		this.barXml = barXml;
	}

	public void setMap(HashMap<ProcessEnum, String> map) {
		this.map = map;
	}

	public void setPieXml(String pieXml) {
		this.pieXml = pieXml;
	}

}
