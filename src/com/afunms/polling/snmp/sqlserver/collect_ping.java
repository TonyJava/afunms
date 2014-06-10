package com.afunms.polling.snmp.sqlserver;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.DBDao;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.ShareData;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.DBNode;
import com.afunms.polling.om.PingCollectEntity;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class collect_ping {
	Calendar date = Calendar.getInstance();
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable resultHt = new Hashtable();
		DBDao dbDao = null;
		int id = Integer.parseInt(nodeGatherIndicators.getNodeid());
		DBNode dbNode = (DBNode) PollingEngine.getInstance().getDbByID(id);
		if (dbNode != null) {
			boolean sqlserverIsOK = false;
			try {
				dbDao = new DBDao();
				sqlserverIsOK = dbDao.getSqlserverIsOk(dbNode.getIpAddress(), dbNode.getUser(), EncryptUtil.decode(dbNode.getPassword()));
			} catch (Exception e) {
				sqlserverIsOK = false;
			} finally {
				dbDao.close();
			}

			String status = "0";
			String thePingValue = "0";
			if (sqlserverIsOK) {
				resultHt.put("ping", "100");
				status = "1";
				thePingValue = "100";
			} else {
				resultHt.put("ping", "0");
				status = "0";
				thePingValue = "0";
			}
			String hex = IpTranslation.formIpToHex(dbNode.getIpAddress());
			dbDao = new DBDao();
			try {
				dbDao.clearTableData("nms_sqlserverstatus", hex + ":" + dbNode.getAlias());
				dbDao.addSqlserver_nmsstatus(hex + ":" + dbNode.getAlias(), status);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbDao.close();
			}
			// 更新内存
			if (!(ShareData.getSharedata().containsKey(hex + ":" + dbNode.getAlias()))) {
				ShareData.getSharedata().put(hex + ":" + dbNode.getAlias(), resultHt);
			} else {
				Hashtable sqlserverHash = (Hashtable) ShareData.getSharedata().get(hex + ":" + dbNode.getAlias());
				sqlserverHash.put("ping", (String) resultHt.get("ping"));
			}

			// 入库
			try {
				dbDao = new DBDao();
				PingCollectEntity hostdata = null;
				hostdata = new PingCollectEntity();
				hostdata.setIpaddress(dbNode.getIpAddress());
				hostdata.setCollecttime(date);
				hostdata.setCategory("SQLPing");
				hostdata.setEntity("Utilization");
				hostdata.setSubentity("ConnectUtilization");
				hostdata.setRestype("dynamic");
				hostdata.setUnit("%");
				hostdata.setThevalue(thePingValue);
				try {
					dbDao.createHostData(hostdata);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dbDao.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 判断告警
			try {
				if (thePingValue != null) {
					NodeUtil nodeUtil = new NodeUtil();
					NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(dbNode);
					// 判断是否存在此告警指标
					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
					List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(nodeDTO.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype());
					CheckEventUtil checkEventUtil = new CheckEventUtil();
					for (int i = 0; i < list.size(); i++) {
						AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(i);
						if ("ping".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
							if (thePingValue != null) {
								checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, thePingValue);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return resultHt;
	}

}
