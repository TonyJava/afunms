/**
 * <p>Description:probe the router table</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-13
 */

package com.afunms.discovery;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.afunms.common.util.SnmpUtil;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;

public class CDPSubThread {

	/**
	 * ��������
	 */
	public static Runnable createTask(final CdpCachEntryInterface cdp, final Host node) {
		return new Runnable() {
			@SuppressWarnings( { "static-access", "unchecked" })
			public void run() {
				if (DiscoverEngine.getInstance().getStopStatus() == 1)
					return;
				Set shieldList = DiscoverResource.getInstance().getShieldSet();
				List netshieldList = DiscoverResource.getInstance().getNetshieldList();
				List netincludeList = DiscoverResource.getInstance().getNetincludeList();
				try {
					String cdpip = cdp.getIp();
					String cdpportdesc = cdp.getPortdesc();
					List faildIpList = DiscoverEngine.getInstance().getFaildIpList();
					if (faildIpList != null && faildIpList.size() > 0) {
						for (int i = 0; i < faildIpList.size(); i++) {
							SysLogger.info(" �ɼ�ʧ�ܵ�IP����    " + (String) faildIpList.get(i) + " ");
						}
						if (faildIpList.contains(cdpip))
							return;
					}

					// �жϸ�IP�Ƿ���ֻ��Ҫ���ֵ�������
					int netincludeflag = 0;
					if (netincludeList != null && netincludeList.size() > 0) {
						long longip = DiscoverEngine.getInstance().ip2long(cdpip);
						for (int k = 0; k < netincludeList.size(); k++) {
							Vector netinclude = (Vector) netincludeList.get(k);
							if (netinclude != null && netinclude.size() == 2) {
								try {
									if (longip >= ((Long) netinclude.get(0)).longValue() && longip <= ((Long) netinclude.get(1)).longValue()) {
										SysLogger.info("�豸IP " + cdpip + "������Ҫ���ֵ�����");
										netincludeflag = 1;
										break;
									}
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
						}
						if (netincludeflag == 0)
							return;
					}

					// �жϸ�IP�Ƿ��ڱ����ε�������
					int netshieldflag = 0;
					if (netshieldList != null && netshieldList.size() > 0) {
						long longip = DiscoverEngine.getInstance().ip2long(cdpip);
						for (int k = 0; k < netshieldList.size(); k++) {
							Vector netshield = (Vector) netshieldList.get(k);
							if (netshield != null && netshield.size() == 2) {
								try {
									if (longip >= ((Long) netshield.get(0)).longValue() && longip <= ((Long) netshield.get(1)).longValue()) {
										SysLogger.info("�豸IP " + cdpip + "���ڱ���������");
										netshieldflag = 1;
										break;
									}
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
						}
						if (netshieldflag == 1)
							return;
					}

					for (int sh = 0; sh < shieldList.size(); sh++) {
						if (shieldList.contains(cdpip))
							return;
					}

					IfEntity nodeifEntity = null;
					IfEntity host_ifEntity = null;

					// �ж��Ѿ����ֵ��豸�б����Ƿ��Ѿ����ڸ�IP(���߸�IP��Ӧ�Ĺ���IP)
					SysLogger.info("��ʼ�ж�IP " + cdpip + "�Ƿ��Ѿ����Ѿ����ֵ�IP����IP����");
					List hostList = DiscoverEngine.getInstance().getHostList();
					Host existHost = null;
					if (hostList != null && hostList.size() > 0) {
						for (int k = 0; k < hostList.size(); k++) {
							Host tmpNode = (Host) hostList.get(k);
							if (tmpNode.getIpAddress().equalsIgnoreCase(cdpip)) {
								existHost = tmpNode;
								SysLogger.info("�ѷ��ֵ��豸�б����Ѿ�����" + tmpNode.getCategory() + "���豸:" + cdpip);
								break;
							} else {
								// �жϱ���IP�Ƿ����
								List aliasIPs = tmpNode.getAliasIPs();
								if (aliasIPs != null && aliasIPs.size() > 0) {
									if (aliasIPs.contains(cdpip)) {
										existHost = tmpNode;
										SysLogger.info("�ѷ��ֵ��豸�б����Ѿ�����" + tmpNode.getCategory() + "���豸:" + cdpip);
										break;
									}
								}
							}

						}
					}
					SysLogger.info("�����ж�IP " + cdpip + "�Ƿ��Ѿ����Ѿ����ֵ�IP����IP����");

					// �ж��Ѿ�����ʧ�ܵ��豸�б����Ƿ��Ѿ����ڸ�IP
					List cdplist = null;
					Host host = new Host();
					if (existHost != null) {
						host = existHost;
						cdplist = host.getCdpList();
						return;
					} else {
						String community = SnmpUtil.getInstance().getCommunity(cdpip);
						if (community == null) {
							DiscoverEngine.getInstance().getFaildIpList().add(cdpip);
							return;
						}
						String sysOid = SnmpUtil.getInstance().getSysOid(cdpip, community);
						if (sysOid == null) {
							DiscoverEngine.getInstance().getFaildIpList().add(cdpip);
							return;
						}
						int deviceType = SnmpUtil.getInstance().checkDevice(cdpip, community, sysOid);

						try {
							cdplist = SnmpUtil.getInstance().getCiscoCDPList(cdpip, community);
						} catch (Exception e) {
							e.printStackTrace();
						}
						host.setCategory(deviceType);
						host.setCommunity(community);
						host.setWritecommunity(DiscoverEngine.getInstance().getWritecommunity());
						host.setSnmpversion(DiscoverEngine.getInstance().getSnmpversion());
						host.setSysOid(sysOid);

						host.setSuperNode(node.getId());
						host.setLocalNet(node.getLocalNet());
						host.setIpAddress(cdpip);
						host.setLayer(node.getLayer() + 1);
						try {
							host.setIfEntityList(SnmpUtil.getInstance().getIfEntityList(cdpip, community, deviceType));
						} catch (Exception e) {

						}
					}
					try {
						host_ifEntity = host.getIfEntityByDesc(cdpportdesc);
					} catch (Exception e) {

					}
					if (cdplist == null || cdplist.size() == 0) {
						for (int k = 0; k < cdplist.size(); k++) {
							CdpCachEntryInterface node_cdp = (CdpCachEntryInterface) cdplist.get(k);
							String node_cdpip = node_cdp.getIp();
							String node_cdpportdesc = node_cdp.getPortdesc();
							if (node_cdpip != null && node_cdpip.trim().length() > 0) {
								// ��Ҫ����IP�������ж�
								if (node.getAliasIPs().contains(node_cdpip)) {
									nodeifEntity = node.getIfEntityByDesc(node_cdpportdesc);
									break;
								}
							}
						}
					}
					int sublinktype = SystemConstant.NONEPHYSICALLINK;
					if (nodeifEntity != null && host_ifEntity != null) {
						sublinktype = SystemConstant.BOTHPHYSICALLINK;
					} else if (nodeifEntity != null && host_ifEntity == null) {
						sublinktype = SystemConstant.STARTPHYSICALLINK;
					} else if (nodeifEntity == null && host_ifEntity != null) {
						sublinktype = SystemConstant.ENDPHYSICALLINK;
					}
					if (nodeifEntity == null) {
						nodeifEntity = node.getIfEntityByIP(node.getIpAddress());
					}
					Link link = new Link();
					link.setStartId(node.getId());
					link.setStartIndex(nodeifEntity.getIndex());
					if (nodeifEntity.getIpAddress() != null && nodeifEntity.getIpAddress().trim().length() > 0)
						link.setStartIp(nodeifEntity.getIpAddress());
					else
						link.setStartIp(node.getIpAddress());
					link.setStartPort(nodeifEntity.getIndex());
					link.setStartPhysAddress(nodeifEntity.getPhysAddress());
					link.setStartDescr(nodeifEntity.getDescr());
					link.setEndIp(cdpip);
					if (host_ifEntity != null) {
						link.setEndIndex(host_ifEntity.getIndex());
						link.setEndDescr(host_ifEntity.getDescr());
						link.setEndPhysAddress(host_ifEntity.getPhysAddress());
						link.setEndPort(host_ifEntity.getPort());
					}
					link.setFindtype(SystemConstant.ISCDP);// ����CDP����
					link.setSublinktype(sublinktype);
					try {
						DiscoverEngine.getInstance().addHost(host, link);
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
	}
}