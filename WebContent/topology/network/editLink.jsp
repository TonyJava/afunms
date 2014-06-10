<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.polling.node.IfEntity"%>
<%  
   String rootPath = request.getContextPath();  
   
   Iterator it1 = (Iterator)request.getAttribute("start_if");  
   Iterator it2 = (Iterator)request.getAttribute("end_if");  
   String alias_start = (String)request.getAttribute("alias_start");  
   String ipAddress_start = (String)request.getAttribute("ipAddress_start");     
   String alias_end = (String)request.getAttribute("alias_end");  
   String ipAddress_end = (String)request.getAttribute("ipAddress_end");       
   int startId = ((Integer)request.getAttribute("start_id")).intValue();
   int endId = ((Integer)request.getAttribute("end_id")).intValue();
   String startIndex = (String)request.getAttribute("start_index");  
   String endIndex = (String)request.getAttribute("end_index"); 
   String link_name = (String)request.getAttribute("link_name"); 
   String max_speed = (String)request.getAttribute("max_speed"); 
   String max_per = (String)request.getAttribute("max_per");
   String linetext = (String)request.getAttribute("linetext");
   String showinterf = (String)request.getAttribute("showinterf");
   String id = (String)request.getAttribute("id");  
   String linkAliasName = (String)request.getAttribute("linkAliasName");  
%>
<html>
<head>
<base target="_self">

<meta http-equiv="Pragma" content="no-cache">
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
		<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
		<script type="text/javascript" 	src="<%=rootPath%>/application/resource/js/addTimeConfig.js" charset="gb2312"></script>
		<script type="text/javascript" 	src="<%=rootPath%>/application/resource/js/jquery-1.4.2.js" charset="gb2312"></script>
<script language="javascript">
var linkName='<%=link_name%>';
function changeDirection(temp)
{
 var direction1=document.getElementById("direction1");
 var direction2=document.getElementById("direction2");
 var direction1value=direction1.value;
 var direction2value=direction2.value;
 if(temp==1)
 {
  if(direction1value==1)
  {
   direction2.value="2";
  }else
  {
   direction2.value="1";
  }
 }else
 {
  if(direction2value==1)
  {
    direction1.value="2";
  }else
  {
    direction1.value="1";
  }
 }
 
}
function portChange1(obj)
{
 var nowLinkName=document.getElementById("link_name").value;
 if(nowLinkName==linkName)
 {
  var linkNameArray=nowLinkName.split('/');
  var part1=linkNameArray[0];
  var part2=linkNameArray[1];
  part1=part1.substr(0,part1.indexOf('_'));
  part1+='_'+obj.value;
  document.getElementById("link_name").value=part1+'/'+part2;
  linkName=part1+'/'+part2;
  changflag1=true;
 }
}
function portChange2(obj)
{
 var nowLinkName=document.getElementById("link_name").value;
 if(nowLinkName==linkName)
 {
  var linkNameArray=nowLinkName.split('/');
  var part1=linkNameArray[0];
  var part2=linkNameArray[1];
  part2=part2.substr(0,part1.indexOf('_'));
  part2+='_'+obj.value;
  document.getElementById("link_name").value=part1+'/'+part2;
  linkName=part1+'/'+part2;
  changflag2=true;
 }
}          
  function toEdit()
  {
      var args = window.dialogArguments;   
      mainForm.action = "<%=rootPath%>/link.do?action=saveEdit";
      mainForm.submit();
      window.close(); 
      args.location.reload(); 
  }

</script>
</head>
<body id="body" class="body">
		<form id="mainForm" method="post" name="mainForm"  >
		<input type=hidden name="id" value="<%=id%>">
