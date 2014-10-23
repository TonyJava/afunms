<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>
<%@page import="com.afunms.inform.util.SystemSnap" %>
<%@page import="com.afunms.polling.base.*"%>
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

<%
 	String rootPath = request.getContextPath();
 	String menuTable = (String)request.getAttribute("menuTable");
 	Double mqAvgPing = (Double)request.getAttribute("mqAvgPing");
	int percent1 = Double.valueOf(mqAvgPing).intValue();
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
	document.getElementById('MQDetailTitle-2').className='detail-data-title';
	document.getElementById('MQDetailTitle-2').onmouseover="this.className='detail-data-title'";
	document.getElementById('MQDetailTitle-2').onmouseout="this.className='detail-data-title'";
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
									                       <td width="15%" class=txtGlobal align="right" valign=middle nowrap>&nbsp;状态:&nbsp;</td>
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
									<table id="application-detail-data" class="application-detail-data">
                                       <tr>
									    	<td>
									    		<table class="application-detail-data-body">
									 				  <tr>
														<td>
												      		<table width="96%" align="center">
																<tr>
																	<td align=center>
																		<table width=80%>
		  																	<tr bgcolor="#FFFFFF">
		  																		<td width=10% class="detail-data-body-title" style="height:29;align:center"><strong>序号</strong></td>
																			    <td width=20% class="detail-data-body-title" style="height:29;align:center"><strong>名称</strong></td>
																			    <td width=30% class="detail-data-body-title" style="height:29;align:center"><strong>监听器支持的并发连接请求的数量</strong></td>
																			    <td width=14% class="detail-data-body-title" style="height:29;align:center"><strong>端口</strong></td>
																			    <td width=15% class="detail-data-body-title" style="height:29;align:center"><strong>状态</strong></td>
		    																</tr>
										     								<%
										     								Hashtable basicInfoHashtable = (Hashtable)request.getAttribute("basicInfoHashtable");
				                    										if(null != basicInfoHashtable && basicInfoHashtable.size() > 0){
				                    											String statu = (String)basicInfoHashtable.get("statu");
				                    											if("null".equals(statu)){statu = "";}
				                    											String listener = (String)basicInfoHashtable.get("listener");
				                    											if("null".equals(listener)){listener = "";}
				                    											String backlog = (String)basicInfoHashtable.get("backlog");
				                    											if("null".equals(backlog)){backlog = "";}
				                    											String port = (String)basicInfoHashtable.get("port");
				                    											if("null".equals(port)){port = "";}
				                    										 %>  
																			  <tr  class="othertr" <%=onmouseoverstyle%> >
																			    <td height="28" class="detail-data-body-list">1</td>
																			    <td height="28" class="detail-data-body-list"><%=listener%></td>
																			    <td height="28" class="detail-data-body-list"><%=backlog%></td>
																			    <td height="28" class="detail-data-body-list"><%=port%></td>
																			    <td height="28" class="detail-data-body-list"><%=statu%></td>
																			   </tr>
																			<%
																			}
																			%>      														
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