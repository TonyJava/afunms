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
<%@page import="com.afunms.config.dao.*"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.application.dao.*"%>
<%@page import="com.afunms.application.model.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="java.lang.*"%>
<%@page import="com.afunms.monitor.item.base.*"%>
<%@page import="com.afunms.monitor.executor.base.*"%>
<%@page import="com.afunms.application.manage.WeblogicManager"%>
<%@page import="com.afunms.application.dao.WeblogicConfigDao"%>
<%@page import="com.afunms.application.model.WeblogicConfig"%>
<%@page import="com.afunms.application.weblogicmonitor.*"%>
<%@page import="com.afunms.report.jfree.ChartCreator"%>
<%@page import="com.afunms.temp.dao.WeblogicDao" %>

<%
	String runmodel = PollingEngine.getCollectwebflag(); 
	 String rootPath = request.getContextPath();
	 String menuTable = (String)request.getAttribute("menuTable");
	 Hashtable hash = (Hashtable)request.getAttribute("hash");
	 String id= (String)request.getAttribute("id");
	 String chart1=null;
	 double weblogicping=0;
	 Hashtable pollingtime_ht=new Hashtable();
	 String lasttime;
	 String nexttime;

	 WeblogicManager wm=new WeblogicManager();
	 WeblogicConfigDao weblogicconfigdao=new WeblogicConfigDao();
     WeblogicConfig weblogicconf = null;
	 try{
     	weblogicconf = (WeblogicConfig) weblogicconfigdao.findByID(id);
     }catch(Exception e){
     	e.printStackTrace();
     }finally{
     	if(weblogicconfigdao != null){
     		weblogicconfigdao.close();
     	}
     }
	
	 pollingtime_ht=wm.getCollecttime(weblogicconf.getIpAddress());

	 if(pollingtime_ht!=null){
	 lasttime=(String)pollingtime_ht.get("lasttime");
	 nexttime=(String)pollingtime_ht.get("nexttime");
	 
	 }else{
	 lasttime=null;
	 nexttime=null;	 
	 }

	weblogicping=(double)wm.weblogicping(weblogicconf.getId());
	int percent1 = Double.valueOf(weblogicping).intValue();
	int percent2 = 100-percent1;

	DefaultPieDataset dpd = new DefaultPieDataset();
	dpd.setValue("可用时间",weblogicping);
	dpd.setValue("不可用时间",100 - weblogicping);
	chart1 = ChartCreator.createPieChart(dpd,"",130,130); 

	WeblogicNormal normalvalue=null;
	List normaldatalist =new ArrayList();
	List servletdatalist =new ArrayList();
	if("0".equals(runmodel)){
		//采集与访问是集成模式
		hash = (Hashtable)ShareData.getWeblogicdata().get(weblogicconf.getIpAddress());
	}else{
 		List labelList = new ArrayList();
		labelList.add("normalValue");
		//labelList.add("jdbcValue");
		//labelList.add("jdbcValue");
		//labelList.add("webappValue");
		//labelList.add("heapValue");
		//labelList.add("serverValue");
		labelList.add("servletValue");
		WeblogicDao weblogicDao = new WeblogicDao();
		hash = weblogicDao.getWeblogicData(labelList,id);
	}
	if(hash!=null){
		normaldatalist =(List) hash.get("normalValue");
	    servletdatalist = (List)hash.get("servletValue");
	 	if(normaldatalist!=null&&normaldatalist.size()>0){
		 	normalvalue = (WeblogicNormal)normaldatalist.get(0);
		}else{
		 	normalvalue = new WeblogicNormal();
		}
	}
	String dbPage = "servletdetail";
	String flag_1 = (String)request.getAttribute("flag");
	
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>

<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>


<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/css/common.css" charset="gb2312"/>
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="gb2312"></script>
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
	document.getElementById('weblogicDetailTitle-5').className='detail-data-title';
	document.getElementById('weblogicDetailTitle-5').onmouseover="this.className='detail-data-title'";
	document.getElementById('weblogicDetailTitle-5').onmouseout="this.className='detail-data-title'";
}

function refer(action){
		document.getElementById("id").value="<%=weblogicconf.getId()%>";
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}

</script>

</head>
<body id="body" class="body" onload="initmenu();">
<form method="post" name="mainForm">
<input type=hidden name="orderflag">
<input type=hidden name="id">
<div id="loading">
	<div class="loading-indicator">
	<img src="<%=rootPath%>/js/ext/lib/resources/extanim64.gif" width="32" height="32" style="margin-right: 8px;" align="middle" />Loading...</div>
