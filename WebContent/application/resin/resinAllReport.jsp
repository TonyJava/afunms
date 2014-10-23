<%@ page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.event.model.EventList"%>
<%@page import="java.util.Date"%>

<%@page import="com.afunms.polling.PollingEngine"%>
<%@page import="com.afunms.polling.node.Resin"%>
<%@page import="java.util.Hashtable"%>
<%@ include file="/include/globe.inc"%>
<html>
	<head>
		<%
			String rootPath = request.getContextPath();
			String id = (String) request.getAttribute("id");
			String Ping = (String) request.getAttribute("Ping");
			String pingmax = (String) request.getAttribute("pingmax");
			String avgpingcon = (String) request.getAttribute("avgpingcon");
			String newip = (String) request.getAttribute("newip");
			String ipaddress = (String) request.getAttribute("ipaddress");
			String startdate = (String) request.getAttribute("startdate");
			String todate = (String) request.getAttribute("todate");
			String type = (String) request.getAttribute("type");
			Resin resin = (Resin) PollingEngine.getInstance().getResinByID(Integer.parseInt(id));

			String curmemcon = (String) request.getAttribute("curmemcon");
			String maxmemcon = (String) request.getAttribute("maxmemcon");
			String avgmemcon = (String) request.getAttribute("avgmemcon");
			Hashtable reportHash=(Hashtable)session.getAttribute("resinreporthash");
			String avgheapcon="0";
			String maxheapcon="0";
			String curheapcon="0";
			
			String avgswapcon="0";
			String maxswapcon="0";
			String curswapcon="0";
			
			String avgphycon="0";
			String maxphycon="0";
			String curphycon="0";
			if(reportHash!=null){
				avgheapcon=(String)reportHash.get("avgheapcon");
				maxheapcon=(String)reportHash.get("maxheapcon");
				curheapcon=(String)reportHash.get("curheapcon");
				
				avgswapcon=(String)reportHash.get("avgswapcon");
				maxswapcon=(String)reportHash.get("maxswapcon");
				curswapcon=(String)reportHash.get("curswapcon");
				
				avgphycon=(String)reportHash.get("avgphycon");
				maxphycon=(String)reportHash.get("maxphycon");
				curphycon=(String)reportHash.get("curphycon");
				
			}
		%>
		<link
			href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css"
			rel="stylesheet" type="text/css" />

		<title>综合报表</title>
		<script type='text/javascript' src='/afunms/dwr/interface/DWRUtil.js'></script>
		<script type='text/javascript' src='/afunms/dwr/engine.js'></script>
		<script type='text/javascript' src='/afunms/dwr/util.js'></script>
		<script language="javascript" src="/afunms/js/tool.js"></script>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script type="text/javascript"
			src="<%=rootPath%>/include/swfobject.js"></script>
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js"
			charset="utf-8"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="utf-8"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js"
			charset="utf-8"></script>
		<script language="JavaScript" type="text/JavaScript">
function ping_word_report()
{
	mainForm.action = "<%=rootPath%>/resin.do?action=downloadAllReport&id=<%=id%>&ipaddress=<%=ipaddress%>&str=0";
	mainForm.submit();
}
function ping_excel_report()
{
	mainForm.action = "<%=rootPath%>/resin.do?action=downloadAllReport&id=<%=id%>&ipaddress=<%=ipaddress%>&str=1";
	mainForm.submit();
}
function ping_pdf_report()
{
	mainForm.action = "<%=rootPath%>/resin.do?action=downloadAllReport&id=<%=id%>&ipaddress=<%=ipaddress%>&str=2";
	mainForm.submit();
}
function query()
{
	var starttime = document.getElementById("mystartdate").value;
	var endtime = document.getElementById("mytodate").value;
	mainForm.action = "<%=rootPath%>/resin.do?action=allReport&ipaddress=<%=ipaddress%>&id=<%=id%>&startdate="+starttime+"&todate="+endtime;
	mainForm.submit();
}
function ping_cancel()
{
window.close();
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
        Ext.MessageBox.wait('数据加载中，请稍后.. '); 
	    mainForm.action="<%=rootPath%>/hostreport.do?action=hostcpu";
	   mainForm.submit();        
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

}



