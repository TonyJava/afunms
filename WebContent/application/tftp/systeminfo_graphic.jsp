<%@ page language="java" contentType="text/html;charset=gb2312"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>

<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@page import="com.afunms.common.util.*"%>
<%@page import="com.afunms.monitor.item.*"%>
<%@page import="com.afunms.polling.node.*"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="com.afunms.polling.impl.*"%>
<%@page import="com.afunms.polling.api.*"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.topology.dao.*"%>
<%@page import="com.afunms.monitor.item.base.MoidConstants"%>
<%@page import="org.jfree.data.general.DefaultPieDataset"%>
<%@ page import="com.afunms.polling.api.I_Portconfig"%>
<%@ page import="com.afunms.polling.om.Portconfig"%>
<%@ page import="com.afunms.polling.om.*"%>
<%@ page import="com.afunms.polling.impl.PortconfigManager"%>
<%@page import="com.afunms.report.jfree.ChartCreator"%>

<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="java.lang.*"%>
<%@page import="com.afunms.monitor.item.base.*"%>
<%@page import="com.afunms.monitor.executor.base.*"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.config.dao.*"%>

<%@page import="com.afunms.indicators.util.NodeUtil"%>
<%@page import="com.afunms.indicators.model.NodeDTO"%>
<%@page import="com.afunms.detail.net.service.NetService"%>
<%@page import="com.afunms.temp.model.NodeTemp"%>
<%@page import="com.afunms.temp.model.*"%>
<%@ page import="com.afunms.polling.loader.HostLoader"%>

<%@page import="com.afunms.initialize.*"%>
<%
	String rootPath = "";
	String picip = "";
	rootPath = request.getParameter("rootPath") == null ? "" : request.getParameter("rootPath");
//	boolean isOnlyPing = Boolean.parseBoolean(request.getParameter("isOnlyPing"));
	picip = request.getParameter("picip") == null ? "" : request.getParameter("picip");
	String avgresponse = request.getParameter("avgresponse") == null ? "": request.getParameter("avgresponse");
	//内存利用率
//	String pvalue = request.getParameter("pvalue") == null ? "": request.getParameter("pvalue"); 
	//avgresponse="8889"; 
	int reslength = 3;//响应时间显示的位数；
	if (avgresponse.indexOf(".") > 0) {
		avgresponse = avgresponse.substring(0, avgresponse.indexOf("."));//删除小数点后值
	}
	if (avgresponse.length() > reslength) {
		avgresponse = avgresponse.substring(0, reslength);
	}
//	String pingavg = request.getParameter("pingavg");
	//在这里将数据转化为整型 其余地方均可以省略该过程
//	pingavg = pingavg.replace("%", "");
//	pingavg=String.valueOf(Math.round(Float.parseFloat(pingavg)));
	 
	StringBuffer dataStr = new StringBuffer();
//	dataStr.append("连通;").append(pingavg).append(";false;7CFC00\\n");
//	dataStr.append("未连通;").append(100-Integer.parseInt(pingavg)).append(";false;FF0000\\n");
	String pingdata = dataStr.toString();
	String pathPing = ResourceCenter.getInstance().getSysPath()+ "resource\\image\\dashBoardGray.png";
	
//	if(!isOnlyPing){
//		CreateMetersPic cmp = new CreateMetersPic();
//		String path = ResourceCenter.getInstance().getSysPath()+ "resource\\image\\dashBoard1.png";
//		cmp.createPic(picip, new Double(pvalue.replaceAll("%", "")), path,"内存利用率", "avgpmemory");
//		cmp.createChartByParam(picip , pingavg , pathPing,"连通率","pingdata"); 
//	}
//	dataStr.append("连通;").append(34).append(";false;7CFC00\\n");
//	dataStr.append("未连通;").append(66).append(";false;FF0000\\n");
	
%>
<table cellPadding=0 cellspacing="0" align="center" border="1" bordercolor="#D3D3D3">
	<tr>
	  <td width="50%" align="center" valign="top">
			<table  align=center  cellpadding=0 cellspacing="0" width=100%>
				<tr bgcolor=#F1F1F1 height="29">
					<td align="center">今日平均连通率 </td>
				</tr>
				<tr  height=160>
					<td align="center"> <img src="<%=rootPath%>/resource/image/jfreechart/reportimg/<%=picip%>pingdata.png"> </td>
				</tr>
			</table>
		</td>
		<!--  
		<td width="50%" align="center" valign="top">
			<table    align=center  cellpadding=0 cellspacing="0" width=100%>
				<tr bgcolor=#F1F1F1 height="29">
					<td align="center">
						今日平均响应时间
					</td>
				</tr>
				<tr  height=160>
					<td align="center">
						<table style="align: center; width: 100%; background-image: url(<%=rootPath%>/resource/image/chartdirector/responsebg.png); background-repeat: no-repeat; background-position: center;">
							<tr height=160>
								<td align="center">
									<%
										if (avgresponse != null && avgresponse != "") {
											for (int i = 0; i < reslength - avgresponse.length(); i++) {
									%>
									<img src="<%=rootPath%>/resource/image/chartdirector/0.png" border="0">
									<%
										}
											for (int i = 0; i < avgresponse.length(); i++) {
									%>
									<img
										src="<%=rootPath%>/resource/image/chartdirector/<%=avgresponse.charAt(i)%>.png"
										border="0">
									<%
										}
										} else {
									%>
									<%
										for (int i = 0; i < reslength; i++) {
									%>
									<img src="<%=rootPath%>/resource/image/chartdirector/0.png" border="0">
									<%
										}
										}
									%>
									<img src="<%=rootPath%>/resource/image/chartdirector/ms.png" border="0">
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr height="7">
					<td align=center>
						&nbsp;
					</td>
				</tr>
			</table>
		</td>
		 -->
	</tr>
</table>

