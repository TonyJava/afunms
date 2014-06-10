package com.afunms.detail.service.userInfo;

import java.util.List;
import java.util.Vector;

import com.afunms.temp.dao.UserTempDao;

@SuppressWarnings( { "unchecked", "unused" })
public class UserInfoService {
	private String type;

	private String subtype;

	private String nodeid;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public String getNodeid() {
		return nodeid;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public UserInfoService(String nodeid, String type, String subtype) {
		super();
		this.type = type;
		this.subtype = subtype;
		this.nodeid = nodeid;
	}

	/**
	 * �õ��û���Ϣ
	 * 
	 * @return
	 */
	public Vector getUserInfo() {
		Vector retVector = null;
		UserTempDao userTempDao = null;
		try {
			userTempDao = new UserTempDao();
			retVector = userTempDao.getUserInfo(nodeid, type, subtype);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (userTempDao != null) {
				userTempDao.close();
			}
		}
		return retVector;
	}

	/**
	 * <p>
	 * �õ�userlist ��������Ϊ List<Hashtable<String,String>
	 * </p>
	 * <p>
	 * windows wmi
	 * </p>
	 * 
	 * @return
	 */
	public List getUserInfoList() {
		List retList = null;
		UserTempDao userTempDao = null;
		try {
			userTempDao = new UserTempDao();
			retList = userTempDao.getUserInfoList(nodeid, type, subtype);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (userTempDao != null) {
				userTempDao.close();
			}
		}
		return retList;
	}
}
