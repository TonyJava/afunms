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
    Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
    NodeUtil nodeUtil = new NodeUtil();
    NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);
    Vector vfilerVector = (Vector) request.getAttribute("vfiler");
    Vector vfilerIpVector = (Vector) request.getAttribute("vfilerIp");
    Vector vfilerPathVector = (Vector) request.getAttribute("vfilerPath");
    Vector vfilerProtocolVector = (Vector) request.getAttribute("vfilerProtocol");
    int alarmLevel = (Integer) request.getAttribute("alarmLevel");
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
					<!-- 左侧菜单 -->
					<td class="td-container-menu-bar">
						<table id="container-menu-bar" class="container-menu-bar">
							<tr>
								<td>
									<%=menuTable%>
								</td>
							</tr>
						</table>
					</td>
					<!-- 左侧菜单end -->

					<!-- 中间主体区域开始 -->
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-detail" width=98%>
									<table id="container-main-detail" class="container-main-detail">
										<!-- 顶部区域块 -->
										<tr>
											<td>
												<table id="detail-content" class="detail-content"
													width="100%" cellspacing="0" cellpadding="0">
													<!-- 顶部标题 -->
													<tr class="bar">
														<td colspan=4 align=center>
															<b>设备详细信息</b>
														</td>
													</tr>
													<!-- 顶部标题end -->
													<!-- 设备概要信息 -->
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
													<!-- 设备概要信息end -->
												</table>
											</td>
										</tr>
										<!-- 顶部区域块end -->

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
															<table class="tbEntity">
																<tr>
																	<td class="title">
																		VFiler 配置概要信息
																	</td>
																</tr>
															</table>
															<table class="tbEntity">
																<thead>
																	<tr>
																		<th>
																			vIndex
																		</th>
																		<th>
																			vfName
																		</th>
																		<th>
																			vfUuid
																		</th>
																		<th>
																			Ip总数
																		</th>
																		<th>
																			路径总数
																		</th>
																		<th>
																			vfIpSpace
																		</th>
																		<th>
																			运行协议数
																		</th>
																		<th>
																			禁止协议数
																		</th>
																		<th>
																			状态
																		</th>
																	</tr>
																</thead>
																<tbody>
																	<%
																	    NetAppVFiler tempVFiler = null;
																	    Hashtable vStateHash = new Hashtable();
																	    vStateHash.put("1", "stopped");
																	    vStateHash.put("2", "running");
																	    if (vfilerVector != null && vfilerVector.size() > 0) {
																	        String vFilerState = "";
																	        for (int i = 0; i < vfilerVector.size(); i++) {
																	            tempVFiler = (NetAppVFiler) vfilerVector.elementAt(i);
																	            if (tempVFiler.getVfState() != null && vStateHash.get(tempVFiler.getVfState()) != null) {
																	                vFilerState = (String) vStateHash.get(tempVFiler.getVfState());
																	            }
																	%>
																	<tr class="data">
																		<td><%=tempVFiler.getVfIndex()%></td>
																		<td><%=tempVFiler.getVfName()%></td>
																		<td><%=tempVFiler.getVfUuid()%></td>
																		<td><%=tempVFiler.getVfIpAddresses()%></td>
																		<td><%=tempVFiler.getVfStoragePaths()%></td>
																		<td><%=tempVFiler.getVfIpSpace()%></td>
																		<td><%=tempVFiler.getVfAllowedProtocols()%></td>
																		<td><%=tempVFiler.getVfDisallowedProtocols()%></td>
																		<td><%=vFilerState%></td>
																	</tr>
																	<%
																	    }
																	    }
																	%>
																</tbody>
															</table>
														</td>
													</tr>

													<tr>
														<td style="background-color: #EEEEE0; height: 30px;"></td>
													</tr>

													<tr>
														<td>
															<table class="tbEntity">
																<tr>
																	<td class="title">
																		VFiler 配置详细信息
																	</td>
																</tr>
															</table>
															<table class="tbEntity">
																<thead>
																	<tr>
																		<th>
																			vfIndex
																		</th>
																		<th>
																			vfIpAddresses
																		</th>
																		<th>
																			vfStoragePaths
																		</th>
																		<th>
																			vfAllowedProtocols
																		</th>
																	</tr>
																</thead>
																<%
																    NetAppVFilerIpEntity ipOm = null;
																    NetAppVFilerPathEntity pathOm = null;
																    NetAppVFilerProtocolEntity protocolOm = null;

																    String vfIndex = "";
																    String ipIndex = "";
																    String ipString = "";
																    for (int i = 0; i < vfilerIpVector.size(); i++) {
																        ipOm = (NetAppVFilerIpEntity) vfilerIpVector.elementAt(i);
																        vfIndex = ipOm.getVfFiIndex();
																        if (vfIndex == null) {
																        System.out.println("1111111111111111111");
																            continue;
																        }
																        ipIndex = ipOm.getVfIpIndex();
																%>
																<tbody>
																	<tr style="text-align: center;">
																		<td><%=vfIndex%></td>
																		<td>
																			<table>
																				<%
																				    for (int j = 0; j < vfilerIpVector.size(); j++) {
																				            ipOm = (NetAppVFilerIpEntity) vfilerIpVector.elementAt(j);
																				            if (ipOm.getVfFiIndex().equals(vfIndex)) {
																				                ipIndex = ipOm.getVfIpIndex();
																				                ipString = ipOm.getIpaddress();
																				%>
																				<tr>
																					<td><%=ipIndex%></td>
																					<td><%=ipString%></td>
																				</tr>
																				<%
																				    }
																				        }
																				%>
																			</table>
																		</td>
																		<td>
																			<table>
																				<%
																				    for (int k = 0; k < vfilerPathVector.size(); k++) {
																				            pathOm = (NetAppVFilerPathEntity) vfilerPathVector.elementAt(k);
																				            if (pathOm.getVfFsIndex().equals(vfIndex)) {
																				%>
																				<tr>
																					<td><%=pathOm.getVfSpIndex()%></td>
																					<td><%=pathOm.getVfSpName()%></td>
																				</tr>
																				<%
																				    }
																				        }
																				%>
																			</table>
																		</td>
																		<td>
																			<table style="margin-top: 5px; margin-bottom: 5px;">
																				<%
																				    Hashtable stateHash = new Hashtable();
																				        stateHash.put("1", "false");
																				        stateHash.put("2", "true");

																				        for (int m = 0; m < vfilerProtocolVector.size(); m++) {
																				            protocolOm = (NetAppVFilerProtocolEntity) vfilerProtocolVector.elementAt(m);
																				            if (protocolOm.getVfFpIndex().equals(vfIndex)) {
																				%>
																				<tr>
																					<td><%=protocolOm.getVfProIndex()%></td>
																					<td><%=protocolOm.getVfProName()%></td>
																					<td><%=stateHash.get(protocolOm.getVfProStatus())%></td>
																				</tr>
																				<%
																				    }
																				        }
																				%>
																			</table>
																		</td>

																	</tr>
																	<%
																	    }
																	%>
																</tbody>
															</table>
														</td>
													</tr>
													<!-- 内容区域end -->
												</table>

											</td>
										</tr>
										<!-- 内容模块end -->
									</table>
								</td>
							</tr>
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
