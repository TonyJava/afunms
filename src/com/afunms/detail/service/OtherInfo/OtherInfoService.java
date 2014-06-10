package com.afunms.detail.service.OtherInfo;

import java.util.Hashtable;
import java.util.List;

import com.afunms.temp.dao.OthersTempDao;

@SuppressWarnings( { "unchecked", "unused" })
public class OtherInfoService {

	private String type;

	private String subtype;

	private String nodeid;

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public OtherInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}

	/**
	 * 得到数据采集时间
	 * 
	 * @return
	 */
	public String getCollecttime() {
		String collecttime = null;
		OthersTempDao othersTempDao = new OthersTempDao();
		try {
			collecttime = othersTempDao.getCollecttime(nodeid, type, subtype);
		} catch (Exception e) {

		} finally {
			if (othersTempDao != null) {
				othersTempDao.close();
			}
		}
		return collecttime;
	}

	/**
	 * 得到Paging Space利用率信息
	 * 
	 * @return
	 */
	public Hashtable getPaginghash() {
		Hashtable paginghash = null;
		OthersTempDao othersTempDao = new OthersTempDao();
		try {
			paginghash = othersTempDao.getPaginghash(nodeid, type, subtype);
		} catch (Exception e) {

		} finally {
			if (othersTempDao != null) {
				othersTempDao.close();
			}
		}
		return paginghash;
	}

	/**
	 * 页面信息
	 * 
	 * @return
	 */
	public Hashtable getPagehash() {
		Hashtable pagehash = null;
		OthersTempDao othersTempDao = new OthersTempDao();
		try {
			pagehash = othersTempDao.getPagehash(nodeid, type, subtype);
		} catch (Exception e) {

		} finally {
			if (othersTempDao != null) {
				othersTempDao.close();
			}
		}
		return pagehash;
	}

	/**
	 * 获取nms_other_data_temp中集合类型为List<Hashtable<String,String>> 的信息
	 * 
	 * @param entity
	 *            类别 如:cpuconfig
	 * @return
	 */
	public List getlistInfo(String entity) {
		List retList = null;
		OthersTempDao othersTempDao = null;
		try {
			othersTempDao = new OthersTempDao();
			retList = othersTempDao.getlistInfo(nodeid, type, subtype, entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (othersTempDao != null) {
				othersTempDao.close();
			}
		}
		return retList;
	}

	/**
	 * 获取nms_other_data_temp中集合类型为Hashtable<String,String> 的信息
	 * 
	 * @param entity
	 *            类别 如：memoryconfig
	 * @return
	 */
	public Hashtable getHashInfo(String entity) {
		Hashtable retHash = null;
		OthersTempDao othersTempDao = null;
		try {
			othersTempDao = new OthersTempDao();
			retHash = othersTempDao.getHashInfo(nodeid, type, subtype, entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (othersTempDao != null) {
				othersTempDao.close();
			}
		}
		return retHash;
	}
}
