<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>



<%@page import="com.afunms.application.model.*"%>

<%@page import="java.util.*"%>
<%
  String rootPath = request.getContextPath();
  String menuTable = (String)request.getAttribute("menuTable");
  
  List urllist = (List)request.getAttribute("urllist");
  EmailMonitorConfig queryconf = (EmailMonitorConfig)request.getAttribute("initconf");
%>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>

<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

<script language="JavaScript" type="text/JavaScript">


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
			menu =new Menu(td.id,td.id+"child",'dtu','100',hide,my_on,my_off);
			menu.init();		
		}
	}
}


function show_graph(){
      mainForm.action = "<%=rootPath%>/mail.do?action=detail";
      mainForm.submit();
} 


</script>


</head>
<body id="body" class="body" onload="initmenu();">
	<form id="mainForm" method="post" name="mainForm">
		
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
							<td class="td-container-main-application-detail">
								<table id="container-main-application-detail" class="container-main-application-detail">
									<tr>
										<td>
											<table id="application-detail-content" class="application-detail-content">
												<tr>
													<td>
														<table id="application-detail-content-header" class="application-detail-content-header">
										                	<tr>
											                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
											                	<td class="application-detail-content-title">email 详细信息</td>
											                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
											       			</tr>
											        	</table>
				        							</td>
				        						</tr>
				        						<tr>
				        							<td>
				        								<table id="application-detail-content-body" class="application-detail-content-body">
				        									<tr>
				        										<td>
				        										
				        											<table>
										               					<tr>
										     								<td>
	
																				服务名称
																				<select name="id" >
																				<%
																					if(urllist != null && urllist.size()>0){
																						for(int i=0;i<urllist.size();i++){
																							EmailMonitorConfig webconfig = (EmailMonitorConfig)urllist.get(i);
																							if( webconfig.getId() == queryconf.getId()){
																								
																							
																				%>
																				
																				<option value="<%=webconfig.getId()%>" selected="selected"> <%=webconfig.getName()%></option>
																				
																				<%
																							}else{
																				%>
																				<option value="<%=webconfig.getId()%>"> <%=webconfig.getName()%></option>
																				<%
																							}
																						}
																					}
																				%>
																				</select>
																			<input type="button" onclick="show_graph()" class=button value="查询">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br>
																			</td>
																	                        
													                        
									                        			</tr>
									                        			<tr>
									                        				<td>
									                        					<table cellspacing="20">
									                        						<tr> 
																			 			<td width="48%" align="center"> 
																			 				<table width="100%" cellspacing="0" cellpadding="0" align="center">
																					            <tr> 
																					         		<td width="100%" align="center"> 
																					         			<div id="flashcontent1">
																											<strong>You need to upgrade your Flash Player</strong>
																										</div>
																										<script type="text/javascript">
																											var so = new SWFObject("<%=rootPath%>/flex/Email_Info.swf?names=<%=queryconf.getName()%>&adress=<%=queryconf.getAddress()%>&send=<%=queryconf.getUsername()%>&accept=<%=queryconf.getRecivemail()%>&lasttime=<%=request.getAttribute("lasttime")%>&nexttime=<%=request.getAttribute("nexttime")%>", "Email_Info", "400", "250", "8", "#ffffff");
																											so.write("flashcontent1");
																										</script>				
																					                </td>
																								</tr> 
																				          </table>			
																			        	</td>        
																						<td width="48%" align="center">
																		          			<table width="100%" cellspacing="0" cellpadding="0" align="center">
																					            <tr> 
																					         		<td width="100%" align="center"> 
																					         			<div id="flashcontent2">
																											<strong>You need to upgrade your Flash Player</strong>
																										</div>
																										<script type="text/javascript">
																											var so = new SWFObject("<%=rootPath%>/flex/Email_Ping_Pie.swf?id=<%=queryconf.getId()%>", "Email_Ping_Pie", "400", "250", "8", "#ffffff");
																											so.write("flashcontent2");
																										</script>				
																					                </td>
																								</tr> 
																		          			</table>		
																						</td>
																			      	</tr>
																			      	<tr> 
																				        <td width="48%" align="center">
																				        	<table width="100%" cellspacing="0" cellpadding="0" align="center">
																					            <tr> 
																					         		<td width="100%" align="center"> 
																					         			<div id="flashcontent3">
																											<strong>You need to upgrade your Flash Player</strong>
																										</div>
																										<script type="text/javascript">
																											var so = new SWFObject("<%=rootPath%>/flex/Email_Ping_Line.swf?id=<%=queryconf.getId()%>", "Email_Ping_Line", "400", "250", "8", "#ffffff");
																											so.write("flashcontent3");
																										</script>				
																					                </td>
																								</tr> 
																				          	</table>
																				        </td>  
																				        <td width="48%" align="center">
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
				        						<tr>
				        							<td>
				        								<table id="application-detail-content-footer" class="application-detail-content-footer">
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