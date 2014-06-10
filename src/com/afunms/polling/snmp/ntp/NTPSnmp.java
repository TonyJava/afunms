package com.afunms.polling.snmp.ntp;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Ntp;
import com.afunms.system.util.TimeGratherConfigUtil;

@SuppressWarnings("unchecked")
public class NTPSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returnHash = new Hashtable();
		Ntp node = (Ntp) PollingEngine.getInstance().getNtpByID(Integer.parseInt(nodeGatherIndicators.getNodeid()));
		if (node == null) {
			return returnHash;
		}

		// �ж��Ƿ��ڲɼ�ʱ�����
		if (ShareData.getTimegatherhash() != null) {
			if (ShareData.getTimegatherhash().containsKey(node.getId() + ":equipment")) {
				TimeGratherConfigUtil timeconfig = new TimeGratherConfigUtil();
				int _result = 0;
				_result = timeconfig.isBetween((List) ShareData.getTimegatherhash().get(node.getId() + ":equipment"));
				if (_result == 1) {
				} else if (_result == 2) {
				} else {
					// ���֮ǰ�ڴ��в����ĸ澯��Ϣ
					try {
						NodeDTO nodedto = null;
						NodeUtil nodeUtil = new NodeUtil();
						nodedto = nodeUtil.creatNodeDTOByNode(node);
						CheckEventUtil checkutil = new CheckEventUtil();
						checkutil.deleteEvent(node.getId() + "", nodedto.getType(), nodedto.getSubtype(), "storage", null);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return returnHash;
				}

			}
		}
		String date = "";
		String collecttime = "";
		String datestatus = "1";
		try {
			int tries = 0;
			int MAXTRIES = 3;
			boolean receivedResponse = false;
			DatagramPacket packet = null;
			DatagramSocket socket = null;
			do {
				try {
					// ����UDP DatagramSocket����
					socket = new DatagramSocket();
					// ָ��timeoutʱ�䣬��ֹ�������޵ȴ�״̬
					socket.setSoTimeout(3000);
					InetAddress address = InetAddress.getByName(node.getIpAddress());
					byte[] buf = new NtpMessage().toByteArray();
					packet = new DatagramPacket(buf, buf.length, address, 123);
					NtpMessage.encodeTimestamp(packet.getData(), 40, (System.currentTimeMillis() / 1000.0) + 2208988800.0);
					socket.send(packet);
					packet = new DatagramPacket(buf, buf.length);
					socket.receive(packet);

					if (!packet.getAddress().equals(address)) {
						throw new IOException("Received packet from an unknown source");
					}
					receivedResponse = true;

				} catch (InterruptedIOException e) {
					e.printStackTrace();
					tries += 1;
				}
			} while ((!receivedResponse) && (tries < MAXTRIES));

			// �����Ƿ���յ����Ľ��з���
			if (receivedResponse) {
				double destinationTimestamp = (System.currentTimeMillis() / 1000.0) + 2208988800.0;

				// Process response
				NtpMessage msg = new NtpMessage(packet.getData());
				// Corrected, according to RFC2030 errata
				double localClockOffset = ((msg.receiveTimestamp - msg.originateTimestamp) + (msg.transmitTimestamp - destinationTimestamp)) / 2;
				double newSystemTime = destinationTimestamp + localClockOffset;
				long ms = (long) (newSystemTime * 1000.0);
				date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(ms)).toString();
				collecttime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				socket.close();
			} else {
				if (date == "" || date.equals("")) {
					datestatus = "-1";
				}
			}

			try {
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(node);
				// �ж��Ƿ���ڴ˸澯ָ��
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(nodeDTO.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype());
				CheckEventUtil checkEventUtil = new CheckEventUtil();
				for (int i = 0; i < list.size(); i++) {
					AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(i);
					if ("datestatus".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
						checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, datestatus, "ntp");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		returnHash.put("date", date);
		returnHash.put("node", node);
		returnHash.put("collecttime", collecttime);

		new NTPSnmp().addTNP(returnHash);
		return returnHash;
	}

	public boolean addTNP(Hashtable returnhash) {

		if (returnhash != null) {
			String ipaddress = "";
			Ntp ntp = (Ntp) returnhash.get("node");
			ipaddress = ntp.getIpAddress();
			String date = (String) returnhash.get("date");
			String collecttime = (String) returnhash.get("collecttime");
			DBManager dbmanager = new DBManager();
			try {
				String sql1 = "delete from nms_ntp where ipaddress='" + ipaddress + "'";
				dbmanager.addBatch(sql1);

				String sql = "";
				try {
					sql = "insert into nms_ntp(ipaddress,datetime,collecttime)" + "values('" + ntp.getIpAddress() + "','" + date + "','" + collecttime + "')";
					dbmanager.addBatch(sql);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				dbmanager.executeBatch();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbmanager.close();
			}
		}
		return true;
	}
}