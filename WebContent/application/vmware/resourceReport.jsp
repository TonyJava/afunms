<%@ page language="java" contentType="text/html;charset=gb2312"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc" %>

<%@page import="com.afunms.detail.service.IISInfo.IISInfoService"%>
<%@page import="com.afunms.report.jfree.ChartCreator"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.monitor.item.*"%>
<%@page import="com.afunms.polling.node.*" %>
<%@page import="com.afunms.polling.om.*" %>
<%@page import="com.afunms.monitor.item.base.MonitorResult"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="com.afunms.monitor.item.base.MoidConstants"%>
<%@page import="org.jfree.data.general.DefaultPieDataset"%>
<%@page import="com.afunms.application.util.*"%>
<%@page import="org.jdom.Element"%>
<%@page import="com.afunms.common.util.*"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.application.manage.*"%>
<%@page import="com.afunms.application.dao.*"%>
<%@page import="com.afunms.application.model.*"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.lang.*"%>
<%@page import="com.afunms.polling.*"%>
<html>
<head>
<%
   String runmodel = PollingEngine.getCollectwebflag(); 
   String rootPath = request.getContextPath(); 
   String menuTable = (String)request.getAttribute("menuTable");
   
   VMWareConnectConfig vo = (VMWareConnectConfig)request.getAttribute("vo");
   if(vo == null){
		vo = new VMWareConnectConfig();
   }
	String tmp = vo.getId()+"";
	
	ArrayList<HashMap<String, Object>> crlist = (ArrayList<HashMap<String, Object>>)request.getAttribute("crlist");
    ArrayList<HashMap<String, Object>> dslist = (ArrayList<HashMap<String, Object>>)request.getAttribute("dslist");
    ArrayList<HashMap<String, Object>> wulist = (ArrayList<HashMap<String, Object>>)request.getAttribute("wulist");
    ArrayList<HashMap<String, Object>> dclist = (ArrayList<HashMap<String, Object>>)request.getAttribute("dclist");
    ArrayList<HashMap<String, Object>> rplist = (ArrayList<HashMap<String, Object>>)request.getAttribute("rplist");
%>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

