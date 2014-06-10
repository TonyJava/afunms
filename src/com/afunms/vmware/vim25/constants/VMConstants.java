package com.afunms.vmware.vim25.constants;

/**
 * 虚拟机中定义的常量
 * 
 * @author LXL
 * 
 */
public interface VMConstants extends VIMConstants {

	// 虚拟机操作
	// PowerOnVM_Task
	String OP_POWERON = "poweron";
	// PowerOffVM_Task
	String OP_POWEROFF = "poweroff";
	// SuspendVM_Task
	String OP_SUSPEND = "suspend";
	// ResetVM_Task
	String OP_RESET = "reset";
	// ShutdownGuest
	String OP_SHUTDOWN = "shutdown";
	// StandbyGuest
	String OP_STANDBY = "standby";
	// RebootGuest
	String OP_REBOOT = "reboot";

	// 电源状态,开机
	String POWERSTATE_POWERON = "poweredOn";

	// 磁盘类型
	String DISKTYPE_THICK = "thick";
	String DISKTYPE_EAGERZEROEDTHINK = "eagerZeroedThick";
	String DISKTYPE_THIN = "thin";

	// 磁盘模式
	String DISKMODE_PERSISTENT = "persistent";

	// 网卡地址模式
	String ETHERNETCARD_ADDRESSTYPE_GENERATED = "generated";

	// 网卡默认名称
	String ETHERNETCARD_DEFAULT_NAME = "VM Network";

	// 摘要Map中的key值
	// 常规
	// 客户机操作系统
	String SUMMARY_COMMON_GUESTFULLNAME = "guestfullname";
	// 虚拟机版本,样本数据vmx-08
	String SUMMARY_COMMON_VERSION = "version";
	// CPU
	String SUMMARY_COMMON_CPU = "numcpu";
	// 内存,单位为MB
	String SUMMARY_COMMON_MEMORYSIZEMB = "memorysizemb";
	// 内存开销,current overhead reservation,单位为字节
	// 未启动的虚拟机或者虚拟机模板也有值
	String SUMMARY_COMMON_MEMORYOVERHEAD = "memoryoverhead";
	// VMware Tools
	// 运行状态toolsRunningStatus
	// VirtualMachineToolsRunningStatus:guestToolsExecutingScripts,guestToolsNotRunning,guestToolsRunning
	// 状态(toolsStatus,toolsVersionStatus[Since4.0],toolsVersionStatus2[Since5.0])
	// VirtualMachineToolsVersionStatus:guestToolsBlacklisted[Since5.0],guestToolsCurrent,guestToolsNeedUpgrade,guestToolsNotInstalled,
	// guestToolsSupportedNew[Since5.0],guestToolsSupportedOld[Since5.0],guestToolsTooNew[Since5.0],guestToolsTooOld[Since5.0],guestToolsUnmanaged
	String SUMMARY_COMMON_VMWARETOOLS = "vmwaretools";
	// IP地址
	String SUMMARY_COMMON_IPADDRESS = "ipaddress";
	// DNS名称
	String SUMMARY_COMMON_HOSTNAME = "hostname";
	// EVC模式,暂时无法获取
	String SUMMARY_COMMON_EVCMODEL = "evcmodel";
	// 状况,poweredOff,poweredOn,suspended
	String SUMMARY_COMMON_POWERSTATE = "powerstate";
	// 主机
	String SUMMARY_COMMON_HOST = "host";
	// 活动任务,暂时无法获取
	String SUMMARY_COMMON_ACTIVETASK = "activetask";
	// vSphere HA保护,暂时无法获取
	String SUMMARY_COMMON_VSPHEREHA = "vsphereha";
	// 资源
	// 已消耗的主机CPU,Basic CPU performance statistics,单位MHz,大概是这个值此值变化比较快
	String SUMMARY_RESOURCE_OVERALLCPUUSAGE = "overallcpuusage";
	// 已消耗的主机内存：consumed host memory,单位为MB
	String SUMMARY_RESOURCE_HOSTMEMORYUSAGE = "hostmemoryusage";
	// 活动客户机内存：active guest memory，单位为MB
	String SUMMARY_RESOURCE_GUESTMEMORYUSAGE = "guestmemoryusage";
	// 未共享的存储
	String SUMMARY_RESOURCE_STORAGE_UNSHARED = "storageunshared";
	// 已使用的存储
	String SUMMARY_RESOURCE_STORAGE_COMMITTED = "storagecommitted";
	// 置备的存储
	// 置备的存储44.23GB为getCommitted+getUncommitted
	// 未共享和已使用为40.00GB
	// getCommitted=42949982899
	// getUncommitted=4537319424
	// getUnshared=42949982899
	String SUMMARY_RESOURCE_STORAGE_ALL = "storageall";

	// 存储器列表相关的定义放在VIMConstants

}
