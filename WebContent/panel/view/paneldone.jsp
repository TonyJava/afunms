<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.List"%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@page import="com.yhgl.*"%>
<%@page import="com.afunms.config.dao.*"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.polling.node.*"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="com.afunms.polling.impl.*"%>
<%@page import="com.afunms.polling.api.*"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.topology.dao.*"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%
  String rootPath = request.getContextPath();
  
  
%>
<html>
<head>
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script language="javascript">	
  
  function doChange()
  {
     if(mainForm.view_type.value==1)
        window.location = "<%=rootPath%>/topology/network/index.jsp";
     else
        window.location = "<%=rootPath%>/topology/network/port.jsp";
  }

  function toAdd()
  {
      mainForm.action = "<%=rootPath%>/network.do?action=ready_add";
      mainForm.submit();
  }
  
// ?∥1?′
function gotoFullScreen() {
	parent.mainFrame.resetProcDlg();
	var status = "toolbar=no,height="+ window.screen.height + ",";
	status += "width=" + (window.screen.width-8) + ",scrollbars=no";
	status += "screenX=0,screenY=0";
	window.open("topology/network/index.jsp", "fullScreenWindow", status);
	parent.mainFrame.zoomProcDlg("out");
}

</script>
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa" onload="setfilename();">
<FORM name="uploadform" method=post id="uploadform" enctype="multipart/form-data">
<table border="0" id="table1" cellpadding="0" cellspacing="0" width=100%>
	<tr>
		<td width="200" valign=top align=center>
			<table width="90%" style="BORDER-COLLAPSE: collapse" borderColor=#397DBD cellPadding=0 rules=none w align=center border=1 algin="center">
                        <tbody>                     										                        								
                    		<tr algin="left" valign="center">                      														
                      			<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold">&nbsp;拓扑</td>
                    		</tr>
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/icon_cloud.gif" border=0>&nbsp;<a href="javascript:void(null)" onClick='window.open("<%=rootPath%>/topology/network/index.jsp","fullScreenWindow", "toolbar=no,height="+ window.screen.height + ","+"width=" + (window.screen.width-8) + ",scrollbars=no"+"screenX=0,screenY=0")'>网络拓扑</td>
                    		</tr> 
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/bmgl.GIF" border=0>&nbsp;<a href="javascript:void(null)" onClick='window.open("<%=rootPath%>/topology/server/index.jsp","fullScreenWindow", "toolbar=no,height="+ window.screen.height + ","+"width=" + (window.screen.width-8) + ",scrollbars=no"+"screenX=0,screenY=0")'>主机服务器</td>
                    		</tr>                    										                 										                      								
            		</tbody>
            		</table>  
            		<br>
			<table width="90%" style="BORDER-COLLAPSE: collapse" borderColor=#397DBD cellPadding=0 rules=none w align=center border=1 algin="center">
                        <tbody>                     										                        								
                    		<tr algin="left" valign="center">                      														
                      			<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold">&nbsp;设备维护</td>
                    		</tr>
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/addDevice.gif" border=0>&nbsp;<a href="<%=rootPath%>/network.do?action=ready_add">添加设备</td>
                    		</tr> 
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/icon_detail.gif" border=0>&nbsp;<a href="<%=rootPath%>/network.do?action=list&jp=1">设备列表</td>
                    		</tr> 
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/manageDev.gif" border=0>&nbsp;<a href="<%=rootPath%>/ipmac.do?action=list">IP/MAC</td>
                    		</tr>  
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/manageDev.gif" border=0>&nbsp;<a href="<%=rootPath%>/portconfig.do?action=list">端口配置</td>
                    		</tr>
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/infopoint.gif" border=0>&nbsp;<a href="<%=rootPath%>/link.do?action=list">链路信息</td>
                    		</tr>                    		                      		                   		                   										                 										                      								
            		</tbody>
            		</table> 
            		<br>
			<table width="90%" style="BORDER-COLLAPSE: collapse" borderColor=#397DBD cellPadding=0 rules=none w align=center border=1 algin="center">
                        <tbody>                     										                        								
                    		<tr algin="left" valign="center">                      														
                      			<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold">&nbsp;性能监视</td>
                    		</tr>
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/hostl.gif" border=0 width=18>&nbsp;<a href="<%=rootPath%>/network.do?action=monitornodelist&jp=1">监视对象一览表</td>
                    		</tr> 
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/desktops.gif" border=0 width=18>&nbsp;<a href="<%=rootPath%>/moid.do?action=allmoidlist">指标全局阀值一览表</td>
                    		</tr> 
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/manageDev.gif" border=0>&nbsp;<a href="<%=rootPath%>/network.do?action=list">TopN</td>
                    		</tr>                     		                   										                 										                      								
            		</tbody>
            		</table>             		           				
            		<br>
			<table width="90%" style="BORDER-COLLAPSE: collapse" borderColor=#397DBD cellPadding=0 rules=none w align=center border=1 algin="center">
                        <tbody>                     										                        								
                    		<tr algin="left" valign="center">                      														
                      			<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold">&nbsp;视图管理</td>
                    		</tr>
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/mkdz.gif" border=0>&nbsp;<a href="<%=rootPath%>/customxml.do?action=list&jp=1">视图编辑</td>
                    		</tr> 
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/zcbf.gif" border=0>&nbsp;<a href="javascript:void(null)" onClick='window.open("<%=rootPath%>/topology/view/custom.jsp","fullScreenWindow", "toolbar=no,height="+ window.screen.height + ","+"width=" + (window.screen.width-8) + ",scrollbars=no"+"screenX=0,screenY=0")'>视图展示</td>
                    		</tr>                    		                   										                 										                      								
            		</tbody>
            		</table> 		
            		<br>
			<table width="90%" style="BORDER-COLLAPSE: collapse" borderColor=#397DBD cellPadding=0 rules=none w align=center border=1 algin="center">
                        <tbody>                     										                        								
                    		<tr algin="left" valign="center">                      														
                      			<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold">&nbsp;设备面板配置管理</td>
                    		</tr>
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/editicon.gif" border=0>&nbsp;<a href="<%=rootPath%>/panel.do?action=showaddpanel&jp=1">面板模板编辑</td>
                    		</tr> 
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/manageDev.gif" border=0 width=18>&nbsp;<a href="<%=rootPath%>/network.do?action=panelnodelist&jp=1">设备面板编辑</td>
                    		</tr>                     		                   										                 										                      								
            		</tbody>
            		</table>
            				
		</td>
		<td bgcolor="#cedefa" align="center" valign=top>
			<table width="98%" style="BORDER-COLLAPSE: collapse" borderColor=#397DBD cellPadding=0 rules=none align=center border=1 algin="center">
				<tr>
					<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold" colspan=3>&nbsp;&nbsp;<font color=#ffffff>资源 >> 设备面板配置管理 >> 设备面板配置</font></td>
				</tr>
				<tr>
		
					<td height=300 bgcolor="#FFFFFF" valign="top" align=center>
					<br>
						<table cellSpacing="1" cellPadding="0" width="100%" border="0">						
        						
							<tr>
								<td colspan="2">
<%    
    String rvalue = (String)request.getAttribute("rvalue");
%>
								&nbsp;&nbsp;<%=rvalue%>
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
</BODY>
</HTML>
