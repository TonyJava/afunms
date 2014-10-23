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
    Vector<NetAppDisk> diskVector = (Vector<NetAppDisk>) request.getAttribute("disk");
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
																		磁盘列表
																	</td>
																</tr>
															</table>
															<table id="tbEntity" class="tbEntity">
																<thead>
																	<tr>
																		<th>
																			磁盘索引
																		</th>
																		<th>
																			磁盘名称
																		</th>
																		<th>
																			磁盘利用率(%)
																		</th>
																		<th>
																			磁盘类型
																		</th>
																		<th>
																			磁盘挂载点
																		</th>

																		<th>
																			状态
																		</th>
																	</tr>
																</thead>
																<tbody>
																	<%
																	    NetAppDisk vo = null;
																	    Hashtable statusHash = new Hashtable();
																	    statusHash.put("1", "未挂载");
																	    statusHash.put("2", "已挂载");
																	    statusHash.put("3", "冻结");
																	    statusHash.put("4", "正在卸载");
																	    statusHash.put("5", "正在创建");
																	    statusHash.put("6", "正在挂载");
																	    statusHash.put("7", "unmounting");
																	    statusHash.put("8", "nofsinfo");
																	    statusHash.put("9", "replaying");
																	    statusHash.put("10", "replayed");

																	    Hashtable typeHash = new Hashtable();
																	    typeHash.put("1", "traditionalVolume");
																	    typeHash.put("2", "flexibleVolume");
																	    typeHash.put("3", "aggregate");

																	    if (diskVector != null && diskVector.size() > 0) {
																	        String diskState = "";
																	        String diskType = "";
																	        for (int i = 0; i < diskVector.size(); i++) {
																	            vo = (NetAppDisk) diskVector.get(i);
																	            if (vo.getDfType() != null && typeHash.get(vo.getDfType()) != null) {
																	                diskType = (String) typeHash.get(vo.getDfType());
																	            } else if (vo.getDfStatus() != null && statusHash.get(vo.getDfStatus()) != null) {
																	                diskState = (String) statusHash.get(vo.getDfStatus());
																	            }
																	%>
																	<tr class="data">
																		<td>
																			<%=vo.getDfIndex()%></td>
																		<td><%=vo.getDfFileSys()%></td>
																		<td><%=vo.getDfPerCentKBytesCapacity()%></td>
																		<td><%=diskType%></td>
																		<td><%=vo.getDfMountedOn()%></td>
																		<td><%=diskState%></td>
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