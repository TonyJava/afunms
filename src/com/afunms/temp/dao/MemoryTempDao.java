package com.afunms.temp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.detail.reomte.model.MemoryConfigInfo;
import com.afunms.polling.om.MemoryCollectEntity;
import com.afunms.temp.model.NodeTemp;
import com.afunms.topology.model.HostNode;


@SuppressWarnings("unchecked")
public class MemoryTempDao extends BaseDao implements DaoInterface {

	public MemoryTempDao() {
		super("nms_memory_data_temp");
	}

	/**
	 * 删除一条记录
	 */
	public boolean deleteByNodeIdSindex(String nodeid, String sid, String subentity) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_memory_data_temp where nodeid='" + nodeid + "' and sindex='" + sid + "' and subentity='" + subentity + "'");
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
			conn.executeUpdate("delete from nms_memory_data_temp where ip='" + ip + "'");
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
		sql.append("insert into nms_memory_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
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

	@SuppressWarnings("unchecked")
	public List<MemoryConfigInfo> getMemoryConfigInfoList(String nodeid, String type, String subtype) {
		List<MemoryConfigInfo> memoryConfigInfoList = new ArrayList<MemoryConfigInfo>();
		StringBuffer sql = new StringBuffer();
		sql.append("select sindex from nms_memory_data_temp where nodeid='");
		sql.append(nodeid);
		sql.append("' and type='");
		sql.append(type);
		sql.append("' and subtype='");
		sql.append(subtype);
		sql.append("' and subentity='Capability' group by sindex");
		rs = conn.executeQuery(sql.toString());
		DBManager manager = new DBManager();
		try {
			while (rs.next()) {
				try {
					String sindex = rs.getString(1); // memory配置索引(名称)
					StringBuffer sql2 = new StringBuffer();
					sql2.append("select * from nms_memory_data_temp where ");
					sql2.append(" nodeid='" + nodeid + "' ");
					sql2.append(" and type='" + type + "' ");
					sql2.append(" and subtype='" + subtype + "'");
					sql2.append(" and sindex='" + sindex + "'");
					sql2.append(" and subentity='Capability'");
					String size = ""; // 大小
					String configType = ""; // 类型
					String descr_cn = ""; // 中文描述
					String unit = ""; // 单位
					ResultSet resultSet = manager.executeQuery(sql2.toString());
					while (resultSet.next()) {
						size = resultSet.getString("thevalue");
						configType = sindex;
						unit = resultSet.getString("unit");
						if ("PhysicalMemory".equalsIgnoreCase(configType)) {
							descr_cn = "物理内存";
						} else if ("SwapMemory".equalsIgnoreCase(configType)) {
							descr_cn = "Swap内存";
						}
					}
					resultSet.close();
					MemoryConfigInfo memoryConfigInfo = new MemoryConfigInfo();
					memoryConfigInfo.setSindex(sindex);
					memoryConfigInfo.setType(configType);
					memoryConfigInfo.setSize(size);
					memoryConfigInfo.setUnit(unit);
					memoryConfigInfo.setDescr_cn(descr_cn);
					memoryConfigInfoList.add(memoryConfigInfo);
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
		return memoryConfigInfoList;
	}

	
	public List<NodeTemp> getCurrPerMemoryList(String nodeid, String type, String subtype) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "' and sindex ='avg'");
		return findByCondition(sql.toString());
	}

	public List<NodeTemp> getCurrMemoryListInfo(String nodeid, String type, String subtype, String sindex) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "' and sindex ='" + sindex + "'");
		return findByCondition(sql.toString());
	}

	public List<NodeTemp> getCurrMemoryListInfo(String nodeid, String type, String subtype) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "'");
		return findByCondition(sql.toString());
	}

	@SuppressWarnings("unchecked")
	public List getCurrMemorySindex(String nodeid, String type, String subtype) throws SQLException {
		List sindexsList = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append(" select sindex from nms_memory_data_temp t where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "' group by sindex");
		try {
			rs = conn.executeQuery(sql.toString());
			while (rs.next()) {
				String sindex = rs.getString("sindex");
				sindexsList.add(sindex);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
			rs.close();
		}
		return sindexsList;
	}

	/**
	 * 得到内存信息
	 * 
	 * @param nodeid
	 * @param type
	 * @param subtype
	 * @return
	 */
	public Vector getMemoryInfo(String nodeid, String type, String subtype) {
		Vector retVector = new Vector();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_memory_data_temp where nodeid = '");
		sql.append(nodeid);
		sql.append("' and type = '");
		sql.append(type);
		sql.append("' and subtype = '");
		sql.append(subtype);
		sql.append("'");
		DBManager dbManager = new DBManager();
		try {
			rs = dbManager.executeQuery(sql.toString());
			while (rs.next()) {
				MemoryCollectEntity memorycollectdata = new MemoryCollectEntity();
				memorycollectdata.setEntity(rs.getString("subentity"));
				memorycollectdata.setSubentity(rs.getString("sindex"));
				memorycollectdata.setThevalue(rs.getString("thevalue"));
				memorycollectdata.setUnit(rs.getString("unit"));
				retVector.add(memorycollectdata);
			}
		} catch (SQLException e) {
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
		return retVector;
	}

	/**
	 * 得到每个节点对应的平局内存利用率
	 * 
	 * @param monitorNodelist
	 * @return
	 */
	public List<NodeTemp> getMemoryInfo(List monitorNodelist) {
		List<NodeTemp> retList = new ArrayList<NodeTemp>();
		StringBuffer sql = new StringBuffer();
		sql
				.append(" select nodeid,ip,type,subtype,avg(thevalue) avg_thevalue from nms_memory_data_temp where subentity='Utilization' and sindex <> 'VirtualMemory' and sindex <>  'SwapMemory' ");
		sql.append(" and nodeid in ('");
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
		sql.append("') group by nodeid");
		try {
			rs = conn.executeQuery(sql.toString());
			while (rs.next()) {
				NodeTemp vo = new NodeTemp();
				vo.setNodeid(rs.getString("nodeid"));
				vo.setIp(rs.getString("ip"));
				vo.setType(rs.getString("type"));
				vo.setSubtype(rs.getString("subtype"));
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
