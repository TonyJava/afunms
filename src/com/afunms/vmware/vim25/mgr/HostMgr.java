package com.afunms.vmware.vim25.mgr;

import com.afunms.vmware.vim25.cache.VIMCache;
import com.afunms.vmware.vim25.common.VIMMgr;
import com.afunms.vmware.vim25.constants.HostConstants;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.apputils.version.ExtendedAppUtil;
import com.vmware.vim25.HostHardwareInfo;
import com.vmware.vim25.HostListSummary;
import com.vmware.vim25.ManagedObjectReference;

/**
 * 主机管理,<br>
 * 
 * 所有操作的列表参见<br>
 * http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.
 * doc_50%2Fvim.HostSystem.html中的描述<br>
 * 
 * @author LXL
 * 
 */
public class HostMgr extends VIMMgr implements HostConstants {

	// LOGGER
	private final static Logger LOGGER = Logger.getLogger(HostMgr.class);

	/**
	 * 获取主机的摘要信息
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param hoId
	 * @return
	 */
	public static Map<String, Object> getSummary(String url, String username,
			String password, String hoId) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		try {
			VIMCache cache = VIMCache.getInstance(url, username, password);
			ManagedObjectReference mor = cache.getHostSystem(hoId);
			if (mor != null) {
				ExtendedAppUtil ecb = getECB(url, username, password);

				// 虚拟机
				ManagedObjectReference[] vmList = (ManagedObjectReference[]) getDynamicProperty(
						ecb, mor, DYNAMICPROPERTY_VM);

				// 硬件
				HostHardwareInfo hardware = (HostHardwareInfo) getDynamicProperty(
						ecb, mor, DYNAMICPROPERTY_HARDWARE);

				// 摘要
				HostListSummary summary = (HostListSummary) getDynamicProperty(
						ecb, mor, DYNAMICPROPERTY_SUMMARY);

				// 存储列表
				ManagedObjectReference[] dsList = (ManagedObjectReference[]) getDynamicProperty(
						ecb, mor, DYNAMICPROPERTY_DATASTORE);

				// 网络列表
				ManagedObjectReference[] netList = (ManagedObjectReference[]) getDynamicProperty(
						ecb, mor, DYNAMICPROPERTY_NETWORK);

				// 摘要Map中的key值
				// 常规
				// 制造商
				String vendor = "";
				// 型号,样本:PowerEdge R710
				String model = "";
				// CPU内核
				String cpucore = "";
				// 处理器类型
				String cpumodel = "";
				// XXX 许可证
				// 处理器插槽
				String numcpupkgs = "0";
				// 每个插槽的内核数
				String numcorespersocket = "0";
				// 逻辑处理器
				String numcputhreads = "";
				// XXX 超线程(活动),暂时无法获取
				// 网卡数目
				String numnics = "0";
				// 状况(已连接)
				String connectionstate = "";
				// 虚拟机和模板
				String vm = "0";
				// vMotion已启用(否)
				String vmotion = "";
				// XXX VMWare EVC模式,暂时无法获取,summary.getCurrentEVCModeKey()
				// XXX vSphere HA状况(正在运行(主机)),暂时无法获取,
				// summary.getRuntime().getDasHostState().getState()=master
				// XXX 已配置FT的主机(否)
				// summary.getConfig().getFaultToleranceEnabled()
				// XXX 活动任务,暂时无法获取
				// XXX 主机配置文件,暂时无法获取
				// XXX 映像配置文件,暂时无法获取
				// XXX 配置文件合规性(不可用),暂时无法获取
				// XXX DirectPath I/O(不受支持),暂时无法获取

				// 资源
				// CPU使用情况,单位MHz
				String overallcpuusage = "0";
				// 内存使用情况,单位MB
				String overallmemoryusage = "0";
				// 内存容量,单位MB
				String memorysizemb = "0";

				// Fault Tolerance
				// XXX Fault Tolerance 版本,暂时无法获取
				// XXX 主虚拟机总数,暂时无法获取
				// XXX 已打开电源的主虚拟机总数,暂时无法获取
				// XXX 辅助虚拟机总数,暂时无法获取
				// XXX 已打开电源的辅助虚拟机总数,暂时无法获取

				if (hardware != null) {
					vendor = hardware.getSystemInfo().getVendor();
					model = hardware.getSystemInfo().getModel();
				}

				if (summary != null) {
					cpucore = summary.getHardware().getNumCpuCores() + " × "
							+ summary.getHardware().getCpuMhz() + MHZ;
					cpumodel = summary.getHardware().getCpuModel();
					numcpupkgs = Integer.toString(summary.getHardware()
							.getNumCpuPkgs());
					numcorespersocket = Integer.toString(summary.getHardware()
							.getNumCpuCores()
							/ summary.getHardware().getNumCpuPkgs());
					numcputhreads = Integer.toString(summary.getHardware()
							.getNumCpuThreads());
					numnics = Integer.toString(summary.getHardware()
							.getNumNics());
					connectionstate = summary.getRuntime().getConnectionState()
							.toString();
					vmotion = Boolean.toString(summary.getConfig()
							.isVmotionEnabled());
					// ft = summary.getConfig().getFaultToleranceEnabled()
					// .toString();

					overallcpuusage = Integer.toString(summary.getQuickStats()
							.getOverallCpuUsage());
					overallmemoryusage = Integer.toString(summary
							.getQuickStats().getOverallMemoryUsage());
					memorysizemb = getByteSizeStrFromMB(convertBytes2MBLong(summary
							.getHardware().getMemorySize()));
				}

				if (vmList != null) {
					vm = Integer.toString(vmList.length);
				}

				// 常规
				resultMap.put(SUMMARY_COMMON_VENDOR, vendor);
				resultMap.put(SUMMARY_COMMON_MODEL, model);
				resultMap.put(SUMMARY_COMMON_CPUCORE, cpucore);
				resultMap.put(SUMMARY_COMMON_CPUMODEL, cpumodel);
				resultMap.put(SUMMARY_COMMON_NUMCPUKGS, numcpupkgs);
				resultMap.put(SUMMARY_COMMON_NUMCORESPERSOCKET,
						numcorespersocket);
				resultMap.put(SUMMARY_COMMON_NUMCPUTHREADS, numcputhreads);
				resultMap.put(SUMMARY_COMMON_NUMNICS, numnics);
				resultMap.put(SUMMARY_COMMON_CONNECTIONSTATE, connectionstate);
				resultMap.put(SUMMARY_COMMON_VM, vm);
				resultMap.put(SUMMARY_COMMON_VMOTION, vmotion);

				// 资源
				resultMap
						.put(SUMMARY_RESOURCE_OVERALLCPUUSAGE, overallcpuusage);
				resultMap.put(SUMMARY_RESOURCE_OVERALLMEMORYUSAGE,
						overallmemoryusage);
				resultMap.put(SUMMARY_RESOURCE_MEMORYSIZEMB, memorysizemb);

				// 存储列表
				DatastoreMgr.recordResultMapDsList(resultMap, ecb, dsList);

				// 网络列表
				NetworkMgr.recordResultMapNetList(resultMap, ecb, netList);

				// 记录处理正确的标识
				recordResultMapSuccess(resultMap);
			} else {
				recordResultMapError(resultMap, "主机'" + hoId + "'不存在");
			}
		} catch (Exception e) {
			LOGGER.error("getSummary error, ", e);
			recordResultMapException(resultMap, e);
		}

		return resultMap;
	}
}
