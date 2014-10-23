<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@ include file="/include/globeChinese.inc"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.inform.util.SystemSnap"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.config.dao.*"%>
<%@page import="com.afunms.indicators.util.NodeUtil"%>

<%
	String rootPath = request.getParameter("rootPath");
	String tmp = request.getParameter("tmp");
	String sysdescr = request.getParameter("sysDescr");
	String sysuptime = request.getParameter("sysUpTime");
	String collecttime = request.getParameter("collectTime");
	String cpuAvg = request.getParameter("cpuAvg");
	String picip = request.getParameter("picip");
	String pingAvg = request.getParameter("pingAvg");
	String productType = request.getParameter("productType");
	String productVersion = request.getParameter("productVersion");
	String productId = request.getParameter("productId");
	String productVendor = request.getParameter("productVendor");
	String productModel = request.getParameter("productModel");
	String productFirmwareVersion = request.getParameter("productFirmwareVersion");
	String productSerialNum = request.getParameter("productSerialNum");
	String productMachineType = request.getParameter("productMachineType");

	Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));

	//该设备采集的数据，是否只有ping数据标志
	boolean isOnlyPing = NodeUtil.isOnlyCollectPing(host);
	int alarmlevel = SystemSnap.getNodeStatus(host);
	
	String flag_1 = (String)request.getAttribute("flag");
%>

<link
	href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css"
	rel="stylesheet" type="text/css" />
	<style>
<!--
.sysInfoTD{
text-align:right;
padding-right:15px;
}

table.sysInfo tr{
background-color:expression((this.sectionRowIndex%2==0)?"#FFFFFF":"#F8F8FF");
}
-->
</style>

<script type="text/javascript">
<!--
function sychronizeData(nodeID){
	 mainForm.action = "<%=rootPath%>/netapp.do?action=sychronizeData&id="+nodeID+"&flag=<%=flag_1%>";
	 mainForm.submit();
}
//-->
</script>

