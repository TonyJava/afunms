package com.afunms.temp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.detail.reomte.model.FibreConfigInfo;
import com.afunms.temp.model.NodeTemp;

public class FbconfigTempDao extends BaseDao implements DaoInterface {

	public FbconfigTempDao() {
		super("nms_fibreconfig_data_temp");
	}

	/**
	 * 删除一条记录
	 */
	public boolean deleteByNodeIdSindex(String nodeid, String sid, String subentity) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_fibreconfig_data_temp where nodeid='" + nodeid + "' and sindex='" + sid + "' and subentity='" + subentity + "'");
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
			conn.executeUpdate("delete from nms_fibreconfig_data_temp where ip='" + ip + "'");
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
		sql.append("insert into nms_fibreconfig_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
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

	public List<FibreConfigInfo> getFibreConfigInfoList(String nodeid, String type, String subtype, String[] subentities) {
		List<FibreConfigInfo> fibreConfigInfoList = new ArrayList<FibreConfigInfo>();
		StringBuffer sql = new StringBuffer();
		sql.append("select sindex from nms_fibreconfig_data_temp where nodeid='");
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
					sql2.append("select * from nms_fibreconfig_data_temp where ");
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

					String fabricName = ""; // 描述
					String elementName = ""; // 元素名称
					String moduleName = ""; // 模块名称
					String moduleCapacity = ""; // 模块数量
					String moduleDescr = ""; // 模块描述
					String feModuleObjectID = ""; // 模块OID
					String moduleOperStatus = ""; // 模块状态
					String upTime = ""; // 启动时间
					String moduleFxPortCapacity = ""; // 端口数
					ResultSet resultSet = manager.executeQuery(sql2.toString());
					while (resultSet.next()) {
						String subentity = resultSet.getString("subentity");
						String thevalue = resultSet.getString("thevalue");
						if ("FabricName".equalsIgnoreCase(subentity)) {
							fabricName = thevalue;
						} else if ("ElementName".equalsIgnoreCase(subentity)) {
							elementName = thevalue;
						} else if ("ModuleName".equalsIgnoreCase(subentity)) {
							moduleName = thevalue;
						} else if ("ModuleCapacity".equalsIgnoreCase(subentity)) {
							moduleCapacity = thevalue;
						} else if ("ModuleDescr".equalsIgnoreCase(subentity)) {
							moduleDescr = thevalue;
						} else if ("FeModuleObjectID".equalsIgnoreCase(subentity)) {
							feModuleObjectID = thevalue;
						} else if ("ModuleOperStatus".equalsIgnoreCase(subentity)) {
							moduleOperStatus = thevalue;
						} else if ("UPTime".equalsIgnoreCase(subentity)) {
							upTime = thevalue;
						} else if ("ModuleFxPortCapacity".equalsIgnoreCase(subentity)) {
							moduleFxPortCapacity = thevalue;
						}
					}
					resultSet.close();
					FibreConfigInfo fibreConfigInfo = new FibreConfigInfo();
					fibreConfigInfo.setSindex(sindex);
					fibreConfigInfo.setFabricName(fabricName);
					fibreConfigInfo.setElementName(elementName);
					fibreConfigInfo.setModuleName(moduleName);
					fibreConfigInfo.setModuleCapacity(moduleCapacity);
					fibreConfigInfo.setModuleDescr(moduleDescr);
					fibreConfigInfo.setFeModuleObjectID(feModuleObjectID);
					fibreConfigInfo.setModuleOperStatus(moduleOperStatus);
					fibreConfigInfo.setUpTime(upTime);
					fibreConfigInfo.setModuleFxPortCapacity(moduleFxPortCapacity);

					fibreConfigInfoList.add(fibreConfigInfo);
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
		return fibreConfigInfoList;
	}

}
