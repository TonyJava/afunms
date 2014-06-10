<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.topology.model.ManageXml"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@ include file="/include/globe.inc"%>
<%
   User current_user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
   System.out.println(current_user.getBusinessids());
   String bids[] = current_user.getBusinessids().split(",");
  List list = (List)request.getAttribute("list");
  int rc = list.size();
  String rootPath = request.getContextPath();
  //JspPage jp = (JspPage)request.getAttribute("page");
  String nodeId = (String)request.getAttribute("nodeId");
  String category = (String)request.getAttribute("category");
  String mapId = (String)request.getAttribute("mapId");
%>
<html>
<head>
<title>关联拓扑图</title>
<base target="_self">
<link href="<%=rootPath%>/resource/css/global/global.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<meta http-equiv="Pragma" content="no-cache">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
		<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
		<script type="text/javascript" 	src="<%=rootPath%>/application/resource/js/addTimeConfig.js" charset="gb2312"></script>
		<script type="text/javascript" 	src="<%=rootPath%>/application/resource/js/jquery-1.4.2.js" charset="gb2312"></script>
<script language="JavaScript" type="text/javascript">
function save()//关联拓扑图
{
    var args = window.dialogArguments;
	mainForm.action = "<%=rootPath%>/submap.do?action=save_relation_node&xml="+args.fatherXML;
    mainForm.submit();
    alert("关联成功!");
	window.close();
	args.location.reload();
}
  
function cancel()
{
    var args = window.dialogArguments;
    mainForm.action = "<%=rootPath%>/submap.do?action=cancel_relation_node&xml="+args.fatherXML;
    mainForm.submit();
    alert("取消成功!");
	window.close();
	args.location.reload();
}

</script>
</head>

<body id="body" class="body">
		<form id="mainForm" method="post" name="mainForm"  >
		<input type=hidden name="nodeId" id="nodeid" value="<%=nodeId%>">
<input type=hidden name="category" value="<%=category%>">
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
																	<td class="add-content-title">关联拓扑图
																	</td>
																	<td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td bgcolor="#FFFFFF">
															 <input type="hidden" name="intMultibox">	
				                   <table id="content-body" class="content-body">
					                   <tr>
						               <td>
						               <table cellspacing="1" cellpadding="1" width="100%" border="1" bordercolor="#ffeeee">
						                   <tr class="microsoftLook0" height=28>
				                           <th align='center' width='15%'>序号</th>				
				                           <th align='center' width='85%'>视图名</th>
				                           </tr>
											<%
											    //int startRow = jp.getStartRow();
											    
											    for(int i=0;i<rc;i++)
											    {
											       ManageXml vo = (ManageXml)list.get(i);
										        int tag = 0;
												if(bids!=null&&bids.length>0){
												    for(int j=0;j<bids.length;j++){
												        if(vo.getBid()!=null&&!"".equals(vo.getBid())&&vo.getBid().indexOf(bids[j])!=-1){
												            tag++;
												        }
												    }
												}
												if(tag>0){
											       if(vo.getId()==Integer.parseInt(mapId)){
										   %>
				                              <tr <%=onmouseoverstyle%> class="microsoftLook">
				   	                       <td  align='center'><INPUT type="radio" class=noborder name=radio value="<%=vo.getId()%>" class=noborder checked><font color='blue'><%=i+1%></font></td>    	 	
				   	                       <td  align='center'><%=vo.getTopoName()%></td>			
				                           </tr>
											<%
											       } else {
							
											%>
				                              <tr <%=onmouseoverstyle%> class="microsoftLook">
				   	                       <td  align='center'><INPUT type="radio" class=noborder name=radio value="<%=vo.getId()%>" class=noborder ><font color='blue'><%=i+1%></font></td>    	 	
				   	                       <td  align='center'><%=vo.getTopoName()%></td>			
				                           </tr>
											<%
											       }}
											    }
											%> 				
						               </table>
						               </td>
					                   </tr>
					                   <tr><td height="20">
						     </td>
						    </tr>
					                    <tr><td>
										    <table height="60"><tr>
											    <td nowrap colspan="20" align=center>
													<input type="button" value="确定" style="width:50"  onclick="save()">
													<input type="button" value="取消关联" style="width:80" onclick="cancel();">
													<input type="button" value="关闭" style="width:50" onclick="window.close();">
										       </td>
										   </tr></table>
										    </td>
										    </tr> 
										    <tr><td height="20">
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

</HTML>
