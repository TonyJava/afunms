package com.afunms.vmware.vim25.constants;

/**
 * 主机中定义的常量
 * 
 * @author LXL
 * 
 */
public interface HostConstants extends VIMConstants {

	// 摘要Map中的key值
	// 常规
	// 制造商
	String SUMMARY_COMMON_VENDOR = "vendor";
	// 型号,样本:PowerEdge R710
	String SUMMARY_COMMON_MODEL = "model";
	// CPU内核
	String SUMMARY_COMMON_CPUCORE = "cpucore";
	// 处理器类型
	String SUMMARY_COMMON_CPUMODEL = "cpumodel";
	// 许可证,暂时无法获取
	// String SUMMARY_COMMON_LICENSE = "license";
	// 处理器插槽
	String SUMMARY_COMMON_NUMCPUKGS = "numcpupkgs";
	// 每个插槽的内核数
	String SUMMARY_COMMON_NUMCORESPERSOCKET = "numcorespersocket";
	// 逻辑处理器
	String SUMMARY_COMMON_NUMCPUTHREADS = "numcputhreads";
	// 超线程(活动),暂时无法获取
	// 网卡数目
	String SUMMARY_COMMON_NUMNICS = "numnics";
	// 状况(已连接)
	String SUMMARY_COMMON_CONNECTIONSTATE = "connectionstate";
	// 虚拟机和模板
	String SUMMARY_COMMON_VM = "vm";
	// vMotion已启用(否)
	String SUMMARY_COMMON_VMOTION = "vmotion";
	// VMWare EVC模式,暂时无法获取
	// vSphere HA状况(正在运行(主机)),暂时无法获取,
	// summary.getRuntime().getDasHostState().getState()=master
	// String SUMMARY_COMMON_HA = "ha";
	// 已配置FT的主机(否),暂时无法获取,summary.getConfig().getFaultToleranceEnabled()
	// String SUMMARY_COMMON_FT = "ft";
	// 活动任务,暂时无法获取
	// 主机配置文件,暂时无法获取
	// 映像配置文件,暂时无法获取
	// 配置文件合规性(不可用),暂时无法获取
	// DirectPath I/O(不受支持),暂时无法获取

	// 资源
	// CPU使用情况,单位MHz
	String SUMMARY_RESOURCE_OVERALLCPUUSAGE = "overallcpuusage";
	// 内存使用情况,单位MB
	String SUMMARY_RESOURCE_OVERALLMEMORYUSAGE = "overallmemoryusage";
	// 内存容量,单位MB
	String SUMMARY_RESOURCE_MEMORYSIZEMB = "memorysizemb";

	// Fault Tolerance
	// Fault Tolerance 版本,暂时无法获取
	// String SUMMARY_FT_VERSION = "ftversion";
	// 主虚拟机总数,暂时无法获取
	// 已打开电源的主虚拟机总数,暂时无法获取
	// 辅助虚拟机总数,暂时无法获取
	// 已打开电源的辅助虚拟机总数,暂时无法获取

}