<input type=hidden name="radio" value="<%=id%>">
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
																	<td class="add-content-title">修改实体链路
																	</td>
																	<td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td bgcolor="#FFFFFF">
															<table border="0" id="table1" cellpadding="0" cellspacing="1" width="100%" align="center">
					<TBODY>						
			<tr>						
			<TD  nowrap align="right" height="24" width="20%">链路名称&nbsp;</TD>				
			<TD nowrap width="80%">
			   &nbsp; <input type="hidden" name="link_name" size="30" class="formStyle" value="<%=link_name%>" >
			   <input type="text" name="link_alias_name" size="30" class="formStyle" value="<%=linkAliasName%>">
			</TD>
			</tr>
			<!--  
			<tr>						
			<TD style="background-color: #ECECEC;" nowrap align="right" height="24" width="20%">链路类型&nbsp;</TD>				
			<TD nowrap width="80%">
			   &nbsp; <input type="text" name="link_type" maxlength="50%" size="20" class="formStyle">
			</TD>
			</tr>
			-->
			<tr>						
			<TD  nowrap align="right" height="24" width="20%">链路流量阀值&nbsp;</TD>				
			<TD nowrap width="80%">
			   &nbsp; <input type="text" name="max_speed" maxlength="50%" size="20" class="formStyle" value="<%=max_speed%>">(KB/秒)
			</TD>
			</tr>
			<tr>						
			<TD  nowrap align="right" height="24" width="20%">带宽利用率阀值&nbsp;</TD>				
			<TD nowrap width="80%">
			   &nbsp; <input type="text" name="max_per" maxlength="50%" size="20" class="formStyle" value="<%=max_per%>">(%)
			</TD>
			</tr>
			<tr>						
			<TD  nowrap align="right" height="24" width="20%">链路显示&nbsp;</TD>				
			<TD nowrap width="80%">&nbsp;
			   <select size=1 name='linetext' style='width:200px;' id="linetext">
			   <% 
			        String selected0 = "";
			        String selected1 = "";
			        String selected2 = "";
			        String selected3 = "";
			        String selected4 = "";
			        //System.out.println(linetext);
                    if("0".equals(linetext)) selected0 = "selected";
                    if("1".equals(linetext)) selected1 = "selected";
                    if("2".equals(linetext)) selected2 = "selected";
                    if("3".equals(linetext)) selected3 = "selected";
                    if("4".equals(linetext)) selected4 = "selected";
			   %>
			       <option value="0" <%=selected0%>>无</option>
				   <option value="1" <%=selected1%>>上行带宽利用率(%)</option>
				   <option value="2" <%=selected2%>>下行带宽利用率(%)</option>
				   <option value="3" <%=selected3%>>上行流速(KB/s)</option>
				   <option value="4" <%=selected4%>>下行流速(KB/s)</option>
			   </select>
			</TD>
			</tr>
			<tr>						
			<TD  nowrap align="right" height="24" width="20%">接口显示&nbsp;</TD>				
			<TD nowrap width="80%">&nbsp;
			   <select size=1 name='interf' style='width:200px;' id="interf">
			       <% 
			        String selected00 = "";
			        String selected11 = "";
			        System.out.println(showinterf);
                    if("0".equals(showinterf)) selected00 = "selected";
                    if("1".equals(showinterf)) selected11 = "selected";
			       %>
			       <option value="0" <%=selected00%>>不显示</option>
				   <option value="1" <%=selected11%>>显示</option>
			   </select>
			</TD>
			</tr>
			<tr>						
			<TD  nowrap align="right" height="24" width="20%">设备类型&nbsp;</TD>				
			<TD nowrap width="80%">&nbsp;
			   <select size=1 name='direction1' style='width:200px;' id="direction1" onchange="changeDirection(1)">
			       <option value="1">上行设备</option>
				   <option value="2" selected>下行设备</option>
			   </select>
			</TD>
			</tr>
			<tr >						
			<TD  nowrap align="right" height="24" width="20%">设备名称&nbsp;</TD>				
			<TD nowrap width="80%">&nbsp; <%=alias_start%></TD>	
            </tr>	
            <tr>						
			<TD  nowrap align="right" height="24" width="20%">设备IP&nbsp;</TD>				
			<TD nowrap width="80%">
			   &nbsp; <%=ipAddress_start%>
			</TD>
			<tr>
            <tr>				
			<TD  nowrap align="right" height="24" width="20%">设备端口索引&nbsp;</TD>				
			<TD nowrap width="80%">&nbsp;
            <select size=1 name='start_index' style='width:200px;' onchange="portChange1(this)">
<%
  while(it1.hasNext())
  {
     IfEntity ifObj = (IfEntity)it1.next();
     String selected = "";
     if(ifObj.getIndex().equals(startIndex)) selected = "selected";
%> 			
		<option value='<%=ifObj.getIndex()%>' <%=selected%> title="<%=ifObj.getIndex()%>(<%=ifObj.getDescr()%>)"><%=ifObj.getIndex()%>(<%=ifObj.getDescr()%>)</option>
<%}%></select>			
			</TD>															
			</tr>
			<tr>						
			<TD  nowrap align="right" height="24" width="20%">设备类型&nbsp;</TD>				
			<TD nowrap width="80%">&nbsp;
			   <select size=1 name='direction2' style='width:200px;' id="direction2" onchange="changeDirection(2)">
			       <option value="1" selected>上行设备</option>
				   <option value="2">下行设备</option>
			   </select>
			</TD>
			</tr>
			<tr>						
			<TD  nowrap align="right" height="24" width="20%">设备名称&nbsp;</TD>				
			<TD nowrap width="80%">&nbsp; <%=alias_end%></TD>	
            </tr>
            <tr>
            <TD  nowrap align="right" height="24" width="20%">设备IP&nbsp;</TD>				
			<TD nowrap width="80%">
			   &nbsp; <%=ipAddress_end%>
			</TD>
			</tr>
            <tr>					
			<TD  nowrap align="right" height="24" width="20%">设备端口索引&nbsp;</TD>				
			<TD nowrap width="80%">&nbsp;
            <select size=1 name='end_index' style='width:200px;' onchange="portChange2(this)">
<%
  while(it2.hasNext())
  {
     IfEntity ifObj = (IfEntity)it2.next();
     String selected = "";
     if(ifObj.getIndex().equals(endIndex)) selected = "selected";
%> 			
		<option value='<%=ifObj.getIndex()%>' <%=selected%> title="<%=ifObj.getIndex()%>(<%=ifObj.getDescr()%>)"><%=ifObj.getIndex()%>(<%=ifObj.getDescr()%>)</option>
<%}%></select>	
			

</TD>															
			</tr>
			<tr><td>
			  <input type="hidden" name='start_id' value="<%=startId%>"/>
			  <input type="hidden" name='end_id' value="<%=endId%>"/>
			</td></tr>
			<tr>
				<TD nowrap colspan="4" align=center>
					<br>
					<input type="button" value="修改" style="width:50"  onclick="toEdit()">
					<input type="button" value="关闭" style="width:50"  onclick="window.close();">
				</TD>	
			</tr>
			</TBODY>
				</TABLE>
														</td>
													</tr>
													<tr>
														<td>
															<table id="detail-content-footer" class="detail-content-footer">
																<tr>
																	<td>
																		<table width="100%" border="0" cellspacing="0" cellpadding="0">
																			<tr>
																				<td align="left" valign="bottom">
																					<img src="<%=rootPath%>/common/images/right_b_01.jpg" width="5" height="12" />
																				</td>
																				<td></td>
																				<td align="right" valign="bottom">
																					<img src="<%=rootPath%>/common/images/right_b_03.jpg" width="5" height="12" />
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
	</body>

</html>