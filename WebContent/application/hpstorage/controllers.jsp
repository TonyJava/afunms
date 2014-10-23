<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="com.afunms.polling.node.Controller"%>
<%@page import="com.afunms.polling.node.CtrlPort"%>
<%@page import="com.afunms.polling.node.DIMM"%>
<%@page import="com.afunms.polling.node.Battery"%>
<%@page import="com.afunms.polling.node.Processor"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>
<%@page import="com.afunms.common.base.JspPage"%>   
<%@page import="java.util.*"%>      
<%@page import="java.text.*"%>
<%@page import="com.afunms.polling.impl.*"%>
<%@page import="com.afunms.polling.api.*"%>
  
<%
	 String runmodel = PollingEngine.getCollectwebflag();//采集与访问模式
	 String rootPath = request.getContextPath();
	 String menuTable = (String)request.getAttribute("menuTable");
	 
	 List<Controller> controllers = (List<Controller>)request.getAttribute("controllers");
	 if(controllers == null)
	 {
	     controllers = new ArrayList();
	 }
	
	 Host host = (Host)request.getAttribute("hpstorage");   
	 if(host == null)
	 {
	   host = new Host();
	 }    	
%>
<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script type="text/javascript"
			src="<%=rootPath%>/include/swfobject.js"></script>
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>

		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>

		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css"
			charset="utf-8" />
		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/js/ext/css/common.css" charset="utf-8" />
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js"
			charset="utf-8"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="utf-8"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js"
			charset="utf-8"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/resource/xml/flush/amcolumn/swfobject.js"></script>
		<script language="javascript">	
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
	document.getElementById('hpStorageDetailTitle-3').className='detail-data-title';
	document.getElementById('hpStorageDetailTitle-3').onmouseover="this.className='detail-data-title'";
	document.getElementById('hpStorageDetailTitle-3').onmouseout="this.className='detail-data-title'";
}
function refer(action){
		document.getElementById("id").value="<%=host.getId()%>";
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}
</script>
	</head>
	<body id="body" class="body" onload="initmenu();">
		<form id="mainForm" method="post" name="mainForm">
		<input type=hidden name="orderflag">
		<input type=hidden name="id">
		
		<table id="body-container" class="body-container">
			<tr>
				 <!--<td class="td-container-menu-bar">
					<table id="container-menu-bar" class="container-menu-bar">
						<tr>
							<td>
								<=menuTable%>
							</td>	
						</tr>
					</table>
				</td>-->
			
				<td class="td-container-main">
					<table id="container-main" class="container-main" >
						<tr>
							<td class="td-container-main-detail"  width=98%> 
								<table id="container-main-detail" class="container-main-detail" >
										<tr>
										<td> 
											<table id="detail-content" class="detail-content" width="100%" background="<%=rootPath%>/common/images/right_t_02.jpg" cellspacing="0" cellpadding="0">
				<tr>
					<td align="left"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
					<td class="layout_title"><b>设备详细信息</b></td>
					<td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
				</tr>
				<tr>
				 <td width="20%" height="26" align="left" nowrap  class=txtGlobal>&nbsp;设备标签:										
										        <span id="lable"><%=host.getAlias()%></span> </td>
                         <td width="20%" height="26" align="left" nowrap class=txtGlobal>&nbsp;系统名称:
											<span id="sysname">HP存储</span></td>
                         <td width="20%" height="26" align=left valign=middle nowrap class=txtGlobal>&nbsp;IP地址:
											<%=host.getIpAddress()%>
						 </td>
				       </tr>
			         </table>
										</td>
									</tr>
									<tr>
										<td>
											<table id="detail-data" class="detail-data">
												<tr>
											    	<td class="detail-data-header">
											    		<%=hpStorageDetailTitleTable%>
											    	</td>
											  </tr>
											  <tr>
											      
											    	<td>
											    		<table class="detail-data-body">
												      		<tr>
												      			<td>
													      			<table width="100%" border="0" cellpadding="0" cellspacing="0">
   	                                         <tr>
										      <td> 
											           <table>
											                 <%  
											                        if(controllers != null && controllers.size()>0)
											                        {
											                           for(int i=0;i<controllers.size();i++)
											                           {
											                               Controller cont = controllers.get(i);
											                  %>
											                  <tr bgcolor="#ECECEC">
											                     <td colspan="4" align="left"><font style="font-weight: bold">控制器信息:</font></td>
											                  </tr>
                    										<tr>
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;名称:</td>
                      											<td width="35%"><%=SysUtil.ifNull(cont.getName())%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;状态:</td>
                      											<td width="35%"><%=SysUtil.ifNull(cont.getStatus())%> </td>
                    										</tr>
                    										<tr bgcolor="#ECECEC">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;序列号:</td>
                      											<td width="35%"><%=SysUtil.ifNull(cont.getSerialNumber())%> </td>                      											
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;供货商编号:</td>
                      											<td width="35%"><%=SysUtil.ifNull(cont.getVendorID())%> </td>
                    										</tr> 
                    										<tr>
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;产品编号:</td>
                      											<td width="35%"><%=SysUtil.ifNull(cont.getProductID())%> </td>                      											
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;产品版本:</td>
                      											<td width="35%"><%=SysUtil.ifNull(cont.getProductRevision())%> </td>
                    										</tr>
                    										
                    										<tr bgcolor="#ECECEC">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;固件版本:</td>
                      											<td width="35%"><%=SysUtil.ifNull(cont.getFirmwareRevision())%> </td>                      											
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;制造商产品编码:</td>
                      											<td width="35%"><%=SysUtil.ifNull(cont.getManufacturingProductCode())%> </td>
                    										</tr>
                    										<tr>
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;控制类型:</td>
                      											<td width="35%"><%=SysUtil.ifNull(cont.getControllerType())%> </td>                      											
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;充电电池固件版本:</td>
                      											<td width="35%"><%=SysUtil.ifNull(cont.getBatteryChargerFirmwareRevision())%> </td>
                    										</tr>
                    										<tr bgcolor="#ECECEC">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;附件交换设置:</td>
                      											<td width="35%"><%=SysUtil.ifNull(cont.getEnclosureSwitchSetting())%> </td>                      											
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;附件编号:</td>
                      											<td width="35%"><%=SysUtil.ifNull(cont.getEnclosureID())%> </td>
                    										</tr>
                    										<tr>
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;连环对:</td>
                      											<td width="35%"><%=SysUtil.ifNull(cont.getLoopPair())%> </td>                      											
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;连环编号:</td>
                      											<td width="35%"><%=SysUtil.ifNull(cont.getLoopID())%> </td>
                    										</tr>
                    										<tr bgcolor="#ECECEC">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;基础驱动地址:</td>
                      											<td width="35%"><%=SysUtil.ifNull(cont.getDriveAddressBasis())%> </td>                      											
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;牢固地址:</td>
                      											<td width="35%"><%=SysUtil.ifNull(cont.getHardAddress())%> </td>
                    										</tr> 
                    										 <%  
                    										         if(cont.getFrontPortList() != null && cont.getFrontPortList().size()>0)
                    										         {                    										                       										 %>
                    										
                    										  <tr>
                    										     <td colspan="4" align="left"><font style="font-weight: bold">前端口列表</font></td>
                    										  </tr>                    						
                    										  
                    										  <%
                    										             for(int j=0;j<cont.getFrontPortList().size();j++)
                    										             {
                    										                    CtrlPort fctp = cont.getFrontPortList().get(j);
                    										  
                    										  %>                     										  
                    										    <tr bgcolor="#ECECEC">
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;名称:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(fctp.getName())%> </td>                      											
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;状态:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(fctp.getStatus())%> </td>
                    										   </tr>
                    										    <tr bgcolor="#ECECEC">
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;端口实例:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(fctp.getPortInstance())%> </td>                      											
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;物理地址:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(fctp.getHardAddress())%> </td>
                    										   </tr>
                    										   <tr bgcolor="#ECECEC">
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;链接声明:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(fctp.getLinkState())%> </td>                      											
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;节点全局名称:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(fctp.getNodeWWN())%> </td>
                    										   </tr>
                    										   
                    										   <tr bgcolor="#ECECEC">
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;端口全局名称:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(fctp.getPortWWN())%> </td>                      											
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;拓扑:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(fctp.getTopology())%> </td>
                    										   </tr>
                    										   <tr bgcolor="#ECECEC">
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;传输速率:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(fctp.getDataRate())%> </td>                      											
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;端口编号:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(fctp.getPortID())%> </td>
                    										   </tr> 
                    										   <tr bgcolor="#ECECEC">
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;设备名称:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(fctp.getDeviceHostName())%> </td>                      											
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;设备路径:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(fctp.getDevicePath())%> </td>
                    										   </tr>
                    										   <tr>
                      											    <td colspan="4">&nbsp;</td>
                    										   </tr>            										                  										
                    										<%
                    										            }
                    										            }
                    										            
                    										  
                    										         if(cont.getBackPortList() != null && cont.getBackPortList().size()>0)
                    										         {                    										                       										 %>
                    										 
                    										  <tr>
                    										     <td colspan="4" align="left"><font style="font-weight: bold">后端口列表</font></td>
                    										  </tr>                    						
                    										  
                    										  <%
                    										             for(int j=0;j<cont.getBackPortList().size();j++)
                    										             {
                    										                    CtrlPort bctp = cont.getBackPortList().get(j);
                    										  
                    										  %>                     										  
                    										    <tr bgcolor="#ECECEC">
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;名称:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(bctp.getName())%> </td>                      											
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;状态:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(bctp.getStatus())%> </td>
                    										   </tr>
                    										    <tr bgcolor="#ECECEC">
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;端口实例:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(bctp.getPortInstance())%> </td>                      											
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;物理地址:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(bctp.getHardAddress())%> </td>
                    										   </tr>
                    										   <tr bgcolor="#ECECEC">
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;链接声明:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(bctp.getLinkState())%> </td>                      											
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;节点全局名称:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(bctp.getNodeWWN())%> </td>
                    										   </tr>
                    										   
                    										   <tr bgcolor="#ECECEC">
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;端口全局名称:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(bctp.getPortWWN())%> </td>                      											
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;拓扑:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(bctp.getTopology())%> </td>
                    										   </tr>
                    										   <tr bgcolor="#ECECEC">
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;传输速率:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(bctp.getDataRate())%> </td>                      											
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;端口编号:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(bctp.getPortID())%> </td>
                    										   </tr> 
                    										   <tr bgcolor="#ECECEC">
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;设备名称:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(bctp.getDeviceHostName())%> </td>                      											
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;设备路径:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(bctp.getDevicePath())%> </td>
                    										   </tr>
                    										   <tr>
                      											    <td colspan="4">&nbsp;</td>
                    										   </tr>           										                  										
                    										<%
                    										            }
                    										            }
                    										            if(cont.getDimmList() != null && cont.getDimmList().size()>0)
                    										            {
                    										%>
                    										  <tr>
                    										     <td colspan="4" align="left"><font style="font-weight: bold">双列直插式存储模块列表</font></td>
                    										  </tr> 
                    										  
                    										<%
                    										             for(int j=0;j<cont.getDimmList().size();j++)
                    										             {
                    										                    DIMM dimm = cont.getDimmList().get(j);
                    										  
                    										  %>
                    										   <tr bgcolor="#ECECEC">
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;名称:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(dimm.getName())%> </td>                      											
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;状态:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(dimm.getStatus())%> </td>
                    										   </tr>
                    										   <tr bgcolor="#ECECEC">
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;容量:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(dimm.getCapacity())%> </td>                      											
                      											    <!-- <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;状态:</td>
                      											    <td width="35%"><=dimm.getStatus()%> </td> -->
                    										   </tr>
                    										   <tr>
                      											    <td colspan="4">&nbsp;</td>
                    										   </tr>
                    										<%
                    										            }
                    										            }
                    										            if(cont.getBattery() != null)
                    										            {
                    						
                    										 %>
                    										    <tr>
                    										        <td colspan="4" align="left"><font style="font-weight: bold">电池信息</font></td>
                    										    </tr> 
                    										    
                    										 <%
                    										            Battery battery = cont.getBattery();
                    										  %>
                    										   <tr bgcolor="#ECECEC">
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;名称:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(battery.getName())%> </td>                      											
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;状态:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(battery.getStatus())%> </td>
                    										   </tr>
                    										   <tr bgcolor="#ECECEC">
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;标识:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(battery.getIdentification())%> </td>                      											
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;厂商:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(battery.getManufacturerName())%> </td>
                    										   </tr>
                    										   <tr bgcolor="#ECECEC">
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;设备名称:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(battery.getDeviceName())%> </td>                      											
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;生产日期:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(battery.getManufacturerDate())%> </td>
                    										   </tr>
                    										   <tr bgcolor="#ECECEC">
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;剩余电量:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(battery.getRemainingCapacity())%> </td>                      											
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;剩余电量百分比:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(battery.getPctRemainingCapacity())%> </td>
                    										   </tr>
                    										   <tr bgcolor="#ECECEC">
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;电压:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(battery.getVoltage())%> </td>                      											
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;放电周期:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(battery.getDischargeCycles())%> </td>
                    										   </tr>
                    										   <tr>
                      											    <td colspan="4">&nbsp;</td>
                    										   </tr>
                    										 <%
                    										            }
                    										            if(cont.getProcessor() != null)
                    										            {
                    										  %>
                    										    <tr>
                    										        <td colspan="4" align="left"><font style="font-weight: bold">处理器信息</font></td>
                    										    </tr>
                    										    <%
                    										               Processor processor = cont.getProcessor();
                    										     %>
                    										     <tr bgcolor="#ECECEC">
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;名称:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(processor.getName())%> </td>                      											
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;状态:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(processor.getStatus())%> </td>
                    										    </tr>
                    										     <tr bgcolor="#ECECEC">
                      											    <td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;标识:</td>
                      											    <td width="35%"><%=SysUtil.ifNull(processor.getIdentification())%> </td>                      											
                      											    <!-- <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;状态:</td>
                      											    <td width="35%"><=processor.getStatus()%> </td> -->
                    										    </tr>
                    										    <tr>
                      											    <td colspan="4">&nbsp;</td>
                    										    </tr>
                    										  <%
                    										            }                    										       
                    										            
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
									</table>
									
							</td>						
							<td class="td-container-main-tool" width="15%">
								<jsp:include page="/application/hpstorage/hpstoragetoolbar.jsp">										
								    <jsp:param value="<%=host.getIpAddress()%>" name="ipaddress" />
									<jsp:param value="<%=host.getSysOid()%>" name="sys_oid" />
									<jsp:param value="<%=host.getId()%>" name="tmp" />
									<jsp:param value="storage" name="category" />
									<jsp:param value="hp" name="subtype" />
								</jsp:include>
						   </td>
						</tr>					
					</table>
				<td></td>
			</tr>
		</table>
	</form>
	</body>
</html>