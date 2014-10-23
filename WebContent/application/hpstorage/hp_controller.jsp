<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.polling.node.ArrayInfo"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="com.afunms.polling.impl.*"%>
<%@page import="com.afunms.polling.api.*"%>
<%@page import="com.afunms.polling.node.SshController"%>
<%
	String runmodel = PollingEngine.getCollectwebflag();//采集与访问模式
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");

	List<SshController> list = (List<SshController>) request.getAttribute("controllers");
	

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
			href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css"
			charset="utf-8" />
		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/js/ext/css/common.css" charset="utf-8" />

		<script language="javascript">	
var show = true;
var hide = false;
//修改菜单的上下箭头符号
function my_on(head,body)
{
	var tag_a;
	for(var i=0;i<head.childNodes.length;i++)
	{
		if (head.childNodes[i].nodeName=="A")
		{
			tag_a=head.childNodes[i];
			break;
		}
	}
	tag_a.className="on";
}
function my_off(head,body)
{
	var tag_a;
	for(var i=0;i<head.childNodes.length;i++)
	{
		if (head.childNodes[i].nodeName=="A")
		{
			tag_a=head.childNodes[i];
			break;
		}
	}
	tag_a.className="off";
}
//添加菜单	
function initmenu()
{
	var idpattern=new RegExp("^menu");
	var menupattern=new RegExp("child$");
	var tds = document.getElementsByTagName("div");
	for(var i=0,j=tds.length;i<j;i++){
		var td = tds[i];
		if(idpattern.test(td.id)&&!menupattern.test(td.id)){					
			menu =new Menu(td.id,td.id+"child",'dtu','100',show,my_on,my_off);
			menu.init();		
		}
	}
	setClass();
}
function setClass(){
	document.getElementById('hpStorageDetailTitle-3').className='detail-data-title';
	document.getElementById('hpStorageDetailTitle-3').onmouseover="this.className='detail-data-title'";
	document.getElementById('hpStorageDetailTitle-3').onmouseout="this.className='detail-data-title'";
}
function refer(action){
		document.getElementById("id").value="<%=host.getId()%>";
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}
</script>
	</head>
	<body id="body" class="body" onload="initmenu();">
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
																					<% if(list!=null){
																					for(int i=0;i<list.size();i++){
																						SshController controller=list.get(i);
																					%>
																					    <tr bgcolor="#00FFFF">
																							<td width="15%" height="22" align="center" colspan=2  nowrap class=txtGlobal>
																								&nbsp;控 制 器 <%=i+1 %>:
																							</td>
																							
																						</tr>
																						<tr bgcolor="#ECECEC">
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;ID:
																							</td>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;<%=controller.getId() %>
																							</td>
																						</tr>
																						<tr>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;序列号:
																							</td>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;<%=controller.getSerialNum() %>
																							</td>
																						</tr>
																						<tr bgcolor="#ECECEC">
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;硬件版本号:
																							</td>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;<%=controller.getHardwareVersion() %>
																							</td>
																						</tr>
																						<tr>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;CPLD 版本号:
																							</td>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;<%=controller.getCpldVersion() %>
																							</td>
																						</tr>
																						<tr bgcolor="#ECECEC">
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;Mac:
																							</td>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;<%=controller.getMac() %>
																							</td>
																						</tr>
																						<tr>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;WWNN:
																							</td>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;<%=controller.getWwnn() %>
																							</td>
																						</tr>
																						<tr bgcolor="#ECECEC">
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;IP:
																							</td>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;<%=controller.getIp() %>
																							</td>
																						</tr>
																						<tr>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;子网掩码:
																							</td>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;<%=controller.getMask() %>
																							</td>
																						</tr>
																						<tr bgcolor="#ECECEC">
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;网关:
																							</td>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;<%=controller.getGateway() %>
																							</td>
																						</tr>
																						<tr>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;硬盘数:
																							</td>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;<%=controller.getDisks() %>
																							</td>
																						</tr>
																						<tr bgcolor="#ECECEC">
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;vdisks:
																							</td>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;<%=controller.getVdisks() %>
																							</td>
																						</tr>
																						<tr>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;缓存:
																							</td>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;<%=controller.getCache() %>MB
																							</td>
																						</tr>
																						<tr bgcolor="#ECECEC"> 
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;主机端口数:
																							</td>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;<%=controller.getHostPorts() %>
																							</td>
																						</tr>
																						<tr>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;磁盘通道数:
																							</td>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;<%=controller.getDiskChannels() %>
																							</td>
																						</tr>
																						<tr bgcolor="#ECECEC">
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;磁盘总线类型:
																							</td>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;<%=controller.getDiskBusType() %>
																							</td>
																						</tr>
																						<tr>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;状态:
																							</td>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;<%=controller.getStatus() %>
																							</td>
																						</tr>
																						
																						<tr bgcolor="#ECECEC">
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;失败:
																							</td>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;<%=controller.getFailedOver() %>
																							</td>
																						</tr>
																						<tr>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;失败原因:
																							</td>
																							<td width="15%" height="22" align="left" nowrap class=txtGlobal>
																								&nbsp;<%=controller.getFailOverReason() %>
																							</td>
																						</tr>
																						
                                                                                     <%}} %>
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