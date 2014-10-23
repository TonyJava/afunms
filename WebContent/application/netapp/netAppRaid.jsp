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
    Vector<NetAppRaid> raidgroup = (Vector<NetAppRaid>) request.getAttribute("raid");
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
	<body id="body" class="body"">
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

										<!-- 内容模块 -->
										<tr>
											<td>
												<table id="detail-data" class="detail-data">
													<!-- 导航器 -->
													<tr>
														<td class="detail-data-header">
															<%=netAppDetailTitleTable%>
														</td>
													</tr>
													<!-- 导航器end -->

													<!-- 内容区域开始 -->
													<tr>
														<td>
															<table class="tbEntity">
																<tr>
																	<td class="title">
																		Raid列表
																	</td>
																</tr>
															</table>
															<table id="tbEntity" class="tbEntity">
																<thead>
																	<tr>
																		<th>
																			RAID组
																		</th>
																		<th>
																			序号
																		</th>
																		<th>
																			磁盘名称
																		</th>
																		<th>
																			总大小(Mb)
																		</th>
																		<th>
																			磁盘类型
																		</th>

																		<th>
																			状态
																		</th>
																	</tr>
																</thead>
																<tbody>
																	<%
																	    NetAppRaid vo = null;
																	    Hashtable statusHash = new Hashtable();
																	    statusHash.put("1", "活动");
																	    statusHash.put("2", "正在重建");
																	    statusHash.put("3", "部分重建");
																	    statusHash.put("4", "部分验证");
																	    statusHash.put("5", "正在清除");
																	    statusHash.put("6", "失败");
																	    if (raidgroup != null && raidgroup.size() > 0) {
																	        String raidStatus = "";
																	        for (int i = 0; i < raidgroup.size(); i++) {
																	            vo = (NetAppRaid) raidgroup.get(i);
																	            if (vo.getRaidVStatus() != null && statusHash.get(vo.getRaidVStatus()) != null) {
																	                raidStatus = (String) statusHash.get(vo.getRaidVStatus());
																	            }
																	%>
																	<tr class="data">
																		<td>
																			RAID组-<%=vo.getRaidVGroup()%></td>
																		<td><%=vo.getRaidVIndex()%></td>
																		<td><%=vo.getRaidVDiskName()%></td>
																		<td><%=vo.getRaidVTotalMb()%></td>
																		<td><%=vo.getRaidVDiskType()%></td>
																		<td><%=raidStatus%></td>
																	</tr>
																	<%
																	    }
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