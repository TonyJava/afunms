package com.afunms.vmware.vim25.common;

import java.util.HashMap;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.vmware.apputils.version.ExtendedAppUtil;

/**
 * VMWare连接管理
 * 
 * @author LXL
 * 
 */
public class VIMConnection {

	// LOGGER
	private final static Logger LOGGER = Logger.getLogger(VIMConnection.class);

	// 连接的缓存
	private static HashMap<String, ExtendedAppUtil> ecbMap = new HashMap<String, ExtendedAppUtil>();

	/**
	 * 获取公共参数
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	public static String[] getVMArgs(String url, String username,
			String password) {
		String[] vmargs = { "--url", url, "--username", username, "--password",
				password, "--ignorecert", "ignorecert", };
		return vmargs;
	}

	/**
	 * 获取ExtendedAppUtil存储的Key
	 * 
	 * @param url
	 * @return
	 */
	protected static String getVIMKey(String url, String username) {
		return url + username;
	}

	/**
	 * 获取ExtendedAppUtil,并根据条件是否连接服务器
	 * 
	 * @param name
	 * @param url
	 * @param username
	 * @param password
	 * @param isConnect
	 * @return
	 */
	private static ExtendedAppUtil getECB(String url, String username,
			String password, boolean isConnect) {

		ExtendedAppUtil ecb = null;

		try {
			LOGGER.info("getECB, url='" + url + "', username='" + username
					+ "', password='" + password + "', isConnect='" + isConnect
					+ "'");

			if (ecbMap.containsKey(getVIMKey(url, username))) {
				ecb = ecbMap.get(getVIMKey(url, username));
			} else {
				ecb = ExtendedAppUtil.initialize(UUID.randomUUID().toString(),
						getVMArgs(url, username, password));
				if (ecb != null) {
					ecbMap.put(getVIMKey(url, username), ecb);
				}
			}

			// ecb创建连接
			if (isConnect) {
				if (!ecb.getConnection().isConnected()) {
					ecb.connect();
				}
			}
		} catch (Exception e) {
			// 释放连接
			releaseECB(url, username);

			LOGGER.error("getECB error, ", e);
		}

		return ecb;
	}

	/**
	 * 获取ExtendedAppUtil,并连接服务器
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	public static ExtendedAppUtil getECB(String url, String username,
			String password) {
		return getECB(url, username, password, true);
	}

	/**
	 * 获取ExtendedAppUtil,不连接服务器
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	public static ExtendedAppUtil getECBNotConnected(String url,
			String username, String password) {
		return getECB(url, username, password, false);
	}

	/**
	 * 释放ExtendedAppUtil连接
	 * 
	 * @param ecb
	 */
	public static void releaseECB(String url, String username) {
		try {
			if (ecbMap.containsKey(getVIMKey(url, username))) {
				ExtendedAppUtil ecb = ecbMap.remove(getVIMKey(url, username));
				if (ecb != null) {
					ecb.disConnect();
				}
				ecb = null;
			}
		} catch (Exception e) {
			LOGGER.error("releaseECB error,", e);
		}
	}
}
