package com.afunms.temp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.detail.reomte.model.DiskioInfo;
import com.afunms.temp.model.NodeTemp;

@SuppressWarnings("unchecked")
public class DiskioTempDao extends BaseDao implements DaoInterface {

	public DiskioTempDao() {
		super("nms_diskio_data_temp");
	}

	/**
	 * 删除一条记录
	 */
	public boolean deleteByNodeIdSindex(String nodeid, String sid, String subentity) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_diskio_data_temp where nodeid='" + nodeid + "' and sindex='" + sid + "' and subentity='" + subentity + "'");
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
			conn.executeUpdate("delete from nms_diskio_data_temp where ip='" + ip + "'");
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
		sql.append("insert into nms_diskio_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
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
		System.out.println(sql.toString());
		return findByCondition(sql.toString());
	}

	
	public List<DiskioInfo> getDiskInfoList(String nodeid, String type, String subtype, String[] subentities) {
		List<DiskioInfo> diskioInfoList = new ArrayList<DiskioInfo>();
		StringBuffer sql = new StringBuffer();
		sql.append("select sindex from nms_diskio_data_temp where nodeid='");
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
					sql2.append("select * from nms_diskio_data_temp where ");
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
					String disks = ""; // 磁盘名称
					String tmAct = "";
					String kbps = "";
					String tps = "";
					String kbRead = "";
					String kbWrtn = "";
					ResultSet resultSet = manager.executeQuery(sql2.toString());
					while (resultSet.next()) {
						String subentity = resultSet.getString("subentity");
						String thevalue = resultSet.getString("thevalue");
						if ("Disks".equalsIgnoreCase(subentity)) {
							disks = thevalue;
						} else if ("%tm_act".equalsIgnoreCase(subentity)) {
							tmAct = thevalue;
						} else if ("Kbps".equalsIgnoreCase(subentity)) {
							kbps = thevalue;
						} else if ("tps".equalsIgnoreCase(subentity)) {
							tps = thevalue;
						} else if ("kb_read".equalsIgnoreCase(subentity)) {
							kbRead = thevalue;
						} else if ("kb_wrtn".equalsIgnoreCase(subentity)) {
							kbWrtn = thevalue;
						}
					}
					resultSet.close();
					DiskioInfo diskioInfo = new DiskioInfo();
					diskioInfo.setDisks(disks);
					diskioInfo.setTmAct(tmAct);
					diskioInfo.setKbps(kbps);
					diskioInfo.setTps(tps);
					diskioInfo.setKbRead(kbRead);
					diskioInfo.setKbWrtn(kbWrtn);
					diskioInfoList.add(diskioInfo);
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
		return diskioInfoList;
	}

	/**
	 * 得到diskio 磁盘性能信息
	 * 
	 * @param nodeid
	 * @return
	 */
	public List getdiskiolistInfo(String nodeid, String type, String subtype) {
		List diskioList = new ArrayList();
		DBManager dbManager = new DBManager();
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select distinct sindex from nms_diskio_data_temp where nodeid = '");
		sqlBuffer.append(nodeid);
		sqlBuffer.append("' and type = '");
		sqlBuffer.append(type);
		sqlBuffer.append("' and subtype = '");
		sqlBuffer.append(subtype);
		sqlBuffer.append("'");
		ResultSet rs1 = null;
		rs1 = dbManager.executeQuery(sqlBuffer.toString());
		Hashtable alllist = new Hashtable();

		try {
			while (rs1.next()) {
				String sindex = rs1.getString("sindex");
				Hashtable diskperflistHashtable = new Hashtable();
				alllist.put(sindex, diskperflistHashtable);
			}
			rs1.close();
			sqlBuffer = new StringBuffer();
			sqlBuffer.append("select distinct * from nms_diskio_data_temp where nodeid = '");
			sqlBuffer.append(nodeid).append("'");
			rs = dbManager.executeQuery(sqlBuffer.toString());
			while (rs.next()) {
				String subentity = rs.getString("subentity");
				String thevalue = rs.getString("thevalue");
				String sindex = rs.getString("sindex");
				((Hashtable) alllist.get(sindex)).put(subentity, thevalue);
			}
			rs.close();

			Iterator it = alllist.keySet().iterator();
			while (it.hasNext()) {
				diskioList.add(alllist.get((String) it.next()));
			}
			alllist = null;

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
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			dbManager.close();
		}
		return diskioList;
	}
}
