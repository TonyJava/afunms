package com.afunms.vmware.vim25.constants;

import com.afunms.vmware.vim25.util.Util;

/**
 * VMWare中定义的常量
 * 
 * @author LXL
 */
public interface VIMConstants {

	// VMWare中枚举值
	// http://pubs.vmware.com/vsphere-51/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.doc%2Findex-enums.html

	// 数据中心dc对象的更新间隔,默认30分钟
	long DC_UPDATE_INTERVAL = 30 * Util.MINUTE_MILLISECONDS;

	// 管理实体的状态
	// http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.doc_50%2Fvim.ManagedEntity.Status.html
	// 状态未知
	String STATUE_GRAY = "gray";
	// 状态正常
	String STATUE_GREEN = "green";
	// 实体一定有问题
	String STATUE_RED = "red";
	// 实体可能有问题
	String STATUE_YELOW = "yellow";

	// 字节数单位
	// Byte、KB、MB、GB、TB、PB、EB、ZB、YB、DB、NB
	long KB_SIZE = 1024;
	long MB_SIZE = KB_SIZE * KB_SIZE;
	long GB_SIZE = MB_SIZE * KB_SIZE;
	long TB_SIZE = GB_SIZE * KB_SIZE;
	long PB_SIZE = TB_SIZE * KB_SIZE;
	String KB = " KB";
	String MB = " MB";
	String GB = " GB";
	String TB = " TB";
	String PB = " PB";

	// CPU单位
	String MHZ = "MHZ";

	// VMWare中的资源
	// http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.doc_50%2Fvmodl.ManagedObjectReference.html
	// http://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.wssdk.apiref.doc_50%2Fvim.ManagedEntity.html
	// vsdk_prog_guide.pdf的45/246页Table 4-1. Standalone ESX/ESXi and vCenter
	// Server Inventories
	String RESOURCE_CCR = "ClusterComputeResource";
	String RESOURCE_CR = "ComputeResource";
	String RESOURCE_DC = "Datacenter";
	String RESOURCE_DS = "Datastore";
	String RESOURCE_DVS = "DistributedVirtualSwitch";
	String RESOURCE_FOLDER = "Folder";
	String RESOURCE_HO = "HostSystem";
	// String RESOURCE_NETWORK = "Network";
	String RESOURCE_RP = "ResourcePool";
	String RESOURCE_VAPP = "VirtualApp";
	String RESOURCE_VM = "VirtualMachine";
	String RESOURCE_STORAGEPOD = "StoragePod";
	// 数据中心Managed Object - Datacenter的属性
	// 资源名同时也是属性名
	// 一个数据中心对应多个的资源包括数据存储和网络,使用自身的id做key
	String RESOURCE_DC_DATASTORE = "datastore";
	String RESOURCE_DC_NETWORK = "network";
	// 一个数据中心对应一个的资源包括4个文件夹,使用数据中心的dcId做key
	String RESOURCE_DC_DSFOLDER = "datastoreFolder";
	String RESOURCE_DC_HOFOLDER = "hostFolder";
	String RESOURCE_DC_NWFOLDER = "networkFolder";
	String RESOURCE_DC_VMFOLDER = "vmFolder";

	// VMWare中各种操作的权限
	// vsdk_prog_guide.pdf的215/246页Table D-1. Privileges Required for vCenter
	// Server and ESX/ESXi Operations

	// VMWare中任务返回的结果
	String INFO_STATE = "info.state";
	String INFO_ERROR = "info.error";
	String INFO_PROGRESS = "info.progress";
	String INFO_RESULT = "info.result";
	// 自扩展,为了虚拟机创建完成后返回主机的id
	String INFO_RESULT2 = "info.result2";

	// info.state中成功的状态
	String STATE_OK = "success";
	String STATE_ERROR = "error";

	// 错误信息
	String ERROR_TASK = "执行任务错误:";
	String ERROR_UNKNOWN = "未知错误:";

