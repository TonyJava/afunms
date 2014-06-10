package com.afunms.vmware.vim25.mgr;

import com.afunms.vmware.vim25.cache.VIMCache;
import com.afunms.vmware.vim25.common.VIMMgr;
import com.afunms.vmware.vim25.constants.DatastoreConstants;
import com.afunms.vmware.vim25.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.apputils.version.ExtendedAppUtil;
import com.vmware.vim25.DatastoreHostMount;
import com.vmware.vim25.DatastoreInfo;
import com.vmware.vim25.DatastoreSummary;
import com.vmware.vim25.ManagedEntityStatus;
import com.vmware.vim25.ManagedObjectReference;

/**
 * 存储管理,<br>
 * 
 * 所有操作的列表参见<br>
 * http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.
 * doc_50%2Fvim.Datastore.html中的描述<br>
 * 
 * @author LXL
 * 
 */
public class DatastoreMgr extends VIMMgr implements DatastoreConstants {

	// LOGGER
	private final static Logger LOGGER = Logger.getLogger(DatastoreMgr.class);

	/**
	 * 获取存储的摘要信息
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param dsId
	 * @return
	 */
	public static Map<String, Object> getSummary(String url, String username,
			String password, String dsId) {

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		try {
			VIMCache cache = VIMCache.getInstance(url, username, password);
			ManagedObjectReference mor = cache.getDatastore(dsId);
			if (mor != null) {
				ExtendedAppUtil ecb = getECB(url, username, password);

				// 信息
				DatastoreInfo info = (DatastoreInfo) getDynamicProperty(ecb,
						mor, DYNAMICPROPERTY_INFO);

				// 摘要
				DatastoreSummary summary = (DatastoreSummary) getDynamicProperty(
						ecb, mor, DYNAMICPROPERTY_SUMMARY);

				// 主机挂载列表
				DatastoreHostMount[] hostMountList = (DatastoreHostMount[]) getDynamicProperty(
						ecb, mor, DYNAMICPROPERTY_HOST);

				// 虚拟机列表
				ManagedObjectReference[] vmList = (ManagedObjectReference[]) getDynamicProperty(
						ecb, mor, DYNAMICPROPERTY_VM);

				// 摘要Map中的key值
				// 常规
				// 位置
				String dsurl = "";
				// 类型
				String type = "";
				// 所连接主机数目
				String hostNum = "0";
				// 虚拟机和模板
				String vmNum = "0";

				// 容量
				// 容量
				String capacity = "0";
				// 置备的空间=capacity+uncommitted-freespace
				String storageall = "0";
				// 可用空间
				String freespace = "0";
				// 上次更新时间
				String timestamp = "";

				if (info != null) {
					timestamp = Util.getDBDateTime(info.getTimestamp()
							.getTimeInMillis());
				}

				if (summary != null) {
					dsurl = summary.getUrl();
					type = summary.getType();
					capacity = getByteSizeStrFromBytes(summary.getCapacity());
					freespace = getByteSizeStrFromBytes(summary.getFreeSpace());
					storageall = getByteSizeStrFromBytes(summary.getCapacity()
							+ summary.getUncommitted() - summary.getFreeSpace());
				}

				if (hostMountList != null) {
					hostNum = Integer.toString(hostMountList.length);
				}

				if (vmList != null) {
					vmNum = Integer.toString(vmList.length);
				}

				// 保存所有的值
				resultMap.put(SUMMARY_COMMON_URL, dsurl);
				resultMap.put(SUMMARY_COMMON_TYPE, type);
				resultMap.put(SUMMARY_COMMON_HOST, hostNum);
				resultMap.put(SUMMARY_COMMON_VM, vmNum);
				resultMap.put(SUMMARY_CAPACITY_CAPACITY, capacity);
				resultMap.put(SUMMARY_CAPACITY_STORAGEALL, storageall);
				resultMap.put(SUMMARY_CAPACITY_FREESPACE, freespace);
				resultMap.put(SUMMARY_CAPACITY_TIMESTAMP, timestamp);

				// 记录处理正确的标识
				recordResultMapSuccess(resultMap);
			} else {
				recordResultMapError(resultMap, "数据存储'" + dsId + "'不存在");
			}
		} catch (Exception e) {
			LOGGER.error("getSummary error, ", e);
			recordResultMapException(resultMap, e);
		}

		return resultMap;
	}

