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
 * ������ɺ�Ҫ��3����: 1.�������,��������ͼXML; 2.�ͷŷ��ֳ��� 3.��ʼ����ѯ����
 */

@SuppressWarnings("unchecked")
public class DiscoverComplete {
	private  Logger logger = Logger.getLogger(this.getClass());
	
	private DiscoverDataHelper helper = new DiscoverDataHelper();
	private List<NodeToNodeLink> links = new ArrayList<NodeToNodeLink>();

	private List<MacToNodeLink> maclinks = new ArrayList<MacToNodeLink>();

	private HashMap<Integer, Host> bridgeNodes = new HashMap<Integer, Host>();

	private List<Host> routerNodes = new ArrayList<Host>();

	private List<Host> cdpNodes = new ArrayList<Host>();

	private List<Host> ndpNodes = new ArrayList<Host>();

	private List<Host> atNodes = new ArrayList<Host>();

	// this is the list of mac address just parsed by discovery process
	private List<String> macsParsed = new ArrayList<String>();

	// this is the list of mac address excluded by discovery process
	private List<String> macsExcluded = new ArrayList<String>();

	// this is tha list of atinterfaces for which to be discovery link
	// here there aren't the bridge identifier becouse they should be discovered
	// by main processes. This is used by addlinks method.
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

	/**
	 * 
	 */

	private void unloadDiscover() {
		DiscoverEngine.getInstance().unload();
		DiscoverResource.getInstance().unload();
		// ��Ҫ���һ����ȫ���ķ����߳�destroy��,��ֹ�ڴ����
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
				.append("<tr><td align='center'><font color='blue'><b>���ֽ��̼���</b><input type=button class='button' value='ֹͣ����' onclick='/afunms/user.do?action=logout'></font></td></tr>\n");
		htmlFile.append("<tr><td valign='top' align='center'>\n");
		htmlFile.append("<table width='100%' border=1 cellspacing=0 cellpadding=0 bordercolorlight='#000000' bordercolordark='#FFFFFF'>");
		htmlFile.append("<tr><td>��ʼʱ��</td><td>");
		htmlFile.append(monitor.getStartTime());
		htmlFile.append("</td></tr>");
		htmlFile.append("<tr><td>����ʱ��</td><td>");
		htmlFile.append(monitor.getEndTime());
		htmlFile.append("</td></tr>");
		htmlFile.append("<tr><td>�Ѿ���ʱ</td><td>");
		htmlFile.append(monitor.getElapseTime());
		htmlFile.append("</td></tr></table></td></tr>");
		htmlFile.append("<tr><td>\n");
		htmlFile.append("<table width='100%' border=1 cellspacing=0 cellpadding=0 bordercolorlight='#000000' bordercolordark='#FFFFFF'>");
		htmlFile.append("<tr bgcolor='#D4E1D5'><td>&nbsp;</td><td><b>����</b></td></tr>\n");
		htmlFile.append("<tr><td>�豸</td><td>");
		htmlFile.append(monitor.getHostTotal());
		htmlFile.append("</td></tr>");
		htmlFile.append("<tr><td>����</td><td>");
		htmlFile.append(monitor.getSubNetTotal());
		htmlFile.append("</tr></table></td></tr>");
		htmlFile.append("<tr><td align='center'><font color='blue'><b><br>��ϸ</b></font></td></tr><tr><td>");
		htmlFile.append(monitor.getResultTable());
		htmlFile.append("</td></tr></table></body></html>");
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;

		try {
			fos = new FileOutputStream(ResourceCenter.getInstance().getSysPath() + "topology\\discover\\discover.html");
			osw = new OutputStreamWriter(fos, "GB2312");
			osw.write(htmlFile.toString());
		} catch (Exception e) {
			logger.error("DiscoverDataHelper.createHtml()", e);
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

			logger.error("Error in DiscoverComplete.updateSystemXml()", e);
		}
	}

	/**
	 * �ҳ����н�����������е���· 2007.3.12��ˮ������
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
						logger.error("�ڵ�Ϊ��ֵ������������һ������");
						continue;
					}
					// ���˵��������豸
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
						// ��HOST��ID�����ȥ
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

		logger.info("����: ��atNodes to populate macToAtinterface");

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
						logger.info("���� at Interface nodeid/ipaddr/macaddr: " + nodeid + "/" + ipaddr + "/" + macAddress);
						if (isMacIdentifierOfBridgeNode(macAddress)) {
							logger.info("����: at interface " + macAddress + " belongs to bridge node! Not adding to discoverable atinterface.");
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

		logger.info("����: end populate macToAtinterface");

		/*
		 * //now perform operation to complete if (enableDownloadDiscovery) { if
		 * (log().isInfoEnabled()) log().info("run: get further unknown mac
		 * address snmp bridge table info"); snmpParseBridgeNodes(); } else { if
		 * (log().isInfoEnabled()) log().info("run: skipping get further unknown
		 * mac address snmp bridge table info"); }
		 */

