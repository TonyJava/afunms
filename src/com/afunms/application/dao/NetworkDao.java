package com.afunms.application.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.base.BaseVo;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.CpuCollectEntity;
import com.afunms.polling.om.DiskCollectEntity;
import com.afunms.polling.om.InPkts;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.MemoryCollectEntity;
import com.afunms.polling.om.OutPkts;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.polling.om.UtilHdx;
import com.afunms.polling.om.UtilHdxPerc;
import com.afunms.temp.model.NodeTemp;
import com.afunms.util.connectionPool.DBProperties;

@SuppressWarnings("unchecked")
public class NetworkDao {

	/**
	 * 删除多个设备的临时表中的数据
	 * 
	 * @param tableName
	 *            表名称
	 * @param nodeid
	 * @return
	 */
	public Boolean clearNmsTempDatas(String[] tableNames, String[] ids) {
		DBManager dbmanager = new DBManager();
		Boolean returnFlag = false;
		if (ids != null && ids.length > 0) {
			try {
				for (int i = 0; i < ids.length; i++) {
					String id = ids[i];
					PollingEngine.getInstance().deleteNodeByID(Integer.parseInt(id));
					for (String tableName : tableNames) {
						String sql = "delete from " + tableName + " where nodeid='" + id + "'";
						System.out.println(sql);
						dbmanager.addBatch(sql);
					}
				}
				dbmanager.executeBatch();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbmanager.close();
			}
			returnFlag = true;
		}
		return returnFlag;
	}

