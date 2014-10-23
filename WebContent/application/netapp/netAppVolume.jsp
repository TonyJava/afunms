<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@ include file="/include/globeChinese.inc"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.polling.om.*"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.indicators.util.NodeUtil"%>
<%@page import="com.afunms.indicators.model.NodeDTO"%>
<%
    String menuTable = (String) request.getAttribute("menuTable");//菜单
    String tmp = request.getParameter("id");
    int alarmLevel = (Integer) request.getAttribute("alarmLevel");
    Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
    NodeUtil nodeUtil = new NodeUtil();
    NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);
    Vector<NetAppVolume> volumeVector = (Vector<NetAppVolume>) request.getAttribute("volume");
    String rootPath = request.getContextPath();
%>
<html>
	<head>
		<link
			href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css"
			rel="stylesheet" type="text/css" />

		<link rel="stylesheet"
			href="<%=rootPath%>/common/css/tableNewStyle.css" type="text/css"></link>

		<script type="text/javascript">
				function refer(action){
					document.getElementById("id").value="<%=tmp%>";
					var mainForm = document.getElementById("mainForm");
					mainForm.action = '<%=rootPath%>' + action;
					mainForm.submit();
				}
			</script>
	</head>
	<body id="body" class="body">
		<form id="mainForm" method="post" name="mainForm">
			<input type=hidden name="id">
			<table id="body-container" class="body-container">
				<tr>
					<td class="td-container-menu-bar">
						<table id="container-menu-bar" class="container-menu-bar">
							<tr>
								<td>
									<%=menuTable%>
								</td>
							</tr>
						</table>
					</td>
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-detail" width=98%>
									<table id="container-main-detail" class="container-main-detail">
										<tr>
											<td>
												<table id="detail-content" class="detail-content"
													width="100%" cellspacing="0" cellpadding="0">
													<tr class="bar">
														<td colspan=4 align=center>
															<b>设备详细信息</b>
														</td>
													</tr>
													<tr class="bar">
														<td width="20%" style="padding-left: 30px;">
															设备标签:<%=host.getAlias()%>
														</td>
														<td width="20%">
															系统名称:<%=host.getSysName()%>
														</td>
														<td width=15%>
															<table>
																<tr>
																	<td width="10px;">
																		<img
																			src="<%=rootPath%>/resource/<%=NodeHelper.getCurrentStatusImage(alarmLevel)%>">
																	</td>
																	<td>
																		<span><%=NodeHelper.getStatusDescr(alarmLevel)%></span>
																	</td>
																</tr>
															</table>
														</td>
														<td width="20%">
															IP地址:<%=host.getIpAddress()%>
														</td>
													</tr>
												</table>
											</td>
										</tr>

										<tr>
											<td>
												<table id="detail-data" class="detail-data">
													<tr>
														<td class="detail-data-header">
															<%=netAppDetailTitleTable%>
														</td>
													</tr>
													<tr>
														<td>
															<!-- 内容区域开始 -->
															<table class="tbEntity">
																<tr>
																	<td class="title">
																		卷(Volume)信息
																	</td>
																</tr>
															</table>

															<table id="tbEntity" class="tbEntity">
																<thead>
																	<tr>
																		<th>
																			volIndex
																		</th>
																		<th>
																			volName
																		</th>
																		<th>
																			volFSID
																		</th>
																		<th>
																			volOwningHost
																		</th>
																		<th>
																			volState
																		</th>
																		<th>
																			volStatus
																		</th>
																		<th>
																			volUUID
																		</th>
																		<th>
																			volAggrName
																		</th>
																		<th>
																			volType
																		</th>
																	</tr>
																</thead>
																<tbody>
																	<%
																	    NetAppVolume volume = null;
																	    Hashtable hostHash = new Hashtable();
																	    hostHash.put("1", "local");
																	    hostHash.put("2", "partner");

																	    Hashtable typeHash = new Hashtable();
																	    typeHash.put("1", "traditional");
																	    typeHash.put("2", "flexible");
																	    if (volumeVector != null && volumeVector.size() > 0) {
																	        String hostType = "";
																	        String volumeType = "";
																	        for (int i = 0; i < volumeVector.size(); i++) {
																	            volume = (NetAppVolume) volumeVector.elementAt(i);
																	            if (volume.getVolOwningHost() != null && hostHash.get(volume.getVolOwningHost()) != null) {
																	                hostType = (String) hostHash.get(volume.getVolOwningHost());
																	            } else if (volume.getVolType() != null && typeHash.get(volume.getVolType()) != null) {
																	                volumeType = (String) typeHash.get(volume.getVolType());
																	            }
																	%>
																	<tr class="data">
																		<td><%=volume.getVolIndex()%></td>
																		<td><%=volume.getVolName()%></td>
																		<td><%=volume.getVolFSID()%></td>
																		<td><%=hostType%></td>
																		<td><%=volume.getVolState()%></td>
																		<td><%=volume.getVolStatus()%></td>
																		<td><%=volume.getVolUUID()%></td>
																		<td><%=volume.getVolAggrName()%></td>
																		<td><%=volumeType%></td>
																	</tr>
																	<%
																	    }
																	    }
																	%>
																</tbody>
															</table>
															<!-- 内容区域end -->
														</td>
													</tr>
												</table>

											</td>
										</tr>
									</table>
						</table>
					</td>
					<td class="td-container-main-tool" width='15%'>
						<jsp:include page="/include/netapptoolbar.jsp">
							<jsp:param value="<%=host.getIpAddress()%>" name="ipaddress" />
							<jsp:param value="<%=host.getSysOid()%>" name="sys_oid" />
							<jsp:param value="<%=tmp%>" name="tmp" />
							<jsp:param value="network" name="category" />
							<jsp:param value="<%=nodedto.getSubtype()%>" name="subtype" />
						</jsp:include>
					</td>
				</tr>
			</table>
		</form>
	</BODY>
</HTML>
