/**
 * <p>Description:operate table NMS_DISCOVER_CONDITION</p>
 * 主要用于发现完之后，数据入库 
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.CommonUtil;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.NetworkUtil;
import com.afunms.common.util.PollDataUtil;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.dao.IpAliasDao;
import com.afunms.discovery.DiscoverEngine;
import com.afunms.discovery.Host;
import com.afunms.discovery.IfEntity;
import com.afunms.discovery.IpAddress;
import com.afunms.discovery.KeyGenerator;
import com.afunms.discovery.Link;
import com.afunms.discovery.RepairLink;
import com.afunms.discovery.SubNet;
import com.afunms.event.dao.NetSyslogRuleDao;
import com.afunms.event.model.NetSyslogRule;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.monitor.executor.base.MonitorFactory;
import com.afunms.monitor.item.base.MonitorObject;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.snmp.LoadWindowsWMIFile;
import com.afunms.topology.model.HostNode;

@SuppressWarnings("unchecked")
public class DiscoverCompleteDao extends BaseDao {
	private static final List moList = MonitorFactory.getMonitorObjectList();
	private int nmID = 0;

	public DiscoverCompleteDao() {
		super("topo_host_node");
		nmID = getNextID("topo_node_monitor");
	}

	/**
	 * 加入主机接口数据
	 */
	public void addARPData(List hostList) {
		int id = getNextID("topo_arp");
		List calhostlist = hostList;
		Hashtable tempHash = new Hashtable();
		Hashtable aliasHash = new Hashtable();
		List aliaslist = new ArrayList();
		for (int k = 0; k < calhostlist.size(); k++) {
			com.afunms.discovery.Host host = (com.afunms.discovery.Host) hostList.get(k);
			tempHash.put(host.getIpAddress(), "");
			aliaslist = host.getAliasIPs();
			if (aliaslist != null && aliaslist.size() > 0) {
				for (int m = 0; m < aliaslist.size(); m++) {
					aliasHash.put((String) aliaslist.get(m), host.getIpAddress());
				}
			}
		}
		for (int i = 0; i < hostList.size(); i++) {
			com.afunms.discovery.Host host = (com.afunms.discovery.Host) hostList.get(i);
			List arpList = host.getIpNetTable();
			if (arpList == null) {
				continue;
			}
			IpAddress ipAddress = null;
			int snmpflag = 0;
			for (int j = 0; j < arpList.size(); j++) {
				snmpflag = 0;
				ipAddress = (IpAddress) arpList.get(j);
				if (tempHash.containsKey(ipAddress.getIpAddress())) {
					// snmp is open
					snmpflag = 1;
				} else if (aliasHash.containsKey(ipAddress.getIpAddress())) {
					// alias ip is existed
					snmpflag = 1;
				}
				String physAddress = ipAddress.getPhysAddress();
				physAddress = CommonUtil.removeIllegalStr(physAddress);
				StringBuffer sql = new StringBuffer(300);
				sql.append("insert into topo_arp(id,node_id,ifindex,physaddress,ipaddress,monflag)values(");
				sql.append(id++);
				sql.append(",");
				sql.append(host.getId());
				sql.append(",'");
				sql.append(ipAddress.getIfIndex());
				sql.append("','");
				sql.append(ipAddress.getPhysAddress());
				sql.append("','");
				sql.append(ipAddress.getIpAddress());
				sql.append("',");
				sql.append(snmpflag); // 默认情况下是未启动SNMP
				sql.append(")");
				conn.executeUpdate(sql.toString(), false);
			}// end_for_j
			conn.commit();
		}// end_for_i
	}

	/**
	 * 增加数据库监控项
	 */
	public void addDBMonitor(int nodeId, String ip, String category) {
		nmID++;
		StringBuffer sql = new StringBuffer(200);
		sql.append("insert into topo_node_monitor(id,node_id,moid,threshold,compare,compare_type,upper_times,");
		sql.append("alarm_info,enabled,alarm_level,poll_interval,interval_unit,threshold_unit)values(");
		sql.append(nmID);
		sql.append(",");
		sql.append(nodeId);
		sql.append(",'052001',-1,1,1,2,'数据库不可用',1,3,20,'m','')");
		conn.executeUpdate(sql.toString());

		addMonitor(nodeId, ip, category);
	}

	/**
	 * 加入主机数据,同时加入网络设备IP的别名
	 */
	public void addHostData(List hostList) {
		Hashtable donehost = new Hashtable();
		for (int i = 0; i < hostList.size(); i++) {
			try {
				hostList.get(i).toString();
				Host node = (Host) hostList.get(i);
				if (donehost.containsKey(node.getId())) {
					continue;
				}
				node.setBid(DiscoverEngine.getInstance().getDiscover_bid());
				// 测试生成表
				String ip = node.getIpAddress();
				String allipstr = SysUtil.doip(ip);
				CreateTableManager ctable = new CreateTableManager();
				if ((node.getCategory() > 0 && node.getCategory() < 4) || node.getCategory() == 7) {
					if (DiscoverEngine.getInstance().getDiscovermodel() == 1) {
						// 补充发现
						if (node.getDiscoverstatus() == -1) {
							// 新发现的设备
							// 生成网络设备表
							// 连通率
							ctable.createTable("ping", allipstr, "ping");// Ping
							ctable.createTable("pinghour", allipstr, "pinghour");// Ping
							ctable.createTable("pingday", allipstr, "pingday");// Ping

							// 内存
							ctable.createTable("memory", allipstr, "mem");// 内存
							ctable.createTable("memoryhour", allipstr, "memhour");// 内存
							ctable.createTable("memoryday", allipstr, "memday");// 内存

							ctable.createTable("flash", allipstr, "flash");// 闪存
							ctable.createTable("flashhour", allipstr, "flashhour");// 闪存
							ctable.createTable("flashday", allipstr, "flashday");// 闪存

							ctable.createTable("buffer", allipstr, "buffer");// 缓存
							ctable.createTable("bufferhour", allipstr, "bufferhour");// 缓存
							ctable.createTable("bufferday", allipstr, "bufferday");// 缓存

							ctable.createTable("fan", allipstr, "fan");// 风扇
							ctable.createTable("fanhour", allipstr, "fanhour");// 风扇
							ctable.createTable("fanday", allipstr, "fanday");// 风扇

							ctable.createTable("power", allipstr, "power");// 电源
							ctable.createTable("powerhour", allipstr, "powerhour");// 电源
							ctable.createTable("powerday", allipstr, "powerday");// 电源

							ctable.createTable("vol", allipstr, "vol");// 电压
							ctable.createTable("volhour", allipstr, "volhour");// 电压
							ctable.createTable("volday", allipstr, "volday");// 电压

							// CPU
							ctable.createTable("cpu", allipstr, "cpu");// CPU
							ctable.createTable("cpuhour", allipstr, "cpuhour");// CPU
							ctable.createTable("cpuday", allipstr, "cpuday");// CPU

							// 带宽利用率
							ctable.createTable("utilhdxperc", allipstr, "hdperc");
							ctable.createTable("hdxperchour", allipstr, "hdperchour");
							ctable.createTable("hdxpercday", allipstr, "hdpercday");

							// 每个端口流速
							ctable.createTable("utilhdx", allipstr, "hdx");
							ctable.createTable("utilhdxhour", allipstr, "hdxhour");
							ctable.createTable("utilhdxday", allipstr, "hdxday");

							// 综合流速
							ctable.createTable("allutilhdx", allipstr, "allhdx");
							ctable.createTable("autilhdxh", allipstr, "ahdxh");
							ctable.createTable("autilhdxd", allipstr, "ahdxd");

							// 关键端口状态
							ctable.createTable("portstatus", allipstr, "port");

							// 丢包率
							ctable.createTable("discardsperc", allipstr, "dcardperc");
							ctable.createTable("dcarperh", allipstr, "dcarperh");
							ctable.createTable("dcarperd", allipstr, "dcarperd");

							// 错误率
							ctable.createTable("errorsperc", allipstr, "errperc");
							ctable.createTable("errperch", allipstr, "errperch");
							ctable.createTable("errpercd", allipstr, "errpercd");

							// 数据包
							ctable.createTable("packs", allipstr, "packs");
							ctable.createTable("packshour", allipstr, "packshour");
							ctable.createTable("packsday", allipstr, "packsday");

							// 入口数据库包
							ctable.createTable("inpacks", allipstr, "inpacks");
							ctable.createTable("ipacksh", allipstr, "ipacksh");
							ctable.createTable("ipackd", allipstr, "ipackd");

							// 出口数据包
							ctable.createTable("outpacks", allipstr, "outpacks");
							ctable.createTable("opackh", allipstr, "opackh");
							ctable.createTable("opacksd", allipstr, "opacksd");

							// 温度
							ctable.createTable("temper", allipstr, "temper");
							ctable.createTable("temperh", allipstr, "temperh");
							ctable.createTable("temperd", allipstr, "temperd");
						}
					} else {
						// 重新发现

						// 生成网络设备表
						// 连通率表
						ctable.createTable("ping", allipstr, "ping");// Ping
						ctable.createTable("pinghour", allipstr, "pinghour");// Ping
						ctable.createTable("pingday", allipstr, "pingday");// Ping

						// 内存表
						ctable.createTable("memory", allipstr, "mem");// 内存
						ctable.createTable("memoryhour", allipstr, "memhour");// 内存
						ctable.createTable("memoryday", allipstr, "memday");// 内存

						ctable.createTable("flash", allipstr, "flash");// 闪存
						ctable.createTable("flashhour", allipstr, "flashhour");// 闪存
						ctable.createTable("flashday", allipstr, "flashday");// 闪存

						ctable.createTable("buffer", allipstr, "buffer");// 缓存
						ctable.createTable("bufferhour", allipstr, "bufferhour");// 缓存
						ctable.createTable("bufferday", allipstr, "bufferday");// 缓存

						ctable.createTable("fan", allipstr, "fan");// 风扇
						ctable.createTable("fanhour", allipstr, "fanhour");// 风扇
						ctable.createTable("fanday", allipstr, "fanday");// 风扇

						ctable.createTable("power", allipstr, "power");// 电源
						ctable.createTable("powerhour", allipstr, "powerhour");// 电源
						ctable.createTable("powerday", allipstr, "powerday");// 电源

						ctable.createTable("vol", allipstr, "vol");// 电压
						ctable.createTable("volhour", allipstr, "volhour");// 电压
						ctable.createTable("volday", allipstr, "volday");// 电压

						// CPU
						ctable.createTable("cpu", allipstr, "cpu");// CPU
						ctable.createTable("cpuhour", allipstr, "cpuhour");// CPU
						ctable.createTable("cpuday", allipstr, "cpuday");// CPU

						// 带宽利用率表
						ctable.createTable("utilhdxperc", allipstr, "hdperc");
						ctable.createTable("hdxperchour", allipstr, "hdperchour");
						ctable.createTable("hdxpercday", allipstr, "hdpercday");

						// 流速
						ctable.createTable("utilhdx", allipstr, "hdx");
						ctable.createTable("utilhdxhour", allipstr, "hdxhour");
						ctable.createTable("utilhdxday", allipstr, "hdxday");

						// 综合流速
						ctable.createTable("allutilhdx", allipstr, "allhdx");
						ctable.createTable("autilhdxh", allipstr, "ahdxh");
						ctable.createTable("autilhdxd", allipstr, "ahdxd");

						// 关键端口状态
						ctable.createTable("portstatus", allipstr, "port");

						// 丢包
						ctable.createTable("discardsperc", allipstr, "dcardperc");
						ctable.createTable("dcarperh", allipstr, "dcarperh");
						ctable.createTable("dcarperd", allipstr, "dcarperd");

						// 错误率
						ctable.createTable("errorsperc", allipstr, "errperc");
						ctable.createTable("errperch", allipstr, "errperch");
						ctable.createTable("errpercd", allipstr, "errpercd");

						// 数据包
						ctable.createTable("packs", allipstr, "packs");
						ctable.createTable("packshour", allipstr, "packshour");
						ctable.createTable("packsday", allipstr, "packsday");

						// 入口数据包
						ctable.createTable("inpacks", allipstr, "inpacks");
						ctable.createTable("ipacksh", allipstr, "ipacksh");
						ctable.createTable("ipackd", allipstr, "ipackd");

						// 出口数据包
						ctable.createTable("outpacks", allipstr, "outpacks");
						ctable.createTable("opackh", allipstr, "opackh");
						ctable.createTable("opacksd", allipstr, "opacksd");

						// 温度
						ctable.createTable("temper", allipstr, "temper");
						ctable.createTable("temperh", allipstr, "temperh");
						ctable.createTable("temperd", allipstr, "temperd");
					}
				} else if (node.getCategory() == 4) {
					// 主机设备
					if (DiscoverEngine.getInstance().getDiscovermodel() == 1) {
						// 补充发现
						if (node.getDiscoverstatus() == -1) {
							// 新发现的设备
							// 生成主机设备表
							ctable.createTable("pro", allipstr, "pro");// 进程
							ctable.createTable("prohour", allipstr, "prohour");// 进程小时
							ctable.createTable("proday", allipstr, "proday");// 进程天

							ctable.createSyslogTable("log", allipstr, "log");// 进程天

							ctable.createTable("memory", allipstr, "mem");// 内存
							ctable.createTable("memoryhour", allipstr, "memhour");// 内存
							ctable.createTable("memoryday", allipstr, "memday");// 内存

							ctable.createTable("cpu", allipstr, "cpu");// CPU
							ctable.createTable("cpuhour", allipstr, "cpuhour");// CPU
							ctable.createTable("cpuday", allipstr, "cpuday");// CPU

							ctable.createTable("diskincre", allipstr, "diskincre");// 磁盘增长率yangjun
							ctable.createTable("diskinch", allipstr, "diskinch");// 磁盘增长率小时
							ctable.createTable("diskincd", allipstr, "diskincd");// 磁盘增长率天

							ctable.createTable("disk", allipstr, "disk");// yangjun
							ctable.createTable("diskhour", allipstr, "diskhour");
							ctable.createTable("diskday", allipstr, "diskday");

							ctable.createTable("ping", allipstr, "ping");
							ctable.createTable("pinghour", allipstr, "pinghour");
							ctable.createTable("pingday", allipstr, "pingday");

							ctable.createTable("utilhdxperc", allipstr, "hdperc");
							ctable.createTable("hdxperchour", allipstr, "hdperchour");
							ctable.createTable("hdxpercday", allipstr, "hdpercday");

							ctable.createTable("utilhdx", allipstr, "hdx");
							ctable.createTable("utilhdxhour", allipstr, "hdxhour");
							ctable.createTable("utilhdxday", allipstr, "hdxday");

							ctable.createTable("allutilhdx", allipstr, "allhdx");
							ctable.createTable("autilhdxh", allipstr, "ahdxh");
							ctable.createTable("autilhdxd", allipstr, "ahdxd");

							ctable.createTable("discardsperc", allipstr, "dcardperc");
							ctable.createTable("dcarperh", allipstr, "dcarperh");
							ctable.createTable("dcarperd", allipstr, "dcarperd");

							ctable.createTable("errorsperc", allipstr, "errperc");
							ctable.createTable("errperch", allipstr, "errperch");
							ctable.createTable("errpercd", allipstr, "errpercd");

							ctable.createTable("packs", allipstr, "packs");
							ctable.createTable("packshour", allipstr, "packshour");
							ctable.createTable("packsday", allipstr, "packsday");

							ctable.createTable("inpacks", allipstr, "inpacks");
							ctable.createTable("ipacksh", allipstr, "ipacksh");
							ctable.createTable("ipackd", allipstr, "ipackd");

							ctable.createTable("outpacks", allipstr, "outpacks");
							ctable.createTable("opackh", allipstr, "opackh");
							ctable.createTable("opacksd", allipstr, "opacksd");

						}
					} else {
						// 重新发现
						// 生成主机设备表
						ctable.createTable("pro", allipstr, "pro");// 进程
						ctable.createTable("prohour", allipstr, "prohour");// 进程小时
						ctable.createTable("proday", allipstr, "proday");// 进程天

						ctable.createSyslogTable("log", allipstr, "log");// 进程天

						ctable.createTable("memory", allipstr, "mem");// 内存
						ctable.createTable("memoryhour", allipstr, "memhour");// 内存
						ctable.createTable("memoryday", allipstr, "memday");// 内存

						ctable.createTable("cpu", allipstr, "cpu");// CPU
						ctable.createTable("cpuhour", allipstr, "cpuhour");// CPU
						ctable.createTable("cpuday", allipstr, "cpuday");// CPU

						ctable.createTable("cpudtl", allipstr, "cpudtl");// CPU
						// detail
						ctable.createTable("cpudtlhour", allipstr, "cpudtlhour");// CPU
						// detail
						ctable.createTable("cpudtlday", allipstr, "cpudtlday");// CPU
						// detail

						ctable.createTable("disk", allipstr, "disk");// yangjun
						ctable.createTable("diskhour", allipstr, "diskhour");
						ctable.createTable("diskday", allipstr, "diskday");

						ctable.createTable("diskincre", allipstr, "diskincre");// 磁盘增长率yangjun
						ctable.createTable("diskinch", allipstr, "diskinch");// 磁盘增长率小时
						ctable.createTable("diskincd", allipstr, "diskincd");// 磁盘增长率天
						ctable.createTable("ping", allipstr, "ping");
						ctable.createTable("pinghour", allipstr, "pinghour");
						ctable.createTable("pingday", allipstr, "pingday");

						ctable.createTable("utilhdxperc", allipstr, "hdperc");
						ctable.createTable("hdxperchour", allipstr, "hdperchour");
						ctable.createTable("hdxpercday", allipstr, "hdpercday");

						ctable.createTable("utilhdx", allipstr, "hdx");
						ctable.createTable("utilhdxhour", allipstr, "hdxhour");
						ctable.createTable("utilhdxday", allipstr, "hdxday");

						ctable.createTable("allutilhdx", allipstr, "allhdx");
						ctable.createTable("autilhdxh", allipstr, "ahdxh");
						ctable.createTable("autilhdxd", allipstr, "ahdxd");
					}
				}
				try {
					conn.executeBatch();
				} catch (Exception e) {

				}
				String bridgeAddress = node.getBridgeAddress();
				bridgeAddress = CommonUtil.removeIllegalStr(bridgeAddress);
				StringBuffer sql = new StringBuffer(300);
				sql.append("insert into topo_host_node(id,ip_address,ip_long,net_mask,category,community,sys_oid,sys_name,super_node,");
				sql
						.append("local_net,layer,sys_descr,sys_location,sys_contact,alias,type,managed,bridge_address,status,discoverstatus,write_community,snmpversion,ostype,collecttype,bid,sendemail,sendmobiles,sendphone)values(");
				sql.append(node.getId());
				sql.append(",'");
				sql.append(node.getIpAddress());
				sql.append("',");
				sql.append(NetworkUtil.ip2long(node.getIpAddress()));
				sql.append(",'");
				sql.append(node.getNetMask());
				sql.append("',");
				sql.append(node.getCategory());
				sql.append(",'");
				sql.append(node.getCommunity());
				sql.append("','");
				sql.append(node.getSysOid());
				sql.append("','");
				sql.append(replace(node.getSysName()));
				sql.append("',");
				sql.append(node.getSuperNode());
				sql.append(",");
				sql.append(node.getLocalNet());
				sql.append(",");
				sql.append(node.getLayer());
				sql.append(",'");
				sql.append(replace(node.getSysDescr()));
				sql.append("','");
				sql.append(replace(node.getSysLocation()));
				sql.append("','");
				sql.append(replace(node.getSysContact()));
				sql.append("','");
				if (node.getAlias() == null) {
					sql.append(replace(node.getSysName()));
				} else {
					sql.append(replace(node.getAlias()));
				}
				sql.append("','',0,'");// 默认情况下不监视
				sql.append(bridgeAddress);
				sql.append("',");
				sql.append(node.getStatus());
				sql.append(",");
				sql.append(node.getDiscoverstatus());
				sql.append(",'");
				sql.append(node.getWritecommunity());
				sql.append("',");
				sql.append(node.getSnmpversion());
				sql.append(",");
				sql.append(node.getOstype());
				sql.append(",1");// 默认情况下是SNMP采集方式
				sql.append(",'");
				sql.append(node.getBid());
				sql.append("','");
				sql.append("");
				sql.append("','");
				sql.append("");
				sql.append("','");
				sql.append("");
				sql.append("')");
				conn.executeUpdate(sql.toString(), false);
				donehost.put(node.getId(), node);

				// 设置采集指标
				// 采集设备信息
				try {
					if (node.getEndpoint() == 2) {
						// REMOTEPING的子节点，跳过
						// return;
					} else {
						if (node.getCategory() == 4) {
							// 初始化服务器采集指标和阀值
							if (node.getSysOid().startsWith("1.3.6.1.4.1.311.")) {
								// windows服务器
								// 阀值
								try {
									AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
									alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_HOST, "windows");
								} catch (RuntimeException e) {
									e.printStackTrace();
								}
								// 采集指标
								try {
									NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
									nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_HOST, "windows", "1");
								} catch (RuntimeException e) {
									e.printStackTrace();
								}
							} else if (node.getSysOid().startsWith("1.3.6.1.4.1.2021") || node.getSysOid().startsWith("1.3.6.1.4.1.8072")) {
								// LINUX服务器
								try {
									AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
									alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_HOST, "linux");
								} catch (RuntimeException e) {
									e.printStackTrace();
								}
								try {
									NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
									nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_HOST, "linux", "1");
								} catch (RuntimeException e) {
									e.printStackTrace();
								}
							} else if (node.getSysOid().startsWith("as400")) {
								// AS400服务器
								try {
									AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
									alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_HOST, "as400");
								} catch (RuntimeException e) {
									e.printStackTrace();
								}
								try {
									NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
									nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_HOST, "as400", "1");
								} catch (RuntimeException e) {
									e.printStackTrace();
								}
							}

						} else if (node.getCategory() < 4 || node.getCategory() == 7 || node.getCategory() == 8) {
							// 初始化网络设备采集指标
							if (node.getSysOid().startsWith("1.3.6.1.4.1.9.")) {
								// cisco网络设备
								try {
									AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
									alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_NET, "cisco");
								} catch (RuntimeException e) {
									e.printStackTrace();
								}
								try {
									NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
									nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_NET, "cisco", "1");
								} catch (RuntimeException e) {
									e.printStackTrace();
								}
							} else if (node.getSysOid().startsWith("1.3.6.1.4.1.25506.")) {
								// h3c网络设备
								try {
									AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
									alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_NET, "h3c");
								} catch (RuntimeException e) {
									e.printStackTrace();
								}
								try {
									NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
									nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_NET, "h3c", "1");
								} catch (RuntimeException e) {
									e.printStackTrace();
								}
							} else if (node.getSysOid().startsWith("1.3.6.1.4.1.2011.")) {
								// h3c网络设备
								try {
									AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
									alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_NET, "h3c");
								} catch (RuntimeException e) {
									e.printStackTrace();
								}
								try {
									NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
									nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_NET, "h3c", "1");
								} catch (RuntimeException e) {
									e.printStackTrace();
								}
							} else if (node.getSysOid().startsWith("1.3.6.1.4.1.4881.")) {
								// 锐捷网络设备
								try {
									AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
									alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_NET, "redgiant");
								} catch (RuntimeException e) {
									e.printStackTrace();
								}
								try {
									NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
									nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_NET, "redgiant", "1");
								} catch (RuntimeException e) {
									e.printStackTrace();
								}
							} else if (node.getSysOid().startsWith("1.3.6.1.4.1.5651.")) {
								// 迈普网络设备
								try {
									AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
									alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_NET, "maipu");
								} catch (RuntimeException e) {
									e.printStackTrace();
								}
								try {
									NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
									nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_NET, "maipu", "1");
								} catch (RuntimeException e) {
									e.printStackTrace();
								}
							} else if (node.getSysOid().startsWith("1.3.6.1.4.1.171.")) {
								// DLink网络设备
								try {
									AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
									alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_NET, "dlink");
								} catch (RuntimeException e) {
									e.printStackTrace();
								}
								try {
									NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
									nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_NET, "dlink", "1");
								} catch (RuntimeException e) {
									e.printStackTrace();
								}
							} else if (node.getSysOid().startsWith("1.3.6.1.4.1.2272.")) {
								// 北电网络设备
								try {
									AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
									alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_NET, "northtel");
								} catch (RuntimeException e) {
									e.printStackTrace();
								}
								try {
									NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
									nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_NET, "northtel", "1");
								} catch (RuntimeException e) {
									e.printStackTrace();
								}
							} else if (node.getSysOid().startsWith("1.3.6.1.4.1.89.")) {
								// RADWARE网络设备
								try {
									AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
									alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_NET, "radware");
								} catch (RuntimeException e) {
									e.printStackTrace();
								}
								try {
									NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
									nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_NET, "radware", "1");
								} catch (RuntimeException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} else if (node.getSysOid().startsWith("1.3.6.1.4.1.3320.")) {
								// 博达网络设备
								try {
									AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
									alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_NET, "bdcom");
								} catch (RuntimeException e) {
									e.printStackTrace();
								}
								try {
									NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
									nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_NET, "bdcom", "1");
								} catch (RuntimeException e) {
									e.printStackTrace();
								}
							}
						}

						// 若只用PING TELNET SSH方式检测可用性,则性能数据不采集,跳过
						if (node.getCollecttype() == SystemConstant.COLLECTTYPE_PING || node.getCollecttype() == SystemConstant.COLLECTTYPE_TELNETCONNECT
								|| node.getCollecttype() == SystemConstant.COLLECTTYPE_SSHCONNECT) {
							// SysLogger.info("只PING TELNET
							// SSH方式检测可用性,性能数据不采集,跳过");
						} else {
							if (node.getCategory() < 4 || node.getCategory() == 7) {
								PollDataUtil polldata = new PollDataUtil();
								polldata.collectNetData(node.getId() + "");
							} else if (node.getCategory() == 4) {
								collectHostData(node);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 加入连通性检测类型表,若是PING检测则不加入
				if (node.getCollecttype() == SystemConstant.COLLECTTYPE_TELNETCONNECT || node.getCollecttype() == SystemConstant.COLLECTTYPE_SSHCONNECT) {
					StringBuffer configsql = new StringBuffer(200);
					configsql.append("insert into nms_connecttypeconfig(node_id,connecttype,username,password,login_prompt,password_prompt,shell_prompt)" + "values('");
					configsql.append(node.getId());
					configsql.append("','");
					if (node.getCollecttype() == SystemConstant.COLLECTTYPE_TELNETCONNECT) {
						configsql.append("telnet");
					} else if (node.getCollecttype() == SystemConstant.COLLECTTYPE_SSHCONNECT) {
						configsql.append("ssh");
					} else {
						configsql.append("ping");
					}
					configsql.append("','");
					configsql.append("");
					configsql.append("','");
					configsql.append("");
					configsql.append("','");
					configsql.append("");
					configsql.append("','");
					configsql.append("");
					configsql.append("','");
					configsql.append("");
					configsql.append("')");
					conn.executeUpdate(configsql.toString(), false);
				}

				NetSyslogRuleDao ruledao = new NetSyslogRuleDao();
				NetSyslogNodeRuleDao netlog = new NetSyslogNodeRuleDao();
				try {
					String strFacility = "";
					List rulelist = new ArrayList();
					try {
						rulelist = ruledao.loadAll();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						ruledao.close();
					}
					if (rulelist != null && rulelist.size() > 0) {
						NetSyslogRule logrule = (NetSyslogRule) rulelist.get(0);
						strFacility = logrule.getFacility();
					}

					String strSql = "";
					strSql = "insert into nms_netsyslogrule_node(nodeid,facility)values('" + node.getId() + "','" + strFacility + "')";
					try {
						netlog.saveOrUpdate(strSql);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						netlog.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					ruledao.close();
					netlog.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		conn.commit();
		conn.executeUpdate("update topo_host_node a set a.type=(select b.descr  from nms_device_type b where a.sys_oid=b.sys_oid)");
		// 加入网络设备IP的别名
		donehost = new Hashtable();
		for (int i = 0; i < hostList.size(); i++) {
			try {
				Host node = (Host) hostList.get(i);
				if (donehost.containsKey(node.getId())) {
					continue;
				}
				if (node.getAliasIfEntitys() != null && node.getAliasIfEntitys().size() > 0) {
					for (int k = 0; k < node.getAliasIfEntitys().size(); k++) {
						IfEntity ifEntity = (IfEntity) node.getAliasIfEntitys().get(k);
						StringBuffer sql = new StringBuffer(300);
						sql.append("insert into topo_ipalias(ipaddress,aliasip,indexs,descr,speeds,types) values('");
						if (node.getAdminIp() != null && node.getAdminIp().trim().length() > 0) {
							sql.append(node.getAdminIp());
							// 过滤掉管理地址和别名IP相同的记录
							if (node.getAdminIp().equalsIgnoreCase(ifEntity.getIpAddress())) {
								continue;
							}
						} else {
							// 过滤掉管理地址和别名IP相同的记录
							sql.append(node.getIpAddress());
							if (node.getIpAddress().equalsIgnoreCase(ifEntity.getIpAddress())) {
								continue;
							}
						}

						sql.append("','");
						sql.append(ifEntity.getIpAddress());
						sql.append("','");
						sql.append(ifEntity.getIndex());
						sql.append("','");
						sql.append(ifEntity.getDescr());
						sql.append("','");
						sql.append(ifEntity.getSpeed());
						sql.append("',");
						sql.append(ifEntity.getType());
						sql.append(")");
						conn.executeUpdate(sql.toString(), false);
						donehost.put(node.getId(), node);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		IpAliasDao ipaliasdao = new IpAliasDao();
		try {
			ipaliasdao.RefreshIpAlias();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ipaliasdao.close();
		}
	}

	/**
	 * 手工加入主机数据,同时加入网络设备IP的别名
	 */
	public synchronized void addHostDataByHand(List hostList) {
		Hashtable donehost = new Hashtable();
		for (int i = 0; i < hostList.size(); i++) {
			try {
				hostList.get(i).toString();
				Host node = (Host) hostList.get(i);
				if (donehost.containsKey(node.getId())) {
					continue;
				}

				// 测试生成表
				String ip = node.getIpAddress();
				String allipstr = SysUtil.doip(ip);
				CreateTableManager ctable = new CreateTableManager();
				if ((node.getCategory() > 0 && node.getCategory() < 4) || node.getCategory() == 7 || node.getCategory() == 8 || node.getCategory() == 9 || node.getCategory() == 10
						|| node.getCategory() == 11) {
					if (node.getDiscoverstatus() == -1) {
						// 新发现的设备
						// 生成网络设备表
						if (node.getCollecttype() == 3) {
							ctable.createTable("ping", allipstr, "ping");// Ping
							ctable.createTable("pinghour", allipstr, "pinghour");// Ping
							ctable.createTable("pingday", allipstr, "pingday");// Ping
						} else {
							ctable.createTable("ping", allipstr, "ping");// Ping
							ctable.createTable("pinghour", allipstr, "pinghour");// Ping
							ctable.createTable("pingday", allipstr, "pingday");// Ping

							ctable.createSyslogTable("log", allipstr, "log");// 进程天
							
							ctable.createTable("memory", allipstr, "mem");// 内存
							ctable.createTable("memoryhour", allipstr, "memhour");// 内存
							ctable.createTable("memoryday", allipstr, "memday");// 内存

							ctable.createTable("flash", allipstr, "flash");// 闪存
							ctable.createTable("flashhour", allipstr, "flashhour");// 闪存
							ctable.createTable("flashday", allipstr, "flashday");// 闪存

							ctable.createTable("buffer", allipstr, "buffer");// 缓存
							ctable.createTable("bufferhour", allipstr, "bufferhour");// 缓存
							ctable.createTable("bufferday", allipstr, "bufferday");// 缓存

							ctable.createTable("fan", allipstr, "fan");// 风扇
							ctable.createTable("fanhour", allipstr, "fanhour");// 风扇
							ctable.createTable("fanday", allipstr, "fanday");// 风扇

							ctable.createTable("power", allipstr, "power");// 电源
							ctable.createTable("powerhour", allipstr, "powerhour");// 电源
							ctable.createTable("powerday", allipstr, "powerday");// 电源

							ctable.createTable("vol", allipstr, "vol");// 电压
							ctable.createTable("volhour", allipstr, "volhour");// 电压
							ctable.createTable("volday", allipstr, "volday");// 电压

							ctable.createTable("cpu", allipstr, "cpu");// CPU
							ctable.createTable("cpuhour", allipstr, "cpuhour");// CPU
							ctable.createTable("cpuday", allipstr, "cpuday");// CPU

							ctable.createTable("utilhdxperc", allipstr, "hdperc");
							ctable.createTable("hdxperchour", allipstr, "hdperchour");
							ctable.createTable("hdxpercday", allipstr, "hdpercday");

							ctable.createTable("portstatus", allipstr, "port");

							ctable.createTable("utilhdx", allipstr, "hdx");
							ctable.createTable("utilhdxhour", allipstr, "hdxhour");
							ctable.createTable("utilhdxday", allipstr, "hdxday");

							ctable.createTable("allutilhdx", allipstr, "allhdx");
							ctable.createTable("autilhdxh", allipstr, "ahdxh");
							ctable.createTable("autilhdxd", allipstr, "ahdxd");

							ctable.createTable("discardsperc", allipstr, "dcardperc");
							ctable.createTable("dcarperh", allipstr, "dcarperh");
							ctable.createTable("dcarperd", allipstr, "dcarperd");

							ctable.createTable("errorsperc", allipstr, "errperc");
							ctable.createTable("errperch", allipstr, "errperch");
							ctable.createTable("errpercd", allipstr, "errpercd");

							ctable.createTable("packs", allipstr, "packs");
							ctable.createTable("packshour", allipstr, "packshour");
							ctable.createTable("packsday", allipstr, "packsday");

							ctable.createTable("inpacks", allipstr, "inpacks");
							ctable.createTable("ipacksh", allipstr, "ipacksh");
							ctable.createTable("ipackd", allipstr, "ipackd");

							ctable.createTable("outpacks", allipstr, "outpacks");
							ctable.createTable("opackh", allipstr, "opackh");
							ctable.createTable("opacksd", allipstr, "opacksd");

							ctable.createTable("temper", allipstr, "temper");
							ctable.createTable("temperh", allipstr, "temperh");
							ctable.createTable("temperd", allipstr, "temperd");
						}
					}
				} else if (node.getCategory() == 12) {
					// 生成VPN设备表
					if (node.getCollecttype() == 3) {
						ctable.createTable("ping", allipstr, "ping");// Ping
						ctable.createTable("pinghour", allipstr, "pinghour");// Ping
						ctable.createTable("pingday", allipstr, "pingday");// Ping
					} else {
						ctable.createTable("ping", allipstr, "ping");// Ping
						ctable.createTable("pinghour", allipstr, "pinghour");// Ping
						ctable.createTable("pingday", allipstr, "pingday");// Ping

						ctable.createTable("memory", allipstr, "mem");// 内存
						ctable.createTable("memoryhour", allipstr, "memhour");// 内存
						ctable.createTable("memoryday", allipstr, "memday");// 内存

						ctable.createTable("cpu", allipstr, "cpu");// CPU
						ctable.createTable("cpuhour", allipstr, "cpuhour");// CPU
						ctable.createTable("cpuday", allipstr, "cpuday");// CPU

						ctable.createTable("utilhdxperc", allipstr, "hdperc");
						ctable.createTable("hdxperchour", allipstr, "hdperchour");
						ctable.createTable("hdxpercday", allipstr, "hdpercday");

						ctable.createTable("portstatus", allipstr, "port");

						ctable.createTable("utilhdx", allipstr, "hdx");
						ctable.createTable("utilhdxhour", allipstr, "hdxhour");
						ctable.createTable("utilhdxday", allipstr, "hdxday");

						ctable.createTable("allutilhdx", allipstr, "allhdx");
						ctable.createTable("autilhdxh", allipstr, "ahdxh");
						ctable.createTable("autilhdxd", allipstr, "ahdxd");

						ctable.createTable("discardsperc", allipstr, "dcardperc");
						ctable.createTable("dcarperh", allipstr, "dcarperh");
						ctable.createTable("dcarperd", allipstr, "dcarperd");

						ctable.createTable("errorsperc", allipstr, "errperc");
						ctable.createTable("errperch", allipstr, "errperch");
						ctable.createTable("errpercd", allipstr, "errpercd");

						ctable.createTable("packs", allipstr, "packs");
						ctable.createTable("packshour", allipstr, "packshour");
						ctable.createTable("packsday", allipstr, "packsday");

						ctable.createTable("inpacks", allipstr, "inpacks");
						ctable.createTable("ipacksh", allipstr, "ipacksh");
						ctable.createTable("ipackd", allipstr, "ipackd");

						ctable.createTable("outpacks", allipstr, "outpacks");
						ctable.createTable("opackh", allipstr, "opackh");
						ctable.createTable("opacksd", allipstr, "opacksd");
					}
				} else if (node.getCategory() == 13) {
					// 生成CMTS设备表
					if (node.getCollecttype() == 3) {
						ctable.createTable("ping", allipstr, "ping");// Ping
						ctable.createTable("pinghour", allipstr, "pinghour");// Ping
						ctable.createTable("pingday", allipstr, "pingday");// Ping
					} else {
						ctable.createTable("ping", allipstr, "ping");// Ping
						ctable.createTable("pinghour", allipstr, "pinghour");// Ping
						ctable.createTable("pingday", allipstr, "pingday");// Ping

						ctable.createTable("status", allipstr, "status");// 通道状态
						ctable.createTable("statushour", allipstr, "statushour");// 通道状态按小时
						ctable.createTable("statusday", allipstr, "statusday");// 通道状态按天

						ctable.createCiscoCMTSTable(conn, "noise", allipstr, "noise");// 通道信躁比
						ctable.createCiscoCMTSTable(conn, "noisehour", allipstr, "noisehour");// 通道信躁比按小时
						ctable.createCiscoCMTSTable(conn, "noiseday", allipstr, "noiseday");// 通道信躁比按天

						ctable.createCiscoCMTSIPMACTable(conn, "ipmac", allipstr, "ipmac");// IPMAC信息（在线用户信息）

						ctable.createTable("utilhdxpercs", allipstr, "hdpercs");
						ctable.createTable("hdxperchours", allipstr, "hdperchours");
						ctable.createTable("hdxpercdays", allipstr, "hdpercdays");

						ctable.createTable("utilhdxs", allipstr, "hdxs");
						ctable.createTable("utilhdxhours", allipstr, "hdxhours");
						ctable.createTable("utilhdxdays", allipstr, "hdxdays");

						ctable.createTable("allutilhdxs", allipstr, "allhdxs");
						ctable.createTable("autilhdxhs", allipstr, "ahdxhs");
						ctable.createTable("autilhdxds", allipstr, "ahdxds");

						ctable.createTable("discardspercs", allipstr, "dcardpercs");
						ctable.createTable("dcarperhs", allipstr, "dcarperhs");
						ctable.createTable("dcarperds", allipstr, "dcarperds");

						ctable.createTable("errorspercs", allipstr, "errpercs");
						ctable.createTable("errperchs", allipstr, "errperchs");
						ctable.createTable("errpercds", allipstr, "errpercds");

						ctable.createTable("packss", allipstr, "packss");
						ctable.createTable("packshours", allipstr, "packshours");
						ctable.createTable("packsdays", allipstr, "packsdays");

						ctable.createTable("inpackss", allipstr, "inpackss");
						ctable.createTable("ipackshs", allipstr, "ipackshs");
						ctable.createTable("ipackds", allipstr, "ipackds");

						ctable.createTable("outpackss", allipstr, "outpackss");
						ctable.createTable("opackhs", allipstr, "opackhs");
						ctable.createTable("opacksds", allipstr, "opacksds");
					}
				} else if (node.getCategory() == 14) {
					// 生成存储设备表
					if (node.getCollecttype() == 3) {
						ctable.createTable("pings", allipstr, "pings");// Ping
						ctable.createTable("pinghours", allipstr, "pinghours");// Ping
						ctable.createTable("pingdays", allipstr, "pingdays");// Ping
					} else if (node.getCollecttype() == 7 || (node.getSysOid() != null && node.getSysOid().trim().startsWith("1.3.6.1.4.1.11.2.3.7.11"))) {
						// hp storage
						ctable.createTable("ping", allipstr, "ping");// Ping
						ctable.createTable("pinghour", allipstr, "pinghour");// Ping
						ctable.createTable("pingday", allipstr, "pingday");// Ping
					} else if (node.getSysOid() != null && node.getSysOid().trim().startsWith("1.3.6.1.4.1.789.")) {
						// NETAPP存储
						ctable.createTable("pings", allipstr, "pings");// Ping
						ctable.createTable("pingshour", allipstr, "pingshour");// Ping
						ctable.createTable("pingsday", allipstr, "pingsday");// Ping

						ctable.createTable("cpu", allipstr, "cpu");// CPU
						ctable.createTable("cpuhour", allipstr, "cpuhour");// CPU
						ctable.createTable("cpuday", allipstr, "cpuday");// CPU

						ctable.createTable("utilhdxperc", allipstr, "hdperc");
						ctable.createTable("hdxperchour", allipstr, "hdperchour");
						ctable.createTable("hdxpercday", allipstr, "hdpercday");

						ctable.createTable("utilhdx", allipstr, "hdx");
						ctable.createTable("utilhdxhour", allipstr, "hdxhour");
						ctable.createTable("utilhdxday", allipstr, "hdxday");

						ctable.createTable("allutilhdx", allipstr, "allhdx");
						ctable.createTable("autilhdxh", allipstr, "ahdxh");
						ctable.createTable("autilhdxd", allipstr, "ahdxd");

						ctable.createTable("discardsperc", allipstr, "dcardperc");
						ctable.createTable("dcarperh", allipstr, "dcarperh");
						ctable.createTable("dcarperd", allipstr, "dcarperd");

						ctable.createTable("errorsperc", allipstr, "errperc");
						ctable.createTable("errperch", allipstr, "errperch");
						ctable.createTable("errpercd", allipstr, "errpercd");

						ctable.createTable("packs", allipstr, "packs");
						ctable.createTable("packshour", allipstr, "packshour");
						ctable.createTable("packsday", allipstr, "packsday");

						ctable.createTable("inpacks", allipstr, "inpacks");
						ctable.createTable("ipacksh", allipstr, "ipacksh");
						ctable.createTable("ipackd", allipstr, "ipackd");

						ctable.createTable("outpacks", allipstr, "outpacks");
						ctable.createTable("opackh", allipstr, "opackh");
						ctable.createTable("opacksd", allipstr, "opacksd");
					} else if (node.getSysOid() != null && !node.getSysOid().trim().startsWith("1.3.6.1.4.1.11.2.3.7.11")
							&& !node.getSysOid().trim().startsWith("1.3.6.1.4.1.789.")) {
						ctable.createTable("pings", allipstr, "pings");// Ping
						ctable.createTable("pinghours", allipstr, "pinghours");// Ping
						ctable.createTable("pingdays", allipstr, "pingdays");// Ping

						ctable.createTable("env", allipstr, "env");// 存储设备环境-风扇\电源\环境状态\驱动状态

						ctable.createTable("efan", allipstr, "efan");// 存储设备环境-风扇
						ctable.createTable("epower", allipstr, "epower");// 存储设备环境-电源
						ctable.createTable("eenv", allipstr, "eenv");// 存储设备环境-环境状态
						ctable.createTable("edrive", allipstr, "edrive");// 存储设备环境-驱动状态

						ctable.createTable("rcable", allipstr, "rcable");// 运行状体：内部总线状态
						ctable.createTable("rcache", allipstr, "rcache");// 运行状体：缓存状态
						ctable.createTable("rmemory", allipstr, "rmemory");// 运行状体：共享内存状态
						ctable.createTable("rpower", allipstr, "rpower");// 运行状体：电源状态
						ctable.createTable("rbutter", allipstr, "rbutter");// 运行状体：电池状态
						ctable.createTable("rfan", allipstr, "rfan");// 运行状体：风扇状态
						ctable.createTable("renv", allipstr, "renv");// 存储设备环境-环境状态
						ctable.createTable("rluncon", allipstr, "rluncon");
						ctable.createTable("rsluncon", allipstr, "rsluncon");
						ctable.createTable("rwwncon", allipstr, "rwwncon");
						ctable.createTable("rsafety", allipstr, "rsafety");
						ctable.createTable("rnumber", allipstr, "rnumber");
						ctable.createTable("rswitch", allipstr, "rswitch");
						ctable.createTable("rcpu", allipstr, "rcpu");

						ctable.createTable("events", allipstr, "events");// 事件

						ctable.createEmcTable(conn, "emcdiskper", allipstr, "emcdiskper");
						ctable.createEmcTable(conn, "emclunper", allipstr, "emclunper");
						ctable.createEmcTable(conn, "emcenvpower", allipstr, "emcenvpower");
						ctable.createEmcTable(conn, "emcenvstore", allipstr, "emcenvstore");
						ctable.createEmcTable(conn, "emcbakpower", allipstr, "emcbakpower");
					}
				} else if (node.getCategory() == 15) {
					// 生成VMWare表
					if (node.getCollecttype() == 3) {
						ctable.createTable("ping", allipstr, "ping");// Ping
						ctable.createTable("pinghour", allipstr, "pinghour");// Ping
						ctable.createTable("pingday", allipstr, "pingday");// Ping
					} else {
						ctable.createTable("ping", allipstr, "ping");// Ping
						ctable.createTable("pinghour", allipstr, "pinghour");// Ping
						ctable.createTable("pingday", allipstr, "pingday");// Ping

						ctable.createTable("memory", allipstr, "memory");// 内存利用率
						ctable.createTable("memoryhour", allipstr, "memhour");// 内存
						ctable.createTable("memoryday", allipstr, "memday");// 内存

						ctable.createTable("cpu", allipstr, "cpu");// CPU
						ctable.createTable("cpuhour", allipstr, "cpuhour");// CPU
						ctable.createTable("cpuday", allipstr, "cpuday");// CPU

						ctable.createTable("state", allipstr, "state");// 虚拟机电源状况（打开或关闭）。
						ctable.createTable("gstate", allipstr, "gstate");// 客户机操作系统的状况（开或关）。
						ctable.createVMhostTable(conn, "vm_host", allipstr, "vm_host");// 创建VMWare
						// 物理机的性能信息表
						ctable.createVMguesthostTable(conn, "vm_guesthost", allipstr, "vm_guesthost");// 创建VMWare
						// 虚拟机的性能信息表
						ctable.createVMCRTable(conn, "vm_cluster", allipstr, "vm_cluster");// 创建VMWare
						// 集群的性能信息表
						ctable.createVMDSTable(conn, "vm_datastore", allipstr, "vm_datastore");// 创建VMWare
						// 存储的性能信息表
						ctable.createVMRPTable(conn, "vm_resourcepool", allipstr, "vm_resourcepool");// 创建VMWare
						// 资源池的性能信息表
						// vm_basephysical
						ctable.createVMBaseTable(conn, "vm_basephysical", allipstr, "vm_basephysical");// 创建VMWare
						// 物理机的基础信息表
						ctable.createVMBaseTable(conn, "vm_basevmware", allipstr, "vm_basevmware");// 创建VMWare
						// 虚拟机的基础信息表
						ctable.createVMBaseTable(conn, "vm_baseyun", allipstr, "vm_baseyun");// 创建VMWare
						// 云资源的基础信息表
						ctable.createVMBaseTable(conn, "vm_basedatastore", allipstr, "vm_basedatastore");// 创建VMWare
						// 存储的基础信息表
						ctable.createVMBaseTable(conn, "vm_basedatacenter", allipstr, "vm_basedatacenter");// 创建VMWare
						// 数据中心的基础信息表
						ctable.createVMBaseTable(conn, "vm_baseresource", allipstr, "vm_baseresource");// 创建VMWare
						// 资源池的基础信息表

					}
				} else if (node.getCategory() == 4) {
					// 主机服务器
					// 生成主机表
					if (node.getCollecttype() == 3) {
						ctable.createTable("ping", allipstr, "ping");// Ping
						ctable.createTable("pinghour", allipstr, "pinghour");// Ping
						ctable.createTable("pingday", allipstr, "pingday");// Ping
					} else {
						ctable.createTable("ping", allipstr, "ping");
						ctable.createTable("pinghour", allipstr, "pinghour");
						ctable.createTable("pingday", allipstr, "pingday");

						ctable.createTable("pro", allipstr, "pro");// 进程
						ctable.createTable("prohour", allipstr, "prohour");// 进程小时
						ctable.createTable("proday", allipstr, "proday");// 进程天

						ctable.createSyslogTable("log", allipstr, "log");// 进程天

						ctable.createTable("memory", allipstr, "mem");// 内存
						ctable.createTable("memoryhour", allipstr, "memhour");// 内存
						ctable.createTable("memoryday", allipstr, "memday");// 内存

						ctable.createTable("cpu", allipstr, "cpu");// CPU
						ctable.createTable("cpuhour", allipstr, "cpuhour");// CPU
						ctable.createTable("cpuday", allipstr, "cpuday");// CPU

						ctable.createTable("cpudtl", allipstr, "cpudtl");
						ctable.createTable("cpudtlhour", allipstr, "cpudtlhour");
						ctable.createTable("cpudtlday", allipstr, "cpudtlday");

						ctable.createTable("disk", allipstr, "disk");// yangjun
						ctable.createTable("diskhour", allipstr, "diskhour");
						ctable.createTable("diskday", allipstr, "diskday");

						ctable.createTable("diskincre", allipstr, "diskincre");// 磁盘增长率yangjun
						ctable.createTable("diskinch", allipstr, "diskinch");// 磁盘增长率小时
						ctable.createTable("diskincd", allipstr, "diskincd");// 磁盘增长率天

						ctable.createTable("utilhdxperc", allipstr, "hdperc");
						ctable.createTable("hdxperchour", allipstr, "hdperchour");
						ctable.createTable("hdxpercday", allipstr, "hdpercday");

						ctable.createTable("utilhdx", allipstr, "hdx");
						ctable.createTable("utilhdxhour", allipstr, "hdxhour");
						ctable.createTable("utilhdxday", allipstr, "hdxday");

						ctable.createTable("allutilhdx", allipstr, "allhdx");
						ctable.createTable("autilhdxh", allipstr, "ahdxh");
						ctable.createTable("autilhdxd", allipstr, "ahdxd");

						ctable.createTable("discardsperc", allipstr, "dcardperc");
						ctable.createTable("dcarperh", allipstr, "dcarperh");
						ctable.createTable("dcarperd", allipstr, "dcarperd");

						ctable.createTable("errorsperc", allipstr, "errperc");
						ctable.createTable("errperch", allipstr, "errperch");
						ctable.createTable("errpercd", allipstr, "errpercd");

						ctable.createTable("packs", allipstr, "packs");
						ctable.createTable("packshour", allipstr, "packshour");
						ctable.createTable("packsday", allipstr, "packsday");

						ctable.createTable("inpacks", allipstr, "inpacks");
						ctable.createTable("ipacksh", allipstr, "ipacksh");
						ctable.createTable("ipackd", allipstr, "ipackd");

						ctable.createTable("outpacks", allipstr, "outpacks");
						ctable.createTable("opackh", allipstr, "opackh");
						ctable.createTable("opacksd", allipstr, "opacksd");

						ctable.createTable("software", allipstr, "software");
						if (node.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.2.3.1.2.1.1")) {
							// 生成换页率
							ctable.createTable("pgused", allipstr, "pgused");
							ctable.createTable("pgusedhour", allipstr, "pgusedhour");
							ctable.createTable("pgusedday", allipstr, "pgusedday");
						}
					}
				} else if (node.getCategory() == 16) {// 中电国际艾默生空调添加的类型
					ctable.createTable("ping", allipstr, "ping");// Ping
					ctable.createTable("pinghour", allipstr, "pinghour");// Ping
					ctable.createTable("pingday", allipstr, "pingday");// Ping
				} else if (node.getCategory() == 17) {
					ctable.createTable("ping", allipstr, "ping");// Ping
					ctable.createTable("pinghour", allipstr, "pinghour");// Ping
					ctable.createTable("pingday", allipstr, "pingday");// Ping
					ctable.createTable("input", allipstr, "input");// input
					ctable.createTable("inputhour", allipstr, "inputhour");// input
					ctable.createTable("inputday", allipstr, "inputday");// input
					ctable.createTable("output", allipstr, "output");// output
					ctable.createTable("outputhour", allipstr, "outputhour");// output
					ctable.createTable("outputday", allipstr, "outputday");// output
				}

				String bridgeAddress = node.getBridgeAddress();
				if (bridgeAddress != null && !"".equalsIgnoreCase(bridgeAddress)) {
					bridgeAddress = CommonUtil.removeIllegalStr(bridgeAddress.replace("'", "").replace("<", "").replace(">", ""));
				}

				StringBuffer sql = new StringBuffer(300);
				sql.append("insert into topo_host_node(id,asset_id,location,ip_address,ip_long,net_mask,category,community,sys_oid,sys_name,super_node,");
				sql
						.append("local_net,layer,sys_descr,sys_location,sys_contact,alias,type,managed,bridge_address,status,discoverstatus,write_community,snmpversion,ostype,transfer,collecttype,bid,sendemail,sendmobiles,sendphone,supperid,");
				sql.append("securityLevel,securityName,v3_ap,authPassPhrase,v3_privacy,privacyPassPhrase)values(");
				sql.append(node.getId());
				sql.append(",'");
				sql.append(node.getAssetid());
				sql.append("','");
				sql.append(node.getLocation());
				sql.append("','");
				sql.append(node.getIpAddress());
				sql.append("',");
				sql.append(NetworkUtil.ip2long(node.getIpAddress()));
				sql.append(",'");
				sql.append(node.getNetMask());
				sql.append("',");
				sql.append(node.getCategory());
				sql.append(",'");
				sql.append(node.getCommunity());
				sql.append("','");
				sql.append(node.getSysOid());
				sql.append("','");
				sql.append(replace(node.getSysName()));
				sql.append("',");
				sql.append(node.getSuperNode());
				sql.append(",");
				sql.append(node.getLocalNet());
				sql.append(",");
				sql.append(node.getLayer());
				sql.append(",'");
				sql.append(replace(node.getSysDescr()));
				sql.append("','");
				sql.append(replace(node.getSysLocation()));
				sql.append("','");
				sql.append(replace(node.getSysContact()));
				sql.append("','");
				if (node.getAlias() == null) {
					sql.append(replace(node.getSysName()));
				} else {
					sql.append(replace(node.getAlias()));
				}
				sql.append("','");
				sql.append(node.getType());
				sql.append("',");
				if (node.isManaged()) {
					sql.append("1,'");
				} else {
					sql.append("0,'");
				}

				// if (null != bridgeAddress && bridgeAddress.length() > 60) {
				// bridgeAddress = "";
				// }

				sql.append(bridgeAddress);
				sql.append("',");
				sql.append(node.getStatus());
				sql.append(",");
				sql.append(node.getDiscoverstatus());
				sql.append(",'");
				sql.append(node.getWritecommunity());
				sql.append("',");
				sql.append(node.getSnmpversion());
				sql.append(",");
				sql.append(node.getOstype());
				sql.append(",");
				sql.append(node.getTransfer());
				sql.append(",");
				sql.append(node.getCollecttype());
				sql.append(",'");
				sql.append(node.getBid());
				sql.append("','");
				sql.append(node.getSendemail());
				sql.append("','");
				sql.append(node.getSendmobiles());
				sql.append("','");
				sql.append(node.getSendphone());
				sql.append("','");
				sql.append(node.getSupperid());
				sql.append("',");
				sql.append(node.getSecuritylevel());
				sql.append(",'");
				sql.append(node.getSecurityName());
				sql.append("',");
				sql.append(node.getV3_ap());
				sql.append(",'");
				sql.append(node.getAuthpassphrase());
				sql.append("',");
				sql.append(node.getV3_privacy());
				sql.append(",'");
				sql.append(node.getPrivacyPassphrase());
				sql.append("')");
				conn.addBatch(sql.toString());
				donehost.put(node.getId(), node);
				// 加入连通性检测类型表,若是PING检测则不加入
				if (node.getCollecttype() == SystemConstant.COLLECTTYPE_TELNETCONNECT || node.getCollecttype() == SystemConstant.COLLECTTYPE_SSHCONNECT) {
					StringBuffer configsql = new StringBuffer(200);
					configsql.append("insert into nms_connecttypeconfig(node_id,connecttype,username,password,login_prompt,password_prompt,shell_prompt)" + "values('");
					configsql.append(node.getId());
					configsql.append("','");
					if (node.getCollecttype() == SystemConstant.COLLECTTYPE_TELNETCONNECT) {
						configsql.append("telnet");
					} else if (node.getCollecttype() == SystemConstant.COLLECTTYPE_SSHCONNECT) {
						configsql.append("ssh");
					} else {
						configsql.append("ping");
					}
					configsql.append("','");
					configsql.append("");
					configsql.append("','");
					configsql.append("");
					configsql.append("','");
					configsql.append("");
					configsql.append("','");
					configsql.append("");
					configsql.append("','");
					configsql.append("");
					configsql.append("')");
					conn.addBatch(sql.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		try {
			conn.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
		conn.commit();
		conn.addBatch("update topo_host_node a set a.type=(select b.descr  from nms_device_type b where a.sys_oid=b.sys_oid)");
		// 加入网络设备IP的别名
		donehost = new Hashtable();
		for (int i = 0; i < hostList.size(); i++) {
			try {
				Host node = (Host) hostList.get(i);
				if (donehost.containsKey(node.getId())) {
					continue;
				}
				if (node.getAliasIfEntitys() != null && node.getAliasIfEntitys().size() > 0) {
					for (int k = 0; k < node.getAliasIfEntitys().size(); k++) {
						IfEntity ifEntity = (IfEntity) node.getAliasIfEntitys().get(k);
						StringBuffer sql = new StringBuffer(300);
						sql.append("insert into topo_ipalias(ipaddress,aliasip,indexs,descr,speeds,types) values('");
						if (node.getAdminIp() != null && node.getAdminIp().trim().length() > 0) {
							sql.append(node.getAdminIp());
							// 过滤掉管理地址和别名IP相同的记录
							// SysLogger.info(node.getAdminIp()+"----"+aliasip);
							if (node.getAdminIp().equalsIgnoreCase(ifEntity.getIpAddress())) {
								continue;
							}
						} else {
							// 过滤掉管理地址和别名IP相同的记录
							sql.append(node.getIpAddress());
							if (node.getIpAddress().equalsIgnoreCase(ifEntity.getIpAddress())) {
								continue;
							}
						}

						sql.append("','");
						sql.append(ifEntity.getIpAddress());
						sql.append("','");
						sql.append(ifEntity.getIndex());
						sql.append("','");
						sql.append(ifEntity.getDescr());
						sql.append("','");
						sql.append(ifEntity.getSpeed());
						sql.append("',");
						sql.append(ifEntity.getType());

						sql.append(")");
						conn.addBatch(sql.toString());
						donehost.put(node.getId(), node);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			conn.executeBatch();
		} catch (Exception e) {

		}
		IpAliasDao ipaliasdao = new IpAliasDao();
		try {
			ipaliasdao.RefreshIpAlias();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ipaliasdao.close();
		}
	}

	public void addID() {
		conn.executeUpdate("update topo_node_id set id=" + KeyGenerator.getInstance().getHostKey());
	}

	/**
	 * 加入主机接口数据
	 */
	public void addInterfaceData(List hostList) {
		int id = getNextID("topo_interface");
		for (int i = 0; i < hostList.size(); i++) {
			com.afunms.discovery.Host host = (com.afunms.discovery.Host) hostList.get(i);
			List ifList = host.getIfEntityList();
			if (ifList == null) {
				continue;
			}
			com.afunms.discovery.IfEntity ifEntity = null;
			for (int j = 0; j < ifList.size(); j++) {
				ifEntity = (com.afunms.discovery.IfEntity) ifList.get(j);
				String physAddress = ifEntity.getPhysAddress().replace("'", "").replace("<", "").replace(">", "");
				physAddress = CommonUtil.removeIllegalStr(physAddress);
				StringBuffer sql = new StringBuffer(300);
				sql.append("insert into topo_interface(id,node_id,entity,descr,port,speed,phys_address,ip_address,oper_status,type,chassis,slot,uport)values(");
				sql.append(id++);
				sql.append(",");
				sql.append(host.getId());
				sql.append(",'");
				sql.append(ifEntity.getIndex());
				sql.append("','");
				sql.append(replace(ifEntity.getDescr()));
				sql.append("','");
				sql.append(ifEntity.getPort() == null ? "" : ifEntity.getPort());
				sql.append("','");
				sql.append(ifEntity.getSpeed());
				sql.append("','");
				sql.append(physAddress);
				sql.append("','");
				sql.append(ifEntity.getIpList()); // 所有IP地址
				sql.append("',");
				sql.append(ifEntity.getOperStatus());
				sql.append(",");
				sql.append(ifEntity.getType()); // 端口类型
				sql.append(",");
				sql.append(ifEntity.getChassis()); // 框架
				sql.append(",");
				sql.append(ifEntity.getSlot()); // 槽
				sql.append(",");
				sql.append(ifEntity.getUport()); // 口
				sql.append(")");
				conn.executeUpdate(sql.toString(), false);
			}// end_for_j
			conn.commit();
			conn.executeUpdate("update topo_interface set alias=descr");
		}// end_for_i
	}

	/**
	 * 加入链路数据
	 */
	public void addLinkData(List linkList) {
		if (linkList == null) {
			return;
		}

		RepairLinkDao repairdao = new RepairLinkDao();
		List repairlist = repairdao.loadAll();
		if (repairlist == null) {
			repairlist = new ArrayList();
		}
		for (int i = 0; i < linkList.size(); i++) {
			Link link = (Link) linkList.get(i);

			for (int k = 0; k < repairlist.size(); k++) {
				RepairLink repairlink = (RepairLink) repairlist.get(k);
				Host starthost = DiscoverEngine.getInstance().getHostByID(link.getStartId());
				Host endhost = DiscoverEngine.getInstance().getHostByID(link.getEndId());
				if (starthost.getIpAddress().equalsIgnoreCase(repairlink.getStartIp()) && link.getStartIndex().equalsIgnoreCase(repairlink.getStartIndex())
						&& endhost.getIpAddress().equalsIgnoreCase(repairlink.getEndIp()) && link.getEndIndex().equalsIgnoreCase(repairlink.getEndIndex())) {
					// 存在修改过的连接关系
					link.setStartIndex(repairlink.getNewStartIndex());
					link.setEndIndex(repairlink.getNewEndIndex());
					link.setStartDescr(starthost.getIfEntityByIndex(repairlink.getNewStartIndex()).getDescr());
					link.setEndDescr(endhost.getIfEntityByIndex(repairlink.getNewEndIndex()).getDescr());
					link.setStartPort(starthost.getIfEntityByIndex(repairlink.getNewStartIndex()).getPort());
					link.setEndPort(endhost.getIfEntityByIndex(repairlink.getNewEndIndex()).getPort());
					linkList.set(i, link);
				} else if (starthost.getIpAddress().equalsIgnoreCase(repairlink.getEndIp()) && link.getStartIndex().equalsIgnoreCase(repairlink.getEndIndex())
						&& endhost.getIpAddress().equalsIgnoreCase(repairlink.getStartIp()) && link.getEndIndex().equalsIgnoreCase(repairlink.getStartIndex())) {
					// 存在修改过的连接关系
					link.setStartIndex(repairlink.getNewEndIndex());
					link.setEndIndex(repairlink.getNewStartIndex());
					link.setStartDescr(endhost.getIfEntityByIndex(repairlink.getNewEndIndex()).getDescr());
					link.setEndDescr(starthost.getIfEntityByIndex(repairlink.getNewStartIndex()).getDescr());
					link.setStartPort(endhost.getIfEntityByIndex(repairlink.getNewEndIndex()).getPort());
					link.setEndPort(starthost.getIfEntityByIndex(repairlink.getNewStartIndex()).getPort());
					linkList.set(i, link);
				}
			}
		}

		int id = getNextID("topo_network_link");
		Hashtable donelinkhashtable = new Hashtable();
		for (int i = 0; i < linkList.size(); i++) {

			Link link = (Link) linkList.get(i);
			String startPhysAddress = link.getStartPhysAddress();
			String endPhysAddress = link.getEndPhysAddress();
			if (startPhysAddress != null) {
				startPhysAddress = CommonUtil.removeIllegalStr(startPhysAddress);
			} else {
				startPhysAddress = "";
			}
			if (endPhysAddress != null) {
				endPhysAddress = CommonUtil.removeIllegalStr(endPhysAddress);
			} else {
				endPhysAddress = "";
			}
			if (donelinkhashtable.containsKey(link.getStartIp() + "_" + link.getStartIndex() + "/" + link.getEndIp() + "_" + link.getEndIndex())) {
				continue;
			} else {
				donelinkhashtable.put(link.getStartIp() + "_" + link.getStartIndex() + "/" + link.getEndIp() + "_" + link.getEndIndex(), "");
			}
			id = id + 1;

			StringBuffer sql = new StringBuffer(300);
			sql.append("insert into topo_network_link(id,link_name,start_id,end_id,start_ip,end_ip,start_index,");
			sql.append("end_index,start_descr,end_descr,start_port,end_port,start_mac,end_mac,assistant,type,findtype,linktype,max_speed)values(");
			sql.append(id);
			sql.append(",'");
			sql.append(link.getStartIp() + "_" + link.getStartIndex() + "/" + link.getEndIp() + "_" + link.getEndIndex());
			sql.append("',");
			sql.append(link.getStartId());
			sql.append(",");
			sql.append(link.getEndId());
			sql.append(",'");
			sql.append(link.getStartIp());
			sql.append("','");
			sql.append(link.getEndIp());
			sql.append("','");
			sql.append(link.getStartIndex());
			sql.append("','");
			sql.append(link.getEndIndex());
			sql.append("','");
			sql.append(link.getStartDescr());
			sql.append("','");
			sql.append(link.getEndDescr());
			sql.append("','");
			sql.append(link.getStartPort() == null ? "" : link.getStartPort());
			sql.append("','");
			sql.append(link.getEndPort() == null ? "" : link.getEndPort());
			sql.append("','");
			sql.append(startPhysAddress);
			sql.append("','");
			sql.append(endPhysAddress);
			sql.append("',");
			sql.append(link.getAssistant());
			sql.append(",");
			sql.append(1);
			sql.append(",");
			sql.append(link.getFindtype());
			sql.append(",");
			sql.append(link.getLinktype());
			sql.append(",'200000')");
			try {
				conn.executeUpdate(sql.toString(), false);
			} catch (Exception e) {

			}

			CreateTableManager ctable = new CreateTableManager();
			try {
				ctable.createTable("lkping", (id) + "", "lkping");// 链路状态
				ctable.createTable("lkpinghour", (id) + "", "lkpinghour");// 链路状态按小时
				ctable.createTable("lkpingday", (id) + "", "lkpingday");// 链路状态按天

				ctable.createTable("lkuhdx", (id) + "", "lkuhdx");// 链路流速
				ctable.createTable("lkuhdxhour", (id) + "", "lkuhdxhour");// 链路流速
				ctable.createTable("lkuhdxday", (id) + "", "lkuhdxday");// 链路流速

				ctable.createTable("lkuhdxp", (id) + "", "lkuhdxp");// 链路带宽利用率
				ctable.createTable("lkuhdxphour", (id) + "", "lkuhdxphour");// 链路带宽利用率
				ctable.createTable("lkuhdxpday", (id) + "", "lkuhdxpday");// 链路带宽利用率

				conn.executeBatch();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		donelinkhashtable.clear();
		donelinkhashtable = null;
		conn.commit();
	}

	public void addMonitor(int node_id, String ip, String category) {
		try {
			for (int i = 0; i < moList.size(); i++) {
				MonitorObject moid = (MonitorObject) moList.get(i);
				if (!moid.isDefault()) {
					continue; // 只加入默认需要的监视器
				}
				if (!moid.getNodetype().equalsIgnoreCase(category)) {
					continue;
				}
				// nmID++;
				StringBuffer sql = new StringBuffer(200);
				sql.append("insert into topo_node_monitor(node_id,node_ip,category,moid,unit,threshold,compare,compare_type,upper_times,");
				sql.append("alarm_info,enabled,alarm_level,poll_interval,interval_unit,threshold_unit,descr,nodetype,subentity,limenvalue0,limenvalue1,limenvalue2,");
				sql.append("time0,time1,time2,sms0,sms1,sms2) values(");
				sql.append(node_id);
				sql.append(",'");
				sql.append(ip);
				sql.append("','");
				sql.append(moid.getCategory());
				sql.append("','");
				sql.append(moid.getMoid());
				sql.append("','");
				sql.append(moid.getUnit());
				sql.append("',");
				sql.append(moid.getThreshold());
				sql.append(",");
				sql.append(moid.getCompare());
				sql.append(",");
				sql.append(moid.getCompareType());
				sql.append(",");
				sql.append(moid.getUpperTimes());
				sql.append(",'");
				sql.append(moid.getAlarmInfo());
				sql.append("',");
				sql.append(moid.isEnabled() ? 1 : 0);
				sql.append(",");
				sql.append(moid.getAlarmLevel());
				sql.append(",");
				sql.append(moid.getPollInterval());
				sql.append(",'");
				sql.append(moid.getIntervalUnit());
				sql.append("','");
				sql.append(moid.getUnit());
				sql.append("','");
				sql.append(moid.getDescr());
				sql.append("','");
				sql.append(moid.getNodetype());
				sql.append("','");
				sql.append(moid.getSubentity());
				sql.append("',");
				sql.append(moid.getLimenvalue0());
				sql.append(",");
				sql.append(moid.getLimenvalue1());
				sql.append(",");
				sql.append(moid.getLimenvalue2());
				sql.append(",");
				sql.append(moid.getTime0());
				sql.append(",");
				sql.append(moid.getTime1());
				sql.append(",");
				sql.append(moid.getTime2());
				sql.append(",");
				sql.append(moid.getSms0());
				sql.append(",");
				sql.append(moid.getSms1());
				sql.append(",");
				sql.append(moid.getSms2());
				sql.append(")");
				conn.executeUpdate(sql.toString(), false);
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 加入监视器数据
	 */
	public void addMonitor(List hostList) {
		for (int i = 0; i < hostList.size(); i++) {
			Host node = (Host) hostList.get(i);
			try {
				if (node.getCategory() == 4) {
					addMonitor(node.getId(), node.getIpAddress(), "host");
				}
				if (node.getCategory() < 4) {
					addMonitor(node.getId(), node.getIpAddress(), "net");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} // end_for
	}

	/**
	 * 加入子网数据
	 */
	public void addSubNetData(List netList) {
		if (netList == null) {
			return;
		}

		for (int i = 0; i < netList.size(); i++) {
			SubNet subnet = (SubNet) netList.get(i);
			StringBuffer sql = new StringBuffer(200);
			sql.append("insert into topo_subnet(id,net_address,net_mask,net_long,managed)values(");
			sql.append(subnet.getId());
			sql.append(",'");
			sql.append(subnet.getNetAddress());
			sql.append("','");
			sql.append(subnet.getNetMask());
			sql.append("',");
			sql.append(NetworkUtil.ip2long(subnet.getNetAddress()));
			sql.append(",1)");
			conn.executeUpdate(sql.toString(), false);
		}
		conn.commit();
	}

	/**
	 * 清空数据
	 */
	public void clear() {

		// 开始清除所有的节点历史数据信息 修改人:hukelei 修改时间: 2010-07-28
		conn.addBatch("delete from topo_host_node");

		LinkDao linkdao = new LinkDao();
		List linkList = new ArrayList();
		try {
			linkList = linkdao.loadByTpye(0);
			linkdao = new LinkDao();
			linkdao.deleteutils(linkList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			linkdao.close();
		}
		// 结束清除所有的链路历史数据信息 修改人:hukelei 修改时间: 2010-07-28
		conn.addBatch("delete from topo_network_link");

		conn.addBatch("delete from topo_subnet");
		conn.addBatch("delete from topo_node_monitor");
		conn.addBatch("delete from topo_node_multi_data");
		conn.addBatch("delete from topo_node_single_data");
		conn.addBatch("delete from topo_interface");
		conn.addBatch("delete from topo_interface_data");
		conn.addBatch("delete from topo_custom_xml");
		conn.addBatch("delete from nms_alarm_message");
		conn.addBatch("delete from server_telnet_config");
		conn.addBatch("delete from app_ip_node");
		conn.addBatch("delete from app_tomcat_node");
		conn.addBatch("delete from app_db_node");
		conn.addBatch("delete from system_eventlist where subtype='host' or subtype='net' or subtype='db'");
		conn.executeBatch();
		nmID = 0;
	}

	private void collectHostData(Host node) {
		try {
			Hashtable hashv = null;
			LoadWindowsWMIFile windowswmi = null;
			I_HostCollectData hostdataManager = new HostCollectDataManager();
			if (node.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL) {
				// SHELL获取方式
				try {
					if (node.getOstype() == 6) {
						// SysLogger.info("采集:
						// 开始采集IP地址为"+node.getIpAddress()+"类型为AIX主机服务器的数据");
						// //AIX服务器
						// try{
						// aix = new LoadAixFile(node.getIpAddress());
						// hashv=aix.getTelnetMonitorDetail();
						// hostdataManager.createHostData(node.getIpAddress(),hashv);
						// }catch(Exception e){
						// e.printStackTrace();
						// }
					} else if (node.getOstype() == 9) {
						// SysLogger.info("采集:
						// 开始采集IP地址为"+node.getIpAddress()+"类型为LINUX主机服务器的数据");
						// //LINUX服务器
						// try{
						// linux = new LoadLinuxFile(node.getIpAddress());
						// hashv=linux.getTelnetMonitorDetail();
						// hostdataManager.createHostData(node.getIpAddress(),hashv);
						// }catch(Exception e){
						// e.printStackTrace();
						// }
					} else if (node.getOstype() == 7) {
						// SysLogger.info("采集:
						// 开始采集IP地址为"+node.getIpAddress()+"类型为HPUNIX主机服务器的数据");
						// //HPUNIX服务器
						// try{
						// hpunix = new LoadHpUnixFile(node.getIpAddress());
						// hashv=hpunix.getTelnetMonitorDetail();
						// hostdataManager.createHostData(node.getIpAddress(),hashv);
						// }catch(Exception e){
						// e.printStackTrace();
						// }
					} else if (node.getOstype() == 8) {
						// SysLogger.info("采集:
						// 开始采集IP地址为"+node.getIpAddress()+"类型为SOLARIS主机服务器的数据");
						// //WINDOWS服务器
						// try{
						// sununix = new LoadSunOSFile(node.getIpAddress());
						// hashv=sununix.getTelnetMonitorDetail();
						// hostdataManager.createHostData(node.getIpAddress(),hashv);
						// }catch(Exception e){
						// e.printStackTrace();
						// }
					} else if (node.getOstype() == 5) {
						try {
							windowswmi = new LoadWindowsWMIFile(node.getIpAddress());
							hashv = windowswmi.getTelnetMonitorDetail();
							hostdataManager.createHostData(node.getIpAddress(), hashv);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				hashv = null;
			} else if (node.getCollecttype() == SystemConstant.COLLECTTYPE_WMI) {
				// WINDOWS下的WMI采集方式
				try {
					windowswmi = new LoadWindowsWMIFile(node.getIpAddress());
					hashv = windowswmi.getTelnetMonitorDetail();
					hostdataManager.createHostData(node.getIpAddress(), hashv);
				} catch (Exception e) {
					e.printStackTrace();
				}
				hashv = null;
			} else {
				// SNMP采集方式
				HostNode hostnode = new HostNode();
				hostnode.setId(node.getId());
				hostnode.setSysName(node.getSysName());
				hostnode.setCategory(node.getCategory());
				hostnode.setCommunity(node.getCommunity());
				hostnode.setSnmpversion(node.getSnmpversion());
				hostnode.setIpAddress(node.getIpAddress());
				hostnode.setLocalNet(node.getLocalNet());
				hostnode.setNetMask(node.getNetMask());
				hostnode.setAlias(node.getAlias());
				hostnode.setSysDescr(node.getSysDescr());
				hostnode.setSysOid(node.getSysOid());
				hostnode.setType(node.getType());
				hostnode.setManaged(node.isManaged());
				hostnode.setOstype(node.getOstype());
				hostnode.setCollecttype(node.getCollecttype());
				hostnode.setSysLocation(node.getSysLocation());
				hostnode.setSendemail(node.getSendemail());
				hostnode.setSendmobiles(node.getSendmobiles());
				hostnode.setSendphone(node.getSendphone());
				hostnode.setBid(node.getBid());
				hostnode.setEndpoint(node.getEndpoint());
				hostnode.setStatus(0);
				hostnode.setSupperid(node.getSupperid());
				try {
					NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
					List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
					try {
						// 获取被启用的所有被监视指标
						monitorItemList = indicatorsdao.findByNodeIdAndTypeAndSubtype(hostnode.getId() + "", "host", "");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						indicatorsdao.close();
					}
					if (monitorItemList != null && monitorItemList.size() > 0) {
						for (int i = 0; i < monitorItemList.size(); i++) {
							NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators) monitorItemList.get(i);
							PollDataUtil polldatautil = new PollDataUtil();
							polldatautil.collectHostData(nodeGatherIndicators);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public boolean createTableForAS400(Host host) {
		boolean result = false;

		try {
			CreateTableManager ctable = new CreateTableManager();

			String ip = host.getIpAddress();
			String allipstr = "";
			allipstr = SysUtil.doip(ip);

			ctable.createRootTable("systemasp", allipstr);
			ctable.createRootTable("systemasphour", allipstr);
			ctable.createRootTable("systemaspday", allipstr);

			ctable.createRootTable("dbcapability", allipstr);
			ctable.createRootTable("dbcaphour", allipstr);
			ctable.createRootTable("dbcapday", allipstr);

			result = true;
		} catch (RuntimeException e) {
			e.printStackTrace();

			result = false;
		}

		return result;
	}

	/*
	 * 删除监视项
	 */
	public void deleteIpAlias(int node_id, String ip) {
		try {
			String sql = "delete from topo_node_monitor where node_id=" + node_id;
			conn.executeUpdate(sql.toString(), false);
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			e.printStackTrace();
		}
	}

	/*
	 * 删除监视项
	 */
	public void deleteMonitor(int node_id, String ip) {
		try {
			String sql = "delete from topo_node_monitor where node_id=" + node_id;
			conn.executeUpdate(sql.toString(), false);
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			e.printStackTrace();
		}
	}

	public BaseVo loadFromRS(ResultSet rs) {
		return null;
	}

	/**
	 * 把'换成_,保证sql不出错
	 */
	private String replace(String oldStr) {
		if (oldStr == null) {
			return "";
		}

		if (oldStr.length() > 45) {
			oldStr = oldStr.substring(0, 45);
		}
		if (oldStr.indexOf("'") >= 0) {
			return oldStr.replace('\'', '_');
		} else {
			return oldStr;
		}
	}
}