package com.afunms.vmware.vim25.mgr;

import com.afunms.vmware.vim25.cache.VIMCache;
import com.afunms.vmware.vim25.common.VIMMgr;
import com.afunms.vmware.vim25.constants.VMConstants;
import com.afunms.vmware.vim25.util.Util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.apputils.version.ExtendedAppUtil;
import com.vmware.vim25.DatastoreSummary;
import com.vmware.vim25.FileFault;
import com.vmware.vim25.InvalidDatastore;
import com.vmware.vim25.InvalidPowerState;
import com.vmware.vim25.InvalidState;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.NotSupported;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.TaskInProgress;
import com.vmware.vim25.ToolsUnavailable;
import com.vmware.vim25.VirtualCdrom;
import com.vmware.vim25.VirtualCdromRemotePassthroughBackingInfo;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualDeviceConfigSpec;
import com.vmware.vim25.VirtualDeviceConfigSpecFileOperation;
import com.vmware.vim25.VirtualDeviceConfigSpecOperation;
import com.vmware.vim25.VirtualDeviceConnectInfo;
import com.vmware.vim25.VirtualDisk;
import com.vmware.vim25.VirtualDiskFlatVer2BackingInfo;
import com.vmware.vim25.VirtualEthernetCard;
import com.vmware.vim25.VirtualEthernetCardNetworkBackingInfo;
import com.vmware.vim25.VirtualIDEController;
import com.vmware.vim25.VirtualLsiLogicController;
import com.vmware.vim25.VirtualMachineCloneSpec;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineConfigOption;
import com.vmware.vim25.VirtualMachineConfigSpec;
import com.vmware.vim25.VirtualMachineFileInfo;
import com.vmware.vim25.VirtualMachineRelocateSpec;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.VirtualMachineSummary;
import com.vmware.vim25.VirtualPCNet32;
import com.vmware.vim25.VirtualSCSISharing;
import com.vmware.vim25.VmConfigFault;

/**
 * 虚拟机管理,<br>
 * 数据的获取参照vCenter中的界面,包括摘要、性能(实时和历史)、任务与事件、警报。<br>
 * 虚拟机的增删改操作包括CreateVM_Task、CloneVM_Task、ReconfigVM_Task、Destroy_Task。<br>
 * 虚拟机的状态操作包括PowerOffVM_Task、PowerOnVM_Task、SuspendVM_Task、ResetVM_Task、
 * ShutdownGuest、StandbyGuest、RebootGuest。<br>
 * 
 * 所有操作的列表参见<br>
 * http://pubs.vmware.com/vsphere-50/topic/com.vmware.wssdk.apiref.doc_50/vim.
 * VirtualMachine.html中的描述<br>
 * 
 * @author LXL
 * 
 */
public class VMMgr extends VIMMgr implements VMConstants {

	// LOGGER
	private final static Logger LOGGER = Logger.getLogger(VMMgr.class);

	/**
	 * 创建虚拟机,CreateVM_Task
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param dcId
	 * @param haId
	 * @param rpId
	 * @param name
	 * @param descr
	 * @param guestId
	 * @param numCpus
	 * @param numCores
	 * @param memoryMB
	 * @param dsId
	 * @param diskSizeMB
	 * @return
	 */
	public static Map<String, Object> createVM(String url, String username,
			String password, String dcId, String haId, String rpId,
			String name, String descr, String guestId, int numCpus,
			int numCores, long memoryMB, String dsId, long diskSizeMB) {
		// CreateVM_Task
		// http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.doc_50%2Fvim.Folder.html&path=5_0_2_5_3_75

		// VirtualMachineConfigSpec
		// http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.doc_50%2Fvim.vm.ConfigSpec.html

		// 1.VimBindingStub对象执行createVM_Task方法
		// 共4个参数,_this,config,pool,host
		// 其中host可以不指定,但是pool需要有权限Resource.AssignVMToPool

		// 1._this:从dc获取一下vmFolder
		// 公司环境的vmFolder树形结构itimsDC(datacenter-2)-->vm(group-v3)
		// 公司环境中的主机群集属性结果itimsDC(datacenter-2)-->host,group-h4(datacenter-2)-->itimsHA(domain-c73)
		// 在管理系统中的云对应主机群集,在保存主机群集的id同时也保存dc的id
		// ManagedObjectReference vmFolderMor =
		// cb.getServiceUtil().getMoRefProp(
		// dcmor, "vmFolder");

		// 2.config:VirtualMachineConfigSpec虚拟机配置

		// 3.pool:从dc的hostFolder对象下获取ComputeResource
		// 公司环境的ComputeResource树形结构itimsDC(datacenter-2)-->host(group-h4)-->itimsHA(domain-c73)-->Resources(resgroup-74)-->testRP(resgroup-121)
		// vim代码:从主机目录中获取计算资源
		// ManagedObjectReference hfmor =
		// cb.getServiceUtil().getMoRefProp(dcmor,
		// "hostFolder");
		// ArrayList crmors = cb.getServiceUtil().getDecendentMoRefs(hfmor,
		// "ComputeResource");

		// 4.host:主机不指定,由计算资源自动分配

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		try {
			VIMCache cache = VIMCache.getInstance(url, username, password);

			// 获取_this和pool
			ManagedObjectReference dcMor = cache.getDatacenter(dcId);
			// 获取数据中心下的虚拟机文件夹
			ManagedObjectReference vmFolderMor = cache.getVMFolder(dcId);
			// 获取主机群集
			ManagedObjectReference haMor = cache.getComputeResource(haId);
			if (haMor == null) {
				haMor = cache.getClusterComputeResource(haId);
			}
			// 获取资源池
			ManagedObjectReference rpMor = cache.getResourcePool(rpId);
			// 获取数据存储
			ManagedObjectReference dsMor = cache.getDatastore(dsId);

			if ((dcMor != null) && (vmFolderMor != null) && (haMor != null)
					&& (rpMor != null) && (dsMor != null)) {

				ExtendedAppUtil ecb = getECB(url, username, password);

				// 创建VirtualMachineConfigSpec
				// http://pubs.vmware.com/vsphere-50/topic/com.vmware.wssdk.apiref.doc_50/vim.vm.ConfigSpec.html
				VirtualMachineConfigSpec vmConfig = createVmConfigSpec(ecb,
						dcMor, haMor, name, descr, guestId, numCpus, numCores,
						memoryMB, dsMor, diskSizeMB);

				ManagedObjectReference taskMor = ecb.getServiceConnection3()
						.getService().createVM_Task(vmFolderMor, vmConfig,
								rpMor, null);

				execTaskAndRecordResultMap(ecb, taskMor, resultMap);

				// 如果虚拟机创建成功,获取虚拟机对应的主机id
				if (Util.normalizeObject(resultMap.get(INFO_STATE)).toString()
						.equals(STATE_OK)) {
					String vmId = Util.normalizeObject(
							resultMap.get(INFO_RESULT)).toString();
					if (!vmId.equals("")) {
						ManagedObjectReference vmMor = cache
								.getVirtualMachine(vmId);
						Object runtime = getDynamicProperty(ecb, vmMor,
								DYNAMICPROPERTY_RUNTIME);
						if ((runtime != null)
								&& (runtime instanceof VirtualMachineRuntimeInfo)) {
							VirtualMachineRuntimeInfo runtimeInfo = (VirtualMachineRuntimeInfo) runtime;
							if (runtimeInfo.getHost() != null) {
								String hoId = runtimeInfo.getHost().get_value();
								resultMap.put(INFO_RESULT2, hoId);
							}
						}
					}
				}
			} else {
				if (dcMor == null) {
					recordResultMapError(resultMap, "数据中心'" + dcId + "'不存在");
				}
				if (vmFolderMor == null) {
					recordResultMapError(resultMap, "数据中心'" + dcId
							+ "'的vmFolder不存在");
				}
				if (haMor == null) {
					recordResultMapError(resultMap, "主机群集'" + haId + "'不存在");
				}
				if (rpMor == null) {
					recordResultMapError(resultMap, "'资源池" + rpId + "'不存在");
				}
				if (dsMor == null) {
					recordResultMapError(resultMap, "'数据存储" + dsId + "'不存在");
				}
			}
		} catch (Exception e) {
			LOGGER.error("createVM error, ", e);
			recordResultMapException(resultMap, e);
		}

		return resultMap;
	}