function refer(action){
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}
</script>

	</head>
	<body id="body" class="body" onload="init();">
		<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0
			noResize scrolling=no src="<%=rootPath%>/include/calendar.htm"
			style="DISPLAY: none; HEIGHT: 189px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
		<form id="mainForm" method="post" name="mainForm">
			<input type=hidden id="ipaddress" name="ipaddress"
				value=<%=request.getParameter("ipaddress")%>>
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
														<td align="left" width="5">
															<img src="<%=rootPath%>/common/images/right_t_01.jpg"
																width="5" height="29" />
														</td>
														<td class="win-content-title" style="align: center">
															&nbsp;连通率报表
														</td>
														<td align="right">
															<img src="<%=rootPath%>/common/images/right_t_03.jpg"
																width="5" height="29" />
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
																		开始日期
																		<input type="text" id="mystartdate" name="startdate"
																			value="<%=startdate%>" size="10">
																		<a onclick="event.cancelBubble=true;"
																			href="javascript:ShowCalendar(document.forms[0].imageCalendar1,document.forms[0].startdate,null,0,330)">
																			<img id=imageCalendar1 align=absmiddle width=34
																				height=21
																				src="<%=rootPath%>/include/calendar/button.gif"
																				border=0> </a> 截止日期
																		<input type="text" id="mytodate" name="todate"
																			value="<%=todate%>" size="10" />
																		<a onclick="event.cancelBubble=true;"
																			href="javascript:ShowCalendar(document.forms[0].imageCalendar2,document.forms[0].todate,null,0,330)">
																			<img id=imageCalendar2 align=absmiddle width=34
																				height=21
																				src="<%=rootPath%>/include/calendar/button.gif"
																				border=0> </a>
																		<input type="button" name="doprocess" value="确定"
																			onclick="query()">
																	</td>
																	<td height="28" align="left">
																		<a href="javascript:ping_word_report()"><img
																				name="selDay1" alt='导出word' style="CURSOR: hand"
																				src="<%=rootPath%>/resource/image/export_word.gif"
																				width=18 border="0">导出WORLD</a>
																	</td>
																	<td height="28" align="left">
																		<a href="javascript:ping_excel_report()"><img
																				name="selDay1" alt='导出EXCEL' style="CURSOR: hand"
																				src="<%=rootPath%>/resource/image/export_excel.gif"
																				width=18 border="0">导出EXCEL</a>
																	</td>
																	<td height="28" align="left">
																		&nbsp;
																		<a href="javascript:ping_pdf_report()"><img
																				name="selDay1" alt='导出word' style="CURSOR: hand"
																				src="<%=rootPath%>/resource/image/export_pdf.gif"
																				width=18 border="0">导出PDF</a>
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td class="win-data-title" style="height: 29px;"></td>
													</tr>
													<tr align="left" valign="center">
														<td height="28" align="left" border="0">

															<input type=hidden name="eventid">
															<div id="loading">
																<div class="loading-indicator">
																	<img
																		src="<%=rootPath%>/js/ext/lib/resources/extanim64.gif"
																		width="32" height="32" style="margin-right: 8px;"
																		align="middle" />
																	Loading...
																</div>
															</div>
															<table border="1" width="90%">
																<tr>
																	<td>
																		连通率
																	</td>
																	<td>
																		当前连通率
																	</td>
																	<td>
																		最小连通率
																	</td>
																	<td>
																		平均连通率
																	</td>
																</tr>
																<tr>
																	<td>
																		&nbsp;
																	</td>
																	<td><%=Ping%>%
																	</td>
																	<td><%=pingmax%>%
																	</td>
																	<td><%=avgpingcon%>%
																	</td>
																</tr>
																<tr>
																	<td colspan="4" align="center">
																		<img
																			src="<%=rootPath%>/resource/image/jfreechart/<%=newip%>pingConnect.png" />
																	</td>
																</tr>
																<%
																	if (resin.getVersion().equals("3")) {
																%>
																<tr>
																	<td>
																		内存利用率
																	</td>
																	<td>
																		当前内存利用率
																	</td>
																	<td>
																		最大内存利用率
																	</td>
																	<td>
																		平均内存利用率
																	</td>
																</tr>
																<tr>
																	<td>
																		&nbsp;
																	</td>
																	<td><%=curmemcon%>%
																	</td>
																	<td><%=maxmemcon%>%
																	</td>
																	<td><%=avgmemcon%>%
																	</td>
																</tr>
																<tr>
																	<td colspan="4" align="center">
																		<img
																			src="<%=rootPath%>/resource/image/jfreechart/<%=newip%>memUtil.png" />
																	</td>
																</tr>
																<%
																	}
																%>
																<%
																	if (resin.getVersion().equals("4")) {
																%>
																<tr>
																	<td>
																		Heap利用率
																	</td>
																	<td>
																		当前Heap利用率
																	</td>
																	<td>
																		最大Heap利用率
																	</td>
																	<td>
																		平均Heap利用率
																	</td>
																</tr>
																<tr>
																	<td>
																		&nbsp;
																	</td>
																	<td><%=curheapcon%>%
																	</td>
																	<td><%=maxheapcon%>%
																	</td>
																	<td><%=avgheapcon%>%
																	</td>
																</tr>
																<tr>
																	<td colspan="4" align="center">
																		<img
																			src="<%=rootPath%>/resource/image/jfreechart/<%=newip%>heapUtil.png" />
																	</td>
																</tr>
																
																<tr>
																	<td>
																		物理利用率
																	</td>
																	<td>
																		当前物理利用率
																	</td>
																	<td>
																		最大物理利用率
																	</td>
																	<td>
																		平均物理利用率
																	</td>
																</tr>
																<tr>
																	<td>
																		&nbsp;
																	</td>
																	<td><%=curphycon%>%
																	</td>
																	<td><%=maxphycon%>%
																	</td>
																	<td><%=avgphycon%>%
																	</td>
																</tr>
																<tr>
																	<td colspan="4" align="center">
																		<img
																			src="<%=rootPath%>/resource/image/jfreechart/<%=newip%>phyUtil.png" />
																	</td>
																</tr>
																
																<tr>
																	<td>
																		交换利用率
																	</td>
																	<td>
																		当前交换利用率
																	</td>
																	<td>
																		最大交换利用率
																	</td>
																	<td>
																		平均交换利用率
																	</td>
																</tr>
																<tr>
																	<td>
																		&nbsp;
																	</td>
																	<td><%=curswapcon%>%
																	</td>
																	<td><%=maxswapcon%>%
																	</td>
																	<td><%=avgswapcon%>%
																	</td>
																</tr>
																<tr>
																	<td colspan="4" align="center">
																		<img
																			src="<%=rootPath%>/resource/image/jfreechart/<%=newip%>swapUtil.png" />
																	</td>
																</tr>
																<%
																	}
																%>
																<tr align="left">
																<tr bgcolor="#ECECEC">
																	<td colspan=4>
																		<input type=hidden name="eventid">
																		<table cellSpacing="0" cellPadding="0" border=0>
																			<tr>
																				<td class="detail-data-body-title">
																					<INPUT type="checkbox" name="checkall"
																						onclick="javascript:chkall()">
																				</td>
																				<td width="10%" class="detail-data-body-title">
																					<strong>事件等级</strong>
																				</td>
																				<td width="40%" class="detail-data-body-title">
																					<strong>事件描述</strong>
																				</td>
																				<td class="detail-data-body-title">
																					<strong>最近告警时间</strong>
																				</td>
																				<td class="detail-data-body-title">
																					<strong>告警次数</strong>
																				</td>
																				<td class="detail-data-body-title">
																					<strong>查看状态</strong>
																				</td>
																				<td class="detail-data-body-title">
																					<strong></strong>
																				</td>
																			</tr>
																			<%
																				int index = 0;
																				java.text.SimpleDateFormat _sdf = new java.text.SimpleDateFormat("MM-dd HH:mm");
																				List list = (List) request.getAttribute("list");

																				for (int i = 0; i < list.size(); i++) {
																					index++;
																					EventList eventlist = (EventList) list.get(i);
																					Date cc = eventlist.getRecordtime().getTime();
																					Integer eventid = eventlist.getId();
																					String eventlocation = eventlist.getEventlocation();
																					String content = eventlist.getContent();
																					String level = String.valueOf(eventlist.getLevel1());
																					String status = String.valueOf(eventlist.getManagesign());
																					String s = status;
																					String bgcolor = "";
																					String act = "处理报告";
																					if ("0".equals(level)) {
																						level = "提示信息";
																						bgcolor = "bgcolor='blue'";
																					}
																					if ("1".equals(level)) {
																						level = "普通告警";
																						bgcolor = "bgcolor='yellow'";
																					}
																					if ("2".equals(level)) {
																						level = "严重告警";
																						bgcolor = "bgcolor='orange'";
																					}
																					if ("3".equals(level)) {
																						level = "紧急告警";
																						bgcolor = "bgcolor='red'";
																					}
																					String bgcolorstr = "";
																					if ("0".equals(status)) {
																						status = "未处理";
																						bgcolorstr = "#9966FF";
																					}
																					if ("1".equals(status)) {
																						status = "处理中";
																						bgcolorstr = "#3399CC";
																					}
																					if ("2".equals(status)) {
																						status = "处理完成";
																						bgcolorstr = "#33CC33";
																					}
																					String rptman = eventlist.getReportman();
																					String rtime1 = _sdf.format(cc);
																			%>

																			<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%>>

																				<td class="detail-data-body-list">
																					<INPUT type="checkbox" name="checkbox"
																						value="<%=eventlist.getId()%>"><%=i + 1%></td>
																				<td class="detail-data-body-list" <%=bgcolor%>><%=level%></td>
																				<td class="detail-data-body-list"><%=content%></td>
																				<td class="detail-data-body-list"><%=rtime1%></td>
																				<td class="detail-data-body-list"><%=rptman%></td>
																				<td class="detail-data-body-list"
																					bgcolor=<%=bgcolorstr%>><%=status%></td>
																				<td class="detail-data-body-list" align="center">
																					<%
																						if ("0".equals(s)) {
																					%>
																					<input type="button" value="接受处理" class="button"
																						onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=600, width= 800, top=200, left= 200,resizable=yes,scrollbars=yes,screenX=0,screenY=0")'>
																					<!--<input type ="button" value="接受处理" class="button" onclick="accEvent('<%=eventid%>')">-->
																					<%
																						}
																							if ("1".equals(s)) {
																					%>
																					<input type="button" value="填写报告" class="button"
																						onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=600, width= 800, top=200, left= 200,resizable=yes,scrollbars=yes,screenX=0,screenY=0")'>
																					<!--<input type ="button" value="填写报告" class="button" onclick="fiReport('<%=eventid%>')">-->
																					<%
																						}
																							if ("2".equals(s)) {
																					%>
																					<input type="button" value="查看报告" class="button"
																						onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=600, width= 800, top=200, left= 200,resizable=yes,scrollbars=yes,screenX=0,screenY=0")'>
																					<!--<input type ="button" value="查看报告" class="button" onclick="viewReport('<%=eventid%>')">-->
																					<%
																						}
																					%>
																				</td>
																			</tr>
																			<%
																				}
																			%>
																		</table>
																	</td>
																</tr>

															</table>

															<div align=center>
																<input type=button value="关闭窗口" onclick="window.close()">
															</div>

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



		</form>
	</body>
</html>