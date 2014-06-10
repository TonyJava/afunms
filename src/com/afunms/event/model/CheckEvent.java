/**
 * <p>Description:mapping table NMS_POSITION</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.event.model;

import com.afunms.common.base.BaseVo;

public class CheckEvent extends BaseVo implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5806773346351302257L;

	/**
	 * @return the serialVersionUID
	 */
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	/**
	 * 设备id
	 */
	private String nodeid;

	/**
	 * 指标名称
	 */
	private String indicatorsName;

	/**
	 * sIndex
	 */
	private String sindex;

	/**
	 * 类型
	 */
	private String type;

	/**
	 * 子类型
	 */
	private String subtype;

	/**
	 * 告警等级
	 */
	private int alarmlevel;

	/**
	 * 告警内容
	 */
	private String content;
	/**
	 * 告警值
	 */
	private String thevalue;

	/**
	 * 所属业务
	 */
	private String bid;

	/**
	 * 拓扑图显示
	 */
	private String collecttime;

	/**
	 * @return the alarmlevel
	 */
	public int getAlarmlevel() {
		return alarmlevel;
	}

	public String getBid() {
		return bid;
	}

	public String getCollecttime() {
		return collecttime;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @return the indicatorsName
	 */
	public String getIndicatorsName() {
		return indicatorsName;
	}

	/**
	 * @return the nodeid
	 */
	public String getNodeid() {
		return nodeid;
	}

	/**
	 * @return the sindex
	 */
	public String getSindex() {
		return sindex;
	}

	/**
	 * @return the subtype
	 */
	public String getSubtype() {
		return subtype;
	}

	/**
	 * @return the thevalue
	 */
	public String getThevalue() {
		return thevalue;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param alarmlevel
	 *            the alarmlevel to set
	 */
	public void setAlarmlevel(int alarmlevel) {
		this.alarmlevel = alarmlevel;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public void setCollecttime(String collecttime) {
		this.collecttime = collecttime;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @param indicatorsName
	 *            the indicatorsName to set
	 */
	public void setIndicatorsName(String indicatorsName) {
		this.indicatorsName = indicatorsName;
	}

	/**
	 * @param nodeid
	 *            the nodeid to set
	 */
	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	/**
	 * @param sindex
	 *            the sindex to set
	 */
	public void setSindex(String sindex) {
		this.sindex = sindex;
	}

	/**
	 * @param subtype
	 *            the subtype to set
	 */
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	/**
	 * @param thevalue
	 *            the thevalue to set
	 */
	public void setThevalue(String thevalue) {
		this.thevalue = thevalue;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}
