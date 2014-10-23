<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>

<%@page import="com.afunms.report.jfree.ChartCreator"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="org.jfree.data.general.DefaultPieDataset"%>
<%@page import="com.afunms.polling.node.Tomcat"%>
<%@page import="com.afunms.common.util.*"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.application.manage.TomcatManager"%>
<%
    String rootPath = request.getContextPath();
    String tmp = request.getParameter("id");
    String menuTable = (String) request.getAttribute("menuTable");
    Hashtable hash = (Hashtable) request.getAttribute("pingcon");
    String pingavg = (String) hash.get("avgpingcon");
    String avgpingcon = pingavg.replaceAll("%", "");
    Hashtable avgjvm = (Hashtable) request.getAttribute("avgjvm");
    String avgjvmcon = (String) avgjvm.get("avg_tomcat_jvm");
    avgjvmcon = avgjvmcon.replaceAll("%", "");
    double jvm_memoryuiltillize = 0;
    double tomcatping = 0;

    String lasttime;
    String nexttime;
    String jvm = "";
    String jvm_utilization = "";

    Hashtable data_ht = new Hashtable();
    Hashtable pollingtime_ht = new Hashtable();
    TomcatManager tm = new TomcatManager();

    Tomcat tomcat = (Tomcat) PollingEngine.getInstance().getTomcatByID(Integer.parseInt(tmp));
    Hashtable tomcatvalues = ShareData.getTomcatdata();
    if (tomcatvalues != null && tomcatvalues.size() > 0) {
        data_ht = (Hashtable) tomcatvalues.get(tomcat.getIpAddress()+":"+tomcat.getId());
    }

    tomcatping = (double) tm.tomcatping(tomcat.getId());
    pollingtime_ht = tm.getCollecttime(tomcat.getIpAddress());

    if (pollingtime_ht != null) {
        lasttime = (String) pollingtime_ht.get("lasttime");
        nexttime = (String) pollingtime_ht.get("nexttime");

    } else {
        lasttime = null;
        nexttime = null;
    }
    if (data_ht != null) {
        if (data_ht.get("jvm") != null)
            jvm = (String) data_ht.get("jvm");
        if (data_ht.get("jvm_utilization") != null) {
            jvm_utilization = (String) data_ht.get("jvm_utilization");
        }

    } else {
        jvm = "";
    }
    double totalmemory = 0;
    double maxmemory = 0;
    double freememory = 0;
    StringBuffer info = new StringBuffer(100);
    if (jvm != null && jvm.trim().length() > 0) {
        String[] temjvm = jvm.split(",");
        freememory = Double.parseDouble(temjvm[0].trim());
        totalmemory = (double) Double.parseDouble(temjvm[1].trim());
        maxmemory = (double) Double.parseDouble(temjvm[2].trim());
        info.append("可用内存：");
        info.append(freememory);
        info.append("MB,总内存：");
        info.append(totalmemory);
        info.append("MB,最大内存：");
        info.append(maxmemory);
        info.append("MB");
        System.out.println(totalmemory + "===" + freememory);
        jvm_memoryuiltillize = (totalmemory - freememory) * 100 / totalmemory;
    }

    String chart1 = null, chart2 = null, chart3 = null, responseTime = null;

    DefaultPieDataset dpd = new DefaultPieDataset();
    dpd.setValue("可用时间", tomcatping);
    dpd.setValue("不可用时间", 100 - tomcatping);
    chart1 = ChartCreator.createPieChart(dpd, "", 120, 120);
    int percent1 = Double.valueOf(tomcatping).intValue();
    int percent2 = 100 - percent1;
    String cpuper = "0";
    cpuper = jvm_utilization;

    chart2 = ChartCreator.createMeterChart(Math.rint(jvm_memoryuiltillize), "", 100, 100);

    chart3 = ChartCreator.createMeterChart(100 - jvm_memoryuiltillize, "", 100, 100);
    String flag_1 = (String) request.getAttribute("flag");
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
			charset="gb2312" />
		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/js/ext/css/common.css" charset="gb2312" />
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js"
			charset="gb2312"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js"
			charset="gb2312"></script>

		<link
			href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css"
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
	document.getElementById('tomDetailTitle-2').className='detail-data-title';
	document.getElementById('tomDetailTitle-2').onmouseover="this.className='detail-data-title'";
	document.getElementById('tomDetailTitle-2').onmouseout="this.className='detail-data-title'";			
		}
</script>
		<script type="text/javascript">
