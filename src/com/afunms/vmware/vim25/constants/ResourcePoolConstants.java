package com.afunms.vmware.vim25.constants;

/**
 * 资源池中定义的常量
 * 
 * @author LXL
 * 
 */
public interface ResourcePoolConstants extends VIMConstants {

	// 摘要Map中的key值
	// 常规
	// 虚拟机和模板
	String SUMMARY_COMMON_VM = "vm";
	// 已打开电源的虚拟机虚拟机和模板
	String SUMMARY_COMMON_OPENVM = "openvm";
	// 子资源池
	String SUMMARY_COMMON_RP = "rp";
	// 子vApp
	String SUMMARY_COMMON_VAPP = "vapp";

}