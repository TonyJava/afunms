package com.afunms.application.wasmonitor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.afunms.application.dao.WasConfigDao;
import com.afunms.application.model.WasConfig;
import com.afunms.polling.om.Interfacecollectdata;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.AdminConstants;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.pmi.stat.WSStatistic;
import com.ibm.websphere.pmi.stat.WSStats;
import com.ibm.ws.pmi.stat.AverageStatisticImpl;
import com.ibm.ws.pmi.stat.BoundedRangeStatisticImpl;
import com.ibm.ws.pmi.stat.CountStatisticImpl;
import com.ibm.ws.pmi.stat.RangeStatisticImpl;
import com.ibm.ws.pmi.stat.TimeStatisticImpl;

@SuppressWarnings("unchecked")
public class AdminClient5 {

	private AdminClient adminClnt = null;

	public Hashtable collectData(final WasConfig wasconf) {

		Hashtable rValue = new Hashtable();
		Hashtable JvmHst = collectJvmData(wasconf.getIpaddress(), wasconf.getPortnum(), wasconf.getNodename(), wasconf.getServername());
		Hashtable JDBCProviderHst = collectJDBCProviderData(wasconf.getIpaddress(), wasconf.getPortnum(), wasconf.getNodename(), wasconf.getServername());
		Hashtable SessionManagerHst = collectSessionManagerData(wasconf.getIpaddress(), wasconf.getPortnum(), wasconf.getNodename(), wasconf.getServername());
		Hashtable SystemMetricsHst = collectSystemMetricsData(wasconf.getIpaddress(), wasconf.getPortnum(), wasconf.getNodename(), wasconf.getServername());
		Hashtable DynaCacheHst = collectDynaCacheData(wasconf.getIpaddress(), wasconf.getPortnum(), wasconf.getNodename(), wasconf.getServername());
		Hashtable TransactionServiceHst = collectTransactionServiceData(wasconf.getIpaddress(), wasconf.getPortnum(), wasconf.getNodename(), wasconf.getServername());
		Hashtable ORBHst = collectORBData(wasconf.getIpaddress(), wasconf.getPortnum(), wasconf.getNodename(), wasconf.getServername());

		rValue.put("JvmHst", JvmHst);
		rValue.put("JDBCProviderHst", JDBCProviderHst);
		rValue.put("SessionManagerHst", SessionManagerHst);
		rValue.put("SystemMetricsHst", SystemMetricsHst);
		rValue.put("DynaCacheHst", DynaCacheHst);
		rValue.put("TransactionServiceHst", TransactionServiceHst);
		rValue.put("ORBHst", ORBHst);
		return rValue;
	}

	public Hashtable collectData(final WasConfig wasconf, Hashtable gatherhash) {
		Hashtable rValue = new Hashtable();
		rValue = collectWasData(wasconf.getIpaddress(), wasconf.getPortnum(), wasconf.getNodename(), wasconf.getServername(), gatherhash);
		return rValue;
	}

	public String InitgetObjectNameForType(Hashtable inParam) {
		String objectNameType = (String) inParam.get("type");
		String myNode = (String) inParam.get("myNode");
		String myServer = (String) inParam.get("myServer");
		String returnStr = "";
		ArrayList result = null;
		if (objectNameType == null) {
			return null;
		}
		if (objectNameType.equalsIgnoreCase("node")) {
			result = listNodes();
		} else if (objectNameType.equalsIgnoreCase("Server")) {
			result = listServers(myNode);
		} else {
			String queryString = "WebSphere:*,node=" + myNode + ",process=" + myServer + ",type=" + objectNameType;
			// ��ѯ������
			Set theTypeSet = this.queryObjectNames(queryString);
			if (theTypeSet == null)
				return null;
			// ��ʼ��֯���ض���
			Iterator it = theTypeSet.iterator();
			ObjectName on;
			while (it.hasNext()) {
				on = (ObjectName) it.next();
				returnStr += on.toString() + "###";
			}
			return returnStr;
		}
		// �� ARRAYLIST �����
		if (result != null) {
			for (int i = 0; i < result.size(); i++) {
				returnStr += result.get(i).toString() + "###";
			}
		}
		return returnStr;
	}