		// First of all use quick methods to get backbone ports for speeding
		// up the link discovery
		logger.info("����: finding links among nodes using Cisco Discovery Protocol");

		logger.info("����Cisco Discovery Protocol���ֽڵ�������");
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
						// �ж��Ƿ����Ѿ�������host�б����IP
						Host targetHost = DiscoverEngine.getInstance().getHostByAliasIP(targetIpAddr);
						if (targetHost == null) {
							logger.info("IP��ַ" + targetIpAddr + "�����ѷ��ֵ������豸�����");
							continue;
						}

						int targetCdpNodeId = targetHost.getId();
						if (targetCdpNodeId == -1 || targetCdpNodeId == 0) {
							logger.info("û���������豸IP " + targetHost.getIpAddress() + "��ID������");
							continue;
						}
						if (targetCdpNodeId == curCdpNodeId) {
							logger.info("����: ��IPΪ����IP " + targetIpAddr + " ����");
							continue;
						}

						int cdpIfIndex = -1;
						if (targetHost.getCdpList() != null && targetHost.getCdpList().size() > 0) {
							Iterator<CdpCachEntryInterface> target_ite = targetHost.getCdpList().iterator();
							while (target_ite.hasNext()) {
								CdpCachEntryInterface targetcdpIface = target_ite.next();
								if (host.getAliasIPs().contains(targetcdpIface.getIp())) {
									// ��Ҫ���統ǰPORTDESC�Ƿ��Ѿ��������������
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
							// ���߼��˿ڴ���
							cdpIfIndex = Integer.parseInt(host.getIfEntityByIP(host.getIpAddress()).getIndex());
							logger.info("���ǺϷ���CDP IfIndex�����߼��˿ڴ���");
						} else {
							logger.info("���ֺϷ��� CDP ifindex " + cdpIfIndex);
						}

						logger.info("����: ���� nodeid/CDP Ŀ��IP: " + targetCdpNodeId + ":" + targetIpAddr);

						int cdpDestIfindex = -1;
						if (targetHost.getIfEntityByDesc(cdpIface.getPortdesc()) != null) {
							cdpDestIfindex = Integer.parseInt(targetHost.getIfEntityByDesc(cdpIface.getPortdesc()).getIndex());
							if (cdpDestIfindex < 0) {
								logger.info("���У����Ϸ���CDP destination IfIndex " + cdpDestIfindex + ". ����");
								continue;
							}
						} else {
							logger.info("���У����Ϸ���CDP destination. ����");
							continue;
						}
						logger.info("���У� ���� CDP target ifindex " + cdpDestIfindex);

						logger.info("����: ���� CDP link: nodeid=" + curCdpNodeId + " ifindex=" + cdpIfIndex + " nodeparentid=" + targetCdpNodeId + " parentifindex="
								+ cdpDestIfindex);

						boolean add = false;
						// now add the cdp link
						logger.info("����: no node is bridge node! Adding CDP link");
						add = true;
						if (add) {
							NodeToNodeLink lk = new NodeToNodeLink(targetCdpNodeId, cdpDestIfindex);
							lk.setFindtype(SystemConstant.ISCDP);
							lk.setNodeparentid(curCdpNodeId);
							lk.setParentifindex(cdpIfIndex);
							addNodetoNodeLink(lk);
							logger.info("����: CDP link added: " + lk.toString());
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

		logger.info("����HUAWEI �� Network Discovery Protocol���ֽڵ�������");
		// Try Cisco Discovery Protocol to found link among all nodes
		// Add CDP info for backbones ports

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
							if ("10.10.0.6".equalsIgnoreCase(host.getIpAddress()) || "10.10.0.38".equalsIgnoreCase(host.getIpAddress())) {
								logger.info("--------------------------------");
								logger.info(host.getIpAddress() + " endndpMac:" + endndpMac + " endndpDescr:" + endndpDescr + "  ����������NDP����");
								logger.info("--------------------------------");
							}
							Host endNode = getNodeFromMacIdentifierOfNdpNode(endndpMac);
							if (endNode == null) {
								logger.info("--------------------------------");
								logger.info(host.getIpAddress() + " endndpMac:" + endndpMac + " endndpDescr:" + endndpDescr + "  �Ҳ���MAC��ַ");
								logger.info("--------------------------------");

								logger.info("�Ҳ���MAC��ַ" + endndpMac + ",���ѷ��ֵ������豸�����");
								continue;
							}
							IfEntity endIfEntity = endNode.getIfEntityByDesc(endndpDescr);
							IfEntity startIfEntity = null;
							if (endIfEntity == null) {
								if ("10.10.0.6".equalsIgnoreCase(host.getIpAddress()) || "10.10.0.38".equalsIgnoreCase(host.getIpAddress())) {
									logger.info("--------------------------------");
									logger.info(endNode.getIpAddress() + " endndpMac:" + endndpMac + " endndpDescr:" + endndpDescr + " �Ҳ����˿�@@@ :");
									logger.info("--------------------------------");
								}
								logger.info("�Ҳ����˿�����Ϊ" + endndpDescr + ",���ѷ��ֵ������豸�����");
								// continue;
							}
							// Ѱ�ҿ�ʼ�˵�����
							// Ĭ�������,endNode��NdpHash��Ϊ��
							Hashtable endNodeNdpHash = endNode.getNdpHash();
							if (endNodeNdpHash == null)
								endNodeNdpHash = new Hashtable();
							if (host.getMac() == null)
								continue;

							if (endNodeNdpHash != null && !endNodeNdpHash.isEmpty()) {
								Iterator<String> iterator = endNodeNdpHash.keySet().iterator();
								while (iterator.hasNext()) {
									String MAC = iterator.next();
									String PORTDESC = (String) endNodeNdpHash.get(MAC);
									logger.info(endNode.getIpAddress() + " #### MAC:" + MAC + "     PORT:" + PORTDESC + " # hostMAC:" + host.getMac());
								}
							}

							if (endNodeNdpHash.containsKey(host.getMac())) {
								// ���ڸ�IP

								String ndpDescr = (String) endNodeNdpHash.get(host.getMac());
								startIfEntity = host.getIfEntityByDesc(ndpDescr);
								if ("10.10.0.6".equalsIgnoreCase(host.getIpAddress()) || "10.10.0.38".equalsIgnoreCase(host.getIpAddress())) {
									logger.info("--------------------------------");
									logger.info("endndpMac:" + endndpMac + " endndpDescr:" + endndpDescr + " startndpMac:" + host.getMac() + "  startndpDescr:" + ndpDescr);
									logger.info("--------------------------------");
								}
							}

							if (startIfEntity == null) {
								if ("10.10.0.6".equalsIgnoreCase(host.getIpAddress()) || "10.10.0.38".equalsIgnoreCase(host.getIpAddress())) {
									logger.info("--------------------------------");
									logger.info(host.getIpAddress() + " endndpMac:" + endndpMac + " endndpDescr:" + endndpDescr + " startIfEntity is null");
									logger.info("--------------------------------");
								}
								startIfEntity = host.getIfEntityByIP(host.getIpAddress());

								if (startIfEntity == null) {
									continue;
								}

							}
							if (endIfEntity == null) {
								if ("10.10.0.6".equalsIgnoreCase(host.getIpAddress()) || "10.10.0.38".equalsIgnoreCase(host.getIpAddress())) {
									logger.info("--------------------------------");
									logger.info(endNode.getIpAddress() + " endndpMac:" + endndpMac + " endndpDescr:" + endndpDescr + " endIfEntity is null");
									logger.info("--------------------------------");
								}
								endIfEntity = endNode.getIfEntityByIP(endNode.getIpAddress());
								if (endIfEntity == null)
									continue;

							}
							if (startIfEntity != null && endIfEntity != null) {
								// �������Ӷ�����
								if (host.getId() == endNode.getId()) {
									logger.info("����: ������Ϊ����, ����");
									continue;
								}
							}

							logger.info("����: ���� nodeid/NDP Ŀ��IP: " + endNode.getId() + ":" + endNode.getIpAddress());

							logger.info("����: ���� NDP link: nodeid=" + host.getId() + " ifindex=" + startIfEntity.getIndex() + " nodeparentid=" + endNode.getId()
									+ " parentifindex=" + endIfEntity.getIndex());

							boolean add = false;
							// now add the cdp link
							logger.info("����: no node is bridge node! Adding NDP link");
							add = true;
							if (add) {
								NodeToNodeLink lk = new NodeToNodeLink(endNode.getId(), Integer.parseInt(endIfEntity.getIndex()));
								lk.setFindtype(SystemConstant.ISNDP);
								lk.setNodeparentid(host.getId());
								lk.setParentifindex(Integer.parseInt(startIfEntity.getIndex()));
								addNodetoNodeLink(lk);
								logger.info("����: NDP link added: " + lk.toString());
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
		logger.info("����: try to found backbone ethernet links among bridge nodes using Spanning Tree Protocol");

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
								logger.info("����: STP designated root is the bridge itself. Skipping");
								continue;
							}
							Host designatedNode = getNodeFromMacIdentifierOfBridgeNode(bstp.getBridge().substring(5));
							if (designatedNode == null)
								continue;
							// if port is a backbone port continue
							logger.info(curNode.getIpAddress() + "   Port " + bstp.getPort());
							if (curNode.isBackBoneBridgePort(Integer.parseInt(bstp.getPort()))) {
								logger.info("����: bridge port " + bstp.getPort() + " already found .... Skipping");
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
								logger.info("����: got invalid ifindex on designated node");
								continue;
							}

							if (designatedifindex == -1 || designatedifindex == 0) {
								logger.info("����: got invalid ifindex on designated node");
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
								logger.info("����: got invalid ifindex");
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
		logger.info("����: try to found  not ethernet links on Router nodes");

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
					logger.info("�������: ##########################");
					logger.info("�������: " + link.getStartIp() + " --- " + link.getEndIp());
					addNodetoNodeLink(lk);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}
		// ��maclinklist��������,���������CDP/NDP/STP/ROUTER������û�е����Ӽӽ�ȥ
		List macLinks = DiscoverEngine.getInstance().getMacLinkList();
		if (macLinks != null && macLinks.size() > 0) {
			for (int k = 0; k < macLinks.size(); k++) {
				try {
					Link maclink = (Link) macLinks.get(k);
					if (!NodeToNodeLinkExist(maclink)) {
						// �������ڸ�����,����ӽ�ȥ
						NodeToNodeLink lk = new NodeToNodeLink(maclink.getEndId(), Integer.parseInt(maclink.getEndIndex()));
						lk.setFindtype(SystemConstant.ISMac);
						lk.setNodeparentid(maclink.getStartId());
						lk.setParentifindex(Integer.parseInt(maclink.getStartIndex()));
						logger.info("�������: ##########################");
						logger.info("�������: " + maclink.getStartIp() + " --- " + maclink.getEndIp());
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
					logger.info("����: " + link.getNodeparentid() + " " + link.getParentifindex() + " " + link.getNodeId() + " " + link.getIfindex());
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

		// �ж��Ƿ���û�����ӵ�Node
		// ��û�в������ӵĹ����Ľӵ����߼����Ӵ���
		List macLinkList = DiscoverEngine.getInstance().getMacLinkList();
		if (macLinkList != null && macLinkList.size() > 0) {
			for (int k = 0; k < macLinkList.size(); k++) {
				try {
					Link maclink = (Link) macLinkList.get(k);
					if (!existNode.containsKey(maclink.getStartId()) || !existNode.containsKey(maclink.getEndId())) {
						// ���и��˵㲻���Ѿ����ڵ������б���
						// Saving link also when ifindex = -1 (not found)
						NodeToNodeLink link = new NodeToNodeLink(maclink.getEndId(), Integer.parseInt(maclink.getEndIndex()));
						link.setFindtype(SystemConstant.ISMac);
						link.setNodeparentid(maclink.getStartId());
						link.setParentifindex(Integer.parseInt(maclink.getStartIndex()));
						logger.info("�������: ##########################");
						logger.info("�������: " + maclink.getStartIp() + " --- " + maclink.getEndIp());
						addNodetoNodeLink(link);
						// NodeToNodeLink link = (NodeToNodeLink)links.get(i);
						logger.info("����: " + link.getNodeparentid() + " " + link.getParentifindex() + " " + link.getNodeId() + " " + link.getIfindex());
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
						addlink.setLinktype(0);// ��������
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

		// ��·��������ӽ�ȥ
		if (routeLinkList != null && routeLinkList.size() > 0) {
			for (int k = 0; k < routeLinkList.size(); k++) {
				try {
					Link routelink = (Link) routeLinkList.get(k);
					routelink.setLinktype(-1);// ��������
					DiscoverEngine.getInstance().getLinkList().add(routelink);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}
		// ��VLAN������ӽ�ȥ
		if (macLinkList != null && macLinkList.size() > 0) {
			for (int k = 0; k < macLinkList.size(); k++) {
				try {
					Link maclink = (Link) macLinkList.get(k);
					maclink.setLinktype(-1);// ��������
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
		// Iterator<Host> ite = null;
		// List hostlist = DiscoverEngine.getInstance().getHostList();
		if (hostlist != null && hostlist.size() > 0) {
			for (int i = 0; i < hostlist.size(); i++) {
				Host host = (Host) hostlist.get(i);
				if (host == null) {
					logger.error("�ڵ�Ϊ��ֵ������������һ������");
					continue;
				}
				// ���˵��������豸
				if (host.getCategory() != 1 && host.getCategory() != 2 && host.getCategory() != 3 && host.getCategory() != 7)
					continue;
				// int flag = 0;
				if (!exitsnodelink.containsKey(host.getId() + "")) {
					// û�����ӹ�ϵ,��Ҫ�������нڵ�,�������ӹ�ϵ
					for (int k = 0; k < hostlist.size(); k++) {
						Host phost = (Host) hostlist.get(k);
						if (host.getId() == phost.getId())
							continue;
						List arplist = phost.getIpNetTable();
						if (arplist != null && arplist.size() > 0) {
							for (int m = 0; m < arplist.size(); m++) {
								IpAddress ipAddress = (IpAddress) arplist.get(m);
								if (host.getIpAddress().equalsIgnoreCase(ipAddress.getIpAddress())) {
									// �������ӹ�ϵ
									IfEntity ifentity = host.getIfEntityByIP(host.getIpAddress());
									if (ifentity == null) {
										continue;
									}

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
									addlink.setLinktype(0);// �߼�����
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
					// û�����ӹ�ϵ,��Ҫ������IP�����������ӹ�ϵ
					for (int k = 0; k < hostlist.size(); k++) {
						Host phost = (Host) hostlist.get(k);
						if (host.getId() == phost.getId())
							continue;
						List hostiplist = host.getIfEntityList();
						if (hostiplist != null && hostiplist.size() > 0) {
							for (int j = 0; j < hostiplist.size(); j++) {
								List arplist = phost.getIpNetTable();
								if (arplist != null && arplist.size() > 0) {
									for (int m = 0; m < arplist.size(); m++) {
										IpAddress ipAddress = (IpAddress) arplist.get(m);
										if (host.getIpAddress().equalsIgnoreCase(ipAddress.getIpAddress())) {
											// �������ӹ�ϵ
											IfEntity if_entity = host.getIfEntityByIP(host.getIpAddress());
											if (if_entity == null) {
												continue;
											}

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
											addlink.setLinktype(0);// �߼�����
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
						logger.error("�ڵ�Ϊ��ֵ������������һ������");
						continue;
					}
					// ���˵��������豸
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
						// ��HOST��ID�����ȥ
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

		logger.info("����: ��atNodes to populate macToAtinterface");

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
						logger.info("���� at Interface nodeid/ipaddr/macaddr: " + nodeid + "/" + ipaddr + "/" + macAddress);
						if (isMacIdentifierOfBridgeNode(macAddress)) {
							logger.info("����: at interface " + macAddress + " belongs to bridge node! Not adding to discoverable atinterface.");
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

		logger.info("����: end populate macToAtinterface");

		/*
		 * //now perform operation to complete if (enableDownloadDiscovery) { if
		 * (log().isInfoEnabled()) log().info("run: get further unknown mac
		 * address snmp bridge table info"); snmpParseBridgeNodes(); } else { if
		 * (log().isInfoEnabled()) log().info("run: skipping get further unknown
		 * mac address snmp bridge table info"); }
		 */

		// First of all use quick methods to get backbone ports for speeding
		// up the link discovery
		logger.info("����: finding links among nodes using Cisco Discovery Protocol");

		logger.info("����Cisco Discovery Protocol���ֽڵ�������");
		// Try Cisco Discovery Protocol to found link among all nodes
		// Add CDP info for backbones ports

		ite = cdpNodes.iterator();
		while (ite.hasNext()) {
			try {
				Host host = ite.next();
				int curCdpNodeId = host.getId();
				String curCdpIpAddr = host.getAdminIp();
				List executedPort = new ArrayList();
				Iterator<CdpCachEntryInterface> sub_ite = host.getCdpList().iterator();
				while (sub_ite.hasNext()) {
					try {
						CdpCachEntryInterface cdpIface = sub_ite.next();
						String targetIpAddr = cdpIface.getIp();
						// �ж��Ƿ����Ѿ�������host�б����IP
						Host targetHost = DiscoverEngine.getInstance().getHostByAliasIP(targetIpAddr);
						if (targetHost == null) {
							logger.info("IP��ַ" + targetIpAddr + "�����ѷ��ֵ������豸�����");
							continue;
						}

						int targetCdpNodeId = targetHost.getId();
						if (targetCdpNodeId == -1 || targetCdpNodeId == 0) {
							logger.info("û���������豸IP " + targetHost.getIpAddress() + "��ID������");
							continue;
						}
						if (targetCdpNodeId == curCdpNodeId) {
							logger.info("����: ��IPΪ����IP " + targetIpAddr + " ����");
							continue;
						}

						int cdpIfIndex = -1;
						if (targetHost.getCdpList() != null && targetHost.getCdpList().size() > 0) {
							Iterator<CdpCachEntryInterface> target_ite = targetHost.getCdpList().iterator();
							while (target_ite.hasNext()) {
								CdpCachEntryInterface targetcdpIface = target_ite.next();
								if (host.getAliasIPs().contains(targetcdpIface.getIp())) {
									// ��Ҫ���統ǰPORTDESC�Ƿ��Ѿ��������������
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
							// ���߼��˿ڴ���
							cdpIfIndex = Integer.parseInt(host.getIfEntityByIP(host.getIpAddress()).getIndex());
							logger.info("���ǺϷ���CDP IfIndex�����߼��˿ڴ���");
						} else {
							logger.info("���ֺϷ��� CDP ifindex " + cdpIfIndex);
						}

						logger.info("����: ���� nodeid/CDP Ŀ��IP: " + targetCdpNodeId + ":" + targetIpAddr);

						int cdpDestIfindex = -1;
						if (targetHost.getIfEntityByDesc(cdpIface.getPortdesc()) != null) {
							cdpDestIfindex = Integer.parseInt(targetHost.getIfEntityByDesc(cdpIface.getPortdesc()).getIndex());
							if (cdpDestIfindex < 0) {
								logger.info("���У����Ϸ���CDP destination IfIndex " + cdpDestIfindex + ". ����");
								continue;
							}
						} else {
							logger.info("���У����Ϸ���CDP destination. ����");
							continue;
						}
						logger.info("���У� ���� CDP target ifindex " + cdpDestIfindex);

						logger.info("����: ���� CDP link: nodeid=" + curCdpNodeId + " ifindex=" + cdpIfIndex + " nodeparentid=" + targetCdpNodeId + " parentifindex="
								+ cdpDestIfindex);

						boolean add = false;
						// now add the cdp link
						logger.info("����: no node is bridge node! Adding CDP link");
						add = true;
						if (add) {
							NodeToNodeLink lk = new NodeToNodeLink(targetCdpNodeId, cdpDestIfindex);
							lk.setFindtype(SystemConstant.ISCDP);
							lk.setNodeparentid(curCdpNodeId);
							lk.setParentifindex(cdpIfIndex);
							addNodetoNodeLink(lk);
							logger.info("����: CDP link added: " + lk.toString());
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

		logger.info("����HUAWEI �� Network Discovery Protocol���ֽڵ�������");
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
								logger.info("�Ҳ���MAC��ַ" + endndpMac + ",���ѷ��ֵ������豸�����");
								continue;
							}
							IfEntity endIfEntity = endNode.getIfEntityByDesc(endndpDescr);
							IfEntity startIfEntity = null;
							if (endIfEntity == null) {
								logger.info("�Ҳ����˿�����Ϊ" + endndpDescr + ",���ѷ��ֵ������豸�����");
								// continue;
							}
							// Ѱ�ҿ�ʼ�˵�����
							// Ĭ�������,endNode��NdpHash��Ϊ��
							Hashtable endNodeNdpHash = endNode.getNdpHash();
							if (endNodeNdpHash == null)
								endNodeNdpHash = new Hashtable();
							if (host.getMac() == null)
								continue;
							if (endNodeNdpHash.containsKey(host.getMac())) {
								// ���ڸ�IP
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
								// �������Ӷ�����
								if (host.getId() == endNode.getId()) {
									logger.info("����: ������Ϊ����, ����");
									continue;
								}
							}

							logger.info("����: ���� nodeid/NDP Ŀ��IP: " + endNode.getId() + ":" + endNode.getIpAddress());

							logger.info("����: ���� NDP link: nodeid=" + host.getId() + " ifindex=" + startIfEntity.getIndex() + " nodeparentid=" + endNode.getId()
									+ " parentifindex=" + endIfEntity.getIndex());

							boolean add = false;
							// now add the cdp link
							logger.info("����: no node is bridge node! Adding NDP link");
							add = true;
							if (add) {
								NodeToNodeLink lk = new NodeToNodeLink(endNode.getId(), Integer.parseInt(endIfEntity.getIndex()));
								lk.setFindtype(SystemConstant.ISNDP);
								lk.setNodeparentid(host.getId());
								lk.setParentifindex(Integer.parseInt(startIfEntity.getIndex()));
								addNodetoNodeLink(lk);
								logger.info("����: NDP link added: " + lk.toString());
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
		logger.info("����: try to found backbone ethernet links among bridge nodes using Spanning Tree Protocol");

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
								logger.info("����: STP designated root is the bridge itself. Skipping");
								continue;
							}
							Host designatedNode = getNodeFromMacIdentifierOfBridgeNode(bstp.getBridge().substring(5));
							if (designatedNode == null)
								continue;
							// if port is a backbone port continue
							logger.info(curNode.getIpAddress() + "   Port " + bstp.getPort());
							if (curNode.isBackBoneBridgePort(Integer.parseInt(bstp.getPort()))) {
								logger.info("����: bridge port " + bstp.getPort() + " already found .... Skipping");
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
								logger.info("����: got invalid ifindex on designated node");
								continue;
							}

							if (designatedifindex == -1 || designatedifindex == 0) {
								logger.info("����: got invalid ifindex on designated node");
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
								logger.info("����: got invalid ifindex");
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
		logger.info("����: try to found  not ethernet links on Router nodes");

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
					logger.info("�������: ##########################");
					logger.info("�������: " + link.getStartIp() + " --- " + link.getEndIp());
					addNodetoNodeLink(lk);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}
		// ��maclinklist��������,���������CDP/NDP/STP/ROUTER������û�е����Ӽӽ�ȥ
		List macLinks = DiscoverEngine.getInstance().getMacLinkList();
		if (macLinks != null && macLinks.size() > 0) {
			for (int k = 0; k < macLinks.size(); k++) {
				try {
					Link maclink = (Link) macLinks.get(k);
					if (!NodeToNodeLinkExist(maclink)) {
						// �������ڸ�����,����ӽ�ȥ
						NodeToNodeLink lk = new NodeToNodeLink(maclink.getEndId(), Integer.parseInt(maclink.getEndIndex()));
						lk.setFindtype(SystemConstant.ISMac);
						lk.setNodeparentid(maclink.getStartId());
						lk.setParentifindex(Integer.parseInt(maclink.getStartIndex()));
						logger.info("�������: ##########################");
						logger.info("�������: " + maclink.getStartIp() + " --- " + maclink.getEndIp());
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
					logger.info("����: " + link.getNodeparentid() + " " + link.getParentifindex() + " " + link.getNodeId() + " " + link.getIfindex());
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

		// �ж��Ƿ���û�����ӵ�Node
		// ��û�в������ӵĹ����Ľӵ����߼����Ӵ���
		List macLinkList = DiscoverEngine.getInstance().getMacLinkList();
		if (macLinkList != null && macLinkList.size() > 0) {
			for (int k = 0; k < macLinkList.size(); k++) {
				try {
					Link maclink = (Link) macLinkList.get(k);
					if (!existNode.containsKey(maclink.getStartId()) || !existNode.containsKey(maclink.getEndId())) {
						// ���и��˵㲻���Ѿ����ڵ������б���
						// Saving link also when ifindex = -1 (not found)
						NodeToNodeLink link = new NodeToNodeLink(maclink.getEndId(), Integer.parseInt(maclink.getEndIndex()));
						link.setFindtype(SystemConstant.ISMac);
						link.setNodeparentid(maclink.getStartId());
						link.setParentifindex(Integer.parseInt(maclink.getStartIndex()));
						logger.info("�������: ##########################");
						logger.info("�������: " + maclink.getStartIp() + " --- " + maclink.getEndIp());
						addNodetoNodeLink(link);
						// NodeToNodeLink link = (NodeToNodeLink)links.get(i);
						logger.info("����: " + link.getNodeparentid() + " " + link.getParentifindex() + " " + link.getNodeId() + " " + link.getIfindex());
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
						addlink.setLinktype(0);// ��������
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

		// ��·��������ӽ�ȥ
		if (routeLinkList != null && routeLinkList.size() > 0) {
			for (int k = 0; k < routeLinkList.size(); k++) {
				try {
					Link routelink = (Link) routeLinkList.get(k);
					routelink.setLinktype(-1);// ��������
					DiscoverEngine.getInstance().getLinkList().add(routelink);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}
		// ��VLAN������ӽ�ȥ
		if (macLinkList != null && macLinkList.size() > 0) {
			for (int k = 0; k < macLinkList.size(); k++) {
				try {
					Link maclink = (Link) macLinkList.get(k);
					maclink.setLinktype(-1);// ��������
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
					logger.error("�ڵ�Ϊ��ֵ������������һ������");
					continue;
				}
				// ���˵��������豸
				if (host.getCategory() != 1 && host.getCategory() != 2 && host.getCategory() != 3 && host.getCategory() != 7)
					continue;
				// int flag = 0;
				if (!exitsnodelink.containsKey(host.getId() + "")) {
					// û�����ӹ�ϵ,��Ҫ�������нڵ�,�������ӹ�ϵ
					for (int k = 0; k < hostlist.size(); k++) {
						Host phost = (Host) hostlist.get(i);
						if (host.getId() == phost.getId())
							continue;
						List arplist = phost.getIpNetTable();
						if (arplist != null && arplist.size() > 0) {
							for (int m = 0; m < arplist.size(); i++) {
								IpAddress ipAddress = (IpAddress) arplist.get(m);
								if (host.getIpAddress().equalsIgnoreCase(ipAddress.getIpAddress())) {
									// �������ӹ�ϵ
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
									addlink.setLinktype(0);// �߼�����
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
					// û�����ӹ�ϵ,��Ҫ������IP�����������ӹ�ϵ
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
											// �������ӹ�ϵ
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
											addlink.setLinktype(0);// �߼�����
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
			logger.info("���У��Ҳ���ifindex1 " + ifindex1 + "��Ӧ�Ľӿڣ�����");
			return false;
		}

		logger.info("���У�ifindex1 " + ifindex1 + "��Ӧ�� port " + ifEntity.getPort());
		if (ifEntity.getPort() == "")
			ifEntity.setPort(ifEntity.getIndex());
		int bridgeport1 = Integer.parseInt(ifEntity.getPort());

		if (node1.isBackBoneBridgePort(bridgeport1)) {
			logger.info("����parseCdpLinkOn: �����Ŷ˿� " + bridgeport1 + " �Ѿ�������. Skipping");
			return false;
		}

		ifEntity = node2.getIfEntityByIndex(ifindex2 + "");
		if (ifEntity == null) {
			logger.info("���У��Ҳ���ifindex2 " + ifindex2 + "��Ӧ�Ľӿڣ�����");
			return false;
		}
		if (ifEntity.getPort() == "")
			ifEntity.setPort(ifEntity.getIndex());
		int bridgeport2 = Integer.parseInt(ifEntity.getPort());

		if (node2.isBackBoneBridgePort(bridgeport2)) {
			logger.info("����parseCdpLinkOn: �����Ŷ˿� " + bridgeport2 + " �Ѿ�������. Skipping");
			return false;
		}

		if (isNearestBridgeLink(node1, bridgeport1, node2, bridgeport2)) {

			node1.addBackBoneBridgePorts(bridgeport1);
			bridgeNodes.put(new Integer(node1.getId()), node1);

			node2.addBackBoneBridgePorts(bridgeport2);
			bridgeNodes.put(new Integer(node2.getId()), node2);

			logger.info("����CdpLinkOn: ��ӽڵ����ӹ�ϵ.");

			addLinks(getMacsOnBridgeLink(node1, bridgeport1, node2, bridgeport2), node1.getId(), ifindex1);
		} else {
			logger.info("����CdpLinkOn: û�������������.����");
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
					logger.info("�������: MAC��ַ" + curMacAddress + "�������Ŷ˿ڷ���!����...");
					continue;
				}

				if (macsExcluded.contains(curMacAddress)) {
					logger.info("�������: MAC��ַ" + curMacAddress + " is excluded from discovery package! Skipping...");
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
					logger.info("�������:not find nodeid for ethernet mac address " + curMacAddress + " found on node/ifindex" + nodeid + "/" + ifindex);
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
					logger.info("��ӽڵ�����: link " + nnlink.toString() + " exists, not adding");
					return;
				}
			}
		}

		int assitantLink = countNodetoNodeLink(nnlink);
		if (assitantLink == 0) {
			// �����ڸ�����,�����
			logger.info("��ӽڵ�����: adding link " + nnlink.toString());
			links.add(nnlink);
		} else if (assitantLink == 1) {
			// �Ѿ�����һ��,��Ҫ�Ѹ���������Ϊ��������
			logger.info("��ӽڵ�����: adding link " + nnlink.toString());
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
					logger.info("���Ӵ���: link " + nnlink.toString() + " exists, not adding");
				}
			}
		}
		logger.info("�����ӵ���: " + counts + " ��" + nnlink.toString());
		return counts;
	}

	@SuppressWarnings("unused")
	private boolean parseCdpLinkOn(Host node1, int ifindex1, int nodeid2) {

		int bridgeport = Integer.parseInt(node1.getIfEntityByIndex(ifindex1 + "").getPort());

		if (node1.isBackBoneBridgePort(bridgeport)) {
			logger.info("����CDPLINK����: node/backbone bridge port " + node1.getId() + "/" + bridgeport + " already parsed. Skipping");
			return false;
		}

		if (isEndBridgePort(node1, bridgeport)) {

			node1.addBackBoneBridgePorts(bridgeport);
			bridgeNodes.put(new Integer(node1.getId()), node1);
			Set<String> macs = node1.getMacAddressesOnBridgePort(bridgeport);
			addLinks(macs, node1.getId(), ifindex1);
		} else {
			logger.info("����CDPLINK����: link cannot be saved. Skipping");
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
						logger.info("getBridgePortOnEndBridge: found backbone bridge port " + port + " .... ����");
						continue;
					}
					if (port == -1) {
						logger.info("run: no port found on bridge nodeid " + endBridge.getId() + " for node bridge identifiers nodeid " + startBridge.getId()
								+ " . .....Skipping");
						continue;
					}
					logger.info("run: using mac address table found bridge port " + port + " on node " + endBridge.getId());
					return port;
				}

			} else {
				logger.info("����: 1bridge identifier not found on node " + endBridge.getId());
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
