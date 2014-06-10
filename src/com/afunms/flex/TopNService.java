package com.afunms.flex;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.detail.service.cpuInfo.CpuInfoService;
import com.afunms.detail.service.interfaceInfo.InterfaceInfoService;
import com.afunms.detail.service.memoryInfo.MemoryInfoService;
import com.afunms.detail.service.pingInfo.PingInfoService;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.Avgcollectdata;
import com.afunms.polling.om.CpuCollectEntity;
import com.afunms.polling.om.DiskCollectEntity;
import com.afunms.polling.om.MemoryCollectEntity;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.polling.om.UtilHdxPerc;
import com.afunms.system.vo.FlexVo;
import com.afunms.system.vo.Vos;
import com.afunms.temp.model.NodeTemp;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;

import flex.messaging.FlexSession;

@SuppressWarnings("unchecked")
public class TopNService {

	public TopNService() {
	}

	public FlexSession flexSession;

	// 根据权限获取各种设备列表

	public List getNodeList(int category, String bids) {
		List networklist = new ArrayList();
		HostNodeDao nodedao = new HostNodeDao();
		try {
			networklist = nodedao.loadNetworkByBid(category, bids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			nodedao.close();
		}
		return networklist;
	}

	/**
	 * 服务器磁盘利用率(flex调用)
	 */
	public List<FlexVo> getHostDiskList(String bids, int size) {
		List hostlist = getNodeList(4, bids);
		List hostdisklist = new ArrayList();
		List allhostdisklist = new ArrayList();
		if (hostlist != null && hostlist.size() > 0) {
			for (int i = 0; i < hostlist.size(); i++) {
				HostNode node = (HostNode) hostlist.get(i);
				Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
				if (ipAllData != null) {
					Vector diskVector = null;
					try {
						diskVector = (Vector) ipAllData.get("disk");
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
					if (diskVector != null && diskVector.size() > 0) {
						for (int si = 0; si < diskVector.size(); si++) {
							DiskCollectEntity diskdata = (DiskCollectEntity) diskVector.elementAt(si);
							if (diskdata.getEntity().equalsIgnoreCase("Utilization")) {
								// 利用率
								if (node.getOstype() == 4 || node.getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")) {
									diskdata.setSubentity(diskdata.getSubentity().substring(0, 3));
								}
								hostdisklist.add(diskdata);
							}
						}
					}
				}
			}
			if (hostdisklist != null && hostdisklist.size() > 0) {
				for (int m = 0; m < hostdisklist.size(); m++) {
					DiskCollectEntity hdata = (DiskCollectEntity) hostdisklist.get(m);
					for (int n = m + 1; n < hostdisklist.size(); n++) {
						DiskCollectEntity hosdata = (DiskCollectEntity) hostdisklist.get(n);
						if (new Double(hdata.getThevalue()).doubleValue() < new Double(hosdata.getThevalue()).doubleValue()) {
							hostdisklist.remove(m);
							hostdisklist.add(m, hosdata);
							hostdisklist.remove(n);
							hostdisklist.add(n, hdata);
							hdata = hosdata;
							hosdata = null;
						}
					}
					// 得到排序后的Subentity的列表
					allhostdisklist.add(hdata);
					if (allhostdisklist.size() >= size - 1) {
						break;
					}
					hdata = null;
				}
			}
		}
		List<FlexVo> flexDataList = new ArrayList<FlexVo>();
		FlexVo fVo;
		DecimalFormat df = new DecimalFormat("#.##");
		for (int i = 0; i < allhostdisklist.size(); i++) {
			DiskCollectEntity diskcollectdata = (DiskCollectEntity) allhostdisklist.get(i);
			fVo = new FlexVo();
			fVo.setObjectName(diskcollectdata.getIpaddress() + " " + diskcollectdata.getSubentity());
			fVo.setObjectNumber(df.format(Double.parseDouble(diskcollectdata.getThevalue())));
			flexDataList.add(fVo);
		}
		return flexDataList;
	}

	// 网络设备实时数据topn
	public List getNetworkTempList(int category, int size, String type, String bids) {
		List networkList = new ArrayList();
		List list = new ArrayList();
		List networklist = getNodeList(category, bids);
		String runmodel = PollingEngine.getCollectwebflag();
		Hashtable allpingdata = ShareData.getPingdata();
		if (networklist != null && networklist.size() > 0) {
			if ("1".equals(runmodel)) {
				// 采集与访问是分离模式
				PingInfoService pingInfoService = new PingInfoService();
				CpuInfoService cpuInfoService = new CpuInfoService();
				MemoryInfoService memoryInfoService = new MemoryInfoService();
				InterfaceInfoService interfaceInfoService = new InterfaceInfoService();
				if ("ping".equalsIgnoreCase(type)) {
					List<NodeTemp> pingInfoList = pingInfoService.getPingInfo(networklist);
					if (pingInfoList != null && pingInfoList.size() > 0) {
						for (int j = 0; j < pingInfoList.size(); j++) {
							NodeTemp nodeTemp = pingInfoList.get(j);
							Avgcollectdata avgcollectdata = new Avgcollectdata();
							avgcollectdata.setIpaddress(nodeTemp.getIp());
							avgcollectdata.setThevalue(nodeTemp.getThevalue());
							avgcollectdata.setBak("连通率");
							avgcollectdata.setUnit("%");
							list.add(avgcollectdata);
						}
					}
				} else if ("ResponseTime".equalsIgnoreCase(type)) {
					List<NodeTemp> pingInfoList = pingInfoService.getResponseInfo(networklist);
					if (pingInfoList != null && pingInfoList.size() > 0) {
						for (int j = 0; j < pingInfoList.size(); j++) {
							NodeTemp nodeTemp = pingInfoList.get(j);
							Avgcollectdata avgcollectdata = new Avgcollectdata();
							avgcollectdata.setIpaddress(nodeTemp.getIp());
							avgcollectdata.setThevalue(nodeTemp.getThevalue());
							avgcollectdata.setBak("响应时间");
							avgcollectdata.setUnit("ms");
							list.add(avgcollectdata);
						}
					}
				} else if ("cpu".equalsIgnoreCase(type)) {
					List<NodeTemp> cpuInfoList = cpuInfoService.getCpuPerListInfo(networklist);
					if (cpuInfoList != null && cpuInfoList.size() > 0) {
						for (int j = 0; j < cpuInfoList.size(); j++) {
							NodeTemp nodeTemp = cpuInfoList.get(j);
							Avgcollectdata avgcollectdata = new Avgcollectdata();
							avgcollectdata.setIpaddress(nodeTemp.getIp());
							avgcollectdata.setThevalue(nodeTemp.getThevalue());
							avgcollectdata.setBak("CPU利用率");
							avgcollectdata.setUnit("%");
							list.add(avgcollectdata);
						}
					}
				} else if ("memory".equalsIgnoreCase(type)) {
					List<NodeTemp> memoryInfoList = memoryInfoService.getMemoryInfo(networklist);
					if (memoryInfoList != null && memoryInfoList.size() > 0) {
						for (int j = 0; j < memoryInfoList.size(); j++) {
							NodeTemp nodeTemp = memoryInfoList.get(j);
							Avgcollectdata avgcollectdata = new Avgcollectdata();
							avgcollectdata.setIpaddress(nodeTemp.getIp());
							avgcollectdata.setThevalue(nodeTemp.getThevalue());
							avgcollectdata.setBak("内存利用率");
							avgcollectdata.setUnit("%");
							list.add(avgcollectdata);
						}
					}
				} else if ("inutil".equalsIgnoreCase(type)) {
					List<NodeTemp> inutilInfoList = interfaceInfoService.getInterfaceInfo(networklist);
					if (inutilInfoList != null && inutilInfoList.size() > 0) {
						for (int j = 0; j < inutilInfoList.size(); j++) {
							NodeTemp nodeTemp = inutilInfoList.get(j);
							if (nodeTemp.getSubentity() != null && nodeTemp.getSubentity().equalsIgnoreCase("AllInBandwidthUtilHdx")) {
								Avgcollectdata avgcollectdata = new Avgcollectdata();
								avgcollectdata.setIpaddress(nodeTemp.getIp());
								avgcollectdata.setThevalue(nodeTemp.getThevalue());
								avgcollectdata.setBak("入口流速");
								avgcollectdata.setUnit("KB/s");
								list.add(avgcollectdata);
							}
						}
					}
				} else if ("oututil".equalsIgnoreCase(type)) {
					List<NodeTemp> oututilInfoList = interfaceInfoService.getInterfaceInfo(networklist);
					if (oututilInfoList != null && oututilInfoList.size() > 0) {
						for (int j = 0; j < oututilInfoList.size(); j++) {
							NodeTemp nodeTemp = oututilInfoList.get(j);
							if (nodeTemp.getSubentity() != null && nodeTemp.getSubentity().equalsIgnoreCase("AllOutBandwidthUtilHdx")) {
								Avgcollectdata avgcollectdata = new Avgcollectdata();
								avgcollectdata.setIpaddress(nodeTemp.getIp());
								avgcollectdata.setThevalue(nodeTemp.getThevalue());
								avgcollectdata.setBak("出口流速");
								avgcollectdata.setUnit("Kb/s");
								list.add(avgcollectdata);
							}
						}
					}
				}
			} else {
				if ("ping".equalsIgnoreCase(type)) {
					if (allpingdata != null) {
						for (int i = 0; i < networklist.size(); i++) {
							HostNode node = (HostNode) networklist.get(i);
							String pingValue = "0"; // ping 默认为 0
							Vector pingData = (Vector) allpingdata.get(node.getIpAddress());
							if (pingData != null && pingData.size() > 0) {
								PingCollectEntity pingcollectdata = (PingCollectEntity) pingData.get(0);
								pingValue = pingcollectdata.getThevalue();
								Avgcollectdata avgcollectdata = new Avgcollectdata();
								avgcollectdata.setIpaddress(node.getIpAddress());
								avgcollectdata.setThevalue(pingValue);
								avgcollectdata.setBak("连通率");
								avgcollectdata.setUnit("%");
								list.add(avgcollectdata);
							}
						}
					}
				} else if ("ResponseTime".equalsIgnoreCase(type)) {
					if (allpingdata != null) {
						for (int i = 0; i < networklist.size(); i++) {
							HostNode node = (HostNode) networklist.get(i);
							String pingValue = "0"; // ping 默认为 0
							Vector pingData = (Vector) allpingdata.get(node.getIpAddress());
							if (pingData != null && pingData.size() > 0) {
								PingCollectEntity pingcollectdata = (PingCollectEntity) pingData.get(1);
								pingValue = pingcollectdata.getThevalue();
								Avgcollectdata avgcollectdata = new Avgcollectdata();
								avgcollectdata.setIpaddress(node.getIpAddress());
								avgcollectdata.setThevalue(pingValue);
								avgcollectdata.setBak("响应时间");
								avgcollectdata.setUnit("ms");
								list.add(avgcollectdata);
							}
						}
					}
				} else if ("cpu".equalsIgnoreCase(type)) {
					for (int i = 0; i < networklist.size(); i++) {
						HostNode node = (HostNode) networklist.get(i);
						Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
						if (ipAllData != null) {
							Vector cpuV = (Vector) ipAllData.get("cpu");
							if (cpuV != null && cpuV.size() > 0) {
								CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
								Avgcollectdata avgcollectdata = new Avgcollectdata();
								avgcollectdata.setIpaddress(node.getIpAddress());
								String cpuValue = cpu.getThevalue();
								if (cpuValue == null) {
									cpuValue = "0";
								}
								cpuValue = cpuValue.replace("%", "");
								avgcollectdata.setThevalue(cpuValue);
								avgcollectdata.setBak("CPU利用率");
								avgcollectdata.setUnit("%");
								list.add(avgcollectdata);
							}
						}
					}
				} else if ("memory".equalsIgnoreCase(type)) {
					for (int i = 0; i < networklist.size(); i++) {
						HostNode node = (HostNode) networklist.get(i);
						Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
						if (ipAllData != null) {
							if (category == 1) {
								Vector memoryV = (Vector) ipAllData.get("memory");
								MemoryCollectEntity memory = getMaxUtilization(memoryV);
								Avgcollectdata avgcollectdata = new Avgcollectdata();
								avgcollectdata.setIpaddress(node.getIpAddress());
								String memoryValue = "0.0";
								if (memory != null) {
									memoryValue = memory.getThevalue();
								}
								memoryValue = memoryValue.replace("%", "");
								avgcollectdata.setThevalue(memoryValue);
								avgcollectdata.setBak("内存利用率");
								avgcollectdata.setUnit("%");
								list.add(avgcollectdata);
							} else if (category == 4) {
								DecimalFormat df = new DecimalFormat("#.##");
								Vector memoryVector = (Vector) ipAllData.get("memory");
								if (memoryVector != null && memoryVector.size() > 0) {
									for (int si = 0; si < memoryVector.size(); si++) {
										MemoryCollectEntity memorydata = (MemoryCollectEntity) memoryVector.elementAt(si);
										if (memorydata.getEntity().equalsIgnoreCase("Utilization")) {
											// 利用率
											if (memorydata.getSubentity().equalsIgnoreCase("PhysicalMemory")) {
												Avgcollectdata avgcollectdata = new Avgcollectdata();
												avgcollectdata.setIpaddress(node.getIpAddress());
												avgcollectdata.setThevalue(df.format(Double.parseDouble(memorydata.getThevalue().replace("%", ""))));
												avgcollectdata.setBak("物理内存利用率");
												avgcollectdata.setUnit("%");
												list.add(avgcollectdata);
											}
										}
									}
								}
							}
						}
					}
				} else if ("inutil".equalsIgnoreCase(type)) {
					for (int i = 0; i < networklist.size(); i++) {
						HostNode node = (HostNode) networklist.get(i);
						Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
						if (ipAllData != null) {
							Vector allutil = (Vector) ipAllData.get("allutilhdx");
							if (allutil != null && allutil.size() == 3) {
								Avgcollectdata avgcollectdatai = new Avgcollectdata();
								AllUtilHdx inutilhdx = (AllUtilHdx) allutil.get(0);
								avgcollectdatai.setIpaddress(node.getIpAddress());
								avgcollectdatai.setThevalue(inutilhdx.getThevalue());
								avgcollectdatai.setBak("入口流速");
								avgcollectdatai.setUnit("Kb/s");
								list.add(avgcollectdatai);
							}
						}
					}
				} else if ("oututil".equalsIgnoreCase(type)) {
					for (int i = 0; i < networklist.size(); i++) {
						HostNode node = (HostNode) networklist.get(i);
						Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
						if (ipAllData != null) {
							Vector allutil = (Vector) ipAllData.get("allutilhdx");
							if (allutil != null && allutil.size() == 3) {
								Avgcollectdata avgcollectdatao = new Avgcollectdata();
								AllUtilHdx oututilhdx = (AllUtilHdx) allutil.get(1);
								avgcollectdatao.setIpaddress(node.getIpAddress());
								avgcollectdatao.setThevalue(oututilhdx.getThevalue());
								avgcollectdatao.setBak("出口流速");
								avgcollectdatao.setUnit("Kb/s");
								list.add(avgcollectdatao);
							}
						}
					}
				} else if ("inutilper".equalsIgnoreCase(type)) {
					for (int i = 0; i < networklist.size(); i++) {
						HostNode node = (HostNode) networklist.get(i);
						Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
						if (ipAllData != null) {
							Vector utilhdxperc = (Vector) ipAllData.get("utilhdxperc");
							if (utilhdxperc != null && utilhdxperc.size() > 0) {
								for (int j = 0; j < utilhdxperc.size(); j++) {
									UtilHdxPerc inutilhdx = (UtilHdxPerc) utilhdxperc.get(j);
									if ("InBandwidthUtilHdxPerc".equalsIgnoreCase(inutilhdx.getEntity())) {
										Avgcollectdata avgcollectdatao = new Avgcollectdata();
										avgcollectdatao.setIpaddress(node.getIpAddress() + "-" + inutilhdx.getSubentity());
										avgcollectdatao.setThevalue(inutilhdx.getThevalue());
										avgcollectdatao.setBak("入口带宽利用率");
										avgcollectdatao.setUnit("Kb/s");
										list.add(avgcollectdatao);
									}
								}
							}
						}
					}
				} else if ("oututilper".equalsIgnoreCase(type)) {
					for (int i = 0; i < networklist.size(); i++) {
						HostNode node = (HostNode) networklist.get(i);
						Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
						if (ipAllData != null) {
							Vector utilhdxperc = (Vector) ipAllData.get("utilhdxperc");
							if (utilhdxperc != null && utilhdxperc.size() > 0) {
								for (int j = 0; j < utilhdxperc.size(); j++) {
									UtilHdxPerc oututilhdx = (UtilHdxPerc) utilhdxperc.get(j);
									if ("OutBandwidthUtilHdxPerc".equalsIgnoreCase(oututilhdx.getEntity())) {
										Avgcollectdata avgcollectdatao = new Avgcollectdata();
										avgcollectdatao.setIpaddress(node.getIpAddress() + "-" + oututilhdx.getSubentity());
										avgcollectdatao.setThevalue(oututilhdx.getThevalue());
										avgcollectdatao.setBak("出口带宽利用率");
										avgcollectdatao.setUnit("Kb/s");
										list.add(avgcollectdatao);
									}
								}
							}
						}
					}
				}
			}
		}
		if (list != null && list.size() > 0) {
			for (int m = 0; m < list.size(); m++) {
				Avgcollectdata hdata = (Avgcollectdata) list.get(m);
				for (int n = m + 1; n < list.size(); n++) {
					Avgcollectdata hosdata = (Avgcollectdata) list.get(n);
					if (new Double(hdata.getThevalue().replace("%", "")).doubleValue() < new Double(hosdata.getThevalue().replace("%", "")).doubleValue()) {
						list.remove(m);
						list.add(m, hosdata);
						list.remove(n);
						list.add(n, hdata);
						hdata = hosdata;
						hosdata = null;
					}
				}
				networkList.add(hdata);
				if (networkList.size() >= size) {
					break;
				}
				hdata = null;
			}
		}
		return networkList;
	}

	// 计算网络设备内存利用率最大的内存块
	private MemoryCollectEntity getMaxUtilization(Vector memoryV) {
		MemoryCollectEntity memory = null;
		Vector networkVector = new Vector();
		if (memoryV != null && memoryV.size() > 0) {
			for (int m = 0; m < memoryV.size(); m++) {
				MemoryCollectEntity hdata = (MemoryCollectEntity) memoryV.get(m);
				for (int n = m + 1; n < memoryV.size(); n++) {
					MemoryCollectEntity hosdata = (MemoryCollectEntity) memoryV.get(n);
					if (new Double(hdata.getThevalue().replace("%", "")).doubleValue() < new Double(hosdata.getThevalue().replace("%", "")).doubleValue()) {
						memoryV.remove(m);
						memoryV.add(m, hosdata);
						memoryV.remove(n);
						memoryV.add(n, hdata);
						hdata = hosdata;
						hosdata = null;
					}
				}
				networkVector.add(hdata);
				hdata = null;
			}
			memory = (MemoryCollectEntity) networkVector.get(0);
		}
		return memory;

	}

	/**
	 * 网络设备历史数据topn
	 */
	public List getNetworkList(int category, int size, String b_time, String t_time, String type, String bids, String time) {
		List networklist = getNodeList(category, bids);
		List networkList = new ArrayList();
		List list = new ArrayList();
		if (networklist != null && networklist.size() > 0) {
			for (int i = 0; i < networklist.size(); i++) {
				HostNode node = (HostNode) networklist.get(i);
				I_HostCollectData hostManager = new HostCollectDataManager();
				String startTime = b_time + " 00:00:00";
				String endTime = t_time + " 23:59:59";
				// 从collectdata取cpu的历史数据,存放在表中
				Hashtable networkHash = new Hashtable();
				String bak = "";
				String unit = "";
				if ("ping".equalsIgnoreCase(type)) {
					bak = "连通率";
					try {
						networkHash = hostManager.getCategory(node.getIpAddress(), "Ping", "ConnectUtilization", startTime, endTime);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if ("ResponseTime".equalsIgnoreCase(type)) {
					bak = "响应时间";
					try {
						networkHash = hostManager.getCategory(node.getIpAddress(), "Ping", "ResponseTime", startTime, endTime);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if ("cpu".equalsIgnoreCase(type)) {
					bak = "CPU利用率";
					try {
						networkHash = hostManager.getCategory(node.getIpAddress(), "CPU", "Utilization", startTime, endTime);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if ("memory".equalsIgnoreCase(type)) {
					bak = "内存利用率";
					Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
					if (ipAllData != null) {
						Vector memoryV = (Vector) ipAllData.get("memory");
						if (category == 1 && memoryV != null) {
							MemoryCollectEntity memory = getMaxUtilization(memoryV);
							try {
								if (memory != null) {
									networkHash = hostManager.getCategory(node.getIpAddress(), "Memory", memory.getSubentity(), startTime, endTime);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (category == 4) {
							try {
								networkHash = hostManager.getCategory(node.getIpAddress(), "Memory", "PhysicalMemory", startTime, endTime);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				} else if ("inutil".equalsIgnoreCase(type)) {
					bak = "入口流速";
					unit = "Kb/s";
					try {
						networkHash = hostManager.getAllutilhdx(node.getIpAddress(), "AllInBandwidthUtilHdx", startTime, endTime, "");
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if ("oututil".equalsIgnoreCase(type)) {
					bak = "出口流速";
					unit = "Kb/s";
					try {
						networkHash = hostManager.getAllutilhdx(node.getIpAddress(), "AllOutBandwidthUtilHdx", startTime, endTime, "");
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if ("inutilper".equalsIgnoreCase(type)) {
					Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
					if (ipAllData != null) {
						Vector utilhdxperc = (Vector) ipAllData.get("utilhdxperc");
						if (utilhdxperc != null && utilhdxperc.size() > 0) {
							for (int j = 0; j < utilhdxperc.size(); j++) {
								UtilHdxPerc inutilhdx = (UtilHdxPerc) utilhdxperc.get(j);
								try {
									networkHash = hostManager.getUtilhdxper(node.getIpAddress(), inutilhdx.getSubentity(), startTime, endTime, "");
								} catch (Exception e) {
									e.printStackTrace();
								}
								if (networkHash.get("avgin") != null) {
									String avgcpucon = (String) networkHash.get("avgin");
									Avgcollectdata avgcollectdata = new Avgcollectdata();
									avgcollectdata.setIpaddress(node.getIpAddress() + "-" + inutilhdx.getSubentity());
									avgcollectdata.setThevalue(avgcpucon.replace("%", ""));
									avgcollectdata.setBak(bak);
									avgcollectdata.setUnit("%");
									list.add(avgcollectdata);
								}
							}
						}
					}
				} else if ("oututilper".equalsIgnoreCase(type)) {
					Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
					if (ipAllData != null) {
						Vector utilhdxperc = (Vector) ipAllData.get("utilhdxperc");
						if (utilhdxperc != null && utilhdxperc.size() > 0) {
							for (int j = 0; j < utilhdxperc.size(); j++) {
								UtilHdxPerc inutilhdx = (UtilHdxPerc) utilhdxperc.get(j);
								try {
									networkHash = hostManager.getUtilhdxper(node.getIpAddress(), inutilhdx.getSubentity(), startTime, endTime, "");
								} catch (Exception e) {
									e.printStackTrace();
								}
								if (networkHash.get("avgout") != null) {
									String avgcpucon = (String) networkHash.get("avgout");
									Avgcollectdata avgcollectdata = new Avgcollectdata();
									avgcollectdata.setIpaddress(node.getIpAddress() + "-" + inutilhdx.getSubentity());
									avgcollectdata.setThevalue(avgcpucon.replace("%", ""));
									avgcollectdata.setBak(bak);
									avgcollectdata.setUnit("%");
									list.add(avgcollectdata);
								}
							}
						}
					}
				}
				if (networkHash.get("avgpingcon") != null) {
					String avgcpucon = (String) networkHash.get("avgpingcon");
					unit = (String) networkHash.get("unit");
					Avgcollectdata avgcollectdata = new Avgcollectdata();
					avgcollectdata.setIpaddress(node.getIpAddress());
					avgcollectdata.setThevalue(avgcpucon.replace("%", "").replace("毫秒", ""));
					avgcollectdata.setBak(bak);
					avgcollectdata.setUnit(unit);
					list.add(avgcollectdata);
				}
				if ("cpu".equalsIgnoreCase(type) && networkHash.get("avgcpucon") != null) {
					String avgcpucon = (String) networkHash.get("avgcpucon");
					if (networkHash.get("unit") != null && !"null".equals(networkHash.get("unit"))) {
						unit = (String) networkHash.get("unit");
					} else {
						unit = "%";
					}
					Avgcollectdata avgcollectdata = new Avgcollectdata();
					avgcollectdata.setIpaddress(node.getIpAddress());
					avgcollectdata.setThevalue(avgcpucon.replace("%", ""));
					avgcollectdata.setBak(bak);
					avgcollectdata.setUnit(unit);
					list.add(avgcollectdata);
				}
				if ("memory".equalsIgnoreCase(type) && networkHash.get("avgmemory") != null) {
					String avgcpucon = (String) networkHash.get("avgmemory");
					unit = (String) networkHash.get("unit");
					if (networkHash.get("unit") != null && !"null".equals(networkHash.get("unit"))) {
						unit = (String) networkHash.get("unit");
					} else {
						unit = "%";
					}
					Avgcollectdata avgcollectdata = new Avgcollectdata();
					avgcollectdata.setIpaddress(node.getIpAddress());
					avgcollectdata.setThevalue(avgcpucon.replace("%", ""));
					avgcollectdata.setBak(bak);
					avgcollectdata.setUnit(unit);
					list.add(avgcollectdata);
				}
				if (("inutil".equalsIgnoreCase(type) || "oututil".equalsIgnoreCase(type)) && networkHash.get("avgput") != null) {
					String avgutilcon = (String) networkHash.get("avgput");
					if (networkHash.get("unit") != null && !"null".equals(networkHash.get("unit"))) {
						unit = (String) networkHash.get("unit");
					} else {
						unit = "kb/s";
					}
					Avgcollectdata avgcollectdata = new Avgcollectdata();
					avgcollectdata.setIpaddress(node.getIpAddress());
					avgcollectdata.setThevalue(avgutilcon);
					avgcollectdata.setBak(bak);
					avgcollectdata.setUnit(unit);
					list.add(avgcollectdata);
				}
			}
			if (list != null && list.size() > 0) {
				for (int m = 0; m < list.size(); m++) {
					Avgcollectdata hdata = (Avgcollectdata) list.get(m);
					for (int n = m + 1; n < list.size(); n++) {
						Avgcollectdata hosdata = (Avgcollectdata) list.get(n);
						if (new Double(hdata.getThevalue().replace("%", "").replace("毫秒", "")).doubleValue() < new Double(hosdata.getThevalue().replace("%", "").replace("毫秒", ""))
								.doubleValue()) {
							list.remove(m);
							list.add(m, hosdata);
							list.remove(n);
							list.add(n, hdata);
							hdata = hosdata;
							hosdata = null;
						}
					}
					// 得到排序后的Subentity的列表
					networkList.add(hdata);
					if (networkList.size() >= size - 1) {
						break;
					}
					hdata = null;
				}
			}
		}
		return networkList;
	}

	/**
	 * 获取相应ip地址在当天各个时间段的值(flex调用)
	 */
	public ArrayList<Vos> getNetworkValue(int category, String ipAddress, String b_time, String t_time, String type) {
		I_HostCollectData hostManager = new HostCollectDataManager();
		String startTime = b_time + " 00:00:00";
		String endTime = t_time + " 23:59:59";
		Hashtable networkHash = new Hashtable();
		String bak = "";
		String unit = "";
		if ("ping".equalsIgnoreCase(type)) {
			bak = "连通率";
			try {
				networkHash = hostManager.getCategory(ipAddress, "Ping", "ConnectUtilization", startTime, endTime);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("ResponseTime".equalsIgnoreCase(type)) {
			bak = "响应时间";
			try {
				networkHash = hostManager.getCategory(ipAddress, "Ping", "ResponseTime", startTime, endTime);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("cpu".equalsIgnoreCase(type)) {
			bak = "CPU利用率";
			try {
				networkHash = hostManager.getCategory(ipAddress, "CPU", "Utilization", startTime, endTime);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("memory".equalsIgnoreCase(type)) {
			bak = "内存利用率";
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ipAddress);
			if (ipAllData != null) {
				Vector memoryV = (Vector) ipAllData.get("memory");
				if (category == 1) {
					MemoryCollectEntity memory = getMaxUtilization(memoryV);
					try {
						networkHash = hostManager.getCategory(ipAddress, "Memory", memory.getSubentity(), startTime, endTime);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (category == 4) {
					try {
						networkHash = hostManager.getCategory(ipAddress, "Memory", "PhysicalMemory", startTime, endTime);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else if ("inutil".equalsIgnoreCase(type)) {
			bak = "入口流速";
			unit = "Kb/s";
			try {
				networkHash = hostManager.getAllutilhdx(ipAddress, "AllInBandwidthUtilHdx", startTime, endTime, "");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("oututil".equalsIgnoreCase(type)) {
			bak = "出口流速";
			unit = "Kb/s";
			try {
				networkHash = hostManager.getAllutilhdx(ipAddress, "AllOutBandwidthUtilHdx", startTime, endTime, "");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("inutilper".equalsIgnoreCase(type)) {
			bak = "入口带宽利用率";
			unit = "%";
			String cate[] = ipAddress.split("-");
			try {
				networkHash = hostManager.getUtilhdxper(cate[0], cate[1], startTime, endTime, "");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("oututilper".equalsIgnoreCase(type)) {
			bak = "出口带宽利用率";
			unit = "%";
			String cate[] = ipAddress.split("-");
			try {
				networkHash = hostManager.getUtilhdxper(cate[0], cate[1], startTime, endTime, "");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		List networkList = new ArrayList();
		if (networkHash.get("list") != null) {
			ArrayList<Vos> flexDataList = new ArrayList<Vos>();
			networkList = (ArrayList) networkHash.get("list");
			Vos fVo;
			for (int i = 0; i < networkList.size(); i++) {
				Vector networkVector = new Vector();
				fVo = new Vos();
				networkVector = (Vector) networkList.get(i);
				if (networkVector != null || networkVector.size() > 0) {
					fVo.setObjectNumber(((String) networkVector.get(0)).replace("%", "").replace("毫秒", ""));
					fVo.setObjectName2((String) networkVector.get(1));
					fVo.setObjectName1(ipAddress);
					fVo.setBak(bak);
					if (networkVector.get(2) != null || !"null".equals(networkVector.get(2))) {
						unit = (String) networkVector.get(2);
					}
					fVo.setUnit(unit);
					flexDataList.add(fVo);
				}
			}
			return flexDataList;
		}
		if ("inutilper".equalsIgnoreCase(type) && networkHash.get("inList") != null) {
			ArrayList<Vos> flexDataList = new ArrayList<Vos>();
			networkList = (ArrayList) networkHash.get("inList");
			Vos fVo;
			for (int i = 0; i < networkList.size(); i++) {
				Vector networkVector = new Vector();
				fVo = new Vos();
				networkVector = (Vector) networkList.get(i);
				if (networkVector != null || networkVector.size() > 0) {
					fVo.setObjectNumber(((String) networkVector.get(0)).replace("%", ""));
					fVo.setObjectName2((String) networkVector.get(1));
					fVo.setObjectName1(ipAddress);
					fVo.setBak(bak);
					fVo.setUnit(unit);
					flexDataList.add(fVo);
				}
			}
			return flexDataList;
		}
		if ("oututilper".equalsIgnoreCase(type) && networkHash.get("outList") != null) {
			ArrayList<Vos> flexDataList = new ArrayList<Vos>();
			networkList = (ArrayList) networkHash.get("outList");
			Vos fVo;
			for (int i = 0; i < networkList.size(); i++) {
				Vector networkVector = new Vector();
				fVo = new Vos();
				networkVector = (Vector) networkList.get(i);
				if (networkVector != null || networkVector.size() > 0) {
					fVo.setObjectNumber(((String) networkVector.get(0)).replace("%", ""));
					fVo.setObjectName2((String) networkVector.get(1));
					fVo.setObjectName1(ipAddress);
					fVo.setBak(bak);
					fVo.setUnit(unit);
					flexDataList.add(fVo);
				}
			}
			return flexDataList;
		}
		return null;
	}
}
