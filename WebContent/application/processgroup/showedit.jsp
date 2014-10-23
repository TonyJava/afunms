<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="com.afunms.application.model.ProcessGroup"%>
<%@page import="com.afunms.application.model.ProcessGroupConfiguration"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.config.dao.DistrictDao"%>
<%@page import="com.afunms.config.model.DistrictConfig"%>
<%
  String rootPath = request.getContextPath();
  String menuTable = (String)request.getAttribute("menuTable");
  String ipaddress = (String)request.getAttribute("ipaddress");
  String nodeid = (String)request.getAttribute("nodeid");
  String groupId = (String)request.getAttribute("groupId");
  ProcessGroup processGroup = (ProcessGroup)request.getAttribute("processGroup");
  List list = (List)request.getAttribute("list");
  %>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>

<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>

<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="gb2312"></script>

<!--nielin add for processgroupconfiguration at 2010-08-17 start-->
<script type="text/javascript" 	src="<%=rootPath%>/application/resource/js/processgroupconfiguration.js" charset="gb2312"></script>
<!--nielin add for processgroupconfiguration at 2010-08-17 end-->




<script language="JavaScript" type="text/javascript">


  Ext.onReady(function()
{  

setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
	
	
 Ext.get("process").on("click",function(){
  			var chk = checkinput("name","string","进程组名称",200,false);
  			if(chk){
  				 Ext.MessageBox.wait('数据加载中，请稍后.. ');
        	mainForm.action = "<%=rootPath%>/processgroup.do?action=edit&forward=showlist";
        	mainForm.submit();
  			}
           
       // mainForm.submit();
 });	
	
});
//-- nielin modify at 2010-01-04 start ----------------
function CreateWindow(url)
{
	
msgWindow=window.open(url,"protypeWindow","toolbar=no,width=750,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
}    

function chooseProcess(eventId){
	var ipaddress = document.getElementById("ipaddress");
	var nodeid = document.getElementById("nodeid");
	if( ipaddress.value && nodeid.value){
		return CreateWindow('<%=rootPath%>/processgroup.do?action=chooseProcess&ipaddress=' + ipaddress.value + '&nodeid=' + nodeid.value + '&eventId='+eventId);
	}
	alert("请先选择设备");
	return;
}

function chooseNode(nodeId , ipaddress){
		return CreateWindow('<%=rootPath%>/processgroup.do?action=chooseNode&' + '&nodeIdevent='+nodeId + '&ipaddressevent=' + ipaddress);
	
}
</script>
<script language="JavaScript" type="text/javascript">
	function processConfigInit(){
		var rowNum = document.getElementById("rowNum");
		rowNum.value = "0";
		var processConfigs = new Array();
		<%	
			if(list!=null && list.size()>=0){
				for(int i = 0 ; i < list.size(); i++){	   
		            ProcessGroupConfiguration processGroupConfiguration = (ProcessGroupConfiguration) list.get(i);
		            int processConfigId = processGroupConfiguration.getId();
		            String processConfigName = processGroupConfiguration.getName();
		            String processConfigTimes = processGroupConfiguration.getTimes();
		            String processConfigStatus = processGroupConfiguration.getStatus();
		    %>
		            processConfigs.push({
		                processId:"<%=processConfigId%>",
		                processName:"<%=processConfigName%>",
		                processTimes:"<%=processConfigTimes%>",
		                processStatus:"<%=processConfigStatus%>"
		            });
		    <%
		        }
	        }
	        
	    %> 
		processConfig("processGroupConfigureTable",processConfigs);
		
		document.getElementById("alarm_level").value = "<%=processGroup.getAlarm_level()%>";
		document.getElementById("mon_flag").value = "<%=processGroup.getMon_flag()%>";
	}
</script>

</head>
<body id="body" class="body" onload="processConfigInit();">
	<form id="mainForm" method="post" name="mainForm">
		
		<table>
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
											                	<td class="add-content-title"> 资源 &gt;&gt; 性能监视 &gt;&gt; 进程监视列表 &gt;&gt; 编辑 </td>
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
				        												<table>
				        													<tr>
				        														<td>
				        															<table>
																						<tr>						
																							<td align="right" height="24" width="10%">IP 地址:&nbsp;</td>				
																							<td width="40%">&nbsp;<input type="text" id="ipaddress" readonly="readonly" value="<%=processGroup.getIpaddress()%>" name="ipaddress">
																							<input type="hidden" id="nodeid"  value="<%=processGroup.getNodeid()%>" name="nodeid">
																							<input type="hidden" id="groupId"  value="<%=processGroup.getId()%>" name="groupId">
																							<input type="button" value="选择主机服务器" onclick="chooseNode('nodeid' , 'ipaddress');">
																							</td>
																						</tr>														
																						<tr>						
																							<td align="right" height="24" width="10%">进程组名称:&nbsp;</td>				
																							<td width="40%">&nbsp;<input type="text" id="name" name="name" value="<%=processGroup.getName() %>">
																							</td>
																						</tr>
																						<tr>						
																							<td align="right" height="24" width="10%">告警等级:&nbsp;</td>				
																							<td width="40%">&nbsp;<select id="alarm_level" name="alarm_level">
																									<option value="1">普通告警</option>
																									<option value="2">严重告警</option>
																									<option value="3">紧急告警</option>
																								</select>
																							</td>
																						</tr>
																						<tr>						
																							<td align="right" height="24" width="10%">是否监控:&nbsp;</td>				
																							<td width="40%">&nbsp;<select id="mon_flag" name="mon_flag">
																									<option value="0">否</option>
																									<option value="1">是</option>
																								</select>
																							</td>
																						</tr>
																						<tr>
																							<td nowrap colspan="8">
																							
																							    <!-- nielin modify begin (Phone div)*/ 2009-01-03--->
																							        <div id="formDiv" style="">         
																						                <table width="100%" style="BORDER-COLLAPSE: collapse" borderColor=#cedefa cellPadding=0 rules=none border=1 algin="center" >
																					                        <tr>
																					                            <td style="padding:0px 0px 0px 5px">&nbsp;&nbsp;进程组详细配置</td>
																					                        </tr>
																					                        <tr>
																					                            <td align="center">           
																					                                <table id="processGroupConfigureTable"  style="width:90%; padding:0;  background-color:#FFFFFF;" >
																				                                        <tr>
																				                                            <td colspan="0" height="50" align="center"> 
																				                                                <span  onClick='addRow("processGroupConfigureTable","processgroup");' style="border: 1px solid black;margin:10px;padding:0px 3px 0px 3px;cursor: hand;">增加一行</span>
																				                                            </td>
																				                                        </tr>
																					                                </table>
																					                            </td>
																					                        </tr>
																						                </table>
																						            </div> 
																							      <!-- nielin modify end */ 2009-01-03--->	
																												
																								
																							</td>
																						</tr>
																						<tr>
																							<!-- nielin add (for timeShareConfig) start 2010-01-03 -->
																							<td><input type="hidden" id="rowNum" name="rowNum"></td>
																							<!-- nielin add (for timeShareConfig) end 2010-01-03 -->
																						</tr>	
																						<tr>
																							<TD nowrap colspan="4" align=center>
																							<br><input type="button" value="保 存" style="width:50" id="process" onclick="#">&nbsp;&nbsp;
																								
																								<input type="reset" style="width:50" value="返 回" onclick="javascript:window.history.back(1)">
																							</TD>	
																						</tr>	
														
													                        </TABLE>		
			        														</td>
			        													<tr>
			        												</table>					
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
									<tr>
										<td>
											
										<br></td>
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