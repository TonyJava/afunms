/**
 * <p>Description:host,including server and exchange device</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-27
 */

package com.afunms.polling.node;


public class Interface extends Application {

	private int id;

	private int fid;

	private String desc;

	private int collecttype;

	private String method;

	private String name;

	private int flag;

	@Override
	public int getCollecttype() {
		return collecttype;
	}

	public String getDesc() {
		return desc;
	}

	public int getFid() {
		return fid;
	}

	public int getFlag() {
		return flag;
	}

	@Override
	public int getId() {
		return id;
	}

	public String getMethod() {
		return method;
	}

	public String getName() {
		return name;
	}

	@Override
	public void setCollecttype(int collecttype) {
		this.collecttype = collecttype;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setFid(int fid) {
		this.fid = fid;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setName(String name) {
		this.name = name;
	}

}