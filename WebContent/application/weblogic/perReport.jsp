<%@ page language="java" contentType="text/html;charset=gb2312"%>
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
<%@page import="com.afunms.monitor.item.base.*"%>
<%@page import="com.afunms.monitor.executor.base.*"%>
<%@page import="com.afunms.application.manage.WeblogicManager"%>
<%@page import="com.afunms.application.dao.WeblogicConfigDao"%>
<%@page import="com.afunms.application.model.WeblogicConfig"%>
<%@page import="com.afunms.application.weblogicmonitor.*"%>
<%@page import="com.afunms.report.jfree.ChartCreator"%>
<%@page import="com.afunms.temp.dao.WeblogicDao" %>
<html>
<head>
<%
  
  String rootPath = request.getContextPath(); 
  String id = request.getParameter("id");
  String menuTable = (String)request.getAttribute("menuTable");
  //String flag = (String)request.getAttribute("flag");
  
  double jvm_memoryuiltillize=0;
  double tomcatping=0;
     String runmodel = PollingEngine.getCollectwebflag(); 
	 Hashtable hash = null;
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
	

		WeblogicNormal normalvalue=new WeblogicNormal() ;
		List normaldatalist =new ArrayList();
		List queuedatalist =new ArrayList();
		List jdbcdatalist = new ArrayList();
		List webappdatalist =new ArrayList();
		List heapdatalist =new ArrayList();
		List serverdatalist =new ArrayList();
		
	if("0".equals(runmodel)){
   		//采集与访问是集成模式
   		hash = (Hashtable)ShareData.getWeblogicdata().get(weblogicconf.getIpAddress());
  	}else{
		List labelList = new ArrayList();
		labelList.add("normalValue");
		labelList.add("queueValue");
		labelList.add("jdbcValue");
		labelList.add("webappValue");
		labelList.add("heapValue");
		labelList.add("serverValue");
		WeblogicDao weblogicDao = new WeblogicDao();  
		hash = weblogicDao.getWeblogicData(labelList,id);
	}
	if(hash!=null){
	 	normaldatalist =(List) hash.get("normalValue");
	 	queuedatalist =(List) hash.get("queueValue");
		jdbcdatalist = (List)hash.get("jdbcValue");
		webappdatalist = (List)hash.get("webappValue");
		heapdatalist =(List) hash.get("heapValue");
		serverdatalist = (List)hash.get("serverValue");
	}
  	if(normaldatalist!=null&&normaldatalist.size()>0){
		normalvalue = (WeblogicNormal)normaldatalist.get(0);
	}else{
 		normalvalue = new WeblogicNormal();
	}
	String dbPage = "detail";
	String flag_1 = (String)request.getAttribute("flag");
%>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

<title>weblogic性能报表</title>
<script type='text/javascript' src='/afunms/dwr/interface/DWRUtil.js'></script>
<script type='text/javascript' src='/afunms/dwr/engine.js'></script>
<script type='text/javascript' src='/afunms/dwr/util.js'></script>
<script language="javascript" src="/afunms/js/tool.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 
<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script> 
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="utf-8"></script>
<script language="JavaScript" type="text/JavaScript">
function ping_word_report()
{
	mainForm.action = "<%=rootPath%>/weblogic.do?action=downloadReport&flag=per&str=1&id=<%=id%>";
	mainForm.submit();
}
function ping_excel_report()
{
	mainForm.action = "<%=rootPath%>/weblogic.do?action=downloadReport&flag=per&str=0&id=<%=id%>";
	mainForm.submit();
}
function ping_pdf_report()
{
	mainForm.action = "<%=rootPath%>/weblogic.do?action=downloadReport&flag=per&str=2&id=<%=id%>";
	mainForm.submit();
}
function ping_ok()
{
	var starttime = document.getElementById("mystartdate").value;
	var endtime = document.getElementById("mytodate").value;
	mainForm.action = "<%=rootPath%>/tomcat.do?action=showPingReport&id=<%=id%>&startdate="+starttime+"&todate="+endtime;
	mainForm.submit();
}
function ping_cancel()
{
window.close();
}
function query(){
  	var startdate = mainForm.startdate.value;
  	var todate = mainForm.todate.value;      
  	var oids ="";
  	var checkbox = document.getElementsByName("checkbox");
 		for (var i=0;i<checkbox.length;i++){
 			if(checkbox[i].checked==true){
 				if (oids==""){
 					oids=checkbox[i].value;
 				}else{
 					oids=oids+","+checkbox[i].value;
 				}
 			}
 		}
 	if(oids==null||oids==""){
 		alert("请至少选择一个设备");
 		return;
 	}
 	window.open ("<%=rootPath%>/hostreport.do?action=downloadmultihostreport&startdate="+startdate+"&todate="+todate+"&ids="+oids, "newwindow", "height=500, width=600, toolbar= no, menubar=no, scrollbars=yes, resizable=yes, location=yes, status=yes")    	      
}

