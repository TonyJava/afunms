<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc" %>

<%@page import="com.afunms.report.jfree.ChartCreator"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="org.jfree.data.general.DefaultPieDataset"%>
<%@page import="com.afunms.polling.node.Tomcat"%>
<%@page import="com.afunms.common.util.*"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.application.manage.TomcatManager"%>
<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.report.jfree.ChartCreator"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="org.jfree.data.general.DefaultPieDataset"%>
<%@page import="com.afunms.polling.node.Tomcat"%>
<%@page import="com.afunms.common.util.*"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.application.manage.TomcatManager"%>
<%@ page import="com.afunms.event.model.EventList"%>
<%@ page import="com.afunms.detail.service.tomcatInfo.TomcatInfoService"%>
<%@page import="com.afunms.initialize.*"%>
<%@page import="com.afunms.application.model.*"%>
<%@page import="com.afunms.inform.util.SystemSnap" %>
<%@page import="com.afunms.polling.base.*"%>
<%
  

   String menuTable = (String) request.getAttribute("menuTable");
    String rootPath = request.getContextPath();
    JBossConfig jBossConfig = (JBossConfig) request.getAttribute("jBossConfig");
    String _flag = (String)request.getAttribute("flag");
    Double pingAvg = Double.valueOf((String) request.getAttribute("pingAvg"));
    Double JVM = Double.valueOf((Double) request.getAttribute("JVM"));
    StringBuffer dataStr1 = new StringBuffer();
	dataStr1.append("连通;").append(Math.round(pingAvg)).append(";false;7CFC00\\n");
	dataStr1.append("未连通;").append(100-Math.round(pingAvg)).append(";false;FF0000\\n");
	String realdata = dataStr1.toString();
    String tmp =jBossConfig.getId()+"";
    
    int status = 0;
	Node node = (Node) PollingEngine.getInstance().getJBossByID(
			jBossConfig.getId());
    status = SystemSnap.getNodeStatus(node);
    
    CreateMetersPic cmp = new CreateMetersPic();
	String path = ResourceCenter.getInstance().getSysPath()
			+ "resource\\image\\dashboard1.png";
	cmp.createPic(tmp , JVM , path, "JVM利用率", "jboss_jvm");
    
    //Integer status = (Integer) request.getAttribute("status");
    String version = (String) request.getAttribute("version");
    String date = (String) request.getAttribute("date");
    String versionname = (String) request.getAttribute("versionname");
    String builton = (String) request.getAttribute("builton");
    String startdate = (String) request.getAttribute("startdate");
    String host = (String) request.getAttribute("host");
    String baselocation = (String) request.getAttribute("baselocation");
    String baselocationlocal = (String) request.getAttribute("baselocationlocal");
    String runconfig = (String) request.getAttribute("runconfig");
    String threads = (String) request.getAttribute("threads");
    String os = (String) request.getAttribute("os");
    String jvmversion = (String) request.getAttribute("jvmversion");
    String jvmname = (String) request.getAttribute("jvmname");

    String ajp = (String) request.getAttribute("ajp");
    String ajp_maxthreads = (String) request.getAttribute("ajp_maxthreads");
    String ajp_thrcount = (String) request.getAttribute("ajp_thrcount");
    String ajp_thrbusy = (String) request.getAttribute("ajp_thrbusy");
    String ajp_maxtime = (String) request.getAttribute("ajp_maxtime");
    String ajp_processtime = (String) request.getAttribute("ajp_processtime");
    String ajp_requestcount = (String) request.getAttribute("ajp_requestcount");
    String ajp_errorcount = (String) request.getAttribute("ajp_errorcount");
    String ajp_bytereceived = (String) request.getAttribute("ajp_bytereceived");
    String ajp_bytessent = (String) request.getAttribute("ajp_bytessent");
%>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script type="text/javascript"
			src="<%=rootPath%>/include/swfobject.js"></script>
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css"
			charset="utf-8" />
		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/js/ext/css/common.css" charset="utf-8" />
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js"
			charset="utf-8"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="utf-8"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js"
			charset="utf-8"></script>

		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />


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

