package com.afunms.polling.snmp.dhcp;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.afunms.application.dao.DHCPConfigDao;
import com.afunms.application.model.DHCPConfig;
import com.afunms.application.weblogicmonitor.AbstractSnmp;
import com.afunms.application.weblogicmonitor.WeblogicTrans;
import com.afunms.common.util.PingUtil;

@SuppressWarnings("unchecked")
public class DhcpScopeSnmp extends AbstractSnmp {
	private String nethost = "1.1.1.1";

	public DhcpScopeSnmp(String host, String community, Integer port) {
		super(community, port, 1600);
		this.nethost = host;
	}

	public Hashtable collectData() {
		Hashtable rValue = new Hashtable();
		List dhcpscopeValue = collectDHCPScopeData();
		List dhcpparValue = collectTransData();
		rValue.put("dhcpscope", dhcpscopeValue);
		rValue.put("dhcppar", dhcpparValue);
		return rValue;
	}

	public Hashtable collectData(Hashtable gatherhash, DHCPConfig dhcpconf) {
		Hashtable rValue = new Hashtable();
		List dhcpscopeValue = new ArrayList();
		Vector dhcppingvector = new Vector();
		if (gatherhash.containsKey("dhcpscope")) {
			// �ɼ���
			try {
				dhcpscopeValue = collectDHCPScopeData();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (gatherhash.containsKey("ping")) {
			// ��ͨ��
			PingUtil pingU = new PingUtil(nethost);
			try {
				Integer[] packet = pingU.ping();
				dhcppingvector = pingU.addhis(packet);
				if (dhcppingvector != null) {
					DHCPConfigDao dhcpconfigdao = new DHCPConfigDao();
					try {
						dhcpconfigdao.createHostData(dhcppingvector, dhcpconf);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						dhcpconfigdao.close();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				dhcpscopeValue = collectDHCPScopeData();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		rValue.put("dhcpscopeValue", dhcpscopeValue);
		rValue.put("dhcpping", dhcppingvector);
		return rValue;
	}

	public List collectDHCPScopeData() {
		List dhcpscopeList = new ArrayList();
		Hashtable scope = new Hashtable();

		try {
			try {
				String[] oids = new String[] { ".1.3.6.1.4.1.311.1.3.2.1.1.1", // ������ַ
						".1.3.6.1.4.1.311.1.3.2.1.1.2", // in use
						".1.3.6.1.4.1.311.1.3.2.1.1.3", // in free
						".1.3.6.1.4.1.311.1.3.2.1.1.4" // PendingOffers

				};
				this.setVariableBindings(oids);
				List list = this.table(this.getDefault_community(), nethost);
				for (int i = 0; i < list.size(); i++) {

					TableEvent tbevent = (TableEvent) list.get(i);
					VariableBinding[] vb = tbevent.getColumns();
					if (vb != null) {
						String vbString = vb[0].toString();

						for (int j = 0; j < vb.length; j++) {
							if (vb[j] != null) {
								vbString = vb[j].toString();
								String sValue = vbString.substring(vbString.lastIndexOf("=") + 1, vbString.length()).trim();
								if (j == 0) {
									scope.put("netadd", sValue);
								} else if (j == 1) {
									scope.put("inuse", sValue);
								} else if (j == 2) {
									scope.put("free", sValue);
								} else if (j == 3) {
									scope.put("pendingoffers", sValue);
								}
							}
						}
						dhcpscopeList.add(scope);
						scope = new Hashtable();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dhcpscopeList;
	}

	public List collectTransData() {
		List transList = new ArrayList();
		WeblogicTrans trans = new WeblogicTrans();

		try {
			try {
				String[] oids = new String[] { ".1.3.6.1.4.1.140.625.420.1.45", //
						".1.3.6.1.4.1.140.625.420.1.25", //
						".1.3.6.1.4.1.140.625.420.1.30", //
						".1.3.6.1.4.1.140.625.420.1.35", //
						".1.3.6.1.4.1.140.625.420.1.40", //
						".1.3.6.1.4.1.140.625.420.1.50", //
						".1.3.6.1.4.1.140.625.420.1.55", //
						".1.3.6.1.4.1.140.625.420.1.60", //
						".1.3.6.1.4.1.140.625.420.1.65" //
				};
				this.setVariableBindings(oids);
				List list = this.table(this.getDefault_community(), nethost);
				for (int i = 0; i < list.size(); i++) {

					TableEvent tbevent = (TableEvent) list.get(i);
					VariableBinding[] vb = tbevent.getColumns();
					if (vb != null) {
						String vbString = vb[0].toString();

						for (int j = 0; j < vb.length; j++) {
							if (vb[j] != null) {
								vbString = vb[j].toString();
								String sValue = vbString.substring(vbString.lastIndexOf("=") + 1, vbString.length()).trim();
								if (j == 0) {
									trans.setTransactionResourceRuntimeResourceName(sValue);
								} else if (j == 1) {
									trans.setTransactionResourceRuntimeTransactionTotalCount(sValue);
								} else if (j == 2) {
									trans.setTransactionResourceRuntimeTransactionCommittedTotalCount(sValue);
								} else if (j == 3) {
									trans.setTransactionResourceRuntimeTransactionRolledBackTotalCount(sValue);
								} else if (j == 4) {
									trans.setTransactionResourceRuntimeTransactionHeuristicsTotalCount(sValue);
								} else if (j == 5) {
									trans.setTransactionResourceRuntimeTransactionHeuristicCommitTotalCount(sValue);
								} else if (j == 6) {
									trans.setTransactionResourceRuntimeTransactionHeuristicRollbackTotalCount(sValue);
								} else if (j == 7) {
									trans.setTransactionResourceRuntimeTransactionHeuristicMixedTotalCount(sValue);
								} else if (j == 8) {
									trans.setTransactionResourceRuntimeTransactionHeuristicHazardTotalCount(sValue);
								}
							}
						}
						transList.add(trans);
						trans = new WeblogicTrans();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return transList;
	}

	public int getInterval(float d, String t) {
		int interval = 0;
		if (t.equals("d"))
			interval = (int) d * 24 * 60 * 60; // ����
		else if (t.equals("h"))
			interval = (int) d * 60 * 60; // Сʱ
		else if (t.equals("m"))
			interval = (int) d * 60; // ����
		else if (t.equals("s"))
			interval = (int) d; // ��
		return interval;
	}

}
