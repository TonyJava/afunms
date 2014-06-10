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
import com.afunms.detail.reomte.model.DiskInfo;
import com.afunms.polling.om.DiskCollectEntity;
import com.afunms.temp.model.NodeTemp;

@SuppressWarnings("unchecked")
public class DiskTempDao extends BaseDao implements DaoInterface {

	public DiskTempDao() {
		super("nms_disk_data_temp");
	}

	/**
	 * 删除一条记录
	 */
	public boolean deleteByNodeIdSindex(String nodeid, String sid, String subentity) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_disk_data_temp where nodeid='" + nodeid + "' and sindex='" + sid + "' and subentity='" + subentity + "'");
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
			conn.executeUpdate("delete from nms_disk_data_temp where ip='" + ip + "'");
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
		sql.append("insert into nms_disk_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
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

	public List<NodeTemp> getCurrDiskListInfo(String nodeid, String type, String subtype, String sindex) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "' and sindex = '" + sindex + "' order by sindex");
		return findByCondition(sql.toString());
	}

	public List getCurrDiskSindex(String nodeid, String type, String subtype) throws SQLException {
		List sindexsList = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append(" select sindex from nms_disk_data_temp t where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "' group by sindex");
		try {
			rs = conn.executeQuery(sql.toString());
			while (rs.next()) {
				String sindex = rs.getString("sindex");
				sindexsList.add(sindex);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			rs.close();
			conn.close();
		}
		return sindexsList;
	}

	public List<DiskInfo> getDiskInfoList(String nodeid, String type, String subtype, String[] subentities) {
		List<DiskInfo> diskInfoList = new ArrayList<DiskInfo>();
		StringBuffer sql = new StringBuffer();
		sql.append("select sindex from nms_disk_data_temp where nodeid='");
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
					String sindex = rs.getString(1); // 磁盘索引(名称)
					StringBuffer sql2 = new StringBuffer();
					sql2.append("select * from nms_disk_data_temp where ");
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
					String allSize = "0";
					String usedSize = "0";
					String utilization = "0";
					String utilizationInc = "0";
					String allSizeUnit = "";
					String usedSizeUnit = "";
					String utilizationUnit = "";
					ResultSet resultSet = manager.executeQuery(sql2.toString());
					while (resultSet.next()) {
						String subentity = resultSet.getString("subentity");
						String thevalue = resultSet.getString("thevalue");
						String unit = resultSet.getString("unit");
						if ("AllSize".equalsIgnoreCase(subentity)) {
							allSize = thevalue;
							allSizeUnit = unit;
						} else if ("UsedSize".equalsIgnoreCase(subentity)) {
							usedSize = thevalue;
							usedSizeUnit = unit;
						} else if ("Utilization".equalsIgnoreCase(subentity)) {
							utilization = thevalue;
							utilizationUnit = unit;
						} else if ("UtilizationInc".equalsIgnoreCase(subentity)) {
							utilizationInc = thevalue;
						}
					}
					resultSet.close();
					DiskInfo diskInfo = new DiskInfo();
					diskInfo.setSindex(sindex);
					diskInfo.setAllSize(allSize);
					diskInfo.setAllSizeUnit(allSizeUnit);
					diskInfo.setUsedSize(usedSize);
					diskInfo.setUsedSizeUnit(usedSizeUnit);
					diskInfo.setUtilization(utilization);
					diskInfo.setUtilizationInc(utilizationInc);
					diskInfo.setUtilizationUnit(utilizationUnit);
					diskInfoList.add(diskInfo);
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
		return diskInfoList;
	}

	/**
	 * 得到diskperf 磁盘性能信息
	 * 
	 * @param nodeid
	 * @return
	 */
	public List getDiskperflistInfo(String nodeid) {
		List diskperfList = new ArrayList();
		DBManager dbManager = new DBManager();
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select distinct sindex from nms_diskperf_data_temp where nodeid = '");
		sqlBuffer.append(nodeid);
		sqlBuffer.append("'");
		ResultSet rs1 = null;
		rs1 = dbManager.executeQuery(sqlBuffer.toString());
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
				sqlBuffer.append("select distinct * from nms_diskperf_data_temp where nodeid = '");
				sqlBuffer.append(nodeid);
				sqlBuffer.append("' and sindex = '");
				sqlBuffer.append(sindex);
				sqlBuffer.append("'");
				rs = dbManager.executeQuery(sqlBuffer.toString());
				Hashtable diskperflistHashtable = new Hashtable();
				while (rs.next()) {
					String subentity = rs.getString("subentity");
					String thevalue = rs.getString("thevalue");
					diskperflistHashtable.put(subentity, thevalue);
				}
				diskperfList.add(diskperflistHashtable);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			dbManager.close();
		}
		return diskperfList;
	}

	public Vector<DiskCollectEntity> getDiskInfoVector(String nodeid, String type, String subtype) {
		Vector<DiskCollectEntity> diskInfoVector = new Vector<DiskCollectEntity>();
		StringBuffer sql = new StringBuffer();
		sql.append(" select * from nms_disk_data_temp t where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "'");
		try {
			rs = conn.executeQuery(sql.toString());
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
				diskInfoVector.add(diskcollectdata);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			conn.close();
		}
		return diskInfoVector;
	}
}
