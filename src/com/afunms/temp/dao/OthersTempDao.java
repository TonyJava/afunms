package com.afunms.temp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.detail.reomte.model.CpuPerfInfo;
import com.afunms.detail.reomte.model.NetmediaConfigInfo;
import com.afunms.detail.reomte.model.PagePerfInfo;
import com.afunms.detail.reomte.model.PageSpaceInfo;
import com.afunms.polling.om.CpuCollectEntity;
import com.afunms.temp.model.NodeTemp;

@SuppressWarnings("unchecked")
public class OthersTempDao extends BaseDao implements DaoInterface {

	public OthersTempDao() {
		super("nms_other_data_temp");
	}

	/**
	 * 删除一条记录
	 */
	public boolean deleteByNodeIdSindex(String nodeid, String sid, String subentity) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_other_data_temp where nodeid='" + nodeid + "' and sindex='" + sid + "' and subentity='" + subentity + "'");
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public boolean deleteByIpEntity(String ip, String entity) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_other_data_temp where ip='" + ip + "' and entity='" + entity + "'");
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public boolean deleteByIp(String ip) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_other_data_temp where ip='" + ip + "'");
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
		sql.append("insert into nms_other_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
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

	public List<NodeTemp> getNodeTemp(String nodeid, String type, String subtype) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "'");
		return findByCondition(sql.toString());
	}

	public List<CpuPerfInfo> getCpuPerfInfoList(String nodeid, String type, String subtype, String[] subentities) {
		List<CpuPerfInfo> cpuPerfInfoList = new ArrayList<CpuPerfInfo>();
		StringBuffer sql = new StringBuffer();
		sql.append("select sindex from nms_other_data_temp where nodeid='");
		sql.append(nodeid);
		sql.append("' and type='");
		sql.append(type);
		sql.append("' and subtype='");
		sql.append(subtype);
		sql.append("' and entity='cpuperflist' group by sindex");
		rs = conn.executeQuery(sql.toString());
		DBManager manager = new DBManager();
		try {
			while (rs.next()) {
				try {
					String sindex = rs.getString(1); // CPU性能索引(名称)
					StringBuffer sql2 = new StringBuffer();
					sql2.append("select * from nms_other_data_temp where ");
					sql2.append(" nodeid='" + nodeid + "' ");
					sql2.append(" and type='" + type + "' ");
					sql2.append(" and subtype='" + subtype + "'");
					sql2.append(" and sindex='" + sindex + "'");
					sql2.append(" and entity='cpuperflist'");
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
					String user = ""; // %用户
					String sysRate = ""; // %系统
					String wioRate = ""; // %io等待
					String idleRate = ""; // %空闲
					String physc = ""; // 物理
					ResultSet resultSet = manager.executeQuery(sql2.toString());
					while (resultSet.next()) {
						String subentity = resultSet.getString("subentity");
						String thevalue = resultSet.getString("thevalue");
						if ("%usr".equalsIgnoreCase(subentity)) {
							user = thevalue;
						} else if ("%sys".equalsIgnoreCase(subentity)) {
							sysRate = thevalue;
						} else if ("%wio".equalsIgnoreCase(subentity)) {
							wioRate = thevalue;
						} else if ("%idle".equalsIgnoreCase(subentity)) {
							idleRate = thevalue;
						} else if ("physc".equalsIgnoreCase(subentity)) {
							physc = thevalue;
						}
					}
					resultSet.close();
					CpuPerfInfo cpuPerfInfo = new CpuPerfInfo();
					cpuPerfInfo.setUser(user);
					cpuPerfInfo.setSysRate(sysRate);
					cpuPerfInfo.setWioRate(wioRate);
					cpuPerfInfo.setIdleRate(idleRate);
					cpuPerfInfo.setPhysc(physc);
					cpuPerfInfoList.add(cpuPerfInfo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			manager.close();
			conn.close();
		}
		return cpuPerfInfoList;
	}

	public List<CpuCollectEntity> getCpuPerfInfoList(String ipaddress) {
		List<CpuCollectEntity> cpuPerfInfoList = new ArrayList<CpuCollectEntity>();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_other_data_temp where ip='");
		sql.append(ipaddress);
		sql.append("' and entity='cpuperflist'");
		rs = conn.executeQuery(sql.toString());
		try {
			CpuCollectEntity cpudata = null;
			while (rs.next()) {
				try {
					cpudata = new CpuCollectEntity();
					cpudata.setIpaddress("");
					Calendar tempCal = Calendar.getInstance();
					Date cc = new Date();
					cc.setTime(rs.getTimestamp("collecttime").getTime());
					tempCal.setTime(cc);
					cpudata.setCollecttime(tempCal);
					cpudata.setCategory("CPU");
					cpudata.setEntity("Utilization");
					cpudata.setSubentity(rs.getString("subentity"));
					cpudata.setRestype("");
					cpudata.setUnit(rs.getString("unit"));
					cpudata.setThevalue(rs.getString("thevalue"));
					cpuPerfInfoList.add(cpudata);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return cpuPerfInfoList;
	}

	@SuppressWarnings("unchecked")
	public List<PagePerfInfo> getPagePerfInfoList(String nodeid, String type, String subtype, String[] subentities) {
		List<PagePerfInfo> pagePerfInfoList = new ArrayList<PagePerfInfo>();
		StringBuffer sql = new StringBuffer();
		sql.append("select sindex from nms_other_data_temp where nodeid='");
		sql.append(nodeid);
		sql.append("' and type='");
		sql.append(type);
		sql.append("' and subtype='");
		sql.append(subtype);
		sql.append("' and entity='pagehash' group by sindex");
		rs = conn.executeQuery(sql.toString());
		DBManager manager = new DBManager();
		try {
			while (rs.next()) {
				try {
					String sindex = rs.getString(1); // 页面性能索引(名称)
					StringBuffer sql2 = new StringBuffer();
					sql2.append("select * from nms_other_data_temp where ");
					sql2.append(" nodeid='" + nodeid + "' ");
					sql2.append(" and type='" + type + "' ");
					sql2.append(" and subtype='" + subtype + "'");
					sql2.append(" and sindex='" + sindex + "'");
					sql2.append(" and entity='pagehash'");
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
					String re = ""; // 页面调度程序输入/输出列表
					String pi = ""; // 内存页面调进数
					String po = ""; // 内存页面调出数
					String fr = ""; // 释放的页数
					String sr = ""; // 扫描的页
					String cy = ""; // 时钟周期
					ResultSet resultSet = manager.executeQuery(sql2.toString());
					while (resultSet.next()) {
						String subentity = resultSet.getString("subentity");
						String thevalue = resultSet.getString("thevalue");
						if ("re".equalsIgnoreCase(subentity)) {
							re = thevalue;
						} else if ("pi".equalsIgnoreCase(subentity)) {
							pi = thevalue;
						} else if ("po".equalsIgnoreCase(subentity)) {
							po = thevalue;
						} else if ("fr".equalsIgnoreCase(subentity)) {
							fr = thevalue;
						} else if ("sr".equalsIgnoreCase(subentity)) {
							sr = thevalue;
						} else if ("cy".equalsIgnoreCase(subentity)) {
							cy = thevalue;
						}
					}
					resultSet.close();
					PagePerfInfo pagePerfInfo = new PagePerfInfo();
					pagePerfInfo.setRe(re);
					pagePerfInfo.setPi(pi);
					pagePerfInfo.setPo(po);
					pagePerfInfo.setFr(fr);
					pagePerfInfo.setSr(sr);
					pagePerfInfo.setCy(cy);
					pagePerfInfoList.add(pagePerfInfo);
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
		return pagePerfInfoList;
	}

	@SuppressWarnings("unchecked")
	public List<PageSpaceInfo> getPageSpaceInfoList(String nodeid, String type, String subtype, String[] subentities) {
		List<PageSpaceInfo> pagePerfInfoList = new ArrayList<PageSpaceInfo>();
		StringBuffer sql = new StringBuffer();
		sql.append("select sindex from nms_other_data_temp where nodeid='");
		sql.append(nodeid);
		sql.append("' and type='");
		sql.append(type);
		sql.append("' and subtype='");
		sql.append(subtype);
		sql.append("' and entity='paginghash' group by sindex");
		rs = conn.executeQuery(sql.toString());
		DBManager manager = new DBManager();
		try {
			while (rs.next()) {
				try {
					String sindex = rs.getString(1); // 页面交换索引
					StringBuffer sql2 = new StringBuffer();
					sql2.append("select * from nms_other_data_temp where ");
					sql2.append(" nodeid='" + nodeid + "' ");
					sql2.append(" and type='" + type + "' ");
					sql2.append(" and subtype='" + subtype + "'");
					sql2.append(" and sindex='" + sindex + "'");
					sql2.append(" and entity='paginghash'");
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
					String total_Paging_Space = ""; // Total Paging Space
					String percent_Used = ""; // Percent Used
					ResultSet resultSet = manager.executeQuery(sql2.toString());
					while (resultSet.next()) {
						String subentity = resultSet.getString("subentity");
						String thevalue = resultSet.getString("thevalue");
						if ("Total_Paging_Space".equalsIgnoreCase(subentity)) {
							total_Paging_Space = thevalue;
						} else if ("Percent_Used".equalsIgnoreCase(subentity)) {
							percent_Used = thevalue;
						}
					}
					resultSet.close();
					PageSpaceInfo pageSpaceInfo = new PageSpaceInfo();
					pageSpaceInfo.setTotal_Paging_Space(total_Paging_Space);
					pageSpaceInfo.setPercent_Used(percent_Used);

					pagePerfInfoList.add(pageSpaceInfo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			manager.close();
			conn.close();
		}
		return pagePerfInfoList;
	}

	@SuppressWarnings("unchecked")
	public List<NetmediaConfigInfo> getNetmediaConfigInfoList(String nodeid, String type, String subtype, String[] subentities) {
		List<NetmediaConfigInfo> netmediaConfigList = new ArrayList<NetmediaConfigInfo>();
		StringBuffer sql = new StringBuffer();
		sql.append("select sindex from nms_other_data_temp where nodeid='");
		sql.append(nodeid);
		sql.append("' and type='");
		sql.append(type);
		sql.append("' and subtype='");
		sql.append(subtype);
		sql.append("' and entity='netmedialist' group by sindex");
		rs = conn.executeQuery(sql.toString());
		DBManager manager = new DBManager();
		try {
			while (rs.next()) {
				try {
					String sindex = rs.getString(1); // 网卡配置索引
					StringBuffer sql2 = new StringBuffer();
					sql2.append("select * from nms_other_data_temp where ");
					sql2.append(" nodeid='" + nodeid + "' ");
					sql2.append(" and type='" + type + "' ");
					sql2.append(" and subtype='" + subtype + "'");
					sql2.append(" and sindex='" + sindex + "'");
					sql2.append(" and entity='netmedialist'");
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
					String desc = ""; // 名称
					String mac = ""; // MAC
					String speed = ""; // 速率
					String status = ""; // 状态
					ResultSet resultSet = manager.executeQuery(sql2.toString());
					while (resultSet.next()) {
						String subentity = resultSet.getString("subentity");
						String thevalue = resultSet.getString("thevalue");
						if ("desc".equalsIgnoreCase(subentity)) {
							desc = thevalue;
						} else if ("mac".equalsIgnoreCase(subentity)) {
							mac = thevalue;
						} else if ("speed".equalsIgnoreCase(subentity)) {
							speed = thevalue;
						} else if ("status".equalsIgnoreCase(subentity)) {
							status = thevalue;
						}
					}
					resultSet.close();
					NetmediaConfigInfo netmediaConfigInfo = new NetmediaConfigInfo();
					netmediaConfigInfo.setDesc(desc);
					netmediaConfigInfo.setMac(mac);
					netmediaConfigInfo.setSpeed(speed);
					netmediaConfigInfo.setStatus(status);
					netmediaConfigList.add(netmediaConfigInfo);
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
		return netmediaConfigList;
	}

	public String getCollecttime(String nodeid, String type, String subtype) {
		String collecttime = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select thevalue from nms_other_data_temp where nodeid = '");
		sql.append(nodeid);
		sql.append("' and type = '");
		sql.append(type);
		sql.append("' and subtype = '");
		sql.append(subtype);
		sql.append("' and entity = 'collecttime'");
		DBManager dbManager = new DBManager();
		try {
			rs = dbManager.executeQuery(sql.toString());
			while (rs.next()) {
				collecttime = rs.getString("thevalue");
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
			dbManager.close();
		}
		return collecttime;
	}

	public Hashtable getPaginghash(String nodeid, String type, String subtype) {
		Hashtable paginghash = new Hashtable();
		StringBuffer sql = new StringBuffer();
		sql.append("select subentity,thevalue from nms_other_data_temp where nodeid = '");
		sql.append(nodeid);
		sql.append("' and type = '");
		sql.append(type);
		sql.append("' and subtype = '");
		sql.append(subtype);
		sql.append("' and entity = 'paginghash'");
		DBManager dbManager = new DBManager();
		try {
			rs = dbManager.executeQuery(sql.toString());
			while (rs.next()) {
				String subentity = rs.getString("subentity");
				String thevalue = rs.getString("thevalue");
				paginghash.put(subentity, thevalue);
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
			dbManager.close();
		}
		return paginghash;
	}

	public Hashtable getPagehash(String nodeid, String type, String subtype) {
		Hashtable pagehash = new Hashtable();
		StringBuffer sql = new StringBuffer();
		sql.append("select subentity,thevalue from nms_other_data_temp where nodeid = '");
		sql.append(nodeid);
		sql.append("' and type = '");
		sql.append(type);
		sql.append("' and subtype = '");
		sql.append(subtype);
		sql.append("' and entity = 'pagehash'");
		DBManager dbManager = new DBManager();
		try {
			rs = dbManager.executeQuery(sql.toString());
			while (rs.next()) {
				String subentity = rs.getString("subentity");
				String thevalue = rs.getString("thevalue");
				pagehash.put(subentity, thevalue);
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
			dbManager.close();
		}
		return pagehash;
	}

	public List getNetmediaConfigInfo(String nodeid, String type, String subtype) {
		List netmediaconfigList = new ArrayList();
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select distinct sindex from nms_other_data_temp where nodeid = '");
		sqlBuffer.append(nodeid);
		sqlBuffer.append("' and type = '");
		sqlBuffer.append(type);
		sqlBuffer.append("' and subtype = '");
		sqlBuffer.append(subtype);
		sqlBuffer.append("' and entity = 'netmedialist'");
		DBManager dbManager = new DBManager();
		rs1 = dbManager.executeQuery(sqlBuffer.toString());
		List sindexList = new ArrayList();
		try {
			while (rs1.next()) {
				String sindex = rs1.getString("sindex");
				sindexList.add(sindex);
			}
			rs1.close();
			for (int i = 0; i < sindexList.size(); i++) {
				Hashtable tempHashtable = new Hashtable();
				String sindex = (String) sindexList.get(i);
				StringBuffer sql = new StringBuffer();
				sql.append("select * from nms_other_data_temp where nodeid = '");
				sql.append(nodeid);
				sql.append("' and type = '");
				sql.append(type);
				sql.append("' and subtype = '");
				sql.append(subtype);
				sql.append("' and entity = 'netmedialist' and sindex = '");
				sql.append(sindex);
				sql.append("'");
				rs2 = dbManager.executeQuery(sql.toString());
				while (rs2.next()) {
					String subentity = rs2.getString("subentity");
					String thevalue = rs2.getString("thevalue");
					tempHashtable.put(subentity, thevalue);
				}
				netmediaconfigList.add(tempHashtable);
				rs2.close();
			}
		} catch (Exception e) {
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
			dbManager.close();
		}
		return netmediaconfigList;
	}

	/**
	 * 得到cpuperflist cpu性能信息
	 * 
	 * @param nodeid
	 * @param type
	 * @param subype
	 * @return
	 */
	public List getCpuPerListInfo(String nodeid, String type, String subtype) {
		List retList = new ArrayList();
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select * from nms_other_data_temp where nodeid = '");
		sqlBuffer.append(nodeid);
		sqlBuffer.append("' and type = '");
		sqlBuffer.append(type);
		sqlBuffer.append("' and subtype = '");
		sqlBuffer.append(subtype);
		sqlBuffer.append("' and entity = 'cpuperflist'");
		DBManager dbManager = new DBManager();
		try {
			rs = dbManager.executeQuery(sqlBuffer.toString());
			Hashtable cpuperHash = new Hashtable();
			while (rs.next()) {
				String subentity = rs.getString("subentity");
				String thevalue = rs.getString("thevalue");
				cpuperHash.put(subentity, thevalue);
			}
			retList.add(cpuperHash);
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
			dbManager.close();
		}
		return retList;
	}

	/**
	 * 获取nms_other_data_temp中集合类型为List<Hashtable<String,String>> 的信息
	 * 
	 * @param nodeid
	 * @param type
	 * @param subtype
	 * @param entity
	 *            类别 如:cpuconfig
	 * @return
	 */
	public List getlistInfo(String nodeid, String type, String subtype, String entity) {
		List retList = new ArrayList();
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select distinct sindex from nms_other_data_temp where nodeid = '");
		sqlBuffer.append(nodeid);
		sqlBuffer.append("' and type = '");
		sqlBuffer.append(type);
		sqlBuffer.append("' and subtype = '");
		sqlBuffer.append(subtype);
		sqlBuffer.append("' and entity = '");
		sqlBuffer.append(entity);
		sqlBuffer.append("'");
		DBManager dbManager = new DBManager();
		rs1 = dbManager.executeQuery(sqlBuffer.toString());
		List sindexList = new ArrayList();
		try {
			while (rs1.next()) {
				String sindex = rs1.getString("sindex");
				sindexList.add(sindex);
			}
			rs1.close();
			for (int i = 0; i < sindexList.size(); i++) {
				Hashtable tempHashtable = new Hashtable();
				String sindex = (String) sindexList.get(i);
				StringBuffer sql = new StringBuffer();
				sql.append("select * from nms_other_data_temp where nodeid = '");
				sql.append(nodeid);
				sql.append("' and type = '");
				sql.append(type);
				sql.append("' and subtype = '");
				sql.append(subtype);
				sql.append("' and entity = '");
				sql.append(entity);
				sql.append("' and sindex = '");
				sql.append(sindex);
				sql.append("'");
				rs2 = dbManager.executeQuery(sql.toString());
				while (rs2.next()) {
					String subentity = rs2.getString("subentity");
					String thevalue = rs2.getString("thevalue");
					tempHashtable.put(subentity, thevalue);
				}
				retList.add(tempHashtable);
				rs2.close();
			}
		} catch (Exception e) {
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
			dbManager.close();
		}
		return retList;
	}

	/**
	 * 获取nms_other_data_temp中集合类型为Hashtable<String,String> 的信息
	 * 
	 * @param nodeid
	 * @param type
	 * @param subtype
	 * @param entity
	 *            类别 如：memoryconfig
	 * @return
	 */
	public Hashtable getHashInfo(String nodeid, String type, String subtype, String entity) {
		Hashtable retHash = new Hashtable();
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select * from nms_other_data_temp where nodeid = '");
		sqlBuffer.append(nodeid);
		sqlBuffer.append("' and type = '");
		sqlBuffer.append(type);
		sqlBuffer.append("' and subtype = '");
		sqlBuffer.append(subtype);
		sqlBuffer.append("' and entity = '");
		sqlBuffer.append(entity);
		sqlBuffer.append("'");
		DBManager dbManager = new DBManager();
		try {
			rs = dbManager.executeQuery(sqlBuffer.toString());
			while (rs.next()) {
				String subentity = rs.getString("subentity");
				String thevalue = rs.getString("thevalue");
				retHash.put(subentity, thevalue);
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
			dbManager.close();
		}
		return retHash;
	}
}
