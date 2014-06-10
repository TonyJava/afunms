package com.afunms.polling.impl;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.IISConfigDao;
import com.afunms.application.dao.ResinDao;
import com.afunms.application.dao.WeblogicConfigDao;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.IISConfig;
import com.afunms.application.model.Resin;
import com.afunms.application.model.WeblogicConfig;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.DateInformation;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectDataDay;
import com.afunms.topology.model.HostNode;
import com.afunms.util.DataGate;
import com.database.config.SystemConfig;

@SuppressWarnings("unchecked")
public class HostCollectDataDayManager implements I_HostCollectDataDay {

	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public HostCollectDataDayManager() {
		super();
	}

	private BaseVo loadFromRS(ResultSet rs) {
		HostNode vo = new HostNode();
		try {
			vo.setId(rs.getInt("id"));
			vo.setAssetid(rs.getString("asset_id"));
			vo.setLocation(rs.getString("location"));
			vo.setIpAddress(rs.getString("ip_address"));
			vo.setIpLong(rs.getLong("ip_long"));
			vo.setSysName(rs.getString("sys_name"));
			vo.setAlias(rs.getString("alias"));
			vo.setNetMask(rs.getString("net_mask"));
			vo.setSysDescr(rs.getString("sys_descr"));
			vo.setSysLocation(rs.getString("sys_location"));
			vo.setSysContact(rs.getString("sys_contact"));
			vo.setSysOid(rs.getString("sys_oid"));
			vo.setCommunity(rs.getString("community"));
			vo.setWriteCommunity(rs.getString("write_community"));
			vo.setSnmpversion(rs.getInt("snmpversion"));
			vo.setTransfer(rs.getInt("transfer"));
			vo.setCategory(rs.getInt("category"));
			vo.setManaged(rs.getInt("managed") == 1 ? true : false);
			vo.setType(rs.getString("type"));
			vo.setSuperNode(rs.getInt("super_node"));
			vo.setLocalNet(rs.getInt("local_net"));
			vo.setLayer(rs.getInt("layer"));
			vo.setBridgeAddress(rs.getString("bridge_address"));
			vo.setStatus(rs.getInt("status"));
			vo.setDiscovertatus(rs.getInt("discoverstatus"));
			vo.setOstype(rs.getInt("ostype"));
			vo.setCollecttype(rs.getInt("collecttype"));
			vo.setSendemail(rs.getString("sendemail"));
			vo.setSendmobiles(rs.getString("sendmobiles"));
			vo.setSendphone(rs.getString("sendphone"));
			vo.setBid(rs.getString("bid"));
			vo.setEndpoint(rs.getInt("endpoint"));
			vo.setSupperid(rs.getInt("supperid"));
			vo.setSecuritylevel(rs.getInt("securitylevel"));
			vo.setSecurityName(rs.getString("securityName"));
			vo.setV3_ap(rs.getInt("v3_ap"));
			vo.setAuthpassphrase(rs.getString("authpassphrase"));
			vo.setV3_privacy(rs.getInt("v3_privacy"));
			vo.setPrivacyPassphrase(rs.getString("privacyPassphrase"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean schemeTask() throws Exception {
		String runmodel = PollingEngine.getCollectwebflag();
		if (ShareData.getCount() == 1) {
			Connection conn = null;
			Statement stmt = null;
			ResultSet rs = null;
			List list = new ArrayList();
			try {
				conn = DataGate.getCon();
				conn.setAutoCommit(false);
				stmt = conn.createStatement();
				if ("0".equals(runmodel)) {
					rs = stmt.executeQuery("select * from topo_host_node where 1 like '%1%'");
				} else {
					int agentid = -1;
					agentid = Integer.parseInt(SystemConfig.getConfigInfomation("Agentconfig", "AGENTID"));
					rs = stmt.executeQuery("select * from topo_host_node a ,nms_node_agent c where a.managed=1 and c.nodeid=a.id and c.agentid='" + agentid + "'");
				}
				while (rs.next()) {
					list.add(loadFromRS(rs));
				}
				if (list != null && list.size() > 0) {
					HostNode host = null;
					String ip = "";
					String allipstr = "";
					String sql = "";
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						for (int i = 0; i < list.size(); i++) {
							host = (HostNode) list.get(i);
							ip = host.getIpAddress();
							allipstr = SysUtil.doip(ip);
							sql = "";
							if ((host.getCategory() > 0 && host.getCategory() < 4) || host.getCategory() == 7) {
								// 处理网络设备数据
								/**
								 * 每天定时归档历史数据
								 */

								// Ping
								// 按天归档表PINGHOUR数据到小时表PINGDAY
								sql = "insert into pingday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM pinghour" + allipstr + " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP- INTERVAL 1 DAY,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								/**
								 * 按照规定的天数删除历史数据
								 */
								// Ping
								sql = "delete from ping" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>31)";
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								/**
								 * 按照规定的天数删除历史数据里按小时归档的部分
								 */

								// 删除pinghour 365天前的数据
								sql = "delete FROM pinghour" + allipstr + "  where (TO_DAYS(NOW())-TO_DAYS(collecttime)>365)";
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								/**
								 * 按照规定的天数删除历史数据里按天归档的部分
								 */

								// 删除pingday 1000天前的数据
								sql = "delete FROM pingday" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>1000)";
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								if (host.getCollecttype() != SystemConstant.COLLECTTYPE_PING && host.getCollecttype() != SystemConstant.COLLECTTYPE_SSHCONNECT && host.getCollecttype() != SystemConstant.COLLECTTYPE_TELNETCONNECT) {
									// 只PING操作的时候,只有三张表需要汇总,其他情况需要汇总如下的表格

									// Memory
									// 按天归档表MEMORYHOUR数据到小时表MEMORYDAY
									sql = "insert into memoryday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM memoryhour" + allipstr + " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP- INTERVAL 1 DAY,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表CPUHOUR数据到小时表CPUDAY
									sql = "insert into cpuday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM cpuhour" + allipstr + " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP- INTERVAL 1 DAY,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表UTILHDXHOUR数据到小时表UTILHDXDAY
									sql = "insert into utilhdxday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM utilhdxhour" + allipstr + " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP- INTERVAL 1 DAY,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表ALLUTILHDXHOUR数据到小时表ALLUTILHDXDAY
									sql = "insert into autilhdxd" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM autilhdxh" + allipstr + " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP- INTERVAL 1 DAY,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表HDXPERCHOUR数据到小时表HDXPERCDAY
									sql = "insert into hdxpercday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM hdxperchour" + allipstr + " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP- INTERVAL 1 DAY,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表dcardperchour数据到小时表dcardpercday
									sql = "insert into dcarperd" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM dcarperh" + allipstr + " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP- INTERVAL 1 DAY,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表errperchour数据到小时表errpercday
									sql = "insert into errpercd" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM errperch" + allipstr + " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP- INTERVAL 1 DAY,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表packshour数据到小时表packsday
									sql = "insert into packsday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM packshour" + allipstr + " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP- INTERVAL 1 DAY,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									/**
									 * 按照规定的天数删除历史数据
									 */
									// Memory
									sql = "delete from memory" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// CPU
									sql = "delete from cpu" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// UtilHdx
									sql = "delete from utilhdx" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// UtilHdx
									sql = "delete from allutilhdx" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// UtilHdxPerc
									sql = "delete from utilhdxperc" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// DiscardsPerc
									sql = "delete from discardsperc" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// ErrorsPerc
									sql = "delete from errorsperc" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// Packs
									sql = "delete from packs" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// InPacks
									sql = "delete from inpacks" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// OutPacks
									sql = "delete from outpacks" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// Temper温度
									sql = "delete from temper" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									/**
									 * 按照规定的天数删除历史数据里按小时归档的部分
									 */
									/*
									 * //删除memoryhour 365天前的数据 sql="delete FROM
									 * memoryhour"+allipstr+" datahour where
									 * (SYSDATE-datahour.collecttime>365)"; stmt
									 * = con.prepareStatement(sql);
									 * stmt.execute(); stmt.close();
									 */
									// 删除cpuhour 365天前的数据
									sql = "delete FROM cpuhour" + allipstr + "  where (TO_DAYS(NOW())-TO_DAYS(collecttime)>365)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									// 删除utilhdxhour 365天前的数据
									sql = "delete FROM utilhdxhour" + allipstr + "  where (TO_DAYS(NOW())-TO_DAYS(collecttime)>365)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									// 删除URL历史表URLMONITOR_HISTORY 3个月前的数据
									sql = "delete FROM nms_web_history  where (TO_DAYS(NOW())-TO_DAYS(MON_TIME)>90)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									// 删除URLMONITOR_REALTIME 3个月前的数据
									sql = "delete FROM nms_web_realtime  where (TO_DAYS(NOW())-TO_DAYS(MON_TIME)>90)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									// 删除cpuday 1000天前的数据
									sql = "delete FROM cpuday" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除utilhdxday 1000天前的数据
									sql = "delete FROM utilhdxday" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除hdxpercday 1000天前的数据
									sql = "delete FROM hdxpercday" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除dcardpercday 1000天前的数据
									sql = "delete FROM dcarperd" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除errpercday 1000天前的数据
									sql = "delete FROM errpercd" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除packsday 1000天前的数据
									sql = "delete FROM packsday" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

								}

							} else if (host.getCategory() == 4) {
								// 汇总主机表
								/**
								 * 每天定时归档历史数据
								 */

								// Ping
								// 按天归档表PINGHOUR数据到小时表PINGDAY
								sql = "insert into pingday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM pinghour" + allipstr + " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP- INTERVAL 1 DAY,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								/**
								 * 按照规定的天数删除历史数据
								 */
								// Ping
								sql = "delete from ping" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>31)";
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								/**
								 * 按照规定的天数删除历史数据里按小时归档的部分
								 */

								// 删除pinghour 365天前的数据
								sql = "delete FROM pinghour" + allipstr + "  where (TO_DAYS(NOW())-TO_DAYS(collecttime)>365)";
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								/**
								 * 按照规定的天数删除历史数据里按天归档的部分
								 */

								// 删除pingday 1000天前的数据
								sql = "delete FROM pingday" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>1000)";
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								if (host.getCollecttype() != SystemConstant.COLLECTTYPE_PING && host.getCollecttype() != SystemConstant.COLLECTTYPE_SSHCONNECT && host.getCollecttype() != SystemConstant.COLLECTTYPE_TELNETCONNECT) {
									// 只PING操作的时候,只有三张表需要汇总,其他情况需要汇总如下的表格
									// 按天归档表MEMORYHOUR数据到小时表MEMORYDAY
									sql = "insert into memoryday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM memoryhour" + allipstr + " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP- INTERVAL 1 DAY,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表CPUHOUR数据到小时表CPUDAY
									sql = "insert into cpuday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM cpuhour" + allipstr + " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP- INTERVAL 1 DAY,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// Disk yangjun
									// 按天归档表diskincreHOUR数据到小时表diskincreDAY
									sql = "insert into diskincd" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,avg(hostcollectdata.thevalue) FROM diskinch" + allipstr + " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP- INTERVAL 1 DAY,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									sql = "insert into diskday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,avg(hostcollectdata.thevalue) FROM diskhour" + allipstr + " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP- INTERVAL 1 DAY,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表PROHOUR数据到小时表PRODAY
									sql = "insert into proday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM prohour" + allipstr + " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP- INTERVAL 1 DAY,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									/**
									 * 按照规定的天数删除历史数据
									 */
									// Process
									sql = "delete from pro" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// Memory
									sql = "delete from memory" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// CPU
									sql = "delete from cpu" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// Disk
									sql = "delete from disk" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// UtilHdx
									sql = "delete from utilhdx" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// SYSLOG
									sql = "delete from log" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(recordtime)>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									/**
									 * 按照规定的天数删除历史数据里按小时归档的部分
									 */
									// 删除memoryhour 365天前的数据
									sql = "delete FROM memoryhour" + allipstr + "  where (TO_DAYS(NOW())-TO_DAYS(collecttime)>365)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除cpuhour 365天前的数据
									sql = "delete FROM cpuhour" + allipstr + "  where (TO_DAYS(NOW())-TO_DAYS(collecttime)>365)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除diskhour 365天前的数据
									sql = "delete FROM diskhour" + allipstr + "  where (TO_DAYS(NOW())-TO_DAYS(collecttime)>365)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除UtilHdx 365天前的数据
									sql = "delete from utilhdxhour" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>365)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除prohour 365天前的数据
									sql = "delete FROM prohour" + allipstr + "  where (TO_DAYS(NOW())-TO_DAYS(collecttime)>365)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									/**
									 * 按照规定的天数删除历史数据里按天归档的部分
									 */
									// 删除memoryday 1000天前的数据
									sql = "delete FROM memoryday" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除cpuday 1000天前的数据
									sql = "delete FROM cpuday" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除diskday 1000天前的数据
									sql = "delete FROM diskday" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除utilhdxday 1000天前的数据
									sql = "delete FROM utilhdxday" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除proday 1000天前的数据
									sql = "delete FROM proday" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						for (int i = 0; i < list.size(); i++) {
							host = (HostNode) list.get(i);
							// 测试生成表
							ip = host.getIpAddress();
							allipstr = SysUtil.doip(ip);
							sql = "";
							if ((host.getCategory() > 0 && host.getCategory() < 4) || host.getCategory() == 7) {
								// 处理网络设备数据
								/**
								 * 每天定时归档历史数据
								 */

								// Ping
								// 按天归档表PINGHOUR数据到小时表PINGDAY
								sql = "insert into pingday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM pinghour" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								/**
								 * 按照规定的天数删除历史数据
								 */
								// Ping
								sql = "delete from ping" + allipstr + "  where  (SYSDATE-collecttime>31)";
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								/**
								 * 按照规定的天数删除历史数据里按小时归档的部分
								 */

								// 删除pinghour 365天前的数据
								sql = "delete FROM pinghour" + allipstr + "  where (SYSDATE-collecttime>365)";
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								/**
								 * 按照规定的天数删除历史数据里按天归档的部分
								 */

								// 删除pingday 1000天前的数据
								sql = "delete FROM pingday" + allipstr + "  where  (SYSDATE-collecttime>1000)";
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								if (host.getCollecttype() != SystemConstant.COLLECTTYPE_PING && host.getCollecttype() != SystemConstant.COLLECTTYPE_SSHCONNECT && host.getCollecttype() != SystemConstant.COLLECTTYPE_TELNETCONNECT) {
									// 只PING操作的时候,只有三张表需要汇总,其他情况需要汇总如下的表格

									// Memory
									// 按天归档表MEMORYHOUR数据到小时表MEMORYDAY
									sql = "insert into memoryday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM memoryhour" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表CPUHOUR数据到小时表CPUDAY
									sql = "insert into cpuday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM cpuhour" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表UTILHDXHOUR数据到小时表UTILHDXDAY
									sql = "insert into utilhdxday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM utilhdxhour" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表ALLUTILHDXHOUR数据到小时表ALLUTILHDXDAY
									sql = "insert into allutilhdxday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM autilhdxh" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表HDXPERCHOUR数据到小时表HDXPERCDAY
									sql = "insert into hdxpercday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM hdxperchour" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表dcardperchour数据到小时表dcardpercday
									sql = "insert into dcarperd" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM dcardperchour" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表errperchour数据到小时表errpercday
									sql = "insert into errpercd" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM errperch" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表packshour数据到小时表packsday
									sql = "insert into packsday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM packshour" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									/**
									 * 按照规定的天数删除历史数据
									 */
									// Memory
									sql = "delete from memory" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// CPU
									sql = "delete from cpu" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// UtilHdx
									sql = "delete from utilhdx" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// UtilHdx
									sql = "delete from allutilhdx" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// UtilHdxPerc
									sql = "delete from utilhdxperc" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// DiscardsPerc
									sql = "delete from discardsperc" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// ErrorsPerc
									sql = "delete from errorsperc" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// Packs
									sql = "delete from packs" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// InPacks
									sql = "delete from inpacks" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// OutPacks
									sql = "delete from outpacks" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// Temper温度
									sql = "delete from temper" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									// 删除cpuhour 365天前的数据
									sql = "delete FROM cpuhour" + allipstr + "  where (SYSDATE-collecttime>365)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									// 删除utilhdxhour 365天前的数据
									sql = "delete FROM utilhdxhour" + allipstr + "  where (SYSDATE-collecttime>365)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									// 删除URL历史表URLMONITOR_HISTORY 3个月前的数据
									sql = "delete FROM nms_web_history  where (SYSDATE-MON_TIME>90)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									// 删除URLMONITOR_REALTIME 3个月前的数据
									sql = "delete FROM nms_web_realtime  where (SYSDATE-MON_TIME>90)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									// 删除cpuday 1000天前的数据
									sql = "delete FROM cpuday" + allipstr + "  where  (SYSDATE-collecttime>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除utilhdxday 1000天前的数据
									sql = "delete FROM utilhdxday" + allipstr + "  where  (SYSDATE-collecttime>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除hdxpercday 1000天前的数据
									sql = "delete FROM hdxpercday" + allipstr + "  where  (SYSDATE-collecttime>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除dcardpercday 1000天前的数据
									sql = "delete FROM dcarperd" + allipstr + "  where  (SYSDATE-collecttime>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除errpercday 1000天前的数据
									sql = "delete FROM errpercd" + allipstr + "  where  (SYSDATE-collecttime>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除packsday 1000天前的数据
									sql = "delete FROM packsday" + allipstr + "  where  (SYSDATE-collecttime>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

								}

							} else if (host.getCategory() == 4) {
								// 汇总主机表
								/**
								 * 每天定时归档历史数据
								 */

								// Ping
								// 按天归档表PINGHOUR数据到小时表PINGDAY
								sql = "insert into pingday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM pinghour" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								/**
								 * 按照规定的天数删除历史数据
								 */
								// Ping
								sql = "delete from ping" + allipstr + "  where  (SYSDATE-collecttime>31)";
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								/**
								 * 按照规定的天数删除历史数据里按小时归档的部分
								 */

								// 删除pinghour 365天前的数据
								sql = "delete FROM pinghour" + allipstr + "  where (SYSDATE-collecttime>365)";
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								/**
								 * 按照规定的天数删除历史数据里按天归档的部分
								 */

								// 删除pingday 1000天前的数据
								sql = "delete FROM pingday" + allipstr + "  where  (SYSDATE-collecttime>1000)";
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								if (host.getCollecttype() != SystemConstant.COLLECTTYPE_PING && host.getCollecttype() != SystemConstant.COLLECTTYPE_SSHCONNECT && host.getCollecttype() != SystemConstant.COLLECTTYPE_TELNETCONNECT) {
									// 只PING操作的时候,只有三张表需要汇总,其他情况需要汇总如下的表格
									// 按天归档表MEMORYHOUR数据到小时表MEMORYDAY
									sql = "insert into memoryday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM memoryhour" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表CPUHOUR数据到小时表CPUDAY
									sql = "insert into cpuday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM cpuhour" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// Disk yangjun
									// 按天归档表diskincreHOUR数据到小时表diskincreDAY
									sql = "insert into diskincd" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,avg(hostcollectdata.thevalue) FROM diskinch" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									sql = "insert into diskday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,avg(hostcollectdata.thevalue) FROM diskhour" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表PROHOUR数据到小时表PRODAY
									sql = "insert into proday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM prohour" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									/**
									 * 按照规定的天数删除历史数据
									 */
									// Process
									sql = "delete from pro" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// Memory
									sql = "delete from memory" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// CPU
									sql = "delete from cpu" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// UtilHdx
									sql = "delete from utilhdx" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// SYSLOG
									sql = "delete from log" + allipstr + "  where  (SYSDATE-recordtime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									/**
									 * 按照规定的天数删除历史数据里按小时归档的部分
									 */
									// 删除memoryhour 365天前的数据
									sql = "delete FROM memoryhour" + allipstr + "  where (SYSDATE-collecttime>365)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除cpuhour 365天前的数据
									sql = "delete FROM cpuhour" + allipstr + "  where (SYSDATE-collecttime>365)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除UtilHdx 365天前的数据
									sql = "delete from utilhdxhour" + allipstr + "  where  (SYSDATE-collecttime>365)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除prohour 365天前的数据
									sql = "delete FROM prohour" + allipstr + "  where (SYSDATE-collecttime>365)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									/**
									 * 按照规定的天数删除历史数据里按天归档的部分
									 */
									// 删除memoryday 1000天前的数据
									sql = "delete FROM memoryday" + allipstr + "  where  (SYSDATE-collecttime>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除cpuday 1000天前的数据
									sql = "delete FROM cpuday" + allipstr + "  where  (SYSDATE-collecttime>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除utilhdxday 1000天前的数据
									sql = "delete FROM utilhdxday" + allipstr + "  where  (SYSDATE-collecttime>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除proday 1000天前的数据
									sql = "delete FROM proday" + allipstr + "  where  (SYSDATE-collecttime>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					} else if ("dm".equalsIgnoreCase(SystemConstant.DBType)) {
						for (int i = 0; i < list.size(); i++) {
							host = (HostNode) list.get(i);
							// 测试生成表
							ip = host.getIpAddress();
							allipstr = SysUtil.doip(ip);
							sql = "";
							if ((host.getCategory() > 0 && host.getCategory() < 4) || host.getCategory() == 7) {
								// 处理网络设备数据
								/**
								 * 每天定时归档历史数据
								 */

								// Ping
								// 按天归档表PINGHOUR数据到小时表PINGDAY
								sql = "insert into pingday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM pinghour" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								/**
								 * 按照规定的天数删除历史数据
								 */
								// Ping
								sql = "delete from ping" + allipstr + "  where  (SYSDATE-collecttime>31)";
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								/**
								 * 按照规定的天数删除历史数据里按小时归档的部分
								 */

								// 删除pinghour 365天前的数据
								sql = "delete FROM pinghour" + allipstr + "  where (SYSDATE-collecttime>365)";
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								/**
								 * 按照规定的天数删除历史数据里按天归档的部分
								 */

								// 删除pingday 1000天前的数据
								sql = "delete FROM pingday" + allipstr + "  where  (SYSDATE-collecttime>1000)";
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								if (host.getCollecttype() != SystemConstant.COLLECTTYPE_PING && host.getCollecttype() != SystemConstant.COLLECTTYPE_SSHCONNECT && host.getCollecttype() != SystemConstant.COLLECTTYPE_TELNETCONNECT) {
									// 只PING操作的时候,只有三张表需要汇总,其他情况需要汇总如下的表格

									// Memory
									// 按天归档表MEMORYHOUR数据到小时表MEMORYDAY
									sql = "insert into memoryday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM memoryhour" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表CPUHOUR数据到小时表CPUDAY
									sql = "insert into cpuday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM cpuhour" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表UTILHDXHOUR数据到小时表UTILHDXDAY
									sql = "insert into utilhdxday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM utilhdxhour" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表ALLUTILHDXHOUR数据到小时表ALLUTILHDXDAY
									sql = "insert into allutilhdxday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM autilhdxh" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表HDXPERCHOUR数据到小时表HDXPERCDAY
									sql = "insert into hdxpercday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM hdxperchour" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表dcardperchour数据到小时表dcardpercday
									sql = "insert into dcarperd" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM dcardperchour" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表errperchour数据到小时表errpercday
									sql = "insert into errpercd" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM errperch" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表packshour数据到小时表packsday
									sql = "insert into packsday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM packshour" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									/**
									 * 按照规定的天数删除历史数据
									 */
									// Memory
									sql = "delete from memory" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// CPU
									sql = "delete from cpu" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// UtilHdx
									sql = "delete from utilhdx" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// UtilHdx
									sql = "delete from allutilhdx" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// UtilHdxPerc
									sql = "delete from utilhdxperc" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// DiscardsPerc
									sql = "delete from discardsperc" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// ErrorsPerc
									sql = "delete from errorsperc" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// Packs
									sql = "delete from packs" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// InPacks
									sql = "delete from inpacks" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// OutPacks
									sql = "delete from outpacks" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// Temper温度
									sql = "delete from temper" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									/**
									 * 按照规定的天数删除历史数据里按小时归档的部分
									 */
									/*
									 * //删除memoryhour 365天前的数据 sql="delete FROM
									 * memoryhour"+allipstr+" datahour where
									 * (SYSDATE-datahour.collecttime>365)"; stmt
									 * = con.prepareStatement(sql);
									 * stmt.execute(); stmt.close();
									 */
									// 删除cpuhour 365天前的数据
									sql = "delete FROM cpuhour" + allipstr + "  where (SYSDATE-collecttime>365)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									// 删除utilhdxhour 365天前的数据
									sql = "delete FROM utilhdxhour" + allipstr + "  where (SYSDATE-collecttime>365)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									// 删除URL历史表URLMONITOR_HISTORY 3个月前的数据
									sql = "delete FROM nms_web_history  where (SYSDATE-MON_TIME>90)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									// 删除URLMONITOR_REALTIME 3个月前的数据
									sql = "delete FROM nms_web_realtime  where (SYSDATE-MON_TIME>90)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									// 删除cpuday 1000天前的数据
									sql = "delete FROM cpuday" + allipstr + "  where  (SYSDATE-collecttime>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除utilhdxday 1000天前的数据
									sql = "delete FROM utilhdxday" + allipstr + "  where  (SYSDATE-collecttime>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除hdxpercday 1000天前的数据
									sql = "delete FROM hdxpercday" + allipstr + "  where  (SYSDATE-collecttime>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除dcardpercday 1000天前的数据
									sql = "delete FROM dcarperd" + allipstr + "  where  (SYSDATE-collecttime>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除errpercday 1000天前的数据
									sql = "delete FROM errpercd" + allipstr + "  where  (SYSDATE-collecttime>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除packsday 1000天前的数据
									sql = "delete FROM packsday" + allipstr + "  where  (SYSDATE-collecttime>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

								}

							} else if (host.getCategory() == 4) {
								// 汇总主机表
								/**
								 * 每天定时归档历史数据
								 */

								// Ping
								// 按天归档表PINGHOUR数据到小时表PINGDAY
								sql = "insert into pingday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM pinghour" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								/**
								 * 按照规定的天数删除历史数据
								 */
								// Ping
								sql = "delete from ping" + allipstr + "  where  (SYSDATE-collecttime>31)";
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								/**
								 * 按照规定的天数删除历史数据里按小时归档的部分
								 */

								// 删除pinghour 365天前的数据
								sql = "delete FROM pinghour" + allipstr + "  where (SYSDATE-collecttime>365)";
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								/**
								 * 按照规定的天数删除历史数据里按天归档的部分
								 */

								// 删除pingday 1000天前的数据
								sql = "delete FROM pingday" + allipstr + "  where  (SYSDATE-collecttime>1000)";
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								if (host.getCollecttype() != SystemConstant.COLLECTTYPE_PING && host.getCollecttype() != SystemConstant.COLLECTTYPE_SSHCONNECT && host.getCollecttype() != SystemConstant.COLLECTTYPE_TELNETCONNECT) {
									// 只PING操作的时候,只有三张表需要汇总,其他情况需要汇总如下的表格
									// 按天归档表MEMORYHOUR数据到小时表MEMORYDAY
									sql = "insert into memoryday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM memoryhour" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表CPUHOUR数据到小时表CPUDAY
									sql = "insert into cpuday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM cpuhour" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// Disk yangjun
									// 按天归档表diskincreHOUR数据到小时表diskincreDAY
									sql = "insert into diskincd" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,avg(hostcollectdata.thevalue) FROM diskinch" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									sql = "insert into diskday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,avg(hostcollectdata.thevalue) FROM diskhour" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 按天归档表PROHOUR数据到小时表PRODAY
									sql = "insert into proday" + allipstr + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM prohour" + allipstr + " hostcollectdata where (to_char(SYSDATE-1,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									/**
									 * 按照规定的天数删除历史数据
									 */
									// Process
									sql = "delete from pro" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// Memory
									sql = "delete from memory" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// CPU
									sql = "delete from cpu" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// UtilHdx
									sql = "delete from utilhdx" + allipstr + "  where  (SYSDATE-collecttime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// SYSLOG
									sql = "delete from log" + allipstr + "  where  (SYSDATE-recordtime>31)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									/**
									 * 按照规定的天数删除历史数据里按小时归档的部分
									 */
									// 删除memoryhour 365天前的数据
									sql = "delete FROM memoryhour" + allipstr + "  where (SYSDATE-collecttime>365)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除cpuhour 365天前的数据
									sql = "delete FROM cpuhour" + allipstr + "  where (SYSDATE-collecttime>365)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除UtilHdx 365天前的数据
									sql = "delete from utilhdxhour" + allipstr + "  where  (SYSDATE-collecttime>365)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除prohour 365天前的数据
									sql = "delete FROM prohour" + allipstr + "  where (SYSDATE-collecttime>365)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}

									/**
									 * 按照规定的天数删除历史数据里按天归档的部分
									 */
									// 删除memoryday 1000天前的数据
									sql = "delete FROM memoryday" + allipstr + "  where  (SYSDATE-collecttime>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除cpuday 1000天前的数据
									sql = "delete FROM cpuday" + allipstr + "  where  (SYSDATE-collecttime>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除utilhdxday 1000天前的数据
									sql = "delete FROM utilhdxday" + allipstr + "  where  (SYSDATE-collecttime>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
									// 删除proday 1000天前的数据
									sql = "delete FROM proday" + allipstr + "  where  (SYSDATE-collecttime>1000)";
									try {
										stmt.addBatch(sql);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					}

				}

				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					// 删除事件表数据
					String eventlist_sql = "delete from system_eventlist  where  (TO_DAYS(NOW())-TO_DAYS(recordtime)>30)";
					String event_sql = "delete from nms_alarminfo  where  (TO_DAYS(NOW())-TO_DAYS(recordtime)>30)";
					try {
						stmt.addBatch(eventlist_sql);
						stmt.addBatch(event_sql);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					// 删除事件表数据
					String eventlist_sql = "delete from system_eventlist  where  (SYSDATE-recordtime>30)";
					String event_sql = "delete from nms_alarminfo   where  (SYSDATE-recordtime>30)";
					try {
						stmt.addBatch(eventlist_sql);
						stmt.addBatch(event_sql);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if ("dm".equalsIgnoreCase(SystemConstant.DBType)) {
					// 删除事件表数据
					String eventlist_sql = "delete from system_eventlist  where  (SYSDATE-recordtime>30)";
					String event_sql = "delete from nms_alarminfo   where  (SYSDATE-recordtime>30)";
					try {
						stmt.addBatch(eventlist_sql);
						stmt.addBatch(event_sql);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				//
				String ip = "";
				String allipstr = "";
				String sql = "";
				list = new ArrayList();
				IISConfigDao iisDao = new IISConfigDao();
				try {
					list = iisDao.loadAll();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					iisDao.close();
				}
				if (list != null && list.size() > 0) {
					IISConfig iis = null;
					for (int i = 0; i < list.size(); i++) {
						iis = (IISConfig) list.get(i);
						ip = iis.getIpaddress();
						allipstr = SysUtil.doip(ip);
						sql = "";
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							// 删除iispinghour 365天前的数据
							sql = "delete from iispinghour" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>365)";
							stmt.addBatch(sql);
							// 删除iispingday 1000天前的数据
							sql = "delete FROM iispingday" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>1000)";
							stmt.addBatch(sql);

							sql = "delete from iisping" + allipstr + " where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>31)";
							stmt.addBatch(sql);
							sql = "delete from iisconn" + allipstr + " where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>31)";
							stmt.addBatch(sql);
							sql = "delete from iiserr" + allipstr + " where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>31)";
							stmt.addBatch(sql);

						} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
							// 删除iispinghour 365天前的数据
							sql = "delete from iispinghour" + allipstr + "  where  (SYSDATE-collecttime>365)";
							stmt.addBatch(sql);
							// 删除iispingday 1000天前的数据
							sql = "delete FROM iispingday" + allipstr + "  where  (SYSDATE-collecttime>1000)";
							stmt.addBatch(sql);

							sql = "delete from iisping" + allipstr + " where  (SYSDATE-collecttime>31)";
							stmt.addBatch(sql);
							sql = "delete from iisconn" + allipstr + " where  (SYSDATE-collecttime>31)";
							stmt.addBatch(sql);
							sql = "delete from iiserr" + allipstr + " where  (SYSDATE-collecttime>31)";
							stmt.addBatch(sql);

						} else if ("dm".equalsIgnoreCase(SystemConstant.DBType)) {
							// 删除iispinghour 365天前的数据
							sql = "delete from iispinghour" + allipstr + "  where  (SYSDATE-collecttime>365)";
							stmt.addBatch(sql);
							// 删除iispingday 1000天前的数据
							sql = "delete FROM iispingday" + allipstr + "  where  (SYSDATE-collecttime>1000)";
							stmt.addBatch(sql);

							sql = "delete from iisping" + allipstr + " where  (SYSDATE-collecttime>31)";
							stmt.addBatch(sql);
							sql = "delete from iisconn" + allipstr + " where  (SYSDATE-collecttime>31)";
							stmt.addBatch(sql);
							sql = "delete from iiserr" + allipstr + " where  (SYSDATE-collecttime>31)";
							stmt.addBatch(sql);

						}
					}
				}

				list = new ArrayList();
				WeblogicConfigDao weblogicDao = new WeblogicConfigDao();
				try {
					list = weblogicDao.loadAll();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					weblogicDao.close();
				}
				if (list != null && list.size() > 0) {
					WeblogicConfig weblogic = null;
					for (int i = 0; i < list.size(); i++) {
						weblogic = (WeblogicConfig) list.get(i);
						ip = weblogic.getIpAddress();
						allipstr = SysUtil.doip(ip);
						sql = "";
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							// 删除weblgicphour 365天前的数据
							sql = "delete from weblgicphour" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>365)";
							stmt.addBatch(sql);
							// 删除weblgicpday 1000天前的数据
							sql = "delete FROM weblgicpday" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>1000)";
							stmt.addBatch(sql);

							sql = "delete from weblogicping" + allipstr + " where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>31)";
							stmt.addBatch(sql);

						} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
							// 删除weblgicphour 365天前的数据
							sql = "delete from weblgicphour" + allipstr + "  where  (SYSDATE-collecttime>365)";
							stmt.addBatch(sql);
							// 删除weblgicpday 1000天前的数据
							sql = "delete FROM weblgicpday" + allipstr + "  where  (SYSDATE-collecttime>1000)";
							stmt.addBatch(sql);

							sql = "delete from weblogicping" + allipstr + " where  (SYSDATE-collecttime>31)";
							stmt.addBatch(sql);

						} else if ("dm".equalsIgnoreCase(SystemConstant.DBType)) {
							// 删除weblgicphour 365天前的数据
							sql = "delete from weblgicphour" + allipstr + "  where  (SYSDATE-collecttime>365)";
							stmt.addBatch(sql);
							// 删除weblgicpday 1000天前的数据
							sql = "delete FROM weblgicpday" + allipstr + "  where  (SYSDATE-collecttime>1000)";
							stmt.addBatch(sql);

							sql = "delete from weblogicping" + allipstr + " where  (SYSDATE-collecttime>31)";
							stmt.addBatch(sql);

						}
					}
				}
				// resin
				ResinDao resindao = new ResinDao();
				try {
					list = resindao.loadAll();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					resindao.close();
				}
				if (list != null && list.size() > 0) {
					Resin resin = null;
					for (int i = 0; i < list.size(); i++) {
						resin = (Resin) list.get(i);
						ip = resin.getIpAddress();
						allipstr = SysUtil.doip(ip);
						sql = "";
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							// 删除weblgic 31天前的数据
							sql = "delete from resinping" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>31)";
							stmt.addBatch(sql);
							// 删除weblgicphour 365天前的数据

							sql = "delete FROM resinpingh" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>365)";
							stmt.addBatch(sql);
							// 删除weblgicpday 1000天前的数据
							sql = "delete FROM resinpingd" + allipstr + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>1000)";
							stmt.addBatch(sql);
							sql = "delete from resin_mem" + allipstr + " where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>31)";
							stmt.addBatch(sql);

						} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
							// 删除weblgicphour 365天前的数据
							sql = "delete from resinping" + allipstr + "  where  (SYSDATE-collecttime>365)";
							stmt.addBatch(sql);
							// 删除weblgicpday 1000天前的数据
							sql = "delete FROM resinpingh" + allipstr + "   where  (SYSDATE-collecttime>1000)";
							stmt.addBatch(sql);

							sql = "delete FROM resinpingd" + allipstr + "  where  (SYSDATE-collecttime>31)";
							stmt.addBatch(sql);

							sql = "delete from resin_mem" + allipstr + " where   (SYSDATE-collecttime>31)";
							stmt.addBatch(sql);
						} else if ("dm".equalsIgnoreCase(SystemConstant.DBType)) {
							// 删除weblgicphour 365天前的数据
							sql = "delete from resinping" + allipstr + "  where  (SYSDATE-collecttime>365)";
							stmt.addBatch(sql);
							// 删除weblgicpday 1000天前的数据
							sql = "delete FROM resinpingh" + allipstr + "   where  (SYSDATE-collecttime>1000)";
							stmt.addBatch(sql);

							sql = "delete FROM resinpingd" + allipstr + "  where  (SYSDATE-collecttime>31)";
							stmt.addBatch(sql);

							sql = "delete from resin_mem" + allipstr + " where   (SYSDATE-collecttime>31)";
							stmt.addBatch(sql);

						}
					}
				}
				DBDao dbdao = null;
				try {
					dbdao = new DBDao();
					list = dbdao.loadAll();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dbdao.close();
				}
				if (list != null && list.size() > 0) {
					DBVo vo = null;
					for (int i = 0; i < list.size(); i++) {
						vo = (DBVo) list.get(i);
						if (vo != null) {
							int id = vo.getId();

							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "delete from oraping" + id + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>31)";
								stmt.addBatch(sql);
								sql = "delete from orapinghour" + id + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>365)";
								stmt.addBatch(sql);
								sql = "delete from orapingday" + id + "  where  (TO_DAYS(NOW())-TO_DAYS(collecttime)>1000)";
								stmt.addBatch(sql);
							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "delete from oraping" + id + "  where  (SYSDATE-collecttime>31)";
								stmt.addBatch(sql);
								sql = "delete from orapinghour" + id + "  where  (SYSDATE-collecttime>365)";
								stmt.addBatch(sql);
								sql = "delete from orapingday" + id + "  where  (SYSDATE-collecttime>1000)";
								stmt.addBatch(sql);
							} else if ("dm".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "delete from oraping" + id + "  where  (SYSDATE-collecttime>31)";
								stmt.addBatch(sql);
								sql = "delete from orapinghour" + id + "  where  (SYSDATE-collecttime>365)";
								stmt.addBatch(sql);
								sql = "delete from orapingday" + id + "  where  (SYSDATE-collecttime>1000)";
								stmt.addBatch(sql);
							}
							try {
								stmt.addBatch(sql);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}
				}
				try {
					stmt.executeBatch();
				} catch (Exception e) {
					e.printStackTrace();
				}
				conn.commit();
			} catch (Exception ex) {
				return false;
			} finally {
				try {
					if (rs != null)
						rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					DataGate.freeCon(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else
			// {
			ShareData.setCount(1);
		// }

		return true;
	}

	private String dofloat(String num) {
		String snum = "0.0";
		if (num != null) {
			int inum = (int) (Float.parseFloat(num) * 100);
			snum = Double.toString(inum / 100.0);
		}
		return snum;
	}

	public String[][] getdayHis(String ip, String category, String entity, String subentity, String year, String month) throws Exception {
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		String[][] returnVal = null;

		try {
			String allipstr = SysUtil.doip(ip);

			DateInformation di = new DateInformation();
			int days = di.getIntervalInOneMonth(Integer.parseInt(year), Integer.parseInt(month));
			String starttime = di.getFirstDayOfMonth(year, month) + " 00:00:00";
			String totime = di.getLastDayOfMonth(year, month) + " 23:59:59";
			returnVal = new String[2][days];
			for (int i = 0; i < days; i++) {
				returnVal[0][i] = String.valueOf(i + 1);
				returnVal[1][i] = "0";
			}
			String consql = "";
			String entity1 = entity;
			String subentity1 = subentity;
			String tablename = "";
			if (entity.equalsIgnoreCase("utilization")) {
				entity1 = "Utilization";
			} else if (entity.equalsIgnoreCase("ResponseTime")) {
				entity1 = "ResponseTime";
			}
			if (subentity.equalsIgnoreCase("utilization")) {
				subentity1 = "Utilization";
			} else if (subentity.equalsIgnoreCase("ResponseTime")) {
				subentity1 = "ResponseTime";
			}
			if (category.equalsIgnoreCase("CPU")) {
				consql = " and a.category='" + category + "' ";
				tablename = "cpuday";
			}
			if (category.equalsIgnoreCase("Ping")) {
				consql = " and a.category='" + category + "' and a.entity='" + entity1 + "' ";
				tablename = "pingday";
			}
			if (category.equalsIgnoreCase("Process")) {
				consql = " and a.category='" + category + "' and a.subentity='" + subentity1 + "' ";
				tablename = "proday";
			}

			// session = this.beginTransaction();
			String sql = "select DATE_FORMAT(a.collecttime,'%Y-%m-%d %H:%i:%s') as colltime,a.thevalue from " + tablename + allipstr + " a " + "where 1=1 " + consql + " and a.collecttime >=DATE_FORMAT('" + starttime + "','%Y-%m-%d %H:%i:%s') and a.collecttime <=DATE_FORMAT('" + totime + "','%Y-%m-%d %H:%i:%s') " + " order by a.collecttime ";
			List list = new ArrayList();
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				Vector v = new Vector();
				v.add(0, rs.getString("thevalue"));
				v.add(1, rs.getString("colltime"));
				list.add(v);
			}
			rs.close();

			for (int i = 0; i < list.size(); i++) {
				Vector row = (Vector) list.get(i);
				String time = (String) row.get(1);
				String day = time.substring(8, 10);
				String num = dofloat((String) row.get(0));
				returnVal[1][Integer.parseInt(day) - 1] = num;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}
		return returnVal;
	}

	public Hashtable getdayHis1(String ip, String category, String entity, String subentity, String year, String month) throws Exception {
		Hashtable returnVal = null;
		ResultSet rs = null;
		DBManager dbmanager = new DBManager();
		try {
			String allipstr = SysUtil.doip(ip);

			DateInformation di = new DateInformation();
			String starttime = di.getFirstDayOfMonth(year, month) + " 00:00:00";
			String totime = di.getLastDayOfMonth(year, month) + " 23:59:59";
			returnVal = new Hashtable();
			String consql = "";
			String entity1 = entity;
			String subentity1 = subentity;
			String tablename = "";
			if (entity.equalsIgnoreCase("utilization")) {
				entity1 = "Utilization";
			} else if (entity.equalsIgnoreCase("ResponseTime")) {
				entity1 = "ResponseTime";
			}
			if (subentity.equalsIgnoreCase("utilization")) {
				subentity1 = "Utilization";
			} else if (subentity.equalsIgnoreCase("ResponseTime")) {
				subentity1 = "ResponseTime";
			}
			if (category.equalsIgnoreCase("CPU")) {
				consql = " and a.category='" + category + "' ";
				tablename = "cpuday";
			}
			if (category.equalsIgnoreCase("Ping")) {
				consql = " and a.category='" + category + "' and a.entity='" + entity1 + "' ";
				tablename = "pingday";
			}
			if (category.equalsIgnoreCase("ORAPing")) {
				consql = " and a.category='" + category + "' and a.entity='" + entity1 + "' ";
				tablename = "orapingday";
			}
			if (category.equalsIgnoreCase("DB2Ping")) {
				consql = " and a.category='" + category + "' and a.entity='" + entity1 + "' ";
				tablename = "db2pingday";
			}
			if (category.equalsIgnoreCase("SYSPing")) {
				consql = " and a.category='" + category + "' and a.entity='" + entity1 + "' ";
				tablename = "syspingday";
			}
			if (category.equalsIgnoreCase("SQLPing")) {
				consql = " and a.category='" + category + "' and a.entity='" + entity1 + "' ";
				tablename = "sqlpingday";
			}

			if (category.equalsIgnoreCase("Process")) {
				consql = " and a.category='" + category + "' and a.subentity='" + subentity1 + "' ";
				tablename = "proday";
			}

			String sql = "select DATE_FORMAT(a.collecttime,'%Y-%m-%d %H:%i:%s') as colltime,a.thevalue from " + tablename + allipstr + " a " + "where 1=1 " + consql + " and a.collecttime >=DATE_FORMAT('" + starttime + "','%Y-%m-%d %H:%i:%s') and a.collecttime <=DATE_FORMAT('" + totime + "','%Y-%m-%d %H:%i:%s') " + " order by a.collecttime ";
			List list = new ArrayList();
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				Vector v = new Vector();
				v.add(0, rs.getString("thevalue"));
				v.add(1, rs.getString("colltime"));
				list.add(v);
			}
			rs.close();
			returnVal.put("list", list);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}
		return returnVal;
	}

	public Hashtable getmultiHis(String ip, String category, String year, String month) throws Exception {
		Hashtable hash = new Hashtable();
		ResultSet rs = null;
		DBManager dbmanager = new DBManager();
		try {
			String allipstr = SysUtil.doip(ip);
			String tablename = "";
			if (category.equals("Memory")) {
				tablename = "memoryday";
			} else if (category.equals("Disk")) {
				tablename = "diskday";
			}
			DateInformation di = new DateInformation();
			String starttime = di.getFirstDayOfMonth(year, month) + " 00:00:00";
			String totime = di.getLastDayOfMonth(year, month) + " 23:59:59";
			String sql1 = "select distinct h.subentity from " + tablename + allipstr + " h ";
			List list1 = new ArrayList();
			rs = dbmanager.executeQuery(sql1);
			while (rs.next()) {
				Vector v = new Vector();
				v.add(0, rs.getString("subentity"));
				list1.add(v);
			}
			rs.close();

			if (list1.size() != 0) {
				int size = list1.size();
				String[] key = new String[list1.size()];
				Vector[] vector = new Vector[key.length];
				for (int i = 0; i < size; i++) {
					vector[i] = new Vector();
					Vector row = ((Vector) list1.get(i));
					key[i] = (String) row.get(0);
				}
				String sql = "";
				StringBuffer sb = new StringBuffer();
				sb.append("select DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as colltime,h.thevalue,h.subentity from " + tablename + allipstr + " h where ");
				sb.append(" h.collecttime >=DATE_FORMAT('");
				sb.append(starttime);
				sb.append("','%Y-%m-%d %H:%i:%s') and h.collecttime <=DATE_FORMAT('");
				sb.append(totime);
				sb.append("','%Y-%m-%d %H:%i:%s') order by h.collecttime");
				sql = sb.toString();
				System.out.println("sql=" + sql);
				List list2 = new ArrayList();
				rs = dbmanager.executeQuery(sql);
				while (rs.next()) {
					Vector v = new Vector();
					v.add(0, rs.getString("thevalue"));
					v.add(1, rs.getString("colltime"));
					v.add(2, rs.getString("subentity"));
					list2.add(v);
				}
				rs.close();

				for (int k = 0; k < list2.size(); k++) {
					Vector obj = (Vector) list2.get(k);
					for (int i = 0; i < key.length; i++) {
						if (((String) obj.get(2)).equalsIgnoreCase(key[i])) {
							vector[i].add(obj);
							break;
						}
					}
				}
				for (int i = 0; i < size; i++) {
					hash.put(key[i], vector[i]);
				}
				hash.put("key", key);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}
		return hash;
	}

	public Hashtable getmultiHis(String ip, String category, String subentity, String[] bandkey, String[] bandch, String year, String month) throws Exception {
		Hashtable hash = new Hashtable();
		return hash;
	}

	public Hashtable getmultiHisMonth(String ip, String category, String subentity, String[] bandkey, String[] bandch, String year, String month, String tablename) throws Exception {
		Hashtable hash = new Hashtable();
		return hash;
	}

	public Hashtable getmultiHis(String ip, String category, String subentity, String[] bandkey, String[] bandch, String startyear) throws Exception {
		Hashtable hash = new Hashtable();
		return hash;
	}

	public Hashtable getmultiHisHdx(String ip, String category, String subentity, String[] bandkey, String[] bandch, String startyear, String tablename) throws Exception {
		Hashtable hash = new Hashtable();
		return hash;
	}

	public Hashtable getmultiHisHdxMonth(String ip, String category, String subentity, String[] bandkey, String[] bandch, String year, String month, String tablename) throws Exception {
		Hashtable hash = new Hashtable();
		return hash;
	}

	public Hashtable getmultiHisHdx(String ip, String category, String subentity, String[] bandkey, String[] bandch, String starttime, String totime, String tablename) throws Exception {
		Hashtable hash = new Hashtable();
		Hashtable hash3 = new Hashtable();
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		try {
			String allipstr = SysUtil.doip(ip);
			String starttime1 = starttime + " 00:00:00";
			String totime1 = totime + " 23:59:59";

			StringBuffer sb = new StringBuffer();

			int size = bandkey.length;
			String sql2 = "";
			if (category.indexOf("all") != -1) {
				sb.append(" and(");
				for (int j = 0; j < size; j++) {
					if (j != 0) {
						sb.append("or");
					}
					sb.append(" h.subentity='");
					sb.append(bandkey[j]);
					sb.append("' ");
				}
				sb.append(") ");

				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sql2 = "select DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as colltime,h.thevalue,h.entity from " + tablename + allipstr + " h where " + " h.category='Interface'" + sb.toString() + " and h.collecttime >='" + starttime1 + "' and h.collecttime <='" + totime1 + "' order by h.collecttime asc";
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sql2 = "select to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as colltime,h.thevalue,h.entity from " + tablename + allipstr + " h where " + " h.category='Interface'" + sb.toString() + " and h.collecttime >=to_date('" + starttime1 + "','YYYY-MM-DD HH24:MI:SS') and h.collecttime <=to_date('" + totime1 + "','YYYY-MM-DD HH24:MI:SS') order by h.collecttime asc";
				} else if ("dm".equalsIgnoreCase(SystemConstant.DBType)) {
					sql2 = "select to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as colltime,h.thevalue,h.entity from " + tablename + allipstr + " h where " + " h.category='Interface'" + sb.toString() + " and h.collecttime >=to_date('" + starttime1 + "','YYYY-MM-DD HH24:MI:SS') and h.collecttime <=to_date('" + totime1 + "','YYYY-MM-DD HH24:MI:SS') order by h.collecttime asc";
				}

			} else {
				sb.append(" and h.subentity='" + subentity + "' and (");
				for (int j = 0; j < size; j++) {
					if (j != 0) {
						sb.append("or");
					}
					sb.append(" h.entity='");
					sb.append(bandkey[j]);
					sb.append("' ");
				}
				sb.append(") ");

				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sql2 = "select DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as colltime,h.thevalue,h.entity from " + tablename + allipstr + " h where " + " 1=1 " + sb.toString() + " and h.collecttime >='" + starttime1 + "' and h.collecttime <='" + totime1 + "' order by h.collecttime asc";
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sql2 = "select to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as colltime,h.thevalue,h.entity from " + tablename + allipstr + " h where " + " 1=1 " + sb.toString() + " and h.collecttime >=to_date('" + starttime1 + "','YYYY-MM-DD HH24:MI:SS') and h.collecttime <=to_date('" + totime1 + "','YYYY-MM-DD HH24:MI:SS') order by h.collecttime asc";
				} else if ("dm".equalsIgnoreCase(SystemConstant.DBType)) {
					sql2 = "select to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as colltime,h.thevalue,h.entity from " + tablename + allipstr + " h where " + " 1=1 " + sb.toString() + " and h.collecttime >=to_date('" + starttime1 + "','YYYY-MM-DD HH24:MI:SS') and h.collecttime <=to_date('" + totime1 + "','YYYY-MM-DD HH24:MI:SS') order by h.collecttime asc";
				}

			}
			List list2 = new ArrayList();
			Vector[] vector = new Vector[bandkey.length];
			for (int k = 0; k < bandkey.length; k++) {
				vector[k] = new Vector();
			}
			rs = dbmanager.executeQuery(sql2);
			while (rs.next()) {
				Vector v = new Vector();
				String thevalue = rs.getString("thevalue");
				String collecttime = rs.getString("colltime");
				v.add(0, thevalue);
				v.add(1, collecttime);
				v.add(2, rs.getString("entity"));
				list2.add(v);
			}
			rs.close();
			int days = list2.size();
			String[][] value = new String[size][days];
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < days; j++) {
					value[i][j] = "0";
				}
			}
			for (int k = 0; k < list2.size(); k++) {
				Vector row = (Vector) list2.get(k);
				for (int i = 0; i < size; i++) {
					if (((String) row.get(2)).equals(bandkey[i])) {
						String time = (String) row.get(1);
						value[i][k] = time + "&" + (String) row.get(0);
						break;
					}
				}
			}
			for (int i = 0; i < size; i++) {
				hash.put(bandch[i], value[i]);
			}
			hash.put("key", bandch);
			hash.put("hash3", hash3);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return hash;
	}

	public Hashtable getmultiHisPerc(String ip, String category, String subentity, String[] bandkey, String[] bandch, String startyear, String tablename) throws Exception {
		Hashtable hash = new Hashtable();
		return hash;
	}

	public void createDir(String commonPath) {
		File dir = new File(commonPath);
		if (!dir.exists()) {// 检查Sub目录是否存在
			dir.mkdir();
		}
	}

	public Hashtable getmultiHisHdx(String ip, String subentity, String entity, String starttime, String endtime, String time) throws Exception {
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		try {
			if (!starttime.equals("") && !endtime.equals("")) {
				String allipstr = SysUtil.doip(ip);
				String sql = "";
				List list1 = new ArrayList();
				StringBuffer sb = new StringBuffer();
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from utilhdx" + time + allipstr + " h where ");
					sb.append(" h.SUBENTITY='");
					sb.append(subentity);
					sb.append("' and h.ENTITY= '");
					sb.append(entity);
					sb.append("' and h.collecttime >= '");
					sb.append(starttime);
					sb.append("' and h.collecttime <= '");
					sb.append(endtime);
					sb.append("' order by h.collecttime");
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from utilhdx" + time + allipstr + " h where ");
					sb.append(" h.SUBENTITY='");
					sb.append(subentity);
					sb.append("' and h.ENTITY= '");
					sb.append(entity);
					sb.append("' and h.collecttime >= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('" + endtime + "','YYYY-MM-DD HH24:MI:SS') order by h.collecttime");
				} else if ("dm".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from utilhdx" + time + allipstr + " h where ");
					sb.append(" h.SUBENTITY='");
					sb.append(subentity);
					sb.append("' and h.ENTITY= '");
					sb.append(entity);
					sb.append("' and h.collecttime >= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('" + endtime + "','YYYY-MM-DD HH24:MI:SS') order by h.collecttime");
				}

				sql = sb.toString();
				SysLogger.info(sql);
				rs = dbmanager.executeQuery(sql);
				int i = 0;
				double maxfloat = 0;
				double minfloat = 10000000;
				double tempfloat = 0;
				double avgput = 0;
				try {
					while (rs.next()) {
						i = i + 1;
						Vector v = new Vector();
						String thevalue = rs.getString("thevalue");
						String collecttime = rs.getString("collecttime");
						v.add(0, thevalue);
						v.add(1, collecttime);
						v.add(2, rs.getString("unit"));
						avgput = avgput + Double.parseDouble(thevalue);
						tempfloat = Double.parseDouble(thevalue);
						if (maxfloat < Double.parseDouble(thevalue))
							maxfloat = Double.parseDouble(thevalue);
						if (minfloat > Double.parseDouble(thevalue))
							minfloat = Double.parseDouble(thevalue);
						list1.add(v);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					dbmanager.close();
				}
				hash.put("list", list1);
				if (list1 != null && list1.size() > 0) {
					hash.put("avgput", CEIString.round(avgput / list1.size(), 2) + "");
				} else {
					hash.put("avgput", "0.0");
				}
				hash.put("max", CEIString.round(maxfloat, 2) + "");
				hash.put("min", CEIString.round(minfloat, 2) + "");
				hash.put("temp", CEIString.round(tempfloat, 2) + "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}
		dbmanager.close();
		return hash;
	}

	/**
	 * 用来统计网络设备指定接口网卡的入口平均速度、出口平均速度、入口峰值、出口峰值 并把值保持在Hashtable 中 key ： ipaddress
	 * ip地址 ifname 接口名称 linkuse 链路使用说明 agvout 出口平均流速 agvin 入口平均流速 maxout 出口峰值
	 * maxin 入口峰值
	 * 
	 * @param ip
	 *            ip地址
	 * @param subentity
	 *            //设备接口索引
	 * @param starttime
	 *            开始时间
	 * @param endtime
	 *            结束时间
	 * @param tablename
	 *            数据库表前缀
	 * @param ifnmae
	 *            接口名称
	 * @return 返回
	 * @throws Exception
	 * 
	 *             作者：konglq
	 */
	public Hashtable getmultiHisHdx_OA(String ip, String subentity, String starttime, String endtime, String tablename, String ifnmae, String linkuse) throws Exception {
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		try {
			if (!starttime.equals("") && !endtime.equals("")) {
				// String ip1 ="",ip2="",ip3="",ip4="";
				// String tempStr = "";
				// String allipstr = "";
				// if (ip.indexOf(".")>0) {
				// ip1=ip.substring(0,ip.indexOf("."));
				// ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());
				// tempStr =
				// ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
				// }
				// ip2=tempStr.substring(0,tempStr.indexOf("."));
				// ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
				// allipstr=ip1+ip2+ip3+ip4;
				String allipstr = SysUtil.doip(ip);

				String sqlavgout = "";
				String sqlagtint = "";
				String maxout = "";
				String maxint = "";
				String sqlavgcpu = "";
				String sqlavgmemory = "";
				String sqlpingavg = "";
				String sqlresponseavg = "";

				StringBuffer sb = new StringBuffer();

				// 拼写查询入口平均流速sql
				sb.append("select ROUND(avg(h.thevalue),2) as InBand from " + tablename + allipstr + " h where ");
				sb.append(" h.SUBENTITY='");
				sb.append(subentity);
				sb.append("' and h.ENTITY= '");
				sb.append("InBandwidthUtilHdx");
				sb.append("' and h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");
				sb.append(endtime).append("  23:59:59'");
				// sb.append("' order by h.collecttime");
				sqlagtint = sb.toString();

				sb.delete(0, sb.length());// 清空
				// 拼写查询出口平均流速sql
				sb.append("select ROUND(avg(h.thevalue),2) as OutBand  from " + tablename + allipstr + " h where ");
				sb.append(" h.SUBENTITY='");
				sb.append(subentity);
				sb.append("' and h.ENTITY= '");
				sb.append("OutBandwidthUtilHdx");
				sb.append("' and h.collecttime >= '");
				sb.append(starttime).append(" 00:00:00");
				sb.append("' and h.collecttime <= '");
				sb.append(endtime).append("  23:59:59'");
				// sb.append("' order by h.collecttime");
				sqlavgout = sb.toString();

				sb.delete(0, sb.length());// 清空
				// 拼写查询出口峰值sql
				sb.append("select Max(h.thevalue) as maxout from " + tablename + allipstr + " h where ");
				sb.append(" h.SUBENTITY='");
				sb.append(subentity);
				sb.append("' and h.ENTITY= '");
				sb.append("OutBandwidthUtilHdx");
				sb.append("' and h.collecttime >= '");
				sb.append(starttime).append(" 00:00:00");
				sb.append("' and h.collecttime <= '");
				sb.append(endtime).append("  23:59:59'");
				// sb.append("' order by h.collecttime");
				maxout = sb.toString();

				sb.delete(0, sb.length());// 清空
				// 拼写查询入口峰值sql
				sb.append("select  Max(h.thevalue) as maxin from " + tablename + allipstr + " h where ");
				sb.append(" h.SUBENTITY='");
				sb.append(subentity);
				sb.append("' and h.ENTITY= '");
				sb.append("InBandwidthUtilHdx");
				sb.append("' and h.collecttime >= '");
				sb.append(starttime).append(" 00:00:00");
				sb.append("' and h.collecttime <= '");
				sb.append(endtime).append("  23:59:59'");
				// sb.append("' order by h.collecttime");
				maxint = sb.toString();

				// 统计平均cpu使用率
				sb.delete(0, sb.length());// 清空
				sb.append("select  ROUND(avg(h.thevalue),2) as avgcpu from cpu" + allipstr + " h where ");
				sb.append(" h.collecttime >= '");
				sb.append(starttime).append(" 00:00:00");
				sb.append("' and h.collecttime <= '");
				sb.append(endtime).append("  23:59:59'");
				// sb.append("' order by h.collecttime");
				sqlavgcpu = sb.toString();

				// 统计平均内存使用率
				sb.delete(0, sb.length());// 清空
				sb.append("select  ROUND(avg(h.thevalue),2) as avgmemory from memory" + allipstr + " h where ");
				sb.append(" h.collecttime >= '");
				sb.append(starttime).append(" 00:00:00");
				sb.append("' and h.collecttime <= '");
				sb.append(endtime).append("  23:59:59'");
				// sb.append("' order by h.collecttime");
				sqlavgmemory = sb.toString();

				// 统计平均连通率
				sb.delete(0, sb.length());// 清空
				sb.append("select  ROUND(avg(h.thevalue),2) as avgping from ping" + allipstr + " h where ");
				sb.append(" h.SUBENTITY='").append("ConnectUtilization'");
				sb.append("and h.collecttime >= '");
				sb.append(starttime).append(" 00:00:00");
				sb.append("' and h.collecttime <= '");
				sb.append(endtime).append("  23:59:59'");
				// sb.append("' order by h.collecttime");
				sqlpingavg = sb.toString();

				// 统计平均响应时间
				sb.delete(0, sb.length());// 清空
				sb.append("select  ROUND(avg(h.thevalue),2) as avgresponse from ping" + allipstr + " h where ");
				sb.append(" h.SUBENTITY='").append("ResponseTime'");
				sb.append("and h.collecttime >= '");
				sb.append(starttime).append(" 00:00:00");
				sb.append("' and h.collecttime <= '");
				sb.append(endtime).append("  23:59:59'");
				// sb.append("' order by h.collecttime");
				sqlresponseavg = sb.toString();
				// sb=null;
				// SysLogger.info(sql);

				hash.put("ipaddress", ip);// 添加ip
				hash.put("ifname", ifnmae);// 添加接口名称
				hash.put("linkuse", linkuse);// 链路使用
				try {
					// 下面的取值可能有错误，很久没有写了
					rs = dbmanager.executeQuery(sqlagtint);
					while (rs.next()) {
						hash.put("agvin", rs.getFloat("InBand"));
					}
					rs = dbmanager.executeQuery(sqlavgout);
					while (rs.next()) {
						hash.put("agvout", rs.getFloat("OutBand"));
					}

					rs = dbmanager.executeQuery(maxout);
					while (rs.next()) {
						hash.put("maxout", rs.getInt("maxout"));
					}
					rs = dbmanager.executeQuery(maxint);
					while (rs.next()) {
						hash.put("maxin", rs.getInt("maxin"));
					}

					rs = dbmanager.executeQuery(sqlavgcpu);
					while (rs.next()) {
						hash.put("avgcpu", rs.getInt("avgcpu"));
					}

					rs = dbmanager.executeQuery(sqlavgmemory);
					while (rs.next()) {
						hash.put("avgmemory", rs.getInt("avgmemory"));
					}

					rs = dbmanager.executeQuery(sqlpingavg);
					while (rs.next()) {
						hash.put("avgping", rs.getInt("avgping"));
					}

					rs = dbmanager.executeQuery(sqlresponseavg);
					while (rs.next()) {
						hash.put("avgresponse", rs.getInt("avgresponse"));
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						rs.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}
		dbmanager.close();
		return hash;
	}

	/**
	 * 用来统计网络设备综合 入口平均速度、出口平均速度、入口峰值、出口峰值 并把值保持在Hashtable 中 key ： ipaddress ip地址
	 * agvout 出口平均流速 agvin 入口平均流速 maxout 出口峰值 maxin 入口峰值
	 * 
	 * @param ip
	 *            ip地址
	 * @param starttime
	 *            开始时间
	 * @param endtime
	 *            结束时间
	 * @return 返回
	 * @throws Exception
	 * 
	 *             作者：konglq
	 */
	public Hashtable getAllAvgAndMaxHisHdx(String ip, String starttime, String endtime) throws Exception {
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		try {
			if (!starttime.equals("") && !endtime.equals("")) {
				String allipstr = SysUtil.doip(ip);

				String sqlavgout = "";
				String sqlagtint = "";

				StringBuffer sb = new StringBuffer();

				// 拼写查询入口平均流速sql
				sb.append("select ROUND(avg(h.thevalue)) as InBand,MAX(h.thevalue) as maxin,MIN(h.thevalue) as minin from allutilhdx" + allipstr + " h where ");
				sb.append(" h.SUBENTITY='");
				sb.append("AllInBandwidthUtilHdx");
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("' and h.collecttime >= '");
					sb.append(starttime);
					sb.append("' and h.collecttime <= '");
					sb.append(endtime).append("'");
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("' and h.collecttime >= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('" + endtime + "','YYYY-MM-DD HH24:MI:SS')");
				} else if ("dm".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("' and h.collecttime >= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('" + endtime + "','YYYY-MM-DD HH24:MI:SS')");
				}

				sqlagtint = sb.toString();
				if ("dm".equalsIgnoreCase(SystemConstant.DBType)) {
					// 先转换成数值，不然直接对字符串做比较操作
					sqlagtint = sqlagtint.replaceAll("h.thevalue", "cast(h.thevalue as long)");
				}

				sb.delete(0, sb.length());// 清空
				// 拼写查询出口平均流速sql
				sb.append("select ROUND(avg(h.thevalue)) as OutBand ,MAX(h.thevalue) as maxout,MIN(h.thevalue) as minout from allutilhdx" + allipstr + " h where ");
				sb.append(" h.SUBENTITY='");
				sb.append("AllOutBandwidthUtilHdx");
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("' and h.collecttime >= '");
					sb.append(starttime);
					sb.append("' and h.collecttime <= '");
					sb.append(endtime).append("'");
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("' and h.collecttime >= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('" + endtime + "','YYYY-MM-DD HH24:MI:SS')");
				} else if ("dm".equalsIgnoreCase(SystemConstant.DBType)) {
					sb.append("' and h.collecttime >= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('" + endtime + "','YYYY-MM-DD HH24:MI:SS')");
				}
				sqlavgout = sb.toString();
				if ("dm".equalsIgnoreCase(SystemConstant.DBType)) {
					// 先转换成数值，不然直接对字符串做比较操作
					sqlavgout = sqlavgout.replaceAll("h.thevalue", "cast(h.thevalue as long)");
				}
				hash.put("ipaddress", ip);// 添加ip
				try {
					// 下面的取值可能有错误，很久没有写了
					rs = dbmanager.executeQuery(sqlagtint);
					while (rs.next()) {
						// 直接getInt达梦数据库抛异常
						hash.put("agvin", Integer.parseInt(rs.getString("InBand")));
						hash.put("maxin", Integer.parseInt(rs.getString("maxin")));
						hash.put("minin", Integer.parseInt(rs.getString("minin")));
					}
					rs = dbmanager.executeQuery(sqlavgout);
					while (rs.next()) {
						hash.put("agvout", Integer.parseInt(rs.getString("OutBand")));
						hash.put("maxout", Integer.parseInt(rs.getString("maxout")));
						hash.put("minout", Integer.parseInt(rs.getString("minout")));
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						rs.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}
		dbmanager.close();
		return hash;
	}

	public int deleteDayTask() throws Exception {
		return 0;
	}

	public Hashtable getmultiHis(String ip, String category, String subentity, String[] bandkey, String[] bandch, String starttime, String totime, String tablename) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