function openwin(str,operate,ip) 
{	
  var startdate = mainForm.startdate.value;
  var todate = mainForm.todate.value;
  window.open ("<%=rootPath%>/hostreport.do?action="+operate+"&startdate="+startdate+"&todate="+todate+"&ipaddress="+ip+"&str="+str+"&type=host", "newwindow", "height=500, width=600, toolbar= no, menubar=no, scrollbars=yes, resizable=yes, location=yes, status=yes") 
}
function changeOrder(para){
	mainForm.orderflag.value = para;
	mainForm.action="<%=rootPath%>/netreport.do?action=netping"
  	mainForm.submit();
}
  Ext.onReady(function()
{  

setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
	
 Ext.get("process").on("click",function(){
     
     //if(chk1&&chk2&&chk3)
     //{
     
        Ext.MessageBox.wait('数据加载中，请稍后.. '); 
        //msg.style.display="block";
	mainForm.action="<%=rootPath%>/hostreport.do?action=hostcpu";
	mainForm.submit();        
        //mainForm.action = "<%=rootPath%>/network.do?action=add";
        //mainForm.submit();
     //}  
       // mainForm.submit();
 });	
	
});
</script>



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

function setClass(){
	document.getElementById('hostReportTitle-5').className='detail-data-title';
	document.getElementById('hostReportTitle-5').onmouseover="this.className='detail-data-title'";
	document.getElementById('hostReportTitle-5').onmouseout="this.className='detail-data-title'";
}