	// 动态属性
	// 任务Managed Object - Task的属性
	String DYNAMICPROPERTY_INFO = "info";
	// 虚拟机的配置,有很多对象都有config参数
	String DYNAMICPROPERTY_CONFIG = "config";
	// 动摘要
	String DYNAMICPROPERTY_SUMMARY = "summary";
	// 环境浏览
	String DYNAMICPROPERTY_ENVIRONMENTBROWSER = "environmentBrowser";
	// 运行态
	String DYNAMICPROPERTY_RUNTIME = "runtime";
	// 最新一页
	String DYNAMICPROPERTY_LATESTPAGE = "latestPage";
	// 描述
	String DYNAMICPROPERTY_DESCRIPTION = "description";
	// 名称
	String DYNAMICPROPERTY_NAME = "name";
	// 总体状态
	String DYNAMICPROPERTY_OVERALLSTATUS = "overallStatus";
	// 警报操作
	String DYNAMICPROPERTY_ALARMACTIONSENABLED = "alarmActionsEnabled";
	// 存储,datastore
	String DYNAMICPROPERTY_DATASTORE = RESOURCE_DC_DATASTORE;
	// 网络,network
	String DYNAMICPROPERTY_NETWORK = RESOURCE_DC_NETWORK;
	// 存储文件夹,datastoreFolder
	String DYNAMICPROPERTY_DATASTOREFOLDER = RESOURCE_DC_DSFOLDER;
	// 主机文件夹,hostFolder
	String DYNAMICPROPERTY_HOSTFOLDER = RESOURCE_DC_HOFOLDER;
	// 网络文件夹,networkFolder
	String DYNAMICPROPERTY_NETWORKFOLDER = RESOURCE_DC_NWFOLDER;
	// 虚拟机文件夹,vmFolder
	String DYNAMICPROPERTY_VMFOLDER = RESOURCE_DC_VMFOLDER;
	// 文件夹的childEntity
	String DYNAMICPROPERTY_CHILDENTITY = "childEntity";
	// 文件夹的childType
	String DYNAMICPROPERTY_CHILDTYPE = "childType";
	// 主机
	String DYNAMICPROPERTY_HOST = "host";
	// 虚拟机
	String DYNAMICPROPERTY_VM = "vm";
	// 资源池
	String DYNAMICPROPERTY_RESOURCEPOOL = "resourcePool";
	// 存储
	String DYNAMICPROPERTY_STORAGE = "storage";
	// 属主
	String DYNAMICPROPERTY_OWNER = "owner";
	// 硬件
	String DYNAMICPROPERTY_HARDWARE = "hardware";
	// 配置信息扩展
	String DYNAMICPROPERTY_CONFIGURATIONEX = "configurationEx";
	// 触发的告警
	String DYNAMICPROPERTY_TRIGGEREDALARMSTATE = "triggeredAlarmState";
	// 系统中定义的性能间隔,默认是过去一天,过去一周,过去一个月,过去一年
	String DYNAMICPROPERTY_HISTORICALINTERVAL = "historicalInterval";
	// 系统中支持的性能计数器
	String DYNAMICPROPERTY_PERFCOUNTER = "perfCounter";

	// 事件Map的key
	// 描述
	String EVENT_DESC = "desc";
	// 类型(严重性):信息和警告等
	// 好像需要根据时间的类型来确定严重性,如某种类型的Event就是警告等,下面的链接有相关的说明,不确定是否完整
	// https://www.vmware.com/pdf/Messages.pdf
	// http://www.veeam.com/support/vcEvents.html
	String EVENT_CATEGORY = "category";
	// 时间
	String EVENT_TIME = "time";
	// 任务,TaskEvent的info是TaskInfo,里面包含任务信息
	String EVENT_TASK = "task";
	// 目标,当前设备事件,目标通过虚拟机-->主机-->资源池-->数据中心,从下开始取第一个
	String EVENT_TARGET = "target";
	// 用户
	String EVENT_USER = "user";