	/**
	 * 记录存储列表
	 * 
	 * @param resultMap
	 * @param ecb
	 * @param dsList
	 */
	public static void recordResultMapDsList(HashMap<String, Object> resultMap,
			ExtendedAppUtil ecb, ManagedObjectReference[] dsList) {
		try {
			// 存储器列表
			ArrayList<HashMap<String, String>> dsResultList = new ArrayList<HashMap<String, String>>();
			resultMap.put(SUMMARY_RESOURCE_DS_LIST, dsResultList);
			if (dsList != null) {
				for (ManagedObjectReference dsMor : dsList) {
					ManagedEntityStatus overallStatus = (ManagedEntityStatus) VIMMgr
							.getDynamicProperty(ecb, dsMor,
									DYNAMICPROPERTY_OVERALLSTATUS);
					Boolean alarmActionsEnabled = (Boolean) VIMMgr
							.getDynamicProperty(ecb, dsMor,
									DYNAMICPROPERTY_ALARMACTIONSENABLED);
					DatastoreInfo dsInfo = (DatastoreInfo) VIMMgr
							.getDynamicProperty(ecb, dsMor,
									DYNAMICPROPERTY_INFO);
					DatastoreSummary dsSummary = (DatastoreSummary) VIMMgr
							.getDynamicProperty(ecb, dsMor,
									DYNAMICPROPERTY_SUMMARY);

					// 存储器列表中的Map的key
					HashMap<String, String> dsResultMap = new HashMap<String, String>();
					dsResultList.add(dsResultMap);

					if (dsInfo != null) {
						// 存储器
						dsResultMap.put(SUMMARY_RESOURCE_DS_NAME, dsInfo
								.getName());
						// 可用空间
						dsResultMap.put(SUMMARY_RESOURCE_DS_FREESPACE,
								getByteSizeStrFromBytes(dsInfo.getFreeSpace()));
						// 上次更新
						dsResultMap.put(SUMMARY_RESOURCE_DS_TIME, Util
								.getDBDateTime(dsInfo.getTimestamp()
										.getTimeInMillis()));
					} else {
						dsResultMap.put(SUMMARY_RESOURCE_DS_NAME, "");
						dsResultMap.put(SUMMARY_RESOURCE_DS_FREESPACE, "");
						dsResultMap.put(SUMMARY_RESOURCE_DS_TIME, "");
					}

					// 容量
					if (dsSummary != null) {
						dsResultMap
								.put(SUMMARY_RESOURCE_DS_CAPACITY,
										getByteSizeStrFromBytes(dsSummary
												.getCapacity()));
					} else {
						dsResultMap.put(SUMMARY_RESOURCE_DS_CAPACITY, "");
					}
					// 状态
					if (overallStatus != null) {
						dsResultMap.put(SUMMARY_RESOURCE_DS_STATUS,
								overallStatus.getValue());
					} else {
						dsResultMap.put(SUMMARY_RESOURCE_DS_STATUS, "");
					}
					// 警报操作
					if (alarmActionsEnabled != null) {
						dsResultMap.put(SUMMARY_RESOURCE_DS_ALARMACTIONS,
								alarmActionsEnabled.toString());
					} else {
						dsResultMap.put(SUMMARY_RESOURCE_DS_ALARMACTIONS, "");
					}
					// XXX 驱动器类型,暂时无法获取
					// 值有非SSD,
					// 与ssd有关的QueryConfigTarget返回ConfigTarget.scsiDisk(VirtualMachineScsiDiskDeviceInfo).disk(HostScsiDisk).ssd
					// String SUMMARY_RESOURCE_DS_DRIVETYPE =
					// "dsdrivetype";
					// XXX Storage I/O Control,暂时无法获取
					// String SUMMARY_RESOURCE_DS_IOCTRL = "dsioctrl";
				}
			}
		} catch (Exception e) {
			LOGGER.error("recordResultMapDsList error, ", e);
		}
	}
}
