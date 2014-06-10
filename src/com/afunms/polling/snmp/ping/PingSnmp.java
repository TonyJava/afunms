package com.afunms.polling.snmp.ping;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.PingInfoParser;
import com.afunms.common.util.PingUtil;
import com.afunms.common.util.ShareData;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.polling.telnet.SSHWrapper;
import com.afunms.polling.telnet.TelnetWrapper;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.RemotePingHostDao;
import com.afunms.topology.dao.RemotePingNodeDao;
import com.afunms.topology.model.ConnectTypeConfig;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.RemotePingHost;
import com.afunms.topology.model.RemotePingNode;
import com.gatherResulttosql.HostnetPingResultTosql;
import com.gatherResulttosql.NetHostPingdatatempRtosql;
import com.gatherResulttosql.StoragePingResultTosql;

@SuppressWarnings("unchecked")
public class PingSnmp {
	private Hashtable connectConfigHashtable = new Hashtable();

	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {

		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));

		Hashtable returnhash = new Hashtable();
		HostCollectDataManager hostdataManager = new HostCollectDataManager();
		Vector vector = null;

		if (node == null)
			return returnhash;
		connectConfigHashtable = (Hashtable) ShareData.getConnectConfigHashtable().get("connectConfigHashtable");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Node host = (Host) PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
		Calendar _date = Calendar.getInstance();
		Date ccc = _date.getTime();
		String times = sdf.format(ccc);
		host.setLastTime(times);

		try {
			/*
			 * 先判断是否为remoteping的主服务器,若是,则除了进行PING之外,还要对下联的子节点进行REMOTEPING
			 */
			if (node.getEndpoint() == 1) {
				PingUtil pingU = new PingUtil(node.getIpAddress());
				Integer[] packet = pingU.ping();
				vector = pingU.addhis(packet);
				if (vector != null) {
					hostdataManager.createHostData(vector, alarmIndicatorsNode);
					ShareData.setPingdata(node.getIpAddress(), vector);
					returnhash.put("ping", vector);
				}
				vector = null;

				// 需要对其下属的子节点进行PING操作
				List list = null;
				RemotePingNodeDao remotePingNodeDao = new RemotePingNodeDao();
				try {
					list = remotePingNodeDao.findByNodeId(String.valueOf(node.getId()));
				} catch (RuntimeException e) {
					e.printStackTrace();
				} finally {
					remotePingNodeDao.close();
					remotePingNodeDao = null;
				}
				HostNodeDao hostNodeDao = new HostNodeDao();
				try {
					RemotePingHost remotePingHost = null;
					RemotePingHostDao remotePingHostDao = new RemotePingHostDao();
					try {
						remotePingHost = remotePingHostDao.findByNodeId(String.valueOf(node.getId()));
					} catch (RuntimeException e1) {
						e1.printStackTrace();
					} finally {
						remotePingHostDao.close();
						remotePingHostDao = null;
					}
					TelnetWrapper telnet = new TelnetWrapper();
					try {
						telnet.connect(node.getIpAddress(), 23);
						telnet.login(remotePingHost.getUsername(), remotePingHost.getPassword(), remotePingHost.getLoginPrompt(), remotePingHost.getPasswordPrompt(),
								remotePingHost.getShellPrompt());
						// 运行任务
						for (int i = 0; i < list.size(); i++) {
							String result = "";
							RemotePingNode remotePingNode = (RemotePingNode) list.get(i);
							HostNode hostNodeTemp = (HostNode) hostNodeDao.findByID(remotePingNode.getChildNodeId());
							result = telnet.send("ping " + hostNodeTemp.getIpAddress());
							setData(result, hostNodeTemp, alarmIndicatorsNode);
						}
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						telnet.disconnect();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					hostNodeDao.close();
					hostNodeDao = null;

				}
			} else if (node.getEndpoint() == 2) {
				// 若为REMOTEPING子节点，则返回
				// return;
			} else {
				// 正常的设备
				if (connectConfigHashtable.containsKey(node.getId() + "")) {
					// 若存在连通性检测配置,为只通过TELNET或SSH检测连通性
					ConnectTypeConfig connectTypeConfig = (ConnectTypeConfig) connectConfigHashtable.get(node.getId() + "");
					if ("telnet".equalsIgnoreCase(connectTypeConfig.getConnecttype())) {
						// 用TELNET方式进行检测
						PingCollectEntity hostdata = null;
						Calendar date = Calendar.getInstance();
						hostdata = new PingCollectEntity();
						hostdata.setIpaddress(node.getIpAddress());
						hostdata.setCollecttime(date);
						hostdata.setCategory("Ping");
						hostdata.setEntity("Utilization");
						hostdata.setSubentity("ConnectUtilization");
						hostdata.setRestype("dynamic");
						hostdata.setUnit("%");
						int flag = 0;
						Vector<PingCollectEntity> _vector = new Vector<PingCollectEntity>();
						TelnetWrapper telnet = new TelnetWrapper();
						long starttime = 0;
						long endtime = 0;
						long condelay = 0;
						try {
							starttime = System.currentTimeMillis();
							telnet.connect(node.getIpAddress(), 23);
							telnet.login(connectTypeConfig.getUsername(), EncryptUtil.decode(connectTypeConfig.getPassword()), connectTypeConfig.getLoginPrompt(),
									connectTypeConfig.getPasswordPrompt(), connectTypeConfig.getShellPrompt());
							endtime = System.currentTimeMillis();
						} catch (Exception e) {
							endtime = System.currentTimeMillis();
							e.printStackTrace();
							flag = 1;
						} finally {
							try {
								telnet.disconnect();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						condelay = endtime - starttime;
						if (flag == 0) {
							hostdata.setThevalue("100");
						} else {
							hostdata.setThevalue("0");
						}
						_vector.add(0, hostdata);

						// 响应时间
						hostdata = new PingCollectEntity();
						hostdata.setIpaddress(node.getIpAddress());
						hostdata.setCollecttime(date);
						hostdata.setCategory("Ping");
						hostdata.setEntity("ResponseTime");
						hostdata.setSubentity("ResponseTime");
						hostdata.setRestype("dynamic");
						hostdata.setUnit("毫秒");
						hostdata.setThevalue(condelay + "");
						_vector.add(1, hostdata);
						if (_vector != null) {
							hostdataManager.createHostData(vector, alarmIndicatorsNode);
							ShareData.setPingdata(node.getIpAddress(), _vector);
							returnhash.put("ping", _vector);
						}
						vector = null;
					} else if ("ssh".equalsIgnoreCase(connectTypeConfig.getConnecttype())) {
						// 用SSH方式进行检测
						PingCollectEntity hostdata = null;
						Calendar date = Calendar.getInstance();
						hostdata = new PingCollectEntity();
						hostdata.setIpaddress(node.getIpAddress());
						hostdata.setCollecttime(date);
						hostdata.setCategory("Ping");
						hostdata.setEntity("Utilization");
						hostdata.setSubentity("ConnectUtilization");
						hostdata.setRestype("dynamic");
						hostdata.setUnit("%");

						int nodeId = node.getId();

						RemotePingHostDao hostDao = new RemotePingHostDao();
						RemotePingHost params = hostDao.findByNodeId(String.valueOf(nodeId));
						hostDao.close();

						int flag = 0;
						Vector<PingCollectEntity> _vector = new Vector<PingCollectEntity>();
						SSHWrapper ssh = new SSHWrapper();
						long starttime = 0;
						long endtime = 0;
						long condelay = 0;
						try {
							starttime = System.currentTimeMillis();
							ssh.connect(node.getIpAddress(), 22, params.getUsername(), params.getPassword());
							endtime = System.currentTimeMillis();
						} catch (Exception e) {
							endtime = System.currentTimeMillis();
							e.printStackTrace();
							flag = 1;
						} finally {
							try {
								ssh.disconnect();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						condelay = endtime - starttime;
						if (flag == 0) {
							hostdata.setThevalue("100");
						} else {
							hostdata.setThevalue("0");
						}
						_vector.add(0, hostdata);

						// 响应时间
						hostdata = new PingCollectEntity();
						hostdata.setIpaddress(node.getIpAddress());
						hostdata.setCollecttime(date);
						hostdata.setCategory("Ping");
						hostdata.setEntity("ResponseTime");
						hostdata.setSubentity("ResponseTime");
						hostdata.setRestype("dynamic");
						hostdata.setUnit("毫秒");
						hostdata.setThevalue(condelay + "");
						_vector.add(1, hostdata);
						if (_vector != null) {
							hostdataManager.createHostData(_vector, alarmIndicatorsNode);
							ShareData.setPingdata(node.getIpAddress(), _vector);
							returnhash.put("ping", _vector);
						}
						_vector = null;
					} else {
						// 其他方式可以在这里扩展
					}
				} else {
					// 通过PING操作进行连通性检测
					PingUtil pingU = new PingUtil(node.getIpAddress());
					Integer[] packet = pingU.ping();
					try {
						vector = pingU.addhis(packet);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (vector != null) {
						ShareData.setPingdata(node.getIpAddress(), vector);
						returnhash.put("ping", vector);
					}

					vector = null;
				}

			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}

		// 把数据转换成sql HP存储执行普通ping操作
		if (host.getCategory() == 14 && host.getOstype() != 44) {
			// 存储
			StoragePingResultTosql tosql = new StoragePingResultTosql();
			tosql.CreateResultTosql(returnhash, node.getIpAddress());
		} else {
			HostnetPingResultTosql tosql = new HostnetPingResultTosql();
			tosql.CreateResultTosql(returnhash, node.getIpAddress());
		}

		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		if (!"0".equals(runmodel)) {
			// 采集与访问是分离模式,则不需要将监视数据写入临时表格
			NetHostPingdatatempRtosql totempsql = new NetHostPingdatatempRtosql();
			totempsql.CreateResultTosql(returnhash, node);
		}

		// 对PING值进行告警检测
		if (returnhash != null && returnhash.size() > 0) {
			Vector pingvector = (Vector) returnhash.get("ping");
			if (pingvector != null) {
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				for (int i = 0; i < pingvector.size(); i++) {
					PingCollectEntity pingdata = (PingCollectEntity) pingvector.elementAt(i);
					if (pingdata.getSubentity().equalsIgnoreCase("ConnectUtilization")) {
						// 连通率进行判断
						List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), alarmIndicatorsNode.getType(), "");
						for (int m = 0; m < list.size(); m++) {
							AlarmIndicatorsNode _alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(m);
							if ("1".equals(_alarmIndicatorsNode.getEnabled())) {
								if (_alarmIndicatorsNode.getName().equalsIgnoreCase("ping")) {
									CheckEventUtil checkeventutil = new CheckEventUtil();
									checkeventutil.checkEvent(node, _alarmIndicatorsNode, pingdata.getThevalue());
								}
							}
						}

					}
				}
			}

			pingvector = null;
		}

		return returnhash;
	}

	private static void setData(String result, HostNode hostnode, NodeGatherIndicators alarmIndicatorsNode) {
		Vector vector = null;
		int[] pingresult;
		if (result != null && result.length() > 0) {
			HostCollectDataManager hostdataManager = new HostCollectDataManager();
			pingresult = PingInfoParser.parsePingInfo(result);
			if (pingresult != null) {
				Integer[] integer = new Integer[pingresult.length];
				for (int i = 0; i < pingresult.length; i++) {
					integer[i] = pingresult[i];
				}

				PingUtil pingU = new PingUtil(hostnode.getIpAddress());
				vector = pingU.addhis(integer);
				if (vector != null) {
					try {
						hostdataManager.createHostData(vector, alarmIndicatorsNode);
					} catch (Exception e) {
						e.printStackTrace();
					}
					ShareData.setPingdata(hostnode.getIpAddress(), vector);
				}
				vector = null;
			}
		}
	}
}