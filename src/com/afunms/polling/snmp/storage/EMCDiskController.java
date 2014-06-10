package com.afunms.polling.snmp.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.List;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.ShareData;
import com.afunms.emc.dao.diskConDao;
import com.afunms.emc.dao.diskPerDao;
import com.afunms.emc.model.Disk;
import com.afunms.emc.parser.DiskParser;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.topology.dao.ConnectDao;
import com.afunms.topology.model.Connect;

@SuppressWarnings("unchecked")
public class EMCDiskController {
	private Process process;
	private InputStream inputStream;
	private OutputStream outputStream;

	public EMCDiskController(String ipaddress, String name, String username, String password) {
	}

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));// Integer.parseInt(alarmIndicatorsNode.getNodeid())
		if (host == null) {
			return returnHash;
		}

		String username = "";
		String pws = "";
		String password = "";
		ConnectDao condao = new ConnectDao();
		List<Connect> list_vo = condao.getbynodeid(Long.parseLong(host.getId() + ""));
		Connect vo = null;
		if (list_vo != null && list_vo.size() > 0) {
			vo = list_vo.get(0);
		}

		username = vo.getUsername();
		pws = vo.getPwd();
		try {
			if (!pws.equals("")) {
				password = EncryptUtil.decode(pws);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
		if (ipAllData == null) {
			ipAllData = new Hashtable();
		}

		Runtime runtime = Runtime.getRuntime();
		StringBuffer dataBuffer = new StringBuffer();
		try {
			process = runtime.exec("naviseccli -user " + username + " -password " + password + " -Scope 0 -h " + host.getIpAddress() + " getdisk -all");
			inputStream = process.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String inStr = "";
			while ((inStr = bufferedReader.readLine()) != null) {
				dataBuffer.append(inStr + "\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<Disk> diskList = DiskParser.parse(dataBuffer.toString());
		Hashtable emcdata = null;
		if (!(ShareData.getEmcdata().containsKey(host.getIpAddress()))) {
			if (emcdata == null) {
				emcdata = new Hashtable();
			}
			if (diskList != null && diskList.size() > 0) {
				emcdata.put("disk", diskList);
			}
			ShareData.getEmcdata().put(host.getIpAddress(), emcdata);
		} else {
			if (diskList != null && diskList.size() > 0) {
				((Hashtable) ShareData.getEmcdata().get(host.getIpAddress())).put("disk", diskList);
			}
		}
		returnHash.put("disk", diskList);

		try {
			EMCLUNConfigController util = new EMCLUNConfigController();
			List list = util.EMCgetAlarmInicatorsThresholdForNode(String.valueOf(alarmIndicatorsNode.getNodeid()), AlarmConstant.TYPE_STORAGE, "emc", "disk");

			for (int i = 0; i < list.size(); i++) {
				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode) list.get(i);
				String flag = "";
				String name = "";
				if (alarmIndicatorsnode.getEnabled().equals("1")) {
					flag = alarmIndicatorsnode.getSubentity();
					name = alarmIndicatorsnode.getName();
				} else {
					continue;
				}
				if (alarmIndicatorsnode.getName().equalsIgnoreCase("diskhardread")) {
					util.checkDisk(host, flag, alarmIndicatorsnode, "disk", "diskhardread", name, flag);// vid=AlarmIndicatorsNode.getSubentity();flag=map的键值(在utilDao
					// 的queryLast()方法里定义的)；
				}
				if (alarmIndicatorsnode.getName().equalsIgnoreCase("disksoftwrite")) {
					util.checkDisk(host, flag, alarmIndicatorsnode, "disk", "diskread", name, flag);
				}
				if (alarmIndicatorsnode.getName().equalsIgnoreCase("diskread")) {
					util.checkDisk(host, flag, alarmIndicatorsnode, "disk", "diskread", name, flag);
				}
				if (alarmIndicatorsnode.getName().equalsIgnoreCase("diskwrite")) {
					util.checkDisk(host, flag, alarmIndicatorsnode, "disk", "diskwrite", name, flag);
				}
				if (alarmIndicatorsnode.getName().equalsIgnoreCase("diskreadkb")) {
					util.checkDisk(host, flag, alarmIndicatorsnode, "disk", "diskreadkb", name, flag);
				}
				if (alarmIndicatorsnode.getName().equalsIgnoreCase("diskwritekb")) {
					util.checkDisk(host, flag, alarmIndicatorsnode, "disk", "diskwritekb", name, flag);
				}
				if (alarmIndicatorsnode.getName().equalsIgnoreCase("diskhardwrite")) {
					util.checkDisk(host, flag, alarmIndicatorsnode, "disk", "diskhardwrite", name, flag);
				}
				if (alarmIndicatorsnode.getName().equalsIgnoreCase("disksoftwrite")) {
					util.checkDisk(host, flag, alarmIndicatorsnode, "disk", "disksoftwrite", name, flag);
				}
				if (alarmIndicatorsnode.getName().equalsIgnoreCase("diskfree")) {
					util.checkDisk(host, flag, alarmIndicatorsnode, "disk", "diskfree", name, flag);
				}
				if (alarmIndicatorsnode.getName().equalsIgnoreCase("diskbus")) {
					util.checkDisk(host, flag, alarmIndicatorsnode, "disk", "diskbus", name, flag);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		diskConDao sys = new diskConDao();
		List<Disk> a_vo = sys.query(alarmIndicatorsNode.getNodeid() + "");

		if (a_vo.size() > 0 && diskList != null && diskList.size() > 0 && a_vo.size() > 0) {
			sys = new diskConDao();
			sys.delete(alarmIndicatorsNode.getNodeid() + "");
			sys = new diskConDao();
			sys.saveList(diskList, alarmIndicatorsNode.getNodeid() + "");
		} else if (a_vo.size() == 0 && diskList != null && diskList.size() > 0) {
			sys = new diskConDao();
			sys.saveList(diskList, alarmIndicatorsNode.getNodeid() + "");
		}
		// 性能入库
		if (diskList != null && diskList.size() > 0) {
			diskPerDao dao = new diskPerDao();
			dao.saveList(diskList, host.getIpAddress());
		}
		return returnHash;
	}

	public boolean destroy() {
		try {
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
			if (process != null) {
				process.destroy();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean init() {
		return true;
	}

}