function refer(action){
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}
</script>
<!-- snow add 2010-5-28 -->
<style>
<!--
body{
background-image: url(${pageContext.request.contextPath}/common/images/menubg_report.jpg);
TEXT-ALIGN: center; 
}
-->
</style>
</head>
<body id="body" class="body" onload="init();">
<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 189px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
<form id="mainForm" method="post" name="mainForm">
<input type=hidden id="ipaddress" name="ipaddress" value=<%=request.getParameter("ipaddress") %>>
	<table id="container-main" class="container-main">
		<tr>
			<td>
				<table id="container-main-win" class="container-main-win">
					<tr>
						<td>
							<table id="win-content" class="win-content">
								<tr>
									<td>
										<table id="win-content-header" class="win-content-header">
				                			<tr>
							                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
							                	<td class="win-content-title" style="align:center">&nbsp;tomcat性能报表</td>
							                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>											   
											</tr>
									    
					       				</table>
				       				</td>
				       			</tr>
						       	<tr>
						       		<td>
						       			<table id="win-content-body" class="win-content-body">
											<tr>
						       					<td>
													<table bgcolor="#ECECEC">
														<tr align="left" valign="center"> 
														<td height="28" align="left" width=60%>
														</td>
														<td height="28" align="left">
															<a href="javascript:ping_word_report()"><img name="selDay1" alt='导出word' style="CURSOR:hand" src="<%=rootPath%>/resource/image/export_word.gif" width=18  border="0">导出WORLD</a>
														</td>
														<td height="28" align="left">
															<a href="javascript:ping_excel_report()"><img name="selDay1" alt='导出EXCEL' style="CURSOR:hand" src="<%=rootPath%>/resource/image/export_excel.gif" width=18  border="0">导出EXCEL</a>
														</td>
														<td height="28" align="left">&nbsp;
															<a href="javascript:ping_pdf_report()"><img name="selDay1" alt='导出word' style="CURSOR: hand" src="<%=rootPath%>/resource/image/export_pdf.gif" width=18 border="0">导出PDF</a>
														</td>
														</tr>  
													</table>
						       					</td>
						       				</tr>
											<tr>
							                	<td class="win-data-title" style="height: 29px;" ></td>
							       			</tr>
							       			<tr align="left" valign="center"> 
			             						<td height="28" align="left" border="0">
													
													<input type=hidden name="eventid">
													<div id="loading">
													<div class="loading-indicator">
														<img src="<%=rootPath%>/js/ext/lib/resources/extanim64.gif" width="32" height="32" style="margin-right: 8px;" align="middle" />Loading...</div>
													</div>
													<table border="1" width="90%">
													  <tr bgcolor="#FFFFFF">
				   <td width="96%" align="left" height=40>队列信息</td>
				</tr>
														<tr>
														<td>
												      		<table  width="96%" align="center">
                   <tr bgcolor="#ECECEC" height="28">
          			 <td align=center>&nbsp;</td>
					 <td align=center>队列名称</td>
					 <td align=center>执行线程空闲数</td>
					 <td align=center>队列中最长等待请求的存在时间</td>
					 <td align=center>当前线程数</td>
					 <td align=center>总线程数</td>
                  </tr>
			<%
				for(int i=0;i<queuedatalist.size();i++){
					WeblogicQueue vo=(WeblogicQueue)queuedatalist.get(i);
										
			%>
          <tr bgcolor="#FFFFFF"  <%=onmouseoverstyle%> height="28">
			<td align=center class="application-detail-data-body-list"><%=i+1%></td>          
            <td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getExecuteQueueRuntimeName()%></td>
            <td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getThreadPoolRuntimeExecuteThreadIdleCount()%></td>
            <td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getExecuteQueueRuntimePendingRequestOldestTime()%></td>
            <td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getExecuteQueueRuntimePendingRequestCurrentCount()%></td>
			<td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getExecuteQueueRuntimePendingRequestTotalCount()%></td>
          </tr>
          <%}%>
                  </table>       
				</td>
				</tr>
				
				<tr bgcolor="#FFFFFF">
				   <td width="96%" align="left" height=40>JDBC连接池</td>
				</tr>
				
				<!-- -------------------------- -->
				<tr> 
                  <td>
                  <table  width="96%" align="center" border=0 >
                   <tr bgcolor="#ECECEC" height="28">
          			 <td align=center>&nbsp;</td>
					 <td align=center>名称</td>
					 <td align=center>当前连接数</td>
					 <td align=center>驱动器版本</td>
					 <td align=center>最大容量</td>
					 <td align=center>平均连接数</td>
					 <td align=center>最高可活动连接数</td>
					 <td align=center>连接泄漏数</td>
					 <td align=center>连接等待数</td>
					 <td align=center>等待最长时间</td>
                </tr>
			<%
				for(int i=0;i<jdbcdatalist.size();i++){
					WeblogicJdbc vo=(WeblogicJdbc)jdbcdatalist.get(i);
										
			%>
          <tr bgcolor="#FFFFFF" <%=onmouseoverstyle%> height="28">
			<td align=center class="application-detail-data-body-list"><%=i+1%></td>          
            <td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getJdbcConnectionPoolName()%></td>
            <td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getJdbcConnectionPoolRuntimeActiveConnectionsCurrentCount()%></td>
            <td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getJdbcConnectionPoolRuntimeVersionJDBCDriver()%></td>
            <td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getJdbcConnectionPoolRuntimeMaxCapacity()%></td>
			<td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getJdbcConnectionPoolRuntimeActiveConnectionsAverageCount()%></td>
			<td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getJdbcConnectionPoolRuntimeHighestNumAvailable()%></td>
			<td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getJdbcLeaked()%></td>
			<td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getJdbcWaitCurrent()%></td>
			<td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getJdbcWaitMaxTime()%></td>
          </tr>
          <%}%>
                  </table>            							                                  					
                  	</td>
                  	</tr>				
				<!-- -------------------------- -->
				<tr bgcolor="#FFFFFF">
				   <td width="96%" align="left" height=40>Web应用信息</td>
				</tr>
				<tr>
														<td>
												      		 <table  width="96%" align="center">
                   <tr bgcolor="#ECECEC" height="28">
          			 <td align=center>&nbsp;</td>
					 <td align=center>名称</td>
					 <td align=center>状态</td>
					 <td align=center>当前会话数</td>
					 <td align=center>最大会话数</td>
					 <td align=center>总会话数</td>
                </tr>
			<%
				for(int i=0;i<webappdatalist.size();i++){
					WeblogicWeb vo=(WeblogicWeb)webappdatalist.get(i);
								
			%>
          <tr bgcolor="#FFFFFF" <%=onmouseoverstyle%> height="28">
			<td align=center class="application-detail-data-body-list"><%=i+1%></td>          
            <td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getWebAppComponentRuntimeComponentName()%></td>
            <td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getWebAppComponentRuntimeStatus()%></td>
            <td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getWebAppComponentRuntimeOpenSessionsCurrentCount()%></td>
            <td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getWebAppComponentRuntimeOpenSessionsHighCount()%></td>
			<td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getWebAppComponentRuntimeSessionsOpenedTotalCount()%></td>
          </tr>
          <%}%>
                  </table>        
				</td>
				</tr>
				<!-- -------------------------- -->
				<tr bgcolor="#FFFFFF">
				   <td width="96%" align="left" height=40>JVM堆信息</td>
				</tr>
				<tr>
														<td>
												      		<table  width="96%" align="center">
                   <tr bgcolor="#ECECEC" height="28">
          			 <td align=center>&nbsp;</td>
					 <td align=center>名称</td>
					 <td align=center>堆大小</td>
					 <td align=center>当前空闲堆大小</td>
                </tr>
			<%
				for(int i=0;i<heapdatalist.size();i++){
					WeblogicHeap vo=(WeblogicHeap)heapdatalist.get(i);
										
			%>
          <tr bgcolor="#FFFFFF" <%=onmouseoverstyle%> height="28">
			<td align=center class="application-detail-data-body-list"><%=i+1%></td>          
            <td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getJvmRuntimeName()%></td>
            <td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getJvmRuntimeHeapSizeCurrent()%></td>
            <td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getJvmRuntimeHeapFreeCurrent()%></td>
          </tr>
          <%}%>
                  </table>            	
				</td>
				</tr>
				<tr bgcolor="#FFFFFF">
				   <td width="96%" align="left" height=40>服务信息</td>
				</tr>
				<!-- -------------------------- -->		
				<tr>
														<td>
												      		 <table  width="96%" align="center">
                   <tr bgcolor="#ECECEC" height="28">
          			 <td align=center>&nbsp;</td>
					 <td align=center>服务名称</td>
					 <td align=center>服务监听地址</td>
					 <td align=center>服务监听端口</td>
					 <td align=center>当前Socket数</td>
					 <td align=center>服务当前运行状态</td>
                     <td align=center>服务器IP</td>
                </tr>
			<%
				for(int i=0;i<serverdatalist.size();i++){
					WeblogicServer vo=(WeblogicServer)serverdatalist.get(i);
										
			%>
          <tr bgcolor="#FFFFFF" <%=onmouseoverstyle%> height="28">
			<td align=center class="application-detail-data-body-list"><%=i+1%></td>          
            <td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getServerRuntimeName()%></td>
            <td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getServerRuntimeListenAddress()%></td>
            <td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getServerRuntimeListenPort()%></td>
            <td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getServerRuntimeOpenSocketsCurrentCount()%></td>
			<td align=center class="application-detail-data-body-list">&nbsp;<%=vo.getServerRuntimeState()%></td>
			<td align=center class="application-detail-data-body-list">&nbsp;<%=weblogicconf.getIpAddress()%></td>
          </tr>
          <%}%>
                  </table>           
				</td>
				</tr>
				
				
						
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
			
            		<div align=center>
            			<input type=button value="关闭窗口" onclick="window.close()">
            		</div>  
					<br>
</form>  
</body>
</html>