function event(){
	mainForm.action = "<%=rootPath%>/tomcat.do?action=event"+"&flag=<%=flag%>";
    mainForm.submit();
}
function alarm(){
	var id=<%=request.getAttribute("id")%>
	mainForm.action = "<%=rootPath%>/tomcat.do?action=alarm&id="+id+"&flag=<%=flag%>";
    mainForm.submit();
}
function detail(){
	var id=<%=request.getAttribute("id")%>
	mainForm.action = "<%=rootPath%>/tomcat.do?action=tomcat_jvm&id="+id+"&flag=<%=flag%>";
    mainForm.submit();
}
function app(){
	var id=<%=request.getAttribute("id")%>
	mainForm.action = "<%=rootPath%>/tomcat.do?action=app&id="+id+"&flag=<%=flag%>";
    mainForm.submit();
}
function sys(){
	var id=<%=request.getAttribute("id")%>
	mainForm.action = "<%=rootPath%>/tomcat.do?action=sys&id="+id+"&flag=<%=flag%>";
    mainForm.submit();
}
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
								<td><%=menuTable%></td>
							</tr>
						</table>
					</td>
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-service-detail">
									<table id="container-main-service-detail"
										class="container-main-service-detail">
										<tr>
											<td>
												<table id="service-detail-content"
													class="service-detail-content">
													<tr>
		<td>
			<jsp:include page="/topology/includejsp/detail_content_top.jsp">
				<jsp:param name="contentTitle" value="Tomcat信息" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<td>
			<table width="100%"
				background="<%=rootPath%>/common/images/right_t_02.jpg"
				cellspacing="0" cellpadding="100">
				<tr>
					<td height="29" style="padding-left: 20px;">
						名称:<%=tomcat.getAlias()%>
					</td>
					<td height="29">
						IP地址:<%=tomcat.getIpAddress()%>
					</td>
					<td height="29">
						端口:<%=tomcat.getPort()%>
					</td>
					<td height="29">
						状态:
						<img
							src="<%=rootPath%>/resource/<%=NodeHelper.getCurrentStatusImage(tomcat.getStatus())%>">
					</td>
				</tr>
					<tr bgcolor="#B0C4DE" height="15" bgcolor="#B0C4DE">
				<td colspan="4"> </td>
				</tr>
				<tr height="0"  bgcolor="#B0C4DE">
				<td colspan="4">
						<%=tomDetailTitleTable%>
					</td>
				</tr>
			</table>
		</td>
	</tr>
													<tr>
														<td>
															<table class="application-detail-data-body">
																<tr bgcolor="#F1F1F1" height="29" align="left">
																	
																	<td align="left" width="25%">
																		名称
																	</td>
																	<td align="left" width="25%">
																		最大会话数
																	</td>
																	<td align="left" width="25%">
																		活动会话数
																	</td>
																	<td align="left" width="25%">
																		会话数
																	</td>
																</tr><%
																    Vector managerVevor = (Vector) data_ht.get("manager");
																    String[] arr = null;
																    String managerString = null;
																    for (int i = 0; i < managerVevor.size(); i++) {
																        managerString = (String) managerVevor.get(i);
																        arr = managerString.split(":");
																%>
															
																<tr height="29" align="left">
																	<td>
																		<%=arr[0]%>
																	</td>
																	<td>
																		<%=arr[1]%>
																	</td>
																	<td>
																		<%=arr[2]%>
																	</td>
																	<td>
																		<%=arr[3]%>
																	</td>
																</tr>
																<%
																    }
																%>
																<tr bgcolor="#F1F1F1" height="29">
																	<td colspan=4>
																		VM参数
																	</td>
																</tr>
																<%
																    String InputArguments[] = (String[]) data_ht.get("InputArguments");
																    String InputArgumentsString = "";
																    for (int i = 0; i < InputArguments.length; i++) {
																        InputArgumentsString = InputArgumentsString + ";" + InputArguments[i];
																    }
																%>
																<tr>
																	<td colspan=4 height=80 width=800 style="word-break:break-all"><%=InputArgumentsString%></td>
																</tr>
																
																<tr bgcolor="#F1F1F1" height="29">
																	<td colspan=4>
																		类路径
																	</td>
																</tr>
																<tr>
																	<td colspan=4><%= data_ht.get("classPath")%></td>
																</tr>
																<tr bgcolor="#F1F1F1" height="29">
																	<td colspan=4 >
																		库路径
																	</td>
																</tr>
																<tr>
																	<td colspan=4 height=40 width=800 style="word-break:break-all"><%= data_ht.get("libraryPath")%></td>
																</tr>

															</table>
														</td>
													</tr>
													<tr>
														<td>
															<table id="application-detail-content-footer"
																class="application-detail-content-footer">
																<tr>
																	<td>
																		<table width="100%" border="0" cellspacing="0"
																			cellpadding="0">
																			<tr>
																				<td align="left" valign="bottom">
																					<img
																						src="<%=rootPath%>/common/images/right_b_01.jpg"
																						width="5" height="12" />
																				</td>
																				<td></td>
																				<td align="right" valign="bottom">
																					<img
																						src="<%=rootPath%>/common/images/right_b_03.jpg"
																						width="5" height="12" />
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
					<td valign=top width="200px">
						<jsp:include page="/include/tomcattoolbar.jsp">
							<jsp:param value="<%=tomcat.getId()%>" name="id" />
							<jsp:param value="<%=tomcat.getIpAddress()%>" name="ip" />
						</jsp:include>
					</td>
				</tr>
			</table>

		</form>
		<script>
		Ext.onReady(function()
		{  
		
		setTimeout(function(){
			        Ext.get('loading').remove();
			        Ext.get('loading-mask').fadeOut({remove:true});
			    }, 250);
			    Ext.get("process").on("click",function(){
		  
		  Ext.MessageBox.wait('数据加载中，请稍后.. ');   
		  mainForm.action = "<%=rootPath%>/tomcat.do?action=sychronizeData&id=<%=tomcat.getId()%>&flag=<%=flag_1%>";
		  mainForm.submit();
		 });    
		});
	</script>
	</BODY>
</HTML>