<title>VMWare资源信息报表</title>
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
	mainForm.action = "<%=rootPath%>/vmware.do?action=downloadAllReport&type=resource&str=1&id=<%=tmp%>";
	mainForm.submit();
}
function ping_excel_report()
{
	mainForm.action = "<%=rootPath%>/vmware.do?action=downloadAllReport&type=resource&str=0&id=<%=tmp%>";
	mainForm.submit();
}
function ping_pdf_report()
{
	mainForm.action = "<%=rootPath%>/vmware.do?action=downloadAllReport&type=resource&str=2&id=<%=tmp%>";
	mainForm.submit();
}
function ping_ok()
{
	var starttime = document.getElementById("mystartdate").value;
	var endtime = document.getElementById("mytodate").value;
	mainForm.action = "<%=rootPath%>/mq.do?action=showPingReport&id=<%=tmp%>&startdate="+starttime+"&todate="+endtime;
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
							                	<td class="win-content-title" style="align:center">&nbsp;VMWare(<%=request.getAttribute("ipaddress") %>)资源报表</td>
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
											    	<td width=100%>
											    		<table class="application-detail-data-body">
							<tr>
								<td>
									<table id="application-detail-data" class="application-detail-data">
                                       <tr>
									    	<td>
									    		<table class="application-detail-data-body">
									 				  <tr>
														<td>
												      		<table width="96%" align="center">
													      		<tr bgcolor="#F1F1F1">
																	<td>物理机信息</td>
																</tr>
																<tr>
																	<td align=center>
																		<table width=80%>
		  																	<tr bgcolor="#FFFFFF">
		    																	<td width=5% class="detail-data-body-title" style="height:29;align:center"><strong>序号</strong></td>
																			    <td width=20% class="detail-data-body-title" style="height:29;align:center"><strong>物理机名称</strong></td>
																			    <td width=15% class="detail-data-body-title" style="height:29;align:center"><strong>设备型号</strong></td>
																			    <td width=10% class="detail-data-body-title" style="height:29;align:center"><strong>CPU(核数)</strong></td>
																			    <td width=10% class="detail-data-body-title" style="height:29;align:center"><strong>主频(GHz)</strong></td>
																			    <td width=10% class="detail-data-body-title" style="height:29;align:center"><strong>网卡(个)</strong></td>
																			    <td width=10% class="detail-data-body-title" style="height:29;align:center"><strong>内存(M)</strong></td>
																			    <td width=10% class="detail-data-body-title" style="height:29;align:center"><strong>状态</strong></td>
		    																</tr>
										     								<%
										     								
										     								     if(wulist != null && wulist.size()>0){
										     								              for(int j=0;j<wulist.size();j++){ 
																								     String model = wulist.get(j).get("model").toString();
																								     String name = wulist.get(j).get("name").toString();
																								     //int totalcpu = Integer.parseInt(crlist.get(j).get("totalcpu").toString());
																								     String cpumhz = wulist.get(j).get("cpumhz").toString();
																								     String memorysizemb = wulist.get(j).get("memorysizemb").toString();
																								     String powerstate = wulist.get(j).get("powerstate").toString();// 20: 电源状态,值包括poweredOn,poweredOff,standBy,unknown
																								     String numcore = wulist.get(j).get("numcore").toString();
																								     String numnics = wulist.get(j).get("numnics").toString();
																								     String state = "";
																								     if(powerstate.equalsIgnoreCase("poweredOn")){state="通电";}else if(powerstate.equalsIgnoreCase("poweredOff")){state="未通电";}else if(powerstate.equalsIgnoreCase("standBy")){state="待机";}else{state="未知";}
																			 		
																			 %>    
																						  <tr  class="othertr" <%=onmouseoverstyle%> >
																						    <td height="28" class="detail-data-body-list"><%=j+1%></td>
																						    <td class="detail-data-body-list"><%=name%></td>
																						    <td class="detail-data-body-list"><%=model%></td>
																						    <td class="detail-data-body-list"><%=numcore%></td>
																						    <td class="detail-data-body-list"><%=cpumhz%></td>
																						    <td class="detail-data-body-list"><%=numnics%></td>
																						    <td class="detail-data-body-list"><%=memorysizemb%></td>
																						    <td class="detail-data-body-list"><%=state%></td>
																						   </tr>
																			<%
																					}
																				}
																			%>      														
																		</table>
																	</td>
																</tr>     
													      	</table>
									   					</td>
									 				 </tr>	
									 				  <tr>
														<td>
												      		<table width="96%" align="center">
													      		<tr bgcolor="#F1F1F1">
																	<td>资源池信息</td>
																</tr>
																<tr>
																	<td align=center>
																		<table width=80%>
		  																	<tr bgcolor="#FFFFFF">
		  																		<td width=5% class="detail-data-body-title" style="height:29;align:center"><strong>序号</strong></td>
																			    <td width=20% class="detail-data-body-title" style="height:29;align:center"><strong>名称</strong></td>
																			    <td width=20% class="detail-data-body-title" style="height:29;align:center"><strong>资源池id</strong></td>
																			    <td width=20% class="detail-data-body-title" style="height:29;align:center"><strong>所属数据中心</strong></td>
																			    <td width=20% class="detail-data-body-title" style="height:29;align:center"><strong>所属集群</strong></td>
		    																</tr>
										     								<%
				                    										if(rplist != null && rplist.size()>0){
				                    											for(int i=0;i<rplist.size();i++){
				                    											    String name = rplist.get(i).get("name").toString();
																				    String crid = rplist.get(i).get("crid").toString();
																					String dcid = rplist.get(i).get("dcid").toString();
																					String vid = rplist.get(i).get("vid").toString();
				                    										 %>  
																			  <tr  class="othertr" <%=onmouseoverstyle%> >
																			    <td height="28" class="detail-data-body-list"><%=i+1%></td>
																			    <td height="28" class="detail-data-body-list"><%=name%></td>
																			    <td height="28" class="detail-data-body-list"><%=vid%></td>
																			    <td height="28" class="detail-data-body-list"><%=dcid%></td>
																			    <td height="28" class="detail-data-body-list"><%=crid%></td>
																			   </tr>
																			<%
																			}
																			}
																			%>      														
																		</table>
																	</td>
																</tr>     
													      	</table>
									   					</td>
									 				 </tr>	
									 				<tr>
														<td>
												      		<table width="96%" align="center">
													      		<tr bgcolor="#F1F1F1">
																	<td>存储池信息</td>
																</tr>
																<tr>
																	<td align=center>
																		<table width=80%>
		  																	<tr bgcolor="#FFFFFF">
		  																		<td width=5% class="detail-data-body-title" style="height:29;align:center"><strong>序号</strong></td>
																			    <td width=20% class="detail-data-body-title" style="height:29;align:center"><strong>名称</strong></td>
																			    <td width=30% class="detail-data-body-title" style="height:29;align:center"><strong>存储总量(MB)</strong></td>
																			    <td width=30% class="detail-data-body-title" style="height:29;align:center"><strong>未使用存储(MB)</strong></td>
		    																</tr>
										     								<%
				                    										if(null != dslist && dslist.size() > 0){
				                    											for(int j=0;j<dslist.size();j++){ 
																						String name = dslist.get(j).get("name").toString();
																						String capacity = dslist.get(j).get("capacity").toString();
																						String freespace = dslist.get(j).get("freespace").toString();
				                    										 %>  
																			  <tr  class="othertr" <%=onmouseoverstyle%> >
																			    <td height="28" class="detail-data-body-list"><%=j+1%></td>
																			    <td height="28" class="detail-data-body-list"><%=name%></td>
																			    <td height="28" class="detail-data-body-list"><%=capacity%></td>
																			    <td height="28" class="detail-data-body-list"><%=freespace%></td>
																			   </tr>
																			<%
																			}
																			}
																			%>      														
																		</table>
																	</td>
																</tr>     
													      	</table>
									   					</td>
									 				 </tr>
									 				 <tr>
														<td>
												      		<table width="96%" align="center">
													      		<tr bgcolor="#F1F1F1">
																	<td>数据中心信息</td>
																</tr>
																<tr>
																	<td align=center>
																		<table width=80%>
		  																	<tr bgcolor="#FFFFFF">
		  																		<td width=5% class="detail-data-body-title" style="height:29;align:center"><strong>序号</strong></td>
																			    <td width=40% class="detail-data-body-title" style="height:29;align:center"><strong>名称</strong></td>
																			    <td width=40% class="detail-data-body-title" style="height:29;align:center"><strong>数据中心id</strong></td>
		    																</tr>
										     								<%
				                    										if(null != dclist && dclist.size() > 0){
				                    										  for(int j=0;j<dclist.size();j++){
				                    											String name = dclist.get(j).get("name").toString();
																				String vid = dclist.get(j).get("vid").toString();
				                    										 %>  
																			  <tr  class="othertr" <%=onmouseoverstyle%> >
																			    <td height="28" class="detail-data-body-list"><%=j+1%></td>
																			    <td height="28" class="detail-data-body-list"><%=name%></td>
																			    <td height="28" class="detail-data-body-list"><%=vid%></td>
																			   </tr>
																			<%
																			}
																			}
																			%>      														
																		</table>
																	</td>
																</tr>     
													      	</table>
									   					</td>
									 				 </tr>
									 				 <tr>
														<td>
												      		<table width="96%" align="center">
													      		<tr bgcolor="#F1F1F1">
																	<td>云资源信息</td>
																</tr>
																<tr>
																	<td align=center>
																		<table width=80%>
		  																	<tr bgcolor="#FFFFFF">
		  																		<td width=5% class="detail-data-body-title" style="height:29;align:center"><strong>序号</strong></td>
																			    <td width=15% class="detail-data-body-title" style="height:29;align:center"><strong>名称</strong></td>
																			    <td width=15% class="detail-data-body-title" style="height:29;align:center"><strong>磁盘总大小(MB)</strong></td>
																			    <td width=14% class="detail-data-body-title" style="height:29;align:center"><strong>CPU使用频率(MHz)</strong></td>
																			    <td width=15% class="detail-data-body-title" style="height:29;align:center"><strong>主机数量</strong></td>
																			     <td width=15% class="detail-data-body-title" style="height:29;align:center"><strong>总内存(MB)</strong></td>
																			      <td width=15% class="detail-data-body-title" style="height:29;align:center"><strong>CPU核数</strong></td>
		    																</tr>
										     								<%
				                    										if(null != crlist && crlist.size() > 0){
				                    											for(int j=0;j<crlist.size();j++){ 
																								     String totaldssizemb = crlist.get(j).get("totaldssizemb").toString();
																								     String name = crlist.get(j).get("name").toString();
																								     //int totalcpu = Integer.parseInt(crlist.get(j).get("totalcpu").toString());
																								     String totalcpu = crlist.get(j).get("totalcpu").toString();
																								     String numhosts = crlist.get(j).get("numhosts").toString();
																								     String totalmemory = crlist.get(j).get("totalmemory").toString();
																								     String numcpucores = crlist.get(j).get("numcpucores").toString();
				                    										 %>  
																			  <tr  class="othertr" <%=onmouseoverstyle%> >
																			    <td height="28" class="detail-data-body-list"><%=j+1%></td>
																			    <td height="28" class="detail-data-body-list"><%=name%></td>
																			    <td height="28" class="detail-data-body-list"><%=totaldssizemb%></td>
																			    <td height="28" class="detail-data-body-list"><%=totalcpu%></td>
																			    <td height="28" class="detail-data-body-list"><%=numhosts%></td>
																			    <td height="28" class="detail-data-body-list"><%=totalmemory%></td>
																			    <td height="28" class="detail-data-body-list"><%=numcpucores%></td>
																			   </tr>
																			<%
																			}
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
			
            		<div align=center>
            			<input type=button value="关闭窗口" onclick="window.close()">
            		</div>  
					<br>
</form>  
</body>
</html>