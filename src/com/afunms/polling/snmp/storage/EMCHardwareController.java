package com.afunms.polling.snmp.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.util.ShareData;
import com.afunms.emc.dao.hardDao;
import com.afunms.emc.model.Crus;
import com.afunms.emc.model.HardCrus;
import com.afunms.emc.parser.CrusParser;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.topology.dao.ConnectDao;
import com.afunms.topology.model.Connect;

@SuppressWarnings("unchecked")
public class EMCHardwareController {
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
		String password = "";
		ConnectDao condao = new ConnectDao();
		List<Connect> list_vo = condao.getbynodeid(Long.parseLong(host.getId() + ""));
		Connect vo = null;
		if (list_vo != null && list_vo.size() > 0) {
			vo = list_vo.get(0);
		}

		username = vo.getUsername();
		password = vo.getPwd();

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
		if (ipAllData == null) {
			ipAllData = new Hashtable();
		}
		Runtime runtime = Runtime.getRuntime();
		StringBuffer dataBuffer = new StringBuffer();
		try {
			process = runtime.exec("naviseccli -user " + username + " -password " + password + " -Scope 0 -h " + host.getIpAddress() + " getcrus");
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

		List<HardCrus> crus = CrusParser.parse(dataBuffer.toString());
		Hashtable emcdata = null;
		if (!(ShareData.getEmcdata().containsKey(host.getIpAddress()))) {
			if (emcdata == null) {
				emcdata = new Hashtable();
			}
			if (crus != null) {
				emcdata.put("hardwarestatus", crus);
			}
			ShareData.getEmcdata().put(host.getIpAddress(), emcdata);
		} else {
			if (crus != null) {
				((Hashtable) ShareData.getEmcdata().get(host.getIpAddress())).put("hardwarestatus", crus);
			}
		}
		returnHash.put("hardwarestatus", crus);

		// 把采集结果生成sql
		hardDao sys = new hardDao();
		List<Crus> a_vo = sys.queryList(alarmIndicatorsNode.getNodeid() + "");
		if (a_vo == null && a_vo.size() > 0) {
			sys = new hardDao();
			sys.save(crus, alarmIndicatorsNode.getNodeid() + "");
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
