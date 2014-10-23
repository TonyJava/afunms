<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>

<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@page import="com.afunms.common.util.*" %>
<%@page import="com.afunms.monitor.item.*"%>
<%@page import="com.afunms.polling.node.*"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="com.afunms.polling.impl.*"%>
<%@page import="com.afunms.polling.api.*"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.topology.dao.*"%>
<%@page import="com.afunms.monitor.item.base.MoidConstants"%>
<%@page import="org.jfree.data.general.DefaultPieDataset"%>
<%@ page import="com.afunms.polling.api.I_Portconfig"%>
<%@ page import="com.afunms.polling.om.Portconfig"%>
<%@ page import="com.afunms.polling.om.*"%>
<%@ page import="com.afunms.polling.impl.PortconfigManager"%>
<%@page import="com.afunms.report.jfree.ChartCreator"%>

<%@page import="com.afunms.config.dao.*"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.application.dao.*"%>
<%@page import="com.afunms.application.model.*"%>
<%@page import="com.afunms.application.manage.WasManager"%>
<%@page import="com.afunms.application.wasmonitor.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="java.lang.*"%>
<%@page import="com.afunms.monitor.item.base.*"%>
<%@page import="com.afunms.monitor.executor.base.*"%>
<%@ page import="com.afunms.mq.DEServerConstants"%>
<%@ page import="com.ibm.mq.pcf.CMQXC"%>
<%@ page import="com.ibm.mq.pcf.CMQCFC"%>
<%@ page import="com.afunms.mq.*"%>

<%@page import="com.afunms.inform.util.SystemSnap" %>
<%@page import="com.afunms.polling.base.*"%>
<%
 	String rootPath = request.getContextPath();
 	String menuTable = (String)request.getAttribute("menuTable");
 	Double mqAvgPing = (Double)request.getAttribute("mqAvgPing");
	int percent1 = Double.valueOf(mqAvgPing).intValue();
	
	//System.out.println(mqAvgPing+"---------------");
	//System.out.println(percent1+"--------------");
	int percent2 = 100-percent1;
	String dbPage = "detail";
	String flag_1 = (String)request.getAttribute("flag");
	MQConfig vo = (MQConfig)request.getAttribute("vo");
	if(vo == null){
		vo = new MQConfig();
	}
	
	int alarmlevel = 0;
	Node node = (Node) PollingEngine.getInstance().getMqByID(
			vo.getId());
	String alarmmessage = "";
	if (node != null) {
		alarmlevel = SystemSnap.getNodeStatus(node); 
	}
	
	String ip = "";
	try{
	   ip = vo.getIpaddress().toString().replace(".","_");
	   }catch(Exception e){
	          e.printStackTrace();
	   }
	StringBuffer dataStr = new StringBuffer();
 	dataStr.append("连通;").append(Math.round(mqAvgPing)).append(";false;7CFC00\\n");
 	dataStr.append("未连通;").append(100-Math.round(mqAvgPing)).append(";false;FF0000\\n");
 	String avgdata = dataStr.toString();
%>
<html>
<head>
<meta http-equiv="Page-Enter" content="revealTrans(duration=x, transition=y)">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>

<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>


<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/css/common.css" charset="gb2312"/>
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 

<script language="javascript">	

  function doQuery()
  {  
     if(mainForm.key.value=="")
     {
     	alert("请输入查询条件");
     	return false;
     }
     mainForm.action = "<%=rootPath%>/network.do?action=find";
     mainForm.submit();
  }
  
  function doChange()
  {
     if(mainForm.view_type.value==1)
        window.location = "<%=rootPath%>/topology/network/index.jsp";
     else
        window.location = "<%=rootPath%>/topology/network/port.jsp";
  }

  function toAdd()
  {
      mainForm.action = "<%=rootPath%>/network.do?action=ready_add";
      mainForm.submit();
  } 
  
// 全屏观看
function gotoFullScreen() {
	parent.mainFrame.resetProcDlg();
	var status = "toolbar=no,height="+ window.screen.height + ",";
	status += "width=" + (window.screen.width-8) + ",scrollbars=no";
	status += "screenX=0,screenY=0";
	window.open("topology/network/index.jsp", "fullScreenWindow", status);
	parent.mainFrame.zoomProcDlg("out");
}
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
			menu =new Menu(td.id,td.id+"child",'dtu','100',show,my_on,my_off);
			menu.init();		
		}
	}
	setClass();
}

