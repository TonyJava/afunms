package com.afunms.polling.task;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.application.dao.SlaNodeConfigDao;
import com.afunms.application.model.SlaNodeConfig;
import com.afunms.config.dao.HaweitelnetconfDao;
import com.afunms.config.model.Huaweitelnetconf;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.om.Task;
import com.afunms.polling.snmp.sla.ICMPSnmp;

@SuppressWarnings("unchecked")
public class M5SLATelnetTask extends MonitorTask {

	public M5SLATelnetTask() {
		super();
	}

	public void run() {
		try {
			SlaNodeConfigDao configdao = new SlaNodeConfigDao();
			// 得到被监视的SLA列表
			List nodeSnmpList = new ArrayList();
			List nodeTelnetList = new ArrayList();
			Hashtable nodeHash = new Hashtable();
			Hashtable telnetHash = new Hashtable();
			try {
				nodeSnmpList = configdao.getConfigByIntervalAndUnitAndFlagAndColltype(5, "m", 1, "snmp");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}
			configdao = new SlaNodeConfigDao();
			try {
				nodeTelnetList = configdao.getConfigByIntervalAndUnitAndFlagAndColltype(5, "m", 1, "telnet");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}

			HaweitelnetconfDao haweitelnetconfDao = new HaweitelnetconfDao();
			List telnetlist = null;
			try {
				telnetlist = haweitelnetconfDao.getAllTelnetConfig();
				if (telnetlist != null && telnetlist.size() > 0) {
					for (int i = 0; i < telnetlist.size(); i++) {
						Huaweitelnetconf vo = (Huaweitelnetconf) telnetlist.get(i);
						telnetHash.put(vo.getId(), vo);
					}
				}
			} catch (RuntimeException e) {
				e.printStackTrace();
			} finally {
				haweitelnetconfDao.close();
			}

			if (nodeTelnetList != null) {
				for (int i = 0; i < nodeTelnetList.size(); i++) {
					SlaNodeConfig nodeconfig = (SlaNodeConfig) nodeTelnetList.get(i);
					if (nodeHash.containsKey(nodeconfig.getTelnetconfig_id() + "")) {
						List entrylist = (List) nodeHash.get(nodeconfig.getTelnetconfig_id() + "");
						entrylist.add(nodeconfig);
						nodeHash.put(nodeconfig.getTelnetconfig_id() + "", entrylist);
					} else {
						List entrylist = new ArrayList();
						entrylist.add(nodeconfig);
						nodeHash.put(nodeconfig.getTelnetconfig_id() + "", entrylist);
					}
				}
			}
			try {
				List numList = new ArrayList();
				TaskXml taskxml = new TaskXml();
				numList = taskxml.ListXml();
				for (int i = 0; i < numList.size(); i++) {
					Task task = new Task();
					BeanUtils.copyProperties(task, numList.get(i));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			ThreadPool threadPool = null;
			ThreadPool threadPoolTelnet = null;
			final Hashtable alldata = new Hashtable();
			// 采集TELNET数据
			if (nodeHash != null && nodeHash.size() > 0) {
				threadPoolTelnet = new ThreadPool(nodeHash.size());
				Enumeration newProEnu = nodeHash.keys();
				while (newProEnu.hasMoreElements()) {
					String telnetconfig_id = (String) newProEnu.nextElement();
					List nodelist = (List) nodeHash.get(telnetconfig_id);
					Huaweitelnetconf telconf = new Huaweitelnetconf();
					try {
						telconf = (Huaweitelnetconf) telnetHash.get(Integer.parseInt(telnetconfig_id));
					} catch (Exception e) {
						e.printStackTrace();
					}
					threadPoolTelnet.runTask(createTask(telconf, nodelist, alldata));

				}
				threadPoolTelnet.join();
				threadPoolTelnet.close();
				HostCollectDataManager hostdataManager = new HostCollectDataManager();
				try {
					hostdataManager.createAllSLAData(alldata);
				} catch (Exception e) {

				}
				hostdataManager = null;
				alldata.clear();
			}
			threadPoolTelnet = null;

			if (nodeSnmpList != null && nodeSnmpList.size() > 0) {
				threadPool = new ThreadPool(nodeSnmpList.size());
				for (int i = 0; i < nodeSnmpList.size(); i++) {
					SlaNodeConfig nodeconfig = (SlaNodeConfig) nodeSnmpList.get(i);
					Huaweitelnetconf telconf = new Huaweitelnetconf();
					try {
						telconf = (Huaweitelnetconf) telnetHash.get(nodeconfig.getTelnetconfig_id());
					} catch (Exception e) {
						e.printStackTrace();
					}
					threadPool.runTask(createTask(nodeconfig, telconf, alldata));

				}
				threadPool.join();
				threadPool.close();
				HostCollectDataManager hostdataManager = new HostCollectDataManager();
				try {
					hostdataManager.createSLAData(alldata);
				} catch (Exception e) {

				}
				hostdataManager = null;
				alldata.clear();
			}
			threadPool = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建任务
	 */
	private static Runnable createTask(final SlaNodeConfig nodeconfig, final Huaweitelnetconf telconf, final Hashtable alldata) {
		return new Runnable() {
			public void run() {
				try {
					ICMPSnmp icmpsnmp = new ICMPSnmp();
					alldata.put(nodeconfig.getId() + "", icmpsnmp.collect_Data(nodeconfig, telconf));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

	/**
	 * 创建任务
	 */
	private static Runnable createTask(final Huaweitelnetconf telconf, final List nodelist, final Hashtable alldata) {
		return new Runnable() {
			public void run() {
				SLATelnetDataCollector telnetdatacollector = new SLATelnetDataCollector();
				try {
					if (nodelist.size() > 0) {
						alldata.put(telconf.getId() + "", telnetdatacollector.collect_data(telconf, nodelist));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

}
