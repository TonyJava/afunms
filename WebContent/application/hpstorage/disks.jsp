<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="com.afunms.polling.node.Disk"%>
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
	 
	 List<Disk> disks = (List<Disk>)request.getAttribute("disks");
	 if(disks == null)
	 {
	     disks = new ArrayList();
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
	document.getElementById('hpStorageDetailTitle-5').className='detail-data-title';
	document.getElementById('hpStorageDetailTitle-5').onmouseover="this.className='detail-data-title'";
	document.getElementById('hpStorageDetailTitle-5').onmouseout="this.className='detail-data-title'";
}
function refer(action){
		document.getElementById("id").value="<%=host.getId()%>";
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}
function todetail(id,ip)
{       
        window.open ("<%=rootPath%>/application/hpstorage/diskdetail.jsp?id=" + id + "&ip=" + ip, "hpstoragedisk", "height=500, width=700, toolbar=no, menubar=no, scrollbars=no, resizable=no, location=yes, status=yes");
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
											              <tr bgcolor="#ECECEC">
											                  <td width="10%" height="26" align="center">名称</td>
											                  <td width="15%" height="26" align="center">状态</td>
											                  <td width="15%" height="26" align="center">类型</td>
											                  <td width="30%" height="26" align="center">容量</td>											                  
											                  <td width="5%" height="26" align="center">冗余组</td>
											                  <td width="15%" height="26" align="center">初始化声明</td>
											                  <td width="10%" height="26" align="center">详细信息</td>
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
											                   <td width="10%" height="26" align="center"><a href="#" onclick="todetail('<%=i %>','<%=host.getIpAddress() %>')" style="text-decoration: underline;">详细信息</a></td>
											                </tr>
											                  <!-- <tr bgcolor="#ECECEC">
											                  <td colspan="4" align="left"><font style="font-weight: bold">磁盘信息:</font></td>
											                  </tr>
                    										<tr>
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;磁盘名称:</td>
                      											<td width="35%"><=disk.getName()%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;磁盘状态:</td>
                      											<td width="35%"><=disk.getStatus()%> </td>
                    										</tr>
                    										<tr bgcolor="#ECECEC">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;磁盘声明:</td>
                      											<td width="35%"><=disk.getDiskState()%> </td>                      											
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;磁盘类型:</td>
                      											<td width="35%"><=disk.getVendorID()%> </td>	
                    										</tr>
                    										<tr>
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;产品编号:</td>
                      											<td width="35%"><=disk.getProductID()%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;产品版本:</td>
                      											<td width="35%"><=disk.getProductRevision()%> </td>
                    										</tr> 
                    										<tr bgcolor="#ECECEC">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;数据容量:</td>
                      											<td width="35%"><=disk.getDataCapacity()%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;块长度:</td>
                      											<td width="35%"><=disk.getBlockLength()%> </td>
                    										</tr>
                    										<tr>
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;地址:</td>
                      											<td width="35%"><=disk.getAddress()%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;节点全局名称:</td>
                      											<td width="35%"><=disk.getNodeWWN()%> </td>
                    										</tr>
                    										<tr bgcolor="#ECECEC">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;初始化声明:</td>
                      											<td width="35%"><=disk.getInitializeState()%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;冗余组:</td>
                      											<td width="35%"><=disk.getRedundancyGroup()%> </td>
                    										</tr>
                    										<tr>
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;列序列号:</td>
                      											<td width="35%"><=disk.getVolumeSetSerialNumber()%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;序列号:</td>
                      											<td width="35%"><=disk.getSerialNumber()%> </td>
                    										</tr>  
                    										<tr bgcolor="#ECECEC">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;固件版本:</td>
                      											<td width="35%"><=disk.getFirmwareRevision()%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;</td>
                      											<td width="35%">&nbsp; </td>
                    										</tr>-->
                    										<%
                    										           }
                    										      }
                    										 %>                 										
                									</table>
										</td>																		
									</tr> 
									<tr>
									   <td>
										   <table cellpadding="0" cellspacing="0" width=48% align=center>
								              <tr> 
								                <td width="100%" align=center> 								                	
													<script type="text/javascript">
														//var so = new SWFObject("<%=rootPath%>/flex/Area_Input_V.swf?ipadress=<=vo.getIpAddress()%>&table=input&obj1=SRXDYAB&obj2=SRXDYBC&obj3=SRXDYCA&tag=IV", "Area_Input_V", "430", "280", "8", "#ffffff");
														//so.write("flashcontent1");
														//var so = new SWFObject("<%=rootPath%>/flex/common_line.swf?ipadress=<=vo.getIpAddress()%>&title=连通率&tablename=ping<=ip%>&obj1=SRXDYAB&obj2=SRXDYBC&obj3=SRXDYCA&tag=IV", "Area_Input_V", "430", "280", "8", "#ffffff");
														//so.write("flashcontent1");
													    //var so = new SWFObject("<%=rootPath%>/flex/Ping.swf?ipadress=<=vo.getIpAddress()%>", "Ping", "430", "280", "8", "#ffffff");
													    //so.write("flashcontent1");
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
				<td>&nbsp;</td>
			</tr>
		</table>
	</form>
	</body>
</html>