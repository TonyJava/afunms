package com.afunms.polling.snmp.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.ShareData;
import com.afunms.emc.dao.sysDao;
import com.afunms.emc.model.Agent;
import com.afunms.emc.parser.AgentParser;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.topology.dao.ConnectDao;
import com.afunms.topology.model.Connect;

@SuppressWarnings("unchecked")
public class EMCSystemController {
	private Process process;
	private InputStream inputStream;
	private OutputStream outputStream;

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
			process = runtime.exec("naviseccli -user " + username + " -password " + password + " -Scope 0 -h " + host.getIpAddress() + " getagent");
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

		Agent agent = AgentParser.parse(dataBuffer.toString());
		Hashtable emcdata = null;
		if (!(ShareData.getEmcdata().containsKey(host.getIpAddress()))) {
			if (emcdata == null) {
				emcdata = new Hashtable();
			}
			if (agent != null) {
				emcdata.put("system", agent);
			}
			ShareData.getEmcdata().put(host.getIpAddress(), emcdata);
		} else {
			if (agent != null) {
				((Hashtable) ShareData.getEmcdata().get(host.getIpAddress())).put("system", agent);
			}
		}
		returnHash.put("system", agent);
		// 把采集结果生成sql
		sysDao sys = new sysDao();
		Agent a_vo = sys.query(alarmIndicatorsNode.getNodeid() + "");
		if (a_vo != null && agent != null) {
			sys = new sysDao();
			sys.delete(alarmIndicatorsNode.getNodeid() + "");
			sys = new sysDao();
			sys.save(agent, alarmIndicatorsNode.getNodeid() + "");
		} else if (a_vo == null && agent != null) {
			sys = new sysDao();
			sys.save(agent, alarmIndicatorsNode.getNodeid() + "");
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