function setClass(){
	document.getElementById('MQDetailTitle-0').className='detail-data-title';
	document.getElementById('MQDetailTitle-0').onmouseover="this.className='detail-data-title'";
	document.getElementById('MQDetailTitle-0').onmouseout="this.className='detail-data-title'";
}


</script>
</head>
<body id="body" class="body" onload="initmenu();">
	<form id="mainForm" method="post" name="mainForm">
		<input type=hidden name="id">
        <input type=hidden name="orderflag">
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
														 <jsp:include page="/topology/includejsp/detail_content_top.jsp">
														 	<jsp:param name="contentTitle" value="MQ详细信息"/> 
														 </jsp:include>
													</td>
												</tr>												
												<tr>
													<td>
														<table>
									                   <tr bgcolor="#F1F1F1">
									                      <td width="10%" height="26" valign=middle align="right" nowrap  class=txtGlobal>&nbsp;名称:&nbsp;</td>
									                      <td width="20%" valign=middle align="left">
									                      <%=vo.getName()%>
									                      </td>
									                       <td width="15%" class=txtGlobal align="right" valign=middle nowrap>&nbsp;当前状态:&nbsp;</td>
									                      <td width=15% valign=middle ><img src="<%=rootPath%>/resource/<%=NodeHelper.getCurrentStatusImage(alarmlevel)%>">&nbsp;<%=NodeHelper.getStatusDescr(alarmlevel)%></td>
									                      <td width="15%" height="26" align=right valign=center nowrap   class=txtGlobal>&nbsp;IP地址:&nbsp;</td>
									                      <td width="15%">
									                      <%=vo.getIpaddress()%>
									                    </tr>
									                  </table>
												</td>
											</tr>
				                    </table>
				                    </td>
									</tr>
									<tr>
										<td>
											<table id="application-detail-data" class="application-detail-data">
												<tr>
											    	<td class="detail-data-header">
											    		<%=MQDetailTitleTable%>
											    	</td>
											   </tr>
		                                       <tr >
											    	<td width="85%">
											    		<table class="application-detail-data-body">
														<tr>
														<td valign=top>
												      		<table style="BORDER-COLLAPSE: collapse" cellpadding=0 rules=none width=100% align=center border=1 algin="center">
                    									        <tr>
		        							<td>
		        								<table id="application-detail-content-body" class="application-detail-content-body">
		        									<tr>
		        										<td>
		        											<table>
								               					<tr>
								     								<td width="80%" align="left" valign="top">
						        										<table>
				                    										<tr bgcolor="#ECECEC">
				                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;名称:</td>
				                      											<td width="35%"><%=vo.getName()%> </td>	
																				<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;消息管理器名称:</td>
				                      											<td width="35%"><%=vo.getManagername()%> </td>
				                    										</tr>
				                    										<tr >
				                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;IP地址:</td>
				                      											<td width="35%"><%=vo.getIpaddress()%> </td>
				                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;端口:</td>
				                      											<td width="35%"><%=vo.getPortnum()%> </td>
				                    										</tr>   
				                    										<%
				                    										Hashtable basicInfoHashtable = (Hashtable)request.getAttribute("basicInfoHashtable");
				                    										if(null != basicInfoHashtable && basicInfoHashtable.size() > 0){
				                    											String name = (String)basicInfoHashtable.get("name");
				                    											if("null".equals(name)){name = "";}
				                    											String version = (String)basicInfoHashtable.get("version");
				                    											if("null".equals(version)){version = "";}
				                    											String platform = (String)basicInfoHashtable.get("platform");
				                    											if("null".equals(platform)){platform = "";}
				                    											String qmname = (String)basicInfoHashtable.get("qmname");
				                    											if("null".equals(qmname)){qmname = "";}
				                    											String status = (String)basicInfoHashtable.get("status");
				                    											if("null".equals(status)){status = "";}
				                    											String listener = (String)basicInfoHashtable.get("listener");
				                    											if("null".equals(listener)){listener = "";}
				                    											String backlog = (String)basicInfoHashtable.get("backlog");
				                    											if("null".equals(backlog)){backlog = "";}
				                    										 %>
				                    										 <tr bgcolor="#ECECEC">
				                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;服务名称:</td>
				                      											<td width="35%"><%=name%> </td>
				                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;版本:</td>
				                      											<td width="35%"><%=version%> </td>
				                    										 </tr>   
				                    										 <tr >
				                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;平台:</td>
				                      											<td width="35%"><%=platform%> </td>
				                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;QMNAME:</td>
				                      											<td width="35%"><%=qmname%> </td>
				                    										 </tr>   
				                    										 <%
				                    										 }
				                    										  %>
				                    										<tr bgcolor="#ECECEC">
				                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;管理状态:</td>
																				<%if(vo.getMon_flag()==1){%>
				                      											<td width="35%">已监视</td>
																				<%}else{%>
																				<td width="35%">未监视</td>
																				<%}%>
																				<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;采集时间:</td>
																				<%
																				String collecttime = (String)request.getAttribute("collecttime");
																				if("null".equals(collecttime)){collecttime="";}
																				 %>
				                      											<td width="35%"><%=collecttime%> </td>
				                    										</tr> 
			                											</table>
								        							</td>
								        							
								        							<td width="19%" align="center" valign="middle" class=dashLeft>
									<table  bordercolor=#cedefa rules=none align=center border=0 cellpadding=0 cellspacing="0" width=100%>
										<tr>
											<td  align="left" valign="middle" class=dashLeft>
												
													<table style="BORDER-COLLAPSE: collapse" bordercolor=#cedefa
													rules=none align=center border=0 cellpadding=0
													cellspacing="0" width=100%>
													<tr bgcolor=#F1F1F1 height="26">
														<td align="center">
															当前连通率
														</td>
													</tr>
													<tr class="topNameRight">
														<td height="30" align="center">
														<div id="avgping">
				                                            <strong>You need to upgrade your Flash Player</strong>
				                                        </div>
				                                       
				                                        <script type="text/javascript"
							                                  src="<%=rootPath%>/include/swfobject.js"></script>
					                                   <script type="text/javascript">
						                                       var so = new SWFObject("<%=rootPath%>/amchart/ampie.swf", "ampie","165", "165", "8", "#FFFFFF");
						                                           so.addVariable("path", "<%=rootPath%>/amchart/");
						                                           so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/pingStatepie.xml"));
						                                           so.addVariable("chart_data","<%=avgdata%>");
						                                           so.write("avgping");
					                                   </script>
														</td>
													</tr>
													<tr>
														<td height="7" align=center>
															<img src="<%=rootPath%>/resource/image/Loading_2.gif"></td>
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
                    						<!--  
                    							 <tr bgcolor="#ECECEC">
                    							    <td height=20px></td><td></td>
				                    			 </tr> 
				                    	   -->      
				                    	   
				                    	   
                    						   <tr>
											<td>
												<table class="detail-data-body">
													<tr>
														<td align=center valign=top>
															<br>
															<table cellpadding="0" cellspacing="0" width=48%
																align=center>
																<tr>
																	<td width="50%" align="center">
																								<table width="100%" cellspacing="0"
																									cellpadding="0" align="center">
																									<tr>
																										<td width="100%" align="center">
																											<div id="flashcontent1">
																												<strong>You need to upgrade your
																													Flash Player</strong>
																											</div>
																											<script type="text/javascript">
																											var so = new SWFObject("<%=rootPath%>/flex/common_line.swf?title=连通曲线&tablename=mqping<%=ip%>", "common_line", "400", "250", "8", "#ffffff");
																											so.write("flashcontent1");
																										</script>
																										</td>
																									</tr>
																								</table>
																							</td>
																	<td width="48%" align="center">
																		<table width="100%" cellspacing="0" cellpadding="0"
																			align="center">
																			<tr>
																				<td width="100%" align="center">
																					<div id="flashcontent3">
																						<strong>You need to upgrade your Flash
																							Player</strong>
																					</div>
																									<script type="text/javascript">
																											var so = new SWFObject("<%=rootPath%>/flex/common_pie.swf?title=服务连通曲线&tablename=mqping<%=ip%>&category=thevalue&type=null", "common_line", "400", "250", "8", "#ffffff");
																											so.write("flashcontent3");
																									</script>
																				</td>
																			</tr>
																		</table>
																	</td>
																</tr>
															</table>
															<br>
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
				<td width=15% valign=top>
				   <jsp:include page="/include/mqtoolbar.jsp">
				            <jsp:param value="<%=vo.getId() %>" name="id" />
							<jsp:param value="<%=vo.getIpaddress() %>" name="ipaddress" />
				   </jsp:include>
				</td>
			</tr>
		</table>

	</form>
</BODY>
</HTML>