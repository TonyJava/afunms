package com.afunms.application.ajaxManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.dao.AlarmPortDao;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.util.ProcessGroupConfigurationUtil;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.ChartXml;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.NetworkUtil;
import com.afunms.common.util.NodeAlarmUtil;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.common.util.UserAuditUtil;
import com.afunms.config.dao.BusinessDao;
import com.afunms.config.dao.IpAliasDao;
import com.afunms.config.dao.IpaddressPanelDao;
import com.afunms.config.dao.NetNodeCfgFileDao;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.dao.SupperDao;
import com.afunms.config.model.Business;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.dao.NetSyslogDao;
import com.afunms.event.dao.NetSyslogRuleDao;
import com.afunms.event.model.NetSyslogRule;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.initialize.PortConfigCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.loader.HostLoader;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.VMWareConnectConfig;
import com.afunms.polling.om.VMWareVid;
import com.afunms.portscan.dao.PortScanDao;
import com.afunms.system.model.User;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.system.util.TimeShareConfigUtil;
import com.afunms.topology.dao.ConnectDao;
import com.afunms.topology.dao.DiscoverCompleteDao;
import com.afunms.topology.dao.HostInterfaceDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.IpMacBaseDao;
import com.afunms.topology.dao.IpMacChangeDao;
import com.afunms.topology.dao.IpMacDao;
import com.afunms.topology.dao.LineDao;
import com.afunms.topology.dao.LinkDao;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.dao.NetSyslogNodeRuleDao;
import com.afunms.topology.dao.NodeDependDao;
import com.afunms.topology.dao.NodeEquipDao;
import com.afunms.topology.dao.NodeMonitorDao;
import com.afunms.topology.dao.RelationDao;
import com.afunms.topology.dao.RepairLinkDao;
import com.afunms.topology.dao.VMWareConnectConfigDao;
import com.afunms.topology.dao.VMWareVidDao;
import com.afunms.topology.model.Connect;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.Link;
import com.afunms.topology.model.ManageXml;
import com.afunms.topology.model.NetSyslogNodeRule;
import com.afunms.topology.model.NodeDepend;
import com.afunms.topology.model.Relation;
import com.afunms.topology.util.TopoHelper;
import com.afunms.topology.util.XmlOperator;
import com.afunms.vmware.vim25.common.VIMMgr;

@SuppressWarnings("rawtypes")
public class NodeHelperAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {

	@Override
	public void execute(String action) {
		if (action.equals("deleteNodes")) {
			deleteNodes();
		} else if (action.equals("getBids")) {
			getBids();
		} else if (action.equals("addNode")) {
			addNode();
		} else if (action.equals("addBusiness")) {
			addBusiness();
		} else if (action.equals("deleteBusinessNode")) {
			deleteBusinessNode();
		} else if (action.equals("updateBusiness")) {
			updateBusiness();
		} else if (action.equals("beforeEdit")) {
			beforeEdit();
		} else if (action.equals("updateNode")) {
			updateNode();
		} else if (action.equals("batchAddMonitor")) {
			batchAddMonitor();
		} else if (action.equals("batchCancleMonitor")) {
			batchCancleMonitor();
		}
	}

	private void batchAddMonitor() {
		StringBuffer sb = new StringBuffer("启用监控");
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		DBManager dbOp = new DBManager();
		String sql = null;
		try {
			for (int i = 0; i < ids.length; i++) {
				sql = "update topo_host_node set managed=1 where id=" + ids[i];
				dbOp.addBatch(sql);
			}
			dbOp.executeBatch();
			sb.append("成功");
		} catch (Exception e) {
			e.printStackTrace();
			dbOp.rollback();
			sb.append("失败");
		} finally {
			if (null != dbOp) {
				dbOp.close();
			}
		}
		out.print(sb.toString());
		out.flush();
	}

	private void batchCancleMonitor() {
		StringBuffer sb = new StringBuffer("取消监控");
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		DBManager dbOp = new DBManager();
		String sql = null;
		try {
			for (int i = 0; i < ids.length; i++) {
				sql = "update topo_host_node set managed=0 where id=" + ids[i];
				dbOp.addBatch(sql);
			}
			dbOp.executeBatch();
			sb.append("成功");
		} catch (Exception e) {
			e.printStackTrace();
			dbOp.rollback();
			sb.append("失败");
		} finally {
			if (null != dbOp) {
				dbOp.close();
			}
		}
		out.print(sb.toString());
		out.flush();
	}

