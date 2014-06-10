package com.afunms.polling.snmp.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.emc.dao.lunConDao;
import com.afunms.emc.dao.lunPerDao;
import com.afunms.emc.dao.utilDao;
import com.afunms.emc.model.Lun;
import com.afunms.emc.parser.LunParser;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.topology.dao.ConnectDao;
import com.afunms.topology.model.Connect;

@SuppressWarnings("unchecked")
public class EMCLUNConfigController {
	private Process process;
	private InputStream inputStream;
	private OutputStream outputStream;

	public void checkDisk(Host node, String vid, AlarmIndicatorsNode nm, String category, String flag, String name, String guestname) {

		CheckEventUtil ce = new CheckEventUtil();
		if ("0".equals(nm.getEnabled())) {
			return;
		}
		AlarmIndicatorsNodeDao alarm = new AlarmIndicatorsNodeDao();
		AlarmIndicatorsNode vo = new AlarmIndicatorsNode();
		List list = alarm.VMgetByNodeIdAndTypeAndSubType(node.getId() + "", "storage", "emc_vnx", category, vid, name);
		if (list.size() > 0) {
			vo = (AlarmIndicatorsNode) list.get(0);
			int limevalue0 = Integer.parseInt(vo.getLimenvalue0());
			int limevalue1 = Integer.parseInt(vo.getLimenvalue1());
			int limevalue2 = Integer.parseInt(vo.getLimenvalue2());
			nm.setLimenvalue0(limevalue0 + "");
			nm.setLimenvalue1(limevalue1 + "");
			nm.setLimenvalue2(limevalue2 + "");
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(node);

			utilDao dao = new utilDao();
			String tablename = "";
			if (category.equalsIgnoreCase("lun")) {
				tablename = "emclunper";
			} else if (category.equalsIgnoreCase("disk")) {
				tablename = "emcdiskper";
			} else if (category.equalsIgnoreCase("envpower")) {
				tablename = "emcenvpower";
			} else if (category.equalsIgnoreCase("envstore")) {
				tablename = "emcenvstore";
			} else if (category.equalsIgnoreCase("bakpower")) {
				tablename = "emcbakpower";
			}
			HashMap value = dao.queryLast(tablename, node.getIpAddress(), vid);
			if (value.size() != 0) {
				ce.checkEvent(nodeDTO, nm, value.get(flag).toString(), guestname);
			}
		}
	}

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
			process = runtime.exec("cmd /c dscli -hmc1 " + host.getIpAddress() + " -user " + username + " -passwd " + password);
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

		List<Lun> lunList = LunParser.parse(dataBuffer.toString());
		Hashtable emcdata = null;
		if (!(ShareData.getEmcdata().containsKey(host.getIpAddress()))) {
			if (emcdata == null) {
				emcdata = new Hashtable();
			}
			if (lunList != null && lunList.size() > 0) {
				emcdata.put("lunconfig", lunList);
			}
			ShareData.getEmcdata().put(host.getIpAddress(), emcdata);
		} else {
			if (lunList != null && lunList.size() > 0) {
				((Hashtable) ShareData.getEmcdata().get(host.getIpAddress())).put("lunconfig", lunList);
			}
		}
		returnHash.put("lunconfig", lunList);
		try {
			List list = this.EMCgetAlarmInicatorsThresholdForNode(String.valueOf(alarmIndicatorsNode.getNodeid()), AlarmConstant.TYPE_STORAGE, "emc_vnx", "lun");

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
				if (alarmIndicatorsnode.getName().equalsIgnoreCase("harderror")) {
					this.checkDisk(host, flag, alarmIndicatorsnode, "lun", "harderror", name, flag);// vid=AlarmIndicatorsNode.getSubentity();flag=map的键值(在utilDao
					// 的queryLast()方法里定义的)；
				}
				if (alarmIndicatorsnode.getName().equalsIgnoreCase("softerror")) {
					this.checkDisk(host, flag, alarmIndicatorsnode, "lun", "softerror", name, flag);
				}
				if (alarmIndicatorsnode.getName().equalsIgnoreCase("lunlength")) {
					this.checkDisk(host, flag, alarmIndicatorsnode, "lun", "length", name, flag);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		lunConDao sys = new lunConDao();
		List<Lun> a_vo = sys.query(alarmIndicatorsNode.getNodeid() + "");
		if (a_vo.size() == 0 && lunList != null && lunList.size() > 0) {
			sys.saveList(lunList, alarmIndicatorsNode.getNodeid() + "");
		}
		// 性能入库
		if (lunList != null && lunList.size() > 0) {
			lunPerDao dao = new lunPerDao();
			dao.saveList(lunList, host.getIpAddress());
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

	public List EMCgetAlarmInicatorsThresholdForNode(String nodeId, String type, String subtype, String category) {
		String key = nodeId + ":" + type + ":" + subtype;
		List resultList = new ArrayList();// 根据Key查询得到的结果集合需要调整的
		try {
			Hashtable hs = ResourceCenter.getInstance().getAlarmHashtable();

			if (hs == null) {
				hs = new Hashtable();
			}
			if (category != null && category.trim().length() > 0) {
				if (hs.containsKey(key)) {
					resultList = (ArrayList) hs.get(key);
				}
			} else {
				if (hs.size() > 0) {
					Enumeration newProEnu = hs.keys();
					while (newProEnu.hasMoreElements()) {
						String alarmName = (String) newProEnu.nextElement();
						if (alarmName.startsWith(nodeId + ":" + type + ":")) {
							resultList = (ArrayList) hs.get(alarmName);
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		List list = new ArrayList();
		for (int i = 0; i < resultList.size(); i++) {
			AlarmIndicatorsNode vo = new AlarmIndicatorsNode();
			vo = (AlarmIndicatorsNode) resultList.get(i);
			if (vo.getCategory().equalsIgnoreCase(category)) {
				list.add(vo);
			}
		}
		return list;
	}

	public boolean init() {
		return true;
	}

}
