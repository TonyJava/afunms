/*
 * Created on 2005-4-8
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.polling.impl;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.application.dao.DBDao;
import com.afunms.application.model.DBVo;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectDataHour;
import com.afunms.system.model.User;
import com.afunms.topology.model.HostNode;
import com.afunms.util.DataGate;
import com.database.config.SystemConfig;

@SuppressWarnings("unchecked")
public class HostCollectDataHourManager extends BaseManager implements I_HostCollectDataHour {

	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public HostCollectDataHourManager() {
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
			vo.setSupperid(rs.getInt("supperid"));// snow add at 2010-05-18
			// SNMP V3
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

	public boolean schemeTask() {
		// 归并一小时前的数据
		// 第一次启动服务器的时候，不归档数据
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
					String sql = "";
					for (int i = 0; i < list.size(); i++) {
						HostNode equipment = (HostNode) list.get(i);
						// 测试生成表
						String ip = equipment.getIpAddress();
						String allipstr = SysUtil.doip(ip);
						if ((equipment.getCategory() > 0 && equipment.getCategory() < 4) || equipment.getCategory() == 7) {
							// 生成网络设备表
							// Ping
							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into pinghour"
										+ allipstr
										+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) "
										+ "SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) "
										+ "FROM ping"
										+ allipstr
										+ " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP,'%H') - DATE_FORMAT(hostcollectdata.collecttime,'%H')=1) and (DATE_FORMAT(CURRENT_TIMESTAMP,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into pinghour"
										+ allipstr
										+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM ping"
										+ allipstr
										+ " hostcollectdata where (to_char(SYSDATE,'hh24') - to_char(hostcollectdata.collecttime,'hh24')=1) and (to_char(SYSDATE,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
							}

							try {
								stmt.addBatch(sql);
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (equipment.getCollecttype() != SystemConstant.COLLECTTYPE_PING && equipment.getCollecttype() != SystemConstant.COLLECTTYPE_SSHCONNECT
									&& equipment.getCollecttype() != SystemConstant.COLLECTTYPE_TELNETCONNECT) {
								// 只PING操作的时候,只有三张表需要汇总,其他情况需要汇总如下的表格
								// Memory
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into memoryhour"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) "
											+ "SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) "
											+ "FROM memory"
											+ allipstr
											+ " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP,'%H') - DATE_FORMAT(hostcollectdata.collecttime,'%H')=1) and (DATE_FORMAT(CURRENT_TIMESTAMP,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into memoryhour"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM memory"
											+ allipstr
											+ " hostcollectdata where (to_char(SYSDATE,'hh24') - to_char(hostcollectdata.collecttime,'hh24')=1) and (to_char(SYSDATE,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
								}
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}
								// CPU
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into cpuhour"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM cpu"
											+ allipstr
											+ " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP,'%H') - DATE_FORMAT(hostcollectdata.collecttime,'%H')=1) and (DATE_FORMAT(CURRENT_TIMESTAMP,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into cpuhour"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM cpu"
											+ allipstr
											+ " hostcollectdata where (to_char(SYSDATE,'hh24') - to_char(hostcollectdata.collecttime,'hh24')=1) and (to_char(SYSDATE,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
								}
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								// UtilHdx 按小时归档到HOSTUTILHDXHOUR表里
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into utilhdxhour"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM utilhdx"
											+ allipstr
											+ " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP,'%H') - DATE_FORMAT(hostcollectdata.collecttime,'%H')=1) and (DATE_FORMAT(CURRENT_TIMESTAMP,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into utilhdxhour"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(to_number(hostcollectdata.thevalue)) FROM utilhdx"
											+ allipstr
											+ " hostcollectdata where (to_char(SYSDATE,'hh24') - to_char(hostcollectdata.collecttime,'hh24')=1) and (to_char(SYSDATE,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
								}
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}
								// AllUtilHdx 按小时归档到HOSTALLUTILHDXHOUR表里
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into autilhdxh"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM allutilhdx"
											+ allipstr
											+ " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP,'%H') - DATE_FORMAT(hostcollectdata.collecttime,'%H')=1) and (DATE_FORMAT(CURRENT_TIMESTAMP,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into autilhdxh"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(to_number(hostcollectdata.thevalue)) FROM allutilhdx"
											+ allipstr
											+ " hostcollectdata where (to_char(SYSDATE,'hh24') - to_char(hostcollectdata.collecttime,'hh24')=1) and (to_char(SYSDATE,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
								}
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}
								// UtilHdxPerc 按小时归档到HOSTUTILHDXPERCHOUR表里
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into hdxperchour"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM utilhdxperc"
											+ allipstr
											+ " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP,'%H') - DATE_FORMAT(hostcollectdata.collecttime,'%H')=1) and (DATE_FORMAT(CURRENT_TIMESTAMP,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into hdxperchour"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM utilhdxperc"
											+ allipstr
											+ " hostcollectdata where (to_char(SYSDATE,'hh24') - to_char(hostcollectdata.collecttime,'hh24')=1) and (to_char(SYSDATE,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
								}
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								// DiscardsPerc
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into dcarperh"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM discardsperc"
											+ allipstr
											+ " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP,'%H') - DATE_FORMAT(hostcollectdata.collecttime,'%H')=1) and (DATE_FORMAT(CURRENT_TIMESTAMP,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into dcarperh"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM discardsperc"
											+ allipstr
											+ " hostcollectdata where (to_char(SYSDATE,'hh24') - to_char(hostcollectdata.collecttime,'hh24')=1) and (to_char(SYSDATE,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
								}
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}
								// ErrorsPerc
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into errperch"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM errorsperc"
											+ allipstr
											+ " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP,'%H') - DATE_FORMAT(hostcollectdata.collecttime,'%H')=1) and (DATE_FORMAT(CURRENT_TIMESTAMP,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into errperch"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM errorsperc"
											+ allipstr
											+ " hostcollectdata where (to_char(SYSDATE,'hh24') - to_char(hostcollectdata.collecttime,'hh24')=1) and (to_char(SYSDATE,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
								}
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}
								// Packs
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into packshour"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM packs"
											+ allipstr
											+ " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP,'%H') - DATE_FORMAT(hostcollectdata.collecttime,'%H')=1) and (DATE_FORMAT(CURRENT_TIMESTAMP,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into packshour"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM packs"
											+ allipstr
											+ " hostcollectdata where (to_char(SYSDATE,'hh24') - to_char(hostcollectdata.collecttime,'hh24')=1) and (to_char(SYSDATE,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
								}
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}
								// InPacks
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into ipacksh"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM inpacks"
											+ allipstr
											+ " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP,'%H') - DATE_FORMAT(hostcollectdata.collecttime,'%H')=1) and (DATE_FORMAT(CURRENT_TIMESTAMP,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into ipacksh"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM inpacks"
											+ allipstr
											+ " hostcollectdata where (to_char(SYSDATE,'hh24') - to_char(hostcollectdata.collecttime,'hh24')=1) and (to_char(SYSDATE,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
								}
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}
								// InPacks
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into opackh"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM outpacks"
											+ allipstr
											+ " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP,'%H') - DATE_FORMAT(hostcollectdata.collecttime,'%H')=1) and (DATE_FORMAT(CURRENT_TIMESTAMP,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into opackh"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM outpacks"
											+ allipstr
											+ " hostcollectdata where (to_char(SYSDATE,'hh24') - to_char(hostcollectdata.collecttime,'hh24')=1) and (to_char(SYSDATE,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
								}
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}
								// Temper
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into temperh"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM temper"
											+ allipstr
											+ " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP,'%H') - DATE_FORMAT(hostcollectdata.collecttime,'%H')=1) and (DATE_FORMAT(CURRENT_TIMESTAMP,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into temperh"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM temper"
											+ allipstr
											+ " hostcollectdata where (to_char(SYSDATE,'hh24') - to_char(hostcollectdata.collecttime,'hh24')=1) and (to_char(SYSDATE,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
								}
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

						} else if (equipment.getCategory() == 4) {
							// 汇总主机表
							// Ping
							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into pinghour"
										+ allipstr
										+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM ping"
										+ allipstr
										+ " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP,'%H') - DATE_FORMAT(hostcollectdata.collecttime,'%H')=1) and (DATE_FORMAT(CURRENT_TIMESTAMP,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sql = "insert into pinghour"
										+ allipstr
										+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(to_number(hostcollectdata.thevalue)) FROM ping"
										+ allipstr
										+ " hostcollectdata where (to_char(SYSDATE,'hh24') - to_char(hostcollectdata.collecttime,'hh24')=1) and (to_char(SYSDATE,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

							}
							try {
								stmt.addBatch(sql);
							} catch (Exception e) {
								e.printStackTrace();
							}

							if (equipment.getCollecttype() != SystemConstant.COLLECTTYPE_PING && equipment.getCollecttype() != SystemConstant.COLLECTTYPE_SSHCONNECT
									&& equipment.getCollecttype() != SystemConstant.COLLECTTYPE_TELNETCONNECT) {
								// 只PING操作的时候,只有三张表需要汇总,其他情况需要汇总如下的表格
								// Memory
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into memoryhour"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM memory"
											+ allipstr
											+ " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP,'%H') - DATE_FORMAT(hostcollectdata.collecttime,'%H')=1) and (DATE_FORMAT(CURRENT_TIMESTAMP,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into memoryhour"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(to_number(hostcollectdata.thevalue)) FROM memory"
											+ allipstr
											+ " hostcollectdata where (to_char(SYSDATE,'hh24') - to_char(hostcollectdata.collecttime,'hh24')=1) and (to_char(SYSDATE,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
								}
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}
								// CPU
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into cpuhour"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM cpu"
											+ allipstr
											+ " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP,'%H') - DATE_FORMAT(hostcollectdata.collecttime,'%H')=1) and (DATE_FORMAT(CURRENT_TIMESTAMP,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into cpuhour"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(to_number(hostcollectdata.thevalue)) FROM cpu"
											+ allipstr
											+ " hostcollectdata where (to_char(SYSDATE,'hh24') - to_char(hostcollectdata.collecttime,'hh24')=1) and (to_char(SYSDATE,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
								}
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								// disk
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into diskinch"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,avg(hostcollectdata.thevalue) FROM diskincre"
											+ allipstr
											+ " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP,'%H') - DATE_FORMAT(hostcollectdata.collecttime,'%H')=1) and (DATE_FORMAT(CURRENT_TIMESTAMP,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit";

								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into diskinch"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(to_number(hostcollectdata.thevalue)) FROM diskincre"
											+ allipstr
											+ " hostcollectdata where (to_char(SYSDATE,'hh24') - to_char(hostcollectdata.collecttime,'hh24')=1) and (to_char(SYSDATE,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
								}
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}
								// disk
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into diskhour"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,avg(hostcollectdata.thevalue) FROM disk"
											+ allipstr
											+ " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP,'%H') - DATE_FORMAT(hostcollectdata.collecttime,'%H')=1) and (DATE_FORMAT(CURRENT_TIMESTAMP,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit";

								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into diskhour"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(to_number(hostcollectdata.thevalue)) FROM disk"
											+ allipstr
											+ " hostcollectdata where (to_char(SYSDATE,'hh24') - to_char(hostcollectdata.collecttime,'hh24')=1) and (to_char(SYSDATE,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
								}
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}

								// Process
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into prohour"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM pro"
											+ allipstr
											+ " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP,'%H') - DATE_FORMAT(hostcollectdata.collecttime,'%H')=1) and (DATE_FORMAT(CURRENT_TIMESTAMP,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									// 错误原因：加上entity != 'USER'
									sql = "insert into prohour"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(to_number(hostcollectdata.thevalue)) FROM pro"
											+ allipstr
											+ " hostcollectdata where (to_char(SYSDATE,'hh24') - to_char(hostcollectdata.collecttime,'hh24')=1)and entity != 'USER' and (to_char(SYSDATE,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								}
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}
								// UtilHdx 按小时归档到HOSTUTILHDXHOUR表里
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into utilhdxhour"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM utilhdx"
											+ allipstr
											+ " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP,'%H') - DATE_FORMAT(hostcollectdata.collecttime,'%H')=1) and (DATE_FORMAT(CURRENT_TIMESTAMP,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
									sql = "insert into utilhdxhour"
											+ allipstr
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(to_number(hostcollectdata.thevalue)) FROM utilhdx"
											+ allipstr
											+ " hostcollectdata where (to_char(SYSDATE,'hh24') - to_char(hostcollectdata.collecttime,'hh24')=1) and (to_char(SYSDATE,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";
								}
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}
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
								int dbtype = vo.getDbtype();
								if ("mysql".equalsIgnoreCase(SystemConstant.DBType) && dbtype == 1) {
									sql = "insert into orapinghour"
											+ id
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM oraping"
											+ id
											+ " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP,'%H') - DATE_FORMAT(hostcollectdata.collecttime,'%H')=1) and (DATE_FORMAT(CURRENT_TIMESTAMP,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("mysql".equalsIgnoreCase(SystemConstant.DBType) && dbtype == 2) {
									sql = "insert into sqlpinghour"
											+ id
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM sqlping"
											+ id
											+ " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP,'%H') - DATE_FORMAT(hostcollectdata.collecttime,'%H')=1) and (DATE_FORMAT(CURRENT_TIMESTAMP,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("mysql".equalsIgnoreCase(SystemConstant.DBType) && dbtype == 4) {
									sql = "insert into mypinghour"
											+ id
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM myping"
											+ id
											+ " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP,'%H') - DATE_FORMAT(hostcollectdata.collecttime,'%H')=1) and (DATE_FORMAT(CURRENT_TIMESTAMP,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("mysql".equalsIgnoreCase(SystemConstant.DBType) && dbtype == 5) {
									sql = "insert into db2pinghour"
											+ id
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM db2ping"
											+ id
											+ " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP,'%H') - DATE_FORMAT(hostcollectdata.collecttime,'%H')=1) and (DATE_FORMAT(CURRENT_TIMESTAMP,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("mysql".equalsIgnoreCase(SystemConstant.DBType) && dbtype == 6) {
									sql = "insert into syspinghour"
											+ id
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM sysping"
											+ id
											+ " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP,'%H') - DATE_FORMAT(hostcollectdata.collecttime,'%H')=1) and (DATE_FORMAT(CURRENT_TIMESTAMP,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("mysql".equalsIgnoreCase(SystemConstant.DBType) && dbtype == 7) {
									sql = "insert into informixpinghour"
											+ id
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(hostcollectdata.thevalue) FROM informixping"
											+ id
											+ " hostcollectdata where (DATE_FORMAT(CURRENT_TIMESTAMP,'%H') - DATE_FORMAT(hostcollectdata.collecttime,'%H')=1) and (DATE_FORMAT(CURRENT_TIMESTAMP,'%Y-%m-%d')=DATE_FORMAT(hostcollectdata.collecttime,'%Y-%m-%d')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType) && dbtype == 1) {
									sql = "insert into orapinghour"
											+ id
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(to_number(hostcollectdata.thevalue)) FROM oraping"
											+ id
											+ " hostcollectdata where (to_char(SYSDATE,'hh24') - to_char(hostcollectdata.collecttime,'hh24')=1) and (to_char(SYSDATE,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType) && dbtype == 2) {
									sql = "insert into sqlpinghour"
											+ id
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(to_number(hostcollectdata.thevalue)) FROM sqlping"
											+ id
											+ " hostcollectdata where (to_char(SYSDATE,'hh24') - to_char(hostcollectdata.collecttime,'hh24')=1) and (to_char(SYSDATE,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType) && dbtype == 4) {
									sql = "insert into mypinghour"
											+ id
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(to_number(hostcollectdata.thevalue)) FROM myping"
											+ id
											+ " hostcollectdata where (to_char(SYSDATE,'hh24') - to_char(hostcollectdata.collecttime,'hh24')=1) and (to_char(SYSDATE,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType) && dbtype == 5) {
									sql = "insert into db2pinghour"
											+ id
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(to_number(hostcollectdata.thevalue)) FROM oraping"
											+ id
											+ " hostcollectdata where (to_char(SYSDATE,'hh24') - to_char(hostcollectdata.collecttime,'hh24')=1) and (to_char(SYSDATE,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType) && dbtype == 6) {
									sql = "insert into syspinghour"
											+ id
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(to_number(hostcollectdata.thevalue)) FROM oraping"
											+ id
											+ " hostcollectdata where (to_char(SYSDATE,'hh24') - to_char(hostcollectdata.collecttime,'hh24')=1) and (to_char(SYSDATE,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								} else if ("mysql".equalsIgnoreCase(SystemConstant.DBType) && dbtype == 7) {
									sql = "insert into informixpinghour"
											+ id
											+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue) SELECT hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count,avg(to_number(hostcollectdata.thevalue)) FROM oraping"
											+ id
											+ " hostcollectdata where (to_char(SYSDATE,'hh24') - to_char(hostcollectdata.collecttime,'hh24')=1) and (to_char(SYSDATE,'YYYY-MM-DD')=to_char(hostcollectdata.collecttime,'YYYY-MM-DD')) group by hostcollectdata.ipaddress,hostcollectdata.restype,hostcollectdata.category,hostcollectdata.entity,hostcollectdata.subentity,hostcollectdata.unit,hostcollectdata.chname,hostcollectdata.bak,hostcollectdata.count";

								}
								try {
									stmt.addBatch(sql);
								} catch (Exception e) {
									e.printStackTrace();
								}
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
				ex.printStackTrace();
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
			// 第一次启动
			ShareData.setCount(1);
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

	public String[] gethourHis(String ip, String category, String entity, String subentity, String starttime, String totime) throws Exception {
		String[] returnVal = new String[24];
		ResultSet rs = null;
		DBManager dbmanager = new DBManager();
		try {
			String allipstr = SysUtil.doip(ip);
			String tablename = "";
			if (category.equalsIgnoreCase("CPU")) {
				tablename = "cpuhour";
			}
			if (category.equalsIgnoreCase("Ping")) {
				tablename = "pinghour";
			}
			if (category.equalsIgnoreCase("Process")) {
				tablename = "prohour";
			}
			String sql = "select a.thevalue,DATE_FORMAT(a.collecttime,'%Y-%m-%d %H:%i:%s') as colltime from " + tablename + allipstr + " a " + "where "
					+ " a.collecttime >=DATE_FORMAT('" + starttime + "','%Y-%m-%d %H:%i:%s') and a.collecttime<=DATE_FORMAT('" + totime
					+ "','%Y-%m-%d %H:%i:%s') order by a.collecttime";
			rs = dbmanager.executeQuery(sql);
			List list = new ArrayList();
			while (rs.next()) {
				Vector v = new Vector();
				String thevalue = rs.getString("thevalue");
				String collecttime = rs.getString("colltime");
				v.add(0, thevalue);
				v.add(1, collecttime);
				list.add(v);
			}
			rs.close();
			for (int i = 0; i < list.size(); i++) {
				Vector row = (Vector) list.get(i);
				String time = (String) row.get(1);
				returnVal[Integer.parseInt(time.substring(11, 13))] = dofloat((String) row.get(0));
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

	public Hashtable gethourHis1(String ip, String category, String entity, String subentity, String starttime, String totime) throws Exception {
		ResultSet rs = null;
		Hashtable returnV = new Hashtable();
		DBManager dbmanager = new DBManager();
		try {
			String allipstr = SysUtil.doip(ip);
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
				tablename = "cpuhour";
			}
			if (category.equalsIgnoreCase("Ping")) {
				consql = " and a.category='" + category + "' and a.entity='" + entity1 + "' ";
				tablename = "pinghour";
			}
			if (category.equalsIgnoreCase("ORAPing")) {
				consql = " and a.category='" + category + "' and a.entity='" + entity1 + "' ";
				tablename = "orapinghour";
			}
			if (category.equalsIgnoreCase("SQLPing")) {
				consql = " and a.category='" + category + "' and a.entity='" + entity1 + "' ";
				tablename = "sqlpinghour";
			}
			if (category.equalsIgnoreCase("DB2Ping")) {
				consql = " and a.category='" + category + "' and a.entity='" + entity1 + "' ";
				tablename = "db2pinghour";
			}
			if (category.equalsIgnoreCase("SYSPing")) {
				consql = " and a.category='" + category + "' and a.entity='" + entity1 + "' ";
				tablename = "syspinghour";
			}

			if (category.equalsIgnoreCase("Process")) {
				consql = " and a.category='" + category + "' and a.subentity='" + subentity1 + "' ";
				tablename = "prohour";
			}
			String sql = "select a.thevalue,DATE_FORMAT(a.collecttime,'%Y-%m-%d %H:%i:%s') as colltime from " + tablename + allipstr + " a " + "where "
					+ " a.collecttime >=DATE_FORMAT('" + starttime + "','%Y-%m-%d %H:%i:%s') and a.collecttime<=DATE_FORMAT('" + totime + "','%Y-%m-%d %H:%i:%s')" + consql
					+ " order by a.collecttime";
			rs = dbmanager.executeQuery(sql);
			List list = new ArrayList();
			double pingcon = 0;
			double allcpu = 0;
			double maxcpu = 0;
			double minping = 100;
			while (rs.next()) {
				Vector v = new Vector();
				String thevalue = rs.getString("thevalue");
				String collecttime = rs.getString("colltime");
				v.add(0, thevalue);
				v.add(1, collecttime);
				list.add(v);
				if ((category.equals("Ping") || category.equals("ORAPing") || category.equals("SQLPing")) && subentity.equalsIgnoreCase("ConnectUtilization")) {
					pingcon = pingcon + getfloat(thevalue);
					if (minping > getfloat(thevalue))
						minping = getfloat(thevalue);

				} else {
					allcpu += getfloat(thevalue);
					if (maxcpu < getfloat(thevalue))
						maxcpu = getfloat(thevalue);
				}
			}
			rs.close();
			if ((category.equals("Ping") || category.equals("ORAPing") || category.equals("SYSPing") || category.equals("SQLPing"))
					&& subentity.equalsIgnoreCase("ConnectUtilization")) {
				if (list != null && list.size() > 0) {
					returnV.put("avgpingcon", CEIString.round(pingcon / list.size(), 2) + "%");
				} else {
					returnV.put("avgpingcon", "0.0%");
				}
				returnV.put("minping", minping + "%");
			} else {
				if (list != null && list.size() > 0) {
					returnV.put("avgcpu", CEIString.round(allcpu / list.size(), 2) + "%");
				} else {
					returnV.put("avgcpu", "0.0%");
				}
				returnV.put("cpumax", maxcpu + "%");
			}
			returnV.put("list", list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}
		return returnV;
	}

	public Hashtable getmultiHis(String ip, String category, String starttime, String totime) throws Exception {
		Hashtable hash = new Hashtable();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBManager dbmanager = new DBManager();
		try {
			String allipstr = SysUtil.doip(ip);
			String tablename = "";
			if (category.equals("Memory")) {
				tablename = "memoryhour";
			} else if (category.equals("Disk")) {
				tablename = "diskhour";
			}
			String sql1 = "select distinct h.subentity from " + tablename + allipstr + " h ";
			stmt = con.prepareStatement(sql1);
			rs = stmt.executeQuery();
			List list1 = new ArrayList();
			while (rs.next()) {
				Vector v = new Vector();
				String subentity = rs.getString("subentity");
				v.add(0, subentity);
				list1.add(v);
			}
			rs.close();
			stmt.close();
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
				sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as colltime,h.subentity from " + tablename + allipstr + " h where h.ipaddress='");
				sb.append(ip);
				sb.append("' and h.category='");
				sb.append(category);
				sb.append("' and h.collecttime >=DATE_FORMAT('");
				sb.append(starttime);
				sb.append("','%Y-%m-%d %H:%i:%s') and h.collecttime<=DATE_FORMAT('");
				sb.append(totime);
				sb.append("','%Y-%m-%d %H:%i:%s') order by h.collecttime");
				sql = sb.toString();
				List list2 = new ArrayList();
				rs = dbmanager.executeQuery(sql);
				while (rs.next()) {
					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("colltime");
					String subentity = rs.getString("subentity");
					v.add(0, thevalue);
					v.add(1, collecttime);
					v.add(2, subentity);
					list2.add(v);
				}
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

	public Hashtable getmultiHis(String ip, String category, String subentity, String[] bandkey, String[] bandch, String starttime, String totime) throws Exception {
		Hashtable hash = new Hashtable();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBManager dbmanager = new DBManager();
		try {
			String allipstr = SysUtil.doip(ip);
			String checkband = bandkey[0];
			String tablename = "";
			if (checkband.equals("InBandwidthUtilHdxPerc")) {
				tablename = "hdxperchour";
			} else if (checkband.equals("InBandwidthUtilHdx")) {
				tablename = "utilhdxhour";
			} else if (checkband.equals("InDiscardsPerc")) {
				tablename = "dcarperh";
			} else if (checkband.equals("InErrorsPerc")) {
				tablename = "errperch";
			} else if (checkband.equals("InCastPkts")) {
				tablename = "packshour";
			}
			StringBuffer sb = new StringBuffer();
			int size = bandkey.length;
			String sql2 = "";
			if (category.indexOf("all") != -1) {
				sb.append(" and(");
				for (int j = 0; j < bandkey.length; j++) {
					if (j != 0) {
						sb.append("or");
					}
					sb.append(" h.subentity='");
					sb.append(bandkey[j]);
					sb.append("' ");
				}
				sb.append(") ");
				sql2 = "select DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as colltime,h.thevalue,h.subentity from Hostcollectdatahour h where h.ipaddress='" + ip
						+ "' and h.category='Interface'" + sb.toString() + " and h.collecttime >=DATE_FORMAT('" + starttime
						+ "','%Y-%m-%d %H:%i:%s') and h.collecttime<=DATE_FORMAT('" + totime + "','%Y-%m-%d %H:%i:%s') order by h.collecttime";
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
				sql2 = "select DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as colltime,h.thevalue,h.entity from " + tablename + allipstr + " h where 1=1 " + sb.toString()
						+ " and h.collecttime >=DATE_FORMAT('" + starttime + "','%Y-%m-%d %H:%i:%s') and h.collecttime<=DATE_FORMAT('" + totime
						+ "','%Y-%m-%d %H:%i:%s') order by h.collecttime";
			}
			Vector v2 = new Vector();
			rs = dbmanager.executeQuery(sql2);
			while (rs.next()) {
				Vector v = new Vector();
				v.add(0, rs.getString("thevalue"));
				v.add(1, rs.getString("colltime"));
				v.add(2, rs.getString("entity"));
				v2.add(v);
			}
			rs.close();
			stmt.close();
			for (int i = 0; i < bandch.length; i++) {
				hash.put(bandch[i], v2);
			}
			hash.put("key", bandch);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}
		return hash;
	}

	public Hashtable[] getMemory_month(String ip, String category, String starttime, String endtime) throws Exception {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Hashtable[] hash = new Hashtable[3];
		hash[0] = new Hashtable(); // 放图的y值
		hash[1] = new Hashtable(); // 放最大值
		hash[2] = new Hashtable(); // 放平均值
		DBManager dbmanager = new DBManager();
		try {
			String allipstr = SysUtil.doip(ip);
			String sql1 = "select max(h.thevalue) as value,h.subentity,avg(h.thevalue) as avgvalue from memoryhour" + allipstr + " h where " + " h.category='" + category
					+ "' and h.collecttime >= DATE_FORMAT('" + starttime + "','%Y-%m-%d %H:%i:%s') and h.collecttime <= DATE_FORMAT('" + endtime
					+ "','%Y-%m-%d %H:%i:%s') group by h.subentity order by h.subentity";
			rs = dbmanager.executeQuery(sql1);
			List list1 = new ArrayList();
			while (rs.next()) {
				Vector v = new Vector();
				String thevalue = rs.getString("value");
				String subentity = rs.getString("subentity");
				String avgvalue = rs.getString("avgvalue");
				v.add(0, thevalue);
				v.add(1, subentity);
				v.add(2, avgvalue);
				list1.add(v);
			}
			if (list1.size() != 0) {
				String[] key = new String[list1.size()];
				String[] max = new String[list1.size()];
				String[] avg = new String[list1.size()];
				Vector[] vector = new Vector[list1.size()];
				for (int i = 0; i < list1.size(); i++) {
					Vector row = (Vector) list1.get(i);
					key[i] = (String) row.get(1);
					max[i] = (String) row.get(0);
					avg[i] = (String) row.get(2);
					vector[i] = new Vector();
				}
				List list2 = new ArrayList();
				StringBuffer sb = new StringBuffer();
				sb.append("select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as colltime,h.unit,h.subentity from memoryhour" + allipstr + " h where ");
				sb.append(" h.category='");
				sb.append(category);
				sb.append("' and h.collecttime >= DATE_FORMAT('");
				sb.append(starttime);
				sb.append("','%Y-%m-%d %H:%i:%s') and h.collecttime <= DATE_FORMAT('");
				sb.append(endtime);
				sb.append("','%Y-%m-%d %H:%i:%s') order by h.collecttime");
				rs = dbmanager.executeQuery(sql1);
				while (rs.next()) {
					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("colltime");
					v.add(0, thevalue);
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					v.add(3, rs.getString("subentity"));
					list2.add(v);
				}
				rs.close();
				stmt.close();
				for (int i = 0; i < list2.size(); i++) {
					Vector obj = (Vector) list2.get(i);
					for (int j = 0; j < list1.size(); j++) {
						if (((String) obj.get(3)).equals(key[j])) {
							vector[j].add(obj);
						}
					}
				}
				String unit = "";
				if (list2.get(0) != null) {
					Vector obj = (Vector) list2.get(0);
					unit = (String) obj.get(2);
				}
				for (int i = 0; i < list1.size(); i++) {
					hash[0].put(key[i], vector[i]);
					hash[1].put(key[i], dofloat(max[i]) + unit);
					hash[2].put(key[i], dofloat(avg[i]) + unit);
				}
				hash[0].put("unit", unit);
				hash[0].put("key", key);
				if (category.equalsIgnoreCase("disk")) {
					hash[1].put("key", key);
				}
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

	public Hashtable getmultiHis(String ip, String category, String subentity, String[] bandkey, String[] bandch, String starttime, String totime, String tablename)
			throws Exception {
		Hashtable hash = new Hashtable();

		return hash;
	}

	private double getfloat(String num) {
		double snum = 0.0;
		if (num != null) {
			if (num.indexOf(".") >= 0) {
				if (num.substring(num.indexOf(".") + 1).length() > 7) {
					String tempStr = num.substring(num.indexOf(".") + 1);
					num = num.substring(0, num.indexOf(".") + 1) + tempStr.substring(0, 7);
				}
			}
			int inum = (int) (Float.parseFloat(num) * 100);
			snum = new Double(inum / 100.0).doubleValue();
		}
		return snum;
	}

	public void createDir(String commonPath) {
		File dir = new File(commonPath);
		if (!dir.exists()) {// 检查Sub目录是否存在
			dir.mkdir();
		}
	}

	public boolean netreportAll(User user) {
		return false;
	}

	public boolean hostreportAll(User user) {
		return false;
	}

}
