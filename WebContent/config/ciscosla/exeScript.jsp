<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.config.model.Huaweitelnetconf"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>

<%@page import="java.util.List"%>

<%
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");
	List list = (List) request.getAttribute("list");
%>
<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">

		<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>

		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script>
		<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css"
			charset="utf-8" />
		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/js/ext/css/common.css" charset="utf-8" />
		<link href="<%=rootPath%>/resource/css/div.css" rel="stylesheet" type="text/css" />
		<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js"
			charset="utf-8"></script>
		<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="utf-8"></script>
		<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js"
			charset="utf-8"></script>
		<script type="text/javascript" src="<%=rootPath%>/js/jquery/jquery-1.4.2.min.js"></script>
       <script type="text/javascript" src="<%=rootPath%>/js/serviceQuality/script.js"></script>

	</head>
	<body id="body" class="body" onload="setClass();">
		<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0
			noResize scrolling=no src="<%=rootPath%>/include/calendar.htm"
			style="DISPLAY: none; HEIGHT: 189px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
		<form id="mainForm" method="post" name="mainForm">
			<div id="loading">
				<div class="loading-indicator">
					<img src="<%=rootPath%>/js/ext/lib/resources/extanim64.gif"
						width="32" height="32" style="margin-right: 8px;" align="middle" />
					Loading...
				</div>
			</div>
			<div id="loading-mask" style=""></div>

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
								<td class="td-container-main-report">
									<table id="container-main-report" class="container-main-report">
										<tr>
											<td>

												<table id="report-content" class="report-content">
													<tr>
														<td>
															<table width="100%"
																background="<%=rootPath%>/common/images/right_t_02.jpg"
																cellspacing="0" cellpadding="0">
																<tr>
																	<td align="left">
																		<div class="noPrint">
																			<img src="<%=rootPath%>/common/images/right_t_01.jpg"
																				width="5" height="29" />
																		</div>
																	</td>
																	<td class="content-title">
																		<b>服务质量 >> 服务级别管理 >> SLA命令配置</b>
																	</td>
																	<td align="right">
																		<div class="noPrint">
																			<img src="<%=rootPath%>/common/images/right_t_03.jpg"
																				width="5" height="29" />
																		</div>
																	</td>
																</tr>
															</table>
														</td>

													</tr>
													<tr>
														<td>
															<table id="report-content-header"
																class="report-content-header">
																<tr>
																	<td>
																		<%=scriptTitleTable%>
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td>
															<table id="report-content-body"
																class="report-content-body">
																<tr>
																	<td align=center>

																		<table id="report-data-header"
																			class="report-data-header">
																			<tr>
																				<td>

																					<table id="report-data-header-title"
																						class="report-data-header-title">
																						<tr>
																							<td>
																								&nbsp; <font color="#DC143C">执行步骤：1.命令输入 >> 2.选择设备 >> 3.执行结果</font>
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
																		<div id="fragment-0" >
																			<table height="350">
																			<tr>
																					<td>
																					&nbsp;
																					</td>
																					<td>
																					&nbsp;	
																					</td>
																				</tr>
																				<tr>
																					<td width="15%">
																						&nbsp;&nbsp;请选择网络设备类型：
																					</td>
																					<td width="85%">
																						<select name="devicetype" id="devicetype">
																						    <option value="all">全部</option>
																						    <option value="h3c">H3C</option>
																							<option value="cisco">Cisco</option>
																							
																						</select>
																					</td>
																				</tr>																				
																				
																				
																<tr>
																    <td ></td>
																	<td align=center width="85%">

																		<table id="report-data-header"
																			class="report-data-header">
																			<tr>
																				<td>

																					<table id="report-data-header-title" class="report-data-header-title">
																						<tr>
																						   
																							<td>
																								
																								&nbsp;<a href="#" onclick="loadFile('<%=rootPath%>')"><img src="<%=rootPath %>/resource/image/menu/load.gif"/>加载文件</a>
																								&nbsp;<a href="#" onclick="saveFile('<%=rootPath%>')"><img src="<%=rootPath %>/resource/image/menu/save.gif"/>保存文件</a>
																							</td>
																						</tr>
																					</table>
																				</td>
																			</tr>
																		</table>

																	</td>
																</tr>
																
																				<tr valign=top>
																					
																					<td width="15%">
																						&nbsp;&nbsp;脚本命令模板：
																					</td>
																					<td width="85%">
																						<textarea id="commands" name="commands" rows="10" cols="85"></textarea>
																						<input type=hidden id="cmdid" name="cmdid">
																						<input type=hidden id="slatype" name="slatype">
																						<input type=hidden id="deviceType1" name="deviceType1">
																					</td>
																				</tr>
																				
							<tr  id='icmp'>	
								<TD align="left" width="100%" colspan=2>
									<table id="report-data-header-title1" class="report-data-header-title"  bgcolor="#ECECEC">
										<tr>
											<TD align="right" height="32" width="20%" ><font color="#DC143C">ICMP类型&nbsp;</font></td>
											<TD  height="32" width="80%" >目标IP:&nbsp;<input type="text" id="icmp_destip" name="icmp_destip" maxlength="50" size="20" class="formStyle"><font color="red">&nbsp;*</font>
												数据包大小:&nbsp;	<input type="text" id="icmp_datapacket"  name="icmp_datapacket" maxlength="50" size="20" class="formStyle" value="400"><font color="red">&nbsp;*</font>
												TOS:&nbsp;	<input type="text" id="icmp_tos"  name="icmp_tos" maxlength="50" size="20" class="formStyle" value="160"><font color="red">&nbsp;*</font>					
											</TD>
										</tr>
									</table>
								</TD>																		
							</tr>
							<tr  id='icmppath'>	
								<TD align="left" width="100%" colspan=2>
									<table id="report-data-header-title1" class="report-data-header-title"  bgcolor="#ECECEC">
										<tr>
											<TD height="32" align="right" width="20%" ><font color="#DC143C">ICMP Path类型&nbsp;</font></td>
											<TD  height="32" width="80%" >目标IP:&nbsp;<input type="text" id="icmppath_destip" name="icmppath_destip" maxlength="50" size="20" class="formStyle"><font color="red">&nbsp;*</font>
												频率:&nbsp;	<input type="text" id="icmppath_rate" name="icmppath_rate" maxlength="50" size="20" class="formStyle"><font color="red">&nbsp;*</font>
												历史存活:&nbsp;  <input type="text" id="icmppath_history" name="icmppath_history" maxlength="50" size="20" class="formStyle"><font color="red">&nbsp;*</font>					
											</TD>
										</tr>
										<tr>
										<TD  height="32" width="20%" align="right">
										<TD  height="32" width="80%">桶历史:&nbsp;<input type="text" id="icmppath_buckets" name="icmppath_buckets" maxlength="50" size="20" class="formStyle"><font color="red">&nbsp;*</font>
												life:&nbsp;	<input type="text" id="icmppath_life" name="icmppath_life" maxlength="50" size="20" class="formStyle"><font color="red">&nbsp;*</font>
										</tr>
									</table>
								</TD>																		
							</tr>
							<tr  id='udp'>	
								<TD align="left" width="100%" colspan=2>
									<table id="report-data-header-title1" class="report-data-header-title"  bgcolor="#ECECEC">
										<tr>
											<TD align="right" height="32" width="20%" ><font color="#DC143C">UDP类型&nbsp;</font></td>
											<TD  height="32" width="80%">目标IP:&nbsp;<input type="text" id="udp_destip" name="udp_destip" maxlength="50" size="20" class="formStyle"><font color="red">&nbsp;*</font>目标端口:&nbsp;	<input type="text" id="udp_destport"  name="udp_destport" maxlength="50" size="20" class="formStyle"><font color="red">&nbsp;*</font>					
											</TD>
										</tr>
									</table>
								</TD>																		
							</tr>
							<tr  id='jitter'>	
								<TD align="left" width="100%" colspan=2>
									<table id="report-data-header-title1" class="report-data-header-title"  bgcolor="#ECECEC">
										<tr>
											<TD align="right" height="32" width="20%" ><font color="#DC143C">JITTER(抖动)类型&nbsp;</font></td>
											<TD  height="32" width="80%">目标IP:&nbsp;<input type="text" id="jitter_destip" name="jitter_destip" maxlength="50" size="20" class="formStyle"><font color="red">&nbsp;*</font>
												目标端口:&nbsp;	<input type="text" id="jitter_destport" name="jitter_destport" maxlength="50" size="20" class="formStyle"><font color="red">&nbsp;*</font>
												数据包数量:&nbsp;	<input type="text" id="jitter_numpacket" name="jitter_numpacket" maxlength="50" size="20" class="formStyle"><font color="red">&nbsp;*</font>					
											</TD>
										</tr>
										<tr>
										<TD  height="32" width="20%">
										<TD  height="32" width="80%">间隔:&nbsp;	<input type="text" id="jitter_interval" name="jitter_interval" maxlength="50" size="20" class="formStyle"><font color="red">&nbsp;*</font>
										</tr>
									</table>
								</TD>																		
							</tr>
							<tr  id='tcpconnectwithresponder'>	
								<TD align="left" width="100%" colspan=2>
									<table id="report-data-header-title1" class="report-data-header-title"  bgcolor="#ECECEC">
										<tr>
											<TD align="right" height="32" width="20%"><font color="#DC143C">TCP Connect(启用Responder)类型&nbsp;</font></td>
											<TD  height="32" width="80%">目标IP:&nbsp;<input type="text" id="tcpconnectwithresponder_destip" name="tcpconnectwithresponder_destip" maxlength="50" size="20" class="formStyle"><font color="red">&nbsp;*</font>
												目标端口:&nbsp;	<input type="text" id="tcpconnectwithresponder_destport" name="tcpconnectwithresponder_destport" maxlength="50" size="20" class="formStyle"><font color="red">&nbsp;*</font>
												TOS:&nbsp;	<input type="text" id="tcpconnectwithresponder_tos" name="tcpconnectwithresponder_tos" maxlength="50" size="20" class="formStyle"><font color="red">&nbsp;*</font>			
											</TD>
										</tr>
									</table>
								</TD>																		
							</tr>
							<tr  id='tcpconnectnoresponder'>	
								<TD align="left" width="100%" colspan=2>
									<table id="report-data-header-title1" class="report-data-header-title"  bgcolor="#ECECEC">
										<tr>
											<TD align="right" height="32" width="20%" ><font color="#DC143C">TCP Connect(未启用Responder)作业&nbsp;</font></td>
											<TD height="32" width="80%" >目标IP:&nbsp;<input type="text" id="tcpconnectnoresponder_destip" name="tcpconnectnoresponder_destip" maxlength="50" size="20" class="formStyle">
												<font color="red">&nbsp;*</font>目标端口:&nbsp;	<input type="text" id="tcpconnectnoresponder_destport" name="tcpconnectnoresponder_destport" maxlength="50" size="20" class="formStyle"><font color="red">&nbsp;*</font>					
											</TD>
										</tr>
									</table>
								</TD>																		
							</tr>							
							<tr  id='http'>	
								<TD align="left" width="100%" colspan=2>
									<table id="report-data-header-title1" class="report-data-header-title"  bgcolor="#ECECEC">
										<tr>
											<TD align="right" height="32" width="20%"><font color="#DC143C">HTTP类型&nbsp;</font></td>
											<TD  height="32" width="80%">URL连接:&nbsp;<input type="text" id="http_urlconnect" name="http_urlconnect" maxlength="50" size="20" class="formStyle"><font color="red">&nbsp;*</font>	
											</TD>
										</tr>
									</table>
								</TD>																		
							</tr>
							<tr  id='dns'>	
								<TD align="left" width="100%" colspan=2>
									<table id="report-data-header-title1" class="report-data-header-title"  bgcolor="#ECECEC">
										<tr>
											<TD align="right" height="32" width="20%"><font color="#DC143C">DNS类型&nbsp;</font></td>
											<TD  height="32" width="80%">目标IP:&nbsp;<input type="text" id="dns_destip" name="dns_destip" maxlength="50" size="20" class="formStyle"><font color="red">&nbsp;*</font>
											域名服务器:&nbsp;	<input type="text" id="dns_dnsserver"  name="dns_dnsserver" maxlength="50" size="20" class="formStyle"><font color="red">&nbsp;*</font>	
											</TD>
										</tr>
									</table>
								</TD>																		
							</tr>																				
                           <tr  id='h3cIcmp'>	
								<TD align="left" width="100%" colspan=2>
									<table id="report-data-header-title1" class="report-data-header-title"  bgcolor="#ECECEC">
										<tr>
											<TD align="right" height="32" width="20%" ><font color="#DC143C">ICMP类型&nbsp;&nbsp;</font></td>
											<TD  height="32" width="80%" >
											      管理名：&nbsp;<input type="text" id="h3c_icmp_admin" name="h3c_icmp_admin" maxlength="50" size="20" class="formStyle" value=""><font color="red">&nbsp;*</font>
												  操作标识：&nbsp;	<input type="text" id="h3c_icmp_tag"  name="h3c_icmp_tag" maxlength="50" size="20" class="formStyle" value=""><font color="red">&nbsp;*</font>
												 目标IP:&nbsp;	<input type="text" id="h3c_icmp_destip" name="h3c_icmp_destip" maxlength="20" size="20" class="formStyle"><font color="red">&nbsp;*</font>				
											</TD>
										</tr>
									</table>
								</TD>																		
							</tr>
							 <tr  id='h3cHttp'>	
								<TD align="left" width="100%" colspan=2>
									<table id="report-data-header-title1" class="report-data-header-title"  bgcolor="#ECECEC">
										<tr>
											<TD align="right" height="32" width="20%" ><font color="#DC143C">http类型&nbsp;&nbsp;</font></td>
											<TD  height="32" width="80%" >
											       管理名：&nbsp; <input type="text" id="h3c_http_admin" name="h3c_http_admin" maxlength="50" size="20" class="formStyle" value=""><font color="red">&nbsp;*</font>&nbsp;
												 &nbsp; 操作标识：&nbsp;	<input type="text" id="h3c_http_tag"  name="h3c_http_tag" maxlength="50" size="20" class="formStyle" value=""><font color="red">&nbsp;*</font><br>
												  目标IP：&nbsp;	<input type="text" id="h3c_http_destip" name="h3c_http_destip" maxlength="20" size="20" class="formStyle"><font color="red">&nbsp;*</font>
												 &nbsp;&nbsp;&nbsp; URL：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;	<input type="text" id="h3c_http_url" name="h3c_http_url" maxlength="20" size="20" class="formStyle"><font color="red">&nbsp;*</font>				
											</TD>
										</tr>
									</table>
								</TD>																		
							</tr>
							<tr  id='h3cNqa'>	
								<TD align="left" width="100%" colspan=2>
									<table id="report-data-header-title1" class="report-data-header-title"  bgcolor="#ECECEC">
										<tr>
											<TD align="right" height="32" width="20%" ><font color="#DC143C" id="sqatype">类型&nbsp;&nbsp;</font></td>
											<TD  height="32" width="80%" >
											     管理名：&nbsp;   <input type="text" id="h3c_admin" name="h3c_admin" maxlength="50" size="20" class="formStyle" value=""><font color="red">&nbsp;*</font>
												&nbsp;操作标识：&nbsp;	<input type="text" id="h3c_tag"  name="h3c_tag" maxlength="50" size="20" class="formStyle" value=""><font color="red">&nbsp;*</font><br>
												目标IP：&nbsp;	<input type="text" id="h3c_destip" name="h3c_destip" maxlength="20" size="20" class="formStyle"><font color="red">&nbsp;*</font>				
											   &nbsp;目标端口：&nbsp;	<input type="text" id="h3c_destport" name="h3c_destport" maxlength="50" size="20" class="formStyle"><font color="red">&nbsp;*</font>
											</TD>
										</tr>
									</table>
								</TD>																		
							</tr>
							
							
																				<tr>

																					<td colspan=2 align=center>
																						<br>
																						<input type="button" value="下一步"
																							onclick="secondFra('<%=rootPath %>')">
																					</td>
																				</tr>
																				<tr>

																					<td colspan=2 align=center>
																						<br>
																						&nbsp;
																						<br><br><br><br>
																					</td>
																				</tr>
																			</table>

																		</div>
																	</td>
																</tr>
																<tr>
																	<td>

																		<div id="fragment-1">
																			<table cellspacing="0" border="1"
																				bordercolor="#ababab">
																				<tr height=28 style="background: #ECECEC"
																					align="center" class="content-title">
																					<td align="center">
																						<INPUT type="checkbox" name="checkall"
																							onclick="javascript:chkall()">

																					</td>
																					<td align="center">
																						序号
																					</td>
																					<td align="center">
																						IP地址
																					</td>

																					<td align="center">
																						设备类型
																					</td>

																				</tr>
																				<%
																					Huaweitelnetconf vo = null;
																					for (int i = 0; i < list.size(); i++) {
																						vo = (Huaweitelnetconf) list.get(i);
																				%>
																				<tr <%=onmouseoverstyle%>>

																					<td align='center'>
																						<input type="checkbox" name='checkbox'
																							id='checkbox' value="<%=vo.getIpaddress()%>" />
																					</td>
																					<td align='center'>
																						<%=i + 1%>
																					</td>
																					<td align='center'>
																						<%=vo.getIpaddress()%>
																					</td>

																					<td align='center'><%=vo.getDeviceRender()%></td>

																				</tr>
																				<%
																					}
																				%>
																				<tr style="background-color: #ECECEC;">
																					<TD nowrap colspan="7" align=center>
																						<br>
																						<input type="button" style="width: 50" value="上一步"
																							onclick="firstFra()">

																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																						<input type="button" value="执 行" style="width: 50"
																							onclick="exeCmd('<%=rootPath %>')">


																					</TD>

																				</tr>


																			</table>

																		</div>

																		<div id="fragment-2">
																			<table>
																				<tr>
																				    <td align=center>
																				       
																				        <table id="result">
																							<tr height=28>

																								<td class="report-data-body-title">
																									IP
																								</td>
																								<td class="report-data-body-title">
																									运行时间
																								</td>
																								<td class="report-data-body-title">
																									执行命令
																								</td>
																								<td class="report-data-body-title">
																									执行结果
																								</td>

																							</tr>
																				       </table>
																				    </td>
																				</tr>
																				<tr>
																					<td  align=center>

																						<input type="button" value="上一步"
																							onclick="secondFra2()">
																					</td>
																				</tr>
																			</table>

																		</div>

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
           	<div id="BgDiv"></div>
			<div id="DialogDiv" style="display:none">
				<h2>SLA命令配置</h2>
				<div class="form" id="result">
				  正在提交，请稍后...
				</div>
				<div id="bottomDiv">
	            </div>
	         </div>
		</form>
	</BODY>
</HTML>