	/**
	 * 创建虚拟机的配置信息
	 * 
	 * @param ecb
	 * @param dcMor
	 * @param haMor
	 * @param name
	 * @param descr
	 * @param guestId
	 * @param numCpus
	 * @param numCores
	 * @param memoryMB
	 * @param dsMor
	 * @param diskSizeMB
	 * @return
	 * @throws Exception
	 */
	private static VirtualMachineConfigSpec createVmConfigSpec(
			ExtendedAppUtil ecb, ManagedObjectReference dcMor,
			ManagedObjectReference haMor, String name, String descr,
			String guestId, int numCpus, int numCores, long memoryMB,
			ManagedObjectReference dsMor, long diskSizeMB) throws Exception {

		// http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.doc_50%2Fvim.VirtualDiskManager.VirtualDiskType.html
		// 厚置备延迟置零 （zeroed thick） //默认选项
		// thick=zeroedthick
		// 厚置备置零 （eager zeroed thick）,创建的时候特别的长。
		// eagerZeroedThick
		// 精简置备 （thin）
		// thin
		// 厚置备延迟置零：以默认的厚格式创建虚拟磁盘。创建过程中为虚拟磁盘分配所需空间。创建时不会擦除物理设备上保留的任何数据，但是以后从虚拟机首次执行写操作时会按需要将其置零。
		// 厚置备置零：创建支持群集功能（如 Fault
		// Tolerance）的厚磁盘。在创建时为虚拟磁盘分配所需的空间。与平面格式相反，在创建过程中会将物理设备上保留的数据置零。创建这种格式的磁盘所需的时间可能会比创建其他类型的磁盘长。
		// 精简置备：使用精简置备格式。最初，精简置备的磁盘只使用该磁盘最初所需要的数据存储空间。如果以后精简磁盘需要更多空间，则它可以增长到为其分配的最大容量。

		// 0-thick和1-eagerZeroedThick区别：0-thick的getEagerlyScrub=null;1-eagerZeroedThick的getEagerlyScrub=true
		// 0-thick和2-thin区别：0-thick的getThinProvisioned=false;2-thin的getThinProvisioned=true
		// 1-eagerZeroedThickh和2-thin区别：1-eagerZeroedThickh的getEagerlyScrub=true,getThinProvisioned=false;2-thin的getEagerlyScrub=null,getThinProvisioned=true

		String diskType = DISKTYPE_THICK;

		VirtualMachineConfigSpec vmConfig = new VirtualMachineConfigSpec();

		// 名称
		vmConfig.setName(name);
		// 备注
		vmConfig.setAnnotation(descr);
		// 操作系统id
		// http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.doc_50%2Fvim.vm.GuestOsDescriptor.GuestOsIdentifier.html
		vmConfig.setGuestId(guestId);
		// CPU
		vmConfig.setNumCPUs(numCpus);
		vmConfig.setNumCoresPerSocket(numCores);
		// 内存
		vmConfig.setMemoryMB(memoryMB);

		// 虚拟机的所有的设备,如磁盘,光驱,软盘等
		VirtualDeviceConfigSpec[] deviceConfigs = new VirtualDeviceConfigSpec[4];
		vmConfig.setDeviceChange(deviceConfigs);

		// SCSI控制器
		// --getBusNumber=0
		// --getScsiCtlrUnitNumber=7
		// --getHotAddRemove=true
		// --getSharedBus=noSharing
		// --getKey=1000
		// --getControllerKey=100
		// --getUnitNumber=3
		// --getBacking=null
		// --getConnectable=null

		VirtualDeviceConfigSpec scsiConfig = new VirtualDeviceConfigSpec();
		deviceConfigs[0] = scsiConfig;
		scsiConfig.setOperation(VirtualDeviceConfigSpecOperation.add);
		VirtualLsiLogicController scsiCtrl = new VirtualLsiLogicController();
		scsiConfig.setDevice(scsiCtrl);
		scsiCtrl.setBusNumber(0);
		int scsiCtlrKey = 20;
		scsiCtrl.setKey(scsiCtlrKey);
		// 样例代码中是VirtualSCSISharing.physicalSharing
		// scsiCtrl.setSharedBus(VirtualSCSISharing.physicalSharing);
		scsiCtrl.setSharedBus(VirtualSCSISharing.noSharing);
		// 样例代码中未设置ScsiCtlrUnitNumber和HotAddRemove

		// 磁盘
		DatastoreSummary dsSummary = (DatastoreSummary) ecb.getServiceUtil3()
				.getDynamicProperty(dsMor, DYNAMICPROPERTY_SUMMARY);
		String dsName = "";
		if (dsSummary != null) {
			dsName = dsSummary.getName();
		}

		VirtualDeviceConfigSpec diskConfig = new VirtualDeviceConfigSpec();
		deviceConfigs[1] = diskConfig;
		diskConfig
				.setFileOperation(VirtualDeviceConfigSpecFileOperation.create);
		diskConfig.setOperation(VirtualDeviceConfigSpecOperation.add);

		// ----------------------com.vmware.vim25.VirtualDisk
		// --getCapacityInKB=16777216
		// --getShares=com.vmware.vim25.SharesInfo@c206c0d0
		// ----getShares=1000
		// ----getLevel=normal
		// --getStorageIOAllocation=com.vmware.vim25.StorageIOAllocationInfo@c206c0d1
		// ----getLimit=-1
		// ----getShares=com.vmware.vim25.SharesInfo@c206c0d0
		// ------getShares=1000
		// ------getLevel=normal
		// ----------------------
		// --getKey=2000
		// --getControllerKey=1000
		// --getUnitNumber=0
		// --getBacking=com.vmware.vim25.VirtualDiskFlatVer2BackingInfo@acca9802
		// ----getChangeId=null
		// ----getContentId=f03ee93a387f3ed891df36b1fffffffe
		// ----getDatastore=datastore-13
		// ----getDeltaDiskFormat=null
		// ----getDiskMode=persistent
		// ----getDynamicType=null
		// ----getFileName=[datastore1] testOPs/testOPs.vmdk
		// ----getUuid=6000C294-dedc-b5e9-e681-131218cd2db4
		// ----getDigestEnabled=false
		// ----getEagerlyScrub=null
		// ----getParent=null
		// ----getSplit=false
		// ----getThinProvisioned=false
		// ----getWriteThrough=false
		// --getConnectable=null
		VirtualDisk disk = new VirtualDisk();
		diskConfig.setDevice(disk);

		disk.setControllerKey(scsiCtlrKey);
		disk.setUnitNumber(0);
		// disk.setKey(0);
		disk.setKey(30);

		disk.setCapacityInKB(diskSizeMB * 1024);

		// http://pubs.vmware.com/vsphere-50/topic/com.vmware.wssdk.apiref.doc_50/vim.vm.device.VirtualDisk.FlatVer2BackingInfo.html
		VirtualDiskFlatVer2BackingInfo backingInfo = new VirtualDiskFlatVer2BackingInfo();
		disk.setBacking(backingInfo);
		// ===============================================================================
		// http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.doc_50%2Fvim.vm.device.VirtualDiskOption.DiskMode.html
		// The "persistent" mode is supported by every backing type.
		backingInfo.setDiskMode(DISKMODE_PERSISTENT);

		// 0-thick和1-eagerZeroedThick区别：0-thick的getEagerlyScrub=null;1-eagerZeroedThick的getEagerlyScrub=true
		// 0-thick和2-thin区别：0-thick的getThinProvisioned=false;2-thin的getThinProvisioned=true
		// 1-eagerZeroedThickh和2-thin区别：1-eagerZeroedThickh的getEagerlyScrub=true,getThinProvisioned=false;2-thin的getEagerlyScrub=null,getThinProvisioned=true

		// diskType= thick | eagerZeroedThick | thin
		if (diskType.equalsIgnoreCase(DISKTYPE_THIN)) {
			backingInfo.setThinProvisioned(true);
		} else {
			backingInfo.setThinProvisioned(false);
		}
		if (diskType.equalsIgnoreCase(DISKTYPE_EAGERZEROEDTHINK)) {
			backingInfo.setEagerlyScrub(true);
		} else {
			backingInfo.setEagerlyScrub(false);
		}
		backingInfo.setDatastore(dsMor);
		backingInfo.setFileName("");

		// 网络
		VirtualDeviceConfigSpec nicConfig = new VirtualDeviceConfigSpec();
		deviceConfigs[2] = nicConfig;
		nicConfig.setOperation(VirtualDeviceConfigSpecOperation.add);
		VirtualEthernetCard ethCard = new VirtualPCNet32();
		nicConfig.setDevice(ethCard);
		ethCard.setAddressType(ETHERNETCARD_ADDRESSTYPE_GENERATED);
		ethCard.setKey(40);
		VirtualEthernetCardNetworkBackingInfo ethBacking = new VirtualEthernetCardNetworkBackingInfo();
		ethCard.setBacking(ethBacking);
		ethBacking.setDeviceName(ETHERNETCARD_DEFAULT_NAME);

		// 新建IDE设备,出现错误:设备“4”正在引用不存在的控制器“50”。
		// int ideCtrlKey = 50;
		// int ideBusNumber = 3;
		// VirtualDeviceConfigSpec ideConfig = new VirtualDeviceConfigSpec();
		// deviceConfigs[3] = ideConfig;
		// VirtualIDEController ideCtrl = new VirtualIDEController();
		// ideConfig.setDevice(ideCtrl);
		// ideConfig.setOperation(VirtualDeviceConfigSpecOperation.add);
		// ideCtrl.setKey(ideCtrlKey);
		// ideCtrl.setBusNumber(ideBusNumber);

		// 光驱
		VirtualDeviceConfigSpec cdRomConfig = new VirtualDeviceConfigSpec();
		deviceConfigs[3] = cdRomConfig;
		VirtualCdrom cdRom = new VirtualCdrom();
		cdRomConfig.setDevice(cdRom);
		cdRomConfig.setOperation(VirtualDeviceConfigSpecOperation.add);

		// backing_备份,可以不设置VirtualCdromRemotePassthroughBackingInfo
		// // ----getDeviceName=
		// // ----getUseAutoDetect=false
		// // ----isExclusive=false
		VirtualCdromRemotePassthroughBackingInfo vcrpb = new VirtualCdromRemotePassthroughBackingInfo();
		vcrpb.setDeviceName("");
		vcrpb.setExclusive(false);
		vcrpb.setUseAutoDetect(false);
		cdRom.setBacking(vcrpb);

		// connectable_连接
		// ----getStatus=untried
		// ----isAllowGuestControl=true
		// ----isConnected=false
		// ----isStartConnected=false
		VirtualDeviceConnectInfo cdRomConnect = new VirtualDeviceConnectInfo();
		cdRom.setConnectable(cdRomConnect);
		// cdRomConnect.setStatus(arg0);
		cdRomConnect.setAllowGuestControl(true);
		cdRomConnect.setConnected(false);
		cdRomConnect.setStartConnected(false);

		// deviceInfo_设备信息,不设置

		int cdRomUnitNumber = 0;

		// 使用查找的IDE驱动器出现错误:为设备“3”指定了不兼容的设备备用。
		// controllerKey_控制器ID
		// key_设备ID
		// unitNumber_控制器上的连接设备数
		int ideCtrlKey = -1;

		// 寻找IDE驱动器
		ManagedObjectReference envBrowseMor = ecb.getServiceUtil3()
				.getMoRefProp(haMor, DYNAMICPROPERTY_ENVIRONMENTBROWSER);
		VirtualMachineConfigOption cfgOpt = ecb.getServiceConnection3()
				.getService().queryConfigOption(envBrowseMor, null, null);
		VirtualDevice[] defaultDevices = cfgOpt.getDefaultDevice();
		for (int i = 0; i < defaultDevices.length; i++) {
			VirtualDevice device = defaultDevices[i];
			if (device instanceof VirtualIDEController) {
				ideCtrlKey = device.getKey();
				int[] dArr = ((VirtualIDEController) device).getDevice();
				if (dArr != null) {
					cdRomUnitNumber = dArr.length + 1;
					for (int k = 0; k < dArr.length; k++) {
						if (cdRomUnitNumber <= dArr[k]) {
							cdRomUnitNumber = dArr[k] + 1;
						}
					}
				} else {
					cdRomUnitNumber = 0;
				}
				i = defaultDevices.length + 1;
			}
		}
		cdRom.setControllerKey(ideCtrlKey);
		cdRom.setUnitNumber(cdRomUnitNumber);
		// cdRom.setKey(cdRomKey);
		cdRom.setKey(60);

		// 文件
		VirtualMachineFileInfo vmfi = new VirtualMachineFileInfo();
		// VmPathName=[datastore1] testOPs/testOPs.vmx
		vmfi.setVmPathName("[" + dsName + "]");
		vmConfig.setFiles(vmfi);

		return vmConfig;
	}

