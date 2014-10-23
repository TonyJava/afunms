<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="java.util.List"%>
<%@page import="com.afunms.system.model.*" %>
<%@page import="com.afunms.config.model.EnvConfig"%>
<%@ include file="/include/globe.inc"%>
<% 
 

  String rootPath = request.getContextPath(); 
  List list = (List)request.getAttribute("list");
  String id = (String)request.getAttribute("id");
  //Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(id));
  String ipaddress = (String)request.getAttribute("ipaddress");
java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd HH:mm");					  	 
         
%>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link href="<%=rootPath%>/resource/css/global/global.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">

<script language="JavaScript" type="text/javascript">

function doNodeFromlastoconfig()
  {  
     mainForm.action = "<%=rootPath%>/envconfig.do?action=fromNodeLasttoconfig";
     mainForm.submit();
  }  
  
function CreateWindow(url)
{
	
msgWindow=window.open(url,"protypeWindow","toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
}    

<!--wxy add-->
function editNodePort(id,sms)
  {
 
      $.ajax({
			type:"GET",
			dataType:"json",
			url:"<%=rootPath%>/networkDeviceAjaxManager.ajax?action=editnodepower&id="+id+"&enabled="+sms+"&nowtime="+(new Date()),
			success:function(data){
			
			if(data.flagStr==3){
			alert("修改失败！！！");
			}else if(data.flagStr==0){
			var smsId =document.getElementById("smsFlag"+id);
		    smsId.innerHTML ="<span style='cursor:hand' onclick='editNodePort("+id+",1)'><font color=#skyblue>监视</font></span>";
			}else if(data.flagStr==1){
			var smsId =document.getElementById("smsFlag"+id);
		    smsId.innerHTML ="<span style='cursor:hand' onclick='editNodePort("+id+",0)'><font color=#green>取消监视</font></span>";
			}
			}
		});
  }
  <!--wxy add-->
  function editNodePortForReport(id,reportFlag)
  {
  
      $.ajax({
			type:"GET",
			dataType:"json",
			url:"<%=rootPath%>/networkDeviceAjaxManager.ajax?action=editnodeport&id="+id+"&reportflag="+reportFlag+"&nowtime="+(new Date()),
			success:function(data){
		
			if(data.flagStr==3){
			alert("修改失败！！！");
			}else if(data.flagStr==0){
			var reportId =document.getElementById("reportflag"+id);
		    reportId.innerHTML ="<span style='cursor:hand' onclick='editNodePortForReport("+id+",1)'><font color=#skyblue>显示于报表</font></span>";
			}else if(data.flagStr==1){
			var reportId =document.getElementById("reportflag"+id);
		    reportId.innerHTML ="<span style='cursor:hand' onclick='editNodePortForReport("+id+",0)'><font color color=#green>取消显示于报表</font></span>";
			}
			}
		});
  }
</script>

<script language="JavaScript" type="text/JavaScript">
//修改警告级别
function modifyPowerAlarmlevel(id){
	var t = document.getElementById("alarmlevel"+id);
	var alarmvalue = t.selectedIndex;//获取下拉框的值
	$.ajax({
			type:"GET",
			dataType:"json",
			url:"<%=rootPath%>/networkDeviceAjaxManager.ajax?action=modifyPowerAlarmlevel&id="+id+"&alarmlevel="+alarmvalue+"&nowtime="+(new Date()),
			success:function(data){
				window.alert("修改成功！");
			}
		});
}

</script>

</head>
<body id="body" class="body">

   <form name="mainForm" method="post">
   <input type=hidden name="id" value="<%=id%>">
   <input type=hidden name="ipaddress" value="<%=ipaddress%>">
    <input type=hidden name="entity" value="power">
		<table id="body-container" class="body-container">
			<tr>
				
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
											                	<td class="add-content-title">设备电源配置信息 >> <%=ipaddress%></td>
											                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
											       			</tr>
											        	</table>
				        							</td>
				        						</tr>
				        						<tr >
													<td>
														<table id="add-content-header" class="add-content-header" width="100%">
										                	<tr >
											                	<td align="right" width="">
											                	   <INPUT type="button" class="formStyle" value="刷 新" onclick="doNodeFromlastoconfig()"/>
											                	   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											                	</td>	
											       			</tr>
											        	</table>
				        							</td>
				        						</tr>
				        						<tr>
				        							<td>
				        								<table id="detail-content-body" class="detail-content-body">
				        									<tr>
				        										<td>
				        										
				        												<table border="0" id="table1" cellpadding="0" cellspacing="1" width="100%">
																	<TBODY>
																	<tr>
							      											<th width='4%'   class="report-data-body-title" align="center" bgcolor=#F1F1F1>序号</th>				
							      											<th width='18%' class="report-data-body-title" align="center" bgcolor=#F1F1F1>设备名称(ip)</th>
							      											<th width='18%' class="report-data-body-title" align="center" bgcolor=#F1F1F1>电源模块</th>
							      											<th width='14%' class="report-data-body-title" align="center" bgcolor=#F1F1F1>描述</th>      
							      											<th width='7%' class="report-data-body-title" align="center" bgcolor=#F1F1F1>是否监视</th>
							      											<th width='10%' class="report-data-body-title" align="center" bgcolor=#F1F1F1>报警级别</th>
							      											<th width='5%' class="report-data-body-title" align="center" bgcolor=#F1F1F1>操作</th>
							      											
																	</tr>

<%
    EnvConfig vo = null;
    for(int i=0;i<list.size();i++)
    {
       vo = (EnvConfig)list.get(i);
       if(vo==null)continue;

          
%>
   										<tr bgcolor="#FFFFFF" >  
    											<td class="report-data-body-list"><font color='blue'>&nbsp;<%= 1+i%></font></td>
    											<td  class="report-data-body-list"><%=vo.getIpaddress()%></td>
    											<td  class="report-data-body-list"><%=vo.getName()%></td>
    											<td  class="report-data-body-list"><%=vo.getBak()%></td>
    											<%
    												String smsbgcolor="skyblue";
    												String repbgcolor="skyblue";
    												String sms="<span style='cursor:hand' onclick='editNodePort("+vo.getId()+",0)'><font color=green>取消监视</font></span>";
    												if(vo.getEnabled()==0){
    													smsbgcolor="#FFFFFF";
    													sms="<span style='cursor:hand' onclick='editNodePort("+vo.getId()+",1)'><font color=#skyblue>监视</font></span>";
    												}
    											%>
    											<td  align='center' class="report-data-body-list" id="smsFlag<%=vo.getId() %>"><%=sms%></td>
    											<% 
    											   String alarmlevel = vo.getAlarmlevel();
    											   //String varalarmlevel = "";
    											   if(alarmlevel == null || alarmlevel.equals("")) {
    												   alarmlevel = "0";
    											   }
    											%>
    											<td  class="report-data-body-list">
													<select style="width:60px;" id="alarmlevel<%=vo.getId()%>">
    													<option  value="0" <%if(alarmlevel.equals("0")) {%>selected<%} %>>无</option>
    													<option  value="1" <%if(alarmlevel.equals("1")) {%>selected<%} %>>普通</option>
    													<option  value="2" <%if(alarmlevel.equals("2")) {%>selected<%} %>>重要</option>
    													<option  value="3" <%if(alarmlevel.equals("3")) {%>selected<%} %>>紧急</option>    													
    													</select>
     												<img href="#" src="<%= rootPath%>/resource/image/menu/xgmm.gif" style="cursor:hand" onclick="modifyPowerAlarmlevel('<%=vo.getId()%>');"/>
												</td>
											
											<td  class="report-data-body-list" align='center'><span style='cursor:hand' onClick='window.open("<%=rootPath%>/envconfig.do?action=readyEdit&entity=power&id=<%=vo.getId()%>","_blank", "height=400, width= 500, top=200, left= 200")'>
											<img src="<%=rootPath%>/resource/image/editicon.gif" border="0"/></span></td>
        										
  										</tr>			
<% }%>            
            					</tbody>
            					</table>
            					<br>
			</TD>																			
			</tr>			
															
															<tr>
																<!-- nielin add (for timeShareConfig) start 2010-01-03 -->
																<td><input type="hidden" id="rowNum" name="rowNum"></td>
																<!-- nielin add (for timeShareConfig) end 2010-01-03 -->
															
															</tr>
															<tr>
																<TD nowrap colspan="4" align=center>
																
																	<input type="reset" style="width:50" value="关闭" onclick="javascript:window.close()">
																</TD>	
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
</BODY>


</HTML>