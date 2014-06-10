package com.afunms.temp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.detail.reomte.model.FibreCapabilityInfo;
import com.afunms.temp.model.NodeTemp;

public class ChannelTempDao extends BaseDao implements DaoInterface {

	public ChannelTempDao() {
		super("nms_fibrecapability_data_temp");
	}

	/**
	 * 删除一条记录
	 */
	public boolean deleteByNodeIdSindex(String nodeid, String sid, String subentity) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_fibrecapability_data_temp where nodeid='" + nodeid + "' and sindex='" + sid + "' and subentity='" + subentity + "'");
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
			conn.executeUpdate("delete from nms_fibrecapability_data_temp where ip='" + ip + "'");
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
		sql.append("insert into nms_fibrecapability_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
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

	public List<FibreCapabilityInfo> getFibreCapabilityInfoList(String nodeid, String type, String subtype, String[] subentities) {
		List<FibreCapabilityInfo> fibreCapabilityInfoList = new ArrayList<FibreCapabilityInfo>();
		StringBuffer sql = new StringBuffer();
		sql.append("select sindex from nms_fibrecapability_data_temp where nodeid='");
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
					String sindex = rs.getString(1); // 光口配置索引
					StringBuffer sql2 = new StringBuffer();
					sql2.append("select * from nms_fibrecapability_data_temp where ");
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
					String portName = ""; // 光口名称
					String portPhysOperStatus = ""; // 当前状态
					String portC3InFrames = ""; // 入口帧数
					String portC3OutFrames = ""; // 出口帧数
					String portC3InOctets = ""; // 入口帧字节数
					String portC3OutOctets = ""; // 出口帧字节数
					String portC3Discards = ""; // 丢包数
					ResultSet resultSet = manager.executeQuery(sql2.toString());
					while (resultSet.next()) {
						String subentity = resultSet.getString("subentity");
						String thevalue = resultSet.getString("thevalue");
						if ("PortName".equalsIgnoreCase(subentity)) {
							portName = thevalue;
						}
						if ("PortPhysOperStatus".equalsIgnoreCase(subentity)) {
							portPhysOperStatus = thevalue;
						}
						if ("PortC3InFrames".equalsIgnoreCase(subentity)) {
							portC3InFrames = thevalue;
						}
						if ("PortC3OutFrames".equalsIgnoreCase(subentity)) {
							portC3OutFrames = thevalue;
						}
						if ("PortC3InOctets".equalsIgnoreCase(subentity)) {
							portC3InOctets = thevalue;
						}
						if ("PortC3OutOctets".equalsIgnoreCase(subentity)) {
							portC3OutOctets = thevalue;
						}
						if ("PortC3Discards".equalsIgnoreCase(subentity)) {
							portC3Discards = thevalue;
						}
					}
					resultSet.close();
					FibreCapabilityInfo fibreCapabilityInfo = new FibreCapabilityInfo();
					fibreCapabilityInfo.setSindex(sindex);
					fibreCapabilityInfo.setPortName(portName);
					fibreCapabilityInfo.setPortPhysOperStatus(portPhysOperStatus);
					fibreCapabilityInfo.setPortC3InFrames(portC3InFrames);
					fibreCapabilityInfo.setPortC3OutFrames(portC3OutFrames);
					fibreCapabilityInfo.setPortC3InOctets(portC3InOctets);
					fibreCapabilityInfo.setPortC3OutOctets(portC3OutOctets);
					fibreCapabilityInfo.setPortC3Discards(portC3Discards);

					fibreCapabilityInfoList.add(fibreCapabilityInfo);
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
		return fibreCapabilityInfoList;
	}

}
