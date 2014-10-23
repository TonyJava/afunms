<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.config.model.Huaweitelnetconf"%>
<%@page import="com.afunms.config.model.TimingBackupTelnetConfig"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.*" %>

<%
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");
%>
<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/include/swfobject.js"></script>
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>

        <script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />

		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css"
			charset="gb2312" />
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js"
			charset="gb2312"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js"
			charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/application/resource/js/timeShareConfigdiv.js"
			charset="gb2312"></script>

		<script language="JavaScript" type="text/javascript">
 
 Ext.onReady(function()
{
 Ext.get("process").on("click",function(){
    var chk1 = checkinput("ipaddress","string","网络设备",50,false);
    var chk2 = checkinput("destip","string","目标设备",50,false);
    var chk3 = checkinput("name","string","名称",50,false);
     
    var deviceType=$("#devicetype").val();
    if(deviceType=="h3c"){
      var adminsign=$("#adminsign").val();
      var operatesign=$("#operatesign").val();
      if(adminsign==""){
       alert("请输入管理名！");
       return;
      }
      if(operatesign==""){
       alert("请输入操作标识！");
       return;
      }
    }else if(deviceType=="cisco"){
    var adminsign=$("#entrynumber").val();
    if(adminsign==""){
       alert("请输入入口号！");
       return;
      }
    }
    
    if(chk1 && chk2&&chk3)
    {
    	Ext.MessageBox.wait('数据加载中，请稍后.. ');
    	mainForm.action = "<%=rootPath%>/ciscosla.do?action=add";
    	mainForm.submit();
    }
 });
});
function setBid(eventTextId , eventId){
	var event = document.getElementById(eventId);
	return CreateWindow('<%=rootPath%>/business.do?action=setBid&event='+event.id+'&value='+event.value + '&eventText=' + eventTextId);
}
function CreateWindow(url)
{
	
msgWindow=window.open(url,"protypeWindow","toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
}  

$(document).ready(function()
{
     $("#ciscoTr").hide();
　　　$("#devicetype").change(function()
        {
　　　　　　var type=$("#devicetype").val();
          if(type=="h3c"){
            
            $("#ciscoTr").hide();
            $("#h3csign1").show();
            $("#h3csign2").show();
          }else if(type=="cisco"){
           $("#h3csign1").hide();
           $("#h3csign2").hide();
           $("#ciscoTr").show();
          
          }
　　　　　})　
})

</script>

		
		<script type="text/javascript">
			function showTelnetNetList(){
				var url="<%=rootPath%>/ciscosla.do?action=multi_telnet_netip";
				window.open(url,"portScanWindow","toolbar=no,width=900,height=600,directories=no,status=no,scrollbars=yes,menubar=no,resizable=yes");
			}
			
			function toAdd(){
				mainForm.action = "<%=rootPath%>/vpntelnetconf.do?action=ready_add";
			    mainForm.submit();
			}
			
			function toDelete(){  
     				mainForm.action = "<%=rootPath%>/vpntelnetconf.do?action=deleteTimingBackupTelnetConfig";
     				mainForm.submit();
	  		}
	  		
		</script>

	</head>
	<body id="body" class="body" >


		<form name="mainForm" method="post">

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
							<td class="td-container-main-add">
								<table id="container-main-add" class="container-main-add">
									<tr>
										<td>
											<table id="add-content" class="add-content">
												<tr>
													<td>
														<table id="add-content-header" class="add-content-header">
										                	<tr>
											                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
											                	<td class="content-title">&nbsp;>> 服务质量 >> SLA管理 >> 添加SLA监视</td>
											                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
											       			</tr>
											        	</table>
				        							</td>
				        						</tr>
				        						<tr>
				        							<td>
				        								<table id="detail-content-body" class="detail-content-body">
				        									<tr>
				        										<td>
				        										<input type="hidden" name="selVal" id='selVal' value=""/>
                                                                 <input type="hidden" name="textVal" id='textVal' value=""/>
				        												<table border="0" id="table1" cellpadding="0" cellspacing="1" width="100%">
													 						<tr>
																				<TD nowrap align="right" height="24" width="20%">
																					源网络设备：&nbsp;
																				</TD>
																				<TD nowrap   >
																					&nbsp;
																					<input name="ipaddress" type="text" size="40" class="formStyle" id="ipaddress" readonly="readonly" >
																					<input type="button" value="选择网络设备" onclick="showTelnetNetList()"><font color="red">&nbsp;* </font>
																					
																				</TD>  
																			</tr>
																			<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24" width="20%">
																					目标IP：&nbsp;
																				</TD>
																				<TD >&nbsp;
																					<input type="text" name="destip" maxlength="80" size="20" class="formStyle"><font color="red">&nbsp;* </font>
																				</TD>
																			</tr>
																			<tr >
																				<TD nowrap align="right" height="24" width="20%">
																					名称：&nbsp;
																				</TD>
																				<TD >&nbsp;
																					<input type="text" name="name" maxlength="80" size="20" class="formStyle"><font color="red">&nbsp;* </font>
																				</TD>
																			</tr>
																			<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24" width="20%">
																					描述：&nbsp;
																				</TD>
																				<TD >&nbsp;
																					<input type="text" name="descr" maxlength="80" size="20" class="formStyle">
																				</TD>
																			</tr>	
																			<tr >
																				<TD nowrap align="right" height="24" width="20%">设备类型：&nbsp;</TD>
																				<td>&nbsp;
																			    		<select id="devicetype" name="devicetype" >
																			    		    <option value="h3c">H3C</option>
																							<option value="cisco">Cisco</option>
																						</select><font color="red">&nbsp;* </font>
																				</td>
																			</tr>																			
																			<tr >
																				<TD nowrap align="right" height="24" width="20%">SLA类型：&nbsp;</TD>
																				<td>&nbsp;
																			    		<select id="slatype" name="slatype">
																								<option value="icmp">ICMP Echo 作业</option>
																								<option value="icmppath">ICMP Path Echo 作业</option>
																								<option value="tcpconnect-withresponder">TCPConnect(启动Responder)</option>
																								<option value="tcpconnect-noresponder">TCPConnect(未启动Responder)</option>
																								<option value="udp">UDPEcho</option>
																								<option value="dns">DNS</option>
																								<option value="http">HTTP</option>
																								<option value="jitter">Jitter</option>
																						</select><font color="red">&nbsp;* </font>
																				</td>
																			</tr>	
																			<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24" width="20%">采集方式：&nbsp;</TD>
																				<td>&nbsp;
																			    		<select id="collecttype" name="collecttype">
																								<option value="snmp">SNMP</option>
																								<option value="telnet">Telnet</option>
																						</select>
																				</td>
																			</tr>																																				
																			<tr >
																				<TD nowrap align="right" height="24" width="20%">采集间隔：&nbsp;</TD>
																				<td>&nbsp;
																			    		<select id="poll_interval" name="poll_interval">
																								<option value="1-m">1分钟</option>
																								<option value="2-m">2分钟</option>
																								<option value="3-m">3分钟</option>
																								<option value="4-m">4分钟</option>
																								<option value="5-m">5分钟</option>
																								<option value="10-m">10分钟</option>
																								<option value="30-m">30分钟</option>
																								<option value="1-h">1小时</option>
																								<option value="4-h">4小时</option>
																								<option value="8-h">8小时</option>
																								<option value="12-h">12小时</option>
																								<option value="1-d">1天</option>
																								<option value="1-w">1周</option>
																								<option value="1-mt">1月</option>
																								<option value="1-y">1年</option>
																						</select>
																				</td>
																			</tr>
																			<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24" width="20%">
																					是否启动：&nbsp;
																				</TD>
																				<TD nowrap   >
																					&nbsp;
																					<select name="mon_flag">
																						<option value="1" label="是">是</option>
																						<option value="0" label="否">否</option>
																					</select>
																				</TD>
																			</tr>
																			<tr id="ciscoTr">
																				<TD nowrap align="right" height="24" width="20%">
																					入口号（名称）：&nbsp;
																				</TD>
																				<TD >&nbsp;
																					<input type="text" name="entrynumber" maxlength="80" size="20" class="formStyle"><font color="red">&nbsp;* </font>（说明：Cisco类型的设备填写为入口号）
																				</TD>
																			</tr>																			
																			<tr id="h3csign1">
																				<TD nowrap align="right" height="24" width="20%">
																					管理名：&nbsp;
																				</TD>
																				<TD >&nbsp;
																					<input type="text" name="adminsign" maxlength="80" size="20" class="formStyle"><font color="red">&nbsp;* </font>（说明：H3C的设备填写 "管理名"）
																				</TD>
																			</tr>
																			<tr id="h3csign2">
																				<TD nowrap align="right" height="24" width="20%">
																					操作标识：&nbsp;
																				</TD>
																				<TD >&nbsp;
																					<input type="text" name="operatesign" maxlength="80" size="20" class="formStyle"><font color="red">&nbsp;* </font>（说明：H3C的设备填写 "操作标识"）
																				</TD>
																			</tr>																				
																            <tr style="background-color: #ECECEC;">	
																				<TD nowrap align="right" height="24">所属业务：&nbsp;</TD>
																				<td nowrap colspan="3" height="1">&nbsp;
																				<input type=text readonly="readonly" onclick='setBid("bidtext" , "bid");' id="bidtext" name="bidtext" size="50" maxlength="32" value="">&nbsp;&nbsp;<input type=button value="设置所属业务" onclick='setBid("bidtext" , "bid");'>
																				<input type="hidden" id="bid" name="bid" value="">
																				</td>
																			</tr>																			
																			<tr >
																				<TD nowrap colspan="4" align=center > 
																				<br>
																				
																					<input type="button" id="process" style="width:50" value="确 定">&nbsp;&nbsp;  
																					<input type="reset" style="width: 50" value="返  回" onclick="javascript:history.back(1)">
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
				        								<table id="detail-content-footer" class="detail-content-footer">
				        									<tr>
				        										<td>
				        											<table width="100%" border="0" cellspacing="0" cellpadding="0">
											                  			<tr>
											                    			<td align="left" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_01.jpg" width="5" height="12" /></td>
											                    			<td></td>
											                    			<td align="right" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_03.jpg" width="5" height="12" /></td>
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
	</body>
</HTML>