package com.afunms.vmware.vim25.mgr;

import com.afunms.vmware.vim25.cache.VIMCache;
import com.afunms.vmware.vim25.common.VIMMgr;
import com.afunms.vmware.vim25.constants.ResourcePoolConstants;
import com.afunms.vmware.vim25.constants.VMConstants;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.apputils.version.ExtendedAppUtil;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.VirtualMachineRuntimeInfo;

/**
 * 资源池管理,<br>
 * 
 * 所有操作的列表参见<br>
 * http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.
 * doc_50%2Fvim.ResourcePool.html中的描述<br>
 * 
 * @author LXL
 * 
 */
public class ResourcePoolMgr extends VIMMgr implements ResourcePoolConstants {

	// LOGGER
	private final static Logger LOGGER = Logger
			.getLogger(ResourcePoolMgr.class);

	/**
	 * 获取资源池的摘要信息
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param rpId
	 * @return
	 */
	public static Map<String, Object> getSummary(String url, String username,
			String password, String rpId) {

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		try {
			VIMCache cache = VIMCache.getInstance(url, username, password);
			ManagedObjectReference mor = cache.getResourcePool(rpId);
			if (mor != null) {
				ExtendedAppUtil ecb = VIMMgr.getECB(url, username, password);

				ManagedObjectReference[] rpList = (ManagedObjectReference[]) VIMMgr
						.getDynamicProperty(ecb, mor,
								DYNAMICPROPERTY_RESOURCEPOOL);

				ManagedObjectReference[] vmList = (ManagedObjectReference[]) VIMMgr
						.getDynamicProperty(ecb, mor, DYNAMICPROPERTY_VM);

				int rpNum = 0;
				int vAppNum = 0;
				if (rpList != null) {
					for (ManagedObjectReference rpMor : rpList) {
						// 判断类型为ResourcePool和VirtualApp分别统计子资源池和子vApp的个数
						if (rpMor.getType().equals(RESOURCE_RP)) {
							rpNum++;
						}
						if (rpMor.getType().equals(RESOURCE_VAPP)) {
							vAppNum++;
						}
					}
				}
				resultMap.put(SUMMARY_COMMON_RP, rpNum);
				resultMap.put(SUMMARY_COMMON_VAPP, vAppNum);

				int vmNum = 0;
				int openVMNum = 0;
				if (vmList != null) {
					// 虚拟机总数
					vmNum = vmList.length;
					// 遍历所有的虚拟机,判断虚拟机的电源状态
					for (ManagedObjectReference vmMor : vmList) {
						VirtualMachineRuntimeInfo runtime = (VirtualMachineRuntimeInfo) VIMMgr
								.getDynamicProperty(ecb, vmMor,
										DYNAMICPROPERTY_RUNTIME);
						if ((runtime.getPowerState() != null)
								&& (runtime.getPowerState().getValue()
										.equals(VMConstants.POWERSTATE_POWERON))) {
							openVMNum++;
						}
					}
				}
				resultMap.put(SUMMARY_COMMON_VM, vmNum);
				resultMap.put(SUMMARY_COMMON_OPENVM, openVMNum);

				// vCenter界面中CPU和内存的数据已基本找到在ResourcePoolMgrTest.testPrintRPSummary中有说明

				// 记录处理正确的标识
				recordResultMapSuccess(resultMap);
			} else {
				recordResultMapError(resultMap, "资源池'" + rpId + "'不存在");
			}
		} catch (Exception e) {
			LOGGER.error("getSummary error, ", e);
			recordResultMapException(resultMap, e);
		}

		return resultMap;
	}
}
