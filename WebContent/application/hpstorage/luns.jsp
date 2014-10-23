<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="com.afunms.polling.node.Lun"%>
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
	 
	 List<Lun> luns = (List<Lun>)request.getAttribute("luns");
	 if(luns == null)
	 {
	     luns = new ArrayList();
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
	document.getElementById('hpStorageDetailTitle-6').className='detail-data-title';
	document.getElementById('hpStorageDetailTitle-6').onmouseover="this.className='detail-data-title'";
	document.getElementById('hpStorageDetailTitle-6').onmouseout="this.className='detail-data-title'";
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
											                <tr bgcolor="#ECECEC">
                      											<td width="15%" height="26" align="center">名称</td>
                      											<td width="20%" height="26" align="center">冗余组</td>                      											
                      											<td width="15%" height="26" align="center">是否活动</td>
                      											<td width="20%" height="26" align="center">数据容量</td>
                      											<td width="30%" height="26" align="center">全局名称</td>	
                    										</tr>
											              
											                 <%  
											                        if(luns != null && luns.size()>0)
											                        {
											                           for(int i=0;i<luns.size();i++)
											                           {
											                               Lun lun = luns.get(i);
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
											                 <!-- <tr bgcolor="#ECECEC">
											                  <td colspan="4" align="left"><font style="font-weight: bold">逻辑单元信息:</font></td>
											                 </tr> -->											                
                      											<td width="15%" height="26" align="center"><%=SysUtil.ifNull(lun.getName()) %></td>
                      											<td width="20%" height="26" align="center"><%=SysUtil.ifNull(lun.getRedundancyGroup()) %></td>                      											
                      											<td width="15%" height="26" align="center"><%=SysUtil.ifNull(lun.getActive()) %></td>
                      											<td width="20%" height="26" align="center"><%=SysUtil.ifNull(lun.getDataCapacity()) %></td>
                      											<td width="30%" height="26" align="center"><%=SysUtil.ifNull(lun.getWwn()) %></td>	
                    										</tr>
											                 
                    										<!-- <tr>
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;逻辑单元名称:</td>
                      											<td width="35%"><=lun.getName()%> </td>	
																<td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;冗余组:</td>
                      											<td width="35%"><=lun.getRedundancyGroup()%> </td>
                    										</tr>
                    										<tr bgcolor="#ECECEC">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal >&nbsp;是否活动:</td>
                      											<td width="35%"><=lun.getActive()%> </td>                      											
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;数据容量:</td>
                      											<td width="35%"><=lun.getDataCapacity()%> </td>	
                    										</tr>
                    										<tr>
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;全局名称:</td>
                      											<td width="35%"><=lun.getWwn()%> </td>	
																<!-- <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;商业副本序号:</td>
                      											<td width="35%"><=lun.getNumberOfBusinessCopies()%> </td> 
                    										</tr>  -->                   										
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
				</td>
			</tr>
		</table>
	</form>
	</body>
</html>