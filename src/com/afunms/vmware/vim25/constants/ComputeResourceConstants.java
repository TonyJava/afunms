package com.afunms.vmware.vim25.constants;

/**
 * 主机群集中定义的常量
 * 
 * @author LXL
 * 
 */
public interface ComputeResourceConstants extends VIMConstants {

	// 摘要Map中的key值
	// 常规
	// vSphere DRS
	// 摘要Map中的key值
	// 常规
	// vSphere DRS
	String SUMMARY_COMMON_DRS = "drs";
	// vSphere HA
	String SUMMARY_COMMON_HA = "ha";
	// VMWare EVC 模式,暂时无法获取
	String SUMMARY_COMMON_EVC = "evc";
	// 总CPU资源,,单位MHz
	String SUMMARY_COMMON_TOTALCPU = "totalcpu";
	// 总内存,单位字节
	String SUMMARY_COMMON_TOTALMEMORY = "totalmemory";
	// 总存储
	String SUMMARY_COMMON_TOTALDSSIZEMB = "totaldssizemb";
	// 主机数summary.getNumHosts(有效主机数summary.getNumEffectiveHosts)
	String SUMMARY_COMMON_NUMHOSTS = "numhosts";
	// 处理器总数
	String SUMMARY_COMMON_NUMCPUCORES = "numcpucores";
	// 数据存储群集数
	String SUMMARY_COMMON_NUMSTORAGEPODS = "numstoragepods";
	// 总数据存储数
	String SUMMARY_COMMON_NUMDSS = "numdss";
	// 虚拟机和模板
	String SUMMARY_COMMON_NUMVMS = "numvms";
	// 使用vMotion的总迁移数,暂时无法获取
	String SUMMARY_COMMON_NUMVMOTIONS = "numvmotions";

	// vSphere HA,应该是对应ClusterDasConfigInfo类,暂时无法获取
	// 接入控制(已启用),admissionControlEnabled
	// 当前故障切换容量(1个主机)
	// 已配置的故障切换容量(1个主机)
	// 0.当前故障切换容量(1个主机)和已配置的故障切换容量(1个主机),可能与admissionControlPolicy有关,
	// 1.ClusterFailoverHostAdmissionControlPolicy只能指定一个主机,
	// 2.ClusterFailoverLevelAdmissionControlPolicy可以预留一组主机,
	// 3.ClusterFailoverResourcesAdmissionControlPolicy保留CPU和内存
	// 主机监控(已启用),hostMonitoring
	// 虚拟机监控(已禁用),vmMonitoring
	// 应用程序监控(已禁用),可能是vmMonitoring

	// vSphere DRS,应该是对应ClusterDrsConfigInfo类,暂时无法获取
	// 迁移自动化级别(全自动),defaultVmBehavior(fullyAutomated全自动,manual手工,partiallyAutomated部分自动化)
	// 电源管理自动化级别(关闭)
	// DRS建议(0)
	// DRS故障(0)
	// 迁移阀值(应用优先级为1、2和3的建议),可能是vmotionRate
	// 目标主机负载标准偏差(<=0.2)
	// 当前主机负载标准偏差(0.044 负载已平衡)

}