<table id="detail-content" class="detail-content">

	<tr>
		<td>
			<table id="detail-content-body" class="detail-content-body">
				<tr>
					<td>
						<table cellpadding=0 width=100% align=center>
							<tr>
								<td width="60%" align="left" valign="top">
									<table cellpadding="0" cellspacing="0" class="sysInfo">
										<tr>
											<td height="26px" class="sysInfoTD">
												 状态:
											</td>
											<td
												background="<%=rootPath%>/resource/<%=NodeHelper.getCurrentStatusImage(alarmlevel)%>"
												class="performance-status">
												<%=NodeHelper.getStatusDescr(alarmlevel)%>
											</td>
										</tr>
										<tr>
											<td width="30%" height="26" class="sysInfoTD" nowrap>
												 IP地址:
											</td>
											<%
												IpAliasDao ipdao = new IpAliasDao();
												List iplist = ipdao.loadByIpaddress(host.getIpAddress());
												ipdao.close();
												ipdao = new IpAliasDao();
												IpAlias ipalias = ipdao.getByIpAndUsedFlag(host.getIpAddress(), "1");
												ipdao.close();
												if (iplist == null)
													iplist = new ArrayList();
											%>
											<TD nowrap width="40%">
												<select name="ipalias<%=host.getIpAddress()%>">
													<option selected>
														<%=host.getIpAddress()%>
													</option>
													<%
														for (int j = 0; j < iplist.size(); j++) {
															IpAlias voTemp = (IpAlias) iplist.get(j);
													%>
													<option
														<%if(ipalias != null && ipalias.getAliasip().equals(voTemp.getAliasip())){ %>
														selected <%} %>><%=voTemp.getAliasip()%></option>
													<%
														}
													%>
												</select>
												[
												<a href="#" style="cursor: hand"
													onclick="modifyIpAliasajax('<%=host.getIpAddress()%>');">
													修改 </a> ]
											</TD>
										</tr>
										<tr>
											<td width="30%" height="26" class="sysInfoTD">
												 类别:
											</td>
											<td width="70%">
												<%=NodeHelper.getNodeCategory(host.getCategory())%>
											</td>
										</tr>
										<tr>
											<td width="30%" height="26" class="sysInfoTD">
												 类型:
											</td>
											<td width="70%"><%=host.getType()%></td>
										</tr>
										<tr>
											<td width="30%" height="26" class="sysInfoTD" nowrap>
												 系统描述:
											</td>
											<td width="70%">
												<%
													String sysdescrforshow = "";//用于显示设备信息简称
													if (sysdescr != "" && sysdescr != null) {
														if (sysdescr.length() > 40) {
															sysdescrforshow = sysdescr.substring(0, 40) + "...";
														} else {
															sysdescrforshow = sysdescr;
														}
													}
												%>
												<acronym title="<%=sysdescr%>"><%=sysdescrforshow%></acronym>
											</td>
										</tr>
										<tr>
											<td height="26" class="sysInfoTD">
												 设备启动时间:
											</td>
											<td><%=sysuptime%></td>
										</tr>
										<tr>
											<td width="30%" height="26" align=left nowrap class="sysInfoTD">
												 数据采集时间:
											</td>
											<td width="70%">
												<%=collecttime%>
											</td>
										</tr>
										<tr>
											<td width="30%" height="26" class="sysInfoTD" nowrap
												class=txtGlobal>
												 设备资产编号:
											</td>
											<td width="70%">
												<span id="assetid"><%=host.getAssetid()%></span>
											</td>
										</tr>
										<tr>
											<td width="30%" height="26" align="left" nowrap
												class="sysInfoTD">
												 机房位置:
											</td>
											<td width="70%">
												<span id="location"><%=host.getLocation()%></span>
											</td>
										</tr>
										<tr >
											<td height="26" class="sysInfoTD" nowrap>
												 设备供应商:
											</td>
											<td>
											<%
												Hashtable vendorHash=new Hashtable();
												vendorHash.put("1","netapp");
												vendorHash.put("2","dell");
											
											 %>
											<%=vendorHash.get(productVendor) %>
											</td>
										</tr>
										<tr>
											<td height="26" class="sysInfoTD" nowrap>
												 产品类型:
											</td>
											<td>
											<%
												Hashtable typeHash=new Hashtable();
												typeHash.put("1","eisaBased");
												typeHash.put("2","pciBased");
											 %>
												<%=typeHash.get(productType)%>
											</td>
										</tr>
										
										<tr>
											<td height="26" class="sysInfoTD" nowrap>
												 产品版本:
											</td>
											<td>
												<%=productVersion%>
											</td>
										</tr>
										
										<tr>
											<td height="26" class="sysInfoTD" nowrap>
												 产品ID:
											</td>
											<td>
												<%=productId%>
											</td>
										</tr>
										
										<tr>
											<td height="26" class="sysInfoTD" nowrap>
												产品模块:
											</td>
											<td>
												<%=productModel%>
											</td>
										</tr>
										
										<tr>
											<td height="26" class="sysInfoTD" nowrap>
												 软件版本:
											</td>
											<td>
												<%=productFirmwareVersion%>
											</td>
										</tr>
										
										<tr>
											<td height="26" class="sysInfoTD" nowrap>
												 产品序列号:
											</td>
											<td>
												<%=productSerialNum%>
											</td>
										</tr>
										
										<tr>
											<td height="26" class="sysInfoTD" nowrap>
												 机器类型:
											</td>
											<td>
												<%=productMachineType%>
											</td>
										</tr>
										
									</table>
								</td>
								<td width="20%" align="right" valign="top">
									<jsp:include
										page="/application/netapp/systeminfo_graphicNetApp.jsp">
										<jsp:param name="rootPath" value="<%=rootPath%>" />
										<jsp:param name="picip" value="<%=picip%>" />
										<jsp:param name="pingAvg" value="<%=pingAvg%>" />
										<jsp:param name="cpuAvg" value="<%=cpuAvg%>" />
										<jsp:param name="isOnlyPing" value="<%=isOnlyPing%>" />
									</jsp:include>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<jsp:include page="/topology/includejsp/detail_content_footer.jsp" />
		</td>
	</tr>

</table>
