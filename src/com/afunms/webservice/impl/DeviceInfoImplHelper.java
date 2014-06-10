package com.afunms.webservice.impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.MQConfigDao;
import com.afunms.application.dao.TomcatDao;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.MQConfig;
import com.afunms.application.model.Tomcat;
import com.afunms.common.util.ShareData;
import com.afunms.config.dao.SupperDao;
import com.afunms.config.model.Supper;
import com.afunms.detail.net.service.NetService;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.DeviceCollectEntity;
import com.afunms.polling.om.FlashCollectEntity;
import com.afunms.polling.om.SoftwareCollectEntity;
import com.afunms.polling.om.SystemCollectEntity;
import com.afunms.temp.model.DeviceNodeTemp;
import com.afunms.temp.model.NodeTemp;
import com.afunms.temp.model.SoftwareNodeTemp;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.afunms.webservice.dao.RelationDao;
import com.afunms.webservice.model.DeviceCpuInfo;
import com.afunms.webservice.model.DeviceDiskInfo;
import com.afunms.webservice.model.DeviceFlashInfo;
import com.afunms.webservice.model.DeviceHardwareInfo;
import com.afunms.webservice.model.DeviceInterfaceGroup;
import com.afunms.webservice.model.DeviceInterfaceInfo;
import com.afunms.webservice.model.DeviceMemoryInfo;
import com.afunms.webservice.model.MoAndCiRelation;

