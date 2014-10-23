<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@page import="com.afunms.common.util.*"%>
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
<%@page import="com.afunms.report.jfree.ChartCreator"%>
<%@ page import="com.afunms.polling.loader.HostLoader"%>
<%@ page import="com.afunms.common.util.CEIString"%>
<%@page import="com.afunms.config.dao.*"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="com.afunms.monitor.item.base.*"%>
<%@page import="com.afunms.monitor.executor.base.*"%>
<%@page import="com.afunms.indicators.util.NodeUtil"%>
<%@page import="com.afunms.indicators.model.NodeDTO"%>
<%@page import="com.afunms.detail.net.service.NetService"%>
<%@page import="com.afunms.temp.model.NodeTemp"%>
<%
	String menuTable = (String) request.getAttribute("menuTable");//菜单
	String tmp = request.getParameter("id");
	int alarmLevel = (Integer) request.getAttribute("alarmLevel");
	Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
	Vector ifvector = (Vector) request.getAttribute("ifvector");
	NodeUtil nodeUtil = new NodeUtil();
	NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);
	String rootPath = request.getContextPath();
	String showAllPortFlag = request.getParameter("showAllPortFlag");//显示所有端口  0:显示所有端口   1：显示关键端口
	List<String> portIndexList = (ArrayList) request.getAttribute("portIndexList");

	String runmodel = PollingEngine.getCollectwebflag();//采集与访问模式
	String flag1 = request.getParameter("flag");

	String ipaddress = host.getIpAddress();
	Hashtable hash = (Hashtable) request.getAttribute("portConfigHash");
%>
<html>
	<head>
		<link
			href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css"
			rel="stylesheet" type="text/css" />

		<link rel="stylesheet"
			href="<%=rootPath%>/common/css/tableNewStyle.css" type="text/css"></link>
			
			<style type="text/css">
			
			thead.title{
				background-color:#ECECEC;
			}
			
			thead tr{
				text-align:center;
			}
			thead td{
				border-bottom: 1px solid #cad9ea;
			}
			tr.data{
				height:25px;text-align:center;
				background-color:expression((this.sectionRowIndex%2==0)?"#FFFFFF":"#F8F8FF");
			}
			</style>

		<script>

/*
*显示关键端口
*/
function showPort(){
	var showAllPortFlag = document.getElementById("allportflag").value;
	mainForm.action = "<%=rootPath%>/netapp.do?action=interface&id=<%=tmp%>&flag=<%=flag%>&showAllPortFlag="+showAllPortFlag;
    mainForm.submit();
}

</script>
		<script language="javascript">	
  
