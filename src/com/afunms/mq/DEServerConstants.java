package com.afunms.mq;

import com.ibm.mq.pcf.CMQCFC;
import com.ibm.mq.pcf.CMQXC;

public class DEServerConstants {

	/**
	 * MQ 管理及连接相关常量
	 */
	public static final String MQ_SVRCONN = "RONEDE.SVRCONN";
	public static final String MQ_TRANSPORT = "MQSeries";
	public static final int MQ_DEFAULT_PORT = 1414;
	public static final int MQ_ENCODING = 273;
	public static final int MQ_CHARATERSET = 1381;
	public static final int[] Chaneltypes = { 0, CMQXC.MQCHT_SENDER, CMQXC.MQCHT_SERVER, CMQXC.MQCHT_RECEIVER, CMQXC.MQCHT_REQUESTER, CMQXC.MQCHT_ALL, CMQXC.MQCHT_CLNTCONN,
			CMQXC.MQCHT_SVRCONN, CMQXC.MQCHT_CLUSRCVR, CMQXC.MQCHT_CLUSSDR };
	public static final String[] ChaneltypeStr = { "", "发送器", "服务器", "接收器", "请求", "所有", "客户连接", "服务器连接", "群集接收器", "群集发送器" };
	public static final int[] Chanelstatus = { CMQCFC.MQCHS_INACTIVE, CMQCFC.MQCHS_BINDING, CMQCFC.MQCHS_STARTING, CMQCFC.MQCHS_RUNNING, CMQCFC.MQCHS_STOPPING,
			CMQCFC.MQCHS_RETRYING, CMQCFC.MQCHS_STOPPED, CMQCFC.MQCHS_REQUESTING, CMQCFC.MQCHS_PAUSED, CMQCFC.MQCHS_INITIALIZING };
	public static final String[] ChanelstatusStr = { "INACTIVE", "BINDING", "STARTING", "RUNNING", "STOPPING", "RETRYING", "STOPPED", "REQUESTING", "PAUSED", "INITIALIZING" };

}