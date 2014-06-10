/**
 * <p>Description: app_db_node</p>
 * <p>Company:dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.dao;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.Informixspaceconfig;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;

@SuppressWarnings("unchecked")
public class InformixspaceconfigDao extends BaseDao implements DaoInterface {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public InformixspaceconfigDao() {
		super("system_infomixspaceconf");
	}

	public boolean save(BaseVo baseVo) {
		Informixspaceconfig vo = (Informixspaceconfig) baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into system_infomixspaceconf(ipaddress,spacename,linkuse,sms,bak,reportflag,alarmvalue)values(");
		sql.append("'");
		sql.append(vo.getIpaddress());
		sql.append("','");
		sql.append(vo.getSpacename());
		sql.append("','");
		sql.append(vo.getLinkuse());
		sql.append("',");
		sql.append(vo.getSms());
		sql.append(",'");
		sql.append(vo.getBak());
		sql.append("',");
		sql.append(vo.getReportflag());
		sql.append(",");
		sql.append(vo.getAlarmvalue());
		sql.append(")");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo baseVo) {
		Informixspaceconfig vo = (Informixspaceconfig) baseVo;
		boolean result = false;

		StringBuffer sql = new StringBuffer();
		sql.append("update system_infomixspaceconf set ipaddress='");
		sql.append(vo.getIpaddress());
		sql.append("',spacename='");
		sql.append(vo.getSpacename());
		sql.append("',linkuse='");
		sql.append(vo.getLinkuse());
		sql.append("',sms=");
		sql.append(vo.getSms());
		sql.append(",bak='");
		sql.append(vo.getBak());
		sql.append("',reportflag=");
		sql.append(vo.getReportflag());
		sql.append(",alarmvalue=");
		sql.append(vo.getAlarmvalue());
		sql.append(" where id=");
		sql.append(vo.getId());

		try {
			conn.executeUpdate(sql.toString());
			result = true;
		} catch (Exception e) {
			result = false;
			SysLogger.error("InformixspaceconfigDao:update()", e);
		} finally {
			conn.close();
		}

		return result;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		Informixspaceconfig vo = new Informixspaceconfig();
		try {
			vo.setId(rs.getInt("id"));
			vo.setIpaddress(rs.getString("ipaddress"));
			vo.setSpacename(rs.getString("spacename"));
			vo.setLinkuse(rs.getString("linkuse"));
			vo.setAlarmvalue(rs.getInt("alarmvalue"));
			vo.setBak(rs.getString("bak"));
			vo.setReportflag(rs.getInt("reportflag"));
			vo.setSms(rs.getInt("sms"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	/*
	 * 根据IP和是否要显示于日报表的标志位查询
	 * 
	 */
	public Hashtable getByAlarmflag(Integer smsflag) {
		List list = new ArrayList();
		Hashtable retValue = new Hashtable();
		try {
			rs = conn.executeQuery("select * from system_infomixspaceconf where sms=" + smsflag + " order by ipaddress");
			while (rs.next())
				list.add(loadFromRS(rs));
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Informixspaceconfig informixspaceconfig = (Informixspaceconfig) list.get(i);
					retValue.put(informixspaceconfig.getIpaddress() + ":" + informixspaceconfig.getSpacename(), informixspaceconfig);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retValue;
	}

	/*
	 * 根据IP查询
	 * 
	 */
	public List getByIp(String ipaddress, Integer smsflag) {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select * from system_infomixspaceconf where ipaddress = '" + ipaddress + "' and sms=" + smsflag + " order by ipaddress");
			while (rs.next())
				list.add(loadFromRS(rs));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/*
	 * 根据IP查询
	 * 
	 */
	public List getByIp(String ipaddress) {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select * from system_infomixspaceconf where ipaddress = '" + ipaddress + "' order by ipaddress");
			while (rs.next())
				list.add(loadFromRS(rs));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/*
	 * 
	 * 从内存和数据库表里获取每个Informix数据库表空间信息，存入表空间配置表里
	 */
	public void fromLastToInformixspaceconfig() throws Exception {
		List list = new ArrayList();
		List list1 = new ArrayList();
		List shareList = new ArrayList();
		Hashtable informixspacehash = new Hashtable();
		try {
			rs = conn.executeQuery("select * from system_infomixspaceconf order by ipaddress");
			while (rs.next())
				list1.add(loadFromRS(rs));
			if (list1 != null && list1.size() > 0) {
				for (int i = 0; i < list1.size(); i++) {
					Informixspaceconfig informixspaceconfig = (Informixspaceconfig) list1.get(i);
					informixspacehash.put(informixspaceconfig.getIpaddress() + ":" + informixspaceconfig.getSpacename(), informixspaceconfig);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {

			// 从数据库得到监视INFORMIXSPACCE列表
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = typedao.findByDbtype("informix");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}
			DBDao dbdao = new DBDao();
			try {
				shareList = dbdao.getDbByTypeMonFlag(typevo.getId(), 1);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbdao.close();
			}
			if (shareList != null && shareList.size() > 0) {
				for (int i = 0; i < shareList.size(); i++) {
					DBVo dbmonitorlist = (DBVo) shareList.get(i);
					String hex = IpTranslation.formIpToHex(dbmonitorlist.getIpAddress());
					String serverip = hex + ":" + dbmonitorlist.getDbName();
					DBDao dao = new DBDao();
					List databaseList = null;
					try {
						databaseList = dao.getInformix_nmsspace(serverip);
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						dao.close();
					}

					Hashtable spaces = new Hashtable();
					spaces.put("ip", dbmonitorlist.getIpAddress());
					spaces.put("tableinfo_v", databaseList);
					list.add(spaces);
				}
			}
			// 判断采集到的INFROMIXSPACCE信息是否已经在INFORMIXSPACCE配置表里已经存在，若不存在则加入
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Hashtable spaces = (Hashtable) list.get(i);
					if (spaces != null && spaces.size() > 0) {
						String ip = (String) spaces.get("ip");
						List tableinfo_v = (List) spaces.get("tableinfo_v");
						if (tableinfo_v != null && tableinfo_v.size() > 0) {
							Informixspaceconfig informixspaceconfig = null;
							for (int k = 0; k < tableinfo_v.size(); k++) {
								Hashtable return_value = (Hashtable) tableinfo_v.get(k);
								String spacename = (String) return_value.get("dbspace");
								if (!informixspacehash.containsKey(ip + ":" + spacename)) {
									informixspaceconfig = new Informixspaceconfig();
									informixspaceconfig.setSpacename(spacename);
									informixspaceconfig.setBak("");
									informixspaceconfig.setIpaddress(ip);
									informixspaceconfig.setLinkuse("");
									informixspaceconfig.setAlarmvalue(90);
									informixspaceconfig.setSms(new Integer(0));// 0：不告警
									// 1：告警，默认的情况是不发送短信
									informixspaceconfig.setReportflag(new Integer(0));// 0：不存在于报表
									// 1：存在于报表，默认的情况是不存在于报表
									try {
										conn.close();
										conn = new DBManager();
										save(informixspaceconfig);
										informixspacehash.put(ip + ":" + spacename, informixspaceconfig);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
							conn.close();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteByIP(String ip) {
		String sql = "delete from system_infomixspaceconf where ipaddress='" + ip + "'";
		try {
			conn.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}