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
	 * �豸id
	 */
	private String nodeid;

	/**
	 * ָ������
	 */
	private String indicatorsName;

	/**
	 * sIndex
	 */
	private String sindex;

	/**
	 * ����
	 */
	private String type;

	/**
	 * ������
	 */
	private String subtype;

	/**
	 * �澯�ȼ�
	 */
	private int alarmlevel;

	/**
	 * �澯����
	 */
	private String content;
	/**
	 * �澯ֵ
	 */
	private String thevalue;

	/**
	 * ����ҵ��
	 */
	private String bid;

	/**
	 * ����ͼ��ʾ
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
