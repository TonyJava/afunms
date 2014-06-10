package com.afunms.mq;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.pcf.CMQC;
import com.ibm.mq.pcf.CMQCFC;
import com.ibm.mq.pcf.CMQXC;
import com.ibm.mq.pcf.PCFException;
import com.ibm.mq.pcf.PCFMessage;
import com.ibm.mq.pcf.PCFMessageAgent;
import com.icss.ro.de.connector.mqimpl.MQNode;

@SuppressWarnings("unchecked")
public class MqManager {

	private Hashtable properties = new Hashtable();;
	private MQQueueManager mqqueuemanager;
	private String transport = DEServerConstants.MQ_TRANSPORT;
	private PCFMessageAgent agent;
	private PCFMessage request;
	private PCFMessage responses[];

	public MqManager() throws Exception {

		try {
			agent = new PCFMessageAgent();
			agent.setCharacterSet(DEServerConstants.MQ_CHARATERSET);
			agent.setEncoding(DEServerConstants.MQ_ENCODING);

		} catch (Exception e) {
			throw new Exception("mq.connection.init.failed");
		}
	}

	public boolean connMQ(MQNode node) {
		try {
			if (node == null) {
				throw new Exception("Init failed! MQnode is null.");
			}
			// 设置properties项
			properties.put("port", new Integer(Integer.parseInt(node.getPort())));
			properties.put("CCSID", new Integer(DEServerConstants.MQ_CHARATERSET));
			properties.put("hostname", node.getHost());
			properties.put("transport", transport);
			properties.put("channel", DEServerConstants.MQ_SVRCONN);
			// 使用缓冲池
			mqqueuemanager = new MQQueueManager(node.getQmanager(), properties);
			agent.connect(mqqueuemanager);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	// 取队列的名称

	/**
	 * 取得通道状态
	 * 
	 * @param channelName
	 *            通道的名称
	 * @return int 状态代码
	 */
	public void freeConn() {
		try {
			mqqueuemanager.disconnect();
		} catch (MQException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取得通道状态
	 * 
	 * @param channelName
	 *            通道的名称
	 * @return int 状态代码
	 */
	public int getChlStatus(String channelName) {
		Integer status = new Integer(-1);
		PCFMessage[] responses_status;
		try {
			request = new PCFMessage(CMQCFC.MQCMD_INQUIRE_CHANNEL_STATUS);
			request.addParameter(CMQCFC.MQCACH_CHANNEL_NAME, channelName);
			responses_status = agent.send(request);
			status = (Integer) responses_status[0].getParameterValue(CMQCFC.MQIACH_CHANNEL_STATUS);
		} catch (MQException e) {
			if (e.reasonCode == CMQCFC.MQRCCF_CHL_STATUS_NOT_FOUND) // 3065
			{
				return -1;
			}
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return status.intValue();
	}

	/**
	 * 返回通道参数列表
	 * 
	 * @param chlName
	 *            查询通道名称
	 * @param chlType
	 *            通道类型
	 * @return Vector 通道属性集合
	 */
	public List inquireChannel(String chlName, int chlType, boolean isFullName) {
		List chlAttrs = new ArrayList();
		if (chlName == null || chlName.equals("") || chlName.equalsIgnoreCase("null")) {
			chlName = "*";
		}

		if ((!isFullName) && (!chlName.equals("*"))) {
			chlName = chlName + "*";
		}
		try {
			request = new PCFMessage(CMQCFC.MQCMD_INQUIRE_CHANNEL);
			request.addParameter(CMQCFC.MQCACH_CHANNEL_NAME, chlName);
			request.addParameter(CMQCFC.MQIACH_CHANNEL_TYPE, chlType);
			responses = agent.send(request);
			int responsesLength = 0;
			if (responses != null) {
				responsesLength = responses.length;
				for (int i = 0; i < responsesLength; i++) {
					String name = responses[i].getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME);
					int type = responses[i].getIntParameterValue(CMQCFC.MQIACH_CHANNEL_TYPE);
					String desc = responses[i].getStringParameterValue(CMQCFC.MQCACH_DESC);
					String connName = "";
					String host = "";
					String port = "";
					String xmitQName = "";
					int status = -1;
					if (type == CMQXC.MQCHT_SENDER) {
						connName = responses[i].getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME);
						int start = connName.indexOf("(");
						int end = connName.indexOf(")");
						if (start == -1) {
							host = connName;
						} else {
							host = connName.substring(0, start);
							port = connName.substring((start + 1), end);
						}
						xmitQName = responses[i].getStringParameterValue(CMQCFC.MQCACH_XMIT_Q_NAME);
					}
					if ((!name.startsWith("SYSTEM")) && (!name.startsWith("AMQ")) && (!name.startsWith("MQAI"))) {
						MqChannel cAttr = new MqChannel();
						cAttr.setName(name);
						cAttr.setType(Integer.toString(type));
						cAttr.setDesc(desc);
						cAttr.setConnName(connName);
						cAttr.setHost(host);
						cAttr.setPort(port);
						cAttr.setXmitQName(xmitQName);
						status = getChlStatus(name);
						cAttr.setStatus(Integer.toString(status));
						chlAttrs.add(cAttr);
					}
				}
			} else {
				return null;
			}
		} catch (PCFException pcfe) {
			pcfe.printStackTrace();
			return null;
		} catch (MQException mqe) {
			mqe.printStackTrace();
			return null;
		} catch (Exception ioe) {
			ioe.printStackTrace();
			return null;
		}
		return chlAttrs;
	}

	/**
	 * 返回通道状态参数列表
	 * 
	 * @param chlName
	 *            查询通道名称
	 * @param chlType
	 *            通道类型
	 * @return Vector 通道属性集合
	 */

	public Vector inquireChannelStatus(String chlName, int chlType) {
		String chlN = "";
		if (chlName == null || chlName.equals("") || chlName.equalsIgnoreCase("null")) {
			chlN += chlName + "*";
		} else {
			chlN = "*";
		}
		Vector chlAttrs = new Vector();
		try {
			request = new PCFMessage(CMQCFC.MQCMD_INQUIRE_CHANNEL_STATUS);
			request.addParameter(CMQCFC.MQCACH_CHANNEL_NAME, chlN);
			request.addParameter(CMQCFC.MQIACH_CHANNEL_INSTANCE_TYPE, CMQC.MQOT_CURRENT_CHANNEL);

			responses = agent.send(request);
			for (int i = 0; i < responses.length; i++) {
				String name = "";
				try {
					name = responses[i].getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME);
				} catch (Exception e) {
					e.printStackTrace();
				}
				int type = 0;
				try {
					responses[i].getIntParameterValue(CMQCFC.MQIACH_CHANNEL_TYPE);
				} catch (Exception e) {
					e.printStackTrace();
				}
				int status = 0;
				try {
					status = responses[i].getIntParameterValue(CMQCFC.MQIACH_CHANNEL_STATUS);
				} catch (Exception e) {
					e.printStackTrace();
				}
				String connName = "";
				String xmitQName = "";
				try {
					connName = responses[i].getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					xmitQName = responses[i].getStringParameterValue(CMQCFC.MQCACH_XMIT_Q_NAME);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if ((!name.startsWith("SYSTEM")) && (!name.startsWith("AMQ")) && (!name.startsWith("MQAI"))) {
					Hashtable cAttr = new Hashtable();
					cAttr.put("name", name); // 通道名称
					cAttr.put("type", Integer.toString(type));
					cAttr.put("connName", connName);// 连接名
					cAttr.put("xmitQName", xmitQName);// 传输队列名称
					cAttr.put("status", Integer.toString(status));// 状态
					chlAttrs.add(cAttr);
				}
			}
		} catch (PCFException pcfe) {
			pcfe.printStackTrace();
			return null;
		} catch (MQException mqe) {
			mqe.printStackTrace();
			return null;
		} catch (Exception ioe) {
			ioe.printStackTrace();
			return null;
		}
		return chlAttrs;
	}

	/**
	 * 返回通道名称列表
	 * 
	 * @param chlName
	 *            查询通道名称
	 * @param chlType
	 *            通道类型
	 * @return Vector 通道名称集合
	 */
	public Vector inquireChlNames(String chlName, int chlType) {

		Vector chlNames = new Vector();
		try {
			request = new PCFMessage(CMQCFC.MQCMD_INQUIRE_CHANNEL_NAMES);
			request.addParameter(CMQCFC.MQCACH_CHANNEL_NAME, chlName);
			request.addParameter(CMQCFC.MQIACH_CHANNEL_TYPE, chlType);
			responses = agent.send(request);
			String[] chlNameStrs = (String[]) responses[0].getParameterValue(CMQCFC.MQCACH_CHANNEL_NAME);
			String name;
			if (chlNameStrs != null) {
				for (int i = 0; i < chlNameStrs.length; i++) {
					name = chlNameStrs[i];
					chlNames.add(name);
				}
			} else {
				return null;
			}
		} catch (PCFException pcfe) {
			pcfe.printStackTrace();
			return null;
		} catch (MQException mqe) {
			mqe.printStackTrace();
			return null;
		} catch (Exception ioe) {
			ioe.printStackTrace();
			return null;
		}
		return chlNames;
	}

	public Vector inquireQNames(String queueName, int queueType) {
		Vector qNames = new Vector();
		int qType;
		switch (queueType) {
		case 0:
			qType = CMQC.MQQT_ALL;
			break;
		case 1:
			qType = CMQC.MQQT_LOCAL;
			break;
		case 2:
			qType = CMQC.MQQT_REMOTE;
			break;
		default:
			qType = CMQC.MQQT_ALL;
			break;
		}

		if (queueName != null && !queueName.equals("") && !"null".equalsIgnoreCase(queueName)) {
			queueName += "*";
		} else {
			queueName = "*";
		}
		try {

			request = new PCFMessage(CMQCFC.MQCMD_INQUIRE_Q_NAMES);
			request.addParameter(CMQC.MQCA_Q_NAME, queueName);
			request.addParameter(CMQC.MQIA_Q_TYPE, qType);
			responses = agent.send(request);
			String[] qNameStrs = (String[]) responses[0].getParameterValue(CMQCFC.MQCACF_Q_NAMES);
			String name;

			for (int i = 0; i < qNameStrs.length; i++) {
				name = qNameStrs[i];
				qNames.add(name);
			}
		} catch (PCFException pcfe) {
			pcfe.printStackTrace();
			return null;
		} catch (MQException mqe) {
			mqe.printStackTrace();
			return null;
		} catch (Exception ioe) {
			ioe.printStackTrace();
			return null;
		}
		return qNames;
	}

	/**
	 * 返回队列参数列表
	 * 
	 * @param queueName
	 *            查询队列名称
	 * @param queueType
	 *            队列类型
	 * @return Vector 队列属性集合
	 */
	public List inquireQueue(String queueName, int queueType) {
		String qn = "";
		if (queueName != null && !queueName.equals("") && !queueName.equalsIgnoreCase("null")) {
			qn += queueName + "*";
		} else {
			qn = "*";
		}
		List queueAttrs = new ArrayList();

		try {
			request = new PCFMessage(CMQCFC.MQCMD_INQUIRE_Q);
			request.addParameter(CMQC.MQCA_Q_NAME, qn);
			request.addParameter(CMQC.MQIA_Q_TYPE, queueType);
			request.addParameter(CMQCFC.MQIACF_Q_ATTRS, new int[] { CMQC.MQCA_Q_NAME, CMQC.MQIA_Q_TYPE, CMQC.MQIA_DEF_PERSISTENCE, CMQC.MQIA_USAGE, // 本地队列
					CMQC.MQIA_CURRENT_Q_DEPTH, CMQC.MQCA_REMOTE_Q_NAME, // 远程队列
					CMQC.MQCA_REMOTE_Q_MGR_NAME, CMQC.MQCA_XMIT_Q_NAME });
			responses = agent.send(request);
			for (int i = 0; i < responses.length; i++) {
				String name = responses[i].getStringParameterValue(CMQC.MQCA_Q_NAME);
				int type = responses[i].getIntParameterValue(CMQC.MQIA_Q_TYPE);
				int persistent = responses[i].getIntParameterValue(CMQC.MQIA_DEF_PERSISTENCE); //
				MqQueue qAttr = new MqQueue();
				if (type == CMQC.MQQT_LOCAL) // 1
				{
					int depth = responses[i].getIntParameterValue(CMQC.MQIA_CURRENT_Q_DEPTH);
					int usage = responses[i].getIntParameterValue(CMQC.MQIA_USAGE);
					if ((!name.startsWith("SYSTEM")) && (!name.startsWith("AMQ")) && (!name.startsWith("MQAI"))) {
						qAttr.setQname(name);
						qAttr.setQtype(Integer.toString(type));
						qAttr.setPersistent(Integer.toString(persistent));
						qAttr.setQdepth(Integer.toString(depth));
						qAttr.setUsage(Integer.toString(usage));
						queueAttrs.add(qAttr);
					}
				} else if (type == CMQC.MQQT_REMOTE) {
					String remoteQName = responses[i].getStringParameterValue(CMQC.MQCA_REMOTE_Q_NAME);
					String remoteQM = responses[i].getStringParameterValue(CMQC.MQCA_REMOTE_Q_MGR_NAME);
					String xmitQName = responses[i].getStringParameterValue(CMQC.MQCA_XMIT_Q_NAME);
					if ((!name.startsWith("SYSTEM")) && (!name.startsWith("AMQ")) && (!name.startsWith("MQAI"))) {
						qAttr.setQname(name);
						qAttr.setQtype(Integer.toString(type));
						qAttr.setPersistent(Integer.toString(persistent));
						qAttr.setRemoteQName(remoteQName);
						qAttr.setRemoteQM(remoteQM);
						qAttr.setXmitQName(xmitQName);

						queueAttrs.add(qAttr);
					}
				}
			}
		} catch (PCFException pcfe) {
			pcfe.printStackTrace();
			return null;
		} catch (MQException mqe) {
			mqe.printStackTrace();
			return null;
		} catch (Exception ioe) {
			ioe.printStackTrace();
			return null;
		}
		return queueAttrs;
	}

}
