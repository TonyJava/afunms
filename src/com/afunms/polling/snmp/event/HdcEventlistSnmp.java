package com.afunms.polling.snmp.event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.HdcMessage;
import com.gatherdb.GathersqlListManager;

@SuppressWarnings("unchecked")
public class HdcEventlistSnmp extends SnmpMonitor {

	public void CreateResultTosql(Hashtable dataresult, Host node) {
		// 处理hdc—sys-info
		if (dataresult != null && dataresult.size() > 0) {
			Vector sysInfoVector = null;
			HdcMessage hdcVo = null;
			String hendsql = "insert into hdc_eventlist (eventListIndexSerialNumber,eventListNickname,eventListIndexRecordNo,eventListREFCODE,eventListDate,eventListTime,eventListDescription,nodeid) values(";
			String endsql = "')";
			String deleteSql = "delete from hdc_eventlist where nodeid='" + node.getId() + "'";
			sysInfoVector = (Vector) dataresult.get("eventlist");
			Vector list = new Vector();
			if (sysInfoVector != null && sysInfoVector.size() > 0) {
				for (int i = 0; i < sysInfoVector.size(); i++) {
					hdcVo = (HdcMessage) sysInfoVector.elementAt(i);
					StringBuffer sbuffer = new StringBuffer(150);
					sbuffer.append(hendsql);
					sbuffer.append("'").append(hdcVo.getEventListIndexSerialNumber()).append("',");
					sbuffer.append("'").append(hdcVo.getEventListNickname()).append("',");
					sbuffer.append("'").append(hdcVo.getEventListIndexRecordNo()).append("',");
					sbuffer.append("'").append(hdcVo.getEventListREFCODE()).append("',");
					sbuffer.append("'").append(hdcVo.getEventListDate()).append("',");
					sbuffer.append("'").append(hdcVo.getEventListTime()).append("',");
					sbuffer.append("'").append(hdcVo.getEventListDescription()).append("',");
					sbuffer.append("'").append(node.getId());
					sbuffer.append(endsql);
					list.add(sbuffer.toString());
					sbuffer = null;
				}
				GathersqlListManager.AdddateTempsql(deleteSql, list);
				list = null;
			}
		}
	}

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector eventlist = new Vector();
		HdcMessage hdcMessage;
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null)
			return null;
		if (node.getIpAddress().equals(""))
			return null;
		try {
			String[][] valueArray = null;
			String[] oids = new String[] { ".1.3.6.1.4.1.116.5.11.4.1.1.8.1.1",// eventListIndexSerialNumber
					// 索引号
					".1.3.6.1.4.1.116.5.11.4.1.1.8.1.2",// eventListNickname
					// 缺陷名称
					".1.3.6.1.4.1.116.5.11.4.1.1.8.1.3",// eventListIndexRecordNo
					// 事件记录号
					".1.3.6.1.4.1.116.5.11.4.1.1.8.1.4",// eventListREFCODE
					// 记录编码
					".1.3.6.1.4.1.116.5.11.4.1.1.8.1.5",// eventListDate
					// 日期
					".1.3.6.1.4.1.116.5.11.4.1.1.8.1.6",// eventListTime
					// 时间
					".1.3.6.1.4.1.116.5.11.4.1.1.8.1.7",// eventListDescription
			// 事件描述
			};
			valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), node.getSecuritylevel(), node.getSecurityName(), node
					.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node.getPrivacyPassphrase(), 3, 1000 * 30);
			if (valueArray != null) {
				for (int i = 0; i < valueArray.length; i++) {
					String eventListIndexSerialNumber = valueArray[i][0];
					String eventListNickname = valueArray[i][1];
					String eventListIndexRecordNo = valueArray[i][2];
					String eventListREFCODE = valueArray[i][3];
					String eventListDate = valueArray[i][4];
					String eventListTime = valueArray[i][5];
					String eventListDescription = valueArray[i][6];
					hdcMessage = new HdcMessage();
					hdcMessage.setEventListDate(eventListDate);
					hdcMessage.setEventListDescription(eventListDescription);
					hdcMessage.setEventListIndexRecordNo(eventListIndexRecordNo);
					hdcMessage.setEventListIndexSerialNumber(eventListIndexSerialNumber);
					hdcMessage.setEventListNickname(eventListNickname);
					hdcMessage.setEventListREFCODE(eventListREFCODE);
					hdcMessage.setEventListTime(eventListTime);
					eventlist.addElement(hdcMessage);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (ipAllData == null)
				ipAllData = new Hashtable();
			if (eventlist != null && eventlist.size() > 0)
				ipAllData.put("eventlist", eventlist);
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (eventlist != null && eventlist.size() > 0)
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("eventlist", eventlist);
		}
		returnHash.put("eventlist", eventlist);
		// 把采集结果生成sql
		this.CreateResultTosql(returnHash, node);
		return returnHash;
	}

	public int computeDateTime(String str_1, String str_2) {
		int resultTime = 0;
		try {
			if (str_1 != null && str_2 != null) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				long result = ((format.parse(str_2.replaceAll("\n", "")).getTime()) - (format2.parse(str_1.replaceAll("\n", "")).getTime())) / 60000;
				resultTime = new Long(result).intValue();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return resultTime;
	}
}