	public boolean connectWasIsOK(String ip, int port) {
		java.util.Properties props = new java.util.Properties();
		props.put(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
		props.put(AdminClient.CONNECTOR_HOST, ip);
		props.put(AdminClient.CONNECTOR_PORT, port + "");
		try {
			AdminClientFactory.createAdminClient(props);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public static void main(String[] arg) {
		java.util.Properties props = new java.util.Properties();
		props.put(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
		props.put(AdminClient.CONNECTOR_HOST, "10.10.152.59");
		props.put(AdminClient.CONNECTOR_PORT, "8879");

		AdminClient adminClient = null;

		try {
			adminClient = AdminClientFactory.createAdminClient(props);
		} catch (ConnectorException e) {
			e.printStackTrace();
			System.out.println("Exception creating admin client: " + e);
		} catch (Exception e) {

			e.printStackTrace();
		}
		try {
			Set test = adminClient.queryNames(new ObjectName("WebSphere:*,type=JVM"), null);
			Iterator it = test.iterator();
			ObjectName on = null;

			while (it.hasNext()) {
				on = (ObjectName) it.next();

				// ȡָ��
				try {
					AdminClient5 admin5 = new AdminClient5();
					String myNode = "MS151912Node01";
					String myServer = "server1";
					ObjectName perfMbean = null;
					admin5.adminClnt = adminClient;
					perfMbean = admin5.getMbean(myNode, myServer, "Perf");
					if (perfMbean == null) {
						System.out.println("No PerfMBean is found for the selected server. Make sure PMI service is enabled on the server.");

					}
					WSStats wsmyStats = admin5.collectStatsViaPerfMbeanWS(perfMbean, on, false);
					Hashtable out_pram12 = admin5.dispStats(wsmyStats);
					Iterator valueIt12 = out_pram12.keySet().iterator();
					String returnStr = "";
					while (valueIt12.hasNext()) {
						String key = (String) valueIt12.next();
						returnStr += key + "===" + out_pram12.get(key) + "###";
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} catch (ConnectorException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		}

	}

	public void connect(Hashtable inParam) {
		String IP = (String) inParam.get("IP");
		String Port = (String) inParam.get("Port");
		if (IP == null || Port == null) {
			return;
		}
		java.util.Properties props = new java.util.Properties();
		if (inParam.get("securityEnabled") != null && inParam.get("securityEnabled").toString().equals("1")) {
			props.put(AdminClient.CONNECTOR_TYPE, "SOAP");
			props.put(AdminClient.CONNECTOR_HOST, IP);
			props.put(AdminClient.CONNECTOR_PORT, Port);
			props.setProperty(AdminClient.CONNECTOR_SECURITY_ENABLED, "true");
			props.setProperty(AdminClient.USERNAME, (String) inParam.get("UserName"));
			props.setProperty(AdminClient.PASSWORD, (String) inParam.get("passWord"));
			props.setProperty("javax.net.ssl.trustStore", (String) inParam.get("trustStoreFile"));
			props.setProperty("javax.net.ssl.keyStore", (String) inParam.get("keyStoreFile"));
			props.setProperty("javax.net.ssl.trustStorePassword", (String) inParam.get("trustStorePassword"));
			props.setProperty("javax.net.ssl.keyStorePassword", (String) inParam.get("keyStorePassword"));
		} else {
			props.put(AdminClient.CONNECTOR_TYPE, "SOAP");
			props.put(AdminClient.CONNECTOR_HOST, IP);
			props.put(AdminClient.CONNECTOR_PORT, Port);
		}
		try {
			adminClnt = AdminClientFactory.createAdminClient(props);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Set queryName(String queryString, AdminClient adminClient) {
		Set oSet = null;
		try {

			if (adminClient != null) {
				oSet = adminClient.queryNames(new ObjectName(queryString), null);
			}
		} catch (Exception ce) {
			ce.printStackTrace();
		}

		return oSet;
	}

	public ArrayList listNodes() {
		Set oSet = queryObjectNames("WebSphere:*,type=Server");
		if (oSet == null)
			return null;
		Iterator it = oSet.iterator();
		HashSet nodeSet = new HashSet();
		ObjectName on;
		while (it.hasNext()) {
			on = (ObjectName) it.next();
			// filter out dmgr
			if (!on.getKeyProperty("processType").equals(AdminConstants.DEPLOYMENT_MANAGER_PROCESS)) {
				nodeSet.add(on);
			}
		}

		if (nodeSet.size() == 0) {
			return null;
		}

		Object[] genericNodeArray = nodeSet.toArray();
		ArrayList ret = new ArrayList(genericNodeArray.length);
		for (int i = 0; i < genericNodeArray.length; i++)
			ret.add(genericNodeArray[i]);

		return ret;
	}

	public ArrayList listServers(String nodeName) {
		ArrayList servers = new ArrayList();

		Set oSet = queryObjectNames("WebSphere:*,type=Server,node=" + nodeName);
		if (oSet == null)
			return null;
		Iterator it = oSet.iterator();
		ObjectName on;
		while (it.hasNext()) {
			on = (ObjectName) it.next();
			servers.add(on);
		}

		return servers;
	}

	public Set queryObjectNames(String queryString) {
		Set oSet = null;
		try {

			if (adminClnt != null) {

				oSet = adminClnt.queryNames(new ObjectName(queryString), null);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return oSet;
	}

	public void testObjectNameAttrs() {
		try {
			ObjectName testInfo = null;
			testInfo = new ObjectName("WebSphere:platform=common,cell=lenovo-dbf05b19,version=5.0,name=JVM,mbeanIdentifier=JVM,type=JVM,node=lenovo-dbf05b19,process=server1");
			MBeanInfo nodeInfo = adminClnt.getMBeanInfo(testInfo);
			MBeanAttributeInfo[] nodeAttrs = nodeInfo.getAttributes();
			for (int i = 0; i < nodeAttrs.length; i++) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ObjectName getMbean(String node, String server, String mbeanType) {
		try {
			String queryStr = "WebSphere:*,type=" + mbeanType + ",node=" + node + ",process=" + server;
			Set oSet = adminClnt.queryNames(new ObjectName(queryStr), null);
			Iterator it = oSet.iterator();
			if (it.hasNext()) {
				ObjectName tempIt = (ObjectName) it.next();
				return tempIt;
			} else {
				System.out.println("Cannot find PerfMBean for node=" + node + ", server=" + server + ", type=" + mbeanType);
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public WSStats collectStatsViaPerfMbeanWS(ObjectName perfOname, ObjectName oname, boolean recursive) {
		try {
			Object[] params = new Object[] { oname, new Boolean(recursive) };
			String[] sigs = new String[] { "javax.management.ObjectName", "java.lang.Boolean" };
			Object a = adminClnt.invoke(perfOname, "getStatsObject", params, sigs);
			return (WSStats) a;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public Hashtable dispStats(WSStats in_Stats) {
		if (in_Stats == null)
			return null;
		Hashtable rtResult = new Hashtable();
		com.ibm.websphere.pmi.stat.WSStats pmiStat = (com.ibm.websphere.pmi.stat.WSStats) in_Stats;
		WSStatistic[] dataMembers = pmiStat.getStatistics();
		if (dataMembers != null) {
			for (int i = 0; i < dataMembers.length; i++) {
				dataMembers[i].getName();
				if (dataMembers[i].getDataInfo() != null && (dataMembers[i].getDataInfo().getType() == 2 || dataMembers[i] instanceof CountStatisticImpl)) {
					CountStatisticImpl tempCntStat = (CountStatisticImpl) dataMembers[i];
					rtResult.put(dataMembers[i].getName(), String.valueOf(tempCntStat.getCount()));
				} else if (dataMembers[i].getDataInfo() != null && (dataMembers[i].getDataInfo().getType() == 4 || dataMembers[i] instanceof TimeStatisticImpl)) {
					TimeStatisticImpl data = (TimeStatisticImpl) dataMembers[i];
					rtResult.put(dataMembers[i].getName(), String.valueOf(data.getCount()));
				} else if (dataMembers[i].getDataInfo() != null && (dataMembers[i].getDataInfo().getType() == 7 || dataMembers[i] instanceof RangeStatisticImpl)) {
					RangeStatisticImpl data = (RangeStatisticImpl) dataMembers[i];
					rtResult.put(dataMembers[i].getName(), String.valueOf(data.getCurrent()));
				} else if (dataMembers[i].getDataInfo() != null && (dataMembers[i].getDataInfo().getType() == 6 || dataMembers[i] instanceof AverageStatisticImpl)) {
					AverageStatisticImpl data = (AverageStatisticImpl) dataMembers[i];
					rtResult.put(dataMembers[i].getName(), String.valueOf(data.getCount()));
				} else if (dataMembers[i].getDataInfo() != null && (dataMembers[i].getDataInfo().getType() == 5 || dataMembers[i] instanceof BoundedRangeStatisticImpl)) {
					BoundedRangeStatisticImpl data = (BoundedRangeStatisticImpl) dataMembers[i];
					rtResult.put(dataMembers[i].getName(), String.valueOf(data.getCurrent()));
				} else {
					System.out.println("get Value is err");
				}
			}
		}
		return rtResult;
	}

	public WSStats collectStatsAttribute(ObjectName oname) {
		try {

			return (WSStats) adminClnt.getAttribute(oname, "stats");
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public Hashtable getValueForOBJAttrs(ObjectName on) {
		try {
			Hashtable retnResult = new Hashtable();
			ObjectName testInfo = null;
			testInfo = on;
			MBeanInfo nodeInfo = adminClnt.getMBeanInfo(testInfo);
			MBeanAttributeInfo[] nodeAttrs = nodeInfo.getAttributes();
			for (int i = 0; i < nodeAttrs.length; i++) {
				retnResult.put(nodeAttrs[i].getName(), String.valueOf(adminClnt.getAttribute(testInfo, nodeAttrs[i].getName())));
			}
			return retnResult;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// ȡWas������
	public Hashtable collectWasData(String ip, int port, String nodeName, String serverName, Hashtable gatherhash) {
		Hashtable rValue = new Hashtable();

		Hashtable jvmHst = new Hashtable();
		Hashtable JDBCProviderHst = new Hashtable();
		Hashtable SessionManagerHst = new Hashtable();
		Hashtable SystemMetricsHst = new Hashtable();
		Hashtable DynaCacheHst = new Hashtable();
		Hashtable TransactionServiceHst = new Hashtable();
		Hashtable ORBHst = new Hashtable();
		java.util.Properties props = new java.util.Properties();
		props.put(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
		props.put(AdminClient.CONNECTOR_HOST, ip);
		props.put(AdminClient.CONNECTOR_PORT, port + "");
		AdminClient adminClient = null;
		try {
			adminClient = AdminClientFactory.createAdminClient(props);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (gatherhash.containsKey("jvm")) {
			try {
				/**
				 * Author by QuZhi ����ȡ����ָ��ģ������
				 * JVM/JDBCProvider/SessionManager/SystemMetrics/ThreadPool/DynaCache/TransactionService/ObjectPool/ORB
				 * 
				 * @return
				 */
				Set set = adminClient.queryNames(new ObjectName("WebSphere:*,type=JVM"), null);
				Iterator it = set.iterator();
				ObjectName on = null;
				while (it.hasNext()) {
					on = (ObjectName) it.next();
					// ȡָ��
					try {
						AdminClient5 admin5 = new AdminClient5();
						ObjectName perfMbean = null;
						admin5.adminClnt = adminClient;
						perfMbean = admin5.getMbean(nodeName, serverName, "Perf");
						if (perfMbean == null) {
						}
						WSStats wsmyStats = admin5.collectStatsViaPerfMbeanWS(perfMbean, on, false);
						Hashtable out_pram12 = admin5.dispStats(wsmyStats);
						Iterator valueIt12 = out_pram12.keySet().iterator();
						while (valueIt12.hasNext()) {
							String key = (String) valueIt12.next();
							jvmHst.put(key, out_pram12.get(key));
						}
						int usedMem = (int) Float.parseFloat((String) jvmHst.get("UsedMemory"));
						int totalMem = (int) Float.parseFloat((String) jvmHst.get("HeapSize"));

						int memPer = 0;
						if (totalMem != 0)
							memPer = usedMem * 100 / totalMem;
						jvmHst.put("memPer", memPer);
						Interfacecollectdata hostdata = new Interfacecollectdata();
						hostdata.setIpaddress(ip);
						Calendar date = Calendar.getInstance();
						hostdata.setCollecttime(date);
						hostdata.setCategory("WasJVM");
						hostdata.setEntity("Utilization");
						hostdata.setSubentity("jvm");
						hostdata.setRestype("dynamic");
						hostdata.setUnit("%");
						hostdata.setThevalue(Math.round(memPer) + "");
						WasConfigDao wasconfigdao = new WasConfigDao();
						try {
							wasconfigdao.createHostData(hostdata, "wasjvm");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							wasconfigdao.close();
						}

					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (gatherhash.containsKey("jdbc")) {
			try {
				/**
				 * Author by QuZhi ����ȡ����ָ��ģ������
				 * JVM/JDBCProvider/SessionManager/SystemMetrics/ThreadPool/DynaCache/TransactionService/ObjectPool/ORB
				 * 
				 * @return
				 */
				Set set = adminClient.queryNames(new ObjectName("WebSphere:*,type=JDBCProvider"), null);
				Iterator it = set.iterator();
				ObjectName on = null;
				while (it.hasNext()) {
					on = (ObjectName) it.next();
					// ȡָ��
					try {
						AdminClient5 admin5 = new AdminClient5();
						ObjectName perfMbean = null;
						admin5.adminClnt = adminClient;
						perfMbean = admin5.getMbean(nodeName, serverName, "Perf");
						if (perfMbean == null) {
						}
						WSStats wsmyStats = admin5.collectStatsViaPerfMbeanWS(perfMbean, on, false);
						Hashtable out_pram12 = admin5.dispStats(wsmyStats);
						Iterator valueIt12 = out_pram12.keySet().iterator();
						while (valueIt12.hasNext()) {
							String key = (String) valueIt12.next();
							JDBCProviderHst.put(key, out_pram12.get(key));
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

			} catch (Exception e) {

				e.printStackTrace();
			}
		}

		if (gatherhash.containsKey("session")) {
			try {
				/**
				 * Author by QuZhi ����ȡ����ָ��ģ������
				 * JVM/JDBCProvider/SessionManager/SystemMetrics/ThreadPool/DynaCache/TransactionService/ObjectPool/ORB
				 * 
				 * @return
				 */
				Set set = adminClient.queryNames(new ObjectName("WebSphere:*,type=SessionManager"), null);
				Iterator it = set.iterator();
				ObjectName on = null;
				while (it.hasNext()) {
					on = (ObjectName) it.next();
					// ȡָ��
					try {
						AdminClient5 admin5 = new AdminClient5();
						ObjectName perfMbean = null;
						admin5.adminClnt = adminClient;
						perfMbean = admin5.getMbean(nodeName, serverName, "Perf");
						if (perfMbean == null) {
						}
						WSStats wsmyStats = admin5.collectStatsViaPerfMbeanWS(perfMbean, on, false);
						Hashtable out_pram12 = admin5.dispStats(wsmyStats);
						Iterator valueIt12 = out_pram12.keySet().iterator();
						while (valueIt12.hasNext()) {
							String key = (String) valueIt12.next();
							SessionManagerHst.put(key, out_pram12.get(key));
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

			} catch (Exception e) {

				e.printStackTrace();
			}
		}

		if (gatherhash.containsKey("system")) {
			try {
				/**
				 * Author by QuZhi ����ȡ����ָ��ģ������
				 * JVM/JDBCProvider/SessionManager/SystemMetrics/ThreadPool/DynaCache/TransactionService/ObjectPool/ORB
				 * 
				 * @return
				 */
				Set set = adminClient.queryNames(new ObjectName("WebSphere:*,type=SystemMetrics"), null);
				Iterator it = set.iterator();
				ObjectName on = null;
				while (it.hasNext()) {
					on = (ObjectName) it.next();
					// ȡָ��
					try {
						AdminClient5 admin5 = new AdminClient5();
						ObjectName perfMbean = null;
						admin5.adminClnt = adminClient;
						perfMbean = admin5.getMbean(nodeName, serverName, "Perf");
						if (perfMbean == null) {
						}
						WSStats wsmyStats = admin5.collectStatsViaPerfMbeanWS(perfMbean, on, false);
						Hashtable out_pram12 = admin5.dispStats(wsmyStats);
						Iterator valueIt12 = out_pram12.keySet().iterator();
						while (valueIt12.hasNext()) {
							String key = (String) valueIt12.next();
							SystemMetricsHst.put(key, out_pram12.get(key));
						}

					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				String cpuusage = (String) SystemMetricsHst.get("CPUUsageSinceServerStarted");
				int cpuage = 0;
				try {
					cpuage = Integer.parseInt(cpuusage);
				} catch (Exception e) {
				}
				int cpu_percent = cpuage / 100;

				int scpu_percent = cpuage / 100;
				Interfacecollectdata hostdata = new Interfacecollectdata();
				hostdata.setIpaddress(ip);
				Calendar date = Calendar.getInstance();
				hostdata.setCollecttime(date);
				hostdata.setCategory("WasrCpu");
				hostdata.setEntity("Utilization");
				hostdata.setSubentity("RunCPU");
				hostdata.setRestype("dynamic");
				hostdata.setUnit("%");
				hostdata.setThevalue(Math.round(cpu_percent) + "");
				WasConfigDao wasconfigdao = new WasConfigDao();
				try {
					wasconfigdao.createHostData(hostdata, "wasrcpu");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					wasconfigdao.close();
				}

				hostdata = new Interfacecollectdata();
				hostdata.setIpaddress(ip);
				hostdata.setCollecttime(date);
				hostdata.setCategory("WassCpu");
				hostdata.setEntity("Utilization");
				hostdata.setSubentity("SelCPU");
				hostdata.setRestype("dynamic");
				hostdata.setUnit("%");
				hostdata.setThevalue(Math.round(scpu_percent) + "");
				wasconfigdao = new WasConfigDao();
				try {
					wasconfigdao.createHostData(hostdata, "wasscpu");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					wasconfigdao.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (gatherhash.containsKey("cache")) {
			try {
				/**
				 * Author by QuZhi ����ȡ����ָ��ģ������
				 * JVM/JDBCProvider/SessionManager/SystemMetrics/ThreadPool/DynaCache/TransactionService/ORB
				 * 
				 * @return /ObjectPool
				 */
				Set set = adminClient.queryNames(new ObjectName("WebSphere:*,type=DynaCache"), null);
				// System.out.println(test.size());
				Iterator it = set.iterator();
				ObjectName on = null;
				while (it.hasNext()) {
					on = (ObjectName) it.next();
					// ȡָ��
					try {
						AdminClient5 admin5 = new AdminClient5();
						ObjectName perfMbean = null;
						admin5.adminClnt = adminClient;
						perfMbean = admin5.getMbean(nodeName, serverName, "Perf");
						if (perfMbean == null) {
						}
						WSStats wsmyStats = admin5.collectStatsViaPerfMbeanWS(perfMbean, on, false);
						Hashtable out_pram12 = admin5.dispStats(wsmyStats);
						Iterator valueIt12 = out_pram12.keySet().iterator();
						while (valueIt12.hasNext()) {
							String key = (String) valueIt12.next();
							DynaCacheHst.put(key, out_pram12.get(key));
						}

					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (gatherhash.containsKey("service")) {
			try {
				/**
				 * Author by QuZhi ����ȡ����ָ��ģ������
				 * JVM/JDBCProvider/SessionManager/SystemMetrics/ThreadPool/DynaCache/TransactionService/ObjectPool/ORB
				 * 
				 * @return
				 */
				Set set = adminClient.queryNames(new ObjectName("WebSphere:*,type=TransactionService"), null);
				Iterator it = set.iterator();
				ObjectName on = null;
				while (it.hasNext()) {
					on = (ObjectName) it.next();
					// ȡָ��
					try {
						AdminClient5 admin5 = new AdminClient5();
						ObjectName perfMbean = null;
						admin5.adminClnt = adminClient;
						perfMbean = admin5.getMbean(nodeName, serverName, "Perf");
						if (perfMbean == null) {
						}
						WSStats wsmyStats = admin5.collectStatsViaPerfMbeanWS(perfMbean, on, false);
						Hashtable out_pram12 = admin5.dispStats(wsmyStats);
						Iterator valueIt12 = out_pram12.keySet().iterator();
						while (valueIt12.hasNext()) {
							String key = (String) valueIt12.next();
							TransactionServiceHst.put(key, out_pram12.get(key));
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (gatherhash.containsKey("orb")) {
			try {
				/**
				 * Author by QuZhi ����ȡ����ָ��ģ������
				 * JVM/JDBCProvider/SessionManager/SystemMetrics/ThreadPool/DynaCache/TransactionService/ObjectPool/ORB
				 * 
				 * @return
				 */
				Set set = adminClient.queryNames(new ObjectName("WebSphere:*,type=ORB"), null);
				Iterator it = set.iterator();
				ObjectName on = null;
				while (it.hasNext()) {
					on = (ObjectName) it.next();
					// ȡָ��
					try {
						AdminClient5 admin5 = new AdminClient5();
						ObjectName perfMbean = null;
						admin5.adminClnt = adminClient;
						perfMbean = admin5.getMbean(nodeName, serverName, "Perf");
						if (perfMbean == null) {
						}
						WSStats wsmyStats = admin5.collectStatsViaPerfMbeanWS(perfMbean, on, false);
						Hashtable out_pram12 = admin5.dispStats(wsmyStats);
						Iterator valueIt12 = out_pram12.keySet().iterator();
						while (valueIt12.hasNext()) {
							String key = (String) valueIt12.next();
							ORBHst.put(key, out_pram12.get(key));
						}

					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

			} catch (Exception e) {

				e.printStackTrace();
			}
		}
		// return jvmHst;
		rValue.put("JvmHst", jvmHst);
		rValue.put("JDBCProviderHst", JDBCProviderHst);
		rValue.put("SessionManagerHst", SessionManagerHst);
		rValue.put("SystemMetricsHst", SystemMetricsHst);
		rValue.put("DynaCacheHst", DynaCacheHst);
		rValue.put("TransactionServiceHst", TransactionServiceHst);
		rValue.put("ORBHst", ORBHst);
		return rValue;
	}

	// ȡWas������
	public Hashtable collectJvmData(String ip, int port, String nodeName, String serverName) {
		Hashtable jvmHst = new Hashtable();
		/*	*/
		java.util.Properties props = new java.util.Properties();
		props.put(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
		props.put(AdminClient.CONNECTOR_HOST, ip);
		props.put(AdminClient.CONNECTOR_PORT, port + "");
		AdminClient adminClient = null;
		try {
			adminClient = AdminClientFactory.createAdminClient(props);
		} catch (Exception e) {

			e.printStackTrace();
		}
		try {
			/**
			 * Author by QuZhi ����ȡ����ָ��ģ������
			 * JVM/JDBCProvider/SessionManager/SystemMetrics/ThreadPool/DynaCache/TransactionService/ObjectPool/ORB
			 * 
			 * @return
			 */
			Set set = adminClient.queryNames(new ObjectName("WebSphere:*,type=JVM"), null);
			Iterator it = set.iterator();
			ObjectName on = null;
			while (it.hasNext()) {
				on = (ObjectName) it.next();
				// ȡָ��
				try {
					AdminClient5 admin5 = new AdminClient5();
					ObjectName perfMbean = null;
					admin5.adminClnt = adminClient;
					perfMbean = admin5.getMbean(nodeName, serverName, "Perf");
					if (perfMbean == null) {
					}
					WSStats wsmyStats = admin5.collectStatsViaPerfMbeanWS(perfMbean, on, false);
					Hashtable out_pram12 = admin5.dispStats(wsmyStats);
					Iterator valueIt12 = out_pram12.keySet().iterator();
					while (valueIt12.hasNext()) {
						String key = (String) valueIt12.next();
						jvmHst.put(key, out_pram12.get(key));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
		return jvmHst;

	}

	public Hashtable collectJDBCProviderData(String ip, int port, String nodeName, String serverName) {
		Hashtable JDBCProviderHst = new Hashtable();
		java.util.Properties props = new java.util.Properties();
		props.put(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
		props.put(AdminClient.CONNECTOR_HOST, ip);
		props.put(AdminClient.CONNECTOR_PORT, port + "");
		AdminClient adminClient = null;
		try {
			adminClient = AdminClientFactory.createAdminClient(props);
		} catch (Exception e) {

			e.printStackTrace();
		}
		try {
			/**
			 * Author by QuZhi ����ȡ����ָ��ģ������
			 * JVM/JDBCProvider/SessionManager/SystemMetrics/ThreadPool/DynaCache/TransactionService/ObjectPool/ORB
			 * 
			 * @return
			 */
			Set set = adminClient.queryNames(new ObjectName("WebSphere:*,type=JDBCProvider"), null);
			Iterator it = set.iterator();
			ObjectName on = null;
			while (it.hasNext()) {
				on = (ObjectName) it.next();
				// ȡָ��
				try {
					AdminClient5 admin5 = new AdminClient5();
					ObjectName perfMbean = null;
					admin5.adminClnt = adminClient;
					perfMbean = admin5.getMbean(nodeName, serverName, "Perf");
					if (perfMbean == null) {
					}
					WSStats wsmyStats = admin5.collectStatsViaPerfMbeanWS(perfMbean, on, false);
					Hashtable out_pram12 = admin5.dispStats(wsmyStats);
					Iterator valueIt12 = out_pram12.keySet().iterator();
					while (valueIt12.hasNext()) {
						String key = (String) valueIt12.next();
						JDBCProviderHst.put(key, out_pram12.get(key));
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return JDBCProviderHst;

	}

	public Hashtable collectSessionManagerData(String ip, int port, String nodeName, String serverName) {
		Hashtable SessionManagerHst = new Hashtable();
		java.util.Properties props = new java.util.Properties();
		props.put(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
		props.put(AdminClient.CONNECTOR_HOST, ip);
		props.put(AdminClient.CONNECTOR_PORT, port + "");
		AdminClient adminClient = null;
		try {
			adminClient = AdminClientFactory.createAdminClient(props);
		} catch (Exception e) {

			e.printStackTrace();
		}
		try {
			/**
			 * Author by QuZhi ����ȡ����ָ��ģ������
			 * JVM/JDBCProvider/SessionManager/SystemMetrics/ThreadPool/DynaCache/TransactionService/ObjectPool/ORB
			 * 
			 * @return
			 */
			Set set = adminClient.queryNames(new ObjectName("WebSphere:*,type=SessionManager"), null);
			Iterator it = set.iterator();
			ObjectName on = null;
			while (it.hasNext()) {
				on = (ObjectName) it.next();
				// ȡָ��
				try {
					AdminClient5 admin5 = new AdminClient5();
					ObjectName perfMbean = null;
					admin5.adminClnt = adminClient;
					perfMbean = admin5.getMbean(nodeName, serverName, "Perf");
					if (perfMbean == null) {
					}
					WSStats wsmyStats = admin5.collectStatsViaPerfMbeanWS(perfMbean, on, false);
					Hashtable out_pram12 = admin5.dispStats(wsmyStats);
					Iterator valueIt12 = out_pram12.keySet().iterator();
					while (valueIt12.hasNext()) {
						String key = (String) valueIt12.next();
						SessionManagerHst.put(key, out_pram12.get(key));
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
		return SessionManagerHst;

	}

	public Hashtable collectSystemMetricsData(String ip, int port, String nodeName, String serverName) {
		Hashtable SystemMetricsHst = new Hashtable();
		java.util.Properties props = new java.util.Properties();
		props.put(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
		props.put(AdminClient.CONNECTOR_HOST, ip);
		props.put(AdminClient.CONNECTOR_PORT, port + "");
		AdminClient adminClient = null;
		try {
			adminClient = AdminClientFactory.createAdminClient(props);
		} catch (Exception e) {

			e.printStackTrace();
		}
		try {
			/**
			 * Author by QuZhi ����ȡ����ָ��ģ������
			 * JVM/JDBCProvider/SessionManager/SystemMetrics/ThreadPool/DynaCache/TransactionService/ObjectPool/ORB
			 * 
			 * @return
			 */
			Set set = adminClient.queryNames(new ObjectName("WebSphere:*,type=SystemMetrics"), null);
			Iterator it = set.iterator();
			ObjectName on = null;
			while (it.hasNext()) {
				on = (ObjectName) it.next();
				// ȡָ��
				try {
					AdminClient5 admin5 = new AdminClient5();
					ObjectName perfMbean = null;
					admin5.adminClnt = adminClient;
					perfMbean = admin5.getMbean(nodeName, serverName, "Perf");
					if (perfMbean == null) {
					}
					WSStats wsmyStats = admin5.collectStatsViaPerfMbeanWS(perfMbean, on, false);
					Hashtable out_pram12 = admin5.dispStats(wsmyStats);
					Iterator valueIt12 = out_pram12.keySet().iterator();
					while (valueIt12.hasNext()) {
						String key = (String) valueIt12.next();
						SystemMetricsHst.put(key, out_pram12.get(key));
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return SystemMetricsHst;

	}

	public Hashtable collectThreadPoolData(String ip, int port, String nodeName, String serverName) {
		Hashtable ThreadPoolHst = new Hashtable();
		java.util.Properties props = new java.util.Properties();
		props.put(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
		props.put(AdminClient.CONNECTOR_HOST, ip);
		props.put(AdminClient.CONNECTOR_PORT, port + "");
		AdminClient adminClient = null;
		try {
			adminClient = AdminClientFactory.createAdminClient(props);
		} catch (Exception e) {

			e.printStackTrace();
		}
		try {
			/**
			 * Author by QuZhi ����ȡ����ָ��ģ������
			 * JVM/JDBCProvider/SessionManager/SystemMetrics/ThreadPool/DynaCache/TransactionService/ObjectPool/ORB
			 * 
			 * @return
			 */
			Set set = adminClient.queryNames(new ObjectName("WebSphere:*,type=ThreadPool"), null);
			Iterator it = set.iterator();
			ObjectName on = null;
			while (it.hasNext()) {
				on = (ObjectName) it.next();
				// ȡָ��
				try {
					AdminClient5 admin5 = new AdminClient5();
					ObjectName perfMbean = null;
					admin5.adminClnt = adminClient;
					perfMbean = admin5.getMbean(nodeName, serverName, "Perf");
					if (perfMbean == null) {
					}
					WSStats wsmyStats = admin5.collectStatsViaPerfMbeanWS(perfMbean, on, false);
					Hashtable out_pram12 = admin5.dispStats(wsmyStats);
					Iterator valueIt12 = out_pram12.keySet().iterator();
					while (valueIt12.hasNext()) {
						String key = (String) valueIt12.next();
						ThreadPoolHst.put(key, out_pram12.get(key));
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
		return ThreadPoolHst;

	}

	public Hashtable collectDynaCacheData(String ip, int port, String nodeName, String serverName) {
		Hashtable DynaCacheHst = new Hashtable();
		java.util.Properties props = new java.util.Properties();
		props.put(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
		props.put(AdminClient.CONNECTOR_HOST, ip);
		props.put(AdminClient.CONNECTOR_PORT, port + "");
		AdminClient adminClient = null;
		try {
			adminClient = AdminClientFactory.createAdminClient(props);
		} catch (Exception e) {

			e.printStackTrace();
		}
		try {
			/**
			 * Author by QuZhi ����ȡ����ָ��ģ������
			 * JVM/JDBCProvider/SessionManager/SystemMetrics/ThreadPool/DynaCache/TransactionService/ORB
			 * 
			 * @return /ObjectPool
			 */
			Set set = adminClient.queryNames(new ObjectName("WebSphere:*,type=DynaCache"), null);
			Iterator it = set.iterator();
			ObjectName on = null;
			while (it.hasNext()) {
				on = (ObjectName) it.next();
				// ȡָ��
				try {
					AdminClient5 admin5 = new AdminClient5();
					ObjectName perfMbean = null;
					admin5.adminClnt = adminClient;
					perfMbean = admin5.getMbean(nodeName, serverName, "Perf");
					if (perfMbean == null) {
					}
					WSStats wsmyStats = admin5.collectStatsViaPerfMbeanWS(perfMbean, on, false);
					Hashtable out_pram12 = admin5.dispStats(wsmyStats);
					Iterator valueIt12 = out_pram12.keySet().iterator();
					while (valueIt12.hasNext()) {
						String key = (String) valueIt12.next();
						DynaCacheHst.put(key, out_pram12.get(key));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
		return DynaCacheHst;

	}

	public Hashtable collectTransactionServiceData(String ip, int port, String nodeName, String serverName) {
		Hashtable TransactionServiceHst = new Hashtable();
		java.util.Properties props = new java.util.Properties();
		props.put(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
		props.put(AdminClient.CONNECTOR_HOST, ip);
		props.put(AdminClient.CONNECTOR_PORT, port + "");
		AdminClient adminClient = null;
		try {
			adminClient = AdminClientFactory.createAdminClient(props);
		} catch (Exception e) {

			e.printStackTrace();
		}
		try {
			/**
			 * Author by QuZhi ����ȡ����ָ��ģ������
			 * JVM/JDBCProvider/SessionManager/SystemMetrics/ThreadPool/DynaCache/TransactionService/ObjectPool/ORB
			 * 
			 * @return
			 */
			Set set = adminClient.queryNames(new ObjectName("WebSphere:*,type=TransactionService"), null);
			Iterator it = set.iterator();
			ObjectName on = null;
			while (it.hasNext()) {
				on = (ObjectName) it.next();
				// ȡָ��
				try {
					AdminClient5 admin5 = new AdminClient5();
					ObjectName perfMbean = null;
					admin5.adminClnt = adminClient;
					perfMbean = admin5.getMbean(nodeName, serverName, "Perf");
					if (perfMbean == null) {
					}
					WSStats wsmyStats = admin5.collectStatsViaPerfMbeanWS(perfMbean, on, false);
					Hashtable out_pram12 = admin5.dispStats(wsmyStats);
					Iterator valueIt12 = out_pram12.keySet().iterator();
					while (valueIt12.hasNext()) {
						String key = (String) valueIt12.next();
						TransactionServiceHst.put(key, out_pram12.get(key));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
		return TransactionServiceHst;

	}

	public Hashtable collectORBData(String ip, int port, String nodeName, String serverName) {
		Hashtable ORBHst = new Hashtable();
		java.util.Properties props = new java.util.Properties();
		props.put(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
		props.put(AdminClient.CONNECTOR_HOST, ip);
		props.put(AdminClient.CONNECTOR_PORT, port + "");
		AdminClient adminClient = null;
		try {
			adminClient = AdminClientFactory.createAdminClient(props);
		} catch (Exception e) {

			e.printStackTrace();
		}
		try {
			/**
			 * Author by QuZhi ����ȡ����ָ��ģ������
			 * JVM/JDBCProvider/SessionManager/SystemMetrics/ThreadPool/DynaCache/TransactionService/ObjectPool/ORB
			 * 
			 * @return
			 */
			Set set = adminClient.queryNames(new ObjectName("WebSphere:*,type=ORB"), null);
			Iterator it = set.iterator();
			ObjectName on = null;
			while (it.hasNext()) {
				on = (ObjectName) it.next();
				// ȡָ��
				try {
					AdminClient5 admin5 = new AdminClient5();
					ObjectName perfMbean = null;
					admin5.adminClnt = adminClient;
					perfMbean = admin5.getMbean(nodeName, serverName, "Perf");
					if (perfMbean == null) {
					}
					WSStats wsmyStats = admin5.collectStatsViaPerfMbeanWS(perfMbean, on, false);
					Hashtable out_pram12 = admin5.dispStats(wsmyStats);
					Iterator valueIt12 = out_pram12.keySet().iterator();
					while (valueIt12.hasNext()) {
						String key = (String) valueIt12.next();
						ORBHst.put(key, out_pram12.get(key));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
		return ORBHst;

	}

}
