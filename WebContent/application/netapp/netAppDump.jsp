<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@ include file="/include/globeChinese.inc"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@ page import="com.afunms.polling.om.*"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.indicators.util.NodeUtil"%>
<%@page import="com.afunms.indicators.model.NodeDTO"%>
<%
	String menuTable = (String) request.getAttribute("menuTable");//菜单
	String tmp = request.getParameter("id");
	int alarmLevel = (Integer) request.getAttribute("alarmLevel");
	Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));

	Vector<NetAppDump> dump = (Vector<NetAppDump>) request.getAttribute("dump");
	Vector<NetAppRestore> restore = (Vector<NetAppRestore>) request.getAttribute("restore");

	NodeUtil nodeUtil = new NodeUtil();
	NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);

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
	<body id="body" class="body" onload="initmenu();">
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

										<!-- 中间区域开始 -->
										<tr>
											<td>
												<table id="detail-data" class="detail-data">
													<!-- 导航器开始 -->
													<tr>
														<td class="detail-data-header">
															<%=netAppDetailTitleTable%>
														</td>
													</tr>
													<!-- 导航器end -->

													<!-- 内容模块开始 -->
													<tr>
														<td>
															<table class="tbEntity">
																<tr>
																	<td class="title">
																		数据导出列表
																	</td>
																</tr>
															</table>

															<table class="tbEntity">
																<thead>
																	<tr>
																		<th>
																			序号
																		</th>
																		<th>
																			活动
																		</th>
																		<th>
																			尝试导出
																		</th>
																		<th>
																			成功导出
																		</th>
																		<th>
																			导出失败
																		</th>
																	</tr>
																</thead>
																<tbody>
																	<%
																		NetAppDump vo = null;
																		if (dump != null && dump.size() > 0) {
																			for (int i = 0; i < dump.size(); i++) {
																				vo = (NetAppDump) dump.get(i);
																	%>
																	<tr class="data">
																		<td align="center"><%=i + 1%></td>
																		<td align="center"><%=vo.getDmpActives()%></td>
																		<td align="center"><%=vo.getDmpAttempts()%></td>
																		<td align="center"><%=vo.getDmpSuccesses()%></td>
																		<td align="center"><%=vo.getDmpFailures()%></td>
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
														<td>
															<table class="tbEntity">
																<tr>
																	<td class="title">
																		数据导入列表
																	</td>
																</tr>
															</table>

															<table class="tbEntity">
																<thead>
																	<tr>
																		<th>
																			序号
																		</th>
																		<th>
																			活动
																		</th>
																		<th>
																			尝试导入
																		</th>
																		<th>
																			成功导入
																		</th>
																		<th>
																			导出失败
																		</th>
																	</tr>
																</thead>
																<tbody>

																	<%
																		NetAppRestore vo1 = null;
																		if (restore != null && restore.size() > 0) {
																			for (int i = 0; i < dump.size(); i++) {
																				vo1 = (NetAppRestore) restore.get(i);
																	%>
																	<tr class="data">
																		<td align="center"><%=i + 1%></td>
																		<td align="center"><%=vo1.getRstActives()%></td>
																		<td align="center"><%=vo1.getRstAttempts()%></td>
																		<td align="center"><%=vo1.getRstSuccesses()%></td>
																		<td align="center"><%=vo1.getRstFailures()%></td>
																	</tr>
																	<%
																		}
																		}
																	%>
																</tbody>
															</table>
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<!-- 中间区域end -->
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