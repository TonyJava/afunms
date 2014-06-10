<%@page language="java" contentType="text/html;charset=GB2312"%>

<%  
   String rootPath = request.getContextPath();  

   String lineId = (String)request.getAttribute("lineId");  
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
  function toEdit()
  {
      var   args   =   window.dialogArguments;   
      mainForm.action = "<%=rootPath%>/submap.do?action=saveLinkProperty&xml="+args.fatherXML;
      mainForm.submit();
      window.close(); 
      args.location.reload(); 
  }

</script>
</head>
<body id="body" class="body">
		<form id="mainForm" method="post" name="mainForm"  >
		<input type=hidden name="lineId" value="<%=lineId%>">
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
																	<td class="add-content-title">实体链路属性
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
			<TD nowrap width="80%">&nbsp; <input type="text" name="link_name" size="26" class="formStyle"></TD>	
            </tr>
			<tr>						
			<TD  nowrap align="right" height="24" width="20%">链路宽度&nbsp;</TD>				
			<TD nowrap width="80%">&nbsp;
			   <select size=1 name='link_width' style='width:50px;' onchange="">
			       <option value="1" selected>1</option>
				   <option value="2">2</option>
				   <option value="3">3</option>
				   <option value="4">4</option>
				   <option value="5">5</option>
			   </select>
			</TD>
			</tr>
			
     
			<tr>
				<TD nowrap colspan="4" align=center>
					<br>
					<input type="button" value="修改" style="width:50"  onclick="toEdit()">
					<input type="button" value="关闭" style="width:50" onclick="window.close();">
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