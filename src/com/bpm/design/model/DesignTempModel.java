package com.bpm.design.model;

import java.io.Serializable;
public class DesignTempModel implements Serializable {
	private String id;
	private String modelid;
	private String name;
	private String svgxml;
	private String bpmxml;
	private String modeldesc;
	private String typename;
	private String keytext;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getModelid() {
		return modelid;
	}
	public void setModelid(String modelid) {
		this.modelid = modelid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSvgxml() {
		return svgxml;
	}
	public void setSvgxml(String svgxml) {
		this.svgxml = svgxml;
	}
	public String getBpmxml() {
		return bpmxml;
	}
	public void setBpmxml(String bpmxml) {
		this.bpmxml = bpmxml;
	}
	public String getModeldesc() {
		return modeldesc;
	}
	public void setModeldesc(String modeldesc) {
		this.modeldesc = modeldesc;
	}
	public String getTypename() {
		return typename;
	}
	public void setTypename(String typename) {
		this.typename = typename;
	}
	public String getKeytext() {
		return keytext;
	}
	public void setKeytext(String keytext) {
		this.keytext = keytext;
	}
	
	
}
