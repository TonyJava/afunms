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
<%
  

  String rootPath = request.getContextPath();
   String menuTable = (String)request.getAttribute("menuTable");
	String runmodel = PollingEngine.getCollectwebflag();
	String tmp = request.getParameter("id");
	String avgpingStr = (String)request.getAttribute("avgpingcon") == null ? "0"
			: (String)request.getAttribute("avgpingcon");
	double avgpingcon = Double.parseDouble(avgpingStr.replace("%",""));
	String avgjvm = (String)request.getAttribute("avgjvmcon") == null ? "0"
			: (String)request.getAttribute("avgjvmcon");
	double avgjvmcon = Double.parseDouble(avgjvm.replace("%",""));
	double jvm_memoryuiltillize = 0;
	double tomcatping = 0;
	String lasttime;
	String nexttime;
	String jvm = "";
	String jvm_utilization = "";
	Hashtable data_ht = new Hashtable();
	Hashtable pollingtime_ht = new Hashtable();
	TomcatManager tm = new TomcatManager();
	Tomcat tomcat = (Tomcat) PollingEngine.getInstance().getTomcatByID(
			Integer.parseInt(tmp));
	Hashtable hash_data = null;
	if ("0".equals(runmodel)) {
		//采集与访问是集成模式
		System.out.println("----------tomcat采集与访问：集成模式-------------");
		Hashtable tomcatvalues = ShareData.getTomcatdata();
		//System.out.println(">>>>>>>>>>>."+tomcatvalues);
		if (tomcatvalues != null && tomcatvalues.containsKey(tomcat.getIpAddress())) {
			data_ht = (Hashtable) tomcatvalues.get(tomcat.getIpAddress());
			//System.out.println(data_ht+"---"+data_ht.get("server"));
		}
	} else {
		//采集与访问分离模式
		System.out.println("----------tomcat采集与访问：分离模式-------------");
		TomcatInfoService tomcatInfoService = new TomcatInfoService();
		data_ht = tomcatInfoService.getTomcatDataHashtable(tmp);
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
	int percent1 = Double.valueOf(tomcatping).intValue();
	int percent2 = 100 - percent1;
	String cpuper = "0";
	cpuper = jvm_utilization;

	CreateMetersPic cmp = new CreateMetersPic();
	String path = ResourceCenter.getInstance().getSysPath()
			+ "resource\\image\\dashboard1.png";
	cmp.createPic(tmp, avgjvmcon, path, "JVM利用率", "tomcat_jvm");

	StringBuffer dataStr = new StringBuffer();
	dataStr.append("连通;").append(Math.round(avgpingcon)).append(
			";false;7CFC00\\n");
	dataStr.append("未连通;").append(100 - Math.round(avgpingcon)).append(
			";false;FF0000\\n");
	String avgdata = dataStr.toString();
	if(data_ht.get("server")!=null){
		String info = (String)data_ht.get("server");
		String[] seperator = info.split(",");
		tomcat.setVersion(seperator[0]);
		tomcat.setJvmversion(seperator[1]);
		tomcat.setJvmvender(seperator[2]);
		tomcat.setOs(seperator[3]);
		tomcat.setOsversion(seperator[4]);
		}
	if(tomcat.getLastAlarm()==null||tomcat.getLastAlarm()==""){
		tomcat.setLastAlarm("无");
	}

      String flag_1 = (String)request.getAttribute("flag");
%>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/css/common.css" charset="gb2312"/>
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="gb2312"></script>

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
			menu =new Menu(td.id,td.id+"child",'dtu','100',show,my_on,my_off);
			menu.init();		
		}
	}
	setClass();
}
</script>
<script type="text/javascript">
function setClass(){
	document.getElementById('tomDetailTitle-0').className='detail-data-title';
	document.getElementById('tomDetailTitle-0').onmouseover="this.className='detail-data-title'";
	document.getElementById('tomDetailTitle-0').onmouseout="this.className='detail-data-title'";
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
								<table id="container-main-service-detail" class="container-main-service-detail">
									<tr>
										<td>
											<table id="service-detail-content" class="service-detail-content">
												
												<tr>
					<td>
						<jsp:include page="/topology/includejsp/middleware_tomcat.jsp">
							<jsp:param name="tmp" value="<%=tmp%>"/> 
							<jsp:param name="avgpingcon" value="<%=avgpingcon%>"/> 
							<jsp:param name="avgjvmcon" value="<%=avgjvmcon%>"/> 
						</jsp:include>
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
								</table>
							</td>

						</tr>
					</table>
				</td>
				<td valign=top  width=200px>
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