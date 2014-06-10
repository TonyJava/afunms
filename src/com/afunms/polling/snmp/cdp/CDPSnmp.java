package com.afunms.polling.snmp.cdp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.IpAliasDao;
import com.afunms.config.model.IpAlias;
import com.afunms.discovery.CdpCachEntryInterface;
import com.afunms.discovery.IfEntity;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.LinkRoad;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.IpMac;
import com.afunms.polling.om.NDP;
import com.afunms.topology.dao.HostInterfaceDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.LinkDao;
import com.afunms.topology.dao.NDPDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.Link;
import com.afunms.topology.util.TopoHelper;
import com.afunms.topology.util.XmlOperator;
import com.gatherResulttosql.NetHostNDPRttosql;

@SuppressWarnings("unchecked")
public class CDPSnmp extends SnmpMonitor {
	/**
	 * 创建URL采集任务
	 */

	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public String ciscoIP2IP(String ciscoip) {
		String[] s = ciscoip.split(":");
		if (4 == s.length) {
			return "" + Integer.parseInt(s[0], 16) + "." + Integer.parseInt(s[1], 16) + "." + Integer.parseInt(s[2], 16) + "." + Integer.parseInt(s[3], 16);
		}
		return "";
	}

	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash = new Hashtable();
		Vector cdpVector = new Vector();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if (node == null) {
			return returnHash;
		}

		Hashtable formerCDP = new Hashtable();
		Hashtable ipmacHash = new Hashtable();

