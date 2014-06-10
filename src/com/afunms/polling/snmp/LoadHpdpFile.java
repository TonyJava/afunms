package com.afunms.polling.snmp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.PingUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysUtil;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Dp;
import com.afunms.polling.om.Hpdpcollectdata;
import com.afunms.polling.om.PingCollectEntity;
import com.gatherResulttosql.HostnetPingResultTosql;

@SuppressWarnings("unchecked")
public class LoadHpdpFile {
	private String ipaddress;

	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public LoadHpdpFile() {

	}

	public LoadHpdpFile(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public boolean addHpdp(Hashtable returnhash) throws Exception {

		String ipaddress = "";
		ipaddress = (String) returnhash.get("ipaddress");

		if (returnhash.containsKey("hpdp")) {
			DBManager dbmanager = new DBManager();
			Vector hpdpVector = null;
			int flag;
			hpdpVector = (Vector) returnhash.get("hpdp");
			flag = (Integer) returnhash.get("flag");
			Hpdpcollectdata vo = null;
			if (hpdpVector != null && hpdpVector.size() > 0) {
				try {
					if (flag == 1) {
						String sql1 = "delete from nms_hpdp where ipaddress='" + ipaddress + "'";
						dbmanager.addBatch(sql1);
					}

					for (int i = 0; i < hpdpVector.size(); i++) {
						vo = (Hpdpcollectdata) hpdpVector.get(i);
						String sql = "";
						try {
							sql = "insert into nms_hpdp(ipaddress,sessionId,type,status,userGroup,collecttime)" + "values('" + vo.getIpaddress() + "','" + vo.getSessionId()
									+ "','" + vo.getType() + "','" + vo.getStatus() + "','" + vo.getUserGroup() + "','" + vo.getCollecttime() + "')";
							dbmanager.addBatch(sql);
						} catch (Exception e) {
							e.printStackTrace();
							return false;
						}
					}
					dbmanager.executeBatch();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dbmanager.close();
				}
			}

		} else if (returnhash.containsKey("sessionid")) {
			String sessionid = (String) returnhash.get("sessionid");
			String collecttime = (String) returnhash.get("collecttime");
			DBManager dbmanager = new DBManager();
			String sql = "update nms_hpdp set collecttime ='" + collecttime + "' where sessionId = '" + sessionid + "'";
			dbmanager.addBatch(sql);
			dbmanager.executeBatch();
			dbmanager.close();
		}
		return true;
	}

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		ipaddress = "";
		Hashtable returnHash = new Hashtable();
		Dp dp = (Dp) PollingEngine.getInstance().getDpByID(Integer.parseInt(alarmIndicatorsNode.getNodeid() + ""));
		if (dp == null) {
			return returnHash;
		}

		// ping�ɼ�
		Vector vector = null;
		Hashtable pinghash = new Hashtable();
		PingUtil pingU = new PingUtil(dp.getIpAddress());
		Integer[] packet = pingU.ping();
		try {
			vector = pingU.addhis(packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (vector != null) {
			ShareData.setPingdata(dp.getIpAddress(), vector);
			pinghash.put("ping", vector);
		}

		HostnetPingResultTosql tosql = new HostnetPingResultTosql();
		tosql.CreateResultTosql(pinghash, dp.getId() + "");
		if (pinghash != null) {
			// ��PINGֵ���и澯���
			if (pinghash != null && pinghash.size() > 0) {
				Vector pingvector = (Vector) pinghash.get("ping");
				if (pingvector != null) {
					String ping = "0";
					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
					for (int i = 0; i < pingvector.size(); i++) {
						PingCollectEntity pingdata = (PingCollectEntity) pingvector.elementAt(i);
						ping = pingdata.getThevalue();
						if (pingdata.getSubentity().equalsIgnoreCase("ConnectUtilization")) {
							// ��ͨ�ʽ����ж�
							List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(dp.getId()), alarmIndicatorsNode.getType(), "");
							for (int m = 0; m < list.size(); m++) {
								AlarmIndicatorsNode _alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(m);
								if ("1".equals(_alarmIndicatorsNode.getEnabled())) {
									if (_alarmIndicatorsNode.getName().equalsIgnoreCase("ping")) {
										CheckEventUtil checkeventutil = new CheckEventUtil();
										checkeventutil.checkEvent(dp, _alarmIndicatorsNode, pingdata.getThevalue());
									}
								}
							}
						}
					}
					pingvector = null;
					if (ping == "0") {
						return returnHash;
					}
				}
			}
		}

		vector = null;
		ipaddress = dp.getIpAddress();
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ipaddress);
		if (ipAllData == null) {
			ipAllData = new Hashtable();
		}

		StringBuffer fileContent = new StringBuffer();
		try {
			String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver/" + ipaddress + ".hpdp.log";
			FileInputStream fis = new FileInputStream(filename);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			String strLine = null;
			// �����ļ�����
			while ((strLine = br.readLine()) != null) {
				fileContent.append(strLine + "\n");
			}
			isr.close();
			fis.close();
			br.close();
			try {
				copyFile(ipaddress, getMaxNum(ipaddress));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Pattern tmpPt = null;
		Matcher mr = null;
		String collecttime = "";
		tmpPt = Pattern.compile("(cmdbegin:collecttime)(.*)(cmdbegin:collecttimeend)", Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if (mr.find()) {
			collecttime = mr.group(2);
		}
		if (collecttime != null && collecttime.length() > 0) {
			collecttime = collecttime.trim();
		}
		// //log��־�쳣�ж�
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		String lasttime = "";
		try {
			String sql = "select * from nms_hpdp where ipaddress = '" + ipaddress + "'  order by collecttime desc,sessionId desc";
			rs = dbmanager.executeQuery(sql);
			while (rs.next()) {
				lasttime = rs.getString("collecttime");
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			dbmanager.close();
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		String logstatus = "1";
		if (lasttime.equals(collecttime) || collecttime.equals("")) {
			logstatus = "-1";
		}
		try {
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(dp);
			// �ж��Ƿ���ڴ˸澯ָ��
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(nodeDTO.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype());
			CheckEventUtil checkEventUtil = new CheckEventUtil();
			for (int i = 0; i < list.size(); i++) {
				AlarmIndicatorsNode alarmsIndicatorsNode = (AlarmIndicatorsNode) list.get(i);
				if ("filestatus".equalsIgnoreCase(alarmsIndicatorsNode.getName())) {
					checkEventUtil.checkEvent(nodeDTO, alarmsIndicatorsNode, logstatus);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (logstatus == "-1") {
			return returnHash;
		}

		dbmanager = new DBManager();
		String dpsql = "select sessionId from nms_hpdp where id = (SELECT MAX(id) from nms_hpdp)";
		String sessionid = "";
		try {
			ResultSet rsdp = dbmanager.executeQuery(dpsql);
			while (rsdp.next()) {
				sessionid = rsdp.getString(1);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			dbmanager.close();
		}
		String hpdp = "";
		int flag = 0;
		if (sessionid != null && !sessionid.equals("")) {
			if (fileContent.toString().contains(sessionid)) {
				tmpPt = Pattern.compile("(" + sessionid + ")(.*)(cmdbegin:collectend)", Pattern.DOTALL);
			} else {
				tmpPt = Pattern.compile("(cmdbegin:collectsession)(.*)(cmdbegin:collectend)", Pattern.DOTALL);
				flag = 1;
			}
		} else {
			tmpPt = Pattern.compile("(cmdbegin:collectsession)(.*)(cmdbegin:collectend)", Pattern.DOTALL);
			flag = 1;
		}
		mr = tmpPt.matcher(fileContent.toString());
		if (mr.find()) {
			hpdp = mr.group(2);
		}

		String[] hpdpStr = hpdp.trim().split("\\n");
		String userGroup = "";
		Hpdpcollectdata vo = new Hpdpcollectdata();
		Vector hpdpVector = new Vector();
		Hashtable hash = new Hashtable();
		if (hpdpStr != null && hpdpStr.length > 0) {
			for (int i = 0; i < hpdpStr.length; i++) {
				vo = new Hpdpcollectdata();
				String perData = hpdpStr[i];
				if (perData.contains("���ڽ�����") || perData.equals("") || perData.startsWith("======") || perData.startsWith("�Ự ID") || perData.trim().startsWith("����")
						|| perData.trim().startsWith("����")) {
					continue;
				}
				String[] perDataArgs = perData.trim().split("\\s++");
				vo.setIpaddress(ipaddress);
				vo.setSessionId(perDataArgs[0]);
				vo.setType(perDataArgs[1]);
				vo.setStatus(perDataArgs[2]);
				userGroup = perDataArgs[3];
				if (perDataArgs.length > 4) {

					for (int j = 4; j < perDataArgs.length; j++) {
						userGroup += " " + perDataArgs[j];
					}
				}
				vo.setUserGroup(userGroup);
				vo.setCollecttime(collecttime);
				hpdpVector.add(vo);
			}
			try {
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(dp);
				// �ж��Ƿ���ڴ˸澯ָ��
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(nodeDTO.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype());
				CheckEventUtil checkEventUtil = new CheckEventUtil();
				for (int i = 0; i < list.size(); i++) {
					AlarmIndicatorsNode alarmIndicators = (AlarmIndicatorsNode) list.get(i);
					if ("dpstatus".equalsIgnoreCase(alarmIndicators.getName())) {

						SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
						String time = sdf.format(new Date());

						int b = 0;
						String dpstatus = "1";
						for (int k = 0; k < hpdpVector.size(); k++) {
							Hpdpcollectdata dpvo = (Hpdpcollectdata) hpdpVector.get(k);
							String states = (String) dpvo.getStatus();
							String time2 = dpvo.getSessionId().substring(0, 10);
							if (fileContent.toString().contains(sessionid) && !sessionid.equals("")) {
								if (!states.trim().equals("�����")) {
									b++;
								}
								System.out.println("2");
							} else {
								System.out.println("1");
								if (time.equals(time2)) {
									if (!states.trim().equals("�����")) {
										b++;
									}
								}
							}
						}

						if (b > 0) {
							dpstatus = "-1";
						}
						checkEventUtil.checkEvent(nodeDTO, alarmIndicators, dpstatus);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (hpdpVector.size() > 0 && hpdpVector != null) {
			hash.put("ipaddress", ipaddress);
			hash.put("hpdp", hpdpVector);
			hash.put("flag", flag);
			try {
				new LoadHpdpFile().addHpdp(hash);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (sessionid != null) {
			hash.put("ipaddress", ipaddress);
			hash.put("sessionid", sessionid);
			hash.put("collecttime", collecttime);
			hash.put("flag", flag);
			try {
				new LoadHpdpFile().addHpdp(hash);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return returnHash;
	}

	public void copyFile(String ipAddress, String max) {
		try {
			String currenttime = SysUtil.getCurrentTime();
			currenttime = currenttime.replaceAll("-", "");
			currenttime = currenttime.replaceAll(" ", "");
			currenttime = currenttime.replaceAll(":", "");
			String ipdir = ipAddress.replaceAll("\\.", "-");
			String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver_bak/" + ipdir;
			File file = new File(filename);
			if (!file.exists()) {
				file.mkdir();
			}
			String cmd = "cmd   /c   copy   " + ResourceCenter.getInstance().getSysPath() + "linuxserver\\" + ipAddress + ".log" + " " + ResourceCenter.getInstance().getSysPath()
					+ "linuxserver_bak\\" + ipdir + "\\" + ipAddress + "-" + currenttime + ".log";
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteFile(String ipAddress) {

		try {
			File delFile = new File(ResourceCenter.getInstance().getSysPath() + "linuxserver/" + ipAddress + ".log");
			System.out.println("###�ɹ�ɾ���ļ���" + delFile);
		} catch (Exception e) {
		}
	}

	public String getMaxNum(String ipAddress) {
		String maxStr = null;
		File logFolder = new File(ResourceCenter.getInstance().getSysPath() + "linuxserver/");
		String[] fileList = logFolder.list();

		for (int i = 0; i < fileList.length; i++) // ��һ�����µ��ļ�
		{
			if (!fileList[i].startsWith(ipAddress)) {
				continue;
			}

			return ipAddress;
		}
		return maxStr;
	}
}