function refer(action){
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action +"&id=<%=jBossConfig.getId()%>";
		mainForm.submit();
		
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
</script>
<script type="text/javascript">
function setClass(){
	document.getElementById('JBOSSDetailTitle-0').className='detail-data-title';
	document.getElementById('JBOSSDetailTitle-0').onmouseover="this.className='detail-data-title'";
	document.getElementById('JBOSSDetailTitle-0').onmouseout="this.className='detail-data-title'";
		}
</script>
<script type="text/javascript">

</script>

</head>
<body id="body" class="body" onload="initmenu();">
	<form id="mainForm" method="post" name="mainForm">
		<input type=hidden name="id">

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
														 	<jsp:param name="contentTitle" value="JBOSS详细信息"/> 
														 </jsp:include>
													</td>
												</tr>												
												<tr>
													<td>
														<table>
									                   <tr bgcolor="#F1F1F1">
									                      <td width="10%" height="26" valign=middle align="right" nowrap  class=txtGlobal>&nbsp;名称:&nbsp;</td>
									                      <td width="20%" valign=middle align="left">
									                      <%=jBossConfig.getAlias()%>
									                      </td>
									                      <td width="15%" class=txtGlobal align="right" valign=middle nowrap>&nbsp;状态:&nbsp;</td>
									                      <td width=15% valign=middle ><img src="<%=rootPath%>/resource/<%=NodeHelper.getCurrentStatusImage(status)%>" border="0" >&nbsp;&nbsp;<%=NodeHelper.getStatusDescr(status)%></td>
									                      <td width="15%" height="26" align=right valign=center nowrap   class=txtGlobal>&nbsp;IP地址:&nbsp;</td>
									                      <td width="15%">
									                      <%=jBossConfig.getIpaddress()%>
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
											    		<%=JBOSSDetailTitleTable%>
											    	</td>
											   </tr>
		                                       <tr >
											    	<td width="85%">
											    		<table class="application-detail-data-body">
														<tr>
														<td valign=top>
												      		<table style="BORDER-COLLAPSE: collapse" cellpadding=0 rules=none width=100% align=center border=1 algin="center">
								                    				 <tr>
                                              									        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;名称:</td>
                                              											<td width="35%"><%=jBossConfig.getAlias() %></td>
                        													            <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;IP地址:</td>
                                              											<td ><%=jBossConfig.getIpaddress() %> </td>         										
                    										                        </tr>
                    										                        <tr bgcolor="#ECECEC">
                    										                            <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;端口号:</td>
                      											                        <td colspan=3><%=jBossConfig.getPort() %> </td>
                    										                        </tr>   
                    										                        <tr>
                                                                                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;JBOSS版本:</td>
                      											                        <td width="35%"><%=version%></td>
                                                                                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;版本时间:</td>
                    											                        <td ><%=date%></td>
                    										                        </tr>
                                                                                    <tr bgcolor="#ECECEC">
                                                                                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;JBOSS版本名称:</td>
                      											                        <td width="35%"><%=versionname%></td>
                                                                                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;版本时间:</td>
                    											                        <td ><%=builton%></td>
                                                                                    </tr>
                                                                                    <tr>
                                                                                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;服务启动时间:</td>
                      											                        <td width="35%"><%=startdate%></td>
                                                                                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;主机名:</td>
                    											                        <td ><%=host%></td>
            										                                </tr>
                    										                        <tr bgcolor="#ECECEC">
                      											                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;安装目录:</td>
                      											                        <td width="35%"><%=baselocation%></td>
                    											                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;总安装目录:</td>
                      											                        <td ><%=baselocationlocal%></td>
                                                                                    </tr>
                                                                                    <tr>
                                                                                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;运行配置:</td>
                      											                        <td width="35%"><%=runconfig%></td>
                                                                                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;#线程:</td>
                      											                        <td><%=threads%></td>
                                                                                    </tr>
                                                                                    <tr bgcolor="#ECECEC" >
                                                                                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;操作系统:</td>
                      											                        <td width="35%"><%=os%> </td>
                                                                                        <td></td>
                                                                                        <td></td>
                                                                                    </tr>
                                                                                    <tr>
                                                                                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;JVM 版本号:</td>
                      											                        <td width="35%"><%=jvmversion%> </td>
                                                                                        <td width="15%" height="26" align="left" nowrap class=txtGlobal></td>
                      											                        <td ></td>
                                                                                    </tr>
                                                                                    <tr bgcolor="#ECECEC">
                                                                                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;JVM 版本名称:</td>
                      											                        <td ><%=jvmname%> </td>
                                                                                        <td></td>
                                                                                        <td></td>
                                                                                    </tr>
                                                                                    <tr>
                                                                                    <td  align="left" colspan=4>
                                                                                <table>
                                                                                    <tr >
                                                                                        <td height="26" colspan="7" valign="center" nowrap class=txtGlobal><div align="left" class="txtGlobalBigBold">&nbsp;<%=ajp %></div></td>
                    				                                                </tr>
            				                                                        <tr bgcolor="#ECECEC">
                      							                                        <td width="13%" height="26" align="left" nowrap class=txtGlobal>&nbsp;最大线程数:</td>
                      							                                        <td width="20%"><%=ajp_maxthreads %></td>
											                                            <td width="13%" height="26" align="left" nowrap class=txtGlobal>&nbsp;当前线程数:</td>
                      							                                        <td width="20%"><%=ajp_thrcount %> </td>
                      							                                        <td width="14%" height="26" align="left" nowrap class=txtGlobal>&nbsp;当前线程忙:</td>
                      							                                        <td width="20%"><%=ajp_thrbusy %> </td>
                    				                                                </tr>
                    				                                                <tr >
                      							                                        <td width="13%" height="26" align="left" nowrap class=txtGlobal>&nbsp;最大处理时间:</td>
                      							                                        <td width="20%"><%=ajp_maxtime%></td>
											                                            <td width="13%" height="26" align="left" nowrap class=txtGlobal>&nbsp;处理时间:</td>
                      							                                        <td width="20%"><%=ajp_processtime %> </td>
                      							                                        <td width="14%" height="26" align="left" nowrap class=txtGlobal>&nbsp;要求数:</td>
                      							                                        <td width="20%"><%=ajp_requestcount %> </td>
                    				                                                </tr>
                    				                                                <tr bgcolor="#ECECEC">
                      							                                        <td width="13%" height="26" align="left" nowrap class=txtGlobal>&nbsp;错误数:</td>
                      							                                        <td width="20%"><%=ajp_errorcount %></td>
											                                            <td width="13%" height="26" align="left" nowrap class=txtGlobal>&nbsp;接收字节大小:</td>
                      							                                        <td width="20%"><%=ajp_bytereceived %> </td>
                      							                                        <td width="14%" height="26" align="left" nowrap class=txtGlobal>&nbsp;发送字节大小:</td>
                      							                                        <td width="20%"><%=ajp_bytessent %> </td> 
                    				                                                </tr>
                                                                                </table>
                                                                            </td>
                                                                            </tr>
                    									    </table>       
												        </td>
												        
												        
												<td width=20% align="center">
									<table class="container-main-service-detail-tool">
										<tr>
											<td>
												<table style="BORDER-COLLAPSE: collapse" 
													rules=none align=center border=1 cellpadding=0
													cellspacing="0" width=100%>
													<tr bgcolor=#F1F1F1 height="26">
														<td align="center">
															今日连通率
														</td>
													</tr>
													<tr>
														<td align=center>
															<div id="avgping">
																<strong>You need to upgrade your Flash Player</strong>
															</div>
															<script type="text/javascript"
																src="<%=rootPath%>/include/swfobject.js"></script>
															<script type="text/javascript">
						                                       var so = new SWFObject("<%=rootPath%>/amchart/ampie.swf", "ampie","160", "155", "8", "#FFFFFF");
						                                           so.addVariable("path", "<%=rootPath%>/amchart/");
						                                           so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/pingStatepie.xml"));
						                                           so.addVariable("chart_data","<%=realdata%>");
						                                           so.write("avgping");
					                                  </script>
														</td>
													</tr>
													<tr>
														<td height="7" align=center>
															<img src="<%=rootPath%>/resource/image/Loading_2.gif">
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>

											<td>
												<table style="BORDER-COLLAPSE: collapse" 
													rules=none align=center border=1 cellpadding=0
													cellspacing="0" width=100%>
													<tr bgcolor=#F1F1F1 height="26">
														<td align="center">
															JVM平均利用率
														</td>
													</tr>
													<tr height=160>
														<td align="center">
															<img src="<%=rootPath%>/resource/image/jfreechart/reportimg/<%=tmp%>jboss_jvm.png">
														</td>
													</tr>
													<tr height="7">
														<td align=center>
															&nbsp;
															<img src="<%=rootPath%>/resource/image/Loading.gif">
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
										</tr>

										
			
											  <tr>
											  	<td colspan=2>											  		
															&nbsp;													  													  		
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
				<td width=15% valign=top><jsp:include page="/include/jbosstoolbar.jsp"><jsp:param value="<%=jBossConfig.getId() %>" name="id" />
																<jsp:param value="<%=jBossConfig.getIpaddress() %>" name="ipaddress" />
															</jsp:include>
				</td>
			</tr>
		</table>

	</form>
	
		<script>			
Ext.onReady(function()
{  
	    Ext.get("process").on("click",function(){
  Ext.MessageBox.wait('数据加载中，请稍后.. ');   
  mainForm.action = "<%=rootPath%>/jboss.do?action=sychronizeData&id=<%=jBossConfig.getId()%>&flag=<%=_flag%>";
  mainForm.submit();
 });    
});
</script>
</BODY>
</HTML>