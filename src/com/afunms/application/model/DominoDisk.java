
package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class DominoDisk extends BaseVo {
	private String diskname = "";// ����
	private String disksize = "";// ��С
	private String diskfree = "";// ����
	private String diskusedpctutil = "";// ʹ����
	private String disktype = "";// ����

	public DominoDisk() {
		diskname = "";
		disksize = "";
		diskfree = "";
		diskusedpctutil = "";
		disktype = "";
	}

	public String getDiskfree() {
		return diskfree;
	}

	public String getDiskname() {
		return diskname;
	}

	public String getDisksize() {
		return disksize;
	}

	public String getDisktype() {
		return disktype;
	}

	public String getDiskusedpctutil() {
		return diskusedpctutil;
	}

	public void setDiskfree(String diskfree) {
		this.diskfree = diskfree;
	}

	public void setDiskname(String diskname) {
		this.diskname = diskname;
	}

	public void setDisksize(String disksize) {
		this.disksize = disksize;
	}

	public void setDisktype(String disktype) {
		this.disktype = disktype;
	}

	public void setDiskusedpctutil(String diskusedpctutil) {
		this.diskusedpctutil = diskusedpctutil;
	}

}