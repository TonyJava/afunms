/*
 * @(#)JBossPingIndicatorGather.java     v1.01, Feb 25, 2013
 *
 * Copyright (c) 2011, TNT All Rights Reserved.
 */

package com.afunms.polling.snmp.jboss;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import com.afunms.application.dao.JBossConfigDao;
import com.afunms.application.jbossmonitor.HttpClientJBoss;
import com.afunms.application.model.JBossConfig;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.api.IndicatorGather;
import com.afunms.polling.impl.ProcessJBossData;
import com.afunms.polling.node.Result;

@SuppressWarnings("unchecked")
public class JBossPerformanceIndicatorGather extends SnmpMonitor implements IndicatorGather {

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
		Hashtable<String, String> dataHashtable = (Hashtable<String, String>) result.getResult();
		Hashtable<String, Hashtable<String, String>> ipdata = new Hashtable<String, Hashtable<String, String>>();
		ipdata.put("jboss:" + node.getId(), dataHashtable);
		try {
			// 将采集到的JBOSS信息入库
			List<JBossConfig> list = new ArrayList<JBossConfig>();
			list.add(node);
			ProcessJBossData processJBossData = new ProcessJBossData();
			processJBossData.saveJBossData(list, ipdata);
		} catch (Exception e) {
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
		Hashtable<String, String> dataHashtbale = new Hashtable<String, String>();
		if (src != null && src.contains("Version")) {
			int status = 0;
			String str = src.substring(src.indexOf("SVNTag"));
			String version = str.substring(7, str.indexOf("date"));// JBoss_4_2_2_GA
			String date = str.substring(str.indexOf("date") + 5, str.indexOf(")"));// 200710221140

			String str1 = str.substring(str.indexOf("Version Name:"));
			String versionname = str1.substring(str1.indexOf("</b>") + 4, str1.indexOf("</font>"));// Trinity

			String str2 = str1.substring(str1.indexOf("Built on:"));
			String builton = str2.substring(str2.indexOf("</b>") + 4, str2.indexOf("</font>"));

			String str3 = str2.substring(str2.indexOf("Start date:"));
			String startdate = str3.substring(str3.indexOf("</b>") + 4, str3.indexOf("</font>"));

			String str4 = str3.substring(str3.indexOf("Host:"));
			String host = str4.substring(str4.indexOf("</b>") + 4, str4.indexOf("</font>"));

			String str5 = str4.substring(str4.indexOf("Base Location:"));
			String baselocation = str5.substring(str5.indexOf("file:") + 6, str5.indexOf("</font>"));// F:/jboss-4.2.2.GA/jboss-4.2.2.GA/server/

			String str6 = str5.substring(str5.indexOf("Base Location (local):"));
			String baselocationlocal = str6.substring(str6.indexOf("</b>") + 4, str6.indexOf("</font>"));// F:\jboss-4.2.2.GA\jboss-4.2.2.GA\server

			String str7 = str6.substring(str6.indexOf("Running config:"));
			String runconfig = str7.substring(str7.indexOf("</b>") + 4, str7.indexOf("</font>"));// 'default'

			String str8 = str7.substring(str7.indexOf("CPU:"));
			String cpu = str8.substring(str8.indexOf("</b>") + 4, str8.indexOf("</font>"));// 2

			String str9 = str8.substring(str8.indexOf("OS:"));
			String os = str9.substring(str9.indexOf("</b>") + 4, str9.indexOf("</font>"));// Windows
			// XP
			// 5.1
			// (x86)

			String str10 = str9.substring(str9.indexOf("Free Memory:"));
			String freememory = str10.substring(str10.indexOf("</b>") + 4, str10.indexOf(" MB"));// 117
			// MB
			String str11 = str10.substring(str10.indexOf("Max Memory:"));
			String maxmemory = str11.substring(str11.indexOf("</b>") + 4, str11.indexOf("</font>"));// 493
			// MB

			String str12 = str11.substring(str11.indexOf("Total Memory:"));
			String totalmemory = str12.substring(str12.indexOf("</b>") + 4, str12.indexOf(" MB"));// 165
			// MB
			String str13 = str12.substring(str12.indexOf("Threads:"));
			String threads = str13.substring(str13.indexOf("</b>") + 4, str13.indexOf("</font>"));// 50

			String str14 = str13.substring(str13.indexOf("JVM Version:"));
			String jvmversion = str14.substring(str14.indexOf("</b>") + 4, str14.indexOf("</font>"));// 1.5.0_06-b05
			// (Sun
			// Microsystems
			// Inc.)

			String str15 = str14.substring(str14.indexOf("JVM Name:"));
			String jvmname = str15.substring(str15.indexOf("</b>") + 4, str15.indexOf("</font>"));// Java
			// HotSpot(TM)
			// Server
			// VM
			dataHashtbale.put("version", version);
			dataHashtbale.put("date", date);
			dataHashtbale.put("versionname", versionname);
			dataHashtbale.put("builton", builton);
			dataHashtbale.put("startdate", startdate);
			dataHashtbale.put("host", host);
			dataHashtbale.put("baselocation", baselocation);
			dataHashtbale.put("baselocationlocal", baselocationlocal);
			dataHashtbale.put("runconfig", runconfig);
			dataHashtbale.put("cpu", cpu);
			dataHashtbale.put("os", os);
			dataHashtbale.put("freememory", freememory);
			dataHashtbale.put("maxmemory", maxmemory);
			dataHashtbale.put("totalmemory", totalmemory);
			dataHashtbale.put("threads", threads);
			dataHashtbale.put("jvmversion", jvmversion);
			dataHashtbale.put("jvmname", jvmname);
			dataHashtbale.put("status", String.valueOf(status));
		}
		String str = jboss.getGetResponseWithHttpClient("http://" + jbossConfig.getIpaddress() + ":" + jbossConfig.getPort() + "/web-console/status", "GBK");
		if (str.contains("ajp")) {
			String ajp_total = str.substring(str.indexOf("ajp"));
			String ajp1 = ajp_total.substring(ajp_total.indexOf("ajp"), ajp_total.indexOf("</p>"));

			String ajp = ajp1.substring(0, ajp1.indexOf("</h1>")); // ajp-127.0.0.1-8009
			String ajp_maxthreads = ajp1.substring(ajp1.indexOf("Max threads:") + 12, ajp1.indexOf("Current thread count:"));

			String ajp_thrcount = ajp1.substring(ajp1.indexOf("Current thread count:") + 21, ajp1.indexOf("Current thread busy:"));

			String ajp_thrbusy = ajp1.substring(ajp1.indexOf("Current thread busy:") + 20, ajp1.indexOf("Max processing time:") - 5);
			String ajp_maxtime = ajp1.substring(ajp1.indexOf("Max processing time:") + 20, ajp1.indexOf("Processing time:"));
			String ajp_processtime = ajp1.substring(ajp1.indexOf("Processing time:") + 16, ajp1.indexOf("Request count:"));
			String ajp_requestcount = ajp1.substring(ajp1.indexOf("Request count:") + 14, ajp1.indexOf("Error count:"));
			String ajp_errorcount = ajp1.substring(ajp1.indexOf("Error count:") + 12, ajp1.indexOf("Bytes received:"));
			String ajp_bytereceived = ajp1.substring(ajp1.indexOf("Bytes received:") + 15, ajp1.indexOf("Bytes sent:"));
			String ajp_bytessent = ajp1.substring(ajp1.indexOf("Bytes sent:") + 11);
			String http_total1 = str.substring(str.lastIndexOf("JVM"));
			String http_total = http_total1.substring(http_total1.indexOf("http"));
			String http = http_total.substring(0, http_total.indexOf("</h1>"));
			String http_maxthreads = ajp1.substring(http_total.indexOf("Max threads:") + 12, ajp1.indexOf("Current thread count:"));
			String http_thrcount = http_total.substring(http_total.indexOf("Current thread count:") + 21, http_total.indexOf("Current thread busy:"));
			String http_thrbusy = http_total.substring(http_total.indexOf("Current thread busy:") + 20, http_total.indexOf("Max processing time:") - 5);
			String http_maxtime = http_total.substring(http_total.indexOf("Max processing time:") + 20, http_total.indexOf("Processing time:"));
			String http_processtime = http_total.substring(http_total.indexOf("Processing time:") + 16, http_total.indexOf("Request count:"));
			String http_requestcount = http_total.substring(http_total.indexOf("Request count:") + 14, http_total.indexOf("Error count:"));
			String http_errorcount = http_total.substring(http_total.indexOf("Error count:") + 12, http_total.indexOf("Bytes received:"));
			String http_bytereceived = http_total.substring(http_total.indexOf("Bytes received:") + 15, http_total.indexOf("Bytes sent:"));
			String http_bytessent = http_total.substring(http_total.indexOf("Bytes sent:") + 11, http_total.indexOf("</p>"));

			dataHashtbale.put("ajp_maxthreads", ajp_maxthreads);
			dataHashtbale.put("ajp_thrcount", ajp_thrcount);
			dataHashtbale.put("ajp_thrbusy", ajp_thrbusy);
			dataHashtbale.put("ajp_maxtime", ajp_maxtime);
			dataHashtbale.put("ajp_processtime", ajp_processtime);
			dataHashtbale.put("ajp_requestcount", ajp_requestcount);
			dataHashtbale.put("ajp_errorcount", ajp_errorcount);
			dataHashtbale.put("ajp_bytereceived", ajp_bytereceived);
			dataHashtbale.put("ajp_bytessent", ajp_bytessent);
			dataHashtbale.put("ajp", ajp);

			dataHashtbale.put("http_maxthreads", http_maxthreads);
			dataHashtbale.put("http_thrcount", http_thrcount);
			dataHashtbale.put("http_thrbusy", http_thrbusy);
			dataHashtbale.put("http_maxtime", http_maxtime);
			dataHashtbale.put("http_processtime", http_processtime);
			dataHashtbale.put("http_requestcount", http_requestcount);
			dataHashtbale.put("http_errorcount", http_errorcount);
			dataHashtbale.put("http_bytereceived", http_bytereceived);
			dataHashtbale.put("http_bytessent", http_bytessent);
			dataHashtbale.put("http", http);
		}
		Result result = new Result();
		result.setCollectTime(Calendar.getInstance().getTime());
		result.setErrorCode(1);
		result.setErrorInfo("");
		result.setResult(dataHashtbale);
		ShareData.setJbossdata(jbossConfig.getId() + "", dataHashtbale);
		return result;
	}
}
