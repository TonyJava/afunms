package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class GrapesConfig extends BaseVo {

	private int id;
	private String name;
	private String ipaddress;
	private String supperdir;
	private String subdir;
	private String subfilesum;
	private int filesize;
	private String sendmobiles;
	private int mon_flag;
	private String netid;
	private String sendemail;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public String getSupperdir() {
		return supperdir;
	}

	public void setSupperdir(String supperdir) {
		this.supperdir = supperdir;
	}

	public String getSubdir() {
		return subdir;
	}

	public void setSubdir(String subdir) {
		this.subdir = subdir;
	}

	public String getSubfilesum() {
		return subfilesum;
	}

	public void setSubfilesum(String subfilesum) {
		this.subfilesum = subfilesum;
	}

	public int getFilesize() {
		return filesize;
	}

	public void setFilesize(int filesize) {
		this.filesize = filesize;
	}

	public String getSendmobiles() {
		return sendmobiles;
	}

	public void setSendmobiles(String sendmobiles) {
		this.sendmobiles = sendmobiles;
	}

	public int getMon_flag() {
		return mon_flag;
	}

	public void setMon_flag(int mon_flag) {
		this.mon_flag = mon_flag;
	}

	public String getNetid() {
		return netid;
	}

	public void setNetid(String netid) {
		this.netid = netid;
	}

	public String getSendemail() {
		return sendemail;
	}

	public void setSendemail(String sendemail) {
		this.sendemail = sendemail;
	}

}