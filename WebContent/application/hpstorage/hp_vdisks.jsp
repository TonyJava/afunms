<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.polling.node.SshVdisk"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="com.afunms.polling.impl.*"%>
<%@page import="com.afunms.polling.api.*"%>

<%
	String runmodel = PollingEngine.getCollectwebflag();//采集与访问模式
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");

	List<SshVdisk> disks = (List<SshVdisk>) request.getAttribute("vdisk");
	if (disks == null) {
		disks = new ArrayList();
	}

	Host host = (Host) request.getAttribute("hpstorage");
	if (host == null) {
		host = new Host();
	}
%>
<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script type="text/javascript"
			src="<%=rootPath%>/include/swfobject.js"></script>
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<link
			href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css"
			rel="stylesheet" type="text/css" />
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/js/ext/css/common.css" charset="utf-8" />

		<script type="text/javascript"
			src="<%=rootPath%>/resource/xml/flush/amcolumn/swfobject.js"></script>
		<script language="javascript">	
function setClass(){
	document.getElementById('hpStorageDetailTitle-1').className='detail-data-title';
	document.getElementById('hpStorageDetailTitle-1').onmouseover="this.className='detail-data-title'";
	document.getElementById('hpStorageDetailTitle-1').onmouseout="this.className='detail-data-title'";
}
function refer(action){
		document.getElementById("id").value="<%=host.getId()%>";
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}

</script>
	</head>
	<body id="body" class="body" onload="setClass();">
		<form id="mainForm" method="post" name="mainForm">
			<input type=hidden name="orderflag">
			<input type=hidden name="id">
			<table id="body-container" class="body-container">
				<tr>
					

					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-detail" width=98%>
									<table id="container-main-detail" class="container-main-detail">
										<tr>
											<td>
												 <jsp:include page="/topology/includejsp/sysinfo_hpstorage_top.jsp">
												            <jsp:param name="rootPath" value="<%= rootPath %>"/>
												            <jsp:param name="tmp" value="<%=host.getId()%>"/>
										                 </jsp:include>
											</td>
										</tr>
										<tr>
											<td>
												<table id="detail-data" class="detail-data">
													<tr>
														<td class="detail-data-header">
															<%=hpStorageSshDetailTitleTable%>
														</td>
													</tr>
													<tr>

														<td>
															<table class="detail-data-body">
																<tr>
																	<td>
																		<table width="100%" border="0" cellpadding="0"
																			cellspacing="0">
																			<tr>
																				<td>
																					<table>
																						<tr bgcolor="#ECECEC">
																							<td width="10%" height="26" align="center">
																								名称
																							</td>
																							<td width="10%" height="26" align="center">
																								大小
																							</td>
																							<td width="8%" height="26" align="center">
																								空闲
																							</td>
																							<td width="8%" height="26" align="center">
																								Own
																							</td>
																							<td width="8%" height="26" align="center">
																								Pref
																							</td>
																							<td width="10%" height="26" align="center">
																								RAID
																							</td>
																							
																							<td width="8%" height="26" align="center">
																								磁盘数
																							</td>
																							<td width="10%" height="26" align="center">
																								Spr
																							</td>
																							<td width="10%" height="26" align="center">
																								Chk
																							</td>
																							<td width="10%" height="26" align="center">
																								状态
																							</td>
																							<td width="15%" height="26" align="center">
																								序列号
																							</td>
																						</tr>
																						<%
																							if (disks != null && disks.size() > 0) {
																								for (int i = 0; i < disks.size(); i++) {
																									SshVdisk disk = disks.get(i);
																						%>

																						<tr>
																							<td  class="detail-data-body-list" height="26" align="center"><%=SysUtil.ifNull(disk.getName())%></td>
																							<td class="detail-data-body-list" height="26" align="center"><%=SysUtil.ifNull(disk.getSize())%></td>
																							<td class="detail-data-body-list" height="26" align="center"><%=SysUtil.ifNull(disk.getFree())%></td>
																							<td class="detail-data-body-list" height="26" align="center"><%=SysUtil.ifNull(disk.getOwn())%></td>
																							<td class="detail-data-body-list" height="26" align="center"><%=SysUtil.ifNull(disk.getPref())%></td>
																							<td class="detail-data-body-list" height="26" align="center"><%=SysUtil.ifNull(disk.getRaid())%></td>
																							<td class="detail-data-body-list" height="26" align="center"><%=SysUtil.ifNull(disk.getDisks())%></td>
																							<td class="detail-data-body-list" height="26" align="center"><%=SysUtil.ifNull(disk.getSpr())%></td>
																							<td class="detail-data-body-list" height="26" align="center"><%=SysUtil.ifNull(disk.getChk())%></td>
																							<td class="detail-data-body-list" height="26" align="center"><%=SysUtil.ifNull(disk.getStatusJobs())%></td>
																							<td class="detail-data-body-list" height="26" align="center"><%=SysUtil.ifNull(disk.getSerialNumber())%></td>
																						</tr>

																						<%
																							}
																							}
																						%>
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
						</table>

					</td>
					<td class="td-container-main-tool" width="15%">
						<jsp:include page="/application/hpstorage/hpstoragetoolbar.jsp">
							<jsp:param value="<%=host.getIpAddress()%>" name="ipaddress" />
							<jsp:param value="<%=host.getSysOid()%>" name="sys_oid" />
							<jsp:param value="<%=host.getId()%>" name="tmp" />
							<jsp:param value="storage" name="category" />
							<jsp:param value="hp" name="subtype" />
						</jsp:include>
					</td>
				</tr>
			</table>
		</form>
	</body>
</html>