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
	 * �õ����ݲɼ�ʱ��
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
	 * �õ�Paging Space��������Ϣ
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
	 * ҳ����Ϣ
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
	 * ��ȡnms_other_data_temp�м�������ΪList<Hashtable<String,String>> ����Ϣ
	 * 
	 * @param entity
	 *            ��� ��:cpuconfig
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
	 * ��ȡnms_other_data_temp�м�������ΪHashtable<String,String> ����Ϣ
	 * 
	 * @param entity
	 *            ��� �磺memoryconfig
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
