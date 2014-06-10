package com.afunms.vmware.vim25.mgr;

import com.afunms.vmware.vim25.cache.VIMCache;
import com.afunms.vmware.vim25.common.VIMMgr;
import com.afunms.vmware.vim25.constants.ComputeResourceConstants;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.apputils.version.ExtendedAppUtil;
import com.vmware.vim25.ClusterConfigInfoEx;
import com.vmware.vim25.ComputeResourceConfigInfo;
import com.vmware.vim25.ComputeResourceSummary;
import com.vmware.vim25.DatastoreSummary;
import com.vmware.vim25.ManagedObjectReference;

/**
 * 群集管理,<br>
 * 
 * 所有操作的列表参见<br>
 * http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.
 * doc_50%2Fvim.ComputeResource.html中的描述<br>
 * 
 * @author LXL
 * 
 */
public class ComputeResourceMgr extends VIMMgr implements
		ComputeResourceConstants {

	// LOGGER
	private final static Logger LOGGER = Logger
			.getLogger(ComputeResourceMgr.class);

	/**
	 * 获取群集的摘要信息
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param crId
	 * @return
	 */
	public static Map<String, Object> getSummary(String url, String username,
			String password, String crId) {

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		try {
			VIMCache cache = VIMCache.getInstance(url, username, password);
			ManagedObjectReference mor = cache.getClusterComputeResource(crId);
			if (mor == null) {
				mor = cache.getComputeResource(crId);
			}

			if (mor != null) {
				ExtendedAppUtil ecb = getECB(url, username, password);

				// 配置
				ComputeResourceConfigInfo config = (ComputeResourceConfigInfo) getDynamicProperty(
						ecb, mor, DYNAMICPROPERTY_CONFIGURATIONEX);

				// 数据存储
				ManagedObjectReference[] dsList = (ManagedObjectReference[]) getDynamicProperty(
						ecb, mor, DYNAMICPROPERTY_DATASTORE);

				// 摘要
				ComputeResourceSummary summary = (ComputeResourceSummary) getDynamicProperty(
						ecb, mor, DYNAMICPROPERTY_SUMMARY);

				// // 根资源池
				// ManagedObjectReference rpMor = (ManagedObjectReference)
				// VIMMgr
				// .getDynamicProperty(ecb, mor,
				// DYNAMICPROPERTY_RESOURCEPOOL);

				// 主机
				ManagedObjectReference[] hoList = (ManagedObjectReference[]) VIMMgr
						.getDynamicProperty(ecb, mor, DYNAMICPROPERTY_HOST);

				// 摘要Map中的key值
				// 常规
				// vSphere DRS
				String drs = "";
				// vSphere HA
				String ha = "";
				if ((config != null) && (config instanceof ClusterConfigInfoEx)) {
					ClusterConfigInfoEx configEx = (ClusterConfigInfoEx) config;
					drs = configEx.getDrsConfig().getEnabled().toString();
					ha = configEx.getDasConfig().getEnabled().toString();
				}
				resultMap.put(SUMMARY_COMMON_DRS, drs);
				resultMap.put(SUMMARY_COMMON_HA, ha);
				// XXX VMWare EVC 模式,暂时无法获取
				// String SUMMARY_COMMON_EVC = "evc";

				// 总CPU资源,,单位MHz
				String totalcpu = "0";
				// 总内存,单位字节
				String totalmemory = "0";
				// 总存储
				String totaldssizemb = "0";
				// 主机数summary.getNumHosts(有效主机数summary.getNumEffectiveHosts)
				String numhosts = "0";
				// 处理器总数
				String numcpucores = "0";
				// 数据存储群集数
				String numstoragepods = "0";
				// 总数据存储数
				String numdss = "0";
				// 虚拟机和模板
				String numvms = "0";

				if (summary != null) {
					totalcpu = Integer.toString(summary.getTotalCpu());
					totalmemory = convertBytes2MBString(summary
							.getTotalMemory());
					numhosts = Integer.toString(summary.getNumHosts());
					numcpucores = Integer.toString(summary.getNumCpuCores());
				}

				long totalds = 0;
				int numsps = 0;
				if (dsList != null) {
					numdss = Integer.toString(dsList.length);

					for (ManagedObjectReference dsMor : dsList) {
						if (dsMor.getType().equals(RESOURCE_STORAGEPOD)) {
							numsps++;
						}
						// 存储摘要
						DatastoreSummary dsSummary = (DatastoreSummary) getDynamicProperty(
								ecb, dsMor, DYNAMICPROPERTY_SUMMARY);
						totalds += dsSummary.getCapacity();
					}
				}
				numstoragepods = Integer.toString(numsps);
				totaldssizemb = convertBytes2MBString(totalds);

				// 如果通过资源池计算,需要获取根资源池的虚拟机个数以及根资源池下面的所有的资源池的虚拟机个数,需要用递归算法
				// if (rpMor != null) {
				// ManagedObjectReference[] vmList =
				// (ManagedObjectReference[]) VIMMgr
				// .getDynamicProperty(ecb, rpMor, DYNAMICPROPERTY_VM);
				// //根资源池的虚拟机数
				// numvms = Integer.toString(vmList.length);
				// // 还需要计算子资源池中的虚拟机数
				// }

				// 通过主机计算
				int vmNum = 0;
				if (hoList != null) {
					for (ManagedObjectReference hoMor : hoList) {
						ManagedObjectReference[] vmList = (ManagedObjectReference[]) VIMMgr
								.getDynamicProperty(ecb, hoMor,
										DYNAMICPROPERTY_VM);
						if (vmList != null) {
							vmNum += vmList.length;
						}
					}
				}
				numvms = Integer.toString(vmNum);

				resultMap.put(SUMMARY_COMMON_TOTALCPU, totalcpu);
				resultMap.put(SUMMARY_COMMON_TOTALMEMORY, totalmemory);
				resultMap.put(SUMMARY_COMMON_TOTALDSSIZEMB, totaldssizemb);
				resultMap.put(SUMMARY_COMMON_NUMHOSTS, numhosts);
				resultMap.put(SUMMARY_COMMON_NUMCPUCORES, numcpucores);
				resultMap.put(SUMMARY_COMMON_NUMSTORAGEPODS, numstoragepods);
				resultMap.put(SUMMARY_COMMON_NUMDSS, numdss);
				resultMap.put(SUMMARY_COMMON_NUMVMS, numvms);

				// XXX 使用vMotion的总迁移数,暂时无法获取
				// String SUMMARY_COMMON_NUMVMOTIONS = "numvmotions";

				// XXX vSphere HA,暂时无法获取,
				// 应该是对应ClusterDasConfigInfo类

				// XXX vSphere DRS,,暂时无法获取,
				// 应该是对应ClusterDrsConfigInfo类

				// 记录处理正确的标识
				recordResultMapSuccess(resultMap);
			} else {
				recordResultMapError(resultMap, "主机群集'" + crId + "'不存在");
			}
		} catch (Exception e) {
			LOGGER.error("getSummary error, ", e);
			recordResultMapException(resultMap, e);
		}

		return resultMap;
	}
}
