<%@page language="java" contentType="text/html;charset=GB2312"%>
<%  
   String rootPath = request.getContextPath();  
   String start_id = (String)request.getAttribute("start_id");  
   String end_id = (String)request.getAttribute("end_id"); 
   String s_alias = (String)request.getAttribute("s_alias");  
   String e_alias = (String)request.getAttribute("e_alias");     
   String xml = (String)request.getAttribute("xml"); 
   String start_x_y = (String)request.getAttribute("start_x_y"); 
   String end_x_y = (String)request.getAttribute("end_x_y");  
%>
<html>  
<head>
<base target="_self">
<meta http-equiv="Page-Enter" content="revealTrans(duration=x, transition=y)">
<meta http-equiv="Pragma" content="no-cache">
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<meta http-equiv="Page-Enter" content="revealTrans(duration=x, transition=y)">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
		<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
		<script type="text/javascript" 	src="<%=rootPath%>/application/resource/js/addTimeConfig.js" charset="gb2312"></script>
		<script type="text/javascript" 	src="<%=rootPath%>/application/resource/js/jquery-1.4.2.js" charset="gb2312"></script>
<script language="javascript"> 
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
function save()
{
    var args = window.dialogArguments;   
    var direction1 = document.getElementById("direction1").value;
    var xml = "<%=xml%>";
    var line_name = document.getElementById("line_name").value;
    var link_width = document.getElementById("link_width").value;
    var start_id = "<%=start_id%>";
	var start_x_y = document.getElementById("start_x_y").value;
	var s_alias = "<%=s_alias%>";
	var end_id = "<%=end_id%>";
	var end_x_y = document.getElementById("end_x_y").value;
	var e_alias = "<%=e_alias%>";
    args.parent.mainFrame.addline(direction1,xml,line_name,link_width,start_id,start_x_y,s_alias,end_id,end_x_y,e_alias);
    window.close(); 
}

</script>
</head>
<body id="body" class="body">
		<form id="mainForm" method="post" name="mainForm"  >
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
																	<td class="add-content-title">创建示意链路
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
			   &nbsp; <input type="text" name="line_name" size="30" class="formStyle">
			</TD>
			</tr>
			<tr>						
			<TD  nowrap align="right" height="24" width="20%">节点类型&nbsp;</TD>				
			<TD nowrap width="80%">&nbsp;
			   <select size=1 name='direction1' style='width:200px;' id="direction1" onchange="changeDirection(1)">
			       <option value="1" selected>父节点</option>
				   <option value="2">子节点</option>
			   </select>
			</TD>
			</tr>
			<tr >						
			<TD  nowrap align="right" height="24" width="20%">节点名称&nbsp;</TD>				
			<TD nowrap width="80%">&nbsp; <%=s_alias%></TD>	
            </tr>	
            <tr >						
			<TD  nowrap align="right" height="24" width="20%">节点坐标&nbsp;</TD>				
			<TD nowrap width="80%">&nbsp; <input type="text" name="start_x_y" size="20" class="formStyle" value="<%=start_x_y%>"></TD>	
            </tr>
            <tr>						
			<TD  nowrap align="right" height="24" width="20%">描述&nbsp;</TD>				
			<TD nowrap width="80%">
			   &nbsp; <%=start_id%>
			</TD>
			</tr>
			<tr>						
			<TD  nowrap align="right" height="24" width="20%">节点类型&nbsp;</TD>				
			<TD nowrap width="80%">&nbsp;
			   <select size=1 name='direction2' style='width:200px;' id="direction2" onchange="changeDirection(2)">
			       <option value="1">父节点</option>
				   <option value="2" selected>子节点</option>
			   </select>
			</TD>
			</tr>
			<tr>						
			<TD  nowrap align="right" height="24" width="20%">节点名称&nbsp;</TD>				
			<TD nowrap width="80%">&nbsp; <%=e_alias%></TD>	
            </tr>
            <tr >						
			<TD  nowrap align="right" height="24" width="20%">节点坐标&nbsp;</TD>				
			<TD nowrap width="80%">&nbsp; <input type="text" name="end_x_y" size="20" class="formStyle" value="<%=end_x_y%>"></TD>	
            </tr>
            <tr>
            <TD  nowrap align="right" height="24" width="20%">描述&nbsp;</TD>				
			<TD nowrap width="80%">
			   &nbsp; <%=end_id%>
			</TD>
			</tr>
			<tr>						
			<TD  nowrap align="right" height="24" width="20%">链路宽度&nbsp;</TD>				
			<TD nowrap width="80%">&nbsp;
			   <select size=1 name='link_width' style='width:50px;' onchange="">
			       <option value="1" selected>1</option>
				   <option value="2">2</option>
				   <option value="3">3</option>
			   </select>
			</TD>
			</tr>
			<tr>
			<td>
			  <input type="hidden" name='start_id' value="<%=start_id%>"/>
			  <input type="hidden" name='end_id' value="<%=end_id%>"/>
			  <input type="hidden" name='s_alias' value="<%=s_alias%>"/>
			  <input type="hidden" name='e_alias' value="<%=e_alias%>"/>
			  <input type="hidden" name='xml' value="<%=xml%>"/>
			</td></tr>
			<tr>
				<TD nowrap colspan="4" align=center>
					<br>
					<input type="button" value="保存" style="width:50"  onclick="save()">
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