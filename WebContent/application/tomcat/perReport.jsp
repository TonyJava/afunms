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

<html>
<head>
<%
  
  String rootPath = request.getContextPath(); 
  String id = request.getParameter("id");
  String menuTable = (String)request.getAttribute("menuTable");
  //String flag = (String)request.getAttribute("flag");
  Hashtable hash = (Hashtable)request.getAttribute("pingcon");
  
  Hashtable tom = (Hashtable)request.getAttribute("tomcat");
  String pingavg=(String)hash.get("avgpingcon");
  String avgpingcon=pingavg.replaceAll("%",""); 
  Hashtable avgjvm=(Hashtable)request.getAttribute("avgjvm");
  String avgjvmcon=(String)avgjvm.get("avg_tomcat_jvm");
  avgjvmcon=avgjvmcon.replaceAll("%","");
  double jvm_memoryuiltillize=0;
  double tomcatping=0;

  String lasttime ;
  String nexttime;
  String jvm="";
  String jvm_utilization = "";

  Hashtable data_ht=new Hashtable();
  Hashtable pollingtime_ht=new Hashtable();
  TomcatManager tm= new TomcatManager();

   
  Tomcat tomcat = (Tomcat)PollingEngine.getInstance().getTomcatByID(Integer.parseInt(id));  
  Hashtable tomcatvalues = ShareData.getTomcatdata();
  if(tomcatvalues != null && tomcatvalues.size()>0){
  	data_ht = (Hashtable)tomcatvalues.get(tomcat.getIpAddress());
  }


	tomcatping=(double)tm.tomcatping(tomcat.getId());
	pollingtime_ht=tm.getCollecttime(tomcat.getIpAddress());

	 if(pollingtime_ht!=null){
	 lasttime=(String)pollingtime_ht.get("lasttime");
	 nexttime=(String)pollingtime_ht.get("nexttime");
	 
	 }else{
	 lasttime=null;
	 nexttime=null;	 
	 }
	if(data_ht!=null){
		if(data_ht.get("jvm") != null)
			jvm=(String)data_ht.get("jvm");
			//System.out.println("-------------portsum1-----------"+(String)data_ht.get("portsum1"));			
		if(data_ht.get("jvm_utilization") != null){
			jvm_utilization = (String)data_ht.get("jvm_utilization");
			//System.out.println(jvm_utilization+"---------------------");
		}
			
	}else{
			jvm="";
	}	
				double totalmemory = 0;
				double maxmemory= 0;
				double freememory=0;
				StringBuffer info = new StringBuffer(100);
				if(jvm != null && jvm.trim().length()>0){
					String [] temjvm=jvm.split(",");
					freememory=Double.parseDouble(temjvm[0].trim());
					totalmemory=(double)Double.parseDouble(temjvm[1].trim());
					maxmemory=(double)Double.parseDouble(temjvm[2].trim());					
    					info.append("可用内存：");
    					info.append(freememory);
    					info.append("MB,总内存：");
    					info.append(totalmemory);
    					info.append("MB,最大内存：");
    					info.append(maxmemory);
    					info.append("MB");  
    					System.out.println( totalmemory+"==="+ freememory);				
					jvm_memoryuiltillize=(totalmemory-freememory)*100/totalmemory;								
				}


  				String chart1 = null,chart2 = null,chart3 = null,responseTime = null;  
 
  				DefaultPieDataset dpd = new DefaultPieDataset();
  				dpd.setValue("可用时间",tomcatping);
  				dpd.setValue("不可用时间",100 - tomcatping);
  				chart1 = ChartCreator.createPieChart(dpd,"",120,120);  	
  				int percent1 = Double.valueOf(tomcatping).intValue();
				int percent2 = 100-percent1;
				String cpuper ="0";
  				cpuper = jvm_utilization;
		 
		 		chart2 = ChartCreator.createMeterChart(Math.rint(jvm_memoryuiltillize),"",100,100);  
	       	 

				chart3 = ChartCreator.createMeterChart(100-jvm_memoryuiltillize,"",100,100);  
                String flag_1 = (String)request.getAttribute("flag");
%>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