	/**
	 * 刷新所有结点的数据信息
	 * 
	 * @param nodeList
	 * @return
	 */
	public void collectAllNetworkData(List nodeList) {
		if (nodeList == null || nodeList.size() == 0) {
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String username = DBProperties.getUser();
		String password = DBProperties.getPassword();
		String url = DBProperties.getUrl();
		try {
			// 进行修改
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, username, password);
			conn.setAutoCommit(false);
			Node node = null;
			Host host = null;
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodedto = null;
			Vector memoryVector = null;
			Vector<DiskCollectEntity> diskVector = null;
			Vector cpuV = null;
			Vector allutil = null;
			Vector interfaceVector = null;
			Vector interfacevector = null;
			Vector interfaceVector_point = null;
			Vector inpksVector = null;
			Vector outpksVector = null;
			Vector ipPingData = null;
			Vector utilhdxVector = null;
			Vector utilhdxpercVector = null;
			StringBuffer sqlBuffer = null;
			PingCollectEntity pingcollectdata = null;
			Interfacecollectdata interfacecollectdata = null;
			AllUtilHdx allUtilHdx = null;
			stmt = conn.createStatement();
			for (int i = 0; i < nodeList.size(); i++) {
				node = (Node) nodeList.get(i);
				host = new Host();
				host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
				nodedto = nodeUtil.creatNodeDTOByNode(host);
				String nodeid = nodedto.getId() + "";
				String type = nodedto.getType();
				String subtype = nodedto.getSubtype();
				String ipall = nodedto.getIpaddress().replace(".", "_");
				memoryVector = new Vector();
				diskVector = new Vector<DiskCollectEntity>();
				cpuV = new Vector();
				allutil = new Vector();
				interfaceVector = new Vector();
				interfacevector = new Vector();
				interfaceVector_point = new Vector();
				inpksVector = new Vector();
				outpksVector = new Vector();
				utilhdxVector = new Vector();
				utilhdxpercVector = new Vector();
				ipPingData = new Vector();
				sqlBuffer = new StringBuffer();
				sqlBuffer.append("select * from  pingdata" + ipall + " where nodeid = '");
				sqlBuffer.append(nodeid);
				sqlBuffer.append("' and type = '");
				sqlBuffer.append(type);
				sqlBuffer.append("' and subtype = '");
				sqlBuffer.append(subtype);
				sqlBuffer.append("'");
				try {
					rs = stmt.executeQuery(sqlBuffer.toString());
					while (rs.next()) {
						try {
							pingcollectdata = new PingCollectEntity();
							pingcollectdata.setCategory(rs.getString("entity"));
							pingcollectdata.setEntity(rs.getString("subentity"));
							pingcollectdata.setSubentity(rs.getString("sindex"));
							pingcollectdata.setThevalue(rs.getString("thevalue"));
							String collecttime = rs.getString("collecttime");
							Date date = sdf.parse(collecttime);
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(date);
							pingcollectdata.setCollecttime(calendar);
							ipPingData.add(pingcollectdata);
							pingcollectdata = null;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					SysLogger.info(sqlBuffer.toString());
					e.printStackTrace();
				} finally {
				}
				if (host.getCollecttype() != 3) {
					StringBuffer sql = new StringBuffer();
					sql.append("select * from  memorydata" + ipall + " where nodeid = '");
					sql.append(nodeid);
					sql.append("' and type = '");
					sql.append(type);
					sql.append("' and subtype = '");
					sql.append(subtype);
					sql.append("'");
					try {
						rs = stmt.executeQuery(sql.toString());
						while (rs.next()) {
							MemoryCollectEntity memorycollectdata = new MemoryCollectEntity();
							memorycollectdata.setEntity(rs.getString("subentity"));
							memorycollectdata.setSubentity(rs.getString("sindex"));
							memorycollectdata.setThevalue(rs.getString("thevalue"));
							memorycollectdata.setUnit(rs.getString("unit"));
							memoryVector.add(memorycollectdata);
						}
					} catch (SQLException e) {
						SysLogger.info(sqlBuffer.toString());
						e.printStackTrace();
					} finally {
					}
					// 取出cpu信息
					StringBuffer cpuSqlBuffer = new StringBuffer();
					cpuSqlBuffer.append("select * from  cpudata" + ipall + " where nodeid = '");
					cpuSqlBuffer.append(nodeid);
					cpuSqlBuffer.append("' and type = '");
					cpuSqlBuffer.append(type);
					cpuSqlBuffer.append("' and subtype = '");
					cpuSqlBuffer.append(subtype);
					cpuSqlBuffer.append("' and entity = 'CPU'");
					try {
						rs = stmt.executeQuery(cpuSqlBuffer.toString());
						CpuCollectEntity cpUcollectdata = new CpuCollectEntity();
						while (rs.next()) {
							String thevalue = rs.getString("thevalue");
							cpUcollectdata.setThevalue(thevalue);
						}
						cpuV.add(cpUcollectdata);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
					}
					// 取出磁盘信息
					StringBuffer diskSqlBuffer = new StringBuffer();
					diskSqlBuffer.append(" select * from  diskdata" + ipall + " t where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "'");
					try {
						rs = stmt.executeQuery(diskSqlBuffer.toString());
						while (rs.next()) {
							DiskCollectEntity diskcollectdata = new DiskCollectEntity();
							diskcollectdata.setIpaddress(rs.getString("ip"));
							diskcollectdata.setCategory(rs.getString("entity"));
							diskcollectdata.setEntity(rs.getString("subentity"));
							diskcollectdata.setSubentity(rs.getString("sindex"));
							diskcollectdata.setThevalue(rs.getString("thevalue"));
							diskcollectdata.setChname(rs.getString("chname"));
							diskcollectdata.setRestype(rs.getString("restype"));
							diskcollectdata.setUnit(rs.getString("unit"));
							diskcollectdata.setBak(rs.getString("bak"));
							diskVector.add(diskcollectdata);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
					}
					// 取出端口流速信息 入口流速 出口流速 综合流速
					String utilhdxItems = "('AllInBandwidthUtilHdx','AllOutBandwidthUtilHdx','AllBandwidthUtilHdx')";
					sqlBuffer = new StringBuffer();
					sqlBuffer.append("select distinct * from interfacedata" + ipall + " where nodeid = '");
					sqlBuffer.append(nodeid);
					sqlBuffer.append("' and type = '");
					sqlBuffer.append(type);
					sqlBuffer.append("' and subtype = '");
					sqlBuffer.append(subtype);
					sqlBuffer.append("' and subentity in ");
					sqlBuffer.append(utilhdxItems);
					try {
						rs = stmt.executeQuery(sqlBuffer.toString());
						if (rs != null) {
							while (rs.next()) {
								String subentity = rs.getString("subentity");
								String thevalue = rs.getString("thevalue");
								String chname = rs.getString("chname");
								String restype = rs.getString("restype");
								String unit = rs.getString("unit");
								allUtilHdx = new AllUtilHdx();
								allUtilHdx.setSubentity(subentity);
								allUtilHdx.setChname(chname);
								allUtilHdx.setRestype(restype);
								allUtilHdx.setThevalue(thevalue);
								allUtilHdx.setUnit(unit);
								allutil.add(allUtilHdx);
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
					}
					// 取出interface数据
					String netInterfaceItem = "('ifMtu','index','ifType','ifDescr','ifSpeed','ifPhysAddress','ifAdminStatus','ifOperStatus','ifname')";
					sqlBuffer = new StringBuffer();
					sqlBuffer.append("select distinct * from interfacedata" + ipall + " where nodeid = '");
					sqlBuffer.append(nodeid);
					sqlBuffer.append("' and type = '");
					sqlBuffer.append(type);
					sqlBuffer.append("' and subtype = '");
					sqlBuffer.append(subtype);
					sqlBuffer.append("' and subentity in ");
					sqlBuffer.append(netInterfaceItem);
					try {
						rs = stmt.executeQuery(sqlBuffer.toString());
						if (rs != null) {
							while (rs.next()) {
								String subentity = rs.getString("subentity");
								String thevalue = rs.getString("thevalue");
								String chname = rs.getString("chname");
								String restype = rs.getString("restype");
								String unit = rs.getString("unit");
								String category = rs.getString("entity");
								String sindex = rs.getString("sindex");
								interfacecollectdata = new Interfacecollectdata();
								interfacecollectdata.setSubentity(subentity);
								interfacecollectdata.setChname(chname);
								interfacecollectdata.setRestype(restype);
								interfacecollectdata.setThevalue(thevalue);
								interfacecollectdata.setUnit(unit);
								interfacecollectdata.setCategory(category);
								interfacecollectdata.setEntity(sindex);
								interfaceVector.add(interfacecollectdata);
							}
						}
					} catch (SQLException e) {
						SysLogger.info(sqlBuffer.toString());
						e.printStackTrace();
					} finally {
					}
					// 取出interface_point数据
					String netInterfaceItem1 = "('ifDescr','ifSpeed')";
					sqlBuffer = new StringBuffer();
					sqlBuffer.append("select distinct * from interfacedata" + ipall + " where nodeid = '");
					sqlBuffer.append(nodeid);
					sqlBuffer.append("' and type = '");
					sqlBuffer.append(type);
					sqlBuffer.append("' and subtype = '");
					sqlBuffer.append(subtype);
					sqlBuffer.append("' and subentity in ");
					sqlBuffer.append(netInterfaceItem1);
					try {
						rs = stmt.executeQuery(sqlBuffer.toString());
						if (rs != null) {
							while (rs.next()) {
								String subentity = rs.getString("subentity");
								String thevalue = rs.getString("thevalue");
								String chname = rs.getString("chname");
								String restype = rs.getString("restype");
								String unit = rs.getString("unit");
								String category = rs.getString("entity");
								String sindex = rs.getString("sindex");
								interfacecollectdata = new Interfacecollectdata();
								interfacecollectdata.setSubentity(subentity);
								interfacecollectdata.setChname(chname);
								interfacecollectdata.setRestype(restype);
								interfacecollectdata.setThevalue(thevalue);
								interfacecollectdata.setUnit(unit);
								interfacecollectdata.setCategory(category);
								interfacecollectdata.setEntity(sindex);
								interfaceVector_point.add(interfacecollectdata);
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
					}
					List allinterfacelist = new ArrayList();
					sqlBuffer = new StringBuffer();
					sqlBuffer.append("select * from interfacedata" + ipall + " where nodeid = '");
					sqlBuffer.append(nodeid);
					sqlBuffer.append("' and type = '");
					sqlBuffer.append(type);
					sqlBuffer.append("' and subtype = '");
					sqlBuffer.append(subtype);
					sqlBuffer.append("'");
					try {
						rs = stmt.executeQuery(sqlBuffer.toString());
						while (rs.next()) {
							allinterfacelist.add(loadFromRS(rs));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (allinterfacelist != null && allinterfacelist.size() > 0) {
						Interfacecollectdata interfacedata = null;
						UtilHdx utilhdx = new UtilHdx();
						InPkts inpacks = new InPkts();
						OutPkts outpacks = new OutPkts();
						UtilHdxPerc utilhdxperc = new UtilHdxPerc();
						for (int j = 0; j < allinterfacelist.size(); j++) {
							try {
								NodeTemp vo = (NodeTemp) allinterfacelist.get(j);
								if ("AllInBandwidthUtilHdx".equals(vo.getSindex()) || "AllOutBandwidthUtilHdx".equals(vo.getSindex())
										|| "AllBandwidthUtilHdx".equals(vo.getSindex()))
									continue;
								if ("index".equals(vo.getSubentity()) || "ifDescr".equals(vo.getSubentity()) || "ifType".equals(vo.getSubentity())
										|| "ifMtu".equals(vo.getSubentity()) || "ifSpeed".equals(vo.getSubentity()) || "ifPhysAddress".equals(vo.getSubentity())
										|| "ifAdminStatus".equals(vo.getSubentity()) || "ifOperStatus".equals(vo.getSubentity()) || "ifname".equals(vo.getSubentity())) {
									// 接口配置数据
									interfacedata = new Interfacecollectdata();
									interfacedata.setIpaddress(host.getIpAddress());
									interfacedata.setCollecttime(null);
									interfacedata.setCategory("Interface");
									interfacedata.setSubentity(vo.getSindex());
									interfacedata.setRestype("static");
									interfacedata.setUnit(vo.getUnit());
									interfacedata.setThevalue(vo.getThevalue());
									interfacedata.setChname(vo.getChname());
									interfacedata.setBak("");
									interfacedata.setEntity(vo.getSubentity());
									interfacevector.add(interfacedata);
								} else if ("ifInMulticastPkts".equals(vo.getSubentity()) || "ifInBroadcastPkts".equals(vo.getSubentity())) {
									inpacks = new InPkts();
									inpacks.setIpaddress(host.getIpAddress());
									inpacks.setCollecttime(null);
									inpacks.setCategory("Interface");
									inpacks.setEntity(vo.getSubentity());
									inpacks.setSubentity(vo.getSindex());
									inpacks.setRestype("dynamic");
									inpacks.setUnit("");
									if ("ifInMulticastPkts".equals(vo.getSubentity())) {
										inpacks.setChname("多播");
									} else {
										inpacks.setChname("广播");
									}
									inpacks.setThevalue(vo.getThevalue());
									inpksVector.addElement(inpacks);
								} else if ("ifOutMulticastPkts".equals(vo.getSubentity()) || "ifOutBroadcastPkts".equals(vo.getSubentity())) {
									outpacks = new OutPkts();
									outpacks.setIpaddress(host.getIpAddress());
									outpacks.setCollecttime(null);
									outpacks.setCategory("Interface");
									outpacks.setEntity(vo.getSubentity());
									outpacks.setSubentity(vo.getSindex());
									outpacks.setRestype("dynamic");
									outpacks.setUnit("");
									if ("ifOutMulticastPkts".equals(vo.getSubentity())) {
										outpacks.setChname("多播");
									} else {
										outpacks.setChname("广播");
									}
									outpacks.setThevalue(vo.getThevalue());
									outpksVector.addElement(outpacks);
								} else if ("InBandwidthUtilHdx".equals(vo.getSubentity()) || "OutBandwidthUtilHdx".equals(vo.getSubentity())) {
									utilhdx = new UtilHdx();
									utilhdx.setIpaddress(host.getIpAddress());
									utilhdx.setCollecttime(null);
									utilhdx.setCategory("Interface");
									utilhdx.setEntity(vo.getSubentity());
									utilhdx.setSubentity(vo.getSindex());
									utilhdx.setRestype("dynamic");
									utilhdx.setUnit(vo.getUnit());
									if ("InBandwidthUtilHdx".equals(vo.getSubentity())) {
										utilhdx.setChname("入口");
									} else {
										utilhdx.setChname("出口");
									}
									utilhdx.setThevalue(vo.getThevalue());
									utilhdxVector.addElement(utilhdx);
								} else if ("InBandwidthUtilHdxPerc".equals(vo.getSubentity()) || "OutBandwidthUtilHdxPerc".equals(vo.getSubentity())) {
									utilhdxperc = new UtilHdxPerc();
									utilhdxperc.setIpaddress(host.getIpAddress());
									utilhdxperc.setCollecttime(null);
									utilhdxperc.setCategory("Interface");
									utilhdxperc.setEntity(vo.getSubentity());
									utilhdxperc.setSubentity(vo.getSindex());
									utilhdxperc.setRestype("dynamic");
									utilhdxperc.setUnit(vo.getUnit());
									if ("InBandwidthUtilHdxPerc".equals(vo.getSubentity())) {
										utilhdxperc.setChname("入口");
									} else {
										utilhdxperc.setChname("出口");
									}
									utilhdxperc.setThevalue(vo.getThevalue());
									utilhdxpercVector.addElement(utilhdxperc);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}

				// 更新内存
				// 加入ipPingData
				ShareData.getAllNetworkPingData().put(nodedto.getIpaddress(), ipPingData);
				Hashtable ipAllData = new Hashtable();
				// 加入内存数据
				ipAllData.put("memory", memoryVector);
				// 加入cpu数据
				ipAllData.put("cpu", cpuV);
				// 加入磁盘数据
				ipAllData.put("disk", diskVector);
				// 加入端口流速数据
				ipAllData.put("allutilhdx", allutil);
				// 加入interface接口等数据
				ipAllData.put("interface", interfaceVector);
				// 加入interface接口等数据
				ipAllData.put("interface_point", interfaceVector_point);
				ipAllData.put("interfacevector", interfacevector);
				ipAllData.put("inpacks", inpksVector);
				ipAllData.put("outpacks", outpksVector);
				ipAllData.put("utilhdx", utilhdxVector);
				ipAllData.put("utilhdxperc", utilhdxpercVector);
				ShareData.getAllNetworkData().put(nodedto.getIpaddress(), ipAllData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private BaseVo loadFromRS(ResultSet rs) {
		NodeTemp vo = new NodeTemp();
		try {
			vo.setNodeid(rs.getString("nodeid"));
			vo.setIp(rs.getString("ip"));
			vo.setType(rs.getString("type"));
			vo.setSubtype(rs.getString("subtype"));
			vo.setEntity(rs.getString("entity"));
			vo.setSubentity(rs.getString("subentity"));
			vo.setThevalue(rs.getString("thevalue"));
			vo.setChname(rs.getString("chname"));
			vo.setRestype(rs.getString("restype"));
			vo.setSindex(rs.getString("sindex"));
			vo.setCollecttime(rs.getString("collecttime"));
			vo.setUnit(rs.getString("unit"));
			vo.setBak(rs.getString("bak"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	/**
	 * 根据ids 批量修改 managesign
	 * 
	 * @param managesignFrom
	 *            原始状态
	 * @param managesignTo
	 *            批量更改后的状态
	 * @param ids
	 * @return
	 */
	public boolean batchUpdataMoniterByIds(String managesignFrom, String managesignTo, String[] ids) {
		DBManager conn = new DBManager();
		if (ids == null) {
			return false;
		}
		try {
			for (String id : ids) {
				if (id != null && !id.equals("")) {
					String sql = "update topo_host_node set managed='" + managesignTo + "' where id=" + id + " and managed = '" + managesignFrom + "'";
					conn.addBatch(sql);
				}
			}
			conn.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			conn.close();
		}
		return true;
	}

}