@SuppressWarnings("unchecked")
public class DeviceInfoImplHelper {
	public DeviceInterfaceGroup getDeviceInterface(String ip) {
		DeviceInterfaceGroup group = new DeviceInterfaceGroup();
		DeviceInterfaceInfo info = null;
		List<DeviceInterfaceInfo> list = new ArrayList<DeviceInterfaceInfo>();
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		Vector vector = new Vector();
		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
		String[] netInterfaceItem = { "ifDescr", "ifType", "ifPhysAddress" };
		if (runmodel.equals("0")) {// //采集与访问是集成模式
			try {
				vector = hostlastmanager.getInterface_share(ip, netInterfaceItem, "index", null, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				vector = hostlastmanager.getInterface(ip, netInterfaceItem, "index", null, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (vector != null) {
			for (int i = 0; i < vector.size(); i++) {
				info = new DeviceInterfaceInfo();
				String[] strs = (String[]) vector.get(i);
				if (strs != null && strs.length > 0) {
					info.setIfName(strs[0]);
					info.setIfType((String) strs[1]);
					info.setIfPhysAddress((String) strs[2]);
					list.add(info);
				}
			}
			group.setIpAddress(ip);
			group.setGroup(list);
		}
		return group;
	}

	public DeviceHardwareInfo getDeviceHardwareInfo(String ip) {
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		DeviceHardwareInfo hardwareInfo = new DeviceHardwareInfo();
		DeviceCpuInfo cpuInfo = new DeviceCpuInfo();

		DeviceMemoryInfo memoryInfo = null;
		List<DeviceMemoryInfo> memoryList = new ArrayList<DeviceMemoryInfo>();

		DeviceDiskInfo diskInfo = null;
		List<DeviceDiskInfo> diskList = new ArrayList<DeviceDiskInfo>();

		DeviceFlashInfo flashInfo = null;
		List<DeviceFlashInfo> flashList = new ArrayList<DeviceFlashInfo>();
		Vector envvector = new Vector();

		List deviceList = new ArrayList();
		Vector deviceV = null;
		Hashtable memhash = new Hashtable();
		Hashtable diskhash = new Hashtable();
		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
		if ("0".equals(runmodel)) {
			// 采集与访问是集成模式
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
			deviceV = (Vector) ipAllData.get("device");

			try {
				memhash = hostlastmanager.getMemory_share(ip, "Memory", null, null);
				diskhash = hostlastmanager.getDisk_share(ip, "Disk", null, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (ipAllData != null) {
				// 得到闪存信息
				envvector = (Vector) ipAllData.get("flash");
				if (envvector != null && envvector.size() > 0) {
					for (int i = 0; i < envvector.size(); i++) {
						flashInfo = new DeviceFlashInfo();
						FlashCollectEntity nodetemp = (FlashCollectEntity) envvector.get(i);
						if (nodetemp != null) {
							String category = nodetemp.getCategory();
							if (category == null || !category.equals("Flash"))
								continue;
							flashInfo.setFlashName(nodetemp.getSubentity());
							flashList.add(flashInfo);
						}
					}
				}
			}
		} else {
			deviceV = new Vector();
			List envlist = new ArrayList();
			HostNodeDao dao = new HostNodeDao();
			HostNode node = null;
			try {
				node = dao.findByIpaddress(ip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

			Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);
			// CPU
			if (nodedto != null) {
				deviceList = new NetService(host.getId() + "", nodedto.getType(), nodedto.getSubtype()).getDeviceInfo();
				envlist = new NetService(host.getId() + "", nodedto.getType(), nodedto.getSubtype()).getCurrENVInfo();
			}
			if (deviceList != null && deviceList.size() > 0) {
				DeviceCollectEntity devicedata = null;
				for (int i = 0; i < deviceList.size(); i++) {
					DeviceNodeTemp nodetemp = (DeviceNodeTemp) deviceList.get(i);
					devicedata = new DeviceCollectEntity();
					devicedata.setIpaddress(host.getIpAddress());
					devicedata.setName(nodetemp.getName());
					devicedata.setStatus(nodetemp.getStatus());
					devicedata.setType(nodetemp.getDtype());
					deviceV.addElement(devicedata);
				}
			}
			try {
				memhash = hostlastmanager.getMemory(host.getIpAddress(), "Memory", null, null);
				diskhash = hostlastmanager.getDisk(host.getIpAddress(), "Disk", null, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// flash
			if (envlist != null && envlist.size() > 0) {
				for (int i = 0; i < envlist.size(); i++) {
					NodeTemp nodetemp = (NodeTemp) envlist.get(i);
					if (nodetemp != null && nodetemp.getEntity().equals("Flash")) {
						flashInfo = new DeviceFlashInfo();
						flashInfo.setFlashName(nodetemp.getSindex());
						flashList.add(flashInfo);
					}

				}
			}
		}
		// CPU
		if (deviceV != null && deviceV.size() > 0) {
			int count = 0;
			String name = "";
			for (int m = 0; m < deviceV.size(); m++) {
				DeviceCollectEntity devicedata = (DeviceCollectEntity) deviceV.get(m);

				String type = devicedata.getType();
				if (!"CPU".equals(type))
					continue;
				name = devicedata.getName();
				count++;
			}
			cpuInfo.setCmpName(name);
			cpuInfo.setCpuCount(count);
		}
		// 内存
		if (memhash != null) {
			for (int k = 0; k < memhash.size(); k++) {
				Hashtable mhash = (Hashtable) (memhash.get(new Integer(k)));
				String name = (String) mhash.get("name");
				String value = "";
				if (mhash.get("Capability") != null) {
					value = (String) mhash.get("Capability");

				}
				memoryInfo = new DeviceMemoryInfo();
				memoryInfo.setMemoryName(name);
				memoryInfo.setAllSize(value);
				memoryList.add(memoryInfo);
			}
		}
		// 磁盘
		String diskItem = "AllSize";
		if (diskhash != null) {
			for (int k = 0; k < diskhash.size(); k++) {
				Hashtable dhash = (Hashtable) (diskhash.get(new Integer(k)));
				diskInfo = new DeviceDiskInfo();
				String name = "";
				String value = "";
				if (dhash.get("name") != null) {
					name = (String) dhash.get("name");
				}
				if (dhash.get(diskItem) != null) {
					value = (String) dhash.get(diskItem);
				}
				diskInfo.setDiskName(name);
				diskInfo.setAllsize(value);
				diskList.add(diskInfo);

			}
		}
		hardwareInfo.setIpAddress(ip);
		hardwareInfo.setCpu(cpuInfo);
		hardwareInfo.setMemeory(memoryList);
		hardwareInfo.setDisk(diskList);
		hardwareInfo.setFlash(flashList);
		return hardwareInfo;
	}

	/**
	 * 获取所有设备的基本信息
	 * 
	 * @return
	 */
	public void getAllDeviceBaseInfo(Hashtable<String, String> deviceInfoTable) {
		HostNodeDao dao = new HostNodeDao();
		List<HostNode> list = null;
		try {
			list = dao.loadall();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				HostNode node = list.get(i);
				deviceInfoTable.put(node.getId() + "_ipAddress", node.getIpAddress());
				deviceInfoTable.put(node.getId() + "_sysName", node.getSysName());
				deviceInfoTable.put(node.getId() + "_alias", node.getAlias());
				deviceInfoTable.put(node.getId() + "_netMask", node.getNetMask());
				deviceInfoTable.put(node.getId() + "_sysDescr", node.getSysDescr());
				deviceInfoTable.put(node.getId() + "_sysOid", node.getSysOid());
				deviceInfoTable.put(node.getId() + "_osType", node.getOstype() + "");
				deviceInfoTable.put(node.getId() + "_status", node.getStatus() + "");
			}
		}
	}

	/**
	 * 获取设备或主机的接口方法
	 * 
	 * @param moIds
	 * @param interfaceTable
	 */
	public void getDeviceInterface(Vector<String> moIds, Hashtable<String, String> interfaceTable) {
		DeviceInterfaceInfo info = null;
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		Vector vector = new Vector();
		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
		String[] netInterfaceItem = { "ifDescr", "ifType", "ifPhysAddress" };
		if (moIds != null && moIds.size() > 0) {
			try {
				StringBuffer ifName = null;
				StringBuffer ifType = null;
				StringBuffer ifPhysAddress = null;
				for (int i = 0; i < moIds.size(); i++) {
					ifName = new StringBuffer();
					ifType = new StringBuffer();
					ifPhysAddress = new StringBuffer();
					Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt((String) moIds.get(i)));
					if (host == null)
						continue;
					String ip = host.getIpAddress();
					if (ip == null)
						continue;
					if (runmodel.equals("0")) {// //采集与访问是集成模式
						vector = hostlastmanager.getInterface_share(ip, netInterfaceItem, "index", null, null);
					} else {
						try {
							vector = hostlastmanager.getInterface(ip, netInterfaceItem, "index", null, null);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (vector != null) {
						for (int j = 0; j < vector.size(); j++) {
							info = new DeviceInterfaceInfo();
							String[] strs = (String[]) vector.get(j);
							if (strs != null && strs.length > 0) {
								info.setIfName(strs[0]);
								info.setIfType((String) strs[1]);
								info.setIfPhysAddress((String) strs[2]);

								if (j != (vector.size() - 1)) {
									ifName.append(strs[0] + ";");
									ifType.append((String) strs[1] + ";");
									ifPhysAddress.append((String) strs[2] + ";");
								} else {
									ifName.append(strs[0]);
									ifType.append(strs[1]);
									ifPhysAddress.append(strs[2]);
								}
							}

						}
						interfaceTable.put((String) moIds.get(i) + "_ifName", ifName.toString());
						interfaceTable.put((String) moIds.get(i) + "_ifType", ifType.toString());
						interfaceTable.put((String) moIds.get(i) + "_ifPhysAddress", ifPhysAddress.toString());
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * 获取硬件信息
	 * 
	 * @param moIds
	 * @param hardwareTable
	 */
	public void getHardwareInfo(Vector<String> moIds, Hashtable<String, String> hardwareTable) {
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		DeviceCpuInfo cpuInfo = new DeviceCpuInfo();

		Vector envvector = new Vector();

		List deviceList = new ArrayList();
		Vector deviceV = null;
		Hashtable memhash = new Hashtable();
		Hashtable diskhash = new Hashtable();
		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
		if (moIds != null && moIds.size() > 0) {
			for (int v = 0; v < moIds.size(); v++) {
				Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt((String) moIds.get(v)));
				if (host == null)
					continue;
				String ip = host.getIpAddress();
				if (ip == null)
					continue;
				StringBuffer flashName = null;
				StringBuffer memoryName = null;
				StringBuffer memorySize = null;
				StringBuffer diskName = null;
				StringBuffer diskSize = null;

				if ("0".equals(runmodel)) {
					// 采集与访问是集成模式
					Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
					deviceV = (Vector) ipAllData.get("device");

					try {
						memhash = hostlastmanager.getMemory_share(ip, "Memory", null, null);
						diskhash = hostlastmanager.getDisk_share(ip, "Disk", null, null);
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (ipAllData != null) {
						// 得到闪存信息
						envvector = (Vector) ipAllData.get("flash");
						if (envvector != null && envvector.size() > 0) {
							flashName = new StringBuffer();
							for (int i = 0; i < envvector.size(); i++) {

								FlashCollectEntity nodetemp = (FlashCollectEntity) envvector.get(i);
								if (nodetemp != null) {
									String category = nodetemp.getCategory();
									if (category == null || !category.equals("Flash"))
										continue;
									if (i != (envvector.size() - 1)) {
										flashName.append(nodetemp.getSubentity() + ";");
									} else {
										flashName.append(nodetemp.getSubentity());
									}

								}

							}
							hardwareTable.put(moIds.get(v) + "_flashName", flashName.toString());
						}
					}
				} else {

					deviceV = new Vector();
					List envlist = new ArrayList();
					NodeUtil nodeUtil = new NodeUtil();
					NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);
					// CPU
					if (nodedto != null) {
						deviceList = new NetService(host.getId() + "", nodedto.getType(), nodedto.getSubtype()).getDeviceInfo();
						envlist = new NetService(host.getId() + "", nodedto.getType(), nodedto.getSubtype()).getCurrENVInfo();
					}
					if (deviceList != null && deviceList.size() > 0) {
						DeviceCollectEntity devicedata = null;
						for (int i = 0; i < deviceList.size(); i++) {
							DeviceNodeTemp nodetemp = (DeviceNodeTemp) deviceList.get(i);
							devicedata = new DeviceCollectEntity();
							devicedata.setIpaddress(host.getIpAddress());
							devicedata.setName(nodetemp.getName());
							devicedata.setStatus(nodetemp.getStatus());
							devicedata.setType(nodetemp.getDtype());
							deviceV.addElement(devicedata);
						}
					}
					try {
						memhash = hostlastmanager.getMemory(host.getIpAddress(), "Memory", null, null);
						diskhash = hostlastmanager.getDisk(host.getIpAddress(), "Disk", null, null);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// flash
					if (envlist != null && envlist.size() > 0) {
						flashName = new StringBuffer();
						for (int i = 0; i < envlist.size(); i++) {

							NodeTemp nodetemp = (NodeTemp) envlist.get(i);
							if (nodetemp != null && nodetemp.getEntity().equals("Flash")) {
								if (i != (envvector.size() - 1)) {
									flashName.append(nodetemp.getSindex() + ";");
								} else {
									flashName.append(nodetemp.getSindex());
								}
							}

						}
						hardwareTable.put(moIds.get(v) + "_flashName", flashName.toString());
					}
				}
				// ////////////////////////CPU////////////////
				if (deviceV != null && deviceV.size() > 0) {
					int count = 0;
					String name = "";
					for (int m = 0; m < deviceV.size(); m++) {
						DeviceCollectEntity devicedata = (DeviceCollectEntity) deviceV.get(m);

						String type = devicedata.getType();
						if (!"CPU".equals(type))
							continue;
						name = devicedata.getName();
						count++;
					}
					cpuInfo.setCmpName(name);
					cpuInfo.setCpuCount(count);
					hardwareTable.put(moIds.get(v) + "_cpuCount", count + "");
					hardwareTable.put(moIds.get(v) + "_cmpName", name);
				}
				// ////////////////内存/////////////////////
				if (memhash != null) {
					memoryName = new StringBuffer();
					memorySize = new StringBuffer();
					for (int k = 0; k < memhash.size(); k++) {
						Hashtable mhash = (Hashtable) (memhash.get(new Integer(k)));
						String name = (String) mhash.get("name");
						String value = "";
						if (mhash.get("Capability") != null) {
							value = (String) mhash.get("Capability");
						}
						if (k != (memhash.size() - 1)) {
							memoryName.append(name + ";");
							memorySize.append(value + ";");

						} else {
							memoryName.append(name);
							memorySize.append(value);
						}
					}
					hardwareTable.put(moIds.get(v) + "_memoryName", memoryName.toString());
					hardwareTable.put(moIds.get(v) + "_memorySize", memorySize.toString());
				}
				// //////////////////磁盘////////////////
				String diskItem = "AllSize";
				if (diskhash != null) {
					diskName = new StringBuffer();
					diskSize = new StringBuffer();
					for (int k = 0; k < diskhash.size(); k++) {
						Hashtable dhash = (Hashtable) (diskhash.get(new Integer(k)));
						String name = "";
						String value = "";
						if (dhash.get("name") != null) {
							name = (String) dhash.get("name");
						}
						if (dhash.get(diskItem) != null) {
							value = (String) dhash.get(diskItem);
						}
						if (k != (diskhash.size() - 1)) {
							diskName.append(name + ";");
							diskSize.append(value + ";");
						} else {
							diskName.append(name);
							diskSize.append(value);
						}

					}
					hardwareTable.put(moIds.get(v) + "_diskName", diskName.toString());
					hardwareTable.put(moIds.get(v) + "_diskSize", diskSize.toString());
				}
			}
		}

	}

	/**
	 * 获取操作系统信息
	 * 
	 * @param moIds
	 * @param osTable
	 */
	public void getOsInfo(Vector<String> moIds, Hashtable<String, String> osTable) {
		if (moIds != null && moIds.size() > 0) {

			for (int i = 0; i < moIds.size(); i++) {
				Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt((String) moIds.get(i)));
				if (host == null)
					continue;
				String ip = host.getIpAddress();
				if (ip == null)
					continue;
				String runmodel = PollingEngine.getCollectwebflag();
				String sysdescr = "";
				if ("0".equals(runmodel)) {
					// 采集与访问是集成模式
					Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
					// 得到系统启动时间
					Vector systemV = (Vector) ipAllData.get("system");

					if (systemV != null && systemV.size() > 0) {
						for (int k = 0; k < systemV.size(); k++) {
							SystemCollectEntity systemdata = (SystemCollectEntity) systemV.get(k);

							if (systemdata.getSubentity().equalsIgnoreCase("sysDescr")) {
								sysdescr = systemdata.getThevalue();
							}
						}
					}
				} else {
					// 采集与访问是分离模式
					NodeUtil nodeUtil = new NodeUtil();
					NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);
					List systemList = new ArrayList();
					systemList = new NetService(host.getId() + "", nodedto.getType(), nodedto.getSubtype()).getSystemInfo();
					if (systemList != null && systemList.size() > 0) {
						for (int j = 0; j < systemList.size(); j++) {
							NodeTemp nodetemp = (NodeTemp) systemList.get(j);

							if ("sysDescr".equals(nodetemp.getSindex()))
								sysdescr = nodetemp.getThevalue();

						}
					}
				}
				SupperDao supperdao = new SupperDao();
				Supper supper = null;
				String suppername = "";
				try {
					supper = (Supper) supperdao.findByID(host.getSupperid() + "");
					if (supper != null)
						suppername = supper.getSu_name() + "(" + supper.getSu_dept() + ")";
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					supperdao.close();
				}

				osTable.put(moIds.get(i) + "_ipAddress", ip);
				osTable.put(moIds.get(i) + "_sysDesc", sysdescr);
				osTable.put(moIds.get(i) + "_cmpName", suppername);
			}
		}
	}

	/**
	 * 获取所有数据库信息
	 * 
	 * @param dbTable
	 */
	public void getAllDbInfo(Hashtable<String, String> dbTable) {
		DBDao dao = new DBDao();
		List list = null;
		try {
			list = dao.getDbByMonFlag(1);
			if (list != null && list.size() > 0) {
				for (int j = 0; j < list.size(); j++) {
					DBVo vo = (DBVo) list.get(j);
					if (vo == null)
						continue;
					dbTable.put(vo.getId() + "_ipAddress", vo.getId() + "");
					dbTable.put(vo.getId() + "_dbName", vo.getDbName());
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

	}

	/**
	 * 数据同步
	 */
	public void synchronousData() {
		// /////////////加载设备节点///////////////
		HostNodeDao dao = new HostNodeDao();
		List<HostNode> list = null;
		try {
			list = dao.loadall();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		String typeCode = "";
		RelationDao relationDao = new RelationDao();
		relationDao.delteAllData();// 清除数据
		MoAndCiRelation relation = null;
		if (list != null && list.size() > 0) {

			for (int i = 0; i < list.size(); i++) {
				HostNode node = list.get(i);
				if (node != null) {
					if (node.getCategory() == 1 || node.getCategory() == 2 || node.getCategory() == 3) {
						typeCode = "0103";
					} else if (node.getCategory() == 4) {
						typeCode = "0101";// 服务器
						if (node.getOstype() == 5) {
							relation = new MoAndCiRelation();
							relation.setCiType("020501");// 应用软件节点
							relation.setMoId(node.getId());

							try {
								relationDao.addBatch(relation);
							} catch (Exception e) {
								e.printStackTrace();
							}
							relation = new MoAndCiRelation();
							relation.setCiType("020101");// windwos操作系统节点
							relation.setMoId(node.getId());

							try {
								relationDao.addBatch(relation);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (node.getOstype() == 6) {
							relation = new MoAndCiRelation();
							relation.setCiType("020102");// Aix操作系统节点
							relation.setMoId(node.getId());

							try {
								relationDao.addBatch(relation);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (node.getOstype() == 7) {
							relation = new MoAndCiRelation();
							relation.setCiType("020103");// Hpunix操作系统节点
							relation.setMoId(node.getId());

							try {
								relationDao.addBatch(relation);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (node.getOstype() == 8) {
							relation = new MoAndCiRelation();
							relation.setCiType("020104");// solaris操作系统节点
							relation.setMoId(node.getId());

							try {
								relationDao.addBatch(relation);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (node.getOstype() == 9) {
							relation = new MoAndCiRelation();
							relation.setCiType("020105");// linux操作系统节点
							relation.setMoId(node.getId());

							try {
								relationDao.addBatch(relation);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (node.getOstype() == 15) {
							relation = new MoAndCiRelation();
							relation.setCiType("020106");// linux操作系统节点
							relation.setMoId(node.getId());

							try {
								relationDao.addBatch(relation);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (node.getOstype() == 20) {
							relation = new MoAndCiRelation();
							relation.setCiType("020107");// scounix操作系统节点
							relation.setMoId(node.getId());

							try {
								relationDao.addBatch(relation);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (node.getOstype() == 21) {
							relation = new MoAndCiRelation();
							relation.setCiType("020108");// scoopenserver操作系统节点
							relation.setMoId(node.getId());

							try {
								relationDao.addBatch(relation);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

					relation = new MoAndCiRelation();
					relation.setCiType(typeCode);
					relation.setMoId(node.getId());

					try {
						relationDao.addBatch(relation);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}

		}
		// /////////////加载数据库节点///////////////
		List<DBVo> dbList = null;
		DBDao dbdao = new DBDao();
		try {
			dbList = dbdao.getDbByMonFlag(1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbdao.close();
		}

		if (dbList != null && dbList.size() > 0) {
			for (int i = 0; i < dbList.size(); i++) {
				DBVo vo = dbList.get(i);
				if (vo != null) {
					if (vo.getDbtype() == 1) {// oracle
						typeCode = "020201";
					} else if (vo.getDbtype() == 2) {// sqlserver
						typeCode = "020202";
					} else if (vo.getDbtype() == 4) {// mysql
						typeCode = "020203";
					} else if (vo.getDbtype() == 5) {// db2
						typeCode = "020204";
					} else if (vo.getDbtype() == 6) {// sybase
						typeCode = "020205";
					} else if (vo.getDbtype() == 7) {// informix
						typeCode = "020206";
					}
					relation = new MoAndCiRelation();
					relation.setCiType(typeCode);
					relation.setMoId(vo.getId());
					try {
						relationDao.addBatch(relation);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		// /////////////加载Tomcat节点///////////////
		TomcatDao tomcatDao = new TomcatDao();
		List<Tomcat> tomcatList = null;
		try {
			tomcatList = tomcatDao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tomcatDao.close();
		}
		Tomcat tomcat = null;
		if (tomcatList != null && tomcatList.size() > 0) {
			for (int i = 0; i < tomcatList.size(); i++) {
				tomcat = tomcatList.get(i);
				if (tomcat != null) {
					typeCode = "020301";
					relation = new MoAndCiRelation();
					relation.setCiType(typeCode);
					relation.setMoId(tomcat.getId());
					try {
						relationDao.addBatch(relation);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		}
		// /////////////加载MQ节点///////////////
		MQConfigDao mqDao = new MQConfigDao();
		List<MQConfig> mqList = null;
		try {
			mqList = mqDao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mqDao.close();
		}
		MQConfig mqConfig = null;
		if (mqList != null && mqList.size() > 0) {
			for (int i = 0; i < mqList.size(); i++) {
				mqConfig = mqList.get(i);
				if (mqConfig != null) {
					typeCode = "020302";
					relation = new MoAndCiRelation();
					relation.setCiType(typeCode);
					relation.setMoId(tomcat.getId());
					try {
						relationDao.addBatch(relation);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		}
		relationDao.executeBatch();
	}

	/**
	 * 查询主机的软件信息
	 * 
	 * @param moIds
	 * @param softwareTable
	 */
	public void getSoftwareInfo(Vector<String> moIds, Hashtable<String, String> softwareTable) {
		String ip = "";

		Vector softwareV = new Vector();
		StringBuffer softwareName = new StringBuffer();
		StringBuffer startUpTime = new StringBuffer();
		if (moIds != null && moIds.size() > 0) {

			for (int i = 0; i < moIds.size(); i++) {
				Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt((String) moIds.get(i)));
				if (host == null)
					continue;
				ip = host.getIpAddress();
				if (ip == null || host.getCategory() != 4)
					continue;
				String runmodel = PollingEngine.getCollectwebflag();
				if ("0".equals(runmodel)) {
					Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
					if (ipAllData != null) {
						softwareV = (Vector) ipAllData.get("software");
					}
				} else {
					List softwareList = new ArrayList();
					NodeUtil nodeUtil = new NodeUtil();
					NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);
					softwareList = new NetService(host.getId() + "", nodedto.getType(), nodedto.getSubtype()).getSoftwareInfo();
					if (softwareList != null && softwareList.size() > 0) {
						SoftwareCollectEntity softwaredata = null;
						for (int j = 0; j < softwareList.size(); j++) {
							SoftwareNodeTemp nodetemp = (SoftwareNodeTemp) softwareList.get(j);
							softwaredata = new SoftwareCollectEntity();
							softwaredata.setIpaddress(host.getIpAddress());
							softwaredata.setName(nodetemp.getName());
							softwaredata.setSwid(nodetemp.getSwid());
							softwaredata.setType(nodetemp.getStype());
							softwaredata.setInsdate(nodetemp.getInsdate());
							softwareV.addElement(softwaredata);
						}
					}
				}
				try {

					if (softwareV == null)
						softwareV = new Vector();
					for (int m = 0; m < softwareV.size(); m++) {
						SoftwareCollectEntity swdata = (SoftwareCollectEntity) softwareV.get(m);
						String name = swdata.getName();
						String type = swdata.getType();
						String insdate = swdata.getInsdate();
						if (!type.equals("应用软件"))
							continue;
						if (m != (softwareV.size() - 1)) {
							softwareName.append(name + ";");
							startUpTime.append(insdate + ";");
						} else {
							softwareName.append(name);
							startUpTime.append(insdate);
						}
					}
					softwareTable.put(moIds.get(i) + "_softwareName", softwareName.toString());
					softwareTable.put(moIds.get(i) + "_statupTime", startUpTime.toString());

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		}

	}

	/**
	 * 获取tomcat节点信息
	 * 
	 * @param moIds
	 * @param midwareTable
	 */
	public void getAllTomcatInfo(Hashtable<String, String> midwareTable) {
		TomcatDao tomcatDao = new TomcatDao();
		List<Tomcat> list = null;
		try {
			list = tomcatDao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tomcatDao.close();
		}
		Tomcat tomcat = null;
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				tomcat = list.get(i);
				if (tomcat != null) {
					midwareTable.put(tomcat.getId() + "_ipAddress", tomcat.getIpAddress());
					midwareTable.put(tomcat.getId() + "_midwareName", tomcat.getAlias());
				}

			}
		}
	}

	/**
	 * 获取MQ节点信息
	 * 
	 * @param moIds
	 * @param midwareTable
	 */
	public void getAllMQInfo(Hashtable<String, String> midwareTable) {
		MQConfigDao mqDao = new MQConfigDao();
		List<MQConfig> list = null;
		try {
			list = mqDao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mqDao.close();
		}
		MQConfig mqConfig = null;
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				mqConfig = list.get(i);
				if (mqConfig != null) {
					midwareTable.put(mqConfig.getId() + "_ipAddress", mqConfig.getIpaddress());
					midwareTable.put(mqConfig.getId() + "_midwareName", mqConfig.getName());
				}

			}
		}
	}
}
