/**
 * <p>Description:Discover Complete</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-17
 */

package com.afunms.discovery;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;

/**
 * 发现完成后要做3件事: 1.数据入库,生成拓扑图XML; 2.释放发现程序 3.初始化轮询程序
 */

@SuppressWarnings("unchecked")
public class DiscoverComplete_SOLO {
	private Logger logger = Logger.getLogger(this.getClass());

	private DiscoverDataHelper helper = new DiscoverDataHelper();
	private List<NodeToNodeLink> links = new ArrayList<NodeToNodeLink>();

	private List<MacToNodeLink> maclinks = new ArrayList<MacToNodeLink>();

	private HashMap<Integer, Host> bridgeNodes = new HashMap<Integer, Host>();

	private List<Host> routerNodes = new ArrayList<Host>();

	private List<Host> cdpNodes = new ArrayList<Host>();

	private List<Host> ndpNodes = new ArrayList<Host>();

	private List<Host> atNodes = new ArrayList<Host>();

	private List<String> macsParsed = new ArrayList<String>();

	private List<String> macsExcluded = new ArrayList<String>();

	private Map<String, List<AtInterface>> macToAtinterface = new HashMap<String, List<AtInterface>>();

	public void completed(boolean discoverOk) {
		try {
			analyseTopoLinks();
		} catch (Exception e) {
			e.printStackTrace();
		}

		helper.memory2DB();
		helper.DB2NetworkXml();
		helper.DB2NetworkVlanXml();
		helper.DB2ServerXml();

		updateSystemXml();
		createHtml();
		unloadDiscover();
		PollingEngine.getInstance().doPolling();
	}

	private void unloadDiscover() {
		DiscoverEngine.getInstance().unload();
		DiscoverResource.getInstance().unload();
		// 需要添加一个把全部的发现线程destroy掉,防止内存溢出
		List threadList = DiscoverEngine.getInstance().getThreadList();
		if (threadList != null && threadList.size() > 0) {
			for (int i = 0; i < threadList.size(); i++) {
				BaseThread bt = (BaseThread) threadList.get(i);
				bt.setCompleted(true);
				bt = null;
			}
		}
	}

	private void createHtml() {
		DiscoverMonitor monitor = DiscoverMonitor.getInstance();

		StringBuffer htmlFile = new StringBuffer(1000);
		htmlFile.append("<html><head><title>");
		htmlFile.append(SysUtil.getCurrentDate());
		htmlFile.append("</title></head>");
		htmlFile.append("<body bgcolor='#9FB0C4'>\n");
		htmlFile.append("<table width='500' align='center'>\n");
		htmlFile
				.append("<tr><td align='center'><font color='blue'><b>发现进程监视</b><input type=button class='button' value='停止发现' onclick='/afunms/user.do?action=logout'></font></td></tr>\n");
		htmlFile.append("<tr><td valign='top' align='center'>\n");
		htmlFile.append("<table width='100%' border=1 cellspacing=0 cellpadding=0 bordercolorlight='#000000' bordercolordark='#FFFFFF'>");
		htmlFile.append("<tr><td>开始时间</td><td>");
		htmlFile.append(monitor.getStartTime());
		htmlFile.append("</td></tr>");
		htmlFile.append("<tr><td>结束时间</td><td>");
		htmlFile.append(monitor.getEndTime());
		htmlFile.append("</td></tr>");
		htmlFile.append("<tr><td>已经耗时</td><td>");
		htmlFile.append(monitor.getElapseTime());
		htmlFile.append("</td></tr></table></td></tr>");
		htmlFile.append("<tr><td>\n");
		htmlFile.append("<table width='100%' border=1 cellspacing=0 cellpadding=0 bordercolorlight='#000000' bordercolordark='#FFFFFF'>");
		htmlFile.append("<tr bgcolor='#D4E1D5'><td>&nbsp;</td><td><b>总数</b></td></tr>\n");
		htmlFile.append("<tr><td>设备</td><td>");
		htmlFile.append(monitor.getHostTotal());
		htmlFile.append("</td></tr>");
		htmlFile.append("<tr><td>子网</td><td>");
		htmlFile.append(monitor.getSubNetTotal());
		htmlFile.append("</tr></table></td></tr>");
		htmlFile.append("<tr><td align='center'><font color='blue'><b><br>详细</b></font></td></tr><tr><td>");
		htmlFile.append(monitor.getResultTable());
		htmlFile.append("</td></tr></table></body></html>");
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;

		try {
			fos = new FileOutputStream(ResourceCenter.getInstance().getSysPath() + "topology\\discover\\discover.html");
			osw = new OutputStreamWriter(fos, "GB2312");
			osw.write(htmlFile.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				osw.close();
			} catch (Exception ee) {
			}
		}
	}