	// 事件详情中的key
	// ExtendedEvent
	String EVENTDETAIL_KEY_EXTENDEDEVENT = "ExtendedEvent";
	// EventEx
	String EVENTDETAIL_KEY_EVENTEX = "EventEx";

	// 事件"类型"
	String EVENT_CATEGORY_UNKNOWN = "未知";

	// TaskMap的key
	// 名称
	String TASK_NAME = "name";
	// 目标
	String TASK_TARGET = "target";
	// 状态
	String TASK_STATE = "state";
	// 错误信息
	String TASK_ERROR = "error";
	// 启动者
	String TASK_USER = "user";
	// 请求开始时间
	String TASK_QUEUETIME = "queuetime";
	// 开始时间
	String TASK_STARTTIME = "starttime";
	// 完成时间
	String TASK_COMPLETETIME = "completetime";

	// 启动者_系统
	String TASK_USER_SYSTEM = "系统";
	// 启动者_定时任务
	String TASK_USER_SCHEDULE = "定时任务:";
	// 启动者_告警
	String TASK_USER_ALARM = "告警:";

	// AlarmMap的key
	// 对象
	String ALARM_ENTITY = "entity";
	// 状态
	String ALARM_STATUS = "status";
	// 名称
	String ALARM_NAME = "name";
	// 触发时间
	String ALARM_TIME = "time";
	// 确认时间
	String ALARM_ACKNOWLEDGEDTIME = "atime";
	// 确认用户
	String ALARM_ACKNOWLEDGEDUSER = "auser";

	// 存储器列表
	String SUMMARY_RESOURCE_DS_LIST = "dslist";
	// 存储器列表中的Map的key
	// 存储器
	String SUMMARY_RESOURCE_DS_NAME = "dsname";
	// 状态
	String SUMMARY_RESOURCE_DS_STATUS = "dsstatus";
	// 驱动器类型
	// 值有非SSD,
	// 与ssd有关的QueryConfigTarget返回ConfigTarget.scsiDisk(VirtualMachineScsiDiskDeviceInfo).disk(HostScsiDisk).ssd
	String SUMMARY_RESOURCE_DS_DRIVETYPE = "dsdrivetype";
	// 容量
	String SUMMARY_RESOURCE_DS_CAPACITY = "dscapacity";
	// 可用空间
	String SUMMARY_RESOURCE_DS_FREESPACE = "dsfreespace";
	// 上次更新
	String SUMMARY_RESOURCE_DS_TIME = "dstime";
	// 警报操作
	String SUMMARY_RESOURCE_DS_ALARMACTIONS = "dsalarmactions";
	// Storage I/O Control
	String SUMMARY_RESOURCE_DS_IOCTRL = "dsioctrl";

	// 网络列表
	String SUMMARY_RESOURCE_NET_LIST = "netlist";
	// 网络列表中的Map的key
	// 网络
	String SUMMARY_RESOURCE_NET_NAME = "netname";
	// 类型
	String SUMMARY_RESOURCE_NET_TYPE = "nettype";
	// 状态
	String SUMMARY_RESOURCE_NET_STATUS = "netstatus";
	// 警报操作
	String SUMMARY_RESOURCE_NET_ALARMACTIONS = "netalarmactions";

	// 同步接口Map中的key值
	// 1：数据中心
	String SYNC_DC = RESOURCE_DC;
	// 2：云，群集
	String SYNC_CR = RESOURCE_CR;
	// 3：资源池
	String SYNC_RP = RESOURCE_RP;
	// 4：存储 存储群集,RESOURCE_DS
	String SYNC_DS = RESOURCE_DS;
	// 5：物理机
	String SYNC_HO = RESOURCE_HO;
	// 6：虚机
	String SYNC_VM = RESOURCE_VM;
	// 7：模版
	String SYNC_TEMPLATE = "Template";
	// 其他
	String SYNC_OTHER = "other";