function changeOrder(para){
  	location.href="<%=rootPath%>/netapp.do?action=interface&id=<%=tmp%>&ipaddress=<%=host.getIpAddress()%>&flag=<%=flag1%>&orderflag="+para+"&showAllPortFlag=<%=showAllPortFlag%>";
} 
</script>
		<script language="JavaScript">

	//公共变量
	var node="";
	var ipaddress="";
	var operate="";
	/**
	*根据传入的id显示右键菜单
	*/
	function showMenu(id,nodeid,ip)
	{	
		ipaddress=ip;
		node=nodeid;
	    if("" == id)
	    {
	        popMenu(itemMenu,100,"100");
	    }
	    else
	    {
	        popMenu(itemMenu,100,"11111111");
	    }
	    event.returnValue=false;
	    event.cancelBubble=true;
	    return false;
	}
	/**
	*显示弹出菜单
	*menuDiv:右键菜单的内容
	*width:行显示的宽度
	*rowControlString:行控制字符串，0表示不显示，1表示显示，如“101”，则表示第1、3行显示，第2行不显示
	*/
	function popMenu(menuDiv,width,rowControlString)
	{
	    //创建弹出菜单
	    var pop=window.createPopup();
	    //设置弹出菜单的内容
	    pop.document.body.innerHTML=menuDiv.innerHTML;
	    var rowObjs=pop.document.body.all[0].rows;
	    //获得弹出菜单的行数
	    var rowCount=rowObjs.length;
	    //循环设置每行的属性
	   
	    for(var i=0;i<rowObjs.length;i++)
	    {
	        //如果设置该行不显示，则行数减一
	        var hide=rowControlString.charAt(i)!='1';
	        if(hide){
	      
	           rowCount--;
	        }
	        //设置是否显示该行
	        rowObjs[i].style.display=(hide)?"none":"";
	        //设置鼠标滑入该行时的效果
	        rowObjs[i].cells[0].onmouseover=function()
	        {
	            this.style.background="#397DBD";
	            this.style.color="white";
	        }
	        //设置鼠标滑出该行时的效果
	        rowObjs[i].cells[0].onmouseout=function(){
	            this.style.background="#F1F1F1";
	            this.style.color="black";
	        }
	    }
	    //屏蔽菜单的菜单
	    pop.document.oncontextmenu=function()
	    {
	            return false; 
	    }
	    //选择右键菜单的一项后，菜单隐藏
	    pop.document.onclick=function()
	    {
	        pop.hide();
	    }
	    //显示菜单
	    pop.show(event.clientX-1,event.clientY,width,rowCount*25,document.body);
	    return true;
	}
	function detail()
	{
	    window.open ("<%=rootPath%>/monitor.do?action=show_utilhdx&id=<%=tmp%>&ipaddress=<%=host.getIpAddress()%>&ifindex="+node+"&ifname="+ipaddress, "newwindow", "height=450, width=840, toolbar= no, menubar=no, scrollbars=yes, resizable=yes, location=yes, status=yes");
	}
	function portset()
	{
		window.open ("<%=rootPath%>/panel.do?action=show_portreset&ipaddress=<%=host.getIpAddress()%>&ifindex="+node, "newwindow", "height=250, width=700, toolbar= no, menubar=no, scrollbars=yes, resizable=no, location=yes, status=yes");
	}
	function realtimeMonitor()
	{
		window.open ('<%=rootPath%>/monitor.do?action=portdetail&id=<%=tmp%>&ip=<%=host.getIpAddress()%>&ifindex='+node,'端口流速','top=200,left=300,height=480,width=660, toolbar=no, menubar=no, scrollbars=no, resizable=no, location=no, status=no')
	}	
	function bandwidth()
	{
		window.open ('<%=rootPath%>/monitor.do?action=bandwidthdetail&id=<%=tmp%>&ip=<%=host.getIpAddress()%>&ifindex='+node,'带宽','top=200,left=300,height=480,width=660, toolbar=no, menubar=no, scrollbars=no, resizable=no, location=no, status=no')
	}	
	function dataPacket()
	
	{
		window.open ('<%=rootPath%>/monitor.do?action=datapacket&id=<%=tmp%>&ip=<%=host.getIpAddress()%>&ifindex='+node,'广播数据包','top=200,left=300,height=480,width=660, toolbar=no, menubar=no, scrollbars=no, resizable=no, location=no, status=no')
	}	
	function multicastPacket()
	
	{
		window.open ('<%=rootPath%>/monitor.do?action=multicastpacket&id=<%=tmp%>&ip=<%=host.getIpAddress()%>&ifindex='+node,'多播数据包','top=200,left=300,height=480,width=660, toolbar=no, menubar=no, scrollbars=no, resizable=no, location=no, status=no')
	}
	function portstatus()
	{
		window.open ('<%=rootPath%>/portconfig.do?action=showPortStatus&index='+node+'&ip=<%=host.getIpAddress()%>','端口状态','top=200,left=300,height=435,width=660, toolbar=no, menubar=no, scrollbars=no, resizable=no, location=no, status=no')
	}	
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
	document.getElementById('netDetailTitle-0').className='detail-data-title';
	document.getElementById('netDetailTitle-0').onmouseover="this.className='detail-data-title'";
	document.getElementById('netDetailTitle-0').onmouseout="this.className='detail-data-title'";
}

function refer(action){
		document.getElementById("id").value="<%=tmp%>";
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}