	/**
	 * 克隆虚拟机,保持虚拟机当前的模板状态,即虚拟机克隆成虚拟机,模板克隆成模板<br>
	 * 虚拟机的操作:克隆<br>
	 * 模板的操作:克隆
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param dcId
	 * @param vmId
	 * @param cloneName
	 * @return
	 */
	public static Map<String, Object> cloneVM(String url, String username,
			String password, String dcId, String vmId, String cloneName) {
		return cloneVM(url, username, password, dcId, vmId, cloneName, true, "");
	}

	/**
	 * 虚拟机克隆成模板<br>
	 * 虚拟机的操作:克隆为模板<br>
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param dcId
	 * @param vmId
	 * @param cloneName
	 * @return
	 */
	public static Map<String, Object> cloneVM2Template(String url,
			String username, String password, String dcId, String vmId,
			String cloneName) {
		return cloneVM(url, username, password, dcId, vmId, cloneName, false,
				"");
	}

	/**
	 * 模板部署为虚拟机<br>
	 * 模板的操作:从该模板部署虚拟机
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param dcId
	 * @param vmId
	 * @param cloneName
	 * @param rpId
	 * @return
	 */
	public static Map<String, Object> deployTemplate2VM(String url,
			String username, String password, String dcId, String vmId,
			String cloneName, String rpId) {
		return cloneVM(url, username, password, dcId, vmId, cloneName, false,
				rpId);
	}

