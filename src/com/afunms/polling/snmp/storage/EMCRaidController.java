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
import com.afunms.emc.dao.raidDao;
import com.afunms.emc.model.RaidGroup;
import com.afunms.emc.parser.RaidGroupParser;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.topology.dao.ConnectDao;
import com.afunms.topology.model.Connect;

@SuppressWarnings("unchecked")
public class EMCRaidController {
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
			process = runtime.exec("naviseccli -user " + username + " -password " + password + " -Scope 0 -h " + host.getIpAddress() + " getrg");
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

		List<RaidGroup> raidList = RaidGroupParser.parse(dataBuffer.toString());
		Hashtable emcdata = null;
		if (!(ShareData.getEmcdata().containsKey(host.getIpAddress()))) {
			if (emcdata == null) {
				emcdata = new Hashtable();
			}
			if (raidList != null && raidList.size() > 0) {
				emcdata.put("raid", raidList);
			}
			ShareData.getEmcdata().put(host.getIpAddress(), emcdata);
		} else {
			if (raidList != null && raidList.size() > 0) {
				((Hashtable) ShareData.getEmcdata().get(host.getIpAddress())).put("raid", raidList);
			}
		}
		returnHash.put("raid", raidList);

		raidDao sys = new raidDao();
		List<RaidGroup> a_vo = sys.query(alarmIndicatorsNode.getNodeid() + "");
		if (a_vo != null && raidList != null && raidList.size() > 0 && a_vo.size() > 0) {
			sys = new raidDao();
			sys.delete(alarmIndicatorsNode.getNodeid() + "");
			RaidGroup vo_save = null;
			for (int i = 0; i < raidList.size(); i++) {
				sys = new raidDao();
				vo_save = raidList.get(i);
				sys.save(vo_save, alarmIndicatorsNode.getNodeid() + "");
			}
		} else if (a_vo == null && raidList != null && raidList.size() > 0) {
			RaidGroup vo_save = null;
			for (int i = 0; i < raidList.size(); i++) {
				sys = new raidDao();
				vo_save = raidList.get(i);
				sys.save(vo_save, alarmIndicatorsNode.getNodeid() + "");
			}
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
