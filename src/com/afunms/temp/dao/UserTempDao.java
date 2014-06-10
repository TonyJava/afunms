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
import com.afunms.detail.reomte.model.UserConfigInfo;
import com.afunms.polling.om.Usercollectdata;
import com.afunms.temp.model.NodeTemp;

@SuppressWarnings("unchecked")
public class UserTempDao extends BaseDao implements DaoInterface {

	public UserTempDao() {
		super("nms_user_data_temp");
	}

	/**
	 * 删除一条记录
	 */
	public boolean deleteByNodeIdSindex(String nodeid, String sid, String subentity) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_user_data_temp where nodeid='" + nodeid + "' and sindex='" + sid + "' and subentity='" + subentity + "'");
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
			conn.executeUpdate("delete from nms_user_data_temp where ip='" + ip + "'");
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
		sql.append("insert into nms_user_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('");
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
	public List<UserConfigInfo> getUserConfigInfoList(String nodeid, String type, String subtype, String[] subentities) {
		List<UserConfigInfo> userConfigInfoList = new ArrayList<UserConfigInfo>();
		StringBuffer sql = new StringBuffer();
		sql.append("select sindex from nms_user_data_temp where nodeid='");
		sql.append(nodeid);
		sql.append("' and type='");
		sql.append(type);
		sql.append("' and subtype='");
		sql.append(subtype);
		sql.append("' and entity='User' group by sindex");
		rs = conn.executeQuery(sql.toString());
		DBManager manager = new DBManager();
		try {
			while (rs.next()) {
				try {
					String sindex = rs.getString(1); // 用户配置索引
					StringBuffer sql2 = new StringBuffer();
					sql2.append("select * from nms_user_data_temp where ");
					sql2.append(" nodeid='" + nodeid + "' ");
					sql2.append(" and type='" + type + "' ");
					sql2.append(" and subtype='" + subtype + "'");
					sql2.append(" and sindex='" + sindex + "'");
					sql2.append(" and entity='User'");
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
					String name = ""; // 名称
					String userGroup = ""; // 用户组
					ResultSet resultSet = manager.executeQuery(sql2.toString());
					while (resultSet.next()) {
						name = sindex;
						userGroup = resultSet.getString("thevalue");
					}
					resultSet.close();
					UserConfigInfo userConfigInfo = new UserConfigInfo();
					userConfigInfo.setName(name);
					userConfigInfo.setUserGroup(userGroup);
					userConfigInfoList.add(userConfigInfo);
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
		return userConfigInfoList;
	}

	/**
	 * 得到用户信息
	 * 
	 * @param nodeid
	 * @param type
	 * @param subtype
	 * @return
	 */
	public Vector getUserInfo(String nodeid, String type, String subtype) {
		Vector retVector = new Vector();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_user_data_temp where nodeid = '");
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
				Usercollectdata usercollectdata = new Usercollectdata();
				usercollectdata.setEntity(rs.getString("subentity"));
				usercollectdata.setSubentity(rs.getString("sindex"));
				usercollectdata.setThevalue(rs.getString("thevalue"));
				retVector.add(usercollectdata);
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
	 * <p>
	 * 得到userlist 集合类型为 List<Hashtable<String,String>
	 * </p>
	 * <p>
	 * windows wmi
	 * </p>
	 * 
	 * @param nodeid
	 * @param type
	 * @param subtype
	 * @return
	 */

	public List getUserInfoList(String nodeid, String type, String subtype) {
		List retList = new ArrayList();
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select distinct sindex from nms_user_data_temp where nodeid = '");
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
				Hashtable tempHashtable = new Hashtable();
				String sindex = (String) sindexList.get(i);
				StringBuffer sql = new StringBuffer();
				sql.append("select * from nms_user_data_temp where nodeid = '");
				sql.append(nodeid);
				sql.append("' and type = '");
				sql.append(type);
				sql.append("' and subtype = '");
				sql.append(subtype);
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
}
