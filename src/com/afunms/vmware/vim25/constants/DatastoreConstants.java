package com.afunms.vmware.vim25.constants;

/**
 * 数据存储中定义的常量
 * 
 * @author LXL
 * 
 */
public interface DatastoreConstants extends VIMConstants {

	// 摘要Map中的key值
	// 常规
	// 位置
	String SUMMARY_COMMON_URL = "url";
	// 类型
	String SUMMARY_COMMON_TYPE = "type";
	// 所连接主机数目
	String SUMMARY_COMMON_HOST = "host";
	// 虚拟机和模板
	String SUMMARY_COMMON_VM = "vm";

	// 容量
	// 容量
	String SUMMARY_CAPACITY_CAPACITY = "capacity";
	// 置备的空间=capacity+uncommitted-freespace
	String SUMMARY_CAPACITY_STORAGEALL = "storageall";
	// 可用空间
	String SUMMARY_CAPACITY_FREESPACE = "freespace";
	// 上次更新时间
	String SUMMARY_CAPACITY_TIMESTAMP = "timestamp";

}