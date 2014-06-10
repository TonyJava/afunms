package com.afunms.polling.snmp.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.Db2spaceconfigDao;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.Db2spaceconfig;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.ShareData;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;

@SuppressWarnings("unchecked")
public class DB2DataCollector {

	public static void createDb2SpaceSMS(DBVo dbmonitorlist, List retList, String dbStr) {
		try {
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(dbmonitorlist);
			// 判断是否存在此告警指标
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(nodeDTO.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype());
			CheckEventUtil checkEventUtil = new CheckEventUtil();
			for (int i = 0; i < list.size(); i++) {
				AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(i);
				if ("tablespace".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
					if (retList != null && retList.size() > 0) {
						for (int j = 0; j < retList.size(); j++) {
							Hashtable sys_hash = (Hashtable) retList.get(j);
							if (sys_hash != null && sys_hash.size() > 0) {
								// 判断告警
								String tablespace = sys_hash.get("tablespace_name").toString();
								Db2spaceconfigDao db2spaceconfigManager = new Db2spaceconfigDao();
								Hashtable db2alarm = null;
								try {
									db2alarm = db2spaceconfigManager.getByAlarmflag(1);
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									db2spaceconfigManager.close();
								}
								if (db2alarm != null && db2alarm.size() > 0) {
									if (db2alarm.containsKey(dbmonitorlist.getIpAddress() + ":" + dbStr + ":" + sys_hash.get("tablespace_name").toString())) {
										// 判断值是否越界
										Db2spaceconfig db2spaceconfig = (Db2spaceconfig) db2alarm.get(dbmonitorlist.getIpAddress() + ":" + dbStr + ":"
												+ sys_hash.get("tablespace_name").toString());
										String usableper = (String) sys_hash.get("usableper");
										if (usableper.trim().length() == 0) {
											usableper = "0";
										}
										float usablefloatper = new Float(usableper);
										alarmIndicatorsNode.setLimenvalue0(db2spaceconfig.getAlarmvalue() + "");
										alarmIndicatorsNode.setLimenvalue1(db2spaceconfig.getAlarmvalue() + "");
										alarmIndicatorsNode.setLimenvalue2(db2spaceconfig.getAlarmvalue() + "");
										checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, (100 - new Float(usablefloatper).intValue()) + "", tablespace);
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicator) {
		DBDao dbdao = null;
		Hashtable returndata = new Hashtable();
		List dbmonitorlists = new ArrayList();
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
		List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
		dbmonitorlists = ShareData.getDBList();
		try {
			DBVo dbmonitorlist = new DBVo();
			if (dbmonitorlists != null && dbmonitorlists.size() > 0) {
				for (int i = 0; i < dbmonitorlists.size(); i++) {
					DBVo vo = (DBVo) dbmonitorlists.get(i);
					if (vo.getId() == Integer.parseInt(nodeGatherIndicator.getNodeid())) {
						dbmonitorlist = vo;
						break;
					}
				}
			}
			// 未管理
			if (dbmonitorlist.getManaged() == 0) {
				return returndata;
			}
			try {
				// 获取被启用的DB2所有被监视指标
				monitorItemList = indicatorsdao.getByInterval("5", "m", 1, "db", "db2");
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
				NodeGatherIndicators nodeGatherIndicators = monitorItemList.get(i);
				if (nodeGatherIndicators.getNodeid().equals(nodeGatherIndicator.getNodeid())) {
					gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
				}
			}
			String serverip = dbmonitorlist.getIpAddress();
			String username = dbmonitorlist.getUser();
			String passwords = EncryptUtil.decode(dbmonitorlist.getPassword());
			int port = Integer.parseInt(dbmonitorlist.getPort());
			String dbnames = dbmonitorlist.getDbName();
			// JDBC采集方式
			Hashtable allDb2Data = new Hashtable();
			// 对DB2数据进行采集
			dbdao = new DBDao();
			try {
				allDb2Data = dbdao.getDB2Data(serverip, port, dbnames, username, passwords, gatherHash);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
			Hashtable spaceHash = new Hashtable();
			if (allDb2Data != null && allDb2Data.containsKey("spaceInfo")) {
				spaceHash = (Hashtable) allDb2Data.get("spaceInfo");
			}
			String hex = IpTranslation.formIpToHex(dbmonitorlist.getIpAddress());
			String sip = hex + ":" + dbmonitorlist.getId();
			// 更新状态信息
			String[] alldbs = dbnames.split(",");
			Hashtable alltype6spaceHash = new Hashtable();
			Hashtable type6spaceHash = new Hashtable();
			try {
				for (int k = 0; k < alldbs.length; k++) {
					String dbStr = alldbs[k];
					List type6space = new ArrayList();
					if (spaceHash.containsKey(dbStr)) {
						List retList = (List) spaceHash.get(dbStr);
						if (retList != null && retList.size() > 0) {
							for (int j = 0; j < retList.size(); j++) {
								Hashtable sys_hash = (Hashtable) retList.get(j);
								if (sys_hash != null && sys_hash.size() > 0) {
									type6space.add(sys_hash);
								}
							}
						}
						createDb2SpaceSMS(dbmonitorlist, retList, dbStr);
					}
					if (type6space != null && type6space.size() > 0) {
						// 将type为6的表空间加进容器
						type6spaceHash.put(dbStr, type6space);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (type6spaceHash != null && type6spaceHash.size() > 0) {
					alltype6spaceHash.put(serverip, type6spaceHash);
					ShareData.setDb2type6spacedata(serverip, alltype6spaceHash);
				}
				ShareData.setAlldb2data(serverip, allDb2Data);
				Hashtable monitorDB2Data = new Hashtable();
				monitorDB2Data.put("allDb2Data", allDb2Data);
				monitorDB2Data.put("alltype6spaceHash", alltype6spaceHash);
				monitorDB2Data.put("ip", serverip);
				// 删除之前采集的DB2数据信息
				String[] tableNames = { "nms_db2tablespace", "nms_db2common", "nms_db2conn", "nms_db2sysinfo", "nms_db2spaceinfo", "nms_db2log", "nms_db2write", "nms_db2pool",
						"nms_db2lock", "nms_db2read", "nms_db2session", "nms_db2cach" };
				dbdao.clearTablesData(tableNames, sip);
				// 保存采集的DB2数据信息
				dbdao.addDB2_nmsinfo(sip, monitorDB2Data, alldbs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dbdao != null) {
				dbdao.close();
			}
		}
		return returndata;
	}
}
