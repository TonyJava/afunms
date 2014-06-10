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
import com.afunms.emc.dao.envPerDao;
import com.afunms.emc.model.Environment;
import com.afunms.emc.model.MemModel;
import com.afunms.emc.parser.EnvironmentParser;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.topology.dao.ConnectDao;
import com.afunms.topology.model.Connect;

@SuppressWarnings("unchecked")
public class EMCEnvController {
	private Process process;
	private InputStream inputStream;
	private OutputStream outputStream;

	public EMCEnvController(String ipaddress, String name, String username, String password) {
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
			process = runtime.exec("naviseccli -user " + username + " -password " + password + " -Scope 0 -h " + host.getIpAddress() + " environment -list -all");
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

		Environment environment = EnvironmentParser.parse(dataBuffer.toString());
		Hashtable emcdata = null;
		if (!(ShareData.getEmcdata().containsKey(host.getIpAddress()))) {
			if (emcdata == null) {
				emcdata = new Hashtable();
			}
			if (environment != null) {
				emcdata.put("environment", environment);
			}
			ShareData.getEmcdata().put(host.getIpAddress(), emcdata);
		} else {
			if (environment != null) {
				((Hashtable) ShareData.getEmcdata().get(host.getIpAddress())).put("environment", environment);
			}
		}
		returnHash.put("environment", environment);

		try {
			EMCLUNConfigController util = new EMCLUNConfigController();
			List list = util.EMCgetAlarmInicatorsThresholdForNode(String.valueOf(alarmIndicatorsNode.getNodeid()), AlarmConstant.TYPE_STORAGE, "emc", "envpower");

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
				if (alarmIndicatorsnode.getName().equalsIgnoreCase("envwt")) {
					util.checkDisk(host, flag, alarmIndicatorsnode, "envpower", "envwt", name, flag);// vid=AlarmIndicatorsNode.getSubentity();flag=map的键值(在utilDao
					// 的queryLast()方法里定义的)；
				}
				if (alarmIndicatorsnode.getName().equalsIgnoreCase("envavgwt")) {
					util.checkDisk(host, flag, alarmIndicatorsnode, "envpower", "envavgwt", name, flag);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			EMCLUNConfigController util = new EMCLUNConfigController();
			List list = util.EMCgetAlarmInicatorsThresholdForNode(String.valueOf(alarmIndicatorsNode.getNodeid()), AlarmConstant.TYPE_STORAGE, "emc", "envstore");

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
				if (alarmIndicatorsnode.getName().equalsIgnoreCase("memtmp")) {
					util.checkDisk(host, flag, alarmIndicatorsnode, "envstore", "memtmp", name, flag);
				}
				if (alarmIndicatorsnode.getName().equalsIgnoreCase("memavgtmp")) {
					util.checkDisk(host, flag, alarmIndicatorsnode, "envstore", "memavgtmp", name, flag);
				}
				if (alarmIndicatorsnode.getName().equalsIgnoreCase("memwt")) {
					util.checkDisk(host, flag, alarmIndicatorsnode, "envstore", "memwt", name, flag);
				}
				if (alarmIndicatorsnode.getName().equalsIgnoreCase("memavgwt")) {
					util.checkDisk(host, flag, alarmIndicatorsnode, "envstore", "memavgwt", name, flag);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			EMCLUNConfigController util = new EMCLUNConfigController();
			List list = util.EMCgetAlarmInicatorsThresholdForNode(String.valueOf(alarmIndicatorsNode.getNodeid()), AlarmConstant.TYPE_STORAGE, "emc", "bakpower");

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
				if (alarmIndicatorsnode.getName().equalsIgnoreCase("bakwt")) {
					util.checkDisk(host, flag, alarmIndicatorsnode, "bakpower", "bakwt", name, flag);// vid=AlarmIndicatorsNode.getSubentity();flag=map的键值(在utilDao
					// 的queryLast()方法里定义的)；
				}
				if (alarmIndicatorsnode.getName().equalsIgnoreCase("bakavgwt")) {
					util.checkDisk(host, flag, alarmIndicatorsnode, "bakpower", "bakavgwt", name, flag);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (environment != null) {
			com.afunms.emc.model.Array array = environment.getArray();
			List<MemModel> liststore = environment.getMemList();
			List<MemModel> listbakpower = environment.getBakPowerList();

			envPerDao dao = new envPerDao();
			dao.saveArray(array, host.getIpAddress());

			dao = new envPerDao();
			dao.saveStore(liststore, host.getIpAddress());

			dao = new envPerDao();
			dao.saveBakPower(listbakpower, host.getIpAddress());

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
