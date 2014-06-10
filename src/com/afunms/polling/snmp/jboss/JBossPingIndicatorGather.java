package com.afunms.polling.snmp.jboss;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.JBossConfigDao;
import com.afunms.application.jbossmonitor.HttpClientJBoss;
import com.afunms.application.model.JBossConfig;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.api.IndicatorGather;
import com.afunms.polling.node.Result;
import com.afunms.polling.om.PingCollectEntity;
import com.gatherResulttosql.JBossPingResultTosql;

@SuppressWarnings("unchecked")
public class JBossPingIndicatorGather extends SnmpMonitor implements IndicatorGather {

	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		JBossConfig node = null;
		JBossConfigDao dao = new JBossConfigDao();
		try {
			node = (JBossConfig) dao.findByID(nodeGatherIndicators.getNodeid());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		Result result = getValue(node, nodeGatherIndicators);
		PingCollectEntity pingcollectdata = (PingCollectEntity) result.getResult();
		Vector<PingCollectEntity> vector = new Vector<PingCollectEntity>();
		vector.add(pingcollectdata);
		String value = pingcollectdata.getThevalue();

		NodeDTO nodeDTO = new NodeUtil().conversionToNodeDTO(node);
		String nodeid = nodeDTO.getNodeid();
		String type = nodeDTO.getType();
		String subtype = nodeDTO.getSubtype();
		AlarmIndicatorsUtil util = new AlarmIndicatorsUtil();
		List<AlarmIndicatorsNode> list = util.getAlarmIndicatorsForNode(nodeid, type, subtype);
		try {
			CheckEventUtil checkEventUtil = new CheckEventUtil();
			if (list != null) {
				for (AlarmIndicatorsNode alarmIndicatorsNode : list) {
					if ("ping".equals(alarmIndicatorsNode.getName())) {
						checkEventUtil.checkEvent(node, alarmIndicatorsNode, value);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Hashtable<String, Vector<PingCollectEntity>> ipdata = new Hashtable<String, Vector<PingCollectEntity>>();
		ipdata.put("ping", vector);
		try {
			JBossPingResultTosql resultTosql = new JBossPingResultTosql();
			resultTosql.CreateResultTosql(ipdata, nodeid);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return ipdata;
	}

	@SuppressWarnings("static-access")
	public Result getValue(BaseVo node, NodeGatherIndicators nodeGatherIndicators) {
		JBossConfig jbossConfig = (JBossConfig) node;
		String ipaddress = jbossConfig.getIpaddress();
		HttpClientJBoss jboss = new HttpClientJBoss();
		String src = null;
		try {
			src = jboss.getGetResponseWithHttpClient("http://" + ipaddress + ":" + jbossConfig.getPort() + "/web-console/ServerInfo.jsp", "GBK");
		} catch (Exception e) {
		}
		Calendar date = Calendar.getInstance();
		PingCollectEntity pingcollectdata = new PingCollectEntity();
		pingcollectdata.setIpaddress(ipaddress);
		pingcollectdata.setCollecttime(date);
		pingcollectdata.setCategory("Ping");
		pingcollectdata.setEntity("Utilization");
		pingcollectdata.setSubentity("ConnectUtilization");
		pingcollectdata.setRestype("dynamic");
		pingcollectdata.setUnit("%");
		if (src != null && src.contains("Version")) {
			pingcollectdata.setThevalue("100");
		} else {
			pingcollectdata.setThevalue("0");
		}
		Result result = new Result();
		result.setCollectTime(date.getTime());
		result.setErrorCode(1);
		result.setErrorInfo("");
		result.setResult(pingcollectdata);
		return result;
	}
}