	// 公共的配置
	// VID
	String SYNC_COMMON_VID = "vid";
	// 名称
	String SYNC_COMMON_NAME = "name";
	// 存储池ID
	String SYNC_COMMON_DSID = "dsid";
	// 数据中心ID
	String SYNC_COMMON_DCID = "dcid";
	// 群集ID
	String SYNC_COMMON_CRID = "crid";

	// 其他的配置
	// 来源
	String SYNC_OTHER_SOURCE = "source";
	// 类型
	String SYNC_OTHER_TYPE = "type";
	// 调用的方法
	String SYNC_OTHER_METHOD = "method";

	// 虚拟机的配置
	// 4：所属物理主机ID
	String SYNC_VM_HOID = "hoid";
	// 6：描述
	String SYNC_VM_DESC = "desc";
	// 9：存储池ID,SYNC_COMMON_DSID
	// 10：cup总核数
	String SYNC_VM_CPU = "cpu";
	// 11：内存容量
	String SYNC_VM_MEMORYSIZEMB = "memorysizemb";
	// 12：存储容量
	String SYNC_VM_DSSIZEMB = "dssizemb";
	// 13：网卡个数
	String SYNC_VM_NETNUM = "netnum";
	// 15：虚机操作系统
	String SYNC_VM_GUESTFULLNAME = "guestfullname";
	// 18：数据中心ID,SYNC_COMMON_DCID
	// 20：资源池名称ID
	String SYNC_VM_RPID = "rpid";
	// 22：cpu 个数
	String SYNC_VM_NUMCPU = "numcpu";
	// 23：每个cpu核数
	String SYNC_VM_NUMCORE = "numcore";
	// 29: 电源状态,值包括poweredOn,poweredOff,suspended
	String SYNC_VM_POWERSTATE = "powerstate";

	// 数据存储的配置
	// 3：存储池的容量
	String SYNC_DS_CAPACITY = "capacity";
	// 7：空闲容量
	String SYNC_DS_FREESPACE = "freespace";
	// StoragePod的子对象
	String SYNC_DS_CHILDREN = "children";

	// 群集的配置
	// --7：当前云下所有虚机cpu核数总和
	// 摘要-->常规-->总CPU资源,summary.getTotalCpu,单位MHz
	String SYNC_CR_TOTALCPU = "totalcpu";
	// 摘要-->常规-->处理器总数,summary.getNumCpuCores
	String SYNC_CR_NUMCPUCORES = "numcpucores";
	// --8：当前云下所有虚机内存总数
	// 摘要-->常规-->总内存,summary.getTotalMemory,单位字节
	String SYNC_CR_TOTALMEMORY = "totalmemory";
	// --9：当前云下所有虚机储存总数
	// 摘要-->常规-->总存储
	String SYNC_CR_TOTALDSSIZEMB = "totaldssizemb";
	// 摘要-->常规-->主机数summary.getNumHosts(有效主机数summary.getNumEffectiveHosts)
	String SYNC_CR_NUMHOSTS = "numhosts";

	// 主机的配置
	// 3：物理主机型号
	String SYNC_HO_MODEL = "model";
	// 7：IP地址（界面上没有，不确定是否能取到）
	String SYNC_HO_IP = "ip";
	// 13：cpu个数
	String SYNC_HO_NUMCPU = "numcpu";
	// 14：每个cpu核数
	String SYNC_HO_NUMCORE = "numcore";
	// 15：cpu主频
	String SYNC_HO_CPUMHZ = "cpumhz";
	// 16：内存大小
	String SYNC_HO_MEMORYSIZEMB = "memorysizemb";
	// 17：网卡个数（物理）
	String SYNC_HO_NUMNICS = "numnics";
	// 20: 电源状态,值包括poweredOn,poweredOff,standBy,unknown
	String SYNC_HO_POWERSTATE = "powerstate";

	// 性能间隔
	String PERFINTERVAL_DAY = "1";

	// 性能间隔
	String PERFINTERVAL_WEEK = "2";

	// 性能间隔
	String PERFINTERVAL_MONTH = "3";

	// 性能间隔
	String PERFINTERVAL_YEAR = "4";

}
