<%@ page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.polling.node.Disk"%>
<%@page import="com.afunms.common.util.ShareData"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>
<html>
<head>
<%
  String rootPath = request.getContextPath();
  //System.out.println(rootPath); 
  String ipaddress = (String)request.getAttribute("ipaddress");
  String startdate = (String)request.getAttribute("startdate");
  String todate = (String)request.getAttribute("todate");
  int nodeid = (Integer)request.getAttribute("nodeid");
  List<Disk> disks = null;
  Hashtable ipAllData = new Hashtable();
  //只取当前采集的磁盘信息
  ipAllData = (Hashtable) ShareData.getSharedata().get("hpstorage:" + ipaddress);
  if(ipAllData != null){
      disks = (List<Disk>) ipAllData.get("disks");
  }
%>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

<title>磁盘报表</title>
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
function network_disk_word_report()
{
	mainForm.action = "<%=rootPath%>/netreport.do?action=downloadHPStorageDiskReport&ipaddress=<%=ipaddress%>&str=1";
	mainForm.submit();
}
function network_disk_excel_report()
{
	mainForm.action = "<%=rootPath%>/netreport.do?action=downloadHPStorageDiskReport&ipaddress=<%=ipaddress%>&str=0";
	mainForm.submit();
}
function network_disk_pdf_report()
{
	mainForm.action = "<%=rootPath%>/netreport.do?action=downloadHPStorageDiskReport&ipaddress=<%=ipaddress%>&str=4";
	mainForm.submit();
}
function network_disk_ok()
{
	var starttime = document.getElementById("mystartdate").value;
	var endtime = document.getElementById("mytodate").value;
	//var prewindow = window.open("<%=rootPath%>/netreport.do?action=showPingReport&ipaddress=<%=ipaddress%>&startdate="+starttime+"&todate="+endtime,"hello","portScanWindow","width=800,height=400,scrollbars=yes,resizable=yes");
	mainForm.action = "<%=rootPath%>/netreport.do?action=showHPstorDiskReport&ipaddress=<%=ipaddress%>&id=<%=nodeid%>&startdate="+starttime+"&todate="+endtime;
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
	document.getElementById('hpStorageReportDetailTile-1').className='detail-data-title';
	document.getElementById('hpStorageReportDetailTile-1').onmouseover="this.className='detail-data-title'";
	document.getElementById('hpStorageReportDetailTile-1').onmouseout="this.className='detail-data-title'";
}
function refer(action){
		document.getElementById("id").value="<%=nodeid%>";
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
							                	<td class="win-content-title" style="align:center">&nbsp;磁盘报表</td>
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
															<input type="button" name="doprocess" value="确定" onclick="network_disk_ok()">
														</td>
														<td height="28" align="left">
															<a href="javascript:network_disk_word_report()"><img name="selDay1" alt='导出word' style="CURSOR:hand" src="<%=rootPath%>/resource/image/export_word.gif" width=18  border="0">导出WORD</a>
														</td>
														<td height="28" align="left">
															<a href="javascript:network_disk_excel_report()"><img name="selDay1" alt='导出EXCEL' style="CURSOR:hand" src="<%=rootPath%>/resource/image/export_excel.gif" width=18  border="0">导出EXCEL</a>
														</td>
														<td height="28" align="left">&nbsp;
															<a href="javascript:network_disk_pdf_report()"><img name="selDay1" alt='导出word' style="CURSOR: hand" src="<%=rootPath%>/resource/image/export_pdf.gif" width=18 border="0">导出PDF</a>
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
													<table border="1" width="100%">
													     <tr bgcolor="">
							                                  <td colspan="7" align="center" style="font-size: 20"><br><b>HP存储磁盘报表<br><br></td>
						                                 </tr>
						                                 <tr bgcolor="#ECECEC">
											                  <td width="10%" height="26" align="center">名称</td>
											                  <td width="15%" height="26" align="center">状态</td>
											                  <td width="15%" height="26" align="center">类型</td>
											                  <td width="30%" height="26" align="center">容量</td>											                  
											                  <td width="5%" height="26" align="center">冗余组</td>
											                  <td width="15%" height="26" align="center">初始化声明</td>
											                  <td width="10%" height="26" align="center">固件版本</td>
											              </tr>
											                 <%  
											                        if(disks != null && disks.size()>0)
											                        {
											                           for(int i=0;i<disks.size();i++)
											                           {
											                               Disk disk = disks.get(i);										                 
											                                if(i%2 == 1)
											                                {
											                 %>
											                    <tr bgcolor="#ECECEC">   
											                 <%
											                             }else{
											                  %>
											                    <tr>
											                  <%
											                             }
											                   %>  
											                   <td width="10%" height="26" align="center"><%=SysUtil.ifNull(disk.getName()) %></td>
											                   <td width="15%" height="26" align="center"><%=SysUtil.ifNull(disk.getStatus()) %></td>
											                   <td width="15%" height="26" align="center"><%=SysUtil.ifNull(disk.getVendorID()) %></td>
											                   <td width="30%" height="26" align="center"><%=SysUtil.ifNull(disk.getDataCapacity()) %></td>											                  
											                   <td width="5%" height="26" align="center"><%=SysUtil.ifNull(disk.getRedundancyGroup()) %></td>
											                   <td width="15%" height="26" align="center"><%=SysUtil.ifNull(disk.getInitializeState()) %></td>
											                   <td width="15%" height="26" align="center"><%=SysUtil.ifNull(disk.getFirmwareRevision()) %></td>											                   
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
			
            		<div align=center>
            			<input type=button value="关闭窗口" onclick="window.close()">
            		</div>  
					<br>
</form>
</body>
</html>