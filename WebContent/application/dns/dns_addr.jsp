<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.polling.PollingEngine"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.afunms.application.model.DnsConfig"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>
<%
	String rootPath = request.getContextPath();
	DnsConfig vo = (DnsConfig) request.getAttribute("vo");
	int id = 0;
	
	String connrate = request.getAttribute("connrate").toString();
 	
        if(connrate!=null)
	connrate = connrate.replaceAll("%", "");
     String serverName="";//服务名称
     String hostIp="";//服务IP
     String dnsIp="";//DNS ip
     if(vo!=null){
    	 id=vo.getId();
    	 serverName=vo.getDns(); 
    	 hostIp=vo.getHostip();
    	 dnsIp=vo.getDnsip();
     }
	String menuTable = (String) request.getAttribute("menuTable");
	
	float avgPing = Float.parseFloat(connrate);
	StringBuffer dataStr = new StringBuffer();
	dataStr.append("连通;").append(Math.round(avgPing)).append(";false;7CFC00\\n");
	dataStr.append("未连通;").append(100 - Math.round(avgPing)).append(";false;FF0000\\n");
	
	 String addr = (String)request.getAttribute("addr")==null?"":(String)request.getAttribute("addr");
%>
<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
		<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
		<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/css/common.css" charset="gb2312" />
		 <link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css"
			rel="stylesheet" type="text/css" />

		<script type="text/javascript">
function setClass(){
	document.getElementById('dnsDetailTitle-1').className='detail-data-title';
	document.getElementById('dnsDetailTitle-1').onmouseover="this.className='detail-data-title'";
	document.getElementById('dnsDetailTitle-1').onmouseout="this.className='detail-data-title'";
		}
		
function refer(action){
		document.getElementById("id").value="<%=id%>";
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}
</script>


	</head>
	<body id="body" class="body" onload="setClass();">
		<form id="mainForm" method="post" name="mainForm">
			<input type=hidden name="id">

			<table id="body-container" class="body-container">
				<tr>
					<td class="td-container-menu-bar">
						<table id="container-menu-bar" class="container-menu-bar">
							<tr>
								<td><%=menuTable%></td>
							</tr>
						</table>
					</td>
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-service-detail">
									<table id="container-main-service-detail"
										class="container-main-service-detail">
										<tr>
											<td>
												<jsp:include page="/topology/includejsp/middleware_dns.jsp">
													<jsp:param name="id" value="<%=id%>" />
                                                    <jsp:param name="name" value="<%=serverName%>" />
                                                     <jsp:param name="ipaddress" value="<%=hostIp%>" />
												</jsp:include>
											</td>
										</tr>
										<tr>
											<td>
												<table id="service-detail-content"
													class="service-detail-content">
													<tr>
														<td>
															<%=dnsDetailTitleTable%>
														</td>
													</tr>

													<tr>
														<td>
															<table id="service-detail-content-body"
																class="service-detail-content-body">
																<tr>
																	<td>
																		<table style="BORDER-COLLAPSE: collapse" rules=none
																			align=center border=1 cellpadding=0 cellspacing="0"
																			width=100%>
																			<tr>
																				<td width="80%" align="left" valign="top">
																					<table>
																						<tr>
																							<td  height="29" align="left" nowrap>
																							&nbsp;&nbsp;<%=addr %>
																							</td>
																							
																						</tr>
																						

																						


																					</table>
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
															<table id="application-detail-content-footer"
																class="application-detail-content-footer">
																<tr>
																	<td>
																		<table width="100%" border="0" cellspacing="0"
																			cellpadding="0">
																			<tr>
																				<td align="left" valign="bottom">
																					<img
																						src="<%=rootPath%>/common/images/right_b_01.jpg"
																						width="5" height="12" />
																				</td>
																				<td></td>
																				<td align="right" valign="bottom">
																					<img
																						src="<%=rootPath%>/common/images/right_b_03.jpg"
																						width="5" height="12" />
																				</td>
																			</tr>
																		</table>
																	</td>
																</tr>
															</table>
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>

							</tr>
						</table>
					</td>
					<td class="td-container-main-tool" width="14%">
						<jsp:include page="/include/midtoolbar.jsp">
							<jsp:param value="<%=hostIp%>" name="ipaddress" />
							<jsp:param value="<%=id%>" name="id" />
							<jsp:param value="<%=flag%>" name="flag" />
							<jsp:param value="middleware" name="type" />
							<jsp:param value="dns" name="subtype" />
						</jsp:include>
					</td>
				</tr>
			</table>

		</form>

	</BODY>
</HTML>