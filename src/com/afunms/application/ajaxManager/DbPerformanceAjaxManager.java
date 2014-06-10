package com.afunms.application.ajaxManager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.jfree.data.general.DefaultPieDataset;

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.dao.MySqlSpaceConfigDao;
import com.afunms.application.dao.OraspaceconfigDao;
import com.afunms.application.dao.SqldbconfigDao;
import com.afunms.application.manage.HostApplyManager;
import com.afunms.application.manage.OracleManager;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.MonitorDBDTO;
import com.afunms.application.model.OracleLockInfo;
import com.afunms.application.util.DBPool;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.CreateAmColumnPic;
import com.afunms.common.util.CreateMetersPic;
import com.afunms.common.util.CreatePiePicture;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.dao.BusinessDao;
import com.afunms.config.model.Business;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.loader.DBLoader;
import com.afunms.system.model.User;
import com.afunms.topology.util.KeyGenerator;

@SuppressWarnings("rawtypes")
public class DbPerformanceAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {
	NumberFormat df = new DecimalFormat("#.##");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static String[] oracleTbArray = null;
	private static String[] sqlServerTbArray = null;
	private static Hashtable<String, String> rsc_type_ht = new Hashtable<String, String>();
	private static Hashtable<String, String> req_mode_ht = new Hashtable<String, String>();
	private static Hashtable<String, String> mode_ht = new Hashtable<String, String>();
	private static Hashtable<String, String> req_status_ht = new Hashtable<String, String>();
	private static Hashtable<String, String> req_ownertype_ht = new Hashtable<String, String>();

	static {
		oracleTbArray = new String[] { "nms_oracontrfile", "nms_oracursors", "nms_oradbio", "nms_oraextent", "nms_oraisarchive", "nms_orakeepobj", "nms_oralock", "nms_oralogfile", "nms_oramemperfvalue", "nms_oramemvalue", "nms_orarollback", "nms_orasessiondata", "nms_oraspaces", "nms_orastatus", "nms_orasys", "nms_oratables", "nms_oratopsql", "nms_orauserinfo", "nms_orawait", "nms_oratopsql_sort", "nms_oratopsql_readwrite", "nms_oralockinfo", "nms_orabaseinfo" };
		sqlServerTbArray = new String[] { "nms_sqlservercaches", "nms_sqlserverconns", "nms_sqlserverdbvalue", "nms_sqlserverinfo_v", "nms_sqlserverlockinfo_v", "nms_sqlserverlocks", "nms_sqlservermems", "nms_sqlserverpages", "nms_sqlserverscans", "nms_sqlserversqls", "nms_sqlserverstatisticshash", "nms_sqlserverstatus", "nms_sqlserversysvalue" };

		rsc_type_ht.put("1", "NULL ��Դ��δʹ�ã�");
		rsc_type_ht.put("2", "���ݿ�");
		rsc_type_ht.put("3", "�ļ�");
		rsc_type_ht.put("4", "����");
		rsc_type_ht.put("5", "��");
		rsc_type_ht.put("6", "ҳ");
		rsc_type_ht.put("7", "��");
		rsc_type_ht.put("8", "��չ����");
		rsc_type_ht.put("9", "RID���� ID)");
		rsc_type_ht.put("10", "Ӧ�ó���");

		req_mode_ht.put("0", "NULL");
		req_mode_ht.put("1", "Sch-S");
		req_mode_ht.put("2", "Sch-M");
		req_mode_ht.put("3", "S");
		req_mode_ht.put("4", "U");
		req_mode_ht.put("5", "X");
		req_mode_ht.put("6", "IS");
		req_mode_ht.put("7", "IU");
		req_mode_ht.put("8", "IX");
		req_mode_ht.put("9", "SIU");
		req_mode_ht.put("10", "SIX");
		req_mode_ht.put("11", "UIX");
		req_mode_ht.put("12", "BU");
		req_mode_ht.put("13", "RangeS_S");
		req_mode_ht.put("14", "RangeS_U");
		req_mode_ht.put("15", "RangeI_N");
		req_mode_ht.put("16", "RangeI_S");
		req_mode_ht.put("17", "RangeI_U");
		req_mode_ht.put("18", "RangeI_X");
		req_mode_ht.put("19", "RangeX_S");
		req_mode_ht.put("20", "RangeX_U");
		req_mode_ht.put("21", "RangeX_X");

		req_status_ht.put("1", "������	");
		req_status_ht.put("2", "����ת��");
		req_status_ht.put("3", "���ڵȴ�");

		req_ownertype_ht.put("1", "����");
		req_ownertype_ht.put("2", "�α�");
		req_ownertype_ht.put("3", "�Ự");
		req_ownertype_ht.put("4", "ExSession");

		mode_ht.put("0", "����Ȩ������Դ");
		mode_ht.put("1", "�ܹ��ȶ��ԣ�ȷ�������κλỰ���Ƽܹ�Ԫ���ϵļܹ��ȶ�����ʱ��ȥ�ܹ�Ԫ�أ�����������");
		mode_ht.put("2", "�ܹ��޸ģ��������κ�Ҫ����ָ����Դ�ܹ��ĻỰ���п��ơ�ȷ��û�������ĻỰ��������ָ���Ķ���");
		mode_ht.put("3", "������Ȩ���ƻỰ����Դ���й�����ʡ�");
		mode_ht.put("4", "���£���ʾ�����տ��ܸ��µ���Դ�ϻ�ȡ�����������ڷ�ֹ������ʽ�����������������ڶ���Ự������Դ�����Ժ���ܸ�����Դʱ������");
		mode_ht.put("5", "��������Ȩ���ƻỰ����Դ�����������ʡ�");
		mode_ht.put("6", "��������ʾ���⽫ S ������������νṹ�ڵ�ĳ��������Դ�ϡ�");
		mode_ht.put("7", "������£���ʾ���⽫ U ������������νṹ�ڵ�ĳ��������Դ�ϡ�");
		mode_ht.put("8", "������������ʾ���⽫ X ������������νṹ�ڵ�ĳ��������Դ�ϡ�");
		mode_ht.put("9", "����������£���ʾ������������νṹ�ڵĴ�����Դ�ϻ�ȡ����������Դ���й�����ʡ�");
		mode_ht.put("10", "����������������ʾ������������νṹ�ڵĴ�����Դ�ϻ�ȡ����������Դ���й�����ʡ�");
		mode_ht.put("11", "����������������ʾ��������������������νṹ�ڵĴ�����Դ�ϻ�ȡ����������Դ��");
		mode_ht.put("12", "����������");
		mode_ht.put("13", "�������Χ�͹�����Դ������ʾ�ɴ��з�Χɨ�衣");
		mode_ht.put("14", "�������Χ�͸�����Դ������ʾ�ɴ��и���ɨ�衣");
		mode_ht.put("15", "�������Χ�Ϳ���Դ���������������в����¼�֮ǰ���Է�Χ��");
		mode_ht.put("16", "ͨ�� RangeI_N �� S �����ص������ļ���Χת����");
		mode_ht.put("17", "ͨ�� RangeI_N �� U �����ص������ļ���Χת����");
		mode_ht.put("18", "ͨ�� RangeI_N �� X �����ص������ļ���Χת����");
		mode_ht.put("19", "ͨ�� RangeI_N �� RangeS_S �����ص������ļ���Χת����");
		mode_ht.put("20", "ͨ�� RangeI_N �� RangeS_U �����ص������ļ���Χת����");
		mode_ht.put("21", "��������Χ��������Դ������ת�����ڸ��·�Χ�еļ�ʱʹ�á�");

	}

