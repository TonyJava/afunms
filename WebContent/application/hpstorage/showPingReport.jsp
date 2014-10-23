<%@ page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="java.util.*"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>
<html>
<head>
<%
  String rootPath = request.getContextPath();
  //System.out.println(rootPath);
  List pinglist = (List)session.getAttribute("pinglist");
  String ipaddress = (String)request.getAttribute("ipaddress");
String startdate = (String)request.getAttribute("startdate");
String todate = (String)request.getAttribute("todate");
String newip = (String)request.getAttribute("newip");
String imgpath = (String)request.getAttribute("imgpath");
//System.out.println("imgpath...     " + imgpath);
int nodeid = (Integer)request.getAttribute("nodeid");
%>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

<title>可用性报表</title>
<!-- snow add 2010-5-28 -->
<style>
<!--
body{
background-image: url(${pageContext.request.contextPath}/common/images/menubg_report.jpg);
TEXT-ALIGN: center; 
}
-->
</style>
<!-- snow add end -->
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
function network_ping_word_report()
{
	mainForm.action = "<%=rootPath%>/netreport.do?action=downloadNetworkPingReport&ipaddress=<%=ipaddress%>&str=1";
	mainForm.submit();
}
function network_ping_excel_report()
{
	mainForm.action = "<%=rootPath%>/netreport.do?action=downloadNetworkPingReport&ipaddress=<%=ipaddress%>&str=0";
	mainForm.submit();
}
function network_ping_pdf_report()
{
	mainForm.action = "<%=rootPath%>/netreport.do?action=downloadNetworkPingReport&ipaddress=<%=ipaddress%>&str=4";
	mainForm.submit();
}
function network_ping_ok()
{
	var starttime = document.getElementById("mystartdate").value;
	var endtime = document.getElementById("mytodate").value;
	//var prewindow = window.open("<%=rootPath%>/netreport.do?action=showPingReport&ipaddress=<%=ipaddress%>&startdate="+starttime+"&todate="+endtime,"hello","portScanWindow","width=800,height=400,scrollbars=yes,resizable=yes");
	mainForm.action = "<%=rootPath%>/netreport.do?action=showHPstorPingReport&ipaddress=<%=ipaddress%>&id=<%=nodeid%>&startdate="+starttime+"&todate="+endtime;
	mainForm.submit();
	//prewindow.opener.close();
}
function network_ping_cancel()
{
window.close();
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
function setClass(){
	document.getElementById('hpStorageReportDetailTile-0').className='detail-data-title';
	document.getElementById('hpStorageReportDetailTile-0').onmouseover="this.className='detail-data-title'";
	document.getElementById('hpStorageReportDetailTile-0').onmouseout="this.className='detail-data-title'";
}
function refer(action){        
		document.getElementById("id").value = "<%=nodeid%>";		
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}
</script>
</head>
<body id="body" class="body" onload="setClass();">
<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 189px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
<form id="mainForm" method="post" name="mainForm">
<input type=hidden id="ipaddress" name="ipaddress" value=<%=request.getParameter("ipaddress") %>>
<input type=hidden id="id" name="id">
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
							                	<td class="win-content-title" style="align:center">&nbsp;连通率报表</td>
							                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>											   
											</tr>									    
					       				</table>
				       				</td>
				       			</tr>
				       			<tr>
									<td>
										<table id="report-content-header" class="report-content-header">
						                	<tr>
							    				<td>
										    		<%=hpStorageReportDetailTileTable%>
										    	</td>
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
														<td height="28" align="left">
															&nbsp;开始日期
															<input type="text" id="mystartdate" name="startdate" value="<%=startdate%>" size="10">
															<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar1,document.forms[0].startdate,null,0,330)">
															<img id=imageCalendar1 align=absmiddle width=34 height=21 src="<%=rootPath%>/include/calendar/button.gif" border=0></a>
																							
															截止日期
															<input type="text" id="mytodate" name="todate" value="<%=todate%>" size="10"/>
															<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar2,document.forms[0].todate,null,0,330)">
															<img id=imageCalendar2 align=absmiddle width=34 height=21 src="<%=rootPath%>/include/calendar/button.gif" border=0></a>
															<input type="button" name="doprocess" value="确定" onclick="network_ping_ok()">
														</td>
														<td height="28" align="left">
															<a href="javascript:network_ping_word_report()"><img name="selDay1" alt='导出word' style="CURSOR:hand" src="<%=rootPath%>/resource/image/export_word.gif" width=18  border="0">导出WORD</a>
														</td>
														<td height="28" align="left">
															<a href="javascript:network_ping_excel_report()"><img name="selDay1" alt='导出EXCEL' style="CURSOR:hand" src="<%=rootPath%>/resource/image/export_excel.gif" width=18  border="0">导出EXCEL</a>
														</td>
														<td height="28" align="left">&nbsp;
															<a href="javascript:network_ping_pdf_report()"><img name="selDay1" alt='导出word' style="CURSOR: hand" src="<%=rootPath%>/resource/image/export_pdf.gif" width=18 border="0">导出PDF</a>
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
						<tr bgcolor="">
							<td colspan="12" align="center" style="font-size: 20"><br><b>HP存储连通率报表<br><br></td>
						</tr>
						<tr bgcolor="C0C0C0" height="30" style="text-align: center">
							<td>&nbsp;IP地址</td><td>&nbsp;设备名称</td><td>&nbsp;操作系统</td><td>&nbsp;平均连通率</td><td>&nbsp;宕机次数</td><td>&nbsp;平均响应时间(ms)</td><td>&nbsp;最大响应时间(ms)</td>
						</tr>
						<%
						if (pinglist != null && pinglist.size() > 0) {
			for (int i = 0; i < pinglist.size(); i++) {
				List _pinglist = (List) pinglist.get(i);
				String ip = (String)_pinglist.get(0);
				String equname = (String)_pinglist.get(1);	
				String osname = (String)_pinglist.get(2);
				String avgping = (String)_pinglist.get(3);
				String downnum = (String)_pinglist.get(4);
				String responseavg = (String)_pinglist.get(5);
				String responsemax = (String)_pinglist.get(6);
				String cell5 = i + 1 + "";
				String cell6 =ip;
				String cell7 =equname;
				String cell8 = osname;
				String cell9 = avgping;
				String cell10 = downnum;
				String cell15 = new String(responseavg.replace("毫秒", ""));
				String cell16 = new String(responsemax.replace("毫秒", ""));
				%>
				<tr height="20" style="text-align: center">
					<td>&nbsp;<%=cell6 %></td><td>&nbsp;<%=cell7 %></td><td>&nbsp;<%=cell8 %></td><td>&nbsp;<%=cell9 %></td><td>&nbsp;<%=cell10 %></td><td>&nbsp;<%=cell15 %></td><td>&nbsp;<%=cell16 %></td>
				</tr>
				<%
			}
		}
						 %>
				<tr>
					<td align="center" colspan="8">
					<br>
					   <%
					       if(imgpath.equals(".png")){
					    %>
						没有数据
						<%
						}else{
						 %>
						 <img src="<%=rootPath%>/resource/image/jfreechart/<%=imgpath%>"/>
						 <%
						 }
						  %>
						<!--<img src="c:/350124510439.jpg"/>-->
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