package com.afunms.detail.service.mqInfo;

import java.util.Hashtable;

import com.afunms.application.dao.MQConfigDao;

@SuppressWarnings( { "unchecked", "unused" })
public class MQInfoService {

	/**
	 * <p>
	 * 从数据库中获取mq的采集信息
	 * </p>
	 * 
	 * @param nodeid
	 * @return
	 */
	public Hashtable getMQDataHashtable(String nodeid) {
		Hashtable retHashtable = null;
		MQConfigDao mqConfigDao = new MQConfigDao();
		try {
			retHashtable = mqConfigDao.getMQDataHashtable(nodeid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mqConfigDao.close();
		}
		return retHashtable;
	}
}