	/**
	 * 克隆虚拟机<br>
	 * 虚拟机的操作:克隆、克隆为模板、转换成模板(markAsTemplate)<br>
	 * 模板的操作:克隆、转换成虚拟机(markAsVirtualMachine)、从该模板部署虚拟机<br>
	 * 只有"从该模板部署虚拟机"时才需要rpId
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param dcId
	 * @param vmId
	 * @param cloneName
	 * @param isKeep
	 * @param rpId
	 * @return
	 */
	private static Map<String, Object> cloneVM(String url, String username,
			String password, String dcId, String vmId, String cloneName,
			boolean isKeep, String rpId) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		try {
			VIMCache cache = VIMCache.getInstance(url, username, password);
			ManagedObjectReference vmMor = cache.getVirtualMachine(vmId);

			if (vmMor != null) {
				ExtendedAppUtil ecb = getECB(url, username, password);

				// VirtualMachine.config(VirtualMachineConfigInfo).template标识虚拟机是否为模板
				// VirtualMachine.summary(VirtualMachineSummary).config(VirtualMachineConfigSummary).template标识虚拟机是否为模板
				VirtualMachineConfigInfo config = (VirtualMachineConfigInfo) getDynamicProperty(
						ecb, vmMor, DYNAMICPROPERTY_CONFIG);
				// 当前虚拟机的模板状态
				boolean currTemp = config.isTemplate();
				// 期望的虚拟机的模板状态
				boolean expectTemp = false;
				if (isKeep) {
					expectTemp = currTemp;
				} else {
					expectTemp = !currTemp;
				}

				cloneVM(resultMap, url, username, password, dcId, vmMor,
						cloneName, expectTemp, rpId);
			} else {
				if (vmMor == null) {
					recordResultMapError(resultMap, "虚拟机'" + vmId + "'不存在");
				}
			}
		} catch (Exception e) {
			LOGGER.error("cloneVM error, ", e);
			recordResultMapException(resultMap, e);
		}

