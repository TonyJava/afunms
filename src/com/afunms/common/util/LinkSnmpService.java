/**
 * <p>Description:snmp service</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-10
 */

package com.afunms.common.util;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.smi.Counter32;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.PDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableListener;
import org.snmp4j.util.TableUtils;

@SuppressWarnings("unchecked")
public class LinkSnmpService implements PDUFactory {
	public static int default_version = org.snmp4j.mp.SnmpConstants.version1;
	public static int otherversion = org.snmp4j.mp.SnmpConstants.version2c;
	public static final int default_retries = 3;
	public static String snmpversion = "";

	private Vector vbs = new Vector();
	OID lowerBoundIndex, upperBoundIndex;
	private Integer default_port = new Integer(161);
	private int default_timeout = 5000;
	private int m_TimeOut = 5000;

	public PDU createPDU(Target target) {
		PDU request = new PDU();
		request.setType(PDU.GET);
		return request;
	}

	public Snmp createSnmpSession() throws IOException {

		TransportMapping transport = new DefaultUdpTransportMapping();

		Snmp snmp = new Snmp(transport);

		return snmp;
	}

	public List createTable(String address, String community, String[] oids) throws IOException {
		List list = null;
		Snmp snmp = createSnmpSession();
		Target target = createTarget(community);
		target.setVersion(default_version);
		target.setAddress(GenericAddress.parse(address + "/" + default_port));
		target.setRetries(default_retries);
		target.setTimeout(default_timeout);
		try {
			snmp.listen();
		} catch (Exception e) {
			e.printStackTrace();
			if (snmp != null) {
				snmp.close();
			}
			snmp = null;
			return null;
		}
		TableUtils tableUtils = new TableUtils(snmp, this);
		OID[] columns = new OID[oids.length];
		for (int i = 0; i < columns.length; i++) {
			columns[i] = (new VariableBinding(new OID(oids[i]))).getOid();
		}
		try {
			list = tableUtils.getTable(target, columns, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		} finally {
			if (target != null) {
				target = null;
			}
			if (snmp != null) {
				snmp.close();
			}
			snmp = null;
		}
		if (snmpversion.equalsIgnoreCase("v1+v2") || snmpversion.equalsIgnoreCase("v2+v1")) {
			// 混合用V1和V2版本
			if (list == null || (list != null && list.size() > 0 && list.get(0) != null && ((TableEvent) list.get(0)).getColumns() == null)) {
				// 用SNMP不同的版本采集
				snmp = createSnmpSession();
				target = createTarget(community);
				target.setVersion(otherversion);
				target.setAddress(GenericAddress.parse(address + "/" + default_port));
				target.setRetries(default_retries);
				target.setTimeout(default_timeout);
				snmp.listen();
				tableUtils = new TableUtils(snmp, this);
				columns = new OID[oids.length];
				for (int i = 0; i < columns.length; i++) {
					columns[i] = (new VariableBinding(new OID(oids[i]))).getOid();
				}
				try {
					list = tableUtils.getTable(target, columns, null, null);
				} catch (Exception e) {
					e.printStackTrace();
					list = null;
				} finally {
					if (target != null) {
						target = null;
					}
					if (snmp != null) {
						snmp.close();
					}
					snmp = null;
				}
			}
		}

		// end
		target = null;
		tableUtils = null;
		return list;
	}

	protected Target createTarget(String community) {
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString(community));
		return target;

	}

	public String[][] getCiscoVlanTable1(String address, String community, String[] oid) throws IOException {
		Snmp snmp = createSnmpSession();
		Target target = createTarget(community);
		target.setVersion(default_version);
		target.setAddress(GenericAddress.parse(address + "/" + default_port));
		target.setRetries(default_retries);
		target.setTimeout(default_timeout);

		TableUtils tableUtils = new TableUtils(snmp, this);
		tableUtils.setMaxNumRowsPerPDU(10);
		Counter32 counter = new Counter32();

		OID[] columns = new OID[oid.length];
		for (int i = 0; i < columns.length; i++) {
			columns[i] = new OID(oid[i]);
		}
		TableListener listener = new TextTableListener1();
		try {
			synchronized (counter) {
				tableUtils.getTable(target, columns, listener, counter, null, null);
				counter.wait(m_TimeOut * target.getRetries() + 3000);
			}

		} catch (InterruptedException e) {

		}

		int x = ((TextTableListener1) listener).getResult().size();
		int y = 0;
		if (x > 0) {
			y = ((TextTableListener1) listener).getResult().get(0).getColumns().length;
		}
		String[][] s = new String[x][y + 1];

		for (int i = 0; i < x; ++i) {
			s[i][y] = ((TextTableListener1) listener).getResult().get(i).getIndex().toString();

		}
		return s;
	}