</div>
<div id="loading-mask" style=""></div>
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
											<jsp:include page="/topology/includejsp/middleware_weblogic.jsp">
												<jsp:param name="IpAddress" value="<%= weblogicconf.getIpAddress()%>"/> 
												<jsp:param name="id" value="<%= weblogicconf.getId()%>"/>
												<jsp:param name="name" value="<%= weblogicconf.getAlias()%>"/>
												<jsp:param name="Portnum" value="<%= weblogicconf.getPortnum()%>"/> 
												<jsp:param name="DomainName" value="<%= normalvalue.getDomainName()%>"/> 
												<jsp:param name="DomainActive" value="<%= normalvalue.getDomainActive()%>"/> 
												<jsp:param name="DomainAdministrationPort" value="<%= normalvalue.getDomainAdministrationPort()%>"/> 
												<jsp:param name="DomainConfigurationVersion" value="<%=normalvalue.getDomainConfigurationVersion()%>"/> 
												<jsp:param name="lasttime" value="<%=lasttime%>"/>
												<jsp:param name="percent2" value="<%=nexttime%>"/>
												<jsp:param name="percent1" value="<%=percent1%>"/>
												<jsp:param name="percent2" value="<%=percent2%>"/>
												<jsp:param name="Mon_flag" value="<%=weblogicconf.getMon_flag()%>"/>
											</jsp:include>
										</td>
									</tr>
									<tr>
										<td>
											<table id="application-detail-data" class="application-detail-data">
												<tr>
											    	<td class="detail-data-header">
											    		<%=weblogicDetailTitleTable%>
											    	</td>
											   </tr>
		                                       <tr>
											    	<td>
											    		<table class="application-detail-data-body">
														<tr>
														<td>
												      		 <table  width="96%" align="center" >
                   <tr bgcolor="#ECECEC" height="28">
          			 <td align=center>&nbsp;</td>
          			 	<!--<td align=center>类型</td>-->
					 <td align=center>名称</td>
					 <!--<td align=center>Servlet名</td>-->
					 <td align=center>重装载次数</td>
					 <td align=center>调用次数</td>
					 <td align=center>最大容量</td>
					 <td align=center>总执行时间</td>
					 <td align=center>最高执行时间</td>
					 <td align=center>最低执行时间</td>
					 <td align=center>平均执行时间</td>
					 <td align=center>URL</td>
                </tr>
			<%
				for(int i=0;i<servletdatalist.size();i++){
					WeblogicServlet vo=(WeblogicServlet)servletdatalist.get(i);
										
			%>
          <tr bgcolor="#FFFFFF" <%=onmouseoverstyle%> height="28">
			<td align=center class="application-detail-data-body-list" style="width:5%;word-break: break-all; word-wrap:break-word;nowrap:false;"><%=i+1%></td>          
            		<!--<td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getServletRuntimeType()%></td>-->
            		<td align=center class="application-detail-data-body-list" style="width:40%;word-break: break-all; word-wrap:break-word;nowrap:false;">&nbsp;<%=vo.getServletRuntimeName()%></td>
            		<!--<td align=center style="width:30%;word-break: break-all; word-wrap:break-word;nowrap:false;">&nbsp;<%=vo.getServletRuntimeServletName()%></td>-->
            		<td align=center class="application-detail-data-body-list" style="width:5%;word-break: break-all; word-wrap:break-word;nowrap:false;">&nbsp;<%=vo.getServletRuntimeReloadTotalCount()%></td>
            		<td align=center class="application-detail-data-body-list" style="width:5%;word-break: break-all; word-wrap:break-word;nowrap:false;">&nbsp;<%=vo.getServletRuntimeInvocationTotalCount()%></td>
            		<td align=center class="application-detail-data-body-list" style="width:5%;word-break: break-all; word-wrap:break-word;nowrap:false;">&nbsp;<%=vo.getServletRuntimePoolMaxCapacity()%></td>
            		<td align=center class="application-detail-data-body-list" style="width:5%;word-break: break-all; word-wrap:break-word;nowrap:false;">&nbsp;<%=vo.getServletRuntimeExecutionTimeTotal()%></td>
            		<td align=center class="application-detail-data-body-list" style="width:5%;word-break: break-all; word-wrap:break-word;nowrap:false;">&nbsp;<%=vo.getServletRuntimeExecutionTimeHigh()%></td>
            		<td align=center class="application-detail-data-body-list" style="width:5%;word-break: break-all; word-wrap:break-word;nowrap:false;">&nbsp;<%=vo.getServletRuntimeExecutionTimeLow()%></td>
            		<td align=center class="application-detail-data-body-list" style="width:5%;word-break: break-all; word-wrap:break-word;nowrap:false;">&nbsp;<%=vo.getServletRuntimeExecutionTimeAverage()%></td>
            		<td align=center class="application-detail-data-body-list" style="width:20%;word-break: break-all; word-wrap:break-word;nowrap:false;">&nbsp;<%=vo.getServletRuntimeURL()%></td>
          </tr>
          <%}%>
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
															<jsp:include page="/include/weblogictoolbar.jsp">
																<jsp:param value="<%=weblogicconf.getId() %>" name="id" />
															    <jsp:param name="ip" value="<%= weblogicconf.getIpAddress()%>"/> 
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
  mainForm.action = "<%=rootPath%>/weblogic.do?action=sychronizeData&dbPage=<%=dbPage %>&id=<%=id %>&flag=<%=flag_1%>";
  mainForm.submit();
 });    
});
</script>
</BODY>
</HTML>