<title>Tomcat配置报表</title>
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
	mainForm.action = "<%=rootPath%>/tomcat.do?action=downloadReport&flag=per&str=1&id=<%=id%>";
	mainForm.submit();
}
function ping_excel_report()
{
	mainForm.action = "<%=rootPath%>/tomcat.do?action=downloadReport&flag=per&str=0&id=<%=id%>";
	mainForm.submit();
}
function ping_pdf_report()
{
	mainForm.action = "<%=rootPath%>/tomcat.do?action=downloadReport&flag=per&str=2&id=<%=id%>";
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
														<tr>
		<td>
			<table id="service-detail-content-body"
				class="service-detail-content-body">
				<tr>
					<td>
						<table style="BORDER-COLLAPSE: collapse" bordercolor=#cedefa
							rules=none align=center border=1 cellpadding=0 cellspacing="0"
							width=100%>
							<tr>
								<td width="100%" align="left" valign="top">
									<table>
										<tr>
											<td width="25%" height="29" align="left" nowrap
												class=txtGlobal>
												&nbsp;名称:
											</td>
											<td width="25%"><%=tomcat.getAlias()%></td>
											<td width="25%" height="29" align="left" nowrap
												class=txtGlobal>
												&nbsp;最大线程:
											</td>
											<td width="25%"><%if(tom != null)out.println(tom.get("maxthread"));%></td>
										</tr>
										<tr bgcolor="#F1F1F1">
											<td height="29" class=txtGlobal width="25%" align="left" nowrap>
												&nbsp;最小共享线程:
											</td>
											<td width="25%"><%if(tom != null)out.println(tom.get("minsthread"));%></td>
											<td width="25%" height="29" align="left" nowrap
												class=txtGlobal>
												&nbsp;最大共享线程:
											</td>
											<td width="25%"><%if(tom != null)out.println(tom.get("maxsthread"));%></td>
										</tr>
										
										<tr>
											<td width="25%" height="29" align="left" nowrap
												class=txtGlobal>
												&nbsp;当前线程数:
											</td>
											<td width="25%"><%if(tom != null)out.println(tom.get("curcount"));%></td>
											<td width="25%" height="29" align="left" nowrap
												class=txtGlobal>
												&nbsp;当前繁忙线程数:
											</td>
											<td width="25%"><%if(tom != null)out.println(tom.get("curthbusy"));%></td>
										</tr>
										<tr bgcolor="#F1F1F1">
											<td height="29" class=txtGlobal width="25%" align="left" nowrap>
												&nbsp;最大处理时间:
											</td>
											<td width="25%"><%if(tom != null)out.println(tom.get("maxprotime")+"  ms");%></td>
											<td width="25%" height="29" align="left" nowrap
												class=txtGlobal>
												&nbsp;处理时间:
											</td>
											<td width="25%"><%if(tom != null)out.println(tom.get("protime")+"  s");%></td>
										</tr>
										
										<tr>
											<td width="25%" height="29" align="left" nowrap
												class=txtGlobal>
												&nbsp;请求数量:
											</td>
											<td width="25%"><%if(tom != null)out.println(tom.get("requestcount"));%></td>
											<td width="25%" height="29" align="left" nowrap
												class=txtGlobal>
												&nbsp;请求错误数:
											</td>
											<td width="25%"><%if(tom != null)out.println(tom.get("errorcount"));%></td>
										</tr>
										<tr bgcolor="#F1F1F1">
											<td height="29" class=txtGlobal width="25%" align="left" nowrap>
												&nbsp;接受字节数:
											</td>
											<td width="25%"><%if(tom != null)out.println(tom.get("bytesreceived")+"  MB");%></td>
											<td width="25%" height="29" align="left" nowrap
												class=txtGlobal>
												&nbsp;发送字节数:
											</td>
											<td width="25%"><%if(tom != null)out.println(tom.get("bytessent")+"  MB");%></td>
										</tr>
									</table>
								</td>
				</tr>
			</table>
		</td>
	</tr>
												
												
												<tr>
													<td>
														<table id="service-detail-content-body" class="service-detail-content-body">
															<tr>
																<td>
																	<table>
																		<tr>
																			<td>
																				<table border="0" cellpadding="0" cellspacing="0" align=center>
																					<tr>
																						<td align=center>
																							<br>
																							<table cellpadding="0" cellspacing="0" width=98%>
																								<tr>
																									<td width="100%">
																										<div id="flashcontent1">
																											<strong>You need to upgrade your Flash Player</strong>
																										</div>
																										<script type="text/javascript">
																											var so = new SWFObject("<%=rootPath%>/flex/Tomcat_Ping.swf?ipadress=<%=tomcat.getIpAddress()%>", "Tomcat_Ping", "346", "250", "8", "#ffffff");
																											so.write("flashcontent1");
																										</script>
																										
																									</td>
																								</tr>
																							</table>
																						</td>
																						<td>
																							&nbsp;
																						</td>
																						<td align=center>
																							<br>
																							<table cellpadding="0" cellspacing="0" width=98%>
																								<tr>
																									<td width="100%">
																										<div id="flashcontent2">
																											<strong>You need to upgrade your Flash Player</strong>
																										</div>
																										<script type="text/javascript">
																											var so = new SWFObject("<%=rootPath%>/flex/Tomcat_JVM_Memory.swf?ipadress=<%=tomcat.getIpAddress()%>", "Tomcat_JVM_Memory", "346", "250", "8", "#ffffff");
																											so.write("flashcontent2");
																										</script>
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
				</tr>
       			</table>
			
            		<div align=center>
            			<input type=button value="关闭窗口" onclick="window.close()">
            		</div>  
					<br>
</form>  
</body>
</html>