package com.afunms.vmware.vim25.mgr;

import com.afunms.vmware.vim25.common.VIMMgr;
import com.afunms.vmware.vim25.constants.NetworkConstants;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.vmware.apputils.version.ExtendedAppUtil;
import com.vmware.vim25.ManagedEntityStatus;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.NetworkSummary;

/**
 * 网络管理,<br>
 * 
 * 所有操作的列表参见<br>
 * http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.
 * doc_50%2Fvim.Network.html中的描述<br>
 * 
 * @author LXL
 * 
 */
public class NetworkMgr extends VIMMgr implements NetworkConstants {

	// LOGGER
	private final static Logger LOGGER = Logger.getLogger(NetworkMgr.class);

	/**
	 * 记录网络列表
	 * 
	 * @param resultMap
	 * @param ecb
	 * @param netList
	 */
	public static void recordResultMapNetList(
			HashMap<String, Object> resultMap, ExtendedAppUtil ecb,
			ManagedObjectReference[] netList) {
		try {
			// 网络列表
			ArrayList<HashMap<String, String>> netResultList = new ArrayList<HashMap<String, String>>();
			resultMap.put(SUMMARY_RESOURCE_NET_LIST, netResultList);
			if (netList != null) {
				for (ManagedObjectReference netMor : netList) {
					ManagedEntityStatus overallStatus = (ManagedEntityStatus) VIMMgr
							.getDynamicProperty(ecb, netMor,
									DYNAMICPROPERTY_OVERALLSTATUS);
					Boolean alarmActionsEnabled = (Boolean) VIMMgr
							.getDynamicProperty(ecb, netMor,
									DYNAMICPROPERTY_ALARMACTIONSENABLED);
					NetworkSummary netSummary = (NetworkSummary) VIMMgr
							.getDynamicProperty(ecb, netMor,
									DYNAMICPROPERTY_SUMMARY);

					// 网络列表中的Map的key
					HashMap<String, String> netResultMap = new HashMap<String, String>();
					netResultList.add(netResultMap);

					// 状态
					if (overallStatus != null) {
						netResultMap.put(SUMMARY_RESOURCE_NET_STATUS,
								overallStatus.getValue());
					} else {
						netResultMap.put(SUMMARY_RESOURCE_NET_STATUS, "");
					}
					// 警报操作
					if (alarmActionsEnabled != null) {
						netResultMap.put(SUMMARY_RESOURCE_NET_ALARMACTIONS,
								alarmActionsEnabled.toString());
					} else {
						netResultMap.put(SUMMARY_RESOURCE_NET_ALARMACTIONS, "");
					}

					// 网络
					if (netSummary != null) {
						netResultMap.put(SUMMARY_RESOURCE_NET_NAME, netSummary
								.getName());
					} else {
						netResultMap.put(SUMMARY_RESOURCE_NET_NAME, "");

					}
					// XXX 类型,暂时无法获取
					// String SUMMARY_RESOURCE_NET_TYPE = "nettype";
				}
			}
		} catch (Exception e) {
			LOGGER.error("recordResultMapNetList error, ", e);
		}
	}
}