	// by lyl
	private void getDbNodeDataByType() {
		String type = getParaValue("type");
		int dbtype = 1;
		dbtype = getDbtypeFromString(type);
		List list = new ArrayList();
		String sql = "";
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		String curentbids = current_user.getBusinessids();

		String selectbids = getParaValue("selectbids");

		StringBuffer sql1 = new StringBuffer();
		StringBuffer s1 = new StringBuffer();
		StringBuffer sql2 = new StringBuffer();
		StringBuffer s2 = new StringBuffer();
		int flag = 0;

		if (selectbids != null) {
			if (selectbids != "-1") {
				String[] bids = selectbids.split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (flag == 0) {
								s2.append(" and ( bid like '%" + bids[i].trim() + "%' ");
								flag = 1;
							} else {
								s2.append(" or bid like '%" + bids[i].trim() + "%' ");
							}
						}
					}
					s2.append(") ");
				}

			}
		}
		sql2.append("select * from app_db_node where 1=1 and dbtype=" + dbtype + s2.toString());

		flag = 0;
		if (current_user.getRole() != 0 && curentbids != null) {
			if (curentbids != "-1") {
				String[] bids = curentbids.split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (flag == 0) {
								s1.append(" and ( bid like '%" + bids[i].trim() + "%' ");
								flag = 1;
							} else {
								s1.append(" or bid like '%" + bids[i].trim() + "%' ");
							}
						}
					}
					s1.append(") ");
				}

			}
		}
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			sql1.append("select * from (" + sql2.toString() + ") as t where 1=1 " + s1.toString());
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			sql1.append("select * from (" + sql2.toString() + ") where 1=1 " + s1.toString());
		}

		String treeBid = request.getParameter("treeBid");
		if (treeBid != null && treeBid.trim().length() > 0) {
			treeBid = treeBid.trim();
			treeBid = "," + treeBid + ",";
			String[] treeBids = treeBid.split(",");
			if (treeBids != null) {
				for (int i = 0; i < treeBids.length; i++) {
					if (treeBids[i].trim().length() > 0) {
						sql1 = sql1.append(" and " + "bid" + " like '%," + treeBids[i].trim() + ",%'");
					}
				}
			}
		}

		sql = sql1.toString();
		DBDao dao = new DBDao();
		try {
			list = dao.findByCriteria(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		List monitorDBDTOList = new ArrayList();

		for (int i = 0; i < list.size(); i++) {
			DBVo vo = (DBVo) list.get(i);
			MonitorDBDTO monitorDBDTO = null;
			monitorDBDTO = getMonitorDBDTOByDBVo(vo, 0);
			monitorDBDTOList.add(monitorDBDTO);
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != monitorDBDTOList && monitorDBDTOList.size() > 0) {
			for (int i = 0; i < monitorDBDTOList.size(); i++) {
				MonitorDBDTO monitorDBDTO = (MonitorDBDTO) monitorDBDTOList.get(i);
				Hashtable eventListSummary = monitorDBDTO.getEventListSummary();
				String generalAlarm = (String) eventListSummary.get("generalAlarm");
				String urgentAlarm = (String) eventListSummary.get("urgentAlarm");
				String seriousAlarm = (String) eventListSummary.get("seriousAlarm");

				String status = monitorDBDTO.getStatus();

				String statusImg = "";
				if ("1".equals(status)) {
					statusImg = "a_level_1.gif";
				} else if ("2".equals(status)) {
					statusImg = "a_level_2.gif";
				} else if ("3".equals(status)) {
					statusImg = "a_level_3.gif";
				} else {
					statusImg = "a_level_0.gif";
				}
				jsonString.append("{\"dbid\":\"");
				jsonString.append(monitorDBDTO.getId());
				jsonString.append("\",");

				jsonString.append("\"alias\":\"");
				jsonString.append(monitorDBDTO.getAlias());
				jsonString.append("\",");

				jsonString.append("\"dbtype\":\"");
				jsonString.append(monitorDBDTO.getDbtype());
				jsonString.append("\",");

				jsonString.append("\"dbname\":\"");
				jsonString.append(monitorDBDTO.getDbname());
				jsonString.append("\",");

				jsonString.append("\"ipaddress\":\"");
				jsonString.append(monitorDBDTO.getIpAddress());
				jsonString.append("\",");

				jsonString.append("\"port\":\"");
				jsonString.append(monitorDBDTO.getPort());
				jsonString.append("\",");

				jsonString.append("\"managed\":\"");
				jsonString.append(monitorDBDTO.getMon_flag());
				jsonString.append("\",");

				jsonString.append("\"status\":\"");
				jsonString.append(statusImg);
				jsonString.append("\",");

				jsonString.append("\"generalAlarm\":\"");
				jsonString.append(generalAlarm);
				jsonString.append("\",");

				jsonString.append("\"urgentAlarm\":\"");
				jsonString.append(urgentAlarm);
				jsonString.append("\",");

				jsonString.append("\"seriousAlarm\":\"");
				jsonString.append(seriousAlarm);
				jsonString.append("\",");

				jsonString.append("\"pingvalue\":\"");
				jsonString.append(monitorDBDTO.getPingValue());
				jsonString.append("\"}");

				if (i != monitorDBDTOList.size() - 1) {
					jsonString.append(",");
				}

			}
		}
		jsonString.append("],total:" + monitorDBDTOList.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	/**
	 * ͨ�� DBVo ����װ MonitorDBDTO
	 * 
	 * @author nielin
	 * @date 2010-08-13
	 * @param <code>DBVo</code>
	 * @return
	 */
	public MonitorDBDTO getMonitorDBDTOByDBVo(DBVo vo, int sid) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = simpleDateFormat.format(new Date());
		String starttime = date + " 00:00:00";
		String totime = date + " 23:59:59";

		int id = vo.getId(); // id
		String ipAddress = vo.getIpAddress(); // ipaddress
		String alias = vo.getAlias(); // ����
		String dbname = vo.getDbName(); // ���ݿ�����
		String port = vo.getPort(); // �˿�
		String mon_flag = "��";

		String dbtype = ""; // ���ݿ�����
		String status = ""; // ״̬

		String pingValue = "δ֪"; // ������
		int alarmLevel = 0;
		Hashtable eventListSummary = new Hashtable(); // �澯

		if (vo.getManaged() == 1) {
			mon_flag = "��";
		}
		Hashtable checkEventHashtable = ShareData.getCheckEventHash();
		NodeUtil nodeUtil = new NodeUtil();
		Node DBNode = null;
		if (sid != 0) {
			DBNode = PollingEngine.getInstance().getDbByID(sid);
		} else {
			DBNode = PollingEngine.getInstance().getDbByID(vo.getId());
		}
		NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(DBNode);
		if (nodeDTO != null) {
			String chexkname = id + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
			if (checkEventHashtable != null) {
				for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
					String key = (String) it.next();
					if (key.startsWith(chexkname)) {
						if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
							alarmLevel = (Integer) checkEventHashtable.get(key);
						}
					}
				}
			}
		}
		status = alarmLevel + "";
		DBTypeDao typedao = new DBTypeDao();
		DBTypeVo dbTypeVo = null;
		try {
			dbTypeVo = (DBTypeVo) typedao.findByID(String.valueOf(vo.getDbtype()));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			typedao.close();
		}
		if (dbTypeVo != null) {
			dbtype = dbTypeVo.getDbtype();
		} else {
			dbtype = "δ֪";
		}

		if ("Oracle".equalsIgnoreCase(dbtype)) {
			DBDao dao = new DBDao();
			Hashtable oracleHash = (Hashtable) ShareData.getSharedata().get(ipAddress + ":" + id);
			try {
				String statusStr = "-1";
				if (oracleHash != null) {
					statusStr = String.valueOf(oracleHash.get("ping"));
				}
				if ("100".equals(statusStr)) {
					pingValue = "��������";
				} else if ("0".equals(statusStr)) {
					pingValue = "����ֹͣ";
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
		} else if ("SQLServer".equalsIgnoreCase(dbtype)) {
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + vo.getAlias();
			Hashtable sqlserverHash = (Hashtable) ShareData.getSharedata().get(serverip);
			try {
				String statusStr = "-1";
				if (sqlserverHash != null) {
					statusStr = String.valueOf(sqlserverHash.get("ping"));
				}
				if ("100".equals(statusStr)) {
					pingValue = "��������";
				} else if ("0".equals(statusStr)) {
					pingValue = "����ֹͣ";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("MySql".equalsIgnoreCase(dbtype)) {
			Hashtable mysqlHashtable = (Hashtable) ShareData.getMySqlmonitordata().get(vo.getIpAddress());
			if (null != mysqlHashtable) {
				pingValue = (String) mysqlHashtable.get("runningflag");
			}
		} else if ("DB2".equalsIgnoreCase(dbtype)) {
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			DBDao dao = new DBDao();
			String serverip = hex + ":" + vo.getId();
			String statusStr = "0";
			Hashtable tempStatusHashtable = null;
			try {
				tempStatusHashtable = dao.getDB2_nmsstatus(serverip + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dao != null) {
					dao.close();
				}
			}
			if (tempStatusHashtable != null && tempStatusHashtable.containsKey("status")) {
				statusStr = (String) tempStatusHashtable.get("status");
			}
			if (statusStr.equals("1")) {
				pingValue = "��������";
			}
		} else if ("Sybase".equalsIgnoreCase(dbtype)) {
			Hashtable sysbaseHashtable = (Hashtable) ShareData.getSysbasedata().get(vo.getIpAddress());
			if (null != sysbaseHashtable) {
				pingValue = (String) sysbaseHashtable.get("runningflag");
			}
		} else if ("Informix".equalsIgnoreCase(dbtype)) {
			DBDao dao = new DBDao();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + vo.getDbName();
			String statusStr;
			try {
				statusStr = String.valueOf(((Hashtable) dao.getInformix_nmsstatus(serverip)).get("status"));
				if ("1".equalsIgnoreCase(statusStr)) {
					pingValue = "��������";
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
		}

		String generalAlarm = "0"; // ��ͨ�澯�� Ĭ��Ϊ 0
		String urgentAlarm = "0"; // ���ظ澯�� Ĭ��Ϊ 0
		String seriousAlarm = "0"; // �����澯�� Ĭ��Ϊ 0

		EventListDao eventListDao = new EventListDao();
		try {
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where subtype = 'db' and nodeid='" + id + "'" + " and level1='1' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				urgentAlarm = eventListDao.getCountByWhere(" where  subtype = 'db' and nodeid='" + id + "'" + " and level1='2' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				seriousAlarm = eventListDao.getCountByWhere(" where  subtype = 'db' and nodeid='" + id + "'" + " and level1='3' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where subtype = 'db' and nodeid='" + id + "'" + " and level1='1' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				urgentAlarm = eventListDao.getCountByWhere(" where  subtype = 'db' and nodeid='" + id + "'" + " and level1='2' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
				seriousAlarm = eventListDao.getCountByWhere(" where  subtype = 'db' and nodeid='" + id + "'" + " and level1='3' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') and recordtime<=to_date('" + totime + "','YYYY-MM-DD HH24:MI:SS')");
			}

		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			eventListDao.close();
		}
		eventListSummary.put("generalAlarm", generalAlarm);
		eventListSummary.put("urgentAlarm", urgentAlarm);
		eventListSummary.put("seriousAlarm", seriousAlarm);

		MonitorDBDTO monitorDBDTO = new MonitorDBDTO();
		monitorDBDTO.setId(id);
		monitorDBDTO.setAlias(alias);
		monitorDBDTO.setDbname(dbname);
		monitorDBDTO.setDbtype(dbtype);
		monitorDBDTO.setPingValue(pingValue);
		monitorDBDTO.setEventListSummary(eventListSummary);
		monitorDBDTO.setIpAddress(ipAddress);
		monitorDBDTO.setPort(port);
		monitorDBDTO.setStatus(status);
		monitorDBDTO.setMon_flag(mon_flag);

		return monitorDBDTO;
	}

	@SuppressWarnings("unchecked")
	private void addDb() {
		DBVo vo = new DBVo();
		vo.setUser(getParaValue("user"));
		String password = getParaValue("password");
		String enpassword = "";
		try {
			enpassword = EncryptUtil.encode(password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		vo.setPassword(enpassword);
		vo.setAlias(getParaValue("alias"));
		vo.setIpAddress(getParaValue("ip_address"));
		vo.setPort(getParaValue("port"));
		vo.setDbName(getParaValue("db_name"));
		vo.setCategory(getParaIntValue("category"));
		vo.setDbuse(getParaValue("dbuse"));
		vo.setBid(getParaValue("bid"));
		vo.setManaged(getParaIntValue("managed"));
		vo.setDbtype(getParaIntValue("dbtype"));
		vo.setCollecttype(getParaIntValue("collecttype"));
		vo.setId(KeyGenerator.getInstance().getNextKey());
		DBLoader dbloader = new DBLoader();
		dbloader.loadOne(vo);
		// �ŵ��ڴ���
		ShareData.getDBList().add(vo);
		DBDao dao = new DBDao();
		try {
			dao.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		// ����Ӧ��
		HostApplyManager.save(vo);
		// ˢ���ڴ��е����ݿ��б�
		new DBLoader().refreshDBConfiglist();

		DBTypeVo dbTypeVo = null;
		DBTypeDao typedao = null;
		try {
			typedao = new DBTypeDao();
			dbTypeVo = (DBTypeVo) typedao.findByID(getParaValue("dbtype"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			typedao.close();
		}

		// ��ʼ���澯ָ��
		try {
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(String.valueOf(vo.getId()), AlarmConstant.TYPE_DB, dbTypeVo.getDbtype());
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		// ��ʼ���ɼ�ָ��
		try {
			NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
			if (vo.getCollecttype() == 2) {
				vo.setCollecttype(1);
			}
			nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(vo.getId() + "", AlarmConstant.TYPE_DB, dbTypeVo.getDbtype(), "1", vo.getCollecttype());
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		// �����ݿ�������ݲɼ�
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
		List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
		try {
			// ��ȡ�����õ����ݿ����б�����ָ��
			monitorItemList = indicatorsdao.getByNodeId(vo.getId() + "", 1, "db", dbTypeVo.getDbtype());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			indicatorsdao.close();
		}
		if (monitorItemList == null) {
			monitorItemList = new ArrayList<NodeGatherIndicators>();
		}
		Hashtable gatherHash = new Hashtable();
		for (int i = 0; i < monitorItemList.size(); i++) {
			NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators) monitorItemList.get(i);
			// ���ݿ�ɼ�ָ��
			gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
		}

		out.print("��ӳɹ�");
		out.flush();
	}

	private void deleteDbs() {
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		if (ids != null && ids.length > 0) {
			DBTypeDao typeDao = null;
			DBDao dbDao = null;
			DBTypeVo typeVo = null;
			DBVo vo = null;

			NodeGatherIndicatorsDao nodeDao = null;
			AlarmIndicatorsNodeDao alarmDao = null;

			String hex = (String) null;
			String serverIp = (String) null;
			for (int i = 0; i < ids.length; i++) {
				String id = ids[i];
				try {
					typeDao = new DBTypeDao();
					dbDao = new DBDao();
					vo = (DBVo) dbDao.findByID(id);
					typeVo = (DBTypeVo) typeDao.findByID(String.valueOf(vo.getDbtype()));

					// ɾ���ɼ�ָ��
					nodeDao = new NodeGatherIndicatorsDao();
					try {
						nodeDao.deleteByNodeIdAndTypeAndSubtype(id, "db", typeVo.getDbtype().toLowerCase());
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						nodeDao.close();
					}
					// ɾ���澯��ֵ
					alarmDao = new AlarmIndicatorsNodeDao();
					try {
						alarmDao.deleteByNodeId(id, "db", typeVo.getDbtype().toLowerCase());
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						alarmDao.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (typeVo.getDbtype().toLowerCase().equals("oracle")) {
					dbDao = new DBDao();
					OraspaceconfigDao configDao = new OraspaceconfigDao();
					hex = IpTranslation.formIpToHex(vo.getIpAddress());
					serverIp = hex + ":" + id;
					try {
						configDao.deleteByIP(id);
						dbDao.clearTable("system_oraspaceconf", serverIp);
						dbDao.clearTablesData(oracleTbArray, serverIp);
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						if (null != dbDao) {
							dbDao.close();
						}
						if (null != configDao) {
							configDao.close();
						}
					}
				} else if (typeVo.getDbtype().toLowerCase().equals("sqlserver")) {
					dbDao = new DBDao();
					SqldbconfigDao sqlConfigDao = new SqldbconfigDao();
					hex = IpTranslation.formIpToHex(vo.getIpAddress());
					serverIp = hex + ":" + vo.getAlias();
					try {
						sqlConfigDao.deleteByIP(id);
						dbDao.clearTable("system_sqldbconf", vo.getIpAddress());
						dbDao.clearTablesData(sqlServerTbArray, serverIp);
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						if (null != dbDao) {
							dbDao.close();
						}
						if (null != sqlConfigDao) {
							sqlConfigDao.close();
						}
					}
					ShareData.getSqlserverdata().remove(vo.getIpAddress());
				} else if (typeVo.getDbtype().toLowerCase().equals("mysql")) {
					dbDao = new DBDao();
					MySqlSpaceConfigDao mysqlConfigDao = new MySqlSpaceConfigDao();
					hex = IpTranslation.formIpToHex(vo.getIpAddress());
					serverIp = hex + ":" + vo.getId();
					try {
						mysqlConfigDao.deleteByIP(id);
						dbDao.clearTable("system_mysqlspaceconf", vo.getIpAddress());
						dbDao.clearTableData("nms_mysqlinfo", serverIp);
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						if (null != dbDao) {
							dbDao.close();
						}
						if (null != mysqlConfigDao) {
							mysqlConfigDao.close();
						}
					}
					ShareData.getMySqlmonitordata().remove(vo.getIpAddress());
				}

				dbDao = new DBDao();
				try {
					dbDao.delete(id);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (null != dbDao) {
						dbDao.close();
					}
				}
				int nodeId = Integer.parseInt(id);
				PollingEngine.getInstance().deleteDbByID(nodeId);
				DBPool.getInstance().removeConnect(nodeId);
				new DBLoader().refreshDBConfiglist();
			}
			out.print("�����ɹ�");
			out.flush();
		}
	}

	private void getMysqlNodeSystem() {
		DBVo vo = new DBVo();
		String pingconavg = "0";
		String basePath = ""; // ����·��
		String dataPath = ""; // ����·��
		String logerrorPath = ""; // ����·��
		String version = ""; // ���ݿ�汾
		String hostOS = ""; // ����������ϵͳ
		int doneFlag = 0;
		String id = getParaValue("id");
		String dbtype = "";
		String managed = "������";
		String runstr = "<font color=red>����ֹͣ</font>";
		try {
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}
			dbtype = typevo.getDbdesc();

			if (vo.getManaged() == 0)
				managed = "δ����";

			IpTranslation tranfer = new IpTranslation();
			String hex = tranfer.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + vo.getId();
			DBDao dbDao = null;
			Hashtable ipData = null;
			try {
				dbDao = new DBDao();
				ipData = dbDao.getMysqlDataByServerip(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dbDao != null) {
					dbDao.close();
				}
			}
			if (ipData != null && ipData.size() > 0) {
				runstr = (String) ipData.get("runningflag");
				String dbnames = vo.getDbName();
				String[] dbs = dbnames.split(",");
				for (int k = 0; k < dbs.length; k++) {
					// �ж��Ƿ��Ѿ���ȡ�˵�ǰ��������Ϣ
					if (doneFlag == 1)
						break;
					String dbStr = dbs[k];
					if (ipData.containsKey(dbStr)) {
						Hashtable returnValue = new Hashtable();
						returnValue = (Hashtable) ipData.get(dbStr);
						if (returnValue != null && returnValue.size() > 0) {
							if (returnValue.containsKey("configVal")) {
								Hashtable configVal = (Hashtable) returnValue.get("configVal");
								if (configVal.containsKey("basePath"))
									basePath = (String) configVal.get("basePath");
								if (configVal.containsKey("dataPath"))
									dataPath = (String) configVal.get("dataPath");
								if (configVal.containsKey("logerrorPath"))
									logerrorPath = (String) configVal.get("logerrorPath");
								if (configVal.containsKey("version"))
									version = (String) configVal.get("version");
								if (configVal.containsKey("hostOS"))
									hostOS = (String) configVal.get("hostOS");
								doneFlag = 1;
							}
						}
					}
				}

			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
			String newip = vo.getIpAddress();

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "MYPing", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}

			double avgpingcon = new Double(pingconavg + "").doubleValue();
			DefaultPieDataset dpd = new DefaultPieDataset();
			dpd.setValue("������", avgpingcon);
			dpd.setValue("��������", 100 - avgpingcon);
			String pingavg = String.valueOf(Math.round(avgpingcon));
			CreateMetersPic cmp = new CreateMetersPic();
			String pathPing = ResourceCenter.getInstance().getSysPath() + "resource\\image\\dashBoardGray.png";
			cmp.createChartByParam(newip, pingavg, pathPing, "��ͨ��", "pingdata");
			CreatePiePicture _cpp = new CreatePiePicture();
			_cpp.createAvgPingPic(newip, avgpingcon);
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != vo) {
			jsonString.append("{\"alias\":\"");
			jsonString.append(vo.getAlias());
			jsonString.append("\",");

			jsonString.append("\"dbname\":\"");
			jsonString.append(vo.getDbName());
			jsonString.append("\",");

			jsonString.append("\"dbtype\":\"");
			jsonString.append(dbtype);
			jsonString.append("\",");

			jsonString.append("\"ipaddress\":\"");
			jsonString.append(vo.getIpAddress());
			jsonString.append("\",");

			jsonString.append("\"port\":\"");
			jsonString.append(vo.getPort());
			jsonString.append("\",");

			jsonString.append("\"managed\":\"");
			jsonString.append(managed);
			jsonString.append("\",");

			jsonString.append("\"status\":\"");
			jsonString.append(runstr);
			jsonString.append("\",");

			jsonString.append("\"version\":\"");
			jsonString.append(version);
			jsonString.append("\",");

			jsonString.append("\"hostOS\":\"");
			jsonString.append(hostOS);
			jsonString.append("\",");

			jsonString.append("\"basePath\":\"");
			jsonString.append(basePath);
			jsonString.append("\",");

			jsonString.append("\"dataPath\":\"");
			jsonString.append(dataPath);
			jsonString.append("\",");

			jsonString.append("\"logerrorPath\":\"");
			jsonString.append(logerrorPath);
			jsonString.append("\"}");

		}
		jsonString.append("],total:" + 1 + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void dbReadyEdit() {
		DBDao dao = new DBDao();
		BusinessDao bussdao = new BusinessDao();

		DBVo vo = null;
		List allbuss = new ArrayList();
		List bidlist = new ArrayList();
		String bid = "";
		try {
			vo = (DBVo) dao.findByID(getParaValue("id"));
			allbuss = bussdao.loadAll();
			bid = vo.getBid();
			String id[] = bid.split(",");
			bidlist = new ArrayList();
			if (id != null && id.length > 0) {
				for (int i = 0; i < id.length; i++) {
					bidlist.add(id[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
			if (bussdao != null) {
				bussdao.close();
			}
		}
		String bussName = "";
		if (allbuss.size() > 0) {
			for (int i = 0; i < allbuss.size(); i++) {
				Business buss = (Business) allbuss.get(i);
				if (bidlist.contains(buss.getId() + "")) {
					bussName = bussName + ',' + buss.getName();
				}
			}
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (vo != null) {
			jsonString.append("{\"alias\":\"");
			jsonString.append(vo.getAlias());
			jsonString.append("\",");

			jsonString.append("\"ipaddress\":\"");
			jsonString.append(vo.getIpAddress());
			jsonString.append("\",");

			jsonString.append("\"port\":\"");
			jsonString.append(vo.getPort());
			jsonString.append("\",");

			jsonString.append("\"dbname\":\"");
			jsonString.append(vo.getDbName());
			jsonString.append("\",");

			jsonString.append("\"category\":\"");
			jsonString.append(vo.getCategory());
			jsonString.append("\",");

			jsonString.append("\"dbuse\":\"");
			jsonString.append(vo.getDbuse());
			jsonString.append("\",");

			jsonString.append("\"bid\":\"");
			jsonString.append(bussName);
			jsonString.append("\",");

			jsonString.append("\"bidvalue\":\"");
			jsonString.append(bid);
			jsonString.append("\",");

			jsonString.append("\"managed\":\"");
			jsonString.append(vo.getManaged());
			jsonString.append("\",");

			jsonString.append("\"collecttype\":\"");
			jsonString.append(vo.getCollecttype());
			jsonString.append("\",");

			jsonString.append("\"user\":\"");
			jsonString.append(vo.getUser());
			jsonString.append("\",");

			jsonString.append("\"password\":\"");
			jsonString.append(vo.getPassword());
			jsonString.append("\"}");
			jsonString.append("],total:" + 1 + "}");
		} else {
			jsonString.append("],total:" + 0 + "}");
		}
		out.print(jsonString.toString());
		out.flush();
	}

	@SuppressWarnings("unchecked")
	private void getMysqlConnectDetail() {
		String ipaddress = getParaValue("ip");
		String id = getParaValue("id");
		DBVo vo = new DBVo();
		DBDao dao = new DBDao();
		try {
			vo = (DBVo) dao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		String hex = IpTranslation.formIpToHex(ipaddress);
		String serverip = hex + ":" + id;
		DBDao dbDao = null;
		Hashtable ipData = null;
		try {
			dbDao = new DBDao();
			ipData = dbDao.getMysqlDataByServerip(serverip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dbDao != null) {
				dbDao.close();
			}
		}
		int doneFlag = 0;
		List sessionlist = new ArrayList();
		if (ipData != null && ipData.size() > 0) {
			String dbnames = vo.getDbName();
			String[] dbs = dbnames.split(",");
			for (int k = 0; k < dbs.length; k++) {
				// �ж��Ƿ��Ѿ���ȡ�˵�ǰ��������Ϣ
				if (doneFlag == 1)
					break;
				String dbStr = dbs[k];
				if (ipData.containsKey(dbStr)) {
					Hashtable returnValue = new Hashtable();
					returnValue = (Hashtable) ipData.get(dbStr);
					if (returnValue != null && returnValue.size() > 0) {
						if (returnValue.containsKey("sessionsDetail")) {
							// �������ݿ�������Ϣ
							sessionlist.add((List) returnValue.get("sessionsDetail"));
						}
					}
				}
			}
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		int size = 0;
		if (sessionlist != null && sessionlist.size() > 0) {
			for (int i = 0; i < sessionlist.size(); i++) {
				List ipsessionlist = (List) sessionlist.get(i);
				size = ipsessionlist.size();
				if (ipsessionlist != null && ipsessionlist.size() > 0) {
					for (int k = 0; k < ipsessionlist.size(); k++) {
						String[] sessions = (String[]) ipsessionlist.get(k);
						if (sessions != null && sessions.length == 5) {
							jsonString.append("{\"dbname\":\"");
							jsonString.append(sessions[4]);
							jsonString.append("\",");

							jsonString.append("\"username\":\"");
							jsonString.append(sessions[0]);
							jsonString.append("\",");

							jsonString.append("\"hostname\":\"");
							jsonString.append(sessions[1]);
							jsonString.append("\",");

							jsonString.append("\"comm\":\"");
							jsonString.append(sessions[2]);
							jsonString.append("\",");

							jsonString.append("\"conntime\":\"");
							jsonString.append(sessions[3]);
							jsonString.append("\"}");
							if (k != ipsessionlist.size() - 1) {
								jsonString.append(",");
							}
						}
					}
				}
			}
		}
		jsonString.append("],total:" + size + "}");
		out.print(jsonString);
		out.flush();
	}

	private void getMysqlTablesDetail() {
		String ipaddress = getParaValue("ip");
		String id = getParaValue("id");
		DBVo vo = new DBVo();
		DBDao dao = new DBDao();
		try {
			vo = (DBVo) dao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		String hex = IpTranslation.formIpToHex(ipaddress);
		String serverip = hex + ":" + id;
		DBDao dbDao = null;
		Hashtable ipData = null;
		try {
			dbDao = new DBDao();
			ipData = dbDao.getMysqlDataByServerip(serverip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dbDao != null) {
				dbDao.close();
			}
		}
		int doneFlag = 0;
		List tableslist = new ArrayList();
		if (ipData != null && ipData.size() > 0) {
			String dbnames = vo.getDbName();
			String[] dbs = dbnames.split(",");
			for (int k = 0; k < dbs.length; k++) {
				// �ж��Ƿ��Ѿ���ȡ�˵�ǰ��������Ϣ
				if (doneFlag == 1)
					break;
				String dbStr = dbs[k];
				if (ipData.containsKey(dbStr)) {
					Hashtable returnValue = new Hashtable();
					returnValue = (Hashtable) ipData.get(dbStr);
					if (returnValue != null && returnValue.size() > 0) {
						if (returnValue.containsKey("tablesDetail")) {
							// �������ݿ�������Ϣ
							tableslist = (List) returnValue.get("tablesDetail");
						}
					}
				}
			}
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (tableslist != null && tableslist.size() > 0) {
			for (int i = 0; i < tableslist.size(); i++) {
				String[] tables = (String[]) tableslist.get(i);
				if (tables != null && tables.length == 4) {
					jsonString.append("{\"tablename\":\"");
					jsonString.append(tables[0]);
					jsonString.append("\",");

					jsonString.append("\"tablerows\":\"");
					jsonString.append(tables[1]);
					jsonString.append("\",");

					jsonString.append("\"tablesize\":\"");
					jsonString.append(tables[2]);
					jsonString.append("\",");

					jsonString.append("\"createtime\":\"");
					jsonString.append(tables[3]);
					jsonString.append("\"}");
					if (i != tableslist.size() - 1) {
						jsonString.append(",");
					}
				}
			}
		}
		jsonString.append("],total:" + tableslist.size() + "}");
		out.print(jsonString);
		out.flush();
	}

	@SuppressWarnings("static-access")
	private void getMysqlSpacesDetail() {
		String ipaddress = getParaValue("ip");
		String id = getParaValue("id");
		DBVo vo = new DBVo();
		DBDao dao = new DBDao();
		try {
			vo = (DBVo) dao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		IpTranslation tranfer = new IpTranslation();
		String hex = tranfer.formIpToHex(ipaddress);
		String serverip = hex + ":" + id;
		DBDao dbDao = null;
		Hashtable ipData = null;
		try {
			dbDao = new DBDao();
			ipData = dbDao.getMysqlDataByServerip(serverip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dbDao != null) {
				dbDao.close();
			}
		}
		int doneFlag = 0;
		Vector Val = new Vector();
		if (ipData != null && ipData.size() > 0) {
			String dbnames = vo.getDbName();
			String[] dbs = dbnames.split(",");
			for (int k = 0; k < dbs.length; k++) {
				// �ж��Ƿ��Ѿ���ȡ�˵�ǰ��������Ϣ
				if (doneFlag == 1)
					break;
				String dbStr = dbs[k];
				if (ipData.containsKey(dbStr)) {
					Hashtable returnValue = new Hashtable();
					returnValue = (Hashtable) ipData.get(dbStr);
					if (returnValue != null && returnValue.size() > 0) {
						if (returnValue.containsKey("Val")) {
							Val = (Vector) returnValue.get("Val");
						}
					}
				}
			}
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (Val != null && Val.size() > 0) {
			for (int i = 0; i < Val.size(); i++) {
				Hashtable return_value = (Hashtable) Val.get(i);
				if (return_value != null && return_value.size() > 0) {
					String name = return_value.get("variable_name").toString();
					String value = return_value.get("value").toString();
					name = getValueFromNameForMysqlSpace(name);
					jsonString.append("{\"name\":\"");
					jsonString.append(name);
					jsonString.append("\",");

					jsonString.append("\"value\":\"");
					jsonString.append(value);
					jsonString.append("\"}");
					if (i != Val.size() - 1) {
						jsonString.append(",");
					}
				}
			}
		}
		jsonString.append("],total:" + Val.size() + "}");
		out.print(jsonString);
		out.flush();
	}

	@SuppressWarnings("static-access")
	private void getMysqlStatusDetail() {
		String ipaddress = getParaValue("ip");
		String id = getParaValue("id");
		DBVo vo = new DBVo();
		DBDao dao = new DBDao();
		try {
			vo = (DBVo) dao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		IpTranslation tranfer = new IpTranslation();
		String hex = tranfer.formIpToHex(ipaddress);
		String serverip = hex + ":" + id;
		DBDao dbDao = null;
		Hashtable ipData = null;
		try {
			dbDao = new DBDao();
			ipData = dbDao.getMysqlDataByServerip(serverip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dbDao != null) {
				dbDao.close();
			}
		}
		int doneFlag = 0;
		Vector tableinfo_v = new Vector();
		if (ipData != null && ipData.size() > 0) {
			String dbnames = vo.getDbName();
			String[] dbs = dbnames.split(",");
			for (int k = 0; k < dbs.length; k++) {
				// �ж��Ƿ��Ѿ���ȡ�˵�ǰ��������Ϣ
				if (doneFlag == 1)
					break;
				String dbStr = dbs[k];
				if (ipData.containsKey(dbStr)) {
					Hashtable returnValue = new Hashtable();
					returnValue = (Hashtable) ipData.get(dbStr);
					if (returnValue != null && returnValue.size() > 0) {
						if (returnValue.containsKey("global_status")) {
							tableinfo_v = (Vector) returnValue.get("global_status");
						}
					}
				}
			}
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (tableinfo_v != null && tableinfo_v.size() > 0) {
			for (int i = 0; i < tableinfo_v.size(); i++) {
				Hashtable return_value = (Hashtable) tableinfo_v.get(i);
				if (return_value != null && return_value.size() > 0) {
					String name = return_value.get("variable_name").toString();
					String value = return_value.get("value").toString();
					name = getSizeForMysqlStatus(name);
					jsonString.append("{\"name\":\"");
					jsonString.append(name);
					jsonString.append("\",");

					jsonString.append("\"value\":\"");
					jsonString.append(value);
					jsonString.append("\"}");
					if (i != tableinfo_v.size() - 1) {
						jsonString.append(",");
					}
				}
			}
		}
		jsonString.append("],total:" + tableinfo_v.size() + "}");
		out.print(jsonString);
		out.flush();
	}

	@SuppressWarnings("static-access")
	private void getMysqlVariablesDetail() {
		String ipaddress = getParaValue("ip");
		String id = getParaValue("id");
		DBVo vo = new DBVo();
		DBDao dao = new DBDao();
		try {
			vo = (DBVo) dao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		IpTranslation tranfer = new IpTranslation();
		String hex = tranfer.formIpToHex(ipaddress);
		String serverip = hex + ":" + id;
		DBDao dbDao = null;
		Hashtable ipData = null;
		try {
			dbDao = new DBDao();
			ipData = dbDao.getMysqlDataByServerip(serverip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dbDao != null) {
				dbDao.close();
			}
		}
		int doneFlag = 0;
		Vector tableinfo_v = new Vector();
		if (ipData != null && ipData.size() > 0) {
			String dbnames = vo.getDbName();
			String[] dbs = dbnames.split(",");
			for (int k = 0; k < dbs.length; k++) {
				// �ж��Ƿ��Ѿ���ȡ�˵�ǰ��������Ϣ
				if (doneFlag == 1)
					break;
				String dbStr = dbs[k];
				if (ipData.containsKey(dbStr)) {
					Hashtable returnValue = new Hashtable();
					returnValue = (Hashtable) ipData.get(dbStr);
					if (returnValue != null && returnValue.size() > 0) {
						if (returnValue.containsKey("variables")) {
							// �������ݿ�������Ϣ
							tableinfo_v = (Vector) returnValue.get("variables");
						}
					}
				}
			}
		}
		int size = 0;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (tableinfo_v != null && tableinfo_v.size() > 0) {
			for (int i = 0; i < tableinfo_v.size(); i++) {
				Hashtable ht = (Hashtable) tableinfo_v.get(i);
				String name = (String) ht.get("variable_name");
				String value = (String) ht.get("value");
				value = value.replace("\"", "'");
				size++;
				name = getConfigForMysqlConfig(name);

				jsonString.append("{\"variablesname\":\"");
				jsonString.append(name);
				jsonString.append("\",");

				jsonString.append("\"variablesvalue\":\"");
				jsonString.append(value);
				jsonString.append("\"}");

				if (i != tableinfo_v.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + size + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	@SuppressWarnings("static-access")
	private void getMysqlConfigDetail1() {
		String ipaddress = getParaValue("ip");
		String id = getParaValue("id");
		DBVo vo = new DBVo();
		DBDao dao = new DBDao();
		try {
			vo = (DBVo) dao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		IpTranslation tranfer = new IpTranslation();
		String hex = tranfer.formIpToHex(ipaddress);
		String serverip = hex + ":" + id;
		DBDao dbDao = null;
		Hashtable ipData = null;
		try {
			dbDao = new DBDao();
			ipData = dbDao.getMysqlDataByServerip(serverip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dbDao != null) {
				dbDao.close();
			}
		}
		int doneFlag = 0;
		Vector tableinfo_v = new Vector();
		if (ipData != null && ipData.size() > 0) {
			String dbnames = vo.getDbName();
			String[] dbs = dbnames.split(",");
			for (int k = 0; k < dbs.length; k++) {
				// �ж��Ƿ��Ѿ���ȡ�˵�ǰ��������Ϣ
				if (doneFlag == 1)
					break;
				String dbStr = dbs[k];
				if (ipData.containsKey(dbStr)) {
					Hashtable returnValue = new Hashtable();
					returnValue = (Hashtable) ipData.get(dbStr);
					if (returnValue != null && returnValue.size() > 0) {
						if (returnValue.containsKey("variables")) {
							tableinfo_v = (Vector) returnValue.get("variables");
						}
					}
				}
			}
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (tableinfo_v != null && tableinfo_v.size() > 0) {
			for (int i = 0; i < tableinfo_v.size(); i++) {
				Hashtable return_value = (Hashtable) tableinfo_v.get(i);
				if (return_value != null && return_value.size() > 0) {
					String name = return_value.get("variable_name").toString();
					String value = return_value.get("value").toString();
					if (value == null || value.equals("")) {
						value = "--";
					}
					name = getConfigForMysqlConfig(name);
					jsonString.append("{\"configname\":\"");
					jsonString.append(name);
					jsonString.append("\",");

					jsonString.append("\"configvalue\":\"");
					jsonString.append(value);
					jsonString.append("\"}");
					if (i != tableinfo_v.size() - 1) {
						jsonString.append(",");
					}
				}
			}
		}
		jsonString.append("],total:" + tableinfo_v.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getDBEventDetail() {
		List list = new ArrayList();
		int status = 99;
		int level1 = 99;
		int id = getParaIntValue("id");

		try {
			String starttime1 = "1970-01-01 00:00:00";
			String totime1 = "2050-12-31 23:59:59";

			EventListDao dao = new EventListDao();
			try {
				User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // �û�����
				list = dao.getQuery(starttime1, totime1, "db", status + "", level1 + "", vo.getBusinessids(), id);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dao.close();
			}

			request.setAttribute("list", list);
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != list && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				EventList eventlist = (EventList) list.get(i);
				java.text.SimpleDateFormat _sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date cc = eventlist.getRecordtime().getTime();
				Integer eventid = eventlist.getId();
				String content = eventlist.getContent();
				String level = String.valueOf(eventlist.getLevel1());
				String eventstatus = String.valueOf(eventlist.getManagesign());
				if ("0".equals(level)) {
					level = "��ʾ��Ϣ";
				}
				if ("1".equals(level)) {
					level = "��ͨ�澯";
				}
				if ("2".equals(level)) {
					level = "���ظ澯";
				}
				if ("3".equals(level)) {
					level = "�����澯";
				}
				if ("0".equals(eventstatus)) {
					eventstatus = "δ����";
				}
				if ("1".equals(eventstatus)) {
					eventstatus = "������";
				}
				if ("2".equals(eventstatus)) {
					eventstatus = "�������";
				}
				String rptman = eventlist.getReportman();
				String rptTime = _sdf.format(cc);

				jsonString.append("{\"eventId\":\"");
				jsonString.append(eventid + "");
				jsonString.append("\",");

				jsonString.append("\"eventLevel\":\"");
				jsonString.append(level);
				jsonString.append("\",");

				jsonString.append("\"eventContent\":\"");
				jsonString.append(content);
				jsonString.append("\",");

				jsonString.append("\"rptTime\":\"");
				jsonString.append(rptTime);
				jsonString.append("\",");

				jsonString.append("\"rptMan\":\"");
				jsonString.append(rptman);
				jsonString.append("\",");

				jsonString.append("\"eventStatus\":\"");
				jsonString.append(eventstatus);
				jsonString.append("\"}");

				if (i != list.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + list.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void dbEdit() {
		DBVo vo = new DBVo();

		vo.setId(getParaIntValue("id"));
		vo.setUser(getParaValue("user"));
		vo.setPassword(getParaValue("password"));
		vo.setAlias(getParaValue("alias"));
		vo.setIpAddress(getParaValue("ip_address"));
		vo.setPort(getParaValue("port"));
		vo.setDbName(getParaValue("db_name"));
		vo.setCategory(getParaIntValue("category"));
		vo.setCollecttype(getParaIntValue("collecttype"));
		vo.setDbuse(getParaValue("dbuse"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setSendemail(getParaValue("sendemail"));
		if (getParaValue("bid") == null || getParaValue("bid").equals("notSet") || getParaValue("bid").equals("")) {
			vo.setBid(getParaValue("bids"));
		} else {
			vo.setBid(getParaValue("bid"));
		}
		vo.setManaged(getParaIntValue("managed"));
		DBDao dao = new DBDao();
		boolean flag = true;
		try {
			flag = dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		String jsonString = "�޸ĳɹ�";
		if (!flag) {
			jsonString = "�޸�ʧ��";
		}
		out.print(jsonString);
		out.flush();
	}

	private void getOracleSystem() {
		DBVo vo = new DBVo();

		Hashtable memPerfValue = new Hashtable();
		Hashtable isArchive_h = new Hashtable();
		Hashtable cursors = new Hashtable();
		Hashtable memValue = new Hashtable();
		Vector tableinfo = new Vector();
		String lstrnStatu = "";
		String pingconavg = "0";
		String dbtype = "";
		String managed = "������";
		String runstr = "����ֹͣ";
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			try {
				String id = getParaValue("id");
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}
			dbtype = typevo.getDbdesc();
			if (vo.getManaged() == 0)
				managed = "δ����";
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			String status = "0";
			try {
				dao = new DBDao();
				Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// ȡ״̬��Ϣ
				memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);
				status = String.valueOf(statusHashtable.get("status"));
				lstrnStatu = String.valueOf(statusHashtable.get("lstrnstatu"));
				isArchive_h = dao.getOracle_nmsoraisarchive(serverip);
				cursors = dao.getOracle_nmsoracursors(serverip);
				memValue = dao.getOracle_nmsoramemvalue(serverip);
				tableinfo = dao.getOracle_nmsoraspaces(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			if ("1".equals(status)) {
				runstr = "��������";
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}
			double avgpingcon = new Double(pingconavg + "").doubleValue();
			DefaultPieDataset dpd = new DefaultPieDataset();
			dpd.setValue("������", avgpingcon);
			dpd.setValue("��������", 100 - avgpingcon);
			String pingavg = String.valueOf(Math.round(avgpingcon));
			CreateMetersPic cmp = new CreateMetersPic();
			String pathPing = ResourceCenter.getInstance().getSysPath() + "resource\\image\\dashBoardGray.png";
			cmp.createChartByParam(vo.getIpAddress(), pingavg, pathPing, "��ͨ��", "pingdata");
		} catch (Exception e) {
			e.printStackTrace();
		}

		String buffercache = "0";
		String opencurstr = "0";
		String curconnectstr = "0";
		String dictionarycache = "0";
		String librarycache = "0";
		String pctmemorysorts = "";
		String pctbufgets = "0";
		if (memPerfValue != null) {
			if (memPerfValue.containsKey("buffercache") && memPerfValue.get("buffercache") != null)
				buffercache = (String) memPerfValue.get("buffercache");
			if (memPerfValue.containsKey("dictionarycache") && memPerfValue.get("dictionarycache") != null)
				dictionarycache = (String) memPerfValue.get("dictionarycache");
			if (memPerfValue.containsKey("librarycache") && memPerfValue.get("librarycache") != null)
				librarycache = (String) memPerfValue.get("librarycache");
			if (memPerfValue.containsKey("pctmemorysorts") && memPerfValue.get("pctmemorysorts") != null)
				pctmemorysorts = (String) memPerfValue.get("pctmemorysorts");
			if (memPerfValue.containsKey("pctbufgets") && memPerfValue.get("pctbufgets") != null)
				pctbufgets = (String) memPerfValue.get("pctbufgets");
			if (cursors.containsKey("opencur") && cursors.get("opencur") != null) {
				opencurstr = (String) cursors.get("opencur");
			}
			if (cursors.containsKey("curconnect") && cursors.get("curconnect") != null) {
				curconnectstr = (String) cursors.get("curconnect");
			}
			if (pctmemorysorts.equals(""))
				pctmemorysorts = "0";
			if (dictionarycache.equals(""))
				dictionarycache = "0";
			if (librarycache.equals(""))
				librarycache = "0";
			if (buffercache.equals(""))
				buffercache = "0";
			if (pctbufgets.equals(""))
				pctbufgets = "0";
		}
		// sqlserver ����������ͼ
		String[] title = { "������������", "�����ֵ�������", "�⻺��������", "�ڴ��е�����" };
		String[] data = { buffercache, dictionarycache, librarycache, pctmemorysorts };
		CreateAmColumnPic amColumn = new CreateAmColumnPic();
		String dbdata = amColumn.createSqlUtilChart(data, title);
		// ��ռ�������
		String tabledata = amColumn.createOraTableSpaceUtilChart(tableinfo);
		// sga
		String sgadata = amColumn.createSGAChart(memValue);
		// pga
		String pgadata = amColumn.createPGAChart(memValue);
		String created = "";
		if (isArchive_h != null && isArchive_h.containsKey("CREATED")) {
			created = (String) isArchive_h.get("CREATED");
		}
		OracleManager om = new OracleManager();
		Hashtable orasys = om.geHashtable(vo.getIpAddress(), sid);
		String hostname = (String) orasys.get("HOST_NAME");
		String dbname = (String) orasys.get("DBNAME");
		String version = (String) orasys.get("VERSION");
		String instancename = (String) orasys.get("INSTANCE_NAME");
		String status = (String) orasys.get("STATUS");
		String startup_time = (String) orasys.get("STARTUP_TIME");
		String archiver = (String) orasys.get("ARCHIVER");

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		jsonString.append("{\"alias\":\"");
		jsonString.append(vo.getAlias());
		jsonString.append("\",");

		jsonString.append("\"dbname\":\"");
		jsonString.append(vo.getDbName());
		jsonString.append("\",");

		jsonString.append("\"dbtype\":\"");
		jsonString.append(dbtype);
		jsonString.append("\",");

		jsonString.append("\"ipaddress\":\"");
		jsonString.append(vo.getIpAddress());
		jsonString.append("\",");

		jsonString.append("\"port\":\"");
		jsonString.append(vo.getPort());
		jsonString.append("\",");

		jsonString.append("\"managed\":\"");
		jsonString.append(managed);
		jsonString.append("\",");

		jsonString.append("\"status\":\"");
		jsonString.append(runstr);
		jsonString.append("\",");

		jsonString.append("\"lstrnStatu\":\"");
		jsonString.append(lstrnStatu);
		jsonString.append("\",");

		jsonString.append("\"hostname\":\"");
		jsonString.append(hostname);
		jsonString.append("\",");

		jsonString.append("\"DBname\":\"");
		jsonString.append(dbname);
		jsonString.append("\",");

		jsonString.append("\"version\":\"");
		jsonString.append(version);
		jsonString.append("\",");

		jsonString.append("\"instancename\":\"");
		jsonString.append(instancename);
		jsonString.append("\",");

		jsonString.append("\"instancestatus\":\"");
		jsonString.append(status);
		jsonString.append("\",");

		jsonString.append("\"startup_time\":\"");
		jsonString.append(startup_time);
		jsonString.append("\",");

		jsonString.append("\"archiver\":\"");
		jsonString.append(archiver);
		jsonString.append("\",");

		jsonString.append("\"created\":\"");
		jsonString.append(created);
		jsonString.append("\",");

		jsonString.append("\"memsql\":\"");
		jsonString.append(pctmemorysorts);
		jsonString.append("\",");

		jsonString.append("\"opencurstr\":\"");
		jsonString.append(opencurstr);
		jsonString.append("\",");

		jsonString.append("\"curconnectstr\":\"");
		jsonString.append(curconnectstr);
		jsonString.append("\",");

		jsonString.append("\"tabledata\":\"");
		jsonString.append(tabledata);
		jsonString.append("\",");

		jsonString.append("\"sgadata\":\"");
		jsonString.append(sgadata);
		jsonString.append("\",");

		jsonString.append("\"pgadata\":\"");
		jsonString.append(pgadata);
		jsonString.append("\",");

		jsonString.append("\"dbdata\":\"");
		jsonString.append(dbdata);
		jsonString.append("\"}");

		jsonString.append("],total: 1}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getOracleLockDetail() {
		String type = getParaValue("type");
		String ipaddress = getParaValue("ip");
		DBVo vo = new DBVo();
		Vector lockinfo_v = new Vector();
		OracleLockInfo oracleLockInfo = null;
		String id = "";
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			try {
				id = getParaValue("id");
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			IpTranslation tranfer = new IpTranslation();
			String hex = tranfer.formIpToHex(ipaddress);
			String serverip = hex + ":" + sid;
			try {
				dao = new DBDao();
				lockinfo_v = dao.getOracle_nmsoralock(serverip);
				oracleLockInfo = dao.getOracle_nmsoralockinfo(serverip);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				dao.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		int size = 0;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (type != null && type.equals("lock")) {
			if (lockinfo_v != null && lockinfo_v.size() > 0) {
				for (int i = 0; i < lockinfo_v.size(); i++) {
					Hashtable ht = (Hashtable) lockinfo_v.get(i);
					String username = ht.get("username").toString().trim();
					String status = ht.get("status").toString().trim();
					String machine = ht.get("machine").toString().trim();
					String sessiontype = ht.get("sessiontype").toString().trim();
					String logontime = ht.get("logontime").toString().trim();
					String program = ht.get("program").toString().trim();
					String locktype = ht.get("locktype").toString().trim();
					String lmode = ht.get("lmode").toString().trim();
					String requeststr = ht.get("request").toString().trim();
					jsonString.append("{\"alias\":\"");
					jsonString.append(vo.getAlias());
					jsonString.append("\",");

					jsonString.append("\"username\":\"");
					jsonString.append(username);
					jsonString.append("\",");

					jsonString.append("\"status\":\"");
					jsonString.append(status);
					jsonString.append("\",");

					jsonString.append("\"machine\":\"");
					jsonString.append(machine);
					jsonString.append("\",");

					jsonString.append("\"sessiontype\":\"");
					jsonString.append(sessiontype);
					jsonString.append("\",");

					jsonString.append("\"logontime\":\"");
					jsonString.append(logontime);
					jsonString.append("\",");

					jsonString.append("\"program\":\"");
					jsonString.append(program);
					jsonString.append("\",");

					jsonString.append("\"locktype\":\"");
					jsonString.append(locktype);
					jsonString.append("\",");

					jsonString.append("\"lmode\":\"");
					jsonString.append(lmode);
					jsonString.append("\",");

					jsonString.append("\"requeststr\":\"");
					jsonString.append(requeststr);
					jsonString.append("\"}");

					if (i != lockinfo_v.size() - 1) {
						jsonString.append(",");
					}
				}
				size = lockinfo_v.size();
			}
		}

		if (type != null && type.equals("lockInfo")) {
			if (oracleLockInfo != null) {
				String deadlockcount = oracleLockInfo.getDeadlockcount();
				String lockwaitcount = oracleLockInfo.getLockwaitcount();
				String maxprocesscount = oracleLockInfo.getMaxprocesscount();
				String processcount = oracleLockInfo.getProcesscount();
				String currentsessioncount = oracleLockInfo.getCurrentsessioncount();
				String useablesessioncount = oracleLockInfo.getUseablesessioncount();
				String useablesessionpercent = oracleLockInfo.getUseablesessionpercent();
				String lockdsessioncount = oracleLockInfo.getLockdsessioncount();
				String rollbacks = oracleLockInfo.getRollbacks();
				String rollbackcommitpercent = oracleLockInfo.getRollbackcommitpercent();
				jsonString.append("{\"deadlockcount\":\"");
				jsonString.append(deadlockcount);
				jsonString.append("\",");

				jsonString.append("\"lockwaitcount\":\"");
				jsonString.append(lockwaitcount);
				jsonString.append("\",");

				jsonString.append("\"maxprocesscount\":\"");
				jsonString.append(maxprocesscount);
				jsonString.append("\",");

				jsonString.append("\"processcount\":\"");
				jsonString.append(processcount);
				jsonString.append("\",");

				jsonString.append("\"currentsessioncount\":\"");
				jsonString.append(currentsessioncount);
				jsonString.append("\",");

				jsonString.append("\"useablesessioncount\":\"");
				jsonString.append(useablesessioncount);
				jsonString.append("\",");

				jsonString.append("\"useablesessionpercent\":\"");
				jsonString.append(useablesessionpercent);
				jsonString.append("\",");

				jsonString.append("\"lockdsessioncount\":\"");
				jsonString.append(lockdsessioncount);
				jsonString.append("\",");

				jsonString.append("\"rollbacks\":\"");
				jsonString.append(rollbacks);
				jsonString.append("\",");

				jsonString.append("\"rollbackcommitpercent\":\"");
				jsonString.append(rollbackcommitpercent);
				jsonString.append("\"}");
			}
			size = 1;
		}

		jsonString.append("],total:" + size + "}");
		out.print(jsonString.toString());
		out.flush();

	}

	private void getOracleSessionDetail() {
		String ipaddress = getParaValue("ip");
		Vector sessioninfo_v = new Vector();
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			String hex = IpTranslation.formIpToHex(ipaddress);
			String serverip = hex + ":" + sid;
			try {
				dao = new DBDao();
				sessioninfo_v = dao.getOracle_nmsorasessiondata(serverip);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				dao.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (sessioninfo_v != null && sessioninfo_v.size() > 0) {
			for (int i = 0; i < sessioninfo_v.size(); i++) {
				Hashtable ht = (Hashtable) sessioninfo_v.get(i);
				String machine = ht.get("machine").toString();
				String username = ht.get("username").toString();
				String program = ht.get("program").toString();
				String status = ht.get("status").toString();
				String sessiontype = ht.get("sessiontype").toString();
				String command = ht.get("command").toString();
				String logontime = ht.get("logontime").toString();

				jsonString.append("{\"machine\":\"");
				jsonString.append(machine);
				jsonString.append("\",");

				jsonString.append("\"username\":\"");
				jsonString.append(username);
				jsonString.append("\",");

				jsonString.append("\"program\":\"");
				jsonString.append(program);
				jsonString.append("\",");

				jsonString.append("\"status\":\"");
				jsonString.append(status);
				jsonString.append("\",");

				jsonString.append("\"sessiontype\":\"");
				jsonString.append(sessiontype);
				jsonString.append("\",");

				jsonString.append("\"command\":\"");
				jsonString.append(command);
				jsonString.append("\",");

				jsonString.append("\"logontime\":\"");
				jsonString.append(logontime);
				jsonString.append("\"}");

				if (i != sessioninfo_v.size() - 1) {
					jsonString.append(",");
				}
			}

		}
		jsonString.append("],total:" + sessioninfo_v.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getOracleRollbackDetail() {
		String ipaddress = getParaValue("ip");
		Vector rollbackinfo_v = new Vector();
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			String hex = IpTranslation.formIpToHex(ipaddress);
			String serverip = hex + ":" + sid;
			try {
				dao = new DBDao();
				rollbackinfo_v = dao.getOracle_nmsorarollback(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (rollbackinfo_v != null && rollbackinfo_v.size() > 0) {
			for (int i = 0; i < rollbackinfo_v.size(); i++) {
				Hashtable ht = (Hashtable) rollbackinfo_v.get(i);
				String rollback = ht.get("rollback_segment").toString().trim();
				String wraps = ht.get("wraps").toString();
				String shrink = ht.get("shrinks").toString();
				String ashrink = ht.get("average_shrink").toString();
				String extend = ht.get("extends").toString();

				jsonString.append("{\"rollback\":\"");
				jsonString.append(rollback);
				jsonString.append("\",");

				jsonString.append("\"wraps\":\"");
				jsonString.append(wraps);
				jsonString.append("\",");

				jsonString.append("\"shrink\":\"");
				jsonString.append(shrink);
				jsonString.append("\",");

				jsonString.append("\"ashrink\":\"");
				jsonString.append(ashrink);
				jsonString.append("\",");

				jsonString.append("\"extend\":\"");
				jsonString.append(extend);
				jsonString.append("\"}");

				if (i != rollbackinfo_v.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + rollbackinfo_v.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getOracleTableDetail() {
		String ipaddress = getParaValue("ip");
		Vector table_v = new Vector();
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			String hex = IpTranslation.formIpToHex(ipaddress);
			String serverip = hex + ":" + sid;
			try {
				dao = new DBDao();
				table_v = dao.getOracle_nmsoratables(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (table_v != null && table_v.size() > 0) {
			DecimalFormat df = new DecimalFormat("#.##");
			for (int i = 0; i < table_v.size(); i++) {
				Hashtable ht = (Hashtable) table_v.get(i);
				String spaces = ht.get("spaces").toString().trim();
				String segment_name = ht.get("segment_name").toString();

				jsonString.append("{\"tablename\":\"");
				jsonString.append(segment_name);
				jsonString.append("\",");

				jsonString.append("\"spaces\":\"");
				jsonString.append(df.format(Double.parseDouble(spaces)));
				jsonString.append("\"}");

				if (i != table_v.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + table_v.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getOracleSpacesDetail() {
		String ipaddress = getParaValue("ip");
		Vector tablespace_v = new Vector();
		Hashtable dbio = new Hashtable();
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			String hex = IpTranslation.formIpToHex(ipaddress);
			String serverip = hex + ":" + sid;
			try {
				dao = new DBDao();
				tablespace_v = dao.getOracle_nmsoraspaces(serverip);
				dbio = dao.getOracle_nmsoradbio(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dao != null) {
					dao.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (tablespace_v != null) {
			for (int i = 0; i < tablespace_v.size(); i++) {
				Hashtable ht = (Hashtable) tablespace_v.get(i);
				String filename = ht.get("file_name").toString();
				String tablespace = ht.get("tablespace").toString();
				String size = ht.get("size_mb").toString();
				String free = ht.get("free_mb").toString();
				String percent = ht.get("percent_free").toString();
				String status = ht.get("status").toString();
				String pyr = "";
				String pbr = "";
				String pyw = "";
				String pbw = "";
				if (dbio.containsKey(filename)) {
					Hashtable iodetail = (Hashtable) dbio.get(filename);
					if (iodetail != null && iodetail.size() > 0) {
						pyr = (String) iodetail.get("pyr");
						pbr = (String) iodetail.get("pbr");
						pyw = (String) iodetail.get("pyw");
						pbw = (String) iodetail.get("pbw");
					}
				}

				jsonString.append("{\"filename\":\"");
				jsonString.append(filename);
				jsonString.append("\",");

				jsonString.append("\"tablespace\":\"");
				jsonString.append(tablespace);
				jsonString.append("\",");

				jsonString.append("\"size\":\"");
				jsonString.append(size);
				jsonString.append("\",");

				jsonString.append("\"free\":\"");
				jsonString.append(free);
				jsonString.append("\",");

				jsonString.append("\"percent\":\"");
				jsonString.append(percent);
				jsonString.append("\",");

				jsonString.append("\"pyr\":\"");
				jsonString.append(pyr);
				jsonString.append("\",");

				jsonString.append("\"pbr\":\"");
				jsonString.append(pbr);
				jsonString.append("\",");

				jsonString.append("\"pyw\":\"");
				jsonString.append(pyw);
				jsonString.append("\",");

				jsonString.append("\"pbw\":\"");
				jsonString.append(pbw);
				jsonString.append("\",");

				jsonString.append("\"status\":\"");
				jsonString.append(status);
				jsonString.append("\"}");

				if (i != tablespace_v.size() - 1) {
					jsonString.append(",");
				}
			}

		}
		jsonString.append("],total:" + tablespace_v.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getOracleTopsqlDetail() {
		String type = getParaValue("type");
		String ipaddress = getParaValue("ip");
		Vector sql_v = new Vector();
		Vector sql_readwrite_v = new Vector();
		Vector sql_sort_v = new Vector();
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			String hex = IpTranslation.formIpToHex(ipaddress);
			String serverip = hex + ":" + sid;
			try {
				dao = new DBDao();
				if (type.equals("memsql") && type != null) {
					sql_v = dao.getOracle_nmsoratopsql(serverip);
				} else if (type.equals("disksql") && type != null) {
					sql_readwrite_v = dao.getOracle_nmsoratopsql_readwrite(serverip);
				} else if (type.equals("sortsql") && type != null) {
					sql_sort_v = dao.getOracle_nmsoratopsql_sort(serverip);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dao != null) {
					dao.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		int size = 0;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (sql_v != null && sql_v.size() > 0) {
			for (int i = 0; i < sql_v.size(); i++) {
				Hashtable ht = (Hashtable) sql_v.get(i);
				String memsql = ht.get("sql_text").toString();
				String pct_bufgets = ht.get("pct_bufgets").toString();
				String username = ht.get("username").toString();

				jsonString.append("{\"memsql\":\"");
				jsonString.append(memsql);
				jsonString.append("\",");

				jsonString.append("\"pct_bufgets\":\"");
				jsonString.append(pct_bufgets);
				jsonString.append("\",");

				jsonString.append("\"username\":\"");
				jsonString.append(username);
				jsonString.append("\"}");

				if (i != sql_v.size() - 1) {
					jsonString.append(",");
				}
			}
			size = sql_v.size();
		}

		if (sql_readwrite_v != null && sql_readwrite_v.size() > 0) {
			for (int i = 0; i < sql_readwrite_v.size(); i++) {
				Hashtable ht = (Hashtable) sql_readwrite_v.get(i);
				String sqltext = ht.get("sqltext").toString();
				String totaldisk = ht.get("totaldisk").toString();
				String totalexec = ht.get("totalexec").toString();
				String diskreads = ht.get("diskreads").toString();

				jsonString.append("{\"disksql\":\"");
				jsonString.append(sqltext);
				jsonString.append("\",");

				jsonString.append("\"totaldisk\":\"");
				jsonString.append(totaldisk);
				jsonString.append("\",");

				jsonString.append("\"totalexec\":\"");
				jsonString.append(totalexec);
				jsonString.append("\",");

				jsonString.append("\"diskreads\":\"");
				jsonString.append(diskreads);
				jsonString.append("\"}");

				if (i != sql_readwrite_v.size() - 1) {
					jsonString.append(",");
				}
			}
			size = sql_readwrite_v.size();
		}

		if (sql_sort_v != null && sql_sort_v.size() > 0) {
			for (int i = 0; i < sql_sort_v.size(); i++) {
				Hashtable ht = (Hashtable) sql_sort_v.get(i);
				String sqltext = ht.get("sqltext").toString();
				String sorts = ht.get("sorts").toString();
				String executions = ht.get("executions").toString();
				String sortsexec = ht.get("sortsexec").toString();

				jsonString.append("{\"sortsql\":\"");
				jsonString.append(sqltext);
				jsonString.append("\",");

				jsonString.append("\"sorts\":\"");
				jsonString.append(sorts);
				jsonString.append("\",");

				jsonString.append("\"executions\":\"");
				jsonString.append(executions);
				jsonString.append("\",");

				jsonString.append("\"sortsexec\":\"");
				jsonString.append(sortsexec);
				jsonString.append("\"}");

				if (i != sql_sort_v.size() - 1) {
					jsonString.append(",");
				}
			}
			size = sql_sort_v.size();
		}

		jsonString.append("],total:" + size + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getOracleUserDetail() {
		String ipaddress = getParaValue("ip");
		Hashtable userinfo_h = new Hashtable();
		Vector returnVal = null;
		Vector returnVal1 = null;
		Vector returnVal2 = null;
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			String hex = IpTranslation.formIpToHex(ipaddress);
			String serverip = hex + ":" + sid;
			try {
				dao = new DBDao();
				userinfo_h = dao.getOracle_nmsorauserinfo(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (userinfo_h != null) {
			returnVal = (Vector) userinfo_h.get("returnVal0");
			returnVal1 = (Vector) userinfo_h.get("returnVal1");
			returnVal2 = (Vector) userinfo_h.get("returnVal2");
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		int size = 0;
		if (returnVal2 != null) {
			for (int i = 0; i < returnVal2.size(); i++) {
				Hashtable ht2 = (Hashtable) returnVal2.get(i);
				String username = ht2.get("username").toString().trim();
				String userid = ht2.get("user_id").toString().trim();
				String status = ht2.get("account_status").toString().trim();
				int k = 0;
				if (returnVal1 != null) {
					for (int j = 0; j < returnVal1.size(); j++) {
						Hashtable ht1 = (Hashtable) returnVal1.get(j);
						String user_ = ht1.get("user#").toString().trim();
						if (user_.equals(userid)) {
							k++;
						}
					}
				}
				if (k > 0) {
					status = "ACTIVE(" + k + ")";
				}
				String cpu_time = "---";
				String sorts = "---";
				String buffer_gets = "---";
				String runtime_mem = "---";
				String cursor = "---";
				String disk_reads = "---";
				String disk_write = "---";
				if (returnVal != null) {
					for (int j = 0; j < returnVal.size(); j++) {
						Hashtable ht = (Hashtable) returnVal.get(j);
						String user_ = ht.get("parsing_user_id").toString().trim();
						if (user_.equals(userid)) {
							cpu_time = ht.get("sum(a.cpu_time)").toString().trim();
							cpu_time = Long.parseLong(cpu_time.toString()) / 60000000 + "��";
							sorts = ht.get("sum(a.sorts)").toString().trim();
							buffer_gets = ht.get("sum(a.buffer_gets)").toString().trim();
							runtime_mem = ht.get("sum(a.runtime_mem)").toString().trim();
							cursor = ht.get("sum(a.version_count)").toString().trim();
							disk_reads = ht.get("sum(a.disk_reads)").toString().trim();
							break;
						}
					}
				}

				jsonString.append("{\"username\":\"");
				jsonString.append(username);
				jsonString.append("\",");

				jsonString.append("\"status\":\"");
				jsonString.append(status);
				jsonString.append("\",");

				jsonString.append("\"cpu_time\":\"");
				jsonString.append(cpu_time);
				jsonString.append("\",");

				jsonString.append("\"sorts\":\"");
				jsonString.append(sorts);
				jsonString.append("\",");

				jsonString.append("\"buffer_gets\":\"");
				jsonString.append(buffer_gets);
				jsonString.append("\",");

				jsonString.append("\"runtime_mem\":\"");
				jsonString.append(runtime_mem);
				jsonString.append("\",");

				jsonString.append("\"cursor\":\"");
				jsonString.append(cursor);
				jsonString.append("\",");

				jsonString.append("\"disk_reads\":\"");
				jsonString.append(disk_reads);
				jsonString.append("\",");

				jsonString.append("\"disk_write\":\"");
				jsonString.append(disk_write);
				jsonString.append("\"}");

				if (i != returnVal2.size() - 1) {
					jsonString.append(",");
				}

			}
			size = returnVal2.size();
		}

		jsonString.append("],total:" + size + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getOracleWaitDetail() {
		String ipaddress = getParaValue("ip");
		Vector wait_v = new Vector();
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			String hex = IpTranslation.formIpToHex(ipaddress);
			String serverip = hex + ":" + sid;
			try {
				dao = new DBDao();
				wait_v = dao.getOracle_nmsorawait(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		int size = 0;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (wait_v != null && wait_v.size() > 0) {
			for (int i = 0; i < wait_v.size(); i++) {
				Hashtable ht2 = (Hashtable) wait_v.get(i);
				String event = ht2.get("event").toString().trim();
				String prev = ht2.get("prev").toString().trim();
				String curr = ht2.get("curr").toString().trim();
				String total = ht2.get("tot").toString().trim();

				jsonString.append("{\"event\":\"");
				jsonString.append(event);
				jsonString.append("\",");

				jsonString.append("\"prev\":\"");
				jsonString.append(prev);
				jsonString.append("\",");

				jsonString.append("\"curr\":\"");
				jsonString.append(curr);
				jsonString.append("\",");

				jsonString.append("\"total\":\"");
				jsonString.append(total);
				jsonString.append("\"}");

				if (i != wait_v.size() - 1) {
					jsonString.append(",");
				}
			}
			size = wait_v.size();
		}
		jsonString.append("],total:" + size + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getOracleBaseInfoDetail() {
		String ipaddress = getParaValue("ip");
		Hashtable baseinfoHash = new Hashtable();
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			String hex = IpTranslation.formIpToHex(ipaddress);
			String serverip = hex + ":" + sid;
			try {
				dao = new DBDao();
				baseinfoHash = dao.getOracle_nmsorabaseinfo(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		int size = 0;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (baseinfoHash != null) {
			@SuppressWarnings("unchecked")
			Iterator<String> keyIterator = baseinfoHash.keySet().iterator();
			while (keyIterator.hasNext()) {
				size++;
				String subentity = keyIterator.next();
				String thevalue = String.valueOf(baseinfoHash.get(subentity));

				jsonString.append("{\"subentity\":\"");
				jsonString.append(subentity);
				jsonString.append("\",");

				jsonString.append("\"thevalue\":\"");
				jsonString.append(thevalue);
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
		}
		jsonString.append("],total:" + size + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getOracleJobDetail() {
		String ipaddress = getParaValue("ip");
		Vector job_v = new Vector();
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			String hex = IpTranslation.formIpToHex(ipaddress);
			String serverip = hex + ":" + sid;
			try {
				dao = new DBDao();
				job_v = dao.getOracle_nmsorajobs(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		int size = 0;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (job_v != null && job_v.size() > 0) {
			for (int i = 0; i < job_v.size(); i++) {
				Hashtable ht2 = (Hashtable) job_v.get(i);
				String job = ht2.get("job").toString().trim();
				String loguser = ht2.get("loguser").toString().trim();
				String lastdate = ht2.get("lastdate").toString().trim();
				String failures = ht2.get("failures").toString().trim();

				jsonString.append("{\"job\":\"");
				jsonString.append(job);
				jsonString.append("\",");

				jsonString.append("\"loguser\":\"");
				jsonString.append(loguser);
				jsonString.append("\",");

				jsonString.append("\"lastdate\":\"");
				jsonString.append(lastdate);
				jsonString.append("\",");

				jsonString.append("\"total\":\"");
				jsonString.append(failures);
				jsonString.append("\"}");

				if (i != job_v.size() - 1) {
					jsonString.append(",");
				}
			}
			size = job_v.size();
		}
		jsonString.append("],total:" + size + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getOracleFileDetail() {
		String type = getParaValue("type");
		String ipaddress = getParaValue("ip");
		Vector contrFile_v = new Vector();
		Vector logFile_v = new Vector();
		Vector keepObj_v = new Vector();
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			String hex = IpTranslation.formIpToHex(ipaddress);
			String serverip = hex + ":" + sid;
			try {
				dao = new DBDao();
				if (type != null && type.equals("contr")) {
					contrFile_v = dao.getOracle_nmsoracontrfile(serverip);
				} else if (type != null && type.equals("log")) {
					logFile_v = dao.getOracle_nmsoralogfile(serverip);
				} else if (type != null && type.equals("keepobj")) {
					keepObj_v = dao.getOracle_nmsorakeepobj(serverip);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		int size = 0;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (contrFile_v != null && contrFile_v.size() > 0) {
			for (int i = 0; i < contrFile_v.size(); i++) {
				Hashtable ht = (Hashtable) contrFile_v.get(i);
				String name = ht.get("name").toString().trim();
				String status = ht.get("status").toString();

				jsonString.append("{\"contrName\":\"");
				jsonString.append(name);
				jsonString.append("\",");

				jsonString.append("\"contrStatus\":\"");
				jsonString.append(status);
				jsonString.append("\"}");

				if (i != contrFile_v.size() - 1) {
					jsonString.append(",");
				}
			}
			size = contrFile_v.size();
		}

		if (logFile_v != null && logFile_v.size() > 0) {
			for (int i = 0; i < logFile_v.size(); i++) {
				Hashtable ht = (Hashtable) logFile_v.get(i);
				String group = ht.get("group#").toString().trim();
				String logstatus = ht.get("status").toString();
				String logtype = ht.get("type").toString();
				String logname = ht.get("member").toString();

				jsonString.append("{\"group\":\"");
				jsonString.append(group);
				jsonString.append("\",");

				jsonString.append("\"logstatus\":\"");
				jsonString.append(logstatus);
				jsonString.append("\",");

				jsonString.append("\"logtype\":\"");
				jsonString.append(logtype);
				jsonString.append("\",");

				jsonString.append("\"logname\":\"");
				jsonString.append(logname);
				jsonString.append("\"}");

				if (i != logFile_v.size() - 1) {
					jsonString.append(",");
				}
			}
			size = logFile_v.size();
		}

		if (keepObj_v != null && keepObj_v.size() > 0) {
			for (int i = 0; i < keepObj_v.size(); i++) {
				Hashtable ht = (Hashtable) keepObj_v.get(i);
				String owner = ht.get("owner").toString().trim();
				String name = ht.get("name").toString();
				String namespace = ht.get("namespace").toString();
				String types = ht.get("type").toString();
				String sharable_mem = ht.get("sharable_mem").toString();
				String pins = ht.get("pins").toString();
				String kept = ht.get("kept").toString();

				jsonString.append("{\"owner\":\"");
				jsonString.append(owner);
				jsonString.append("\",");

				jsonString.append("\"name\":\"");
				jsonString.append(name);
				jsonString.append("\",");

				jsonString.append("\"namespace\":\"");
				jsonString.append(namespace);
				jsonString.append("\",");

				jsonString.append("\"type\":\"");
				jsonString.append(types);
				jsonString.append("\",");

				jsonString.append("\"sharable_mem\":\"");
				jsonString.append(sharable_mem);
				jsonString.append("\",");

				jsonString.append("\"pins\":\"");
				jsonString.append(pins);
				jsonString.append("\",");

				jsonString.append("\"kept\":\"");
				jsonString.append(kept);
				jsonString.append("\"}");

				if (i != keepObj_v.size() - 1) {
					jsonString.append(",");
				}
			}
			size = keepObj_v.size();
		}

		jsonString.append("],total:" + size + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	public void execute(String action) {
		if (action.equals("getDbNodeDataByType")) {
			getDbNodeDataByType();
		} else if (action.equals("addDb")) {
			addDb();
		} else if (action.equals("dbReadyEdit")) {
			dbReadyEdit();
		} else if (action.equals("dbEdit")) {
			dbEdit();
		} else if (action.equals("deleteDbs")) {
			deleteDbs();
		} else if (action.equals("getMysqlVariablesDetail")) {
			getMysqlVariablesDetail();
		} else if (action.equals("getMysqlNodeSystem")) {
			getMysqlNodeSystem();
		} else if (action.equals("getMysqlConnectDetail")) {
			getMysqlConnectDetail();
		} else if (action.equals("getMysqlTablesDetail")) {
			getMysqlTablesDetail();
		} else if (action.equals("getMysqlSpacesDetail")) {
			getMysqlSpacesDetail();
		} else if (action.equals("getMysqlStatusDetail")) {
			getMysqlStatusDetail();
		} else if (action.equals("getMysqlConfigDetail1")) {
			getMysqlConfigDetail1();
		} else if (action.equals("getDBEventDetail")) {
			getDBEventDetail();
		} else if (action.equals("getOracleLockDetail")) {
			getOracleLockDetail();
		} else if (action.equals("getOracleSessionDetail")) {
			getOracleSessionDetail();
		} else if (action.equals("getOracleRollbackDetail")) {
			getOracleRollbackDetail();
		} else if (action.equals("getOracleTableDetail")) {
			getOracleTableDetail();
		} else if (action.equals("getOracleSpacesDetail")) {
			getOracleSpacesDetail();
		} else if (action.equals("getOracleTopsqlDetail")) {
			getOracleTopsqlDetail();
		} else if (action.equals("getOracleUserDetail")) {
			getOracleUserDetail();
		} else if (action.equals("getOracleWaitDetail")) {
			getOracleWaitDetail();
		} else if (action.equals("getOracleBaseInfoDetail")) {
			getOracleBaseInfoDetail();
		} else if (action.equals("getOracleJobDetail")) {
			getOracleJobDetail();
		} else if (action.equals("getOracleSystem")) {
			getOracleSystem();
		} else if (action.equals("getOracleFileDetail")) {
			getOracleFileDetail();
		} else if (action.equals("batchAddMonitor")) {
			batchAddMonitor();
		} else if (action.equals("batchCancleMonitor")) {
			batchCancleMonitor();
		} else if (action.equals("getSqlserverConfig")) {
			getSqlserverConfig();
		} else if (action.equals("getSqlserverSystem")) {
			getSqlserverSystem();
		} else if (action.equals("getSqlserverProcess")) {
			getSqlserverProcess();
		} else if (action.equals("getSqlserverLock")) {
			getSqlserverLock();
		} else if (action.equals("getSqlserverPerformance")) {
			getSqlserverPerformance();
		} else if (action.equals("getSqlserverDb")) {
			getSqlserverDb();
		}
	}

	private void getSqlserverDb() {
		String id = getParaValue("id");
		DBVo vo = new DBVo();
		DBDao dbDao = new DBDao();
		try {
			vo = (DBVo) dbDao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbDao.close();
		}
		Hashtable connHt = new Hashtable();
		Hashtable cacheHt = new Hashtable();
		Hashtable sqlHt = new Hashtable();
		Hashtable scanHt = new Hashtable();
		try {
			dbDao = new DBDao();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverIp = hex + ":" + vo.getAlias();
			connHt = dbDao.getSqlserver_nmsconns(serverIp);
			cacheHt = dbDao.getSqlserver_nmscaches(serverIp);
			sqlHt = dbDao.getSqlserver_nmssqls(serverIp);
			scanHt = dbDao.getSqlserver_nmsscans(serverIp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbDao.close();
		}

		StringBuffer jsonString = new StringBuffer("{");
		if (null != cacheHt && cacheHt.size() > 0) {
			jsonString.append("cacheRows:[");
			Enumeration cacheE = cacheHt.keys();
			String cacheKey = (String) null;
			while (cacheE.hasMoreElements()) {
				cacheKey = (String) cacheE.nextElement();

				jsonString.append("{\"key\":\"");
				jsonString.append(cacheKey);
				jsonString.append("\",");

				jsonString.append("\"value\":\"");
				jsonString.append(cacheHt.get(cacheKey));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("],");
		}

		if (null != connHt && connHt.size() > 0) {
			jsonString.append("connRows:[");
			Enumeration connE = connHt.keys();
			String connKey = (String) null;
			while (connE.hasMoreElements()) {
				connKey = (String) connE.nextElement();

				jsonString.append("{\"key\":\"");
				jsonString.append(connKey);
				jsonString.append("\",");

				jsonString.append("\"value\":\"");
				jsonString.append(connHt.get(connKey));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("],");
		}

		if (null != sqlHt && sqlHt.size() > 0) {
			jsonString.append("sqlRows:[");
			Enumeration sqlE = sqlHt.keys();
			String sqlKey = (String) null;
			while (sqlE.hasMoreElements()) {
				sqlKey = (String) sqlE.nextElement();

				jsonString.append("{\"key\":\"");
				jsonString.append(sqlKey);
				jsonString.append("\",");

				jsonString.append("\"value\":\"");
				jsonString.append(sqlHt.get(sqlKey));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("],");
		}

		if (null != scanHt && scanHt.size() > 0) {
			jsonString.append("scanRows:[");
			Enumeration scanE = scanHt.keys();
			String scanKey = (String) null;
			while (scanE.hasMoreElements()) {
				scanKey = (String) scanE.nextElement();

				jsonString.append("{\"key\":\"");
				jsonString.append(scanKey);
				jsonString.append("\",");

				jsonString.append("\"value\":\"");
				jsonString.append(scanHt.get(scanKey));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("],");
		}
		jsonString.deleteCharAt(jsonString.length() - 1);
		jsonString.append("}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getSqlserverPerformance() {
		String id = getParaValue("id");
		DBVo vo = new DBVo();
		DBDao dbDao = new DBDao();
		try {
			vo = (DBVo) dbDao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbDao.close();
		}
		Hashtable pagesHt = new Hashtable();
		Hashtable dataBaseHt = new Hashtable();
		try {
			dbDao = new DBDao();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverIp = hex + ":" + vo.getAlias();
			pagesHt = dbDao.getSqlserver_nmspages(serverIp);
			dataBaseHt = dbDao.getSqlserver_nmsdbvalue(serverIp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbDao.close();
		}

		CreateAmColumnPic amColumn = new CreateAmColumnPic();
		String hitDataString = "-1";
		String dataBaseString = "-1";
		String logFileString = "-1";

		String bufferCacheHitRatio = (String) null;
		String planCacheHitRatio = (String) null;
		String cursorManagerByTypeHitRatio = (String) null;
		String catalogMetadataHitRatio = (String) null;

		double dBufferCacheHitRatio = 0.0;
		double dPlanCacheHitRatio = 0.0;
		double dCursorManagerByTypeHitRatio = 0.0;
		double dCatalogMetadataHitRatio = 0.0;

		if (null != pagesHt && pagesHt.size() > 0) {
			bufferCacheHitRatio = (String) pagesHt.get("bufferCacheHitRatio");
			if (null != bufferCacheHitRatio && !bufferCacheHitRatio.equals(""))
				dBufferCacheHitRatio = Double.valueOf(bufferCacheHitRatio);
			planCacheHitRatio = (String) pagesHt.get("planCacheHitRatio");
			if (null != planCacheHitRatio && !planCacheHitRatio.equals(""))
				dPlanCacheHitRatio = Double.valueOf(planCacheHitRatio);
			cursorManagerByTypeHitRatio = (String) pagesHt.get("cursorManagerByTypeHitRatio");
			if (null != cursorManagerByTypeHitRatio && !cursorManagerByTypeHitRatio.equals(""))
				dCursorManagerByTypeHitRatio = Double.valueOf(cursorManagerByTypeHitRatio);
			catalogMetadataHitRatio = (String) pagesHt.get("catalogMetadataHitRatio");
			if (null != catalogMetadataHitRatio && !catalogMetadataHitRatio.equals(""))
				dCatalogMetadataHitRatio = Double.valueOf(catalogMetadataHitRatio);

			String[] items = { "������������ ", "planCache������", "CursorManagerByType������", "CatalogMetadata������" };
			String[] data = { dBufferCacheHitRatio + "", dPlanCacheHitRatio + "", dCursorManagerByTypeHitRatio + "", dCatalogMetadataHitRatio + "" };

			hitDataString = amColumn.createSqlUtilChart(data, items);
		}

		Hashtable tempDataBaseHt = null;
		if (dataBaseHt != null && dataBaseHt.size() > 0)
			tempDataBaseHt = (Hashtable) dataBaseHt.get("database");
		if (tempDataBaseHt != null && tempDataBaseHt.size() > 0) {
			StringBuffer seriesSb = new StringBuffer();
			StringBuffer graphsSb = new StringBuffer();
			seriesSb.append("<chart><series>");
			graphsSb.append("<graphs><graph gid='0'>");
			int i = 0;
			Hashtable tablespaceHt = new Hashtable();
			for (Iterator it = tempDataBaseHt.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				tablespaceHt = (Hashtable) tempDataBaseHt.get(key);
				if (tablespaceHt != null && tablespaceHt.size() > 0) {
					seriesSb.append("<value xid='").append(i).append("'>").append(tablespaceHt.get("dbname")).append("</value>");
					graphsSb.append("<value xid='").append(i).append("' color='").append("#" + generateColor()).append("'>" + tablespaceHt.get("usedperc")).append("</value>");
					i++;
				}
			}
			seriesSb.append("</series>");
			seriesSb.append(graphsSb);
			seriesSb.append("</graph></graphs></chart>");

			dataBaseString = seriesSb.toString();
		}

		Hashtable tempLogFileHt = null;
		if (dataBaseHt != null && dataBaseHt.size() > 0)
			tempLogFileHt = (Hashtable) dataBaseHt.get("logfile");
		if (tempLogFileHt != null && tempLogFileHt.size() > 0) {
			StringBuffer seriesSb = new StringBuffer();
			StringBuffer graphsSb = new StringBuffer();
			seriesSb.append("<chart><series>");
			graphsSb.append("<graphs><graph gid='0'>");
			int i = 0;
			Hashtable tablespaceHt = new Hashtable();
			for (Iterator it = tempLogFileHt.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				tablespaceHt = (Hashtable) tempLogFileHt.get(key);
				if (tablespaceHt != null && tablespaceHt.size() > 0) {
					seriesSb.append("<value xid='").append(i).append("'>").append(tablespaceHt.get("logname")).append("</value>");
					graphsSb.append("<value xid='").append(i).append("' color='").append("#" + generateColor()).append("'>" + tablespaceHt.get("usedperc")).append("</value>");
					i++;
				}
			}
			seriesSb.append("</series>");
			seriesSb.append(graphsSb);
			seriesSb.append("</graph></graphs></chart>");

			logFileString = seriesSb.toString();
		}
		StringBuffer jsonString = new StringBuffer("{");
		if (null != tempDataBaseHt && tempDataBaseHt.size() > 0) {
			jsonString.append("tableSpaceRows:[");

			Enumeration tableSpaceE = tempDataBaseHt.elements();
			Hashtable tableSpaceHt = new Hashtable();
			while (tableSpaceE.hasMoreElements()) {
				tableSpaceHt = (Hashtable) tableSpaceE.nextElement();
				jsonString.append("{\"dbName\":\"");
				jsonString.append(tableSpaceHt.get("dbname"));
				jsonString.append("\",");

				jsonString.append("\"capacity\":\"");
				jsonString.append(tableSpaceHt.get("sizes"));
				jsonString.append("\",");

				jsonString.append("\"used\":\"");
				jsonString.append(tableSpaceHt.get("usedsize"));
				jsonString.append("\",");

				jsonString.append("\"usePercent\":\"");
				jsonString.append(tableSpaceHt.get("usedperc"));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("],");
		}

		if (null != hitDataString && !hitDataString.equals("-1")) {
			jsonString.append("\"hitRateFlexString\":\"" + hitDataString + "\",");
		}
		if (null != dataBaseString && !dataBaseString.equals("-1")) {
			jsonString.append("\"tableSpaceFlexString\":\"" + dataBaseString + "\",");
		}
		if (null != logFileString && !logFileString.equals("-1")) {
			jsonString.append("\"logFileFlexString\":\"" + logFileString + "\",");
		}
		jsonString.deleteCharAt(jsonString.length() - 1);
		jsonString.append("}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getSqlserverLock() {
		String id = getParaValue("id");
		DBVo vo = new DBVo();
		DBDao dbDao = new DBDao();
		try {
			vo = (DBVo) dbDao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbDao.close();
		}
		Hashtable lockHt = new Hashtable();
		Vector lockinfoVector = new Vector();
		try {
			dbDao = new DBDao();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverIp = hex + ":" + vo.getAlias();
			lockHt = dbDao.getSqlserver_nmslocks(serverIp);
			lockinfoVector = dbDao.getSqlserver_nmslockinfo_v(serverIp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbDao.close();
		}
		StringBuffer jsonString = new StringBuffer("{");
		if (null != lockHt && lockHt.size() > 0) {
			jsonString.append("lockRows:[");
			Enumeration sysE = lockHt.keys();
			String sysKey = (String) null;
			while (sysE.hasMoreElements()) {
				sysKey = (String) sysE.nextElement();

				jsonString.append("{\"key\":\"");
				jsonString.append(sysKey);
				jsonString.append("\",");

				jsonString.append("\"value\":\"");
				jsonString.append(lockHt.get(sysKey));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("],");
		}

		if (null != lockinfoVector && lockinfoVector.size() > 0) {
			jsonString.append("lockDetailRows:[");
			Hashtable tempHt = new Hashtable();
			String resourceType = "-1";
			String req_mode = "-1";
			String req_status = "-1";
			String req_ownertype = "-1";
			for (int i = 0; i < lockinfoVector.size(); i++) {
				tempHt = (Hashtable) lockinfoVector.get(i);

				if (null != tempHt.get("rsc_type")) {
					resourceType = (String) tempHt.get("rsc_type");
				}

				if (null != tempHt.get("req_mode")) {
					req_mode = (String) tempHt.get("req_mode");
				}

				if (null != tempHt.get("req_status")) {
					req_status = (String) tempHt.get("req_status");
				}

				if (null != tempHt.get("req_ownertype")) {
					req_ownertype = (String) tempHt.get("req_ownertype");
				}
				jsonString.append("{\"lockResource\":\"");
				jsonString.append(tempHt.get("rsc_text"));
				jsonString.append("\",");

				jsonString.append("\"dataBase\":\"");
				jsonString.append(tempHt.get("dbname"));
				jsonString.append("\",");

				jsonString.append("\"resourceType\":\"");
				jsonString.append(rsc_type_ht.get(resourceType));
				jsonString.append("\",");

				jsonString.append("\"requestType\":\"");
				jsonString.append(req_mode_ht.get(req_mode));
				jsonString.append("\",");

				jsonString.append("\"requestStatus\":\"");
				jsonString.append(req_status_ht.get(req_status));
				jsonString.append("\",");

				jsonString.append("\"useCount\":\"");
				jsonString.append(tempHt.get("req_refcnt"));
				jsonString.append("\",");

				jsonString.append("\"lockTTL\":\"");
				jsonString.append(tempHt.get("req_lifetime"));
				jsonString.append("\",");

				jsonString.append("\"processId\":\"");
				jsonString.append(tempHt.get("req_spid"));
				jsonString.append("\",");

				jsonString.append("\"objectType\":\"");
				jsonString.append(req_ownertype_ht.get(req_ownertype));
				jsonString.append("\"}");

				if (i != lockinfoVector.size() - 1) {
					jsonString.append(",");
				}
			}
			jsonString.append("]");
		}
		jsonString.append("}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getSqlserverProcess() {
		String id = getParaValue("id");
		DBVo vo = new DBVo();
		DBDao dbDao = new DBDao();
		try {
			vo = (DBVo) dbDao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbDao.close();
		}
		Vector processVector = new Vector();
		try {
			dbDao = new DBDao();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverIp = hex + ":" + vo.getAlias();
			processVector = dbDao.getSqlserver_nmsinfo_v(serverIp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbDao.close();
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != processVector && processVector.size() > 0) {
			Hashtable tempHt = new Hashtable();
			for (int i = 0; i < processVector.size(); i++) {
				tempHt = (Hashtable) processVector.get(i);
				if (null != tempHt) {
					jsonString.append("{\"processId\":\"");
					jsonString.append(tempHt.get("spid"));
					jsonString.append("\",");

					jsonString.append("\"dataBase\":\"");
					jsonString.append(tempHt.get("dbname"));
					jsonString.append("\",");

					jsonString.append("\"user\":\"");
					jsonString.append(tempHt.get("username"));
					jsonString.append("\",");

					jsonString.append("\"cpuTime\":\"");
					jsonString.append(tempHt.get("cpu"));
					jsonString.append("\",");

					jsonString.append("\"diskIO\":\"");
					jsonString.append(tempHt.get("physical_io"));
					jsonString.append("\",");

					jsonString.append("\"pages\":\"");
					jsonString.append(tempHt.get("memusage"));
					jsonString.append("\",");

					jsonString.append("\"status\":\"");
					jsonString.append(tempHt.get("status"));
					jsonString.append("\",");

					jsonString.append("\"workStation\":\"");
					jsonString.append(tempHt.get("hostname"));
					jsonString.append("\",");

					jsonString.append("\"application\":\"");
					jsonString.append(tempHt.get("program_name"));
					jsonString.append("\",");

					jsonString.append("\"startTime\":\"");
					jsonString.append(tempHt.get("login_time"));
					jsonString.append("\"}");

					if (i != processVector.size() - 1) {
						jsonString.append(",");
					}
				}
			}
		}
		jsonString.append("],total:" + processVector.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getSqlserverSystem() {
		String id = getParaValue("id");
		DBVo vo = new DBVo();
		DBDao dbDao = new DBDao();
		try {
			vo = (DBVo) dbDao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbDao.close();
		}

		Hashtable pageHt = new Hashtable();
		Hashtable statisticsHt = new Hashtable();
		Hashtable memoryHt = new Hashtable();

		try {
			dbDao = new DBDao();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverIp = hex + ":" + vo.getAlias();
			pageHt = dbDao.getSqlserver_nmspages(serverIp);
			statisticsHt = dbDao.getSqlserver_nmsstatisticsHash(serverIp);
			memoryHt = dbDao.getSqlserver_nmsmems(serverIp);
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuffer jsonString = new StringBuffer("{");
		if (null != pageHt && pageHt.size() > 0) {
			jsonString.append("pageRows:[");
			Enumeration pageE = pageHt.keys();
			String pageKey = (String) null;
			while (pageE.hasMoreElements()) {
				pageKey = (String) pageE.nextElement();

				jsonString.append("{\"key\":\"");
				jsonString.append(pageKey);
				jsonString.append("\",");

				jsonString.append("\"value\":\"");
				jsonString.append(pageHt.get(pageKey));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("],");
		}
		if (null != memoryHt && memoryHt.size() > 0) {
			jsonString.append("memoryRows:[");
			Enumeration memoryE = memoryHt.keys();
			String memoryKey = (String) null;
			while (memoryE.hasMoreElements()) {
				memoryKey = (String) memoryE.nextElement();

				jsonString.append("{\"key\":\"");
				jsonString.append(memoryKey);
				jsonString.append("\",");

				jsonString.append("\"value\":\"");
				jsonString.append(memoryHt.get(memoryKey));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("],");
		}
		if (null != statisticsHt && statisticsHt.size() > 0) {
			jsonString.append("avgWaitRows:[");
			Enumeration avgWaitE = statisticsHt.keys();
			String avgWaitKey = (String) null;
			while (avgWaitE.hasMoreElements()) {
				avgWaitKey = (String) avgWaitE.nextElement();
				if (!avgWaitKey.startsWith("pingjun_")) {
					continue;
				}
				jsonString.append("{\"key\":\"");
				jsonString.append(avgWaitKey.replace("pingjun_", ""));
				jsonString.append("\",");

				jsonString.append("\"value\":\"");
				jsonString.append(statisticsHt.get(avgWaitKey));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("],");
		}

		if (null != statisticsHt && statisticsHt.size() > 0) {
			jsonString.append("processWaitRows:[");
			Enumeration processWaitE = statisticsHt.keys();
			String processWaitKey = (String) null;
			while (processWaitE.hasMoreElements()) {
				processWaitKey = (String) processWaitE.nextElement();
				if (!processWaitKey.startsWith("jingxing_")) {
					continue;
				}
				jsonString.append("{\"key\":\"");
				jsonString.append(processWaitKey.replace("jingxing_", ""));
				jsonString.append("\",");

				jsonString.append("\"value\":\"");
				jsonString.append(statisticsHt.get(processWaitKey));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("],");
		}

		if (null != statisticsHt && statisticsHt.size() > 0) {
			jsonString.append("startUpWaitRows:[");
			Enumeration startUpWaitE = statisticsHt.keys();
			String startUpWaitKey = (String) null;
			while (startUpWaitE.hasMoreElements()) {
				startUpWaitKey = (String) startUpWaitE.nextElement();
				if (!startUpWaitKey.startsWith("qidong_")) {
					continue;
				}
				jsonString.append("{\"key\":\"");
				jsonString.append(startUpWaitKey.replace("qidong_", ""));
				jsonString.append("\",");

				jsonString.append("\"value\":\"");
				jsonString.append(statisticsHt.get(startUpWaitKey));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("],");
		}

		if (null != statisticsHt && statisticsHt.size() > 0) {
			jsonString.append("countWaitRows:[");
			Enumeration countWaitE = statisticsHt.keys();
			String countWaitKey = (String) null;
			while (countWaitE.hasMoreElements()) {
				countWaitKey = (String) countWaitE.nextElement();
				if (!countWaitKey.startsWith("leiji_")) {
					continue;
				}
				jsonString.append("{\"key\":\"");
				jsonString.append(countWaitKey.replace("leiji_", ""));
				jsonString.append("\",");

				jsonString.append("\"value\":\"");
				jsonString.append(statisticsHt.get(countWaitKey));
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.deleteCharAt(jsonString.length() - 1);
			jsonString.append("],");
		}

		jsonString.deleteCharAt(jsonString.length() - 1);
		jsonString.append("}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getSqlserverConfig() {
		String id = getParaValue("id");
		Hashtable statusHt = new Hashtable();
		Hashtable sysValueHt = new Hashtable();
		DBVo vo = new DBVo();
		DBDao dbDao = new DBDao();
		try {
			vo = (DBVo) dbDao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbDao.close();
		}
		String isM = "������";
		if (vo.getManaged() == 0)
			isM = "δ����";

		String status = "<font color=red>����ֹͣ</font>";
		String hostName = "";
		String version = "";
		String servicePackage = "";
		String processId = "";
		String userMode = "";
		String securityMode = "";
		String integratedInstance = "";
		try {
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverIp = hex + ":" + vo.getAlias();
			statusHt = dbDao.getSqlserver_nmsstatus(serverIp);
			sysValueHt = dbDao.getSqlserver_nmssysvalue(serverIp);
			String p_status = (String) statusHt.get("status");

			if (p_status != null && p_status.length() > 0) {
				if ("1".equalsIgnoreCase(p_status)) {
					status = "��������";
				}
			}
			if (null != sysValueHt && sysValueHt.size() > 0) {
				if (sysValueHt.get("MACHINENAME") != null) {
					hostName = (String) sysValueHt.get("MACHINENAME");
				}
				if (sysValueHt.get("VERSION") != null) {
					version = (String) sysValueHt.get("VERSION");
					version = version.replaceAll("(\r\n|\r|\n|\n\r)", "");
				}
				if (sysValueHt.get("productlevel") != null) {
					servicePackage = (String) sysValueHt.get("productlevel");
				}
				if (sysValueHt.get("ProcessID") != null) {
					processId = (String) sysValueHt.get("ProcessID");
				}
				if (sysValueHt.get("IsSingleUser") != null) {
					userMode = (String) sysValueHt.get("IsSingleUser");
				}
				if (sysValueHt.get("IsIntegratedSecurityOnly") != null) {
					securityMode = (String) sysValueHt.get("IsIntegratedSecurityOnly");
				}
				if (sysValueHt.get("IsClustered") != null) {
					integratedInstance = (String) sysValueHt.get("IsClustered");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		jsonString.append("{\"nodeIp\":\"");
		jsonString.append(vo.getIpAddress());
		jsonString.append("\",");

		jsonString.append("\"nodeAlias\":\"");
		jsonString.append(vo.getAlias());
		jsonString.append("\",");

		jsonString.append("\"isM\":\"");
		jsonString.append(isM);
		jsonString.append("\",");

		jsonString.append("\"status\":\"");
		jsonString.append(status);
		jsonString.append("\",");

		jsonString.append("\"hostName\":\"");
		jsonString.append(hostName);
		jsonString.append("\",");

		jsonString.append("\"version\":\"");
		jsonString.append(version);
		jsonString.append("\",");

		jsonString.append("\"servicePackage\":\"");
		jsonString.append(servicePackage);
		jsonString.append("\",");

		jsonString.append("\"processId\":\"");
		jsonString.append(processId);
		jsonString.append("\",");

		jsonString.append("\"userMode\":\"");
		jsonString.append(userMode);
		jsonString.append("\",");

		jsonString.append("\"securityMode\":\"");
		jsonString.append(securityMode);
		jsonString.append("\",");

		jsonString.append("\"integratedInstance\":\"");
		jsonString.append(integratedInstance);
		jsonString.append("\"}");

		jsonString.append("],total:1}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void batchAddMonitor() {
		StringBuffer sb = new StringBuffer("���ü��");
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		DBManager dbOp = new DBManager();
		String sql = null;
		try {
			for (int i = 0; i < ids.length; i++) {
				sql = "update app_db_node set managed=1 where id=" + ids[i];
				dbOp.addBatch(sql);
			}
			dbOp.executeBatch();
			sb.append("�ɹ�");
		} catch (Exception e) {
			e.printStackTrace();
			dbOp.rollback();
			sb.append("ʧ��");
		} finally {
			if (null != dbOp) {
				dbOp.close();
			}
		}
		out.print(sb.toString());
		out.flush();
	}

	private void batchCancleMonitor() {
		StringBuffer sb = new StringBuffer("ȡ�����");
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		DBManager dbOp = new DBManager();
		String sql = null;
		try {
			for (int i = 0; i < ids.length; i++) {
				sql = "update app_db_node set managed=0 where id=" + ids[i];
				dbOp.addBatch(sql);
			}
			dbOp.executeBatch();
			sb.append("�ɹ�");
		} catch (Exception e) {
			e.printStackTrace();
			dbOp.rollback();
			sb.append("ʧ��");
		} finally {
			if (null != dbOp) {
				dbOp.close();
			}
		}
		out.print(sb.toString());
		out.flush();
	}

	private int getDbtypeFromString(String type) {
		int dbtype = 1;
		if (type.equals("oracle")) {
			dbtype = 1;
		} else if (type.equals("sqlserver")) {
			dbtype = 2;
		} else if (type.equals("mysql")) {
			dbtype = 4;
		} else if (type.equals("db2")) {
			dbtype = 5;
		} else if (type.equals("sybase")) {
			dbtype = 6;
		} else if (type.equals("informix")) {
			dbtype = 7;
		}
		return dbtype;
	}

	private String getValueFromNameForMysqlSpace(String name) {
		if (name.equalsIgnoreCase("Max_used_connections")) {
			name = "��������Ӧ�����������";
		}
		if (name.equalsIgnoreCase("Handler_read_first")) {
			name = "�����е�һ�������Ĵ���";
		}
		if (name.equalsIgnoreCase("Handler_read_key")) {
			name = "���ݼ���һ�е�������";
		}
		if (name.equalsIgnoreCase("Handler_read_next")) {
			name = "���ռ�˳�����һ�е�������";
		}
		if (name.equalsIgnoreCase("Handler_read_prev")) {
			name = "���ռ�˳���ǰһ�е�������";
		}
		if (name.equalsIgnoreCase("Handler_read_rnd")) {
			name = "H���ݹ̶�λ�ö�һ�е�������";
		}
		if (name.equalsIgnoreCase("Handler_read_rnd_next")) {
			name = "�������ļ��ж���һ�е�������";
		}
		if (name.equalsIgnoreCase("Open_tables")) {
			name = "��ǰ�򿪵ı������";
		}
		if (name.equalsIgnoreCase("Opened_tables")) {
			name = "�Ѿ��򿪵ı������";
		}
		if (name.equalsIgnoreCase("Threads_cached")) {
			name = "�̻߳����ڵ��̵߳�����";
		}
		if (name.equalsIgnoreCase("Threads_connected")) {
			name = "��ǰ�򿪵����ӵ�����";
		}
		if (name.equalsIgnoreCase("Threads_created")) {
			name = "���������������ӵ��߳���";
		}
		if (name.equalsIgnoreCase("Threads_running")) {
			name = "����ķ�˯��״̬���߳���";
		}
		if (name.equalsIgnoreCase("Table_locks_immediate")) {
			name = "������õı�����Ĵ���";
		}
		if (name.equalsIgnoreCase("Table_locks_waited")) {
			name = "����������õı�����Ĵ���";
		}
		if (name.equalsIgnoreCase("Key_read_requests")) {
			name = "�ӻ�����������ݿ��������";
		}
		if (name.equalsIgnoreCase("Key_reads")) {
			name = "��Ӳ�̶�ȡ�������ݿ�Ĵ���";
		}
		if (name.equalsIgnoreCase("log_slow_queries")) {
			name = "�Ƿ��¼����ѯ";
		}
		if (name.equalsIgnoreCase("slow_launch_time")) {
			name = "�����̵߳�ʱ�䳬��������������������Slow_launch_threads״̬����";
		}
		return name;

	}

	private String getSizeForMysqlStatus(String size) {
		if (size.equalsIgnoreCase("Aborted_clients")) {
			size = "���ڿͻ���û����ȷ�ر����ӵ��¿ͻ�����ֹ���жϵ�������";
		}
		if (size.equalsIgnoreCase("Aborted_connects")) {
			size = "��ͼ���ӵ�MySQL��������ʧ�ܵ�������";
		}
		if (size.equalsIgnoreCase("Binlog_cache_disk_use")) {
			size = "ʹ����ʱ��������־���浫����binlog_cache_sizeֵ��ʹ����ʱ�ļ������������е�������������";
		}
		if (size.equalsIgnoreCase("Binlog_cache_use")) {
			size = "ʹ����ʱ��������־�������������";
		}
		if (size.equalsIgnoreCase("Bytes_received")) {
			size = "�����пͻ��˽��յ����ֽ���";
		}
		if (size.equalsIgnoreCase("Bytes_sent")) {
			size = "���͸����пͻ��˵��ֽ���";
		}
		if (size.equalsIgnoreCase("Com_admin_commands")) {
			size = "admin_commands���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_alter_db")) {
			size = "alter_db���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_alter_table")) {
			size = "alter_table���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_analyze")) {
			size = "analyze���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_backup_table")) {
			size = "backup_table���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_begin")) {
			size = "begin���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_change_db")) {
			size = "change_db���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_change_master")) {
			size = "change_master���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_check")) {
			size = "check���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_checksum")) {
			size = "checksum���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_commit")) {
			size = "commit���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_create_db")) {
			size = "create_db���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_create_function")) {
			size = "create_function���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_create_index")) {
			size = "create_index���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_create_table")) {
			size = "create_table���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_dealloc_sql")) {
			size = "dealloc_sql���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_delete")) {
			size = "delete���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_delete_multi")) {
			size = "delete_multi���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_do")) {
			size = "do���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_drop_db")) {
			size = "drop_db���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_drop_function")) {
			size = "drop_function���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_drop_index")) {
			size = "drop_index���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_drop_table")) {
			size = "drop_table���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_drop_user")) {
			size = "drop_user���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_execute_sql")) {
			size = "execute_sql���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_flush")) {
			size = "flush���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_grant")) {
			size = "grant���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_ha_close")) {
			size = "ha_close���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_ha_open")) {
			size = "ha_open���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_ha_read")) {
			size = "ha_read���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_help")) {
			size = "help���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_insert")) {
			size = "insert���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_insert_select")) {
			size = "insert_select���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_kill")) {
			size = "kill���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_load")) {
			size = "load���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_load_master_data")) {
			size = "load_master_data���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_load_master_table")) {
			size = "load_master_table���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_lock_tables")) {
			size = "lock_tables���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_optimize")) {
			size = "optimize���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_preload_keys")) {
			size = "preload_keys���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_prepare_sql")) {
			size = "prepare_sql���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_purge")) {
			size = "purge���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_purge_before_date")) {
			size = "purge_before_date���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_rename_table")) {
			size = "rename_table���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_repair")) {
			size = "repair���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_replace")) {
			size = "replace���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_replace_select")) {
			size = "replace_select���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_reset")) {
			size = "reset���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_restore_table")) {
			size = "restore_table���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_revoke")) {
			size = "revoke���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_revoke_all")) {
			size = "revoke_all���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_rollback")) {
			size = "rollback���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_savepoint")) {
			size = "savepoint���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_select")) {
			size = "select���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_set_option")) {
			size = "set_option���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_binlog_events")) {
			size = "show_binlog_events���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_binlogs")) {
			size = "show_binlogs���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_charsets")) {
			size = "show_charsets���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_collations")) {
			size = "show_collations���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_column_types")) {
			size = "how_column_types���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_create_db")) {
			size = "show_create_db���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_create_table")) {
			size = "show_create_table���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_databases")) {
			size = "show_databases���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_errors")) {
			size = "show_errors���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_fields")) {
			size = "show_fields���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_grants")) {
			size = "show_grants���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_innodb_status")) {
			size = "show_innodb_status���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_keys")) {
			size = "show_keys���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_logs")) {
			size = "show_logs���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_master_status")) {
			size = "show_master_status���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_ndb_status")) {
			size = "show_ndb_status���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_new_master")) {
			size = "show_new_master���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_open_tables")) {
			size = "show_open_tables���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_privileges")) {
			size = "show_privileges���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_processlist")) {
			size = "show_processlist���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_slave_hosts")) {
			size = "show_slave_hosts���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_slave_status")) {
			size = "show_slave_status���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_status")) {
			size = "show_status���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_storage_engines")) {
			size = "show_storage_engines���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_tables")) {
			size = "show_tables���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_triggers")) {
			size = "show_triggers���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_variables")) {
			size = "show_variables���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_show_warnings")) {
			size = "show_warnings���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_slave_start")) {
			size = "slave_start���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_slave_stop")) {
			size = "slave_stop���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_truncate")) {
			size = "truncate���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_unlock_tables")) {
			size = "unlock_tables���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_update")) {
			size = "update���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_update_multi")) {
			size = "update_multi���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_xa_commit")) {
			size = "xa_commi���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_xa_end")) {
			size = "xa_end���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_xa_prepare")) {
			size = "xa_prepare���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_xa_recover")) {
			size = "xa_recover���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_xa_rollback")) {
			size = "xa_rollback���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Com_xa_start")) {
			size = "xa_start���ִ�еĴ���";
		}
		if (size.equalsIgnoreCase("Connections")) {
			size = "��ͼ���ӵ�(�����Ƿ�ɹ�)MySQL��������������";
		}
		if (size.equalsIgnoreCase("Created_tmp_disk_tables")) {
			size = "������ִ�����ʱ��Ӳ�����Զ���������ʱ�������";
		}
		if (size.equalsIgnoreCase("Created_tmp_files")) {
			size = "mysqld�Ѿ���������ʱ�ļ�������";
		}
		if (size.equalsIgnoreCase("Created_tmp_tables")) {
			size = "������ִ�����ʱ�Զ��������ڴ��е���ʱ�������";
		}
		if (size.equalsIgnoreCase("Delayed_errors")) {
			size = "��INSERT DELAYEDд�ĳ��ִ��������(����Ϊduplicate key)";
		}
		if (size.equalsIgnoreCase("Delayed_insert_threads")) {
			size = "��ʹ�õ�INSERT DELAYED�������߳���";
		}
		if (size.equalsIgnoreCase("Delayed_writes")) {
			size = "д���INSERT DELAYED����";
		}
		if (size.equalsIgnoreCase("Flush_commands")) {
			size = "ִ�е�FLUSH�����";
		}
		if (size.equalsIgnoreCase("Handler_commit")) {
			size = "�ڲ��ύ�����";
		}
		if (size.equalsIgnoreCase("Handler_delete")) {
			size = "�дӱ���ɾ���Ĵ���";
		}
		if (size.equalsIgnoreCase("Handler_discover")) {
			size = "MySQL������������NDB CLUSTER�洢�����Ƿ�֪��ĳһ���ֵı�";
		}
		if (size.equalsIgnoreCase("Handler_read_first")) {
			size = "�����е�һ�������Ĵ���";
		}
		if (size.equalsIgnoreCase("Handler_read_key")) {
			size = "���ݼ���һ�е�������";
		}
		if (size.equalsIgnoreCase("Handler_read_next")) {
			size = "���ռ�˳�����һ�е�������";
		}
		if (size.equalsIgnoreCase("Handler_read_prev")) {
			size = "���ռ�˳���ǰһ�е�������";
		}
		if (size.equalsIgnoreCase("Handler_read_rnd")) {
			size = "���ݹ̶�λ�ö�һ�е�������";
		}
		if (size.equalsIgnoreCase("Handler_read_rnd_next")) {
			size = "�������ļ��ж���һ�е�������";
		}
		if (size.equalsIgnoreCase("Handler_rollback")) {
			size = "�ڲ�ROLLBACK��������";
		}
		if (size.equalsIgnoreCase("Handler_update")) {
			size = "�ڱ��ڸ���һ�е�������";
		}
		if (size.equalsIgnoreCase("Handler_write")) {
			size = "�ڱ��ڲ���һ�е�������";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_pages_data")) {
			size = "�������ݵ�ҳ��(���ɾ�)";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_pages_dirty")) {
			size = "��ǰ����ҳ��";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_pages_flushed")) {
			size = "����յĻ����ҳ��";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_pages_free")) {
			size = "��ҳ��";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_pages_latched")) {
			size = "��InnoDB�������������ҳ��";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_pages_misc")) {
			size = "æ��ҳ��";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_pages_total")) {
			size = "������ܴ�С��ҳ����";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_read_ahead_rnd")) {
			size = "InnoDB��ʼ���ġ������read-aheads��";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_read_ahead_seq")) {
			size = "InnoDB��ʼ����˳��read-aheads��";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_read_requests")) {
			size = "InnoDB�Ѿ���ɵ��߼���������";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_reads")) {
			size = "��������InnoDB���뵥ҳ��ȡ�Ļ�����е��߼�������";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_wait_free")) {
			size = "û��������� ͨ����̨��InnoDB�����д";
		}
		if (size.equalsIgnoreCase("Innodb_buffer_pool_write_requests")) {
			size = "��InnoDB����ص�д����";
		}
		if (size.equalsIgnoreCase("Innodb_data_fsyncs")) {
			size = "fsync()������";
		}
		if (size.equalsIgnoreCase("Innodb_data_pending_fsyncs")) {
			size = "��ǰ�����fsync()������";
		}
		if (size.equalsIgnoreCase("Innodb_data_pending_reads")) {
			size = "��ǰ����Ķ���";
		}
		if (size.equalsIgnoreCase("Innodb_data_pending_writes")) {
			size = "��ǰ�����д��";
		}
		if (size.equalsIgnoreCase("Innodb_data_read")) {
			size = "�����Ѿ���ȡ�������������ֽڣ�";
		}
		if (size.equalsIgnoreCase("Innodb_data_reads")) {
			size = "���ݶ�������";
		}
		if (size.equalsIgnoreCase("IInnodb_data_writes")) {
			size = "����д������";
		}
		if (size.equalsIgnoreCase("Innodb_data_written")) {
			size = "�����Ѿ�д������������ֽڣ�";
		}
		if (size.equalsIgnoreCase("Innodb_dblwr_pages_written")) {
			size = "Ϊ�Ѿ�ִ�е�˫д��������д�õ�ҳ��";
		}
		if (size.equalsIgnoreCase("Innodb_dblwr_writes")) {
			size = "�Ѿ�ִ�е�˫д��������";
		}
		if (size.equalsIgnoreCase("Innodb_log_waits")) {
			size = "����ȴ���ʱ�䣬��Ϊ��־������̫С���ڼ���ǰ�����ȵȴ��������";
		}
		if (size.equalsIgnoreCase("Innodb_log_write_requests")) {
			size = "��־д������";
		}
		if (size.equalsIgnoreCase("Innodb_log_writes")) {
			size = "����־�ļ�������д����";
		}
		if (size.equalsIgnoreCase("Innodb_os_log_fsyncs")) {
			size = "����־�ļ���ɵ�fsync()д����";
		}
		if (size.equalsIgnoreCase("Innodb_os_log_pending_fsyncs")) {
			size = "�������־�ļ�fsync()��������";
		}
		if (size.equalsIgnoreCase("Innodb_os_log_pending_writes")) {
			size = "�������־�ļ�д����";
		}
		if (size.equalsIgnoreCase("Innodb_os_log_written")) {
			size = "д����־�ļ����ֽ���";
		}
		if (size.equalsIgnoreCase("Innodb_page_size")) {
			size = "�����InnoDBҳ��С(Ĭ��16KB) ���ֵ��ҳ������ ҳ�Ĵ�С������ת��Ϊ�ֽ�";
		}
		if (size.equalsIgnoreCase("Innodb_pages_created")) {
			size = "������ҳ��";
		}
		if (size.equalsIgnoreCase("Innodb_pages_read")) {
			size = "��ȡ��ҳ��";
		}
		if (size.equalsIgnoreCase("Innodb_pages_written")) {
			size = "д���ҳ��";
		}
		if (size.equalsIgnoreCase("Innodb_row_lock_current_waits")) {
			size = "��ǰ�ȴ��Ĵ�����������";
		}
		if (size.equalsIgnoreCase("Innodb_row_lock_time")) {
			size = "���������ѵ���ʱ�䣬��λ����";
		}
		if (size.equalsIgnoreCase("Innodb_row_lock_time_avg")) {
			size = "��������ƽ��ʱ�䣬��λ����";
		}
		if (size.equalsIgnoreCase("Innodb_row_lock_time_max")) {
			size = "���������ʱ�䣬��λ����";
		}
		if (size.equalsIgnoreCase("Innodb_row_lock_waits")) {
			size = "һ����������ȴ���ʱ����";
		}
		if (size.equalsIgnoreCase("Innodb_rows_deleted")) {
			size = "��InnoDB��ɾ��������";
		}
		if (size.equalsIgnoreCase("Innodb_rows_inserted")) {
			size = "���뵽InnoDB�������";
		}
		if (size.equalsIgnoreCase("Innodb_rows_read")) {
			size = "��InnoDB���ȡ������";
		}
		if (size.equalsIgnoreCase("Innodb_rows_updated")) {
			size = "InnoDB���ڸ��µ�����";
		}
		if (size.equalsIgnoreCase("Key_blocks_not_flushed")) {
			size = "���������Ѿ����ĵ���û����յ�Ӳ���ϵļ������ݿ�����";
		}
		if (size.equalsIgnoreCase("Key_blocks_unused")) {
			size = "��������δʹ�õĿ�����";
		}
		if (size.equalsIgnoreCase("Key_blocks_used")) {
			size = "��������ʹ�õĿ�����";
		}
		if (size.equalsIgnoreCase("Key_read_requests")) {
			size = "�ӻ�����������ݿ��������";
		}
		if (size.equalsIgnoreCase("Key_reads")) {
			size = "��Ӳ�̶�ȡ�������ݿ�Ĵ���";
		}
		if (size.equalsIgnoreCase("Key_write_requests")) {
			size = "���������ݿ�д�뻺���������";
		}
		if (size.equalsIgnoreCase("Key_writes")) {
			size = "��Ӳ��д�뽫�������ݿ������д�����Ĵ���";
		}
		if (size.equalsIgnoreCase("Last_query_cost")) {
			size = "�ò�ѯ�Ż��������������Ĳ�ѯ���ܳɱ�";
		}
		if (size.equalsIgnoreCase("Max_used_connections")) {
			size = "�������������Ѿ�ͬʱʹ�õ����ӵ��������";
		}
		if (size.equalsIgnoreCase("Not_flushed_delayed_rows")) {
			size = "�ȴ�д��INSERT DELAY���е�����";
		}
		if (size.equalsIgnoreCase("Open_files")) {
			size = "�򿪵��ļ�����Ŀ";
		}
		if (size.equalsIgnoreCase("Open_streams")) {
			size = "�򿪵���������(��Ҫ���ڼ�¼)";
		}
		if (size.equalsIgnoreCase("Open_tables")) {
			size = "��ǰ�򿪵ı������";
		}
		if (size.equalsIgnoreCase("Opened_tables")) {
			size = "�Ѿ��򿪵ı������";
		}
		if (size.equalsIgnoreCase("Qcache_free_blocks")) {
			size = "��ѯ�����������ڴ�������";
		}
		if (size.equalsIgnoreCase("Qcache_free_memory")) {
			size = "���ڲ�ѯ����������ڴ������";
		}
		if (size.equalsIgnoreCase("Qcache_hits")) {
			size = "��ѯ���汻���ʵĴ���";
		}
		if (size.equalsIgnoreCase("Qcache_inserts")) {
			size = "���뵽����Ĳ�ѯ����";
		}
		if (size.equalsIgnoreCase("Qcache_lowmem_prunes")) {
			size = "�����ڴ���ٴӻ���ɾ���Ĳ�ѯ����";
		}
		if (size.equalsIgnoreCase("Qcache_not_cached")) {
			size = "�ǻ����ѯ��(���ɻ��棬������query_cache_type�趨ֵδ����)";
		}
		if (size.equalsIgnoreCase("Qcache_queries_in_cache")) {
			size = "�Ǽǵ������ڵĲ�ѯ������";
		}
		if (size.equalsIgnoreCase("Qcache_total_blocks")) {
			size = "��ѯ�����ڵ��ܿ���";
		}
		if (size.equalsIgnoreCase("Questions")) {
			size = "�Ѿ����͸��������Ĳ�ѯ�ĸ���";
		}
		if (size.equalsIgnoreCase("Rpl_status")) {
			size = "ʧ�ܰ�ȫ����״̬(��δʹ��)";
		}
		if (size.equalsIgnoreCase("Select_full_join")) {
			size = "û��ʹ�����������ӵ������������ֵ��Ϊ0,��Ӧ��ϸ���������";
		}
		if (size.equalsIgnoreCase("Select_full_range_join")) {
			size = "�����õı���ʹ�÷�Χ���������ӵ�����";
		}
		if (size.equalsIgnoreCase("Select_range")) {
			size = "�ڵ�һ������ʹ�÷�Χ�����ӵ�����";
		}
		if (size.equalsIgnoreCase("Select_range_check")) {
			size = "��ÿһ�����ݺ�Լ�ֵ���м��Ĳ�����ֵ�����ӵ�����";
		}
		if (size.equalsIgnoreCase("Select_scan")) {
			size = "�Ե�һ���������ȫɨ������ӵ�����";
		}
		if (size.equalsIgnoreCase("Slave_open_temp_tables")) {
			size = "��ǰ�ɴ�SQL�̴߳򿪵���ʱ�������";
		}
		if (size.equalsIgnoreCase("Slave_retried_transactions")) {
			size = "�������ƴӷ�����SQL�̳߳���������ܴ���";
		}
		if (size.equalsIgnoreCase("Slave_running")) {
			size = "����÷����������ӵ����������Ĵӷ����������ֵΪON";
		}
		if (size.equalsIgnoreCase("Slow_launch_threads")) {
			size = "����ʱ�䳬��slow_launch_time����߳���";
		}
		if (size.equalsIgnoreCase("Slow_queries")) {
			size = "��ѯʱ�䳬��long_query_time��Ĳ�ѯ�ĸ���";
		}
		if (size.equalsIgnoreCase("Sort_merge_passes")) {
			size = "�����㷨�Ѿ�ִ�еĺϲ�������";
		}
		if (size.equalsIgnoreCase("Sort_range")) {
			size = "�ڷ�Χ��ִ�е����������";
		}
		if (size.equalsIgnoreCase("Sort_rows")) {
			size = "�Ѿ����������";
		}
		if (size.equalsIgnoreCase("Sort_scan")) {
			size = "ͨ��ɨ�����ɵ����������";
		}
		if (size.equalsIgnoreCase("Ssl_accept_renegotiates")) {
			size = "����SSL���ӵı���";
		}
		if (size.equalsIgnoreCase("Ssl_accept_renegotiates")) {
			size = "����SSL���ӵı���accept_renegotiates";
		}
		if (size.equalsIgnoreCase("Ssl_accepts")) {
			size = "����SSL���ӵı���accepts";
		}
		if (size.equalsIgnoreCase("Ssl_callback_cache_hits")) {
			size = "����SSL���ӵı���callback_cache_hits";
		}
		if (size.equalsIgnoreCase("Ssl_cipher")) {
			size = "����SSL���ӵı���cipher";
		}
		if (size.equalsIgnoreCase("Ssl_cipher_list")) {
			size = "����SSL���ӵı���cipher_list";
		}
		if (size.equalsIgnoreCase("Ssl_client_connects")) {
			size = "����SSL���ӵı���client_connects";
		}
		if (size.equalsIgnoreCase("Ssl_connect_renegotiates")) {
			size = "����SSL���ӵı���connect_renegotiates";
		}
		if (size.equalsIgnoreCase("Ssl_ctx_verify_depth")) {
			size = "����SSL���ӵı���ctx_verify_depth";
		}
		if (size.equalsIgnoreCase("Ssl_ctx_verify_mode")) {
			size = "����SSL���ӵı���ctx_verify_mode";
		}
		if (size.equalsIgnoreCase("Ssl_default_timeout")) {
			size = "����SSL���ӵı���default_timeout";
		}
		if (size.equalsIgnoreCase("Ssl_finished_accepts")) {
			size = "����SSL���ӵı���finished_accepts";
		}
		if (size.equalsIgnoreCase("Ssl_finished_connects")) {
			size = "����SSL���ӵı���finished_connects";
		}
		if (size.equalsIgnoreCase("Ssl_session_cache_hits")) {
			size = "����SSL���ӵı���session_cache_hits";
		}
		if (size.equalsIgnoreCase("Ssl_session_cache_misses")) {
			size = "����SSL���ӵı���session_cache_misses";
		}
		if (size.equalsIgnoreCase("Ssl_session_cache_mode")) {
			size = "����SSL���ӵı���session_cache_mode";
		}
		if (size.equalsIgnoreCase("Ssl_session_cache_overflows")) {
			size = "����SSL���ӵı���session_cache_overflows";
		}
		if (size.equalsIgnoreCase("Ssl_session_cache_size")) {
			size = "����SSL���ӵı���session_cache_size";
		}
		if (size.equalsIgnoreCase("Ssl_session_cache_timeouts")) {
			size = "����SSL���ӵı���session_cache_timeouts";
		}
		if (size.equalsIgnoreCase("Ssl_sessions_reused")) {
			size = "����SSL���ӵı���sessions_reused";
		}
		if (size.equalsIgnoreCase("Ssl_used_session_cache_entries")) {
			size = "����SSL���ӵı���used_session_cache_entries";
		}
		if (size.equalsIgnoreCase("Ssl_verify_depth")) {
			size = "����SSL���ӵı���verify_depth";
		}
		if (size.equalsIgnoreCase("Ssl_verify_mode")) {
			size = "����SSL���ӵı���verify_mode";
		}
		if (size.equalsIgnoreCase("Ssl_version")) {
			size = "����SSL���ӵı���version";
		}
		if (size.equalsIgnoreCase("Table_locks_immediate")) {
			size = "������õı�����Ĵ���";
		}
		if (size.equalsIgnoreCase("Table_locks_waited")) {
			size = "����������õı�����Ĵ���";
		}
		if (size.equalsIgnoreCase("Threads_cached")) {
			size = "�̻߳����ڵ��̵߳�����";
		}
		if (size.equalsIgnoreCase("Threads_connected")) {
			size = "��ǰ�򿪵����ӵ�����";
		}
		if (size.equalsIgnoreCase("Threads_created")) {
			size = "���������������ӵ��߳���";
		}
		if (size.equalsIgnoreCase("Threads_running")) {
			size = "����ģ���˯��״̬���߳���";
		}
		if (size.equalsIgnoreCase("Uptime")) {
			size = "�������Ѿ����е�ʱ�䣨����Ϊ��λ��";
		}
		return size;
	}

	private String getConfigForMysqlConfig(String size) {
		if (size.equalsIgnoreCase("auto_increment_increment")) {
			size = "�������е�ֵ������ֵ";
		}
		if (size.equalsIgnoreCase("auto_increment_offset")) {
			size = "ȷ��AUTO_INCREMENT��ֵ�����";
		}
		if (size.equalsIgnoreCase("automatic_sp_privileges")) {
			size = "automatic_sp_privileges";
		}
		if (size.equalsIgnoreCase("back_log")) {
			size = "�������������";
		}
		if (size.equalsIgnoreCase("basedir")) {
			size = "MySQL��װ��׼Ŀ¼";
		}
		if (size.equalsIgnoreCase("binlog_cache_size")) {
			size = "���ɶ�������־SQL���Ļ����С";
		}
		if (size.equalsIgnoreCase("bulk_insert_buffer_size")) {
			size = "ÿ�̵߳��ֽ������ƻ������Ĵ�С";
		}
		if (size.equalsIgnoreCase("character_set_client")) {
			size = "���Կͻ��˵������ַ���";
		}
		if (size.equalsIgnoreCase("character_set_connection")) {
			size = "û���ַ�����������ַ���ת��";
		}
		if (size.equalsIgnoreCase("character_set_database")) {
			size = "Ĭ�����ݿ�ʹ�õ��ַ���";
		}
		if (size.equalsIgnoreCase("character_set_filesystem")) {
			size = "character_set_filesystem";
		}
		if (size.equalsIgnoreCase("character_set_results")) {
			size = "������ͻ��˷��ز�ѯ������ַ���";
		}
		if (size.equalsIgnoreCase("character_set_server")) {
			size = "��������Ĭ���ַ���";
		}
		if (size.equalsIgnoreCase("character_set_system")) {
			size = "��������������ʶ������ַ���";
		}
		if (size.equalsIgnoreCase("character_sets_dir")) {
			size = "�ַ�����װĿ¼";
		}
		if (size.equalsIgnoreCase("collation_connection")) {
			size = "�����ַ�����У�Թ���";
		}
		if (size.equalsIgnoreCase("collation_database")) {
			size = "Ĭ�����ݿ�ʹ�õ�У�Թ���";
		}
		if (size.equalsIgnoreCase("collation_server")) {
			size = "��������Ĭ��У�Թ���";
		}
		if (size.equalsIgnoreCase("completion_type")) {
			size = "�����������";
		}
		if (size.equalsIgnoreCase("concurrent_insert")) {
			size = "�洢ֵ���";
		}
		if (size.equalsIgnoreCase("connect_timeout")) {
			size = "��������Bad handshake��Ӧǰ�ȴ����Ӱ�������";
		}
		if (size.equalsIgnoreCase("datadir")) {
			size = "MySQL����Ŀ¼";
		}
		if (size.equalsIgnoreCase("date_format")) {
			size = "date_format(Ϊ��ʹ��)";
		}
		if (size.equalsIgnoreCase("datetime_format")) {
			size = "datetime_format(Ϊ��ʹ��)";
		}
		if (size.equalsIgnoreCase("default_week_format")) {
			size = "WEEK() ����ʹ�õ�Ĭ��ģʽ";
		}
		if (size.equalsIgnoreCase("delay_key_write")) {
			size = "ʹ�õ�DELAY_KEY_WRITE��ѡ��Ĵ���";
		}
		if (size.equalsIgnoreCase("delayed_insert_limit")) {
			size = "INSERT DELAYED�������̼߳���Ƿ��й����SELECT���";
		}
		if (size.equalsIgnoreCase("delayed_insert_timeout")) {
			size = "INSERT DELAYED�������߳���ֹǰӦ�ȴ�INSERT����ʱ��";
		}
		if (size.equalsIgnoreCase("delayed_queue_size")) {
			size = "����INSERT DELAYED���ʱ�������е���������";
		}
		// if (size.equalsIgnoreCase("div_precision_increment")) {
		// size = "��/������ִ�г������Ľ�������ӵľ�ȷ�ȵ�λ��";
		// }
		if (size.equalsIgnoreCase("engine_condition_pushdown")) {
			size = "������NDB�ļ��";
		}
		if (size.equalsIgnoreCase("expire_logs_days")) {
			size = "��������־�Զ�ɾ��������";
		}
		if (size.equalsIgnoreCase("flush")) {
			size = "flushѡ������mysqldֵ";
		}
		if (size.equalsIgnoreCase("flush_time")) {
			size = "�鿴�ͷ���Դ���";
		}
		if (size.equalsIgnoreCase("ft_boolean_syntax")) {
			size = "ʹ��IN BOOLEAN MODEִ�еĲ���ȫ������֧�ֵĲ�����ϵ��";
		}
		if (size.equalsIgnoreCase("ft_max_word_len")) {
			size = "FULLTEXT���������������ֵ���󳤶�";
		}
		if (size.equalsIgnoreCase("ft_min_word_len")) {
			size = "FULLTEXT���������������ֵ���С����";
		}
		if (size.equalsIgnoreCase("ft_query_expansion_limit")) {
			size = "ʹ��WITH QUERY EXPANSION����ȫ�����������ƥ����";
		}
		if (size.equalsIgnoreCase("ft_stopword_file")) {
			size = "���ڶ�ȡȫ��������ֹͣ���嵥���ļ�";
		}
		if (size.equalsIgnoreCase("group_concat_max_len")) {
			size = "�����GROUP_CONCAT()�����������󳤶�";
		}
		if (size.equalsIgnoreCase("have_archive")) {
			size = "mysqld֧��ARCHIVE��֧�ֱ����";
		}
		if (size.equalsIgnoreCase("have_bdb")) {
			size = "mysqld֧��BDB�����";
		}
		if (size.equalsIgnoreCase("have_blackhole_engine")) {
			size = "mysqld֧��BLACKHOLE�����";
		}
		if (size.equalsIgnoreCase("have_compress")) {
			size = "�Ƿ�zlibѹ�����ʺϸ÷�����";
		}
		if (size.equalsIgnoreCase("have_crypt")) {
			size = "�Ƿ�crypt()ϵͳ�����ʺϸ÷�����";
		}
		if (size.equalsIgnoreCase("have_csv")) {
			size = "mysqld֧��ARCHIVE�����";
		}
		if (size.equalsIgnoreCase("have_example_engine")) {
			size = "mysqld֧��EXAMPLE�����";
		}
		if (size.equalsIgnoreCase("have_federated_engine")) {
			size = "mysqld֧��FEDERATED�����";
		}
		if (size.equalsIgnoreCase("have_geometry")) {
			size = "�Ƿ������֧�ֿռ���������";
		}
		if (size.equalsIgnoreCase("have_innodb")) {
			size = "mysqld֧��InnoDB�����";
		}
		if (size.equalsIgnoreCase("have_isam")) {
			size = "������";
		}
		if (size.equalsIgnoreCase("have_ndbcluster")) {
			size = "mysqld֧��NDB CLUSTER�����";
		}
		if (size.equalsIgnoreCase("have_openssl")) {
			size = "mysqld֧�ֿͻ���/������Э���SSL(����)���";
		}
		if (size.equalsIgnoreCase("have_query_cache")) {
			size = "mysqld֧�ֲ�ѯ�������";
		}
		if (size.equalsIgnoreCase("have_raid")) {
			size = "mysqld֧��RAIDѡ�����";
		}
		if (size.equalsIgnoreCase("have_rtree_keys")) {
			size = "RTREE�����Ƿ����";
		}
		if (size.equalsIgnoreCase("have_symlink")) {
			size = "�Ƿ����÷�������֧��";
		}
		if (size.equalsIgnoreCase("init_connect")) {
			size = "�ַ�������";
		}
		if (size.equalsIgnoreCase("init_file")) {
			size = "����������ʱ��--init-fileѡ��ָ�����ļ���";
		}
		if (size.equalsIgnoreCase("init_slave")) {
			size = "SQL�߳�����ʱ�ӷ�����Ӧִ�и��ַ���";
		}
		if (size.equalsIgnoreCase("innodb_additional_mem_pool_size")) {
			size = "InnoDB�����洢�����ڴ��С���";
		}
		if (size.equalsIgnoreCase("innodb_autoextend_increment")) {
			size = "��ռ䱻����֮ʱ��չ��ռ�ĳߴ�";
		}
		if (size.equalsIgnoreCase("innodb_buffer_pool_awe_mem_mb")) {
			size = "����ر�����32λWindows��AWE�ڴ��ﻺ��ش�С";
		}
		if (size.equalsIgnoreCase("innodb_buffer_pool_size")) {
			size = "InnoDB���������������ݺ��������ڴ滺�����Ĵ�С";
		}
		if (size.equalsIgnoreCase("innodb_checksums")) {
			size = "InnoDB�����жԴ��̵�ҳ���ȡ�ϵ�״̬";
		}
		if (size.equalsIgnoreCase("innodb_commit_concurrency")) {
			size = "innodb_commit_concurrency";
		}
		if (size.equalsIgnoreCase("innodb_concurrency_tickets")) {
			size = "innodb_concurrency_tickets";
		}
		if (size.equalsIgnoreCase("innodb_data_file_path")) {
			size = "���������ļ������ǳߴ��·��";
		}
		if (size.equalsIgnoreCase("innodb_data_home_dir")) {
			size = "Ŀ¼·��������InnoDB�����ļ��Ĺ�ͬ����";
		}
		if (size.equalsIgnoreCase("innodb_doublewrite")) {
			size = "InnoDB�洢�����������";
		}
		if (size.equalsIgnoreCase("innodb_fast_shutdown")) {
			size = "InnoDB�ڹر������ֵѡ��";
		}
		if (size.equalsIgnoreCase("innodb_file_io_threads")) {
			size = "InnoDB���ļ�I/O�̵߳���";
		}
		if (size.equalsIgnoreCase("innodb_file_per_table")) {
			size = "ȷ���Ƿ�InnoDB���Լ���.ibd�ļ�Ϊ�洢���ݺ���������ÿһ���±�";
		}
		if (size.equalsIgnoreCase("innodb_flush_log_at_trx_commit")) {
			size = "InnoDB����־�������";
		}
		if (size.equalsIgnoreCase("innodb_flush_method")) {
			size = "InnoDBʹ��fsync()��ˢ�����ݺ���־�ļ�";
		}
		if (size.equalsIgnoreCase("innodb_force_recovery")) {
			size = "�𻵵����ݿ�ת����ķ���";
		}
		if (size.equalsIgnoreCase("innodb_lock_wait_timeout")) {
			size = "InnoDB�����ڱ��ع�֮ǰ���Եȴ�һ�������ĳ�ʱ����";
		}
		if (size.equalsIgnoreCase("innodb_locks_unsafe_for_binlog")) {
			size = "InnoDB����������ɨ���йر���һ������";
		}
		if (size.equalsIgnoreCase("innodb_log_arch_dir")) {
			size = "ʹ����־���� ������д�����־�ļ����ڵ�Ŀ¼�Ĺ鵵ֵ";
		}
		if (size.equalsIgnoreCase("innodb_log_archive")) {
			size = "��־�������";
		}
		if (size.equalsIgnoreCase("innodb_log_buffer_size")) {
			size = "InnoDB�����������ϵ���־�ļ�д�����Ļ������Ĵ�С";
		}
		if (size.equalsIgnoreCase("innodb_log_file_size")) {
			size = "��־����ÿ����־�ļ��Ĵ�С";
		}
		if (size.equalsIgnoreCase("innodb_log_files_in_group")) {
			size = "��־������־�ļ�����Ŀ";
		}
		if (size.equalsIgnoreCase("innodb_log_group_home_dir")) {
			size = "InnoDB��־�ļ���Ŀ¼·��";
		}
		if (size.equalsIgnoreCase("innodb_max_dirty_pages_pct")) {
			size = "InnoDB�д�����ҳ�����";
		}
		if (size.equalsIgnoreCase("innodb_max_purge_lag")) {
			size = "�����������ͺ�֮ʱ������ӳ�INSERT,UPDATE��DELETE����";
		}
		if (size.equalsIgnoreCase("innodb_mirrored_log_groups")) {
			size = "Ϊ���ݿⱣ�ֵ���־����ͬ������������";
		}
		if (size.equalsIgnoreCase("innodb_open_files")) {
			size = "��InnoDBһ�ο��Ա��ִ򿪵�.ibd�ļ��������";
		}
		if (size.equalsIgnoreCase("innodb_support_xa")) {
			size = "InnoDB֧����XA�����е�˫���ύ���";
		}
		if (size.equalsIgnoreCase("innodb_sync_spin_loops")) {
			size = "innodb_sync_spin_loops";
		}
		if (size.equalsIgnoreCase("innodb_table_locks")) {
			size = "InnoDB�Ա���������";
		}
		if (size.equalsIgnoreCase("innodb_thread_concurrency")) {
			size = "InnoDB������InnoDB�ڱ��ֲ���ϵͳ�̵߳��������ڻ��������������������Ʒ�Χ";
		}
		if (size.equalsIgnoreCase("innodb_thread_sleep_delay")) {
			size = "��InnoDBΪ���ڵ�SHOW INNODB STATUS�������һ���ļ�<datadir>/innodb_status";
		}
		if (size.equalsIgnoreCase("interactive_timeout")) {
			size = "�������رս���ʽ����ǰ�ȴ��������";
		}
		if (size.equalsIgnoreCase("join_buffer_size")) {
			size = "������ȫ���ӵĻ������Ĵ�С";
		}
		if (size.equalsIgnoreCase("key_buffer_size")) {
			size = "�����黺�����Ĵ�С";
		}
		if (size.equalsIgnoreCase("key_cache_age_threshold")) {
			size = "���ƽ��������Ӽ�ֵ����������(sub-chain)������������(sub-chain)��ֵ";
		}
		if (size.equalsIgnoreCase("key_cache_block_size")) {
			size = "��ֵ�����ڿ���ֽڴ�С";
		}
		if (size.equalsIgnoreCase("key_cache_division_limit")) {
			size = "��ֵ���滺���������������������Ļ��ֵ�";
		}
		if (size.equalsIgnoreCase("language")) {
			size = "������Ϣ��������";
		}
		if (size.equalsIgnoreCase("large_files_support")) {
			size = "mysqld����ʱ�Ƿ�ʹ���˴��ļ�֧��ѡ��";
		}
		if (size.equalsIgnoreCase("large_page_size")) {
			size = "large_page_size";
		}
		if (size.equalsIgnoreCase("large_pages")) {
			size = "�Ƿ������˴�ҳ��֧��";
		}
		if (size.equalsIgnoreCase("license")) {
			size = "���������������";
		}
		if (size.equalsIgnoreCase("local_infile")) {
			size = "�Ƿ�LOCAL֧��LOAD DATA INFILE���";
		}
		if (size.equalsIgnoreCase("log")) {
			size = "�Ƿ����ý����в�ѯ��¼�������ѯ��־��";
		}
		if (size.equalsIgnoreCase("log_bin")) {
			size = "�Ƿ����ö�������־";
		}
		if (size.equalsIgnoreCase("log_bin_trust_function_creators")) {
			size = "�Ƿ�������α���ĳ�������߲��ᴴ�����������־д�벻��ȫ�¼��ĳ���";
		}
		if (size.equalsIgnoreCase("log_error")) {
			size = "������־��λ��";
		}
		if (size.equalsIgnoreCase("log_slave_updates")) {
			size = "�Ƿ�ӷ����������������յ��ĸ���Ӧ����ӷ������Լ��Ķ�������־";
		}
		if (size.equalsIgnoreCase("log_slow_queries")) {
			size = "�Ƿ��¼����ѯ";
		}
		if (size.equalsIgnoreCase("log_warnings")) {
			size = "�Ƿ��������������Ϣ";
		}
		if (size.equalsIgnoreCase("long_query_time")) {
			size = "��ѯʱ�䳬����ֵ��������Slow_queries״̬����";
		}
		if (size.equalsIgnoreCase("low_priority_updates")) {
			size = "��ʾsql���ȴ���佫�ȴ�ֱ����Ӱ��ı�û�й����SELECT��LOCK TABLE READ";
		}
		if (size.equalsIgnoreCase("lower_case_file_system")) {
			size = "˵���Ƿ�����Ŀ¼���ڵ��ļ�ϵͳ���ļ����Ĵ�Сд����";
		}
		if (size.equalsIgnoreCase("lower_case_table_names")) {
			size = "Ϊ1��ʾ������Сд���浽Ӳ���ϣ����ұ����Ƚ�ʱ���Դ�Сд����";
		}
		if (size.equalsIgnoreCase("max_allowed_packet")) {
			size = "�����κ����ɵ�/�м��ַ���������С";
		}
		if (size.equalsIgnoreCase("max_binlog_cache_size")) {
			size = "�����������Ҫ������ڴ�ʱ���ֵ����";
		}
		if (size.equalsIgnoreCase("max_binlog_size")) {
			size = "�����������Ҫ������ڴ�ʱ���ֵ����";
		}
		if (size.equalsIgnoreCase("max_connect_errors")) {
			size = "�ϵ������������ӵ����������";
		}
		if (size.equalsIgnoreCase("max_connections")) {
			size = "����Ĳ��пͻ���������Ŀ";
		}
		if (size.equalsIgnoreCase("max_delayed_threads")) {
			size = "�����߳�������INSERT DELAYED����������";
		}
		if (size.equalsIgnoreCase("max_error_count")) {
			size = "����SHOW ERRORS��SHOW WARNINGS��ʾ�Ĵ��󡢾����ע��������Ŀ";
		}
		if (size.equalsIgnoreCase("max_heap_table_size")) {
			size = "����MEMORY (HEAP)����������������ռ��С";
		}
		if (size.equalsIgnoreCase("max_insert_delayed_threads")) {
			size = "�����߳�������INSERT DELAYED����������(ͬmax_delayed_threads)";
		}
		if (size.equalsIgnoreCase("max_join_size")) {
			size = "�����������Ҫ������max_join_size�е����";
		}
		if (size.equalsIgnoreCase("max_length_for_sort_data")) {
			size = "ȷ��ʹ�õ�filesort�㷨������ֵ��С����ֵ";
		}
		if (size.equalsIgnoreCase("max_prepared_stmt_count")) {
			size = "max_prepared_stmt_count";
		}
		if (size.equalsIgnoreCase("max_relay_log_size")) {
			size = "������ƴӷ�����д���м���־ʱ��������ֵ��������м���";
		}
		if (size.equalsIgnoreCase("max_seeks_for_key")) {
			size = "���Ƹ��ݼ�ֵѰ����ʱ�����������";
		}
		if (size.equalsIgnoreCase("max_sort_length")) {
			size = "����BLOB��TEXTֵʱʹ�õ��ֽ���";
		}
		if (size.equalsIgnoreCase("max_sp_recursion_depth")) {
			size = "max_sp_recursion_depth";
		}
		if (size.equalsIgnoreCase("max_tmp_tables")) {
			size = "�ͻ��˿���ͬʱ�򿪵���ʱ��������";
		}
		if (size.equalsIgnoreCase("max_user_connections")) {
			size = "������MySQL�˻���������ͬʱ������";
		}
		if (size.equalsIgnoreCase("max_write_lock_count")) {
			size = "����д�������ƺ������ֶ�����";
		}
		if (size.equalsIgnoreCase("multi_range_count")) {
			size = "multi_range_count";
		}
		if (size.equalsIgnoreCase("myisam_data_pointer_size")) {
			size = "Ĭ��ָ���С��ֵ";
		}
		if (size.equalsIgnoreCase("myisam_max_sort_file_size")) {
			size = "�ؽ�MyISAM����ʱ������MySQLʹ�õ���ʱ�ļ������ռ��С";
		}
		if (size.equalsIgnoreCase("myisam_recover_options")) {
			size = "myisam-recoverѡ���ֵ";
		}
		if (size.equalsIgnoreCase("myisam_repair_threads")) {
			size = "�����ֵ����1����Repair by sorting�����в��д���MyISAM������";
		}
		if (size.equalsIgnoreCase("myisam_sort_buffer_size")) {
			size = "��REPAIR TABLE����CREATE INDEX����������ALTER TABLE����������MyISAM��������Ļ�����";
		}
		if (size.equalsIgnoreCase("myisam_stats_method")) {
			size = "MyISAM���Ѽ���������ֵ�ַ���ͳ����Ϣʱ��������δ���NULLֵ";
		}
		if (size.equalsIgnoreCase("named_pipe")) {
			size = "���������Ƿ�֧�������ܵ�����";
		}
		if (size.equalsIgnoreCase("net_buffer_length")) {
			size = "�ڲ�ѯ֮�佫ͨ�Ż���������Ϊ��ֵ";
		}
		if (size.equalsIgnoreCase("net_read_timeout")) {
			size = "�ж϶�ǰ�ȴ����ӵ��������ݵ�����";
		}
		if (size.equalsIgnoreCase("net_retry_count")) {
			size = "��ʾĳ��ͨ�Ŷ˿ڵĶ������ж��ˣ��ڷ���ǰ���Զ��";
		}
		if (size.equalsIgnoreCase("net_write_timeout")) {
			size = "�ж�д֮ǰ�ȴ���д�����ӵ�����";
		}
		if (size.equalsIgnoreCase("new")) {
			size = "��ʾ��MySQL 4.0��ʹ�øñ�������4.1�е�һЩ��Ϊ����������������";
		}
		if (size.equalsIgnoreCase("old_passwords")) {
			size = "�Ƿ������ӦΪMySQL�û��˻�ʹ��pre-4.1-style������";
		}
		if (size.equalsIgnoreCase("open_files_limit")) {
			size = "����ϵͳ����mysqld�򿪵��ļ�������";
		}
		if (size.equalsIgnoreCase("optimizer_prune_level")) {
			size = "�ڲ�ѯ�Ż����Ż��������ռ�ü���ϣ���ֲ��ƻ���ʹ�õĿ��Ʒ��� 0��ʾ���÷���";
		}
		if (size.equalsIgnoreCase("optimizer_search_depth")) {
			size = "��ѯ�Ż������е�������������";
		}
		if (size.equalsIgnoreCase("pid_file")) {
			size = "����ID (PID)�ļ���·����";
		}
		if (size.equalsIgnoreCase("prepared_stmt_count")) {
			size = "prepared_stmt_count";
		}
		if (size.equalsIgnoreCase("port")) {
			size = "������֡��TCP/IP�������ö˿�";
		}
		if (size.equalsIgnoreCase("preload_buffer_size")) {
			size = "��������ʱ����Ļ�������С";
		}
		if (size.equalsIgnoreCase("protocol_version")) {
			size = "MySQL������ʹ�õĿͻ���/������Э��İ汾";
		}
		if (size.equalsIgnoreCase("query_alloc_block_size")) {
			size = "Ϊ��ѯ������ִ�й����д����Ķ��������ڴ���С";
		}
		if (size.equalsIgnoreCase("query_cache_limit")) {
			size = "��Ҫ������ڸ�ֵ�Ľ��";
		}
		if (size.equalsIgnoreCase("query_cache_min_res_unit")) {
			size = "��ѯ����������С��Ĵ�С(�ֽ�)";
		}
		if (size.equalsIgnoreCase("query_cache_size")) {
			size = "Ϊ�����ѯ���������ڴ������";
		}
		if (size.equalsIgnoreCase("query_cache_type")) {
			size = "���ò�ѯ��������";
		}
		if (size.equalsIgnoreCase("query_cache_wlock_invalidate")) {
			size = "�Ա����WRITE����������ֵ";
		}
		if (size.equalsIgnoreCase("query_prealloc_size")) {
			size = "���ڲ�ѯ������ִ�еĹ̶��������Ĵ�С";
		}
		if (size.equalsIgnoreCase("range_alloc_block_size")) {
			size = "��Χ�Ż�ʱ����Ŀ�Ĵ�С";
		}
		if (size.equalsIgnoreCase("read_buffer_size")) {
			size = "ÿ���߳�����ɨ��ʱΪɨ���ÿ�������Ļ������Ĵ�С(�ֽ�)";
		}
		if (size.equalsIgnoreCase("read_only")) {
			size = "�����Ը��ƴӷ���������ΪONʱ���������Ƿ��������";
		}
		if (size.equalsIgnoreCase("read_only")) {
			size = "�����Ը��ƴӷ���������ΪONʱ���ӷ��������������";
		}
		if (size.equalsIgnoreCase("relay_log_purge")) {
			size = "��������Ҫ�м���־ʱ���û������Զ�����м���־";
		}
		if (size.equalsIgnoreCase("read_rnd_buffer_size")) {
			size = "�������������˳���ȡ��ʱ����ͨ���û�������ȡ�У���������Ӳ��";
		}
		if (size.equalsIgnoreCase("secure_auth")) {
			size = "�����--secure-authѡ��������MySQL���������Ƿ������оɸ�ʽ(4.1֮ǰ)����������˻������������";
		}
		if (size.equalsIgnoreCase("shared_memory")) {
			size = "(ֻ����Windows)�������Ƿ��������ڴ�����";
		}
		if (size.equalsIgnoreCase("shared_memory_base_name")) {
			size = "(ֻ����Windows)˵���������Ƿ��������ڴ����ӣ���Ϊ�����ڴ�����ʶ���";
		}
		if (size.equalsIgnoreCase("server_id")) {
			size = "���������Ʒ������ʹӸ��Ʒ�����";
		}
		if (size.equalsIgnoreCase("skip_external_locking")) {
			size = "mysqld�Ƿ�ʹ���ⲿ����";
		}
		if (size.equalsIgnoreCase("skip_networking")) {
			size = "���������ֻ������(��TCP/IP)����";
		}
		if (size.equalsIgnoreCase("skip_show_database")) {
			size = "��ֹ������SHOW DATABASESȨ�޵�����ʹ��SHOW DATABASES���";
		}
		if (size.equalsIgnoreCase("slave_compressed_protocol")) {
			size = "��������ӷ�������֧�֣�ȷ���Ƿ�ʹ�ô�/��ѹ��Э��";
		}
		if (size.equalsIgnoreCase("slave_load_tmpdir")) {
			size = "�ӷ�����Ϊ����LOAD DATA INFILE��䴴����ʱ�ļ���Ŀ¼��";
		}
		if (size.equalsIgnoreCase("slave_net_timeout")) {
			size = "����������ǰ�ȴ���/�����ӵĸ������ݵĵȴ�����";
		}
		if (size.equalsIgnoreCase("slave_skip_errors")) {
			size = "�ӷ�����Ӧ����(����)�ĸ��ƴ���";
		}
		if (size.equalsIgnoreCase("slave_transaction_retries")) {
			size = "���ƴӷ�����SQL�߳�δ��ִ����������ʾ����ֹͣǰ���Զ��ظ�slave_transaction_retries��";
		}
		if (size.equalsIgnoreCase("slow_launch_time")) {
			size = "��������̵߳�ʱ�䳬��������������������Slow_launch_threads״̬����";
		}
		if (size.equalsIgnoreCase("sort_buffer_size")) {
			size = "ÿ�������̷߳���Ļ������Ĵ�С";
		}
		if (size.equalsIgnoreCase("sql_mode")) {
			size = "��ǰ�ķ�����SQLģʽ�����Զ�̬����";
		}
		if (size.equalsIgnoreCase("storage_engine")) {
			size = "�ñ�����table_typeis��ͬ��ʡ���MySQL 5.1��,��ѡstorage_engine";
		}
		if (size.equalsIgnoreCase("sync_binlog")) {
			size = "���Ϊ������ÿ��sync_binlog'thд��ö�������־��MySQL�����������Ķ�������־ͬ����Ӳ����";
		}
		if (size.equalsIgnoreCase("sync_frm")) {
			size = "����ñ�����Ϊ1,����������ʱ��ʱ����.frm�ļ��Ƿ�ͬ����Ӳ����";
		}
		if (size.equalsIgnoreCase("system_time_zone")) {
			size = "������ϵͳʱ��";
		}
		if (size.equalsIgnoreCase("table_cache")) {
			size = "�����̴߳򿪵ı����Ŀ";
		}
		if (size.equalsIgnoreCase("table_type")) {
			size = "Ĭ�ϱ�����(�洢����)";
		}
		if (size.equalsIgnoreCase("thread_cache_size")) {
			size = "������Ӧ��������߳��Ա�����ʹ��";
		}
		if (size.equalsIgnoreCase("thread_stack")) {
			size = "ÿ���̵߳Ķ�ջ��С";
		}
		if (size.equalsIgnoreCase("time_format")) {
			size = "�ñ���Ϊʹ��";
		}
		if (size.equalsIgnoreCase("time_zone")) {
			size = "��ǰ��ʱ��";
		}
		if (size.equalsIgnoreCase("tmp_table_size")) {
			size = "����ڴ��ڵ���ʱ������ֵ��MySQL�Զ�����ת��ΪӲ���ϵ�MyISAM��";
		}
		if (size.equalsIgnoreCase("tmpdir")) {
			size = "������ʱ�ļ�����ʱ���Ŀ¼";
		}
		if (size.equalsIgnoreCase("transaction_alloc_block_size")) {
			size = "Ϊ���潫���浽��������־�е�����Ĳ�ѯ��������ڴ��Ĵ�С(�ֽ�)";
		}
		if (size.equalsIgnoreCase("transaction_prealloc_size")) {
			size = "transaction_alloc_blocks����Ĺ̶��������Ĵ�С���ֽڣ��������β�ѯ֮�䲻���ͷ�";
		}
		if (size.equalsIgnoreCase("tx_isolation")) {
			size = "Ĭ��������뼶��";
		}
		if (size.equalsIgnoreCase("updatable_views_with_limit")) {
			size = "�ñ�������������°���LIMIT�Ӿ䣬�Ƿ�����ڵ�ǰ����ʹ�ò��������ؼ��ֵ���ͼ���и���";
		}
		if (size.equalsIgnoreCase("version")) {
			size = "�������汾��";
		}
		if (size.equalsIgnoreCase("version_bdb")) {
			size = "BDB�洢����汾";
		}
		if (size.equalsIgnoreCase("version_comment")) {
			size = "configure�ű���һ��--with-commentѡ�������MySQLʱ���Խ���ע��";
		}
		if (size.equalsIgnoreCase("version_compile_machine")) {
			size = "MySQL�����Ļ�����ܹ�������";
		}
		if (size.equalsIgnoreCase("version_compile_os")) {
			size = "MySQL�����Ĳ���ϵͳ������";
		}
		if (size.equalsIgnoreCase("wait_timeout")) {
			size = "�������رշǽ�������֮ǰ�ȴ��������";
		}
		if (size == null || size.equals("")) {
			size = "";
		}
		return size;
	}

	private String generateColor() {
		String r, g, b;
		Random random = new Random();
		r = Integer.toHexString(random.nextInt(256)).toUpperCase();
		g = Integer.toHexString(random.nextInt(256)).toUpperCase();
		b = Integer.toHexString(random.nextInt(256)).toUpperCase();

		r = r.length() == 1 ? "0" + r : r;
		g = g.length() == 1 ? "0" + g : g;
		b = b.length() == 1 ? "0" + b : b;

		return (r + g + b);
	}

}
