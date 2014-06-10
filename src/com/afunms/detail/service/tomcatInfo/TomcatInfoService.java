package com.afunms.detail.service.tomcatInfo;

import java.util.Hashtable;

import com.afunms.application.dao.TomcatDao;

@SuppressWarnings( { "unchecked", "unused" })
public class TomcatInfoService {

	/**
	 * <p>
	 * �����ݿ��л�ȡtomcat�Ĳɼ���Ϣ
	 * </p>
	 * 
	 * @param nodeid
	 * @return
	 */
	public Hashtable getTomcatDataHashtable(String nodeid) {
		Hashtable retHashtable = null;
		TomcatDao tomcatDao = new TomcatDao();
		try {
			retHashtable = tomcatDao.getTomcatDataHashtable(nodeid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tomcatDao.close();
		}
		return retHashtable;
	}
}
