<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.*"%>

<%
	String rootPath = request.getContextPath();
	String commands = (String) request.getAttribute("commands");
	String fileName = (String) request.getAttribute("fileName");
	//String fileName = filePath.substring(filePath.lastIndexOf("\\")+1);
%>

<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css"
			rel="stylesheet" type="text/css" />
		<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
        <script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
        <script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/js/jquery/jquery-1.4.2.min.js"></script>
		<link rel="stylesheet" href="<%=rootPath%>/resource/css/discover.css" type="text/css">
		<script language="JavaScript" type="text/javascript">

Ext.onReady(function(){

 Ext.get("backup").on("click",function(){
	  var name=$("#name").val();
	 
	   if(name==""){
	   alert("名称不允许为空！");
	   return;
	   }
     	Ext.MessageBox.wait('数据加载中，请稍后.. ');
        mainForm.action = "<%=rootPath%>/slacmd.do?action=saveCmdCfg";
        mainForm.submit();
    
 });
});
   
   
	function selectDeviceType(){
	
	var slatype=$("#slatype").val();
	if(slatype=="icmp"){
	$("#fileDesc").val("ICMP Echo 作业");
	}else if(slatype=="icmppath"){
	$("#fileDesc").val("ICMP Path Echo 作业");
	}else if(slatype=="tcpconnect-withresponder"){
	$("#fileDesc").val("TCPConnect(启动Responder)");
	}else if(slatype=="tcpconnect-noresponder"){
	$("#fileDesc").val("TCPConnect(未启动Responder)");
	}else if(slatype=="udp"){
	$("#fileDesc").val("UDP Eche 作业");
	}else if(slatype=="dns"){
	$("#fileDesc").val("DNS");
	}else if(slatype=="http"){
	$("#fileDesc").val("HTTP");
	}else if(slatype=="jitter"){
	$("#fileDesc").val("Jitter");
	}else{
	$("#fileDesc").val("");
	}
	
	}

</script>


	</head>
	<body id="body">



		<form name="mainForm" method="post">
			<input type=hidden name="commands" value="<%=commands%>">
			<table id="body-container" class="body-container">
				<tr style="background-color: #FFFFFF;">
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-add">
									<table id="container-main-add" class="container-main-add">
										<tr>
											<td>
												<table id="add-content" class="add-content">
													<tr>
														<td>
															<table id="add-content-header" class="add-content-header">
																<tr>
																	<td align="left" width="5">
																		<img src="<%=rootPath%>/common/images/right_t_01.jpg"
																			width="5" height="29" />
																	</td>
																	<td class="content-title">
																		&nbsp;保存命令文件
																	</td>
																	<td align="right">
																		<img src="<%=rootPath%>/common/images/right_t_03.jpg"
																			width="5" height="29" />
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td>
															<table width="100%" border=0 cellpadding=0 cellspacing=1>

																<tr>
																	<td>
																		<table id="detail-content-body"
																			class="detail-content-body">
																			<tr>
																				<td>

																					<table border="0" id="table1" cellpadding="0"
																						cellspacing="1" width="100%">
																						<tr>
																							<td colspan="4" width='100%' align="center"
																								bgcolor="#E6E6FA" height="27"></td>
																						</tr>
																						<tr>
																							<TD nowrap align="left" height="24" width="10%">
																								&nbsp;&nbsp;<font color="red">*&nbsp;</font>名   称：&nbsp;
																							</TD>
																							<TD nowrap width="40%">
																								&nbsp;
																								<input type="text" name="name" id="name" maxlength="50"
																									size="30" class="formStyle" value="">
																							</TD>
																							<TD nowrap align="left" height="24" width="10%">
																								&nbsp;&nbsp;描   述：&nbsp;
																							</TD>
																							<TD nowrap width="40%">
																								&nbsp;
																								<input type="text" name="fileDesc" id="fileDesc"
																									maxlength="60" size="40" class="formStyle"
																									value="">
																							</TD>
																							
																						</tr>
																						<tr>
																							<td colspan="4" width='100%' align="center"
																								bgcolor="#E6E6FA" height="27"></td>
																						</tr>
																						<tr >

																							<TD nowrap align="left" height="24" width="10%">
																								&nbsp;&nbsp;&nbsp;&nbsp;SLA类型：&nbsp;
																							</TD>
																							<TD nowrap width="40%">
																								&nbsp;
																								<select id="slatype" name="slatype">
																									<option value="icmp">
																										ICMP Echo 作业
																									</option>
																									<option value="icmppath">
																										ICMP Path Echo 作业
																									</option>
																									<option value="tcpconnectwithresponder">
																										TCPConnect(启动Responder)
																									</option>
																									<option value="tcpconnect-noresponder">
																										TCPConnect(未启动Responder)
																									</option>
																									<option value="udp">
																										UDP Eche 作业
																									</option>
																									<option value="dns">
																										DNS
																									</option>
																									<option value="http">
																										HTTP
																									</option>
																									<option value="jitter">
																										Jitter
																									</option>
																									
																								</select>
																							</TD>
																							<TD nowrap align="left" height="24" width="10%">
																								&nbsp;&nbsp;设备类型：&nbsp;
																							</TD>

																							<TD nowrap width="40%">
																								&nbsp;
																								<select id="devicetype" name="devicetype">
																									<option value="cisco">
																										Cisco
																									</option>
																									<option value="h3c">
																										H3C
																									</option>
																								</select>
																							</TD>
																						</tr>
																						
																						<tr>
																							<td colspan="4" width='100%' align="center"
																								bgcolor="#E6E6FA" height="27"></td>
																						</tr>
																						<tr>
																							<TD nowrap colspan="4" align=center>
																								<br>
																								<br>

																								<input type="button" id="backup"
																									style="width: 50" value="确  定" style="width:50"
																									class=btn_mouseout
																									onmouseover="this.className='btn_mouseover'"
																									onmouseout="this.className='btn_mouseout'"
																									onmousedown="this.className='btn_mousedown'"
																									onmouseup="this.className='btn_mouseup'">
																								&nbsp;&nbsp;
																								<input type="button" style="width: 50"
																									value="关 闭" onclick="window.close()"
																									style="width:50" class=btn_mouseout
																									onmouseover="this.className='btn_mouseover'"
																									onmouseout="this.className='btn_mouseout'"
																									onmousedown="this.className='btn_mousedown'"
																									onmouseup="this.className='btn_mouseup'">
																								&nbsp;&nbsp;
																							</TD>
																						</tr>

																					</TABLE>


																				</td>
																			</tr>
																		</table>
																	</td>
																</tr>
																<tr>
																	<td>
																		<table id="detail-content-footer"
																			class="detail-content-footer">
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
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</form>
	</BODY>


</HTML>