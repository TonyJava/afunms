package com.gatherResulttosql;

import java.util.Hashtable;
import java.util.Vector;

import com.afunms.config.model.Nodeconfig;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.gatherdb.GathersqlListManager;

public class HostDatatempNodeconfRtosql {

	/**
	 * �ѽ������sql
	 * 
	 * @param dataresult
	 *            �ɼ����
	 * @param node
	 *            ��Ԫ�ڵ�
	 */
	@SuppressWarnings("unchecked")
	public void CreateResultTosql(Hashtable dataresult, Host node) {

		if ("1".equals(PollingEngine.getCollectwebflag())) {// �Ƿ���������ģʽ
			// ����nodeconfig��Ϣ���

			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);
			Nodeconfig nodeconfig = (Nodeconfig) dataresult.get("nodeconfig");
			String deleteSql = "delete from nms_nodeconfig where nodeid='" + node.getId() + "'";

			Vector list = new Vector();
			if (nodeconfig != null) {
				try {
					StringBuffer sql = new StringBuffer(200);
					sql.append("insert into nms_nodeconfig(nodeid,hostname,sysname,serialNumber,cSDVersion,numberOfProcessors,mac)values('");
					sql.append(nodeDTO.getId());
					sql.append("','");
					sql.append(nodeconfig.getHostname());// hostname
					sql.append("','");
					sql.append(nodeconfig.getSysname());// sysname
					sql.append("','");
					sql.append(nodeconfig.getSerialNumber());// serialNumber
					sql.append("','");
					sql.append(nodeconfig.getCSDVersion());// cSDVersion
					sql.append("','");
					sql.append(nodeconfig.getNumberOfProcessors());// numberOfProcessors
					sql.append("','");
					sql.append(nodeconfig.getMac());// mac
					sql.append("')");
					list.add(sql.toString());

					GathersqlListManager.AdddateTempsql(deleteSql, list);
					deleteSql = null;
					sql = null;
					nodeconfig = null;
					nodeDTO = null;

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
