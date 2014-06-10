package com.afunms.temp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.detail.reomte.model.InterfaceInfo;
import com.afunms.temp.model.NodeTemp;
import com.afunms.topology.model.HostNode;

@SuppressWarnings("unchecked")
public class InterfaceTempDao extends BaseDao implements DaoInterface {

	public InterfaceTempDao() {
		super("nms_interface_data_temp");
	}

	/**
	 * 删除一条记录
	 */
	public boolean deleteByNodeIdSindex(String nodeid, String sid, String subentity) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_interface_data_temp where nodeid='" + nodeid + "' and sindex='" + sid + "' and subentity='" + subentity + "'");
			result = true;
		} catch (Exception ex) {
			result = false;
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public boolean deleteByIp(String ip) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_interface_data_temp where ip='" + ip + "'");
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public BaseVo loadFromRS(ResultSet rs) {
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

	public boolean save(BaseVo baseVo) {
		NodeTemp vo = (NodeTemp) baseVo;
		StringBuffer sql = new StringBuffer(500);
		sql.append("insert into nms_interface_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
		sql.append(vo.getNodeid());
		sql.append("','");
		sql.append(vo.getIp());
		sql.append("','");
		sql.append(vo.getType());
		sql.append("','");
		sql.append(vo.getSubtype());
		sql.append("','");
		sql.append(vo.getEntity());
		sql.append("','");
		sql.append(vo.getSubentity());
		sql.append("','");
		sql.append(vo.getSindex());
		sql.append("','");
		sql.append(vo.getThevalue());
		sql.append("','");
		sql.append(vo.getChname());
		sql.append("','");
		sql.append(vo.getRestype());
		sql.append("','");
		sql.append(vo.getCollecttime());
		sql.append("','");
		sql.append(vo.getUnit());
		sql.append("','");
		sql.append(vo.getBak());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		return false;
	}

	public List<NodeTemp> getNodeTempList(String nodeid, String type, String subtype, String[] subentities) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "'");
		if (subentities != null && subentities.length > 0) {
			for (int i = 0; i < subentities.length; i++) {
				if (i == 0) {
					sql.append(" and (");
				} else {
					sql.append(" or");
				}
				sql.append(" subentity='" + subentities[i] + "'");
			}
			sql.append(" )");
		}
		return findByCondition(sql.toString());
	}

	/**
	 * 取出已知IP地址组的所有端口流速信息集合
	 * 
	 * @param nodeipList
	 * @param subentities
	 * @return
	 */
	public Hashtable<String, List<NodeTemp>> getNodeTempListHash(List<String> nodeipList, String[] subentities) {
		Hashtable retHash = new Hashtable();
		DBManager dbManager = new DBManager();
		ResultSet rs = null;
		try {
			for (int m = 0; m < nodeipList.size(); m++) {
				List<NodeTemp> nodeTempList = new ArrayList<NodeTemp>();
				String ip = nodeipList.get(m);
				StringBuffer sql = new StringBuffer();
				sql.append("select * from nms_interface_data_temp where ip='");
				sql.append(ip);
				sql.append("'");
				if (subentities != null && subentities.length > 0) {
					for (int i = 0; i < subentities.length; i++) {
						if (i == 0) {
							sql.append(" and (");
						} else {
							sql.append(" or");
						}
						sql.append(" subentity='" + subentities[i] + "'");
					}
					sql.append(" )");
				}
				try {
					rs = dbManager.executeQuery(sql.toString());
					if (rs == null) {
						continue;
					}
					while (rs.next()) {
						nodeTempList.add((NodeTemp) loadFromRS(rs));
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (rs != null) {
							rs.close();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				retHash.put(ip, nodeTempList);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			dbManager.close();
		}
		return retHash;
	}

	public List<InterfaceInfo> getInterfaceInfoList(String nodeid, String type, String subtype, String[] subentities) {
		List<InterfaceInfo> interfaceInfoList = new ArrayList<InterfaceInfo>();
		StringBuffer sql = new StringBuffer();
		sql.append("select sindex from nms_interface_data_temp where nodeid='");
		sql.append(nodeid);
		sql.append("' and type='");
		sql.append(type);
		sql.append("' and subtype='");
		sql.append(subtype);
		sql.append("' and sindex!='AllOutBandwidthUtilHdx' and sindex!='AllBandwidthUtilHdx' and sindex!='AllInBandwidthUtilHdx'");
		sql.append(" group by sindex");
		sql.append(" order by sindex");// sindex还需要排序
		rs = conn.executeQuery(sql.toString());
		DBManager manager = new DBManager();
		try {
			while (rs.next()) {
				try {
					String sindex = rs.getString(1); // 端口索引
					StringBuffer sql2 = new StringBuffer();
					sql2.append("select * from nms_interface_data_temp where ");
					sql2.append(" nodeid='" + nodeid + "' ");
					sql2.append(" and type='" + type + "' ");
					sql2.append(" and subtype='" + subtype + "'");
					sql2.append(" and sindex='" + sindex + "'");
					if (subentities != null && subentities.length > 0) {
						for (int i = 0; i < subentities.length; i++) {
							if (i == 0) {
								sql2.append(" and (");
							} else {
								sql2.append(" or");
							}
							sql2.append(" subentity='" + subentities[i] + "'");
						}
						sql2.append(" )");
					}
					sql2.append(" order by sindex");// HONG
					String ifDescr = "";
					String ifSpeed = "";
					String ifOperStatus = "";
					String ifOutBroadcastPkts = "0";
					String ifInBroadcastPkts = "0";
					String ifOutMulticastPkts = "0";
					String ifInMulticastPkts = "0";
					String outBandwidthUtilHdx = "0";
					String inBandwidthUtilHdx = "0";
					ResultSet resultSet = manager.executeQuery(sql2.toString());
					while (resultSet.next()) {
						String subentity = resultSet.getString("subentity");
						String thevalue = resultSet.getString("thevalue");
						if ("ifDescr".equalsIgnoreCase(subentity)) {
							ifDescr = thevalue;
						} else if ("ifSpeed".equalsIgnoreCase(subentity)) {
							ifSpeed = thevalue;
						} else if ("ifOperStatus".equalsIgnoreCase(subentity)) {
							ifOperStatus = thevalue;
						} else if ("ifOutBroadcastPkts".equalsIgnoreCase(subentity)) {
							ifOutBroadcastPkts = thevalue;
						} else if ("ifInBroadcastPkts".equalsIgnoreCase(subentity)) {
							ifInBroadcastPkts = thevalue;
						} else if ("ifOutMulticastPkts".equalsIgnoreCase(subentity)) {
							ifOutMulticastPkts = thevalue;
						} else if ("ifInMulticastPkts".equalsIgnoreCase(subentity)) {
							ifInMulticastPkts = thevalue;
						} else if ("outBandwidthUtilHdx".equalsIgnoreCase(subentity)) {
							outBandwidthUtilHdx = thevalue;
						} else if ("inBandwidthUtilHdx".equalsIgnoreCase(subentity)) {
							inBandwidthUtilHdx = thevalue;
						}
					}

					InterfaceInfo interfaceInfo = new InterfaceInfo();
					interfaceInfo.setSindex(sindex);
					interfaceInfo.setIfDescr(ifDescr);
					interfaceInfo.setIfSpeed(ifSpeed);
					interfaceInfo.setIfOperStatus(ifOperStatus);
					interfaceInfo.setIfOutBroadcastPkts(ifOutBroadcastPkts);
					interfaceInfo.setIfInBroadcastPkts(ifInBroadcastPkts);
					interfaceInfo.setIfOutMulticastPkts(ifOutMulticastPkts);
					interfaceInfo.setIfInMulticastPkts(ifInMulticastPkts);
					interfaceInfo.setOutBandwidthUtilHdx(outBandwidthUtilHdx);
					interfaceInfo.setInBandwidthUtilHdx(inBandwidthUtilHdx);

					interfaceInfoList.add(interfaceInfo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			manager.close();
			conn.close();
		}
		return interfaceInfoList;
	}

	/**
	 * 
	 * @param nodeid
	 * @param type
	 * @param subtype
	 * @param subentities
	 * @param sindexs
	 *            索引
	 * @return
	 */
	public List<InterfaceInfo> getInterfaceInfoList(String nodeid, String type, String subtype, String[] subentities, String[] sindexs, String starttime, String totime) {
		List<InterfaceInfo> interfaceInfoList = new ArrayList<InterfaceInfo>();
		DBManager manager = new DBManager();
		try {
			for (int k = 0; k < sindexs.length; k++) {
				try {
					String sindex = sindexs[k]; // 端口索引
					StringBuffer sql2 = new StringBuffer();
					sql2.append("select * from nms_interface_data_temp where ");
					sql2.append(" nodeid='" + nodeid + "' ");
					sql2.append(" and type='" + type + "' ");
					sql2.append(" and subtype='" + subtype + "'");
					sql2.append(" and collecttime>= to_date('");
					sql2.append(starttime);
					sql2.append("','YYYY-MM-DD HH24:MI:SS') and collecttime<=to_date('");
					sql2.append(totime);
					sql2.append("','YYYY-MM-DD HH24:MI:SS') ");
					sql2.append(" and sindex='" + sindex + "'");
					if (subentities != null && subentities.length > 0) {
						for (int i = 0; i < subentities.length; i++) {
							if (i == 0) {
								sql2.append(" and (");
							} else {
								sql2.append(" or");
							}
							sql2.append(" subentity='" + subentities[i] + "'");
						}
						sql2.append(" )");
					}
					sql2.append(" order by sindex"); 
					String ifDescr = "";
					String ifSpeed = "";
					String ifOperStatus = "";
					String ifOutBroadcastPkts = "0";
					String ifInBroadcastPkts = "0";
					String ifOutMulticastPkts = "0";
					String ifInMulticastPkts = "0";
					String outBandwidthUtilHdx = "0";
					String inBandwidthUtilHdx = "0";
					ResultSet resultSet = manager.executeQuery(sql2.toString());
					while (resultSet.next()) {
						String subentity = resultSet.getString("subentity");
						String thevalue = resultSet.getString("thevalue");
						if ("ifDescr".equalsIgnoreCase(subentity)) {
							ifDescr = thevalue;
						} else if ("ifSpeed".equalsIgnoreCase(subentity)) {
							ifSpeed = thevalue;
						} else if ("ifOperStatus".equalsIgnoreCase(subentity)) {
							ifOperStatus = thevalue;
						} else if ("ifOutBroadcastPkts".equalsIgnoreCase(subentity)) {
							ifOutBroadcastPkts = thevalue;
						} else if ("ifInBroadcastPkts".equalsIgnoreCase(subentity)) {
							ifInBroadcastPkts = thevalue;
						} else if ("ifOutMulticastPkts".equalsIgnoreCase(subentity)) {
							ifOutMulticastPkts = thevalue;
						} else if ("ifInMulticastPkts".equalsIgnoreCase(subentity)) {
							ifInMulticastPkts = thevalue;
						} else if ("outBandwidthUtilHdx".equalsIgnoreCase(subentity)) {
							outBandwidthUtilHdx = thevalue;
						} else if ("inBandwidthUtilHdx".equalsIgnoreCase(subentity)) {
							inBandwidthUtilHdx = thevalue;
						}
					}
					resultSet.close();
					InterfaceInfo interfaceInfo = new InterfaceInfo();
					interfaceInfo.setSindex(sindex);
					interfaceInfo.setIfDescr(ifDescr);
					interfaceInfo.setIfSpeed(ifSpeed);
					interfaceInfo.setIfOperStatus(ifOperStatus);
					interfaceInfo.setIfOutBroadcastPkts(ifOutBroadcastPkts);
					interfaceInfo.setIfInBroadcastPkts(ifInBroadcastPkts);
					interfaceInfo.setIfOutMulticastPkts(ifOutMulticastPkts);
					interfaceInfo.setIfInMulticastPkts(ifInMulticastPkts);
					interfaceInfo.setOutBandwidthUtilHdx(outBandwidthUtilHdx);
					interfaceInfo.setInBandwidthUtilHdx(inBandwidthUtilHdx);

					interfaceInfoList.add(interfaceInfo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} finally {
			manager.close();
			conn.close();
		}
		return interfaceInfoList;
	}

	/**
	 * 
	 * @param nodeid
	 * @param type
	 * @param subtype
	 * @param subentities
	 * @return
	 */
	public List<InterfaceInfo> getInterfaceInfosList(String nodeid, String type, String subtype, String[] subentities) {
		List<InterfaceInfo> interfaceInfoList = new ArrayList<InterfaceInfo>();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_interface_data_temp where nodeid='");
		sql.append(nodeid);
		sql.append("' and type='");
		sql.append(type);
		sql.append("' and subtype='");
		sql.append(subtype);
		sql.append("' ");
		if (subentities != null && subentities.length > 0) {
			for (int i = 0; i < subentities.length; i++) {
				if (i == 0) {
					sql.append(" and (");
				} else {
					sql.append(" or");
				}
				sql.append(" subentity='" + subentities[i] + "'");
			}
			sql.append(") ");
		}
		rs = conn.executeQuery(sql.toString());
		try {
			while (rs.next()) {
				String outBandwidthUtilHdx = "0";
				String inBandwidthUtilHdx = "0";
				String allBandwidthUtilHdx = "0";
				String subentity = rs.getString("subentity");
				String thevalue = rs.getString("thevalue");
				if ("AllInBandwidthUtilHdx".equalsIgnoreCase(subentity)) {
					inBandwidthUtilHdx = thevalue;
				} else if ("AllOutBandwidthUtilHdx".equalsIgnoreCase(subentity)) {
					outBandwidthUtilHdx = thevalue;
				} else if ("AllBandwidthUtilHdx".equalsIgnoreCase(subentity)) {
					allBandwidthUtilHdx = thevalue;
				}
				InterfaceInfo interfaceInfo = new InterfaceInfo();
				interfaceInfo.setOutBandwidthUtilHdx(outBandwidthUtilHdx);
				interfaceInfo.setInBandwidthUtilHdx(inBandwidthUtilHdx);
				interfaceInfo.setAllBandwidthUtilHdx(allBandwidthUtilHdx);
				interfaceInfoList.add(interfaceInfo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return interfaceInfoList;
	}

	public List<InterfaceInfo> getInterfaceInfoBySindes(String nodeid, String type, String subtype, String[] sindexs, String starttime, String totime) {
		List<InterfaceInfo> interfaceInfoList = new ArrayList<InterfaceInfo>();
		DBManager manager = new DBManager();
		try {
			for (int k = 0; k < sindexs.length; k++) {
				try {
					String sindex = sindexs[k]; // 端口索引
					StringBuffer sql2 = new StringBuffer();
					sql2.append("select * from nms_interface_data_temp where ");
					sql2.append(" nodeid='" + nodeid + "' ");
					sql2.append(" and type='" + type + "' ");
					sql2.append(" and subtype='" + subtype + "'");
					sql2.append(" and collecttime>= to_date('");
					sql2.append(starttime);
					sql2.append("','YYYY-MM-DD HH24:MI:SS') and collecttime<=to_date('");
					sql2.append(totime);
					sql2.append("','YYYY-MM-DD HH24:MI:SS') ");
					sql2.append(" and sindex ='" + sindex + "'");
					sql2.append(" order by sindex");// HONG
					String ifDescr = "";
					String ifSpeed = "";
					String ifOperStatus = "";
					String ifOutBroadcastPkts = "0";
					String ifInBroadcastPkts = "0";
					String ifOutMulticastPkts = "0";
					String ifInMulticastPkts = "0";
					String outBandwidthUtilHdx = "0";
					String inBandwidthUtilHdx = "0";
					String allBandwidthUtilHdx = "0";
					String ifType = "0";
					String ifMtu = "0";
					String ifAdminStatus = "0";
					String InBandwidthUtilHdxPerc = "0";
					String OutBandwidthUtilHdxPerc = "0";
					ResultSet resultSet = manager.executeQuery(sql2.toString());
					while (resultSet.next()) {
						String subentity = resultSet.getString("subentity");
						String thevalue = resultSet.getString("thevalue");
						if ("ifDescr".equalsIgnoreCase(subentity)) {
							ifDescr = thevalue;
						} else if ("ifSpeed".equalsIgnoreCase(subentity)) {
							ifSpeed = thevalue;
						} else if ("ifOperStatus".equalsIgnoreCase(subentity)) {
							ifOperStatus = thevalue;
						} else if ("ifOutBroadcastPkts".equalsIgnoreCase(subentity)) {
							ifOutBroadcastPkts = thevalue;
						} else if ("ifInBroadcastPkts".equalsIgnoreCase(subentity)) {
							ifInBroadcastPkts = thevalue;
						} else if ("ifOutMulticastPkts".equalsIgnoreCase(subentity)) {
							ifOutMulticastPkts = thevalue;
						} else if ("ifInMulticastPkts".equalsIgnoreCase(subentity)) {
							ifInMulticastPkts = thevalue;
						} else if ("outBandwidthUtilHdx".equalsIgnoreCase(subentity)) {
							outBandwidthUtilHdx = thevalue;
						} else if ("inBandwidthUtilHdx".equalsIgnoreCase(subentity)) {
							inBandwidthUtilHdx = thevalue;
						} else if ("allBandwidthUtilHdx".equalsIgnoreCase(subentity)) {
							allBandwidthUtilHdx = thevalue;
						} else if ("ifType".equalsIgnoreCase(subentity)) {
							ifType = thevalue;
						} else if ("ifMtu".equalsIgnoreCase(subentity)) {
							ifMtu = thevalue;
						} else if ("ifAdminStatus".equalsIgnoreCase(subentity)) {
							ifAdminStatus = thevalue;
						} else if ("InBandwidthUtilHdxPerc".equalsIgnoreCase(subentity)) {
							InBandwidthUtilHdxPerc = thevalue;
						}
						if ("OutBandwidthUtilHdxPerc".equalsIgnoreCase(subentity)) {
							OutBandwidthUtilHdxPerc = thevalue;
						}
					}
					resultSet.close();
					InterfaceInfo interfaceInfo = new InterfaceInfo();
					interfaceInfo.setSindex(sindex);
					interfaceInfo.setIfDescr(ifDescr);
					interfaceInfo.setIfSpeed(ifSpeed);
					interfaceInfo.setIfOperStatus(ifOperStatus);
					interfaceInfo.setIfOutBroadcastPkts(ifOutBroadcastPkts);
					interfaceInfo.setIfInBroadcastPkts(ifInBroadcastPkts);
					interfaceInfo.setIfOutMulticastPkts(ifOutMulticastPkts);
					interfaceInfo.setIfInMulticastPkts(ifInMulticastPkts);
					interfaceInfo.setOutBandwidthUtilHdx(outBandwidthUtilHdx);
					interfaceInfo.setInBandwidthUtilHdx(inBandwidthUtilHdx);
					interfaceInfo.setAllBandwidthUtilHdx(allBandwidthUtilHdx);
					interfaceInfo.setIfType(ifType);
					interfaceInfo.setIfMtu(ifMtu);
					interfaceInfo.setIfAdminStatus(ifAdminStatus);
					interfaceInfo.setInBandwidthUtilHdxPerc(InBandwidthUtilHdxPerc);
					interfaceInfo.setOutBandwidthUtilHdxPerc(OutBandwidthUtilHdxPerc);
					interfaceInfoList.add(interfaceInfo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} finally {
			manager.close();
			conn.close();
		}
		return interfaceInfoList;
	}

	/**
	 * String[]
	 * netInterfaceItem={"index","ifDescr","ifSpeed","ifOperStatus","OutBandwidthUtilHdx","InBandwidthUtilHdx"};
	 * 
	 * @param nodeid
	 * @param subentitys
	 * @return
	 */
	public Vector getInterfaceInfo(String nodeid, String type, String subtype, String[] subentitys) {
		Vector retVector = new Vector();
		DBManager manager = new DBManager();
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select distinct sindex from nms_interface_data_temp where nodeid = '");
		sqlBuffer.append(nodeid);
		sqlBuffer.append("' and type = '");
		sqlBuffer.append(type);
		sqlBuffer.append("' and subtype = '");
		sqlBuffer.append(subtype);
		sqlBuffer.append("'");
		rs1 = manager.executeQuery(sqlBuffer.toString());
		List sindexList = new ArrayList();
		try {
			while (rs1.next()) {
				String sindex = rs1.getString("sindex");
				sindexList.add(sindex);
			}
			rs1.close();
			for (int i = 0; i < sindexList.size(); i++) {
				String[] itemStrings = new String[6];
				String sindex = (String) sindexList.get(i);
				sqlBuffer = new StringBuffer();
				sqlBuffer.append("select distinct * from nms_interface_data_temp where nodeid = '");
				sqlBuffer.append(nodeid);
				sqlBuffer.append("' and type = '");
				sqlBuffer.append(type);
				sqlBuffer.append("' and subtype = '");
				sqlBuffer.append(subtype);
				sqlBuffer.append("' and sindex = '");
				sqlBuffer.append(sindex);
				sqlBuffer.append("'");
				rs2 = manager.executeQuery(sqlBuffer.toString());
				while (rs2.next()) {
					String subentity = rs2.getString("subentity");
					String thevalue = rs2.getString("thevalue");
					if (subentity.equalsIgnoreCase("index")) {
						itemStrings[0] = thevalue;
					} else if (subentity.equalsIgnoreCase("ifDescr")) {
						itemStrings[1] = thevalue;
					} else if (subentity.equalsIgnoreCase("ifSpeed")) {
						itemStrings[2] = thevalue;
					} else if (subentity.equalsIgnoreCase("ifOperStatus")) {
						itemStrings[3] = thevalue;
					} else if (subentity.equalsIgnoreCase("OutBandwidthUtilHdx")) {
						itemStrings[4] = thevalue;
					} else if (subentity.equalsIgnoreCase("InBandwidthUtilHdx")) {
						itemStrings[5] = thevalue;
					}
				}
				rs2.close();
				retVector.add(itemStrings);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs1 != null) {
					rs1.close();
				}
				if (rs2 != null) {
					rs2.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			manager.close();
		}
		return retVector;
	}

	public List getNetflowInfo(String nodeid, String type, String subtype) {
		List retList = new ArrayList();
		DBManager manager = new DBManager();
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select distinct sindex from nms_interface_data_temp where nodeid = '");
		sqlBuffer.append(nodeid);
		sqlBuffer.append("' and type = '");
		sqlBuffer.append(type);
		sqlBuffer.append("' and subtype = '");
		sqlBuffer.append(subtype);
		sqlBuffer.append("' and entity = 'Interface'");
		rs1 = manager.executeQuery(sqlBuffer.toString());
		List sindexList = new ArrayList();
		try {
			while (rs1.next()) {
				String sindex = rs1.getString("sindex");
				sindexList.add(sindex);
			}
			rs1.close();
			for (int i = 0; i < sindexList.size(); i++) {
				String sindex = (String) sindexList.get(i);
				sqlBuffer = new StringBuffer();
				sqlBuffer.append("select distinct * from nms_interface_data_temp where nodeid = '");
				sqlBuffer.append(nodeid);
				sqlBuffer.append("' and type = '");
				sqlBuffer.append(type);
				sqlBuffer.append("' and subtype = '");
				sqlBuffer.append(subtype);
				sqlBuffer.append("' and sindex = '");
				sqlBuffer.append(sindex);
				sqlBuffer.append("'");
				rs2 = manager.executeQuery(sqlBuffer.toString());
				Hashtable netFlowItemHashtable = new Hashtable();
				while (rs2.next()) {
					String subentity = rs2.getString("subentity");
					String thevalue = rs2.getString("thevalue");
					netFlowItemHashtable.put(subentity, thevalue);
				}
				rs2.close();
				retList.add(netFlowItemHashtable);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs1 != null) {
					rs1.close();
				}
				if (rs2 != null) {
					rs2.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			manager.close();
		}
		return retList;
	}

	/**
	 * 根据监控的Node列表得到平均端口流速列表
	 * 
	 * @param monitorNodelist
	 * @return
	 */
	public List<NodeTemp> getCurrAllInterfaceInfo(List monitorNodelist) {
		List<NodeTemp> retList = new ArrayList<NodeTemp>();
		StringBuffer sql = new StringBuffer();
		sql.append("select nodeid,ip,type,subtype,subentity, avg(thevalue) avg_thevalue from nms_interface_data_temp ");
		sql.append("where (subentity='AllInBandwidthUtilHdx' or subentity='AllOutBandwidthUtilHdx') and nodeid in ('");
		if (monitorNodelist != null && monitorNodelist.size() > 0) {
			for (int i = 0; i < monitorNodelist.size(); i++) {
				Object obj = monitorNodelist.get(i);
				if (obj instanceof HostNode) {
					HostNode hostNode = (HostNode) obj;
					sql.append(hostNode.getId());
					if (i != monitorNodelist.size() - 1) {
						sql.append("','");
					}
				}
			}
		}
		sql.append("') group by nodeid,subentity");
		try {
			rs = conn.executeQuery(sql.toString());
			while (rs.next()) {
				NodeTemp vo = new NodeTemp();
				vo.setNodeid(rs.getString("nodeid"));
				vo.setIp(rs.getString("ip"));
				vo.setType(rs.getString("type"));
				vo.setSubtype(rs.getString("subtype"));
				vo.setSubentity(rs.getString("subentity"));
				vo.setThevalue(rs.getString("avg_thevalue"));
				retList.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			conn.close();
		}
		return retList;
	}
}
