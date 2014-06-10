package com.afunms.sysset.model;

import com.afunms.common.base.BaseVo;

public class DBBackup extends BaseVo {
	private int id;
	private String filename;
	private String time;

	public DBBackup() {
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param filename
	 *            the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}

}
