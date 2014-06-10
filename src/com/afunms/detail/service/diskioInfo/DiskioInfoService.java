package com.afunms.detail.service.diskioInfo;

import java.util.List;

import com.afunms.temp.dao.DiskioTempDao;

@SuppressWarnings("unchecked")
public class DiskioInfoService {

	private String type;

	private String subtype;

	private String nodeid;

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public DiskioInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}

	public DiskioInfoService(String nodeid) {
		super();
		this.nodeid = nodeid;
	}

	public List getDiskiolistInfo() {
		List diskioInfoList = null;
		DiskioTempDao diskPeriofTempDao = new DiskioTempDao();
		try {
			diskioInfoList = diskPeriofTempDao.getdiskiolistInfo(nodeid, type, subtype);
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			diskPeriofTempDao.close();
		}
		return diskioInfoList;
	}
}
