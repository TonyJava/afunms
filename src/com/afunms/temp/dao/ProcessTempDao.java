package com.afunms.temp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.detail.reomte.model.ProcessInfo;
import com.afunms.temp.model.NodeTemp;

@SuppressWarnings("unchecked")
public class ProcessTempDao extends BaseDao implements DaoInterface {

	public ProcessTempDao() {
		super("nms_process_data_temp");
	}

	/**
	 * 删除一条记录
	 */
	public boolean deleteByNodeIdSindex(String nodeid, String sid, String subentity) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_process_data_temp where nodeid='" + nodeid + "' and sindex='" + sid + "' and subentity='" + subentity + "'");
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
			conn.executeUpdate("delete from nms_process_data_temp where ip='" + ip + "'");
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
		sql.append("insert into nms_process_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
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

	public List<ProcessInfo> countProcessInfoByName(String nodeid, String type, String subtype) {
		List<ProcessInfo> list = new ArrayList<ProcessInfo>();
		StringBuffer sql = new StringBuffer();
		sql.append("select thevalue,count(thevalue) from nms_process_data_temp where nodeid='");
		sql.append(nodeid);
		sql.append("' and type='");
		sql.append(type);
		sql.append("' and subtype='");
		sql.append(subtype);
		sql.append("' and subentity='Name' group by thevalue");
		rs = conn.executeQuery(sql.toString());
		DBManager manager = new DBManager();
		try {
			Pattern p1 = Pattern.compile("(\\d+):(\\d+)秒");
			while (rs.next()) {
				try {
					String name = rs.getString(1); // 进程名称
					String count = rs.getString(2); // 进程个数
					StringBuffer sql2 = new StringBuffer();
					sql2.append("select * from nms_process_data_temp where sindex in (select sindex from nms_process_data_temp where ");
					sql2.append(" nodeid='" + nodeid + "' ");
					sql2.append(" and type='" + type + "' ");
					sql2.append(" and subtype='" + subtype + "'");
					sql2.append(" and thevalue='" + name + "')");
					String status = "";
					String proType = "";
					double cpuTime = 0;
					double cpuUtilization = 0;
					double memoryUtilization = 0;
					double memory = 0;
					ResultSet resultSet = manager.executeQuery(sql2.toString());
					while (resultSet.next()) {
						String subentity = resultSet.getString("subentity");
						String thevalue = resultSet.getString("thevalue");
						if ("CpuTime".equals(subentity)) {
							float sumOfCPU = 0;
							if (thevalue != null) {
								if (thevalue.indexOf(":") != -1) {
									Matcher matcher2 = p1.matcher(thevalue);
									if (matcher2.find()) {
										String t1 = matcher2.group(1);
										String t2 = matcher2.group(2);
										sumOfCPU = Float.parseFloat(t1) * 60 + Float.parseFloat(t2);
									}
								} else {
									sumOfCPU = Float.parseFloat(thevalue.replace("秒", ""));
								}
							}
							cpuTime = cpuTime + sumOfCPU;
						} else if ("Status".equals(subentity)) {
							status = thevalue;
						} else if ("CpuUtilization".equals(subentity)) {
							cpuUtilization = cpuUtilization + Double.parseDouble(thevalue);
						} else if ("Type".equals(subentity)) {
							proType = thevalue;
						} else if ("MemoryUtilization".equals(subentity)) {
							memoryUtilization = memoryUtilization + Double.parseDouble(thevalue);
						} else if ("Memory".equals(subentity)) {
							memory = memory + Double.parseDouble(thevalue);
						}
					}
					resultSet.close();
					ProcessInfo processInfo = new ProcessInfo();
					processInfo.setName(name);
					processInfo.setCount(count);
					processInfo.setCpuTime(cpuTime + "");
					processInfo.setMemory(memory + "");
					processInfo.setMemoryUtilization(memoryUtilization + "");
					processInfo.setStatus(status);
					processInfo.setType(proType);
					list.add(processInfo);
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
		return list;
	}

	public List<ProcessInfo> getProcessDetailInfoByName(String nodeid, String type, String subtype, String name) {
		List<ProcessInfo> list = new ArrayList<ProcessInfo>();
		StringBuffer sql = new StringBuffer();
		sql.append("select sindex from nms_process_data_temp where nodeid='");
		sql.append(nodeid);
		sql.append("' and type='");
		sql.append(type);
		sql.append("' and subtype='");
		sql.append(subtype);
		sql.append("' and thevalue='" + name + "'");
		rs = conn.executeQuery(sql.toString());
		DBManager manager = new DBManager();
		try {
			while (rs.next()) {
				try {
					String sindex = rs.getString(1); // 进程id
					StringBuffer sql2 = new StringBuffer();
					sql2.append("select * from nms_process_data_temp where");
					sql2.append(" nodeid='" + nodeid + "' ");
					sql2.append(" and type='" + type + "' ");
					sql2.append(" and subtype='" + subtype + "'");
					sql2.append(" and sindex='" + sindex + "'");
					String status = "";
					String proType = "";
					String cpuTime = "";
					String cpuUtilization = "";
					String memoryUtilization = "";
					String memory = "";
					ResultSet resultSet = manager.executeQuery(sql2.toString());
					while (resultSet.next()) {
						String subentity = resultSet.getString("subentity");
						String thevalue = resultSet.getString("thevalue");
						if ("CpuTime".equals(subentity)) {
							cpuTime = thevalue;
						} else if ("Status".equals(subentity)) {
							status = thevalue;
						} else if ("CpuUtilization".equals(subentity)) {
							cpuUtilization = cpuUtilization + Double.parseDouble(thevalue);
						} else if ("Type".equals(subentity)) {
							proType = thevalue;
						} else if ("MemoryUtilization".equals(subentity)) {
							memoryUtilization = thevalue;
						} else if ("Memory".equals(subentity)) {
							memory = memory + thevalue;
						}
					}
					resultSet.close();
					ProcessInfo processInfo = new ProcessInfo();
					processInfo.setPid(sindex);
					processInfo.setName(name);
					processInfo.setCpuTime(cpuTime + "");
					processInfo.setMemory(memory + "");
					processInfo.setMemoryUtilization(memoryUtilization + "");
					processInfo.setStatus(status);
					processInfo.setType(proType);
					list.add(processInfo);
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
		return list;
	}

	public List<ProcessInfo> getProcessInfo(String nodeid, String type, String subtype) {
		List<ProcessInfo> list = new ArrayList<ProcessInfo>();
		StringBuffer sql = new StringBuffer();
		sql.append("select sindex from nms_process_data_temp where nodeid='");
		sql.append(nodeid);
		sql.append("' and type='");
		sql.append(type);
		sql.append("' and subtype='");
		sql.append(subtype);
		sql.append("' group by sindex");
		rs = conn.executeQuery(sql.toString());
		DBManager manager = new DBManager();
		try {
			while (rs.next()) {
				try {
					String sindex = rs.getString(1); // 进程id
					StringBuffer sql2 = new StringBuffer();
					sql2.append("select * from nms_process_data_temp where");
					sql2.append(" nodeid='" + nodeid + "' ");
					sql2.append(" and type='" + type + "' ");
					sql2.append(" and subtype='" + subtype + "'");
					sql2.append(" and sindex='" + sindex + "'");
					String name = ""; // 进程名称
					String proType = ""; // 进程类型
					String cpuTime = ""; // CPU时间
					String memoryUtilization = ""; // 内存占用率
					String memory = ""; // 内存占用量
					String status = ""; // 当前状态
					String averageUSecs = ""; // 平均消耗CPU运行时间(光纤交换机)
					String extPriorityRev = ""; // 优先级(光纤交换机)
					String runtime = ""; // 运行时间(光纤交换机)
					ResultSet resultSet = manager.executeQuery(sql2.toString());
					while (resultSet.next()) {
						String subentity = resultSet.getString("subentity");
						String thevalue = resultSet.getString("thevalue");
						if ("Name".equals(subentity)) {
							name = thevalue;
						} else if ("CpuTime".equals(subentity)) {
							cpuTime = thevalue;
						} else if ("Status".equals(subentity)) {
							status = thevalue;
						} else if ("Type".equals(subentity)) {
							proType = thevalue;
						} else if ("MemoryUtilization".equals(subentity)) {
							memoryUtilization = thevalue;
						} else if ("Memory".equals(subentity)) {
							memory = memory + thevalue;
						} else if ("Status".equalsIgnoreCase(subentity)) {
							status = thevalue;
						} else if ("AverageUSecs".equalsIgnoreCase(subentity)) {
							averageUSecs = thevalue;
						} else if ("ExtPriorityRev".equalsIgnoreCase(subentity)) {
							extPriorityRev = thevalue;
						} else if ("Runtime".equalsIgnoreCase(subentity)) {
							runtime = thevalue;
						}
					}
					resultSet.close();
					ProcessInfo processInfo = new ProcessInfo();
					processInfo.setPid(sindex);
					processInfo.setName(name);
					processInfo.setCpuTime(cpuTime + "");
					processInfo.setMemory(memory + "");
					processInfo.setMemoryUtilization(memoryUtilization + "");
					processInfo.setStatus(status);
					processInfo.setType(proType);
					processInfo.setAverageUSecs(averageUSecs);
					processInfo.setExtPriorityRev(extPriorityRev);
					processInfo.setRuntime(runtime);
					list.add(processInfo);
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
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<NodeTemp> getNodeTempList(String nodeid, String type, String subtype) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "'");
		return findByCondition(sql.toString());
	}

	/**
	 * 得到进程信息
	 * 
	 * @param nodeid
	 * @param type
	 * @param subtype
	 * @param order
	 * @return
	 */
	public Hashtable getProcessInfo(String nodeid, String type, String subtype, String order) {
		Hashtable retHashtable = new Hashtable();
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select distinct sindex from nms_process_data_temp where nodeid = '");
		sqlBuffer.append(nodeid);
		sqlBuffer.append("' and type = '");
		sqlBuffer.append(type);
		sqlBuffer.append("' and subtype = '");
		sqlBuffer.append(subtype);
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
				Hashtable itemHashtable = new Hashtable();
				String sindex = (String) sindexList.get(i);
				StringBuffer sql = new StringBuffer();
				sql.append(" select * from nms_process_data_temp where nodeid='");
				sql.append(nodeid);
				sql.append("' and type='");
				sql.append(type);
				sql.append("' and subtype='");
				sql.append(subtype);
				sql.append("' and sindex='");
				sql.append(sindex);
				sql.append("' order by '");
				sql.append(order);
				sql.append("'");
				System.out.println(sql.toString());
				rs2 = dbManager.executeQuery(sql.toString());
				while (rs2.next()) {
					String subentity = rs2.getString("subentity");
					String thevalue = rs2.getString("thevalue");
					itemHashtable.put(subentity, thevalue);
				}
				retHashtable.put(i, itemHashtable);
				rs2.close();
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
			dbManager.close();
		}
		return retHashtable;
	}
}