	private void updateSystemXml() {
		SAXBuilder builder = null;
		Document doc = null;
		try {
			String fullPath = ResourceCenter.getInstance().getSysPath() + "WEB-INF\\classes\\system-config.xml";
			FileInputStream fis = new FileInputStream(fullPath);

			builder = new SAXBuilder();
			doc = builder.build(fis);
			Element ele = doc.getRootElement().getChild("has_discoverd");
			ele.setText("true");

			Format format = Format.getCompactFormat();
			format.setEncoding("UTF-8");
			format.setIndent("	");
			XMLOutputter serializer = new XMLOutputter(format);
			FileOutputStream fos = new FileOutputStream(fullPath);
			serializer.output(doc, fos);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 找出所有交换机间可能有的链路 2007.3.12衡水信用社
	 */
	@SuppressWarnings("unused")
	private void analyseLinks() {
		List list = DiscoverEngine.getInstance().getHostList();

		int loop = 0;
		Host firstSwitch = null;
		for (int i = 0; i < list.size(); i++) {
			Host host = (Host) list.get(i);
			if (host.getCategory() == 2 || host.getCategory() == 3) {
				loop++;
				if (loop == 1)
					firstSwitch = host;
				else
					firstSwitch.addSwitchId(host.getId());
			}
		}
		if (firstSwitch != null) {
			LinkProber linkProber = new LinkProber(firstSwitch);
			List allLinks = linkProber.confirmLinks();
			DiscoverEngine.getInstance().addLinks(allLinks);
		}
	}

	private synchronized void analyseTopoLinks() {
		Iterator<Host> ite = null;
		List hostlist = DiscoverEngine.getInstance().getHostList();
		if (hostlist != null && hostlist.size() > 0) {
			for (int i = 0; i < hostlist.size(); i++) {
				try {
					Host host = (Host) hostlist.get(i);
					logger.info(host.toString());
					if (host == null) {
						logger.error("节点为空值，继续进行下一步操作");
						continue;
					}
					// 过滤掉非网络设备
					if (host.getCategory() != 1 && host.getCategory() != 2 && host.getCategory() != 3 && host.getCategory() != 7)
						continue;
					if (host.getBridgestpList() != null && host.getBridgestpList().size() > 0) {
						bridgeNodes.put(new Integer(host.getId()), host);
					}
					if (host.getCdpList() != null && host.getCdpList().size() > 0) {
						cdpNodes.add(host);
					}
					if (host.getNdpHash() != null && host.getNdpHash().size() > 0) {
						ndpNodes.add(host);
					}
					if (host.getRouteList() != null && host.getRouteList().size() > 0) {
						routerNodes.add(host);
					}

					if (host.getAtInterfaces() != null && host.getAtInterfaces().size() > 0) {
						List atInterfaces = host.getAtInterfaces();
						// 将HOST的ID补充进去
						List atList = new ArrayList();
						for (int k = 0; k < atInterfaces.size(); k++) {
							AtInterface at = (AtInterface) atInterfaces.get(k);
							AtInterface _at = new AtInterface(host.getId(), at.getIpAddress(), at.getMacAddress(), at.getIfindex());
							atList.add(_at);
						}
						if (atList != null && atList.size() > 0)
							host.setAtInterfaces(atList);
						atNodes.add(host);
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}

		logger.info("运行: 用atNodes to populate macToAtinterface");

		ite = atNodes.iterator();
		while (ite.hasNext()) {
			Host host = ite.next();
			List atInterfaces = host.getAtInterfaces();
			if (atInterfaces != null && atInterfaces.size() > 0) {
				for (int k = 0; k < atInterfaces.size(); k++) {
					try {
						AtInterface at = (AtInterface) atInterfaces.get(k);
						int nodeid = host.getId();
						String ipaddr = at.getIpAddress();
						String macAddress = at.getMacAddress();
						logger.info("解析 at Interface nodeid/ipaddr/macaddr: " + nodeid + "/" + ipaddr + "/" + macAddress);
						if (isMacIdentifierOfBridgeNode(macAddress)) {
							logger.info("运行: at interface " + macAddress + " belongs to bridge node! Not adding to discoverable atinterface.");
							macsExcluded.add(macAddress);
							continue;
						}
						List<AtInterface> ats = macToAtinterface.get(macAddress);
						if (ats == null)
							ats = new ArrayList<AtInterface>();
						logger.info("parseAtNodes: Adding to discoverable atinterface.");
						ats.add(at);
						macToAtinterface.put(macAddress, ats);
						logger.info("parseAtNodes: mac:" + macAddress + " has now atinterface reference: " + ats.size());
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(e.getMessage());
					}
				}
			}
		}

		logger.info("运行: end populate macToAtinterface");

		// First of all use quick methods to get backbone ports for speeding
		// up the link discovery
		logger.info("运行: finding links among nodes using Cisco Discovery Protocol");

		logger.info("利用Cisco Discovery Protocol发现节点间的连接");
		// Try Cisco Discovery Protocol to found link among all nodes
		// Add CDP info for backbones ports

		ite = cdpNodes.iterator();
		while (ite.hasNext()) {
			try {
				Host host = ite.next();
				int curCdpNodeId = host.getId();
				List executedPort = new ArrayList();
				Iterator<CdpCachEntryInterface> sub_ite = host.getCdpList().iterator();
				while (sub_ite.hasNext()) {
					try {
						CdpCachEntryInterface cdpIface = sub_ite.next();
						String targetIpAddr = cdpIface.getIp();
						// 判断是否是已经存在在host列表里的IP
						Host targetHost = DiscoverEngine.getInstance().getHostByAliasIP(targetIpAddr);
						if (targetHost == null) {
							logger.info("IP地址" + targetIpAddr + "不在已发现的网络设备里，跳过");
							continue;
						}

						int targetCdpNodeId = targetHost.getId();
						if (targetCdpNodeId == -1 || targetCdpNodeId == 0) {
							logger.info("没发现网络设备IP " + targetHost.getIpAddress() + "的ID，跳过");
							continue;
						}
						if (targetCdpNodeId == curCdpNodeId) {
							logger.info("运行: 该IP为自身IP " + targetIpAddr + " 跳过");
							continue;
						}

						int cdpIfIndex = -1;
						if (targetHost.getCdpList() != null && targetHost.getCdpList().size() > 0) {
							Iterator<CdpCachEntryInterface> target_ite = targetHost.getCdpList().iterator();
							while (target_ite.hasNext()) {
								CdpCachEntryInterface targetcdpIface = target_ite.next();
								if (host.getAliasIPs().contains(targetcdpIface.getIp())) {
									// 需要加如当前PORTDESC是否已经被处理过的条件
									if (executedPort.contains(targetcdpIface.getPortdesc()))
										continue;
									if (host.getIfEntityByDesc(targetcdpIface.getPortdesc()) != null) {
										cdpIfIndex = Integer.parseInt(host.getIfEntityByDesc(targetcdpIface.getPortdesc()).getIndex());
										executedPort.add(targetcdpIface.getPortdesc());
										break;
									}
								}
							}
						}
						if (cdpIfIndex <= 0) {
							// 用逻辑端口代替
							cdpIfIndex = Integer.parseInt(host.getIfEntityByIP(host.getIpAddress()).getIndex());
							logger.info("不是合法的CDP IfIndex，用逻辑端口代替");
						} else {
							logger.info("发现合法的 CDP ifindex " + cdpIfIndex);
						}

						logger.info("运行: 发现 nodeid/CDP 目标IP: " + targetCdpNodeId + ":" + targetIpAddr);
						int cdpDestIfindex = -1;
						if (targetHost.getIfEntityByDesc(cdpIface.getPortdesc()) != null) {
							cdpDestIfindex = Integer.parseInt(targetHost.getIfEntityByDesc(cdpIface.getPortdesc()).getIndex());
							if (cdpDestIfindex < 0) {
								logger.info("运行：不合法的CDP destination IfIndex " + cdpDestIfindex + ". 跳过");
								continue;
							}
						} else {
							logger.info("运行：不合法的CDP destination. 跳过");
							continue;
						}
						logger.info("运行： 发现 CDP target ifindex " + cdpDestIfindex);

						logger.info("运行: 解析 CDP link: nodeid=" + curCdpNodeId + " ifindex=" + cdpIfIndex + " nodeparentid=" + targetCdpNodeId + " parentifindex=" + cdpDestIfindex);

						boolean add = false;
						logger.info("运行: no node is bridge node! Adding CDP link");
						add = true;
						if (add) {
							NodeToNodeLink lk = new NodeToNodeLink(targetCdpNodeId, cdpDestIfindex);
							lk.setFindtype(SystemConstant.ISCDP);
							lk.setNodeparentid(curCdpNodeId);
							lk.setParentifindex(cdpIfIndex);
							addNodetoNodeLink(lk);
							logger.info("运行: CDP link added: " + lk.toString());
						}
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(e.getMessage());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}

		logger.info("利用HUAWEI 的 Network Discovery Protocol发现节点间的连接");
		ite = ndpNodes.iterator();
		while (ite.hasNext()) {
			try {
				Host host = ite.next();
				if (host.getNdpHash() != null && host.getNdpHash().size() > 0) {
					Iterator<String> sub_ite = host.getNdpHash().keySet().iterator();
					while (sub_ite.hasNext()) {
						try {
							String endndpMac = sub_ite.next();
							String endndpDescr = (String) host.getNdpHash().get(endndpMac);
							Host endNode = getNodeFromMacIdentifierOfNdpNode(endndpMac);
							if (endNode == null) {
								logger.info("找不到MAC地址" + endndpMac + ",在已发现的网络设备里，跳过");
								continue;
							}
							IfEntity endIfEntity = endNode.getIfEntityByDesc(endndpDescr);
							IfEntity startIfEntity = null;
							if (endIfEntity == null) {
								logger.info("找不到端口描述为" + endndpDescr + ",在已发现的网络设备里，跳过");
							}
							// 寻找开始端的连接
							// 默认情况下,endNode的NdpHash不为空
							Hashtable endNodeNdpHash = endNode.getNdpHash();
							if (endNodeNdpHash == null)
								endNodeNdpHash = new Hashtable();
							if (host.getMac() == null)
								continue;
							if (endNodeNdpHash.containsKey(host.getMac())) {
								// 存在该IP
								String ndpDescr = (String) endNodeNdpHash.get(host.getMac());
								startIfEntity = endNode.getIfEntityByDesc(ndpDescr);
							}

							if (startIfEntity == null) {
								startIfEntity = host.getIfEntityByIP(host.getIpAddress());
								if (startIfEntity == null)
									continue;

							}
							if (endIfEntity == null) {
								endIfEntity = endNode.getIfEntityByIP(endNode.getIpAddress());
								if (endIfEntity == null)
									continue;

							}
							if (startIfEntity != null && endIfEntity != null) {
								// 两个连接都存在
								if (host.getId() == endNode.getId()) {
									logger.info("运行: 该连接为自身, 跳过");
									continue;
								}
							}

							logger.info("运行: 发现 nodeid/NDP 目标IP: " + endNode.getId() + ":" + endNode.getIpAddress());

							logger.info("运行: 解析 NDP link: nodeid=" + host.getId() + " ifindex=" + startIfEntity.getIndex() + " nodeparentid=" + endNode.getId() + " parentifindex="
									+ endIfEntity.getIndex());

							boolean add = false;
							// now add the cdp link
							logger.info("运行: no node is bridge node! Adding NDP link");
							add = true;
							if (add) {
								NodeToNodeLink lk = new NodeToNodeLink(endNode.getId(), Integer.parseInt(endIfEntity.getIndex()));
								lk.setFindtype(SystemConstant.ISNDP);
								lk.setNodeparentid(host.getId());
								lk.setParentifindex(Integer.parseInt(startIfEntity.getIndex()));
								addNodetoNodeLink(lk);
								logger.info("运行: NDP link added: " + lk.toString());
							}
						} catch (Exception e) {
							e.printStackTrace();
							logger.error(e.getMessage());
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
		// try get backbone links between switches using STP info
		// and store information in Bridge class
		logger.info("运行: try to found backbone ethernet links among bridge nodes using Spanning Tree Protocol");

		ite = bridgeNodes.values().iterator();
		while (ite.hasNext()) {
			try {
				Host curNode = ite.next();
				List curNodeStpList = curNode.getBridgestpList();
				if (curNodeStpList != null && curNodeStpList.size() > 0) {
					for (int k = 0; k < curNodeStpList.size(); k++) {
						try {
							BridgeStpInterface bstp = (BridgeStpInterface) curNodeStpList.get(k);
							if (curNode.isBridgeIdentifier(bstp.getBridge().substring(5))) {
								logger.info("运行: STP designated root is the bridge itself. Skipping");
								continue;
							}
							Host designatedNode = getNodeFromMacIdentifierOfBridgeNode(bstp.getBridge().substring(5));
							if (designatedNode == null)
								continue;
							// if port is a backbone port continue
							logger.info(curNode.getIpAddress() + "   Port " + bstp.getPort());
							if (curNode.isBackBoneBridgePort(Integer.parseInt(bstp.getPort()))) {
								logger.info("运行: bridge port " + bstp.getPort() + " already found .... Skipping");
								continue;
							}
							String stpPortDesignatedPort = bstp.getBridgeport();
							stpPortDesignatedPort = stpPortDesignatedPort.replace(":", "");
							logger.info(curNode.getIpAddress() + "   designatedbridgeport " + Integer.parseInt(stpPortDesignatedPort.substring(1), 16));
							int designatedbridgeport = Integer.parseInt(stpPortDesignatedPort.substring(1), 16);

							int designatedifindex = -1;
							if (designatedNode.getIfEntityByPort(designatedbridgeport + "") != null) {
								designatedifindex = Integer.parseInt(designatedNode.getIfEntityByPort(designatedbridgeport + "").getIndex());
							} else {
								logger.info("运行: got invalid ifindex on designated node");
								continue;
							}

							if (designatedifindex == -1 || designatedifindex == 0) {
								logger.info("运行: got invalid ifindex on designated node");
								continue;
							}

							logger.info("run: backbone port found for node " + curNode.getId() + ". Adding to bridge" + bstp.getPort());

							curNode.addBackBoneBridgePorts(Integer.parseInt(bstp.getPort()));
							bridgeNodes.put(new Integer(curNode.getId()), curNode);

							logger.info("run: backbone port found for node " + designatedNode.getId() + " .Adding to helper class bb port " + " bridge port "
									+ designatedbridgeport);

							// test if there are other bridges between this link
							// USING MAC ADDRESS FORWARDING TABLE

							if (!isNearestBridgeLink(curNode, Integer.parseInt(bstp.getPort()), designatedNode, designatedbridgeport)) {
								logger.info("run: other bridge found between nodes. No links to save!");
								continue; // no saving info if no nodeid
							}
							int curIfIndex = Integer.parseInt(curNode.getIfEntityByPort(bstp.getPort() + "").getIndex());

							if (curIfIndex == -1 || curIfIndex == 0) {
								logger.info("运行: got invalid ifindex");
								continue;
							}
							designatedNode.addBackBoneBridgePorts(designatedbridgeport);
							bridgeNodes.put(new Integer(designatedNode.getId()), designatedNode);

							logger.info("run: adding links on bb bridge port " + designatedbridgeport);

							addLinks(getMacsOnBridgeLink(curNode, Integer.parseInt(bstp.getPort()), designatedNode, designatedbridgeport), curNode.getId(), curIfIndex);

							// writing to db using class
							// DbDAtaLinkInterfaceEntry
							NodeToNodeLink lk = new NodeToNodeLink(curNode.getId(), curIfIndex);
							lk.setFindtype(SystemConstant.ISBridge);
							lk.setNodeparentid(designatedNode.getId());
							lk.setParentifindex(designatedifindex);
							addNodetoNodeLink(lk);
						} catch (Exception e) {
							e.printStackTrace();
							logger.error(e.getMessage());
						}
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}

		// fourth find inter router links,
		// this part could have several special function to get inter router
		// links, but at the moment we worked much on switches.
		// In future we can try to extend this part.
		logger.info("运行: try to found  not ethernet links on Router nodes");

		List routeLinkList = DiscoverEngine.getInstance().getRouteLinkList();
		if (routeLinkList != null && routeLinkList.size() > 0) {
			for (int k = 0; k < routeLinkList.size(); k++) {
				try {
					Link link = (Link) routeLinkList.get(k);
					// Saving link also when ifindex = -1 (not found)
					NodeToNodeLink lk = new NodeToNodeLink(link.getEndId(), Integer.parseInt(link.getEndIndex()));
					lk.setFindtype(SystemConstant.ISRouter);
					lk.setNodeparentid(link.getStartId());
					lk.setParentifindex(Integer.parseInt(link.getStartIndex()));
					logger.info("添加连接: ##########################");
					logger.info("添加连接: " + link.getStartIp() + " --- " + link.getEndIp());
					addNodetoNodeLink(lk);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}
		// 将maclinklist中有连接,而在上面的CDP/NDP/STP/ROUTER计算中没有的连接加进去
		List macLinks = DiscoverEngine.getInstance().getMacLinkList();
		if (macLinks != null && macLinks.size() > 0) {
			for (int k = 0; k < macLinks.size(); k++) {
				try {
					Link maclink = (Link) macLinks.get(k);
					if (!NodeToNodeLinkExist(maclink)) {
						// 若不存在该连接,则添加进去
						NodeToNodeLink lk = new NodeToNodeLink(maclink.getEndId(), Integer.parseInt(maclink.getEndIndex()));
						lk.setFindtype(SystemConstant.ISMac);
						lk.setNodeparentid(maclink.getStartId());
						lk.setParentifindex(Integer.parseInt(maclink.getStartIndex()));
						addNodetoNodeLink(lk);
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}

		Hashtable existNode = new Hashtable();
		DiscoverEngine.getInstance().getLinkList().clear();
		;
		if (links != null && links.size() > 0) {
			for (int i = 0; i < links.size(); i++) {
				try {
					NodeToNodeLink link = (NodeToNodeLink) links.get(i);
					logger.info("连接: " + link.getNodeparentid() + " " + link.getParentifindex() + " " + link.getNodeId() + " " + link.getIfindex());
					Host startNode = DiscoverEngine.getInstance().getHostByID(link.getNodeparentid());
					Host endNode = DiscoverEngine.getInstance().getHostByID(link.getNodeId());
					IfEntity startIfEntity = startNode.getIfEntityByIndex(link.getParentifindex() + "");
					IfEntity endIfEntity = endNode.getIfEntityByIndex(link.getIfindex() + "");
					Link addlink = new Link();
					addlink.setStartId(link.getNodeparentid());
					addlink.setStartIndex(link.getParentifindex() + "");
					addlink.setStartIp(startNode.getIpAddress());
					addlink.setStartDescr(startIfEntity.getDescr());
					addlink.setStartPort(startIfEntity.getPort());
					addlink.setStartPhysAddress(startNode.getBridgeAddress());

					addlink.setEndId(link.getNodeId());
					addlink.setEndIndex(link.getIfindex() + "");
					addlink.setEndIp(endNode.getIpAddress());
					addlink.setEndDescr(endIfEntity.getDescr());
					addlink.setEndPort(endIfEntity.getPort());
					addlink.setEndPhysAddress(endNode.getBridgeAddress());

					addlink.setAssistant(link.getAssistant());
					addlink.setFindtype(link.getFindtype());
					addlink.setLinktype(0);
					DiscoverEngine.getInstance().getLinkList().add(addlink);
					if (!existNode.containsKey(addlink.getStartId())) {
						existNode.put(addlink.getStartId(), addlink.getStartId());
					}
					if (!existNode.containsKey(addlink.getEndId())) {
						existNode.put(addlink.getEndId(), addlink.getEndId());
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}

		// 判断是否有没有连接的Node
		// 将没有产生连接的孤立的接点用逻辑连接代替
		List macLinkList = DiscoverEngine.getInstance().getMacLinkList();
		if (macLinkList != null && macLinkList.size() > 0) {
			for (int k = 0; k < macLinkList.size(); k++) {
				try {
					Link maclink = (Link) macLinkList.get(k);
					if (!existNode.containsKey(maclink.getStartId()) || !existNode.containsKey(maclink.getEndId())) {
						// 若有个端点不在已经存在的连接列表里
						// Saving link also when ifindex = -1 (not found)
						NodeToNodeLink link = new NodeToNodeLink(maclink.getEndId(), Integer.parseInt(maclink.getEndIndex()));
						link.setFindtype(SystemConstant.ISMac);
						link.setNodeparentid(maclink.getStartId());
						link.setParentifindex(Integer.parseInt(maclink.getStartIndex()));
						addNodetoNodeLink(link);
						logger.info("连接: " + link.getNodeparentid() + " " + link.getParentifindex() + " " + link.getNodeId() + " " + link.getIfindex());
						Host startNode = DiscoverEngine.getInstance().getHostByID(link.getNodeparentid());
						Host endNode = DiscoverEngine.getInstance().getHostByID(link.getNodeId());
						IfEntity startIfEntity = startNode.getIfEntityByIndex(link.getParentifindex() + "");
						IfEntity endIfEntity = endNode.getIfEntityByIndex(link.getIfindex() + "");
						Link addlink = new Link();
						addlink.setStartId(link.getNodeparentid());
						addlink.setStartIndex(link.getParentifindex() + "");
						addlink.setStartIp(startNode.getIpAddress());
						addlink.setStartDescr(startIfEntity.getDescr());
						addlink.setStartPort(startIfEntity.getPort());
						addlink.setStartPhysAddress(startNode.getBridgeAddress());

						addlink.setEndId(link.getNodeId());
						addlink.setEndIndex(link.getIfindex() + "");
						addlink.setEndIp(endNode.getIpAddress());
						addlink.setEndDescr(endIfEntity.getDescr());
						addlink.setEndPort(endIfEntity.getPort());
						addlink.setEndPhysAddress(endNode.getBridgeAddress());

						addlink.setAssistant(link.getAssistant());
						addlink.setFindtype(link.getFindtype());
						addlink.setLinktype(0);// 物理连接
						DiscoverEngine.getInstance().getLinkList().add(addlink);
						if (!existNode.containsKey(addlink.getStartId())) {
							existNode.put(addlink.getStartId(), addlink.getStartId());
						}
						if (!existNode.containsKey(addlink.getEndId())) {
							existNode.put(addlink.getEndId(), addlink.getEndId());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}

		// 将路由连接添加进去
		if (routeLinkList != null && routeLinkList.size() > 0) {
			for (int k = 0; k < routeLinkList.size(); k++) {
				try {
					Link routelink = (Link) routeLinkList.get(k);
					routelink.setLinktype(-1);// 物理连接
					DiscoverEngine.getInstance().getLinkList().add(routelink);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}
		// 将VLAN连接添加进去
		if (macLinkList != null && macLinkList.size() > 0) {
			for (int k = 0; k < macLinkList.size(); k++) {
				try {
					Link maclink = (Link) macLinkList.get(k);
					maclink.setLinktype(-1);// 物理连接
					DiscoverEngine.getInstance().getLinkList().add(maclink);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}
		Hashtable exitsnodelink = new Hashtable();
		List linklists = DiscoverEngine.getInstance().getLinkList();
		if (linklists != null && linklists.size() > 0) {
			for (int i = 0; i < linklists.size(); i++) {
				Link link = (Link) linklists.get(i);
				if (!exitsnodelink.containsKey(link.getStartId() + "")) {
					exitsnodelink.put(link.getStartId() + "", link.getStartId() + "");
				}
				if (!exitsnodelink.containsKey(link.getEndId() + "")) {
					exitsnodelink.put(link.getEndId() + "", link.getEndId() + "");
				}
			}
		}
		if (hostlist != null && hostlist.size() > 0) {
			for (int i = 0; i < hostlist.size(); i++) {
				Host host = (Host) hostlist.get(i);
				if (host == null) {
					logger.error("节点为空值，继续进行下一步操作");
					continue;
				}
				// 过滤掉非网络设备
				if (host.getCategory() != 1 && host.getCategory() != 2 && host.getCategory() != 3 && host.getCategory() != 7)
					continue;
				if (!exitsnodelink.containsKey(host.getId() + "")) {
					// 没有连接关系,需要遍历所有节点,计算连接关系
					for (int k = 0; k < hostlist.size(); k++) {
						Host phost = (Host) hostlist.get(i);
						if (host.getId() == phost.getId())
							continue;
						List arplist = phost.getIpNetTable();
						if (arplist != null && arplist.size() > 0) {
							for (int m = 0; m < arplist.size(); i++) {
								IpAddress ipAddress = (IpAddress) arplist.get(m);
								if (host.getIpAddress().equalsIgnoreCase(ipAddress.getIpAddress())) {
									// 存在连接关系
									IfEntity ifentity = host.getIfEntityByIP(host.getIpAddress());
									if (ifentity == null)
										continue;

									Link addlink = new Link();

									addlink.setStartId(phost.getId());
									addlink.setStartIndex(ipAddress.getIfIndex());
									addlink.setStartIp(phost.getIpAddress());
									addlink.setStartDescr(ipAddress.getIfIndex());
									addlink.setStartPort(ipAddress.getIfIndex());
									addlink.setStartPhysAddress(ipAddress.getPhysAddress());

									addlink.setEndId(host.getId());
									addlink.setEndIndex(ifentity.getIndex());
									addlink.setEndIp(ifentity.getIpAddress());
									addlink.setEndDescr(ifentity.getDescr());
									addlink.setEndPort(ifentity.getPort());
									addlink.setEndPhysAddress(host.getBridgeAddress());

									addlink.setAssistant(0);
									addlink.setFindtype(1);
									addlink.setLinktype(0);// 逻辑连接
									DiscoverEngine.getInstance().getLinkList().add(addlink);
									if (!exitsnodelink.containsKey(addlink.getStartId() + "")) {
										exitsnodelink.put(addlink.getStartId() + "", addlink.getStartId() + "");
										break;
									}
									if (!exitsnodelink.containsKey(addlink.getEndId() + "")) {
										exitsnodelink.put(addlink.getEndId() + "", addlink.getEndId() + "");
										break;
									}
								}
							}

						}
					}
				}

				if (!exitsnodelink.containsKey(host.getId() + "")) {
					// 没有连接关系,需要遍历该IP别名计算连接关系
					for (int k = 0; k < hostlist.size(); k++) {
						Host phost = (Host) hostlist.get(i);
						if (host.getId() == phost.getId())
							continue;
						List hostiplist = host.getIfEntityList();
						if (hostiplist != null && hostiplist.size() > 0) {
							for (int j = 0; j < hostiplist.size(); j++) {
								IfEntity ifentity = (IfEntity) hostiplist.get(j);
								List arplist = phost.getIpNetTable();
								if (arplist != null && arplist.size() > 0) {
									for (int m = 0; m < arplist.size(); i++) {
										IpAddress ipAddress = (IpAddress) arplist.get(m);
										if (host.getIpAddress().equalsIgnoreCase(ipAddress.getIpAddress())) {
											// 存在连接关系
											IfEntity if_entity = host.getIfEntityByIP(host.getIpAddress());
											if (ifentity == null)
												continue;

											Link addlink = new Link();

											addlink.setStartId(phost.getId());
											addlink.setStartIndex(ipAddress.getIfIndex());
											addlink.setStartIp(phost.getIpAddress());
											addlink.setStartDescr(ipAddress.getIfIndex());
											addlink.setStartPort(ipAddress.getIfIndex());
											addlink.setStartPhysAddress(ipAddress.getPhysAddress());

											addlink.setEndId(host.getId());
											addlink.setEndIndex(if_entity.getIndex());
											addlink.setEndIp(if_entity.getIpAddress());
											addlink.setEndDescr(if_entity.getDescr());
											addlink.setEndPort(if_entity.getPort());
											addlink.setEndPhysAddress(host.getBridgeAddress());

											addlink.setAssistant(0);
											addlink.setFindtype(1);
											addlink.setLinktype(0);// 逻辑连接
											DiscoverEngine.getInstance().getLinkList().add(addlink);
											if (!exitsnodelink.containsKey(addlink.getStartId() + "")) {
												exitsnodelink.put(addlink.getStartId() + "", addlink.getStartId() + "");
												break;
											}
											if (!exitsnodelink.containsKey(addlink.getEndId() + "")) {
												exitsnodelink.put(addlink.getEndId() + "", addlink.getEndId() + "");
												break;
											}
										}
									}

								}

							}
						}

					}
				}

			}
		}

	}

	@SuppressWarnings("unused")
	private synchronized void analyseTopoLinks(Host thost) {
		Iterator<Host> ite = null;
		List hostlist = DiscoverEngine.getInstance().getHostList();
		if (hostlist != null && hostlist.size() > 0) {
			for (int i = 0; i < hostlist.size(); i++) {
				try {
					Host host = (Host) hostlist.get(i);
					logger.info(host.toString());
					if (host == null) {
						logger.error("节点为空值，继续进行下一步操作");
						continue;
					}
					// 过滤掉非网络设备
					if (host.getCategory() != 1 && host.getCategory() != 2 && host.getCategory() != 3 && host.getCategory() != 7)
						continue;
					if (host.getBridgestpList() != null && host.getBridgestpList().size() > 0) {
						bridgeNodes.put(new Integer(host.getId()), host);
					}
					if (host.getCdpList() != null && host.getCdpList().size() > 0) {
						cdpNodes.add(host);
					}
					if (host.getNdpHash() != null && host.getNdpHash().size() > 0) {
						ndpNodes.add(host);
					}
					if (host.getRouteList() != null && host.getRouteList().size() > 0) {
						routerNodes.add(host);
					}

					if (host.getAtInterfaces() != null && host.getAtInterfaces().size() > 0) {
						List atInterfaces = host.getAtInterfaces();
						// 将HOST的ID补充进去
						List atList = new ArrayList();
						for (int k = 0; k < atInterfaces.size(); k++) {
							AtInterface at = (AtInterface) atInterfaces.get(k);
							AtInterface _at = new AtInterface(host.getId(), at.getIpAddress(), at.getMacAddress(), at.getIfindex());
							atList.add(_at);
						}
						if (atList != null && atList.size() > 0)
							host.setAtInterfaces(atList);
						atNodes.add(host);
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}

		logger.info("运行: 用atNodes to populate macToAtinterface");

		ite = atNodes.iterator();
		while (ite.hasNext()) {
			Host host = ite.next();
			List atInterfaces = host.getAtInterfaces();
			if (atInterfaces != null && atInterfaces.size() > 0) {
				for (int k = 0; k < atInterfaces.size(); k++) {
					try {
						AtInterface at = (AtInterface) atInterfaces.get(k);
						int nodeid = host.getId();
						String ipaddr = at.getIpAddress();
						String macAddress = at.getMacAddress();
						logger.info("解析 at Interface nodeid/ipaddr/macaddr: " + nodeid + "/" + ipaddr + "/" + macAddress);

						if (isMacIdentifierOfBridgeNode(macAddress)) {
							logger.info("运行: at interface " + macAddress + " belongs to bridge node! Not adding to discoverable atinterface.");
							macsExcluded.add(macAddress);
							continue;
						}
						List<AtInterface> ats = macToAtinterface.get(macAddress);
						if (ats == null)
							ats = new ArrayList<AtInterface>();
						logger.info("parseAtNodes: Adding to discoverable atinterface.");
						ats.add(at);
						macToAtinterface.put(macAddress, ats);
						logger.info("parseAtNodes: mac:" + macAddress + " has now atinterface reference: " + ats.size());
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(e.getMessage());
					}
				}
			}
		}

		logger.info("运行: end populate macToAtinterface");

		// First of all use quick methods to get backbone ports for speeding
		// up the link discovery
		logger.info("运行: finding links among nodes using Cisco Discovery Protocol");

		logger.info("利用Cisco Discovery Protocol发现节点间的连接");
		// Try Cisco Discovery Protocol to found link among all nodes
		// Add CDP info for backbones ports

		ite = cdpNodes.iterator();
		while (ite.hasNext()) {
			try {
				Host host = ite.next();
				int curCdpNodeId = host.getId();
				List executedPort = new ArrayList();
				Iterator<CdpCachEntryInterface> sub_ite = host.getCdpList().iterator();
				while (sub_ite.hasNext()) {
					try {
						CdpCachEntryInterface cdpIface = sub_ite.next();
						String targetIpAddr = cdpIface.getIp();
						// 判断是否是已经存在在host列表里的IP
						Host targetHost = DiscoverEngine.getInstance().getHostByAliasIP(targetIpAddr);
						if (targetHost == null) {
							logger.info("IP地址" + targetIpAddr + "不在已发现的网络设备里，跳过");
							continue;
						}

						int targetCdpNodeId = targetHost.getId();
						if (targetCdpNodeId == -1 || targetCdpNodeId == 0) {
							logger.info("没发现网络设备IP " + targetHost.getIpAddress() + "的ID，跳过");
							continue;
						}
						if (targetCdpNodeId == curCdpNodeId) {
							logger.info("运行: 该IP为自身IP " + targetIpAddr + " 跳过");
							continue;
						}

						int cdpIfIndex = -1;
						if (targetHost.getCdpList() != null && targetHost.getCdpList().size() > 0) {
							Iterator<CdpCachEntryInterface> target_ite = targetHost.getCdpList().iterator();
							while (target_ite.hasNext()) {
								CdpCachEntryInterface targetcdpIface = target_ite.next();
								if (host.getAliasIPs().contains(targetcdpIface.getIp())) {
									// 需要加如当前PORTDESC是否已经被处理过的条件
									if (executedPort.contains(targetcdpIface.getPortdesc()))
										continue;
									if (host.getIfEntityByDesc(targetcdpIface.getPortdesc()) != null) {
										cdpIfIndex = Integer.parseInt(host.getIfEntityByDesc(targetcdpIface.getPortdesc()).getIndex());
										executedPort.add(targetcdpIface.getPortdesc());
										break;
									}
								}
							}
						}
						if (cdpIfIndex <= 0) {
							// 用逻辑端口代替
							cdpIfIndex = Integer.parseInt(host.getIfEntityByIP(host.getIpAddress()).getIndex());
							logger.info("不是合法的CDP IfIndex，用逻辑端口代替");
							// continue;
						} else {
							logger.info("发现合法的 CDP ifindex " + cdpIfIndex);
						}

						logger.info("运行: 发现 nodeid/CDP 目标IP: " + targetCdpNodeId + ":" + targetIpAddr);

						int cdpDestIfindex = -1;
						if (targetHost.getIfEntityByDesc(cdpIface.getPortdesc()) != null) {
							cdpDestIfindex = Integer.parseInt(targetHost.getIfEntityByDesc(cdpIface.getPortdesc()).getIndex());
							if (cdpDestIfindex < 0) {
								logger.info("运行：不合法的CDP destination IfIndex " + cdpDestIfindex + ". 跳过");
								continue;
							}
						} else {
							logger.info("运行：不合法的CDP destination. 跳过");
							continue;
						}
						logger.info("运行： 发现 CDP target ifindex " + cdpDestIfindex);

						logger.info("运行: 解析 CDP link: nodeid=" + curCdpNodeId + " ifindex=" + cdpIfIndex + " nodeparentid=" + targetCdpNodeId + " parentifindex=" + cdpDestIfindex);

						boolean add = false;
						// now add the cdp link
						logger.info("运行: no node is bridge node! Adding CDP link");
						add = true;
						if (add) {
							NodeToNodeLink lk = new NodeToNodeLink(targetCdpNodeId, cdpDestIfindex);
							lk.setFindtype(SystemConstant.ISCDP);
							lk.setNodeparentid(curCdpNodeId);
							lk.setParentifindex(cdpIfIndex);
							addNodetoNodeLink(lk);
							logger.info("运行: CDP link added: " + lk.toString());
						}
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(e.getMessage());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}

		logger.info("利用HUAWEI 的 Network Discovery Protocol发现节点间的连接");
		// Try Cisco Discovery Protocol to found link among all nodes
		// Add CDP info for backbones ports

		ite = ndpNodes.iterator();
		while (ite.hasNext()) {
			try {
				Host host = ite.next();
				int curNdpNodeId = host.getId();
				String curNdpIpAddr = host.getAdminIp();
				if (host.getNdpHash() != null && host.getNdpHash().size() > 0) {
					Iterator<String> sub_ite = host.getNdpHash().keySet().iterator();
					while (sub_ite.hasNext()) {
						try {
							String endndpMac = sub_ite.next();
							String endndpDescr = (String) host.getNdpHash().get(endndpMac);
							Host endNode = getNodeFromMacIdentifierOfNdpNode(endndpMac);
							if (endNode == null) {
								logger.info("找不到MAC地址" + endndpMac + ",在已发现的网络设备里，跳过");
								continue;
							}
							IfEntity endIfEntity = endNode.getIfEntityByDesc(endndpDescr);
							IfEntity startIfEntity = null;
							if (endIfEntity == null) {
								logger.info("找不到端口描述为" + endndpDescr + ",在已发现的网络设备里，跳过");
								// continue;
							}
							// 寻找开始端的连接
							// 默认情况下,endNode的NdpHash不为空
							Hashtable endNodeNdpHash = endNode.getNdpHash();
							if (endNodeNdpHash == null)
								endNodeNdpHash = new Hashtable();
							if (host.getMac() == null)
								continue;
							if (endNodeNdpHash.containsKey(host.getMac())) {
								// 存在该IP
								String ndpDescr = (String) endNodeNdpHash.get(host.getMac());
								startIfEntity = endNode.getIfEntityByDesc(ndpDescr);
							}

							if (startIfEntity == null) {
								startIfEntity = host.getIfEntityByIP(host.getIpAddress());
								if (startIfEntity == null)
									continue;

							}
							if (endIfEntity == null) {
								endIfEntity = endNode.getIfEntityByIP(endNode.getIpAddress());
								if (endIfEntity == null)
									continue;

							}
							if (startIfEntity != null && endIfEntity != null) {
								// 两个连接都存在
								if (host.getId() == endNode.getId()) {
									logger.info("运行: 该连接为自身, 跳过");
									continue;
								}
							}

							logger.info("运行: 发现 nodeid/NDP 目标IP: " + endNode.getId() + ":" + endNode.getIpAddress());

							logger.info("运行: 解析 NDP link: nodeid=" + host.getId() + " ifindex=" + startIfEntity.getIndex() + " nodeparentid=" + endNode.getId() + " parentifindex="
									+ endIfEntity.getIndex());

							boolean add = false;
							// now add the cdp link
							logger.info("运行: no node is bridge node! Adding NDP link");
							add = true;
							if (add) {
								NodeToNodeLink lk = new NodeToNodeLink(endNode.getId(), Integer.parseInt(endIfEntity.getIndex()));
								lk.setFindtype(SystemConstant.ISNDP);
								lk.setNodeparentid(host.getId());
								lk.setParentifindex(Integer.parseInt(startIfEntity.getIndex()));
								addNodetoNodeLink(lk);
								logger.info("运行: NDP link added: " + lk.toString());
							}
						} catch (Exception e) {
							e.printStackTrace();
							logger.error(e.getMessage());
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
		// try get backbone links between switches using STP info
		// and store information in Bridge class
		logger.info("运行: try to found backbone ethernet links among bridge nodes using Spanning Tree Protocol");

		ite = bridgeNodes.values().iterator();
		while (ite.hasNext()) {
			try {
				Host curNode = ite.next();
				List curNodeStpList = curNode.getBridgestpList();
				if (curNodeStpList != null && curNodeStpList.size() > 0) {
					for (int k = 0; k < curNodeStpList.size(); k++) {
						try {
							BridgeStpInterface bstp = (BridgeStpInterface) curNodeStpList.get(k);
							if (curNode.isBridgeIdentifier(bstp.getBridge().substring(5))) {
								logger.info("运行: STP designated root is the bridge itself. Skipping");
								continue;
							}
							Host designatedNode = getNodeFromMacIdentifierOfBridgeNode(bstp.getBridge().substring(5));
							if (designatedNode == null)
								continue;
							// if port is a backbone port continue
							logger.info(curNode.getIpAddress() + "   Port " + bstp.getPort());
							if (curNode.isBackBoneBridgePort(Integer.parseInt(bstp.getPort()))) {
								logger.info("运行: bridge port " + bstp.getPort() + " already found .... Skipping");
								continue;
							}
							String stpPortDesignatedPort = bstp.getBridgeport();
							stpPortDesignatedPort = stpPortDesignatedPort.replace(":", "");
							logger.info(curNode.getIpAddress() + "   designatedbridgeport " + Integer.parseInt(stpPortDesignatedPort.substring(1), 16));
							int designatedbridgeport = Integer.parseInt(stpPortDesignatedPort.substring(1), 16);

							int designatedifindex = -1;
							if (designatedNode.getIfEntityByPort(designatedbridgeport + "") != null) {
								designatedifindex = Integer.parseInt(designatedNode.getIfEntityByPort(designatedbridgeport + "").getIndex());
							} else {
								logger.info("运行: got invalid ifindex on designated node");
								continue;
							}

							if (designatedifindex == -1 || designatedifindex == 0) {
								logger.info("运行: got invalid ifindex on designated node");
								continue;
							}

							logger.info("run: backbone port found for node " + curNode.getId() + ". Adding to bridge" + bstp.getPort());

							curNode.addBackBoneBridgePorts(Integer.parseInt(bstp.getPort()));
							bridgeNodes.put(new Integer(curNode.getId()), curNode);

							logger.info("run: backbone port found for node " + designatedNode.getId() + " .Adding to helper class bb port " + " bridge port "
									+ designatedbridgeport);

							// test if there are other bridges between this link
							// USING MAC ADDRESS FORWARDING TABLE

							if (!isNearestBridgeLink(curNode, Integer.parseInt(bstp.getPort()), designatedNode, designatedbridgeport)) {
								logger.info("run: other bridge found between nodes. No links to save!");
								continue; // no saving info if no nodeid
							}
							int curIfIndex = Integer.parseInt(curNode.getIfEntityByPort(bstp.getPort() + "").getIndex());

							if (curIfIndex == -1 || curIfIndex == 0) {
								logger.info("运行: got invalid ifindex");
								continue;
							}
							designatedNode.addBackBoneBridgePorts(designatedbridgeport);
							bridgeNodes.put(new Integer(designatedNode.getId()), designatedNode);

							logger.info("run: adding links on bb bridge port " + designatedbridgeport);

							addLinks(getMacsOnBridgeLink(curNode, Integer.parseInt(bstp.getPort()), designatedNode, designatedbridgeport), curNode.getId(), curIfIndex);

							// writing to db using class
							// DbDAtaLinkInterfaceEntry
							NodeToNodeLink lk = new NodeToNodeLink(curNode.getId(), curIfIndex);
							lk.setFindtype(SystemConstant.ISBridge);
							lk.setNodeparentid(designatedNode.getId());
							lk.setParentifindex(designatedifindex);
							addNodetoNodeLink(lk);
						} catch (Exception e) {
							e.printStackTrace();
							logger.error(e.getMessage());
						}
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}

		// fourth find inter router links,
		// this part could have several special function to get inter router
		// links, but at the moment we worked much on switches.
		// In future we can try to extend this part.
		logger.info("运行: try to found  not ethernet links on Router nodes");

		List routeLinkList = DiscoverEngine.getInstance().getRouteLinkList();
		if (routeLinkList != null && routeLinkList.size() > 0) {
			for (int k = 0; k < routeLinkList.size(); k++) {
				try {
					Link link = (Link) routeLinkList.get(k);
					// Saving link also when ifindex = -1 (not found)
					NodeToNodeLink lk = new NodeToNodeLink(link.getEndId(), Integer.parseInt(link.getEndIndex()));
					lk.setFindtype(SystemConstant.ISRouter);
					lk.setNodeparentid(link.getStartId());
					lk.setParentifindex(Integer.parseInt(link.getStartIndex()));
					logger.info("添加连接: ##########################");
					logger.info("添加连接: " + link.getStartIp() + " --- " + link.getEndIp());
					addNodetoNodeLink(lk);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}
		// 将maclinklist中有连接,而在上面的CDP/NDP/STP/ROUTER计算中没有的连接加进去
		List macLinks = DiscoverEngine.getInstance().getMacLinkList();
		if (macLinks != null && macLinks.size() > 0) {
			for (int k = 0; k < macLinks.size(); k++) {
				try {
					Link maclink = (Link) macLinks.get(k);
					if (!NodeToNodeLinkExist(maclink)) {
						// 若不存在该连接,则添加进去
						NodeToNodeLink lk = new NodeToNodeLink(maclink.getEndId(), Integer.parseInt(maclink.getEndIndex()));
						lk.setFindtype(SystemConstant.ISMac);
						lk.setNodeparentid(maclink.getStartId());
						lk.setParentifindex(Integer.parseInt(maclink.getStartIndex()));
						logger.info("添加连接: ##########################");
						logger.info("添加连接: " + maclink.getStartIp() + " --- " + maclink.getEndIp());
						addNodetoNodeLink(lk);
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}

		// List allLinks = DiscoverEngine.getInstance().getLinkList();
		Hashtable existNode = new Hashtable();
		DiscoverEngine.getInstance().getLinkList().clear();
		;
		if (links != null && links.size() > 0) {
			for (int i = 0; i < links.size(); i++) {
				try {
					NodeToNodeLink link = (NodeToNodeLink) links.get(i);
					logger.info("连接: " + link.getNodeparentid() + " " + link.getParentifindex() + " " + link.getNodeId() + " " + link.getIfindex());
					Host startNode = DiscoverEngine.getInstance().getHostByID(link.getNodeparentid());
					Host endNode = DiscoverEngine.getInstance().getHostByID(link.getNodeId());
					IfEntity startIfEntity = startNode.getIfEntityByIndex(link.getParentifindex() + "");
					IfEntity endIfEntity = endNode.getIfEntityByIndex(link.getIfindex() + "");
					Link addlink = new Link();
					addlink.setStartId(link.getNodeparentid());
					addlink.setStartIndex(link.getParentifindex() + "");
					addlink.setStartIp(startNode.getIpAddress());
					addlink.setStartDescr(startIfEntity.getDescr());
					addlink.setStartPort(startIfEntity.getPort());
					addlink.setStartPhysAddress(startNode.getBridgeAddress());

					addlink.setEndId(link.getNodeId());
					addlink.setEndIndex(link.getIfindex() + "");
					addlink.setEndIp(endNode.getIpAddress());
					addlink.setEndDescr(endIfEntity.getDescr());
					addlink.setEndPort(endIfEntity.getPort());
					addlink.setEndPhysAddress(endNode.getBridgeAddress());

					addlink.setAssistant(link.getAssistant());
					addlink.setFindtype(link.getFindtype());
					addlink.setLinktype(0);
					DiscoverEngine.getInstance().getLinkList().add(addlink);
					if (!existNode.containsKey(addlink.getStartId())) {
						existNode.put(addlink.getStartId(), addlink.getStartId());
					}
					if (!existNode.containsKey(addlink.getEndId())) {
						existNode.put(addlink.getEndId(), addlink.getEndId());
					}
					// allLinks.add(addlink);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}

		// 判断是否有没有连接的Node
		// 将没有产生连接的孤立的接点用逻辑连接代替
		List macLinkList = DiscoverEngine.getInstance().getMacLinkList();
		if (macLinkList != null && macLinkList.size() > 0) {
			for (int k = 0; k < macLinkList.size(); k++) {
				try {
					Link maclink = (Link) macLinkList.get(k);
					if (!existNode.containsKey(maclink.getStartId()) || !existNode.containsKey(maclink.getEndId())) {
						// 若有个端点不在已经存在的连接列表里
						// Saving link also when ifindex = -1 (not found)
						NodeToNodeLink link = new NodeToNodeLink(maclink.getEndId(), Integer.parseInt(maclink.getEndIndex()));
						link.setFindtype(SystemConstant.ISMac);
						link.setNodeparentid(maclink.getStartId());
						link.setParentifindex(Integer.parseInt(maclink.getStartIndex()));
						logger.info("添加连接: ##########################");
						logger.info("添加连接: " + maclink.getStartIp() + " --- " + maclink.getEndIp());
						addNodetoNodeLink(link);
						// NodeToNodeLink link = (NodeToNodeLink)links.get(i);
						logger.info("连接: " + link.getNodeparentid() + " " + link.getParentifindex() + " " + link.getNodeId() + " " + link.getIfindex());
						Host startNode = DiscoverEngine.getInstance().getHostByID(link.getNodeparentid());
						Host endNode = DiscoverEngine.getInstance().getHostByID(link.getNodeId());
						IfEntity startIfEntity = startNode.getIfEntityByIndex(link.getParentifindex() + "");
						IfEntity endIfEntity = endNode.getIfEntityByIndex(link.getIfindex() + "");
						Link addlink = new Link();
						addlink.setStartId(link.getNodeparentid());
						addlink.setStartIndex(link.getParentifindex() + "");
						addlink.setStartIp(startNode.getIpAddress());
						addlink.setStartDescr(startIfEntity.getDescr());
						addlink.setStartPort(startIfEntity.getPort());
						addlink.setStartPhysAddress(startNode.getBridgeAddress());

						addlink.setEndId(link.getNodeId());
						addlink.setEndIndex(link.getIfindex() + "");
						addlink.setEndIp(endNode.getIpAddress());
						addlink.setEndDescr(endIfEntity.getDescr());
						addlink.setEndPort(endIfEntity.getPort());
						addlink.setEndPhysAddress(endNode.getBridgeAddress());

						addlink.setAssistant(link.getAssistant());
						addlink.setFindtype(link.getFindtype());
						addlink.setLinktype(0);// 物理连接
						DiscoverEngine.getInstance().getLinkList().add(addlink);
						if (!existNode.containsKey(addlink.getStartId())) {
							existNode.put(addlink.getStartId(), addlink.getStartId());
						}
						if (!existNode.containsKey(addlink.getEndId())) {
							existNode.put(addlink.getEndId(), addlink.getEndId());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}

		// 将路由连接添加进去
		if (routeLinkList != null && routeLinkList.size() > 0) {
			for (int k = 0; k < routeLinkList.size(); k++) {
				try {
					Link routelink = (Link) routeLinkList.get(k);
					routelink.setLinktype(-1);// 物理连接
					DiscoverEngine.getInstance().getLinkList().add(routelink);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}
		// 将VLAN连接添加进去
		if (macLinkList != null && macLinkList.size() > 0) {
			for (int k = 0; k < macLinkList.size(); k++) {
				try {
					Link maclink = (Link) macLinkList.get(k);
					maclink.setLinktype(-1);// 物理连接
					DiscoverEngine.getInstance().getLinkList().add(maclink);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}
		Hashtable exitsnodelink = new Hashtable();
		List linklists = DiscoverEngine.getInstance().getLinkList();
		if (linklists != null && linklists.size() > 0) {
			for (int i = 0; i < linklists.size(); i++) {
				Link link = (Link) linklists.get(i);
				if (!exitsnodelink.containsKey(link.getStartId() + "")) {
					exitsnodelink.put(link.getStartId() + "", link.getStartId() + "");
				}
				if (!exitsnodelink.containsKey(link.getEndId() + "")) {
					exitsnodelink.put(link.getEndId() + "", link.getEndId() + "");
				}
			}
		}
		if (hostlist != null && hostlist.size() > 0) {
			for (int i = 0; i < hostlist.size(); i++) {
				Host host = (Host) hostlist.get(i);
				if (host == null) {
					logger.error("节点为空值，继续进行下一步操作");
					continue;
				}
				// 过滤掉非网络设备
				if (host.getCategory() != 1 && host.getCategory() != 2 && host.getCategory() != 3 && host.getCategory() != 7)
					continue;
				// int flag = 0;
				if (!exitsnodelink.containsKey(host.getId() + "")) {
					// 没有连接关系,需要遍历所有节点,计算连接关系
					for (int k = 0; k < hostlist.size(); k++) {
						Host phost = (Host) hostlist.get(i);
						if (host.getId() == phost.getId())
							continue;
						List arplist = phost.getIpNetTable();
						if (arplist != null && arplist.size() > 0) {
							for (int m = 0; m < arplist.size(); i++) {
								IpAddress ipAddress = (IpAddress) arplist.get(m);
								if (host.getIpAddress().equalsIgnoreCase(ipAddress.getIpAddress())) {
									// 存在连接关系
									IfEntity ifentity = host.getIfEntityByIP(host.getIpAddress());
									if (ifentity == null)
										continue;

									Link addlink = new Link();

									addlink.setStartId(phost.getId());
									addlink.setStartIndex(ipAddress.getIfIndex());
									addlink.setStartIp(phost.getIpAddress());
									addlink.setStartDescr(ipAddress.getIfIndex());
									addlink.setStartPort(ipAddress.getIfIndex());
									addlink.setStartPhysAddress(ipAddress.getPhysAddress());

									addlink.setEndId(host.getId());
									addlink.setEndIndex(ifentity.getIndex());
									addlink.setEndIp(ifentity.getIpAddress());
									addlink.setEndDescr(ifentity.getDescr());
									addlink.setEndPort(ifentity.getPort());
									addlink.setEndPhysAddress(host.getBridgeAddress());

									addlink.setAssistant(0);
									addlink.setFindtype(1);
									addlink.setLinktype(0);// 逻辑连接
									DiscoverEngine.getInstance().getLinkList().add(addlink);
									if (!exitsnodelink.containsKey(addlink.getStartId() + "")) {
										exitsnodelink.put(addlink.getStartId() + "", addlink.getStartId() + "");
										break;
									}
									if (!exitsnodelink.containsKey(addlink.getEndId() + "")) {
										exitsnodelink.put(addlink.getEndId() + "", addlink.getEndId() + "");
										break;
									}
								}
							}

						}
					}
				}

				if (!exitsnodelink.containsKey(host.getId() + "")) {
					// 没有连接关系,需要遍历该IP别名计算连接关系
					for (int k = 0; k < hostlist.size(); k++) {
						Host phost = (Host) hostlist.get(i);
						if (host.getId() == phost.getId())
							continue;
						List hostiplist = host.getIfEntityList();
						if (hostiplist != null && hostiplist.size() > 0) {
							for (int j = 0; j < hostiplist.size(); j++) {
								IfEntity ifentity = (IfEntity) hostiplist.get(j);
								List arplist = phost.getIpNetTable();
								if (arplist != null && arplist.size() > 0) {
									for (int m = 0; m < arplist.size(); i++) {
										IpAddress ipAddress = (IpAddress) arplist.get(m);
										if (host.getIpAddress().equalsIgnoreCase(ipAddress.getIpAddress())) {
											// 存在连接关系
											IfEntity if_entity = host.getIfEntityByIP(host.getIpAddress());
											if (ifentity == null)
												continue;

											Link addlink = new Link();

											addlink.setStartId(phost.getId());
											addlink.setStartIndex(ipAddress.getIfIndex());
											addlink.setStartIp(phost.getIpAddress());
											addlink.setStartDescr(ipAddress.getIfIndex());
											addlink.setStartPort(ipAddress.getIfIndex());
											addlink.setStartPhysAddress(ipAddress.getPhysAddress());

											addlink.setEndId(host.getId());
											addlink.setEndIndex(if_entity.getIndex());
											addlink.setEndIp(if_entity.getIpAddress());
											addlink.setEndDescr(if_entity.getDescr());
											addlink.setEndPort(if_entity.getPort());
											addlink.setEndPhysAddress(host.getBridgeAddress());

											addlink.setAssistant(0);
											addlink.setFindtype(1);
											addlink.setLinktype(0);// 逻辑连接
											DiscoverEngine.getInstance().getLinkList().add(addlink);
											if (!exitsnodelink.containsKey(addlink.getStartId() + "")) {
												exitsnodelink.put(addlink.getStartId() + "", addlink.getStartId() + "");
												break;
											}
											if (!exitsnodelink.containsKey(addlink.getEndId() + "")) {
												exitsnodelink.put(addlink.getEndId() + "", addlink.getEndId() + "");
												break;
											}
										}
									}

								}

							}
						}

					}
				}

			}
		}

	}

	@SuppressWarnings("unused")
	private boolean parseCdpLinkOn(Host node1, int ifindex1, Host node2, int ifindex2) {
		IfEntity ifEntity = node1.getIfEntityByIndex(ifindex1 + "");
		if (ifEntity == null) {
			logger.info("运行：找不到ifindex1 " + ifindex1 + "对应的接口，跳过");
			return false;
		}

		logger.info("运行：ifindex1 " + ifindex1 + "对应的 port " + ifEntity.getPort());
		if (ifEntity.getPort() == "")
			ifEntity.setPort(ifEntity.getIndex());
		int bridgeport1 = Integer.parseInt(ifEntity.getPort());

		if (node1.isBackBoneBridgePort(bridgeport1)) {
			logger.info("方法parseCdpLinkOn: 主干桥端口 " + bridgeport1 + " 已经被解吸. Skipping");
			return false;
		}

		ifEntity = node2.getIfEntityByIndex(ifindex2 + "");
		if (ifEntity == null) {
			logger.info("运行：找不到ifindex2 " + ifindex2 + "对应的接口，跳过");
			return false;
		}
		if (ifEntity.getPort() == "")
			ifEntity.setPort(ifEntity.getIndex());
		int bridgeport2 = Integer.parseInt(ifEntity.getPort());

		if (node2.isBackBoneBridgePort(bridgeport2)) {
			logger.info("方法parseCdpLinkOn: 主干桥端口 " + bridgeport2 + " 已经被解吸. Skipping");
			return false;
		}

		if (isNearestBridgeLink(node1, bridgeport1, node2, bridgeport2)) {

			node1.addBackBoneBridgePorts(bridgeport1);
			bridgeNodes.put(new Integer(node1.getId()), node1);

			node2.addBackBoneBridgePorts(bridgeport2);
			bridgeNodes.put(new Integer(node2.getId()), node2);

			logger.info("解析CdpLinkOn: 添加节点连接关系.");

			addLinks(getMacsOnBridgeLink(node1, bridgeport1, node2, bridgeport2), node1.getId(), ifindex1);
		} else {
			logger.info("解析CdpLinkOn: 没发现最近的连接.跳过");
			return false;
		}
		return true;
	}

	private boolean isNearestBridgeLink(Host bridge1, int bp1, Host bridge2, int bp2) {

		boolean hasbridge2forwardingRule = false;
		Set<String> macsOnBridge2 = bridge2.getMacAddressesOnBridgePort(bp2);

		Set<String> macsOnBridge1 = bridge1.getMacAddressesOnBridgePort(bp1);

		if (macsOnBridge2 == null || macsOnBridge1 == null)
			return false;

		if (macsOnBridge2.isEmpty() || macsOnBridge1.isEmpty())
			return false;

		Iterator<String> macsonbridge1_ite = macsOnBridge1.iterator();

		while (macsonbridge1_ite.hasNext()) {
			String curMacOnBridge1 = macsonbridge1_ite.next();
			// if mac address is bridge identifier of bridge 2 continue

			if (bridge2.isBridgeIdentifier(curMacOnBridge1)) {
				hasbridge2forwardingRule = true;
				continue;
			}
			// if mac address is itself identifier of bridge1 continue
			if (bridge1.isBridgeIdentifier(curMacOnBridge1))
				continue;
			// then no identifier of bridge one no identifier of bridge 2
			// bridge 2 contains
			if (macsOnBridge2.contains(curMacOnBridge1) && isMacIdentifierOfBridgeNode(curMacOnBridge1))
				return false;
		}

		return hasbridge2forwardingRule;
	}

	private boolean isMacIdentifierOfBridgeNode(String macAddress) {
		Iterator<Host> ite = bridgeNodes.values().iterator();
		while (ite.hasNext()) {
			Host curNode = ite.next();
			if (curNode.isBridgeIdentifier(macAddress))
				return true;
		}
		return false;
	}

	private Set<String> getMacsOnBridgeLink(Host bridge1, int bp1, Host bridge2, int bp2) {

		Set<String> macsOnLink = new HashSet<String>();

		Set<String> macsOnBridge1 = bridge1.getMacAddressesOnBridgePort(bp1);

		Set<String> macsOnBridge2 = bridge2.getMacAddressesOnBridgePort(bp2);

		if (macsOnBridge2 == null || macsOnBridge1 == null)
			return null;

		if (macsOnBridge2.isEmpty() || macsOnBridge1.isEmpty())
			return null;

		Iterator<String> macsonbridge1_ite = macsOnBridge1.iterator();

		while (macsonbridge1_ite.hasNext()) {
			String curMacOnBridge1 = macsonbridge1_ite.next();
			if (bridge2.isBridgeIdentifier(curMacOnBridge1))
				continue;
			if (macsOnBridge2.contains(curMacOnBridge1))
				macsOnLink.add(curMacOnBridge1);
		}
		return macsOnLink;
	}

	private void addLinks(Set<String> macs, int nodeid, int ifindex) {
		if (macs == null || macs.isEmpty()) {
			logger.info("addLinks: mac's list on link is empty.");
		} else {
			Iterator<String> mac_ite = macs.iterator();

			while (mac_ite.hasNext()) {
				String curMacAddress = mac_ite.next();
				if (macsParsed.contains(curMacAddress)) {
					logger.info("添加连接: MAC地址" + curMacAddress + "在其他桥端口发现!跳过...");
					continue;
				}

				if (macsExcluded.contains(curMacAddress)) {
					logger.info("添加连接: MAC地址" + curMacAddress + " is excluded from discovery package! Skipping...");
					continue;
				}

				if (macToAtinterface.containsKey(curMacAddress)) {
					List<AtInterface> ats = macToAtinterface.get(curMacAddress);
					Iterator<AtInterface> ite = ats.iterator();
					while (ite.hasNext()) {
						AtInterface at = ite.next();

						NodeToNodeLink lNode = new NodeToNodeLink(at.getNodeId(), at.getIfindex());
						lNode.setNodeparentid(nodeid);
						lNode.setParentifindex(ifindex);

						addNodetoNodeLink(lNode);
					}
				} else {
					logger.info("添加连接:not find nodeid for ethernet mac address " + curMacAddress + " found on node/ifindex" + nodeid + "/" + ifindex);
					MacToNodeLink lMac = new MacToNodeLink(curMacAddress);
					lMac.setNodeparentid(nodeid);
					lMac.setParentifindex(ifindex);
					maclinks.add(lMac);
				}
				macsParsed.add(curMacAddress);
			}
		}
	}

	private void addNodetoNodeLink(NodeToNodeLink nnlink) {
		if (nnlink == null) {
			logger.info("addNodetoNodeLink: node link is null.");
			return;
		}
		if (!links.isEmpty()) {
			Iterator<NodeToNodeLink> ite = links.iterator();
			while (ite.hasNext()) {
				NodeToNodeLink curNnLink = ite.next();
				if (curNnLink.equals(nnlink)) {
					logger.info("添加节点连接: link " + nnlink.toString() + " exists, not adding");
					return;
				}
			}
		}

		int assitantLink = countNodetoNodeLink(nnlink);
		if (assitantLink == 0) {
			// 不存在该连接,则添加
			logger.info("添加节点连接: adding link " + nnlink.toString());
			links.add(nnlink);
		} else if (assitantLink == 1) {
			// 已经存在一条,则要把该连接设置为辅助连接
			logger.info("添加节点连接: adding link " + nnlink.toString());
			nnlink.setAssistant(1);
			links.add(nnlink);
		} else {
			return;
		}
	}

	private int countNodetoNodeLink(NodeToNodeLink nnlink) {
		int counts = 0;
		if (nnlink == null) {
			logger.info("addNodetoNodeLink: node link is null.");
			return counts;
		}
		if (!links.isEmpty()) {
			Iterator<NodeToNodeLink> ite = links.iterator();
			while (ite.hasNext()) {
				NodeToNodeLink curNnLink = ite.next();
				if (curNnLink.assistantequals(nnlink)) {
					counts = counts + 1;
					logger.info("连接存在: link " + nnlink.toString() + " exists, not adding");
				}
			}
		}
		logger.info("该连接点有: " + counts + " 条" + nnlink.toString());
		return counts;
	}

	@SuppressWarnings("unused")
	private boolean parseCdpLinkOn(Host node1, int ifindex1, int nodeid2) {

		int bridgeport = Integer.parseInt(node1.getIfEntityByIndex(ifindex1 + "").getPort());

		if (node1.isBackBoneBridgePort(bridgeport)) {
			logger.info("解析CDPLINK连接: node/backbone bridge port " + node1.getId() + "/" + bridgeport + " already parsed. Skipping");
			return false;
		}

		if (isEndBridgePort(node1, bridgeport)) {

			node1.addBackBoneBridgePorts(bridgeport);
			bridgeNodes.put(new Integer(node1.getId()), node1);
			Set<String> macs = node1.getMacAddressesOnBridgePort(bridgeport);
			addLinks(macs, node1.getId(), ifindex1);
		} else {
			logger.info("解析CDPLINK连接: link cannot be saved. Skipping");
			return false;
		}
		return true;
	}

	private boolean isEndBridgePort(Host bridge, int bridgeport) {

		Set<String> macsOnBridge = bridge.getMacAddressesOnBridgePort(bridgeport);

		if (macsOnBridge == null || macsOnBridge.isEmpty())
			return true;

		Iterator<String> macsonbridge_ite = macsOnBridge.iterator();

		while (macsonbridge_ite.hasNext()) {
			String macaddr = macsonbridge_ite.next();
			if (isMacIdentifierOfBridgeNode(macaddr))
				return false;
		}

		return true;
	}

	/**
	 * 
	 * @param nodeid
	 * @return LinkableSnmpNode or null if not found
	 */

	boolean isBridgeNode(int nodeid) {

		Iterator<Host> ite = bridgeNodes.values().iterator();
		while (ite.hasNext()) {
			Host curNode = ite.next();
			if (nodeid == curNode.getId())
				return true;
		}
		return false;
	}

	@SuppressWarnings("unused")
	private List<Host> getBridgesFromMacs(Set<String> macs) {
		List<Host> bridges = new ArrayList<Host>();
		Iterator<Host> ite = bridgeNodes.values().iterator();
		while (ite.hasNext()) {
			Host curNode = ite.next();

			Iterator<String> sub_ite = curNode.getBridgeIdentifiers().iterator();
			while (sub_ite.hasNext()) {
				String curBridgeIdentifier = (String) sub_ite.next();
				if (macs.contains((curBridgeIdentifier)))
					bridges.add(curNode);
			}
		}
		return bridges;
	}

	@SuppressWarnings("unused")
	private int getBridgePortOnEndBridge(Host startBridge, Host endBridge) {

		int port = -1;
		Iterator<String> bridge_ident_ite = startBridge.getBridgeIdentifiers().iterator();
		while (bridge_ident_ite.hasNext()) {
			String curBridgeIdentifier = bridge_ident_ite.next();
			logger.info("getBridgePortOnEndBridge: parsing bridge identifier " + curBridgeIdentifier);

			if (endBridge.hasMacAddress(curBridgeIdentifier)) {
				List<Integer> ports = endBridge.getBridgePortsFromMac(curBridgeIdentifier);
				Iterator<Integer> ports_ite = ports.iterator();
				while (ports_ite.hasNext()) {
					port = ports_ite.next();
					if (endBridge.isBackBoneBridgePort(port)) {
						logger.info("getBridgePortOnEndBridge: found backbone bridge port " + port + " .... 跳过");
						continue;
					}
					if (port == -1) {
						logger.info("run: no port found on bridge nodeid " + endBridge.getId() + " for node bridge identifiers nodeid " + startBridge.getId() + " . .....Skipping");
						continue;
					}
					logger.info("run: using mac address table found bridge port " + port + " on node " + endBridge.getId());
					return port;
				}

			} else {
				logger.info("运行: 1bridge identifier not found on node " + endBridge.getId());
			}
		}
		return -1;
	}

	/**
	 * 
	 * @param stpportdesignatedbridge
	 * @return Bridge Bridge Node if found else null
	 */

	private Host getNodeFromMacIdentifierOfBridgeNode(String macAddress) {
		Iterator<Host> ite = bridgeNodes.values().iterator();
		while (ite.hasNext()) {
			Host curNode = ite.next();
			if (curNode.isBridgeIdentifier(macAddress))
				return curNode;
		}
		return null;
	}

	private Host getNodeFromMacIdentifierOfNdpNode(String macAddress) {
		Iterator<Host> ite = ndpNodes.iterator();
		while (ite.hasNext()) {
			Host curNode = ite.next();
			if (curNode.getMac() == null)
				continue;
			if (curNode.getMac().equalsIgnoreCase(macAddress))
				return curNode;
		}
		return null;
	}

	private boolean NodeToNodeLinkExist(Link link) {
		boolean flag = false;
		for (int i = 0; i < links.size(); i++) {
			NodeToNodeLink nodelink = (NodeToNodeLink) links.get(i);
			if (nodelink.getNodeId() == link.getStartId() && nodelink.getNodeparentid() == link.getEndId()) {
				flag = true;
				break;
			}

			if (nodelink.getNodeparentid() == link.getStartId() && nodelink.getNodeId() == link.getEndId()) {
				flag = true;
				break;
			}
		}
		return flag;
	}

}