</script>
	</head>
	<body id="body" class="body" onload="initmenu();">
		<!-- 这里用来定义需要显示的右键菜单 -->
		<div id="itemMenu" style="display: none";>
			<table border="1" width="100%" height="100%" bgcolor="#F1F1F1"
				style="border: thin; font-size: 12px" cellspacing="0">
				<tr>
					<td style="cursor: default; border: outset 1;" align="center"
						onclick="parent.detail()">
						查看状态
					</td>
				</tr>

				<tr>
					<td style="cursor: default; border: outset 1;" align="center"
						onclick="parent.portset();">
						端口设置
					</td>
				</tr>
				<tr>
					<td style="cursor: default; border: outset 1;" align="center"
						onclick="parent.realtimeMonitor();">
						流速实时
					</td>
				</tr>

				<tr>
					<td style="cursor: default; border: outset 1;" align="center"
						onclick="parent.bandwidth();">
						带宽实时
					</td>
				</tr>
				<tr>
					<td style="cursor: default; border: outset 1;" align="center"
						onclick="parent.dataPacket();">
						广播包实时
					</td>
				</tr>
				<tr>
					<td style="cursor: default; border: outset 1;" align="center"
						onclick="parent.multicastPacket();">
						多播包实时
					</td>
				</tr>
				<tr>
					<td style="cursor: default; border: outset 1;" align="center"
						onclick="parent.portstatus();">
						端口状态
					</td>
				</tr>
			</table>
		</div>
		<!-- 右键菜单结束-->
		<form id="mainForm" method="post" name="mainForm">
			<input type=hidden name="orderflag">
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
								<td class="td-container-main-detail" width=98%>
									<table id="container-main-detail" class="container-main-detail">
										<tr>
											<td>
												<table id="detail-content" class="detail-content"
													width="100%" cellspacing="0" cellpadding="0">
													<!-- 顶部标题 -->
													<tr class="bar">
														<td colspan=4 align=center>
															<b>设备详细信息</b>
														</td>
													</tr>
													<!-- 顶部标题end -->
													<!-- 设备概要信息 -->
													<tr class="bar">
														<td width="20%" style="padding-left: 30px;">
															设备标签:<%=host.getAlias()%>
														</td>
														<td width="20%">
															系统名称:<%=host.getSysName()%>
														</td>
														<td width=15%>
															<table>
																<tr>
																	<td width="10px;">
																		<img
																			src="<%=rootPath%>/resource/<%=NodeHelper.getCurrentStatusImage(alarmLevel)%>">
																	</td>
																	<td>
																		<span><%=NodeHelper.getStatusDescr(alarmLevel)%></span>
																	</td>
																</tr>
															</table>
														</td>
														<td width="20%">
															IP地址:<%=host.getIpAddress()%>
														</td>
													</tr>
													<!-- 设备概要信息end -->
												</table>
											</td>
										</tr>
										<!-- 顶部区域块end -->

										<tr>
											<td>
												<table id="detail-data" class="detail-data">
													<tr>
														<td class="detail-data-header">
															<%=netAppDetailTitleTable%>
														</td>
													</tr>
													<tr>
														<td>
															<table class="detail-data-body">
																<tr>
																	<td>
																		<table width="100%" border="0" cellpadding="0"
																			cellspacing="0" bgcolor="white">
																			<thead class="title">
																				<tr>
																					<td rowspan="2">
																						索引
																					</td>
																					<td rowspan="2">
																						名字
																					</td>
																					<td rowspan="2">
																						关联应用
																					</td>
																					<td rowspan="2">
																						最大字节数(MB)
																					</td>
																					<td rowspan="2" style="width:5%">
																						状态
																					</td>
																					<td colspan="2" style="border:0px;width:15%">
																						带宽
																					</td>
																					<td colspan="2" style="border:0px;width:15%">
																						流速
																					</td>
																					<td style="border:0px;width:10%">
																						查看
																					</td>
																				</tr>
																				<tr>
																					<td align="center">
																						<input type=button name="button3" value="出口"
																							onclick="changeOrder('OutBandwidthUtilHdxPerc')">
																					</td>
																					<td align="center">
																						<input type=button name="button4" value="入口"
																							onclick="changeOrder('InBandwidthUtilHdxPerc')">
																					</td>
																					<td align="center">
																						<input type=button name="button3" value="出口"
																							onclick="changeOrder('OutBandwidthUtilHdx')">
																					</td>
																					<td align="center">
																						<input type=button name="button4" value="入口"
																							onclick="changeOrder('InBandwidthUtilHdx')">
																					</td>
																					<td align="center">
																						<select name="allportflag" id="allportflag"
																							onchange="showPort();">
																							<option value="0" label="显示所有端口"
																								<%="0".equals(showAllPortFlag) ? "selected" : ""%>>
																								显示所有端口
																							</option>
																							<option value="1" label="显示关键端口"
																								<%="1".equals(showAllPortFlag) ? "selected" : ""%>>
																								显示关键端口
																							</option>
																						</select>
																					</td>
																				</tr>

																			</thead>
																			<tbody>
																				<%
																					String url = "";
																					String linkuse = "";
																					for (int i = 0; i < ifvector.size(); i++) {
																						String[] strs = (String[]) ifvector.get(i);
																						String ifname = strs[1];
																						String index = strs[0];
																						if ("1".equals(showAllPortFlag) && !portIndexList.contains(index)) {//显示关键端口   
																							continue;
																						}

																						if (hash != null && hash.size() > 0) {
																							if (hash.get(ipaddress + ":" + index) != null)
																								linkuse = (String) hash.get(ipaddress + ":" + index);
																						}

																						String status = strs[3];
																						if (status.equals("up")) {
																							url = rootPath + "/resource/image/topo/up.gif";
																						} else if (status.equals("down")) {
																							url = rootPath + "/resource/image/topo/down.gif";
																						} else {
																							url = rootPath + "/resource/image/topo/testing.gif";
																						}
																				%>
																				<tr  class="data">
																					<td><%=index%></td>
																					<td><%=ifname%></td>
																					<td><%=linkuse%></td>
																					<td><%=strs[2]%></td>
																					<td>
																						<img src='<%=url%>'>
																					</td>
																					<td><%=strs[4]%></td>
																					<td><%=strs[5]%></td>
																					<td><%=strs[6]%></td>
																					<td><%=strs[7]%></td>
																					<td>
																						<img src="<%=rootPath%>/resource/image/status.gif"
																							border="0" width=15 oncontextmenu=showMenu('2','<%=index%>','<%=ifname.replaceAll(" ", "-")%>')>

																					</td>
																				</tr>
																				<%
																					}
																				%>
																			</tbody>

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
						</table>
					</td>
					
					<td class="td-container-main-tool" width='15%'>
						<jsp:include page="/include/netapptoolbar.jsp">
							<jsp:param value="<%=host.getIpAddress()%>" name="ipaddress" />
							<jsp:param value="<%=host.getSysOid()%>" name="sys_oid" />
							<jsp:param value="<%=tmp%>" name="tmp" />
							<jsp:param value="network" name="category" />
							<jsp:param value="<%=nodedto.getSubtype()%>" name="subtype" />
						</jsp:include>
					</td>
					
				</tr>
			</table>
		</form>
	</BODY>
</HTML>
