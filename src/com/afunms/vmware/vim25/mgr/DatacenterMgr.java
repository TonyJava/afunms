package com.afunms.vmware.vim25.mgr;

import com.afunms.vmware.vim25.cache.VIMCache;
import com.afunms.vmware.vim25.common.VIMMgr;
import com.afunms.vmware.vim25.constants.DatacenterConstants;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.apputils.version.ExtendedAppUtil;
import com.vmware.vim25.ManagedObjectReference;

/**
 * 数据中心管理,<br>
 * 
 * 所有操作的列表参见<br>
 * http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.
 * doc_50%2Fvim.Datacenter.html中的描述<br>
 * 
 * @author LXL
 * 
 */
public class DatacenterMgr extends VIMMgr implements DatacenterConstants {

	// LOGGER
	private final static Logger LOGGER = Logger.getLogger(DatacenterMgr.class);

	/**
	 * 获取数据中心的摘要信息
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param dcId
	 * @return
	 */
	public static Map<String, Object> getSummary(String url, String username,
			String password, String dcId) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		try {
			VIMCache cache = VIMCache.getInstance(url, username, password);
			ManagedObjectReference mor = cache.getDatacenter(dcId);
			if (mor != null) {
				ExtendedAppUtil ecb = getECB(url, username, password);

				// 摘要Map中的key值
				// 常规
				// 主机
				int hostNum = 0;
				// 群集
				int crNum = 0;
				ManagedObjectReference hostFolder = (ManagedObjectReference) getDynamicProperty(
						ecb, mor, DYNAMICPROPERTY_HOSTFOLDER);
				if (hostFolder != null) {
					ManagedObjectReference[] childEntity = (ManagedObjectReference[]) getDynamicProperty(
							ecb, hostFolder, DYNAMICPROPERTY_CHILDENTITY);
					if (childEntity != null) {
						for (ManagedObjectReference hoMor : childEntity) {
							if ((hoMor.getType().equals(RESOURCE_CR))
									|| (hoMor.getType().equals(RESOURCE_CCR))) {
								crNum++;
								ManagedObjectReference[] hoList = (ManagedObjectReference[]) getDynamicProperty(
										ecb, hoMor, DYNAMICPROPERTY_HOST);
								if (hoList != null) {
									hostNum += hoList.length;
								}
							} else if (hoMor.getType().equals(RESOURCE_HO)) {
								hostNum++;
							} else {
								// 不应该出现的mor类型
								LOGGER.error("出现了非主机类的对象,id='"
										+ hoMor.get_value() + "',类型='"
										+ hoMor.getType() + "'");
							}
						}
					}
				}

				resultMap.put(SUMMARY_COMMON_HOST, Integer.toString(hostNum));
				resultMap.put(SUMMARY_COMMON_CR, Integer.toString(crNum));

				// 虚拟机和模板
				int vmNum = 0;
				ManagedObjectReference vmFolder = (ManagedObjectReference) getDynamicProperty(
						ecb, mor, DYNAMICPROPERTY_VMFOLDER);
				if (vmFolder != null) {
					ManagedObjectReference[] childEntity = (ManagedObjectReference[]) getDynamicProperty(
							ecb, vmFolder, DYNAMICPROPERTY_CHILDENTITY);
					if (childEntity != null) {
						vmNum = childEntity.length;
					}
				}
				resultMap.put(SUMMARY_COMMON_VM, Integer.toString(vmNum));

				// 网络
				ManagedObjectReference[] netList = (ManagedObjectReference[]) getDynamicProperty(
						ecb, mor, DYNAMICPROPERTY_NETWORK);
				if (netList != null) {
					resultMap.put(SUMMARY_COMMON_NET, Integer
							.toString(netList.length));
				} else {
					resultMap.put(SUMMARY_COMMON_NET, "0");
				}

				// 数据存储
				ManagedObjectReference[] dsList = (ManagedObjectReference[]) getDynamicProperty(
						ecb, mor, DYNAMICPROPERTY_DATASTORE);
				if (dsList != null) {
					resultMap.put(SUMMARY_COMMON_DS, Integer
							.toString(dsList.length));
				} else {
					resultMap.put(SUMMARY_COMMON_DS, "0");
				}

				// 记录处理正确的标识
				recordResultMapSuccess(resultMap);
			} else {
				recordResultMapError(resultMap, "数据中心'" + dcId + "'不存在");
			}
		} catch (Exception e) {
			LOGGER.error("getSummary error, ", e);
			recordResultMapException(resultMap, e);
		}

		return resultMap;
	}

}