	private void updateNode() {

		HostNode vo = new HostNode();
		vo.setId(getParaIntValue("id"));
		vo.setAlias(getParaValue("alias"));
		vo.setSnmpversion(getParaIntValue("snmpversion"));
		vo.setCommunity(getParaValue("community"));
		vo.setManaged(getParaIntValue("managed") == 1 ? true : false);
		if (getParaValue("bid") == null || getParaValue("bid").equals("notSet") || getParaValue("bid").equals("")) {
			vo.setBid(getParaValue("bids"));
		} else {
			vo.setBid(getParaValue("bid"));
		}

		String ipaddress = getParaValue("ipaddress");
		vo.setIpAddress(ipaddress);
		// 更新内存
		String formerip = "";
		Host host = (Host) PollingEngine.getInstance().getNodeByID(vo.getId());
		if (host != null) {

			host.setAlias(vo.getAlias());
			host.setSnmpversion(vo.getSnmpversion());
			host.setManaged(vo.isManaged());
			host.setCommunity(vo.getCommunity());
		} else {
			if (getParaIntValue("managed") == 1) {
				HostNodeDao dao = new HostNodeDao();
				HostNode hostnode = null;
				try {
					hostnode = dao.loadHost(vo.getId());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dao.close();
				}
				hostnode.setAssetid(getParaValue("assetid"));
				hostnode.setLocation(getParaValue("location"));
				hostnode.setAlias(getParaValue("alias"));
				host.setSnmpversion(getParaIntValue("snmpversion"));
				hostnode.setCommunity(getParaValue("community"));
				host.setTransfer(getParaIntValue("transfer"));
				hostnode.setManaged(getParaIntValue("managed") == 1 ? true : false);
				hostnode.setSendemail(getParaValue("sendemail"));
				hostnode.setSendmobiles(getParaValue("sendmobiles"));
				hostnode.setSendphone(getParaValue("sendphone"));
				hostnode.setSupperid(getParaIntValue("supper"));// snow add at
				// 2010-5-18
				HostLoader loader = new HostLoader();
				try {
					loader.loadOne(hostnode);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// PollingEngine.getInstance().addNode(node)
			}
		}

		// 重新获取一下内存中的对象
		host = (Host) PollingEngine.getInstance().getNodeByID(vo.getId());

		if (!host.getIpAddress().equalsIgnoreCase(ipaddress)) {
			// IP地址已经被修改,需要更新相关的IP关联的信息
			formerip = host.getIpAddress();

			String ip = formerip;
			String allipstr = SysUtil.doip(ip);

			CreateTableManager ctable = new CreateTableManager();
			DBManager conn = new DBManager();
			try {

				if (host.getCategory() < 4 || host.getCategory() == 7 || host.getCategory() == 8) {
					// 先删除网络设备表
					// 连通率
					try {
						ctable.deleteTable("ping", allipstr, "pinghour");// Ping
						ctable.deleteTable("pinghour", allipstr, "pinghour");// PingHour
						ctable.deleteTable("pingday", allipstr, "pingday");// PingDay
					} catch (Exception e) {
						e.printStackTrace();
					}

					// 内存
					try {
						ctable.deleteTable("memory", allipstr, "mem");// 内存
						ctable.deleteTable("memoryhour", allipstr, "memhour");// 内存
						ctable.deleteTable("memoryday", allipstr, "memday");// 内存
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						ctable.deleteTable("flash", allipstr, "flash");// 闪存
						ctable.deleteTable("flashhour", allipstr, "flashhour");// 闪存
						ctable.deleteTable("flashday", allipstr, "flashday");// 闪存
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						ctable.deleteTable("buffer", allipstr, "buffer");// 缓存
						ctable.deleteTable("bufferhour", allipstr, "bufferhour");// 缓存
						ctable.deleteTable("bufferday", allipstr, "bufferday");// 缓存
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						ctable.deleteTable("fan", allipstr, "fan");// 风扇
						ctable.deleteTable("fanhour", allipstr, "fanhour");// 风扇
						ctable.deleteTable("fanday", allipstr, "fanday");// 风扇
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						ctable.deleteTable("power", allipstr, "power");// 电源
						ctable.deleteTable("powerhour", allipstr, "powerhour");// 电源
						ctable.deleteTable("powerday", allipstr, "powerday");// 电源
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						ctable.deleteTable("vol", allipstr, "vol");// 电压
						ctable.deleteTable("volhour", allipstr, "volhour");// 电压
						ctable.deleteTable("volday", allipstr, "volday");// 电压
					} catch (Exception e) {
						e.printStackTrace();
					}

					// CPU
					try {
						ctable.deleteTable("cpu", allipstr, "cpu");// CPU
						ctable.deleteTable("cpuhour", allipstr, "cpuhour");// CPU
						ctable.deleteTable("cpuday", allipstr, "cpuday");// CPU
					} catch (Exception e) {
						e.printStackTrace();
					}

					// 带宽利用率
					try {
						ctable.deleteTable("utilhdxperc", allipstr, "hdperc");
						ctable.deleteTable("hdxperchour", allipstr, "hdperchour");
						ctable.deleteTable("hdxpercday", allipstr, "hdpercday");
					} catch (Exception e) {
						e.printStackTrace();
					}

					// 流速
					try {
						ctable.deleteTable("utilhdx", allipstr, "hdx");
						ctable.deleteTable("utilhdxhour", allipstr, "hdxhour");
						ctable.deleteTable("utilhdxday", allipstr, "hdxday");
					} catch (Exception e) {
						e.printStackTrace();
					}

					// 综合流速
					try {
						ctable.deleteTable("allutilhdx", allipstr, "allhdx");
						ctable.deleteTable("autilhdxh", allipstr, "ahdxh");
						ctable.deleteTable("autilhdxd", allipstr, "ahdxd");
					} catch (Exception e) {
						e.printStackTrace();
					}

					// 丢包率
					try {
						ctable.deleteTable("discardsperc", allipstr, "dcardperc");
						ctable.deleteTable("dcarperh", allipstr, "dcarperh");
						ctable.deleteTable("dcarperd", allipstr, "dcarperd");
					} catch (Exception e) {
						e.printStackTrace();
					}

					// 错误率
					try {
						ctable.deleteTable("errorsperc", allipstr, "errperc");
						ctable.deleteTable("errperch", allipstr, "errperch");
						ctable.deleteTable("errpercd", allipstr, "errpercd");
					} catch (Exception e) {
						e.printStackTrace();
					}

					// 数据包
					try {
						ctable.deleteTable("packs", allipstr, "packs");
						ctable.deleteTable("packshour", allipstr, "packshour");
						ctable.deleteTable("packsday", allipstr, "packsday");
					} catch (Exception e) {
						e.printStackTrace();
					}

					// 入口数据包
					try {
						ctable.deleteTable("inpacks", allipstr, "inpacks");
						ctable.deleteTable("ipacksh", allipstr, "ipacksh");
						ctable.deleteTable("ipackd", allipstr, "ipackd");
					} catch (Exception e) {
						e.printStackTrace();
					}

					// 出口数据包
					try {
						ctable.deleteTable("outpacks", allipstr, "outpacks");
						ctable.deleteTable("opackh", allipstr, "opackh");
						ctable.deleteTable("opacksd", allipstr, "opacksd");
					} catch (Exception e) {
						e.printStackTrace();
					}

					// 温度
					try {
						ctable.deleteTable("temper", allipstr, "temper");
						ctable.deleteTable("temperh", allipstr, "temperh");
						ctable.deleteTable("temperd", allipstr, "temperd");
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						conn.commit();
					} catch (Exception e) {
						e.printStackTrace();
					}

					DiscoverCompleteDao dcDao = new DiscoverCompleteDao();
					try {
						dcDao.deleteMonitor(host.getId(), host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						dcDao.close();
						// conn.close();
					}

					EventListDao eventdao = new EventListDao();
					try {
						// 同时删除事件表里的相关数据
						eventdao.delete(host.getId(), "network");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						eventdao.close();
					}

					PortconfigDao portconfigdao = new PortconfigDao();
					try {
						// 同时删除端口配置表里的相关数据
						portconfigdao.deleteByIpaddress(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						portconfigdao.close();
					}

					// 删除IP-MAC-BASE表里的对应的数据
					IpMacChangeDao macchangebasedao = new IpMacChangeDao();
					try {
						// delte后,conn已经关闭
						macchangebasedao.deleteByHostIp(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						macchangebasedao.close();
					}

					// 删除网络设备配置文件表里的对应的数据
					NetNodeCfgFileDao dao = new NetNodeCfgFileDao();
					try {
						// delte后,conn已经关闭
						dao.deleteByHostIp(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						dao.close();
					}

					// 删除网络设备SYSLOG接收表里的对应的数据
					NetSyslogDao syslogdao = new NetSyslogDao();
					try {
						// delte后,conn已经关闭
						syslogdao.deleteByHostIp(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						syslogdao.close();
					}

					// 删除网络设备端口扫描表里的对应的数据
					PortScanDao portscandao = new PortScanDao();
					try {
						// delte后,conn已经关闭
						portscandao.deleteByHostIp(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						portscandao.close();
					}

					// 删除网络设备面板图表里的对应的数据
					IpaddressPanelDao addresspaneldao = new IpaddressPanelDao();
					try {
						// delte后,conn已经关闭
						addresspaneldao.deleteByHostIp(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						addresspaneldao.close();
					}

					// 删除网络设备接口表里的对应的数据
					HostInterfaceDao interfacedao = new HostInterfaceDao();
					try {
						// delte后,conn已经关闭
						interfacedao.deleteByHostId(host.getId() + "");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						interfacedao.close();
					}

					// 删除网络设备IP别名表里的对应的数据
					IpAliasDao ipaliasdao = new IpAliasDao();
					try {
						// delte后,conn已经关闭
						ipaliasdao.deleteByHostIp(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						ipaliasdao.close();
					}

					// 删除网络设备手工配置的链路表里的对应的数据
					RepairLinkDao repairdao = new RepairLinkDao();
					try {
						// delte后,conn已经关闭
						repairdao.updatestartlinkip(host.getIpAddress(), ipaddress);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						repairdao.close();
					}
					// 删除网络设备手工配置的链路表里的对应的数据
					repairdao = new RepairLinkDao();
					try {
						// delte后,conn已经关闭
						repairdao.updateendlinkip(host.getIpAddress(), ipaddress);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						repairdao.close();
					}

					// 删除网络设备IPMAC表里的对应的数据
					IpMacDao ipmacdao = new IpMacDao();
					try {
						// delte后,conn已经关闭
						ipmacdao.deleteByHostIp(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						ipmacdao.close();
					}
					// 删除nms_ipmacchange表里的对应的数据
					IpMacBaseDao macbasedao = new IpMacBaseDao();
					try {
						// delte后,conn已经关闭
						macbasedao.deleteByHostIp(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						macbasedao.close();
					}
					host.setIpAddress(ipaddress);
					vo.setIpAddress(ipaddress);
					// 创建临时表
					// 生成网络设备表
					ip = ipaddress;
					// if (ip.indexOf(".")>0){
					// ip1=ip.substring(0,ip.indexOf("."));
					// ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());
					// tempStr =
					// ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
					// }
					// ip2=tempStr.substring(0,tempStr.indexOf("."));
					// ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
					// allipstr=ip1+ip2+ip3+ip4;
					allipstr = SysUtil.doip(ip);
					try {
						ctable.createTable("ping", allipstr, "ping");// Ping
						ctable.createTable("pinghour", allipstr, "pinghour");// Ping
						ctable.createTable("pingday", allipstr, "pingday");// Ping

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
						conn.commit();
					} catch (Exception e) {
						// e.printStackTrace();
					} finally {
						try {
							conn.executeBatch();
						} catch (Exception e) {

						}
						// conn.close();
					}

				} else if (host.getCategory() == 4) {
					// 删除主机服务器
					try {
						ctable.deleteTable("pro", allipstr, "pro");// 进程
					} catch (Exception e) {

					}
					try {
						ctable.deleteTable("prohour", allipstr, "prohour");// 进程小时
					} catch (Exception e) {

					}
					try {
						ctable.deleteTable("proday", allipstr, "proday");// 进程天
					} catch (Exception e) {

					}

					try {
						ctable.deleteTable("log", allipstr, "log");// 进程天
					} catch (Exception e) {

					}

					try {
						ctable.deleteTable("memory", allipstr, "mem");// 内存
					} catch (Exception e) {

					}
					try {
						ctable.deleteTable("memoryhour", allipstr, "memhour");// 内存
					} catch (Exception e) {

					}
					try {
						ctable.deleteTable("memoryday", allipstr, "memday");// 内存
					} catch (Exception e) {

					}

					try {
						ctable.deleteTable("cpu", allipstr, "cpu");// CPU
					} catch (Exception e) {

					}
					try {
						ctable.deleteTable("cpuhour", allipstr, "cpuhour");// CPU
					} catch (Exception e) {

					}
					try {
						ctable.deleteTable("cpuday", allipstr, "cpuday");// CPU
					} catch (Exception e) {

					}
					try {
						ctable.deleteTable("cpudtl", allipstr, "cpudtl");// CPU
					} catch (Exception e) {

					}
					try {
						ctable.deleteTable("cpudtlhour", allipstr, "cpudtlhour");// CPU
					} catch (Exception e) {

					}
					try {
						ctable.deleteTable("cpudtlday", allipstr, "cpudtlday");// CPU
					} catch (Exception e) {

					}

					try {
						ctable.deleteTable("disk", allipstr, "disk");// disk
					} catch (Exception e) {

					}
					try {
						ctable.deleteTable("diskhour", allipstr, "diskhour");// disk
					} catch (Exception e) {

					}
					try {
						ctable.deleteTable("diskday", allipstr, "diskday");// disk
					} catch (Exception e) {

					}

					try {
						ctable.deleteTable("diskincre", allipstr, "diskincre");// 磁盘增长
					} catch (Exception e) {

					}
					try {
						ctable.deleteTable("diskinch", allipstr, "diskinch");// 磁盘增长
					} catch (Exception e) {

					}
					try {
						ctable.deleteTable("diskincd", allipstr, "diskincd");// 磁盘增长
					} catch (Exception e) {

					}
					try {
						ctable.deleteTable("ping", allipstr, "ping");
					} catch (Exception e) {

					}
					try {
						ctable.deleteTable("pinghour", allipstr, "pinghour");
					} catch (Exception e) {

					}
					try {
						ctable.deleteTable("pingday", allipstr, "pingday");
					} catch (Exception e) {

					}

					try {
						ctable.deleteTable("utilhdxperc", allipstr, "hdperc");
					} catch (Exception e) {

					}
					try {
						ctable.deleteTable("hdxperchour", allipstr, "hdperchour");
					} catch (Exception e) {

					}
					try {
						ctable.deleteTable("hdxpercday", allipstr, "hdpercday");
					} catch (Exception e) {

					}

					try {
						ctable.deleteTable("utilhdx", allipstr, "hdx");
					} catch (Exception e) {

					}
					try {
						ctable.deleteTable("utilhdxhour", allipstr, "hdxhour");
					} catch (Exception e) {

					}
					try {
						ctable.deleteTable("utilhdxday", allipstr, "hdxday");
					} catch (Exception e) {

					}
					try {
						ctable.deleteTable("software", allipstr, "software");
					} catch (Exception e) {

					}
					ctable.deleteTable("allutilhdx", allipstr, "allhdx");
					ctable.deleteTable("autilhdxh", allipstr, "ahdxh");
					ctable.deleteTable("autilhdxd", allipstr, "ahdxd");

					ctable.deleteTable("discardsperc", allipstr, "dcardperc");
					ctable.deleteTable("dcarperh", allipstr, "dcarperh");
					ctable.deleteTable("dcarperd", allipstr, "dcarperd");

					ctable.deleteTable("errorsperc", allipstr, "errperc");
					ctable.deleteTable("errperch", allipstr, "errperch");
					ctable.deleteTable("errpercd", allipstr, "errpercd");

					ctable.deleteTable("packs", allipstr, "packs");
					ctable.deleteTable("packshour", allipstr, "packshour");
					ctable.deleteTable("packsday", allipstr, "packsday");

					ctable.deleteTable("inpacks", allipstr, "inpacks");
					ctable.deleteTable("ipacksh", allipstr, "ipacksh");
					ctable.deleteTable("ipackd", allipstr, "ipackd");

					ctable.deleteTable("outpacks", allipstr, "outpacks");
					ctable.deleteTable("opackh", allipstr, "opackh");
					ctable.deleteTable("opacksd", allipstr, "opacksd");
					if (host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.2.3.1.2.1.1")) {
						// 删除换页率
						try {
							ctable.deleteTable("pgused", allipstr, "pgused");
							ctable.deleteTable("pgusedhour", allipstr, "pgusedhour");
							ctable.deleteTable("pgusedday", allipstr, "pgusedday");
						} catch (Exception e) {

						}
					}

					try {
						conn.commit();
					} catch (Exception e) {
						e.printStackTrace();
					}

					DiscoverCompleteDao dcDao = new DiscoverCompleteDao();
					try {
						dcDao.deleteMonitor(host.getId(), host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						dcDao.close();
						// conn.close();
					}

					EventListDao eventdao = new EventListDao();
					try {
						// 同时删除事件表里的相关数据
						eventdao.delete(host.getId(), "network");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						eventdao.close();
					}

					PortconfigDao portconfigdao = new PortconfigDao();
					try {
						// 同时删除端口配置表里的相关数据
						portconfigdao.deleteByIpaddress(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						portconfigdao.close();
					}

					// 删除IP-MAC-BASE表里的对应的数据
					IpMacChangeDao macchangebasedao = new IpMacChangeDao();
					try {
						// delte后,conn已经关闭
						macchangebasedao.deleteByHostIp(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						macchangebasedao.close();
					}

					// 删除网络设备配置文件表里的对应的数据
					NetNodeCfgFileDao dao = new NetNodeCfgFileDao();
					try {
						// delte后,conn已经关闭
						dao.deleteByHostIp(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						dao.close();
					}

					// 删除网络设备SYSLOG接收表里的对应的数据
					NetSyslogDao syslogdao = new NetSyslogDao();
					try {
						// delte后,conn已经关闭
						syslogdao.deleteByHostIp(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						syslogdao.close();
					}

					// 删除网络设备端口扫描表里的对应的数据
					PortScanDao portscandao = new PortScanDao();
					try {
						// delte后,conn已经关闭
						portscandao.deleteByHostIp(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						portscandao.close();
					}

					// 删除网络设备面板图表里的对应的数据
					IpaddressPanelDao addresspaneldao = new IpaddressPanelDao();
					try {
						// delte后,conn已经关闭
						addresspaneldao.deleteByHostIp(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						addresspaneldao.close();
					}

					// 删除网络设备接口表里的对应的数据
					HostInterfaceDao interfacedao = new HostInterfaceDao();
					try {
						// delte后,conn已经关闭
						interfacedao.deleteByHostId(host.getId() + "");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						interfacedao.close();
					}

					// 删除网络设备IP别名表里的对应的数据
					IpAliasDao ipaliasdao = new IpAliasDao();
					try {
						// delte后,conn已经关闭
						ipaliasdao.deleteByHostIp(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						ipaliasdao.close();
					}

					// 删除网络设备手工配置的链路表里的对应的数据
					RepairLinkDao repairdao = new RepairLinkDao();
					try {
						// delte后,conn已经关闭
						repairdao.updatestartlinkip(host.getIpAddress(), ipaddress);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						repairdao.close();
					}
					// 删除网络设备手工配置的链路表里的对应的数据
					repairdao = new RepairLinkDao();
					try {
						// delte后,conn已经关闭
						repairdao.updateendlinkip(host.getIpAddress(), ipaddress);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						repairdao.close();
					}

					// 删除网络设备IPMAC表里的对应的数据
					IpMacDao ipmacdao = new IpMacDao();
					try {
						// delte后,conn已经关闭
						ipmacdao.deleteByHostIp(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						ipmacdao.close();
					}
					// 删除nms_ipmacchange表里的对应的数据
					IpMacBaseDao macbasedao = new IpMacBaseDao();
					try {
						// delte后,conn已经关闭
						macbasedao.deleteByHostIp(host.getIpAddress());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						macbasedao.close();
					}
					host.setIpAddress(ipaddress);
					vo.setIpAddress(ipaddress);
					// 创建临时表
					// 生成网络设备表
					ip = ipaddress;//
					allipstr = SysUtil.doip(ip);
					try {
						ctable.createTable("ping", allipstr, "ping");// Ping
						ctable.createTable("pinghour", allipstr, "pinghour");// Ping
						ctable.createTable("pingday", allipstr, "pingday");// Ping

						ctable.createTable("pro", allipstr, "pro");// 进程
						ctable.createTable("prohour", allipstr, "prohour");// 进程小时
						ctable.createTable("proday", allipstr, "proday");// 进程天

						ctable.createTable("memory", allipstr, "mem");// 内存
						ctable.createTable("memoryhour", allipstr, "memhour");// 内存
						ctable.createTable("memoryday", allipstr, "memday");// 内存

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

						ctable.createTable("cpudtl", allipstr, "cpudtl");
						ctable.createTable("cpudtlhour", allipstr, "cpudtlhour");
						ctable.createTable("cpudtlday", allipstr, "cpudtlday");

						ctable.createTable("disk", allipstr, "disk");// yangjun
						ctable.createTable("diskhour", allipstr, "diskhour");
						ctable.createTable("diskday", allipstr, "diskday");

						ctable.createTable("diskincre", allipstr, "diskincre");// 磁盘增长率yangjun
						ctable.createTable("diskinch", allipstr, "diskinch");// 磁盘增长率小时
						ctable.createTable("diskincd", allipstr, "diskincd");// 磁盘增长率天

						ctable.createSyslogTable("log", allipstr, "log");// 进程天
						ctable.createTable("software", allipstr, "software");

						conn.commit();
					} catch (Exception e) {
						// e.printStackTrace();
					} finally {
						try {
							conn.executeBatch();
						} catch (Exception e) {

						}
						// conn.close();
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				conn.close();
			}
		}
		String sysLogLevels = getParaValue("sysLogLevels");
		String[] fs = sysLogLevels.split(";");
		String faci_str = "";
		if (fs != null && fs.length > 0) {
			for (int i = 0; i < fs.length; i++) {

				String fa = fs[i];
				faci_str = faci_str + fa + ",";
			}
		}

		NetSyslogNodeRuleDao nodeRuleDao = new NetSyslogNodeRuleDao();
		NetSyslogNodeRule noderule = null;
		try {
			noderule = (NetSyslogNodeRule) nodeRuleDao.findByID(vo.getId() + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String strSql = "";
		if (noderule == null) {
			strSql = "insert into nms_netsyslogrule_node(id,nodeid,facility)values(0,'" + vo.getId() + "','" + faci_str + "')";
		} else {
			strSql = "update nms_netsyslogrule_node set facility='" + faci_str + "' where nodeid='" + vo.getId() + "'";
		}
		try {
			nodeRuleDao.saveOrUpdate(strSql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			nodeRuleDao.close();
		}

		TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
		try {
			timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("0"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
		try {
			timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("0"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		vo.setEndpoint(host.getEndpoint());

		boolean flag = true;
		HostNodeDao _hostNodeDao = new HostNodeDao();
		try {
			flag = _hostNodeDao.update(vo);
		} catch (Exception e) {

		} finally {
			_hostNodeDao.close();
		}

		_hostNodeDao = new HostNodeDao();
		Hashtable nodehash = ShareData.getNodehash();
		try {
			List hostlist = _hostNodeDao.loadIsMonitored(1);
			if (hostlist != null && hostlist.size() > 0) {
				for (int i = 0; i < hostlist.size(); i++) {
					HostNode node = (HostNode) hostlist.get(i);
					if (nodehash.containsKey(node.getCategory() + "")) {
						List list = (List) nodehash.get(node.getCategory() + "");
						for (int k = 0; k < list.size(); k++) {
							HostNode exitnode = (HostNode) list.get(k);
							if (exitnode.getId() == node.getId()) {
								list.remove(k);
								list.add(k, node);
								break;
							}
						}
						nodehash.put(node.getCategory() + "", list);
					} else {
						List nodelist = new ArrayList();
						nodelist.add(node);
						nodehash.put(node.getCategory() + "", nodelist);
					}
				}
			}
		} catch (Exception e) {

		} finally {
			_hostNodeDao.close();
		}
		ShareData.setNodehash(nodehash);
		if (flag) {
			out.print("修改成功");
		} else {
			out.print("修改失败");
		}
		out.flush();
	}

	private void beforeEdit() {
		String nodeId = getParaValue("id");

		NetSyslogNodeRuleDao noderuledao = new NetSyslogNodeRuleDao();
		NetSyslogNodeRule noderule = null;
		try {
			noderule = (NetSyslogNodeRule) noderuledao.findByID(nodeId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			noderuledao.close();
		}
		String nodefacility = "";
		if (noderule != null) {
			nodefacility = noderule.getFacility();
			List nodeflist = new ArrayList();
			if (nodefacility != null && nodefacility.trim().length() > 0) {
				String[] nodefacilitys = nodefacility.split(",");

				if (nodefacilitys != null && nodefacilitys.length > 0) {
					for (int i = 0; i < nodefacilitys.length; i++) {
						nodeflist.add(nodefacilitys[i]);
					}
				}
			}
		}

		SupperDao supperdao = new SupperDao();
		List allSupper = new ArrayList();
		String bid = "";
		List bidlist = new ArrayList();
		List allbuss = new ArrayList();

		HostNodeDao dao = new HostNodeDao();
		BusinessDao bussdao = new BusinessDao();

		HostNode vo = null;
		try {
			vo = (HostNode) dao.findByID(getParaValue("id"));
			allSupper = supperdao.loadAll();
			allbuss = bussdao.loadAll();
			bid = vo.getBid();
			String id[] = bid.split(",");
			bidlist = new ArrayList();
			if (id != null && id.length > 0) {
				for (int i = 0; i < id.length; i++) {
					bidlist.add(id[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
			if (supperdao != null) {
				supperdao.close();
			}
			if (bussdao != null) {
				bussdao.close();
			}
		}

		String bussName = "";
		if (allbuss.size() > 0) {
			for (int i = 0; i < allbuss.size(); i++) {
				Business buss = (Business) allbuss.get(i);
				if (bidlist.contains(buss.getId() + "")) {
					bussName = bussName + ',' + buss.getName();
				}
			}
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (vo != null) {
			jsonString.append("{\"id\":\"");
			jsonString.append(vo.getId());
			jsonString.append("\",");

			jsonString.append("\"ip_address\":\"");
			jsonString.append(vo.getIpAddress());
			jsonString.append("\",");

			jsonString.append("\"alias\":\"");
			jsonString.append(vo.getAlias());
			jsonString.append("\",");

			jsonString.append("\"collecttype\":\"");
			jsonString.append(vo.getCollecttype());
			jsonString.append("\",");

			jsonString.append("\"snmpversion\":\"");
			jsonString.append(vo.getSnmpversion());
			jsonString.append("\",");

			jsonString.append("\"communityRO\":\"");
			jsonString.append(vo.getCommunity());
			jsonString.append("\",");

			jsonString.append("\"communityRW\":\"");
			jsonString.append(vo.getWriteCommunity());
			jsonString.append("\",");

			jsonString.append("\"type\":\"");
			jsonString.append(vo.getCategory());
			jsonString.append("\",");

			jsonString.append("\"subtype\":\"");
			jsonString.append(vo.getOstype());
			jsonString.append("\",");

			jsonString.append("\"bid\":\"");
			jsonString.append(vo.getBid());
			jsonString.append("\",");

			jsonString.append("\"bidvalue\":\"");
			jsonString.append(bussName);
			jsonString.append("\",");
			String managed = "";
			if (vo.isManaged() == true) {
				managed = "1";
			} else {
				managed = "0";
			}
			jsonString.append("\"managed\":\"");
			jsonString.append(managed);
			jsonString.append("\",");

			jsonString.append("\"syslog\":\"");
			jsonString.append(nodefacility.replace(",", ";"));
			jsonString.append("\"}");

		}
		jsonString.append("],total : 1 }");
		out.print(jsonString.toString());
		out.flush();
	}

	private void deleteBusinessNode() {
		String id = getParaValue("id");
		BusinessDao dao = new BusinessDao();
		boolean flag = true;
		try {
			flag = dao.deleteVoAndChildVoById(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (flag) {
			out.print("删除成功");
		} else {
			out.print("删除失败");
		}
		out.flush();
	}

	private Hashtable getChildBid(Business bs, List bsList) {
		Hashtable obHt = new Hashtable();
		if (null != bsList && bsList.size() > 0) {
			Business vo = null;
			for (int i = 0; i < bsList.size(); i++) {
				vo = (Business) bsList.get(i);
				if (vo.getPid().equals(bs.getId())) {
					obHt.put(vo, getChildBid(vo, bsList));
					bsList.remove(i);
					break;
				}
			}
		}
		return obHt;
	}

	private void getBids() {
		BusinessDao businessDao = new BusinessDao();
		List allBusiness = null;
		try {
			allBusiness = businessDao.loadAll();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			if (businessDao != null) {
				businessDao.close();
			}
		}
		StringBuffer jsonString = new StringBuffer("[");
		List sameLevelList = new ArrayList();
		Hashtable tempHt = null;
		if (null != allBusiness) {
			Business vo = null;
			for (int i = 0; i < allBusiness.size();) {
				vo = (Business) allBusiness.get(i);
				tempHt = getChildBid(vo, allBusiness);
				if (null != tempHt && tempHt.size() > 0) {
					sameLevelList.add(tempHt);
				}
				if (allBusiness.size() == 1) {
					jsonString.append("{id:\"");
					jsonString.append(vo.getId());
					jsonString.append("\",");

					jsonString.append("text:\"");
					jsonString.append(vo.getName());
					jsonString.append("\",");

					jsonString.append("pid:\"");
					jsonString.append(vo.getPid());
					jsonString.append("\",");

					jsonString.append("descr:\"");
					jsonString.append(vo.getDescr());
					jsonString.append("\",");

					jsonString.append("isexpand:");
					jsonString.append(true);
					if (null != sameLevelList && sameLevelList.size() > 0) {
						jsonString.append(",children:[");
						for (int j = 0; j < sameLevelList.size(); j++) {
							tempHt = (Hashtable) sameLevelList.get(j);
							if (null != tempHt && tempHt.size() > 0) {
								jsonString.append(handleBidHashtable(tempHt));
								if (j != sameLevelList.size() - 1) {
									jsonString.append(",");
								}
							}
						}
						jsonString.append("]");
					}

					jsonString.append("}]");
					break;
				} else {
					i = 0;
				}
			}
		}
		out.print(jsonString.toString());
		out.flush();
	}

	private void addBusiness() {
		Business vo = new Business();
		String name = getParaValue("name");
		String descr = getParaValue("descr");
		String pid = getParaValue("pid");
		vo.setName(name);
		vo.setDescr(descr);
		vo.setPid(pid);
		BusinessDao dao = new BusinessDao();
		boolean flag = true;
		try {
			flag = dao.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
		}
		if (flag) {
			out.print("添加成功");
		} else {
			out.print("添加失败");
		}
		out.flush();
	}

	private void updateBusiness() {
		Business vo = new Business();
		String id = getParaValue("bid");
		String name = getParaValue("name");
		String descr = getParaValue("descr");
		String pid = getParaValue("pid");
		vo.setId(id);
		vo.setName(name);
		vo.setDescr(descr);
		vo.setPid(pid);
		BusinessDao dao = new BusinessDao();
		boolean flag = true;
		try {
			flag = dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
		}
		if (flag) {
			out.print("修改成功");
		} else {
			out.print("修改失败");
		}
		out.flush();
	}

	private String handleBidHashtable(Hashtable ht) {
		StringBuffer sb = new StringBuffer();
		Enumeration e = ht.keys();
		Business vo = null;
		Hashtable tempHt = null;
		while (e.hasMoreElements()) {
			vo = (Business) e.nextElement();
			sb.append("{id:\"");
			sb.append(vo.getId());
			sb.append("\",");

			sb.append("text:\"");
			sb.append(vo.getName());
			sb.append("\",");

			sb.append("descr:\"");
			sb.append(vo.getDescr());
			sb.append("\",");

			sb.append("pid:\"");
			sb.append(vo.getPid());
			sb.append("\",");

			sb.append("isexpand:");
			sb.append(true);

			if (null != ht.get(vo)) {
				tempHt = (Hashtable) ht.get(vo);
				if (null != tempHt && tempHt.size() > 0) {
					sb.append(",children:[");
					sb.append(handleBidHashtable(tempHt));
					sb.append("]");
				}
			}
		}
		sb.append("}");
		return sb.toString();
	}

	private void addNode() {
		String assetid = getParaValue("assetid");// 设备资产编号
		String sysLogLevels = getParaValue("sysLogLevels");// 设备资产编号
		String location = getParaValue("location");// 机房位置
		String ipAddress = getParaValue("ip_address");
		String alias = getParaValue("alias");
		int snmpversion = getParaIntValue("snmpversion");

		String community = getParaValue("community");
		String writecommunity = getParaValue("writecommunity");
		String vmwareusername = getParaValue("uname");
		String vmwarepassword = getParaValue("pw");
		int type = getParaIntValue("type");
		int transfer = getParaIntValue("transfer");
		String subtype = getParaValue("subtype");
		if (type == 14 && subtype != null) {
			location = location + "," + subtype;
		}
		int ostype = 0;
		try {
			ostype = getParaIntValue("ostype");
		} catch (Exception e) {
			e.printStackTrace();
		}
		int collecttype = 0;
		try {
			collecttype = getParaIntValue("collecttype");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String bid = getParaValue("bid");

		String sendmobiles = getParaValue("sendmobiles");
		String sendemail = getParaValue("sendemail");
		String sendphone = getParaValue("sendphone");
		int supperid = 0;

		if (sendmobiles == null) {
			sendmobiles = "";
		}
		if (sendemail == null) {
			sendemail = "";
		}
		if (sendphone == null) {
			sendphone = "";
		}

		// SNMP V3
		int securityLevel = getParaIntValue("securityLevel");
		String securityName = getParaValue("securityName");
		int v3_ap = getParaIntValue("v3_ap");
		String authPassPhrase = getParaValue("authPassPhrase");
		int v3_privacy = getParaIntValue("v3_privacy");
		String privacyPassPhrase = getParaValue("privacyPassPhrase");
		boolean managed = true;
		if (securityName == null) {
			securityName = "";
		}
		if (authPassPhrase == null || authPassPhrase.trim().length() == 0) {
			authPassPhrase = securityName;
		}
		if (privacyPassPhrase == null || privacyPassPhrase.trim().length() == 0) {
			privacyPassPhrase = securityName;
		}

		TopoHelper helper = new TopoHelper(); // 包括更新数据库和更新内存
		int addResult = 0;
		if (supperid >= 0) {
			if (collecttype == 3) {
				if (PollingEngine.getAddiplist() != null && PollingEngine.getAddiplist().size() > 0) {
					List list = PollingEngine.getAddiplist();
					for (int i = 0; i < list.size(); i++) {
						Hashtable hst = (Hashtable) list.get(i);
						NetworkUtil test = new NetworkUtil();
						List listip = test.parseAllIp((String) hst.get("startip"), (String) hst.get("endip"));
						for (int j = 0; j < listip.size(); j++) {
							String iplist = (String) listip.get(j);
							addResult = helper.addHost(assetid, location, iplist, iplist, snmpversion, community, writecommunity, transfer, type, ostype, collecttype, bid, sendmobiles, sendemail, sendphone, supperid, managed, securityLevel, securityName, v3_ap, authPassPhrase, v3_privacy, privacyPassPhrase);
							Host node = (Host) PollingEngine.getInstance().getNodeByIP(iplist);
							try {
								if (node.getEndpoint() == 2) {
								} else {
									if (node.getCategory() == 4) {
										// 初始化服务器采集指标和阀值
										try {
											AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
											alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_HOST, getSutType(node.getSysOid()));

											// 判断采集方式
											if (node.getCollecttype() == 1) {// snmp方式采集
												NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
												nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_HOST, getSutType(node.getSysOid()), "1");
											} else if (node.getCollecttype() > 1) {// 其他方式采集
												NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
												nodeGatherIndicatorsUtil.addGatherIndicatorsOtherForNode(node.getId() + "", AlarmConstant.TYPE_HOST, getSutType(node.getSysOid()), "0", node.getCollecttype());
											}
										} catch (RuntimeException e) {
											e.printStackTrace();
										}
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					PollingEngine.setAddiplist(new ArrayList());
					addResult = helper.addHost(assetid, location, ipAddress, alias, snmpversion, community, writecommunity, transfer, type, ostype, collecttype, bid, sendmobiles, sendemail, sendphone, supperid, managed, securityLevel, securityName, v3_ap, authPassPhrase, v3_privacy, privacyPassPhrase);
				} else {
					addResult = helper.addHost(assetid, location, ipAddress, alias, snmpversion, community, writecommunity, transfer, type, ostype, collecttype, bid, sendmobiles, sendemail, sendphone, supperid, managed, securityLevel, securityName, v3_ap, authPassPhrase, v3_privacy, privacyPassPhrase);
				}
			} else {
				addResult = helper.addHost(assetid, location, ipAddress, alias, snmpversion, community, writecommunity, transfer, type, ostype, collecttype, bid, sendmobiles, sendemail, sendphone, supperid, managed, securityLevel, securityName, v3_ap, authPassPhrase, v3_privacy, privacyPassPhrase);
			}
		}
		if (addResult > 0) {
			String[] fs = sysLogLevels.split(";");
			String faci_str = "";
			if (fs != null && fs.length > 0) {
				for (int i = 0; i < fs.length; i++) {
					String fa = fs[i];
					faci_str = faci_str + fa + ",";
				}
			}

			NetSyslogNodeRuleDao netruledao = new NetSyslogNodeRuleDao();
			NetSyslogRuleDao ruledao = new NetSyslogRuleDao();
			try {
				String strNodeId = "";
				try {
					strNodeId = netruledao.findIdByIpaddress(ipAddress);
				} catch (Exception e) {
					e.printStackTrace();
				}
				String strFacility = "";
				List rulelist = new ArrayList();
				try {
					rulelist = ruledao.loadAll();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					ruledao.close();
				}
				if (rulelist != null && rulelist.size() > 0 && "".equals(faci_str)) {
					NetSyslogRule logrule = (NetSyslogRule) rulelist.get(0);
					strFacility = logrule.getFacility();
				} else {
					strFacility = faci_str;
				}
				String strSql = "";
				strSql = "insert into nms_netsyslogrule_node(nodeid,facility)values('" + strNodeId + "','" + strFacility + "')";
				try {
					netruledao.saveOrUpdate(strSql);
				} catch (Exception e) {
					e.printStackTrace();
				}
				TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
				try {
					timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(addResult), timeShareConfigUtil.getObjectType("0"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
				try {
					timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(addResult), timeGratherConfigUtil.getObjectType("0"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				netruledao.close();
				ruledao.close();
			}
		}

		Host node = (Host) PollingEngine.getInstance().getNodeByID(addResult);
		// 采集设备信息
		try {
			if (node.getEndpoint() == 2) {
			} else {
				if (node.getCategory() == 4) {
					// 初始化服务器采集指标和阀值
					try {
						AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
						if (node.getCollecttype() == SystemConstant.COLLECTTYPE_PING) {
							alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_HOST, getSutType(node.getSysOid()), "ping");
						} else {
							alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_HOST, getSutType(node.getSysOid()));
						}
						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
						nodeGatherIndicatorsUtil.addGatherIndicatorsOtherForNode(node.getId() + "", AlarmConstant.TYPE_HOST, getSutType(node.getSysOid()), "1", node.getCollecttype());

					} catch (RuntimeException e) {
						e.printStackTrace();
					}

				} else if (node.getCategory() < 4 || node.getCategory() == 7) {
					// 初始化服务器采集指标和阀值
					try {
						AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
						if (node.getCollecttype() == SystemConstant.COLLECTTYPE_PING) {
							alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_NET, getSutType(node.getSysOid()), "ping");
						} else {
							alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_NET, getSutType(node.getSysOid()));
						}

						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
						nodeGatherIndicatorsUtil.addGatherIndicatorsOtherForNode(node.getId() + "", AlarmConstant.TYPE_NET, getSutType(node.getSysOid()), "1", node.getCollecttype());

						PortConfigCenter.getInstance().setPortHastable();
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				} else if (node.getCategory() == 8) {
					// 初始化防火墙采集指标和阀值
					try {
						AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
						if (node.getCollecttype() == SystemConstant.COLLECTTYPE_PING) {
							alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_FIREWALL, getSutType(node.getSysOid()), "ping");
						} else {
							alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_FIREWALL, getSutType(node.getSysOid()));
						}

						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
						nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_FIREWALL, getSutType(node.getSysOid()), "1");
						PortConfigCenter.getInstance().setPortHastable();
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				} else if (node.getCategory() == 9) {
					// 初始化ATM设备采集指标
					// ATM设备
					if (node.getCollecttype() == SystemConstant.COLLECTTYPE_PING) {
						// 只用PING检测连通性
						try {
							AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
							alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_NET, "atm", "ping");
						} catch (RuntimeException e) {
							e.printStackTrace();
						}
						try {
							NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
							nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_NET, "atm", "1", "ping");
						} catch (RuntimeException e) {
							e.printStackTrace();
						}
					}
					// }
				} else if (node.getCategory() == 13) {
					// 初始化CMTS设备采集指标
					// HDS设备
					if (node.getCollecttype() == SystemConstant.COLLECTTYPE_PING) {
						// 只用PING检测连通性
						try {
							AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
							alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_CMTS, getSutType(node.getSysOid()), "ping");
						} catch (RuntimeException e) {
							e.printStackTrace();
						}
						try {
							NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
							nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_CMTS, getSutType(node.getSysOid()), "1", "ping");
						} catch (RuntimeException e) {
							e.printStackTrace();
						}
					} else {
						// 正常采集状态
						try {
							AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
							alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_CMTS, getSutType(node.getSysOid()));
							NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
							nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_CMTS, getSutType(node.getSysOid()), "1");
							PortConfigCenter.getInstance().setPortHastable();
						} catch (RuntimeException e) {
							e.printStackTrace();
						}
					}
					// }
				} else if (node.getCategory() == 14) {
					// 初始化存储设备采集指标
					// 正常采集状态
					try {
						AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
						if (node.getCollecttype() == SystemConstant.COLLECTTYPE_PING) {
							alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_STORAGE, getSutType(node.getSysOid()), "ping");
						} else {
							alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_STORAGE, getSutType(node.getSysOid()));
						}

						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
						nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_STORAGE, getSutType(node.getSysOid()), "1");
						PortConfigCenter.getInstance().setPortHastable();

						Connect vo_c = new Connect();
						ConnectDao cdao = new ConnectDao();
						vo_c.setNodeid(Long.parseLong(node.getId() + ""));
						vo_c.setUsername(vmwareusername);
						vo_c.setPwd(EncryptUtil.encode(vmwarepassword));
						vo_c.setType("storage");
						vo_c.setSubtype(subtype);
						vo_c.setIpaddress(ipAddress);
						cdao.save(vo_c);

					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				} else if (node.getCategory() == 15) {
					// 初始化VMWare设备采集指标
					// vmware设备
					if (node.getCollecttype() == SystemConstant.COLLECTTYPE_PING) {
						// 只用PING检测连通性
						try {
							AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
							alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_VIRTUAL, getSutType(node.getSysOid()), "ping");
						} catch (RuntimeException e) {
							e.printStackTrace();
						}
						try {
							NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
							nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_VIRTUAL, getSutType(node.getSysOid()), "1", "ping");
						} catch (RuntimeException e) {
							e.printStackTrace();
						}

					} else {
						// 正常采集状态
						try {
							NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
							nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_VIRTUAL, getSutType(node.getSysOid()), "1");
							PortConfigCenter.getInstance().setPortHastable();
							VMWareConnectConfigDao vmwaredao = new VMWareConnectConfigDao();
							try {
								VMWareConnectConfig vmware = new VMWareConnectConfig();
								vmware.setNodeid(Long.parseLong(node.getId() + ""));
								vmware.setUsername(vmwareusername);
								vmware.setPwd(EncryptUtil.encode(vmwarepassword));
								vmware.setHosturl("");
								vmware.setBak("");
								vmwaredao.save(vmware);

								HashMap vmids = new HashMap();
								try {
									if (vmids != null && vmids.size() > 0) {
										Iterator iterator = vmids.keySet().iterator();
										List vmwarevids = new ArrayList();
										while (iterator.hasNext()) {
											String vmid = (String) iterator.next();
											String guestname = (String) vmids.get(vmid);
											VMWareVid vmwarevid = new VMWareVid();
											vmwarevid.setNodeid(Long.parseLong(node.getId() + ""));
											vmwarevid.setVid(vmid);
											vmwarevid.setGuestname(guestname);
											vmwarevid.setBak("");
											vmwarevids.add(vmwarevid);
										}
										VMWareVidDao vmwareviddao = new VMWareVidDao();
										try {
											vmwareviddao.save(vmwarevids);
										} catch (Exception e) {
										} finally {
											vmwareviddao.close();
										}
									}

									try {
										String url = "https://" + node.getIpAddress() + "/sdk";
										HashMap<String, Object> summaryresultMap = (HashMap<String, Object>) VIMMgr.syncVIMObjs(url, vmwareusername, vmwarepassword);
										ArrayList<HashMap<String, Object>> dslist = (ArrayList<HashMap<String, Object>>) summaryresultMap.get("Datastore");
										ArrayList<HashMap<String, Object>> wulist = (ArrayList<HashMap<String, Object>>) summaryresultMap.get("HostSystem");
										ArrayList<HashMap<String, Object>> rplist = (ArrayList<HashMap<String, Object>>) summaryresultMap.get("ResourcePool");
										ArrayList<HashMap<String, Object>> vmlist = (ArrayList<HashMap<String, Object>>) summaryresultMap.get("VirtualMachine");
										ArrayList<HashMap<String, Object>> dclist = (ArrayList<HashMap<String, Object>>) summaryresultMap.get("Datacenter");
										ArrayList<HashMap<String, Object>> crlist = (ArrayList<HashMap<String, Object>>) summaryresultMap.get("ComputeResource");// 群集
										PerformancePanelAjaxManager per = new PerformancePanelAjaxManager();
										per.savePhysical(wulist, node.getId() + "", node.getIpAddress());
										per.saveVmware(vmlist, node.getId() + "", node.getIpAddress());
										per.saveDatastore(dslist, node.getId() + "", node.getIpAddress());
										per.saveResourcepool(rplist, node.getId() + "", node.getIpAddress());
										per.saveDatacenter(dclist, node.getId() + "", node.getIpAddress());
										per.saveYun(crlist, node.getId() + "", node.getIpAddress());
										// 初始化告警
										if (wulist != null && wulist.size() > 0) {
											String vid = "";
											for (int i = 0; i < wulist.size(); i++) {
												vid = wulist.get(i).get("vid").toString();
												AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
												alarmIndicatorsUtil.VMsaveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_VIRTUAL, getSutType(node.getSysOid()), "physical", vid);
											}
										}
										if (vmlist != null && vmlist.size() > 0) {
											String vid = "";
											for (int i = 0; i < vmlist.size(); i++) {
												vid = vmlist.get(i).get("vid").toString();
												AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
												alarmIndicatorsUtil.VMsaveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_VIRTUAL, getSutType(node.getSysOid()), "vmware", vid);
											}
										}
										if (dslist != null && dslist.size() > 0) {
											String vid = "";
											for (int i = 0; i < dslist.size(); i++) {
												vid = dslist.get(i).get("vid").toString();
												AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
												alarmIndicatorsUtil.VMsaveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_VIRTUAL, getSutType(node.getSysOid()), "datastore", vid);
											}
										}
										if (rplist != null && rplist.size() > 0) {
											String vid = "";
											for (int i = 0; i < rplist.size(); i++) {
												vid = rplist.get(i).get("vid").toString();
												AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
												alarmIndicatorsUtil.VMsaveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_VIRTUAL, getSutType(node.getSysOid()), "resourcepool", vid);
											}
										}
										if (crlist != null && crlist.size() > 0) {
											String vid = "";
											for (int i = 0; i < crlist.size(); i++) {
												vid = crlist.get(i).get("vid").toString();
												AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
												alarmIndicatorsUtil.VMsaveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_VIRTUAL, getSutType(node.getSysOid()), "yun", vid);
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								vmwaredao.close();
							}
						} catch (RuntimeException e) {
							e.printStackTrace();
						}
					}
				} else if (node.getCategory() == 16) {
					// 正常采集状态
					try {
						AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
						alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_AIRCONDITION, getSutType(node.getSysOid()));
						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
						nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_AIRCONDITION, getSutType(node.getSysOid()), "1");
						PortConfigCenter.getInstance().setPortHastable();
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				} else if (node.getCategory() == 17) {
					// 正常采集状态
					try {
						AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
						alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_UPS, getSutType(node.getSysOid()));
						NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
						nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_UPS, getSutType(node.getSysOid()), "1");
						PortConfigCenter.getInstance().setPortHastable();
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		HostNodeDao hostNodeDao = new HostNodeDao();
		Hashtable nodehash = new Hashtable();
		try {
			List hostlist = hostNodeDao.loadIsMonitored(1);
			if (hostlist != null && hostlist.size() > 0) {
				for (int i = 0; i < hostlist.size(); i++) {
					HostNode _node = (HostNode) hostlist.get(i);
					if (nodehash.containsKey(_node.getCategory() + "")) {
						((List) nodehash.get(_node.getCategory() + "")).add(_node);
					} else {
						List nodelist = new ArrayList();
						nodelist.add(_node);
						nodehash.put(_node.getCategory() + "", nodelist);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			hostNodeDao.close();
		}
		ShareData.setNodehash(nodehash);
		if (addResult > 0) {
			out.print("添加成功");
			out.flush();
		} else {
			out.print("添加失败");
			out.flush();
		}
	}

	private void deleteNodes() {
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		CreateTableManager createTableManager = new CreateTableManager();
		if (ids != null && ids.length > 0) {
			String id = (String) null;
			String ipAddress = (String) null;
			String ipString = (String) null;

			HostNodeDao dao = null;
			HostNode host = null;

			LinkDao linkDao = null;
			Link link = null;
			List linkList = new ArrayList();
			for (int i = 0; i < ids.length; i++) {
				id = ids[i];
				// 取消采集任务
				PollingEngine.getInstance().deleteNodeByID(Integer.parseInt(id));

				dao = new HostNodeDao();
				try {
					host = (HostNode) dao.findByID(id);
					dao.delete(id);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dao.close();
				}
				ipAddress = host.getIpAddress();
				// 删除内存数据
				ShareData.getSharedata().remove(ipAddress);

				linkDao = new LinkDao();
				linkList = new ArrayList();
				try {
					linkList = linkDao.findByNodeId(id);
					if (linkList != null && linkList.size() > 0) {
						link = (Link) linkList.get(0);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					linkDao.close();
				}

				// 刷新内存中采集指标
				NodeGatherIndicatorsUtil gatherutil = new NodeGatherIndicatorsUtil();
				gatherutil.refreshShareDataGather();

				ipString = SysUtil.doip(ipAddress);
				CreateTableManager ctable = new CreateTableManager();
				try {
					if (host.getCategory() < 4 || host.getCategory() == 7 //
							|| host.getCategory() == 8 //
							|| host.getCategory() == 9 //
							|| host.getCategory() == 10 //
							|| host.getCategory() == 11 //
							|| host.getCategory() == 12 //
							|| host.getCategory() == 13 //
							|| host.getCategory() == 14 //
							|| host.getCategory() == 15 //
							|| host.getCategory() == 16 //
							|| host.getCategory() == 17) {//
						try {
							// 连通率
							ctable.deleteTable("ping", ipString, "ping");
							ctable.deleteTable("pinghour", ipString, "pinghour");
							ctable.deleteTable("pingday", ipString, "pingday");

							// 内存
							ctable.deleteTable("memory", ipString, "mem");
							ctable.deleteTable("memoryhour", ipString, "memhour");
							ctable.deleteTable("memoryday", ipString, "memday");

							// 端口状态
							ctable.deleteTable("portstatus", ipString, "port");
							
							ctable.deleteTable("log", ipString, "log");

							// CPU
							ctable.deleteTable("cpu", ipString, "cpu");
							ctable.deleteTable("cpuhour", ipString, "cpuhour");
							ctable.deleteTable("cpuday", ipString, "cpuday");

							// 带宽利用率
							ctable.deleteTable("utilhdxperc", ipString, "hdperc");
							ctable.deleteTable("hdxperchour", ipString, "hdperchour");
							ctable.deleteTable("hdxpercday", ipString, "hdpercday");

							// 流速
							ctable.deleteTable("utilhdx", ipString, "hdx");
							ctable.deleteTable("utilhdxhour", ipString, "hdxhour");
							ctable.deleteTable("utilhdxday", ipString, "hdxday");

							// 综合流速
							ctable.deleteTable("allutilhdx", ipString, "allhdx");
							ctable.deleteTable("autilhdxh", ipString, "ahdxh");
							ctable.deleteTable("autilhdxd", ipString, "ahdxd");

							// 丢包率
							ctable.deleteTable("discardsperc", ipString, "dcardperc");
							ctable.deleteTable("dcarperh", ipString, "dcarperh");
							ctable.deleteTable("dcarperd", ipString, "dcarperd");

							// 错误率
							ctable.deleteTable("errorsperc", ipString, "errperc");
							ctable.deleteTable("errperch", ipString, "errperch");
							ctable.deleteTable("errpercd", ipString, "errpercd");

							// 数据包
							ctable.deleteTable("packs", ipString, "packs");
							ctable.deleteTable("packshour", ipString, "packshour");
							ctable.deleteTable("packsday", ipString, "packsday");

							// 入口数据包
							ctable.deleteTable("inpacks", ipString, "inpacks");
							ctable.deleteTable("ipacksh", ipString, "ipacksh");
							ctable.deleteTable("ipackd", ipString, "ipackd");

							// 出口数据包
							ctable.deleteTable("outpacks", ipString, "outpacks");
							ctable.deleteTable("opackh", ipString, "opackh");
							ctable.deleteTable("opacksd", ipString, "opacksd");

							// 温度
							ctable.deleteTable("temper", ipString, "temper");
							ctable.deleteTable("temperh", ipString, "temperh");
							ctable.deleteTable("temperd", ipString, "temperd");
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (host.getCategory() != 12) {
							// VPN类型时候没有以下表信息
							ctable.deleteTable("flash", ipString, "flash");// 闪存
							ctable.deleteTable("flashhour", ipString, "flashhour");// 闪存
							ctable.deleteTable("flashday", ipString, "flashday");// 闪存

							ctable.deleteTable("buffer", ipString, "buffer");// 缓存
							ctable.deleteTable("bufferhour", ipString, "bufferhour");// 缓存
							ctable.deleteTable("bufferday", ipString, "bufferday");// 缓存

							ctable.deleteTable("fan", ipString, "fan");// 风扇
							ctable.deleteTable("fanhour", ipString, "fanhour");// 风扇
							ctable.deleteTable("fanday", ipString, "fanday");// 风扇

							ctable.deleteTable("power", ipString, "power");// 电源
							ctable.deleteTable("powerhour", ipString, "powerhour");// 电源
							ctable.deleteTable("powerday", ipString, "powerday");// 电源

							ctable.deleteTable("vol", ipString, "vol");// 电压
							ctable.deleteTable("volhour", ipString, "volhour");// 电压
							ctable.deleteTable("volday", ipString, "volday");// 电压
						} else if (host.getCategory() == 13) {
							// CMTS设备
							ctable.deleteTable("status", ipString, "status");// 通道状态
							ctable.deleteTable("statushour", ipString, "statushour");// 通道状态
							ctable.deleteTable("statusday", ipString, "statusday");// 通道状态

							ctable.deleteTable("noise", ipString, "noise");// 通道信躁比
							ctable.deleteTable("noisehour", ipString, "noisehour");// 通道信躁比
							ctable.deleteTable("noiseday", ipString, "noiseday");// 通道信躁比

							ctable.deleteTable("ipmac", ipString, "ipmac");// IPMAC信息（在线用户信息）
						} else if (host.getCategory() == 14) {
							ctable.deleteTable("env", ipString, "env");//
							ctable.deleteTable("efan", ipString, "efan");//
							ctable.deleteTable("epower", ipString, "epower");//
							ctable.deleteTable("eenv", ipString, "eenv");//
							ctable.deleteTable("edrive", ipString, "edrive");//

							ctable.deleteTable("rcpu", ipString, "rcpu");
							ctable.deleteTable("rcable", ipString, "rcable");// 运行状体：内部总线状态
							ctable.deleteTable("rcache", ipString, "rcache");// 运行状体：缓存状态
							ctable.deleteTable("rmemory", ipString, "rmemory");// 运行状体：共享内存状态
							ctable.deleteTable("rpower", ipString, "rpower");// 运行状体：电源状态
							ctable.deleteTable("rbutter", ipString, "rbutter");// 运行状体：电池状态
							ctable.deleteTable("rfan", ipString, "rfan");// 运行状体：风扇状态
							ctable.deleteTable("renv", ipString, "renv");// 存储设备环境-环境状态

							ctable.deleteTable("rluncon", ipString, "rluncon");
							ctable.deleteTable("rsluncon", ipString, "rsluncon");
							ctable.deleteTable("rwwncon", ipString, "rwwncon");
							ctable.deleteTable("rsafety", ipString, "rsafety");
							ctable.deleteTable("rnumber", ipString, "rnumber");
							ctable.deleteTable("rswitch", ipString, "rswitch");

							ctable.deleteTable("events", ipString, "events");// 事件

							ctable.deleteTable("emcdiskper", ipString, "emcdiskper");
							ctable.deleteTable("emclunper", ipString, "emclunper");
							ctable.deleteTable("emcenvpower", ipString, "emcenvpower");
							ctable.deleteTable("emcenvstore", ipString, "emcenvstore");
							ctable.deleteTable("emcbakpower", ipString, "emcbakpower");

						} else if (host.getCategory() == 15) {
							ctable.deleteTable("state", ipString, "state");// 虚拟机电源状况（打开或关闭）。
							ctable.deleteTable("gstate", ipString, "gstate");// 客户机操作系统的状况（开或关）。

							ctable.deleteTable("vm_host", ipString, "vm_host");
							// 物理机的性能信息表
							ctable.deleteTable("vm_guesthost", ipString, "vm_guesthost");
							// 虚拟机的性能信息表
							ctable.deleteTable("vm_cluster", ipString, "vm_cluster");
							// 集群的性能信息表
							ctable.deleteTable("vm_datastore", ipString, "vm_datastore");
							// 存储的性能信息表
							ctable.deleteTable("vm_resourcepool", ipString, "vm_resourcepool");
							// 资源池的性能信息表
							// vm_basephysical
							ctable.deleteTable("vm_basephysical", ipString, "vm_basephysical");
							// 物理机的基础信息表
							ctable.deleteTable("vm_basevmware", ipString, "vm_basevmware");
							// 虚拟机的基础信息表
							ctable.deleteTable("vm_baseyun", ipString, "vm_baseyun");
							// 云资源的基础信息表
							ctable.deleteTable("vm_basedatastore", ipString, "vm_basedatastore");
							// 存储的基础信息表
							ctable.deleteTable("vm_basedatacenter", ipString, "vm_basedatacenter");
							// 数据中心的基础信息表
							ctable.deleteTable("vm_baseresource", ipString, "vm_baseresource");
							// 资源池的基础信息表
						} else if (host.getCategory() == 16) {
							// 删除空调临时表中的数据aircondition
							String[] nmsTempDataTables = { "nms_emeairconhum", "nms_emeairconparinfo", "nms_emeaircontem" };
							String[] uniqueKeyValues = { host.getIpAddress() };
							createTableManager.clearTablesData(nmsTempDataTables, "ipaddress", uniqueKeyValues);
						} else if (host.getCategory() == 17) {// UPS
							ctable.deleteTable("input", ipString, "input");
							ctable.deleteTable("inputhour", ipString, "inputhour");
							ctable.deleteTable("inputday", ipString, "inputday");
							ctable.deleteTable("output", ipString, "output");
							ctable.deleteTable("outputhour", ipString, "outputhour");
							ctable.deleteTable("outputday", ipString, "outputday");
						}

						DiscoverCompleteDao dcDao = new DiscoverCompleteDao();
						try {
							dcDao.deleteMonitor(host.getId(), host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							dcDao.close();
						}

						EventListDao eventdao = new EventListDao();
						try {
							// 同时删除事件表里的相关数据
							if (host.getCategory() == 12) {
								eventdao.delete(host.getId(), "vpn");
							} else if (host.getCategory() == 13) {
								eventdao.delete(host.getId(), "cmts");
							} else if (host.getCategory() == 14) {
								eventdao.delete(host.getId(), "storage");
							} else if (host.getCategory() == 16) {
								eventdao.delete(host.getId(), "air");
							} else if (host.getCategory() == 17) {
								eventdao.delete(host.getId(), "ups");
							} else {
								eventdao.delete(host.getId(), "net");
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							eventdao.close();
						}

						PortconfigDao portconfigdao = new PortconfigDao();
						try {
							// 同时删除端口配置表里的相关数据
							portconfigdao.deleteByIpaddress(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							portconfigdao.close();
						}

						AlarmPortDao portdao = new AlarmPortDao();
						try {
							// 同时删除端口监视配置表里的相关数据
							portdao.deleteByIpaddress(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							portdao.close();
						}

						// 删除nms_ipmacchange表里的对应的数据
						IpMacChangeDao macchangebasedao = new IpMacChangeDao();
						try {
							macchangebasedao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							macchangebasedao.close();
						}

						// 删除网络设备配置文件表里的对应的数据
						NetNodeCfgFileDao configdao = new NetNodeCfgFileDao();
						try {
							configdao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							configdao.close();
						}

						// 删除网络设备SYSLOG接收表里的对应的数据
						NetSyslogDao syslogdao = new NetSyslogDao();
						try {
							syslogdao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							syslogdao.close();
						}

						// 删除网络设备端口扫描表里的对应的数据
						PortScanDao portscandao = new PortScanDao();
						try {
							portscandao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							portscandao.close();
						}

						// 删除网络设备面板图表里的对应的数据
						IpaddressPanelDao addresspaneldao = new IpaddressPanelDao();
						try {
							addresspaneldao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							addresspaneldao.close();
						}

						// 删除网络设备接口表里的对应的数据
						HostInterfaceDao interfacedao = new HostInterfaceDao();
						try {
							interfacedao.deleteByHostId(host.getId() + "");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							interfacedao.close();
						}

						// 删除网络设备IP别名表里的对应的数据
						IpAliasDao ipaliasdao = new IpAliasDao();
						try {
							ipaliasdao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							ipaliasdao.close();
						}

						// 删除网络设备手工配置的链路表里的对应的数据
						RepairLinkDao repairdao = new RepairLinkDao();
						try {
							repairdao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							repairdao.close();
						}

						// 删除网络设备IPMAC表里的对应的数据
						IpMacDao ipmacdao = new IpMacDao();
						try {
							ipmacdao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							ipmacdao.close();
						}

						if (host.getCategory() == 15 && host.getOstype() == 40 && host.getCollecttype() == 10) {
							VMWareConnectConfigDao vmwaredao = new VMWareConnectConfigDao();
							try {
								vmwaredao.delete(Long.parseLong(host.getId() + ""));
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								vmwaredao.close();
							}
						}

						// 删除该设备的采集指标
						NodeGatherIndicatorsDao gatherdao = new NodeGatherIndicatorsDao();
						try {
							if (host.getCategory() == 12) {
								gatherdao.deleteByNodeIdAndTypeAndSubtype(host.getId() + "", "vpn", "");
							} else if (host.getCategory() == 13) {
								gatherdao.deleteByNodeIdAndTypeAndSubtype(host.getId() + "", "cmts", "");
							} else if (host.getCategory() == 14) {
								gatherdao.deleteByNodeIdAndTypeAndSubtype(host.getId() + "", "storage", "");
							} else if (host.getCategory() == 15) {
								gatherdao.deleteByNodeIdAndTypeAndSubtype(host.getId() + "", "virtual", "");
							} else if (host.getCategory() == 16) {
								gatherdao.deleteByNodeIdAndTypeAndSubtype(host.getId() + "", "air", "");
							} else if (host.getCategory() == 17) {
								gatherdao.deleteByNodeIdAndTypeAndSubtype(host.getId() + "", "ups", "");
							} else {
								gatherdao.deleteByNodeIdAndTypeAndSubtype(host.getId() + "", "net", "");
							}
						} catch (RuntimeException e) {
							e.printStackTrace();
						} finally {
							gatherdao.close();
						}

						// 删除网络设备指标采集表里的对应的数据
						AlarmIndicatorsNodeDao indicatdao = new AlarmIndicatorsNodeDao();
						try {
							if (host.getCategory() == 12) {
								indicatdao.deleteByNodeId(host.getId() + "", "vpn");
							} else if (host.getCategory() == 13) {
								indicatdao.deleteByNodeId(host.getId() + "", "cmts");
							} else if (host.getCategory() == 14) {
								indicatdao.deleteByNodeId(host.getId() + "", "storage");
							} else if (host.getCategory() == 15) {
								indicatdao.deleteByNodeId(host.getId() + "", "virtual");
							} else if (host.getCategory() == 16) {
								indicatdao.deleteByNodeId(host.getId() + "", "air");
							} else if (host.getCategory() == 17) {
								indicatdao.deleteByNodeId(host.getId() + "", "ups");
							} else {
								indicatdao.deleteByNodeId(host.getId() + "", "net");
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							indicatdao.close();
						}

						// 删除IP-MAC-BASE表里的对应的数据
						IpMacBaseDao macbasedao = new IpMacBaseDao();
						try {
							macbasedao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							macbasedao.close();
						}
						// 删除设备当前最新告警信息表中的数据
						NodeAlarmUtil.deleteByDeviceIdAndDeviceType(host);

						// 删除SYSLOG规则表
						NetSyslogNodeRuleDao ruledao = new NetSyslogNodeRuleDao();
						try {
							ruledao.deleteByNodeID(host.getId() + "");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							ruledao.close();
						}
					} else if (host.getCategory() == 4) {
						// 删除主机服务器
						try {
							ctable.deleteTable("pro", ipString, "pro");
							ctable.deleteTable("prohour", ipString, "prohour");
							ctable.deleteTable("proday", ipString, "proday");

							ctable.deleteTable("log", ipString, "log");

							ctable.deleteTable("memory", ipString, "mem");
							ctable.deleteTable("memoryhour", ipString, "memhour");
							ctable.deleteTable("memoryday", ipString, "memday");

							ctable.deleteTable("cpu", ipString, "cpu");
							ctable.deleteTable("cpuhour", ipString, "cpuhour");
							ctable.deleteTable("cpuday", ipString, "cpuday");
							ctable.deleteTable("cpudtl", ipString, "cpudtl");
							ctable.deleteTable("cpudtlhour", ipString, "cpudtlhour");
							ctable.deleteTable("cpudtlday", ipString, "cpudtlday");

							ctable.deleteTable("disk", ipString, "disk");
							ctable.deleteTable("diskhour", ipString, "diskhour");
							ctable.deleteTable("diskday", ipString, "diskday");

							ctable.deleteTable("diskincre", ipString, "diskincre");
							ctable.deleteTable("diskinch", ipString, "diskinch");
							ctable.deleteTable("diskincd", ipString, "diskincd");

							ctable.deleteTable("ping", ipString, "ping");
							ctable.deleteTable("pinghour", ipString, "pinghour");
							ctable.deleteTable("pingday", ipString, "pingday");

							ctable.deleteTable("utilhdxperc", ipString, "hdperc");
							ctable.deleteTable("hdxperchour", ipString, "hdperchour");
							ctable.deleteTable("hdxpercday", ipString, "hdpercday");

							ctable.deleteTable("utilhdx", ipString, "hdx");
							ctable.deleteTable("utilhdxhour", ipString, "hdxhour");
							ctable.deleteTable("utilhdxday", ipString, "hdxday");

							ctable.deleteTable("software", ipString, "software");

							ctable.deleteTable("allutilhdx", ipString, "allhdx");
							ctable.deleteTable("autilhdxh", ipString, "ahdxh");
							ctable.deleteTable("autilhdxd", ipString, "ahdxd");

							ctable.deleteTable("discardsperc", ipString, "dcardperc");
							ctable.deleteTable("dcarperh", ipString, "dcarperh");
							ctable.deleteTable("dcarperd", ipString, "dcarperd");

							ctable.deleteTable("errorsperc", ipString, "errperc");
							ctable.deleteTable("errperch", ipString, "errperch");
							ctable.deleteTable("errpercd", ipString, "errpercd");

							ctable.deleteTable("packs", ipString, "packs");
							ctable.deleteTable("packshour", ipString, "packshour");
							ctable.deleteTable("packsday", ipString, "packsday");

							ctable.deleteTable("inpacks", ipString, "inpacks");
							ctable.deleteTable("ipacksh", ipString, "ipacksh");
							ctable.deleteTable("ipackd", ipString, "ipackd");

							ctable.deleteTable("outpacks", ipString, "outpacks");
							ctable.deleteTable("opackh", ipString, "opackh");
							ctable.deleteTable("opacksd", ipString, "opacksd");
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.2.3.1.2.1.1")) {
							// 删除换页率
							try {
								ctable.deleteTable("pgused", ipString, "pgused");
								ctable.deleteTable("pgusedhour", ipString, "pgusedhour");
								ctable.deleteTable("pgusedday", ipString, "pgusedday");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						// 删除采集指标
						NodeGatherIndicatorsDao gatherdao = new NodeGatherIndicatorsDao();
						try {
							gatherdao.deleteByNodeIdAndTypeAndSubtype(host.getId() + "", "host", "");
						} catch (RuntimeException e) {
							e.printStackTrace();
						} finally {
							gatherdao.close();
						}

						// 删除告警指标
						AlarmIndicatorsNodeDao indicatdao = new AlarmIndicatorsNodeDao();
						try {
							indicatdao.deleteByNodeId(host.getId() + "", "host");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							indicatdao.close();
						}

						EventListDao eventdao = new EventListDao();
						try {
							// 同时删除事件表里的相关数据
							eventdao.delete(host.getId(), "host");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							eventdao.close();
						}
						// 删除diskconfig
						String[] otherTempData = new String[] { "nms_diskconfig" };
						String[] ipStrs = new String[] { host.getIpAddress() };
						ctable.clearTablesData(otherTempData, "ipaddress", ipStrs);
						
						// 删除进程组的数据
						ProcessGroupConfigurationUtil processGroupConfigurationUtil = new ProcessGroupConfigurationUtil();
						processGroupConfigurationUtil.deleteProcessGroupAndConfigurationByNodeid(host.getId() + "");

						// 删除SYSLOG规则表
						NetSyslogNodeRuleDao ruledao = new NetSyslogNodeRuleDao();
						try {
							ruledao.deleteByNodeID(host.getId() + "");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							ruledao.close();
						}

						PortconfigDao portconfigdao = new PortconfigDao();
						try {
							// 同时删除端口配置表里的相关数据
							portconfigdao.deleteByIpaddress(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							portconfigdao.close();
						}
						
						// 删除网络设备接口表里的对应的数据
						HostInterfaceDao interfacedao = new HostInterfaceDao();
						try {
							interfacedao.deleteByHostId(host.getId() + "");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							interfacedao.close();
						}

						// 删除设备当前最新告警信息表中的数据
						NodeAlarmUtil.deleteByDeviceIdAndDeviceType(host);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				// 2.更新xml
				if (host.getCategory() < 4) {
					// 网络设备
					ManageXmlDao mdao = new ManageXmlDao();
					List<ManageXml> list = mdao.loadAll();
					if (list.size() > 0) {
						for (int k = 0; k < list.size(); k++) {
							ManageXml manageXml = list.get(k);
							XmlOperator xopr = new XmlOperator();
							String xmlName = manageXml.getXmlName();
							String nodeid = "net" + host.getId();
							xopr.setFile(xmlName);
							xopr.init4updateXml();
							if (xopr.isNodeExist(nodeid)) {
								xopr.deleteNodeByID(nodeid);
							}
							xopr.writeXml();
							RelationDao rdao = new RelationDao();
							Relation vo = (Relation) rdao.findByNodeId(id, xmlName);
							if (vo != null) {
								rdao.deleteByNode(id, xmlName);
							} else {
								rdao.close();
							}
							// 删除关联图元表的数据
							NodeEquipDao nodeEquipDao = new NodeEquipDao();
							if (nodeEquipDao.findByNode(nodeid) != null) {
								nodeEquipDao.deleteByNode(nodeid);
							} else {
								nodeEquipDao.close();
							}
						}
					}
				} else if (host.getCategory() == 4) {
					// 主机服务器
					try {
						XmlOperator opr = new XmlOperator();
						opr.setFile("server.jsp");
						opr.init4updateXml();
						opr.deleteNodeByID(host.getId() + "");
						opr.writeXml();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				// 删除指标全局阈值表对应的数据
				NodeMonitorDao nodeMonitorDao = new NodeMonitorDao();
				try {
					nodeMonitorDao.deleteByID(id);
				} catch (RuntimeException e1) {
					e1.printStackTrace();
				} finally {
					nodeMonitorDao.close();
				}

				if (host.getOstype() == 15) {
					// as400
					CreateTableManager ctable2 = new CreateTableManager();
					try {
						ctable2.deleteTable("systemasp", ipString, "systemasp");
						ctable2.deleteTable("dbcapability", ipString, "dbcapability");
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				}

				// 将连接删除
				if (linkList != null && linkList.size() > 0) {
					for (int l = 0; l < linkList.size(); l++) {
						link = (Link) linkList.get(l);
						if (link != null) {
							LinkDao ldao = new LinkDao();
							try {
								ldao.delete(link.getId() + "");
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								ldao.close();
							}
						}
					}
				}

				// 更新业务视图
				NodeDependDao nodedependao = new NodeDependDao();
				List list = nodedependao.findByNode("net" + id);
				if (list != null && list.size() > 0) {
					for (int j = 0; j < list.size(); j++) {
						NodeDepend vo = (NodeDepend) list.get(j);
						if (vo != null) {
							LineDao lineDao = new LineDao();
							lineDao.deleteByidXml("net" + id, vo.getXmlfile());
							NodeDependDao nodeDependDao = new NodeDependDao();
							if (nodeDependDao.isNodeExist("net" + id, vo.getXmlfile())) {
								nodeDependDao.deleteByIdXml("net" + id, vo.getXmlfile());
							} else {
								nodeDependDao.close();
							}

							User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
							ManageXmlDao mXmlDao = new ManageXmlDao();
							List xmlList = new ArrayList();
							try {
								xmlList = mXmlDao.loadByPerAll(user.getBusinessids());
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								mXmlDao.close();
							}
							try {
								ChartXml chartxml;
								chartxml = new ChartXml("tree");
								chartxml.addViewTree(xmlList);
							} catch (Exception e) {
								e.printStackTrace();
							}

							ManageXmlDao subMapDao = new ManageXmlDao();
							ManageXml manageXml = (ManageXml) subMapDao.findByXml(vo.getXmlfile());
							if (manageXml != null) {
								NodeDependDao nodeDepenDao = new NodeDependDao();
								try {
									List lists = nodeDepenDao.findByXml(vo.getXmlfile());
									ChartXml chartxml;
									chartxml = new ChartXml("NetworkMonitor", "/" + vo.getXmlfile().replace("jsp", "xml"));
									chartxml.addBussinessXML(manageXml.getTopoName(), lists);
									ChartXml chartxmlList;
									chartxmlList = new ChartXml("NetworkMonitor", "/" + vo.getXmlfile().replace("jsp", "xml").replace("businessmap", "list"));
									chartxmlList.addListXML(manageXml.getTopoName(), lists);
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									nodeDepenDao.close();
								}
							}
						}
					}
				}

				// 用户操作审计
				User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Calendar tempCal = Calendar.getInstance();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				UserAuditUtil useraudit = new UserAuditUtil();
				String useraction = "";
				useraction = useraction + "删除设备 IP:" + host.getIpAddress() + " 别名:" + host.getAlias() + " 类型:" + host.getType();
				try {
					useraudit.saveUserAudit(current_user, time, useraction);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (host.getOstype() == 46) {// netapp
					String[] netAppDataTables = { "netappaggregate", "netappconsistencypoint", "netappdisk", "netappdump", "netappdumplist", "netappenvironment", "netappplex", "netappproductinformation", "netappquota", "netappraid", "netapprestore", "netappsnapshot", "netappspare", "netapptree", "netappvfiler", "netappvfileripentity", "netappvfilerpathentity", "netappvfilerprotocolentity", "netappvolume" };
					createTableManager.clearNetAppDatas(netAppDataTables, ipAddress, id);
				}
			}

			// 删除设备在临时表里中存储的数据
			String[] nmsTempDataTables = { "nms_cpu_data_temp", "nms_device_data_temp", "nms_disk_data_temp", "nms_diskperf_data_temp", "nms_envir_data_temp", "nms_fdb_data_temp", "nms_fibrecapability_data_temp", "nms_fibreconfig_data_temp", "nms_flash_data_temp", "nms_interface_data_temp", "nms_lights_data_temp", "nms_memory_data_temp", "nms_other_data_temp", "nms_ping_data_temp", "nms_process_data_temp", "nms_route_data_temp", "nms_sercice_data_temp", "nms_software_data_temp", "nms_storage_data_temp", "nms_system_data_temp", "nms_user_data_temp", "nms_nodeconfig", "nms_nodecpuconfig", "nms_nodediskconfig", "nms_nodememconfig", "nms_vmwarevid", "nms_emcdiskcon", "nms_emcluncon", "nms_emchard", "nms_emcraid", "nms_emcsystem", "nms_connect" };
			createTableManager.clearNmsTempDatas(nmsTempDataTables, ids);
		}

		HostNodeDao hostNodeDao = new HostNodeDao();
		Hashtable nodehash = new Hashtable();
		try {
			List hostlist = hostNodeDao.loadIsMonitored(1);
			if (hostlist != null && hostlist.size() > 0) {
				for (int i = 0; i < hostlist.size(); i++) {
					HostNode node = (HostNode) hostlist.get(i);
					if (nodehash.containsKey(node.getCategory() + "")) {
						((List) nodehash.get(node.getCategory() + "")).add(node);
					} else {
						List nodelist = new ArrayList();
						nodelist.add(node);
						nodehash.put(node.getCategory() + "", nodelist);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			hostNodeDao.close();
		}
		ShareData.setNodehash(nodehash);

		PortConfigCenter.getInstance().setPortHastable();

		out.print("成功删除");
		out.flush();
	}

	public String getSutType(String oids) {
		String subtype = "";
		if (oids.startsWith("1.3.6.1.4.1.311.")) {
			subtype = "windows";
		} else if (oids.startsWith("1.3.6.1.4.1.2021") || oids.startsWith("1.3.6.1.4.1.8072")) {
			subtype = "linux";
		} else if (oids.startsWith("as400")) {
			subtype = "as400";

		} else if (oids.startsWith("1.3.6.1.4.1.42.2.1.1")) {
			subtype = "solaris";
		} else if (oids.startsWith("1.3.6.1.4.1.2.3.1.2.1.1")) {
			subtype = "aix";
		} else if (oids.startsWith("1.3.6.1.4.1.11.2.3.10.1")) {
			subtype = "hpunix";
		} else if (oids.startsWith("1.3.6.1.4.1.11.2.3.7.11")) {
			subtype = "hp";
		} else if (oids.startsWith("1.3.6.1.4.1.9.")) {
			subtype = "cisco";
		} else if (oids.startsWith("1.3.6.1.4.1.25506.") || oids.startsWith("1.3.6.1.4.1.2011.")) {
			subtype = "h3c";
		} else if (oids.startsWith("1.3.6.1.4.1.4881.")) {
			subtype = "redgiant";
		} else if (oids.startsWith("1.3.6.1.4.1.5651.")) {
			subtype = "maipu";
		} else if (oids.startsWith("1.3.6.1.4.1.171.")) {
			subtype = "dlink";
		} else if (oids.startsWith("1.3.6.1.4.1.2272.")) {
			subtype = "northtel";
		} else if (oids.startsWith("1.3.6.1.4.1.89.")) {
			subtype = "radware";
		} else if (oids.startsWith("1.3.6.1.4.1.3320.")) {
			subtype = "bdcom";
		} else if (oids.startsWith("1.3.6.1.4.1.1588.2.1.")) {
			subtype = "brocade";
		} else if (oids.startsWith("1.3.6.1.4.1.3902.")) {
			subtype = "zte";
		} else if (oids.startsWith("1.3.6.1.4.1.116.")) {
			subtype = "hds";
		} else if (oids.startsWith("1.3.6.1.4.1.14331.")) {
			// 天融信防火墙
			subtype = "topsec";
		} else if (oids.startsWith("1.3.6.1.4.1.800.")) {
			// Alcatel
			subtype = "alcatel";
		} else if (oids.startsWith("1.3.6.1.4.1.45.")) {
			// Avaya
			subtype = "avaya";
		} else if (oids.startsWith("1.3.6.1.4.1.6876.")) {
			// VMWare
			subtype = "vmware";
		} else if (oids.startsWith("1.3.6.1.4.1.1981.1")) {
			subtype = "emc_vnx";
		} else if (oids.startsWith("1.3.6.1.4.1.1981.2")) {
			subtype = "emc_dmx";
		} else if (oids.startsWith("1.3.6.1.4.1.1981.3")) {
			subtype = "emc_vmax";
		} else if (oids.startsWith("1.3.6.1.4.1.2636.")) {
			subtype = "juniper";
		} else if (oids.startsWith("1.3.6.1.4.1.3224.")) {
			subtype = "checkpoint";
		} else if (oids.startsWith("1.3.6.1.4.1.789.")) {
			subtype = "netapp";
		} else if (oids.startsWith("1.3.6.1.4.1.476.1.42") || oids.startsWith("1.3.6.1.4.1.13400.2.1")) {
			subtype = "emerson";
		}

		return subtype;
	}
}