	public String[][] getCiscoVlanTableData(String address, String community, String[] columnoids) throws Exception {
		String[][] tablevalues = null;
		try {
			List rowvalues = createTable(address, community, columnoids);
			TableEvent row = null;
			VariableBinding[] columnvalues = null;
			VariableBinding columnvalue = null;
			tablevalues = new String[rowvalues.size()][columnoids.length + 1];

			for (int i = 0; i < rowvalues.size(); i++) {
				row = (TableEvent) rowvalues.get(i);
				columnvalues = row.getColumns();
				if (columnvalues != null) {
					for (int j = 0; j < columnvalues.length; j++) {
						columnvalue = columnvalues[j];
						String[] strtemp = columnvalue.getOid().toString().split("\\.");
						String value = columnvalue.toString().substring(columnvalue.toString().indexOf("=") + 1, columnvalue.toString().length()).trim();
						tablevalues[i][j] = value;
						if (strtemp[strtemp.length - 1].equalsIgnoreCase("0")) {
							// 取倒数第二位
							tablevalues[i][j + 1] = strtemp[strtemp.length - 2];
						} else {
							tablevalues[i][j + 1] = strtemp[strtemp.length - 1];// 取最后一位
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			tablevalues = null;
		}
		return tablevalues;
	}

	public String[][] getCpuTableData(String address, String community, String[] columnoids) throws Exception {
		String[][] tablevalues = null;
		TableEvent row = null;
		VariableBinding[] columnvalues = null;
		VariableBinding columnvalue = null;

		try {
			List rowvalues = createTable(address, community, columnoids);
			if (rowvalues == null) {
				return tablevalues;
			}
			tablevalues = new String[rowvalues.size()][columnoids.length + 1];

			for (int i = 0; i < rowvalues.size(); i++) {
				row = (TableEvent) rowvalues.get(i);
				columnvalues = row.getColumns();

				if (columnvalues != null) {
					for (int j = 0; j < columnvalues.length; j++) {
						columnvalue = columnvalues[j];
						if (columnvalue == null) {
							continue;
						}
						String value = columnvalue.toString().substring(columnvalue.toString().indexOf("=") + 1, columnvalue.toString().length()).trim();
						tablevalues[i][j] = value;
						tablevalues[i][j + 1] = row.getIndex().toString();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			tablevalues = null;
		}
		row = null;
		columnvalues = null;
		columnvalue = null;
		return tablevalues;
	}

	public String getMibValue(String address, String community, String oid) {
		String sResponse = null;
		PDU response = null;
		try {
			Vector _vbs = new Vector();
			VariableBinding vb = new VariableBinding(new OID(oid));
			_vbs.add(vb);
			response = this.send(community, address, _vbs);
			String operat = response.getVariableBindings().get(0).toString();
			sResponse = operat.substring(operat.lastIndexOf("=") + 1, operat.length()).trim();
			response.clear();
			if (sResponse.equalsIgnoreCase("Null")) {
				response.clear();
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			if (response != null) {
				response.clear();
			}
			return null;
		}
		response = null;
		return sResponse;
	}

	public String[][] getNDPTableData(String address, String community, String[] columnoids) throws Exception {
		String[][] tablevalues = null;
		TableEvent row = null;
		VariableBinding[] columnvalues = null;
		VariableBinding columnvalue = null;
		try {
			List rowvalues = createTable(address, community, columnoids);
			if (rowvalues == null) {
				return tablevalues;
			}

			tablevalues = new String[rowvalues.size()][columnoids.length + 1];

			for (int i = 0; i < rowvalues.size(); i++) {
				row = (TableEvent) rowvalues.get(i);
				columnvalues = row.getColumns();
				if (columnvalues != null) {
					for (int j = 0; j < columnvalues.length; j++) {
						columnvalue = columnvalues[j];
						String[] strtemp = columnvalue.getOid().toString().split("\\.");
						String value = columnvalue.toString().substring(columnvalue.toString().indexOf("=") + 1, columnvalue.toString().length()).trim();
						tablevalues[i][j] = value;

						if (strtemp[strtemp.length - 1].equalsIgnoreCase("0")) {
							// 取倒数第二位
							tablevalues[i][j + 1] = strtemp[strtemp.length - 2];
						} else {
							tablevalues[i][j + 1] = strtemp[strtemp.length - 1];// 取最后一位
						}
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			tablevalues = null;
		}
		row = null;
		columnvalues = null;
		columnvalue = null;
		return tablevalues;
	}

	public String[][] getTableData(String address, String community, String[] columnoids) throws Exception {
		String[][] tablevalues = null;
		TableEvent row = null;
		VariableBinding[] columnvalues = null;
		VariableBinding columnvalue = null;

		try {
			List rowvalues = null;
			try {
				rowvalues = createTable(address, community, columnoids);
			} catch (Exception e) {

			}
			if (rowvalues == null) {
				return tablevalues;
			}
			tablevalues = new String[rowvalues.size()][columnoids.length];

			for (int i = 0; i < rowvalues.size(); i++) {
				row = (TableEvent) rowvalues.get(i);
				columnvalues = row.getColumns();

				if (columnvalues != null) {
					for (int j = 0; j < columnvalues.length; j++) {
						columnvalue = columnvalues[j];
						if (columnvalue == null) {
							continue;
						}

						String value = columnvalue.toString().substring(columnvalue.toString().indexOf("=") + 1, columnvalue.toString().length()).trim();
						tablevalues[i][j] = value;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			tablevalues = null;
		}
		row = null;
		columnvalues = null;
		columnvalue = null;
		return tablevalues;
	}

	public String[][] getTemperatureTableData(String address, String community, String[] columnoids) throws Exception {
		String[][] tablevalues = null;
		TableEvent row = null;
		VariableBinding[] columnvalues = null;
		VariableBinding columnvalue = null;

		try {
			List rowvalues = createTable(address, community, columnoids);
			if (rowvalues == null) {
				return tablevalues;
			}
			tablevalues = new String[rowvalues.size()][columnoids.length + 1];

			for (int i = 0; i < rowvalues.size(); i++) {
				row = (TableEvent) rowvalues.get(i);
				columnvalues = row.getColumns();

				if (columnvalues != null) {
					for (int j = 0; j < columnvalues.length; j++) {
						columnvalue = columnvalues[j];
						if (columnvalue == null) {
							continue;
						}
						String value = columnvalue.toString().substring(columnvalue.toString().indexOf("=") + 1, columnvalue.toString().length()).trim();
						tablevalues[i][j] = value;
						tablevalues[i][columnoids.length] = row.getIndex().toString();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			tablevalues = null;
		}
		row = null;
		columnvalues = null;
		columnvalue = null;
		return tablevalues;
	}

	public PDU send(String community, String address) throws IOException {

		Snmp snmp = createSnmpSession();
		Target target = createTarget(community);
		target.setVersion(default_version);
		target.setAddress(GenericAddress.parse(address + "/" + default_port));
		target.setRetries(default_retries);
		target.setTimeout(default_timeout);
		snmp.listen();

		PDU request = createPDU(target);

		for (int i = 0; i < vbs.size(); i++) {
			request.add((VariableBinding) vbs.get(i));
		}

		PDU response = null;
		try {
			response = snmp.send(request, target).getResponse();
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			snmp.close();
			snmp = null;
			response.clear();
		}
		if (snmpversion.equalsIgnoreCase("v1+v2") || snmpversion.equalsIgnoreCase("v2+v1")) {
			// 混合用V1和V2版本
			if (response == null || response.getErrorIndex() > 0) {
				// 换一个SNMP版本
				snmp = createSnmpSession();
				target = createTarget(community);
				target.setVersion(otherversion);
				target.setAddress(GenericAddress.parse(address + "/" + default_port));
				target.setRetries(default_retries);
				target.setTimeout(default_timeout);
				snmp.listen();

				request = createPDU(target);

				for (int i = 0; i < vbs.size(); i++) {
					request.add((VariableBinding) vbs.get(i));
				}
				response = null;
				try {
					response = snmp.send(request, target).getResponse();
				} catch (IOException e) {
					throw e;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					snmp.close();
					snmp = null;
				}
			}
		}
		snmp = null;

		target = null;
		request = null;

		return response;
	}

	public PDU send(String community, String address, Vector _vbs) throws IOException {
		Snmp snmp = createSnmpSession();
		Target target = createTarget(community);
		target.setVersion(default_version);
		target.setAddress(GenericAddress.parse(address + "/" + default_port));
		target.setRetries(default_retries);
		target.setTimeout(default_timeout);
		snmp.listen();

		PDU request = createPDU(target);

		for (int i = 0; i < _vbs.size(); i++) {
			request.add((VariableBinding) _vbs.get(i));
		}

		PDU response = null;
		try {
			response = snmp.send(request, target).getResponse();
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			snmp.close();
		}
		if (snmpversion.equalsIgnoreCase("v1+v2") || snmpversion.equalsIgnoreCase("v2+v1")) {
			// 混合用V1和V2版本
			if (response == null || response.getErrorIndex() > 0) {
				// 换一个SNMP版本
				snmp = createSnmpSession();
				target = createTarget(community);
				target.setVersion(otherversion);
				target.setAddress(GenericAddress.parse(address + "/" + default_port));
				target.setRetries(default_retries);
				target.setTimeout(default_timeout);
				snmp.listen();

				request = createPDU(target);

				for (int i = 0; i < _vbs.size(); i++) {
					request.add((VariableBinding) _vbs.get(i));
				}
				response = null;
				try {
					response = snmp.send(request, target).getResponse();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					snmp.close();
				}
			}
		}
		snmp = null;
		target = null;
		request = null;

		return response;
	}

	public void setMibValues(String address, String community, int version, String[] oids, String[] mibvalues) {
		try {
			CommunityTarget target = new CommunityTarget();
			target.setCommunity(new OctetString(community));
			target.setVersion(version);
			target.setAddress(GenericAddress.parse(address + "/" + new Integer(161)));
			target.setRetries(1);
			target.setTimeout(5000);
			VariableBinding[] values = new VariableBinding[oids.length];
			for (int i = 0; i < values.length; i++) {
				values[i] = new VariableBinding(new OID(oids[i]), new OctetString(mibvalues[i]));
			}

			PDU pdu = new PDU();
			for (int i = 0; i < values.length; i++) {
				pdu.add(values[i]);
			}
			pdu.setType(PDU.SET);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setVariableBindings(String oid) {
		vbs = new Vector();
		VariableBinding vb = new VariableBinding(new OID(oid));
		vbs.add(vb);
	}

	public void setVariableBindings(String[] oids) {
		vbs = new Vector();
		for (int i = 0; i < oids.length; i++) {
			String oid = oids[i];
			VariableBinding vb = new VariableBinding(new OID(oid));
			vbs.add(vb);
		}
	}

	public List table(String community, String address, String[] columnoids) throws IOException {
		List list = null;

		Snmp snmp = null;
		try {
			snmp = createSnmpSession();
			Target target = createTarget(community);
			target.setVersion(default_version);
			target.setAddress(GenericAddress.parse(address + "/" + default_port));
			target.setRetries(default_retries);
			target.setTimeout(default_timeout);
			snmp.listen();
			TableUtils tableUtils = new TableUtils(snmp, this);

			OID[] columns = new OID[columnoids.length];
			for (int i = 0; i < columns.length; i++) {
				columns[i] = (new VariableBinding(new OID(columnoids[i]))).getOid();
			}

			list = tableUtils.getTable(target, columns, lowerBoundIndex, upperBoundIndex);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			snmp.close();
		}
		return list;
	}
}