		return resultMap;
	}

	/**
	 * 克隆虚拟机,CloneVM_Task<br>
	 * <br>
	 * 是否转换为模板
	 * 
	 * @param resultMap
	 * @param url
	 * @param username
	 * @param password
	 * @param dcId
	 * @param vmMor
	 * @param cloneName
	 * @param isTemplate
	 * @param rpId
	 * @return
	 */
	private static void cloneVM(HashMap<String, Object> resultMap, String url,
			String username, String password, String dcId,
			ManagedObjectReference vmMor, String cloneName, boolean isTemplate,
			String rpId) {
		try {
			VIMCache cache = VIMCache.getInstance(url, username, password);

			ManagedObjectReference dcMor = cache.getDatacenter(dcId);
			// ManagedObjectReference vmMor = cache.getVirtualMachine(vmId);
			ManagedObjectReference vmFolderMor = cache.getVMFolder(dcId);
			ManagedObjectReference rpMor = null;
			if (!Util.normalizeString(rpId).equals("")) {
				rpMor = cache.getResourcePool(rpId);
			}

			if ((dcMor != null) && (vmMor != null) && (vmFolderMor != null)) {
				ExtendedAppUtil ecb = getECB(url, username, password);

				VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();
				VirtualMachineRelocateSpec relocSpec = new VirtualMachineRelocateSpec();
				cloneSpec.setLocation(relocSpec);
				cloneSpec.setPowerOn(false);
				cloneSpec.setTemplate(isTemplate);
				// 如果是从模板部署为虚拟机需要指定资源池
				if (rpMor != null) {
					relocSpec.setPool(rpMor);
				}

				ManagedObjectReference taskMor = ecb.getServiceConnection3()
						.getService().cloneVM_Task(vmMor, vmFolderMor,
								cloneName, cloneSpec);

				execTaskAndRecordResultMap(ecb, taskMor, resultMap);
			} else {
				if (dcMor == null) {
					recordResultMapError(resultMap, "数据中心'" + dcId + "'不存在");
				}
				if (vmMor == null) {
					// recordResultMapError(resultMap, "虚拟机'" + vmId
					// + "'不存在");
					recordResultMapError(resultMap, "虚拟机'" + "'不存在");
				}
				if (vmFolderMor == null) {
					recordResultMapError(resultMap, "数据中心'" + dcId
							+ "'的vmFolder不存在");
				}
			}
		} catch (Exception e) {
			LOGGER.error("cloneVM error, ", e);
			recordResultMapException(resultMap, e);
		}
	}

	/**
	 * 将虚拟机转换为模板<br>
	 * 虚拟机的操作:转换成模板(markAsTemplate)<br>
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param dcId
	 * @param vmId
	 * @return
	 */
	public static Map<String, Object> markAsTemplate(String url,
			String username, String password, String dcId, String vmId) {

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		try {
			VIMCache cache = VIMCache.getInstance(url, username, password);
			ManagedObjectReference vmMor = cache.getVirtualMachine(vmId);

			if (vmMor != null) {
				ExtendedAppUtil ecb = getECB(url, username, password);

				// VirtualMachine.config(VirtualMachineConfigInfo).template标识虚拟机是否为模板
				// VirtualMachine.summary(VirtualMachineSummary).config(VirtualMachineConfigSummary).template标识虚拟机是否为模板

				ecb.getServiceConnection3().getService().markAsTemplate(vmMor);

				// 记录处理正确的标识
				recordResultMapSuccess(resultMap);
			} else {
				if (vmMor == null) {
					recordResultMapError(resultMap, "虚拟机'" + vmId + "'不存在");
				}
			}
		} catch (FileFault ff) {
			LOGGER.error("markAsTemplate error, ", ff);
			recordResultMapError(resultMap, "访问虚拟机文件错误");
		} catch (InvalidPowerState ips) {
			LOGGER.error("markAsTemplate error, ", ips);
			recordResultMapError(resultMap, "虚拟机电源未关闭");
		} catch (InvalidState is) {
			LOGGER.error("markAsTemplate error, ", is);
			recordResultMapError(resultMap, "虚拟机当前状态异常");
		} catch (NotSupported ns) {
			LOGGER.error("markAsTemplate error, ", ns);
			recordResultMapError(resultMap, "虚拟机不支持");
		} catch (RuntimeFault rf) {
			LOGGER.error("markAsTemplate error, ", rf);
			recordResultMapError(resultMap, "出现运行态故障");
		} catch (VmConfigFault vcf) {
			LOGGER.error("markAsTemplate error, ", vcf);
			recordResultMapError(resultMap, "模板不兼容");
		} catch (Exception e) {
			LOGGER.error("markAsTemplate error, ", e);
			recordResultMapException(resultMap, e);
		}
		return resultMap;
	}

	/**
	 * 将模板转换为虚拟机<br>
	 * 模板的操作:转换成虚拟机(markAsVirtualMachine)<br>
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param dcId
	 * @param vmId
	 * @param rpId
	 * @return
	 */
	public static Map<String, Object> markAsVirtualMachine(String url,
			String username, String password, String dcId, String vmId,
			String rpId) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		try {
			VIMCache cache = VIMCache.getInstance(url, username, password);
			ManagedObjectReference vmMor = cache.getVirtualMachine(vmId);
			ManagedObjectReference rpMor = cache.getResourcePool(rpId);

			if ((vmMor != null) && (rpMor != null)) {
				ExtendedAppUtil ecb = getECB(url, username, password);

				// VirtualMachine.config(VirtualMachineConfigInfo).template标识虚拟机是否为模板
				// VirtualMachine.summary(VirtualMachineSummary).config(VirtualMachineConfigSummary).template标识虚拟机是否为模板

				ecb.getServiceConnection3().getService().markAsVirtualMachine(
						vmMor, rpMor, null);

				// 记录处理正确的标识
				recordResultMapSuccess(resultMap);
			} else {
				if (vmMor == null) {
					recordResultMapError(resultMap, "虚拟机'" + vmId + "'不存在");
				}
				if (rpMor == null) {
					recordResultMapError(resultMap, "'资源池" + rpId + "'不存在");
				}
			}
		} catch (FileFault ff) {
			LOGGER.error("markAsTemplate error, ", ff);
			recordResultMapError(resultMap, "访问虚拟机文件错误");
		} catch (InvalidDatastore ids) {
			LOGGER.error("markAsTemplate error, ", ids);
			recordResultMapError(resultMap, "在目标存储上无法执行当前操作");
		} catch (InvalidState is) {
			LOGGER.error("markAsTemplate error, ", is);
			recordResultMapError(resultMap, "虚拟机当前状态异常");
		} catch (NotSupported ns) {
			LOGGER.error("markAsTemplate error, ", ns);
			recordResultMapError(resultMap, "虚拟机不支持");
		} catch (RuntimeFault rf) {
			LOGGER.error("markAsTemplate error, ", rf);
			recordResultMapError(resultMap, "出现运行态故障");
		} catch (VmConfigFault vcf) {
			LOGGER.error("markAsTemplate error, ", vcf);
			recordResultMapError(resultMap, "模板不兼容");
		} catch (Exception e) {
			LOGGER.error("markAsTemplate error, ", e);
			recordResultMapException(resultMap, e);
		}
		return resultMap;
	}

	/**
	 * 修改虚拟机,ReconfigVM_Task<br>
	 * 只提供修改CPU数,内存数,增加一个新的磁盘等功能,不支持其他如增减磁盘、增加光驱、增减网络等操作<br>
	 * CPU数(2个)、内存数、磁盘数只有大于0时才会引发变化,其中磁盘为新增加一块磁盘。
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param dcId
	 * @param numCpus
	 * @param numCores
	 * @param memoryMB
	 * @param diskSizeMB
	 * @return
	 */
	public static Map<String, Object> reconfigVM(String url, String username,
			String password, String dcId, String vmId, int numCpus,
			int numCores, long memoryMB, long diskSizeMB) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		try {
			VIMCache cache = VIMCache.getInstance(url, username, password);

			ManagedObjectReference dcMor = cache.getDatacenter(dcId);
			ManagedObjectReference vmMor = cache.getVirtualMachine(vmId);

			if ((dcMor != null) && (vmMor != null)) {
				ExtendedAppUtil ecb = getECB(url, username, password);

				VirtualMachineConfigSpec vmConfig = new VirtualMachineConfigSpec();
				if (numCpus > 0) {
					vmConfig.setNumCPUs(numCpus);
				}
				if (numCores > 0) {
					vmConfig.setNumCoresPerSocket(numCores);
				}
				if (memoryMB > 0) {
					vmConfig.setMemoryMB(memoryMB);
				}

				// 增加一个新磁盘
				if (diskSizeMB > 0) {
					// 获取虚拟机原有配置信息
					VirtualMachineConfigInfo vmConfigInfo = (VirtualMachineConfigInfo) ecb
							.getServiceUtil3().getDynamicProperty(vmMor,
									DYNAMICPROPERTY_CONFIG);

					if (vmConfigInfo != null) {

						// 设置device的变化
						VirtualDeviceConfigSpec diskConfig = new VirtualDeviceConfigSpec();
						vmConfig
								.setDeviceChange(new VirtualDeviceConfigSpec[] { diskConfig });
						diskConfig
								.setFileOperation(VirtualDeviceConfigSpecFileOperation.create);
						diskConfig
								.setOperation(VirtualDeviceConfigSpecOperation.add);

						// 磁盘
						VirtualDisk disk = new VirtualDisk();
						diskConfig.setDevice(disk);

						// SCSI控制器
						VirtualLsiLogicController scsiCtrl = null;
						// 已有磁盘
						VirtualDisk oldDisk = null;

						for (VirtualDevice vDevice : vmConfigInfo.getHardware()
								.getDevice()) {
							if (vDevice instanceof VirtualLsiLogicController) {
								scsiCtrl = (VirtualLsiLogicController) vDevice;
							}
							if (vDevice instanceof VirtualDisk) {
								oldDisk = (VirtualDisk) vDevice;
							}
							if ((scsiCtrl != null) && (oldDisk != null)) {
								break;
							}
						}

						if ((scsiCtrl != null) && (oldDisk != null)) {
							disk.setControllerKey(scsiCtrl.getKey());
							disk.setUnitNumber(vmConfigInfo.getHardware()
									.getDevice().length + 1);
							disk.setCapacityInKB(diskSizeMB * 1024);
							disk.setKey(-1);

							VirtualDiskFlatVer2BackingInfo backingInfo = new VirtualDiskFlatVer2BackingInfo();
							disk.setBacking(backingInfo);

							if (oldDisk.getBacking() instanceof VirtualDiskFlatVer2BackingInfo) {
								VirtualDiskFlatVer2BackingInfo oldBacking = (VirtualDiskFlatVer2BackingInfo) oldDisk
										.getBacking();
								backingInfo.setDiskMode(oldBacking
										.getDiskMode());
								backingInfo.setThinProvisioned(oldBacking
										.getThinProvisioned());
								backingInfo.setEagerlyScrub(oldBacking
										.getEagerlyScrub());
								backingInfo.setDatastore(oldBacking
										.getDatastore());
								backingInfo.setFileName("");

							} else {
								// VirtualDeviceBackingInfo类型不对
							}
						} else {
							// SCSI和磁盘为空
						}
					} else {
						// 配置信息为空
					}
				}

				ManagedObjectReference taskMor = ecb.getServiceConnection3()
						.getService().reconfigVM_Task(vmMor, vmConfig);

				execTaskAndRecordResultMap(ecb, taskMor, resultMap);
			} else {
				if (dcMor == null) {
					recordResultMapError(resultMap, "数据中心'" + dcId + "'不存在");
				}
				if (vmMor == null) {
					recordResultMapError(resultMap, "虚拟机'" + vmId + "'不存在");
				}
			}
		} catch (Exception e) {
			LOGGER.error("cloneVM error, ", e);
			recordResultMapException(resultMap, e);
		}

		return resultMap;
	}

	/**
	 * 删除虚拟机,destroy_Task
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param vmId
	 * @return
	 */
	public static Map<String, Object> destroyVM(String url, String username,
			String password, String vmId) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		try {
			VIMCache cache = VIMCache.getInstance(url, username, password);
			ManagedObjectReference mor = cache.getVirtualMachine(vmId);
			if (mor != null) {
				ExtendedAppUtil ecb = getECB(url, username, password);

				ManagedObjectReference taskMor = ecb.getServiceConnection3()
						.getService().destroy_Task(mor);

				execTaskAndRecordResultMap(ecb, taskMor, resultMap);
			} else {
				recordResultMapError(resultMap, "虚拟机'" + vmId + "'不存在");
			}
		} catch (Exception e) {
			LOGGER.error("destroyVM error, ", e);
			recordResultMapException(resultMap, e);
		}

		return resultMap;
	}

	/**
	 * 修改虚拟机状态,起停等操作
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param vmId
	 * @param operation
	 * @return
	 */
	public static Map<String, Object> vmOperation(String url, String username,
			String password, String vmId, String operation) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		try {
			VIMCache cache = VIMCache.getInstance(url, username, password);
			ManagedObjectReference mor = cache.getVirtualMachine(vmId);
			if (mor != null) {
				ExtendedAppUtil ecb = getECB(url, username, password);

				ManagedObjectReference taskMor = null;

				if (operation.equals(OP_POWERON)) {
					taskMor = ecb.getServiceConnection3().getService()
							.powerOnVM_Task(mor, null);
				} else if (operation.equals(OP_POWEROFF)) {
					taskMor = ecb.getServiceConnection3().getService()
							.powerOffVM_Task(mor);
				} else if (operation.equals(OP_SUSPEND)) {
					taskMor = ecb.getServiceConnection3().getService()
							.suspendVM_Task(mor);
				} else if (operation.equals(OP_RESET)) {
					taskMor = ecb.getServiceConnection3().getService()
							.resetVM_Task(mor);
				} else if ((operation.equals(OP_SHUTDOWN))
						|| (operation.equals(OP_STANDBY))
						|| (operation.equals(OP_REBOOT))) {
					vmGuestOperation(ecb, mor, operation, resultMap);
				}
				if (taskMor != null) {
					execTaskAndRecordResultMap(ecb, taskMor, resultMap);
				}

				if (!resultMap.containsKey(INFO_STATE)) {
					recordResultMapError(resultMap, "虚拟机操作'" + operation
							+ "'无法识别");
				}
			} else {
				recordResultMapError(resultMap, "虚拟机'" + vmId + "'不存在");
			}
		} catch (Exception e) {
			LOGGER.error("vmOperation error, ", e);
			recordResultMapException(resultMap, e);
		}

		return resultMap;
	}

	/**
	 * 虚拟机操作系统操作
	 * 
	 * @param ecb
	 * @param mor
	 * @param resultMap
	 */
	private static void vmGuestOperation(ExtendedAppUtil ecb,
			ManagedObjectReference mor, String operation,
			HashMap<String, Object> resultMap) {
		try {
			if (operation.equals(OP_SHUTDOWN)) {
				ecb.getServiceConnection3().getService().shutdownGuest(mor);
			} else if (operation.equals(OP_STANDBY)) {
				ecb.getServiceConnection3().getService().standbyGuest(mor);
			} else if (operation.equals(OP_REBOOT)) {
				ecb.getServiceConnection3().getService().rebootGuest(mor);
			}
		} catch (InvalidPowerState ips) {
			LOGGER.error("vmShutdownGuest error, ", ips);
			recordResultMapError(resultMap, "虚拟机当前未开机");
		} catch (InvalidState is) {
			LOGGER.error("vmShutdownGuest error, ", is);
			recordResultMapError(resultMap, "虚拟机当前状态异常");
		} catch (RuntimeFault rf) {
			LOGGER.error("vmShutdownGuest error, ", rf);
			recordResultMapError(resultMap, "出现运行态故障");
		} catch (TaskInProgress tp) {
			LOGGER.error("vmShutdownGuest error, ", tp);
			recordResultMapError(resultMap, "虚拟机繁忙");
		} catch (ToolsUnavailable tu) {
			LOGGER.error("vmShutdownGuest error, ", tu);
			recordResultMapError(resultMap, "虚拟机的VMware Tools未运行");
		} catch (Exception e) {
			recordResultMapException(resultMap, e);
		}
	}

	/**
	 * 获取虚拟机的摘要信息
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param vmId
	 * @return
	 */
	public static Map<String, Object> getSummary(String url, String username,
			String password, String vmId) {

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		try {
			VIMCache cache = VIMCache.getInstance(url, username, password);
			ManagedObjectReference mor = cache.getVirtualMachine(vmId);
//			System.out.println("vmId--:"+vmId+"===mor-是否存在---------------------------"+mor);
			
			if (mor != null) {
				ExtendedAppUtil ecb = getECB(url, username, password);

				// 配置
				VirtualMachineConfigInfo config = (VirtualMachineConfigInfo) getDynamicProperty(
						ecb, mor, DYNAMICPROPERTY_CONFIG);

				// 运行
				VirtualMachineRuntimeInfo runtime = (VirtualMachineRuntimeInfo) getDynamicProperty(
						ecb, mor, DYNAMICPROPERTY_RUNTIME);
				
				
				// 摘要
				VirtualMachineSummary summary = (VirtualMachineSummary) getDynamicProperty(
						ecb, mor, DYNAMICPROPERTY_SUMMARY);

				// 存储列表
				ManagedObjectReference[] dsList = (ManagedObjectReference[]) getDynamicProperty(
						ecb, mor, DYNAMICPROPERTY_DATASTORE);

				// 网络列表
				ManagedObjectReference[] netList = (ManagedObjectReference[]) getDynamicProperty(
						ecb, mor, DYNAMICPROPERTY_NETWORK);

				// List<Object> properties = getDynamicProperties(ecb, mor,
				// new String[] { DYNAMICPROPERTY_CONFIG,
				// DYNAMICPROPERTY_SUMMARY,
				// DYNAMICPROPERTY_DATASTORE,
				// DYNAMICPROPERTY_NETWORK });
				// VirtualMachineConfigInfo config = (VirtualMachineConfigInfo)
				// properties
				// .get(0);
				// VirtualMachineSummary summary = (VirtualMachineSummary)
				// properties
				// .get(1);
				// ManagedObjectReference[] dsList = (ManagedObjectReference[])
				// properties
				// .get(2);
				// ManagedObjectReference[] netList = (ManagedObjectReference[])
				// properties
				// .get(3);

				String hoId = "";
				if ((runtime != null) && (runtime.getHost() != null)) {
					hoId = runtime.getHost().get_value();
				}
				resultMap.put(SYNC_VM_HOID, hoId);
				resultMap.put(SYNC_COMMON_VID, mor.get_value());
				resultMap.put(SYNC_COMMON_NAME, config.getName());
				int numCore = config.getHardware().getNumCoresPerSocket();
				resultMap.put(SYNC_VM_NUMCORE, numCore);
				if ((config != null) && (summary != null)) {
					// 常规
					// 客户机操作系统
					resultMap.put(SUMMARY_COMMON_GUESTFULLNAME, config
							.getGuestFullName());

					// 虚拟机版本,样本数据vmx-08
					resultMap.put(SUMMARY_COMMON_VERSION, config.getVersion());
					// CPU
					resultMap.put(SUMMARY_COMMON_CPU, summary.getConfig()
							.getNumCpu());
					// 内存,单位为MB
					resultMap.put(SUMMARY_COMMON_MEMORYSIZEMB,
							getByteSizeStrFromMB(summary.getConfig()
									.getMemorySizeMB()));

					// 内存开销,current overhead reservation,单位为字节
					resultMap.put(SUMMARY_COMMON_MEMORYOVERHEAD,
							getByteSizeStrFromBytes(summary.getRuntime()
									.getMemoryOverhead()));
					// VMware Tools:
					// 运行状态toolsRunningStatus
					// VirtualMachineToolsRunningStatus:guestToolsExecutingScripts,guestToolsNotRunning,guestToolsRunning
					// 状态(toolsStatus,toolsVersionStatus[Since4.0],toolsVersionStatus2[Since5.0])
					// VirtualMachineToolsVersionStatus:guestToolsBlacklisted[Since5.0],guestToolsCurrent,guestToolsNeedUpgrade,guestToolsNotInstalled,
					// guestToolsSupportedNew[Since5.0],guestToolsSupportedOld[Since5.0],guestToolsTooNew[Since5.0],guestToolsTooOld[Since5.0],guestToolsUnmanaged
					// 正在运行(当前版本)
					// http://pubs.vmware.com/vsphere-50/topic/com.vmware.wssdk.apiref.doc_50/vim.vm.Summary.GuestSummary.html
					// toolsStatus
					// toolsVersionStatus[Since4.0]
					// toolsVersionStatus2[Since5.0]
					// 暂时只获取toolsVersionStatus
					resultMap.put(SUMMARY_COMMON_VMWARETOOLS,
							cache.getEnum(summary.getGuest()
									.getToolsRunningStatus())
									+ "("
									+ cache.getEnum(summary.getGuest()
											.getToolsVersionStatus()) + ")");
					// IP地址
					resultMap
							.put(SUMMARY_COMMON_IPADDRESS, Util
									.normalizeString(summary.getGuest()
											.getIpAddress()));
					// DNS名称
					resultMap.put(SUMMARY_COMMON_HOSTNAME, Util
							.normalizeString(summary.getGuest().getHostName()));
					// XXX EVC模式,暂时无法获取
					// String SUMMARY_COMMON_EVCMODEL = "evcmodel";
					// 状况,poweredOff,poweredOn,suspended
					// powerstate在alarmDesc中有翻译
//					resultMap.put(SUMMARY_COMMON_POWERSTATE, cache
//							.getAlarmDesc(summary.getRuntime().getPowerState()
//									.getValue()));
					
					String powerState = "";
					if (runtime != null) {
						powerState = runtime.getPowerState().toString();
					}
					resultMap.put(SYNC_VM_POWERSTATE, powerState);
					// 主机
					resultMap.put(SUMMARY_COMMON_HOST, getEntityName(ecb,
							summary.getRuntime().getHost()));
					// XXX 活动任务,暂时无法获取
					// String SUMMARY_COMMON_ACTIVETASK = "activetask";
					// XXX vSphere HA保护,暂时无法获取
					// String SUMMARY_COMMON_VSPHEREHA = "vsphereha";

					// 资源
					// 已消耗的主机CPU,Basic CPU performance
					// statistics,单位MHz,大概是这个值此值变化比较快
					resultMap.put(SUMMARY_RESOURCE_OVERALLCPUUSAGE, summary
							.getQuickStats().getOverallCpuUsage()
							+ MHZ);
					// 已消耗的主机内存：consumed host memory,单位为MB
					resultMap.put(SUMMARY_RESOURCE_HOSTMEMORYUSAGE,
							getByteSizeStrFromMB(summary.getQuickStats()
									.getHostMemoryUsage()));
					// 活动客户机内存：active guest memory，单位为MB
					resultMap.put(SUMMARY_RESOURCE_GUESTMEMORYUSAGE,
							getByteSizeStrFromMB(summary.getQuickStats()
									.getGuestMemoryUsage()));
					// 未共享的存储
					resultMap.put(SUMMARY_RESOURCE_STORAGE_UNSHARED,
							getByteSizeStrFromBytes(summary.getStorage()
									.getUnshared()));
					// 已使用的存储
					resultMap.put(SUMMARY_RESOURCE_STORAGE_COMMITTED,
							getByteSizeStrFromBytes(summary.getStorage()
									.getCommitted()));
					// 置备的存储
					resultMap.put(SUMMARY_RESOURCE_STORAGE_ALL,
							getByteSizeStrFromBytes(summary.getStorage()
									.getCommitted()
									+ summary.getStorage().getUncommitted()));

					// 存储列表
					DatastoreMgr.recordResultMapDsList(resultMap, ecb, dsList);

					// 网络列表
					NetworkMgr.recordResultMapNetList(resultMap, ecb, netList);

					// 记录处理正确的标识
					recordResultMapSuccess(resultMap);
				} else {
					recordResultMapError(resultMap, "获取'"
							+ DYNAMICPROPERTY_CONFIG + "'或'"
							+ DYNAMICPROPERTY_SUMMARY + "'信息为空");
				}
			} else {
				recordResultMapError(resultMap, "虚拟机'" + vmId + "'不存在");
			}
		} catch (Exception e) {
			LOGGER.error("getSummary error, ", e);
			recordResultMapException(resultMap, e);
		}

		return resultMap;
	}
}