		IpAliasDao nodeipaliasdao = new IpAliasDao();
		Hashtable nodeipaliasHash = new Hashtable();
		try {
			List aliasList = nodeipaliasdao.loadByIpaddress(node.getIpAddress());
			if (aliasList != null && aliasList.size() > 0) {
				for (int k = 0; k < aliasList.size(); k++) {
					IpAlias vo = (IpAlias) aliasList.get(k);
					nodeipaliasHash.put(vo.getAliasip(), vo.getIpaddress());
				}
				nodeipaliasHash.put(node.getIpAddress(), node.getIpAddress());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			nodeipaliasdao.close();
		}

		Hashtable nodelistHash = new Hashtable();
		try {
			List hostlist = PollingEngine.getInstance().getNodeList();
			if (hostlist != null && hostlist.size() > 0) {
				for (int i = 0; i < hostlist.size(); i++) {
					Host _node = (Host) hostlist.get(i);
					nodelistHash.put(_node.getIpAddress(), _node.getIpAddress());

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Calendar date = Calendar.getInstance();
			if (ShareData.getSharedata().get(node.getIpAddress()) != null) {
				if (((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).containsKey("cdp")) {
					Vector former_cdpVector = (Vector) ((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).get("cdp");
					if (former_cdpVector != null && former_cdpVector.size() > 0) {
						CdpCachEntryInterface cdp = null;
						for (int i = 0; i < former_cdpVector.size(); i++) {
							cdp = (CdpCachEntryInterface) former_cdpVector.get(i);
							formerCDP.put(cdp.getIp() + "|" + cdp.getPortdesc(), cdp);
						}
					}
					Vector ipmacVector = (Vector) ((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).get("ipmac");
					if (ipmacVector != null && ipmacVector.size() > 0) {
						IpMac ipmac = null;
						for (int i = 0; i < ipmacVector.size(); i++) {
							ipmac = (IpMac) ipmacVector.get(i);
							ipmacHash.put(ipmac.getMac(), ipmac.getIpaddress());
						}
					}
				}
			}
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
			if (ipAllData == null) {
				ipAllData = new Hashtable();
			}

			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node) PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				Date cc = date.getTime();
				String time = sdf.format(cc);
				snmpnode.setLastTime(time);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				String[] oids = new String[] { "1.3.6.1.4.1.9.9.23.1.2.1.1.4", // 1.cdpCacheAddress
						"1.3.6.1.4.1.9.9.23.1.2.1.1.7", // 2.cdpCacheDevicePort
				};
				String[][] valueArray = null;
				try {
					valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000 * 30);
				} catch (Exception e) {
					valueArray = null;
				}
				if (valueArray == null) {
					return null;
				}
				CdpCachEntryInterface cdp = null;
				for (int i = 0; i < valueArray.length; i++) {
					cdp = new CdpCachEntryInterface();
					if (valueArray[i][0] == null) {
						continue;
					}
					cdp.setIp(ciscoIP2IP(valueArray[i][0]));
					cdp.setPortdesc(valueArray[i][1]);
					cdpVector.addElement(cdp);
					SysLogger.info(node.getIpAddress() + "   deviceid:" + cdp.getIp() + "   portname:" + cdp.getPortdesc());
				}
				valueArray = null;
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		returnHash.put("cdp", cdpVector);
		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (ipAllData == null) {
				ipAllData = new Hashtable();
			}
			if (cdpVector != null && cdpVector.size() > 0) {
				ipAllData.put("cdp", cdpVector);
			}
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (cdpVector != null && cdpVector.size() > 0) {
				((Hashtable) ShareData.getSharedata().get(node.getIpAddress())).put("cdp", cdpVector);
			}

		}

		HostNodeDao hostNodeDao = new HostNodeDao();
		Hashtable nodehash = new Hashtable();
		try {
			List hostlist = hostNodeDao.loadall();
			if (hostlist != null && hostlist.size() > 0) {
				for (int i = 0; i < hostlist.size(); i++) {
					HostNode _node = (HostNode) hostlist.get(i);
					if (_node.getBridgeAddress() != null && _node.getBridgeAddress().trim().length() > 0) {
						if (_node.getBridgeAddress().contains("|")) {
							String[] macs = _node.getBridgeAddress().split("|");
							for (int k = 0; k < macs.length; k++) {
								nodehash.put(macs[k], _node.getIpAddress());
							}
						} else {
							nodehash.put(_node.getBridgeAddress(), _node.getIpAddress());
						}
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			hostNodeDao.close();
		}

		Vector newCDP = new Vector();
		if (cdpVector != null && cdpVector.size() > 0) {
			NDPDao cdpdao = new NDPDao();
			List ndplistdb = new ArrayList();
			Hashtable cdpFromDbHash = new Hashtable();
			try {
				ndplistdb = cdpdao.getbynodeid(Long.parseLong(node.getId() + ""));
				if (ndplistdb != null && ndplistdb.size() > 0) {
					NDP ndp = null;
					for (int i = 0; i < ndplistdb.size(); i++) {
						ndp = (NDP) ndplistdb.get(i);
						cdpFromDbHash.put(ndp.getDeviceId() + "|" + ndp.getPortName(), ndp);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cdpdao.close();
			}

			IpAliasDao ipaliasdao = new IpAliasDao();
			Hashtable ipaliasHash = new Hashtable();
			try {
				List aliasList = ipaliasdao.loadAll();
				if (aliasList != null && aliasList.size() > 0) {
					for (int k = 0; k < aliasList.size(); k++) {
						IpAlias vo = (IpAlias) aliasList.get(k);
						ipaliasHash.put(vo.getAliasip(), vo.getIpaddress());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ipaliasdao.close();
			}

			CdpCachEntryInterface cdp = null;
			for (int i = 0; i < cdpVector.size(); i++) {
				cdp = (CdpCachEntryInterface) cdpVector.get(i);

				if (!formerCDP.containsKey(cdp.getIp() + "|" + cdp.getPortdesc())) {
					// 找到新的NDP信息，设备有可能是新加的
					if (ipaliasHash.containsKey(cdp.getIp()) || nodelistHash.containsKey(cdp.getIp())) {
						// 已经在系统存在该设备,只更改NDP表
						newCDP.add(cdp);
						// 需要重新计算链路关系
						String ip = cdp.getIp();
						if (ipaliasHash.containsKey(cdp.getIp())) {
							ip = (String) ipaliasHash.get(cdp.getIp());
						}
						if (nodelistHash.containsKey(cdp.getIp())) {
							ip = (String) nodelistHash.get(cdp.getIp());
						}
						Host _host = (Host) PollingEngine.getInstance().getNodeByIp(ip);
						if (_host == null) {
							continue;
						}
						Hashtable portDescHash = new Hashtable();

						Vector _cdpVector = null;
						if (ShareData.getSharedata() != null && ShareData.getSharedata().containsKey(ip)) {
							if (((Hashtable) ShareData.getSharedata().get(ip)).containsKey("cdp")) {
								_cdpVector = (Vector) ((Hashtable) ShareData.getSharedata().get(ip)).get("cdp");
							}
							Vector interfaceVector = null;
							if (((Hashtable) ShareData.getSharedata().get(ip)).containsKey("interface")) {
								interfaceVector = (Vector) ((Hashtable) ShareData.getSharedata().get(ip)).get("interface");
								if (interfaceVector != null && interfaceVector.size() > 0) {
									Interfacecollectdata interfaceCollectData = null;
									for (int m = 0; m < interfaceVector.size(); m++) {
										interfaceCollectData = (Interfacecollectdata) interfaceVector.get(m);
										if ("ifDescr".equalsIgnoreCase(interfaceCollectData.getEntity())) {
											// 端口描述:端口索引
											portDescHash.put(interfaceCollectData.getThevalue(), interfaceCollectData.getSubentity());
										}
									}
								}
							}
						}

						if (_cdpVector != null && _cdpVector.size() > 0) {
							com.afunms.polling.node.IfEntity endIfEntity = null;
							IfEntity startIfEntity = null;
							if (portDescHash.containsKey(cdp.getPortdesc())) {
								// 存在改端口描述
								endIfEntity = _host.getIfEntityByIndex((String) portDescHash.get(cdp.getPortdesc()));
							}
							if (endIfEntity != null) {
								// 若存在对端接口
								CdpCachEntryInterface _cdp = null;
								for (int k = 0; k < _cdpVector.size(); k++) {
									_cdp = (CdpCachEntryInterface) _cdpVector.get(k);
									if (nodeipaliasHash.containsKey(_cdp.getIp())) {
										HostInterfaceDao ifdao = new HostInterfaceDao();
										try {
											startIfEntity = (IfEntity) ifdao.loadInterfacesByNodeIDAndDesc(node.getId(), _cdp.getPortdesc());
										} catch (Exception e) {
											e.printStackTrace();
										} finally {
											ifdao.close();
										}
										if (startIfEntity != null && endIfEntity != null) {
											Link link = new Link();
											link.setStartId(node.getId());
											link.setStartIndex(startIfEntity.getIndex());
											link.setStartIp(startIfEntity.getIpAddress() == null ? "" : startIfEntity.getIpAddress());
											link.setStartDescr(startIfEntity.getDescr());

											link.setEndId(_host.getId());
											link.setEndIndex(endIfEntity.getIndex());
											link.setEndIp(endIfEntity.getIpAddress() == null ? "" : endIfEntity.getIpAddress());
											link.setEndDescr(endIfEntity.getDescr());

											link.setMaxSpeed("200000");
											link.setMaxPer("50");
											link.setLinktype(1);
											link.setType(1);
											link.setFindtype(1);
											link.setLinkName(node.getIpAddress() + "/" + link.getStartDescr() + "-" + _host.getIpAddress() + "/" + link.getEndDescr());

											List linklist = PollingEngine.getInstance().getLinkList();
											Hashtable existLinkHash = new Hashtable();
											Hashtable existEndLinkHash = new Hashtable();
											if (linklist != null && linklist.size() > 0) {
												LinkRoad lr = null;

												for (int p = 0; p < linklist.size(); p++) {
													lr = (LinkRoad) linklist.get(p);
													existLinkHash.put(lr.getStartId() + ":" + lr.getStartIndex() + ":" + lr.getEndId() + ":" + lr.getEndIndex(), lr);
													existEndLinkHash.put(lr.getEndId() + ":" + lr.getEndIndex() + ":" + lr.getStartId() + ":" + lr.getStartIndex(), lr);

												}
											}

											if (!existLinkHash.containsKey(link.getStartId() + ":" + link.getStartIndex() + ":" + link.getEndId() + ":" + link.getEndIndex())
													&& !existLinkHash
															.containsKey(link.getEndId() + ":" + link.getEndIndex() + ":" + link.getStartId() + ":" + link.getStartIndex())
													&& !existEndLinkHash.containsKey(link.getStartId() + ":" + link.getStartIndex() + ":" + link.getEndId() + ":"
															+ link.getEndIndex())
													&& !existEndLinkHash.containsKey(link.getEndId() + ":" + link.getEndIndex() + ":" + link.getStartId() + ":"
															+ link.getStartIndex())) {
												if (link.getStartId() == link.getEndId()) {
													continue;
												}
												LinkDao linkdao = new LinkDao();
												try {
													linkdao.save(link);

													XmlOperator xopr = new XmlOperator();
													xopr.setFile("network.jsp");
													xopr.init4updateXml();
													xopr.addLine(link.getLinkName(), String.valueOf(link.getId()), "net" + String.valueOf(link.getStartId()), "net"
															+ String.valueOf(link.getEndId()));
													xopr.writeXml();

													// 链路信息实时更新
													LinkRoad lr = new LinkRoad();
													lr.setId(link.getId());
													lr.setLinkName(link.getLinkName());
													lr.setLinkName(link.getLinkName());// yangjun
													// add
													lr.setMaxSpeed(link.getMaxSpeed());// yangjun
													// add
													lr.setMaxPer(link.getMaxPer());// yangjun
													// add
													lr.setStartId(link.getStartId());
													lr.setStartIp(link.getStartIp());
													lr.setStartIndex(link.getStartIndex());
													lr.setStartDescr(link.getStartDescr());
													lr.setEndIp(link.getEndIp());
													lr.setEndId(link.getEndId());
													lr.setEndIndex(link.getEndIndex());
													lr.setEndDescr(link.getEndDescr());
													lr.setAssistant(link.getAssistant());
													lr.setType(link.getType());
													lr.setShowinterf(link.getShowinterf());
													PollingEngine.getInstance().getLinkList().add(lr);
												} catch (Exception e) {
													e.printStackTrace();
												} finally {
													linkdao.close();
												}
												break;
											}

										}

									}
								}
							}
						}
					} else {
						// 系统不存在该设备
						String ip = cdp.getIp();
						// 对这个IP地址进行SNMP添加
						SysLogger.info(ip + "     对这个IP地址进行SNMP添加1");
						try {
							if (ipaliasHash.containsKey(ip)) {
								// 已经存在该设备
								String nodeIp = "";
								if (nodelistHash.containsKey(ip)) {
									nodeIp = (String) nodelistHash.get(ip);
								}
								if (nodeIp == null || nodeIp.trim().length() == 0) {
									if (ipaliasHash.containsKey(ip)) {
										nodeIp = (String) ipaliasHash.get(ip);
									}
								}
								Host _host = null;
								if (nodeIp != null && nodeIp.trim().length() > 0) {
									_host = (Host) PollingEngine.getInstance().getNodeByIp(nodeIp);
									if (_host != null) {
										// 只计算链路关系
										continue;
									}
								}
							}

							TopoHelper helper = new TopoHelper(); // 包括更新数据库和更新内存
							int addResult = 0;
							addResult = helper.addHost(node.getAssetid(), node.getLocation(), ip, ip, node.getSnmpversion(), node.getCommunity(), node.getWritecommunity(), node
									.getTransfer(), 2, node.getOstype(), 1, node.getBid(), node.getSendmobiles(), node.getSendemail(), node.getSendphone(), node.getSupperid(),
									true, node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node.getV3_privacy(), node
											.getPrivacyPassphrase());

							List linklist = PollingEngine.getInstance().getLinkList();
							Hashtable existLinkHash = new Hashtable();
							Hashtable existEndLinkHash = new Hashtable();
							if (linklist != null && linklist.size() > 0) {
								LinkRoad lr = null;

								for (int p = 0; p < linklist.size(); p++) {
									lr = (LinkRoad) linklist.get(p);
									existLinkHash.put(lr.getStartId() + ":" + lr.getStartIndex() + ":" + lr.getEndId() + ":" + lr.getEndIndex(), lr);
									existEndLinkHash.put(lr.getEndId() + ":" + lr.getEndIndex() + ":" + lr.getStartId() + ":" + lr.getStartIndex(), lr);

								}
							}
							// 生成链路
							CDPSingleSnmp cdpsnmp = null;
							com.afunms.discovery.Host host = null;
							NodeGatherIndicators _alarmIndicatorsNode = new NodeGatherIndicators();
							try {
								host = helper.getHost();
								if (host == null) {
									continue;
								}
								HostNodeDao nodedao = new HostNodeDao();
								try {
									nodedao.addNodeByNDP(host, addResult);
									// 网络设备
									XmlOperator opr = new XmlOperator();
									opr.setFile("network.jsp");
									opr.init4updateXml();
									opr.addNode(helper.getHost());
									opr.writeXml();
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									nodedao.close();
								}

								_alarmIndicatorsNode.setNodeid(host.getId() + "");
								cdpsnmp = (CDPSingleSnmp) Class.forName("com.afunms.polling.snmp.cdp.CDPSingleSnmp").newInstance();
								returnHash = cdpsnmp.collect_Data(_alarmIndicatorsNode);
								IfEntity endIfEntity = host.getIfEntityByDesc(cdp.getPortdesc());
								IfEntity startIfEntity = null;
								if (returnHash != null && returnHash.containsKey("cdp")) {
									Vector _cdpVector = (Vector) returnHash.get("cdp");
									if (_cdpVector != null && _cdpVector.size() > 0) {
										CdpCachEntryInterface _cdp = null;
										for (int k = 0; k < _cdpVector.size(); k++) {
											_cdp = (CdpCachEntryInterface) _cdpVector.get(k);

											if (nodeipaliasHash.containsKey(_cdp.getIp())) {
												HostInterfaceDao ifdao = new HostInterfaceDao();
												try {
													startIfEntity = (IfEntity) ifdao.loadInterfacesByNodeIDAndDesc(node.getId(), _cdp.getPortdesc());
												} catch (Exception e) {
													e.printStackTrace();
												} finally {
													ifdao.close();
												}
												if (startIfEntity != null && endIfEntity != null) {
													Link link = new Link();
													link.setStartId(node.getId());
													link.setStartIndex(startIfEntity.getIndex());
													link.setStartIp(startIfEntity.getIpAddress() == null ? "" : startIfEntity.getIpAddress());
													link.setStartDescr(startIfEntity.getDescr());

													link.setEndId(host.getId());
													link.setEndIndex(endIfEntity.getIndex());
													link.setEndIp(endIfEntity.getIpAddress() == null ? "" : endIfEntity.getIpAddress());
													link.setEndDescr(endIfEntity.getDescr());

													link.setMaxSpeed("200000");
													link.setMaxPer("50");
													link.setLinktype(1);
													link.setType(1);
													link.setFindtype(1);
													link.setLinkName(node.getIpAddress() + "/" + link.getStartDescr() + "-" + host.getIpAddress() + "/" + link.getEndDescr());

													if (!existLinkHash.containsKey(link.getStartId() + ":" + link.getStartIndex() + ":" + link.getEndId() + ":"
															+ link.getEndIndex())
															&& !existLinkHash.containsKey(link.getEndId() + ":" + link.getEndIndex() + ":" + link.getStartId() + ":"
																	+ link.getStartIndex())
															&& !existEndLinkHash.containsKey(link.getStartId() + ":" + link.getStartIndex() + ":" + link.getEndId() + ":"
																	+ link.getEndIndex())
															&& !existEndLinkHash.containsKey(link.getEndId() + ":" + link.getEndIndex() + ":" + link.getStartId() + ":"
																	+ link.getStartIndex())) {
														if (link.getStartId() == link.getEndId()) {
															continue;
														}
														LinkDao linkdao = new LinkDao();
														try {
															linkdao.save(link);

															XmlOperator xopr = new XmlOperator();
															xopr.setFile("network.jsp");
															xopr.init4updateXml();
															xopr.addLine(link.getLinkName(), String.valueOf(link.getId()), "net" + String.valueOf(link.getStartId()), "net"
																	+ String.valueOf(link.getEndId()));
															xopr.writeXml();

															// 链路信息实时更新
															LinkRoad lr = new LinkRoad();
															lr.setId(link.getId());
															lr.setLinkName(link.getLinkName());
															lr.setLinkName(link.getLinkName());// yangjun
															// add
															lr.setMaxSpeed(link.getMaxSpeed());// yangjun
															// add
															lr.setMaxPer(link.getMaxPer());// yangjun
															// add
															lr.setStartId(link.getStartId());
															lr.setStartIp(link.getStartIp());
															lr.setStartIndex(link.getStartIndex());
															lr.setStartDescr(link.getStartDescr());
															lr.setEndIp(link.getEndIp());
															lr.setEndId(link.getEndId());
															lr.setEndIndex(link.getEndIndex());
															lr.setEndDescr(link.getEndDescr());
															lr.setAssistant(link.getAssistant());
															lr.setType(link.getType());
															lr.setShowinterf(link.getShowinterf());
															PollingEngine.getInstance().getLinkList().add(lr);
														} catch (Exception e) {
															e.printStackTrace();
														} finally {
															linkdao.close();
														}
														break;
													}

												}

											}
										}
									}
								}

							} catch (Exception e) {
								e.printStackTrace();
							} finally {

							}

							newCDP.add(cdp);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					// 若在上一次采集中有该CDP信息，则需要判断数据库中是否有，若数据库中没有，
					// 则说明上次没有对该CDP信息进行发现
					if (!cdpFromDbHash.containsKey(cdp.getIp() + "|" + cdp.getPortdesc())) {
						// 找到新的CDP信息，设备有可能是新加的
						if (ipaliasHash.containsKey(cdp.getIp()) || nodelistHash.containsKey(cdp.getIp())) {
							// 已经在系统存在该设备,只更改CDP表
							newCDP.add(cdp);
							// 需要重新计算链路关系
							String ip = "";
							if (ipaliasHash.containsKey(cdp.getIp())) {
								ip = (String) ipaliasHash.get(cdp.getIp());
							}
							if (nodelistHash.containsKey(cdp.getIp())) {
								ip = (String) nodelistHash.get(cdp.getIp());
							}
							Host _host = (Host) PollingEngine.getInstance().getNodeByIp(ip);
							if (_host == null) {
								continue;
							}
							Hashtable portDescHash = new Hashtable();

							Vector _cdpVector = null;
							if (ShareData.getSharedata() != null && ShareData.getSharedata().containsKey(ip)) {
								if (((Hashtable) ShareData.getSharedata().get(ip)).containsKey("cdp")) {
									_cdpVector = (Vector) ((Hashtable) ShareData.getSharedata().get(ip)).get("cdp");
								}
								Vector interfaceVector = null;
								if (((Hashtable) ShareData.getSharedata().get(ip)).containsKey("interface")) {
									interfaceVector = (Vector) ((Hashtable) ShareData.getSharedata().get(ip)).get("interface");
									if (interfaceVector != null && interfaceVector.size() > 0) {
										Interfacecollectdata interfaceCollectData = null;
										for (int m = 0; m < interfaceVector.size(); m++) {
											interfaceCollectData = (Interfacecollectdata) interfaceVector.get(m);
											if ("ifDescr".equalsIgnoreCase(interfaceCollectData.getEntity())) {
												// 端口描述:端口索引
												SysLogger.info(ip + "  " + interfaceCollectData.getThevalue() + "  " + interfaceCollectData.getSubentity());
												portDescHash.put(interfaceCollectData.getThevalue(), interfaceCollectData.getSubentity());
											}
										}
									}
								}
							}
							if (_cdpVector != null && _cdpVector.size() > 0) {
								com.afunms.polling.node.IfEntity endIfEntity = null;
								IfEntity startIfEntity = null;
								if (portDescHash.containsKey(cdp.getPortdesc())) {
									// 存在改端口描述
									endIfEntity = _host.getIfEntityByIndex((String) portDescHash.get(cdp.getPortdesc()));
								}
								if (endIfEntity != null) {
									// 若存在对端接口
									CdpCachEntryInterface _cdp = null;
									for (int k = 0; k < _cdpVector.size(); k++) {
										_cdp = (CdpCachEntryInterface) _cdpVector.get(k);
										if (nodeipaliasHash.containsKey(_cdp.getIp())) {
											HostInterfaceDao ifdao = new HostInterfaceDao();
											try {
												startIfEntity = (IfEntity) ifdao.loadInterfacesByNodeIDAndDesc(node.getId(), _cdp.getPortdesc());
											} catch (Exception e) {
												e.printStackTrace();
											} finally {
												ifdao.close();
											}
											if (startIfEntity != null && endIfEntity != null) {
												Link link = new Link();
												link.setStartId(node.getId());
												link.setStartIndex(startIfEntity.getIndex());
												link.setStartIp(startIfEntity.getIpAddress() == null ? "" : startIfEntity.getIpAddress());
												link.setStartDescr(startIfEntity.getDescr());

												link.setEndId(_host.getId());
												link.setEndIndex(endIfEntity.getIndex());
												link.setEndIp(endIfEntity.getIpAddress() == null ? "" : endIfEntity.getIpAddress());
												link.setEndDescr(endIfEntity.getDescr());

												link.setMaxSpeed("200000");
												link.setMaxPer("50");
												link.setLinktype(1);
												link.setType(1);
												link.setFindtype(1);
												link.setLinkName(node.getIpAddress() + "/" + link.getStartDescr() + "-" + _host.getIpAddress() + "/" + link.getEndDescr());

												List linklist = PollingEngine.getInstance().getLinkList();
												Hashtable existLinkHash = new Hashtable();
												Hashtable existEndLinkHash = new Hashtable();
												if (linklist != null && linklist.size() > 0) {
													LinkRoad lr = null;

													for (int p = 0; p < linklist.size(); p++) {
														lr = (LinkRoad) linklist.get(p);
														existLinkHash.put(lr.getStartId() + ":" + lr.getStartIndex() + ":" + lr.getEndId() + ":" + lr.getEndIndex(), lr);
														existEndLinkHash.put(lr.getEndId() + ":" + lr.getEndIndex() + ":" + lr.getStartId() + ":" + lr.getStartIndex(), lr);

													}
												}
												if (!existLinkHash.containsKey(link.getStartId() + ":" + link.getStartIndex() + ":" + link.getEndId() + ":" + link.getEndIndex())
														&& !existLinkHash.containsKey(link.getEndId() + ":" + link.getEndIndex() + ":" + link.getStartId() + ":"
																+ link.getStartIndex())
														&& !existEndLinkHash.containsKey(link.getStartId() + ":" + link.getStartIndex() + ":" + link.getEndId() + ":"
																+ link.getEndIndex())
														&& !existEndLinkHash.containsKey(link.getEndId() + ":" + link.getEndIndex() + ":" + link.getStartId() + ":"
																+ link.getStartIndex())) {
													if (link.getStartId() == link.getEndId()) {
														continue;
													}
													LinkDao linkdao = new LinkDao();
													try {
														linkdao.save(link);

														XmlOperator xopr = new XmlOperator();
														xopr.setFile("network.jsp");
														xopr.init4updateXml();
														xopr.addLine(link.getLinkName(), String.valueOf(link.getId()), "net" + String.valueOf(link.getStartId()), "net"
																+ String.valueOf(link.getEndId()));
														xopr.writeXml();

														// 链路信息实时更新
														LinkRoad lr = new LinkRoad();
														lr.setId(link.getId());
														lr.setLinkName(link.getLinkName());
														lr.setLinkName(link.getLinkName());// yangjun
														// add
														lr.setMaxSpeed(link.getMaxSpeed());// yangjun
														// add
														lr.setMaxPer(link.getMaxPer());// yangjun
														// add
														lr.setStartId(link.getStartId());
														lr.setStartIp(link.getStartIp());
														lr.setStartIndex(link.getStartIndex());
														lr.setStartDescr(link.getStartDescr());
														lr.setEndIp(link.getEndIp());
														lr.setEndId(link.getEndId());
														lr.setEndIndex(link.getEndIndex());
														lr.setEndDescr(link.getEndDescr());
														lr.setAssistant(link.getAssistant());
														lr.setType(link.getType());
														lr.setShowinterf(link.getShowinterf());
														PollingEngine.getInstance().getLinkList().add(lr);
													} catch (Exception e) {
														e.printStackTrace();
													} finally {
														linkdao.close();
													}
													break;
												}

											}

										}
									}
								}
							}

						} else {
							// 系统不存在该设备
							String ip = cdp.getIp();
							// 判断该IP是否已经在系统中存在
							com.afunms.discovery.Host host = null;
							// 对这个IP地址进行SNMP添加
							try {

								if (ipaliasHash.containsKey(ip)) {
									// 已经存在该设备
									String nodeIp = "";
									if (nodelistHash.containsKey(ip)) {
										nodeIp = (String) nodelistHash.get(ip);
									}
									if (nodeIp == null || nodeIp.trim().length() == 0) {
										if (ipaliasHash.containsKey(ip)) {
											nodeIp = (String) ipaliasHash.get(ip);
										}
									}
									Host _host = null;
									if (nodeIp != null && nodeIp.trim().length() > 0) {
										_host = (Host) PollingEngine.getInstance().getNodeByIp(nodeIp);
										if (_host != null) {
											// 只计算链路关系
											continue;
										}
									}
								}

								TopoHelper helper = new TopoHelper(); // 包括更新数据库和更新内存
								int addResult = 0;
								addResult = helper.addHost(node.getAssetid(), node.getLocation(), ip, ip, node.getSnmpversion(), node.getCommunity(), node.getWritecommunity(),
										node.getTransfer(), 2, node.getOstype(), 1, node.getBid(), node.getSendmobiles(), node.getSendemail(), node.getSendphone(), node
												.getSupperid(), true, node.getSecuritylevel(), node.getSecurityName(), node.getV3_ap(), node.getAuthpassphrase(), node
												.getV3_privacy(), node.getPrivacyPassphrase());

								// 生成链路
								CDPSingleSnmp cdpsnmp = null;
								NodeGatherIndicators _alarmIndicatorsNode = new NodeGatherIndicators();
								try {
									host = helper.getHost();
									if (host == null) {
										continue;
									}
									HostNodeDao nodedao = new HostNodeDao();
									try {
										nodedao.addNodeByNDP(host, addResult);
										// 网络设备
										XmlOperator opr = new XmlOperator();
										opr.setFile("network.jsp");
										opr.init4updateXml();
										opr.addNode(helper.getHost());
										opr.writeXml();
									} catch (Exception e) {

									} finally {
										nodedao.close();
									}

									_alarmIndicatorsNode.setNodeid(host.getId() + "");
									cdpsnmp = (CDPSingleSnmp) Class.forName("com.afunms.polling.snmp.cdp.CDPSingleSnmp").newInstance();
									returnHash = cdpsnmp.collect_Data(_alarmIndicatorsNode);

									IfEntity endIfEntity = host.getIfEntityByDesc(cdp.getPortdesc());
									IfEntity startIfEntity = null;
									if (returnHash != null && returnHash.containsKey("cdp")) {
										Vector _cdpVector = (Vector) returnHash.get("cdp");
										if (_cdpVector != null && _cdpVector.size() > 0) {

											CdpCachEntryInterface _cdp = null;
											for (int k = 0; k < _cdpVector.size(); k++) {
												_cdp = (CdpCachEntryInterface) _cdpVector.get(k);
												if (nodeipaliasHash.containsKey(_cdp.getIp())) {
													HostInterfaceDao ifdao = new HostInterfaceDao();
													try {
														startIfEntity = (IfEntity) ifdao.loadInterfacesByNodeIDAndDesc(node.getId(), _cdp.getPortdesc());
													} catch (Exception e) {
														e.printStackTrace();
													} finally {
														ifdao.close();
													}
													if (startIfEntity != null && endIfEntity != null) {
														Link link = new Link();
														link.setStartId(node.getId());
														link.setStartIndex(startIfEntity.getIndex());
														link.setStartIp(startIfEntity.getIpAddress() == null ? "" : startIfEntity.getIpAddress());
														link.setStartDescr(startIfEntity.getDescr());

														link.setEndId(host.getId());
														link.setEndIndex(endIfEntity.getIndex());
														link.setEndIp(endIfEntity.getIpAddress() == null ? "" : endIfEntity.getIpAddress());
														link.setEndDescr(endIfEntity.getDescr());

														link.setMaxSpeed("200000");
														link.setMaxPer("50");
														link.setLinktype(1);
														link.setType(1);
														link.setFindtype(1);
														link.setLinkName(node.getIpAddress() + "/" + link.getStartDescr() + "-" + host.getIpAddress() + "/" + link.getEndDescr());

														List linklist = PollingEngine.getInstance().getLinkList();
														Hashtable existLinkHash = new Hashtable();
														Hashtable existEndLinkHash = new Hashtable();
														if (linklist != null && linklist.size() > 0) {
															LinkRoad lr = null;

															for (int p = 0; p < linklist.size(); p++) {
																lr = (LinkRoad) linklist.get(p);
																existLinkHash.put(lr.getStartId() + ":" + lr.getStartIndex() + ":" + lr.getEndId() + ":" + lr.getEndIndex(), lr);
																existEndLinkHash.put(lr.getEndId() + ":" + lr.getEndIndex() + ":" + lr.getStartId() + ":" + lr.getStartIndex(), lr);

															}
														}
														if (!existLinkHash.containsKey(link.getStartId() + ":" + link.getStartIndex() + ":" + link.getEndId() + ":"
																+ link.getEndIndex())
																&& !existLinkHash.containsKey(link.getEndId() + ":" + link.getEndIndex() + ":" + link.getStartId() + ":"
																		+ link.getStartIndex())
																&& !existEndLinkHash.containsKey(link.getStartId() + ":" + link.getStartIndex() + ":" + link.getEndId() + ":"
																		+ link.getEndIndex())
																&& !existEndLinkHash.containsKey(link.getEndId() + ":" + link.getEndIndex() + ":" + link.getStartId() + ":"
																		+ link.getStartIndex())) {
															if (link.getStartId() == link.getEndId()) {
																continue;
															}
															LinkDao linkdao = new LinkDao();
															try {
																linkdao.save(link);

																XmlOperator xopr = new XmlOperator();
																xopr.setFile("network.jsp");
																xopr.init4updateXml();
																xopr.addLine(link.getLinkName(), String.valueOf(link.getId()), "net" + String.valueOf(link.getStartId()), "net"
																		+ String.valueOf(link.getEndId()));
																xopr.writeXml();

																// 链路信息实时更新
																LinkRoad lr = new LinkRoad();
																lr.setId(link.getId());
																lr.setLinkName(link.getLinkName());
																lr.setLinkName(link.getLinkName());// yangjun
																// add
																lr.setMaxSpeed(link.getMaxSpeed());// yangjun
																// add
																lr.setMaxPer(link.getMaxPer());// yangjun
																// add
																lr.setStartId(link.getStartId());
																lr.setStartIp(link.getStartIp());
																lr.setStartIndex(link.getStartIndex());
																lr.setStartDescr(link.getStartDescr());
																lr.setEndIp(link.getEndIp());
																lr.setEndId(link.getEndId());
																lr.setEndIndex(link.getEndIndex());
																lr.setEndDescr(link.getEndDescr());
																lr.setAssistant(link.getAssistant());
																lr.setType(link.getType());
																lr.setShowinterf(link.getShowinterf());
																PollingEngine.getInstance().getLinkList().add(lr);
															} catch (Exception e) {
																e.printStackTrace();
															} finally {
																linkdao.close();
															}
															break;
														}

													}

												}
											}

										}
									}

								} catch (Exception e) {
									e.printStackTrace();
								}
								newCDP.add(cdp);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}

				}
			}
		}
		// 把采集结果生成sql
		NetHostNDPRttosql ndptosql = new NetHostNDPRttosql();
		ndptosql.CreateResultTosql(newCDP, node);

		cdpVector = null;
		return returnHash;
	}
}
